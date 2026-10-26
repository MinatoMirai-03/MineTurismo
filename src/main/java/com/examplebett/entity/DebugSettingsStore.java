package com.examplebett.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class DebugSettingsStore {
    private static final Path FILE = Paths.get("config", "mineturismo-debug-settings.properties");

    private static boolean loading;

    private DebugSettingsStore() {
    }

    static synchronized void load() {
        if (!Files.isRegularFile(FILE)) {
            return;
        }
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(FILE)) {
            props.load(in);
        } catch (IOException | RuntimeException e) {
            return;
        }
        loading = true;
        try {
            CarEntity.setHitboxDisabled(bool(props, "hitboxDisabled", CarEntity.isHitboxDisabled()));
            CarEntity.setBreakVanillaBlocksEnabled(bool(props, "breakVanillaBlocks", CarEntity.isBreakVanillaBlocksEnabled()));
            CarEntity.setMobDamageEnabled(bool(props, "mobDamage", CarEntity.isMobDamageEnabled()));
            CarEntity.setSecurityAlarmEnabled(bool(props, "securityAlarm", CarEntity.isSecurityAlarmEnabled()));
            CarEntity.setForceRotateSideEnabled(bool(props, "forceRotateSide", CarEntity.isForceRotateSideEnabled()));
            CarEntity.setForceRotateFrontEnabled(bool(props, "forceRotateFront", CarEntity.isForceRotateFrontEnabled()));
            CarEntity.setSparkPositionOffsetEnabled(bool(props, "sparkPositionOffset", CarEntity.isSparkPositionOffsetEnabled()));
            CarEntity.setTumbleHeightMultiplier(num(props, "tumbleHeightMultiplier", CarEntity.getTumbleHeightMultiplier()));
            CarEntity.setTumbleRotationMultiplier(num(props, "tumbleRotationMultiplier", CarEntity.getTumbleRotationMultiplier()));
            CarEntity.setPushDistanceMultiplier(num(props, "pushDistanceMultiplier", CarEntity.getPushDistanceMultiplier()));
            CarEntity.setSuspensionIntensityMultiplier(num(props, "suspensionIntensityMultiplier", CarEntity.getSuspensionIntensityMultiplier()));
        } finally {
            loading = false;
        }
    }

    static synchronized void save() {
        if (loading) {
            return;
        }
        Properties props = new Properties();
        props.setProperty("hitboxDisabled", Boolean.toString(CarEntity.isHitboxDisabled()));
        props.setProperty("breakVanillaBlocks", Boolean.toString(CarEntity.isBreakVanillaBlocksEnabled()));
        props.setProperty("mobDamage", Boolean.toString(CarEntity.isMobDamageEnabled()));
        props.setProperty("securityAlarm", Boolean.toString(CarEntity.isSecurityAlarmEnabled()));
        props.setProperty("forceRotateSide", Boolean.toString(CarEntity.isForceRotateSideEnabled()));
        props.setProperty("forceRotateFront", Boolean.toString(CarEntity.isForceRotateFrontEnabled()));
        props.setProperty("sparkPositionOffset", Boolean.toString(CarEntity.isSparkPositionOffsetEnabled()));
        props.setProperty("tumbleHeightMultiplier", Float.toString(CarEntity.getTumbleHeightMultiplier()));
        props.setProperty("tumbleRotationMultiplier", Float.toString(CarEntity.getTumbleRotationMultiplier()));
        props.setProperty("pushDistanceMultiplier", Float.toString(CarEntity.getPushDistanceMultiplier()));
        props.setProperty("suspensionIntensityMultiplier", Float.toString(CarEntity.getSuspensionIntensityMultiplier()));
        try {
            Files.createDirectories(FILE.getParent());
            try (OutputStream out = Files.newOutputStream(FILE)) {
                props.store(out, "MineTurismo Debug Wrench settings");
            }
        } catch (IOException | RuntimeException e) {

        }
    }

    private static boolean bool(Properties props, String key, boolean fallback) {
        String value = props.getProperty(key);
        return value == null ? fallback : Boolean.parseBoolean(value.trim());
    }

    private static float num(Properties props, String key, float fallback) {
        String value = props.getProperty(key);
        if (value == null) {
            return fallback;
        }
        try {
            return Float.parseFloat(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
