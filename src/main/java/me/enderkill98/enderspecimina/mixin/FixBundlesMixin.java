package me.enderkill98.enderspecimina.mixin;

import me.enderkill98.enderspecimina.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.BundleTooltipSubmenuHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.BundleItemSelectedC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BundleTooltipSubmenuHandler.class)
public class FixBundlesMixin {

    @Shadow @Final private MinecraftClient client;
    @Unique private Integer enderspecimina$packetSelectedItemIndex = null;

    @Inject(method = "sendPacket", at = @At("HEAD"))
    public void sendPacketHead(ItemStack item, int slotId, int selectedItemIndex, CallbackInfo info) {
        enderspecimina$packetSelectedItemIndex = null;
        if (!Config.HANDLER.instance().fix2b2tBundles.isActive(client)) return;

        if (!item.contains(DataComponentTypes.BUNDLE_CONTENTS)) return;
        if (selectedItemIndex == -1) return; // Ignore nothing selected
        BundleContentsComponent bundleContents = item.get(DataComponentTypes.BUNDLE_CONTENTS);
        if (bundleContents.isEmpty()) return;
        enderspecimina$packetSelectedItemIndex = (bundleContents.size()-1) - selectedItemIndex; // Reverse selection
    }

    @ModifyArg(method = "sendPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0))
    public Packet<?> sendPacketAtSetSelectedItem(Packet<?> packet) {
        if (packet instanceof BundleItemSelectedC2SPacket itemSelPacket && enderspecimina$packetSelectedItemIndex != null) {
            return new BundleItemSelectedC2SPacket(itemSelPacket.slotId(), enderspecimina$packetSelectedItemIndex);
        } else {
            return packet;
        }
    }

}
