package com.hechu.mindustry.client.renderer.blockentity;

import com.hechu.mindustry.MindustryConstants;
import com.hechu.mindustry.world.level.block.Equipment.PowerNodeBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

/**
 * @author luobochuanqi
 */
@OnlyIn(Dist.CLIENT)
public class PowerNodeRenderer implements BlockEntityRenderer<PowerNodeBlockEntity> {
    public static final ResourceLocation BEAM_LOCATION = new ResourceLocation(MindustryConstants.MOD_ID, "textures/entity/power_node_beam.png");
    VertexConsumer TRANSLUCENT_BUFFER;
    VertexConsumer NO_TRANSPARENCY_BUFFER;

//    private void renderPart1(PoseStack pPoseStack, VertexConsumer pConsumer, float pX0, float pX1, float pX2, float pX3, int pMinY, int pMaxY, float pZ0, float pZ1, float pZ2, float pZ3, float pMinU, float pMaxU, float pMinV, float pMaxV) {
//        PoseStack.Pose pose = pPoseStack.last();
//        Matrix4f matrix4f = pose.pose();
//        Matrix3f matrix3f = pose.normal();
//        renderQuad1(matrix4f, matrix3f, pConsumer, pX0, pX1, pMinY, pMaxY, pZ0, pZ1, pMinU, pMaxU, pMinV, pMaxV);
//        renderQuad1(matrix4f, matrix3f, pConsumer, pX2, pX3, pMinY, pMaxY, pZ2, pZ3, pMinU, pMaxU, pMinV, pMaxV);
//        renderQuad1(matrix4f, matrix3f, pConsumer, pX1, pX3, pMinY, pMaxY, pZ1, pZ3, pMinU, pMaxU, pMinV, pMaxV);
//        renderQuad1(matrix4f, matrix3f, pConsumer, pX0, pX2, pMinY, pMaxY, pZ2, pZ0, pMinU, pMaxU, pMinV, pMaxV);
//    }

    // 绘制矩形
//    private void renderQuad(Matrix4f pPose, Matrix3f pNormal, VertexConsumer pConsumer, float pMinU, float pMaxU, float pMinV, float pMaxV, BlockPos localNodePos, BlockPos remoteNodePos, float pOffset) {
//        // 以方块中心为基点，通过偏移量计算矩形顶点的各个位置
//        float minX = localNodePos.getX() - pOffset;
//        float maxX = localNodePos.getX() + pOffset;
//        float minY = localNodePos.getY() - pOffset;
//        float maxY = localNodePos.getY() + pOffset;
//        float minZ = localNodePos.getZ() - pOffset;
//        float maxZ = localNodePos.getZ() + pOffset;
//        float minX = remoteNodePos.getX() - pOffset;
//        float maxX = remoteNodePos.getX() + pOffset;
//        float minY = remoteNodePos.getY() - pOffset;
//        float maxY = remoteNodePos.getY() + pOffset;
//        float minZ = remoteNodePos.getZ() - pOffset;
//        float maxZ = remoteNodePos.getZ() + pOffset;
//        addVertex1(pPose, pNormal, pConsumer, pMinX, pMaxY, pMinZ, pMaxU, pMinV);
//        addVertex1(pPose, pNormal, pConsumer, pMinX, pMinY, pMinZ, pMaxU, pMaxV);
//        addVertex1(pPose, pNormal, pConsumer, pMaxX, pMinY, pMaxZ, pMinU, pMaxV);
//        addVertex1(pPose, pNormal, pConsumer, pMaxX, pMaxY, pMaxZ, pMinU, pMinV);
//    }

    private void renderBeam(Matrix4f pPose, Matrix3f pNormal, VertexConsumer pConsumer, float pMinU, float pMaxU, float pMinV, float pMaxV, BlockPos localNodePos, BlockPos remoteNodePos, float pOffset) {
        // 计算光束中心线的方向向量
        Vector3f direction = new Vector3f(remoteNodePos.getX() - localNodePos.getX(),
                remoteNodePos.getY() - localNodePos.getY(),
                remoteNodePos.getZ() - localNodePos.getZ());
        // 归一化方向向量(不知道啥意思)
        direction = direction.normalize();

        // 从每个端点沿垂直于光束方向向两侧扩展一定距离得到四个顶点
        // 假设上侧为Y轴正方向，得到左侧偏移
        Vector3f leftOffset = direction.cross(new Vector3f(0, 1, 0)).mul(pOffset);
        // 得到右侧偏移
        Vector3f rightOffset = direction.cross(new Vector3f(0, -1, 0)).mul(pOffset);

        // 生成四个顶点坐标
        // 暂时没想到什么好的实现...
        int lx = localNodePos.getX();
        int rx = remoteNodePos.getX();
        int ly = localNodePos.getY();
        int ry = remoteNodePos.getY();
        int lz = localNodePos.getZ();
        int rz = remoteNodePos.getZ();
        Vector3f topLeft = new Vector3f(lx, ly, lz).add(leftOffset);
        Vector3f topRight = new Vector3f(lx, ly, lz).add(rightOffset);
        Vector3f bottomLeft = new Vector3f(rx, ry, rz).sub(leftOffset);
        Vector3f bottomRight = new Vector3f(rx, ry, rz).sub(rightOffset);

        // 添加顶点，顺序决定光束的正面朝向（这里假设Z轴正向为视锥前方）
        addVertex(pPose, pNormal, pConsumer, topLeft.x, topLeft.y, topLeft.z, pMaxU, pMinV);
        addVertex(pPose, pNormal, pConsumer, topRight.x, topRight.y, topRight.z, pMaxU, pMaxV);
        addVertex(pPose, pNormal, pConsumer, bottomRight.x, bottomRight.y, bottomRight.z, pMinU, pMaxV);
        addVertex(pPose, pNormal, pConsumer, bottomLeft.x, bottomLeft.y, bottomLeft.z, pMinU, pMinV);
    }

    // 添加顶点
    private void addVertex(Matrix4f pPose, Matrix3f pNormal, VertexConsumer pConsumer, float pX, float pY, float pZ, float pU, float pV) {
        pConsumer.vertex(pPose, pX, pY, pZ)
                .color(1.0f, 5.0f, 5.0f, pConsumer == TRANSLUCENT_BUFFER ? 0.125f : 1.0f)
                .uv(pU, pV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(pNormal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public void render(PowerNodeBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        // 获取 此节点 存储的 待连接节点
        List<BlockPos> nodesToBeConnected = pBlockEntity.getConnectedNodes();
//        MindustryConstants.logger.debug(String.valueOf(nodesToBeConnected));
        long gameTime = pBlockEntity.getLevel().getGameTime();

        /*
          1. get 存储的每个待连接的节点坐标
          2. 绘制矩形
          3. 绘制四个矩形
          4. 贴图
          5. 随时间旋转
         */
        NO_TRANSPARENCY_BUFFER = pBuffer.getBuffer(RenderType.beaconBeam(BEAM_LOCATION, false));
        TRANSLUCENT_BUFFER = pBuffer.getBuffer(RenderType.beaconBeam(BEAM_LOCATION, true));

        // 此节点的坐标
        BlockPos localNodePos = pBlockEntity.getBlockPos();
        for (BlockPos node : nodesToBeConnected) {
//            MindustryConstants.logger.debug(String.valueOf(node));
            pPoseStack.pushPose();
            renderBeam(pPoseStack.last().pose(), pPoseStack.last().normal(), TRANSLUCENT_BUFFER, 0.0F, 1.0F, 0.0F, 1.0F, localNodePos, node, 0.5F);
            pPoseStack.popPose();
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

    @Override
    public boolean shouldRender(PowerNodeBlockEntity pBlockEntity, Vec3 pCameraPos) {
        return Vec3.atCenterOf(pBlockEntity.getBlockPos())
                .multiply(1.0D, 0.0D, 1.0D)
                .closerThan(pCameraPos.multiply(1.0D, 0.0D, 1.0D), (double) this.getViewDistance());
    }
}
