package com._101929ha.autoportalwaypoints;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
//@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class AutoPortalWaypointsNeoforge {

    public AutoPortalWaypointsNeoforge(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);

        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.
        //Constants.LOG.info("Hello NeoForge world!");
        //CommonClass.init();

    }

}