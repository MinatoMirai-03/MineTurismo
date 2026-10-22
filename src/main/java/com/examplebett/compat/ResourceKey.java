package com.examplebett.compat;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public final class ResourceKey<T> {
    private final ResourceLocation location;

    private ResourceKey(ResourceLocation location) {
        this.location = location;
    }

    public static <T> ResourceKey<T> create(ResourceKey<Registry<T>> registryKey, ResourceLocation location) {
        return new ResourceKey<>(location);
    }

    public ResourceLocation location() {
        return location;
    }
}
