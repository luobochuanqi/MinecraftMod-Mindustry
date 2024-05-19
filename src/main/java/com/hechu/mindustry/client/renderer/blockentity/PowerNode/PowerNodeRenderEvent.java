package com.hechu.mindustry.client.renderer.blockentity.PowerNode;

import com.hechu.mindustry.world.level.block.Equipment.PowerNodeBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.List;

public class PowerNodeRenderEvent {
    public static void render(RenderLevelStageEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        locateTileEntities(player, event.getPoseStack());
    }

    private static void powerLine(VertexConsumer builder, Matrix4f positionMatrix, BlockPos pos1, BlockPos pos2) {
        builder.vertex(positionMatrix, pos1.getX(), pos1.getY(), pos1.getZ())
                .color(1.0f, 0.0f, 0.0f, 1.0f)
                .endVertex();
        builder.vertex(positionMatrix, pos2.getX(), pos2.getY(), pos2.getZ())
                .color(1.0f, 0.0f, 0.0f, 1.0f)
                .endVertex();
    }

    private static void locateTileEntities(LocalPlayer localPlayer, PoseStack matrixStack) {
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer builder = buffer.getBuffer(PowerLineRenderType.POWER_LINE);

        Level world = localPlayer.level();

        BlockPos playerPos = localPlayer.blockPosition();
//        int px = playerPos.getX();
//        int py = playerPos.getY();
//        int pz = playerPos.getZ();

        matrixStack.pushPose();

        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);

        Matrix4f matrix = matrixStack.last().pose();

        for (int dx = -10; dx <= 10; dx++) {
            for (int dy = -10; dy <= 10; dy++) {
                for (int dz = -10; dz <= 10; dz++) {
                    BlockPos blockPos = playerPos.offset(dx, dy, dz);
                    BlockEntity blockEntity = world.getBlockEntity(blockPos);
                    if (blockEntity instanceof PowerNodeBlockEntity) {
                        PowerNodeBlockEntity powerNodeBlockEntity = (PowerNodeBlockEntity) blockEntity;
                        List<BlockPos> connectedNodeslist = powerNodeBlockEntity.getConnectedNodes();
                        if (!connectedNodeslist.isEmpty()) {
                            for (BlockPos node : connectedNodeslist) {
                                powerLine(builder, matrix, blockPos, node);
                            }
                        }
                    }
                }
            }
        }

        matrixStack.popPose();

        RenderSystem.disableDepthTest();
        buffer.endBatch(PowerLineRenderType.POWER_LINE);
    }
}
