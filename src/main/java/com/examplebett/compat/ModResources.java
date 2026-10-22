package com.examplebett.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.io.Reader;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class ModResources {
	private ModResources() {
	}

	public interface IoSupplier<T> {
		T get() throws IOException;
	}

	public static Map<ResourceLocation, IoSupplier<InputStream>> listJsonResources(
			ResourceManager resourceManager, String namespace) {
		Map<ResourceLocation, IoSupplier<InputStream>> result = new LinkedHashMap<>();

		if (resourceManager == null) {
			return result;
		}
		//? if >=1.19 {
		Map<ResourceLocation, net.minecraft.server.packs.resources.Resource> resources =
				resourceManager.listResources(namespace, location -> location.getPath().endsWith(".json"));
		for (Map.Entry<ResourceLocation, net.minecraft.server.packs.resources.Resource> entry : resources.entrySet()) {
			net.minecraft.server.packs.resources.Resource resource = entry.getValue();
			result.put(entry.getKey(), resource::open);
		}
		//?} else {
		/*java.util.Collection<ResourceLocation> resources = resourceManager.listResources(
				namespace, path -> path.endsWith(".json"));
		for (ResourceLocation location : resources) {
			result.put(location, () -> resourceManager.getResource(location).getInputStream());
		}*/
		//?}
		return result;
	}

	public static Optional<Resource> getResourceOptional(ResourceManager resourceManager, ResourceLocation location) {
		//? if >=1.19 {
		return resourceManager.getResource(location);
		//?} else {
		/*try {
			return Optional.of(resourceManager.getResource(location));
		} catch (IOException e) {
			return Optional.empty();
		}*/
		//?}
	}

	public static List<IoSupplier<InputStream>> getResourceStack(ResourceManager resourceManager, ResourceLocation location) {
		List<IoSupplier<InputStream>> result = new java.util.ArrayList<>();
		//? if >=1.19 {
		for (Resource resource : resourceManager.getResourceStack(location)) {
			result.add(resource::open);
		}
		//?} else {
		/*try {
			for (Resource resource : resourceManager.getResources(location)) {
				result.add(resource::getInputStream);
			}
		} catch (IOException e) {

		}*/
		//?}
		return result;
	}

	public static InputStream open(Resource resource) throws IOException {
		//? if >=1.19 {
		return resource.open();
		//?} else
		/*return resource.getInputStream();*/
	}

	public static JsonObject parseJsonObject(Reader reader) {
		//? if >=1.18 {
		return JsonParser.parseReader(reader).getAsJsonObject();
		//?} else
		/*return new JsonParser().parse(reader).getAsJsonObject();*/
	}

	//? if >1.16 && <1.17 {
	/*private static net.minecraft.server.packs.resources.ResourceManager mineturismo$serverResourceManager;

	public static void registerServerResourceManagerHook() {
		ModServerEvents.onServerStarted(server -> {
			try {
				java.lang.reflect.Field resourcesField = ModReflection.findField(
						net.minecraft.server.MinecraftServer.class, "resources", "field_25318");
				net.minecraft.server.ServerResources resources = (net.minecraft.server.ServerResources) resourcesField.get(server);
				mineturismo$serverResourceManager = resources.getResourceManager();
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException("Failed to read MinecraftServer#resources via reflection", e);
			}
		});
		net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(
				(server, resources, success) -> mineturismo$serverResourceManager = resources.getResourceManager());
	}*/
	//?}

	//? if 1.16 {
	/*private static net.minecraft.server.packs.resources.ResourceManager mineturismo$serverResourceManager;

	public static void registerServerResourceManagerHook() {

		ModServerEvents.onServerStarted(server -> {
			try {

				java.lang.reflect.Field resourcesField = ModReflection.findField(
						net.minecraft.server.MinecraftServer.class, "resources", "field_25318");
				net.minecraft.server.ServerResources resources = (net.minecraft.server.ServerResources) resourcesField.get(server);
				mineturismo$serverResourceManager = resources.getResourceManager();
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException("Failed to read MinecraftServer#resources via reflection", e);
			}
		});
	}*/
	//?}

	//? if <1.16 {
	/*private static net.minecraft.server.packs.resources.ResourceManager mineturismo$serverResourceManager;

	public static void registerServerResourceManagerHook() {
		ModServerEvents.onServerStarted(server -> mineturismo$serverResourceManager = server.getResources());
	}*/
	//?}

	public static ResourceManager getServerResourceManager(net.minecraft.server.MinecraftServer server) {
		//? if >=1.17 {
		return server.getResourceManager();
		//?} else
		/*return mineturismo$serverResourceManager;*/
	}
}
