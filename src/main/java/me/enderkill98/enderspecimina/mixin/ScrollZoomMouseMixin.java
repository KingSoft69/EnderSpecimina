package me.enderkill98.enderspecimina.mixin;

import me.enderkill98.enderspecimina.Mod;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Mouse.class)
public class ScrollZoomMouseMixin {

    @Inject(at = @At("HEAD"), method = "onMouseScroll", cancellable = true)
    public void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo info) {
        if(Mod.scrollZoom.onScroll(vertical))
            info.cancel();
    }

    @ModifyArgs(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    public void updateMouse(Args args) {
        if(!Mod.scrollZoom.isActive() || Mod.scrollZoom.zoomStep <= 0) return;
        double cursorDeltaX = args.get(0);
        double cursorDeltaY = args.get(1);

        cursorDeltaX /= Mod.scrollZoom.getZoomMultiplierFactor();
        cursorDeltaY /= Mod.scrollZoom.getZoomMultiplierFactor();

        args.set(0, cursorDeltaX);
        args.set(1, cursorDeltaY);
    }
}
