package com.mineturismo.aiobjects;

import com.examplebett.compat.ModRegistries;
import com.examplebett.item.CarSpawnItem;
import net.fabricmc.api.ModInitializer;
//? if <1.16 {
/*import com.examplebett.compat.ResourceKey;*/
//?} else
import net.minecraft.resources.ResourceKey;
//? if >=1.21 {
import net.minecraft.resources.ResourceLocation;
//?}
//? if >=1.19.3 {
import net.minecraft.world.item.CreativeModeTabs;
//?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

//? if >=1.17 {
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//?} else {
/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;*/
//?}

public class MineturismoAiObjects implements ModInitializer {

	public static final String MOD_ID = "mineturismo-ai-objects";
	//? if >=1.17 {
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	//?} else
	/*public static final Logger LOGGER = LogManager.getLogger(MOD_ID);*/

	private static net.minecraft.resources.ResourceLocation id(String path) {
		//? if >=1.21 {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
		//?} else
		/*return new net.minecraft.resources.ResourceLocation(MOD_ID, path);*/
	}

	private static Item.Properties withId(Item.Properties properties, ResourceKey<Item> key) {
		//? if >=1.21.2 {
		properties = properties.setId(key);
		//?}
		//? if <1.19.3 {
		/*properties = properties.tab(net.minecraft.world.item.CreativeModeTab.TAB_MISC);*/
		//?}
		return properties;
	}

	public static final ResourceKey<Item> LEVIAS_ZS_89_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("levias_zs_89_spawn"));
	public static final CarSpawnItem LEVIAS_ZS_89_SPAWN =
			new CarSpawnItem("levias_zs_89", withId(new Item.Properties(), LEVIAS_ZS_89_SPAWN_KEY));

	public static final ResourceKey<Item> FORLI_F30_87_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("forli_f30_87_spawn"));
	public static final CarSpawnItem FORLI_F30_87_SPAWN =
			new CarSpawnItem("forli_f30_87", withId(new Item.Properties(), FORLI_F30_87_SPAWN_KEY));

	public static final ResourceKey<Item> ALGHERO_1C_GTS_01_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("alghero_1c_gts_01_spawn"));
	public static final CarSpawnItem ALGHERO_1C_GTS_01_SPAWN =
			new CarSpawnItem("alghero_1c_gts_01", withId(new Item.Properties(), ALGHERO_1C_GTS_01_SPAWN_KEY));

	public static final ResourceKey<Item> KARIYA_ABYSS_20_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("kariya_abyss_20_spawn"));
	public static final CarSpawnItem KARIYA_ABYSS_20_SPAWN =
			new CarSpawnItem("kariya_abyss_20", withId(new Item.Properties(), KARIYA_ABYSS_20_SPAWN_KEY));

	public static final ResourceKey<Item> MISUZU_MEGA_17_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("misuzu_mega_17_spawn"));
	public static final CarSpawnItem MISUZU_MEGA_17_SPAWN =
			new CarSpawnItem("misuzu_mega_17", withId(new Item.Properties(), MISUZU_MEGA_17_SPAWN_KEY));

	public static final ResourceKey<Item> PONTA_EPIC_TYPE_A_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("ponta_epic_type_a_spawn"));
	public static final CarSpawnItem PONTA_EPIC_TYPE_A_SPAWN =
			new CarSpawnItem("ponta_epic_type_a", withId(new Item.Properties(), PONTA_EPIC_TYPE_A_SPAWN_KEY));

	public static final ResourceKey<Item> NITTAKU_WEED_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("nittaku_weed_spawn"));
	public static final CarSpawnItem NITTAKU_WEED_SPAWN =
			new CarSpawnItem("nittaku_weed", withId(new Item.Properties(), NITTAKU_WEED_SPAWN_KEY));

	public static final ResourceKey<Item> MECHTHILD_N_CLASS_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("mechthild_n_class_spawn"));
	public static final CarSpawnItem MECHTHILD_N_CLASS_SPAWN =
			new CarSpawnItem("mechthild_n_class", withId(new Item.Properties(), MECHTHILD_N_CLASS_SPAWN_KEY));

	public static final ResourceKey<Item> UMW_BEANS_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("umw_beans_spawn"));
	public static final CarSpawnItem UMW_BEANS_SPAWN =
			new CarSpawnItem("umw_beans", withId(new Item.Properties(), UMW_BEANS_SPAWN_KEY));

	public static final ResourceKey<Item> KARIYA_AZUSA_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("kariya_azusa_spawn"));
	public static final CarSpawnItem KARIYA_AZUSA_SPAWN =
			new CarSpawnItem("kariya_azusa", withId(new Item.Properties(), KARIYA_AZUSA_SPAWN_KEY));

	public static final ResourceKey<Item> KARIYA_AZURITE_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("kariya_azurite_spawn"));
	public static final CarSpawnItem KARIYA_AZURITE_SPAWN =
			new CarSpawnItem("kariya_azurite", withId(new Item.Properties(), KARIYA_AZURITE_SPAWN_KEY));

	public static final ResourceKey<Item> KAWAGUCHI_ST339_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("kawaguchi_st339_spawn"));
	public static final CarSpawnItem KAWAGUCHI_ST339_SPAWN =
			new CarSpawnItem("kawaguchi_st339", withId(new Item.Properties(), KAWAGUCHI_ST339_SPAWN_KEY));

	public static final ResourceKey<Item> KAWAGUCHI_KAMARI_20Z_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("kawaguchi_kamari_20z_spawn"));
	public static final CarSpawnItem KAWAGUCHI_KAMARI_20Z_SPAWN =
			new CarSpawnItem("kawaguchi_kamari_20z", withId(new Item.Properties(), KAWAGUCHI_KAMARI_20Z_SPAWN_KEY));

	public static final ResourceKey<Item> BOEHME_SR100_22_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("boehme_sr100_22_spawn"));
	public static final CarSpawnItem BOEHME_SR100_22_SPAWN =
			new CarSpawnItem("boehme_sr100_22", withId(new Item.Properties(), BOEHME_SR100_22_SPAWN_KEY));

	public static final ResourceKey<Item> PANINO_CAELUM_22_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("panino_caelum_22_spawn"));
	public static final CarSpawnItem PANINO_CAELUM_22_SPAWN =
			new CarSpawnItem("panino_caelum_22", withId(new Item.Properties(), PANINO_CAELUM_22_SPAWN_KEY));

	public static final ResourceKey<Item> KARIYA_AIR_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("kariya_air_spawn"));
	public static final CarSpawnItem KARIYA_AIR_SPAWN =
			new CarSpawnItem("kariya_air", withId(new Item.Properties(), KARIYA_AIR_SPAWN_KEY));

	public static final ResourceKey<Item> FL_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("fl_spawn"));
	public static final CarSpawnItem FL_SPAWN =
			new CarSpawnItem("fl", withId(new Item.Properties(), FL_SPAWN_KEY));

	public static final ResourceKey<Item> AI1_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("ai1_spawn"));
	public static final CarSpawnItem AI1_SPAWN =
			new CarSpawnItem("ai1", withId(new Item.Properties(), AI1_SPAWN_KEY));

	public static final ResourceKey<Item> ATLANTIS_STIVALE_POLICE_23_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("atlantis_stivale_police_23_spawn"));
	public static final CarSpawnItem ATLANTIS_STIVALE_POLICE_23_SPAWN =
			new CarSpawnItem("atlantis_stivale_police_23", withId(new Item.Properties(), ATLANTIS_STIVALE_POLICE_23_SPAWN_KEY));

	public static final ResourceKey<Item> KARIYA_AERIS_PHV_17_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("kariya_aeris_phv_17_spawn"));
	public static final CarSpawnItem KARIYA_AERIS_PHV_17_SPAWN =
			new CarSpawnItem("kariya_aeris_phv_17", withId(new Item.Properties(), KARIYA_AERIS_PHV_17_SPAWN_KEY));

	public static final ResourceKey<Item> YATSUHASHI_RESOLUTION_WAGON_05_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("yatsuhashi_resolution_wagon_05_spawn"));
	public static final CarSpawnItem YATSUHASHI_RESOLUTION_WAGON_05_SPAWN =
			new CarSpawnItem("yatsuhashi_resolution_wagon_05", withId(new Item.Properties(), YATSUHASHI_RESOLUTION_WAGON_05_SPAWN_KEY));

	public static final ResourceKey<Item> PONTA_TITANIC_03_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("ponta_titanic_03_spawn"));
	public static final CarSpawnItem PONTA_TITANIC_03_SPAWN =
			new CarSpawnItem("ponta_titanic_03", withId(new Item.Properties(), PONTA_TITANIC_03_SPAWN_KEY));

	public static final ResourceKey<Item> KARIYA_VENKI_03_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("kariya_venki_03_spawn"));
	public static final CarSpawnItem KARIYA_VENKI_03_SPAWN =
			new CarSpawnItem("kariya_venki_03", withId(new Item.Properties(), KARIYA_VENKI_03_SPAWN_KEY));

	public static final ResourceKey<Item> D_SPAWN_KEY = ResourceKey.create(
			ModRegistries.ITEM_KEY, id("d_spawn"));
	public static final CarSpawnItem D_SPAWN =
			new CarSpawnItem("d", withId(new Item.Properties(), D_SPAWN_KEY));

	@Override
	public void onInitialize() {
		ModRegistries.register(ModRegistries.ITEM, LEVIAS_ZS_89_SPAWN_KEY, LEVIAS_ZS_89_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, FORLI_F30_87_SPAWN_KEY, FORLI_F30_87_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, ALGHERO_1C_GTS_01_SPAWN_KEY, ALGHERO_1C_GTS_01_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, KARIYA_ABYSS_20_SPAWN_KEY, KARIYA_ABYSS_20_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, MISUZU_MEGA_17_SPAWN_KEY, MISUZU_MEGA_17_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, PONTA_EPIC_TYPE_A_SPAWN_KEY, PONTA_EPIC_TYPE_A_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, NITTAKU_WEED_SPAWN_KEY, NITTAKU_WEED_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, MECHTHILD_N_CLASS_SPAWN_KEY, MECHTHILD_N_CLASS_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, UMW_BEANS_SPAWN_KEY, UMW_BEANS_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, KARIYA_AZUSA_SPAWN_KEY, KARIYA_AZUSA_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, KARIYA_AZURITE_SPAWN_KEY, KARIYA_AZURITE_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, KAWAGUCHI_ST339_SPAWN_KEY, KAWAGUCHI_ST339_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, KAWAGUCHI_KAMARI_20Z_SPAWN_KEY, KAWAGUCHI_KAMARI_20Z_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, BOEHME_SR100_22_SPAWN_KEY, BOEHME_SR100_22_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, PANINO_CAELUM_22_SPAWN_KEY, PANINO_CAELUM_22_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, KARIYA_AIR_SPAWN_KEY, KARIYA_AIR_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, FL_SPAWN_KEY, FL_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, AI1_SPAWN_KEY, AI1_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, ATLANTIS_STIVALE_POLICE_23_SPAWN_KEY, ATLANTIS_STIVALE_POLICE_23_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, KARIYA_AERIS_PHV_17_SPAWN_KEY, KARIYA_AERIS_PHV_17_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, YATSUHASHI_RESOLUTION_WAGON_05_SPAWN_KEY, YATSUHASHI_RESOLUTION_WAGON_05_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, PONTA_TITANIC_03_SPAWN_KEY, PONTA_TITANIC_03_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, KARIYA_VENKI_03_SPAWN_KEY, KARIYA_VENKI_03_SPAWN);
		ModRegistries.register(ModRegistries.ITEM, D_SPAWN_KEY, D_SPAWN);

		//? if >=1.19.3 && <26.1 {
		net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS)
				.register(content -> {
					content.accept(new ItemStack(LEVIAS_ZS_89_SPAWN));
					content.accept(new ItemStack(FORLI_F30_87_SPAWN));
					content.accept(new ItemStack(ALGHERO_1C_GTS_01_SPAWN));
					content.accept(new ItemStack(KARIYA_ABYSS_20_SPAWN));
					content.accept(new ItemStack(MISUZU_MEGA_17_SPAWN));
					content.accept(new ItemStack(PONTA_EPIC_TYPE_A_SPAWN));
					content.accept(new ItemStack(NITTAKU_WEED_SPAWN));
					content.accept(new ItemStack(MECHTHILD_N_CLASS_SPAWN));
					content.accept(new ItemStack(UMW_BEANS_SPAWN));
					content.accept(new ItemStack(KARIYA_AZUSA_SPAWN));
					content.accept(new ItemStack(KARIYA_AZURITE_SPAWN));
					content.accept(new ItemStack(KAWAGUCHI_ST339_SPAWN));
					content.accept(new ItemStack(KAWAGUCHI_KAMARI_20Z_SPAWN));
					content.accept(new ItemStack(BOEHME_SR100_22_SPAWN));
					content.accept(new ItemStack(PANINO_CAELUM_22_SPAWN));
					content.accept(new ItemStack(KARIYA_AIR_SPAWN));
					content.accept(new ItemStack(FL_SPAWN));
					content.accept(new ItemStack(AI1_SPAWN));
					content.accept(new ItemStack(ATLANTIS_STIVALE_POLICE_23_SPAWN));
					content.accept(new ItemStack(KARIYA_AERIS_PHV_17_SPAWN));
					content.accept(new ItemStack(YATSUHASHI_RESOLUTION_WAGON_05_SPAWN));
					content.accept(new ItemStack(PONTA_TITANIC_03_SPAWN));
					content.accept(new ItemStack(KARIYA_VENKI_03_SPAWN));
					content.accept(new ItemStack(D_SPAWN));
				});
		//?}
		//? if >=26.1 {
		/*net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS)
				.register(output -> {
					output.accept(new ItemStack(LEVIAS_ZS_89_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(FORLI_F30_87_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(ALGHERO_1C_GTS_01_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(KARIYA_ABYSS_20_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(MISUZU_MEGA_17_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(PONTA_EPIC_TYPE_A_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(NITTAKU_WEED_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(MECHTHILD_N_CLASS_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(UMW_BEANS_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(KARIYA_AZUSA_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(KARIYA_AZURITE_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(KAWAGUCHI_ST339_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(KAWAGUCHI_KAMARI_20Z_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(BOEHME_SR100_22_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(PANINO_CAELUM_22_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(KARIYA_AIR_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(FL_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(AI1_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(ATLANTIS_STIVALE_POLICE_23_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(KARIYA_AERIS_PHV_17_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(YATSUHASHI_RESOLUTION_WAGON_05_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(PONTA_TITANIC_03_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(KARIYA_VENKI_03_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					output.accept(new ItemStack(D_SPAWN), net.minecraft.world.item.CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
				});*/
		//?}

		LOGGER.info("mineturismo-ai-objects loaded");
	}
}
