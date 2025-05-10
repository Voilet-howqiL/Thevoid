package thevoid.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import thevoid.init.ModEntity;

public class EntityFallingBlock extends Entity {
    private BlockState blockState;
    private BlockState initialBelowState; // 新增字段存储初始下方方块
    public static float GRAVITY = 0.1f;
    public double prevMotionX, prevMotionY, prevMotionZ;
//    private final BlockPos startPos = BlockPos.ZERO;
private static final EntityDataAccessor<Float> ROTATION_X = SynchedEntityData.defineId(EntityFallingBlock.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ROTATION_Y = SynchedEntityData.defineId(EntityFallingBlock.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<BlockState> BLOCK_STATE = SynchedEntityData.defineId(EntityFallingBlock.class, EntityDataSerializers.BLOCK_STATE);
    private static final EntityDataAccessor<Integer> DURATION = SynchedEntityData.defineId(EntityFallingBlock.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TICKS_EXISTED = SynchedEntityData.defineId(EntityFallingBlock.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> MODE = SynchedEntityData.defineId(EntityFallingBlock.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> ANIM_V_Y = SynchedEntityData.defineId(EntityFallingBlock.class, EntityDataSerializers.FLOAT);

    public EntityFallingBlock(@NotNull EntityType<EntityFallingBlock> entityFallingBlockEntityType, Level level, BlockState state, float vy) {
        super(entityFallingBlockEntityType,level);
    }

    public BlockPos getStartPos() {
        return BlockPos.containing(prevMotionX,prevMotionY,prevMotionZ);
    }

    public boolean shouldRender() {
        return random.nextBoolean();
    }


    public enum EnumFallingBlockMode {
        MOBILE,
        POPUP_ANIM
    }

    public float animY = 0;
    public float prevAnimY = 0;

    public EntityFallingBlock(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
        setBlock(Blocks.GRASS_BLOCK.defaultBlockState());
        setDuration(20);

    }

    public EntityFallingBlock(EntityType<?> entityTypeIn, Level worldIn, int duration, BlockState blockState, Vec3 startPos) {
        super(entityTypeIn, worldIn);
        setBlock(blockState);
        setDuration(duration);
    }

    public EntityFallingBlock(EntityType<?> entityTypeIn, Level worldIn, BlockState blockState, float vy, Vec3 startPos) {
        super(entityTypeIn, worldIn);
        setBlock(blockState);
        setMode(EnumFallingBlockMode.POPUP_ANIM);
        setAnimVY(vy);
    }

    public EntityFallingBlock(EntityType<?> entityType, Level level, BlockPos spawnPos, BlockState fallingBlockState) {
        super(entityType, level);
        this.blockState = fallingBlockState;
        // 立即捕获初始下方状态
        this.initialBelowState = level.getBlockState(spawnPos.below());
        setBlock(initialBelowState);
        // 设置实体位置为方块中心
        this.moveTo(
                spawnPos.getX() + 0.5D,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5D,
                0.0F,
                0.0F
        );
    }

    public static EntityFallingBlock create(Level level, BlockPos pos, BlockState state) {
        return new EntityFallingBlock(
                ModEntity.FALLING_BLOCK.get(), // 你的自定义实体类型
                level,
                pos,
                state
        );
    }

    // 获取初始下方方块的方法
    public BlockState getInitialBelowState() {
        return initialBelowState != null ? initialBelowState : Blocks.AIR.defaultBlockState();
    }

    @Override
    public void onAddedToWorld() {
        if (getDeltaMovement().x() > 0 || getDeltaMovement().z() > 0) setYRot((float) ((180f/Math.PI) * Math.atan2(getDeltaMovement().x(), getDeltaMovement().z())));
        setXRot(getXRot() + random.nextFloat() * 360);
        super.onAddedToWorld();
    }

    @Override
    public void tick() {
        if (getMode() == EnumFallingBlockMode.POPUP_ANIM) {
            setDeltaMovement(0, 0, 0);
        }
        prevMotionX = getDeltaMovement().x;
        prevMotionY = getDeltaMovement().y;
        prevMotionZ = getDeltaMovement().z;
        super.tick();
        if (getMode() == EnumFallingBlockMode.MOBILE) {
            setDeltaMovement(getDeltaMovement().subtract(0, GRAVITY, 0));
            if (onGround()) setDeltaMovement(getDeltaMovement().scale(0.7));
            else setXRot(getXRot() + 15);
            this.move(MoverType.SELF, this.getDeltaMovement());

            if (tickCount > 20 ) {discard();
            }
        }
        else {
            float animVY = getAnimVY();
            prevAnimY = animY;
            animY += animVY;
            setAnimVY(animVY - GRAVITY);
            if (animY < -0.2) discard() ;
        }
    }

    @Override
    protected void defineSynchedData() {
        getEntityData().define(BLOCK_STATE, Blocks.DIRT.defaultBlockState());
        getEntityData().define(DURATION, 20);
        getEntityData().define(TICKS_EXISTED, 0);
        getEntityData().define(MODE, EnumFallingBlockMode.MOBILE.toString());
        getEntityData().define(ANIM_V_Y, 1f);
        getEntityData().define(ROTATION_X, 0.0f);
        getEntityData().define(ROTATION_Y, 0.0f);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        Tag blockStateCompound = compound.get("block");
        if (blockStateCompound != null) {
            BlockState blockState = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), (CompoundTag) blockStateCompound);
            setBlock(blockState);
        }
        setDuration(compound.getInt("duration"));
        tickCount = compound.getInt("ticksExisted");
        getEntityData().set(MODE, compound.getString("mode"));
        setAnimVY(compound.getFloat("vy"));

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        BlockState blockState = getBlock();
        if (blockState != null) compound.put("block", NbtUtils.writeBlockState(blockState));
        compound.putInt("duration", getDuration());
        compound.putInt("ticksExisted", tickCount);
        compound.putString("mode", getEntityData().get(MODE));
        compound.putFloat("vy", getEntityData().get(ANIM_V_Y));
    }

    public BlockState getBlock() {
        return getEntityData().get(BLOCK_STATE);
    }

    public void setBlock(BlockState block) {
        getEntityData().set(BLOCK_STATE, block);
    }

    public int getDuration() {
        return getEntityData().get(DURATION);
    }

    public void setDuration(int duration) {
        getEntityData().set(DURATION, duration);
    }


    public EnumFallingBlockMode getMode() {
        String mode = getEntityData().get(MODE);
        if (mode.isEmpty()) return EnumFallingBlockMode.MOBILE;
        return EnumFallingBlockMode.valueOf(getEntityData().get(MODE));
    }

    private void setMode(EnumFallingBlockMode mode) {
        getEntityData().set(MODE, mode.toString());
    }

    public float getAnimVY() {
        return getEntityData().get(ANIM_V_Y);
    }

    private void setAnimVY(float vy) {
        getEntityData().set(ANIM_V_Y, vy);
    }


    @Override
    public boolean isPushable() {
        return false;
    }

}