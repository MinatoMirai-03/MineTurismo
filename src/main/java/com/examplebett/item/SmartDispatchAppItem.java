package com.examplebett.item;
import com.examplebett.compat.ModEntity;
import com.examplebett.compat.ModPlayer;
import com.examplebett.compat.ModRegistries;
import com.examplebett.compat.ModText;

import com.examplebett.AiObjectMod;
import com.examplebett.sound.AiObjectModSounds;
import com.examplebett.entity.CarEntity;
import com.examplebett.util.AiCarPool;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class SmartDispatchAppItem extends Item {

    public static final int VEHICLE_CALL_COST = 3;

    public static final int SHOP_COST = 1;

    private static final double SPAWN_DISTANCE = 3.0;

    private static final float DOOR_LOCK_HIGH_PITCH = 1.2f;

    public static Consumer<Player> appScreenOpener;

    public SmartDispatchAppItem(Properties properties) {
        super(properties);
    }

    private void openAppIfClient(Level level, Player player) {
        if (level.isClientSide() && appScreenOpener != null) {
            appScreenOpener.accept(player);
        }
    }

    //? if >=1.20.5 {
    //? if >=1.21.5 {
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, net.minecraft.world.item.component.TooltipDisplay tooltipDisplay,
                                Consumer<net.minecraft.network.chat.Component> tooltipAdder, net.minecraft.world.item.TooltipFlag flag) {
        tooltipAdder.accept(ModText.translatable("item.mineturismo.smart_dispatch_app.desc")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
    //?} else {
    /*@Override
    public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltip,
                                net.minecraft.world.item.TooltipFlag flag) {
        tooltip.add(ModText.translatable("item.mineturismo.smart_dispatch_app.desc")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
    }*/
    //?}
    //?} else {
    /*@Override
    public void appendHoverText(ItemStack stack, Level level, java.util.List<net.minecraft.network.chat.Component> tooltip,
                                net.minecraft.world.item.TooltipFlag flag) {
        tooltip.add(ModText.translatable("item.mineturismo.smart_dispatch_app.desc")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
    }*/
    //?}

    //? if >=1.21.2 {
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        openAppIfClient(level, player);
        return InteractionResult.SUCCESS;
    }
    //?} else {
    /*@Override
    public net.minecraft.world.InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        openAppIfClient(level, player);
        return net.minecraft.world.InteractionResultHolder.success(player.getItemInHand(hand));
    }
    *///?}

    private static boolean isHoldingApp(Player player) {
        return player.getMainHandItem().getItem() instanceof SmartDispatchAppItem
                || player.getOffhandItem().getItem() instanceof SmartDispatchAppItem;
    }

    public static final String[] PHONE_MODELS = {"popphone_3", "popphone_6", "popphone_18", "popphone_classic"};
    public static final String DEFAULT_PHONE_MODEL = "popphone_6";

    public static final String[] WALLPAPERS = {"default", "sunset_beach", "white", "gray", "grass", "flow"};
    private static final String TAG_PHONE_MODEL = "PhoneModel";
    private static final String TAG_WALLPAPER = "PhoneWallpaper";

    public static ItemStack heldPhone(Player player) {
        if (player.getMainHandItem().getItem() instanceof SmartDispatchAppItem) {
            return player.getMainHandItem();
        }
        return player.getOffhandItem().getItem() instanceof SmartDispatchAppItem ? player.getOffhandItem() : ItemStack.EMPTY;
    }

    private static net.minecraft.nbt.CompoundTag phoneTag(ItemStack stack) {
        //? if >=1.20.5 {
        return stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        //?} else
        /*return stack.getTag() != null ? stack.getTag().copy() : new net.minecraft.nbt.CompoundTag();*/
    }

    private static String phoneSetting(ItemStack stack, String key, String[] choices, String fallback) {
        net.minecraft.nbt.CompoundTag tag = phoneTag(stack);
        //? if >=1.21.5 {
        String value = tag.getStringOr(key, fallback);
        //?} else
        /*String value = tag.contains(key) ? tag.getString(key) : fallback;*/
        if ("popphone_pro".equals(value)) {
            value = "popphone_classic";
        }
        return java.util.Arrays.asList(choices).contains(value) ? value : fallback;
    }

    public static String phoneModelOf(ItemStack stack) {
        return phoneSetting(stack, TAG_PHONE_MODEL, PHONE_MODELS, DEFAULT_PHONE_MODEL);
    }

    public static String wallpaperOf(ItemStack stack) {
        return phoneSetting(stack, TAG_WALLPAPER, WALLPAPERS, WALLPAPERS[0]);
    }

    public static void applyPhoneModelIcon(ItemStack stack, String model) {
        //? if >=1.21.4 {
        stack.set(net.minecraft.core.component.DataComponents.ITEM_MODEL, AiObjectMod.id(model));
        //?}
        //? if >=1.20.5 && <1.21.4 {
        /*net.minecraft.nbt.CompoundTag tag = phoneTag(stack);
        tag.putString(TAG_PHONE_MODEL, model);
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));*/
        //?}
        //? if <1.20.5 {
        /*net.minecraft.nbt.CompoundTag tag = phoneTag(stack);
        tag.putString(TAG_PHONE_MODEL, model);
        stack.setTag(tag);*/
        //?}
    }

    public static void setPhoneStyle(Player player, String model, String wallpaper) {
        ItemStack stack = heldPhone(player);
        if (stack.isEmpty() || !java.util.Arrays.asList(PHONE_MODELS).contains(model)
                || !java.util.Arrays.asList(WALLPAPERS).contains(wallpaper)) {
            return;
        }
        net.minecraft.nbt.CompoundTag tag = phoneTag(stack);
        tag.putString(TAG_PHONE_MODEL, model);
        tag.putString(TAG_WALLPAPER, wallpaper);
        //? if >=1.20.5 {
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
        //?} else
        /*stack.setTag(tag);*/
        applyPhoneModelIcon(stack, model);
    }

    public static void callVehicle(Level level, Player player, String modelId) {
        if (!isHoldingApp(player) || !AiCarPool.allModelIds(id -> false).contains(modelId) || !canPay(player, VEHICLE_CALL_COST)) {
            return;
        }

        //? if >=1.17 {
        float yaw = player.getYRot();
        //?} else
        /*float yaw = player.yRot;*/
        double px = ModEntity.getX(player);
        double py = ModEntity.getY(player);
        double pz = ModEntity.getZ(player);

        double dx = -Mth.sin((float) Math.toRadians(yaw)) * SPAWN_DISTANCE;
        double dz = Mth.cos((float) Math.toRadians(yaw)) * SPAWN_DISTANCE;
        CarEntity car = new CarEntity(level, px + dx, py, pz + dz, modelId);
        //? if >=1.17 {
        car.setYRot(yaw);
        //?} else
        /*car.yRot = yaw;*/
        if (!level.noCollision(car)) {
            car.setPos(px, py, pz);
        }
        level.addFreshEntity(car);
        pay(player, VEHICLE_CALL_COST);

        SoundEvent lockSound = level.random.nextBoolean() ? AiObjectModSounds.DOOR_LOCK_1 : AiObjectModSounds.DOOR_LOCK_2;
        float pitch = level.random.nextBoolean() ? 1.0f : DOOR_LOCK_HIGH_PITCH;
        level.playSound(null, ModEntity.getX(car), ModEntity.getY(car), ModEntity.getZ(car), lockSound, SoundSource.NEUTRAL, 1.0f, pitch);
    }

    private static boolean canPay(Player player, int cost) {
        if (ModPlayer.hasInstabuild(player) || ModPlayer.getInventory(player).countItem(Items.EMERALD) >= cost) {
            return true;
        }
        ModPlayer.displayActionBar(player, ModText.translatable("gui.mineturismo.smart_dispatch.not_enough_emeralds", cost));
        return false;
    }

    private static void pay(Player player, int cost) {
        if (ModPlayer.hasInstabuild(player)) {
            return;
        }
        Inventory inventory = ModPlayer.getInventory(player);
        int remaining = cost;
        for (int i = 0; i < inventory.getContainerSize() && remaining > 0; i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() == Items.EMERALD) {
                int take = Math.min(remaining, stack.getCount());
                stack.shrink(take);
                remaining -= take;
            }
        }
    }

    private static final java.util.Set<String> SHOP_NAMESPACES = java.util.Set.of(AiObjectMod.MOD_ID, "mineturismo-ai-objects");
    private static java.util.List<Item> shopItems;

    public static java.util.List<Item> shopItems() {
        if (shopItems == null) {
            java.util.List<Item> items = new java.util.ArrayList<>();
            for (Item item : ModRegistries.ITEM) {

                if (item instanceof CarSpawnItem || item instanceof SmartDispatchAppItem || item instanceof BoxedCarItem
                        || item instanceof WheelWrenchItem) {
                    continue;
                }
                if (SHOP_NAMESPACES.contains(ModRegistries.ITEM.getKey(item).getNamespace())) {
                    items.add(item);
                }
            }
            shopItems = java.util.List.copyOf(items);
        }
        return shopItems;
    }

    public static final String WARDEN_ENERGY_ID = "mineturismo:warden_energy";

    public static final int WARDEN_ENERGY_PACK = 10;
    private static final int WARDEN_ENERGY_MAX_STACK = 64;

    public static final String WARDEN_ENERGY_SONIC_BOOM_ID = "mineturismo:warden_energy_sonic_boom";

    public static ItemStack createWardenEnergy(int count) {
        return createWardenEnergy(count, false);
    }

    public static ItemStack createWardenEnergySonicBoom(int count) {
        return createWardenEnergy(count, true);
    }

    //? if >=1.21.5 {
    private static final net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> STRENGTH_EFFECT = net.minecraft.world.effect.MobEffects.STRENGTH;
    private static final net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> SPEED_EFFECT = net.minecraft.world.effect.MobEffects.SPEED;
    //?}
    //? if >=1.21.4 && <1.21.5 {
    /*private static final net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> STRENGTH_EFFECT = net.minecraft.world.effect.MobEffects.DAMAGE_BOOST;
    private static final net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> SPEED_EFFECT = net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED;
    *///?}

    private static ItemStack createWardenEnergy(int count, boolean sonicBoom) {
        //? if >=1.21.4 {
        String name = sonicBoom ? "warden_energy_sonic_boom" : "warden_energy";
        ItemStack stack = new ItemStack(Items.HONEY_BOTTLE, count);
        stack.set(net.minecraft.core.component.DataComponents.ITEM_NAME, ModText.translatable("item.mineturismo." + name));
        stack.set(net.minecraft.core.component.DataComponents.ITEM_MODEL, AiObjectMod.id(name));
        stack.set(net.minecraft.core.component.DataComponents.MAX_STACK_SIZE, WARDEN_ENERGY_MAX_STACK);
        stack.remove(net.minecraft.core.component.DataComponents.FOOD);
        stack.remove(net.minecraft.core.component.DataComponents.USE_REMAINDER);

        java.util.List<net.minecraft.world.effect.MobEffectInstance> effects = new java.util.ArrayList<>(java.util.List.of(
                new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.REGENERATION, 100, 0),
                new net.minecraft.world.effect.MobEffectInstance(STRENGTH_EFFECT, 1200, 0),
                new net.minecraft.world.effect.MobEffectInstance(SPEED_EFFECT, 1200, 0),
                new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.DARKNESS, 120, 0)));
        if (sonicBoom) {
            effects.add(new net.minecraft.world.effect.MobEffectInstance(com.examplebett.effect.SonicBoomEnergy.EFFECT, 1200, 0));
        }
        stack.set(net.minecraft.core.component.DataComponents.CONSUMABLE, net.minecraft.world.item.component.Consumable.builder()
                .consumeSeconds(1.2f)
                .animation(net.minecraft.world.item.ItemUseAnimation.DRINK)
                .sound(net.minecraft.sounds.SoundEvents.GENERIC_DRINK)
                .hasConsumeParticles(false)
                .onConsume(new net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect(effects))
                .build());

        java.util.List<net.minecraft.network.chat.Component> lore = new java.util.ArrayList<>();
        for (net.minecraft.world.effect.MobEffectInstance effect : effects) {
            net.minecraft.network.chat.MutableComponent line = ModText.translatable(effect.getDescriptionId());
            if (effect.getAmplifier() > 0) {
                line = ModText.translatable("potion.withAmplifier", line, ModText.translatable("potion.potency." + effect.getAmplifier()));
            }
            int seconds = effect.getDuration() / 20;
            line = ModText.translatable("potion.withDuration", line, String.format("%d:%02d", seconds / 60, seconds % 60));
            boolean harmful = effect.getEffect().value().getCategory() == net.minecraft.world.effect.MobEffectCategory.HARMFUL;
            lore.add(line.withStyle(style -> style.withItalic(false)
                    .withColor(harmful ? net.minecraft.ChatFormatting.RED : net.minecraft.ChatFormatting.BLUE)));
        }
        stack.set(net.minecraft.core.component.DataComponents.LORE, new net.minecraft.world.item.component.ItemLore(lore));
        return stack;
        //?} else
        /*return new ItemStack(sonicBoom ? AiObjectModItems.WARDEN_ENERGY_SONIC_BOOM : AiObjectModItems.WARDEN_ENERGY, count);*/
    }

    public static final String CHOCO_BAR_ID = "mineturismo:choco_bar";

    public static final int CHOCO_BAR_PACK = 10;
    private static final int CHOCO_BAR_NUTRITION = 1;
    private static final float CHOCO_BAR_SATURATION = 0.1f;

    public static ItemStack createChocoBar(int count) {
        //? if >=1.21.4 {
        ItemStack stack = new ItemStack(Items.COOKIE, count);
        stack.set(net.minecraft.core.component.DataComponents.ITEM_NAME, ModText.translatable("item.mineturismo.choco_bar"));
        stack.set(net.minecraft.core.component.DataComponents.ITEM_MODEL, AiObjectMod.id("choco_bar"));

        stack.set(net.minecraft.core.component.DataComponents.FOOD, new net.minecraft.world.food.FoodProperties.Builder()
                .nutrition(CHOCO_BAR_NUTRITION)
                .saturationModifier(CHOCO_BAR_SATURATION)
                .build());
        return stack;
        //?} else
        /*return new ItemStack(AiObjectModItems.CHOCO_BAR, count);*/
    }

    private static void give(Player player, ItemStack stack) {
        ModPlayer.getInventory(player).add(stack);
        if (!stack.isEmpty()) {
            player.drop(stack, false);
        }
    }

    public static void purchaseItem(Level level, Player player, String itemId) {
        if (!isHoldingApp(player)) {
            return;
        }
        if (WARDEN_ENERGY_ID.equals(itemId) || WARDEN_ENERGY_SONIC_BOOM_ID.equals(itemId) || CHOCO_BAR_ID.equals(itemId)) {
            ItemStack energy = WARDEN_ENERGY_ID.equals(itemId) ? createWardenEnergy(WARDEN_ENERGY_PACK)
                    : WARDEN_ENERGY_SONIC_BOOM_ID.equals(itemId) ? createWardenEnergySonicBoom(WARDEN_ENERGY_PACK)
                    : createChocoBar(CHOCO_BAR_PACK);
            if (energy.isEmpty() || !canPay(player, SHOP_COST)) {
                return;
            }
            pay(player, SHOP_COST);
            give(player, energy);
            level.playSound(null, ModEntity.getX(player), ModEntity.getY(player), ModEntity.getZ(player),
                    net.minecraft.sounds.SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.4f, 1.4f);
            return;
        }
        Item item = null;
        for (Item candidate : shopItems()) {
            if (ModRegistries.ITEM.getKey(candidate).toString().equals(itemId)) {
                item = candidate;
                break;
            }
        }
        if (item == null || !canPay(player, SHOP_COST)) {
            return;
        }
        pay(player, SHOP_COST);
        give(player, new ItemStack(item));
        level.playSound(null, ModEntity.getX(player), ModEntity.getY(player), ModEntity.getZ(player),
                net.minecraft.sounds.SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.4f, 1.4f);
    }

    public static void buyUsedCar(Level level, Player player, String listingId) {
        if (!isHoldingApp(player)) {
            return;
        }
        UsedCarListings.Listing listing = UsedCarListings.find(listingId);
        if (listing == null) {
            return;
        }
        ItemStack car = listing.createStack();
        if (car.isEmpty() || !canPay(player, listing.price())) {
            return;
        }
        pay(player, listing.price());
        give(player, car);
        level.playSound(null, ModEntity.getX(player), ModEntity.getY(player), ModEntity.getZ(player),
                net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.6f, 1.2f);
    }

    public static final int WEATHER_CLEAR = 0;
    public static final int WEATHER_RAIN = 1;
    public static final int WEATHER_THUNDER = 2;

    private static final int WEATHER_CLEAR_DURATION = 18000;
    private static final int WEATHER_RAIN_DURATION = 12000;

    public static void setWeather(net.minecraft.server.level.ServerLevel level, Player player, int kind) {
        if (!isHoldingApp(player) || kind < WEATHER_CLEAR || kind > WEATHER_THUNDER) {
            return;
        }
        if (!ModPlayer.hasInstabuild(player) && !ModPlayer.hasGamemasterPermission(player)) {
            ModPlayer.displayActionBar(player, ModText.translatable("gui.mineturismo.smart_dispatch.weather_no_permission"));
            return;
        }

        //? if >=26.1 {
        /*net.minecraft.world.level.saveddata.WeatherData weather = level.getWeatherData();
        boolean clearSky = kind == WEATHER_CLEAR;
        weather.setClearWeatherTime(clearSky ? WEATHER_CLEAR_DURATION : 0);
        weather.setRainTime(clearSky ? 0 : WEATHER_RAIN_DURATION);
        weather.setThunderTime(clearSky ? 0 : WEATHER_RAIN_DURATION);
        weather.setRaining(!clearSky);
        weather.setThundering(kind == WEATHER_THUNDER);
        *///?}
        //? if >=1.16 && <26.1 {
        if (kind == WEATHER_CLEAR) {
            level.setWeatherParameters(WEATHER_CLEAR_DURATION, 0, false, false);
        } else {
            level.setWeatherParameters(0, WEATHER_RAIN_DURATION, true, kind == WEATHER_THUNDER);
        }
        //?}
        //? if <1.16 {
        /*net.minecraft.world.level.storage.LevelData data = level.getLevelData();
        boolean clear = kind == WEATHER_CLEAR;
        data.setClearWeatherTime(clear ? WEATHER_CLEAR_DURATION : 0);
        data.setRainTime(clear ? 0 : WEATHER_RAIN_DURATION);
        data.setThunderTime(clear ? 0 : WEATHER_RAIN_DURATION);
        data.setRaining(!clear);
        data.setThundering(kind == WEATHER_THUNDER);
        *///?}
    }

    private static final String[] GTT_VEHICLES = {
            "nissan_400z_voxel", "volkswagen_scirocco_voxel", "dodge_challenger_srt_2020_voxel",
            "honda_civic_hatchback_2000_voxel", "mclaren_f1_voxel", "chevrolet_corvette_c6_voxel"
    };

    private static final int[] GTT_COLORS = {
            0xD32F2F, 0x1976D2, 0xFBC02D, 0x388E3C, 0xF57C00, 0x7B1FA2, 0x00ACC1, 0xC2185B,
            0xFFFFFF, 0x212121, 0x9E9E9E, 0x5D4037
    };

    public static void startGtt(net.minecraft.server.level.ServerLevel level, Player player) {
        if (!isHoldingApp(player)) {
            return;
        }
        CarEntity car = null;
        if (player.getVehicle() instanceof CarEntity riding && riding.getControllingPassenger() == player) {
            car = riding;
        }
        if (car == null) {
            if (player.getVehicle() != null) {
                player.stopRiding();
            }
            String modelId = GTT_VEHICLES[level.random.nextInt(GTT_VEHICLES.length)];
            if (!AiCarPool.allModelIds(id -> false).contains(modelId)) {
                return;
            }
            car = new CarEntity(level, ModEntity.getX(player), ModEntity.getY(player), ModEntity.getZ(player), modelId);
            //? if >=1.17 {
            car.setYRot(player.getYRot());
            //?} else
            /*car.yRot = player.yRot;*/
            car.setColor(GTT_COLORS[level.random.nextInt(GTT_COLORS.length)]);
            level.addFreshEntity(car);
            player.startRiding(car);
        }
        car.startGtt();
    }

    public static void stopGtt(net.minecraft.server.level.ServerLevel level, Player player) {
        if (player.getVehicle() instanceof CarEntity car) {
            car.stopGtt(level);
        }
    }

    private static final double EMERGENCY_SPAWN_DISTANCE_MIN = 30.0;
    private static final double EMERGENCY_SPAWN_DISTANCE_MAX = 40.0;

    private static final int EMERGENCY_SPAWN_ATTEMPTS = 12;

    public static void callEmergency(net.minecraft.server.level.ServerLevel level, Player player) {
        if (!isHoldingApp(player)) {
            return;
        }
        double px = ModEntity.getX(player);
        double py = ModEntity.getY(player);
        double pz = ModEntity.getZ(player);
        for (int attempt = 0; attempt < EMERGENCY_SPAWN_ATTEMPTS; attempt++) {
            double angle = level.random.nextDouble() * Math.PI * 2.0;
            double distance = EMERGENCY_SPAWN_DISTANCE_MIN + level.random.nextDouble() * (EMERGENCY_SPAWN_DISTANCE_MAX - EMERGENCY_SPAWN_DISTANCE_MIN);
            CarEntity car = com.examplebett.entity.VehiclePresets.create(level,
                    px + Math.cos(angle) * distance, py, pz + Math.sin(angle) * distance, com.examplebett.entity.VehiclePresets.FBI_CHALLENGER);
            if (car == null) {
                return;
            }
            if (!level.noCollision(car)) {
                continue;
            }
            car.startResponding(player);
            level.addFreshEntity(car);
            ModPlayer.displayActionBar(player, ModText.translatable("gui.mineturismo.smart_dispatch.emergency_dispatched"));
            return;
        }
        ModPlayer.displayActionBar(player, ModText.translatable("gui.mineturismo.smart_dispatch.emergency_no_space"));
    }

    public static final int SLOT_COST = 3;
    public static final int SLOT_REELS = 3;
    public static final int PRIZE_WARDEN_ENERGY = 0;
    public static final int PRIZE_EMERALD = 1;
    public static final int PRIZE_VEHICLE = 2;
    public static final int PRIZE_CHOCO_BAR = 3;

    public static final String[] SLOT_VEHICLES = {
            "flatiron_opossum", "cycas_pixel_van", "scandza_gc215_tractor_unit_16", "container_trailer", "kawaguchi_kamari_17"
    };

    private static final int WEIGHT_WARDEN_ENERGY = 291;
    private static final int WEIGHT_CHOCO_BAR = 291;
    private static final int WEIGHT_EMERALD_ONE = 270;
    private static final int WEIGHT_EMERALD_WIN = 100;
    private static final int WEIGHT_EMERALD_JACKPOT = 8;
    private static final int SLOT_EMERALD_JACKPOT = 50;
    private static final int SLOT_JACKPOT_MIN_EMERALDS = 30;
    private static final int WEIGHT_TOTAL = 1000;

    public record SlotPrize(int kind, int amount, String modelId) {
        public boolean isWin() {
            return this.kind == PRIZE_VEHICLE || (this.kind == PRIZE_EMERALD && this.amount >= 2);
        }

        public boolean isJackpot() {
            return this.kind == PRIZE_EMERALD && this.amount >= SLOT_JACKPOT_MIN_EMERALDS;
        }

        public ItemStack toStack() {
            if (this.kind == PRIZE_VEHICLE) {
                Item item = AiCarPool.spawnItemFor(this.modelId);
                return item == null ? ItemStack.EMPTY : new ItemStack(item);
            }
            if (this.kind == PRIZE_EMERALD) {
                return new ItemStack(Items.EMERALD, this.amount);
            }
            if (this.kind == PRIZE_CHOCO_BAR) {

                ItemStack choco = createChocoBar(this.amount);
                return choco.isEmpty() ? new ItemStack(Items.COOKIE, this.amount) : choco;
            }
            ItemStack energy = createWardenEnergy(this.amount);

            //? if >=1.15 {
            Item fallback = Items.HONEY_BOTTLE;
            //?} else
            /*Item fallback = Items.SUGAR;*/
            return energy.isEmpty() ? new ItemStack(fallback, this.amount) : energy;
        }
    }

    //? if >=1.19 {
    private static SlotPrize rollSlotPrize(net.minecraft.util.RandomSource random) {
    //?} else
    /*private static SlotPrize rollSlotPrize(java.util.Random random) {*/
        int r = random.nextInt(WEIGHT_TOTAL);
        if (r < WEIGHT_WARDEN_ENERGY) {
            return new SlotPrize(PRIZE_WARDEN_ENERGY, 1, "");
        }
        r -= WEIGHT_WARDEN_ENERGY;
        if (r < WEIGHT_CHOCO_BAR) {
            return new SlotPrize(PRIZE_CHOCO_BAR, 1, "");
        }
        r -= WEIGHT_CHOCO_BAR;
        if (r < WEIGHT_EMERALD_ONE) {
            return new SlotPrize(PRIZE_EMERALD, 1, "");
        }
        r -= WEIGHT_EMERALD_ONE;
        if (r < WEIGHT_EMERALD_WIN) {

            int tier = random.nextInt(100);
            int amount = tier < 70 ? 2 + random.nextInt(4)
                    : tier < 92 ? 6 + random.nextInt(10)
                    : tier < 99 ? 16 + random.nextInt(15)
                    : 31 + random.nextInt(19);
            return new SlotPrize(PRIZE_EMERALD, amount, "");
        }
        r -= WEIGHT_EMERALD_WIN;
        if (r < WEIGHT_EMERALD_JACKPOT) {
            return new SlotPrize(PRIZE_EMERALD, SLOT_EMERALD_JACKPOT, "");
        }
        java.util.List<String> vehicles = new java.util.ArrayList<>();
        for (String modelId : SLOT_VEHICLES) {
            if (AiCarPool.spawnItemFor(modelId) != null) {
                vehicles.add(modelId);
            }
        }
        if (vehicles.isEmpty()) {
            return new SlotPrize(PRIZE_EMERALD, 2, "");
        }
        return new SlotPrize(PRIZE_VEHICLE, 1, vehicles.get(random.nextInt(vehicles.size())));
    }

    public static void spinSlot(net.minecraft.server.level.ServerLevel level, net.minecraft.server.level.ServerPlayer player) {
        java.util.List<SlotPrize> prizes = new java.util.ArrayList<>();
        if (isHoldingApp(player) && canPay(player, SLOT_COST)) {
            pay(player, SLOT_COST);
            for (int i = 0; i < SLOT_REELS; i++) {
                SlotPrize prize = rollSlotPrize(level.random);
                ItemStack stack = prize.toStack();
                if (!stack.isEmpty()) {
                    give(player, stack);
                }
                prizes.add(prize);
            }
        }
        com.examplebett.network.SlotSpinResultPayload payload = new com.examplebett.network.SlotSpinResultPayload(prizes);
        //? if >=1.20.5 {
        com.examplebett.compat.ModNetworking.sendToClient(player, payload);
        //?} else
        /*com.examplebett.compat.ModNetworking.sendToClient(player, com.examplebett.network.SlotSpinResultPayload.ID, payload, com.examplebett.network.SlotSpinResultPayload::write);*/
    }

    public static final long[] TIME_TARGETS = {1000L, 6000L, 12000L, 13000L, 18000L};
    private static final long TICKS_PER_DAY = 24000L;

    public static void setTime(net.minecraft.server.level.ServerLevel level, Player player, int kind) {
        if (!isHoldingApp(player) || kind < 0 || kind >= TIME_TARGETS.length) {
            return;
        }
        if (!ModPlayer.hasInstabuild(player) && !ModPlayer.hasGamemasterPermission(player)) {
            ModPlayer.displayActionBar(player, ModText.translatable("gui.mineturismo.smart_dispatch.time_no_permission"));
            return;
        }

        //? if >=26.1 {
        /*java.util.Optional<net.minecraft.core.Holder<net.minecraft.world.clock.WorldClock>> clock = level.dimensionType().defaultClock();
        if (clock.isEmpty()) {
            return;
        }
        *///?}
        //? if >=26.3 {
        /*long now = level.clockManager().getInstance(clock.get()).totalTicks();
        *///?}
        //? if >=26.1 && <26.3 {
        /*long now = level.clockManager().getTotalTicks(clock.get());
        *///?}
        //? if <26.1 {
        long now = level.getDayTime();
        //?}
        long target = now - Math.floorMod(now, TICKS_PER_DAY) + TIME_TARGETS[kind];
        if (target <= now) {
            target += TICKS_PER_DAY;
        }
        //? if >=26.1 {
        /*level.clockManager().setTotalTicks(clock.get(), target);
        *///?} else {
        for (net.minecraft.server.level.ServerLevel each : level.getServer().getAllLevels()) {
            each.setDayTime(target);
        }
        //?}
    }
}
