package me.enderkill98.enderspecimina.render;

import me.enderkill98.enderspecimina.Mod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RenderManager {

    public static final Logger LOGGER = Mod.getLoggerFor(RenderManager.class);

    private static final HashMap<Class<?>, ArrayList<Renderable>> RENDERABLES = new HashMap<>();
    public static @Nullable Framebuffer overrideMinecraftFramebuffer = null;

    public static void addRenderable(Class<?> owner, Renderable renderable) {
        synchronized (RENDERABLES) {
            if(RENDERABLES.containsKey(owner))
                RENDERABLES.get(owner).add(renderable);
            else {
                ArrayList<Renderable> renderablesList = new ArrayList<>();
                renderablesList.add(renderable);
                RENDERABLES.put(owner, renderablesList);
            }
        }
    }

    public static void removeAllRenderables(Class<?> owner) {
        synchronized (RENDERABLES) {
            RENDERABLES.remove(owner);
        }
    }

    public static void removeRenderables(Class<?> owner, Renderable... renderables) {
        synchronized (RENDERABLES) {
            ArrayList<Renderable> renderablesList = RENDERABLES.getOrDefault(owner, null);
            if(renderablesList == null) return;
            for(Renderable r : renderables)
                renderablesList.remove(r);
        }
    }

    public static void clearRenderables(Class<?> owner) {
        synchronized (RENDERABLES) {
            if(RENDERABLES.containsKey(owner))
                RENDERABLES.get(owner).clear();
        }
    }

    public static int countRenderables(Class<?> owner) {
        synchronized (RENDERABLES) {
            if(RENDERABLES.containsKey(owner))
                return RENDERABLES.get(owner).size();
        }
        return 0;
    }

    public static void setRenderables(Class<?> owner, Renderable... renderables) {
        synchronized (RENDERABLES) {
            if(RENDERABLES.containsKey(owner)) {
                ArrayList<Renderable> renderablesList = RENDERABLES.get(owner);
                renderablesList.clear();
                for(Renderable renderable : renderables)
                    renderablesList.add(renderable);
            } else {
                ArrayList<Renderable> renderablesList = new ArrayList<>(renderables.length);
                for(Renderable renderable : renderables)
                    renderablesList.add(renderable);
                RENDERABLES.put(owner, renderablesList);
            }
        }
    }

    public static void setRenderables(Class<?> owner, ArrayList<Renderable> renderablesList) {
        synchronized (RENDERABLES) {
            RENDERABLES.put(owner, renderablesList);
        }
    }

    public static void renderMap(MinecraftClient client, DrawContext context, @Nullable TextRenderer textRenderer, MatrixStack matrices, VertexConsumerProvider.Immediate renderTypeBuffers, VertexConsumer lineBufferBuilder, double mapScale, double cameraX, double cameraZ, float tickDelta, MapInfo mapInfo) {
        final Profiler profiler = Profilers.get();
        profiler.push("HelloFabric-RenderMap");
        synchronized (RENDERABLES) {
            for(Map.Entry<Class<?>, ArrayList<Renderable>> entry : RENDERABLES.entrySet()) {
                if(entry.getValue().isEmpty()) continue;
                final String ownerName = entry.getKey().getSimpleName();
                profiler.push(ownerName);
                try {
                    for(Renderable renderable : entry.getValue())
                        renderable.renderMap(client, context, textRenderer, matrices, renderTypeBuffers, lineBufferBuilder, mapScale, cameraX, cameraZ, tickDelta, mapInfo);
                }catch (Exception ex) {
                    LOGGER.error("Failed to execute Map-Rendering (type: " + mapInfo.type + ") for " + ownerName + " (any of " + entry.getValue().size() + " entries)!", ex);
                }
                profiler.pop();
            }
        }
        profiler.pop();
    }

    public enum MapType {
        Worldmap,
        Minimap,
    }

    public enum MapWorld {
        Overworld,
        Nether,
        End,
    }

    public record MapInfo(MapType type, MapWorld world, double minX, double minZ, double maxX, double maxZ) {
        public boolean isInside(double x, double z) {
            // +-1's beware
            return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
        }
    }

}
