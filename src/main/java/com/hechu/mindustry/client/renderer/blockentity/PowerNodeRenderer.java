package com.hechu.mindustry.client.renderer.blockentity;

import com.hechu.mindustry.MindustryConstants;
import com.hechu.mindustry.world.level.block.Equipment.PowerNodeBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

/**
 * @author luobochuanqi
 */
@OnlyIn(Dist.CLIENT)
public class PowerNodeRenderer implements BlockEntityRenderer<PowerNodeBlockEntity> {
    public static final ResourceLocation BEAM_LOCATION = new ResourceLocation(MindustryConstants.MOD_ID, "textures/entity/power_node_beam.png");

    private static void renderPowerNodeBeam(PoseStack pPoseStack, MultiBufferSource pBufferSource, float pPartialTick, long pGameTime, int pYOffset, int pHeight) {
        renderPowerNodeBeam(pPoseStack, pBufferSource, BEAM_LOCATION, pPartialTick, 1.0F, pGameTime, pYOffset, 3, 0.07F, 0.06F);
    }

    public static void renderPowerNodeBeam(PoseStack pPoseStack, MultiBufferSource pBufferSource, ResourceLocation pBeamLocation, float pPartialTick, float pTextureScale, long pGameTime, int pYOffset, int pHeight, float pBeamRadius, float pGlowRadius) {
        int pYOffsetAndpHeight = pYOffset + pHeight;
        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, 0.0D, 0.5D);
        float f = (float) Math.floorMod(pGameTime, 40) + pPartialTick;
        float f1 = pHeight < 0 ? f : -f;
        // f2 的计算使得纹理坐标产生一个周期性变化，使得激光看起来像是在流动或闪烁。
        float f2 = Mth.frac(f1 * 0.2F - (float) Mth.floor(f1 * 0.1F));
        float fRed = 1.0f;
        float fGreen = 5.0f;
        float fBlue = 5.0f;
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(f * 2.25F - 45.0F));
        float f6 = 0.0F;
        float f8 = 0.0F;
        float f9 = -pBeamRadius;
        float f12 = -pBeamRadius;
        float f15 = -1.0F + f2;
        float f16 = (float) pHeight * pTextureScale * (0.5F / pBeamRadius) + f15;
        // 渲染内部激光
        renderPart(pPoseStack, pBufferSource.getBuffer(RenderType.beaconBeam(pBeamLocation, false)), fRed, fGreen, fBlue, 1.0F, pYOffset, pYOffsetAndpHeight, 0.0F, pBeamRadius, pBeamRadius, 0.0F, f9, 0.0F, 0.0F, f12, 0.0F, 1.0F, f16, f15);
        pPoseStack.popPose();
        f6 = -pGlowRadius;
        float f7 = -pGlowRadius;
        f8 = -pGlowRadius;
        f9 = -pGlowRadius;
        f15 = -1.0F + f2;
        f16 = (float) pHeight * pTextureScale + f15;
        // 渲染外围光束
        renderPart(pPoseStack, pBufferSource.getBuffer(RenderType.beaconBeam(pBeamLocation, true)), fRed, fGreen, fBlue, 0.125F, pYOffset, pYOffsetAndpHeight, f6, f7, pGlowRadius, f8, f9, pGlowRadius, pGlowRadius, pGlowRadius, 0.0F, 1.0F, f16, f15);
        pPoseStack.popPose();
    }

    private static void renderPart(PoseStack pPoseStack, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, float pAlpha, int pMinY, int pMaxY, float pX0, float pZ0, float pX1, float pZ1, float pX2, float pZ2, float pX3, float pZ3, float pMinU, float pMaxU, float pMinV, float pMaxV) {
        PoseStack.Pose posestack$pose = pPoseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        renderQuad(matrix4f, matrix3f, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX0, pZ0, pX1, pZ1, pMinU, pMaxU, pMinV, pMaxV);
        renderQuad(matrix4f, matrix3f, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX3, pZ3, pX2, pZ2, pMinU, pMaxU, pMinV, pMaxV);
        renderQuad(matrix4f, matrix3f, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX1, pZ1, pX3, pZ3, pMinU, pMaxU, pMinV, pMaxV);
        renderQuad(matrix4f, matrix3f, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX2, pZ2, pX0, pZ0, pMinU, pMaxU, pMinV, pMaxV);
        // int pMinY,int pMaxY,float pMinX,float pMinZ,float pMaxX,float pMaxZ,float pMinU,float pMaxU,float pMinV,float pMaxV
//        renderQuad(matrix4f, matrix3f, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY - 1, pMaxY - 2, pX2 - 2, pZ2 - 3, pX0 - 1, pZ0 - 1, pMinU, pMaxU, pMinV, pMaxV);
    }

    private static void renderQuad(Matrix4f pPose, Matrix3f pNormal, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, float pAlpha, int pMinY, int pMaxY, float pMinX, float pMinZ, float pMaxX, float pMaxZ, float pMinU, float pMaxU, float pMinV, float pMaxV) {
        addVertex(pPose, pNormal, pConsumer, pRed, pGreen, pBlue, pAlpha, pMaxY, pMinX, pMinZ, pMaxU, pMinV);
        addVertex(pPose, pNormal, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMinX, pMinZ, pMaxU, pMaxV);
        addVertex(pPose, pNormal, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxX, pMaxZ, pMinU, pMaxV);
        addVertex(pPose, pNormal, pConsumer, pRed, pGreen, pBlue, pAlpha, pMaxY, pMaxX, pMaxZ, pMinU, pMinV);
    }

    private static void addVertex(Matrix4f pPose, Matrix3f pNormal, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, float pAlpha, int pY, float pX, float pZ, float pU, float pV) {
        pConsumer.vertex(pPose, pX, (float) pY, pZ).color(pRed, pGreen, pBlue, pAlpha).uv(pU, pV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(pNormal, 0.0F, 1.0F, 0.0F).endVertex();
    }

    @Override
    public void render(PowerNodeBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        long gameTime = pBlockEntity.getLevel().getGameTime();
        List<PowerNodeBlockEntity.PowerNodeBeamSection> list = pBlockEntity.getBeamSections();
        int j = 0;

        List<PowerNodeBlockEntity> passivelyConnectedNodes = pBlockEntity.getPassivelyConnectedNodes();
        List<PowerNodeBlockEntity> connectedNodes = pBlockEntity.getConnectedNodes();

        for (int k = 0; k < list.size(); ++k) {
            PowerNodeBlockEntity.PowerNodeBeamSection powerNodeBeamSection = list.get(k);
            renderPowerNodeBeam(pPoseStack, pBuffer, pPartialTick, gameTime, j, k == list.size() - 1 ? 1024 : powerNodeBeamSection.getHeight());
            j += powerNodeBeamSection.getHeight();
        }
    }

    @Override
    public boolean shouldRenderOffScreen(PowerNodeBlockEntity pBlockEntity) {
            return true;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }

    /**
     * Check whether the position of the beam is within the visible range of the camera
     */
    @Override
    public boolean shouldRender(PowerNodeBlockEntity pBlockEntity, Vec3 pCameraPos) {
        return Vec3.atCenterOf(pBlockEntity.getBlockPos())
                .multiply(1.0D, 0.0D, 1.0D)
                .closerThan(pCameraPos.multiply(1.0D, 0.0D, 1.0D), (double) this.getViewDistance());
    }
}
