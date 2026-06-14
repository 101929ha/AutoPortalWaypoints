package com._101929ha.autoportalwaypoints;

import com.lightning.northstar.world.dimension.NorthstarDimensions;
import com.st0x0ef.stellaris.common.data.planets.StellarisData;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.client.event.MappingEvent;
import journeymap.api.v2.common.event.ClientEventRegistry;
import journeymap.api.v2.common.waypoint.Waypoint;
import journeymap.api.v2.common.waypoint.WaypointFactory;
import journeymap.api.v2.common.waypoint.WaypointGroup;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.List;

@journeymap.api.v2.common.JourneyMapPlugin(apiVersion = "2.0.0")
public class AutoPortalWaypointsFabricJMPlugin implements IClientPlugin {
    private static IClientAPI jmAPI = null;

    static ResourceKey<Level> entryDim;
    static ResourceKey<Level> destinationDim;

    static boolean waitingForNextTick = true; //NOTE must be true in Fabric to set entryDim
    static boolean waitingForChunkLoad = false;

    static Level level;
    static List<ResourceKey<Level>> exclusions = new ArrayList<>(); //For any dimension that doesn't use the typical portal/return portal
    static List<ResourceKey<Level>> exclusionsDestinationOnly = new ArrayList<>(); //Orbit doesn't have a surface, so this will disable the waypoint algorithm so it doesn't try to find land all the time
    static List<ResourceKey<Level>> planets = new ArrayList<>(); //For dimensions that are accessed by falling from the sky

    static ResourceLocation netherPortalIcon = ResourceLocation.fromNamespaceAndPath("journeymap","textures/waypoint/icon/01_structures/jwi25portal_nether_lit.png");
    static ResourceLocation flagIcon = ResourceLocation.fromNamespaceAndPath("journeymap","textures/waypoint/icon/00_markers/jwi13flag_full.png");

    static WaypointGroup waypointgroup = WaypointFactory.createWaypointGroup(Constants.MOD_ID, "Portals");

    /** // @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();
    }*/

    public static boolean isDuplicateWaypoint(BlockPos coord, ResourceKey<Level> dim) {
        List<? extends Waypoint> waypoints = jmAPI.getAllWaypoints(dim); //check all waypoints in this dimension
        for (int i = 0; i < waypoints.size(); i++) {
            Waypoint waypoint = waypoints.get(i);
            if (ConfigFabric.DUPLICATE_PROXIMITY.get().equals(-1.0)) {
            //if (YACLConfigFabric.HANDLER.instance().duplicateProximity == -1) {
                return true; // simplest way to turn this mod off :)
            }
            if (waypoint.getBlockPos().closerThan(coord, ConfigFabric.DUPLICATE_PROXIMITY.get())){ //if it is within a cube of radius 3, consider it a duplicate
            //if (waypoint.getBlockPos().closerThan(coord, YACLConfigFabric.HANDLER.instance().duplicateProximity)){ //if it is within a cube of radius 3, consider it a duplicate
                return true; //a waypoint is too close, it is probably a duplicate
            }
        }
        return false; //none of the existing waypoints are too close
    }

    public void mappingStageEvent(MappingEvent event) {
        if (jmAPI.getWaypointGroupByName(Constants.MOD_ID, "Portals") == null) { // If we haven't made a WaypointGroup yet
            jmAPI.addWaypointGroup(waypointgroup);
        }
    }

    @Override
    public String getModId() {
        return Constants.MOD_ID;
    }

    @Override
    public void initialize(IClientAPI jmClientApi) {
        Constants.LOG.info("Initialised");
        this.jmAPI = jmClientApi;
        ClientEventRegistry.MAPPING_EVENT.subscribe(Constants.MOD_ID, this::mappingStageEvent);


        NeoForgeConfigRegistry.INSTANCE.register(
                Constants.MOD_ID,
                ModConfig.Type.CLIENT,
                ConfigFabric.SPEC,
                Constants.MOD_ID + "-client.toml"
        );

        //YACLConfigFabric.HANDLER.load(); //Stolen from Bridging Mod

        //Constants.LOG.info("Initialized " + getClass().getName());
        //waypointgroup.setShowBeacon(YACLConfigFabric.HANDLER.instance().beaconsEnabled); // Stolen from Bridging Mod
        waypointgroup.setShowBeacon(ConfigFabric.BEACONS_ENABLED.get());
        waypointgroup.setOverrideSettings(true);


        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> {
            if (exclusions.isEmpty()) { //exclusions should always contain Level.END. If it doesn't, then this is the first time changing dimensions. Please don't change dependency mods after changing dimensions.
                exclusions.add(Level.END);
                if (FabricLoader.getInstance().isModLoaded("northstar")) {
                    exclusionsDestinationOnly.add(NorthstarDimensions.EARTH_ORBIT_DIM_KEY);
                    planets.add(NorthstarDimensions.MARS_DIM_KEY);
                    planets.add(NorthstarDimensions.MERCURY_DIM_KEY);
                    planets.add(NorthstarDimensions.MOON_DIM_KEY); //Yes, I know that the moon isn't a planet. This is just revenge for astrophysicists calling all post-Helium elements 'metals'.
                    planets.add(NorthstarDimensions.VENUS_DIM_KEY);
                }
                if (FabricLoader.getInstance().isModLoaded("stellaris")) {
                    for (int i = 0; i < StellarisData.getPlanets().size(); i++) {
                        if(StellarisData.getPlanets().get(i).dimension().compareTo(ResourceLocation.withDefaultNamespace("overworld")) != 0) { //If it's not overworld (because overworld is one of the planets)
                            planets.add(ResourceKey.create(Registries.DIMENSION, StellarisData.getPlanets().get(i).dimension()));
                        }
                    }

                }
            }

            //entryDim = event.getOldPlayer().level().dimension();
            //destinationDim = event.getNewPlayer().level().dimension();
            destinationDim = world.dimension(); //NOTE this is Fabric-unique code

            if (exclusions.contains(destinationDim) || exclusions.contains(entryDim) || exclusionsDestinationOnly.contains(destinationDim)){
                Constants.LOG.info("This dimension change is ineligible for a portal");
                entryDim = Minecraft.getInstance().player.level().dimension(); //NOTE this is Fabric-unique code
                return; //don't wait for dimension to load, therefore don't do anything when entering/exiting the End or when entering orbit
            }

            waitingForNextTick = true;
        });

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            if (waitingForNextTick && Minecraft.getInstance().player != null) { //Player has changed dimensions, now we need to wait for the dimension to load properly.
                if (entryDim == null) {  //NOTE this is Fabric-unique code
                    waitingForNextTick = false;
                    entryDim = Minecraft.getInstance().player.level().dimension();
                    return; //To prevent creating waypoints when logging in
                }
                if (Minecraft.getInstance().player.level().dimension().equals(destinationDim)) { //Teleportation complete. Very important.
                    if (planets.contains(destinationDim) || planets.contains(entryDim)){ //Spacecraft fall from y=1750, so we need to handle these differently
                        if (!waitingForChunkLoad) { //Only want to print this once, rather than every tick :)
                            Constants.LOG.info("Entering or exiting a planet. Waiting for chunk to load before placing waypoint at surface level.");
                        }
                        waitingForChunkLoad = true;
                        return; //we'll handle the waypoint creation in waitingForChunkLoad
                    }

                    if (!isDuplicateWaypoint(Minecraft.getInstance().player.blockPosition(), destinationDim)) { //Make sure there isn't already a waypoint there
                        if (jmAPI.getWaypointGroupByName(Constants.MOD_ID, "Portals") == null) { // If we haven't made a WaypointGroup yet
                            jmAPI.addWaypointGroup(waypointgroup);
                        }
                        Waypoint newWaypoint = WaypointFactory.createWaypoint(Constants.MOD_ID, Minecraft.getInstance().player.blockPosition(), "Portal" , destinationDim, true);
                        newWaypoint.setIconResourceLoctaion(netherPortalIcon);
                        //newWaypoint.setIconIdentifier(flagIcon);
                        newWaypoint.setIconColor(8470739); //The same shade of purple as a nether portal

                        jmAPI.getWaypointGroupByName(Constants.MOD_ID, "Portals").addWaypoint(newWaypoint);
                        Constants.LOG.info("Portal marked at " + Minecraft.getInstance().player.blockPosition() + " in " + destinationDim);
                    } else {
                        Constants.LOG.info("Attempted to mark portal at " + Minecraft.getInstance().player.blockPosition() + " in " + destinationDim + ", but another waypoint is too close");
                    }
                    waitingForNextTick = false;
                    entryDim = Minecraft.getInstance().player.level().dimension(); //NOTE this is Fabric-unique code
                }
            }
        });

        ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            if(waitingForChunkLoad && Minecraft.getInstance().player != null) {
                level = Minecraft.getInstance().player.level();
                if(level.isLoaded(Minecraft.getInstance().player.blockPosition())) { //Check if the chunk is loaded so that the heightmap can be read
                    waitingForChunkLoad = false;
                    waitingForNextTick = false;
                    if (!isDuplicateWaypoint(level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, Minecraft.getInstance().player.blockPosition()), destinationDim)) { //Make sure there isn't already a waypoint there
                        if (jmAPI.getWaypointGroupByName(Constants.MOD_ID, "Portals") == null) { // If we haven't made a WaypointGroup yet
                            jmAPI.addWaypointGroup(waypointgroup);
                        }
                        Waypoint newWaypoint = WaypointFactory.createWaypoint(Constants.MOD_ID, level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, Minecraft.getInstance().player.blockPosition()), "Spaceship" , destinationDim, true);
                        newWaypoint.setIconResourceLoctaion(flagIcon);
                        //newWaypoint.setIconIdentifier(flagIcon);
                        newWaypoint.setLabelColor(16777215); //White

                        jmAPI.getWaypointGroupByName(Constants.MOD_ID, "Portals").addWaypoint(newWaypoint);
                        Constants.LOG.info("Spaceship marked at " + level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, Minecraft.getInstance().player.blockPosition()) + " in " + destinationDim);
                    } else {
                        Constants.LOG.info("Attempted to mark spaceship at " + level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, Minecraft.getInstance().player.blockPosition()) + " in " + destinationDim + ", but another waypoint is too close");
                    }
                    entryDim = Minecraft.getInstance().player.level().dimension(); //NOTE this is Fabric-unique code
                }
            }
        });

    }
}
