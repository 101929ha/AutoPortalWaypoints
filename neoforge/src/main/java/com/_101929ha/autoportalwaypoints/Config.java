package com._101929ha.autoportalwaypoints;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import net.neoforged.fml.ModContainer;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {

	public Config(ModContainer modContainer) {
		modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
	}

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    
    public static final ModConfigSpec.DoubleValue DUPLICATE_PROXIMITY = BUILDER
    		.comment("Searches within a cube of this radius to find pre-existing waypoints.",
    				"If a waypoint is within the radius, a new waypoint will not be created.",
    				"Increase this if using large portals (e.g. for Create trains)",
    				"Set to -1.0 to disable this mod")
    		.defineInRange("duplicateProximity", 3.0, -1.0, 10000.0);
    
    public static final ModConfigSpec.BooleanValue BEACONS_ENABLED = BUILDER
    		.comment("",
    				"Whether the 'Portals' WaypointGroup will have beacons enabled.",
    				"Beacons can ruin the experience of supported spaceship mods such as Create: Northstar - Redux.",
					"The 'Portals' waypoint group must be deleted for this to take effect, or the waypoint group's settings can be changed manually.")
    		.define("beaconsEnabled", false);
    
    /**
    public static final ModConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("logDirtBlock", true);

    public static final ModConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), () -> "", Config::validateItemName);
     */
    static final ModConfigSpec SPEC = BUILDER.build();
    /**
    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }*/
}
