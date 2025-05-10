package thevoid.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
//每次攻击回血回饱食度
public class EffectApplier {
    public static void applyComboFinalEffects(Player player) {
        // 生命恢复（确保不超过最大生命值）
        float actualHeal = Math.min(player.getHealth()  + 0.5f, player.getMaxHealth());
        player.setHealth(actualHeal);

        // 饱食度
        FoodData foodData = player.getFoodData();
        int newFoodLevel = Math.min(foodData.getFoodLevel()  + 4, 20); // 1 hunger = 0.25 shank
        foodData.setFoodLevel(newFoodLevel);

        // 摧毁护甲
        player.getInventory().hurtArmor(
                player.damageSources().magic(),  // 更合适的伤害源
                2.0f,
                new int[]{0,1,2,3} // 所有盔甲槽位
        );
    }
}
