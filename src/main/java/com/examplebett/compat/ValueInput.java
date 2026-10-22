package com.examplebett.compat;

import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public final class ValueInput {
	private final CompoundTag tag;

	public ValueInput(CompoundTag tag) {
		this.tag = tag;
	}

	public Optional<String> getString(String key) {
		//? if >=1.21.5 {
		return tag.getString(key);
		//?} else
		/*return tag.contains(key) ? Optional.of(tag.getString(key)) : Optional.empty();*/
	}

	public String getStringOr(String key, String fallback) {
		return getString(key).orElse(fallback);
	}

	public int getIntOr(String key, int fallback) {
		//? if >=1.21.5 {
		return tag.getInt(key).orElse(fallback);
		//?} else
		/*return tag.contains(key) ? tag.getInt(key) : fallback;*/
	}

	public float getFloatOr(String key, float fallback) {
		//? if >=1.21.5 {
		return tag.getFloat(key).orElse(fallback);
		//?} else
		/*return tag.contains(key) ? tag.getFloat(key) : fallback;*/
	}

	public boolean getBooleanOr(String key, boolean fallback) {
		//? if >=1.21.5 {
		return tag.getBoolean(key).orElse(fallback);
		//?} else
		/*return tag.contains(key) ? tag.getBoolean(key) : fallback;*/
	}

	public Optional<int[]> getIntArray(String key) {
		//? if >=1.21.5 {
		return tag.getIntArray(key);
		//?} else
		/*return tag.contains(key) ? Optional.of(tag.getIntArray(key)) : Optional.empty();*/
	}
}
