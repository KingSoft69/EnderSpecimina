package me.enderkill98.fix2b2tghostitems.mixin;

import me.enderkill98.fix2b2tghostitems.Mod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public abstract class ScrollZoomCameraMixin {

    @Redirect(method = "update",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;clipToSpace(F)F"))
    public float clipToSpaceFromUpdate(Camera instance, float desiredCameraDistance) {
        if(!Mod.scrollZoom.isActive() || Mod.scrollZoom.zoomStep >= 0)
            return instance.clipToSpace(desiredCameraDistance);
        float interpolatedExtraDist;
        if(Mod.scrollZoom.previousTickZoomStep <= 1) {
            double previousFactor = Mod.scrollZoom.getPreviousZoomMultiplierFactor();
            if(Mod.scrollZoom.previousTickZoomStep == 1)
                previousFactor = 0;
            interpolatedExtraDist = (float) MathHelper.lerp(MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true), -previousFactor, -Mod.scrollZoom.getZoomMultiplierFactor());
        } else
            interpolatedExtraDist = (float) -Mod.scrollZoom.getZoomMultiplierFactor();
        //return instance.clipToSpace(desiredCameraDistance + interpolatedExtraDist);
        // Ignore walls. Huge distances could cause issues anyway
        return desiredCameraDistance + interpolatedExtraDist;
    }

}
