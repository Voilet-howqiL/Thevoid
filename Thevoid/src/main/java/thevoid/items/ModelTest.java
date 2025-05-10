package thevoid.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thevoid.entity.ThrownCrudeSpear;
import thevoid.entity.ThrownSpear;
import thevoid.implement.ICrosshairItem;

import java.util.List;

public class ModelTest  extends SwordItem  implements ICrosshairItem {
    private static final float MIN_SPEED = 0.5f;  // 最小速度（未蓄力）
    private static final float MAX_SPEED = 2.0f;  // 最大速度（完全蓄力）
    private static final int MAX_CHARGE_TIME = 20; // 达到最大速度所需的tick数（1秒=20tick）
    private static final float INACCURACY = 4.0f; // 精准度（0为枪枪爆头）
    private static final int DURABILITY_COST = 1;

    public ModelTest(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    /// 完全照抄我们前面的长矛
    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.SPEAR;//右键使用时，与三叉戟同款瞄准。
    }

    public int getUseDuration(@NotNull ItemStack stack) {
        return 36000;
    }
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player) || level.isClientSide) {
            return; // 确保只在服务端执行且是玩家
        }

            // 计算实际蓄力时间（总持续时间 - 剩余时间）
            int chargeTime = this.getUseDuration(stack) - Math.max(timeLeft, 0);

            // 计算蓄力比例（0.0~1.0），限制最大蓄力时间
            float chargeRatio = Math.min((float) chargeTime / MAX_CHARGE_TIME, 1.0f);

            player.displayClientMessage(
                Component.literal("蓄力程度：" + (chargeRatio)), true);

            // 根据蓄力比例插值计算速度
            float actualSpeed = MIN_SPEED + (MAX_SPEED - MIN_SPEED) * chargeRatio;

            // 只有扣除后还没完全损坏才能投掷
            if(!stack.isEmpty()) {
            shootSpear(level,player,stack,actualSpeed);
            player.swing(entity.getUsedItemHand());


            }

    }
    private void shootSpear(Level level, Player player, ItemStack stack, float speed){
            /// 更改点
        ThrownCrudeSpear spear = ThrownCrudeSpear.create(level, player, stack.copy());
        spear.setBaseDamage(6.0D);
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
                speed,
                INACCURACY
        );
        ((ServerLevel) level).sendParticles(
                ParticleTypes.CLOUD,
                player.getX(),
                player.getY() + 2.0,
                player.getZ(),
                5,
                look.x, look.y, look.z,
                speed/10
        );
        if (level.addFreshEntity(spear)) {
            // 仅当实体生成成功时消耗耐久
            if (!player.getAbilities().instabuild) {
                int cost = (int)(stack.getMaxDamage() * DURABILITY_COST);
                stack.hurtAndBreak(cost, player,
                        p -> p.broadcastBreakEvent(p.getUsedItemHand()));
            }
        } else {
            player.sendSystemMessage(
                    Component.literal("投掷失败!").withStyle(ChatFormatting.RED));
        }
    }
    public static float getChargeRatio(ItemStack stack, LivingEntity entity) {
        if (entity.getUseItem() != stack) return 0;
        if (!(stack.getItem() instanceof ModelTest)) return 0; // 确保物品是 ModelTest

        ModelTest spear = (ModelTest) stack.getItem();
        int useDuration = spear.getUseDuration(stack); // 通过实例调用非静态方法
        int chargeTime = useDuration - entity.getUseItemRemainingTicks();
        return Math.min((float) chargeTime / MAX_CHARGE_TIME, 1.0f);
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
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {

        pTooltipComponents.add(Component.translatable("item.thevoid.model_test.tip")
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

    }
}
