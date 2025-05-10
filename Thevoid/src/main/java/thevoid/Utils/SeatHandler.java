package thevoid.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.entity.SeatEntity;
import thevoid.init.ModEntity;
import thevoid.items.Hum;
import thevoid.items.ResonanceBlade;
import thevoid.items.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Mod.EventBusSubscriber(modid = TheVoid.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SeatHandler {
    // 可配置的座位高度偏移,在这里更改offset的值,default值是0.3
    private static final Map<Block, Double> SEAT_HEIGHT_OFFSETS = new HashMap<>();
    static {
        //假如下次用模组物品要这样获取
        SEAT_HEIGHT_OFFSETS.put(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(TheVoid.MODID,"oak_stool")),0.1);
    }

    @SubscribeEvent
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        /// bug修复，我不知道为什么出现这个bug
        if (player.getMainHandItem().getItem() instanceof Hum ||
                player.getMainHandItem().getItem() instanceof ResonanceBlade) {
            return; // 如果手里有这两把剑就跳过生成
        }

        // 检测是否为可坐方块
        if (isSittableBlock(state)) {
            // 阻止默认交互（如打开GUI）
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);

            // 获取或创建座位实体
            SeatEntity seat = getOrCreateSeat(level, pos, state.getBlock());
            if (!seat.isOccupied()) {
                seat.setRider(player);
//                player.sendSystemMessage(Component.literal("use被触发了"));
            }
        }
    }

    private static boolean isSittableBlock(BlockState state) {
        // 通过标签或注册表检测
        return state.is(ModTags.Blocks.SITTABLE_BLOCKS) ||
                SEAT_HEIGHT_OFFSETS.containsKey(state.getBlock());
    }

    private static SeatEntity getOrCreateSeat(Level level, BlockPos pos, Block block) {
        // 从方块位置查找现有座位
        AABB searchArea = new AABB(pos).inflate(0.5);
        List<SeatEntity> existing = level.getEntitiesOfClass(
                SeatEntity.class, searchArea, e -> e.blockPosition().equals(pos)
        );

        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        // 创建新座位实体
        SeatEntity seat = new SeatEntity(ModEntity.SEAT.get(), level);
        Vec3 sitPos = calculateSitPosition(pos, block);
        seat.setPos(sitPos.x, sitPos.y, sitPos.z);
        level.addFreshEntity(seat);

        // 绑定到方块（可选持久化）
        seat.getEntityData().set(SeatEntity.BLOCK_POS, Optional.of(pos));
        return seat;
    }

    private static Vec3 calculateSitPosition(BlockPos pos, Block block) {
        // 根据方块类型计算精确座位点
        //其中的Y轴的落点是可调控的
        double baseY = pos.getY();
        double offset = SEAT_HEIGHT_OFFSETS.getOrDefault(block, 0.3);
        return new Vec3(
                pos.getX() + 0.5,
                baseY + offset,
                pos.getZ() + 0.5
        );
    }
}

