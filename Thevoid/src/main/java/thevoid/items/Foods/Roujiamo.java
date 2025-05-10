package thevoid.items.Foods;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class Roujiamo extends Item {
    public Roujiamo(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity player) {
        if (!pLevel.isClientSide && player instanceof Player) {
            // 10%概率触发中毒
            if (pLevel.random.nextFloat() < 0.1f) {
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 400, 2));
                player.sendSystemMessage(Component.literal("恁着孩儿，噎着了吧！"));
                player.getTags().add("thevoid:eat_tooo_fast");
            }

            // 10%概率触发凋零
            if (pLevel.random.nextFloat() < 0.1f) {
                player.addEffect(new MobEffectInstance(MobEffects.WITHER, 300, 2));
                player.sendSystemMessage(Component.literal("快喝水！"));
                player.getTags().add("thevoid:eat_too_fast");
            }

            // 10%概率触发混乱
            if (pLevel.random.nextFloat() < 0.1f) {
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 2000, 2));
                player.sendSystemMessage(Component.literal("吃饱了还吃？！"));
                player.getTags().add("thevoid:eat_tooo_fast");
            }
        }
        return super.finishUsingItem(pStack, pLevel, player);
    }



    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.translatable("Item.Food.roujiamo.tips"));
    }
}