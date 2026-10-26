package com.examplebett;

import com.examplebett.block.AiObjectModBlocks;
import com.examplebett.entity.AiObjectModAttributes;
import com.examplebett.entity.AiObjectModEntities;
import com.examplebett.entity.RailwayTrafficManager;
import com.examplebett.inventory.AiObjectModMenus;
import com.examplebett.entity.TownTrafficManager;
import com.examplebett.item.AiObjectModItems;
import com.examplebett.network.AiObjectModNetworking;
import com.examplebett.sound.AiObjectModSounds;
import com.examplebett.waypoint.WaypointVisualizer;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;

//? if >=1.17 {
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//?} else {
/*import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;*/
//?}

public class AiObjectMod implements ModInitializer {
	public static final String MOD_ID = "mineturismo";

	//? if >=1.17 {
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	//?} else
	/*public static final Logger LOGGER = LogManager.getLogger(MOD_ID);*/

	@Override
	public void onInitialize() {

		//? if <1.17 {
		/*com.examplebett.compat.ModResources.registerServerResourceManagerHook();*/
		//?}

		AiObjectModBlocks.register();
		AiObjectModEntities.register();
		AiObjectModAttributes.register();
		AiObjectModItems.register();
		AiObjectModMenus.register();

		//? if >=1.19.3 {
		AiObjectModItems.addToCreativeTab();
		//?}
		AiObjectModNetworking.register();
		AiObjectModSounds.register();
		com.examplebett.effect.SonicBoomEnergy.register();
		WaypointVisualizer.registerEvents();
		TownTrafficManager.registerEvents();
		RailwayTrafficManager.registerEvents();
	}

	public static ResourceLocation id(String path) {
		//? if >=1.21 {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
		//?} else
		/*return new ResourceLocation(MOD_ID, path);*/
	}
}
