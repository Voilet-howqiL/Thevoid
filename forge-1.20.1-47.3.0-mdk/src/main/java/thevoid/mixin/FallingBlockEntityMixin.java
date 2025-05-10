package thevoid.mixin;

import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin {

//    private static FallingBlockEntity createFallingBlock(Level level, BlockPos pos, BlockState state) {
//        return FallingBlockEntityMixin.invokeConstructor(
//                level,
//                pos.getX() + 0.5,
//                pos.getY(),
//                pos.getZ() + 0.5,
//                state.hasProperty(BlockStateProperties.WATERLOGGED)
//                        ? state.setValue(BlockStateProperties.WATERLOGGED, false)
//                        : state
//        );
//    }



    @Invoker("<init>")
    public static FallingBlockEntity invokeConstructor(
            Level level, double x, double y, double z, BlockState state
    ) {
        throw new AssertionError("Mixin failed to invoke constructor"); // Mixin 运行时替换实际实现
    }
}


