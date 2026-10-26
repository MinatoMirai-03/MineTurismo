//? if >=1.18 {
//?}
//? if <1.18 && >=1.17.1 {
/*
package com.examplebett.client;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.block.AiObjectModBlocks;
import com.examplebett.client.gui.CarWrenchScreen;
import com.examplebett.client.gui.RailwayConsistScreen;
import com.examplebett.client.gui.WheelAdjustScreen;
import com.examplebett.client.gui.OneWayMarkerSettingsScreen;
import com.examplebett.client.gui.TrafficSignalSettingsScreen;
import com.examplebett.client.gui.WaypointTrafficSettingsScreen;
import com.examplebett.entity.AiObjectModEntities;
import com.examplebett.entity.BreakableTireStackEntity;
import com.examplebett.entity.BreakableBoxEntity;
import com.examplebett.entity.BreakableConeEntity;
import com.examplebett.entity.CarEntity;
import com.examplebett.client.render.BrakeCaliperModel;
import com.examplebett.client.render.BreakableTireStackRenderer;
import com.examplebett.client.render.CameraAttachmentLoader;
import com.examplebett.client.render.CarEntityRenderer;
import com.examplebett.client.render.GlbModelLoader;
import com.examplebett.client.render.LightAttachmentLoader;
import com.examplebett.client.render.LiveryLoader;
import com.examplebett.client.render.LodWheelModel;
import com.examplebett.client.render.PlateAttachmentLoader;
import com.examplebett.client.render.PoliceLightModel;
import com.examplebett.client.render.RearWingModel;
import com.examplebett.client.render.OtherPartModel;
import com.examplebett.client.render.TaxiSignModel;
import com.examplebett.client.render.WheelAttachmentLoader;
import com.examplebett.client.render.WheelColorLoader;
import com.examplebett.client.render.WheelStyleCatalog;
import com.examplebett.client.sound.EngineSoundInstance;
import com.examplebett.client.sound.HornSoundInstance;
import com.examplebett.client.sound.PoliceSirenSoundInstance;
import com.examplebett.client.sound.TireSlipSoundInstance;
import com.examplebett.item.CarSpawnItem;
import com.examplebett.item.OneWayWaypointMarkerItem;
import com.examplebett.item.RailwayWaypointMarkerItem;
import com.examplebett.item.TrafficSignalMarkerItem;
import com.examplebett.item.WaypointMarkerItem;
import com.examplebett.network.OpenRailwaySpawnSetupPayload;
import com.examplebett.network.OpenRailwayConsistSettingsPayload;
import com.examplebett.network.OpenSignalSettingsPayload;
import com.examplebett.network.PlayCollisionSoundPayload;
import com.examplebett.network.SetDetectedWheelFootprintPayload;
import com.examplebett.network.SetClientSlipReportPayload;
import com.examplebett.network.SetClientWheelieTriggerPayload;
import com.examplebett.network.SetWheelRotationReportPayload;
import com.examplebett.network.ShowHudWarningPayload;
import com.examplebett.network.SyncAmbientEngineVolumePayload;
import com.examplebett.network.SyncRailwayFormationOriginPayload;
import com.examplebett.network.SyncWheelDistancePayload;
import com.examplebett.waypoint.WaypointNetworkSavedData;
import com.examplebett.network.SetHornStatePayload;
import com.examplebett.network.ToggleTrailerCouplingPayload;
import com.examplebett.network.SetPoliceLightsOnPayload;
import com.examplebett.render.CompiledCarModel;
import com.examplebett.render.CustomPartCatalog;
import com.examplebett.render.FrontBrakeCaliperParts;
import com.examplebett.render.ObjModel;
import com.examplebett.render.ObjModelLoader;
import com.examplebett.render.OtherParts;
import com.examplebett.render.PoliceLightParts;
import com.examplebett.render.RearBrakeCaliperParts;
import com.examplebett.render.RearWingParts;
import com.examplebett.render.TaxiSignParts;
import com.examplebett.sound.AiObjectModSounds;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import com.examplebett.compat.ModRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiObjectModClient implements ClientModInitializer {

	public interface ExternalCarModelLoader {
		void tryLoad(CarEntity car);
	}

	public static ExternalCarModelLoader externalCarModelLoader = null;

	private static final long MOUNT_TOAST_TOTAL_MS = 3000L;
	private static final long MOUNT_TOAST_FADE_IN_MS = 300L;
	private static final long MOUNT_TOAST_FADE_OUT_MS = 700L;
	private static final ResourceLocation MOUNT_TOAST_FONT = AiObjectMod.id("mount_toast");
	private static final float MOUNT_TOAST_SCALE = 1.5f;

	private static boolean mountToastCustomFontEnabled = true;

	public static boolean isMountToastCustomFontEnabled() {
		return mountToastCustomFontEnabled;
	}

	public static void setMountToastCustomFontEnabled(boolean enabled) {
		mountToastCustomFontEnabled = enabled;
	}

	private static boolean fakeReflectionEnabled = false;

	public static boolean isFakeReflectionEnabled() {
		return fakeReflectionEnabled;
	}

	public static void setFakeReflectionEnabled(boolean enabled) {
		fakeReflectionEnabled = enabled;
	}

	private static boolean hintsEnabled = true;

	public static boolean isHintsEnabled() {
		return hintsEnabled;
	}

	public static void setHintsEnabled(boolean enabled) {
		hintsEnabled = enabled;
	}

	private static boolean fpsLogEnabled = false;

	public static boolean isFpsLogEnabled() {
		return fpsLogEnabled;
	}

	public static void setFpsLogEnabled(boolean enabled) {
		fpsLogEnabled = enabled;
	}

	private static final long FPS_LOG_INTERVAL_TICKS = 20L;
	private static long fpsLogTicks = 0L;

	private static final java.lang.reflect.Field LEGACY_FPS_FIELD = resolveLegacyFpsField();

	private static java.lang.reflect.Field resolveLegacyFpsField() {
		try {
			java.lang.reflect.Field field = com.examplebett.compat.ModReflection.findField(net.minecraft.client.Minecraft.class, "fps", "field_1738");
			field.setAccessible(true);
			return field;
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}

	private static int getGameFps(net.minecraft.client.Minecraft client) {
		if (LEGACY_FPS_FIELD == null) {
			return -1;
		}
		try {
			return LEGACY_FPS_FIELD.getInt(null);
		} catch (ReflectiveOperationException e) {
			return -1;
		}
	}

	public enum SpeedDisplayMode {
		NONE, KMH, MPH;

		public SpeedDisplayMode next() {
			return switch (this) {
				case NONE -> KMH;
				case KMH -> MPH;
				case MPH -> NONE;
			};
		}
	}

	private static SpeedDisplayMode speedDisplayMode = SpeedDisplayMode.NONE;
	private static final float SPEED_NUMBER_SCALE = 2.0f;
	private static final float SPEED_UNIT_SCALE = 1.0f;
	private static final int SPEED_UNIT_GAP = 3;

	public static SpeedDisplayMode getSpeedDisplayMode() {
		return speedDisplayMode;
	}

	public static void setSpeedDisplayMode(SpeedDisplayMode mode) {
		speedDisplayMode = mode;
	}
	private static final Map<String, Item> MODEL_ID_TO_SPAWN_ITEM = new HashMap<>();

	private static final Map<String, CompiledCarModel> compiledCarModelCache = new HashMap<>();

	private static final Map<String, WheelCompiledPair> compiledWheelModelCache = new HashMap<>();
	private record WheelCompiledPair(CompiledCarModel normal, CompiledCarModel mirrored) {
	}
	private static Entity lastVehicle = null;
	private static Component mountToastText = null;

	private static Component mountToastCategoryText = null;
	private static long mountToastStartMs = -1L;

	private static final long HUD_WARNING_TOTAL_MS = 3000L;
	private static final long HUD_WARNING_FADE_IN_MS = 200L;
	private static final long HUD_WARNING_FADE_OUT_MS = 700L;
	private static Component hudWarningText = null;
	private static long hudWarningStartMs = -1L;

	private record TimedHint(long delayTicks, String... lineKeys) {
	}

	private static final TimedHint[] TIMED_HINTS = {
			new TimedHint(1200L,
					"gui.mineturismo.reflection_tip.line1",
					"gui.mineturismo.reflection_tip.line2",
					"gui.mineturismo.reflection_tip.line3"),
			new TimedHint(1360L,
					"gui.mineturismo.shader_tip.line1",
					"gui.mineturismo.shader_tip.line2"),
			new TimedHint(3600L,
					"gui.mineturismo.smart_dispatch_tip.line1",
					"gui.mineturismo.smart_dispatch_tip.line2"),
			new TimedHint(6000L,
					"gui.mineturismo.wrench_tip.line1",
					"gui.mineturismo.wrench_tip.line2"),
			new TimedHint(12000L,
					"gui.mineturismo.traffic_marker_tip.line1",
					"gui.mineturismo.traffic_marker_tip.line2"),
	};

	private static final long HINT_TIP_TOTAL_MS = 8000L;
	private static final long HINT_TIP_FADE_IN_MS = 300L;
	private static final long HINT_TIP_FADE_OUT_MS = 700L;
	private static final float HINT_TIP_SCALE = 1.1f;

	private static final float HINT_TIP_SUB_SCALE = 0.75f;
	private static int nextHintIndex = 0;
	private static long hintTicks = 0L;
	private static Component[] hintTipLines = null;
	private static long hintTipStartMs = -1L;
	private static boolean creditsScreenChecked = false;

	private static final Map<CarEntity, EngineSoundInstance> engineSounds = new HashMap<>();

	private static final Map<CarEntity, EngineSoundInstance> engineLowSounds = new HashMap<>();

	private static final int WHEEL_ROTATION_REPORT_INTERVAL_TICKS = 2;
	private static int wheelRotationReportTickCounter = 0;

	private static final Map<CarEntity, TireSlipSoundInstance> tireSlipSounds = new HashMap<>();

	private static final Map<CarEntity, PoliceSirenSoundInstance> policeSirenSounds = new HashMap<>();

	private static final Map<CarEntity, EngineSoundInstance> ambientEngineSounds = new HashMap<>();
	private static final double AMBIENT_ENGINE_RANGE = 24.0;
	private static final int MAX_AMBIENT_ENGINE_SOUNDS = 3;
	private static final float AMBIENT_ENGINE_MIN_VOLUME = 0.2f;
	private static final float AMBIENT_ENGINE_MAX_VOLUME = 0.4f;

	private static float ambientEngineVolumeMultiplier = WaypointNetworkSavedData.DEFAULT_AMBIENT_ENGINE_VOLUME_MULTIPLIER;

	private static final String KEY_CATEGORY = "key.categories.mineturismo";
	private static final KeyMapping HORN_KEY = new KeyMapping(
			"key.mineturismo.horn", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, KEY_CATEGORY);

	private static final KeyMapping TRAILER_COUPLING_KEY = new KeyMapping(
			"key.mineturismo.trailer_coupling", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_L, KEY_CATEGORY);

	public static KeyMapping hornKey() {
		return HORN_KEY;
	}

	public static KeyMapping trailerCouplingKey() {
		return TRAILER_COUPLING_KEY;
	}

	private static HornSoundInstance activeHorn = null;

	private static CarEntity hornBroadcastCar = null;

	private static boolean hornKeyWasDown = false;

	private static boolean trailerCouplingKeyWasDown = false;

	private static boolean sprintKeyHeldSent = false;
	private static CarEntity sprintKeyHeldCar = null;

	private static void mineturismo$handlePlayCollisionSound(PlayCollisionSoundPayload payload) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			return;
		}

		net.minecraft.sounds.SoundEvent collisionSound = payload.lowSpeed() ? AiObjectModSounds.CAR_COLLISION_LOW : AiObjectModSounds.CAR_COLLISION;

		SimpleSoundInstance sound = new SimpleSoundInstance(
				collisionSound.getLocation(), SoundSource.NEUTRAL,
				payload.volume(), payload.pitch(),
				false, 0, SoundInstance.Attenuation.NONE,
				payload.x(), payload.y(), payload.z(), false);
		mc.getSoundManager().play(sound);
	}

	private static void mineturismo$handleShowHudWarning(ShowHudWarningPayload payload) {
		hudWarningText = ModText.translatable(payload.messageKey());
		hudWarningStartMs = System.currentTimeMillis();
		Minecraft.getInstance().getSoundManager().play(
				SimpleSoundInstance.forUI(AiObjectModSounds.PASSENGER_TRANSPORT_DENIED, 1.0f));
	}

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(AiObjectModEntities.CAR_ENTITY, CarEntityRenderer::new);
		EntityRendererRegistry.register(AiObjectModEntities.BREAKABLE_TIRE_STACK, BreakableTireStackRenderer<BreakableTireStackEntity>::new);
		EntityRendererRegistry.register(AiObjectModEntities.BREAKABLE_BOX, BreakableTireStackRenderer<BreakableBoxEntity>::new);
		EntityRendererRegistry.register(AiObjectModEntities.BREAKABLE_BOX_2, BreakableTireStackRenderer<BreakableBoxEntity>::new);
		EntityRendererRegistry.register(AiObjectModEntities.TRAFFIC_CONE, BreakableTireStackRenderer<BreakableConeEntity>::new);
		EntityRendererRegistry.register(AiObjectModEntities.BARRICADE, BreakableTireStackRenderer<com.examplebett.entity.BreakableBarricadeEntity>::new);

		KeyBindingHelper.registerKeyBinding(HORN_KEY);
		KeyBindingHelper.registerKeyBinding(TRAILER_COUPLING_KEY);

		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
				new SimpleSynchronousResourceReloadListener() {
					@Override
					public ResourceLocation getFabricId() {
						return AiObjectMod.id("clear_render_caches");
					}

					@Override
					public void onResourceManagerReload(ResourceManager resourceManager) {
						ObjModelLoader.clearCache();
						GlbModelLoader.clearCache();
						WheelAttachmentLoader.clearCache();
						WheelColorLoader.clearCache();
						LightAttachmentLoader.clearCache();
						com.examplebett.client.render.VehicleCategoryLoader.clearCache();
						PlateAttachmentLoader.clearCache();
						CameraAttachmentLoader.clearCache();
						LodWheelModel.clearCache();
						LiveryLoader.clearCache();
						RearWingModel.clearCache();
						PoliceLightModel.clearCache();
						TaxiSignModel.clearCache();
						OtherPartModel.clearCache();
						BrakeCaliperModel.clearCache();
						CustomPartCatalog.clearCache();
						WheelStyleCatalog.clearCache();
						RearWingParts.clearCache();
						OtherParts.clearCache();
						PoliceLightParts.clearCache();
						TaxiSignParts.clearCache();
						FrontBrakeCaliperParts.clearCache();
						RearBrakeCaliperParts.clearCache();
						compiledCarModelCache.clear();
						compiledWheelModelCache.clear();
					}
				});

		ModClientNetworking.registerS2C(PlayCollisionSoundPayload.ID, PlayCollisionSoundPayload::read, AiObjectModClient::mineturismo$handlePlayCollisionSound);

		ModClientNetworking.registerS2C(SyncWheelDistancePayload.ID, SyncWheelDistancePayload::read, payload -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc.level == null) {
				return;
			}
			if (mc.level.getEntity(payload.entityId()) instanceof CarEntity car) {
				car.setNetworkWheelDistance(payload.distance());
			}
		});

		ModClientNetworking.registerS2C(OpenRailwayConsistSettingsPayload.ID, OpenRailwayConsistSettingsPayload::read, payload -> {
			net.minecraft.client.multiplayer.ClientLevel mineturismo$level = Minecraft.getInstance().level;
			if (mineturismo$level != null && mineturismo$level.getEntity(payload.headEntityId()) instanceof CarEntity car) {
				Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
				Minecraft.getInstance().setScreen(new RailwayConsistScreen(car, null));
			}
		});

		ModClientNetworking.registerS2C(OpenSignalSettingsPayload.ID, OpenSignalSettingsPayload::read, payload -> {
			char direction = payload.direction().isEmpty() ? 'N' : payload.direction().charAt(0);
			TrafficSignalSettingsScreen.applyExternalConfig(payload.signalId(), payload.intersectionName(), direction,
					payload.greenTicks(), payload.redTicks(), payload.phaseOffsetTicks());
			Minecraft.getInstance().setScreen(new TrafficSignalSettingsScreen());
		});

		ModClientNetworking.registerS2C(OpenRailwaySpawnSetupPayload.ID, OpenRailwaySpawnSetupPayload::read, payload -> {
			Minecraft.getInstance().setScreen(new com.examplebett.client.gui.RailwaySpawnVehicleScreen(payload.originNodeId()));
		});

		ModClientNetworking.registerS2C(SyncRailwayFormationOriginPayload.ID, SyncRailwayFormationOriginPayload::read, payload -> {
			com.examplebett.inventory.RailwayFormationMenu.pendingOriginNodeId = payload.originNodeId();
		});

		ModClientNetworking.registerS2C(SyncAmbientEngineVolumePayload.ID, SyncAmbientEngineVolumePayload::read, payload -> {
			ambientEngineVolumeMultiplier = payload.multiplier();
			com.examplebett.client.gui.WaypointClientSettings.apply(payload.multiplier(), payload.settings());
		});

		ModClientNetworking.registerS2C(ShowHudWarningPayload.ID, ShowHudWarningPayload::read, AiObjectModClient::mineturismo$handleShowHudWarning);

		BlockRenderLayerMap.INSTANCE.putBlock(AiObjectModBlocks.CAR_BREAKABLE_GLASS, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(AiObjectModBlocks.WATER_PUDDLE, RenderType.translucent());

		CarEntity.wrenchMenuOpener = (car, player) -> {
			Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
			Minecraft.getInstance().setScreen(new CarWrenchScreen(car));
		};
		CarEntity.wheelWrenchMenuOpener = (car, player) -> {
			Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
			Minecraft.getInstance().setScreen(new WheelAdjustScreen(car));
		};
		CarEntity.debugWrenchMenuOpener = (car, player) -> Minecraft.getInstance().setScreen(new com.examplebett.client.gui.DebugWrenchScreen(car));
		WaypointMarkerItem.trafficSettingsMenuOpener = player -> Minecraft.getInstance().setScreen(new WaypointTrafficSettingsScreen());
		OneWayWaypointMarkerItem.settingsMenuOpener = player -> Minecraft.getInstance().setScreen(new OneWayMarkerSettingsScreen());
		com.examplebett.item.SmartDispatchAppItem.appScreenOpener = player -> Minecraft.getInstance().setScreen(new com.examplebett.client.gui.SmartDispatchAppScreen());
		ClientTickEvents.END_CLIENT_TICK.register(com.examplebett.client.GttClient::tick);
		ClientTickEvents.END_CLIENT_TICK.register(com.examplebett.client.sound.ChorusMusicPlayer::tick);

		ModClientNetworking.registerS2C(com.examplebett.network.GttScorePayload.ID, com.examplebett.network.GttScorePayload::read, payload -> com.examplebett.client.GttClient.setScore(payload.score()));

		ModClientNetworking.registerS2C(com.examplebett.network.SlotSpinResultPayload.ID, com.examplebett.network.SlotSpinResultPayload::read, payload -> com.examplebett.client.gui.SmartDispatchAppScreen.onSlotResult(payload.prizes()));

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> com.examplebett.client.GttClient.renderHud(new GuiGraphics(poseStack)));

		net.minecraft.client.gui.screens.MenuScreens.register(com.examplebett.inventory.AiObjectModMenus.RAILWAY_FORMATION_MENU_TYPE, com.examplebett.client.gui.RailwayFormationScreen::new);
		net.minecraft.client.gui.screens.MenuScreens.register(com.examplebett.inventory.AiObjectModMenus.MASSIVE_AUTO_APPRAISAL_MENU_TYPE, com.examplebett.client.gui.MassiveAutoAppraisalScreen::new);

		HudRenderCallback.EVENT.register((poseStack, deltaTracker) -> mineturismo$renderWaypointHint(new GuiGraphics(poseStack)));

		CarEntity.clientDescendKeyHeld = () -> Minecraft.getInstance().options.keySprint.isDown();

		CarEntity.clientForwardKeyHeld = () -> Minecraft.getInstance().options.keyUp.isDown();
		CarEntity.clientBackKeyHeld = () -> Minecraft.getInstance().options.keyDown.isDown();

		CarEntity.clientDimensionsResourceManager = () -> {
			var integratedServer = Minecraft.getInstance().getSingleplayerServer();
			return integratedServer != null ? integratedServer.getResourceManager() : null;
		};

		for (Item item : ModRegistries.ITEM) {
			if (item instanceof CarSpawnItem spawnItem) {
				MODEL_ID_TO_SPAWN_ITEM.putIfAbsent(spawnItem.getModelId(), item);
			}
		}

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> renderMountToast(new GuiGraphics(poseStack)));

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> renderSpeedDisplay(new GuiGraphics(poseStack)));

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> renderPassengerTransportWarning(new GuiGraphics(poseStack)));

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> renderHintTip(new GuiGraphics(poseStack)));

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			nextHintIndex = 0;
			hintTicks = 0L;
			hintTipStartMs = -1L;
		});

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			boolean mineturismo$pressed = false;
			while (TRAILER_COUPLING_KEY.consumeClick()) {
				mineturismo$pressed = true;
			}

			InputConstants.Key mineturismo$boundKey = KeyBindingHelper.getBoundKeyOf(TRAILER_COUPLING_KEY);
			boolean mineturismo$rawDown = mineturismo$boundKey.getType() == InputConstants.Type.KEYSYM
					&& InputConstants.isKeyDown(client.getWindow().getWindow(), mineturismo$boundKey.getValue());
			if (mineturismo$rawDown && !trailerCouplingKeyWasDown && client.screen == null) {
				mineturismo$pressed = true;
			}
			trailerCouplingKeyWasDown = mineturismo$rawDown;

			if (client.player != null && client.screen == null
					&& client.player.getVehicle() instanceof CarEntity mineturismo$headCar
					&& mineturismo$headCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
				for (KeyMapping mineturismo$other : client.options.keyMappings) {
					if (mineturismo$other != TRAILER_COUPLING_KEY && mineturismo$other.same(TRAILER_COUPLING_KEY)) {
						while (mineturismo$other.consumeClick()) {
							mineturismo$pressed = true;
						}
					}
				}
			}
			if (!mineturismo$pressed || client.player == null || client.screen != null
					|| !(client.player.getVehicle() instanceof CarEntity mineturismo$car)
					|| !mineturismo$car.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
				return;
			}
			for (KeyMapping mineturismo$other : client.options.keyMappings) {
				if (mineturismo$other != TRAILER_COUPLING_KEY && mineturismo$other.same(TRAILER_COUPLING_KEY)) {
					while (mineturismo$other.consumeClick()) {

					}
				}
			}
			ToggleTrailerCouplingPayload mineturismo$payload = new ToggleTrailerCouplingPayload(mineturismo$car.getId());
			ModClientNetworking.sendToServer(ToggleTrailerCouplingPayload.ID, mineturismo$payload, ToggleTrailerCouplingPayload::write);
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (creditsScreenChecked) return;
			if (client.player == null || client.level == null) return;
			creditsScreenChecked = true;
			com.examplebett.client.gui.CreditsScreen.showOnceIfNeeded();
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!fpsLogEnabled) return;
			fpsLogTicks++;
			if (fpsLogTicks % FPS_LOG_INTERVAL_TICKS != 0) return;
			int fps = getGameFps(client);
			int carCount = 0;
			if (client.level != null) {
				for (Entity entity : client.level.entitiesForRendering()) {
					if (entity instanceof CarEntity) {
						carCount++;
					}
				}
			}
			AiObjectMod.LOGGER.info("[fps-log] fps={} frameTimeMs={} carCount={}", fps,
					fps > 0 ? String.format("%.1f", 1000.0 / fps) : "n/a", carCount);
		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (hintsEnabled && client.player != null) {
				hintTicks++;
				while (nextHintIndex < TIMED_HINTS.length && hintTicks >= TIMED_HINTS[nextHintIndex].delayTicks()) {
					TimedHint hint = TIMED_HINTS[nextHintIndex];
					hintTipLines = new Component[hint.lineKeys().length];
					for (int i = 0; i < hint.lineKeys().length; i++) {
						hintTipLines[i] = ModText.translatable(hint.lineKeys()[i]);
					}
					hintTipStartMs = System.currentTimeMillis();
					client.getSoundManager().play(
							SimpleSoundInstance.forUI(AiObjectModSounds.PASSENGER_TRANSPORT_DENIED, 1.0f));
					nextHintIndex++;
				}
			}

			CarEntity drivingCar = null;
			if (client.player != null) {
				Entity vehicle = client.player.getVehicle();
				if (vehicle instanceof CarEntity car) {
					drivingCar = car;
				}
				if (vehicle instanceof CarEntity car && vehicle != lastVehicle) {
					mountToastText = getCarDisplayName(car);
					if (car.getVehicleCategories() == null) {
						com.examplebett.client.render.VehicleCategoryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setVehicleCategories);
					}
					mountToastCategoryText = getCarCategoryText(car);
					mountToastStartMs = System.currentTimeMillis();
				}
				lastVehicle = vehicle;
			}

			if (drivingCar != null) {
				EngineSoundInstance existing = engineSounds.get(drivingCar);

				if (existing != null && !existing.matchesCurrentSoundId()) {
					client.getSoundManager().stop(existing);
					existing = null;
				}

				if (existing == null || existing.isReadyForSuccessor()) {
					EngineSoundInstance sound = new EngineSoundInstance(drivingCar, 1.0f, false,
							drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)
									? EngineSoundInstance.Layer.HYBRID_HIGH : EngineSoundInstance.Layer.NORMAL);
					client.getSoundManager().play(sound);
					engineSounds.put(drivingCar, sound);
				}

				if (drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)) {
					EngineSoundInstance lowExisting = engineLowSounds.get(drivingCar);
					if (lowExisting == null || lowExisting.isReadyForSuccessor()) {
						EngineSoundInstance lowSound = new EngineSoundInstance(drivingCar, 1.0f, false, EngineSoundInstance.Layer.HYBRID_LOW);
						client.getSoundManager().play(lowSound);
						engineLowSounds.put(drivingCar, lowSound);
					}
				}
			}

			CarEntity currentDrivingCar = drivingCar;
			engineSounds.entrySet().removeIf(e -> {
				if (e.getKey() != currentDrivingCar) {
					client.getSoundManager().stop(e.getValue());
					return true;
				}
				return e.getValue().isStopped();
			});

			engineLowSounds.entrySet().removeIf(e -> {
				if (e.getKey() != currentDrivingCar || !e.getKey().hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)) {
					client.getSoundManager().stop(e.getValue());
					return true;
				}
				return e.getValue().isStopped();
			});

			wheelRotationReportTickCounter++;
			if (drivingCar != null && wheelRotationReportTickCounter % WHEEL_ROTATION_REPORT_INTERVAL_TICKS == 0) {
				SetWheelRotationReportPayload mineturismo$payload = new SetWheelRotationReportPayload(
						drivingCar.getId(), drivingCar.getWheelDistanceTraveled());
				ModClientNetworking.sendToServer(SetWheelRotationReportPayload.ID, mineturismo$payload, SetWheelRotationReportPayload::write);
			}

			if (drivingCar != null && HORN_KEY.isDown()
					&& com.examplebett.entity.HornSirenSounds.hornById(drivingCar.getHornSoundId()).sound() != null) {
				if (activeHorn == null || activeHorn.isStopped() || !activeHorn.isFor(drivingCar)) {
					if (activeHorn != null) {
						client.getSoundManager().stop(activeHorn);
					}
					activeHorn = new HornSoundInstance(drivingCar);
					client.getSoundManager().play(activeHorn);
				} else {
					activeHorn.requestPlaying();
				}
			} else if (activeHorn != null) {
				activeHorn.requestRelease();
				if (activeHorn.isStopped()) {
					activeHorn = null;
				}
			}

			boolean hornDown = drivingCar != null && HORN_KEY.isDown();
			if (hornDown) {
				if (hornBroadcastCar != drivingCar) {
					if (hornBroadcastCar != null) {
						SetHornStatePayload mineturismo$payload = new SetHornStatePayload(hornBroadcastCar.getId(), false);
						ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
					}
					SetHornStatePayload mineturismo$payload = new SetHornStatePayload(drivingCar.getId(), true);
					ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
					hornBroadcastCar = drivingCar;
				}
			} else if (hornBroadcastCar != null) {
				SetHornStatePayload mineturismo$payload = new SetHornStatePayload(hornBroadcastCar.getId(), false);
				ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
				hornBroadcastCar = null;
			}

			boolean hornKeyDown = HORN_KEY.isDown();
			if (drivingCar != null && hornKeyDown && !hornKeyWasDown && !drivingCar.getPoliceLightEnabledIds().isEmpty()) {

				boolean newLightsOn;
				boolean newSirenOn;
				if (!drivingCar.isPoliceLightsOn()) {

					newLightsOn = true;
					newSirenOn = true;
				} else if (drivingCar.isPoliceSirenOn()) {

					newLightsOn = true;
					newSirenOn = false;
				} else {

					newLightsOn = false;
					newSirenOn = false;
				}
				drivingCar.setPoliceLightsOn(newLightsOn);
				drivingCar.setPoliceSirenOn(newSirenOn);
				SetPoliceLightsOnPayload mineturismo$payload = new SetPoliceLightsOnPayload(drivingCar.getId(), newLightsOn, newSirenOn);
				ModClientNetworking.sendToServer(SetPoliceLightsOnPayload.ID, mineturismo$payload, SetPoliceLightsOnPayload::write);
			}
			hornKeyWasDown = hornKeyDown;

			if (drivingCar != null && drivingCar.consumeClientSlipFlag()) {
				SetClientSlipReportPayload mineturismo$payload = new SetClientSlipReportPayload(drivingCar.getId());
				ModClientNetworking.sendToServer(SetClientSlipReportPayload.ID, mineturismo$payload, SetClientSlipReportPayload::write);
			}

			if (drivingCar != null && drivingCar.consumeClientWheelieTriggerFlag()) {
				SetClientWheelieTriggerPayload mineturismo$wheeliePayload = new SetClientWheelieTriggerPayload(drivingCar.getId());
				ModClientNetworking.sendToServer(SetClientWheelieTriggerPayload.ID, mineturismo$wheeliePayload, SetClientWheelieTriggerPayload::write);
			}

			boolean mineturismo$sprintDown = drivingCar != null && client.options.keySprint.isDown();
			if (drivingCar != null && (mineturismo$sprintDown != sprintKeyHeldSent || drivingCar != sprintKeyHeldCar)) {
				ModClientNetworking.sendToServer(com.examplebett.network.SetSprintKeyHeldPayload.ID, new com.examplebett.network.SetSprintKeyHeldPayload(drivingCar.getId(), mineturismo$sprintDown), com.examplebett.network.SetSprintKeyHeldPayload::write);
				sprintKeyHeldSent = mineturismo$sprintDown;
				sprintKeyHeldCar = drivingCar;
			} else if (drivingCar == null) {
				sprintKeyHeldSent = false;
				sprintKeyHeldCar = null;
			}

			if (client.level == null) return;

			List<CarEntity> ambientEngineCandidates = new ArrayList<>();
			for (Entity entity : client.level.entitiesForRendering()) {
				if (entity instanceof CarEntity car) {
					if (car.getCompiledModel() == null) {
						if (car.getModelId().startsWith(CarEntity.EXTERNAL_MODEL_PREFIX)) {
							if (externalCarModelLoader != null) {
								try {
									externalCarModelLoader.tryLoad(car);
								} catch (Exception e) {
									AiObjectMod.LOGGER.error("Failed to load external model: " + car.getModelId(), e);
								}
							}
						} else {
							try {
								ResourceLocation glbLocation = AiObjectMod.id("models/" + car.getModelId() + ".glb");

								if (client.getResourceManager().hasResource(glbLocation)) {
									var result = GlbModelLoader.load(client.getResourceManager(), car.getModelId());
									car.setModel(result.model());
									car.setMaterials(result.materials());
									car.setCompiledModel(compiledCarModelCache.computeIfAbsent(car.getModelId(),
											id -> CompiledCarModel.compile(result.model(), result.materials(), CompiledCarModel.FALLBACK_TEXTURE, false, id)));
								} else {
									var model = ObjModelLoader.loadModel(client.getResourceManager(), car.getModelId());
									var materials = ObjModelLoader.loadMaterials(client.getResourceManager(), car.getModelId());
									car.setModel(model);
									car.setMaterials(materials);

									car.setCompiledModel(compiledCarModelCache.computeIfAbsent(car.getModelId(),
											id -> CompiledCarModel.compile(model, materials, CompiledCarModel.FALLBACK_TEXTURE)));
								}
							} catch (Exception e) {
								AiObjectMod.LOGGER.error("Failed to load model: " + car.getModelId(), e);
							}
						}
					}

					CompiledCarModel compiledForWheelDetect = car.getCompiledModel();
					if (compiledForWheelDetect != null && !car.hasDetectedWheelFootprint()
							&& compiledForWheelDetect.wheelPivots.size() == 4) {
						float sumAbsX = 0f;
						float sumFrontZ = 0f;
						int frontCount = 0;
						for (ObjModel.WheelPivot pivot : compiledForWheelDetect.wheelPivots) {
							sumAbsX += Math.abs(pivot.centerX);
							if (pivot.front) {
								sumFrontZ += pivot.centerZ;
								frontCount++;
							}
						}
						if (frontCount > 0) {
							float trackHalfWidth = sumAbsX / compiledForWheelDetect.wheelPivots.size();
							float rearZ = sumFrontZ / frontCount;
							car.setDetectedWheelFootprint(trackHalfWidth, rearZ);

							SetDetectedWheelFootprintPayload mineturismo$payload = new SetDetectedWheelFootprintPayload(car.getId(), trackHalfWidth, rearZ);
							ModClientNetworking.sendToServer(SetDetectedWheelFootprintPayload.ID, mineturismo$payload, SetDetectedWheelFootprintPayload::write);
							AiObjectMod.LOGGER.info("Detected wheel footprint for {}: trackHalfWidth={}, rearZ={}",
									car.getModelId(), trackHalfWidth, rearZ);
						}
					}

					if (car.getWheelAttachment() == null) {
						WheelAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setWheelAttachment);
					}

					com.examplebett.render.WheelAttachment attachment = car.getWheelAttachment();
					if (attachment != null) {

						String override = car.getWheelModelIdOverride();
						String desiredWheelModelId = (override != null && !override.isEmpty())
								? override : attachment.wheelModelId();
						boolean isNoneStyle = com.examplebett.render.WheelAttachment.NONE_STYLE.equals(desiredWheelModelId);

						car.setWheelStyleDefaultWidthScale(isNoneStyle ? 1f
								: WheelStyleCatalog.getDefaultWheelWidthScale(client.getResourceManager(), desiredWheelModelId));

						String colorOverride = car.getWheelColorOverride();
						String desiredWheelColorId = "";
						if (!isNoneStyle) {
							var colorOptions = WheelColorLoader.load(client.getResourceManager(), desiredWheelModelId);
							if (colorOptions.isPresent() && !colorOptions.get().ids().isEmpty()) {
								List<String> colorIds = colorOptions.get().ids();
								desiredWheelColorId = (colorOverride != null && colorIds.contains(colorOverride))
										? colorOverride : colorIds.get(0);
							}
						}
						boolean styleChanged = !desiredWheelModelId.equals(car.getLoadedWheelModelId());
						boolean colorChanged = !desiredWheelColorId.equals(car.getLoadedWheelColorId());
						if (styleChanged || colorChanged) {
							if (isNoneStyle) {

								car.setWheelCompiledModel(null);
								car.setWheelCompiledModelMirrored(null);
								car.setLoadedWheelModelId(desiredWheelModelId);
								car.setLoadedWheelColorId(desiredWheelColorId);
							} else {
								try {

									final String wheelModelIdForCache = desiredWheelModelId;
									final String wheelColorIdForCache = desiredWheelColorId;
									String wheelCacheKey = wheelModelIdForCache + "#" + wheelColorIdForCache;
									WheelCompiledPair compiledPair = compiledWheelModelCache.computeIfAbsent(wheelCacheKey, key -> {

										try {
											var wheelModel = ObjModelLoader.loadModel(client.getResourceManager(), wheelModelIdForCache);
											var wheelMaterials = ObjModelLoader.loadMaterials(client.getResourceManager(), wheelModelIdForCache);
											if (!wheelColorIdForCache.isEmpty()) {

												wheelMaterials = ObjModelLoader.loadLiveryMaterials(
														client.getResourceManager(), wheelModelIdForCache, wheelMaterials, wheelColorIdForCache);
											}

											CompiledCarModel compiled = CompiledCarModel.compile(
													wheelModel, wheelMaterials, CompiledCarModel.FALLBACK_TEXTURE, true);
											return new WheelCompiledPair(compiled, CompiledCarModel.mirrorX(compiled));
										} catch (java.io.IOException wheelIoError) {
											throw new java.io.UncheckedIOException(wheelIoError);
										}
									});
									car.setWheelCompiledModel(compiledPair.normal());
									car.setWheelCompiledModelMirrored(compiledPair.mirrored());
									car.setLoadedWheelModelId(desiredWheelModelId);
									car.setLoadedWheelColorId(desiredWheelColorId);
								} catch (Exception wheelLoadError) {
									AiObjectMod.LOGGER.error("Failed to load wheel model: " + desiredWheelModelId, wheelLoadError);
								}
							}
						}
					}

					if (car.getLightAttachment() == null) {
						com.examplebett.client.render.LightAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setLightAttachment);
					}

					if (car.getCameraAttachment() == null) {
						com.examplebett.client.render.CameraAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setCameraAttachment);
					}

					if (car.getPlateAttachment() == null) {
						com.examplebett.client.render.PlateAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setPlateAttachment);
					}

					if (car.getVehicleCategories() == null) {
						com.examplebett.client.render.VehicleCategoryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setVehicleCategories);
					}

					if (car.getLiveryOptions() == null) {
						com.examplebett.client.render.LiveryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setLiveryOptions);
					}

					com.examplebett.render.LiveryOptions liveryOptions = car.getLiveryOptions();
					if (liveryOptions != null && !liveryOptions.ids().isEmpty() && car.getModel() != null) {
						String liveryOverride = car.getLiveryOverride();
						String desiredLivery = (liveryOverride != null && !liveryOverride.isEmpty())
								? liveryOverride : liveryOptions.ids().get(0);
						if (!desiredLivery.equals(car.getLoadedLiveryId())) {
							try {
								var liveryMaterials = ObjModelLoader.loadLiveryMaterials(
										client.getResourceManager(), car.getModelId(), car.getMaterials(), desiredLivery);
								String liveryCacheKey = car.getModelId() + "#" + desiredLivery;
								var carModel = car.getModel();
								car.setCompiledModel(compiledCarModelCache.computeIfAbsent(liveryCacheKey,
										key -> liveryOptions.isDecal(desiredLivery)
												? CompiledCarModel.compileBodyAndDecal(carModel, liveryMaterials, CompiledCarModel.FALLBACK_TEXTURE, false)
												: CompiledCarModel.compile(carModel, liveryMaterials, CompiledCarModel.FALLBACK_TEXTURE)));
								car.setLoadedLiveryId(desiredLivery);
							} catch (Exception liveryLoadError) {
								AiObjectMod.LOGGER.error("Failed to load livery: " + desiredLivery, liveryLoadError);
							}
						}
					}

					if (car.isTownCar() || car.isRailwayRouteVehicle()) {

						if (client.player != null && !ambientEngineSounds.containsKey(car)
								&& car.distanceToSqr(client.player) <= AMBIENT_ENGINE_RANGE * AMBIENT_ENGINE_RANGE) {
							ambientEngineCandidates.add(car);
						}
					} else {

						car.updateClientSlideIntensity(car == drivingCar);
						TireSlipSoundInstance slipSound = tireSlipSounds.get(car);
						if ((slipSound == null || slipSound.isStopped()) && car.getClientSlideIntensity() > 0.0f) {
							slipSound = new TireSlipSoundInstance(car);
							client.getSoundManager().play(slipSound);
							tireSlipSounds.put(car, slipSound);
						}
					}

					PoliceSirenSoundInstance sirenSound = policeSirenSounds.get(car);
					if (car.isPoliceSirenOn()) {

						if (sirenSound != null && !sirenSound.isStopped() && sirenSound.isSoundOutdated()) {
							sirenSound.requestRelease();
							sirenSound = null;
						}

						boolean sirenHasSound = com.examplebett.entity.HornSirenSounds.sirenById(car.getSirenSoundId()).sound() != null;
						if (sirenHasSound && (sirenSound == null || sirenSound.isReadyForSuccessor())) {
							PoliceSirenSoundInstance successor = new PoliceSirenSoundInstance(car);
							client.getSoundManager().play(successor);
							policeSirenSounds.put(car, successor);
						}
					} else if (sirenSound != null && !sirenSound.isStopped()) {
						sirenSound.requestRelease();
					}
				} else if (entity instanceof BreakableTireStackEntity tireStack) {
					if (tireStack.getCompiledModel() == null) {
						try {
							var model = ObjModelLoader.loadModel(client.getResourceManager(), tireStack.getModelId());
							var materials = ObjModelLoader.loadMaterials(client.getResourceManager(), tireStack.getModelId());
							tireStack.setModel(model);
							tireStack.setMaterials(materials);
							tireStack.setCompiledModel(CompiledCarModel.compile(model, materials, CompiledCarModel.FALLBACK_TEXTURE));
						} catch (Exception e) {
							AiObjectMod.LOGGER.error("Failed to load model: " + tireStack.getModelId(), e);
						}
					}
				}
			}

			tireSlipSounds.entrySet().removeIf(entry -> entry.getValue().isStopped());

			policeSirenSounds.entrySet().removeIf(entry -> entry.getValue().isStopped());

			ambientEngineSounds.entrySet().removeIf(entry -> {
				CarEntity car = entry.getKey();
				boolean outOfRange = client.player == null
						|| car.distanceToSqr(client.player) > AMBIENT_ENGINE_RANGE * AMBIENT_ENGINE_RANGE;
				if (entry.getValue().isStopped() || !car.isAlive() || outOfRange) {
					client.getSoundManager().stop(entry.getValue());
					return true;
				}
				return false;
			});

			for (Map.Entry<CarEntity, EngineSoundInstance> entry : ambientEngineSounds.entrySet()) {
				if (entry.getValue().isReadyForSuccessor()) {
					EngineSoundInstance successor = new EngineSoundInstance(entry.getKey(), entry.getValue().getVolume(), true);
					client.getSoundManager().play(successor);
					entry.setValue(successor);
				}
			}
			while (ambientEngineSounds.size() < MAX_AMBIENT_ENGINE_SOUNDS && !ambientEngineCandidates.isEmpty()) {
				CarEntity picked = ambientEngineCandidates.remove(client.level.random.nextInt(ambientEngineCandidates.size()));
				float volume = (AMBIENT_ENGINE_MIN_VOLUME
						+ client.level.random.nextFloat() * (AMBIENT_ENGINE_MAX_VOLUME - AMBIENT_ENGINE_MIN_VOLUME))
						* ambientEngineVolumeMultiplier;
				EngineSoundInstance ambientSound = new EngineSoundInstance(picked, volume, true);
				client.getSoundManager().play(ambientSound);
				ambientEngineSounds.put(picked, ambientSound);
			}
		});
	}

	private static Component getCarDisplayName(CarEntity car) {
		Item item = MODEL_ID_TO_SPAWN_ITEM.get(car.getModelId());
		Component name = item != null ? new ItemStack(item).getHoverName() : ModText.literal(car.getModelId());
		if (!mountToastCustomFontEnabled) {
			return name;
		}

		return name.copy().withStyle(style -> style.withFont(MOUNT_TOAST_FONT));
	}

	private static Component getCarCategoryText(CarEntity car) {
		java.util.Set<com.examplebett.entity.VehicleCategory> categories = car.getVehicleCategories();
		if (categories == null || categories.isEmpty()) {
			return null;
		}
		java.util.List<com.examplebett.entity.VehicleCategory> sorted = new java.util.ArrayList<>(categories);
		sorted.sort(java.util.Comparator.naturalOrder());
		net.minecraft.network.chat.MutableComponent joined = ModText.literal("");
		for (int i = 0; i < sorted.size(); i++) {
			if (i > 0) {
				joined.append(" / ");
			}
			joined.append(ModText.translatable(sorted.get(i).translationKey));
		}
		if (!mountToastCustomFontEnabled) {
			return joined;
		}
		return joined.withStyle(style -> style.withFont(MOUNT_TOAST_FONT));
	}

	private static boolean mineturismo$canToggleTrailerCoupling(CarEntity drivingCar) {
		if (!drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
			return false;
		}
		return drivingCar.findCoupledTrailer() != null || drivingCar.findCouplableTrailer() != null;
	}

	private static void mineturismo$renderWaypointHint(GuiGraphics guiGraphics) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}
		Item mainHand = client.player.getMainHandItem().getItem();
		Item offHand = client.player.getOffhandItem().getItem();
		Component[] hintLines;
		if (com.examplebett.client.GttClient.isActive()) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.gtt.hud_hint") };
		} else if (client.player.getVehicle() instanceof CarEntity mineturismo$hintCar && mineturismo$canToggleTrailerCoupling(mineturismo$hintCar)) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.trailer.hud_hint", TRAILER_COUPLING_KEY.getTranslatedKeyMessage()) };
		} else if (mainHand instanceof WaypointMarkerItem || offHand instanceof WaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.waypoint_marker.hud_hint") };
		} else if (mainHand instanceof OneWayWaypointMarkerItem || offHand instanceof OneWayWaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.one_way_marker.hud_hint") };
		} else if (mainHand instanceof RailwayWaypointMarkerItem || offHand instanceof RailwayWaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.railway_marker.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.TowRopeItem || offHand instanceof com.examplebett.item.TowRopeItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.tow_rope.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.RailwayStopMarkerItem || offHand instanceof com.examplebett.item.RailwayStopMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.railway_stop_marker.hud_hint") };
		} else if (mainHand instanceof TrafficSignalMarkerItem || offHand instanceof TrafficSignalMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.traffic_signal_marker.hud_hint_edit") };
		} else if (mainHand instanceof com.examplebett.item.SmartDispatchAppItem || offHand instanceof com.examplebett.item.SmartDispatchAppItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.smart_dispatch.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.CustomWrenchItem || offHand instanceof com.examplebett.item.CustomWrenchItem) {

			hintLines = new Component[] { ModText.translatable("gui.mineturismo.custom_wrench.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.WheelWrenchItem || offHand instanceof com.examplebett.item.WheelWrenchItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.wheel_wrench.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.DebugWrenchItem || offHand instanceof com.examplebett.item.DebugWrenchItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.debug_wrench.hud_hint") };
		} else {
			return;
		}
		float scale = 0.85f;
		int padding = 4;
		int lineHeight = 10;
		int maxWidth = 0;
		for (Component line : hintLines) {
			maxWidth = Math.max(maxWidth, client.font.width(line));
		}
		int boxWidth = maxWidth + padding * 2;
		int boxHeight = hintLines.length * lineHeight + padding * 2;
		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(scale, scale, 1.0f);
		int boxX = Math.round(4 / scale);
		int boxY = Math.round(4 / scale);
		guiGraphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xFF000000);
		for (int i = 0; i < hintLines.length; i++) {
			guiGraphics.drawString(client.font, hintLines[i], boxX + padding, boxY + padding + i * lineHeight, 0xFFFFFF00, false);
		}
		guiGraphics.pose().popPose();
	}

	private static void renderMountToast(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (mountToastText == null || mountToastStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - mountToastStartMs;
		if (elapsed >= MOUNT_TOAST_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < MOUNT_TOAST_FADE_IN_MS) {
			alpha = elapsed / (float) MOUNT_TOAST_FADE_IN_MS;
		} else if (elapsed > MOUNT_TOAST_TOTAL_MS - MOUNT_TOAST_FADE_OUT_MS) {
			alpha = (MOUNT_TOAST_TOTAL_MS - elapsed) / (float) MOUNT_TOAST_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));

		int color = ((int) (alpha * 255.0f) << 24) | 0xFFFFFF;

		int x = (int) ((context.guiWidth() - 6) / MOUNT_TOAST_SCALE - mc.font.width(mountToastText));
		int y = (int) ((context.guiHeight() - 34) / MOUNT_TOAST_SCALE);

		context.pose().pushPose();
		context.pose().scale(MOUNT_TOAST_SCALE, MOUNT_TOAST_SCALE, 1.0f);
		context.drawString(mc.font, mountToastText, x, y, color, true);

		if (mountToastCategoryText != null) {
			int categoryX = (int) ((context.guiWidth() - 6) / MOUNT_TOAST_SCALE - mc.font.width(mountToastCategoryText));
			int categoryY = y + mc.font.lineHeight + 2;
			context.drawString(mc.font, mountToastCategoryText, categoryX, categoryY, color, true);
		}
		context.pose().popPose();
	}

	private static void renderSpeedDisplay(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (speedDisplayMode == SpeedDisplayMode.NONE || mc.options.hideGui || mc.player == null) return;

		Entity vehicle = mc.player.getVehicle();
		if (!(vehicle instanceof CarEntity car)) return;

		if (vehicle != lastVehicle) {
			return;
		}

		if (mountToastStartMs >= 0 && System.currentTimeMillis() - mountToastStartMs < MOUNT_TOAST_TOTAL_MS) {
			return;
		}

		double blocksPerTick = car.getDeltaMovement().horizontalDistance();
		double kmh = blocksPerTick * 20.0 * 3.6;
		double displaySpeed = speedDisplayMode == SpeedDisplayMode.MPH ? kmh * 0.621371 : kmh;
		String numberText = String.valueOf(Math.round(displaySpeed));
		String unitText = speedDisplayMode == SpeedDisplayMode.MPH ? "mph" : "km/h";

		int color = 0xFFFFFFFF;
		int rightEdge = context.guiWidth() - 6;
		int nameRowY = context.guiHeight() - 34;

		int numberWidth = Math.round(mc.font.width(numberText) * SPEED_NUMBER_SCALE);
		int unitWidth = Math.round(mc.font.width(unitText) * SPEED_UNIT_SCALE);
		int unitScreenX = rightEdge - unitWidth;
		int numberScreenX = unitScreenX - SPEED_UNIT_GAP - numberWidth;

		int numberHeight = Math.round(mc.font.lineHeight * SPEED_NUMBER_SCALE);
		int unitHeight = Math.round(mc.font.lineHeight * SPEED_UNIT_SCALE);
		int numberScreenY = nameRowY;

		int unitScreenY = nameRowY + (numberHeight - unitHeight);

		context.pose().pushPose();
		context.pose().scale(SPEED_NUMBER_SCALE, SPEED_NUMBER_SCALE, 1.0f);
		context.drawString(mc.font, numberText, Math.round(numberScreenX / SPEED_NUMBER_SCALE), Math.round(numberScreenY / SPEED_NUMBER_SCALE), color, true);
		context.pose().popPose();

		context.pose().pushPose();
		context.pose().scale(SPEED_UNIT_SCALE, SPEED_UNIT_SCALE, 1.0f);
		context.drawString(mc.font, unitText, Math.round(unitScreenX / SPEED_UNIT_SCALE), Math.round(unitScreenY / SPEED_UNIT_SCALE), color, true);
		context.pose().popPose();
	}

	private static void renderPassengerTransportWarning(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (hudWarningText == null || hudWarningStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - hudWarningStartMs;
		if (elapsed >= HUD_WARNING_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < HUD_WARNING_FADE_IN_MS) {
			alpha = elapsed / (float) HUD_WARNING_FADE_IN_MS;
		} else if (elapsed > HUD_WARNING_TOTAL_MS - HUD_WARNING_FADE_OUT_MS) {
			alpha = (HUD_WARNING_TOTAL_MS - elapsed) / (float) HUD_WARNING_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));

		int color = ((int) (alpha * 255.0f) << 24) | 0xFFFFFF;

		context.drawString(mc.font, hudWarningText, 4, 28, color, true);
	}

	private static void renderHintTip(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (!hintsEnabled || hintTipLines == null || hintTipStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - hintTipStartMs;
		if (elapsed >= HINT_TIP_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < HINT_TIP_FADE_IN_MS) {
			alpha = elapsed / (float) HINT_TIP_FADE_IN_MS;
		} else if (elapsed > HINT_TIP_TOTAL_MS - HINT_TIP_FADE_OUT_MS) {
			alpha = (HINT_TIP_TOTAL_MS - elapsed) / (float) HINT_TIP_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));
		int alpha8 = (int) (alpha * 255.0f);

		int textColor = (alpha8 << 24) | 0xFFFFFF;
		int backgroundColor = alpha8 << 24;

		List<String> rows = new ArrayList<>();
		for (Component line : hintTipLines) {
			for (String row : line.getString().split("\n", -1)) {
				rows.add(row);
			}
		}
		int maxWidth = 0;
		for (String row : rows) {
			maxWidth = Math.max(maxWidth, mc.font.width(row));
		}
		int lineHeight = mc.font.lineHeight;
		int padding = 4;
		int boxWidth = maxWidth + padding * 2;
		int boxHeight = rows.size() * lineHeight + padding * 2;

		int screenX = 4;
		int screenY = 4;
		context.pose().pushPose();
		context.pose().scale(HINT_TIP_SCALE, HINT_TIP_SCALE, 1.0f);
		int x = Math.round(screenX / HINT_TIP_SCALE);
		int y = Math.round(screenY / HINT_TIP_SCALE);
		context.fill(x, y, x + boxWidth, y + boxHeight, backgroundColor);
		for (int i = 0; i < rows.size(); i++) {
			context.drawString(mc.font, rows.get(i), x + padding, y + padding + i * lineHeight, textColor, true);
		}
		context.pose().popPose();

		Component disableHint = ModText.translatable("gui.mineturismo.hint_tip.disable_instruction",
				ModText.translatable("item.mineturismo.debug_wrench"),
				ModText.translatable("gui.mineturismo.debug_wrench_menu.other"),
				ModText.translatable("gui.mineturismo.debug_wrench_menu.hide_hints"));
		int subLineHeight = mc.font.lineHeight;
		int subBoxWidth = mc.font.width(disableHint) + padding * 2;
		int subBoxHeight = subLineHeight + padding * 2;
		int subScreenY = screenY + Math.round(boxHeight * HINT_TIP_SCALE);
		context.pose().pushPose();
		context.pose().scale(HINT_TIP_SUB_SCALE, HINT_TIP_SUB_SCALE, 1.0f);
		int subX = Math.round(screenX / HINT_TIP_SUB_SCALE);
		int subY = Math.round(subScreenY / HINT_TIP_SUB_SCALE);
		context.fill(subX, subY, subX + subBoxWidth, subY + subBoxHeight, backgroundColor);
		context.drawString(mc.font, disableHint, subX + padding, subY + padding, textColor, true);
		context.pose().popPose();
	}
}
*/
//?}
//? if <1.17.1 && >=1.17 {
/*
package com.examplebett.client;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.block.AiObjectModBlocks;
import com.examplebett.client.gui.CarWrenchScreen;
import com.examplebett.client.gui.RailwayConsistScreen;
import com.examplebett.client.gui.WheelAdjustScreen;
import com.examplebett.client.gui.OneWayMarkerSettingsScreen;
import com.examplebett.client.gui.TrafficSignalSettingsScreen;
import com.examplebett.client.gui.WaypointTrafficSettingsScreen;
import com.examplebett.entity.AiObjectModEntities;
import com.examplebett.entity.BreakableTireStackEntity;
import com.examplebett.entity.BreakableBoxEntity;
import com.examplebett.entity.BreakableConeEntity;
import com.examplebett.entity.CarEntity;
import com.examplebett.client.render.BrakeCaliperModel;
import com.examplebett.client.render.BreakableTireStackRenderer;
import com.examplebett.client.render.CameraAttachmentLoader;
import com.examplebett.client.render.CarEntityRenderer;
import com.examplebett.client.render.GlbModelLoader;
import com.examplebett.client.render.LightAttachmentLoader;
import com.examplebett.client.render.LiveryLoader;
import com.examplebett.client.render.LodWheelModel;
import com.examplebett.client.render.PlateAttachmentLoader;
import com.examplebett.client.render.PoliceLightModel;
import com.examplebett.client.render.RearWingModel;
import com.examplebett.client.render.OtherPartModel;
import com.examplebett.client.render.TaxiSignModel;
import com.examplebett.client.render.WheelAttachmentLoader;
import com.examplebett.client.render.WheelColorLoader;
import com.examplebett.client.render.WheelStyleCatalog;
import com.examplebett.client.sound.EngineSoundInstance;
import com.examplebett.client.sound.HornSoundInstance;
import com.examplebett.client.sound.PoliceSirenSoundInstance;
import com.examplebett.client.sound.TireSlipSoundInstance;
import com.examplebett.item.CarSpawnItem;
import com.examplebett.item.OneWayWaypointMarkerItem;
import com.examplebett.item.RailwayWaypointMarkerItem;
import com.examplebett.item.TrafficSignalMarkerItem;
import com.examplebett.item.WaypointMarkerItem;
import com.examplebett.network.OpenRailwaySpawnSetupPayload;
import com.examplebett.network.OpenRailwayConsistSettingsPayload;
import com.examplebett.network.OpenSignalSettingsPayload;
import com.examplebett.network.PlayCollisionSoundPayload;
import com.examplebett.network.SetDetectedWheelFootprintPayload;
import com.examplebett.network.SetClientSlipReportPayload;
import com.examplebett.network.SetClientWheelieTriggerPayload;
import com.examplebett.network.SetWheelRotationReportPayload;
import com.examplebett.network.ShowHudWarningPayload;
import com.examplebett.network.SyncAmbientEngineVolumePayload;
import com.examplebett.network.SyncRailwayFormationOriginPayload;
import com.examplebett.network.SyncWheelDistancePayload;
import com.examplebett.waypoint.WaypointNetworkSavedData;
import com.examplebett.network.SetHornStatePayload;
import com.examplebett.network.ToggleTrailerCouplingPayload;
import com.examplebett.network.SetPoliceLightsOnPayload;
import com.examplebett.render.CompiledCarModel;
import com.examplebett.render.CustomPartCatalog;
import com.examplebett.render.FrontBrakeCaliperParts;
import com.examplebett.render.ObjModel;
import com.examplebett.render.ObjModelLoader;
import com.examplebett.render.OtherParts;
import com.examplebett.render.PoliceLightParts;
import com.examplebett.render.RearBrakeCaliperParts;
import com.examplebett.render.RearWingParts;
import com.examplebett.render.TaxiSignParts;
import com.examplebett.sound.AiObjectModSounds;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import com.examplebett.compat.ModRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiObjectModClient implements ClientModInitializer {

	public interface ExternalCarModelLoader {
		void tryLoad(CarEntity car);
	}

	public static ExternalCarModelLoader externalCarModelLoader = null;

	private static final long MOUNT_TOAST_TOTAL_MS = 3000L;
	private static final long MOUNT_TOAST_FADE_IN_MS = 300L;
	private static final long MOUNT_TOAST_FADE_OUT_MS = 700L;
	private static final ResourceLocation MOUNT_TOAST_FONT = AiObjectMod.id("mount_toast");
	private static final float MOUNT_TOAST_SCALE = 1.5f;

	private static boolean mountToastCustomFontEnabled = true;

	public static boolean isMountToastCustomFontEnabled() {
		return mountToastCustomFontEnabled;
	}

	public static void setMountToastCustomFontEnabled(boolean enabled) {
		mountToastCustomFontEnabled = enabled;
	}

	private static boolean fakeReflectionEnabled = false;

	public static boolean isFakeReflectionEnabled() {
		return fakeReflectionEnabled;
	}

	public static void setFakeReflectionEnabled(boolean enabled) {
		fakeReflectionEnabled = enabled;
	}

	private static boolean hintsEnabled = true;

	public static boolean isHintsEnabled() {
		return hintsEnabled;
	}

	public static void setHintsEnabled(boolean enabled) {
		hintsEnabled = enabled;
	}

	private static boolean fpsLogEnabled = false;

	public static boolean isFpsLogEnabled() {
		return fpsLogEnabled;
	}

	public static void setFpsLogEnabled(boolean enabled) {
		fpsLogEnabled = enabled;
	}

	private static final long FPS_LOG_INTERVAL_TICKS = 20L;
	private static long fpsLogTicks = 0L;

	private static final java.lang.reflect.Field LEGACY_FPS_FIELD = resolveLegacyFpsField();

	private static java.lang.reflect.Field resolveLegacyFpsField() {
		try {
			java.lang.reflect.Field field = com.examplebett.compat.ModReflection.findField(net.minecraft.client.Minecraft.class, "fps", "field_1738");
			field.setAccessible(true);
			return field;
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}

	private static int getGameFps(net.minecraft.client.Minecraft client) {
		if (LEGACY_FPS_FIELD == null) {
			return -1;
		}
		try {
			return LEGACY_FPS_FIELD.getInt(null);
		} catch (ReflectiveOperationException e) {
			return -1;
		}
	}

	public enum SpeedDisplayMode {
		NONE, KMH, MPH;

		public SpeedDisplayMode next() {
			return switch (this) {
				case NONE -> KMH;
				case KMH -> MPH;
				case MPH -> NONE;
			};
		}
	}

	private static SpeedDisplayMode speedDisplayMode = SpeedDisplayMode.NONE;
	private static final float SPEED_NUMBER_SCALE = 2.0f;
	private static final float SPEED_UNIT_SCALE = 1.0f;
	private static final int SPEED_UNIT_GAP = 3;

	public static SpeedDisplayMode getSpeedDisplayMode() {
		return speedDisplayMode;
	}

	public static void setSpeedDisplayMode(SpeedDisplayMode mode) {
		speedDisplayMode = mode;
	}
	private static final Map<String, Item> MODEL_ID_TO_SPAWN_ITEM = new HashMap<>();

	private static final Map<String, CompiledCarModel> compiledCarModelCache = new HashMap<>();

	private static final Map<String, WheelCompiledPair> compiledWheelModelCache = new HashMap<>();
	private record WheelCompiledPair(CompiledCarModel normal, CompiledCarModel mirrored) {
	}
	private static Entity lastVehicle = null;
	private static Component mountToastText = null;

	private static Component mountToastCategoryText = null;
	private static long mountToastStartMs = -1L;

	private static final long HUD_WARNING_TOTAL_MS = 3000L;
	private static final long HUD_WARNING_FADE_IN_MS = 200L;
	private static final long HUD_WARNING_FADE_OUT_MS = 700L;
	private static Component hudWarningText = null;
	private static long hudWarningStartMs = -1L;

	private record TimedHint(long delayTicks, String... lineKeys) {
	}

	private static final TimedHint[] TIMED_HINTS = {
			new TimedHint(1200L,
					"gui.mineturismo.reflection_tip.line1",
					"gui.mineturismo.reflection_tip.line2",
					"gui.mineturismo.reflection_tip.line3"),
			new TimedHint(1360L,
					"gui.mineturismo.shader_tip.line1",
					"gui.mineturismo.shader_tip.line2"),
			new TimedHint(3600L,
					"gui.mineturismo.smart_dispatch_tip.line1",
					"gui.mineturismo.smart_dispatch_tip.line2"),
			new TimedHint(6000L,
					"gui.mineturismo.wrench_tip.line1",
					"gui.mineturismo.wrench_tip.line2"),
			new TimedHint(12000L,
					"gui.mineturismo.traffic_marker_tip.line1",
					"gui.mineturismo.traffic_marker_tip.line2"),
	};

	private static final long HINT_TIP_TOTAL_MS = 8000L;
	private static final long HINT_TIP_FADE_IN_MS = 300L;
	private static final long HINT_TIP_FADE_OUT_MS = 700L;
	private static final float HINT_TIP_SCALE = 1.1f;

	private static final float HINT_TIP_SUB_SCALE = 0.75f;
	private static int nextHintIndex = 0;
	private static long hintTicks = 0L;
	private static Component[] hintTipLines = null;
	private static long hintTipStartMs = -1L;
	private static boolean creditsScreenChecked = false;

	private static final Map<CarEntity, EngineSoundInstance> engineSounds = new HashMap<>();

	private static final Map<CarEntity, EngineSoundInstance> engineLowSounds = new HashMap<>();

	private static final int WHEEL_ROTATION_REPORT_INTERVAL_TICKS = 2;
	private static int wheelRotationReportTickCounter = 0;

	private static final Map<CarEntity, TireSlipSoundInstance> tireSlipSounds = new HashMap<>();

	private static final Map<CarEntity, PoliceSirenSoundInstance> policeSirenSounds = new HashMap<>();

	private static final Map<CarEntity, EngineSoundInstance> ambientEngineSounds = new HashMap<>();
	private static final double AMBIENT_ENGINE_RANGE = 24.0;
	private static final int MAX_AMBIENT_ENGINE_SOUNDS = 3;
	private static final float AMBIENT_ENGINE_MIN_VOLUME = 0.2f;
	private static final float AMBIENT_ENGINE_MAX_VOLUME = 0.4f;

	private static float ambientEngineVolumeMultiplier = WaypointNetworkSavedData.DEFAULT_AMBIENT_ENGINE_VOLUME_MULTIPLIER;

	private static final String KEY_CATEGORY = "key.categories.mineturismo";
	private static final KeyMapping HORN_KEY = new KeyMapping(
			"key.mineturismo.horn", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, KEY_CATEGORY);

	private static final KeyMapping TRAILER_COUPLING_KEY = new KeyMapping(
			"key.mineturismo.trailer_coupling", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_L, KEY_CATEGORY);

	public static KeyMapping hornKey() {
		return HORN_KEY;
	}

	public static KeyMapping trailerCouplingKey() {
		return TRAILER_COUPLING_KEY;
	}

	private static HornSoundInstance activeHorn = null;

	private static CarEntity hornBroadcastCar = null;

	private static boolean hornKeyWasDown = false;

	private static boolean trailerCouplingKeyWasDown = false;

	private static boolean sprintKeyHeldSent = false;
	private static CarEntity sprintKeyHeldCar = null;

	private static void mineturismo$handlePlayCollisionSound(PlayCollisionSoundPayload payload) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			return;
		}

		net.minecraft.sounds.SoundEvent collisionSound = payload.lowSpeed() ? AiObjectModSounds.CAR_COLLISION_LOW : AiObjectModSounds.CAR_COLLISION;

		SimpleSoundInstance sound = new SimpleSoundInstance(
				collisionSound.getLocation(), SoundSource.NEUTRAL,
				payload.volume(), payload.pitch(),
				false, 0, SoundInstance.Attenuation.NONE,
				payload.x(), payload.y(), payload.z(), false);
		mc.getSoundManager().play(sound);
	}

	private static void mineturismo$handleShowHudWarning(ShowHudWarningPayload payload) {
		hudWarningText = ModText.translatable(payload.messageKey());
		hudWarningStartMs = System.currentTimeMillis();
		Minecraft.getInstance().getSoundManager().play(
				SimpleSoundInstance.forUI(AiObjectModSounds.PASSENGER_TRANSPORT_DENIED, 1.0f));
	}

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.CAR_ENTITY, CarEntityRenderer::new);
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.BREAKABLE_TIRE_STACK, BreakableTireStackRenderer<BreakableTireStackEntity>::new);
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.BREAKABLE_BOX, BreakableTireStackRenderer<BreakableBoxEntity>::new);
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.BREAKABLE_BOX_2, BreakableTireStackRenderer<BreakableBoxEntity>::new);
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.TRAFFIC_CONE, BreakableTireStackRenderer<BreakableConeEntity>::new);
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.BARRICADE, BreakableTireStackRenderer<com.examplebett.entity.BreakableBarricadeEntity>::new);

		KeyBindingHelper.registerKeyBinding(HORN_KEY);
		KeyBindingHelper.registerKeyBinding(TRAILER_COUPLING_KEY);

		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
				new SimpleSynchronousResourceReloadListener() {
					@Override
					public ResourceLocation getFabricId() {
						return AiObjectMod.id("clear_render_caches");
					}

					@Override
					public void onResourceManagerReload(ResourceManager resourceManager) {
						ObjModelLoader.clearCache();
						GlbModelLoader.clearCache();
						WheelAttachmentLoader.clearCache();
						WheelColorLoader.clearCache();
						LightAttachmentLoader.clearCache();
						com.examplebett.client.render.VehicleCategoryLoader.clearCache();
						PlateAttachmentLoader.clearCache();
						CameraAttachmentLoader.clearCache();
						LodWheelModel.clearCache();
						LiveryLoader.clearCache();
						RearWingModel.clearCache();
						PoliceLightModel.clearCache();
						TaxiSignModel.clearCache();
						OtherPartModel.clearCache();
						BrakeCaliperModel.clearCache();
						CustomPartCatalog.clearCache();
						WheelStyleCatalog.clearCache();
						RearWingParts.clearCache();
						OtherParts.clearCache();
						PoliceLightParts.clearCache();
						TaxiSignParts.clearCache();
						FrontBrakeCaliperParts.clearCache();
						RearBrakeCaliperParts.clearCache();
						compiledCarModelCache.clear();
						compiledWheelModelCache.clear();
					}
				});

		ModClientNetworking.registerS2C(PlayCollisionSoundPayload.ID, PlayCollisionSoundPayload::read, AiObjectModClient::mineturismo$handlePlayCollisionSound);

		ModClientNetworking.registerS2C(SyncWheelDistancePayload.ID, SyncWheelDistancePayload::read, payload -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc.level == null) {
				return;
			}
			if (mc.level.getEntity(payload.entityId()) instanceof CarEntity car) {
				car.setNetworkWheelDistance(payload.distance());
			}
		});

		ModClientNetworking.registerS2C(OpenRailwayConsistSettingsPayload.ID, OpenRailwayConsistSettingsPayload::read, payload -> {
			net.minecraft.client.multiplayer.ClientLevel mineturismo$level = Minecraft.getInstance().level;
			if (mineturismo$level != null && mineturismo$level.getEntity(payload.headEntityId()) instanceof CarEntity car) {
				Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
				Minecraft.getInstance().setScreen(new RailwayConsistScreen(car, null));
			}
		});

		ModClientNetworking.registerS2C(OpenSignalSettingsPayload.ID, OpenSignalSettingsPayload::read, payload -> {
			char direction = payload.direction().isEmpty() ? 'N' : payload.direction().charAt(0);
			TrafficSignalSettingsScreen.applyExternalConfig(payload.signalId(), payload.intersectionName(), direction,
					payload.greenTicks(), payload.redTicks(), payload.phaseOffsetTicks());
			Minecraft.getInstance().setScreen(new TrafficSignalSettingsScreen());
		});

		ModClientNetworking.registerS2C(OpenRailwaySpawnSetupPayload.ID, OpenRailwaySpawnSetupPayload::read, payload -> {
			Minecraft.getInstance().setScreen(new com.examplebett.client.gui.RailwaySpawnVehicleScreen(payload.originNodeId()));
		});

		ModClientNetworking.registerS2C(SyncRailwayFormationOriginPayload.ID, SyncRailwayFormationOriginPayload::read, payload -> {
			com.examplebett.inventory.RailwayFormationMenu.pendingOriginNodeId = payload.originNodeId();
		});

		ModClientNetworking.registerS2C(SyncAmbientEngineVolumePayload.ID, SyncAmbientEngineVolumePayload::read, payload -> {
			ambientEngineVolumeMultiplier = payload.multiplier();
			com.examplebett.client.gui.WaypointClientSettings.apply(payload.multiplier(), payload.settings());
		});

		ModClientNetworking.registerS2C(ShowHudWarningPayload.ID, ShowHudWarningPayload::read, AiObjectModClient::mineturismo$handleShowHudWarning);

		BlockRenderLayerMap.INSTANCE.putBlock(AiObjectModBlocks.CAR_BREAKABLE_GLASS, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(AiObjectModBlocks.WATER_PUDDLE, RenderType.translucent());

		CarEntity.wrenchMenuOpener = (car, player) -> {
			Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
			Minecraft.getInstance().setScreen(new CarWrenchScreen(car));
		};
		CarEntity.wheelWrenchMenuOpener = (car, player) -> {
			Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
			Minecraft.getInstance().setScreen(new WheelAdjustScreen(car));
		};
		CarEntity.debugWrenchMenuOpener = (car, player) -> Minecraft.getInstance().setScreen(new com.examplebett.client.gui.DebugWrenchScreen(car));
		WaypointMarkerItem.trafficSettingsMenuOpener = player -> Minecraft.getInstance().setScreen(new WaypointTrafficSettingsScreen());
		OneWayWaypointMarkerItem.settingsMenuOpener = player -> Minecraft.getInstance().setScreen(new OneWayMarkerSettingsScreen());
		com.examplebett.item.SmartDispatchAppItem.appScreenOpener = player -> Minecraft.getInstance().setScreen(new com.examplebett.client.gui.SmartDispatchAppScreen());
		ClientTickEvents.END_CLIENT_TICK.register(com.examplebett.client.GttClient::tick);
		ClientTickEvents.END_CLIENT_TICK.register(com.examplebett.client.sound.ChorusMusicPlayer::tick);

		ModClientNetworking.registerS2C(com.examplebett.network.GttScorePayload.ID, com.examplebett.network.GttScorePayload::read, payload -> com.examplebett.client.GttClient.setScore(payload.score()));

		ModClientNetworking.registerS2C(com.examplebett.network.SlotSpinResultPayload.ID, com.examplebett.network.SlotSpinResultPayload::read, payload -> com.examplebett.client.gui.SmartDispatchAppScreen.onSlotResult(payload.prizes()));

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> com.examplebett.client.GttClient.renderHud(new GuiGraphics(poseStack)));

		net.minecraft.client.gui.screens.MenuScreens.register(com.examplebett.inventory.AiObjectModMenus.RAILWAY_FORMATION_MENU_TYPE, com.examplebett.client.gui.RailwayFormationScreen::new);
		net.minecraft.client.gui.screens.MenuScreens.register(com.examplebett.inventory.AiObjectModMenus.MASSIVE_AUTO_APPRAISAL_MENU_TYPE, com.examplebett.client.gui.MassiveAutoAppraisalScreen::new);

		HudRenderCallback.EVENT.register((poseStack, deltaTracker) -> mineturismo$renderWaypointHint(new GuiGraphics(poseStack)));

		CarEntity.clientDescendKeyHeld = () -> Minecraft.getInstance().options.keySprint.isDown();

		CarEntity.clientForwardKeyHeld = () -> Minecraft.getInstance().options.keyUp.isDown();
		CarEntity.clientBackKeyHeld = () -> Minecraft.getInstance().options.keyDown.isDown();

		CarEntity.clientDimensionsResourceManager = () -> {
			var integratedServer = Minecraft.getInstance().getSingleplayerServer();
			return integratedServer != null ? integratedServer.getResourceManager() : null;
		};

		for (Item item : ModRegistries.ITEM) {
			if (item instanceof CarSpawnItem spawnItem) {
				MODEL_ID_TO_SPAWN_ITEM.putIfAbsent(spawnItem.getModelId(), item);
			}
		}

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> renderMountToast(new GuiGraphics(poseStack)));

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> renderSpeedDisplay(new GuiGraphics(poseStack)));

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> renderPassengerTransportWarning(new GuiGraphics(poseStack)));

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> renderHintTip(new GuiGraphics(poseStack)));

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			nextHintIndex = 0;
			hintTicks = 0L;
			hintTipStartMs = -1L;
		});

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			boolean mineturismo$pressed = false;
			while (TRAILER_COUPLING_KEY.consumeClick()) {
				mineturismo$pressed = true;
			}

			InputConstants.Key mineturismo$boundKey = KeyBindingHelper.getBoundKeyOf(TRAILER_COUPLING_KEY);
			boolean mineturismo$rawDown = mineturismo$boundKey.getType() == InputConstants.Type.KEYSYM
					&& InputConstants.isKeyDown(client.getWindow().getWindow(), mineturismo$boundKey.getValue());
			if (mineturismo$rawDown && !trailerCouplingKeyWasDown && client.screen == null) {
				mineturismo$pressed = true;
			}
			trailerCouplingKeyWasDown = mineturismo$rawDown;

			if (client.player != null && client.screen == null
					&& client.player.getVehicle() instanceof CarEntity mineturismo$headCar
					&& mineturismo$headCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
				for (KeyMapping mineturismo$other : client.options.keyMappings) {
					if (mineturismo$other != TRAILER_COUPLING_KEY && mineturismo$other.same(TRAILER_COUPLING_KEY)) {
						while (mineturismo$other.consumeClick()) {
							mineturismo$pressed = true;
						}
					}
				}
			}
			if (!mineturismo$pressed || client.player == null || client.screen != null
					|| !(client.player.getVehicle() instanceof CarEntity mineturismo$car)
					|| !mineturismo$car.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
				return;
			}
			for (KeyMapping mineturismo$other : client.options.keyMappings) {
				if (mineturismo$other != TRAILER_COUPLING_KEY && mineturismo$other.same(TRAILER_COUPLING_KEY)) {
					while (mineturismo$other.consumeClick()) {

					}
				}
			}
			ToggleTrailerCouplingPayload mineturismo$payload = new ToggleTrailerCouplingPayload(mineturismo$car.getId());
			ModClientNetworking.sendToServer(ToggleTrailerCouplingPayload.ID, mineturismo$payload, ToggleTrailerCouplingPayload::write);
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (creditsScreenChecked) return;
			if (client.player == null || client.level == null) return;
			creditsScreenChecked = true;
			com.examplebett.client.gui.CreditsScreen.showOnceIfNeeded();
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!fpsLogEnabled) return;
			fpsLogTicks++;
			if (fpsLogTicks % FPS_LOG_INTERVAL_TICKS != 0) return;
			int fps = getGameFps(client);
			int carCount = 0;
			if (client.level != null) {
				for (Entity entity : client.level.entitiesForRendering()) {
					if (entity instanceof CarEntity) {
						carCount++;
					}
				}
			}
			AiObjectMod.LOGGER.info("[fps-log] fps={} frameTimeMs={} carCount={}", fps,
					fps > 0 ? String.format("%.1f", 1000.0 / fps) : "n/a", carCount);
		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (hintsEnabled && client.player != null) {
				hintTicks++;
				while (nextHintIndex < TIMED_HINTS.length && hintTicks >= TIMED_HINTS[nextHintIndex].delayTicks()) {
					TimedHint hint = TIMED_HINTS[nextHintIndex];
					hintTipLines = new Component[hint.lineKeys().length];
					for (int i = 0; i < hint.lineKeys().length; i++) {
						hintTipLines[i] = ModText.translatable(hint.lineKeys()[i]);
					}
					hintTipStartMs = System.currentTimeMillis();
					client.getSoundManager().play(
							SimpleSoundInstance.forUI(AiObjectModSounds.PASSENGER_TRANSPORT_DENIED, 1.0f));
					nextHintIndex++;
				}
			}

			CarEntity drivingCar = null;
			if (client.player != null) {
				Entity vehicle = client.player.getVehicle();
				if (vehicle instanceof CarEntity car) {
					drivingCar = car;
				}
				if (vehicle instanceof CarEntity car && vehicle != lastVehicle) {
					mountToastText = getCarDisplayName(car);
					if (car.getVehicleCategories() == null) {
						com.examplebett.client.render.VehicleCategoryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setVehicleCategories);
					}
					mountToastCategoryText = getCarCategoryText(car);
					mountToastStartMs = System.currentTimeMillis();
				}
				lastVehicle = vehicle;
			}

			if (drivingCar != null) {
				EngineSoundInstance existing = engineSounds.get(drivingCar);

				if (existing != null && !existing.matchesCurrentSoundId()) {
					client.getSoundManager().stop(existing);
					existing = null;
				}

				if (existing == null || existing.isReadyForSuccessor()) {
					EngineSoundInstance sound = new EngineSoundInstance(drivingCar, 1.0f, false,
							drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)
									? EngineSoundInstance.Layer.HYBRID_HIGH : EngineSoundInstance.Layer.NORMAL);
					client.getSoundManager().play(sound);
					engineSounds.put(drivingCar, sound);
				}

				if (drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)) {
					EngineSoundInstance lowExisting = engineLowSounds.get(drivingCar);
					if (lowExisting == null || lowExisting.isReadyForSuccessor()) {
						EngineSoundInstance lowSound = new EngineSoundInstance(drivingCar, 1.0f, false, EngineSoundInstance.Layer.HYBRID_LOW);
						client.getSoundManager().play(lowSound);
						engineLowSounds.put(drivingCar, lowSound);
					}
				}
			}

			CarEntity currentDrivingCar = drivingCar;
			engineSounds.entrySet().removeIf(e -> {
				if (e.getKey() != currentDrivingCar) {
					client.getSoundManager().stop(e.getValue());
					return true;
				}
				return e.getValue().isStopped();
			});

			engineLowSounds.entrySet().removeIf(e -> {
				if (e.getKey() != currentDrivingCar || !e.getKey().hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)) {
					client.getSoundManager().stop(e.getValue());
					return true;
				}
				return e.getValue().isStopped();
			});

			wheelRotationReportTickCounter++;
			if (drivingCar != null && wheelRotationReportTickCounter % WHEEL_ROTATION_REPORT_INTERVAL_TICKS == 0) {
				SetWheelRotationReportPayload mineturismo$payload = new SetWheelRotationReportPayload(
						drivingCar.getId(), drivingCar.getWheelDistanceTraveled());
				ModClientNetworking.sendToServer(SetWheelRotationReportPayload.ID, mineturismo$payload, SetWheelRotationReportPayload::write);
			}

			if (drivingCar != null && HORN_KEY.isDown()
					&& com.examplebett.entity.HornSirenSounds.hornById(drivingCar.getHornSoundId()).sound() != null) {
				if (activeHorn == null || activeHorn.isStopped() || !activeHorn.isFor(drivingCar)) {
					if (activeHorn != null) {
						client.getSoundManager().stop(activeHorn);
					}
					activeHorn = new HornSoundInstance(drivingCar);
					client.getSoundManager().play(activeHorn);
				} else {
					activeHorn.requestPlaying();
				}
			} else if (activeHorn != null) {
				activeHorn.requestRelease();
				if (activeHorn.isStopped()) {
					activeHorn = null;
				}
			}

			boolean hornDown = drivingCar != null && HORN_KEY.isDown();
			if (hornDown) {
				if (hornBroadcastCar != drivingCar) {
					if (hornBroadcastCar != null) {
						SetHornStatePayload mineturismo$payload = new SetHornStatePayload(hornBroadcastCar.getId(), false);
						ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
					}
					SetHornStatePayload mineturismo$payload = new SetHornStatePayload(drivingCar.getId(), true);
					ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
					hornBroadcastCar = drivingCar;
				}
			} else if (hornBroadcastCar != null) {
				SetHornStatePayload mineturismo$payload = new SetHornStatePayload(hornBroadcastCar.getId(), false);
				ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
				hornBroadcastCar = null;
			}

			boolean hornKeyDown = HORN_KEY.isDown();
			if (drivingCar != null && hornKeyDown && !hornKeyWasDown && !drivingCar.getPoliceLightEnabledIds().isEmpty()) {

				boolean newLightsOn;
				boolean newSirenOn;
				if (!drivingCar.isPoliceLightsOn()) {

					newLightsOn = true;
					newSirenOn = true;
				} else if (drivingCar.isPoliceSirenOn()) {

					newLightsOn = true;
					newSirenOn = false;
				} else {

					newLightsOn = false;
					newSirenOn = false;
				}
				drivingCar.setPoliceLightsOn(newLightsOn);
				drivingCar.setPoliceSirenOn(newSirenOn);
				SetPoliceLightsOnPayload mineturismo$payload = new SetPoliceLightsOnPayload(drivingCar.getId(), newLightsOn, newSirenOn);
				ModClientNetworking.sendToServer(SetPoliceLightsOnPayload.ID, mineturismo$payload, SetPoliceLightsOnPayload::write);
			}
			hornKeyWasDown = hornKeyDown;

			if (drivingCar != null && drivingCar.consumeClientSlipFlag()) {
				SetClientSlipReportPayload mineturismo$payload = new SetClientSlipReportPayload(drivingCar.getId());
				ModClientNetworking.sendToServer(SetClientSlipReportPayload.ID, mineturismo$payload, SetClientSlipReportPayload::write);
			}

			if (drivingCar != null && drivingCar.consumeClientWheelieTriggerFlag()) {
				SetClientWheelieTriggerPayload mineturismo$wheeliePayload = new SetClientWheelieTriggerPayload(drivingCar.getId());
				ModClientNetworking.sendToServer(SetClientWheelieTriggerPayload.ID, mineturismo$wheeliePayload, SetClientWheelieTriggerPayload::write);
			}

			boolean mineturismo$sprintDown = drivingCar != null && client.options.keySprint.isDown();
			if (drivingCar != null && (mineturismo$sprintDown != sprintKeyHeldSent || drivingCar != sprintKeyHeldCar)) {
				ModClientNetworking.sendToServer(com.examplebett.network.SetSprintKeyHeldPayload.ID, new com.examplebett.network.SetSprintKeyHeldPayload(drivingCar.getId(), mineturismo$sprintDown), com.examplebett.network.SetSprintKeyHeldPayload::write);
				sprintKeyHeldSent = mineturismo$sprintDown;
				sprintKeyHeldCar = drivingCar;
			} else if (drivingCar == null) {
				sprintKeyHeldSent = false;
				sprintKeyHeldCar = null;
			}

			if (client.level == null) return;

			List<CarEntity> ambientEngineCandidates = new ArrayList<>();
			for (Entity entity : client.level.entitiesForRendering()) {
				if (entity instanceof CarEntity car) {
					if (car.getCompiledModel() == null) {
						if (car.getModelId().startsWith(CarEntity.EXTERNAL_MODEL_PREFIX)) {
							if (externalCarModelLoader != null) {
								try {
									externalCarModelLoader.tryLoad(car);
								} catch (Exception e) {
									AiObjectMod.LOGGER.error("Failed to load external model: " + car.getModelId(), e);
								}
							}
						} else {
							try {
								ResourceLocation glbLocation = AiObjectMod.id("models/" + car.getModelId() + ".glb");

								if (client.getResourceManager().hasResource(glbLocation)) {
									var result = GlbModelLoader.load(client.getResourceManager(), car.getModelId());
									car.setModel(result.model());
									car.setMaterials(result.materials());
									car.setCompiledModel(compiledCarModelCache.computeIfAbsent(car.getModelId(),
											id -> CompiledCarModel.compile(result.model(), result.materials(), CompiledCarModel.FALLBACK_TEXTURE, false, id)));
								} else {
									var model = ObjModelLoader.loadModel(client.getResourceManager(), car.getModelId());
									var materials = ObjModelLoader.loadMaterials(client.getResourceManager(), car.getModelId());
									car.setModel(model);
									car.setMaterials(materials);

									car.setCompiledModel(compiledCarModelCache.computeIfAbsent(car.getModelId(),
											id -> CompiledCarModel.compile(model, materials, CompiledCarModel.FALLBACK_TEXTURE)));
								}
							} catch (Exception e) {
								AiObjectMod.LOGGER.error("Failed to load model: " + car.getModelId(), e);
							}
						}
					}

					CompiledCarModel compiledForWheelDetect = car.getCompiledModel();
					if (compiledForWheelDetect != null && !car.hasDetectedWheelFootprint()
							&& compiledForWheelDetect.wheelPivots.size() == 4) {
						float sumAbsX = 0f;
						float sumFrontZ = 0f;
						int frontCount = 0;
						for (ObjModel.WheelPivot pivot : compiledForWheelDetect.wheelPivots) {
							sumAbsX += Math.abs(pivot.centerX);
							if (pivot.front) {
								sumFrontZ += pivot.centerZ;
								frontCount++;
							}
						}
						if (frontCount > 0) {
							float trackHalfWidth = sumAbsX / compiledForWheelDetect.wheelPivots.size();
							float rearZ = sumFrontZ / frontCount;
							car.setDetectedWheelFootprint(trackHalfWidth, rearZ);

							SetDetectedWheelFootprintPayload mineturismo$payload = new SetDetectedWheelFootprintPayload(car.getId(), trackHalfWidth, rearZ);
							ModClientNetworking.sendToServer(SetDetectedWheelFootprintPayload.ID, mineturismo$payload, SetDetectedWheelFootprintPayload::write);
							AiObjectMod.LOGGER.info("Detected wheel footprint for {}: trackHalfWidth={}, rearZ={}",
									car.getModelId(), trackHalfWidth, rearZ);
						}
					}

					if (car.getWheelAttachment() == null) {
						WheelAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setWheelAttachment);
					}

					com.examplebett.render.WheelAttachment attachment = car.getWheelAttachment();
					if (attachment != null) {

						String override = car.getWheelModelIdOverride();
						String desiredWheelModelId = (override != null && !override.isEmpty())
								? override : attachment.wheelModelId();
						boolean isNoneStyle = com.examplebett.render.WheelAttachment.NONE_STYLE.equals(desiredWheelModelId);

						car.setWheelStyleDefaultWidthScale(isNoneStyle ? 1f
								: WheelStyleCatalog.getDefaultWheelWidthScale(client.getResourceManager(), desiredWheelModelId));

						String colorOverride = car.getWheelColorOverride();
						String desiredWheelColorId = "";
						if (!isNoneStyle) {
							var colorOptions = WheelColorLoader.load(client.getResourceManager(), desiredWheelModelId);
							if (colorOptions.isPresent() && !colorOptions.get().ids().isEmpty()) {
								List<String> colorIds = colorOptions.get().ids();
								desiredWheelColorId = (colorOverride != null && colorIds.contains(colorOverride))
										? colorOverride : colorIds.get(0);
							}
						}
						boolean styleChanged = !desiredWheelModelId.equals(car.getLoadedWheelModelId());
						boolean colorChanged = !desiredWheelColorId.equals(car.getLoadedWheelColorId());
						if (styleChanged || colorChanged) {
							if (isNoneStyle) {

								car.setWheelCompiledModel(null);
								car.setWheelCompiledModelMirrored(null);
								car.setLoadedWheelModelId(desiredWheelModelId);
								car.setLoadedWheelColorId(desiredWheelColorId);
							} else {
								try {

									final String wheelModelIdForCache = desiredWheelModelId;
									final String wheelColorIdForCache = desiredWheelColorId;
									String wheelCacheKey = wheelModelIdForCache + "#" + wheelColorIdForCache;
									WheelCompiledPair compiledPair = compiledWheelModelCache.computeIfAbsent(wheelCacheKey, key -> {

										try {
											var wheelModel = ObjModelLoader.loadModel(client.getResourceManager(), wheelModelIdForCache);
											var wheelMaterials = ObjModelLoader.loadMaterials(client.getResourceManager(), wheelModelIdForCache);
											if (!wheelColorIdForCache.isEmpty()) {

												wheelMaterials = ObjModelLoader.loadLiveryMaterials(
														client.getResourceManager(), wheelModelIdForCache, wheelMaterials, wheelColorIdForCache);
											}

											CompiledCarModel compiled = CompiledCarModel.compile(
													wheelModel, wheelMaterials, CompiledCarModel.FALLBACK_TEXTURE, true);
											return new WheelCompiledPair(compiled, CompiledCarModel.mirrorX(compiled));
										} catch (java.io.IOException wheelIoError) {
											throw new java.io.UncheckedIOException(wheelIoError);
										}
									});
									car.setWheelCompiledModel(compiledPair.normal());
									car.setWheelCompiledModelMirrored(compiledPair.mirrored());
									car.setLoadedWheelModelId(desiredWheelModelId);
									car.setLoadedWheelColorId(desiredWheelColorId);
								} catch (Exception wheelLoadError) {
									AiObjectMod.LOGGER.error("Failed to load wheel model: " + desiredWheelModelId, wheelLoadError);
								}
							}
						}
					}

					if (car.getLightAttachment() == null) {
						com.examplebett.client.render.LightAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setLightAttachment);
					}

					if (car.getCameraAttachment() == null) {
						com.examplebett.client.render.CameraAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setCameraAttachment);
					}

					if (car.getPlateAttachment() == null) {
						com.examplebett.client.render.PlateAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setPlateAttachment);
					}

					if (car.getVehicleCategories() == null) {
						com.examplebett.client.render.VehicleCategoryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setVehicleCategories);
					}

					if (car.getLiveryOptions() == null) {
						com.examplebett.client.render.LiveryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setLiveryOptions);
					}

					com.examplebett.render.LiveryOptions liveryOptions = car.getLiveryOptions();
					if (liveryOptions != null && !liveryOptions.ids().isEmpty() && car.getModel() != null) {
						String liveryOverride = car.getLiveryOverride();
						String desiredLivery = (liveryOverride != null && !liveryOverride.isEmpty())
								? liveryOverride : liveryOptions.ids().get(0);
						if (!desiredLivery.equals(car.getLoadedLiveryId())) {
							try {
								var liveryMaterials = ObjModelLoader.loadLiveryMaterials(
										client.getResourceManager(), car.getModelId(), car.getMaterials(), desiredLivery);
								String liveryCacheKey = car.getModelId() + "#" + desiredLivery;
								var carModel = car.getModel();
								car.setCompiledModel(compiledCarModelCache.computeIfAbsent(liveryCacheKey,
										key -> liveryOptions.isDecal(desiredLivery)
												? CompiledCarModel.compileBodyAndDecal(carModel, liveryMaterials, CompiledCarModel.FALLBACK_TEXTURE, false)
												: CompiledCarModel.compile(carModel, liveryMaterials, CompiledCarModel.FALLBACK_TEXTURE)));
								car.setLoadedLiveryId(desiredLivery);
							} catch (Exception liveryLoadError) {
								AiObjectMod.LOGGER.error("Failed to load livery: " + desiredLivery, liveryLoadError);
							}
						}
					}

					if (car.isTownCar() || car.isRailwayRouteVehicle()) {

						if (client.player != null && !ambientEngineSounds.containsKey(car)
								&& car.distanceToSqr(client.player) <= AMBIENT_ENGINE_RANGE * AMBIENT_ENGINE_RANGE) {
							ambientEngineCandidates.add(car);
						}
					} else {

						car.updateClientSlideIntensity(car == drivingCar);
						TireSlipSoundInstance slipSound = tireSlipSounds.get(car);
						if ((slipSound == null || slipSound.isStopped()) && car.getClientSlideIntensity() > 0.0f) {
							slipSound = new TireSlipSoundInstance(car);
							client.getSoundManager().play(slipSound);
							tireSlipSounds.put(car, slipSound);
						}
					}

					PoliceSirenSoundInstance sirenSound = policeSirenSounds.get(car);
					if (car.isPoliceSirenOn()) {

						if (sirenSound != null && !sirenSound.isStopped() && sirenSound.isSoundOutdated()) {
							sirenSound.requestRelease();
							sirenSound = null;
						}

						boolean sirenHasSound = com.examplebett.entity.HornSirenSounds.sirenById(car.getSirenSoundId()).sound() != null;
						if (sirenHasSound && (sirenSound == null || sirenSound.isReadyForSuccessor())) {
							PoliceSirenSoundInstance successor = new PoliceSirenSoundInstance(car);
							client.getSoundManager().play(successor);
							policeSirenSounds.put(car, successor);
						}
					} else if (sirenSound != null && !sirenSound.isStopped()) {
						sirenSound.requestRelease();
					}
				} else if (entity instanceof BreakableTireStackEntity tireStack) {
					if (tireStack.getCompiledModel() == null) {
						try {
							var model = ObjModelLoader.loadModel(client.getResourceManager(), tireStack.getModelId());
							var materials = ObjModelLoader.loadMaterials(client.getResourceManager(), tireStack.getModelId());
							tireStack.setModel(model);
							tireStack.setMaterials(materials);
							tireStack.setCompiledModel(CompiledCarModel.compile(model, materials, CompiledCarModel.FALLBACK_TEXTURE));
						} catch (Exception e) {
							AiObjectMod.LOGGER.error("Failed to load model: " + tireStack.getModelId(), e);
						}
					}
				}
			}

			tireSlipSounds.entrySet().removeIf(entry -> entry.getValue().isStopped());

			policeSirenSounds.entrySet().removeIf(entry -> entry.getValue().isStopped());

			ambientEngineSounds.entrySet().removeIf(entry -> {
				CarEntity car = entry.getKey();
				boolean outOfRange = client.player == null
						|| car.distanceToSqr(client.player) > AMBIENT_ENGINE_RANGE * AMBIENT_ENGINE_RANGE;
				if (entry.getValue().isStopped() || !car.isAlive() || outOfRange) {
					client.getSoundManager().stop(entry.getValue());
					return true;
				}
				return false;
			});

			for (Map.Entry<CarEntity, EngineSoundInstance> entry : ambientEngineSounds.entrySet()) {
				if (entry.getValue().isReadyForSuccessor()) {
					EngineSoundInstance successor = new EngineSoundInstance(entry.getKey(), entry.getValue().getVolume(), true);
					client.getSoundManager().play(successor);
					entry.setValue(successor);
				}
			}
			while (ambientEngineSounds.size() < MAX_AMBIENT_ENGINE_SOUNDS && !ambientEngineCandidates.isEmpty()) {
				CarEntity picked = ambientEngineCandidates.remove(client.level.random.nextInt(ambientEngineCandidates.size()));
				float volume = (AMBIENT_ENGINE_MIN_VOLUME
						+ client.level.random.nextFloat() * (AMBIENT_ENGINE_MAX_VOLUME - AMBIENT_ENGINE_MIN_VOLUME))
						* ambientEngineVolumeMultiplier;
				EngineSoundInstance ambientSound = new EngineSoundInstance(picked, volume, true);
				client.getSoundManager().play(ambientSound);
				ambientEngineSounds.put(picked, ambientSound);
			}
		});
	}

	private static Component getCarDisplayName(CarEntity car) {
		Item item = MODEL_ID_TO_SPAWN_ITEM.get(car.getModelId());
		Component name = item != null ? new ItemStack(item).getHoverName() : ModText.literal(car.getModelId());
		if (!mountToastCustomFontEnabled) {
			return name;
		}

		return name.copy().withStyle(style -> style.withFont(MOUNT_TOAST_FONT));
	}

	private static Component getCarCategoryText(CarEntity car) {
		java.util.Set<com.examplebett.entity.VehicleCategory> categories = car.getVehicleCategories();
		if (categories == null || categories.isEmpty()) {
			return null;
		}
		java.util.List<com.examplebett.entity.VehicleCategory> sorted = new java.util.ArrayList<>(categories);
		sorted.sort(java.util.Comparator.naturalOrder());
		net.minecraft.network.chat.MutableComponent joined = ModText.literal("");
		for (int i = 0; i < sorted.size(); i++) {
			if (i > 0) {
				joined.append(" / ");
			}
			joined.append(ModText.translatable(sorted.get(i).translationKey));
		}
		if (!mountToastCustomFontEnabled) {
			return joined;
		}
		return joined.withStyle(style -> style.withFont(MOUNT_TOAST_FONT));
	}

	private static boolean mineturismo$canToggleTrailerCoupling(CarEntity drivingCar) {
		if (!drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
			return false;
		}
		return drivingCar.findCoupledTrailer() != null || drivingCar.findCouplableTrailer() != null;
	}

	private static void mineturismo$renderWaypointHint(GuiGraphics guiGraphics) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}
		Item mainHand = client.player.getMainHandItem().getItem();
		Item offHand = client.player.getOffhandItem().getItem();
		Component[] hintLines;
		if (com.examplebett.client.GttClient.isActive()) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.gtt.hud_hint") };
		} else if (client.player.getVehicle() instanceof CarEntity mineturismo$hintCar && mineturismo$canToggleTrailerCoupling(mineturismo$hintCar)) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.trailer.hud_hint", TRAILER_COUPLING_KEY.getTranslatedKeyMessage()) };
		} else if (mainHand instanceof WaypointMarkerItem || offHand instanceof WaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.waypoint_marker.hud_hint") };
		} else if (mainHand instanceof OneWayWaypointMarkerItem || offHand instanceof OneWayWaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.one_way_marker.hud_hint") };
		} else if (mainHand instanceof RailwayWaypointMarkerItem || offHand instanceof RailwayWaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.railway_marker.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.TowRopeItem || offHand instanceof com.examplebett.item.TowRopeItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.tow_rope.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.RailwayStopMarkerItem || offHand instanceof com.examplebett.item.RailwayStopMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.railway_stop_marker.hud_hint") };
		} else if (mainHand instanceof TrafficSignalMarkerItem || offHand instanceof TrafficSignalMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.traffic_signal_marker.hud_hint_edit") };
		} else if (mainHand instanceof com.examplebett.item.SmartDispatchAppItem || offHand instanceof com.examplebett.item.SmartDispatchAppItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.smart_dispatch.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.CustomWrenchItem || offHand instanceof com.examplebett.item.CustomWrenchItem) {

			hintLines = new Component[] { ModText.translatable("gui.mineturismo.custom_wrench.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.WheelWrenchItem || offHand instanceof com.examplebett.item.WheelWrenchItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.wheel_wrench.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.DebugWrenchItem || offHand instanceof com.examplebett.item.DebugWrenchItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.debug_wrench.hud_hint") };
		} else {
			return;
		}
		float scale = 0.85f;
		int padding = 4;
		int lineHeight = 10;
		int maxWidth = 0;
		for (Component line : hintLines) {
			maxWidth = Math.max(maxWidth, client.font.width(line));
		}
		int boxWidth = maxWidth + padding * 2;
		int boxHeight = hintLines.length * lineHeight + padding * 2;
		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(scale, scale, 1.0f);
		int boxX = Math.round(4 / scale);
		int boxY = Math.round(4 / scale);
		guiGraphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xFF000000);
		for (int i = 0; i < hintLines.length; i++) {
			guiGraphics.drawString(client.font, hintLines[i], boxX + padding, boxY + padding + i * lineHeight, 0xFFFFFF00, false);
		}
		guiGraphics.pose().popPose();
	}

	private static void renderMountToast(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (mountToastText == null || mountToastStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - mountToastStartMs;
		if (elapsed >= MOUNT_TOAST_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < MOUNT_TOAST_FADE_IN_MS) {
			alpha = elapsed / (float) MOUNT_TOAST_FADE_IN_MS;
		} else if (elapsed > MOUNT_TOAST_TOTAL_MS - MOUNT_TOAST_FADE_OUT_MS) {
			alpha = (MOUNT_TOAST_TOTAL_MS - elapsed) / (float) MOUNT_TOAST_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));

		int color = ((int) (alpha * 255.0f) << 24) | 0xFFFFFF;

		int x = (int) ((context.guiWidth() - 6) / MOUNT_TOAST_SCALE - mc.font.width(mountToastText));
		int y = (int) ((context.guiHeight() - 34) / MOUNT_TOAST_SCALE);

		context.pose().pushPose();
		context.pose().scale(MOUNT_TOAST_SCALE, MOUNT_TOAST_SCALE, 1.0f);
		context.drawString(mc.font, mountToastText, x, y, color, true);

		if (mountToastCategoryText != null) {
			int categoryX = (int) ((context.guiWidth() - 6) / MOUNT_TOAST_SCALE - mc.font.width(mountToastCategoryText));
			int categoryY = y + mc.font.lineHeight + 2;
			context.drawString(mc.font, mountToastCategoryText, categoryX, categoryY, color, true);
		}
		context.pose().popPose();
	}

	private static void renderSpeedDisplay(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (speedDisplayMode == SpeedDisplayMode.NONE || mc.options.hideGui || mc.player == null) return;

		Entity vehicle = mc.player.getVehicle();
		if (!(vehicle instanceof CarEntity car)) return;

		if (vehicle != lastVehicle) {
			return;
		}

		if (mountToastStartMs >= 0 && System.currentTimeMillis() - mountToastStartMs < MOUNT_TOAST_TOTAL_MS) {
			return;
		}

		double blocksPerTick = car.getDeltaMovement().horizontalDistance();
		double kmh = blocksPerTick * 20.0 * 3.6;
		double displaySpeed = speedDisplayMode == SpeedDisplayMode.MPH ? kmh * 0.621371 : kmh;
		String numberText = String.valueOf(Math.round(displaySpeed));
		String unitText = speedDisplayMode == SpeedDisplayMode.MPH ? "mph" : "km/h";

		int color = 0xFFFFFFFF;
		int rightEdge = context.guiWidth() - 6;
		int nameRowY = context.guiHeight() - 34;

		int numberWidth = Math.round(mc.font.width(numberText) * SPEED_NUMBER_SCALE);
		int unitWidth = Math.round(mc.font.width(unitText) * SPEED_UNIT_SCALE);
		int unitScreenX = rightEdge - unitWidth;
		int numberScreenX = unitScreenX - SPEED_UNIT_GAP - numberWidth;

		int numberHeight = Math.round(mc.font.lineHeight * SPEED_NUMBER_SCALE);
		int unitHeight = Math.round(mc.font.lineHeight * SPEED_UNIT_SCALE);
		int numberScreenY = nameRowY;

		int unitScreenY = nameRowY + (numberHeight - unitHeight);

		context.pose().pushPose();
		context.pose().scale(SPEED_NUMBER_SCALE, SPEED_NUMBER_SCALE, 1.0f);
		context.drawString(mc.font, numberText, Math.round(numberScreenX / SPEED_NUMBER_SCALE), Math.round(numberScreenY / SPEED_NUMBER_SCALE), color, true);
		context.pose().popPose();

		context.pose().pushPose();
		context.pose().scale(SPEED_UNIT_SCALE, SPEED_UNIT_SCALE, 1.0f);
		context.drawString(mc.font, unitText, Math.round(unitScreenX / SPEED_UNIT_SCALE), Math.round(unitScreenY / SPEED_UNIT_SCALE), color, true);
		context.pose().popPose();
	}

	private static void renderPassengerTransportWarning(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (hudWarningText == null || hudWarningStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - hudWarningStartMs;
		if (elapsed >= HUD_WARNING_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < HUD_WARNING_FADE_IN_MS) {
			alpha = elapsed / (float) HUD_WARNING_FADE_IN_MS;
		} else if (elapsed > HUD_WARNING_TOTAL_MS - HUD_WARNING_FADE_OUT_MS) {
			alpha = (HUD_WARNING_TOTAL_MS - elapsed) / (float) HUD_WARNING_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));

		int color = ((int) (alpha * 255.0f) << 24) | 0xFFFFFF;

		context.drawString(mc.font, hudWarningText, 4, 28, color, true);
	}

	private static void renderHintTip(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (!hintsEnabled || hintTipLines == null || hintTipStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - hintTipStartMs;
		if (elapsed >= HINT_TIP_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < HINT_TIP_FADE_IN_MS) {
			alpha = elapsed / (float) HINT_TIP_FADE_IN_MS;
		} else if (elapsed > HINT_TIP_TOTAL_MS - HINT_TIP_FADE_OUT_MS) {
			alpha = (HINT_TIP_TOTAL_MS - elapsed) / (float) HINT_TIP_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));
		int alpha8 = (int) (alpha * 255.0f);

		int textColor = (alpha8 << 24) | 0xFFFFFF;
		int backgroundColor = alpha8 << 24;

		List<String> rows = new ArrayList<>();
		for (Component line : hintTipLines) {
			for (String row : line.getString().split("\n", -1)) {
				rows.add(row);
			}
		}
		int maxWidth = 0;
		for (String row : rows) {
			maxWidth = Math.max(maxWidth, mc.font.width(row));
		}
		int lineHeight = mc.font.lineHeight;
		int padding = 4;
		int boxWidth = maxWidth + padding * 2;
		int boxHeight = rows.size() * lineHeight + padding * 2;

		int screenX = 4;
		int screenY = 4;
		context.pose().pushPose();
		context.pose().scale(HINT_TIP_SCALE, HINT_TIP_SCALE, 1.0f);
		int x = Math.round(screenX / HINT_TIP_SCALE);
		int y = Math.round(screenY / HINT_TIP_SCALE);
		context.fill(x, y, x + boxWidth, y + boxHeight, backgroundColor);
		for (int i = 0; i < rows.size(); i++) {
			context.drawString(mc.font, rows.get(i), x + padding, y + padding + i * lineHeight, textColor, true);
		}
		context.pose().popPose();

		Component disableHint = ModText.translatable("gui.mineturismo.hint_tip.disable_instruction",
				ModText.translatable("item.mineturismo.debug_wrench"),
				ModText.translatable("gui.mineturismo.debug_wrench_menu.other"),
				ModText.translatable("gui.mineturismo.debug_wrench_menu.hide_hints"));
		int subLineHeight = mc.font.lineHeight;
		int subBoxWidth = mc.font.width(disableHint) + padding * 2;
		int subBoxHeight = subLineHeight + padding * 2;
		int subScreenY = screenY + Math.round(boxHeight * HINT_TIP_SCALE);
		context.pose().pushPose();
		context.pose().scale(HINT_TIP_SUB_SCALE, HINT_TIP_SUB_SCALE, 1.0f);
		int subX = Math.round(screenX / HINT_TIP_SUB_SCALE);
		int subY = Math.round(subScreenY / HINT_TIP_SUB_SCALE);
		context.fill(subX, subY, subX + subBoxWidth, subY + subBoxHeight, backgroundColor);
		context.drawString(mc.font, disableHint, subX + padding, subY + padding, textColor, true);
		context.pose().popPose();
	}
}
*/
//?}
//? if <1.17 && >=1.16.2 {
/*
package com.examplebett.client;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.block.AiObjectModBlocks;
import com.examplebett.client.gui.CarWrenchScreen;
import com.examplebett.client.gui.RailwayConsistScreen;
import com.examplebett.client.gui.WheelAdjustScreen;
import com.examplebett.client.gui.OneWayMarkerSettingsScreen;
import com.examplebett.client.gui.TrafficSignalSettingsScreen;
import com.examplebett.client.gui.WaypointTrafficSettingsScreen;
import com.examplebett.entity.AiObjectModEntities;
import com.examplebett.entity.BreakableTireStackEntity;
import com.examplebett.entity.BreakableBoxEntity;
import com.examplebett.entity.BreakableConeEntity;
import com.examplebett.entity.CarEntity;
import com.examplebett.client.render.BrakeCaliperModel;
import com.examplebett.client.render.BreakableTireStackRenderer;
import com.examplebett.client.render.CameraAttachmentLoader;
import com.examplebett.client.render.CarEntityRenderer;
import com.examplebett.client.render.GlbModelLoader;
import com.examplebett.client.render.LightAttachmentLoader;
import com.examplebett.client.render.LiveryLoader;
import com.examplebett.client.render.LodWheelModel;
import com.examplebett.client.render.PlateAttachmentLoader;
import com.examplebett.client.render.PoliceLightModel;
import com.examplebett.client.render.RearWingModel;
import com.examplebett.client.render.OtherPartModel;
import com.examplebett.client.render.TaxiSignModel;
import com.examplebett.client.render.WheelAttachmentLoader;
import com.examplebett.client.render.WheelColorLoader;
import com.examplebett.client.render.WheelStyleCatalog;
import com.examplebett.client.sound.EngineSoundInstance;
import com.examplebett.client.sound.HornSoundInstance;
import com.examplebett.client.sound.PoliceSirenSoundInstance;
import com.examplebett.client.sound.TireSlipSoundInstance;
import com.examplebett.item.CarSpawnItem;
import com.examplebett.item.OneWayWaypointMarkerItem;
import com.examplebett.item.RailwayWaypointMarkerItem;
import com.examplebett.item.TrafficSignalMarkerItem;
import com.examplebett.item.WaypointMarkerItem;
import com.examplebett.network.OpenRailwaySpawnSetupPayload;
import com.examplebett.network.OpenRailwayConsistSettingsPayload;
import com.examplebett.network.OpenSignalSettingsPayload;
import com.examplebett.network.PlayCollisionSoundPayload;
import com.examplebett.network.SetDetectedWheelFootprintPayload;
import com.examplebett.network.SetClientSlipReportPayload;
import com.examplebett.network.SetClientWheelieTriggerPayload;
import com.examplebett.network.SetWheelRotationReportPayload;
import com.examplebett.network.ShowHudWarningPayload;
import com.examplebett.network.SyncAmbientEngineVolumePayload;
import com.examplebett.network.SyncRailwayFormationOriginPayload;
import com.examplebett.network.SyncWheelDistancePayload;
import com.examplebett.waypoint.WaypointNetworkSavedData;
import com.examplebett.network.SetHornStatePayload;
import com.examplebett.network.ToggleTrailerCouplingPayload;
import com.examplebett.network.SetPoliceLightsOnPayload;
import com.examplebett.render.CompiledCarModel;
import com.examplebett.render.CustomPartCatalog;
import com.examplebett.render.FrontBrakeCaliperParts;
import com.examplebett.render.ObjModel;
import com.examplebett.render.ObjModelLoader;
import com.examplebett.render.OtherParts;
import com.examplebett.render.PoliceLightParts;
import com.examplebett.render.RearBrakeCaliperParts;
import com.examplebett.render.RearWingParts;
import com.examplebett.render.TaxiSignParts;
import com.examplebett.sound.AiObjectModSounds;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import com.examplebett.compat.ModRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiObjectModClient implements ClientModInitializer {

	public interface ExternalCarModelLoader {
		void tryLoad(CarEntity car);
	}

	public static ExternalCarModelLoader externalCarModelLoader = null;

	private static final long MOUNT_TOAST_TOTAL_MS = 3000L;
	private static final long MOUNT_TOAST_FADE_IN_MS = 300L;
	private static final long MOUNT_TOAST_FADE_OUT_MS = 700L;
	private static final ResourceLocation MOUNT_TOAST_FONT = AiObjectMod.id("mount_toast");
	private static final float MOUNT_TOAST_SCALE = 1.5f;

	private static boolean mountToastCustomFontEnabled = true;

	public static boolean isMountToastCustomFontEnabled() {
		return mountToastCustomFontEnabled;
	}

	public static void setMountToastCustomFontEnabled(boolean enabled) {
		mountToastCustomFontEnabled = enabled;
	}

	private static boolean fakeReflectionEnabled = false;

	public static boolean isFakeReflectionEnabled() {
		return fakeReflectionEnabled;
	}

	public static void setFakeReflectionEnabled(boolean enabled) {
		fakeReflectionEnabled = enabled;
	}

	private static boolean hintsEnabled = true;

	public static boolean isHintsEnabled() {
		return hintsEnabled;
	}

	public static void setHintsEnabled(boolean enabled) {
		hintsEnabled = enabled;
	}

	private static boolean fpsLogEnabled = false;

	public static boolean isFpsLogEnabled() {
		return fpsLogEnabled;
	}

	public static void setFpsLogEnabled(boolean enabled) {
		fpsLogEnabled = enabled;
	}

	private static final long FPS_LOG_INTERVAL_TICKS = 20L;
	private static long fpsLogTicks = 0L;

	private static final java.lang.reflect.Field LEGACY_FPS_FIELD = resolveLegacyFpsField();

	private static java.lang.reflect.Field resolveLegacyFpsField() {
		try {
			java.lang.reflect.Field field = com.examplebett.compat.ModReflection.findField(net.minecraft.client.Minecraft.class, "fps", "field_1738");
			field.setAccessible(true);
			return field;
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}

	private static int getGameFps(net.minecraft.client.Minecraft client) {
		if (LEGACY_FPS_FIELD == null) {
			return -1;
		}
		try {
			return LEGACY_FPS_FIELD.getInt(null);
		} catch (ReflectiveOperationException e) {
			return -1;
		}
	}

	public enum SpeedDisplayMode {
		NONE, KMH, MPH;

		public SpeedDisplayMode next() {
			return switch (this) {
				case NONE -> KMH;
				case KMH -> MPH;
				case MPH -> NONE;
			};
		}
	}

	private static SpeedDisplayMode speedDisplayMode = SpeedDisplayMode.NONE;
	private static final float SPEED_NUMBER_SCALE = 2.0f;
	private static final float SPEED_UNIT_SCALE = 1.0f;
	private static final int SPEED_UNIT_GAP = 3;

	public static SpeedDisplayMode getSpeedDisplayMode() {
		return speedDisplayMode;
	}

	public static void setSpeedDisplayMode(SpeedDisplayMode mode) {
		speedDisplayMode = mode;
	}
	private static final Map<String, Item> MODEL_ID_TO_SPAWN_ITEM = new HashMap<>();

	private static final Map<String, CompiledCarModel> compiledCarModelCache = new HashMap<>();

	private static final Map<String, WheelCompiledPair> compiledWheelModelCache = new HashMap<>();
	private record WheelCompiledPair(CompiledCarModel normal, CompiledCarModel mirrored) {
	}
	private static Entity lastVehicle = null;
	private static Component mountToastText = null;

	private static Component mountToastCategoryText = null;
	private static long mountToastStartMs = -1L;

	private static final long HUD_WARNING_TOTAL_MS = 3000L;
	private static final long HUD_WARNING_FADE_IN_MS = 200L;
	private static final long HUD_WARNING_FADE_OUT_MS = 700L;
	private static Component hudWarningText = null;
	private static long hudWarningStartMs = -1L;

	private record TimedHint(long delayTicks, String... lineKeys) {
	}

	private static final TimedHint[] TIMED_HINTS = {
			new TimedHint(1200L,
					"gui.mineturismo.reflection_tip.line1",
					"gui.mineturismo.reflection_tip.line2",
					"gui.mineturismo.reflection_tip.line3"),
			new TimedHint(1360L,
					"gui.mineturismo.shader_tip.line1",
					"gui.mineturismo.shader_tip.line2"),
			new TimedHint(3600L,
					"gui.mineturismo.smart_dispatch_tip.line1",
					"gui.mineturismo.smart_dispatch_tip.line2"),
			new TimedHint(6000L,
					"gui.mineturismo.wrench_tip.line1",
					"gui.mineturismo.wrench_tip.line2"),
			new TimedHint(12000L,
					"gui.mineturismo.traffic_marker_tip.line1",
					"gui.mineturismo.traffic_marker_tip.line2"),
	};

	private static final long HINT_TIP_TOTAL_MS = 8000L;
	private static final long HINT_TIP_FADE_IN_MS = 300L;
	private static final long HINT_TIP_FADE_OUT_MS = 700L;
	private static final float HINT_TIP_SCALE = 1.1f;

	private static final float HINT_TIP_SUB_SCALE = 0.75f;
	private static int nextHintIndex = 0;
	private static long hintTicks = 0L;
	private static Component[] hintTipLines = null;
	private static long hintTipStartMs = -1L;
	private static boolean creditsScreenChecked = false;

	private static final Map<CarEntity, EngineSoundInstance> engineSounds = new HashMap<>();

	private static final Map<CarEntity, EngineSoundInstance> engineLowSounds = new HashMap<>();

	private static final int WHEEL_ROTATION_REPORT_INTERVAL_TICKS = 2;
	private static int wheelRotationReportTickCounter = 0;

	private static final Map<CarEntity, TireSlipSoundInstance> tireSlipSounds = new HashMap<>();

	private static final Map<CarEntity, PoliceSirenSoundInstance> policeSirenSounds = new HashMap<>();

	private static final Map<CarEntity, EngineSoundInstance> ambientEngineSounds = new HashMap<>();
	private static final double AMBIENT_ENGINE_RANGE = 24.0;
	private static final int MAX_AMBIENT_ENGINE_SOUNDS = 3;
	private static final float AMBIENT_ENGINE_MIN_VOLUME = 0.2f;
	private static final float AMBIENT_ENGINE_MAX_VOLUME = 0.4f;

	private static float ambientEngineVolumeMultiplier = WaypointNetworkSavedData.DEFAULT_AMBIENT_ENGINE_VOLUME_MULTIPLIER;

	private static final String KEY_CATEGORY = "key.categories.mineturismo";
	private static final KeyMapping HORN_KEY = new KeyMapping(
			"key.mineturismo.horn", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, KEY_CATEGORY);

	private static final KeyMapping TRAILER_COUPLING_KEY = new KeyMapping(
			"key.mineturismo.trailer_coupling", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_L, KEY_CATEGORY);

	public static KeyMapping hornKey() {
		return HORN_KEY;
	}

	public static KeyMapping trailerCouplingKey() {
		return TRAILER_COUPLING_KEY;
	}

	private static HornSoundInstance activeHorn = null;

	private static CarEntity hornBroadcastCar = null;

	private static boolean hornKeyWasDown = false;

	private static boolean trailerCouplingKeyWasDown = false;

	private static boolean sprintKeyHeldSent = false;
	private static CarEntity sprintKeyHeldCar = null;

	private static void mineturismo$handlePlayCollisionSound(PlayCollisionSoundPayload payload) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			return;
		}

		net.minecraft.sounds.SoundEvent collisionSound = payload.lowSpeed() ? AiObjectModSounds.CAR_COLLISION_LOW : AiObjectModSounds.CAR_COLLISION;

		SimpleSoundInstance sound = new SimpleSoundInstance(
				collisionSound.getLocation(), SoundSource.NEUTRAL,
				payload.volume(), payload.pitch(),
				false, 0, SoundInstance.Attenuation.NONE,
				payload.x(), payload.y(), payload.z(), false);
		mc.getSoundManager().play(sound);
	}

	private static void mineturismo$handleShowHudWarning(ShowHudWarningPayload payload) {
		hudWarningText = ModText.translatable(payload.messageKey());
		hudWarningStartMs = System.currentTimeMillis();
		Minecraft.getInstance().getSoundManager().play(
				SimpleSoundInstance.forUI(AiObjectModSounds.PASSENGER_TRANSPORT_DENIED, 1.0f));
	}

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.CAR_ENTITY, (dispatcher, context) -> new CarEntityRenderer(dispatcher));
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.BREAKABLE_TIRE_STACK, (dispatcher, context) -> new BreakableTireStackRenderer<BreakableTireStackEntity>(dispatcher));
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.BREAKABLE_BOX, (dispatcher, context) -> new BreakableTireStackRenderer<BreakableBoxEntity>(dispatcher));
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.BREAKABLE_BOX_2, (dispatcher, context) -> new BreakableTireStackRenderer<BreakableBoxEntity>(dispatcher));
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.TRAFFIC_CONE, (dispatcher, context) -> new BreakableTireStackRenderer<BreakableConeEntity>(dispatcher));
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.BARRICADE, (dispatcher, context) -> new BreakableTireStackRenderer<com.examplebett.entity.BreakableBarricadeEntity>(dispatcher));

		KeyBindingHelper.registerKeyBinding(HORN_KEY);
		KeyBindingHelper.registerKeyBinding(TRAILER_COUPLING_KEY);

		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
				new SimpleSynchronousResourceReloadListener() {
					@Override
					public ResourceLocation getFabricId() {
						return AiObjectMod.id("clear_render_caches");
					}

					@Override
					public void onResourceManagerReload(ResourceManager resourceManager) {
						ObjModelLoader.clearCache();
						GlbModelLoader.clearCache();
						WheelAttachmentLoader.clearCache();
						WheelColorLoader.clearCache();
						LightAttachmentLoader.clearCache();
						com.examplebett.client.render.VehicleCategoryLoader.clearCache();
						PlateAttachmentLoader.clearCache();
						CameraAttachmentLoader.clearCache();
						LodWheelModel.clearCache();
						LiveryLoader.clearCache();
						RearWingModel.clearCache();
						PoliceLightModel.clearCache();
						TaxiSignModel.clearCache();
						OtherPartModel.clearCache();
						BrakeCaliperModel.clearCache();
						CustomPartCatalog.clearCache();
						WheelStyleCatalog.clearCache();
						RearWingParts.clearCache();
						OtherParts.clearCache();
						PoliceLightParts.clearCache();
						TaxiSignParts.clearCache();
						FrontBrakeCaliperParts.clearCache();
						RearBrakeCaliperParts.clearCache();
						compiledCarModelCache.clear();
						compiledWheelModelCache.clear();
					}
				});

		ModClientNetworking.registerS2C(PlayCollisionSoundPayload.ID, PlayCollisionSoundPayload::read, AiObjectModClient::mineturismo$handlePlayCollisionSound);

		ModClientNetworking.registerS2C(SyncWheelDistancePayload.ID, SyncWheelDistancePayload::read, payload -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc.level == null) {
				return;
			}
			if (mc.level.getEntity(payload.entityId()) instanceof CarEntity car) {
				car.setNetworkWheelDistance(payload.distance());
			}
		});

		ModClientNetworking.registerS2C(OpenRailwayConsistSettingsPayload.ID, OpenRailwayConsistSettingsPayload::read, payload -> {
			net.minecraft.client.multiplayer.ClientLevel mineturismo$level = Minecraft.getInstance().level;
			if (mineturismo$level != null && mineturismo$level.getEntity(payload.headEntityId()) instanceof CarEntity car) {
				Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
				Minecraft.getInstance().setScreen(new RailwayConsistScreen(car, null));
			}
		});

		ModClientNetworking.registerS2C(OpenSignalSettingsPayload.ID, OpenSignalSettingsPayload::read, payload -> {
			char direction = payload.direction().isEmpty() ? 'N' : payload.direction().charAt(0);
			TrafficSignalSettingsScreen.applyExternalConfig(payload.signalId(), payload.intersectionName(), direction,
					payload.greenTicks(), payload.redTicks(), payload.phaseOffsetTicks());
			Minecraft.getInstance().setScreen(new TrafficSignalSettingsScreen());
		});

		ModClientNetworking.registerS2C(OpenRailwaySpawnSetupPayload.ID, OpenRailwaySpawnSetupPayload::read, payload -> {
			Minecraft.getInstance().setScreen(new com.examplebett.client.gui.RailwaySpawnVehicleScreen(payload.originNodeId()));
		});

		ModClientNetworking.registerS2C(SyncRailwayFormationOriginPayload.ID, SyncRailwayFormationOriginPayload::read, payload -> {
			com.examplebett.inventory.RailwayFormationMenu.pendingOriginNodeId = payload.originNodeId();
		});

		ModClientNetworking.registerS2C(SyncAmbientEngineVolumePayload.ID, SyncAmbientEngineVolumePayload::read, payload -> {
			ambientEngineVolumeMultiplier = payload.multiplier();
			com.examplebett.client.gui.WaypointClientSettings.apply(payload.multiplier(), payload.settings());
		});

		ModClientNetworking.registerS2C(ShowHudWarningPayload.ID, ShowHudWarningPayload::read, AiObjectModClient::mineturismo$handleShowHudWarning);

		BlockRenderLayerMap.INSTANCE.putBlock(AiObjectModBlocks.CAR_BREAKABLE_GLASS, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(AiObjectModBlocks.WATER_PUDDLE, RenderType.translucent());

		CarEntity.wrenchMenuOpener = (car, player) -> {
			Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
			Minecraft.getInstance().setScreen(new CarWrenchScreen(car));
		};
		CarEntity.wheelWrenchMenuOpener = (car, player) -> {
			Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
			Minecraft.getInstance().setScreen(new WheelAdjustScreen(car));
		};
		CarEntity.debugWrenchMenuOpener = (car, player) -> Minecraft.getInstance().setScreen(new com.examplebett.client.gui.DebugWrenchScreen(car));
		WaypointMarkerItem.trafficSettingsMenuOpener = player -> Minecraft.getInstance().setScreen(new WaypointTrafficSettingsScreen());
		OneWayWaypointMarkerItem.settingsMenuOpener = player -> Minecraft.getInstance().setScreen(new OneWayMarkerSettingsScreen());
		com.examplebett.item.SmartDispatchAppItem.appScreenOpener = player -> Minecraft.getInstance().setScreen(new com.examplebett.client.gui.SmartDispatchAppScreen());
		ClientTickEvents.END_CLIENT_TICK.register(com.examplebett.client.GttClient::tick);
		ClientTickEvents.END_CLIENT_TICK.register(com.examplebett.client.sound.ChorusMusicPlayer::tick);

		ModClientNetworking.registerS2C(com.examplebett.network.GttScorePayload.ID, com.examplebett.network.GttScorePayload::read, payload -> com.examplebett.client.GttClient.setScore(payload.score()));

		ModClientNetworking.registerS2C(com.examplebett.network.SpawnLegacyEntityPayload.ID, com.examplebett.network.SpawnLegacyEntityPayload::read, payload -> {
			Minecraft mineturismo$mc = Minecraft.getInstance();
			if (mineturismo$mc.level == null) {
				return;
			}
			net.minecraft.world.entity.Entity mineturismo$entity = com.examplebett.compat.ModRegistries.ENTITY_TYPE.get(payload.typeId).create(mineturismo$mc.level);
			if (mineturismo$entity == null) {
				return;
			}
			mineturismo$entity.setPacketCoordinates(payload.x, payload.y, payload.z);
			mineturismo$entity.moveTo(payload.x, payload.y, payload.z, payload.yRot, 0.0f);
			mineturismo$entity.setId(payload.entityId);
			mineturismo$entity.setUUID(payload.uuid);
			mineturismo$mc.level.putNonPlayerEntity(payload.entityId, mineturismo$entity);
		});

		ModClientNetworking.registerS2C(com.examplebett.network.SlotSpinResultPayload.ID, com.examplebett.network.SlotSpinResultPayload::read, payload -> com.examplebett.client.gui.SmartDispatchAppScreen.onSlotResult(payload.prizes()));

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> com.examplebett.client.GttClient.renderHud(new GuiGraphics(poseStack)));

		net.minecraft.client.gui.screens.MenuScreens.register(com.examplebett.inventory.AiObjectModMenus.RAILWAY_FORMATION_MENU_TYPE, com.examplebett.client.gui.RailwayFormationScreen::new);
		net.minecraft.client.gui.screens.MenuScreens.register(com.examplebett.inventory.AiObjectModMenus.MASSIVE_AUTO_APPRAISAL_MENU_TYPE, com.examplebett.client.gui.MassiveAutoAppraisalScreen::new);

		HudRenderCallback.EVENT.register((poseStack, deltaTracker) -> mineturismo$renderWaypointHint(new GuiGraphics(poseStack)));

		CarEntity.clientDescendKeyHeld = () -> Minecraft.getInstance().options.keySprint.isDown();

		CarEntity.clientForwardKeyHeld = () -> Minecraft.getInstance().options.keyUp.isDown();
		CarEntity.clientBackKeyHeld = () -> Minecraft.getInstance().options.keyDown.isDown();

		CarEntity.clientDimensionsResourceManager = () -> {
			var integratedServer = Minecraft.getInstance().getSingleplayerServer();
			return integratedServer != null ? com.examplebett.compat.ModResources.getServerResourceManager(integratedServer) : null;
		};

		for (Item item : ModRegistries.ITEM) {
			if (item instanceof CarSpawnItem spawnItem) {
				MODEL_ID_TO_SPAWN_ITEM.putIfAbsent(spawnItem.getModelId(), item);
			}
		}

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> renderMountToast(new GuiGraphics(poseStack)));

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> renderSpeedDisplay(new GuiGraphics(poseStack)));

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> renderPassengerTransportWarning(new GuiGraphics(poseStack)));

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> renderHintTip(new GuiGraphics(poseStack)));

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			nextHintIndex = 0;
			hintTicks = 0L;
			hintTipStartMs = -1L;
		});

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			boolean mineturismo$pressed = false;
			while (TRAILER_COUPLING_KEY.consumeClick()) {
				mineturismo$pressed = true;
			}

			InputConstants.Key mineturismo$boundKey = KeyBindingHelper.getBoundKeyOf(TRAILER_COUPLING_KEY);
			boolean mineturismo$rawDown = mineturismo$boundKey.getType() == InputConstants.Type.KEYSYM
					&& InputConstants.isKeyDown(client.getWindow().getWindow(), mineturismo$boundKey.getValue());
			if (mineturismo$rawDown && !trailerCouplingKeyWasDown && client.screen == null) {
				mineturismo$pressed = true;
			}
			trailerCouplingKeyWasDown = mineturismo$rawDown;

			if (client.player != null && client.screen == null
					&& client.player.getVehicle() instanceof CarEntity mineturismo$headCar
					&& mineturismo$headCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
				for (KeyMapping mineturismo$other : client.options.keyMappings) {
					if (mineturismo$other != TRAILER_COUPLING_KEY && mineturismo$other.same(TRAILER_COUPLING_KEY)) {
						while (mineturismo$other.consumeClick()) {
							mineturismo$pressed = true;
						}
					}
				}
			}
			if (!mineturismo$pressed || client.player == null || client.screen != null
					|| !(client.player.getVehicle() instanceof CarEntity mineturismo$car)
					|| !mineturismo$car.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
				return;
			}
			for (KeyMapping mineturismo$other : client.options.keyMappings) {
				if (mineturismo$other != TRAILER_COUPLING_KEY && mineturismo$other.same(TRAILER_COUPLING_KEY)) {
					while (mineturismo$other.consumeClick()) {

					}
				}
			}
			ToggleTrailerCouplingPayload mineturismo$payload = new ToggleTrailerCouplingPayload(mineturismo$car.getId());
			ModClientNetworking.sendToServer(ToggleTrailerCouplingPayload.ID, mineturismo$payload, ToggleTrailerCouplingPayload::write);
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (creditsScreenChecked) return;
			if (client.player == null || client.level == null) return;
			creditsScreenChecked = true;
			com.examplebett.client.gui.CreditsScreen.showOnceIfNeeded();
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!fpsLogEnabled) return;
			fpsLogTicks++;
			if (fpsLogTicks % FPS_LOG_INTERVAL_TICKS != 0) return;
			int fps = getGameFps(client);
			int carCount = 0;
			if (client.level != null) {
				for (Entity entity : client.level.entitiesForRendering()) {
					if (entity instanceof CarEntity) {
						carCount++;
					}
				}
			}
			AiObjectMod.LOGGER.info("[fps-log] fps={} frameTimeMs={} carCount={}", fps,
					fps > 0 ? String.format("%.1f", 1000.0 / fps) : "n/a", carCount);
		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (hintsEnabled && client.player != null) {
				hintTicks++;
				while (nextHintIndex < TIMED_HINTS.length && hintTicks >= TIMED_HINTS[nextHintIndex].delayTicks()) {
					TimedHint hint = TIMED_HINTS[nextHintIndex];
					hintTipLines = new Component[hint.lineKeys().length];
					for (int i = 0; i < hint.lineKeys().length; i++) {
						hintTipLines[i] = ModText.translatable(hint.lineKeys()[i]);
					}
					hintTipStartMs = System.currentTimeMillis();
					client.getSoundManager().play(
							SimpleSoundInstance.forUI(AiObjectModSounds.PASSENGER_TRANSPORT_DENIED, 1.0f));
					nextHintIndex++;
				}
			}

			CarEntity drivingCar = null;
			if (client.player != null) {
				Entity vehicle = client.player.getVehicle();
				if (vehicle instanceof CarEntity car) {
					drivingCar = car;
				}
				if (vehicle instanceof CarEntity car && vehicle != lastVehicle) {
					mountToastText = getCarDisplayName(car);
					if (car.getVehicleCategories() == null) {
						com.examplebett.client.render.VehicleCategoryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setVehicleCategories);
					}
					mountToastCategoryText = getCarCategoryText(car);
					mountToastStartMs = System.currentTimeMillis();
				}
				lastVehicle = vehicle;
			}

			if (drivingCar != null) {
				EngineSoundInstance existing = engineSounds.get(drivingCar);

				if (existing != null && !existing.matchesCurrentSoundId()) {
					client.getSoundManager().stop(existing);
					existing = null;
				}

				if (existing == null || existing.isReadyForSuccessor()) {
					EngineSoundInstance sound = new EngineSoundInstance(drivingCar, 1.0f, false,
							drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)
									? EngineSoundInstance.Layer.HYBRID_HIGH : EngineSoundInstance.Layer.NORMAL);
					client.getSoundManager().play(sound);
					engineSounds.put(drivingCar, sound);
				}

				if (drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)) {
					EngineSoundInstance lowExisting = engineLowSounds.get(drivingCar);
					if (lowExisting == null || lowExisting.isReadyForSuccessor()) {
						EngineSoundInstance lowSound = new EngineSoundInstance(drivingCar, 1.0f, false, EngineSoundInstance.Layer.HYBRID_LOW);
						client.getSoundManager().play(lowSound);
						engineLowSounds.put(drivingCar, lowSound);
					}
				}
			}

			CarEntity currentDrivingCar = drivingCar;
			engineSounds.entrySet().removeIf(e -> {
				if (e.getKey() != currentDrivingCar) {
					client.getSoundManager().stop(e.getValue());
					return true;
				}
				return e.getValue().isStopped();
			});

			engineLowSounds.entrySet().removeIf(e -> {
				if (e.getKey() != currentDrivingCar || !e.getKey().hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)) {
					client.getSoundManager().stop(e.getValue());
					return true;
				}
				return e.getValue().isStopped();
			});

			wheelRotationReportTickCounter++;
			if (drivingCar != null && wheelRotationReportTickCounter % WHEEL_ROTATION_REPORT_INTERVAL_TICKS == 0) {
				SetWheelRotationReportPayload mineturismo$payload = new SetWheelRotationReportPayload(
						drivingCar.getId(), drivingCar.getWheelDistanceTraveled());
				ModClientNetworking.sendToServer(SetWheelRotationReportPayload.ID, mineturismo$payload, SetWheelRotationReportPayload::write);
			}

			if (drivingCar != null && HORN_KEY.isDown()
					&& com.examplebett.entity.HornSirenSounds.hornById(drivingCar.getHornSoundId()).sound() != null) {
				if (activeHorn == null || activeHorn.isStopped() || !activeHorn.isFor(drivingCar)) {
					if (activeHorn != null) {
						client.getSoundManager().stop(activeHorn);
					}
					activeHorn = new HornSoundInstance(drivingCar);
					client.getSoundManager().play(activeHorn);
				} else {
					activeHorn.requestPlaying();
				}
			} else if (activeHorn != null) {
				activeHorn.requestRelease();
				if (activeHorn.isStopped()) {
					activeHorn = null;
				}
			}

			boolean hornDown = drivingCar != null && HORN_KEY.isDown();
			if (hornDown) {
				if (hornBroadcastCar != drivingCar) {
					if (hornBroadcastCar != null) {
						SetHornStatePayload mineturismo$payload = new SetHornStatePayload(hornBroadcastCar.getId(), false);
						ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
					}
					SetHornStatePayload mineturismo$payload = new SetHornStatePayload(drivingCar.getId(), true);
					ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
					hornBroadcastCar = drivingCar;
				}
			} else if (hornBroadcastCar != null) {
				SetHornStatePayload mineturismo$payload = new SetHornStatePayload(hornBroadcastCar.getId(), false);
				ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
				hornBroadcastCar = null;
			}

			boolean hornKeyDown = HORN_KEY.isDown();
			if (drivingCar != null && hornKeyDown && !hornKeyWasDown && !drivingCar.getPoliceLightEnabledIds().isEmpty()) {

				boolean newLightsOn;
				boolean newSirenOn;
				if (!drivingCar.isPoliceLightsOn()) {

					newLightsOn = true;
					newSirenOn = true;
				} else if (drivingCar.isPoliceSirenOn()) {

					newLightsOn = true;
					newSirenOn = false;
				} else {

					newLightsOn = false;
					newSirenOn = false;
				}
				drivingCar.setPoliceLightsOn(newLightsOn);
				drivingCar.setPoliceSirenOn(newSirenOn);
				SetPoliceLightsOnPayload mineturismo$payload = new SetPoliceLightsOnPayload(drivingCar.getId(), newLightsOn, newSirenOn);
				ModClientNetworking.sendToServer(SetPoliceLightsOnPayload.ID, mineturismo$payload, SetPoliceLightsOnPayload::write);
			}
			hornKeyWasDown = hornKeyDown;

			if (drivingCar != null && drivingCar.consumeClientSlipFlag()) {
				SetClientSlipReportPayload mineturismo$payload = new SetClientSlipReportPayload(drivingCar.getId());
				ModClientNetworking.sendToServer(SetClientSlipReportPayload.ID, mineturismo$payload, SetClientSlipReportPayload::write);
			}

			if (drivingCar != null && drivingCar.consumeClientWheelieTriggerFlag()) {
				SetClientWheelieTriggerPayload mineturismo$wheeliePayload = new SetClientWheelieTriggerPayload(drivingCar.getId());
				ModClientNetworking.sendToServer(SetClientWheelieTriggerPayload.ID, mineturismo$wheeliePayload, SetClientWheelieTriggerPayload::write);
			}

			boolean mineturismo$sprintDown = drivingCar != null && client.options.keySprint.isDown();
			if (drivingCar != null && (mineturismo$sprintDown != sprintKeyHeldSent || drivingCar != sprintKeyHeldCar)) {
				ModClientNetworking.sendToServer(com.examplebett.network.SetSprintKeyHeldPayload.ID, new com.examplebett.network.SetSprintKeyHeldPayload(drivingCar.getId(), mineturismo$sprintDown), com.examplebett.network.SetSprintKeyHeldPayload::write);
				sprintKeyHeldSent = mineturismo$sprintDown;
				sprintKeyHeldCar = drivingCar;
			} else if (drivingCar == null) {
				sprintKeyHeldSent = false;
				sprintKeyHeldCar = null;
			}

			if (client.level == null) return;

			List<CarEntity> ambientEngineCandidates = new ArrayList<>();
			for (Entity entity : client.level.entitiesForRendering()) {
				if (entity instanceof CarEntity car) {
					if (car.getCompiledModel() == null) {
						if (car.getModelId().startsWith(CarEntity.EXTERNAL_MODEL_PREFIX)) {
							if (externalCarModelLoader != null) {
								try {
									externalCarModelLoader.tryLoad(car);
								} catch (Exception e) {
									AiObjectMod.LOGGER.error("Failed to load external model: " + car.getModelId(), e);
								}
							}
						} else {
							try {
								ResourceLocation glbLocation = AiObjectMod.id("models/" + car.getModelId() + ".glb");

								if (client.getResourceManager().hasResource(glbLocation)) {
									var result = GlbModelLoader.load(client.getResourceManager(), car.getModelId());
									car.setModel(result.model());
									car.setMaterials(result.materials());
									car.setCompiledModel(compiledCarModelCache.computeIfAbsent(car.getModelId(),
											id -> CompiledCarModel.compile(result.model(), result.materials(), CompiledCarModel.FALLBACK_TEXTURE, false, id)));
								} else {
									var model = ObjModelLoader.loadModel(client.getResourceManager(), car.getModelId());
									var materials = ObjModelLoader.loadMaterials(client.getResourceManager(), car.getModelId());
									car.setModel(model);
									car.setMaterials(materials);

									car.setCompiledModel(compiledCarModelCache.computeIfAbsent(car.getModelId(),
											id -> CompiledCarModel.compile(model, materials, CompiledCarModel.FALLBACK_TEXTURE)));
								}
							} catch (Exception e) {
								AiObjectMod.LOGGER.error("Failed to load model: " + car.getModelId(), e);
							}
						}
					}

					CompiledCarModel compiledForWheelDetect = car.getCompiledModel();
					if (compiledForWheelDetect != null && !car.hasDetectedWheelFootprint()
							&& compiledForWheelDetect.wheelPivots.size() == 4) {
						float sumAbsX = 0f;
						float sumFrontZ = 0f;
						int frontCount = 0;
						for (ObjModel.WheelPivot pivot : compiledForWheelDetect.wheelPivots) {
							sumAbsX += Math.abs(pivot.centerX);
							if (pivot.front) {
								sumFrontZ += pivot.centerZ;
								frontCount++;
							}
						}
						if (frontCount > 0) {
							float trackHalfWidth = sumAbsX / compiledForWheelDetect.wheelPivots.size();
							float rearZ = sumFrontZ / frontCount;
							car.setDetectedWheelFootprint(trackHalfWidth, rearZ);

							SetDetectedWheelFootprintPayload mineturismo$payload = new SetDetectedWheelFootprintPayload(car.getId(), trackHalfWidth, rearZ);
							ModClientNetworking.sendToServer(SetDetectedWheelFootprintPayload.ID, mineturismo$payload, SetDetectedWheelFootprintPayload::write);
							AiObjectMod.LOGGER.info("Detected wheel footprint for {}: trackHalfWidth={}, rearZ={}",
									car.getModelId(), trackHalfWidth, rearZ);
						}
					}

					if (car.getWheelAttachment() == null) {
						WheelAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setWheelAttachment);
					}

					com.examplebett.render.WheelAttachment attachment = car.getWheelAttachment();
					if (attachment != null) {

						String override = car.getWheelModelIdOverride();
						String desiredWheelModelId = (override != null && !override.isEmpty())
								? override : attachment.wheelModelId();
						boolean isNoneStyle = com.examplebett.render.WheelAttachment.NONE_STYLE.equals(desiredWheelModelId);

						car.setWheelStyleDefaultWidthScale(isNoneStyle ? 1f
								: WheelStyleCatalog.getDefaultWheelWidthScale(client.getResourceManager(), desiredWheelModelId));

						String colorOverride = car.getWheelColorOverride();
						String desiredWheelColorId = "";
						if (!isNoneStyle) {
							var colorOptions = WheelColorLoader.load(client.getResourceManager(), desiredWheelModelId);
							if (colorOptions.isPresent() && !colorOptions.get().ids().isEmpty()) {
								List<String> colorIds = colorOptions.get().ids();
								desiredWheelColorId = (colorOverride != null && colorIds.contains(colorOverride))
										? colorOverride : colorIds.get(0);
							}
						}
						boolean styleChanged = !desiredWheelModelId.equals(car.getLoadedWheelModelId());
						boolean colorChanged = !desiredWheelColorId.equals(car.getLoadedWheelColorId());
						if (styleChanged || colorChanged) {
							if (isNoneStyle) {

								car.setWheelCompiledModel(null);
								car.setWheelCompiledModelMirrored(null);
								car.setLoadedWheelModelId(desiredWheelModelId);
								car.setLoadedWheelColorId(desiredWheelColorId);
							} else {
								try {

									final String wheelModelIdForCache = desiredWheelModelId;
									final String wheelColorIdForCache = desiredWheelColorId;
									String wheelCacheKey = wheelModelIdForCache + "#" + wheelColorIdForCache;
									WheelCompiledPair compiledPair = compiledWheelModelCache.computeIfAbsent(wheelCacheKey, key -> {

										try {
											var wheelModel = ObjModelLoader.loadModel(client.getResourceManager(), wheelModelIdForCache);
											var wheelMaterials = ObjModelLoader.loadMaterials(client.getResourceManager(), wheelModelIdForCache);
											if (!wheelColorIdForCache.isEmpty()) {

												wheelMaterials = ObjModelLoader.loadLiveryMaterials(
														client.getResourceManager(), wheelModelIdForCache, wheelMaterials, wheelColorIdForCache);
											}

											CompiledCarModel compiled = CompiledCarModel.compile(
													wheelModel, wheelMaterials, CompiledCarModel.FALLBACK_TEXTURE, true);
											return new WheelCompiledPair(compiled, CompiledCarModel.mirrorX(compiled));
										} catch (java.io.IOException wheelIoError) {
											throw new java.io.UncheckedIOException(wheelIoError);
										}
									});
									car.setWheelCompiledModel(compiledPair.normal());
									car.setWheelCompiledModelMirrored(compiledPair.mirrored());
									car.setLoadedWheelModelId(desiredWheelModelId);
									car.setLoadedWheelColorId(desiredWheelColorId);
								} catch (Exception wheelLoadError) {
									AiObjectMod.LOGGER.error("Failed to load wheel model: " + desiredWheelModelId, wheelLoadError);
								}
							}
						}
					}

					if (car.getLightAttachment() == null) {
						com.examplebett.client.render.LightAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setLightAttachment);
					}

					if (car.getCameraAttachment() == null) {
						com.examplebett.client.render.CameraAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setCameraAttachment);
					}

					if (car.getPlateAttachment() == null) {
						com.examplebett.client.render.PlateAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setPlateAttachment);
					}

					if (car.getVehicleCategories() == null) {
						com.examplebett.client.render.VehicleCategoryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setVehicleCategories);
					}

					if (car.getLiveryOptions() == null) {
						com.examplebett.client.render.LiveryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setLiveryOptions);
					}

					com.examplebett.render.LiveryOptions liveryOptions = car.getLiveryOptions();
					if (liveryOptions != null && !liveryOptions.ids().isEmpty() && car.getModel() != null) {
						String liveryOverride = car.getLiveryOverride();
						String desiredLivery = (liveryOverride != null && !liveryOverride.isEmpty())
								? liveryOverride : liveryOptions.ids().get(0);
						if (!desiredLivery.equals(car.getLoadedLiveryId())) {
							try {
								var liveryMaterials = ObjModelLoader.loadLiveryMaterials(
										client.getResourceManager(), car.getModelId(), car.getMaterials(), desiredLivery);
								String liveryCacheKey = car.getModelId() + "#" + desiredLivery;
								var carModel = car.getModel();
								car.setCompiledModel(compiledCarModelCache.computeIfAbsent(liveryCacheKey,
										key -> liveryOptions.isDecal(desiredLivery)
												? CompiledCarModel.compileBodyAndDecal(carModel, liveryMaterials, CompiledCarModel.FALLBACK_TEXTURE, false)
												: CompiledCarModel.compile(carModel, liveryMaterials, CompiledCarModel.FALLBACK_TEXTURE)));
								car.setLoadedLiveryId(desiredLivery);
							} catch (Exception liveryLoadError) {
								AiObjectMod.LOGGER.error("Failed to load livery: " + desiredLivery, liveryLoadError);
							}
						}
					}

					if (car.isTownCar() || car.isRailwayRouteVehicle()) {

						if (client.player != null && !ambientEngineSounds.containsKey(car)
								&& car.distanceToSqr(client.player) <= AMBIENT_ENGINE_RANGE * AMBIENT_ENGINE_RANGE) {
							ambientEngineCandidates.add(car);
						}
					} else {

						car.updateClientSlideIntensity(car == drivingCar);
						TireSlipSoundInstance slipSound = tireSlipSounds.get(car);
						if ((slipSound == null || slipSound.isStopped()) && car.getClientSlideIntensity() > 0.0f) {
							slipSound = new TireSlipSoundInstance(car);
							client.getSoundManager().play(slipSound);
							tireSlipSounds.put(car, slipSound);
						}
					}

					PoliceSirenSoundInstance sirenSound = policeSirenSounds.get(car);
					if (car.isPoliceSirenOn()) {

						if (sirenSound != null && !sirenSound.isStopped() && sirenSound.isSoundOutdated()) {
							sirenSound.requestRelease();
							sirenSound = null;
						}

						boolean sirenHasSound = com.examplebett.entity.HornSirenSounds.sirenById(car.getSirenSoundId()).sound() != null;
						if (sirenHasSound && (sirenSound == null || sirenSound.isReadyForSuccessor())) {
							PoliceSirenSoundInstance successor = new PoliceSirenSoundInstance(car);
							client.getSoundManager().play(successor);
							policeSirenSounds.put(car, successor);
						}
					} else if (sirenSound != null && !sirenSound.isStopped()) {
						sirenSound.requestRelease();
					}
				} else if (entity instanceof BreakableTireStackEntity tireStack) {
					if (tireStack.getCompiledModel() == null) {
						try {
							var model = ObjModelLoader.loadModel(client.getResourceManager(), tireStack.getModelId());
							var materials = ObjModelLoader.loadMaterials(client.getResourceManager(), tireStack.getModelId());
							tireStack.setModel(model);
							tireStack.setMaterials(materials);
							tireStack.setCompiledModel(CompiledCarModel.compile(model, materials, CompiledCarModel.FALLBACK_TEXTURE));
						} catch (Exception e) {
							AiObjectMod.LOGGER.error("Failed to load model: " + tireStack.getModelId(), e);
						}
					}
				}
			}

			tireSlipSounds.entrySet().removeIf(entry -> entry.getValue().isStopped());

			policeSirenSounds.entrySet().removeIf(entry -> entry.getValue().isStopped());

			ambientEngineSounds.entrySet().removeIf(entry -> {
				CarEntity car = entry.getKey();
				boolean outOfRange = client.player == null
						|| car.distanceToSqr(client.player) > AMBIENT_ENGINE_RANGE * AMBIENT_ENGINE_RANGE;
				if (entry.getValue().isStopped() || !car.isAlive() || outOfRange) {
					client.getSoundManager().stop(entry.getValue());
					return true;
				}
				return false;
			});

			for (Map.Entry<CarEntity, EngineSoundInstance> entry : ambientEngineSounds.entrySet()) {
				if (entry.getValue().isReadyForSuccessor()) {
					EngineSoundInstance successor = new EngineSoundInstance(entry.getKey(), entry.getValue().getVolume(), true);
					client.getSoundManager().play(successor);
					entry.setValue(successor);
				}
			}
			while (ambientEngineSounds.size() < MAX_AMBIENT_ENGINE_SOUNDS && !ambientEngineCandidates.isEmpty()) {
				CarEntity picked = ambientEngineCandidates.remove(client.level.random.nextInt(ambientEngineCandidates.size()));
				float volume = (AMBIENT_ENGINE_MIN_VOLUME
						+ client.level.random.nextFloat() * (AMBIENT_ENGINE_MAX_VOLUME - AMBIENT_ENGINE_MIN_VOLUME))
						* ambientEngineVolumeMultiplier;
				EngineSoundInstance ambientSound = new EngineSoundInstance(picked, volume, true);
				client.getSoundManager().play(ambientSound);
				ambientEngineSounds.put(picked, ambientSound);
			}
		});
	}

	private static Component getCarDisplayName(CarEntity car) {
		Item item = MODEL_ID_TO_SPAWN_ITEM.get(car.getModelId());
		Component name = item != null ? new ItemStack(item).getHoverName() : ModText.literal(car.getModelId());
		if (!mountToastCustomFontEnabled) {
			return name;
		}

		return name.copy().withStyle(style -> style.withFont(MOUNT_TOAST_FONT));
	}

	private static Component getCarCategoryText(CarEntity car) {
		java.util.Set<com.examplebett.entity.VehicleCategory> categories = car.getVehicleCategories();
		if (categories == null || categories.isEmpty()) {
			return null;
		}
		java.util.List<com.examplebett.entity.VehicleCategory> sorted = new java.util.ArrayList<>(categories);
		sorted.sort(java.util.Comparator.naturalOrder());
		net.minecraft.network.chat.MutableComponent joined = ModText.literal("");
		for (int i = 0; i < sorted.size(); i++) {
			if (i > 0) {
				joined.append(" / ");
			}
			joined.append(ModText.translatable(sorted.get(i).translationKey));
		}
		if (!mountToastCustomFontEnabled) {
			return joined;
		}
		return joined.withStyle(style -> style.withFont(MOUNT_TOAST_FONT));
	}

	private static boolean mineturismo$canToggleTrailerCoupling(CarEntity drivingCar) {
		if (!drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
			return false;
		}
		return drivingCar.findCoupledTrailer() != null || drivingCar.findCouplableTrailer() != null;
	}

	private static void mineturismo$renderWaypointHint(GuiGraphics guiGraphics) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}
		Item mainHand = client.player.getMainHandItem().getItem();
		Item offHand = client.player.getOffhandItem().getItem();
		Component[] hintLines;
		if (com.examplebett.client.GttClient.isActive()) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.gtt.hud_hint") };
		} else if (client.player.getVehicle() instanceof CarEntity mineturismo$hintCar && mineturismo$canToggleTrailerCoupling(mineturismo$hintCar)) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.trailer.hud_hint", TRAILER_COUPLING_KEY.getTranslatedKeyMessage()) };
		} else if (mainHand instanceof WaypointMarkerItem || offHand instanceof WaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.waypoint_marker.hud_hint") };
		} else if (mainHand instanceof OneWayWaypointMarkerItem || offHand instanceof OneWayWaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.one_way_marker.hud_hint") };
		} else if (mainHand instanceof RailwayWaypointMarkerItem || offHand instanceof RailwayWaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.railway_marker.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.TowRopeItem || offHand instanceof com.examplebett.item.TowRopeItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.tow_rope.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.RailwayStopMarkerItem || offHand instanceof com.examplebett.item.RailwayStopMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.railway_stop_marker.hud_hint") };
		} else if (mainHand instanceof TrafficSignalMarkerItem || offHand instanceof TrafficSignalMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.traffic_signal_marker.hud_hint_edit") };
		} else if (mainHand instanceof com.examplebett.item.SmartDispatchAppItem || offHand instanceof com.examplebett.item.SmartDispatchAppItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.smart_dispatch.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.CustomWrenchItem || offHand instanceof com.examplebett.item.CustomWrenchItem) {

			hintLines = new Component[] { ModText.translatable("gui.mineturismo.custom_wrench.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.WheelWrenchItem || offHand instanceof com.examplebett.item.WheelWrenchItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.wheel_wrench.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.DebugWrenchItem || offHand instanceof com.examplebett.item.DebugWrenchItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.debug_wrench.hud_hint") };
		} else {
			return;
		}
		float scale = 0.85f;
		int padding = 4;
		int lineHeight = 10;
		int maxWidth = 0;
		for (Component line : hintLines) {
			maxWidth = Math.max(maxWidth, client.font.width(line));
		}
		int boxWidth = maxWidth + padding * 2;
		int boxHeight = hintLines.length * lineHeight + padding * 2;
		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(scale, scale, 1.0f);
		int boxX = Math.round(4 / scale);
		int boxY = Math.round(4 / scale);
		guiGraphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xFF000000);
		for (int i = 0; i < hintLines.length; i++) {
			guiGraphics.drawString(client.font, hintLines[i], boxX + padding, boxY + padding + i * lineHeight, 0xFFFFFF00, false);
		}
		guiGraphics.pose().popPose();
	}

	private static void renderMountToast(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (mountToastText == null || mountToastStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - mountToastStartMs;
		if (elapsed >= MOUNT_TOAST_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < MOUNT_TOAST_FADE_IN_MS) {
			alpha = elapsed / (float) MOUNT_TOAST_FADE_IN_MS;
		} else if (elapsed > MOUNT_TOAST_TOTAL_MS - MOUNT_TOAST_FADE_OUT_MS) {
			alpha = (MOUNT_TOAST_TOTAL_MS - elapsed) / (float) MOUNT_TOAST_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));

		int color = ((int) (alpha * 255.0f) << 24) | 0xFFFFFF;

		int x = (int) ((context.guiWidth() - 6) / MOUNT_TOAST_SCALE - mc.font.width(mountToastText));
		int y = (int) ((context.guiHeight() - 34) / MOUNT_TOAST_SCALE);

		context.pose().pushPose();
		context.pose().scale(MOUNT_TOAST_SCALE, MOUNT_TOAST_SCALE, 1.0f);
		context.drawString(mc.font, mountToastText, x, y, color, true);

		if (mountToastCategoryText != null) {
			int categoryX = (int) ((context.guiWidth() - 6) / MOUNT_TOAST_SCALE - mc.font.width(mountToastCategoryText));
			int categoryY = y + mc.font.lineHeight + 2;
			context.drawString(mc.font, mountToastCategoryText, categoryX, categoryY, color, true);
		}
		context.pose().popPose();
	}

	private static void renderSpeedDisplay(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (speedDisplayMode == SpeedDisplayMode.NONE || mc.options.hideGui || mc.player == null) return;

		Entity vehicle = mc.player.getVehicle();
		if (!(vehicle instanceof CarEntity car)) return;

		if (vehicle != lastVehicle) {
			return;
		}

		if (mountToastStartMs >= 0 && System.currentTimeMillis() - mountToastStartMs < MOUNT_TOAST_TOTAL_MS) {
			return;
		}

		double blocksPerTick = com.examplebett.compat.ModVec3.horizontalLength(car.getDeltaMovement());
		double kmh = blocksPerTick * 20.0 * 3.6;
		double displaySpeed = speedDisplayMode == SpeedDisplayMode.MPH ? kmh * 0.621371 : kmh;
		String numberText = String.valueOf(Math.round(displaySpeed));
		String unitText = speedDisplayMode == SpeedDisplayMode.MPH ? "mph" : "km/h";

		int color = 0xFFFFFFFF;
		int rightEdge = context.guiWidth() - 6;
		int nameRowY = context.guiHeight() - 34;

		int numberWidth = Math.round(mc.font.width(numberText) * SPEED_NUMBER_SCALE);
		int unitWidth = Math.round(mc.font.width(unitText) * SPEED_UNIT_SCALE);
		int unitScreenX = rightEdge - unitWidth;
		int numberScreenX = unitScreenX - SPEED_UNIT_GAP - numberWidth;

		int numberHeight = Math.round(mc.font.lineHeight * SPEED_NUMBER_SCALE);
		int unitHeight = Math.round(mc.font.lineHeight * SPEED_UNIT_SCALE);
		int numberScreenY = nameRowY;

		int unitScreenY = nameRowY + (numberHeight - unitHeight);

		context.pose().pushPose();
		context.pose().scale(SPEED_NUMBER_SCALE, SPEED_NUMBER_SCALE, 1.0f);
		context.drawString(mc.font, numberText, Math.round(numberScreenX / SPEED_NUMBER_SCALE), Math.round(numberScreenY / SPEED_NUMBER_SCALE), color, true);
		context.pose().popPose();

		context.pose().pushPose();
		context.pose().scale(SPEED_UNIT_SCALE, SPEED_UNIT_SCALE, 1.0f);
		context.drawString(mc.font, unitText, Math.round(unitScreenX / SPEED_UNIT_SCALE), Math.round(unitScreenY / SPEED_UNIT_SCALE), color, true);
		context.pose().popPose();
	}

	private static void renderPassengerTransportWarning(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (hudWarningText == null || hudWarningStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - hudWarningStartMs;
		if (elapsed >= HUD_WARNING_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < HUD_WARNING_FADE_IN_MS) {
			alpha = elapsed / (float) HUD_WARNING_FADE_IN_MS;
		} else if (elapsed > HUD_WARNING_TOTAL_MS - HUD_WARNING_FADE_OUT_MS) {
			alpha = (HUD_WARNING_TOTAL_MS - elapsed) / (float) HUD_WARNING_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));

		int color = ((int) (alpha * 255.0f) << 24) | 0xFFFFFF;

		context.drawString(mc.font, hudWarningText, 4, 28, color, true);
	}

	private static void renderHintTip(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (!hintsEnabled || hintTipLines == null || hintTipStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - hintTipStartMs;
		if (elapsed >= HINT_TIP_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < HINT_TIP_FADE_IN_MS) {
			alpha = elapsed / (float) HINT_TIP_FADE_IN_MS;
		} else if (elapsed > HINT_TIP_TOTAL_MS - HINT_TIP_FADE_OUT_MS) {
			alpha = (HINT_TIP_TOTAL_MS - elapsed) / (float) HINT_TIP_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));
		int alpha8 = (int) (alpha * 255.0f);

		int textColor = (alpha8 << 24) | 0xFFFFFF;
		int backgroundColor = alpha8 << 24;

		List<String> rows = new ArrayList<>();
		for (Component line : hintTipLines) {
			for (String row : line.getString().split("\n", -1)) {
				rows.add(row);
			}
		}
		int maxWidth = 0;
		for (String row : rows) {
			maxWidth = Math.max(maxWidth, mc.font.width(row));
		}
		int lineHeight = mc.font.lineHeight;
		int padding = 4;
		int boxWidth = maxWidth + padding * 2;
		int boxHeight = rows.size() * lineHeight + padding * 2;

		int screenX = 4;
		int screenY = 4;
		context.pose().pushPose();
		context.pose().scale(HINT_TIP_SCALE, HINT_TIP_SCALE, 1.0f);
		int x = Math.round(screenX / HINT_TIP_SCALE);
		int y = Math.round(screenY / HINT_TIP_SCALE);
		context.fill(x, y, x + boxWidth, y + boxHeight, backgroundColor);
		for (int i = 0; i < rows.size(); i++) {
			context.drawString(mc.font, rows.get(i), x + padding, y + padding + i * lineHeight, textColor, true);
		}
		context.pose().popPose();

		Component disableHint = ModText.translatable("gui.mineturismo.hint_tip.disable_instruction",
				ModText.translatable("item.mineturismo.debug_wrench"),
				ModText.translatable("gui.mineturismo.debug_wrench_menu.other"),
				ModText.translatable("gui.mineturismo.debug_wrench_menu.hide_hints"));
		int subLineHeight = mc.font.lineHeight;
		int subBoxWidth = mc.font.width(disableHint) + padding * 2;
		int subBoxHeight = subLineHeight + padding * 2;
		int subScreenY = screenY + Math.round(boxHeight * HINT_TIP_SCALE);
		context.pose().pushPose();
		context.pose().scale(HINT_TIP_SUB_SCALE, HINT_TIP_SUB_SCALE, 1.0f);
		int subX = Math.round(screenX / HINT_TIP_SUB_SCALE);
		int subY = Math.round(subScreenY / HINT_TIP_SUB_SCALE);
		context.fill(subX, subY, subX + subBoxWidth, subY + subBoxHeight, backgroundColor);
		context.drawString(mc.font, disableHint, subX + padding, subY + padding, textColor, true);
		context.pose().popPose();
	}
}
*/
//?}
//? if <1.16.2 && >=1.16 {
/*
package com.examplebett.client;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.block.AiObjectModBlocks;
import com.examplebett.client.gui.CarWrenchScreen;
import com.examplebett.client.gui.RailwayConsistScreen;
import com.examplebett.client.gui.WheelAdjustScreen;
import com.examplebett.client.gui.OneWayMarkerSettingsScreen;
import com.examplebett.client.gui.TrafficSignalSettingsScreen;
import com.examplebett.client.gui.WaypointTrafficSettingsScreen;
import com.examplebett.entity.AiObjectModEntities;
import com.examplebett.entity.BreakableTireStackEntity;
import com.examplebett.entity.BreakableBoxEntity;
import com.examplebett.entity.BreakableConeEntity;
import com.examplebett.entity.CarEntity;
import com.examplebett.client.render.BrakeCaliperModel;
import com.examplebett.client.render.BreakableTireStackRenderer;
import com.examplebett.client.render.CameraAttachmentLoader;
import com.examplebett.client.render.CarEntityRenderer;
import com.examplebett.client.render.GlbModelLoader;
import com.examplebett.client.render.LightAttachmentLoader;
import com.examplebett.client.render.LiveryLoader;
import com.examplebett.client.render.LodWheelModel;
import com.examplebett.client.render.PlateAttachmentLoader;
import com.examplebett.client.render.PoliceLightModel;
import com.examplebett.client.render.RearWingModel;
import com.examplebett.client.render.OtherPartModel;
import com.examplebett.client.render.TaxiSignModel;
import com.examplebett.client.render.WheelAttachmentLoader;
import com.examplebett.client.render.WheelColorLoader;
import com.examplebett.client.render.WheelStyleCatalog;
import com.examplebett.client.sound.EngineSoundInstance;
import com.examplebett.client.sound.HornSoundInstance;
import com.examplebett.client.sound.PoliceSirenSoundInstance;
import com.examplebett.client.sound.TireSlipSoundInstance;
import com.examplebett.item.CarSpawnItem;
import com.examplebett.item.OneWayWaypointMarkerItem;
import com.examplebett.item.RailwayWaypointMarkerItem;
import com.examplebett.item.TrafficSignalMarkerItem;
import com.examplebett.item.WaypointMarkerItem;
import com.examplebett.network.OpenRailwaySpawnSetupPayload;
import com.examplebett.network.OpenRailwayConsistSettingsPayload;
import com.examplebett.network.OpenSignalSettingsPayload;
import com.examplebett.network.PlayCollisionSoundPayload;
import com.examplebett.network.SetDetectedWheelFootprintPayload;
import com.examplebett.network.SetClientSlipReportPayload;
import com.examplebett.network.SetClientWheelieTriggerPayload;
import com.examplebett.network.SetWheelRotationReportPayload;
import com.examplebett.network.ShowHudWarningPayload;
import com.examplebett.network.SyncAmbientEngineVolumePayload;
import com.examplebett.network.SyncRailwayFormationOriginPayload;
import com.examplebett.network.SyncWheelDistancePayload;
import com.examplebett.waypoint.WaypointNetworkSavedData;
import com.examplebett.network.SetHornStatePayload;
import com.examplebett.network.ToggleTrailerCouplingPayload;
import com.examplebett.network.SetPoliceLightsOnPayload;
import com.examplebett.render.CompiledCarModel;
import com.examplebett.render.CustomPartCatalog;
import com.examplebett.render.FrontBrakeCaliperParts;
import com.examplebett.render.ObjModel;
import com.examplebett.render.ObjModelLoader;
import com.examplebett.render.OtherParts;
import com.examplebett.render.PoliceLightParts;
import com.examplebett.render.RearBrakeCaliperParts;
import com.examplebett.render.RearWingParts;
import com.examplebett.render.TaxiSignParts;
import com.examplebett.sound.AiObjectModSounds;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import com.examplebett.compat.ModRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiObjectModClient implements ClientModInitializer {

	public interface ExternalCarModelLoader {
		void tryLoad(CarEntity car);
	}

	public static ExternalCarModelLoader externalCarModelLoader = null;

	private static final long MOUNT_TOAST_TOTAL_MS = 3000L;
	private static final long MOUNT_TOAST_FADE_IN_MS = 300L;
	private static final long MOUNT_TOAST_FADE_OUT_MS = 700L;
	private static final ResourceLocation MOUNT_TOAST_FONT = AiObjectMod.id("mount_toast");
	private static final float MOUNT_TOAST_SCALE = 1.5f;

	private static boolean mountToastCustomFontEnabled = true;

	public static boolean isMountToastCustomFontEnabled() {
		return mountToastCustomFontEnabled;
	}

	public static void setMountToastCustomFontEnabled(boolean enabled) {
		mountToastCustomFontEnabled = enabled;
	}

	private static boolean fakeReflectionEnabled = false;

	public static boolean isFakeReflectionEnabled() {
		return fakeReflectionEnabled;
	}

	public static void setFakeReflectionEnabled(boolean enabled) {
		fakeReflectionEnabled = enabled;
	}

	private static boolean hintsEnabled = true;

	public static boolean isHintsEnabled() {
		return hintsEnabled;
	}

	public static void setHintsEnabled(boolean enabled) {
		hintsEnabled = enabled;
	}

	private static boolean fpsLogEnabled = false;

	public static boolean isFpsLogEnabled() {
		return fpsLogEnabled;
	}

	public static void setFpsLogEnabled(boolean enabled) {
		fpsLogEnabled = enabled;
	}

	private static final long FPS_LOG_INTERVAL_TICKS = 20L;
	private static long fpsLogTicks = 0L;

	private static final java.lang.reflect.Field LEGACY_FPS_FIELD = resolveLegacyFpsField();

	private static java.lang.reflect.Field resolveLegacyFpsField() {
		try {
			java.lang.reflect.Field field = com.examplebett.compat.ModReflection.findField(net.minecraft.client.Minecraft.class, "fps", "field_1738");
			field.setAccessible(true);
			return field;
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}

	private static int getGameFps(net.minecraft.client.Minecraft client) {
		if (LEGACY_FPS_FIELD == null) {
			return -1;
		}
		try {
			return LEGACY_FPS_FIELD.getInt(null);
		} catch (ReflectiveOperationException e) {
			return -1;
		}
	}

	public enum SpeedDisplayMode {
		NONE, KMH, MPH;

		public SpeedDisplayMode next() {
			return switch (this) {
				case NONE -> KMH;
				case KMH -> MPH;
				case MPH -> NONE;
			};
		}
	}

	private static SpeedDisplayMode speedDisplayMode = SpeedDisplayMode.NONE;
	private static final float SPEED_NUMBER_SCALE = 2.0f;
	private static final float SPEED_UNIT_SCALE = 1.0f;
	private static final int SPEED_UNIT_GAP = 3;

	public static SpeedDisplayMode getSpeedDisplayMode() {
		return speedDisplayMode;
	}

	public static void setSpeedDisplayMode(SpeedDisplayMode mode) {
		speedDisplayMode = mode;
	}
	private static final Map<String, Item> MODEL_ID_TO_SPAWN_ITEM = new HashMap<>();

	private static final Map<String, CompiledCarModel> compiledCarModelCache = new HashMap<>();

	private static final Map<String, WheelCompiledPair> compiledWheelModelCache = new HashMap<>();
	private record WheelCompiledPair(CompiledCarModel normal, CompiledCarModel mirrored) {
	}
	private static Entity lastVehicle = null;
	private static Component mountToastText = null;

	private static Component mountToastCategoryText = null;
	private static long mountToastStartMs = -1L;

	private static final long HUD_WARNING_TOTAL_MS = 3000L;
	private static final long HUD_WARNING_FADE_IN_MS = 200L;
	private static final long HUD_WARNING_FADE_OUT_MS = 700L;
	private static Component hudWarningText = null;
	private static long hudWarningStartMs = -1L;

	private record TimedHint(long delayTicks, String... lineKeys) {
	}

	private static final TimedHint[] TIMED_HINTS = {
			new TimedHint(1200L,
					"gui.mineturismo.reflection_tip.line1",
					"gui.mineturismo.reflection_tip.line2",
					"gui.mineturismo.reflection_tip.line3"),
			new TimedHint(1360L,
					"gui.mineturismo.shader_tip.line1",
					"gui.mineturismo.shader_tip.line2"),
			new TimedHint(3600L,
					"gui.mineturismo.smart_dispatch_tip.line1",
					"gui.mineturismo.smart_dispatch_tip.line2"),
			new TimedHint(6000L,
					"gui.mineturismo.wrench_tip.line1",
					"gui.mineturismo.wrench_tip.line2"),
			new TimedHint(12000L,
					"gui.mineturismo.traffic_marker_tip.line1",
					"gui.mineturismo.traffic_marker_tip.line2"),
	};

	private static final long HINT_TIP_TOTAL_MS = 8000L;
	private static final long HINT_TIP_FADE_IN_MS = 300L;
	private static final long HINT_TIP_FADE_OUT_MS = 700L;
	private static final float HINT_TIP_SCALE = 1.1f;

	private static final float HINT_TIP_SUB_SCALE = 0.75f;
	private static int nextHintIndex = 0;
	private static long hintTicks = 0L;
	private static Component[] hintTipLines = null;
	private static long hintTipStartMs = -1L;
	private static boolean creditsScreenChecked = false;

	private static final Map<CarEntity, EngineSoundInstance> engineSounds = new HashMap<>();

	private static final Map<CarEntity, EngineSoundInstance> engineLowSounds = new HashMap<>();

	private static final int WHEEL_ROTATION_REPORT_INTERVAL_TICKS = 2;
	private static int wheelRotationReportTickCounter = 0;

	private static final Map<CarEntity, TireSlipSoundInstance> tireSlipSounds = new HashMap<>();

	private static final Map<CarEntity, PoliceSirenSoundInstance> policeSirenSounds = new HashMap<>();

	private static final Map<CarEntity, EngineSoundInstance> ambientEngineSounds = new HashMap<>();
	private static final double AMBIENT_ENGINE_RANGE = 24.0;
	private static final int MAX_AMBIENT_ENGINE_SOUNDS = 3;
	private static final float AMBIENT_ENGINE_MIN_VOLUME = 0.2f;
	private static final float AMBIENT_ENGINE_MAX_VOLUME = 0.4f;

	private static float ambientEngineVolumeMultiplier = WaypointNetworkSavedData.DEFAULT_AMBIENT_ENGINE_VOLUME_MULTIPLIER;

	private static final String KEY_CATEGORY = "key.categories.mineturismo";
	private static final KeyMapping HORN_KEY = new KeyMapping(
			"key.mineturismo.horn", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, KEY_CATEGORY);

	private static final KeyMapping TRAILER_COUPLING_KEY = new KeyMapping(
			"key.mineturismo.trailer_coupling", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_L, KEY_CATEGORY);

	public static KeyMapping hornKey() {
		return HORN_KEY;
	}

	public static KeyMapping trailerCouplingKey() {
		return TRAILER_COUPLING_KEY;
	}

	private static HornSoundInstance activeHorn = null;

	private static CarEntity hornBroadcastCar = null;

	private static boolean hornKeyWasDown = false;

	private static boolean trailerCouplingKeyWasDown = false;

	private static boolean sprintKeyHeldSent = false;
	private static CarEntity sprintKeyHeldCar = null;

	private static void mineturismo$handlePlayCollisionSound(PlayCollisionSoundPayload payload) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			return;
		}

		net.minecraft.sounds.SoundEvent collisionSound = payload.lowSpeed() ? AiObjectModSounds.CAR_COLLISION_LOW : AiObjectModSounds.CAR_COLLISION;

		SimpleSoundInstance sound = new SimpleSoundInstance(
				collisionSound.getLocation(), SoundSource.NEUTRAL,
				payload.volume(), payload.pitch(),
				false, 0, SoundInstance.Attenuation.NONE,
				payload.x(), payload.y(), payload.z(), false);
		mc.getSoundManager().play(sound);
	}

	private static void mineturismo$handleShowHudWarning(ShowHudWarningPayload payload) {
		hudWarningText = ModText.translatable(payload.messageKey());
		hudWarningStartMs = System.currentTimeMillis();
		Minecraft.getInstance().getSoundManager().play(
				SimpleSoundInstance.forUI(AiObjectModSounds.PASSENGER_TRANSPORT_DENIED, 1.0f));
	}

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.CAR_ENTITY, (dispatcher, context) -> new CarEntityRenderer(dispatcher));
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.BREAKABLE_TIRE_STACK, (dispatcher, context) -> new BreakableTireStackRenderer<BreakableTireStackEntity>(dispatcher));
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.BREAKABLE_BOX, (dispatcher, context) -> new BreakableTireStackRenderer<BreakableBoxEntity>(dispatcher));
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.BREAKABLE_BOX_2, (dispatcher, context) -> new BreakableTireStackRenderer<BreakableBoxEntity>(dispatcher));
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.TRAFFIC_CONE, (dispatcher, context) -> new BreakableTireStackRenderer<BreakableConeEntity>(dispatcher));
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.BARRICADE, (dispatcher, context) -> new BreakableTireStackRenderer<com.examplebett.entity.BreakableBarricadeEntity>(dispatcher));

		KeyBindingHelper.registerKeyBinding(HORN_KEY);
		KeyBindingHelper.registerKeyBinding(TRAILER_COUPLING_KEY);

		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
				new SimpleSynchronousResourceReloadListener() {
					@Override
					public ResourceLocation getFabricId() {
						return AiObjectMod.id("clear_render_caches");
					}

					@Override
					public void onResourceManagerReload(ResourceManager resourceManager) {
						ObjModelLoader.clearCache();
						GlbModelLoader.clearCache();
						WheelAttachmentLoader.clearCache();
						WheelColorLoader.clearCache();
						LightAttachmentLoader.clearCache();
						com.examplebett.client.render.VehicleCategoryLoader.clearCache();
						PlateAttachmentLoader.clearCache();
						CameraAttachmentLoader.clearCache();
						LodWheelModel.clearCache();
						LiveryLoader.clearCache();
						RearWingModel.clearCache();
						PoliceLightModel.clearCache();
						TaxiSignModel.clearCache();
						OtherPartModel.clearCache();
						BrakeCaliperModel.clearCache();
						CustomPartCatalog.clearCache();
						WheelStyleCatalog.clearCache();
						RearWingParts.clearCache();
						OtherParts.clearCache();
						PoliceLightParts.clearCache();
						TaxiSignParts.clearCache();
						FrontBrakeCaliperParts.clearCache();
						RearBrakeCaliperParts.clearCache();
						compiledCarModelCache.clear();
						compiledWheelModelCache.clear();
					}
				});

		ModClientNetworking.registerS2C(PlayCollisionSoundPayload.ID, PlayCollisionSoundPayload::read, AiObjectModClient::mineturismo$handlePlayCollisionSound);

		ModClientNetworking.registerS2C(SyncWheelDistancePayload.ID, SyncWheelDistancePayload::read, payload -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc.level == null) {
				return;
			}
			if (mc.level.getEntity(payload.entityId()) instanceof CarEntity car) {
				car.setNetworkWheelDistance(payload.distance());
			}
		});

		ModClientNetworking.registerS2C(OpenRailwayConsistSettingsPayload.ID, OpenRailwayConsistSettingsPayload::read, payload -> {
			net.minecraft.client.multiplayer.ClientLevel mineturismo$level = Minecraft.getInstance().level;
			if (mineturismo$level != null && mineturismo$level.getEntity(payload.headEntityId()) instanceof CarEntity car) {
				Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
				Minecraft.getInstance().setScreen(new RailwayConsistScreen(car, null));
			}
		});

		ModClientNetworking.registerS2C(OpenSignalSettingsPayload.ID, OpenSignalSettingsPayload::read, payload -> {
			char direction = payload.direction().isEmpty() ? 'N' : payload.direction().charAt(0);
			TrafficSignalSettingsScreen.applyExternalConfig(payload.signalId(), payload.intersectionName(), direction,
					payload.greenTicks(), payload.redTicks(), payload.phaseOffsetTicks());
			Minecraft.getInstance().setScreen(new TrafficSignalSettingsScreen());
		});

		ModClientNetworking.registerS2C(OpenRailwaySpawnSetupPayload.ID, OpenRailwaySpawnSetupPayload::read, payload -> {
			Minecraft.getInstance().setScreen(new com.examplebett.client.gui.RailwaySpawnVehicleScreen(payload.originNodeId()));
		});

		ModClientNetworking.registerS2C(SyncRailwayFormationOriginPayload.ID, SyncRailwayFormationOriginPayload::read, payload -> {
			com.examplebett.inventory.RailwayFormationMenu.pendingOriginNodeId = payload.originNodeId();
		});

		ModClientNetworking.registerS2C(SyncAmbientEngineVolumePayload.ID, SyncAmbientEngineVolumePayload::read, payload -> {
			ambientEngineVolumeMultiplier = payload.multiplier();
			com.examplebett.client.gui.WaypointClientSettings.apply(payload.multiplier(), payload.settings());
		});

		ModClientNetworking.registerS2C(ShowHudWarningPayload.ID, ShowHudWarningPayload::read, AiObjectModClient::mineturismo$handleShowHudWarning);

		BlockRenderLayerMap.INSTANCE.putBlock(AiObjectModBlocks.CAR_BREAKABLE_GLASS, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(AiObjectModBlocks.WATER_PUDDLE, RenderType.translucent());

		CarEntity.wrenchMenuOpener = (car, player) -> {
			Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
			Minecraft.getInstance().setScreen(new CarWrenchScreen(car));
		};
		CarEntity.wheelWrenchMenuOpener = (car, player) -> {
			Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
			Minecraft.getInstance().setScreen(new WheelAdjustScreen(car));
		};
		CarEntity.debugWrenchMenuOpener = (car, player) -> Minecraft.getInstance().setScreen(new com.examplebett.client.gui.DebugWrenchScreen(car));
		WaypointMarkerItem.trafficSettingsMenuOpener = player -> Minecraft.getInstance().setScreen(new WaypointTrafficSettingsScreen());
		OneWayWaypointMarkerItem.settingsMenuOpener = player -> Minecraft.getInstance().setScreen(new OneWayMarkerSettingsScreen());
		com.examplebett.item.SmartDispatchAppItem.appScreenOpener = player -> Minecraft.getInstance().setScreen(new com.examplebett.client.gui.SmartDispatchAppScreen());
		ClientTickEvents.END_CLIENT_TICK.register(com.examplebett.client.GttClient::tick);
		ClientTickEvents.END_CLIENT_TICK.register(com.examplebett.client.sound.ChorusMusicPlayer::tick);

		ModClientNetworking.registerS2C(com.examplebett.network.GttScorePayload.ID, com.examplebett.network.GttScorePayload::read, payload -> com.examplebett.client.GttClient.setScore(payload.score()));

		ModClientNetworking.registerS2C(com.examplebett.network.SpawnLegacyEntityPayload.ID, com.examplebett.network.SpawnLegacyEntityPayload::read, payload -> {
			Minecraft mineturismo$mc = Minecraft.getInstance();
			if (mineturismo$mc.level == null) {
				return;
			}
			net.minecraft.world.entity.Entity mineturismo$entity = com.examplebett.compat.ModRegistries.ENTITY_TYPE.get(payload.typeId).create(mineturismo$mc.level);
			if (mineturismo$entity == null) {
				return;
			}
			mineturismo$entity.setPacketCoordinates(payload.x, payload.y, payload.z);
			mineturismo$entity.moveTo(payload.x, payload.y, payload.z, payload.yRot, 0.0f);
			mineturismo$entity.setId(payload.entityId);
			mineturismo$entity.setUUID(payload.uuid);
			mineturismo$mc.level.putNonPlayerEntity(payload.entityId, mineturismo$entity);
		});

		ModClientNetworking.registerS2C(com.examplebett.network.SlotSpinResultPayload.ID, com.examplebett.network.SlotSpinResultPayload::read, payload -> com.examplebett.client.gui.SmartDispatchAppScreen.onSlotResult(payload.prizes()));

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> com.examplebett.client.GttClient.renderHud(new GuiGraphics(poseStack)));

		net.minecraft.client.gui.screens.MenuScreens.register(com.examplebett.inventory.AiObjectModMenus.RAILWAY_FORMATION_MENU_TYPE, com.examplebett.client.gui.RailwayFormationScreen::new);
		net.minecraft.client.gui.screens.MenuScreens.register(com.examplebett.inventory.AiObjectModMenus.MASSIVE_AUTO_APPRAISAL_MENU_TYPE, com.examplebett.client.gui.MassiveAutoAppraisalScreen::new);

		HudRenderCallback.EVENT.register((poseStack, deltaTracker) -> mineturismo$renderWaypointHint(new GuiGraphics(poseStack)));

		CarEntity.clientDescendKeyHeld = () -> Minecraft.getInstance().options.keySprint.isDown();

		CarEntity.clientForwardKeyHeld = () -> Minecraft.getInstance().options.keyUp.isDown();
		CarEntity.clientBackKeyHeld = () -> Minecraft.getInstance().options.keyDown.isDown();

		CarEntity.clientDimensionsResourceManager = () -> {
			var integratedServer = Minecraft.getInstance().getSingleplayerServer();
			return integratedServer != null ? com.examplebett.compat.ModResources.getServerResourceManager(integratedServer) : null;
		};

		for (Item item : ModRegistries.ITEM) {
			if (item instanceof CarSpawnItem spawnItem) {
				MODEL_ID_TO_SPAWN_ITEM.putIfAbsent(spawnItem.getModelId(), item);
			}
		}

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> renderMountToast(new GuiGraphics(poseStack)));

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> renderSpeedDisplay(new GuiGraphics(poseStack)));

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> renderPassengerTransportWarning(new GuiGraphics(poseStack)));

		HudRenderCallback.EVENT.register((poseStack, tickCounter) -> renderHintTip(new GuiGraphics(poseStack)));

		final boolean[] mineturismo$wasInWorld = {false};
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.level != null && !mineturismo$wasInWorld[0]) {
				nextHintIndex = 0;
				hintTicks = 0L;
				hintTipStartMs = -1L;
			}
			mineturismo$wasInWorld[0] = client.level != null;
		});

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			boolean mineturismo$pressed = false;
			while (TRAILER_COUPLING_KEY.consumeClick()) {
				mineturismo$pressed = true;
			}

			InputConstants.Key mineturismo$boundKey = KeyBindingHelper.getBoundKeyOf(TRAILER_COUPLING_KEY);
			boolean mineturismo$rawDown = mineturismo$boundKey.getType() == InputConstants.Type.KEYSYM
					&& InputConstants.isKeyDown(client.getWindow().getWindow(), mineturismo$boundKey.getValue());
			if (mineturismo$rawDown && !trailerCouplingKeyWasDown && client.screen == null) {
				mineturismo$pressed = true;
			}
			trailerCouplingKeyWasDown = mineturismo$rawDown;

			if (client.player != null && client.screen == null
					&& client.player.getVehicle() instanceof CarEntity mineturismo$headCar
					&& mineturismo$headCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
				for (KeyMapping mineturismo$other : client.options.keyMappings) {
					if (mineturismo$other != TRAILER_COUPLING_KEY && mineturismo$other.same(TRAILER_COUPLING_KEY)) {
						while (mineturismo$other.consumeClick()) {
							mineturismo$pressed = true;
						}
					}
				}
			}
			if (!mineturismo$pressed || client.player == null || client.screen != null
					|| !(client.player.getVehicle() instanceof CarEntity mineturismo$car)
					|| !mineturismo$car.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
				return;
			}
			for (KeyMapping mineturismo$other : client.options.keyMappings) {
				if (mineturismo$other != TRAILER_COUPLING_KEY && mineturismo$other.same(TRAILER_COUPLING_KEY)) {
					while (mineturismo$other.consumeClick()) {

					}
				}
			}
			ToggleTrailerCouplingPayload mineturismo$payload = new ToggleTrailerCouplingPayload(mineturismo$car.getId());
			ModClientNetworking.sendToServer(ToggleTrailerCouplingPayload.ID, mineturismo$payload, ToggleTrailerCouplingPayload::write);
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (creditsScreenChecked) return;
			if (client.player == null || client.level == null) return;
			creditsScreenChecked = true;
			com.examplebett.client.gui.CreditsScreen.showOnceIfNeeded();
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!fpsLogEnabled) return;
			fpsLogTicks++;
			if (fpsLogTicks % FPS_LOG_INTERVAL_TICKS != 0) return;
			int fps = getGameFps(client);
			int carCount = 0;
			if (client.level != null) {
				for (Entity entity : client.level.entitiesForRendering()) {
					if (entity instanceof CarEntity) {
						carCount++;
					}
				}
			}
			AiObjectMod.LOGGER.info("[fps-log] fps={} frameTimeMs={} carCount={}", fps,
					fps > 0 ? String.format("%.1f", 1000.0 / fps) : "n/a", carCount);
		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (hintsEnabled && client.player != null) {
				hintTicks++;
				while (nextHintIndex < TIMED_HINTS.length && hintTicks >= TIMED_HINTS[nextHintIndex].delayTicks()) {
					TimedHint hint = TIMED_HINTS[nextHintIndex];
					hintTipLines = new Component[hint.lineKeys().length];
					for (int i = 0; i < hint.lineKeys().length; i++) {
						hintTipLines[i] = ModText.translatable(hint.lineKeys()[i]);
					}
					hintTipStartMs = System.currentTimeMillis();
					client.getSoundManager().play(
							SimpleSoundInstance.forUI(AiObjectModSounds.PASSENGER_TRANSPORT_DENIED, 1.0f));
					nextHintIndex++;
				}
			}

			CarEntity drivingCar = null;
			if (client.player != null) {
				Entity vehicle = client.player.getVehicle();
				if (vehicle instanceof CarEntity car) {
					drivingCar = car;
				}
				if (vehicle instanceof CarEntity car && vehicle != lastVehicle) {
					mountToastText = getCarDisplayName(car);
					if (car.getVehicleCategories() == null) {
						com.examplebett.client.render.VehicleCategoryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setVehicleCategories);
					}
					mountToastCategoryText = getCarCategoryText(car);
					mountToastStartMs = System.currentTimeMillis();
				}
				lastVehicle = vehicle;
			}

			if (drivingCar != null) {
				EngineSoundInstance existing = engineSounds.get(drivingCar);

				if (existing != null && !existing.matchesCurrentSoundId()) {
					client.getSoundManager().stop(existing);
					existing = null;
				}

				if (existing == null || existing.isReadyForSuccessor()) {
					EngineSoundInstance sound = new EngineSoundInstance(drivingCar, 1.0f, false,
							drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)
									? EngineSoundInstance.Layer.HYBRID_HIGH : EngineSoundInstance.Layer.NORMAL);
					client.getSoundManager().play(sound);
					engineSounds.put(drivingCar, sound);
				}

				if (drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)) {
					EngineSoundInstance lowExisting = engineLowSounds.get(drivingCar);
					if (lowExisting == null || lowExisting.isReadyForSuccessor()) {
						EngineSoundInstance lowSound = new EngineSoundInstance(drivingCar, 1.0f, false, EngineSoundInstance.Layer.HYBRID_LOW);
						client.getSoundManager().play(lowSound);
						engineLowSounds.put(drivingCar, lowSound);
					}
				}
			}

			CarEntity currentDrivingCar = drivingCar;
			engineSounds.entrySet().removeIf(e -> {
				if (e.getKey() != currentDrivingCar) {
					client.getSoundManager().stop(e.getValue());
					return true;
				}
				return e.getValue().isStopped();
			});

			engineLowSounds.entrySet().removeIf(e -> {
				if (e.getKey() != currentDrivingCar || !e.getKey().hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)) {
					client.getSoundManager().stop(e.getValue());
					return true;
				}
				return e.getValue().isStopped();
			});

			wheelRotationReportTickCounter++;
			if (drivingCar != null && wheelRotationReportTickCounter % WHEEL_ROTATION_REPORT_INTERVAL_TICKS == 0) {
				SetWheelRotationReportPayload mineturismo$payload = new SetWheelRotationReportPayload(
						drivingCar.getId(), drivingCar.getWheelDistanceTraveled());
				ModClientNetworking.sendToServer(SetWheelRotationReportPayload.ID, mineturismo$payload, SetWheelRotationReportPayload::write);
			}

			if (drivingCar != null && HORN_KEY.isDown()
					&& com.examplebett.entity.HornSirenSounds.hornById(drivingCar.getHornSoundId()).sound() != null) {
				if (activeHorn == null || activeHorn.isStopped() || !activeHorn.isFor(drivingCar)) {
					if (activeHorn != null) {
						client.getSoundManager().stop(activeHorn);
					}
					activeHorn = new HornSoundInstance(drivingCar);
					client.getSoundManager().play(activeHorn);
				} else {
					activeHorn.requestPlaying();
				}
			} else if (activeHorn != null) {
				activeHorn.requestRelease();
				if (activeHorn.isStopped()) {
					activeHorn = null;
				}
			}

			boolean hornDown = drivingCar != null && HORN_KEY.isDown();
			if (hornDown) {
				if (hornBroadcastCar != drivingCar) {
					if (hornBroadcastCar != null) {
						SetHornStatePayload mineturismo$payload = new SetHornStatePayload(hornBroadcastCar.getId(), false);
						ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
					}
					SetHornStatePayload mineturismo$payload = new SetHornStatePayload(drivingCar.getId(), true);
					ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
					hornBroadcastCar = drivingCar;
				}
			} else if (hornBroadcastCar != null) {
				SetHornStatePayload mineturismo$payload = new SetHornStatePayload(hornBroadcastCar.getId(), false);
				ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
				hornBroadcastCar = null;
			}

			boolean hornKeyDown = HORN_KEY.isDown();
			if (drivingCar != null && hornKeyDown && !hornKeyWasDown && !drivingCar.getPoliceLightEnabledIds().isEmpty()) {

				boolean newLightsOn;
				boolean newSirenOn;
				if (!drivingCar.isPoliceLightsOn()) {

					newLightsOn = true;
					newSirenOn = true;
				} else if (drivingCar.isPoliceSirenOn()) {

					newLightsOn = true;
					newSirenOn = false;
				} else {

					newLightsOn = false;
					newSirenOn = false;
				}
				drivingCar.setPoliceLightsOn(newLightsOn);
				drivingCar.setPoliceSirenOn(newSirenOn);
				SetPoliceLightsOnPayload mineturismo$payload = new SetPoliceLightsOnPayload(drivingCar.getId(), newLightsOn, newSirenOn);
				ModClientNetworking.sendToServer(SetPoliceLightsOnPayload.ID, mineturismo$payload, SetPoliceLightsOnPayload::write);
			}
			hornKeyWasDown = hornKeyDown;

			if (drivingCar != null && drivingCar.consumeClientSlipFlag()) {
				SetClientSlipReportPayload mineturismo$payload = new SetClientSlipReportPayload(drivingCar.getId());
				ModClientNetworking.sendToServer(SetClientSlipReportPayload.ID, mineturismo$payload, SetClientSlipReportPayload::write);
			}

			if (drivingCar != null && drivingCar.consumeClientWheelieTriggerFlag()) {
				SetClientWheelieTriggerPayload mineturismo$wheeliePayload = new SetClientWheelieTriggerPayload(drivingCar.getId());
				ModClientNetworking.sendToServer(SetClientWheelieTriggerPayload.ID, mineturismo$wheeliePayload, SetClientWheelieTriggerPayload::write);
			}

			boolean mineturismo$sprintDown = drivingCar != null && client.options.keySprint.isDown();
			if (drivingCar != null && (mineturismo$sprintDown != sprintKeyHeldSent || drivingCar != sprintKeyHeldCar)) {
				ModClientNetworking.sendToServer(com.examplebett.network.SetSprintKeyHeldPayload.ID, new com.examplebett.network.SetSprintKeyHeldPayload(drivingCar.getId(), mineturismo$sprintDown), com.examplebett.network.SetSprintKeyHeldPayload::write);
				sprintKeyHeldSent = mineturismo$sprintDown;
				sprintKeyHeldCar = drivingCar;
			} else if (drivingCar == null) {
				sprintKeyHeldSent = false;
				sprintKeyHeldCar = null;
			}

			if (client.level == null) return;

			List<CarEntity> ambientEngineCandidates = new ArrayList<>();
			for (Entity entity : client.level.entitiesForRendering()) {
				if (entity instanceof CarEntity car) {
					if (car.getCompiledModel() == null) {
						if (car.getModelId().startsWith(CarEntity.EXTERNAL_MODEL_PREFIX)) {
							if (externalCarModelLoader != null) {
								try {
									externalCarModelLoader.tryLoad(car);
								} catch (Exception e) {
									AiObjectMod.LOGGER.error("Failed to load external model: " + car.getModelId(), e);
								}
							}
						} else {
							try {
								ResourceLocation glbLocation = AiObjectMod.id("models/" + car.getModelId() + ".glb");

								if (client.getResourceManager().hasResource(glbLocation)) {
									var result = GlbModelLoader.load(client.getResourceManager(), car.getModelId());
									car.setModel(result.model());
									car.setMaterials(result.materials());
									car.setCompiledModel(compiledCarModelCache.computeIfAbsent(car.getModelId(),
											id -> CompiledCarModel.compile(result.model(), result.materials(), CompiledCarModel.FALLBACK_TEXTURE, false, id)));
								} else {
									var model = ObjModelLoader.loadModel(client.getResourceManager(), car.getModelId());
									var materials = ObjModelLoader.loadMaterials(client.getResourceManager(), car.getModelId());
									car.setModel(model);
									car.setMaterials(materials);

									car.setCompiledModel(compiledCarModelCache.computeIfAbsent(car.getModelId(),
											id -> CompiledCarModel.compile(model, materials, CompiledCarModel.FALLBACK_TEXTURE)));
								}
							} catch (Exception e) {
								AiObjectMod.LOGGER.error("Failed to load model: " + car.getModelId(), e);
							}
						}
					}

					CompiledCarModel compiledForWheelDetect = car.getCompiledModel();
					if (compiledForWheelDetect != null && !car.hasDetectedWheelFootprint()
							&& compiledForWheelDetect.wheelPivots.size() == 4) {
						float sumAbsX = 0f;
						float sumFrontZ = 0f;
						int frontCount = 0;
						for (ObjModel.WheelPivot pivot : compiledForWheelDetect.wheelPivots) {
							sumAbsX += Math.abs(pivot.centerX);
							if (pivot.front) {
								sumFrontZ += pivot.centerZ;
								frontCount++;
							}
						}
						if (frontCount > 0) {
							float trackHalfWidth = sumAbsX / compiledForWheelDetect.wheelPivots.size();
							float rearZ = sumFrontZ / frontCount;
							car.setDetectedWheelFootprint(trackHalfWidth, rearZ);

							SetDetectedWheelFootprintPayload mineturismo$payload = new SetDetectedWheelFootprintPayload(car.getId(), trackHalfWidth, rearZ);
							ModClientNetworking.sendToServer(SetDetectedWheelFootprintPayload.ID, mineturismo$payload, SetDetectedWheelFootprintPayload::write);
							AiObjectMod.LOGGER.info("Detected wheel footprint for {}: trackHalfWidth={}, rearZ={}",
									car.getModelId(), trackHalfWidth, rearZ);
						}
					}

					if (car.getWheelAttachment() == null) {
						WheelAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setWheelAttachment);
					}

					com.examplebett.render.WheelAttachment attachment = car.getWheelAttachment();
					if (attachment != null) {

						String override = car.getWheelModelIdOverride();
						String desiredWheelModelId = (override != null && !override.isEmpty())
								? override : attachment.wheelModelId();
						boolean isNoneStyle = com.examplebett.render.WheelAttachment.NONE_STYLE.equals(desiredWheelModelId);

						car.setWheelStyleDefaultWidthScale(isNoneStyle ? 1f
								: WheelStyleCatalog.getDefaultWheelWidthScale(client.getResourceManager(), desiredWheelModelId));

						String colorOverride = car.getWheelColorOverride();
						String desiredWheelColorId = "";
						if (!isNoneStyle) {
							var colorOptions = WheelColorLoader.load(client.getResourceManager(), desiredWheelModelId);
							if (colorOptions.isPresent() && !colorOptions.get().ids().isEmpty()) {
								List<String> colorIds = colorOptions.get().ids();
								desiredWheelColorId = (colorOverride != null && colorIds.contains(colorOverride))
										? colorOverride : colorIds.get(0);
							}
						}
						boolean styleChanged = !desiredWheelModelId.equals(car.getLoadedWheelModelId());
						boolean colorChanged = !desiredWheelColorId.equals(car.getLoadedWheelColorId());
						if (styleChanged || colorChanged) {
							if (isNoneStyle) {

								car.setWheelCompiledModel(null);
								car.setWheelCompiledModelMirrored(null);
								car.setLoadedWheelModelId(desiredWheelModelId);
								car.setLoadedWheelColorId(desiredWheelColorId);
							} else {
								try {

									final String wheelModelIdForCache = desiredWheelModelId;
									final String wheelColorIdForCache = desiredWheelColorId;
									String wheelCacheKey = wheelModelIdForCache + "#" + wheelColorIdForCache;
									WheelCompiledPair compiledPair = compiledWheelModelCache.computeIfAbsent(wheelCacheKey, key -> {

										try {
											var wheelModel = ObjModelLoader.loadModel(client.getResourceManager(), wheelModelIdForCache);
											var wheelMaterials = ObjModelLoader.loadMaterials(client.getResourceManager(), wheelModelIdForCache);
											if (!wheelColorIdForCache.isEmpty()) {

												wheelMaterials = ObjModelLoader.loadLiveryMaterials(
														client.getResourceManager(), wheelModelIdForCache, wheelMaterials, wheelColorIdForCache);
											}

											CompiledCarModel compiled = CompiledCarModel.compile(
													wheelModel, wheelMaterials, CompiledCarModel.FALLBACK_TEXTURE, true);
											return new WheelCompiledPair(compiled, CompiledCarModel.mirrorX(compiled));
										} catch (java.io.IOException wheelIoError) {
											throw new java.io.UncheckedIOException(wheelIoError);
										}
									});
									car.setWheelCompiledModel(compiledPair.normal());
									car.setWheelCompiledModelMirrored(compiledPair.mirrored());
									car.setLoadedWheelModelId(desiredWheelModelId);
									car.setLoadedWheelColorId(desiredWheelColorId);
								} catch (Exception wheelLoadError) {
									AiObjectMod.LOGGER.error("Failed to load wheel model: " + desiredWheelModelId, wheelLoadError);
								}
							}
						}
					}

					if (car.getLightAttachment() == null) {
						com.examplebett.client.render.LightAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setLightAttachment);
					}

					if (car.getCameraAttachment() == null) {
						com.examplebett.client.render.CameraAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setCameraAttachment);
					}

					if (car.getPlateAttachment() == null) {
						com.examplebett.client.render.PlateAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setPlateAttachment);
					}

					if (car.getVehicleCategories() == null) {
						com.examplebett.client.render.VehicleCategoryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setVehicleCategories);
					}

					if (car.getLiveryOptions() == null) {
						com.examplebett.client.render.LiveryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setLiveryOptions);
					}

					com.examplebett.render.LiveryOptions liveryOptions = car.getLiveryOptions();
					if (liveryOptions != null && !liveryOptions.ids().isEmpty() && car.getModel() != null) {
						String liveryOverride = car.getLiveryOverride();
						String desiredLivery = (liveryOverride != null && !liveryOverride.isEmpty())
								? liveryOverride : liveryOptions.ids().get(0);
						if (!desiredLivery.equals(car.getLoadedLiveryId())) {
							try {
								var liveryMaterials = ObjModelLoader.loadLiveryMaterials(
										client.getResourceManager(), car.getModelId(), car.getMaterials(), desiredLivery);
								String liveryCacheKey = car.getModelId() + "#" + desiredLivery;
								var carModel = car.getModel();
								car.setCompiledModel(compiledCarModelCache.computeIfAbsent(liveryCacheKey,
										key -> liveryOptions.isDecal(desiredLivery)
												? CompiledCarModel.compileBodyAndDecal(carModel, liveryMaterials, CompiledCarModel.FALLBACK_TEXTURE, false)
												: CompiledCarModel.compile(carModel, liveryMaterials, CompiledCarModel.FALLBACK_TEXTURE)));
								car.setLoadedLiveryId(desiredLivery);
							} catch (Exception liveryLoadError) {
								AiObjectMod.LOGGER.error("Failed to load livery: " + desiredLivery, liveryLoadError);
							}
						}
					}

					if (car.isTownCar() || car.isRailwayRouteVehicle()) {

						if (client.player != null && !ambientEngineSounds.containsKey(car)
								&& car.distanceToSqr(client.player) <= AMBIENT_ENGINE_RANGE * AMBIENT_ENGINE_RANGE) {
							ambientEngineCandidates.add(car);
						}
					} else {

						car.updateClientSlideIntensity(car == drivingCar);
						TireSlipSoundInstance slipSound = tireSlipSounds.get(car);
						if ((slipSound == null || slipSound.isStopped()) && car.getClientSlideIntensity() > 0.0f) {
							slipSound = new TireSlipSoundInstance(car);
							client.getSoundManager().play(slipSound);
							tireSlipSounds.put(car, slipSound);
						}
					}

					PoliceSirenSoundInstance sirenSound = policeSirenSounds.get(car);
					if (car.isPoliceSirenOn()) {

						if (sirenSound != null && !sirenSound.isStopped() && sirenSound.isSoundOutdated()) {
							sirenSound.requestRelease();
							sirenSound = null;
						}

						boolean sirenHasSound = com.examplebett.entity.HornSirenSounds.sirenById(car.getSirenSoundId()).sound() != null;
						if (sirenHasSound && (sirenSound == null || sirenSound.isReadyForSuccessor())) {
							PoliceSirenSoundInstance successor = new PoliceSirenSoundInstance(car);
							client.getSoundManager().play(successor);
							policeSirenSounds.put(car, successor);
						}
					} else if (sirenSound != null && !sirenSound.isStopped()) {
						sirenSound.requestRelease();
					}
				} else if (entity instanceof BreakableTireStackEntity tireStack) {
					if (tireStack.getCompiledModel() == null) {
						try {
							var model = ObjModelLoader.loadModel(client.getResourceManager(), tireStack.getModelId());
							var materials = ObjModelLoader.loadMaterials(client.getResourceManager(), tireStack.getModelId());
							tireStack.setModel(model);
							tireStack.setMaterials(materials);
							tireStack.setCompiledModel(CompiledCarModel.compile(model, materials, CompiledCarModel.FALLBACK_TEXTURE));
						} catch (Exception e) {
							AiObjectMod.LOGGER.error("Failed to load model: " + tireStack.getModelId(), e);
						}
					}
				}
			}

			tireSlipSounds.entrySet().removeIf(entry -> entry.getValue().isStopped());

			policeSirenSounds.entrySet().removeIf(entry -> entry.getValue().isStopped());

			ambientEngineSounds.entrySet().removeIf(entry -> {
				CarEntity car = entry.getKey();
				boolean outOfRange = client.player == null
						|| car.distanceToSqr(client.player) > AMBIENT_ENGINE_RANGE * AMBIENT_ENGINE_RANGE;
				if (entry.getValue().isStopped() || !car.isAlive() || outOfRange) {
					client.getSoundManager().stop(entry.getValue());
					return true;
				}
				return false;
			});

			for (Map.Entry<CarEntity, EngineSoundInstance> entry : ambientEngineSounds.entrySet()) {
				if (entry.getValue().isReadyForSuccessor()) {
					EngineSoundInstance successor = new EngineSoundInstance(entry.getKey(), entry.getValue().getVolume(), true);
					client.getSoundManager().play(successor);
					entry.setValue(successor);
				}
			}
			while (ambientEngineSounds.size() < MAX_AMBIENT_ENGINE_SOUNDS && !ambientEngineCandidates.isEmpty()) {
				CarEntity picked = ambientEngineCandidates.remove(client.level.random.nextInt(ambientEngineCandidates.size()));
				float volume = (AMBIENT_ENGINE_MIN_VOLUME
						+ client.level.random.nextFloat() * (AMBIENT_ENGINE_MAX_VOLUME - AMBIENT_ENGINE_MIN_VOLUME))
						* ambientEngineVolumeMultiplier;
				EngineSoundInstance ambientSound = new EngineSoundInstance(picked, volume, true);
				client.getSoundManager().play(ambientSound);
				ambientEngineSounds.put(picked, ambientSound);
			}
		});
	}

	private static Component getCarDisplayName(CarEntity car) {
		Item item = MODEL_ID_TO_SPAWN_ITEM.get(car.getModelId());
		Component name = item != null ? new ItemStack(item).getHoverName() : ModText.literal(car.getModelId());
		if (!mountToastCustomFontEnabled) {
			return name;
		}

		return name.copy().withStyle(style -> style.withFont(MOUNT_TOAST_FONT));
	}

	private static Component getCarCategoryText(CarEntity car) {
		java.util.Set<com.examplebett.entity.VehicleCategory> categories = car.getVehicleCategories();
		if (categories == null || categories.isEmpty()) {
			return null;
		}
		java.util.List<com.examplebett.entity.VehicleCategory> sorted = new java.util.ArrayList<>(categories);
		sorted.sort(java.util.Comparator.naturalOrder());
		net.minecraft.network.chat.MutableComponent joined = ModText.literal("");
		for (int i = 0; i < sorted.size(); i++) {
			if (i > 0) {
				joined.append(" / ");
			}
			joined.append(ModText.translatable(sorted.get(i).translationKey));
		}
		if (!mountToastCustomFontEnabled) {
			return joined;
		}
		return joined.withStyle(style -> style.withFont(MOUNT_TOAST_FONT));
	}

	private static boolean mineturismo$canToggleTrailerCoupling(CarEntity drivingCar) {
		if (!drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
			return false;
		}
		return drivingCar.findCoupledTrailer() != null || drivingCar.findCouplableTrailer() != null;
	}

	private static void mineturismo$renderWaypointHint(GuiGraphics guiGraphics) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}
		Item mainHand = client.player.getMainHandItem().getItem();
		Item offHand = client.player.getOffhandItem().getItem();
		Component[] hintLines;
		if (com.examplebett.client.GttClient.isActive()) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.gtt.hud_hint") };
		} else if (client.player.getVehicle() instanceof CarEntity mineturismo$hintCar && mineturismo$canToggleTrailerCoupling(mineturismo$hintCar)) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.trailer.hud_hint", TRAILER_COUPLING_KEY.getTranslatedKeyMessage()) };
		} else if (mainHand instanceof WaypointMarkerItem || offHand instanceof WaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.waypoint_marker.hud_hint") };
		} else if (mainHand instanceof OneWayWaypointMarkerItem || offHand instanceof OneWayWaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.one_way_marker.hud_hint") };
		} else if (mainHand instanceof RailwayWaypointMarkerItem || offHand instanceof RailwayWaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.railway_marker.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.TowRopeItem || offHand instanceof com.examplebett.item.TowRopeItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.tow_rope.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.RailwayStopMarkerItem || offHand instanceof com.examplebett.item.RailwayStopMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.railway_stop_marker.hud_hint") };
		} else if (mainHand instanceof TrafficSignalMarkerItem || offHand instanceof TrafficSignalMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.traffic_signal_marker.hud_hint_edit") };
		} else if (mainHand instanceof com.examplebett.item.SmartDispatchAppItem || offHand instanceof com.examplebett.item.SmartDispatchAppItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.smart_dispatch.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.CustomWrenchItem || offHand instanceof com.examplebett.item.CustomWrenchItem) {

			hintLines = new Component[] { ModText.translatable("gui.mineturismo.custom_wrench.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.WheelWrenchItem || offHand instanceof com.examplebett.item.WheelWrenchItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.wheel_wrench.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.DebugWrenchItem || offHand instanceof com.examplebett.item.DebugWrenchItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.debug_wrench.hud_hint") };
		} else {
			return;
		}
		float scale = 0.85f;
		int padding = 4;
		int lineHeight = 10;
		int maxWidth = 0;
		for (Component line : hintLines) {
			maxWidth = Math.max(maxWidth, client.font.width(line));
		}
		int boxWidth = maxWidth + padding * 2;
		int boxHeight = hintLines.length * lineHeight + padding * 2;
		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(scale, scale, 1.0f);
		int boxX = Math.round(4 / scale);
		int boxY = Math.round(4 / scale);
		guiGraphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xFF000000);
		for (int i = 0; i < hintLines.length; i++) {
			guiGraphics.drawString(client.font, hintLines[i], boxX + padding, boxY + padding + i * lineHeight, 0xFFFFFF00, false);
		}
		guiGraphics.pose().popPose();
	}

	private static void renderMountToast(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (mountToastText == null || mountToastStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - mountToastStartMs;
		if (elapsed >= MOUNT_TOAST_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < MOUNT_TOAST_FADE_IN_MS) {
			alpha = elapsed / (float) MOUNT_TOAST_FADE_IN_MS;
		} else if (elapsed > MOUNT_TOAST_TOTAL_MS - MOUNT_TOAST_FADE_OUT_MS) {
			alpha = (MOUNT_TOAST_TOTAL_MS - elapsed) / (float) MOUNT_TOAST_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));

		int color = ((int) (alpha * 255.0f) << 24) | 0xFFFFFF;

		int x = (int) ((context.guiWidth() - 6) / MOUNT_TOAST_SCALE - mc.font.width(mountToastText));
		int y = (int) ((context.guiHeight() - 34) / MOUNT_TOAST_SCALE);

		context.pose().pushPose();
		context.pose().scale(MOUNT_TOAST_SCALE, MOUNT_TOAST_SCALE, 1.0f);
		context.drawString(mc.font, mountToastText, x, y, color, true);

		if (mountToastCategoryText != null) {
			int categoryX = (int) ((context.guiWidth() - 6) / MOUNT_TOAST_SCALE - mc.font.width(mountToastCategoryText));
			int categoryY = y + mc.font.lineHeight + 2;
			context.drawString(mc.font, mountToastCategoryText, categoryX, categoryY, color, true);
		}
		context.pose().popPose();
	}

	private static void renderSpeedDisplay(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (speedDisplayMode == SpeedDisplayMode.NONE || mc.options.hideGui || mc.player == null) return;

		Entity vehicle = mc.player.getVehicle();
		if (!(vehicle instanceof CarEntity car)) return;

		if (vehicle != lastVehicle) {
			return;
		}

		if (mountToastStartMs >= 0 && System.currentTimeMillis() - mountToastStartMs < MOUNT_TOAST_TOTAL_MS) {
			return;
		}

		double blocksPerTick = com.examplebett.compat.ModVec3.horizontalLength(car.getDeltaMovement());
		double kmh = blocksPerTick * 20.0 * 3.6;
		double displaySpeed = speedDisplayMode == SpeedDisplayMode.MPH ? kmh * 0.621371 : kmh;
		String numberText = String.valueOf(Math.round(displaySpeed));
		String unitText = speedDisplayMode == SpeedDisplayMode.MPH ? "mph" : "km/h";

		int color = 0xFFFFFFFF;
		int rightEdge = context.guiWidth() - 6;
		int nameRowY = context.guiHeight() - 34;

		int numberWidth = Math.round(mc.font.width(numberText) * SPEED_NUMBER_SCALE);
		int unitWidth = Math.round(mc.font.width(unitText) * SPEED_UNIT_SCALE);
		int unitScreenX = rightEdge - unitWidth;
		int numberScreenX = unitScreenX - SPEED_UNIT_GAP - numberWidth;

		int numberHeight = Math.round(mc.font.lineHeight * SPEED_NUMBER_SCALE);
		int unitHeight = Math.round(mc.font.lineHeight * SPEED_UNIT_SCALE);
		int numberScreenY = nameRowY;

		int unitScreenY = nameRowY + (numberHeight - unitHeight);

		context.pose().pushPose();
		context.pose().scale(SPEED_NUMBER_SCALE, SPEED_NUMBER_SCALE, 1.0f);
		context.drawString(mc.font, numberText, Math.round(numberScreenX / SPEED_NUMBER_SCALE), Math.round(numberScreenY / SPEED_NUMBER_SCALE), color, true);
		context.pose().popPose();

		context.pose().pushPose();
		context.pose().scale(SPEED_UNIT_SCALE, SPEED_UNIT_SCALE, 1.0f);
		context.drawString(mc.font, unitText, Math.round(unitScreenX / SPEED_UNIT_SCALE), Math.round(unitScreenY / SPEED_UNIT_SCALE), color, true);
		context.pose().popPose();
	}

	private static void renderPassengerTransportWarning(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (hudWarningText == null || hudWarningStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - hudWarningStartMs;
		if (elapsed >= HUD_WARNING_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < HUD_WARNING_FADE_IN_MS) {
			alpha = elapsed / (float) HUD_WARNING_FADE_IN_MS;
		} else if (elapsed > HUD_WARNING_TOTAL_MS - HUD_WARNING_FADE_OUT_MS) {
			alpha = (HUD_WARNING_TOTAL_MS - elapsed) / (float) HUD_WARNING_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));

		int color = ((int) (alpha * 255.0f) << 24) | 0xFFFFFF;

		context.drawString(mc.font, hudWarningText, 4, 28, color, true);
	}

	private static void renderHintTip(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (!hintsEnabled || hintTipLines == null || hintTipStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - hintTipStartMs;
		if (elapsed >= HINT_TIP_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < HINT_TIP_FADE_IN_MS) {
			alpha = elapsed / (float) HINT_TIP_FADE_IN_MS;
		} else if (elapsed > HINT_TIP_TOTAL_MS - HINT_TIP_FADE_OUT_MS) {
			alpha = (HINT_TIP_TOTAL_MS - elapsed) / (float) HINT_TIP_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));
		int alpha8 = (int) (alpha * 255.0f);

		int textColor = (alpha8 << 24) | 0xFFFFFF;
		int backgroundColor = alpha8 << 24;

		List<String> rows = new ArrayList<>();
		for (Component line : hintTipLines) {
			for (String row : line.getString().split("\n", -1)) {
				rows.add(row);
			}
		}
		int maxWidth = 0;
		for (String row : rows) {
			maxWidth = Math.max(maxWidth, mc.font.width(row));
		}
		int lineHeight = mc.font.lineHeight;
		int padding = 4;
		int boxWidth = maxWidth + padding * 2;
		int boxHeight = rows.size() * lineHeight + padding * 2;

		int screenX = 4;
		int screenY = 4;
		context.pose().pushPose();
		context.pose().scale(HINT_TIP_SCALE, HINT_TIP_SCALE, 1.0f);
		int x = Math.round(screenX / HINT_TIP_SCALE);
		int y = Math.round(screenY / HINT_TIP_SCALE);
		context.fill(x, y, x + boxWidth, y + boxHeight, backgroundColor);
		for (int i = 0; i < rows.size(); i++) {
			context.drawString(mc.font, rows.get(i), x + padding, y + padding + i * lineHeight, textColor, true);
		}
		context.pose().popPose();

		Component disableHint = ModText.translatable("gui.mineturismo.hint_tip.disable_instruction",
				ModText.translatable("item.mineturismo.debug_wrench"),
				ModText.translatable("gui.mineturismo.debug_wrench_menu.other"),
				ModText.translatable("gui.mineturismo.debug_wrench_menu.hide_hints"));
		int subLineHeight = mc.font.lineHeight;
		int subBoxWidth = mc.font.width(disableHint) + padding * 2;
		int subBoxHeight = subLineHeight + padding * 2;
		int subScreenY = screenY + Math.round(boxHeight * HINT_TIP_SCALE);
		context.pose().pushPose();
		context.pose().scale(HINT_TIP_SUB_SCALE, HINT_TIP_SUB_SCALE, 1.0f);
		int subX = Math.round(screenX / HINT_TIP_SUB_SCALE);
		int subY = Math.round(subScreenY / HINT_TIP_SUB_SCALE);
		context.fill(subX, subY, subX + subBoxWidth, subY + subBoxHeight, backgroundColor);
		context.drawString(mc.font, disableHint, subX + padding, subY + padding, textColor, true);
		context.pose().popPose();
	}
}
*/
//?}
//? if <1.16 && >=1.15 {
/*
package com.examplebett.client;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.block.AiObjectModBlocks;
import com.examplebett.client.gui.CarWrenchScreen;
import com.examplebett.client.gui.RailwayConsistScreen;
import com.examplebett.client.gui.WheelAdjustScreen;
import com.examplebett.client.gui.OneWayMarkerSettingsScreen;
import com.examplebett.client.gui.TrafficSignalSettingsScreen;
import com.examplebett.client.gui.WaypointTrafficSettingsScreen;
import com.examplebett.entity.AiObjectModEntities;
import com.examplebett.entity.BreakableTireStackEntity;
import com.examplebett.entity.BreakableBoxEntity;
import com.examplebett.entity.BreakableConeEntity;
import com.examplebett.entity.CarEntity;
import com.examplebett.client.render.BrakeCaliperModel;
import com.examplebett.client.render.BreakableTireStackRenderer;
import com.examplebett.client.render.CameraAttachmentLoader;
import com.examplebett.client.render.CarEntityRenderer;
import com.examplebett.client.render.GlbModelLoader;
import com.examplebett.client.render.LightAttachmentLoader;
import com.examplebett.client.render.LiveryLoader;
import com.examplebett.client.render.LodWheelModel;
import com.examplebett.client.render.PlateAttachmentLoader;
import com.examplebett.client.render.PoliceLightModel;
import com.examplebett.client.render.RearWingModel;
import com.examplebett.client.render.OtherPartModel;
import com.examplebett.client.render.TaxiSignModel;
import com.examplebett.client.render.WheelAttachmentLoader;
import com.examplebett.client.render.WheelColorLoader;
import com.examplebett.client.render.WheelStyleCatalog;
import com.examplebett.client.sound.EngineSoundInstance;
import com.examplebett.client.sound.HornSoundInstance;
import com.examplebett.client.sound.PoliceSirenSoundInstance;
import com.examplebett.client.sound.TireSlipSoundInstance;
import com.examplebett.item.CarSpawnItem;
import com.examplebett.item.OneWayWaypointMarkerItem;
import com.examplebett.item.RailwayWaypointMarkerItem;
import com.examplebett.item.TrafficSignalMarkerItem;
import com.examplebett.item.WaypointMarkerItem;
import com.examplebett.network.OpenRailwaySpawnSetupPayload;
import com.examplebett.network.OpenRailwayConsistSettingsPayload;
import com.examplebett.network.OpenSignalSettingsPayload;
import com.examplebett.network.PlayCollisionSoundPayload;
import com.examplebett.network.SetDetectedWheelFootprintPayload;
import com.examplebett.network.SetClientSlipReportPayload;
import com.examplebett.network.SetClientWheelieTriggerPayload;
import com.examplebett.network.SetWheelRotationReportPayload;
import com.examplebett.network.ShowHudWarningPayload;
import com.examplebett.network.SyncAmbientEngineVolumePayload;
import com.examplebett.network.SyncRailwayFormationOriginPayload;
import com.examplebett.network.SyncWheelDistancePayload;
import com.examplebett.waypoint.WaypointNetworkSavedData;
import com.examplebett.network.SetHornStatePayload;
import com.examplebett.network.ToggleTrailerCouplingPayload;
import com.examplebett.network.SetPoliceLightsOnPayload;
import com.examplebett.render.CompiledCarModel;
import com.examplebett.render.CustomPartCatalog;
import com.examplebett.render.FrontBrakeCaliperParts;
import com.examplebett.render.ObjModel;
import com.examplebett.render.ObjModelLoader;
import com.examplebett.render.OtherParts;
import com.examplebett.render.PoliceLightParts;
import com.examplebett.render.RearBrakeCaliperParts;
import com.examplebett.render.RearWingParts;
import com.examplebett.render.TaxiSignParts;
import com.examplebett.sound.AiObjectModSounds;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import com.examplebett.compat.ModRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiObjectModClient implements ClientModInitializer {

	public interface ExternalCarModelLoader {
		void tryLoad(CarEntity car);
	}

	public static ExternalCarModelLoader externalCarModelLoader = null;

	private static final long MOUNT_TOAST_TOTAL_MS = 3000L;
	private static final long MOUNT_TOAST_FADE_IN_MS = 300L;
	private static final long MOUNT_TOAST_FADE_OUT_MS = 700L;
	private static final ResourceLocation MOUNT_TOAST_FONT = AiObjectMod.id("mount_toast");
	private static final float MOUNT_TOAST_SCALE = 1.5f;

	private static boolean mountToastCustomFontEnabled = true;

	public static boolean isMountToastCustomFontEnabled() {
		return mountToastCustomFontEnabled;
	}

	public static void setMountToastCustomFontEnabled(boolean enabled) {
		mountToastCustomFontEnabled = enabled;
	}

	private static boolean fakeReflectionEnabled = false;

	public static boolean isFakeReflectionEnabled() {
		return fakeReflectionEnabled;
	}

	public static void setFakeReflectionEnabled(boolean enabled) {
		fakeReflectionEnabled = enabled;
	}

	private static boolean hintsEnabled = true;

	public static boolean isHintsEnabled() {
		return hintsEnabled;
	}

	public static void setHintsEnabled(boolean enabled) {
		hintsEnabled = enabled;
	}

	private static boolean fpsLogEnabled = false;

	public static boolean isFpsLogEnabled() {
		return fpsLogEnabled;
	}

	public static void setFpsLogEnabled(boolean enabled) {
		fpsLogEnabled = enabled;
	}

	private static final long FPS_LOG_INTERVAL_TICKS = 20L;
	private static long fpsLogTicks = 0L;

	private static final java.lang.reflect.Field LEGACY_FPS_FIELD = resolveLegacyFpsField();

	private static java.lang.reflect.Field resolveLegacyFpsField() {
		try {
			java.lang.reflect.Field field = com.examplebett.compat.ModReflection.findField(net.minecraft.client.Minecraft.class, "fps", "field_1738");
			field.setAccessible(true);
			return field;
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}

	private static int getGameFps(net.minecraft.client.Minecraft client) {
		if (LEGACY_FPS_FIELD == null) {
			return -1;
		}
		try {
			return LEGACY_FPS_FIELD.getInt(null);
		} catch (ReflectiveOperationException e) {
			return -1;
		}
	}

	public enum SpeedDisplayMode {
		NONE, KMH, MPH;

		public SpeedDisplayMode next() {
			return switch (this) {
				case NONE -> KMH;
				case KMH -> MPH;
				case MPH -> NONE;
			};
		}
	}

	private static SpeedDisplayMode speedDisplayMode = SpeedDisplayMode.NONE;
	private static final float SPEED_NUMBER_SCALE = 2.0f;
	private static final float SPEED_UNIT_SCALE = 1.0f;
	private static final int SPEED_UNIT_GAP = 3;

	public static SpeedDisplayMode getSpeedDisplayMode() {
		return speedDisplayMode;
	}

	public static void setSpeedDisplayMode(SpeedDisplayMode mode) {
		speedDisplayMode = mode;
	}
	private static final Map<String, Item> MODEL_ID_TO_SPAWN_ITEM = new HashMap<>();

	private static final Map<String, CompiledCarModel> compiledCarModelCache = new HashMap<>();

	private static final Map<String, WheelCompiledPair> compiledWheelModelCache = new HashMap<>();
	private record WheelCompiledPair(CompiledCarModel normal, CompiledCarModel mirrored) {
	}
	private static Entity lastVehicle = null;
	private static Component mountToastText = null;

	private static Component mountToastCategoryText = null;
	private static long mountToastStartMs = -1L;

	private static final long HUD_WARNING_TOTAL_MS = 3000L;
	private static final long HUD_WARNING_FADE_IN_MS = 200L;
	private static final long HUD_WARNING_FADE_OUT_MS = 700L;
	private static Component hudWarningText = null;
	private static long hudWarningStartMs = -1L;

	private record TimedHint(long delayTicks, String... lineKeys) {
	}

	private static final TimedHint[] TIMED_HINTS = {
			new TimedHint(1200L,
					"gui.mineturismo.reflection_tip.line1",
					"gui.mineturismo.reflection_tip.line2",
					"gui.mineturismo.reflection_tip.line3"),
			new TimedHint(1360L,
					"gui.mineturismo.shader_tip.line1",
					"gui.mineturismo.shader_tip.line2"),
			new TimedHint(3600L,
					"gui.mineturismo.smart_dispatch_tip.line1",
					"gui.mineturismo.smart_dispatch_tip.line2"),
			new TimedHint(6000L,
					"gui.mineturismo.wrench_tip.line1",
					"gui.mineturismo.wrench_tip.line2"),
			new TimedHint(12000L,
					"gui.mineturismo.traffic_marker_tip.line1",
					"gui.mineturismo.traffic_marker_tip.line2"),
	};

	private static final long HINT_TIP_TOTAL_MS = 8000L;
	private static final long HINT_TIP_FADE_IN_MS = 300L;
	private static final long HINT_TIP_FADE_OUT_MS = 700L;
	private static final float HINT_TIP_SCALE = 1.1f;

	private static final float HINT_TIP_SUB_SCALE = 0.75f;
	private static int nextHintIndex = 0;
	private static long hintTicks = 0L;
	private static Component[] hintTipLines = null;
	private static long hintTipStartMs = -1L;
	private static boolean creditsScreenChecked = false;

	private static final Map<CarEntity, EngineSoundInstance> engineSounds = new HashMap<>();

	private static final Map<CarEntity, EngineSoundInstance> engineLowSounds = new HashMap<>();

	private static final int WHEEL_ROTATION_REPORT_INTERVAL_TICKS = 2;
	private static int wheelRotationReportTickCounter = 0;

	private static final Map<CarEntity, TireSlipSoundInstance> tireSlipSounds = new HashMap<>();

	private static final Map<CarEntity, PoliceSirenSoundInstance> policeSirenSounds = new HashMap<>();

	private static final Map<CarEntity, EngineSoundInstance> ambientEngineSounds = new HashMap<>();
	private static final double AMBIENT_ENGINE_RANGE = 24.0;
	private static final int MAX_AMBIENT_ENGINE_SOUNDS = 3;
	private static final float AMBIENT_ENGINE_MIN_VOLUME = 0.2f;
	private static final float AMBIENT_ENGINE_MAX_VOLUME = 0.4f;

	private static float ambientEngineVolumeMultiplier = WaypointNetworkSavedData.DEFAULT_AMBIENT_ENGINE_VOLUME_MULTIPLIER;

	private static final String KEY_CATEGORY = "key.categories.mineturismo";
	private static final KeyMapping HORN_KEY = new KeyMapping(
			"key.mineturismo.horn", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, KEY_CATEGORY);

	private static final KeyMapping TRAILER_COUPLING_KEY = new KeyMapping(
			"key.mineturismo.trailer_coupling", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_L, KEY_CATEGORY);

	public static KeyMapping hornKey() {
		return HORN_KEY;
	}

	public static KeyMapping trailerCouplingKey() {
		return TRAILER_COUPLING_KEY;
	}

	private static HornSoundInstance activeHorn = null;

	private static CarEntity hornBroadcastCar = null;

	private static boolean hornKeyWasDown = false;

	private static boolean trailerCouplingKeyWasDown = false;

	private static boolean sprintKeyHeldSent = false;
	private static CarEntity sprintKeyHeldCar = null;

	private static void mineturismo$handlePlayCollisionSound(PlayCollisionSoundPayload payload) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			return;
		}

		net.minecraft.sounds.SoundEvent collisionSound = payload.lowSpeed() ? AiObjectModSounds.CAR_COLLISION_LOW : AiObjectModSounds.CAR_COLLISION;

		SimpleSoundInstance sound = new SimpleSoundInstance(
				collisionSound.getLocation(), SoundSource.NEUTRAL,
				payload.volume(), payload.pitch(),
				false, 0, SoundInstance.Attenuation.NONE,
				(float) payload.x(), (float) payload.y(), (float) payload.z(), false);
		mc.getSoundManager().play(sound);
	}

	private static void mineturismo$handleShowHudWarning(ShowHudWarningPayload payload) {
		hudWarningText = ModText.translatable(payload.messageKey());
		hudWarningStartMs = System.currentTimeMillis();
		Minecraft.getInstance().getSoundManager().play(
				SimpleSoundInstance.forUI(AiObjectModSounds.PASSENGER_TRANSPORT_DENIED, 1.0f));
	}

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.CAR_ENTITY, (dispatcher, context) -> new CarEntityRenderer(dispatcher));
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.BREAKABLE_TIRE_STACK, (dispatcher, context) -> new BreakableTireStackRenderer<BreakableTireStackEntity>(dispatcher));
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.BREAKABLE_BOX, (dispatcher, context) -> new BreakableTireStackRenderer<BreakableBoxEntity>(dispatcher));
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.BREAKABLE_BOX_2, (dispatcher, context) -> new BreakableTireStackRenderer<BreakableBoxEntity>(dispatcher));
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.TRAFFIC_CONE, (dispatcher, context) -> new BreakableTireStackRenderer<BreakableConeEntity>(dispatcher));
		EntityRendererRegistry.INSTANCE.register(AiObjectModEntities.BARRICADE, (dispatcher, context) -> new BreakableTireStackRenderer<com.examplebett.entity.BreakableBarricadeEntity>(dispatcher));

		KeyBindingHelper.registerKeyBinding(HORN_KEY);
		KeyBindingHelper.registerKeyBinding(TRAILER_COUPLING_KEY);

		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
				new SimpleSynchronousResourceReloadListener() {
					@Override
					public ResourceLocation getFabricId() {
						return AiObjectMod.id("clear_render_caches");
					}

					@Override
					public void onResourceManagerReload(ResourceManager resourceManager) {
						ObjModelLoader.clearCache();
						GlbModelLoader.clearCache();
						WheelAttachmentLoader.clearCache();
						WheelColorLoader.clearCache();
						LightAttachmentLoader.clearCache();
						com.examplebett.client.render.VehicleCategoryLoader.clearCache();
						PlateAttachmentLoader.clearCache();
						CameraAttachmentLoader.clearCache();
						LodWheelModel.clearCache();
						LiveryLoader.clearCache();
						RearWingModel.clearCache();
						PoliceLightModel.clearCache();
						TaxiSignModel.clearCache();
						OtherPartModel.clearCache();
						BrakeCaliperModel.clearCache();
						CustomPartCatalog.clearCache();
						WheelStyleCatalog.clearCache();
						RearWingParts.clearCache();
						OtherParts.clearCache();
						PoliceLightParts.clearCache();
						TaxiSignParts.clearCache();
						FrontBrakeCaliperParts.clearCache();
						RearBrakeCaliperParts.clearCache();
						compiledCarModelCache.clear();
						compiledWheelModelCache.clear();
					}
				});

		ModClientNetworking.registerS2C(PlayCollisionSoundPayload.ID, PlayCollisionSoundPayload::read, AiObjectModClient::mineturismo$handlePlayCollisionSound);

		ModClientNetworking.registerS2C(SyncWheelDistancePayload.ID, SyncWheelDistancePayload::read, payload -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc.level == null) {
				return;
			}
			if (mc.level.getEntity(payload.entityId()) instanceof CarEntity car) {
				car.setNetworkWheelDistance(payload.distance());
			}
		});

		ModClientNetworking.registerS2C(OpenRailwayConsistSettingsPayload.ID, OpenRailwayConsistSettingsPayload::read, payload -> {
			net.minecraft.client.multiplayer.ClientLevel mineturismo$level = Minecraft.getInstance().level;
			if (mineturismo$level != null && mineturismo$level.getEntity(payload.headEntityId()) instanceof CarEntity car) {
				Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
				Minecraft.getInstance().setScreen(new RailwayConsistScreen(car, null));
			}
		});

		ModClientNetworking.registerS2C(OpenSignalSettingsPayload.ID, OpenSignalSettingsPayload::read, payload -> {
			char direction = payload.direction().isEmpty() ? 'N' : payload.direction().charAt(0);
			TrafficSignalSettingsScreen.applyExternalConfig(payload.signalId(), payload.intersectionName(), direction,
					payload.greenTicks(), payload.redTicks(), payload.phaseOffsetTicks());
			Minecraft.getInstance().setScreen(new TrafficSignalSettingsScreen());
		});

		ModClientNetworking.registerS2C(OpenRailwaySpawnSetupPayload.ID, OpenRailwaySpawnSetupPayload::read, payload -> {
			Minecraft.getInstance().setScreen(new com.examplebett.client.gui.RailwaySpawnVehicleScreen(payload.originNodeId()));
		});

		ModClientNetworking.registerS2C(SyncRailwayFormationOriginPayload.ID, SyncRailwayFormationOriginPayload::read, payload -> {
			com.examplebett.inventory.RailwayFormationMenu.pendingOriginNodeId = payload.originNodeId();
		});

		ModClientNetworking.registerS2C(SyncAmbientEngineVolumePayload.ID, SyncAmbientEngineVolumePayload::read, payload -> {
			ambientEngineVolumeMultiplier = payload.multiplier();
			com.examplebett.client.gui.WaypointClientSettings.apply(payload.multiplier(), payload.settings());
		});

		ModClientNetworking.registerS2C(ShowHudWarningPayload.ID, ShowHudWarningPayload::read, AiObjectModClient::mineturismo$handleShowHudWarning);

		BlockRenderLayerMap.INSTANCE.putBlock(AiObjectModBlocks.CAR_BREAKABLE_GLASS, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(AiObjectModBlocks.WATER_PUDDLE, RenderType.translucent());

		CarEntity.wrenchMenuOpener = (car, player) -> {
			Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
			Minecraft.getInstance().setScreen(new CarWrenchScreen(car));
		};
		CarEntity.wheelWrenchMenuOpener = (car, player) -> {
			Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
			Minecraft.getInstance().setScreen(new WheelAdjustScreen(car));
		};
		CarEntity.debugWrenchMenuOpener = (car, player) -> Minecraft.getInstance().setScreen(new com.examplebett.client.gui.DebugWrenchScreen(car));
		WaypointMarkerItem.trafficSettingsMenuOpener = player -> Minecraft.getInstance().setScreen(new WaypointTrafficSettingsScreen());
		OneWayWaypointMarkerItem.settingsMenuOpener = player -> Minecraft.getInstance().setScreen(new OneWayMarkerSettingsScreen());
		com.examplebett.item.SmartDispatchAppItem.appScreenOpener = player -> Minecraft.getInstance().setScreen(new com.examplebett.client.gui.SmartDispatchAppScreen());
		ClientTickEvents.END_CLIENT_TICK.register(com.examplebett.client.GttClient::tick);
		ClientTickEvents.END_CLIENT_TICK.register(com.examplebett.client.sound.ChorusMusicPlayer::tick);

		ModClientNetworking.registerS2C(com.examplebett.network.GttScorePayload.ID, com.examplebett.network.GttScorePayload::read, payload -> com.examplebett.client.GttClient.setScore(payload.score()));

		ModClientNetworking.registerS2C(com.examplebett.network.SpawnLegacyEntityPayload.ID, com.examplebett.network.SpawnLegacyEntityPayload::read, payload -> {
			Minecraft mineturismo$mc = Minecraft.getInstance();
			if (mineturismo$mc.level == null) {
				return;
			}
			net.minecraft.world.entity.Entity mineturismo$entity = com.examplebett.compat.ModRegistries.ENTITY_TYPE.get(payload.typeId).create(mineturismo$mc.level);
			if (mineturismo$entity == null) {
				return;
			}
			mineturismo$entity.setPacketCoordinates(payload.x, payload.y, payload.z);
			mineturismo$entity.moveTo(payload.x, payload.y, payload.z, payload.yRot, 0.0f);
			mineturismo$entity.setId(payload.entityId);
			mineturismo$entity.setUUID(payload.uuid);
			mineturismo$mc.level.putNonPlayerEntity(payload.entityId, mineturismo$entity);
		});

		ModClientNetworking.registerS2C(com.examplebett.network.SlotSpinResultPayload.ID, com.examplebett.network.SlotSpinResultPayload::read, payload -> com.examplebett.client.gui.SmartDispatchAppScreen.onSlotResult(payload.prizes()));

		HudRenderCallback.EVENT.register(tickCounter -> com.examplebett.client.GttClient.renderHud(new GuiGraphics(null)));

		net.minecraft.client.gui.screens.MenuScreens.register(com.examplebett.inventory.AiObjectModMenus.RAILWAY_FORMATION_MENU_TYPE, com.examplebett.client.gui.RailwayFormationScreen::new);
		net.minecraft.client.gui.screens.MenuScreens.register(com.examplebett.inventory.AiObjectModMenus.MASSIVE_AUTO_APPRAISAL_MENU_TYPE, com.examplebett.client.gui.MassiveAutoAppraisalScreen::new);

		HudRenderCallback.EVENT.register(deltaTracker -> mineturismo$renderWaypointHint(new GuiGraphics(null)));

		CarEntity.clientDescendKeyHeld = () -> Minecraft.getInstance().options.keySprint.isDown();

		CarEntity.clientForwardKeyHeld = () -> Minecraft.getInstance().options.keyUp.isDown();
		CarEntity.clientBackKeyHeld = () -> Minecraft.getInstance().options.keyDown.isDown();

		CarEntity.clientDimensionsResourceManager = () -> {
			var integratedServer = Minecraft.getInstance().getSingleplayerServer();
			return integratedServer != null ? com.examplebett.compat.ModResources.getServerResourceManager(integratedServer) : null;
		};

		for (Item item : ModRegistries.ITEM) {
			if (item instanceof CarSpawnItem spawnItem) {
				MODEL_ID_TO_SPAWN_ITEM.putIfAbsent(spawnItem.getModelId(), item);
			}
		}

		HudRenderCallback.EVENT.register(tickCounter -> renderMountToast(new GuiGraphics(null)));

		HudRenderCallback.EVENT.register(tickCounter -> renderSpeedDisplay(new GuiGraphics(null)));

		HudRenderCallback.EVENT.register(tickCounter -> renderPassengerTransportWarning(new GuiGraphics(null)));

		HudRenderCallback.EVENT.register(tickCounter -> renderHintTip(new GuiGraphics(null)));

		final boolean[] mineturismo$wasInWorld = {false};
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.level != null && !mineturismo$wasInWorld[0]) {
				nextHintIndex = 0;
				hintTicks = 0L;
				hintTipStartMs = -1L;
			}
			mineturismo$wasInWorld[0] = client.level != null;
		});

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			boolean mineturismo$pressed = false;
			while (TRAILER_COUPLING_KEY.consumeClick()) {
				mineturismo$pressed = true;
			}

			InputConstants.Key mineturismo$boundKey = KeyBindingHelper.getBoundKeyOf(TRAILER_COUPLING_KEY);
			boolean mineturismo$rawDown = mineturismo$boundKey.getType() == InputConstants.Type.KEYSYM
					&& InputConstants.isKeyDown(client.getWindow().getWindow(), mineturismo$boundKey.getValue());
			if (mineturismo$rawDown && !trailerCouplingKeyWasDown && client.screen == null) {
				mineturismo$pressed = true;
			}
			trailerCouplingKeyWasDown = mineturismo$rawDown;

			if (client.player != null && client.screen == null
					&& client.player.getVehicle() instanceof CarEntity mineturismo$headCar
					&& mineturismo$headCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
				for (KeyMapping mineturismo$other : client.options.keyMappings) {
					if (mineturismo$other != TRAILER_COUPLING_KEY && mineturismo$other.same(TRAILER_COUPLING_KEY)) {
						while (mineturismo$other.consumeClick()) {
							mineturismo$pressed = true;
						}
					}
				}
			}
			if (!mineturismo$pressed || client.player == null || client.screen != null
					|| !(client.player.getVehicle() instanceof CarEntity mineturismo$car)
					|| !mineturismo$car.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
				return;
			}
			for (KeyMapping mineturismo$other : client.options.keyMappings) {
				if (mineturismo$other != TRAILER_COUPLING_KEY && mineturismo$other.same(TRAILER_COUPLING_KEY)) {
					while (mineturismo$other.consumeClick()) {

					}
				}
			}
			ToggleTrailerCouplingPayload mineturismo$payload = new ToggleTrailerCouplingPayload(mineturismo$car.getId());
			ModClientNetworking.sendToServer(ToggleTrailerCouplingPayload.ID, mineturismo$payload, ToggleTrailerCouplingPayload::write);
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (creditsScreenChecked) return;
			if (client.player == null || client.level == null) return;
			creditsScreenChecked = true;
			com.examplebett.client.gui.CreditsScreen.showOnceIfNeeded();
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!fpsLogEnabled) return;
			fpsLogTicks++;
			if (fpsLogTicks % FPS_LOG_INTERVAL_TICKS != 0) return;
			int fps = getGameFps(client);
			int carCount = 0;
			if (client.level != null) {
				for (Entity entity : client.level.entitiesForRendering()) {
					if (entity instanceof CarEntity) {
						carCount++;
					}
				}
			}
			AiObjectMod.LOGGER.info("[fps-log] fps={} frameTimeMs={} carCount={}", fps,
					fps > 0 ? String.format("%.1f", 1000.0 / fps) : "n/a", carCount);
		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (hintsEnabled && client.player != null) {
				hintTicks++;
				while (nextHintIndex < TIMED_HINTS.length && hintTicks >= TIMED_HINTS[nextHintIndex].delayTicks()) {
					TimedHint hint = TIMED_HINTS[nextHintIndex];
					hintTipLines = new Component[hint.lineKeys().length];
					for (int i = 0; i < hint.lineKeys().length; i++) {
						hintTipLines[i] = ModText.translatable(hint.lineKeys()[i]);
					}
					hintTipStartMs = System.currentTimeMillis();
					client.getSoundManager().play(
							SimpleSoundInstance.forUI(AiObjectModSounds.PASSENGER_TRANSPORT_DENIED, 1.0f));
					nextHintIndex++;
				}
			}

			CarEntity drivingCar = null;
			if (client.player != null) {
				Entity vehicle = client.player.getVehicle();
				if (vehicle instanceof CarEntity car) {
					drivingCar = car;
				}
				if (vehicle instanceof CarEntity car && vehicle != lastVehicle) {
					mountToastText = getCarDisplayName(car);
					if (car.getVehicleCategories() == null) {
						com.examplebett.client.render.VehicleCategoryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setVehicleCategories);
					}
					mountToastCategoryText = getCarCategoryText(car);
					mountToastStartMs = System.currentTimeMillis();
				}
				lastVehicle = vehicle;
			}

			if (drivingCar != null) {
				EngineSoundInstance existing = engineSounds.get(drivingCar);

				if (existing != null && !existing.matchesCurrentSoundId()) {
					client.getSoundManager().stop(existing);
					existing = null;
				}

				if (existing == null || existing.isReadyForSuccessor()) {
					EngineSoundInstance sound = new EngineSoundInstance(drivingCar, 1.0f, false,
							drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)
									? EngineSoundInstance.Layer.HYBRID_HIGH : EngineSoundInstance.Layer.NORMAL);
					client.getSoundManager().play(sound);
					engineSounds.put(drivingCar, sound);
				}

				if (drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)) {
					EngineSoundInstance lowExisting = engineLowSounds.get(drivingCar);
					if (lowExisting == null || lowExisting.isReadyForSuccessor()) {
						EngineSoundInstance lowSound = new EngineSoundInstance(drivingCar, 1.0f, false, EngineSoundInstance.Layer.HYBRID_LOW);
						client.getSoundManager().play(lowSound);
						engineLowSounds.put(drivingCar, lowSound);
					}
				}
			}

			CarEntity currentDrivingCar = drivingCar;
			engineSounds.entrySet().removeIf(e -> {
				if (e.getKey() != currentDrivingCar) {
					client.getSoundManager().stop(e.getValue());
					return true;
				}
				return e.getValue().isStopped();
			});

			engineLowSounds.entrySet().removeIf(e -> {
				if (e.getKey() != currentDrivingCar || !e.getKey().hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)) {
					client.getSoundManager().stop(e.getValue());
					return true;
				}
				return e.getValue().isStopped();
			});

			wheelRotationReportTickCounter++;
			if (drivingCar != null && wheelRotationReportTickCounter % WHEEL_ROTATION_REPORT_INTERVAL_TICKS == 0) {
				SetWheelRotationReportPayload mineturismo$payload = new SetWheelRotationReportPayload(
						drivingCar.getId(), drivingCar.getWheelDistanceTraveled());
				ModClientNetworking.sendToServer(SetWheelRotationReportPayload.ID, mineturismo$payload, SetWheelRotationReportPayload::write);
			}

			if (drivingCar != null && HORN_KEY.isDown()
					&& com.examplebett.entity.HornSirenSounds.hornById(drivingCar.getHornSoundId()).sound() != null) {
				if (activeHorn == null || activeHorn.isStopped() || !activeHorn.isFor(drivingCar)) {
					if (activeHorn != null) {
						client.getSoundManager().stop(activeHorn);
					}
					activeHorn = new HornSoundInstance(drivingCar);
					client.getSoundManager().play(activeHorn);
				} else {
					activeHorn.requestPlaying();
				}
			} else if (activeHorn != null) {
				activeHorn.requestRelease();
				if (activeHorn.isStopped()) {
					activeHorn = null;
				}
			}

			boolean hornDown = drivingCar != null && HORN_KEY.isDown();
			if (hornDown) {
				if (hornBroadcastCar != drivingCar) {
					if (hornBroadcastCar != null) {
						SetHornStatePayload mineturismo$payload = new SetHornStatePayload(hornBroadcastCar.getId(), false);
						ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
					}
					SetHornStatePayload mineturismo$payload = new SetHornStatePayload(drivingCar.getId(), true);
					ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
					hornBroadcastCar = drivingCar;
				}
			} else if (hornBroadcastCar != null) {
				SetHornStatePayload mineturismo$payload = new SetHornStatePayload(hornBroadcastCar.getId(), false);
				ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
				hornBroadcastCar = null;
			}

			boolean hornKeyDown = HORN_KEY.isDown();
			if (drivingCar != null && hornKeyDown && !hornKeyWasDown && !drivingCar.getPoliceLightEnabledIds().isEmpty()) {

				boolean newLightsOn;
				boolean newSirenOn;
				if (!drivingCar.isPoliceLightsOn()) {

					newLightsOn = true;
					newSirenOn = true;
				} else if (drivingCar.isPoliceSirenOn()) {

					newLightsOn = true;
					newSirenOn = false;
				} else {

					newLightsOn = false;
					newSirenOn = false;
				}
				drivingCar.setPoliceLightsOn(newLightsOn);
				drivingCar.setPoliceSirenOn(newSirenOn);
				SetPoliceLightsOnPayload mineturismo$payload = new SetPoliceLightsOnPayload(drivingCar.getId(), newLightsOn, newSirenOn);
				ModClientNetworking.sendToServer(SetPoliceLightsOnPayload.ID, mineturismo$payload, SetPoliceLightsOnPayload::write);
			}
			hornKeyWasDown = hornKeyDown;

			if (drivingCar != null && drivingCar.consumeClientSlipFlag()) {
				SetClientSlipReportPayload mineturismo$payload = new SetClientSlipReportPayload(drivingCar.getId());
				ModClientNetworking.sendToServer(SetClientSlipReportPayload.ID, mineturismo$payload, SetClientSlipReportPayload::write);
			}

			if (drivingCar != null && drivingCar.consumeClientWheelieTriggerFlag()) {
				SetClientWheelieTriggerPayload mineturismo$wheeliePayload = new SetClientWheelieTriggerPayload(drivingCar.getId());
				ModClientNetworking.sendToServer(SetClientWheelieTriggerPayload.ID, mineturismo$wheeliePayload, SetClientWheelieTriggerPayload::write);
			}

			boolean mineturismo$sprintDown = drivingCar != null && client.options.keySprint.isDown();
			if (drivingCar != null && (mineturismo$sprintDown != sprintKeyHeldSent || drivingCar != sprintKeyHeldCar)) {
				ModClientNetworking.sendToServer(com.examplebett.network.SetSprintKeyHeldPayload.ID, new com.examplebett.network.SetSprintKeyHeldPayload(drivingCar.getId(), mineturismo$sprintDown), com.examplebett.network.SetSprintKeyHeldPayload::write);
				sprintKeyHeldSent = mineturismo$sprintDown;
				sprintKeyHeldCar = drivingCar;
			} else if (drivingCar == null) {
				sprintKeyHeldSent = false;
				sprintKeyHeldCar = null;
			}

			if (client.level == null) return;

			List<CarEntity> ambientEngineCandidates = new ArrayList<>();
			for (Entity entity : client.level.entitiesForRendering()) {
				if (entity instanceof CarEntity car) {
					if (car.getCompiledModel() == null) {
						if (car.getModelId().startsWith(CarEntity.EXTERNAL_MODEL_PREFIX)) {
							if (externalCarModelLoader != null) {
								try {
									externalCarModelLoader.tryLoad(car);
								} catch (Exception e) {
									AiObjectMod.LOGGER.error("Failed to load external model: " + car.getModelId(), e);
								}
							}
						} else {
							try {
								ResourceLocation glbLocation = AiObjectMod.id("models/" + car.getModelId() + ".glb");

								if (client.getResourceManager().hasResource(glbLocation)) {
									var result = GlbModelLoader.load(client.getResourceManager(), car.getModelId());
									car.setModel(result.model());
									car.setMaterials(result.materials());
									car.setCompiledModel(compiledCarModelCache.computeIfAbsent(car.getModelId(),
											id -> CompiledCarModel.compile(result.model(), result.materials(), CompiledCarModel.FALLBACK_TEXTURE, false, id)));
								} else {
									var model = ObjModelLoader.loadModel(client.getResourceManager(), car.getModelId());
									var materials = ObjModelLoader.loadMaterials(client.getResourceManager(), car.getModelId());
									car.setModel(model);
									car.setMaterials(materials);

									car.setCompiledModel(compiledCarModelCache.computeIfAbsent(car.getModelId(),
											id -> CompiledCarModel.compile(model, materials, CompiledCarModel.FALLBACK_TEXTURE)));
								}
							} catch (Exception e) {
								AiObjectMod.LOGGER.error("Failed to load model: " + car.getModelId(), e);
							}
						}
					}

					CompiledCarModel compiledForWheelDetect = car.getCompiledModel();
					if (compiledForWheelDetect != null && !car.hasDetectedWheelFootprint()
							&& compiledForWheelDetect.wheelPivots.size() == 4) {
						float sumAbsX = 0f;
						float sumFrontZ = 0f;
						int frontCount = 0;
						for (ObjModel.WheelPivot pivot : compiledForWheelDetect.wheelPivots) {
							sumAbsX += Math.abs(pivot.centerX);
							if (pivot.front) {
								sumFrontZ += pivot.centerZ;
								frontCount++;
							}
						}
						if (frontCount > 0) {
							float trackHalfWidth = sumAbsX / compiledForWheelDetect.wheelPivots.size();
							float rearZ = sumFrontZ / frontCount;
							car.setDetectedWheelFootprint(trackHalfWidth, rearZ);

							SetDetectedWheelFootprintPayload mineturismo$payload = new SetDetectedWheelFootprintPayload(car.getId(), trackHalfWidth, rearZ);
							ModClientNetworking.sendToServer(SetDetectedWheelFootprintPayload.ID, mineturismo$payload, SetDetectedWheelFootprintPayload::write);
							AiObjectMod.LOGGER.info("Detected wheel footprint for {}: trackHalfWidth={}, rearZ={}",
									car.getModelId(), trackHalfWidth, rearZ);
						}
					}

					if (car.getWheelAttachment() == null) {
						WheelAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setWheelAttachment);
					}

					com.examplebett.render.WheelAttachment attachment = car.getWheelAttachment();
					if (attachment != null) {

						String override = car.getWheelModelIdOverride();
						String desiredWheelModelId = (override != null && !override.isEmpty())
								? override : attachment.wheelModelId();
						boolean isNoneStyle = com.examplebett.render.WheelAttachment.NONE_STYLE.equals(desiredWheelModelId);

						car.setWheelStyleDefaultWidthScale(isNoneStyle ? 1f
								: WheelStyleCatalog.getDefaultWheelWidthScale(client.getResourceManager(), desiredWheelModelId));

						String colorOverride = car.getWheelColorOverride();
						String desiredWheelColorId = "";
						if (!isNoneStyle) {
							var colorOptions = WheelColorLoader.load(client.getResourceManager(), desiredWheelModelId);
							if (colorOptions.isPresent() && !colorOptions.get().ids().isEmpty()) {
								List<String> colorIds = colorOptions.get().ids();
								desiredWheelColorId = (colorOverride != null && colorIds.contains(colorOverride))
										? colorOverride : colorIds.get(0);
							}
						}
						boolean styleChanged = !desiredWheelModelId.equals(car.getLoadedWheelModelId());
						boolean colorChanged = !desiredWheelColorId.equals(car.getLoadedWheelColorId());
						if (styleChanged || colorChanged) {
							if (isNoneStyle) {

								car.setWheelCompiledModel(null);
								car.setWheelCompiledModelMirrored(null);
								car.setLoadedWheelModelId(desiredWheelModelId);
								car.setLoadedWheelColorId(desiredWheelColorId);
							} else {
								try {

									final String wheelModelIdForCache = desiredWheelModelId;
									final String wheelColorIdForCache = desiredWheelColorId;
									String wheelCacheKey = wheelModelIdForCache + "#" + wheelColorIdForCache;
									WheelCompiledPair compiledPair = compiledWheelModelCache.computeIfAbsent(wheelCacheKey, key -> {

										try {
											var wheelModel = ObjModelLoader.loadModel(client.getResourceManager(), wheelModelIdForCache);
											var wheelMaterials = ObjModelLoader.loadMaterials(client.getResourceManager(), wheelModelIdForCache);
											if (!wheelColorIdForCache.isEmpty()) {

												wheelMaterials = ObjModelLoader.loadLiveryMaterials(
														client.getResourceManager(), wheelModelIdForCache, wheelMaterials, wheelColorIdForCache);
											}

											CompiledCarModel compiled = CompiledCarModel.compile(
													wheelModel, wheelMaterials, CompiledCarModel.FALLBACK_TEXTURE, true);
											return new WheelCompiledPair(compiled, CompiledCarModel.mirrorX(compiled));
										} catch (java.io.IOException wheelIoError) {
											throw new java.io.UncheckedIOException(wheelIoError);
										}
									});
									car.setWheelCompiledModel(compiledPair.normal());
									car.setWheelCompiledModelMirrored(compiledPair.mirrored());
									car.setLoadedWheelModelId(desiredWheelModelId);
									car.setLoadedWheelColorId(desiredWheelColorId);
								} catch (Exception wheelLoadError) {
									AiObjectMod.LOGGER.error("Failed to load wheel model: " + desiredWheelModelId, wheelLoadError);
								}
							}
						}
					}

					if (car.getLightAttachment() == null) {
						com.examplebett.client.render.LightAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setLightAttachment);
					}

					if (car.getCameraAttachment() == null) {
						com.examplebett.client.render.CameraAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setCameraAttachment);
					}

					if (car.getPlateAttachment() == null) {
						com.examplebett.client.render.PlateAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setPlateAttachment);
					}

					if (car.getVehicleCategories() == null) {
						com.examplebett.client.render.VehicleCategoryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setVehicleCategories);
					}

					if (car.getLiveryOptions() == null) {
						com.examplebett.client.render.LiveryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setLiveryOptions);
					}

					com.examplebett.render.LiveryOptions liveryOptions = car.getLiveryOptions();
					if (liveryOptions != null && !liveryOptions.ids().isEmpty() && car.getModel() != null) {
						String liveryOverride = car.getLiveryOverride();
						String desiredLivery = (liveryOverride != null && !liveryOverride.isEmpty())
								? liveryOverride : liveryOptions.ids().get(0);
						if (!desiredLivery.equals(car.getLoadedLiveryId())) {
							try {
								var liveryMaterials = ObjModelLoader.loadLiveryMaterials(
										client.getResourceManager(), car.getModelId(), car.getMaterials(), desiredLivery);
								String liveryCacheKey = car.getModelId() + "#" + desiredLivery;
								var carModel = car.getModel();
								car.setCompiledModel(compiledCarModelCache.computeIfAbsent(liveryCacheKey,
										key -> liveryOptions.isDecal(desiredLivery)
												? CompiledCarModel.compileBodyAndDecal(carModel, liveryMaterials, CompiledCarModel.FALLBACK_TEXTURE, false)
												: CompiledCarModel.compile(carModel, liveryMaterials, CompiledCarModel.FALLBACK_TEXTURE)));
								car.setLoadedLiveryId(desiredLivery);
							} catch (Exception liveryLoadError) {
								AiObjectMod.LOGGER.error("Failed to load livery: " + desiredLivery, liveryLoadError);
							}
						}
					}

					if (car.isTownCar() || car.isRailwayRouteVehicle()) {

						if (client.player != null && !ambientEngineSounds.containsKey(car)
								&& car.distanceToSqr(client.player) <= AMBIENT_ENGINE_RANGE * AMBIENT_ENGINE_RANGE) {
							ambientEngineCandidates.add(car);
						}
					} else {

						car.updateClientSlideIntensity(car == drivingCar);
						TireSlipSoundInstance slipSound = tireSlipSounds.get(car);
						if ((slipSound == null || slipSound.isStopped()) && car.getClientSlideIntensity() > 0.0f) {
							slipSound = new TireSlipSoundInstance(car);
							client.getSoundManager().play(slipSound);
							tireSlipSounds.put(car, slipSound);
						}
					}

					PoliceSirenSoundInstance sirenSound = policeSirenSounds.get(car);
					if (car.isPoliceSirenOn()) {

						if (sirenSound != null && !sirenSound.isStopped() && sirenSound.isSoundOutdated()) {
							sirenSound.requestRelease();
							sirenSound = null;
						}

						boolean sirenHasSound = com.examplebett.entity.HornSirenSounds.sirenById(car.getSirenSoundId()).sound() != null;
						if (sirenHasSound && (sirenSound == null || sirenSound.isReadyForSuccessor())) {
							PoliceSirenSoundInstance successor = new PoliceSirenSoundInstance(car);
							client.getSoundManager().play(successor);
							policeSirenSounds.put(car, successor);
						}
					} else if (sirenSound != null && !sirenSound.isStopped()) {
						sirenSound.requestRelease();
					}
				} else if (entity instanceof BreakableTireStackEntity tireStack) {
					if (tireStack.getCompiledModel() == null) {
						try {
							var model = ObjModelLoader.loadModel(client.getResourceManager(), tireStack.getModelId());
							var materials = ObjModelLoader.loadMaterials(client.getResourceManager(), tireStack.getModelId());
							tireStack.setModel(model);
							tireStack.setMaterials(materials);
							tireStack.setCompiledModel(CompiledCarModel.compile(model, materials, CompiledCarModel.FALLBACK_TEXTURE));
						} catch (Exception e) {
							AiObjectMod.LOGGER.error("Failed to load model: " + tireStack.getModelId(), e);
						}
					}
				}
			}

			tireSlipSounds.entrySet().removeIf(entry -> entry.getValue().isStopped());

			policeSirenSounds.entrySet().removeIf(entry -> entry.getValue().isStopped());

			ambientEngineSounds.entrySet().removeIf(entry -> {
				CarEntity car = entry.getKey();
				boolean outOfRange = client.player == null
						|| car.distanceToSqr(client.player) > AMBIENT_ENGINE_RANGE * AMBIENT_ENGINE_RANGE;
				if (entry.getValue().isStopped() || !car.isAlive() || outOfRange) {
					client.getSoundManager().stop(entry.getValue());
					return true;
				}
				return false;
			});

			for (Map.Entry<CarEntity, EngineSoundInstance> entry : ambientEngineSounds.entrySet()) {
				if (entry.getValue().isReadyForSuccessor()) {
					EngineSoundInstance successor = new EngineSoundInstance(entry.getKey(), entry.getValue().getVolume(), true);
					client.getSoundManager().play(successor);
					entry.setValue(successor);
				}
			}
			while (ambientEngineSounds.size() < MAX_AMBIENT_ENGINE_SOUNDS && !ambientEngineCandidates.isEmpty()) {
				CarEntity picked = ambientEngineCandidates.remove(client.level.random.nextInt(ambientEngineCandidates.size()));
				float volume = (AMBIENT_ENGINE_MIN_VOLUME
						+ client.level.random.nextFloat() * (AMBIENT_ENGINE_MAX_VOLUME - AMBIENT_ENGINE_MIN_VOLUME))
						* ambientEngineVolumeMultiplier;
				EngineSoundInstance ambientSound = new EngineSoundInstance(picked, volume, true);
				client.getSoundManager().play(ambientSound);
				ambientEngineSounds.put(picked, ambientSound);
			}
		});
	}

	private static Component getCarDisplayName(CarEntity car) {
		Item item = MODEL_ID_TO_SPAWN_ITEM.get(car.getModelId());
		Component name = item != null ? new ItemStack(item).getHoverName() : ModText.literal(car.getModelId());
		if (!mountToastCustomFontEnabled) {
			return name;
		}

		return name.copy();
	}

	private static Component getCarCategoryText(CarEntity car) {
		java.util.Set<com.examplebett.entity.VehicleCategory> categories = car.getVehicleCategories();
		if (categories == null || categories.isEmpty()) {
			return null;
		}
		java.util.List<com.examplebett.entity.VehicleCategory> sorted = new java.util.ArrayList<>(categories);
		sorted.sort(java.util.Comparator.naturalOrder());
		net.minecraft.network.chat.Component joined = ModText.literal("");
		for (int i = 0; i < sorted.size(); i++) {
			if (i > 0) {
				joined.append(" / ");
			}
			joined.append(ModText.translatable(sorted.get(i).translationKey));
		}
		if (!mountToastCustomFontEnabled) {
			return joined;
		}
		return joined;
	}

	private static boolean mineturismo$canToggleTrailerCoupling(CarEntity drivingCar) {
		if (!drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
			return false;
		}
		return drivingCar.findCoupledTrailer() != null || drivingCar.findCouplableTrailer() != null;
	}

	private static void mineturismo$renderWaypointHint(GuiGraphics guiGraphics) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}
		Item mainHand = client.player.getMainHandItem().getItem();
		Item offHand = client.player.getOffhandItem().getItem();
		Component[] hintLines;
		if (com.examplebett.client.GttClient.isActive()) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.gtt.hud_hint") };
		} else if (client.player.getVehicle() instanceof CarEntity mineturismo$hintCar && mineturismo$canToggleTrailerCoupling(mineturismo$hintCar)) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.trailer.hud_hint", TRAILER_COUPLING_KEY.getTranslatedKeyMessage()) };
		} else if (mainHand instanceof WaypointMarkerItem || offHand instanceof WaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.waypoint_marker.hud_hint") };
		} else if (mainHand instanceof OneWayWaypointMarkerItem || offHand instanceof OneWayWaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.one_way_marker.hud_hint") };
		} else if (mainHand instanceof RailwayWaypointMarkerItem || offHand instanceof RailwayWaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.railway_marker.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.TowRopeItem || offHand instanceof com.examplebett.item.TowRopeItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.tow_rope.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.RailwayStopMarkerItem || offHand instanceof com.examplebett.item.RailwayStopMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.railway_stop_marker.hud_hint") };
		} else if (mainHand instanceof TrafficSignalMarkerItem || offHand instanceof TrafficSignalMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.traffic_signal_marker.hud_hint_edit") };
		} else if (mainHand instanceof com.examplebett.item.SmartDispatchAppItem || offHand instanceof com.examplebett.item.SmartDispatchAppItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.smart_dispatch.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.CustomWrenchItem || offHand instanceof com.examplebett.item.CustomWrenchItem) {

			hintLines = new Component[] { ModText.translatable("gui.mineturismo.custom_wrench.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.WheelWrenchItem || offHand instanceof com.examplebett.item.WheelWrenchItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.wheel_wrench.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.DebugWrenchItem || offHand instanceof com.examplebett.item.DebugWrenchItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.debug_wrench.hud_hint") };
		} else {
			return;
		}
		float scale = 0.85f;
		int padding = 4;
		int lineHeight = 10;
		int maxWidth = 0;
		for (Component line : hintLines) {
			maxWidth = Math.max(maxWidth, com.examplebett.compat.LegacyGui.width(client.font, line));
		}
		int boxWidth = maxWidth + padding * 2;
		int boxHeight = hintLines.length * lineHeight + padding * 2;
		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(scale, scale, 1.0f);
		int boxX = Math.round(4 / scale);
		int boxY = Math.round(4 / scale);
		guiGraphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xFF000000);
		for (int i = 0; i < hintLines.length; i++) {
			guiGraphics.drawString(client.font, hintLines[i], boxX + padding, boxY + padding + i * lineHeight, 0xFFFFFF00, false);
		}
		guiGraphics.pose().popPose();
	}

	private static void renderMountToast(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		net.minecraft.client.gui.Font mountToastFont = com.examplebett.compat.LegacyGui.fontFor(mountToastCustomFontEnabled ? MOUNT_TOAST_FONT : null);
		if (mountToastText == null || mountToastStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - mountToastStartMs;
		if (elapsed >= MOUNT_TOAST_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < MOUNT_TOAST_FADE_IN_MS) {
			alpha = elapsed / (float) MOUNT_TOAST_FADE_IN_MS;
		} else if (elapsed > MOUNT_TOAST_TOTAL_MS - MOUNT_TOAST_FADE_OUT_MS) {
			alpha = (MOUNT_TOAST_TOTAL_MS - elapsed) / (float) MOUNT_TOAST_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));

		int color = ((int) (alpha * 255.0f) << 24) | 0xFFFFFF;

		int x = (int) ((context.guiWidth() - 6) / MOUNT_TOAST_SCALE - com.examplebett.compat.LegacyGui.width(mountToastFont, mountToastText));
		int y = (int) ((context.guiHeight() - 34) / MOUNT_TOAST_SCALE);

		context.pose().pushPose();
		context.pose().scale(MOUNT_TOAST_SCALE, MOUNT_TOAST_SCALE, 1.0f);
		context.drawString(mountToastFont, mountToastText, x, y, color, true);

		if (mountToastCategoryText != null) {
			int categoryX = (int) ((context.guiWidth() - 6) / MOUNT_TOAST_SCALE - com.examplebett.compat.LegacyGui.width(mountToastFont, mountToastCategoryText));
			int categoryY = y + mountToastFont.lineHeight + 2;
			context.drawString(mountToastFont, mountToastCategoryText, categoryX, categoryY, color, true);
		}
		context.pose().popPose();
	}

	private static void renderSpeedDisplay(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (speedDisplayMode == SpeedDisplayMode.NONE || mc.options.hideGui || mc.player == null) return;

		Entity vehicle = mc.player.getVehicle();
		if (!(vehicle instanceof CarEntity car)) return;

		if (vehicle != lastVehicle) {
			return;
		}

		if (mountToastStartMs >= 0 && System.currentTimeMillis() - mountToastStartMs < MOUNT_TOAST_TOTAL_MS) {
			return;
		}

		double blocksPerTick = com.examplebett.compat.ModVec3.horizontalLength(car.getDeltaMovement());
		double kmh = blocksPerTick * 20.0 * 3.6;
		double displaySpeed = speedDisplayMode == SpeedDisplayMode.MPH ? kmh * 0.621371 : kmh;
		String numberText = String.valueOf(Math.round(displaySpeed));
		String unitText = speedDisplayMode == SpeedDisplayMode.MPH ? "mph" : "km/h";

		int color = 0xFFFFFFFF;
		int rightEdge = context.guiWidth() - 6;
		int nameRowY = context.guiHeight() - 34;

		int numberWidth = Math.round(com.examplebett.compat.LegacyGui.width(mc.font, numberText) * SPEED_NUMBER_SCALE);
		int unitWidth = Math.round(com.examplebett.compat.LegacyGui.width(mc.font, unitText) * SPEED_UNIT_SCALE);
		int unitScreenX = rightEdge - unitWidth;
		int numberScreenX = unitScreenX - SPEED_UNIT_GAP - numberWidth;

		int numberHeight = Math.round(mc.font.lineHeight * SPEED_NUMBER_SCALE);
		int unitHeight = Math.round(mc.font.lineHeight * SPEED_UNIT_SCALE);
		int numberScreenY = nameRowY;

		int unitScreenY = nameRowY + (numberHeight - unitHeight);

		context.pose().pushPose();
		context.pose().scale(SPEED_NUMBER_SCALE, SPEED_NUMBER_SCALE, 1.0f);
		context.drawString(mc.font, numberText, Math.round(numberScreenX / SPEED_NUMBER_SCALE), Math.round(numberScreenY / SPEED_NUMBER_SCALE), color, true);
		context.pose().popPose();

		context.pose().pushPose();
		context.pose().scale(SPEED_UNIT_SCALE, SPEED_UNIT_SCALE, 1.0f);
		context.drawString(mc.font, unitText, Math.round(unitScreenX / SPEED_UNIT_SCALE), Math.round(unitScreenY / SPEED_UNIT_SCALE), color, true);
		context.pose().popPose();
	}

	private static void renderPassengerTransportWarning(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (hudWarningText == null || hudWarningStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - hudWarningStartMs;
		if (elapsed >= HUD_WARNING_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < HUD_WARNING_FADE_IN_MS) {
			alpha = elapsed / (float) HUD_WARNING_FADE_IN_MS;
		} else if (elapsed > HUD_WARNING_TOTAL_MS - HUD_WARNING_FADE_OUT_MS) {
			alpha = (HUD_WARNING_TOTAL_MS - elapsed) / (float) HUD_WARNING_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));

		int color = ((int) (alpha * 255.0f) << 24) | 0xFFFFFF;

		context.drawString(mc.font, hudWarningText, 4, 28, color, true);
	}

	private static void renderHintTip(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (!hintsEnabled || hintTipLines == null || hintTipStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - hintTipStartMs;
		if (elapsed >= HINT_TIP_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < HINT_TIP_FADE_IN_MS) {
			alpha = elapsed / (float) HINT_TIP_FADE_IN_MS;
		} else if (elapsed > HINT_TIP_TOTAL_MS - HINT_TIP_FADE_OUT_MS) {
			alpha = (HINT_TIP_TOTAL_MS - elapsed) / (float) HINT_TIP_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));
		int alpha8 = (int) (alpha * 255.0f);

		int textColor = (alpha8 << 24) | 0xFFFFFF;
		int backgroundColor = alpha8 << 24;

		List<String> rows = new ArrayList<>();
		for (Component line : hintTipLines) {
			for (String row : line.getString().split("\n", -1)) {
				rows.add(row);
			}
		}
		int maxWidth = 0;
		for (String row : rows) {
			maxWidth = Math.max(maxWidth, com.examplebett.compat.LegacyGui.width(mc.font, row));
		}
		int lineHeight = mc.font.lineHeight;
		int padding = 4;
		int boxWidth = maxWidth + padding * 2;
		int boxHeight = rows.size() * lineHeight + padding * 2;

		int screenX = 4;
		int screenY = 4;
		context.pose().pushPose();
		context.pose().scale(HINT_TIP_SCALE, HINT_TIP_SCALE, 1.0f);
		int x = Math.round(screenX / HINT_TIP_SCALE);
		int y = Math.round(screenY / HINT_TIP_SCALE);
		context.fill(x, y, x + boxWidth, y + boxHeight, backgroundColor);
		for (int i = 0; i < rows.size(); i++) {
			context.drawString(mc.font, rows.get(i), x + padding, y + padding + i * lineHeight, textColor, true);
		}
		context.pose().popPose();

		Component disableHint = ModText.translatable("gui.mineturismo.hint_tip.disable_instruction",
				ModText.translatable("item.mineturismo.debug_wrench"),
				ModText.translatable("gui.mineturismo.debug_wrench_menu.other"),
				ModText.translatable("gui.mineturismo.debug_wrench_menu.hide_hints"));
		int subLineHeight = mc.font.lineHeight;
		int subBoxWidth = com.examplebett.compat.LegacyGui.width(mc.font, disableHint) + padding * 2;
		int subBoxHeight = subLineHeight + padding * 2;
		int subScreenY = screenY + Math.round(boxHeight * HINT_TIP_SCALE);
		context.pose().pushPose();
		context.pose().scale(HINT_TIP_SUB_SCALE, HINT_TIP_SUB_SCALE, 1.0f);
		int subX = Math.round(screenX / HINT_TIP_SUB_SCALE);
		int subY = Math.round(subScreenY / HINT_TIP_SUB_SCALE);
		context.fill(subX, subY, subX + subBoxWidth, subY + subBoxHeight, backgroundColor);
		context.drawString(mc.font, disableHint, subX + padding, subY + padding, textColor, true);
		context.pose().popPose();
	}
}
*/
//?}
//? if <1.15 {
/*
package com.examplebett.client;
import com.examplebett.compat.ModText;

import com.examplebett.compat.ModClientNetworking;
import com.examplebett.AiObjectMod;
import com.examplebett.block.AiObjectModBlocks;
import com.examplebett.client.gui.CarWrenchScreen;
import com.examplebett.client.gui.RailwayConsistScreen;
import com.examplebett.client.gui.WheelAdjustScreen;
import com.examplebett.client.gui.OneWayMarkerSettingsScreen;
import com.examplebett.client.gui.TrafficSignalSettingsScreen;
import com.examplebett.client.gui.WaypointTrafficSettingsScreen;
import com.examplebett.entity.AiObjectModEntities;
import com.examplebett.entity.BreakableTireStackEntity;
import com.examplebett.entity.BreakableBoxEntity;
import com.examplebett.entity.BreakableConeEntity;
import com.examplebett.entity.CarEntity;
import com.examplebett.client.render.BrakeCaliperModel;
import com.examplebett.client.render.BreakableTireStackRenderer;
import com.examplebett.client.render.CameraAttachmentLoader;
import com.examplebett.client.render.CarEntityRenderer;
import com.examplebett.client.render.GlbModelLoader;
import com.examplebett.client.render.LightAttachmentLoader;
import com.examplebett.client.render.LiveryLoader;
import com.examplebett.client.render.LodWheelModel;
import com.examplebett.client.render.PlateAttachmentLoader;
import com.examplebett.client.render.PoliceLightModel;
import com.examplebett.client.render.RearWingModel;
import com.examplebett.client.render.OtherPartModel;
import com.examplebett.client.render.TaxiSignModel;
import com.examplebett.client.render.WheelAttachmentLoader;
import com.examplebett.client.render.WheelColorLoader;
import com.examplebett.client.render.WheelStyleCatalog;
import com.examplebett.client.sound.EngineSoundInstance;
import com.examplebett.client.sound.HornSoundInstance;
import com.examplebett.client.sound.PoliceSirenSoundInstance;
import com.examplebett.client.sound.TireSlipSoundInstance;
import com.examplebett.item.CarSpawnItem;
import com.examplebett.item.OneWayWaypointMarkerItem;
import com.examplebett.item.RailwayWaypointMarkerItem;
import com.examplebett.item.TrafficSignalMarkerItem;
import com.examplebett.item.WaypointMarkerItem;
import com.examplebett.network.OpenRailwaySpawnSetupPayload;
import com.examplebett.network.OpenRailwayConsistSettingsPayload;
import com.examplebett.network.OpenSignalSettingsPayload;
import com.examplebett.network.PlayCollisionSoundPayload;
import com.examplebett.network.SetDetectedWheelFootprintPayload;
import com.examplebett.network.SetClientSlipReportPayload;
import com.examplebett.network.SetClientWheelieTriggerPayload;
import com.examplebett.network.SetWheelRotationReportPayload;
import com.examplebett.network.ShowHudWarningPayload;
import com.examplebett.network.SyncAmbientEngineVolumePayload;
import com.examplebett.network.SyncRailwayFormationOriginPayload;
import com.examplebett.network.SyncWheelDistancePayload;
import com.examplebett.waypoint.WaypointNetworkSavedData;
import com.examplebett.network.SetHornStatePayload;
import com.examplebett.network.ToggleTrailerCouplingPayload;
import com.examplebett.network.SetPoliceLightsOnPayload;
import com.examplebett.render.CompiledCarModel;
import com.examplebett.render.CustomPartCatalog;
import com.examplebett.render.FrontBrakeCaliperParts;
import com.examplebett.render.ObjModel;
import com.examplebett.render.ObjModelLoader;
import com.examplebett.render.OtherParts;
import com.examplebett.render.PoliceLightParts;
import com.examplebett.render.RearBrakeCaliperParts;
import com.examplebett.render.RearWingParts;
import com.examplebett.render.TaxiSignParts;
import com.examplebett.sound.AiObjectModSounds;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import com.examplebett.compat.v114.HudRenderCallback;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import com.examplebett.compat.v114.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import com.examplebett.compat.ModRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiObjectModClient implements ClientModInitializer {

	public interface ExternalCarModelLoader {
		void tryLoad(CarEntity car);
	}

	public static ExternalCarModelLoader externalCarModelLoader = null;

	private static final long MOUNT_TOAST_TOTAL_MS = 3000L;
	private static final long MOUNT_TOAST_FADE_IN_MS = 300L;
	private static final long MOUNT_TOAST_FADE_OUT_MS = 700L;
	private static final ResourceLocation MOUNT_TOAST_FONT = AiObjectMod.id("mount_toast");
	private static final float MOUNT_TOAST_SCALE = 1.5f;

	private static boolean mountToastCustomFontEnabled = true;

	public static boolean isMountToastCustomFontEnabled() {
		return mountToastCustomFontEnabled;
	}

	public static void setMountToastCustomFontEnabled(boolean enabled) {
		mountToastCustomFontEnabled = enabled;
	}

	private static boolean fakeReflectionEnabled = false;

	public static boolean isFakeReflectionEnabled() {
		return fakeReflectionEnabled;
	}

	public static void setFakeReflectionEnabled(boolean enabled) {
		fakeReflectionEnabled = enabled;
	}

	private static boolean hintsEnabled = true;

	public static boolean isHintsEnabled() {
		return hintsEnabled;
	}

	public static void setHintsEnabled(boolean enabled) {
		hintsEnabled = enabled;
	}

	private static boolean fpsLogEnabled = false;

	public static boolean isFpsLogEnabled() {
		return fpsLogEnabled;
	}

	public static void setFpsLogEnabled(boolean enabled) {
		fpsLogEnabled = enabled;
	}

	private static final long FPS_LOG_INTERVAL_TICKS = 20L;
	private static long fpsLogTicks = 0L;

	private static final java.lang.reflect.Field LEGACY_FPS_FIELD = resolveLegacyFpsField();

	private static java.lang.reflect.Field resolveLegacyFpsField() {
		try {
			java.lang.reflect.Field field = com.examplebett.compat.ModReflection.findField(net.minecraft.client.Minecraft.class, "fps", "field_1738");
			field.setAccessible(true);
			return field;
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}

	private static int getGameFps(net.minecraft.client.Minecraft client) {
		if (LEGACY_FPS_FIELD == null) {
			return -1;
		}
		try {
			return LEGACY_FPS_FIELD.getInt(null);
		} catch (ReflectiveOperationException e) {
			return -1;
		}
	}

	public enum SpeedDisplayMode {
		NONE, KMH, MPH;

		public SpeedDisplayMode next() {
			return switch (this) {
				case NONE -> KMH;
				case KMH -> MPH;
				case MPH -> NONE;
			};
		}
	}

	private static SpeedDisplayMode speedDisplayMode = SpeedDisplayMode.NONE;
	private static final float SPEED_NUMBER_SCALE = 2.0f;
	private static final float SPEED_UNIT_SCALE = 1.0f;
	private static final int SPEED_UNIT_GAP = 3;

	public static SpeedDisplayMode getSpeedDisplayMode() {
		return speedDisplayMode;
	}

	public static void setSpeedDisplayMode(SpeedDisplayMode mode) {
		speedDisplayMode = mode;
	}
	private static final Map<String, Item> MODEL_ID_TO_SPAWN_ITEM = new HashMap<>();

	private static final Map<String, CompiledCarModel> compiledCarModelCache = new HashMap<>();

	private static final Map<String, WheelCompiledPair> compiledWheelModelCache = new HashMap<>();
	private record WheelCompiledPair(CompiledCarModel normal, CompiledCarModel mirrored) {
	}
	private static Entity lastVehicle = null;
	private static Component mountToastText = null;

	private static Component mountToastCategoryText = null;
	private static long mountToastStartMs = -1L;

	private static final long HUD_WARNING_TOTAL_MS = 3000L;
	private static final long HUD_WARNING_FADE_IN_MS = 200L;
	private static final long HUD_WARNING_FADE_OUT_MS = 700L;
	private static Component hudWarningText = null;
	private static long hudWarningStartMs = -1L;

	private record TimedHint(long delayTicks, String... lineKeys) {
	}

	private static final TimedHint[] TIMED_HINTS = {
			new TimedHint(1200L,
					"gui.mineturismo.reflection_tip.line1",
					"gui.mineturismo.reflection_tip.line2",
					"gui.mineturismo.reflection_tip.line3"),
			new TimedHint(1360L,
					"gui.mineturismo.shader_tip.line1",
					"gui.mineturismo.shader_tip.line2"),
			new TimedHint(3600L,
					"gui.mineturismo.smart_dispatch_tip.line1",
					"gui.mineturismo.smart_dispatch_tip.line2"),
			new TimedHint(6000L,
					"gui.mineturismo.wrench_tip.line1",
					"gui.mineturismo.wrench_tip.line2"),
			new TimedHint(12000L,
					"gui.mineturismo.traffic_marker_tip.line1",
					"gui.mineturismo.traffic_marker_tip.line2"),
	};

	private static final long HINT_TIP_TOTAL_MS = 8000L;
	private static final long HINT_TIP_FADE_IN_MS = 300L;
	private static final long HINT_TIP_FADE_OUT_MS = 700L;
	private static final float HINT_TIP_SCALE = 1.1f;

	private static final float HINT_TIP_SUB_SCALE = 0.75f;
	private static int nextHintIndex = 0;
	private static long hintTicks = 0L;
	private static Component[] hintTipLines = null;
	private static long hintTipStartMs = -1L;
	private static boolean creditsScreenChecked = false;

	private static final Map<CarEntity, EngineSoundInstance> engineSounds = new HashMap<>();

	private static final Map<CarEntity, EngineSoundInstance> engineLowSounds = new HashMap<>();

	private static final int WHEEL_ROTATION_REPORT_INTERVAL_TICKS = 2;
	private static int wheelRotationReportTickCounter = 0;

	private static final Map<CarEntity, TireSlipSoundInstance> tireSlipSounds = new HashMap<>();

	private static final Map<CarEntity, PoliceSirenSoundInstance> policeSirenSounds = new HashMap<>();

	private static final Map<CarEntity, EngineSoundInstance> ambientEngineSounds = new HashMap<>();
	private static final double AMBIENT_ENGINE_RANGE = 24.0;
	private static final int MAX_AMBIENT_ENGINE_SOUNDS = 3;
	private static final float AMBIENT_ENGINE_MIN_VOLUME = 0.2f;
	private static final float AMBIENT_ENGINE_MAX_VOLUME = 0.4f;

	private static float ambientEngineVolumeMultiplier = WaypointNetworkSavedData.DEFAULT_AMBIENT_ENGINE_VOLUME_MULTIPLIER;

	private static final String KEY_CATEGORY = "key.categories.mineturismo";
	private static final KeyMapping HORN_KEY = new KeyMapping(
			"key.mineturismo.horn", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, KEY_CATEGORY);

	private static final KeyMapping TRAILER_COUPLING_KEY = new KeyMapping(
			"key.mineturismo.trailer_coupling", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_L, KEY_CATEGORY);

	public static KeyMapping hornKey() {
		return HORN_KEY;
	}

	public static KeyMapping trailerCouplingKey() {
		return TRAILER_COUPLING_KEY;
	}

	private static HornSoundInstance activeHorn = null;

	private static CarEntity hornBroadcastCar = null;

	private static boolean hornKeyWasDown = false;

	private static boolean trailerCouplingKeyWasDown = false;

	private static boolean sprintKeyHeldSent = false;
	private static CarEntity sprintKeyHeldCar = null;

	private static void mineturismo$handlePlayCollisionSound(PlayCollisionSoundPayload payload) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			return;
		}

		net.minecraft.sounds.SoundEvent collisionSound = payload.lowSpeed() ? AiObjectModSounds.CAR_COLLISION_LOW : AiObjectModSounds.CAR_COLLISION;

		SimpleSoundInstance sound = new SimpleSoundInstance(
				collisionSound.getLocation(), SoundSource.NEUTRAL,
				payload.volume(), payload.pitch(),
				false, 0, SoundInstance.Attenuation.NONE,
				(float) payload.x(), (float) payload.y(), (float) payload.z(), false);
		mc.getSoundManager().play(sound);
	}

	private static void mineturismo$handleShowHudWarning(ShowHudWarningPayload payload) {
		hudWarningText = ModText.translatable(payload.messageKey());
		hudWarningStartMs = System.currentTimeMillis();
		Minecraft.getInstance().getSoundManager().play(
				SimpleSoundInstance.forUI(AiObjectModSounds.PASSENGER_TRANSPORT_DENIED, 1.0f));
	}

	@Override
	public void onInitializeClient() {

		KeyBindingHelper.registerKeyBinding(HORN_KEY);
		KeyBindingHelper.registerKeyBinding(TRAILER_COUPLING_KEY);

		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
				new SimpleSynchronousResourceReloadListener() {
					@Override
					public ResourceLocation getFabricId() {
						return AiObjectMod.id("clear_render_caches");
					}

					@Override
					public void onResourceManagerReload(ResourceManager resourceManager) {
						ObjModelLoader.clearCache();
						GlbModelLoader.clearCache();
						WheelAttachmentLoader.clearCache();
						WheelColorLoader.clearCache();
						LightAttachmentLoader.clearCache();
						com.examplebett.client.render.VehicleCategoryLoader.clearCache();
						PlateAttachmentLoader.clearCache();
						CameraAttachmentLoader.clearCache();
						LodWheelModel.clearCache();
						LiveryLoader.clearCache();
						RearWingModel.clearCache();
						PoliceLightModel.clearCache();
						TaxiSignModel.clearCache();
						OtherPartModel.clearCache();
						BrakeCaliperModel.clearCache();
						CustomPartCatalog.clearCache();
						WheelStyleCatalog.clearCache();
						RearWingParts.clearCache();
						OtherParts.clearCache();
						PoliceLightParts.clearCache();
						TaxiSignParts.clearCache();
						FrontBrakeCaliperParts.clearCache();
						RearBrakeCaliperParts.clearCache();
						compiledCarModelCache.clear();
						compiledWheelModelCache.clear();
					}
				});

		ModClientNetworking.registerS2C(PlayCollisionSoundPayload.ID, PlayCollisionSoundPayload::read, AiObjectModClient::mineturismo$handlePlayCollisionSound);

		ModClientNetworking.registerS2C(SyncWheelDistancePayload.ID, SyncWheelDistancePayload::read, payload -> {
			Minecraft mc = Minecraft.getInstance();
			if (mc.level == null) {
				return;
			}
			if (mc.level.getEntity(payload.entityId()) instanceof CarEntity car) {
				car.setNetworkWheelDistance(payload.distance());
			}
		});

		ModClientNetworking.registerS2C(OpenRailwayConsistSettingsPayload.ID, OpenRailwayConsistSettingsPayload::read, payload -> {
			net.minecraft.client.multiplayer.MultiPlayerLevel mineturismo$level = Minecraft.getInstance().level;
			if (mineturismo$level != null && mineturismo$level.getEntity(payload.headEntityId()) instanceof CarEntity car) {
				Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
				Minecraft.getInstance().setScreen(new RailwayConsistScreen(car, null));
			}
		});

		ModClientNetworking.registerS2C(OpenSignalSettingsPayload.ID, OpenSignalSettingsPayload::read, payload -> {
			char direction = payload.direction().isEmpty() ? 'N' : payload.direction().charAt(0);
			TrafficSignalSettingsScreen.applyExternalConfig(payload.signalId(), payload.intersectionName(), direction,
					payload.greenTicks(), payload.redTicks(), payload.phaseOffsetTicks());
			Minecraft.getInstance().setScreen(new TrafficSignalSettingsScreen());
		});

		ModClientNetworking.registerS2C(OpenRailwaySpawnSetupPayload.ID, OpenRailwaySpawnSetupPayload::read, payload -> {
			Minecraft.getInstance().setScreen(new com.examplebett.client.gui.RailwaySpawnVehicleScreen(payload.originNodeId()));
		});

		ModClientNetworking.registerS2C(SyncRailwayFormationOriginPayload.ID, SyncRailwayFormationOriginPayload::read, payload -> {
			com.examplebett.inventory.RailwayFormationMenu.pendingOriginNodeId = payload.originNodeId();
		});

		ModClientNetworking.registerS2C(SyncAmbientEngineVolumePayload.ID, SyncAmbientEngineVolumePayload::read, payload -> {
			ambientEngineVolumeMultiplier = payload.multiplier();
			com.examplebett.client.gui.WaypointClientSettings.apply(payload.multiplier(), payload.settings());
		});

		ModClientNetworking.registerS2C(ShowHudWarningPayload.ID, ShowHudWarningPayload::read, AiObjectModClient::mineturismo$handleShowHudWarning);

		CarEntity.wrenchMenuOpener = (car, player) -> {
			Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
			Minecraft.getInstance().setScreen(new CarWrenchScreen(car));
		};
		CarEntity.wheelWrenchMenuOpener = (car, player) -> {
			Minecraft.getInstance().options.setCameraType(CameraType.THIRD_PERSON_BACK);
			Minecraft.getInstance().setScreen(new WheelAdjustScreen(car));
		};
		CarEntity.debugWrenchMenuOpener = (car, player) -> Minecraft.getInstance().setScreen(new com.examplebett.client.gui.DebugWrenchScreen(car));
		WaypointMarkerItem.trafficSettingsMenuOpener = player -> Minecraft.getInstance().setScreen(new WaypointTrafficSettingsScreen());
		OneWayWaypointMarkerItem.settingsMenuOpener = player -> Minecraft.getInstance().setScreen(new OneWayMarkerSettingsScreen());
		com.examplebett.item.SmartDispatchAppItem.appScreenOpener = player -> Minecraft.getInstance().setScreen(new com.examplebett.client.gui.SmartDispatchAppScreen());
		ClientTickEvents.END_CLIENT_TICK.register(com.examplebett.client.GttClient::tick);
		ClientTickEvents.END_CLIENT_TICK.register(com.examplebett.client.sound.ChorusMusicPlayer::tick);

		ModClientNetworking.registerS2C(com.examplebett.network.GttScorePayload.ID, com.examplebett.network.GttScorePayload::read, payload -> com.examplebett.client.GttClient.setScore(payload.score()));

		ModClientNetworking.registerS2C(com.examplebett.network.SpawnLegacyEntityPayload.ID, com.examplebett.network.SpawnLegacyEntityPayload::read, payload -> {
			Minecraft mineturismo$mc = Minecraft.getInstance();
			if (mineturismo$mc.level == null) {
				return;
			}
			net.minecraft.world.entity.Entity mineturismo$entity = com.examplebett.compat.ModRegistries.ENTITY_TYPE.get(payload.typeId).create(mineturismo$mc.level);
			if (mineturismo$entity == null) {
				return;
			}
			mineturismo$entity.setPacketCoordinates(payload.x, payload.y, payload.z);
			mineturismo$entity.moveTo(payload.x, payload.y, payload.z, payload.yRot, 0.0f);
			mineturismo$entity.setId(payload.entityId);
			mineturismo$entity.setUUID(payload.uuid);
			mineturismo$mc.level.putNonPlayerEntity(payload.entityId, mineturismo$entity);
		});

		ModClientNetworking.registerS2C(com.examplebett.network.SlotSpinResultPayload.ID, com.examplebett.network.SlotSpinResultPayload::read, payload -> com.examplebett.client.gui.SmartDispatchAppScreen.onSlotResult(payload.prizes()));

		HudRenderCallback.EVENT.register(tickCounter -> com.examplebett.client.GttClient.renderHud(new GuiGraphics(null)));

		net.minecraft.client.gui.screens.MenuScreens.register(com.examplebett.inventory.AiObjectModMenus.RAILWAY_FORMATION_MENU_TYPE, com.examplebett.client.gui.RailwayFormationScreen::new);
		net.minecraft.client.gui.screens.MenuScreens.register(com.examplebett.inventory.AiObjectModMenus.MASSIVE_AUTO_APPRAISAL_MENU_TYPE, com.examplebett.client.gui.MassiveAutoAppraisalScreen::new);

		HudRenderCallback.EVENT.register(deltaTracker -> mineturismo$renderWaypointHint(new GuiGraphics(null)));

		CarEntity.clientDescendKeyHeld = () -> Minecraft.getInstance().options.keySprint.isDown();

		CarEntity.clientForwardKeyHeld = () -> Minecraft.getInstance().options.keyUp.isDown();
		CarEntity.clientBackKeyHeld = () -> Minecraft.getInstance().options.keyDown.isDown();

		CarEntity.clientDimensionsResourceManager = () -> {
			var integratedServer = Minecraft.getInstance().getSingleplayerServer();
			return integratedServer != null ? com.examplebett.compat.ModResources.getServerResourceManager(integratedServer) : null;
		};

		for (Item item : ModRegistries.ITEM) {
			if (item instanceof CarSpawnItem spawnItem) {
				MODEL_ID_TO_SPAWN_ITEM.putIfAbsent(spawnItem.getModelId(), item);
			}
		}

		HudRenderCallback.EVENT.register(tickCounter -> renderMountToast(new GuiGraphics(null)));

		HudRenderCallback.EVENT.register(tickCounter -> renderSpeedDisplay(new GuiGraphics(null)));

		HudRenderCallback.EVENT.register(tickCounter -> renderPassengerTransportWarning(new GuiGraphics(null)));

		HudRenderCallback.EVENT.register(tickCounter -> renderHintTip(new GuiGraphics(null)));

		final boolean[] mineturismo$wasInWorld = {false};
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.level != null && !mineturismo$wasInWorld[0]) {
				nextHintIndex = 0;
				hintTicks = 0L;
				hintTipStartMs = -1L;
			}
			mineturismo$wasInWorld[0] = client.level != null;
		});

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			boolean mineturismo$pressed = false;
			while (TRAILER_COUPLING_KEY.consumeClick()) {
				mineturismo$pressed = true;
			}

			InputConstants.Key mineturismo$boundKey = KeyBindingHelper.getBoundKeyOf(TRAILER_COUPLING_KEY);
			boolean mineturismo$rawDown = mineturismo$boundKey.getType() == InputConstants.Type.KEYSYM
					&& InputConstants.isKeyDown(client.window.getWindow(), mineturismo$boundKey.getValue());
			if (mineturismo$rawDown && !trailerCouplingKeyWasDown && client.screen == null) {
				mineturismo$pressed = true;
			}
			trailerCouplingKeyWasDown = mineturismo$rawDown;

			if (client.player != null && client.screen == null
					&& client.player.getVehicle() instanceof CarEntity mineturismo$headCar
					&& mineturismo$headCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
				for (KeyMapping mineturismo$other : client.options.keyMappings) {
					if (mineturismo$other != TRAILER_COUPLING_KEY && mineturismo$other.same(TRAILER_COUPLING_KEY)) {
						while (mineturismo$other.consumeClick()) {
							mineturismo$pressed = true;
						}
					}
				}
			}
			if (!mineturismo$pressed || client.player == null || client.screen != null
					|| !(client.player.getVehicle() instanceof CarEntity mineturismo$car)
					|| !mineturismo$car.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
				return;
			}
			for (KeyMapping mineturismo$other : client.options.keyMappings) {
				if (mineturismo$other != TRAILER_COUPLING_KEY && mineturismo$other.same(TRAILER_COUPLING_KEY)) {
					while (mineturismo$other.consumeClick()) {

					}
				}
			}
			ToggleTrailerCouplingPayload mineturismo$payload = new ToggleTrailerCouplingPayload(mineturismo$car.getId());
			ModClientNetworking.sendToServer(ToggleTrailerCouplingPayload.ID, mineturismo$payload, ToggleTrailerCouplingPayload::write);
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (creditsScreenChecked) return;
			if (client.player == null || client.level == null) return;
			creditsScreenChecked = true;
			com.examplebett.client.gui.CreditsScreen.showOnceIfNeeded();
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!fpsLogEnabled) return;
			fpsLogTicks++;
			if (fpsLogTicks % FPS_LOG_INTERVAL_TICKS != 0) return;
			int fps = getGameFps(client);
			int carCount = 0;
			if (client.level != null) {
				for (Entity entity : client.level.entitiesForRendering()) {
					if (entity instanceof CarEntity) {
						carCount++;
					}
				}
			}
			AiObjectMod.LOGGER.info("[fps-log] fps={} frameTimeMs={} carCount={}", fps,
					fps > 0 ? String.format("%.1f", 1000.0 / fps) : "n/a", carCount);
		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (hintsEnabled && client.player != null) {
				hintTicks++;
				while (nextHintIndex < TIMED_HINTS.length && hintTicks >= TIMED_HINTS[nextHintIndex].delayTicks()) {
					TimedHint hint = TIMED_HINTS[nextHintIndex];
					hintTipLines = new Component[hint.lineKeys().length];
					for (int i = 0; i < hint.lineKeys().length; i++) {
						hintTipLines[i] = ModText.translatable(hint.lineKeys()[i]);
					}
					hintTipStartMs = System.currentTimeMillis();
					client.getSoundManager().play(
							SimpleSoundInstance.forUI(AiObjectModSounds.PASSENGER_TRANSPORT_DENIED, 1.0f));
					nextHintIndex++;
				}
			}

			CarEntity drivingCar = null;
			if (client.player != null) {
				Entity vehicle = client.player.getVehicle();
				if (vehicle instanceof CarEntity car) {
					drivingCar = car;
				}
				if (vehicle instanceof CarEntity car && vehicle != lastVehicle) {
					mountToastText = getCarDisplayName(car);
					if (car.getVehicleCategories() == null) {
						com.examplebett.client.render.VehicleCategoryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setVehicleCategories);
					}
					mountToastCategoryText = getCarCategoryText(car);
					mountToastStartMs = System.currentTimeMillis();
				}
				lastVehicle = vehicle;
			}

			if (drivingCar != null) {
				EngineSoundInstance existing = engineSounds.get(drivingCar);

				if (existing != null && !existing.matchesCurrentSoundId()) {
					client.getSoundManager().stop(existing);
					existing = null;
				}

				if (existing == null || existing.isReadyForSuccessor()) {
					EngineSoundInstance sound = new EngineSoundInstance(drivingCar, 1.0f, false,
							drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)
									? EngineSoundInstance.Layer.HYBRID_HIGH : EngineSoundInstance.Layer.NORMAL);
					client.getSoundManager().play(sound);
					engineSounds.put(drivingCar, sound);
				}

				if (drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)) {
					EngineSoundInstance lowExisting = engineLowSounds.get(drivingCar);
					if (lowExisting == null || lowExisting.isReadyForSuccessor()) {
						EngineSoundInstance lowSound = new EngineSoundInstance(drivingCar, 1.0f, false, EngineSoundInstance.Layer.HYBRID_LOW);
						client.getSoundManager().play(lowSound);
						engineLowSounds.put(drivingCar, lowSound);
					}
				}
			}

			CarEntity currentDrivingCar = drivingCar;
			engineSounds.entrySet().removeIf(e -> {
				if (e.getKey() != currentDrivingCar) {
					client.getSoundManager().stop(e.getValue());
					return true;
				}
				return e.getValue().isStopped();
			});

			engineLowSounds.entrySet().removeIf(e -> {
				if (e.getKey() != currentDrivingCar || !e.getKey().hasVehicleCategory(com.examplebett.entity.VehicleCategory.HYBRID)) {
					client.getSoundManager().stop(e.getValue());
					return true;
				}
				return e.getValue().isStopped();
			});

			wheelRotationReportTickCounter++;
			if (drivingCar != null && wheelRotationReportTickCounter % WHEEL_ROTATION_REPORT_INTERVAL_TICKS == 0) {
				SetWheelRotationReportPayload mineturismo$payload = new SetWheelRotationReportPayload(
						drivingCar.getId(), drivingCar.getWheelDistanceTraveled());
				ModClientNetworking.sendToServer(SetWheelRotationReportPayload.ID, mineturismo$payload, SetWheelRotationReportPayload::write);
			}

			if (drivingCar != null && HORN_KEY.isDown()
					&& com.examplebett.entity.HornSirenSounds.hornById(drivingCar.getHornSoundId()).sound() != null) {
				if (activeHorn == null || activeHorn.isStopped() || !activeHorn.isFor(drivingCar)) {
					if (activeHorn != null) {
						client.getSoundManager().stop(activeHorn);
					}
					activeHorn = new HornSoundInstance(drivingCar);
					client.getSoundManager().play(activeHorn);
				} else {
					activeHorn.requestPlaying();
				}
			} else if (activeHorn != null) {
				activeHorn.requestRelease();
				if (activeHorn.isStopped()) {
					activeHorn = null;
				}
			}

			boolean hornDown = drivingCar != null && HORN_KEY.isDown();
			if (hornDown) {
				if (hornBroadcastCar != drivingCar) {
					if (hornBroadcastCar != null) {
						SetHornStatePayload mineturismo$payload = new SetHornStatePayload(hornBroadcastCar.getId(), false);
						ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
					}
					SetHornStatePayload mineturismo$payload = new SetHornStatePayload(drivingCar.getId(), true);
					ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
					hornBroadcastCar = drivingCar;
				}
			} else if (hornBroadcastCar != null) {
				SetHornStatePayload mineturismo$payload = new SetHornStatePayload(hornBroadcastCar.getId(), false);
				ModClientNetworking.sendToServer(SetHornStatePayload.ID, mineturismo$payload, SetHornStatePayload::write);
				hornBroadcastCar = null;
			}

			boolean hornKeyDown = HORN_KEY.isDown();
			if (drivingCar != null && hornKeyDown && !hornKeyWasDown && !drivingCar.getPoliceLightEnabledIds().isEmpty()) {

				boolean newLightsOn;
				boolean newSirenOn;
				if (!drivingCar.isPoliceLightsOn()) {

					newLightsOn = true;
					newSirenOn = true;
				} else if (drivingCar.isPoliceSirenOn()) {

					newLightsOn = true;
					newSirenOn = false;
				} else {

					newLightsOn = false;
					newSirenOn = false;
				}
				drivingCar.setPoliceLightsOn(newLightsOn);
				drivingCar.setPoliceSirenOn(newSirenOn);
				SetPoliceLightsOnPayload mineturismo$payload = new SetPoliceLightsOnPayload(drivingCar.getId(), newLightsOn, newSirenOn);
				ModClientNetworking.sendToServer(SetPoliceLightsOnPayload.ID, mineturismo$payload, SetPoliceLightsOnPayload::write);
			}
			hornKeyWasDown = hornKeyDown;

			if (drivingCar != null && drivingCar.consumeClientSlipFlag()) {
				SetClientSlipReportPayload mineturismo$payload = new SetClientSlipReportPayload(drivingCar.getId());
				ModClientNetworking.sendToServer(SetClientSlipReportPayload.ID, mineturismo$payload, SetClientSlipReportPayload::write);
			}

			if (drivingCar != null && drivingCar.consumeClientWheelieTriggerFlag()) {
				SetClientWheelieTriggerPayload mineturismo$wheeliePayload = new SetClientWheelieTriggerPayload(drivingCar.getId());
				ModClientNetworking.sendToServer(SetClientWheelieTriggerPayload.ID, mineturismo$wheeliePayload, SetClientWheelieTriggerPayload::write);
			}

			boolean mineturismo$sprintDown = drivingCar != null && client.options.keySprint.isDown();
			if (drivingCar != null && (mineturismo$sprintDown != sprintKeyHeldSent || drivingCar != sprintKeyHeldCar)) {
				ModClientNetworking.sendToServer(com.examplebett.network.SetSprintKeyHeldPayload.ID, new com.examplebett.network.SetSprintKeyHeldPayload(drivingCar.getId(), mineturismo$sprintDown), com.examplebett.network.SetSprintKeyHeldPayload::write);
				sprintKeyHeldSent = mineturismo$sprintDown;
				sprintKeyHeldCar = drivingCar;
			} else if (drivingCar == null) {
				sprintKeyHeldSent = false;
				sprintKeyHeldCar = null;
			}

			if (client.level == null) return;

			List<CarEntity> ambientEngineCandidates = new ArrayList<>();
			for (Entity entity : client.level.entitiesForRendering()) {
				if (entity instanceof CarEntity car) {
					if (car.getCompiledModel() == null) {
						if (car.getModelId().startsWith(CarEntity.EXTERNAL_MODEL_PREFIX)) {
							if (externalCarModelLoader != null) {
								try {
									externalCarModelLoader.tryLoad(car);
								} catch (Exception e) {
									AiObjectMod.LOGGER.error("Failed to load external model: " + car.getModelId(), e);
								}
							}
						} else {
							try {
								ResourceLocation glbLocation = AiObjectMod.id("models/" + car.getModelId() + ".glb");

								if (client.getResourceManager().hasResource(glbLocation)) {
									var result = GlbModelLoader.load(client.getResourceManager(), car.getModelId());
									car.setModel(result.model());
									car.setMaterials(result.materials());
									car.setCompiledModel(compiledCarModelCache.computeIfAbsent(car.getModelId(),
											id -> CompiledCarModel.compile(result.model(), result.materials(), CompiledCarModel.FALLBACK_TEXTURE, false, id)));
								} else {
									var model = ObjModelLoader.loadModel(client.getResourceManager(), car.getModelId());
									var materials = ObjModelLoader.loadMaterials(client.getResourceManager(), car.getModelId());
									car.setModel(model);
									car.setMaterials(materials);

									car.setCompiledModel(compiledCarModelCache.computeIfAbsent(car.getModelId(),
											id -> CompiledCarModel.compile(model, materials, CompiledCarModel.FALLBACK_TEXTURE)));
								}
							} catch (Exception e) {
								AiObjectMod.LOGGER.error("Failed to load model: " + car.getModelId(), e);
							}
						}
					}

					CompiledCarModel compiledForWheelDetect = car.getCompiledModel();
					if (compiledForWheelDetect != null && !car.hasDetectedWheelFootprint()
							&& compiledForWheelDetect.wheelPivots.size() == 4) {
						float sumAbsX = 0f;
						float sumFrontZ = 0f;
						int frontCount = 0;
						for (ObjModel.WheelPivot pivot : compiledForWheelDetect.wheelPivots) {
							sumAbsX += Math.abs(pivot.centerX);
							if (pivot.front) {
								sumFrontZ += pivot.centerZ;
								frontCount++;
							}
						}
						if (frontCount > 0) {
							float trackHalfWidth = sumAbsX / compiledForWheelDetect.wheelPivots.size();
							float rearZ = sumFrontZ / frontCount;
							car.setDetectedWheelFootprint(trackHalfWidth, rearZ);

							SetDetectedWheelFootprintPayload mineturismo$payload = new SetDetectedWheelFootprintPayload(car.getId(), trackHalfWidth, rearZ);
							ModClientNetworking.sendToServer(SetDetectedWheelFootprintPayload.ID, mineturismo$payload, SetDetectedWheelFootprintPayload::write);
							AiObjectMod.LOGGER.info("Detected wheel footprint for {}: trackHalfWidth={}, rearZ={}",
									car.getModelId(), trackHalfWidth, rearZ);
						}
					}

					if (car.getWheelAttachment() == null) {
						WheelAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setWheelAttachment);
					}

					com.examplebett.render.WheelAttachment attachment = car.getWheelAttachment();
					if (attachment != null) {

						String override = car.getWheelModelIdOverride();
						String desiredWheelModelId = (override != null && !override.isEmpty())
								? override : attachment.wheelModelId();
						boolean isNoneStyle = com.examplebett.render.WheelAttachment.NONE_STYLE.equals(desiredWheelModelId);

						car.setWheelStyleDefaultWidthScale(isNoneStyle ? 1f
								: WheelStyleCatalog.getDefaultWheelWidthScale(client.getResourceManager(), desiredWheelModelId));

						String colorOverride = car.getWheelColorOverride();
						String desiredWheelColorId = "";
						if (!isNoneStyle) {
							var colorOptions = WheelColorLoader.load(client.getResourceManager(), desiredWheelModelId);
							if (colorOptions.isPresent() && !colorOptions.get().ids().isEmpty()) {
								List<String> colorIds = colorOptions.get().ids();
								desiredWheelColorId = (colorOverride != null && colorIds.contains(colorOverride))
										? colorOverride : colorIds.get(0);
							}
						}
						boolean styleChanged = !desiredWheelModelId.equals(car.getLoadedWheelModelId());
						boolean colorChanged = !desiredWheelColorId.equals(car.getLoadedWheelColorId());
						if (styleChanged || colorChanged) {
							if (isNoneStyle) {

								car.setWheelCompiledModel(null);
								car.setWheelCompiledModelMirrored(null);
								car.setLoadedWheelModelId(desiredWheelModelId);
								car.setLoadedWheelColorId(desiredWheelColorId);
							} else {
								try {

									final String wheelModelIdForCache = desiredWheelModelId;
									final String wheelColorIdForCache = desiredWheelColorId;
									String wheelCacheKey = wheelModelIdForCache + "#" + wheelColorIdForCache;
									WheelCompiledPair compiledPair = compiledWheelModelCache.computeIfAbsent(wheelCacheKey, key -> {

										try {
											var wheelModel = ObjModelLoader.loadModel(client.getResourceManager(), wheelModelIdForCache);
											var wheelMaterials = ObjModelLoader.loadMaterials(client.getResourceManager(), wheelModelIdForCache);
											if (!wheelColorIdForCache.isEmpty()) {

												wheelMaterials = ObjModelLoader.loadLiveryMaterials(
														client.getResourceManager(), wheelModelIdForCache, wheelMaterials, wheelColorIdForCache);
											}

											CompiledCarModel compiled = CompiledCarModel.compile(
													wheelModel, wheelMaterials, CompiledCarModel.FALLBACK_TEXTURE, true);
											return new WheelCompiledPair(compiled, CompiledCarModel.mirrorX(compiled));
										} catch (java.io.IOException wheelIoError) {
											throw new java.io.UncheckedIOException(wheelIoError);
										}
									});
									car.setWheelCompiledModel(compiledPair.normal());
									car.setWheelCompiledModelMirrored(compiledPair.mirrored());
									car.setLoadedWheelModelId(desiredWheelModelId);
									car.setLoadedWheelColorId(desiredWheelColorId);
								} catch (Exception wheelLoadError) {
									AiObjectMod.LOGGER.error("Failed to load wheel model: " + desiredWheelModelId, wheelLoadError);
								}
							}
						}
					}

					if (car.getLightAttachment() == null) {
						com.examplebett.client.render.LightAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setLightAttachment);
					}

					if (car.getCameraAttachment() == null) {
						com.examplebett.client.render.CameraAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setCameraAttachment);
					}

					if (car.getPlateAttachment() == null) {
						com.examplebett.client.render.PlateAttachmentLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setPlateAttachment);
					}

					if (car.getVehicleCategories() == null) {
						com.examplebett.client.render.VehicleCategoryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setVehicleCategories);
					}

					if (car.getLiveryOptions() == null) {
						com.examplebett.client.render.LiveryLoader.load(client.getResourceManager(), car.getModelId())
								.ifPresent(car::setLiveryOptions);
					}

					com.examplebett.render.LiveryOptions liveryOptions = car.getLiveryOptions();
					if (liveryOptions != null && !liveryOptions.ids().isEmpty() && car.getModel() != null) {
						String liveryOverride = car.getLiveryOverride();
						String desiredLivery = (liveryOverride != null && !liveryOverride.isEmpty())
								? liveryOverride : liveryOptions.ids().get(0);
						if (!desiredLivery.equals(car.getLoadedLiveryId())) {
							try {
								var liveryMaterials = ObjModelLoader.loadLiveryMaterials(
										client.getResourceManager(), car.getModelId(), car.getMaterials(), desiredLivery);
								String liveryCacheKey = car.getModelId() + "#" + desiredLivery;
								var carModel = car.getModel();
								car.setCompiledModel(compiledCarModelCache.computeIfAbsent(liveryCacheKey,
										key -> liveryOptions.isDecal(desiredLivery)
												? CompiledCarModel.compileBodyAndDecal(carModel, liveryMaterials, CompiledCarModel.FALLBACK_TEXTURE, false)
												: CompiledCarModel.compile(carModel, liveryMaterials, CompiledCarModel.FALLBACK_TEXTURE)));
								car.setLoadedLiveryId(desiredLivery);
							} catch (Exception liveryLoadError) {
								AiObjectMod.LOGGER.error("Failed to load livery: " + desiredLivery, liveryLoadError);
							}
						}
					}

					if (car.isTownCar() || car.isRailwayRouteVehicle()) {

						if (client.player != null && !ambientEngineSounds.containsKey(car)
								&& car.distanceToSqr(client.player) <= AMBIENT_ENGINE_RANGE * AMBIENT_ENGINE_RANGE) {
							ambientEngineCandidates.add(car);
						}
					} else {

						car.updateClientSlideIntensity(car == drivingCar);
						TireSlipSoundInstance slipSound = tireSlipSounds.get(car);
						if ((slipSound == null || slipSound.isStopped()) && car.getClientSlideIntensity() > 0.0f) {
							slipSound = new TireSlipSoundInstance(car);
							client.getSoundManager().play(slipSound);
							tireSlipSounds.put(car, slipSound);
						}
					}

					PoliceSirenSoundInstance sirenSound = policeSirenSounds.get(car);
					if (car.isPoliceSirenOn()) {

						if (sirenSound != null && !sirenSound.isStopped() && sirenSound.isSoundOutdated()) {
							sirenSound.requestRelease();
							sirenSound = null;
						}

						boolean sirenHasSound = com.examplebett.entity.HornSirenSounds.sirenById(car.getSirenSoundId()).sound() != null;
						if (sirenHasSound && (sirenSound == null || sirenSound.isReadyForSuccessor())) {
							PoliceSirenSoundInstance successor = new PoliceSirenSoundInstance(car);
							client.getSoundManager().play(successor);
							policeSirenSounds.put(car, successor);
						}
					} else if (sirenSound != null && !sirenSound.isStopped()) {
						sirenSound.requestRelease();
					}
				} else if (entity instanceof BreakableTireStackEntity tireStack) {
					if (tireStack.getCompiledModel() == null) {
						try {
							var model = ObjModelLoader.loadModel(client.getResourceManager(), tireStack.getModelId());
							var materials = ObjModelLoader.loadMaterials(client.getResourceManager(), tireStack.getModelId());
							tireStack.setModel(model);
							tireStack.setMaterials(materials);
							tireStack.setCompiledModel(CompiledCarModel.compile(model, materials, CompiledCarModel.FALLBACK_TEXTURE));
						} catch (Exception e) {
							AiObjectMod.LOGGER.error("Failed to load model: " + tireStack.getModelId(), e);
						}
					}
				}
			}

			tireSlipSounds.entrySet().removeIf(entry -> entry.getValue().isStopped());

			policeSirenSounds.entrySet().removeIf(entry -> entry.getValue().isStopped());

			ambientEngineSounds.entrySet().removeIf(entry -> {
				CarEntity car = entry.getKey();
				boolean outOfRange = client.player == null
						|| car.distanceToSqr(client.player) > AMBIENT_ENGINE_RANGE * AMBIENT_ENGINE_RANGE;
				if (entry.getValue().isStopped() || !car.isAlive() || outOfRange) {
					client.getSoundManager().stop(entry.getValue());
					return true;
				}
				return false;
			});

			for (Map.Entry<CarEntity, EngineSoundInstance> entry : ambientEngineSounds.entrySet()) {
				if (entry.getValue().isReadyForSuccessor()) {
					EngineSoundInstance successor = new EngineSoundInstance(entry.getKey(), entry.getValue().getVolume(), true);
					client.getSoundManager().play(successor);
					entry.setValue(successor);
				}
			}
			while (ambientEngineSounds.size() < MAX_AMBIENT_ENGINE_SOUNDS && !ambientEngineCandidates.isEmpty()) {
				CarEntity picked = ambientEngineCandidates.remove(client.level.random.nextInt(ambientEngineCandidates.size()));
				float volume = (AMBIENT_ENGINE_MIN_VOLUME
						+ client.level.random.nextFloat() * (AMBIENT_ENGINE_MAX_VOLUME - AMBIENT_ENGINE_MIN_VOLUME))
						* ambientEngineVolumeMultiplier;
				EngineSoundInstance ambientSound = new EngineSoundInstance(picked, volume, true);
				client.getSoundManager().play(ambientSound);
				ambientEngineSounds.put(picked, ambientSound);
			}
		});
	}

	private static Component getCarDisplayName(CarEntity car) {
		Item item = MODEL_ID_TO_SPAWN_ITEM.get(car.getModelId());
		Component name = item != null ? new ItemStack(item).getHoverName() : ModText.literal(car.getModelId());
		if (!mountToastCustomFontEnabled) {
			return name;
		}

		return name.copy();
	}

	private static Component getCarCategoryText(CarEntity car) {
		java.util.Set<com.examplebett.entity.VehicleCategory> categories = car.getVehicleCategories();
		if (categories == null || categories.isEmpty()) {
			return null;
		}
		java.util.List<com.examplebett.entity.VehicleCategory> sorted = new java.util.ArrayList<>(categories);
		sorted.sort(java.util.Comparator.naturalOrder());
		net.minecraft.network.chat.Component joined = ModText.literal("");
		for (int i = 0; i < sorted.size(); i++) {
			if (i > 0) {
				joined.append(" / ");
			}
			joined.append(ModText.translatable(sorted.get(i).translationKey));
		}
		if (!mountToastCustomFontEnabled) {
			return joined;
		}
		return joined;
	}

	private static boolean mineturismo$canToggleTrailerCoupling(CarEntity drivingCar) {
		if (!drivingCar.hasVehicleCategory(com.examplebett.entity.VehicleCategory.TRAILER_HEAD)) {
			return false;
		}
		return drivingCar.findCoupledTrailer() != null || drivingCar.findCouplableTrailer() != null;
	}

	private static void mineturismo$renderWaypointHint(GuiGraphics guiGraphics) {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.screen != null) {
			return;
		}
		Item mainHand = client.player.getMainHandItem().getItem();
		Item offHand = client.player.getOffhandItem().getItem();
		Component[] hintLines;
		if (com.examplebett.client.GttClient.isActive()) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.gtt.hud_hint") };
		} else if (client.player.getVehicle() instanceof CarEntity mineturismo$hintCar && mineturismo$canToggleTrailerCoupling(mineturismo$hintCar)) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.trailer.hud_hint", TRAILER_COUPLING_KEY.getTranslatedKeyMessage()) };
		} else if (mainHand instanceof WaypointMarkerItem || offHand instanceof WaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.waypoint_marker.hud_hint") };
		} else if (mainHand instanceof OneWayWaypointMarkerItem || offHand instanceof OneWayWaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.one_way_marker.hud_hint") };
		} else if (mainHand instanceof RailwayWaypointMarkerItem || offHand instanceof RailwayWaypointMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.railway_marker.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.TowRopeItem || offHand instanceof com.examplebett.item.TowRopeItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.tow_rope.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.RailwayStopMarkerItem || offHand instanceof com.examplebett.item.RailwayStopMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.railway_stop_marker.hud_hint") };
		} else if (mainHand instanceof TrafficSignalMarkerItem || offHand instanceof TrafficSignalMarkerItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.traffic_signal_marker.hud_hint_edit") };
		} else if (mainHand instanceof com.examplebett.item.SmartDispatchAppItem || offHand instanceof com.examplebett.item.SmartDispatchAppItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.smart_dispatch.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.CustomWrenchItem || offHand instanceof com.examplebett.item.CustomWrenchItem) {

			hintLines = new Component[] { ModText.translatable("gui.mineturismo.custom_wrench.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.WheelWrenchItem || offHand instanceof com.examplebett.item.WheelWrenchItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.wheel_wrench.hud_hint") };
		} else if (mainHand instanceof com.examplebett.item.DebugWrenchItem || offHand instanceof com.examplebett.item.DebugWrenchItem) {
			hintLines = new Component[] { ModText.translatable("gui.mineturismo.debug_wrench.hud_hint") };
		} else {
			return;
		}
		float scale = 0.85f;
		int padding = 4;
		int lineHeight = 10;
		int maxWidth = 0;
		for (Component line : hintLines) {
			maxWidth = Math.max(maxWidth, com.examplebett.compat.LegacyGui.width(client.font, line));
		}
		int boxWidth = maxWidth + padding * 2;
		int boxHeight = hintLines.length * lineHeight + padding * 2;
		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(scale, scale, 1.0f);
		int boxX = Math.round(4 / scale);
		int boxY = Math.round(4 / scale);
		guiGraphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xFF000000);
		for (int i = 0; i < hintLines.length; i++) {
			guiGraphics.drawString(client.font, hintLines[i], boxX + padding, boxY + padding + i * lineHeight, 0xFFFFFF00, false);
		}
		guiGraphics.pose().popPose();
	}

	private static void renderMountToast(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		net.minecraft.client.gui.Font mountToastFont = com.examplebett.compat.LegacyGui.fontFor(mountToastCustomFontEnabled ? MOUNT_TOAST_FONT : null);
		if (mountToastText == null || mountToastStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - mountToastStartMs;
		if (elapsed >= MOUNT_TOAST_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < MOUNT_TOAST_FADE_IN_MS) {
			alpha = elapsed / (float) MOUNT_TOAST_FADE_IN_MS;
		} else if (elapsed > MOUNT_TOAST_TOTAL_MS - MOUNT_TOAST_FADE_OUT_MS) {
			alpha = (MOUNT_TOAST_TOTAL_MS - elapsed) / (float) MOUNT_TOAST_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));

		int color = ((int) (alpha * 255.0f) << 24) | 0xFFFFFF;

		int x = (int) ((context.guiWidth() - 6) / MOUNT_TOAST_SCALE - com.examplebett.compat.LegacyGui.width(mountToastFont, mountToastText));
		int y = (int) ((context.guiHeight() - 34) / MOUNT_TOAST_SCALE);

		context.pose().pushPose();
		context.pose().scale(MOUNT_TOAST_SCALE, MOUNT_TOAST_SCALE, 1.0f);
		context.drawString(mountToastFont, mountToastText, x, y, color, true);

		if (mountToastCategoryText != null) {
			int categoryX = (int) ((context.guiWidth() - 6) / MOUNT_TOAST_SCALE - com.examplebett.compat.LegacyGui.width(mountToastFont, mountToastCategoryText));
			int categoryY = y + mountToastFont.lineHeight + 2;
			context.drawString(mountToastFont, mountToastCategoryText, categoryX, categoryY, color, true);
		}
		context.pose().popPose();
	}

	private static void renderSpeedDisplay(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (speedDisplayMode == SpeedDisplayMode.NONE || mc.options.hideGui || mc.player == null) return;

		Entity vehicle = mc.player.getVehicle();
		if (!(vehicle instanceof CarEntity car)) return;

		if (vehicle != lastVehicle) {
			return;
		}

		if (mountToastStartMs >= 0 && System.currentTimeMillis() - mountToastStartMs < MOUNT_TOAST_TOTAL_MS) {
			return;
		}

		double blocksPerTick = com.examplebett.compat.ModVec3.horizontalLength(car.getDeltaMovement());
		double kmh = blocksPerTick * 20.0 * 3.6;
		double displaySpeed = speedDisplayMode == SpeedDisplayMode.MPH ? kmh * 0.621371 : kmh;
		String numberText = String.valueOf(Math.round(displaySpeed));
		String unitText = speedDisplayMode == SpeedDisplayMode.MPH ? "mph" : "km/h";

		int color = 0xFFFFFFFF;
		int rightEdge = context.guiWidth() - 6;
		int nameRowY = context.guiHeight() - 34;

		int numberWidth = Math.round(com.examplebett.compat.LegacyGui.width(mc.font, numberText) * SPEED_NUMBER_SCALE);
		int unitWidth = Math.round(com.examplebett.compat.LegacyGui.width(mc.font, unitText) * SPEED_UNIT_SCALE);
		int unitScreenX = rightEdge - unitWidth;
		int numberScreenX = unitScreenX - SPEED_UNIT_GAP - numberWidth;

		int numberHeight = Math.round(mc.font.lineHeight * SPEED_NUMBER_SCALE);
		int unitHeight = Math.round(mc.font.lineHeight * SPEED_UNIT_SCALE);
		int numberScreenY = nameRowY;

		int unitScreenY = nameRowY + (numberHeight - unitHeight);

		context.pose().pushPose();
		context.pose().scale(SPEED_NUMBER_SCALE, SPEED_NUMBER_SCALE, 1.0f);
		context.drawString(mc.font, numberText, Math.round(numberScreenX / SPEED_NUMBER_SCALE), Math.round(numberScreenY / SPEED_NUMBER_SCALE), color, true);
		context.pose().popPose();

		context.pose().pushPose();
		context.pose().scale(SPEED_UNIT_SCALE, SPEED_UNIT_SCALE, 1.0f);
		context.drawString(mc.font, unitText, Math.round(unitScreenX / SPEED_UNIT_SCALE), Math.round(unitScreenY / SPEED_UNIT_SCALE), color, true);
		context.pose().popPose();
	}

	private static void renderPassengerTransportWarning(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (hudWarningText == null || hudWarningStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - hudWarningStartMs;
		if (elapsed >= HUD_WARNING_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < HUD_WARNING_FADE_IN_MS) {
			alpha = elapsed / (float) HUD_WARNING_FADE_IN_MS;
		} else if (elapsed > HUD_WARNING_TOTAL_MS - HUD_WARNING_FADE_OUT_MS) {
			alpha = (HUD_WARNING_TOTAL_MS - elapsed) / (float) HUD_WARNING_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));

		int color = ((int) (alpha * 255.0f) << 24) | 0xFFFFFF;

		context.drawString(mc.font, hudWarningText, 4, 28, color, true);
	}

	private static void renderHintTip(GuiGraphics context) {
		Minecraft mc = Minecraft.getInstance();
		if (!hintsEnabled || hintTipLines == null || hintTipStartMs < 0 || mc.options.hideGui) return;

		long elapsed = System.currentTimeMillis() - hintTipStartMs;
		if (elapsed >= HINT_TIP_TOTAL_MS) return;

		float alpha = 1.0f;
		if (elapsed < HINT_TIP_FADE_IN_MS) {
			alpha = elapsed / (float) HINT_TIP_FADE_IN_MS;
		} else if (elapsed > HINT_TIP_TOTAL_MS - HINT_TIP_FADE_OUT_MS) {
			alpha = (HINT_TIP_TOTAL_MS - elapsed) / (float) HINT_TIP_FADE_OUT_MS;
		}
		alpha = Math.max(0.0f, Math.min(1.0f, alpha));
		int alpha8 = (int) (alpha * 255.0f);

		int textColor = (alpha8 << 24) | 0xFFFFFF;
		int backgroundColor = alpha8 << 24;

		List<String> rows = new ArrayList<>();
		for (Component line : hintTipLines) {
			for (String row : line.getString().split("\n", -1)) {
				rows.add(row);
			}
		}
		int maxWidth = 0;
		for (String row : rows) {
			maxWidth = Math.max(maxWidth, com.examplebett.compat.LegacyGui.width(mc.font, row));
		}
		int lineHeight = mc.font.lineHeight;
		int padding = 4;
		int boxWidth = maxWidth + padding * 2;
		int boxHeight = rows.size() * lineHeight + padding * 2;

		int screenX = 4;
		int screenY = 4;
		context.pose().pushPose();
		context.pose().scale(HINT_TIP_SCALE, HINT_TIP_SCALE, 1.0f);
		int x = Math.round(screenX / HINT_TIP_SCALE);
		int y = Math.round(screenY / HINT_TIP_SCALE);
		context.fill(x, y, x + boxWidth, y + boxHeight, backgroundColor);
		for (int i = 0; i < rows.size(); i++) {
			context.drawString(mc.font, rows.get(i), x + padding, y + padding + i * lineHeight, textColor, true);
		}
		context.pose().popPose();

		Component disableHint = ModText.translatable("gui.mineturismo.hint_tip.disable_instruction",
				ModText.translatable("item.mineturismo.debug_wrench"),
				ModText.translatable("gui.mineturismo.debug_wrench_menu.other"),
				ModText.translatable("gui.mineturismo.debug_wrench_menu.hide_hints"));
		int subLineHeight = mc.font.lineHeight;
		int subBoxWidth = com.examplebett.compat.LegacyGui.width(mc.font, disableHint) + padding * 2;
		int subBoxHeight = subLineHeight + padding * 2;
		int subScreenY = screenY + Math.round(boxHeight * HINT_TIP_SCALE);
		context.pose().pushPose();
		context.pose().scale(HINT_TIP_SUB_SCALE, HINT_TIP_SUB_SCALE, 1.0f);
		int subX = Math.round(screenX / HINT_TIP_SUB_SCALE);
		int subY = Math.round(subScreenY / HINT_TIP_SUB_SCALE);
		context.fill(subX, subY, subX + subBoxWidth, subY + subBoxHeight, backgroundColor);
		context.drawString(mc.font, disableHint, subX + padding, subY + padding, textColor, true);
		context.pose().popPose();
	}
}
*/
//?}
