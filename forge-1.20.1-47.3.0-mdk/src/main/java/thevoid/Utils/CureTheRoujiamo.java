package thevoid.Utils;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thevoid.Myfirst_MOd.TheVoid;

@Mod.EventBusSubscriber(modid = TheVoid.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CureTheRoujiamo {
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = event.getItemStack();
        Level level = player.level();

        if (!level.isClientSide && stack.getItem() == Items.WATER_BUCKET) {
            if (player.getTags().contains("thevoid:eat_tooo_fast")) {
                // 移除所有负面效果

                player.removeEffect(MobEffects.POISON);
                player.removeEffect(MobEffects.CONFUSION);

                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION,200,1));
                player.removeTag("thevoid:eat_tooo_fast");

                // 替换水桶为空桶（非创造模式）
                if (!player.isCreative()) {
                    player.setItemInHand(event.getHand(), new ItemStack(Items.BUCKET));
                }
                event.setCanceled(true); // 阻止默认行为
            }
        }
    }
    @SubscribeEvent
    public static void onPotionFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack stack = event.getItem();
            Level level = player.level();

            // 检测饮用的是普通药水  且是水瓶
            if (stack.getItem() instanceof PotionItem) {

                if (player.getTags().contains("thevoid:eat_too_fast")) {
                    // 移除所有负面效果
                    player.removeEffect(MobEffects.WITHER);

                    player.removeTag("thevoid:eat_too_fast");
                    player.sendSystemMessage(Component.literal("太中啦！！"));
                }
            }
        }
    }
}
