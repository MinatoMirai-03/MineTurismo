package com.examplebett.entity;

import com.examplebett.AiObjectMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.level.Level;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class VehiclePresets {

    public static final String FBI_CHALLENGER = "fbi_challenger";

    private static final String PRESET_DIR = "/data/" + AiObjectMod.MOD_ID + "/vehicle_presets/";
    private static final Map<String, CompoundTag> CACHE = new HashMap<>();

    private VehiclePresets() {
    }

    public static synchronized CompoundTag get(String id) {
        if (!CACHE.containsKey(id)) {
            CACHE.put(id, load(id));
        }
        CompoundTag tag = CACHE.get(id);
        return tag == null ? null : tag.copy();
    }

    private static CompoundTag load(String id) {
        try (InputStream in = VehiclePresets.class.getResourceAsStream(PRESET_DIR + id + ".snbt")) {
            if (in == null) {
                AiObjectMod.LOGGER.warn("vehicle preset '{}' not found", id);
                return null;
            }
            String snbt = new String(in.readAllBytes(), StandardCharsets.UTF_8).trim();
            //? if >=1.21.5 {
            return TagParser.parseCompoundFully(snbt);
            //?} else
            /*return TagParser.parseTag(snbt);*/
        } catch (Exception e) {
            AiObjectMod.LOGGER.error("failed to load vehicle preset '{}'", id, e);
            return null;
        }
    }

    public static CarEntity spawn(Level level, double x, double y, double z, float yaw, String id) {
        CarEntity car = create(level, x, y, z, id);
        if (car == null) {
            return null;
        }
        //? if >=1.17 {
        car.setYRot(yaw);
        //?} else
        /*car.yRot = yaw;*/
        level.addFreshEntity(car);
        return car;
    }

    public static CarEntity create(Level level, double x, double y, double z, String id) {
        CompoundTag tag = get(id);
        if (tag == null) {
            return null;
        }
        //? if >=1.21.5 {
        String modelId = tag.getStringOr("ModelId", "");
        //?} else
        /*String modelId = tag.contains("ModelId") ? tag.getString("ModelId") : "";*/
        if (modelId.isEmpty()) {
            return null;
        }
        CarEntity car = new CarEntity(level, x, y, z, modelId);
        car.importCustomizationTag(tag);
        return car;
    }
}
