package thevoid.client.renderer.UI;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thevoid.Myfirst_MOd.TheVoid;
import thevoid.implement.ICrosshairItem;

@Mod.EventBusSubscriber(modid = TheVoid.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CrosshairRenderer {

    @SubscribeEvent
    public static void onRenderOverlayPre(RenderGuiOverlayEvent.Pre event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id())) return;

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        // 检查主手或副手是否持有支持准心的武器
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        ICrosshairItem crosshairItem = null;

        if (mainHand.getItem() instanceof ICrosshairItem) {
            crosshairItem = (ICrosshairItem) mainHand.getItem();
        } else if (offHand.getItem() instanceof ICrosshairItem) {
            crosshairItem = (ICrosshairItem) offHand.getItem();
        }

        if (crosshairItem == null || !crosshairItem.hasCustomCrosshair()) return;

        event.setCanceled(true); // 取消原版准心

        // 立即渲染自定义准心
        GuiGraphics guiGraphics = event.getGuiGraphics();
        ResourceLocation texture = crosshairItem.getCrosshairTexture();
        int x = (guiGraphics.guiWidth() - 16) / 2;
        int y = (guiGraphics.guiHeight() - 16) / 2;

        // 使用GuiGraphics的绘制方法，无需手动设置纹理
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.setColor(1, 1, 1, 0.7f); // 暂时使用完全不透明测试
        guiGraphics.blit(texture, x, y, 0, 0, 16, 16, 16, 16);
        guiGraphics.setColor(1, 1, 1, 0.7f); // 重置颜色状态
        RenderSystem.disableBlend();
    }

}