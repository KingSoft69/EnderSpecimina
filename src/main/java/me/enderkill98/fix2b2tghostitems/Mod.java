package me.enderkill98.fix2b2tghostitems;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class Mod implements ModInitializer {

    public static ScrollZoom scrollZoom;

    @Override
    public void onInitialize() {
        scrollZoom = new ScrollZoom();
        ClientTickEvents.START_CLIENT_TICK.register(scrollZoom);
    }
}
