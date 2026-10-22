package com.examplebett.compat;

import net.minecraft.network.chat.Component;
//? if >=1.16 {
import net.minecraft.network.chat.MutableComponent;
//?}

public final class ModText {
	private ModText() {
	}

	//? if >=1.16 {
	public static MutableComponent translatable(String key) {
		//? if >=1.19 {
		return Component.translatable(key);
		//?} else
		/*return new net.minecraft.network.chat.TranslatableComponent(key);*/
	}
	//?}
	//? if <1.16 {
	/*public static Component translatable(String key) {
		return new net.minecraft.network.chat.TranslatableComponent(key);
	}*/
	//?}

	//? if >=1.16 {
	public static MutableComponent translatable(String key, Object... args) {
		//? if >=1.19 {
		return Component.translatable(key, args);
		//?} else
		/*return new net.minecraft.network.chat.TranslatableComponent(key, args);*/
	}
	//?}
	//? if <1.16 {
	/*public static Component translatable(String key, Object... args) {
		return new net.minecraft.network.chat.TranslatableComponent(key, args);
	}*/
	//?}

	//? if >=1.16 {
	public static MutableComponent literal(String text) {
		//? if >=1.19 {
		return Component.literal(text);
		//?} else
		/*return new net.minecraft.network.chat.TextComponent(text);*/
	}
	//?}
	//? if <1.16 {
	/*public static Component literal(String text) {
		return new net.minecraft.network.chat.TextComponent(text);
	}*/
	//?}

	public static Component withFont(Component component, net.minecraft.resources.ResourceLocation font) {
		//? if >=1.16 && <1.21.9 {
		/*return component.copy().withStyle(style -> style.withFont(font));
		*///?} else
		return component;
	}
}
