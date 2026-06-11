package com._101929ha.autoportalwaypoints;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;

import java.lang.reflect.Field;
import java.util.HashMap;

public class YACLConfigFabric {
    public static ConfigClassHandler<YACLConfigFabric> HANDLER = ConfigClassHandler.createBuilder(YACLConfigFabric.class)
        //.id(ResourceLocation.fromNamespaceAndPath("autoportalwaypoints", "config"))
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            //.setPath(FabricLoader.getInstance().getConfigDir().resolve("my_mod.json"))
            .setPath(YACLPlatform.getConfigDir().resolve("autoportalwaypoints-client.json"))
            .setJson5(false)
            .build())
        .build();

    /**
     * Stolen from Bridging Mod, since the YACL documentation is outdated, to say the least
     * https://github.com/squeeglii/BridgingMod/blob/latest/common/src/main/java/me/cg360/mod/bridging/config/BridgingConfig.java
     */
    public YACLConfigFabric() {
        this.saveDefaults(); // This should be run before /any/ saving or loading occurs.
    }

    private HashMap<String, Object> defaultValues = null;
    /**
     * Stolen from Bridging Mod
     * https://github.com/squeeglii/BridgingMod/blob/latest/common/src/main/java/me/cg360/mod/bridging/config/helper/DefaultValueTracker.java
     */
    public void saveDefaults() {
        if(this.defaultValues != null) {
            Constants.LOG.warn("Tried to re-save the defaults variables for object. These are locked!");
            return;
        }

        this.defaultValues = new HashMap<>();

        // Try to get the value of every field and store it.
        for(Field field: this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(this);
                this.defaultValues.put(field.getName(), value);
            } catch (Exception err) {
                Constants.LOG.warn("Unable to get value when saving defaults! Unexpected! [%s]".formatted(err.getMessage()));
                return;
            }
        }
    }

    @SerialEntry
    public static boolean beaconsEnabled = false;
    @SerialEntry
    public static int duplicateProximity = 3;

}
