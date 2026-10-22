package com.examplebett.item;

import com.examplebett.AiObjectMod;
import com.examplebett.block.AiObjectModBlocks;
import com.examplebett.compat.ModRegistries;
import com.examplebett.entity.AiObjectModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
//? if >=1.19.3 {
import net.minecraft.world.item.CreativeModeTabs;
//?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AiObjectModItems {

    private static Item.Properties withId(Item.Properties properties, ResourceKey<Item> key) {
        return withId(properties, key, true);
    }

    private static Item.Properties withId(Item.Properties properties, ResourceKey<Item> key, boolean showInCreativeTab) {
        //? if >=1.21.2 {
        properties = properties.setId(key);
        //?}
        //? if <1.19.3 {
        /*if (showInCreativeTab) {
            properties = properties.tab(net.minecraft.world.item.CreativeModeTab.TAB_MISC);
        }*/
        //?}
        return properties;
    }

    public static final ResourceKey<Item> BREAKABLE_TIRE_STACK_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("breakable_tire_stack_spawn"));
    public static final BreakableTireStackSpawnItem BREAKABLE_TIRE_STACK_SPAWN_ITEM = new BreakableTireStackSpawnItem(
            level -> new com.examplebett.entity.BreakableTireEntity(AiObjectModEntities.BREAKABLE_TIRE_STACK, level),

            5, 0.2057,
            withId(new Item.Properties(), BREAKABLE_TIRE_STACK_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> TRAFFIC_CONE_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("traffic_cone_spawn"));
    public static final BreakableTireStackSpawnItem TRAFFIC_CONE_SPAWN_ITEM = new BreakableTireStackSpawnItem(
            level -> new com.examplebett.entity.BreakableConeEntity(AiObjectModEntities.TRAFFIC_CONE, level, "traffic_cone"),
            withId(new Item.Properties(), TRAFFIC_CONE_SPAWN_ITEM_KEY));
    public static final ResourceKey<Item> BARRICADE_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("barricade_spawn"));
    public static final BreakableTireStackSpawnItem BARRICADE_SPAWN_ITEM = new BreakableTireStackSpawnItem(
            level -> new com.examplebett.entity.BreakableBarricadeEntity(AiObjectModEntities.BARRICADE, level, "barricade"),
            withId(new Item.Properties(), BARRICADE_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> BREAKABLE_BOX_STACK_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("breakable_box_stack_spawn"));
    public static final BreakableBoxStackSpawnItem BREAKABLE_BOX_STACK_SPAWN_ITEM = new BreakableBoxStackSpawnItem(
            AiObjectModEntities.BREAKABLE_BOX, "breakable_cardboard_box", withId(new Item.Properties(), BREAKABLE_BOX_STACK_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> BREAKABLE_BOX_2_STACK_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("breakable_box_2_stack_spawn"));
    public static final BreakableBoxStackSpawnItem BREAKABLE_BOX_2_STACK_SPAWN_ITEM = new BreakableBoxStackSpawnItem(
            AiObjectModEntities.BREAKABLE_BOX_2, "breakable_cardboard_box_2", withId(new Item.Properties(), BREAKABLE_BOX_2_STACK_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> CAR_BREAKABLE_GLASS_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("car_breakable_glass"));
    public static final BlockItem CAR_BREAKABLE_GLASS_ITEM = new BlockItem(AiObjectModBlocks.CAR_BREAKABLE_GLASS, withId(new Item.Properties(), CAR_BREAKABLE_GLASS_ITEM_KEY));

    public static final ResourceKey<Item> WATER_PUDDLE_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("water_puddle"));
    public static final BlockItem WATER_PUDDLE_ITEM = new BlockItem(AiObjectModBlocks.WATER_PUDDLE, withId(new Item.Properties(), WATER_PUDDLE_ITEM_KEY));

    public static final ResourceKey<Item> NISSAN_400Z_VOXEL_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("nissan_400z_voxel_spawn"));
    public static final CarSpawnItem NISSAN_400Z_VOXEL_SPAWN_ITEM = new CarSpawnItem("nissan_400z_voxel", withId(new Item.Properties(), NISSAN_400Z_VOXEL_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> VOLKSWAGEN_SCIROCCO_VOXEL_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("volkswagen_scirocco_voxel_spawn"));
    public static final CarSpawnItem VOLKSWAGEN_SCIROCCO_VOXEL_SPAWN_ITEM = new CarSpawnItem("volkswagen_scirocco_voxel", withId(new Item.Properties(), VOLKSWAGEN_SCIROCCO_VOXEL_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> DODGE_CHALLENGER_SRT_2020_VOXEL_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("dodge_challenger_srt_2020_voxel_spawn"));
    public static final CarSpawnItem DODGE_CHALLENGER_SRT_2020_VOXEL_SPAWN_ITEM = new CarSpawnItem("dodge_challenger_srt_2020_voxel", withId(new Item.Properties(), DODGE_CHALLENGER_SRT_2020_VOXEL_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> HONDA_CIVIC_HATCHBACK_2000_VOXEL_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("honda_civic_hatchback_2000_voxel_spawn"));
    public static final CarSpawnItem HONDA_CIVIC_HATCHBACK_2000_VOXEL_SPAWN_ITEM = new CarSpawnItem("honda_civic_hatchback_2000_voxel", withId(new Item.Properties(), HONDA_CIVIC_HATCHBACK_2000_VOXEL_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> MCLAREN_F1_VOXEL_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("mclaren_f1_voxel_spawn"));
    public static final CarSpawnItem MCLAREN_F1_VOXEL_SPAWN_ITEM = new CarSpawnItem("mclaren_f1_voxel", withId(new Item.Properties(), MCLAREN_F1_VOXEL_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> CHEVROLET_CORVETTE_C6_VOXEL_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("chevrolet_corvette_c6_voxel_spawn"));
    public static final CarSpawnItem CHEVROLET_CORVETTE_C6_VOXEL_SPAWN_ITEM = new CarSpawnItem("chevrolet_corvette_c6_voxel", withId(new Item.Properties(), CHEVROLET_CORVETTE_C6_VOXEL_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> TOYOTA_SUPRA_A80_1993_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("toyota_supra_a80_1993_spawn"));
    public static final CarSpawnItem TOYOTA_SUPRA_A80_1993_SPAWN_ITEM = new CarSpawnItem("toyota_supra_a80_1993", withId(new Item.Properties(), TOYOTA_SUPRA_A80_1993_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> NISSAN_180SX_1989_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("nissan_180sx_1989_spawn"));
    public static final CarSpawnItem NISSAN_180SX_1989_SPAWN_ITEM = new CarSpawnItem("nissan_180sx_1989", withId(new Item.Properties(), NISSAN_180SX_1989_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> BMW_M3_E46_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("bmw_m3_e46_spawn"));
    public static final CarSpawnItem BMW_M3_E46_SPAWN_ITEM = new CarSpawnItem("bmw_m3_e46", withId(new Item.Properties(), BMW_M3_E46_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> BELL_H13_HELICOPTER_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("bell_h13_helicopter_spawn"));
    public static final CarSpawnItem BELL_H13_HELICOPTER_SPAWN_ITEM = new CarSpawnItem("bell_h13_helicopter", withId(new Item.Properties(), BELL_H13_HELICOPTER_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> MERCEDES_BENZ_GCLASS_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("mercedes_benz_gclass_spawn"));
    public static final CarSpawnItem MERCEDES_BENZ_GCLASS_SPAWN_ITEM = new CarSpawnItem("mercedes_benz_gclass", withId(new Item.Properties(), MERCEDES_BENZ_GCLASS_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> NISSAN_SKYLINE_R34_GTR_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("nissan_skyline_r34_gtr_spawn"));
    public static final CarSpawnItem NISSAN_SKYLINE_R34_GTR_SPAWN_ITEM = new CarSpawnItem("nissan_skyline_r34_gtr", withId(new Item.Properties(), NISSAN_SKYLINE_R34_GTR_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> HONDA_INTEGRA_DC2_TYPE_R_1995_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("honda_integra_dc2_type_r_1995_spawn"));
    public static final CarSpawnItem HONDA_INTEGRA_DC2_TYPE_R_1995_SPAWN_ITEM = new CarSpawnItem("honda_integra_dc2_type_r_1995", withId(new Item.Properties(), HONDA_INTEGRA_DC2_TYPE_R_1995_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> NISSAN_FAIRLADY_300ZX_Z32_1989_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("nissan_fairlady_300zx_z32_1989_spawn"));
    public static final CarSpawnItem NISSAN_FAIRLADY_300ZX_Z32_1989_SPAWN_ITEM = new CarSpawnItem("nissan_fairlady_300zx_z32_1989", withId(new Item.Properties(), NISSAN_FAIRLADY_300ZX_Z32_1989_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> NISSAN_SKYLINE_R32_GTR_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("nissan_skyline_r32_gtr_spawn"));
    public static final CarSpawnItem NISSAN_SKYLINE_R32_GTR_SPAWN_ITEM = new CarSpawnItem("nissan_skyline_r32_gtr", withId(new Item.Properties(), NISSAN_SKYLINE_R32_GTR_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> MAZDA_RX7_FD_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("mazda_rx7_fd_spawn"));
    public static final CarSpawnItem MAZDA_RX7_FD_SPAWN_ITEM = new CarSpawnItem("mazda_rx7_fd", withId(new Item.Properties(), MAZDA_RX7_FD_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> TESLA_CYBERTRUCK_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("tesla_cybertruck_spawn"));
    public static final CarSpawnItem TESLA_CYBERTRUCK_SPAWN_ITEM = new CarSpawnItem("tesla_cybertruck", withId(new Item.Properties(), TESLA_CYBERTRUCK_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> LEXUS_SC300_1993_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("lexus_sc300_1993_spawn"));
    public static final CarSpawnItem LEXUS_SC300_1993_SPAWN_ITEM = new CarSpawnItem("lexus_sc300_1993", withId(new Item.Properties(), LEXUS_SC300_1993_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> MAZDA_MIATA_MX5_NA_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("mazda_miata_mx-5_na_spawn"));
    public static final CarSpawnItem MAZDA_MIATA_MX5_NA_SPAWN_ITEM = new CarSpawnItem("mazda_miata_mx-5_na", withId(new Item.Properties(), MAZDA_MIATA_MX5_NA_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> MAZDA_RX7_FC_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("mazda_rx7_fc_spawn"));
    public static final CarSpawnItem MAZDA_RX7_FC_SPAWN_ITEM = new CarSpawnItem("mazda_rx7_fc", withId(new Item.Properties(), MAZDA_RX7_FC_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> NISSAN_200SX_SILVIA_S14_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("nissan_200sx_silvia_s14_spawn"));
    public static final CarSpawnItem NISSAN_200SX_SILVIA_S14_SPAWN_ITEM = new CarSpawnItem("nissan_200sx_silvia_s14", withId(new Item.Properties(), NISSAN_200SX_SILVIA_S14_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> HONDA_NSX_1990_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("honda_nsx_1990_spawn"));
    public static final CarSpawnItem HONDA_NSX_1990_SPAWN_ITEM = new CarSpawnItem("honda_nsx_1990", withId(new Item.Properties(), HONDA_NSX_1990_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> ROSSA_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("rossa_spawn"));
    public static final CarSpawnItem ROSSA_SPAWN_ITEM = new CarSpawnItem("rossa", withId(new Item.Properties(), ROSSA_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> TOYOTA_COROLLA_AE86_TRUENO_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("toyota_corolla_ae86_trueno_spawn"));
    public static final CarSpawnItem TOYOTA_COROLLA_AE86_TRUENO_SPAWN_ITEM = new CarSpawnItem("toyota_corolla_ae86_trueno", withId(new Item.Properties(), TOYOTA_COROLLA_AE86_TRUENO_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> NISSAN_ONEVIA_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("nissan_onevia_spawn"));
    public static final CarSpawnItem NISSAN_ONEVIA_SPAWN_ITEM = new CarSpawnItem("nissan_onevia", withId(new Item.Properties(), NISSAN_ONEVIA_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> HONDA_INTEGRA_TYPE_R_DB8_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("honda_integra_type_r_db8_spawn"));
    public static final CarSpawnItem HONDA_INTEGRA_TYPE_R_DB8_SPAWN_ITEM = new CarSpawnItem("honda_integra_type_r_db8", withId(new Item.Properties(), HONDA_INTEGRA_TYPE_R_DB8_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> NISSAN_SILVIA_S13_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("nissan_silvia_s13_spawn"));
    public static final CarSpawnItem NISSAN_SILVIA_S13_SPAWN_ITEM = new CarSpawnItem("nissan_silvia_s13", withId(new Item.Properties(), NISSAN_SILVIA_S13_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> NISSAN_SILEIGHTY_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("nissan_sileighty_spawn"));
    public static final CarSpawnItem NISSAN_SILEIGHTY_SPAWN_ITEM = new CarSpawnItem("nissan_sileighty", withId(new Item.Properties(), NISSAN_SILEIGHTY_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> FLATIRON_OPOSSUM_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("flatiron_opossum_spawn"));
    public static final CarSpawnItem FLATIRON_OPOSSUM_SPAWN_ITEM = new CarSpawnItem("flatiron_opossum", withId(new Item.Properties(), FLATIRON_OPOSSUM_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> CYCAS_PIXEL_VAN_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("cycas_pixel_van_spawn"));
    public static final CarSpawnItem CYCAS_PIXEL_VAN_SPAWN_ITEM = new CarSpawnItem("cycas_pixel_van", withId(new Item.Properties(), CYCAS_PIXEL_VAN_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> SCANDZA_GC215_TRACTOR_UNIT_16_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("scandza_gc215_tractor_unit_16_spawn"));
    public static final CarSpawnItem SCANDZA_GC215_TRACTOR_UNIT_16_SPAWN_ITEM = new CarSpawnItem("scandza_gc215_tractor_unit_16", withId(new Item.Properties(), SCANDZA_GC215_TRACTOR_UNIT_16_SPAWN_ITEM_KEY));
    public static final ResourceKey<Item> KAWAGUCHI_KAMARI_17_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("kawaguchi_kamari_17_spawn"));
    public static final CarSpawnItem KAWAGUCHI_KAMARI_17_SPAWN_ITEM = new CarSpawnItem("kawaguchi_kamari_17", withId(new Item.Properties(), KAWAGUCHI_KAMARI_17_SPAWN_ITEM_KEY));
    public static final ResourceKey<Item> CONTAINER_TRAILER_SPAWN_ITEM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("container_trailer_spawn"));
    public static final CarSpawnItem CONTAINER_TRAILER_SPAWN_ITEM = new CarSpawnItem("container_trailer", withId(new Item.Properties(), CONTAINER_TRAILER_SPAWN_ITEM_KEY));

    public static final ResourceKey<Item> CUSTOM_WRENCH_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("custom_wrench"));
    public static final CustomWrenchItem CUSTOM_WRENCH = new CustomWrenchItem(withId(new Item.Properties(), CUSTOM_WRENCH_KEY).stacksTo(1));

    public static final ResourceKey<Item> WHEEL_WRENCH_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("wheel_wrench"));
    public static final WheelWrenchItem WHEEL_WRENCH = new WheelWrenchItem(withId(new Item.Properties(), WHEEL_WRENCH_KEY, false).stacksTo(1));

    public static final ResourceKey<Item> DEBUG_WRENCH_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("debug_wrench"));
    public static final DebugWrenchItem DEBUG_WRENCH = new DebugWrenchItem(withId(new Item.Properties(), DEBUG_WRENCH_KEY).stacksTo(1));

    public static final ResourceKey<Item> WAYPOINT_MARKER_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("waypoint_marker"));
    public static final WaypointMarkerItem WAYPOINT_MARKER = new WaypointMarkerItem(withId(new Item.Properties(), WAYPOINT_MARKER_KEY).stacksTo(1));

    public static final ResourceKey<Item> ONE_WAY_WAYPOINT_MARKER_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("one_way_waypoint_marker"));
    public static final OneWayWaypointMarkerItem ONE_WAY_WAYPOINT_MARKER = new OneWayWaypointMarkerItem(withId(new Item.Properties(), ONE_WAY_WAYPOINT_MARKER_KEY).stacksTo(1));

    public static final ResourceKey<Item> TRAFFIC_SIGNAL_MARKER_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("traffic_signal_marker"));
    public static final TrafficSignalMarkerItem TRAFFIC_SIGNAL_MARKER = new TrafficSignalMarkerItem(withId(new Item.Properties(), TRAFFIC_SIGNAL_MARKER_KEY).stacksTo(1));

    public static final ResourceKey<Item> RAILWAY_WAYPOINT_MARKER_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("railway_waypoint_marker"));
    public static final RailwayWaypointMarkerItem RAILWAY_WAYPOINT_MARKER = new RailwayWaypointMarkerItem(withId(new Item.Properties(), RAILWAY_WAYPOINT_MARKER_KEY).stacksTo(1));

    public static final ResourceKey<Item> RAILWAY_STOP_MARKER_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("railway_stop_marker"));
    public static final RailwayStopMarkerItem RAILWAY_STOP_MARKER = new RailwayStopMarkerItem(withId(new Item.Properties(), RAILWAY_STOP_MARKER_KEY).stacksTo(1));

    public static final ResourceKey<Item> TOW_ROPE_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("tow_rope"));
    public static final TowRopeItem TOW_ROPE = new TowRopeItem(withId(new Item.Properties(), TOW_ROPE_KEY).stacksTo(16));

    public static final ResourceKey<Item> SMART_DISPATCH_APP_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("smart_dispatch_app"));
    public static final SmartDispatchAppItem SMART_DISPATCH_APP = new SmartDispatchAppItem(withId(new Item.Properties(), SMART_DISPATCH_APP_KEY).stacksTo(1));

    public static final ResourceKey<Item> BOXED_CAR_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("boxed_car"));
    public static final BoxedCarItem BOXED_CAR = new BoxedCarItem(withId(new Item.Properties(), BOXED_CAR_KEY, false).stacksTo(1));

    //? if <1.21.4 {
    /*public static final ResourceKey<Item> WARDEN_ENERGY_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("warden_energy"));
    public static final Item WARDEN_ENERGY = new LegacyAvalonItems.Drink(withId(new Item.Properties(), WARDEN_ENERGY_KEY, false).stacksTo(64), false);
    public static final ResourceKey<Item> WARDEN_ENERGY_SONIC_BOOM_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("warden_energy_sonic_boom"));
    public static final Item WARDEN_ENERGY_SONIC_BOOM = new LegacyAvalonItems.Drink(withId(new Item.Properties(), WARDEN_ENERGY_SONIC_BOOM_KEY, false).stacksTo(64), true);
    public static final ResourceKey<Item> CHOCO_BAR_KEY = ResourceKey.create(ModRegistries.ITEM_KEY, AiObjectMod.id("choco_bar"));
    public static final Item CHOCO_BAR = new Item(LegacyAvalonItems.chocoBarProperties(withId(new Item.Properties(), CHOCO_BAR_KEY, false)));*/
    //?}

    public static void register() {
        ModRegistries.register(
                ModRegistries.ITEM,
                NISSAN_400Z_VOXEL_SPAWN_ITEM_KEY,
                NISSAN_400Z_VOXEL_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                VOLKSWAGEN_SCIROCCO_VOXEL_SPAWN_ITEM_KEY,
                VOLKSWAGEN_SCIROCCO_VOXEL_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                DODGE_CHALLENGER_SRT_2020_VOXEL_SPAWN_ITEM_KEY,
                DODGE_CHALLENGER_SRT_2020_VOXEL_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                HONDA_CIVIC_HATCHBACK_2000_VOXEL_SPAWN_ITEM_KEY,
                HONDA_CIVIC_HATCHBACK_2000_VOXEL_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                MCLAREN_F1_VOXEL_SPAWN_ITEM_KEY,
                MCLAREN_F1_VOXEL_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                CHEVROLET_CORVETTE_C6_VOXEL_SPAWN_ITEM_KEY,
                CHEVROLET_CORVETTE_C6_VOXEL_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                CUSTOM_WRENCH_KEY,
                CUSTOM_WRENCH
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                WHEEL_WRENCH_KEY,
                WHEEL_WRENCH
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                DEBUG_WRENCH_KEY,
                DEBUG_WRENCH
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                WAYPOINT_MARKER_KEY,
                WAYPOINT_MARKER
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                ONE_WAY_WAYPOINT_MARKER_KEY,
                ONE_WAY_WAYPOINT_MARKER
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                TRAFFIC_SIGNAL_MARKER_KEY,
                TRAFFIC_SIGNAL_MARKER
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                RAILWAY_WAYPOINT_MARKER_KEY,
                RAILWAY_WAYPOINT_MARKER
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                RAILWAY_STOP_MARKER_KEY,
                RAILWAY_STOP_MARKER
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                TOW_ROPE_KEY,
                TOW_ROPE
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                SMART_DISPATCH_APP_KEY,
                SMART_DISPATCH_APP
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                BOXED_CAR_KEY,
                BOXED_CAR
        );
        //? if <1.21.4 {
        /*ModRegistries.register(ModRegistries.ITEM, WARDEN_ENERGY_KEY, WARDEN_ENERGY);
        ModRegistries.register(ModRegistries.ITEM, WARDEN_ENERGY_SONIC_BOOM_KEY, WARDEN_ENERGY_SONIC_BOOM);
        ModRegistries.register(ModRegistries.ITEM, CHOCO_BAR_KEY, CHOCO_BAR);*/
        //?}
        ModRegistries.register(
                ModRegistries.ITEM,
                TOYOTA_SUPRA_A80_1993_SPAWN_ITEM_KEY,
                TOYOTA_SUPRA_A80_1993_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                NISSAN_180SX_1989_SPAWN_ITEM_KEY,
                NISSAN_180SX_1989_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                BMW_M3_E46_SPAWN_ITEM_KEY,
                BMW_M3_E46_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                BELL_H13_HELICOPTER_SPAWN_ITEM_KEY,
                BELL_H13_HELICOPTER_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                MERCEDES_BENZ_GCLASS_SPAWN_ITEM_KEY,
                MERCEDES_BENZ_GCLASS_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                NISSAN_SKYLINE_R34_GTR_SPAWN_ITEM_KEY,
                NISSAN_SKYLINE_R34_GTR_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                HONDA_INTEGRA_DC2_TYPE_R_1995_SPAWN_ITEM_KEY,
                HONDA_INTEGRA_DC2_TYPE_R_1995_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                NISSAN_FAIRLADY_300ZX_Z32_1989_SPAWN_ITEM_KEY,
                NISSAN_FAIRLADY_300ZX_Z32_1989_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                NISSAN_SKYLINE_R32_GTR_SPAWN_ITEM_KEY,
                NISSAN_SKYLINE_R32_GTR_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                MAZDA_RX7_FD_SPAWN_ITEM_KEY,
                MAZDA_RX7_FD_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                TESLA_CYBERTRUCK_SPAWN_ITEM_KEY,
                TESLA_CYBERTRUCK_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                LEXUS_SC300_1993_SPAWN_ITEM_KEY,
                LEXUS_SC300_1993_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                MAZDA_MIATA_MX5_NA_SPAWN_ITEM_KEY,
                MAZDA_MIATA_MX5_NA_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                MAZDA_RX7_FC_SPAWN_ITEM_KEY,
                MAZDA_RX7_FC_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                NISSAN_200SX_SILVIA_S14_SPAWN_ITEM_KEY,
                NISSAN_200SX_SILVIA_S14_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                HONDA_NSX_1990_SPAWN_ITEM_KEY,
                HONDA_NSX_1990_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                ROSSA_SPAWN_ITEM_KEY,
                ROSSA_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                TOYOTA_COROLLA_AE86_TRUENO_SPAWN_ITEM_KEY,
                TOYOTA_COROLLA_AE86_TRUENO_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                NISSAN_ONEVIA_SPAWN_ITEM_KEY,
                NISSAN_ONEVIA_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                HONDA_INTEGRA_TYPE_R_DB8_SPAWN_ITEM_KEY,
                HONDA_INTEGRA_TYPE_R_DB8_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                NISSAN_SILVIA_S13_SPAWN_ITEM_KEY,
                NISSAN_SILVIA_S13_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                NISSAN_SILEIGHTY_SPAWN_ITEM_KEY,
                NISSAN_SILEIGHTY_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                FLATIRON_OPOSSUM_SPAWN_ITEM_KEY,
                FLATIRON_OPOSSUM_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                CYCAS_PIXEL_VAN_SPAWN_ITEM_KEY,
                CYCAS_PIXEL_VAN_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                SCANDZA_GC215_TRACTOR_UNIT_16_SPAWN_ITEM_KEY,
                SCANDZA_GC215_TRACTOR_UNIT_16_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                KAWAGUCHI_KAMARI_17_SPAWN_ITEM_KEY,
                KAWAGUCHI_KAMARI_17_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                CONTAINER_TRAILER_SPAWN_ITEM_KEY,
                CONTAINER_TRAILER_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                BREAKABLE_TIRE_STACK_SPAWN_ITEM_KEY,
                BREAKABLE_TIRE_STACK_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                TRAFFIC_CONE_SPAWN_ITEM_KEY,
                TRAFFIC_CONE_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                BARRICADE_SPAWN_ITEM_KEY,
                BARRICADE_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                BREAKABLE_BOX_STACK_SPAWN_ITEM_KEY,
                BREAKABLE_BOX_STACK_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                BREAKABLE_BOX_2_STACK_SPAWN_ITEM_KEY,
                BREAKABLE_BOX_2_STACK_SPAWN_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                CAR_BREAKABLE_GLASS_ITEM_KEY,
                CAR_BREAKABLE_GLASS_ITEM
        );
        ModRegistries.register(
                ModRegistries.ITEM,
                WATER_PUDDLE_ITEM_KEY,
                WATER_PUDDLE_ITEM
        );
    }

    //? if >=1.19.3 && <26.1 {
    public static void addToCreativeTab() {
        net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS)
                .register(content -> {
                    content.accept(new ItemStack(NISSAN_400Z_VOXEL_SPAWN_ITEM));
                    content.accept(new ItemStack(VOLKSWAGEN_SCIROCCO_VOXEL_SPAWN_ITEM));
                    content.accept(new ItemStack(DODGE_CHALLENGER_SRT_2020_VOXEL_SPAWN_ITEM));
                    content.accept(new ItemStack(HONDA_CIVIC_HATCHBACK_2000_VOXEL_SPAWN_ITEM));
                    content.accept(new ItemStack(MCLAREN_F1_VOXEL_SPAWN_ITEM));
                    content.accept(new ItemStack(CHEVROLET_CORVETTE_C6_VOXEL_SPAWN_ITEM));
                    content.accept(new ItemStack(CUSTOM_WRENCH));
                    content.accept(new ItemStack(DEBUG_WRENCH));
                    content.accept(new ItemStack(WAYPOINT_MARKER));
                    content.accept(new ItemStack(ONE_WAY_WAYPOINT_MARKER));
                    content.accept(new ItemStack(TRAFFIC_SIGNAL_MARKER));
                    content.accept(new ItemStack(RAILWAY_WAYPOINT_MARKER));
                    content.accept(new ItemStack(RAILWAY_STOP_MARKER));
                    content.accept(new ItemStack(TOW_ROPE));
                    content.accept(new ItemStack(SMART_DISPATCH_APP));
                    content.accept(new ItemStack(TOYOTA_SUPRA_A80_1993_SPAWN_ITEM));
                    content.accept(new ItemStack(NISSAN_180SX_1989_SPAWN_ITEM));
                    content.accept(new ItemStack(BMW_M3_E46_SPAWN_ITEM));
                    content.accept(new ItemStack(BELL_H13_HELICOPTER_SPAWN_ITEM));
                    content.accept(new ItemStack(MERCEDES_BENZ_GCLASS_SPAWN_ITEM));
                    content.accept(new ItemStack(NISSAN_SKYLINE_R34_GTR_SPAWN_ITEM));
                    content.accept(new ItemStack(HONDA_INTEGRA_DC2_TYPE_R_1995_SPAWN_ITEM));
                    content.accept(new ItemStack(NISSAN_FAIRLADY_300ZX_Z32_1989_SPAWN_ITEM));
                    content.accept(new ItemStack(NISSAN_SKYLINE_R32_GTR_SPAWN_ITEM));
                    content.accept(new ItemStack(MAZDA_RX7_FD_SPAWN_ITEM));
                    content.accept(new ItemStack(TESLA_CYBERTRUCK_SPAWN_ITEM));
                    content.accept(new ItemStack(LEXUS_SC300_1993_SPAWN_ITEM));
                    content.accept(new ItemStack(MAZDA_MIATA_MX5_NA_SPAWN_ITEM));
                    content.accept(new ItemStack(MAZDA_RX7_FC_SPAWN_ITEM));
                    content.accept(new ItemStack(NISSAN_200SX_SILVIA_S14_SPAWN_ITEM));
                    content.accept(new ItemStack(HONDA_NSX_1990_SPAWN_ITEM));
                    content.accept(new ItemStack(ROSSA_SPAWN_ITEM));
                    content.accept(new ItemStack(TOYOTA_COROLLA_AE86_TRUENO_SPAWN_ITEM));
                    content.accept(new ItemStack(NISSAN_ONEVIA_SPAWN_ITEM));
                    content.accept(new ItemStack(HONDA_INTEGRA_TYPE_R_DB8_SPAWN_ITEM));
                    content.accept(new ItemStack(NISSAN_SILVIA_S13_SPAWN_ITEM));
                    content.accept(new ItemStack(NISSAN_SILEIGHTY_SPAWN_ITEM));
                    content.accept(new ItemStack(FLATIRON_OPOSSUM_SPAWN_ITEM));
                    content.accept(new ItemStack(CYCAS_PIXEL_VAN_SPAWN_ITEM));
                    content.accept(new ItemStack(SCANDZA_GC215_TRACTOR_UNIT_16_SPAWN_ITEM));
                    content.accept(new ItemStack(KAWAGUCHI_KAMARI_17_SPAWN_ITEM));
                    content.accept(new ItemStack(CONTAINER_TRAILER_SPAWN_ITEM));
                    content.accept(new ItemStack(BREAKABLE_TIRE_STACK_SPAWN_ITEM));
                    content.accept(new ItemStack(TRAFFIC_CONE_SPAWN_ITEM));
                    content.accept(new ItemStack(BARRICADE_SPAWN_ITEM));
                    content.accept(new ItemStack(BREAKABLE_BOX_STACK_SPAWN_ITEM));
                    content.accept(new ItemStack(BREAKABLE_BOX_2_STACK_SPAWN_ITEM));
                    content.accept(new ItemStack(CAR_BREAKABLE_GLASS_ITEM));
                    content.accept(new ItemStack(WATER_PUDDLE_ITEM));
                });
    }
    //?}

    //? if >=26.1 {
    /*public static void addToCreativeTab() {
        net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
                .register(output -> {
                    output.accept(new ItemStack(NISSAN_400Z_VOXEL_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(VOLKSWAGEN_SCIROCCO_VOXEL_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(DODGE_CHALLENGER_SRT_2020_VOXEL_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(HONDA_CIVIC_HATCHBACK_2000_VOXEL_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(MCLAREN_F1_VOXEL_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(CHEVROLET_CORVETTE_C6_VOXEL_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(CUSTOM_WRENCH), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(DEBUG_WRENCH), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(WAYPOINT_MARKER), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(ONE_WAY_WAYPOINT_MARKER), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(TRAFFIC_SIGNAL_MARKER), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(RAILWAY_WAYPOINT_MARKER), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(RAILWAY_STOP_MARKER), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(TOW_ROPE), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(SMART_DISPATCH_APP), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(TOYOTA_SUPRA_A80_1993_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(NISSAN_180SX_1989_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(BMW_M3_E46_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(BELL_H13_HELICOPTER_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(MERCEDES_BENZ_GCLASS_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(NISSAN_SKYLINE_R34_GTR_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(HONDA_INTEGRA_DC2_TYPE_R_1995_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(NISSAN_FAIRLADY_300ZX_Z32_1989_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(NISSAN_SKYLINE_R32_GTR_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(MAZDA_RX7_FD_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(TESLA_CYBERTRUCK_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(LEXUS_SC300_1993_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(MAZDA_MIATA_MX5_NA_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(MAZDA_RX7_FC_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(NISSAN_200SX_SILVIA_S14_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(HONDA_NSX_1990_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(ROSSA_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(TOYOTA_COROLLA_AE86_TRUENO_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(NISSAN_ONEVIA_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(HONDA_INTEGRA_TYPE_R_DB8_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(NISSAN_SILVIA_S13_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(NISSAN_SILEIGHTY_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(FLATIRON_OPOSSUM_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(CYCAS_PIXEL_VAN_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(SCANDZA_GC215_TRACTOR_UNIT_16_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(KAWAGUCHI_KAMARI_17_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(CONTAINER_TRAILER_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(BREAKABLE_TIRE_STACK_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(TRAFFIC_CONE_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(BARRICADE_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(BREAKABLE_BOX_STACK_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(BREAKABLE_BOX_2_STACK_SPAWN_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(CAR_BREAKABLE_GLASS_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                    output.accept(new ItemStack(WATER_PUDDLE_ITEM), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
                });
    }*/
    //?}
}
