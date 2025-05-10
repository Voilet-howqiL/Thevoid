package thevoid.Utils.ClientEvent;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkEvent;
import software.bernie.geckolib.animatable.GeoItem;
import thevoid.items.Chrononacht;

import java.util.function.Supplier;

import static thevoid.items.Chrononacht.*;

// 装填请求数据包
public record ReloadRequestPacket() {

    private static final int COOLDOWN_TICKS = 104;//设置冷却时间5s

    public static ReloadRequestPacket decode(FriendlyByteBuf buf) {
        return new ReloadRequestPacket();
    }

    public void encode(FriendlyByteBuf buf) {
        // 无数据需要传输
    }

    public static void handle(ReloadRequestPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                handleServerReload(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    // 实际服务端处理逻辑
    private static void handleServerReload(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof Chrononacht weapon) {
            // 弹药检查,武器中的方法
            if (hasAmmo(player)) {
                if (!isReloading(stack)) {
                    int needed = MAX_AMMO - getAmmo(stack);
                    if (needed <= 0) {
                        return;
                    }
                    int availableArrows = 0;
                    for (ItemStack item : player.getInventory().items) {
                        if (item.is(Items.ARROW)) availableArrows += item.getCount();
                    }

                    if (availableArrows >= needed) {
                        // 触发动画！！
                        weapon.triggerAnim(player, GeoItem.getOrAssignId(stack, player.serverLevel()),
                                "ReloadingController",
                                "reloading");

                        // 消耗弹药
                        if(!player.isCreative()) {
                            Chrononacht.consumeAmmoForReload(player, stack);
                        }
                        // 设置冷却
                        player.getCooldowns().addCooldown(weapon, COOLDOWN_TICKS);
                        Chrononacht.finishReload(stack);
                        // 广播音效
                        player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.CROSSBOW_LOADING_END,
                                SoundSource.PLAYERS,
                                0.8F,
                                1.0F);
                    }
                }
            }
        }
    }
}