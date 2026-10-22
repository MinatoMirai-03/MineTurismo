package com.examplebett.item;

import com.examplebett.entity.VehiclePresets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class UsedCarListings {

    public static final int REPAIR_NONE = 0;
    public static final int REPAIR_YES = 1;

    public record Listing(String id, String presetId, String image, int price, int year, int mileageKm, int repair, String noteKey) {

        public ItemStack createStack() {
            CompoundTag tag = VehiclePresets.get(this.presetId);
            return tag == null ? ItemStack.EMPTY : VehicleItemIcons.createBoxedStack(tag);
        }
    }

    public static final List<Listing> LISTINGS = List.of(

            new Listing("flatiron_opossum_00", "used_flatiron_opossum", "flatiron_opossum", 2100, 2000, 290000, REPAIR_YES, "customized"),

            new Listing("scandza_gc215_tractor_unit_16", "used_scandza_gc215_tractor_unit_16", "scandza_gc215_tractor_unit_16", 1980, 2016, 920000, REPAIR_NONE, "heavy_use"),

            new Listing("cycas_pixel_van", "used_cycas_pixel_van", "cycas_pixel_van", 880, 1990, 470000, REPAIR_YES, "drift_spec")
    );

    private UsedCarListings() {
    }

    public static Listing find(String id) {
        for (Listing listing : LISTINGS) {
            if (listing.id().equals(id)) {
                return listing;
            }
        }
        return null;
    }
}
