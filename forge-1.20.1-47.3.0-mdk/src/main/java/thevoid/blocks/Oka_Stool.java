package thevoid.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thevoid.entity.SeatEntity;
import thevoid.init.ModEntity;

import java.util.List;

public class Oka_Stool extends Block {
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 8, 16);

    public Oka_Stool(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    @Override
    public void appendHoverText(ItemStack pstack, @Nullable BlockGetter pgetter, List<Component> pTooltip, TooltipFlag pflag) {
        pTooltip.add(Component.translatable("block.thevoid.oak_stool.desc")
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(pstack, pgetter, pTooltip, pflag);
    }

//    @Override
//    public InteractionResult use(BlockState pstate, Level plevel, BlockPos pPos, Player player, InteractionHand pHand, BlockHitResult pHit){
//        //如果在客户端且用主手点击了方块触发
//        if(plevel.isClientSide()&&pHand==InteractionHand.MAIN_HAND){
//            player.sendSystemMessage(Component.literal("你可以坐软板凳"));
//        }
//
//
//        if (!plevel.isClientSide && player.getVehicle() == null) {
//            // 生成座椅实体
//            SeatEntity seat = new SeatEntity(ModEntity.SEAT.get(), plevel);
//            seat.setPos(pPos.getX() + 0.5, pPos.getY() + 0.2, pPos.getZ() + 0.5);
//            plevel.addFreshEntity(seat);
//
//            // 绑定玩家
//            seat.setRider(player);
//
//            // 禁止移动
//            player.onUpdateAbilities();
//            return InteractionResult.sidedSuccess(plevel.isClientSide);
//        }
//        return super.use(pstate, plevel, pPos, player, pHand, pHit);
//    }

}
