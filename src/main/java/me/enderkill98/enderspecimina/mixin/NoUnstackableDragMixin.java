package me.enderkill98.enderspecimina.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class NoUnstackableDragMixin<T extends ScreenHandler> {

    @Shadow @Final protected T handler;

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    public void mouseDragged(CallbackInfoReturnable<Boolean> cir) {
        if(!handler.getCursorStack().isEmpty() && !handler.getCursorStack().isStackable())
            cir.setReturnValue(true); // Prevent initiating a drag, if item is unstackable
    }

}
