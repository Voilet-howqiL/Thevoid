package thevoid.Utils;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import thevoid.mixin.FallingBlockEntityMixin;

import java.lang.reflect.Constructor;

public class FallingBlockHander {
    public static FallingBlockEntity createFallingBlock(Level level, BlockPos pos, BlockState state) {
        // 处理 WATERLOGGED 属性（如果存在）
        BlockState entityState = state;
        if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
            entityState = state.setValue(BlockStateProperties.WATERLOGGED, false);
        }

        // 调用 Mixin 暴露的构造函数
        // 正确做法：通过反射调用
        try {
            Constructor<FallingBlockEntity> constructor = FallingBlockEntity.class.getDeclaredConstructor(Level.class, double.class, double.class, double.class, BlockState.class);
            constructor.setAccessible(true);

            return constructor.newInstance(level,
                    pos.getX() + 0.5,
                    pos.getY(),
                    pos.getZ() + 0.5,
                    state);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create FallingBlockEntity", e);
        }
    }
}
