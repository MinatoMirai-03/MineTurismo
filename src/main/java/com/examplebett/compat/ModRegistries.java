package com.examplebett.compat;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModRegistries {
	private ModRegistries() {
	}

	//? if >=1.19.3 {
	public static final ResourceKey<Registry<Block>> BLOCK_KEY = net.minecraft.core.registries.Registries.BLOCK;
	public static final ResourceKey<Registry<Item>> ITEM_KEY = net.minecraft.core.registries.Registries.ITEM;
	public static final ResourceKey<Registry<EntityType<?>>> ENTITY_TYPE_KEY = net.minecraft.core.registries.Registries.ENTITY_TYPE;
	public static final ResourceKey<Registry<SoundEvent>> SOUND_EVENT_KEY = net.minecraft.core.registries.Registries.SOUND_EVENT;

	public static final Registry<Block> BLOCK = net.minecraft.core.registries.BuiltInRegistries.BLOCK;
	public static final Registry<Item> ITEM = net.minecraft.core.registries.BuiltInRegistries.ITEM;
	public static final Registry<EntityType<?>> ENTITY_TYPE = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE;
	public static final Registry<SoundEvent> SOUND_EVENT = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT;
	//?}

	//? if <1.19.3 && >=1.16 {
	/*public static final ResourceKey<Registry<Block>> BLOCK_KEY = Registry.BLOCK_REGISTRY;
	public static final ResourceKey<Registry<Item>> ITEM_KEY = Registry.ITEM_REGISTRY;
	public static final ResourceKey<Registry<EntityType<?>>> ENTITY_TYPE_KEY = Registry.ENTITY_TYPE_REGISTRY;
	public static final ResourceKey<Registry<SoundEvent>> SOUND_EVENT_KEY = Registry.SOUND_EVENT_REGISTRY;

	public static final Registry<Block> BLOCK = Registry.BLOCK;
	public static final Registry<Item> ITEM = Registry.ITEM;
	public static final Registry<EntityType<?>> ENTITY_TYPE = Registry.ENTITY_TYPE;
	public static final Registry<SoundEvent> SOUND_EVENT = Registry.SOUND_EVENT;
	*///?}

	//? if <1.16 {
	/*public static final ResourceKey<Registry<Block>> BLOCK_KEY = null;
	public static final ResourceKey<Registry<Item>> ITEM_KEY = null;
	public static final ResourceKey<Registry<EntityType<?>>> ENTITY_TYPE_KEY = null;
	public static final ResourceKey<Registry<SoundEvent>> SOUND_EVENT_KEY = null;

	public static final Registry<Block> BLOCK = Registry.BLOCK;
	public static final Registry<Item> ITEM = Registry.ITEM;
	public static final Registry<EntityType<?>> ENTITY_TYPE = Registry.ENTITY_TYPE;
	public static final Registry<SoundEvent> SOUND_EVENT = Registry.SOUND_EVENT;
	*///?}

	public static <T> T register(Registry<T> registry, ResourceKey<T> key, T value) {
		//? if >=1.18 {
		return Registry.register(registry, key, value);
		//?} else
		/*return Registry.register(registry, key.location(), value);*/
	}
}
