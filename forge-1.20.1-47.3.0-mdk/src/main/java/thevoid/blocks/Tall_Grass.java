package thevoid.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class Tall_Grass extends BushBlock implements BonemealableBlock {

    public static final EnumProperty<Layer> LAYER = EnumProperty.create("layer", Layer.class);
    public static final int MAX_HEIGHT = 4;

    public enum Layer implements StringRepresentable {
        BOTTOM("bottom"),
        MID("mid"),
        TOP("top");

        private final String name;

        private Layer(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public static Layer byName(String name) {
            for (Layer layer : values()) {
                if (layer.name.equals(name)) {
                    return layer;
                }
            }
            throw new IllegalArgumentException("Unknown layer: " + name);
        }
    }

//构造函数位置别放错了？
    public Tall_Grass(Properties pProperties) {
        super(pProperties);
        // 默认放下时是bottom，然后触发更新机制，检测上方无草更新为top就好了。
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYER, Layer.BOTTOM));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYER);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        BlockState belowState = level.getBlockState(pos.below());

        // 如果放置在另一个 Tall_Grass 上方
        if (belowState.getBlock() instanceof Tall_Grass) {
            return this.defaultBlockState().setValue(LAYER, Layer.TOP);
        }
        // 如果放置在泥土上
        else if (belowState.is(BlockTags.DIRT)) {
            return this.defaultBlockState().setValue(LAYER, Layer.BOTTOM);
        }
        return null;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            // 优先更新下方方块以确保支撑状态
            updateLayerStates(level, pos.below());
            // 再更新当前方块
            updateLayerStates(level, pos);
            // 最后更新上方方块
            updateLayerStates(level, pos.above());
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!level.isClientSide) {
            updateLayerStates(level, pos);
        }
    }

    private void updateLayerStates(Level level, BlockPos pos) {
        BlockState currentState = level.getBlockState(pos);
        if (!(currentState.getBlock() instanceof Tall_Grass)) return;

        Layer currentLayer = currentState.getValue(LAYER);
        BlockPos abovePos = pos.above();
        BlockPos belowPos = pos.below();

        // 自动标记顶部和底部
        boolean isTop = !(level.getBlockState(abovePos).getBlock() instanceof Tall_Grass);
        boolean isBottom = (currentLayer == Layer.BOTTOM) ||
                (currentLayer == Layer.MID && level.getBlockState(belowPos).is(BlockTags.DIRT));
        //更新状态
        Layer newLayer;
        if (isTop) {
            newLayer = Layer.TOP;
        } else if (isBottom) {
            newLayer = Layer.BOTTOM;
        } else {
            newLayer = Layer.MID;
        }

        if (newLayer != currentLayer) {
            level.setBlock(pos, currentState.setValue(LAYER, newLayer), Block.UPDATE_ALL);
        }

    }


    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Layer layer = state.getValue(LAYER);
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        boolean hasAbove = level.getBlockState(pos.above()).getBlock() instanceof Tall_Grass;

        return switch (layer) {
            case BOTTOM ->
                // BOTTOM 必须下方是泥土且上方有 MID/TOP
                    belowState.is(BlockTags.DIRT);
            case MID ->
                // MID 必须下方是 BOTTOM/MID 且上方有 TOP
                    belowState.getBlock() instanceof Tall_Grass && hasAbove;
            case TOP ->
                // TOP 可以单独存在（下方泥土）或在 MID 层上方
                    (belowState.is(BlockTags.DIRT) || belowState.getBlock() instanceof Tall_Grass) && !hasAbove;
            default -> false;
        };
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
        if (!level.isClientSide) {
            // 破坏时更新整个堆叠
            updateLayerStates(level, pos.above());
            updateLayerStates(level, pos.below());
        }
    }

    // 骨粉接口实现
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        tryGrow(level, pos, true); // 强制生长
    }

    // 自然生长逻辑
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getRawBrightness(pos, 0) >= 7 && random.nextFloat() < 0.2) {
            tryGrow(level, pos, false);
        }
    }

    private void tryGrow(ServerLevel level, BlockPos triggerPos, boolean force) {
        // 新增：找到最底层的基础位置
        BlockPos basePos = findBottomBlock(level, triggerPos);
        if (basePos == null) return;

        int currentHeight = getCurrentHeight(level, basePos);
        if (currentHeight >= MAX_HEIGHT) return;

        BlockPos growPos = basePos.above(currentHeight);
        if (level.isEmptyBlock(growPos) && (force || canNaturalGrow(level, growPos))) {
            level.setBlock(growPos, defaultBlockState(), 3);
        }
    }

    // 新增：向下查找最底层植株
    private BlockPos findBottomBlock(LevelReader level, BlockPos startPos) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos().set(startPos);
        int searchDepth = 0;

        while (searchDepth < MAX_HEIGHT && level.getBlockState(pos).is(this)) {
            pos.move(Direction.DOWN);
            searchDepth++;
        }

        // 返回第一个有效底部位置
        return searchDepth > 0 ? pos.above().immutable() : null;
    }

    // 修改后的高度计算方法
    private int getCurrentHeight(LevelReader level, BlockPos basePos) {
        int height = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos().set(basePos);

        while (height < MAX_HEIGHT && level.getBlockState(pos).is(this)) {
            height++;
            pos.move(Direction.UP);
        }
        return height;
    }

    // 自然生长条件
    private boolean canNaturalGrow(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(BlockTags.DIRT) &&
                level.getRawBrightness(pos, 0) > 7;
    }

}