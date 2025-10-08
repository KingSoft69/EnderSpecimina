package me.enderkill98.enderspecimina.mixin;

import me.enderkill98.enderspecimina.Mod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class ScrollZoomGameRendererMixin {

    @Shadow private float lastFovMultiplier;

    @Shadow private float fovMultiplier;

    @Inject(at = @At("HEAD"), method = "updateFovMultiplier", cancellable = true)
    public void updateFovMultiplier(CallbackInfo info) {
        if(Mod.scrollZoom.isActive() && Mod.scrollZoom.zoomStep > 0) {
            // Re-run code to remove limits imposed there
            float f = 1.0F;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getCameraEntity() instanceof AbstractClientPlayerEntity) {
                AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity) client.getCameraEntity();
                float fovEffectScale = client.options.getFovEffectScale().getValue().floatValue();
                f = abstractClientPlayerEntity.getFovMultiplier(client.options.getPerspective().isFirstPerson(), fovEffectScale);
            }

            f /= Mod.scrollZoom.getZoomMultiplierFactor();

            this.lastFovMultiplier = this.fovMultiplier;
            this.fovMultiplier += (f - this.fovMultiplier) * 0.5F;
            info.cancel();
        }
    }

}
