package com._101929ha.autoportalwaypoints;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.gui.controllers.string.number.IntegerFieldController;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parentScreen -> YetAnotherConfigLib.create(YACLConfigFabric.HANDLER, (defaults, config, builder) -> builder
        //YACLConfigFabric.HANDLER.load();
        //return YetAnotherConfigLib.create(YACLConfigFabric.HANDLER, (defaults, config, builder) -> builder
            .title(Component.literal("Auto Portal Waypoints"))
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("Config"))
                .option(Option.<Boolean>createBuilder()
                    .name(Component.literal("Beacons Enabled"))
                    .description(OptionDescription.createBuilder()
                        .text(Component.empty()
                            .append(Component.literal("Whether the 'Portals' WaypointGroup will have beacons enabled.\n"))
                            .append(Component.literal("Beacons can ruin the experience of supported spaceship mods such as Create: Northstar - Redux.\n"))
                            .append(Component.literal("The 'Portals' waypoint group must be deleted for this to take effect, or the waypoint group's settings can be changed manually."))
                        )
                        .build())
                        .binding(
                            defaults.beaconsEnabled,
                            () -> config.beaconsEnabled,
                            (value) -> config.beaconsEnabled = value
                        )
                        .controller(BooleanControllerBuilder::create)
                        .build()
                    )
                    .option(Option.<Integer>createBuilder()
                        .name(Component.literal("Duplicate Proximity"))
                        .description(OptionDescription.createBuilder()
                            .text(Component.empty()
                                .append(Component.literal("Searches within a cube of this radius to find pre-existing waypoints.\n"))
                                .append(Component.literal("If a waypoint is within the radius, a new waypoint will not be created.\n"))
                                .append(Component.literal("Increase this if using large portals (e.g. for Create trains)\n"))
                                .append(Component.literal("Set to -1.0 to disable this mod"))
                            )
                            .build())
                            .binding(
                                defaults.duplicateProximity,
                                () -> config.duplicateProximity,
                                (value) -> config.duplicateProximity = value
                            )
                            .customController(IntegerFieldController::new)
                            .build()
                    )
                .build()
            )
        )
        .generateScreen(parentScreen);
    }
}