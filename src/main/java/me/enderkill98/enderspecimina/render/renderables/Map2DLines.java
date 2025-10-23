package me.enderkill98.enderspecimina.render.renderables;

import com.mojang.blaze3d.systems.RenderSystem;
import me.enderkill98.enderspecimina.render.RenderManager;
import me.enderkill98.enderspecimina.render.Renderable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Map2DLines(Line... lines) implements Renderable {

    public record Line(double x1, double y1, double x2, double y2, int argb) {
        public Line withColor(int argb) {
            return new Line(x1, y1, x2, y2, argb);
        }
    }

    @Override
    public void renderMap(MinecraftClient client, DrawContext context, @Nullable TextRenderer textRenderer, MatrixStack matrices, VertexConsumerProvider.Immediate renderTypeBuffers, VertexConsumer lineBufferBuilder, double mapScale, double cameraX, double cameraZ, float tickDelta, RenderManager.MapInfo mapInfo) {
        RenderSystem.lineWidth(1.0f); // Still needed?
        final MatrixStack.Entry matrixEntry = matrices.peek();

        for(Line line : lines)
            addLineAbs(matrixEntry, lineBufferBuilder, cameraX, cameraZ, line.x1, line.y1, line.x2, line.y2, line.argb);
    }

    public static void addLineAbs(MatrixStack.Entry matrixEntry, VertexConsumer vertexBuffer, double cameraX, double cameraZ, double x1, double y1, double x2, double y2, int argb) {
        // Most stupid workaround. Somehow going up (Y/Z) doesn't render at all. Maybe broken normal, couldn't find a working one. :shrug:
        if(y2 < y1) {
            // Swap x and y values
            double origX1 = x1;
            x1 = x2;
            x2 = origX1;

            double origY1 = y1;
            y1 = y2;
            y2 = origY1;
        }

        // Make relative to camera
        x1 -= cameraX;
        y1 -= cameraZ;
        x2 -= cameraX;
        y2 -= cameraZ;

        //float dist = MathHelper.sqrt((float) Math.pow(Math.abs(x2 - x1), 2) + (float) Math.pow(Math.abs(y2 - y1), 2));
        vertexBuffer.vertex(matrixEntry, (float) x1, (float) y1, 0f).color(argb).normal(matrixEntry, (float) (x2 - x1), (float) (y2 - y1), 0f);
        vertexBuffer.vertex(matrixEntry, (float) x2, (float) y2, 0f).color(argb).normal(matrixEntry, (float) (x2 - x1), (float) (y2 - y1), 0f);
    }

    private static class Map2DChunkSides {
        boolean north = true;
        boolean east = true;
        boolean south = true;
        boolean west = true;
    }

    public static Map2DLines createLinesOutliningChunks(List<ChunkPos> chunkPositions, int argb, double inset) {
        final HashMap<ChunkPos, Map2DChunkSides> chunks = new HashMap<>();
        for(ChunkPos chunkPos : chunkPositions)
            chunks.put(chunkPos, new Map2DChunkSides());

        for(Map.Entry<ChunkPos, Map2DChunkSides> entry : chunks.entrySet()) {
            final ChunkPos pos = entry.getKey();
            final Map2DChunkSides sides = entry.getValue();

            // Disable side if chunk is there
            if(chunks.containsKey(new ChunkPos(pos.x, pos.z - 1))) sides.north = false;
            if(chunks.containsKey(new ChunkPos(pos.x + 1, pos.z))) sides.east = false;
            if(chunks.containsKey(new ChunkPos(pos.x, pos.z + 1))) sides.south = false;
            if(chunks.containsKey(new ChunkPos(pos.x - 1, pos.z))) sides.west = false;
        }

        final ArrayList<Line> lines = new ArrayList<>();
        for(Map.Entry<ChunkPos, Map2DChunkSides> entry : chunks.entrySet()) {
            final ChunkPos pos = entry.getKey();
            final Map2DChunkSides sides = entry.getValue();

            // Simple:
            /*
            if(sides.north) lines.add(new Line((pos.x << 4) + 0, (pos.z << 4) + 0, (pos.x << 4) + 16, (pos.z << 4) + 0, argb));
            if(sides.east) lines.add(new Line((pos.x << 4) + 16, (pos.z << 4) + 0, (pos.x << 4) + 16, (pos.z << 4) + 16, argb));
            if(sides.south) lines.add(new Line((pos.x << 4) + 0, (pos.z << 4) + 16, (pos.x << 4) + 16, (pos.z << 4) + 16, argb));
            if(sides.west) lines.add(new Line((pos.x << 4) + 0, (pos.z << 4) + 0, (pos.x << 4) + 0, (pos.z << 4) + 16, argb));
            */

            // With Inset
            if(sides.north) {
                boolean extendStart = !sides.west;
                boolean extendEnd = !sides.east;
                lines.add(new Line((pos.x << 4) + (extendStart ? -inset : inset), (pos.z << 4) + inset, (pos.x << 4) + 16 + (extendEnd ? inset : -inset), (pos.z << 4) + inset, argb));
            }
            if(sides.east) {
                boolean extendStart = !sides.north;
                boolean extendEnd = !sides.south;
                lines.add(new Line((pos.x << 4) + 16 - inset, (pos.z << 4) + 0 + (extendStart ? -inset : inset), (pos.x << 4) + 16 - inset, (pos.z << 4) + 16 + (extendEnd ? inset : -inset), argb));
            }
            if(sides.south) {
                boolean extendStart = !sides.west;
                boolean extendEnd = !sides.east;
                lines.add(new Line((pos.x << 4) + (extendStart ? -inset : inset), (pos.z << 4) + 16 - inset, (pos.x << 4) + 16 + (extendEnd ? inset : -inset), (pos.z << 4) + 16 - inset, argb));
            }
            if(sides.west) {
                boolean extendStart = !sides.north;
                boolean extendEnd = !sides.south;
                lines.add(new Line((pos.x << 4) + 0 + inset, (pos.z << 4) + 0 + (extendStart ? -inset : inset), (pos.x << 4) + 0 + inset, (pos.z << 4) + 16 + (extendEnd ? inset : -inset), argb));
            }
        }

        return new Map2DLines(lines.toArray(Line[]::new));
    }

    private static final int INSIDE  = 0b0000;
    private static final  int LEFT   = 0b0001;
    private static final  int RIGHT  = 0b0010;
    private static final  int BOTTOM = 0b0100;
    private static final  int TOP    = 0b1000;

    private static int computeOutCode(double x, double y, final double vMinX, final double vMinY, final double vMaxX, final double vMaxY) {
        int code = INSIDE;  // initialised as being inside of clip window
        if (x < vMinX) code |= LEFT;        // to the left of clip window
        else if (x > vMaxX) code |= RIGHT;  // to the right of clip window

        if (y < vMinY)  code |= BOTTOM;     // below the clip window
        else if (y > vMaxY) code |= TOP;    // above the clip window
        return code;
    }

    /**
     * https://en.wikipedia.org/wiki/Cohen%E2%80%93Sutherland_algorithm
     */
    public static @Nullable Line clipToViewport(final Line line, final double vMinX, final double vMinY, final double vMaxX, final double vMaxY) {
        // compute outcodes for P0, P1, and whatever point lies outside the clip rectangle
        double x0 = line.x1, y0 = line.y1, x1 = line.x2, y1 = line.y2;
        int outcode0 = computeOutCode(x0, y0, vMinX, vMinY, vMaxX, vMaxY);
        int outcode1 = computeOutCode(x1, y1, vMinX, vMinY, vMaxX, vMaxY);
        boolean accept = false;

        while (true) {
            if ((outcode0 | outcode1) == 0) {
                // bitwise OR is 0: both points inside window; trivially accept and exit loop
                accept = true;
                break;
            } else if ((outcode0 & outcode1) > 0) {
                // bitwise AND is not 0: both points share an outside zone (LEFT, RIGHT, TOP,
                // or BOTTOM), so both must be outside window; exit loop (accept is false)
                break;
            } else {
                // failed both tests, so calculate the line segment to clip
                // from an outside point to an intersection with clip edge
                double x, y;

                // At least one endpoint is outside the clip rectangle; pick it.
                int outcodeOut = outcode1 > outcode0 ? outcode1 : outcode0;

                // Now find the intersection point;
                // use formulas:
                //   slope = (y1 - y0) / (x1 - x0)
                //   x = x0 + (1 / slope) * (ym - y0), where ym is ymin or ymax
                //   y = y0 + slope * (xm - x0), where xm is xmin or xmax
                // No need to worry about divide-by-zero because, in each case, the
                // outcode bit being tested guarantees the denominator is non-zero
                if ((outcodeOut & TOP) > 0) {           // point is above the clip window
                    x = x0 + (x1 - x0) * (vMaxY - y0) / (y1 - y0);
                    y = vMaxY;
                } else if ((outcodeOut & BOTTOM) > 0) { // point is below the clip window
                    x = x0 + (x1 - x0) * (vMinY - y0) / (y1 - y0);
                    y = vMinY;
                } else if ((outcodeOut & RIGHT) > 0) {  // point is to the right of clip window
                    y = y0 + (y1 - y0) * (vMaxX - x0) / (x1 - x0);
                    x = vMaxX;
                } else if ((outcodeOut & LEFT) > 0) {   // point is to the left of clip window
                    y = y0 + (y1 - y0) * (vMinX - x0) / (x1 - x0);
                    x = vMinX;
                }else {
                    throw new RuntimeException("This case should never get triggered!");
                }

                // Now we move outside point to intersection point to clip
                // and get ready for next pass.
                if (outcodeOut == outcode0) {
                    x0 = x;
                    y0 = y;
                    outcode0 = computeOutCode(x0, y0, vMinX, vMinY, vMaxX, vMaxY);
                } else {
                    x1 = x;
                    y1 = y;
                    outcode1 = computeOutCode(x1, y1, vMinX, vMinY, vMaxX, vMaxY);
                }
            }
        }

        return accept ? new Line(x0, y0, x1, y1, line.argb) : null;
    }

}

