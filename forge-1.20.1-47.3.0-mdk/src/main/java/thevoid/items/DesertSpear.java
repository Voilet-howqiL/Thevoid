package thevoid.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import thevoid.entity.ThrownSpear;
import thevoid.implement.ICrosshairItem;

import javax.annotation.Nullable;
import java.util.List;

public class DesertSpear extends SwordItem implements ICrosshairItem {
    private static final float DURABILITY_COST_PERCENT = 0.01f;
    private static final float BASE_SPEED = 2.0f;  // 基础速度（比箭矢快0.5f）
    private static final float INACCURACY = 1.0f; // 精准度（0为枪枪爆头）
    public DesertSpear(Tiers tier, int attackdamage ,float attackspeed,Properties properties ){
        super(tier,attackdamage,attackspeed,properties);
    }
    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.SPEAR;//右键使用时，与三叉戟同款瞄准。
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 72000; // 蓄力时间，三叉戟的一半儿。
    }
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            // 计算实际消耗量：最大耐久 × 消耗百分比
            int cost = (int)(stack.getMaxDamage() * DURABILITY_COST_PERCENT);

            // 修复超过最大值的边界情况
            if(stack.getDamageValue() + cost > stack.getMaxDamage()) {
                cost = stack.getMaxDamage() - stack.getDamageValue();
            }

            if (!level.isClientSide) {
                if(cost > 0) {
                    // 直接扣除耐久
                    if(!player.isCreative()){
                    stack.setDamageValue(stack.getDamageValue() + cost);
                    }

                    // 只有扣除后还没完全损坏才能投掷
                    if(!stack.isEmpty()) {
                        System.out.println("[DEBUG] 投掷成功！");
                        shootSpear(level,player,stack);
                        player.swing(entity.getUsedItemHand());

                        if (!player.getAbilities().instabuild) {
                            stack.shrink(1);
                        }
                    }
                }
            }
        }
    }
    private void shootSpear(Level level, Player player, ItemStack stack){

        ThrownSpear spear = ThrownSpear.create(level, player, stack.copy());
        spear.setBaseDamage(16.0D);
        spear.setPos(
                player.getX(),
                player.getY() + player.getEyeHeight(),
                player.getZ()
        );

        // 计算方向向量
        Vec3 look = player.getLookAngle();
        spear.shoot(
                look.x,
                look.y,
                look.z,
                BASE_SPEED,
                INACCURACY
        );

        if (level.addFreshEntity(spear)) {
            System.out.println("[SERVER] 实体已成功生成");
        } else {
            System.out.println("[SERVER] 实体生成失败");
        }

    }

    @Override
    public ResourceLocation getCrosshairTexture() {
        return new ResourceLocation("thevoid", "textures/ui/crosshair-deserspear.png");
    }


    // 禁止在附魔台附魔
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false; // 彻底禁用
    }

    // 禁止通过铁砧附魔
    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        // 第一部分：物品名称（金色）
        tooltip.add(Component.translatable("item.thevoid.desert_spear.desc")
                .withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD));


    }
}





