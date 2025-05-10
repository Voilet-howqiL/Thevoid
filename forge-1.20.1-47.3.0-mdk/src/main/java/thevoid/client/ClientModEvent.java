package thevoid.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import thevoid.client.renderer.FallingBlockRenderer;
import thevoid.client.renderer.SeatEntityRenderer;
import thevoid.client.renderer.ThrownCrudeSpearRenderer;
import thevoid.client.renderer.ThrownSpearRenderer;
import thevoid.entity.ThrownCrudeSpear;
import thevoid.init.ModBlocks;
import thevoid.init.ModEntity;

import static thevoid.Myfirst_MOd.TheVoid.MODID;

@Mod.EventBusSubscriber(modid = MODID,bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT )
public class ClientModEvent {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        //实体渲染器注册通过这里注册到主线
        EntityRenderers.register(
                ModEntity.THROWN_SPEAR.get(),
                ThrownSpearRenderer::new
        );
        EntityRenderers.register(
                ModEntity.THROWN_CRUDE_SPEAR.get(),
                ThrownCrudeSpearRenderer::new
        );

        EntityRenderers.register(
                ModEntity.SEAT.get(),
                SeatEntityRenderer::new
        );

        EntityRenderers.register(
                ModEntity.FALLING_BLOCK.get(),
                FallingBlockRenderer::new
        );



        //方块渲染通过这里调整并通知主线
        //一般是设置镂空渲染
        event.enqueueWork(() -> {
            // 绑定自定义蘑菇的渲染类型为镂空
            //没想到竟然是点睛之笔
            ItemBlockRenderTypes.setRenderLayer(
                    ModBlocks.MASROOM.get(),
                    RenderType.cutout()
            );
        });

        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(
                    ModBlocks.TALLGRASS.get(),
                    RenderType.cutout()
            );
            ColorHandler.registerBlockColors();
        });

        event.enqueueWork(() -> {
            // 绑定渲染类型为镂空
            ItemBlockRenderTypes.setRenderLayer(
                    ModBlocks.OAKSTOOL.get(),
                    RenderType.cutout()
            );
        });


    }

}
