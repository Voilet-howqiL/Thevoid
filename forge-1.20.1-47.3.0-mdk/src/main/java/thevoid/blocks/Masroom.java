package thevoid.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class Masroom extends BushBlock {

    protected static final VoxelShape SHAPE =
            Block.box(1.0D, 0.0D, 1.0D, 15.0D, 6.0D, 15.0D);

    public Masroom(Properties properties) {
        super(properties.randomTicks());
    }
    //调整碰撞箱大小
//    getShape()：控制 选择框（鼠标交互）和 渲染边界
//    getCollisionShape()：控制 物理碰撞（实体移动）
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context){
        return SHAPE;
    }


    //我们自定义这个方法,不知道会不会被调用
    //注意一定要是ServerLEVEL!!很多时候，有些方法能不能被调用，取决与你投入的变量类型，java会自动根据变量类型来确定是那个超类的方法。
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(25) == 0) {
            // 检查周围光照条件
            if (level.getRawBrightness(pos, 0) < 13) {
                // 尝试生成新蘑菇
                // 检查基底方块是否合法（菌丝、灰化土等）
                BlockPos belowPos = pos.below();
                BlockState belowState = level.getBlockState(belowPos);
                if (!belowState.is(Blocks.MYCELIUM) && !belowState.is(Blocks.PODZOL)) {
                    return;
                }
                this.trySpread(level, pos, random);
            }
        }
    }
//尝试传播
    private void trySpread(Level level, BlockPos pos, RandomSource random) {
        BlockPos spreadPos = pos.offset(
                random.nextInt(3) - 1,
                random.nextInt(2) - 1,
                random.nextInt(3) - 1);
        BlockPos belowSpreadPos = spreadPos.below();
        BlockState belowSpreadState = level.getBlockState(belowSpreadPos);
        if (level.isEmptyBlock(spreadPos)
                && (belowSpreadState.is(Blocks.MYCELIUM) || belowSpreadState.is(Blocks.PODZOL))) {
            level.setBlock(spreadPos, this.defaultBlockState(), 3);
        }
    }


    @Override
    public void appendHoverText(ItemStack pstack, @Nullable BlockGetter pgetter, List<Component> pTooltip, TooltipFlag pflag) {
        pTooltip.add(Component.translatable("Block.thevoid.masroom.tooltip")
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(pstack, pgetter, pTooltip, pflag);
    }
}
