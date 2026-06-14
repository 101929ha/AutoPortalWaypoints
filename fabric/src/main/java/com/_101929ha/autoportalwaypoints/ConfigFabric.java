package com._101929ha.autoportalwaypoints;

import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class ConfigFabric{
	/**
	public ConfigFabric(ModContainer modContainer) {
		ConfigFabricRegister.register();
		modContainer.registerConfig(ModConfig.Type.CLIENT, ConfigFabric.SPEC);
	}*/


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

    static final ModConfigSpec SPEC = BUILDER.build();
}
