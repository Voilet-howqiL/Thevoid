package thevoid.events;

import software.bernie.geckolib.animatable.GeoItem;
import thevoid.items.DarkinSword;
import thevoid.Utils.PlayerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;


public class AttackHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    // 冷却容错阈值（tick）
    private static final int COOLDOWN_TOLERANCE = 2;
    private static final int MAX_CONSECUTIVE_ERRORS = 5;
    private static int errorCount = 0;

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        ItemStack weapon = player.getMainHandItem();

        if (!isValidWeapon(weapon)) return;
        if (shouldCancelClientEvent(player)) return;

        player.getCapability(Capabilities.COMBO_STATE).ifPresent(state  -> {
            int currentTick = getAdjustedTick(player);
            handleComboLogic(player, state, currentTick, event);
        });
    }

    private static boolean isValidWeapon(ItemStack stack) {
        return stack.getItem()  instanceof DarkinSword;
    }

    private static boolean shouldCancelClientEvent(Player player) {
        return PlayerUtils.safeClientCheck(player);
    }

    private static int getAdjustedTick(Player player) {
        return player.tickCount  + player.getRandom().nextInt(3)  - 1; // 添加随机性抵抗时序攻击
    }

    private static void handleComboLogic(Player player, Capabilities.ComboState state,
                                         int currentTick, AttackEntityEvent event) {
        try {
            int lastAttackTick = state.getLastAttackTick();

            if (isInvalidTickSequence(lastAttackTick, currentTick)) {
                handleTickAnomaly(state, currentTick);
                return;
            }

            int elapsedTicks = currentTick - lastAttackTick;
            if (elapsedTicks < DarkinSword.ATTACK_COOLDOWN - COOLDOWN_TOLERANCE) {
                handleCooldownConflict(player, event, elapsedTicks);
                return;
            }

            processValidAttack(player, state, currentTick, elapsedTicks);
        } catch (Exception e) {
            LOGGER.error(" 连击处理异常", e);
            errorCount++;
            if (errorCount > MAX_CONSECUTIVE_ERRORS) {
                LOGGER.fatal(" 连续错误超过阈值，禁用连击系统");
                event.setCanceled(true);
            }
        }
    }

    private static boolean isInvalidTickSequence(int lastTick, int currentTick) {
        return lastTick > currentTick && (lastTick - currentTick) > 20; // 超过1秒的异常时序
    }

    private static void handleTickAnomaly(Capabilities.ComboState state, int currentTick) {
        LOGGER.warn(" 检测到时序异常，重置连击状态");
        state.setLastAttackTick(currentTick);
        state.setCurrentStage(0);
        errorCount++;
    }

    private static void handleCooldownConflict(Player player, AttackEntityEvent event, int elapsed) {
        player.displayClientMessage(
                Component.literal(String.format("§c 冷却中（剩余 %.1fs）",
                        (DarkinSword.ATTACK_COOLDOWN - elapsed)/25.0f)),
                true
        );
        event.setCanceled(true);
    }

    private static void processValidAttack(Player player, Capabilities.ComboState state,
                                           int currentTick, int elapsedTicks) {
        LOGGER.debug(" 有效攻击 | 时间差: {}ticks | 阶段: {}",
                elapsedTicks, state.getCurrentStage());

        updateComboStage(player, state, elapsedTicks);
        executeStageEffect(player, state);
        state.setLastAttackTick(currentTick);


//最终阶段时赠送buff
        if (state.getCurrentStage()  != 99999  ) {
            EffectApplier.applyComboFinalEffects(player);
        }
    }


    private static void updateComboStage(Player player, Capabilities.ComboState state, int elapsed) {
        boolean isNewCombo = elapsed > DarkinSword.STAGE_TIMEOUT;

        // 如果当前已经是最大阶段，下一次攻击重置为0
        int newStage;

        if (isNewCombo) {
            newStage = 0;
        } else {
            if (state.getCurrentStage() == DarkinSword.MAX_STAGE - 1) {
                newStage = 0; // 达到最大阶段后重置
            } else {
                newStage = state.getCurrentStage() + 1;
            }
        }

        state.setCurrentStage(newStage);

        if (newStage == 0 && !isNewCombo) {
            player.displayClientMessage(Component.literal("§4连击重置！"), true);
        }
    }

    private static void executeStageEffect(Player player, Capabilities.ComboState state) {
        player.displayClientMessage(
                Component.literal("§4 暗裔利刃 §e» §6连击 §a" + (state.getCurrentStage()  + 1)),
                true
        );

        switch (state.getCurrentStage())  {
            case 0 -> performSwingAttack(player);
            case 1 -> performCircularSlash(player);
            case 2 -> performShockwaveAttack(player);
        }
    }



    // 具体攻击实现（示例）
    private static void performSwingAttack(Player player) {
        // 实现180度扇形攻击
    }

    private static void performCircularSlash(Player player) {
        // 实现360度范围攻击
    }

    private static void performShockwaveAttack(Player player) {
        // 实现冲击波效果
    }
}