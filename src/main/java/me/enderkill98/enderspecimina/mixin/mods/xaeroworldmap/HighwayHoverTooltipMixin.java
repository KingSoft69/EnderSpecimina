package me.enderkill98.enderspecimina.mixin.mods.xaeroworldmap;

import com.llamalad7.mixinextras.sugar.Local;
import me.enderkill98.enderspecimina.HighwayVisualizer;
import me.enderkill98.enderspecimina.Mod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.MapProcessor;
import xaero.map.graphics.MapRenderHelper;
import xaero.map.gui.GuiMap;
import xaero.map.gui.ScreenBase;

@Mixin(value = GuiMap.class, remap = false)
public class HighwayHoverTooltipMixin extends ScreenBase {

    @Shadow
    private int mouseBlockPosX;

    @Shadow private int mouseBlockPosZ;

    @Shadow private double scale;

    @Shadow private MapProcessor mapProcessor;

    protected HighwayHoverTooltipMixin(Screen parent, Screen escape, Text titleIn) {
        super(parent, escape, titleIn);
    }

    @Inject(method = "render", at = @At(value = "FIELD", target = "Lxaero/map/settings/ModSettings;coordinates:Z", opcode = Opcodes.GETFIELD, ordinal = 0, remap = false), remap = true)
    public void shownHighwayName(DrawContext guiGraphics, int scaledMouseX, int scaledMouseY, float partialTicks, final CallbackInfo ci, @Local(name = "backgroundVertexBuffer") VertexConsumer backgroundVertexBuffer) {
        if(!Mod.highwayVisualizer.isActive()) return;
        if(mapProcessor.getMapWorld().getCurrentDimension().getDimId() == World.END) return;
        double maxDist = 16 / scale;
        HighwayVisualizer.Highway highway = Mod.highwayVisualizer.findClosestHighway(mouseBlockPosX, mouseBlockPosZ, maxDist, mapProcessor.getMapWorld().getCurrentDimension().getDimId() == World.OVERWORLD);
        if(highway == null) {
            Mod.highwayVisualizer.highlightedHighwayName = null;
            Mod.highwayVisualizer.highlightedHighwayNameAt = -1L;
            return;
        }
        String highwayName = highway.toString();
        Mod.highwayVisualizer.highlightedHighwayName = highwayName;
        Mod.highwayVisualizer.highlightedHighwayNameAt = Util.getMeasuringTimeMs();
        MapRenderHelper.drawStringWithBackground(guiGraphics, textRenderer, highwayName, scaledMouseX + 10, scaledMouseY + 5, -1, 0.0F, 0.0F, 0.0F, 0.4F, backgroundVertexBuffer);
    }

}
