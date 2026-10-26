package com.examplebett.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.util.function.Consumer;

public final class ModServerEvents {

    private static final boolean HAS_LIFECYCLE_V1 = FabricLoader.getInstance().isModLoaded("fabric-lifecycle-events-v1");

    public static void onEndServerTick(Consumer<MinecraftServer> action) {
        if (HAS_LIFECYCLE_V1) {
            registerEndServerTickV1(action);
        } else {
            registerEndServerTickV0(action);
        }
    }

    public static void onServerStarted(Consumer<MinecraftServer> action) {
        if (HAS_LIFECYCLE_V1) {
            registerServerStartedV1(action);
        } else {
            registerServerStartedV0(action);
        }
    }

    private static void registerEndServerTickV1(Consumer<MinecraftServer> action) {
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(action::accept);
    }

    private static void registerServerStartedV1(Consumer<MinecraftServer> action) {
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED.register(action::accept);
    }

    //? if <1.17 {
    /*private static void registerEndServerTickV0(Consumer<MinecraftServer> action) {
        net.fabricmc.fabric.api.event.server.ServerTickCallback.EVENT.register(action::accept);
    }

    private static void registerServerStartedV0(Consumer<MinecraftServer> action) {
        net.fabricmc.fabric.api.event.server.ServerStartCallback.EVENT.register(action::accept);
    }
    *///?} else {
    private static void registerEndServerTickV0(Consumer<MinecraftServer> action) {
        throw new IllegalStateException("fabric-lifecycle-events-v1 is required on this Minecraft version");
    }

    private static void registerServerStartedV0(Consumer<MinecraftServer> action) {
        throw new IllegalStateException("fabric-lifecycle-events-v1 is required on this Minecraft version");
    }
    //?}

    private ModServerEvents() {
    }
}
