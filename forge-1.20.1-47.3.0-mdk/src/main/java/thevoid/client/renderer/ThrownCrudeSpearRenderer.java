package thevoid.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import thevoid.entity.ThrownCrudeSpear;

import static thevoid.Myfirst_MOd.TheVoid.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ThrownCrudeSpearRenderer extends EntityRenderer<ThrownCrudeSpear> {
    private final ItemRenderer itemRenderer;
    public ThrownCrudeSpearRenderer(EntityRendererProvider.Context context){
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ThrownCrudeSpear entity, float yaw, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        // 获取关联的ItemStack
        ItemStack stack = entity.getItem();

        // 设置渲染矩阵
        poseStack.pushPose();
        poseStack.scale(1.5F, 1.5F, 1.5F);

        // 使用插值获取平滑旋转角度
        float entityYaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float entityPitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

        // 调整模型旋转顺序
        poseStack.mulPose(Axis.YP.rotationDegrees(entityYaw - 90F)); // 对齐水平方向
        poseStack.mulPose(Axis.ZP.rotationDegrees(entityPitch));      // 应用俯仰

        // 调整模型初始方向（假设模型默认竖直向上）
        poseStack.mulPose(Axis.XP.rotationDegrees(-90F)); // 将模型转为水平
        // 补偿旋转（X和Y轴各顺时针90度）
        poseStack.mulPose(Axis.YP.rotationDegrees(110F));  // Y轴补偿
        poseStack.mulPose(Axis.XP.rotationDegrees(90F));  // X轴补偿

        poseStack.translate(0D, -0.3D, 0D);


        // 渲染物品模型
        itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                entity.level(),
                0
        );

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownCrudeSpear thrownCrudeSpear) {
        return new ResourceLocation(MODID,"textures/item/model_test.png");
    }



}
