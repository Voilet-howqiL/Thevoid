package thevoid.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import thevoid.entity.EntityFallingBlock;


public class FallingBlockRenderer extends EntityRenderer<EntityFallingBlock> {
    private final BlockRenderDispatcher blockRenderer;
    // 动画控制参数
    private static final float TIME_FACTOR = 0.1f;    // 时间系数
    private static final float AMPLITUDE = 0.8f;         // 振幅（最高点高度）
    private static final float BASE_HEIGHT_OFFSET = -0.7f; // 基础渲染位置修正

    public FallingBlockRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.blockRenderer = pContext.getBlockRenderDispatcher();
    }

    @Override
    public void render(EntityFallingBlock entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        BlockState blockState = entity.getBlock();
        BlockPos currentPos = entity.blockPosition();
        BlockState renderState = entity.getInitialBelowState();
        BlockState belowState = entity.level().getBlockState(currentPos.below());

        if (blockState.isAir()) return;

        // 计算动画进度
        float animationTime = (entity.tickCount + partialTick) * TIME_FACTOR;
        float progress = Mth.clamp(animationTime, 0.0f, 1.4f);

        // 运动公式：基础偏移 + 振幅 * 正弦曲线
        float totalOffsetY = BASE_HEIGHT_OFFSET + (float) Math.sin(progress * Math.PI) * AMPLITUDE;

        // 应用变换时保持X/Z轴居中
        poseStack.pushPose();
        poseStack.translate(-0.5, totalOffsetY, -0.5);

        // 渲染方块
        BakedModel model = blockRenderer.getBlockModel(blockState);
        blockRenderer.getModelRenderer().tesselateBlock(
                entity.level(),
                model,
                belowState,
                currentPos,
                poseStack,
                buffer.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(blockState)),
                false,
                entity.level().random,
                blockState.getSeed(entity.getStartPos()),
                packedLight
        );

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }



//    @Override
//    public void render(EntityFallingBlock entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
//        BlockState blockState = entity.getBlock();
//        if (blockState.isAir()) return;
//        BlockPos startPos = entity.getStartPos();
// //        if (startPos == null || startPos.equals(BlockPos.ZERO)) return;
//
//        // 获取方块模型
//        BakedModel model = blockRenderer.getBlockModel(blockState);
//        // 获取实体初始位置
//        float x = startPos.getX();
//        float z = startPos.getZ();
//
//        // 计算全局时间（带缓动）
//        float lifeTime = (entity.tickCount + partialTick) * 0.05f;
//        float easeFactor = Mth.clamp(lifeTime * 0.5f, 0f, 1f); // 前2秒渐入
//
//        // 多波形叠加计算
//        float totalOffsetY = 0;
//        for(int i=0; i<WAVE_FACTORS.length; i++){
//            float waveFactor = WAVE_FACTORS[i];
//
//            // 计算相位（位置相关 + 时间推进）
//            float phaseX = (x / WAVE_LENGTH) * (float)Math.PI * 2;
//            float phaseZ = (z / WAVE_LENGTH) * (float)Math.PI * 2;
//            float timePhase = lifeTime * WAVE_SPEED + PHASE_OFFSETS[i];
//
//            // 三维波动方程
//            float wave = Mth.sin(phaseX + phaseZ + timePhase)
//                    * Mth.cos(phaseX - timePhase)
//                    * Mth.sin(phaseZ + timePhase * 0.5f);
//
//            totalOffsetY += wave * BASE_AMPLITUDE * waveFactor;
//        }
//
//        // 应用缓动曲线
//        totalOffsetY *= easeFactor * (1 - Mth.sin(lifeTime * 0.2f) * 0.1f);
//
//        // 应用矩阵变换
//        poseStack.pushPose();
//        poseStack.translate(-0.5, totalOffsetY, -0.5);
//
//        // 渲染方块
//        blockRenderer.getModelRenderer().tesselateBlock(
//                entity.level(),
//                model,
//                blockState,
//                entity.blockPosition(),
//                poseStack,
//                buffer.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(blockState)),
//                false,
//                entity.level().random,
//                blockState.getSeed(entity.getStartPos()),
//                packedLight
//        );
//
//        poseStack.popPose();
//        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
//    }




//@Override
//public void render(EntityFallingBlock entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
//    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
//    matrixStackIn.pushPose();
//    matrixStackIn.translate(0, 0.5f, 0);
//    if (entityIn.getMode() == EntityFallingBlock.EnumFallingBlockMode.MOBILE) {
//        matrixStackIn.mulPose(MathUtils.quatFromRotationXYZ(0, Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()), 0, true));
//        matrixStackIn.mulPose(MathUtils.quatFromRotationXYZ(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot()), 0, 0, true));
//    }
//    else {
//        matrixStackIn.translate(0, Mth.lerp(partialTicks, entityIn.prevAnimY, entityIn.animY), 0);
//        matrixStackIn.translate(0, -1, 0);
//    }
//    matrixStackIn.translate(-0.5f, -0.5f, -0.5f);
//    dispatcher.renderSingleBlock(entityIn.getBlock(), matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY);
//    matrixStackIn.popPose();
//}



    /// 抄袭，未启用
    private void applyRotationEffects(PoseStack poseStack, EntityFallingBlock entity, float partialTick) {
        // 旋转动画逻辑
        float rotation = (entity.tickCount + partialTick) * 30F % 360;
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));

        // 弹跳动画（根据实体运动数据）
        if (entity.getDeltaMovement().y() > 0) {
            float scale = 1.0F + (entity.tickCount % 10) * 0.1F;
            poseStack.scale(scale, scale, scale);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(EntityFallingBlock entityFallingBlock) {
        return null;
    }

}