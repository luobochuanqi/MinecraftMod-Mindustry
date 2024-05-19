package com.hechu.mindustry.client.renderer.blockentity.PowerNode;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

public class PowerLineRenderType extends RenderType {

    private static final RenderStateShard.LineStateShard THICK_LINES = new LineStateShard(OptionalDouble.of(5.0D));
    // String pName, VertexFormat pFormat,
    // VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling,
    // boolean pSortOnUpload, CompositeState pState
    public static final RenderType POWER_LINE = RenderType.create("power_line",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.LINES, 256, false, true,
            RenderType.CompositeState.builder().setLineState(THICK_LINES)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setTextureState(NO_TEXTURE)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setCullState(NO_CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false));

    public PowerLineRenderType(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }
}
