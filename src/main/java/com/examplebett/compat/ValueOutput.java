package com.examplebett.compat;

import net.minecraft.nbt.CompoundTag;

public final class ValueOutput {
	private final CompoundTag tag;

	public ValueOutput(CompoundTag tag) {
		this.tag = tag;
	}

	public void putString(String key, String value) {
		tag.putString(key, value);
	}

	public void putInt(String key, int value) {
		tag.putInt(key, value);
	}

	public void putFloat(String key, float value) {
		tag.putFloat(key, value);
	}

	public void putBoolean(String key, boolean value) {
		tag.putBoolean(key, value);
	}

	public void putIntArray(String key, int[] value) {
		tag.putIntArray(key, value);
	}
}
