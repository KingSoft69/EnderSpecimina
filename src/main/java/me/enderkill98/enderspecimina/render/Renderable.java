package me.enderkill98.enderspecimina.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

public interface Renderable {

    /*default void renderWorld(MinecraftClient client, MatrixStack matrices, VertexConsumerProvider.Immediate entityVertexProvider, VertexConsumer lineBuffer, Camera camera, Matrix4f positionMatrix, float tickDelta) {}
    default boolean renderWorldDepthTest() { return false; }
    default void renderGui(MinecraftClient client, DrawContext context, TextRenderer textRenderer, float tickDelta, boolean isGuiShown, boolean isDebugScreenShown) {}*/

    /**
     * @param textRenderer Only available on WorldMap!
     */
    default void renderMap(MinecraftClient client, DrawContext context, @Nullable TextRenderer textRenderer, MatrixStack matrices, VertexConsumerProvider.Immediate renderTypeBuffers, VertexConsumer lineBufferBuilder, double mapScale, double cameraX, double cameraZ, float tickDelta, RenderManager.MapInfo mapInfo) {}

}
