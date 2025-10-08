package me.enderkill98.enderspecimina;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class Mod implements ClientModInitializer {

    public static ScrollZoom scrollZoom;

    @Override
    public void onInitializeClient() {
        scrollZoom = new ScrollZoom();
        ClientTickEvents.START_CLIENT_TICK.register(scrollZoom);
    }
}
