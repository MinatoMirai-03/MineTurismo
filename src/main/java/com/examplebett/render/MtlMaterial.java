package com.examplebett.render;

import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MtlMaterial {

    public float r = 1f, g = 1f, b = 1f, a = 1f;
    public String texturePath = null;

    public ResourceLocation texture = null;

    public boolean tintable = false;

    public boolean translucent = false;

    public boolean doubleSided = false;

    public static Map<String, MtlMaterial> load(InputStream stream) throws IOException {
        Map<String, MtlMaterial> materials = new HashMap<>();
        MtlMaterial current = null;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                switch (parts[0]) {
                    case "newmtl" -> {
                        current = new MtlMaterial();
                        materials.put(parts[1], current);
                    }
                    case "Kd" -> {
                        if (current != null) {
                            current.r = Float.parseFloat(parts[1]);
                            current.g = Float.parseFloat(parts[2]);
                            current.b = Float.parseFloat(parts[3]);
                        }
                    }
                    case "map_Kd" -> {
                        if (current != null) {
                            current.texturePath = parts[1];
                        }
                    }
                }
            }
        }

        return materials;
    }
}
