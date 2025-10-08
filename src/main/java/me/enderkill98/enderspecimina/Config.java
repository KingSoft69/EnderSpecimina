package me.enderkill98.enderspecimina;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Config {

    public static enum ActivationPolicy implements NameableEnum {
        Always,
        Only2b2t,
        Never;

        @Override
        public Text getDisplayName() {
            return switch (this) {
                case Only2b2t -> Text.literal("§6Only on 2b2t");
                case Always -> Text.literal("§2Always");
                case Never -> Text.literal("§4Never");
            };
        }

        public boolean isActive(final MinecraftClient client) {
            switch (this) {
                case Always -> { return true; }
                case Only2b2t -> {
                    final ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
                    if(networkHandler == null) return false;
                    final ServerInfo serverInfo =networkHandler.getServerInfo();
                    if(serverInfo == null || serverInfo.address == null) return false;
                    final String addrLc = serverInfo.address.toLowerCase();
                    return addrLc.equals("2b2t.org") || addrLc.endsWith(".2b2t.org");
                }
                default -> { return false; }
            }
        }
    }

    public static ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
            .id(Identifier.of("enderstuff", "config"))
                    .serializer(config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(FabricLoader.getInstance().getConfigDir().resolve("enderstuff.json5"))
                            .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                            .setJson5(true)
                            .build())
                    .build();

    @SerialEntry(comment = "Prevent ghost items on 2b2t")
    public ActivationPolicy fix2b2tGhostItems = ActivationPolicy.Only2b2t;
    @SerialEntry(comment = "Enable scroll zoom")
    public boolean scrollZoom = true;
    @SerialEntry(comment = "Make bundles usable on 2b2t")
    public ActivationPolicy fix2b2tBundles = ActivationPolicy.Only2b2t;
}
