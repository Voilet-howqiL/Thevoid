package thevoid.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public class SeatEntity extends Entity {
    private static final EntityDataAccessor<Optional<UUID>> RIDER_ID =
            SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> SEAT =
            SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.BOOLEAN);
    private Vec3 originalPos = Vec3.ZERO;
    private String originalDimension;
    public static final EntityDataAccessor<Optional<BlockPos>> BLOCK_POS =
            SynchedEntityData.defineId(SeatEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);

    public SeatEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public boolean isOccupied() {
        return !this.getPassengers().isEmpty();
    }


/// 下面是实体类三件套，必须定义。
    @Override
    protected void defineSynchedData() {
        ///对所有数据进行初始化，定义为empty
        this.entityData.define(RIDER_ID,Optional.empty());
        this.entityData.define(SEAT,false);
        this.entityData.define(BLOCK_POS, Optional.empty());
    }
    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.hasUUID("Rider")) {
            this.entityData.set(RIDER_ID, Optional.of(compoundTag.getUUID("Rider")));
        }
        // 读取原始位置
        if (compoundTag.contains("OriginalX")) {
            originalPos = new Vec3(
                    compoundTag.getDouble("OriginalX"),
                    compoundTag.getDouble("OriginalY"),
                    compoundTag.getDouble("OriginalZ")
            );
        }
        originalDimension = compoundTag.getString("OriginalDim");

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        this.entityData.get(RIDER_ID).ifPresent(uuid ->
                compoundTag.putUUID("Rider", uuid)
        );
        // 保存原始位置
        compoundTag.putDouble("OriginalX", originalPos.x);
        compoundTag.putDouble("OriginalY", originalPos.y);
        compoundTag.putDouble("OriginalZ", originalPos.z);
        compoundTag.putString("OriginalDim", originalDimension);

    }




    // 骑乘验证
    @Override
    public void tick() {
        if (!this.level().isClientSide) {
            Optional<Player> rider = getRider();
            // 验证玩家是否有效且仍在骑乘
            if (rider.isEmpty() ||
                    !rider.get().isAlive() ||
                    !rider.get().isPassengerOfSameVehicle(this)) {
                rider.ifPresent(this::teleportToOriginalPosition);
                    this.discard();

            }
        }
        super.tick();
    }
    private void teleportToOriginalPosition(Player player) {
        if (!player.level().dimension().location().toString().equals(originalDimension)) {
            // 如果跨维度需要特殊处理，这里可以扩展
            return;
        }
        // 设置位置和运动状态
        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0;
        //将玩家传送回上面保存的原始位置
        player.teleportTo(
                originalPos.x,
                originalPos.y,
                originalPos.z
        );
    }



    public Optional<Player> getRider() {
        return this.entityData.get(RIDER_ID).map(uuid -> level().getPlayerByUUID(uuid));
    }

    // 骑乘设置
    public void setRider(Player player) {
        // 记录玩家原始位置和维度
        this.originalPos = player.position();
        this.originalDimension = player.level().dimension().location().toString();
        this.entityData.set(RIDER_ID, Optional.of(player.getUUID()));
        player.startRiding(this, true); // 强制骑乘
        // 设置骑乘偏移（示例值，根据需求调整）
        this.setYRot(player.getYRot());
        player.setYRot(this.getYRot());
    }

    //安全机制,但还未启用
    private boolean isSafePosition(Level level, Vec3 pos) {
        BlockPos blockPos = BlockPos.containing(pos);
        return level.getBlockState(blockPos).isAir() &&
                level.getBlockState(blockPos.above()).isAir();
    }

}
