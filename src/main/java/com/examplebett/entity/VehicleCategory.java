package com.examplebett.entity;

public enum VehicleCategory {

    OPEN_CAR("open_car", "gui.mineturismo.vehicle_category.open_car"),

    EV("ev", "gui.mineturismo.vehicle_category.ev"),

    STANDARD("standard", "gui.mineturismo.vehicle_category.standard"),

    HELICOPTER("helicopter", "gui.mineturismo.vehicle_category.helicopter"),

    SUPERCAR("supercar", "gui.mineturismo.vehicle_category.supercar"),

    SPORTSCAR("sportscar", "gui.mineturismo.vehicle_category.sportscar"),

    PASSENGER_TRANSPORT("passenger_transport", "gui.mineturismo.vehicle_category.passenger_transport"),

    RAILWAY("railway", "gui.mineturismo.vehicle_category.railway"),

    MUSCLE_CAR("muscle_car", "gui.mineturismo.vehicle_category.muscle_car"),

    MOTORCYCLE("motorcycle", "gui.mineturismo.vehicle_category.motorcycle"),

    HYBRID("hybrid", "gui.mineturismo.vehicle_category.hybrid"),

    TRAILER_HEAD("trailer_head", "gui.mineturismo.vehicle_category.trailer_head"),

    TRAILER("trailer", "gui.mineturismo.vehicle_category.trailer");

    public final String id;
    public final String translationKey;

    VehicleCategory(String id, String translationKey) {
        this.id = id;
        this.translationKey = translationKey;
    }

    public static VehicleCategory byId(String id) {

        if ("large_vehicle".equals(id)) {
            return TRAILER_HEAD;
        }
        for (VehicleCategory category : values()) {
            if (category.id.equals(id)) {
                return category;
            }
        }
        return null;
    }
}
