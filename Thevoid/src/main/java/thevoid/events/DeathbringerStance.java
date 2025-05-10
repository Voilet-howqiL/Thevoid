package thevoid.events;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import thevoid.init.ModItems;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
//赐死剑气
public class DeathbringerStance {

        private static final Map<UUID, Long> COOLDOWNS = new HashMap<>();
        public static float HEAL_PERCENT = 0.25f; // 25%伤害转化
        public static int COOLDOWN_TICKS = 200; // 10秒冷却（20tick/秒）


        @SubscribeEvent
        public static void onDamageDealt(LivingHurtEvent event) {

            //检查伤害来源
            if (!(event.getSource().getEntity()  instanceof Player)) return;

            Player player = (Player) event.getSource().getEntity();
            if (player.level().isClientSide)  return;

            // 检查冷却
            if (isOnCooldown(player)) return;
            //检查主手武器是否为暗裔巨剑
            ItemStack mainHand = player.getMainHandItem();
            if (!(mainHand.getItem() == ModItems.DARKIN_GREATSWORD.get() && mainHand.hasTag()))return;

            float damage = event.getAmount();
            float healAmount = damage * HEAL_PERCENT;

            player.heal(healAmount);
            setCooldown(player);

            // 客户端效果
            if (player.level().isClientSide)  {
                spawnHealEffects(player);
            }
        }

        private static boolean isOnCooldown(Player player) {
            Long lastTime = COOLDOWNS.get(player.getUUID());
            if (lastTime == null) return false;

            long currentTime = player.level().getGameTime();
            return (currentTime - lastTime) < COOLDOWN_TICKS;
        }

        private static void setCooldown(Player player) {
            COOLDOWNS.put(player.getUUID(),  player.level().getGameTime());
        }

        // 客户端效果方法
        @OnlyIn(Dist.CLIENT)
        private static void spawnHealEffects(Player player) {
            for(int i=0; i<5; i++){
                player.level().addParticle(ParticleTypes.HEART,
                        player.getX()  + (Math.random()-0.5)*0.5,
                        player.getY()  + 1.2,
                        player.getZ()  + (Math.random()-0.5)*0.5,
                        0, 0.1, 0);
            }
            player.playSound(SoundEvents.ALLAY_DEATH,
                    0.8f, 0.5f + (float)Math.random()*0.5f);


        }



}

