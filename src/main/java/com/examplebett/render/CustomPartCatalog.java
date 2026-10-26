package com.examplebett.render;

//? if >=1.19.3 {
import net.minecraft.resources.FileToIdConverter;
//?}
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CustomPartCatalog {

    private static final Map<String, List<String>> cache = new HashMap<>();

    private CustomPartCatalog() {
    }

    public static List<String> list(ResourceManager resourceManager, String suffix) {
        return cache.computeIfAbsent(suffix, s -> scan(resourceManager, s));
    }

    private static List<String> scan(ResourceManager resourceManager, String suffix) {
        List<ResourceLocation> files = new ArrayList<>();

        //? if >=1.19.3 {
        files.addAll(new FileToIdConverter("models", suffix).listMatchingResources(resourceManager).keySet());
        //?}
        //? if >=1.19 && <1.19.3 {
        /*files.addAll(resourceManager.listResources("models",
                loc -> loc.getPath().endsWith(suffix)).keySet());*/
        //?}
        //? if <1.19 {
        /*files.addAll(resourceManager.listResources("models",
                path -> path.endsWith(suffix)));*/
        //?}
        List<String> ids = new ArrayList<>();
        for (ResourceLocation loc : files) {
            String path = loc.getPath();

            int slash = path.lastIndexOf('/');
            String fileName = slash >= 0 ? path.substring(slash + 1) : path;
            ids.add(fileName.substring(0, fileName.length() - suffix.length()));
        }
        ids.sort(String::compareTo);
        return ids;
    }

    public static void clearCache() {
        cache.clear();
    }
}
