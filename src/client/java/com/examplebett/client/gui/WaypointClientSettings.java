package com.examplebett.client.gui;

import java.util.Arrays;
import java.util.Set;

public final class WaypointClientSettings {
    private WaypointClientSettings() {
    }

    public static void apply(float ambientEngineVolumeMultiplier, String settings) {
        WaypointMiscSettingsScreen.cachedAmbientEngineVolumeMultiplier = ambientEngineVolumeMultiplier;
        if (settings == null || settings.isEmpty()) {
            return;
        }
        for (String line : settings.split("\n")) {
            int eq = line.indexOf('=');
            if (eq <= 0) {
                continue;
            }
            String key = line.substring(0, eq);
            String value = line.substring(eq + 1);
            try {
                switch (key) {
                    case "disabled" -> {
                        replace(WaypointVehicleListScreen.cachedDisabledModelIds, value);

                        WaypointVehicleListScreen.hasAppliedSessionDefaultPreset = true;
                    }
                    case "rare" -> replace(WaypointVehicleListScreen.cachedRareModelIds, value);
                    case "plates" -> replace(WaypointPlateSettingsScreen.cachedTrafficPlateIds, value);
                    case "exclusivePlates" -> replace(WaypointPlateSettingsScreen.cachedExclusivePlateIds, value);
                    case "townTraffic" -> {
                        boolean enabled = Boolean.parseBoolean(value);
                        WaypointTrafficSettingsScreen.cachedTownTrafficEnabled = enabled;
                        OneWayMarkerSettingsScreen.cachedTownTrafficEnabled = enabled;
                    }
                    case "legacySteering" -> WaypointMovementModeScreen.cachedUseLegacySteering = Boolean.parseBoolean(value);
                    case "maxCars" -> WaypointMiscSettingsScreen.cachedMaxSpawnedCars = Integer.parseInt(value);
                    case "aiThrottle" -> WaypointMiscSettingsScreen.cachedAiThrottleIntervalTicks = Integer.parseInt(value);
                    case "spawnInterval" -> WaypointMiscSettingsScreen.cachedSpawnIntervalTicks = Integer.parseInt(value);
                    case "recall" -> WaypointMiscSettingsScreen.cachedRecallDistantCarsEnabled = Boolean.parseBoolean(value);
                    case "nightLights" -> WaypointMiscSettingsScreen.cachedNightLightsEnabled = Boolean.parseBoolean(value);
                    case "customCars" -> WaypointVehicleListScreen.applySyncedCustomCars(value);
                    default -> {
                    }
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private static void replace(Set<String> target, String csv) {
        target.clear();
        if (!csv.isEmpty()) {
            target.addAll(Arrays.asList(csv.split(",")));
        }
    }
}
