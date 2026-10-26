//? if >=1.18 {
//?}
//? if <1.18 && >=1.16.2 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModClientNetworking;
import com.examplebett.compat.ModText;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModPlayer;
import com.examplebett.compat.ModRegistries;
import com.examplebett.item.SmartDispatchAppItem;
import com.examplebett.network.RequestAppCarSpawnPayload;
import com.examplebett.network.RequestAppItemPurchasePayload;
import com.examplebett.util.AiCarPool;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SmartDispatchAppScreen extends Screen {

    private static final int PX = 2;
    private static final int PHONE_MAX_HEIGHT = 240;
    private static final float PHONE_ASPECT = 0.56f;
    private static final int STATUS_BAR_HEIGHT = 11;
    private static final int APP_ICON_SIZE = 24;
    private static final int APP_ICON_TEXTURE_SIZE = 64;
    private static final int APP_COLUMNS = 4;
    private static final int HEADER_HEIGHT = 16;
    private static final int COST_BAR_HEIGHT = 10;
    private static final int ROW_HEIGHT = 20;
    private static final float SMALL_TEXT_SCALE = 0.75f;
    private static final int DIALOG_HEIGHT = 78;
    private static final int DIALOG_BUTTON_HEIGHT = 14;
    private static final long PURCHASED_BANNER_MS = 1500L;

    private static final long SHUTDOWN_MS = 500L;

    private static final int SETTINGS_PAGE_MAIN = 0;
    private static final int SETTINGS_PAGE_WALLPAPER = 1;
    private static final int SETTINGS_PAGE_MODEL = 2;

    private static final int SETTINGS_PAGE_CONTROLS = 3;
    private static final int CONTROLS_ROW_HEIGHT = 11;
    private static final int SETTINGS_ROW_HEIGHT = 20;

    private static final int WALLPAPER_TEXTURE_WIDTH = 64;
    private static final int WALLPAPER_TEXTURE_HEIGHT = 120;

    private static final String[] PHONE_MODEL_NAMES = {"PopPhone 3", "PopPhone 6", "PopPhone 18", null};

    private static final String[] CHORUS_LOGO = {
            "....#.......",
            "#..##.......",
            "########....",
            ".#########..",
            ".##########.",
            "..#########.",
            ".###########",
            "############",
            "############",
            ".###########",
            ".##########.",
            "..########..",
            "...######...",
    };

    private static final int COLOR_WALLPAPER_TOP = 0xFF5696E6;
    private static final int COLOR_WALLPAPER_BOTTOM = 0xFF2C5CB4;
    private static final int COLOR_STATUS_BAR = 0x66000000;
    private static final int COLOR_ROW = 0xFFFFFFFF;
    private static final int COLOR_ROW_ALT = 0xFFF1F1F4;
    private static final int COLOR_ROW_HOVER = 0xFFFFF1B8;
    private static final int COLOR_COST_BAR = 0xFF2A2A30;
    private static final int COLOR_SCROLL_TRACK = 0xFFDADAE0;
    private static final int COLOR_SCROLL_THUMB = 0xFF8C8C96;
    private static final int COLOR_DIALOG_SHADE = 0x99000000;
    private static final int COLOR_DIALOG = 0xFFFFFFFF;
    private static final int COLOR_BUTTON_YES = 0xFF3DAA4F;
    private static final int COLOR_BUTTON_YES_HOVER = 0xFF4FC562;
    private static final int COLOR_BUTTON_NO = 0xFFB4B6BE;
    private static final int COLOR_BUTTON_NO_HOVER = 0xFFC8CAD2;
    private static final int COLOR_BANNER = 0xEE202024;

    private enum App {
        MINES("Mines Car Share", "Mines\nCar Share", null, "mines", 0xFFF2C318, 0xFF000000, true, false),
        AVALON("Avalon", "Avalon", null, "avalon", 0xFF1B2838, 0xFF6FD3F3, false, false),
        WEATHER(null, null, "gui.mineturismo.smart_dispatch.weather_app", "weather", 0xFF3D8BD9, 0xFFFFFFFF, false, true),
        CLOCK(null, null, "gui.mineturismo.smart_dispatch.clock_app", "clock", 0xFF1C1C1E, 0xFFFFFFFF, false, false),

        GTT("Grand Target Track", "GTT", null, "gtt", 0xFF000000, 0xFFFFFFFF, false, false),
        EMERGENCY(null, null, "gui.mineturismo.smart_dispatch.emergency_app", "emergency", 0xFFD32F2F, 0xFFFFFFFF, false, false),

        MUSIC(null, null, "gui.mineturismo.smart_dispatch.music_app", "music", 0xFFF5354F, 0xFFFFFFFF, false, false),

        MASSIVE_AUTO("Massive Auto", "Massive\nAuto", null, "massive_auto", 0xFF1E4FA8, 0xFFFFFFFF, false, false),

        SLOT(null, null, "gui.mineturismo.smart_dispatch.slot_app", "slot", 0xFFE6197E, 0xFFFFFFFF, false, false),

        DETONATOR(null, null, "gui.mineturismo.smart_dispatch.detonator_app", "detonator", 0xFF2B2D33, 0xFFFFFFFF, false, false),

        SETTINGS(null, null, "gui.mineturismo.smart_dispatch.settings_app", "settings", 0xFFE9E9EE, 0xFF000000, false, false);

        private final String title;
        private final String shortName;
        private final String titleKey;
        final ResourceLocation icon;
        final int headerColor;
        final int titleColor;
        final boolean vehicles;
        final boolean weather;

        App(String title, String shortName, String titleKey, String iconId, int headerColor, int titleColor,
            boolean vehicles, boolean weather) {
            this.title = title;
            this.shortName = shortName;
            this.titleKey = titleKey;
            this.icon = AiObjectMod.id("textures/gui/smart_dispatch/" + iconId + ".png");
            this.headerColor = headerColor;
            this.titleColor = titleColor;
            this.vehicles = vehicles;
            this.weather = weather;
        }

        String title() {
            return this.titleKey != null ? ModText.translatable(this.titleKey).getString() : this.title;
        }

        String shortName() {
            if (this.titleKey == null) {
                return this.shortName;
            }
            String labelKey = this.titleKey + "_label";
            return net.minecraft.locale.Language.getInstance().has(labelKey) ? ModText.translatable(labelKey).getString() : this.title();
        }
    }

    private record Entry(String id, ItemStack stack, Component name) {
    }

    private record Layout(int x0, int y0, int x1, int y1, int sx0, int sy0, int sx1, int sy1,
                          int homeX0, int homeY0, int homeX1, int homeY1) {
        int listTop() {
            return sy0 + STATUS_BAR_HEIGHT + HEADER_HEIGHT + COST_BAR_HEIGHT;
        }

        int visibleRows() {
            return Math.max(1, (sy1 - listTop()) / ROW_HEIGHT);
        }

        int dialogTop() {
            return (sy0 + sy1 - DIALOG_HEIGHT) / 2;
        }

        int buttonTop() {
            return dialogTop() + DIALOG_HEIGHT - DIALOG_BUTTON_HEIGHT - 6;
        }

        int buttonWidth() {
            return (sx1 - sx0 - 12 - 6 - 6) / 2;
        }

        int yesX0() {
            return sx0 + 12;
        }

        int noX0() {
            return yesX0() + buttonWidth() + 6;
        }
    }

    private final List<Entry> vehicles = new ArrayList<>();
    private final List<Entry> shopItems = new ArrayList<>();
    private final List<Entry> musicTracks = new ArrayList<>();
    private App openApp = null;
    private int scrollRow = 0;

    private boolean draggingScrollbar = false;

    private boolean weatherPickerOpen = false;

    private boolean clockPickerOpen = false;

    private boolean musicBrowsing = false;

    private boolean draggingVolume = false;

    private Entry pending = null;
    private long purchasedBannerUntil = 0L;

    private long shutdownUntil = 0L;

    private String phoneModel = SmartDispatchAppItem.DEFAULT_PHONE_MODEL;
    private String wallpaper = SmartDispatchAppItem.WALLPAPERS[0];
    private PhoneSkin skin = PhoneSkin.POPPHONE_6;

    private int settingsPage = SETTINGS_PAGE_MAIN;

    private net.minecraft.client.KeyMapping rebindingKey;

    public SmartDispatchAppScreen() {
        super(ModText.translatable("item.mineturismo.smart_dispatch_app"));
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            ItemStack phone = SmartDispatchAppItem.heldPhone(mc.player);
            this.phoneModel = SmartDispatchAppItem.phoneModelOf(phone);
            this.wallpaper = SmartDispatchAppItem.wallpaperOf(phone);
            this.skin = PhoneSkin.byId(this.phoneModel);
        }
        for (String modelId : AiCarPool.allModelIds(id -> false)) {
            Item item = AiCarPool.spawnItemFor(modelId);
            if (item != null) {
                this.vehicles.add(new Entry(modelId, new ItemStack(item), ModText.translatable(item.getDescriptionId())));
            }
        }
        this.vehicles.sort(Comparator.comparing(e -> e.name().getString()));
        for (Item item : SmartDispatchAppItem.shopItems()) {
            this.shopItems.add(new Entry(ModRegistries.ITEM.getKey(item).toString(), new ItemStack(item),
                    ModText.translatable(item.getDescriptionId())));
        }

        ItemStack wardenEnergy = SmartDispatchAppItem.createWardenEnergy(1);
        if (!wardenEnergy.isEmpty()) {
            this.shopItems.add(new Entry(SmartDispatchAppItem.WARDEN_ENERGY_ID, wardenEnergy,
                    ModText.translatable("item.mineturismo.warden_energy").append(" x" + SmartDispatchAppItem.WARDEN_ENERGY_PACK)));
        }

        ItemStack wardenEnergySonicBoom = SmartDispatchAppItem.createWardenEnergySonicBoom(1);
        if (!wardenEnergySonicBoom.isEmpty()) {
            this.shopItems.add(new Entry(SmartDispatchAppItem.WARDEN_ENERGY_SONIC_BOOM_ID, wardenEnergySonicBoom,
                    ModText.translatable("item.mineturismo.warden_energy_sonic_boom").append(" x" + SmartDispatchAppItem.WARDEN_ENERGY_PACK)));
        }

        ItemStack chocoBar = SmartDispatchAppItem.createChocoBar(1);
        if (!chocoBar.isEmpty()) {
            this.shopItems.add(new Entry(SmartDispatchAppItem.CHOCO_BAR_ID, chocoBar,
                    ModText.translatable("item.mineturismo.choco_bar").append(" x" + SmartDispatchAppItem.CHOCO_BAR_PACK)));
        }
        this.shopItems.sort(Comparator.comparing(e -> e.name().getString()));

        java.util.Map<String, Item> discs = new java.util.HashMap<>();
        for (Item item : ModRegistries.ITEM) {
            net.minecraft.resources.ResourceLocation key = ModRegistries.ITEM.getKey(item);
            if ("minecraft".equals(key.getNamespace()) && key.getPath().startsWith("music_disc_")) {
                discs.put(key.getPath().substring("music_disc_".length()), item);
            }
        }
        for (com.examplebett.client.sound.ChorusMusicTracks.Track track : com.examplebett.client.sound.ChorusMusicTracks.TRACKS) {

            if (net.minecraft.client.Minecraft.getInstance().getSoundManager().getSoundEvent(track.soundId()) == null) {
                continue;
            }
            Item icon = switch (track.category()) {
                case RECORDS -> discs.getOrDefault(track.id(), Items.JUKEBOX);
                case CREATIVE -> Items.BRICKS;
                case MENU -> Items.PAINTING;
                case NETHER -> Items.NETHERRACK;
                case END -> Items.END_STONE;
                case UNDERWATER -> Items.PRISMARINE;
                default -> Items.GRASS_BLOCK;
            };
            this.musicTracks.add(new Entry(track.id(), new ItemStack(icon), ModText.literal(track.title())));
        }
    }

    public static SmartDispatchAppScreen massiveAuto() {
        SmartDispatchAppScreen screen = new SmartDispatchAppScreen();
        screen.openApp = App.MASSIVE_AUTO;
        return screen;
    }

    private static com.examplebett.client.sound.ChorusMusicTracks.Track findTrack(String id) {
        for (com.examplebett.client.sound.ChorusMusicTracks.Track track : com.examplebett.client.sound.ChorusMusicTracks.TRACKS) {
            if (track.id().equals(id)) {
                return track;
            }
        }
        return null;
    }

    private List<Entry> entries() {
        if (this.openApp == App.MUSIC) {
            return this.musicBrowsing ? this.musicTracks : List.of();
        }
        return this.openApp == null || this.openApp.weather || this.openApp == App.EMERGENCY || this.openApp == App.GTT
                || this.openApp == App.CLOCK || this.openApp == App.MASSIVE_AUTO || this.openApp == App.SLOT
                || this.openApp == App.SETTINGS ? List.of()
                : this.openApp.vehicles ? this.vehicles : this.shopItems;
    }

    private Layout layout() {
        int phoneH = Math.min(PHONE_MAX_HEIGHT, this.height - 16) / PX * PX;
        int phoneW = Math.round(phoneH * PHONE_ASPECT) / PX * PX;
        int x0 = (this.width - phoneW) / 2 / PX * PX;
        int y0 = (this.height - phoneH) / 2 / PX * PX;
        int x1 = x0 + phoneW;
        int y1 = y0 + phoneH;

        int[] home = this.skin.homeRect(x0, x1, y1 - this.skin.insetBottom, y1);
        return new Layout(x0, y0, x1, y1,
                x0 + this.skin.insetX, y0 + this.skin.insetTop, x1 - this.skin.insetX, y1 - this.skin.insetBottom,
                home[0], home[1], home[2], home[3]);
    }

    private int maxScrollRow(Layout l) {
        return Math.max(0, this.entries().size() - l.visibleRows());
    }

    private boolean isFree() {
        return this.minecraft != null && this.minecraft.player != null && ModPlayer.hasInstabuild(this.minecraft.player);
    }

    private static int costOf(App app) {
        return app != null && app.vehicles ? SmartDispatchAppItem.VEHICLE_CALL_COST : SmartDispatchAppItem.SHOP_COST;
    }

    private boolean hasEnoughEmeralds() {
        return this.minecraft != null && this.minecraft.player != null
                && ModPlayer.getInventory(this.minecraft.player).countItem(Items.EMERALD) >= costOf(this.openApp);
    }

    private static boolean inside(double mx, double my, int x0, int y0, int x1, int y1) {
        return mx >= x0 && mx < x1 && my >= y0 && my < y1;
    }

    static void fillPixelRounded(GuiGraphics g, int x0, int y0, int x1, int y1, int step, int color) {
        g.fill(x0 + step * 2, y0, x1 - step * 2, y0 + step, color);
        g.fill(x0 + step, y0 + step, x1 - step, y0 + step * 2, color);
        g.fill(x0, y0 + step * 2, x1, y1 - step * 2, color);
        g.fill(x0 + step, y1 - step * 2, x1 - step, y1 - step, color);
        g.fill(x0 + step * 2, y1 - step, x1 - step * 2, y1, color);
    }

    static void fillPixelCircle(GuiGraphics g, int cx, int cy, int radius, int color) {
        for (int dy = -radius; dy < radius; dy += PX) {
            double mid = dy + PX / 2.0;
            int half = (int) Math.round(Math.sqrt(Math.max(0.0, radius * (double) radius - mid * mid)) / PX) * PX;
            if (half > 0) {
                g.fill(cx - half, cy + dy, cx + half, cy + dy + PX, color);
            }
        }
    }

    private static int lerpColor(int a, int b, float t) {
        int r = Math.round(((a >> 16) & 0xFF) + (((b >> 16) & 0xFF) - ((a >> 16) & 0xFF)) * t);
        int gr = Math.round(((a >> 8) & 0xFF) + (((b >> 8) & 0xFF) - ((a >> 8) & 0xFF)) * t);
        int bl = Math.round((a & 0xFF) + ((b & 0xFF) - (a & 0xFF)) * t);
        return 0xFF000000 | (r << 16) | (gr << 8) | bl;
    }

    private void drawPhone(GuiGraphics g, Layout l) {
        this.skin.drawBody(g, l.x0(), l.y0(), l.x1(), l.y1(), l.sx0(), l.sy0(), l.sx1(), l.sy1(),
                new int[]{l.homeX0(), l.homeY0(), l.homeX1(), l.homeY1()});
    }

    private static void drawWallpaper(GuiGraphics g, String wallpaper, int x0, int y0, int x1, int y1) {
        if ("white".equals(wallpaper) || "gray".equals(wallpaper)) {
            g.fill(x0, y0, x1, y1, "white".equals(wallpaper) ? 0xFFFFFFFF : 0xFF8E8E93);
            return;
        }
        int[] fallback = switch (wallpaper) {
            case "sunset_beach" -> new int[]{0xFF8A83C6, 0xFFF4AE96};
            case "grass" -> new int[]{0xFF5FA83A, 0xFF7A4E28};
            case "flow" -> new int[]{0xFFE8336A, 0xFF4A6CD8};
            default -> null;
        };
        if (fallback != null) {
            ResourceLocation texture = AiObjectMod.id("textures/gui/smart_dispatch/wallpaper_" + wallpaper + ".png");
            int w = x1 - x0;
            int h = y1 - y0;
            int vH = WALLPAPER_TEXTURE_HEIGHT;
            int uW = Math.min(WALLPAPER_TEXTURE_WIDTH, Math.round((float) vH * w / Math.max(1, h)));
            if (uW == WALLPAPER_TEXTURE_WIDTH) {
                vH = Math.round((float) uW * h / Math.max(1, w));
            }
            float u = (WALLPAPER_TEXTURE_WIDTH - uW) / 2f;
            float v = (WALLPAPER_TEXTURE_HEIGHT - vH) / 2f;
            com.examplebett.compat.GuiTextures.blit(g, texture, x0, y0, u, v, w, h,
                    uW, vH, WALLPAPER_TEXTURE_WIDTH, WALLPAPER_TEXTURE_HEIGHT);
            return;
        }
        drawGradient(g, x0, y0, x1, y1, COLOR_WALLPAPER_TOP, COLOR_WALLPAPER_BOTTOM);
    }

    private static void drawGradient(GuiGraphics g, int x0, int y0, int x1, int y1, int top, int bottom) {
        int bands = Math.max(1, (y1 - y0) / (PX * 2));
        for (int i = 0; i < bands; i++) {
            int bandTop = y0 + i * PX * 2;
            int bandBottom = i == bands - 1 ? y1 : bandTop + PX * 2;
            g.fill(x0, bandTop, x1, bandBottom, lerpColor(top, bottom, (float) i / bands));
        }
    }

    private long levelDayTime() {

        return this.minecraft.level.getDayTime();
    }

    private String currentTimeText() {
        if (this.minecraft == null || this.minecraft.level == null) {
            return "--:--";
        }
        long dayTime = Math.floorMod(this.levelDayTime(), 24000L);
        int hours = (int) ((dayTime / 1000L + 6L) % 24L);
        int minutes = (int) (dayTime % 1000L * 60L / 1000L);
        return String.format("%d:%02d", hours, minutes);
    }

    private void drawStatusBar(GuiGraphics g, Layout l) {
        g.fill(l.sx0(), l.sy0(), l.sx1(), l.sy0() + STATUS_BAR_HEIGHT, COLOR_STATUS_BAR);
        this.drawSmallText(g, this.currentTimeText(), l.sx0() + 3, l.sy0() + 3, 0xFFFFFFFF);

        int bx1 = l.sx1() - 3;
        int by0 = l.sy0() + 3;
        g.fill(bx1 - 12, by0, bx1 - 1, by0 + 6, 0xFFFFFFFF);
        g.fill(bx1 - 1, by0 + 2, bx1, by0 + 4, 0xFFFFFFFF);
        g.fill(bx1 - 11, by0 + 1, bx1 - 2, by0 + 5, 0xFF000000);
        g.fill(bx1 - 10, by0 + 2, bx1 - 4, by0 + 4, 0xFF5BD35B);

        for (int i = 0; i < 4; i++) {
            int bx = bx1 - 26 + i * 3;
            g.fill(bx, by0 + 6 - (i + 1) * 3 / 2, bx + 2, by0 + 6, 0xFFFFFFFF);
        }
    }

    private int[] appIconRect(Layout l, int index) {
        int cellW = (l.sx1() - l.sx0()) / APP_COLUMNS;
        int x = l.sx0() + (index % APP_COLUMNS) * cellW + (cellW - APP_ICON_SIZE) / 2;
        int y = l.sy0() + STATUS_BAR_HEIGHT + 8 + (index / APP_COLUMNS) * (APP_ICON_SIZE + 20);
        return new int[]{x, y, x + APP_ICON_SIZE, y + APP_ICON_SIZE};
    }

    private void drawHome(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        drawWallpaper(g, this.wallpaper, l.sx0(), l.sy0(), l.sx1(), l.sy1());

        boolean lightWallpaper = "white".equals(this.wallpaper);
        int labelColor = lightWallpaper ? 0xFF1C1C1E : 0xFFFFFFFF;
        App[] apps = App.values();
        for (int i = 0; i < apps.length; i++) {
            App app = apps[i];
            int[] r = appIconRect(l, i);
            com.examplebett.compat.GuiTextures.blit(g, app.icon, r[0], r[1], 0f, 0f, APP_ICON_SIZE, APP_ICON_SIZE,
                    APP_ICON_TEXTURE_SIZE, APP_ICON_TEXTURE_SIZE, APP_ICON_TEXTURE_SIZE, APP_ICON_TEXTURE_SIZE);
            if (this.pending == null && inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {

                fillPixelRounded(g, r[0], r[1], r[2], r[3], 2, 0x33FFFFFF);
            }

            int cellWidth = (l.sx1() - l.sx0()) / APP_COLUMNS - 2;
            String[] labelLines = app.shortName().split("\n");
            for (int line = 0; line < labelLines.length; line++) {
                float scale = Math.min(SMALL_TEXT_SCALE, (float) cellWidth / Math.max(1, this.font.width(labelLines[line])));
                int labelWidth = Math.round(this.font.width(labelLines[line]) * scale);
                this.drawScaledText(g, net.minecraft.locale.Language.getInstance().getVisualOrder(
                                net.minecraft.network.chat.FormattedText.of(labelLines[line])), (r[0] + r[2] - labelWidth) / 2,
                        r[3] + 2 + line * 7, labelColor, scale);
            }
        }

        int dotsY = l.sy1() - PX * 4;
        int cx = (l.sx0() + l.sx1()) / 2 / PX * PX;
        int dimDot = lightWallpaper ? 0x881C1C1E : 0x88FFFFFF;
        g.fill(cx - PX * 3, dotsY, cx - PX * 2, dotsY + PX, labelColor);
        g.fill(cx - PX / 2, dotsY, cx + PX / 2 + 1, dotsY + PX, dimDot);
        g.fill(cx + PX * 2, dotsY, cx + PX * 3, dotsY + PX, dimDot);
    }

    private void drawApp(GuiGraphics g, Layout l, App app, int mouseX, int mouseY) {
        int headerTop = l.sy0() + STATUS_BAR_HEIGHT;
        g.fill(l.sx0(), l.sy0(), l.sx1(), l.sy1(), COLOR_ROW);
        g.fill(l.sx0(), l.sy0(), l.sx1(), headerTop + HEADER_HEIGHT, app.headerColor);

        int ax = l.sx0() + 4;
        int ay = headerTop + HEADER_HEIGHT / 2;
        for (int i = 0; i < 4; i++) {
            g.fill(ax + i, ay - i - 1, ax + i + 1, ay - i + 1, app.titleColor);
            g.fill(ax + i, ay + i - 1, ax + i + 1, ay + i + 1, app.titleColor);
        }
        int titleWidth = this.font.width(app.title());
        int titleRoom = l.sx1() - l.sx0() - 24;
        if (titleWidth <= titleRoom) {
            g.drawString(this.font, app.title(), (l.sx0() + l.sx1() - titleWidth) / 2, headerTop + 4, app.titleColor, false);
        } else {
            int scaled = Math.round(titleWidth * SMALL_TEXT_SCALE);
            this.drawSmallText(g, app.title(), (l.sx0() + l.sx1() - scaled) / 2, headerTop + 5, app.titleColor);
        }
        if (app.weather) {
            this.drawWeather(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.EMERGENCY) {
            this.drawEmergency(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.DETONATOR) {
            this.drawDetonator(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.CLOCK) {
            this.drawClock(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.MASSIVE_AUTO) {
            this.drawMassiveAuto(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.SLOT) {
            this.drawSlot(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.SETTINGS) {
            this.drawSettings(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.MUSIC && !this.musicBrowsing) {
            this.drawMusicHome(g, l, mouseX, mouseY);
            return;
        }
        int costTop = headerTop + HEADER_HEIGHT;
        g.fill(l.sx0(), costTop, l.sx1(), costTop + COST_BAR_HEIGHT, COLOR_COST_BAR);
        Component cost = app == App.MUSIC ? this.musicStatus() : this.isFree()
                ? ModText.translatable("gui.mineturismo.smart_dispatch.free")
                : ModText.translatable(app.vehicles ? "gui.mineturismo.smart_dispatch.cost" : "gui.mineturismo.smart_dispatch.price",
                        costOf(app));
        this.drawSmallText(g, cost.getString(), l.sx0() + 3, costTop + 2, 0xFFE8E8E8);

        List<Entry> entries = this.entries();
        int listTop = l.listTop();
        int rows = l.visibleRows();
        this.scrollRow = Math.max(0, Math.min(this.maxScrollRow(l), this.scrollRow));
        int listRight = l.sx1() - 4;
        for (int i = 0; i < rows; i++) {
            int index = this.scrollRow + i;
            if (index >= entries.size()) {
                break;
            }
            Entry e = entries.get(index);
            int top = listTop + i * ROW_HEIGHT;
            boolean hover = this.pending == null && inside(mouseX, mouseY, l.sx0(), top, listRight, top + ROW_HEIGHT);
            boolean playing = app == App.MUSIC && com.examplebett.client.sound.ChorusMusicPlayer.current() != null
                    && com.examplebett.client.sound.ChorusMusicPlayer.current().id().equals(e.id());
            g.fill(l.sx0(), top, listRight, top + ROW_HEIGHT,
                    hover ? COLOR_ROW_HOVER : playing ? 0xFFFFD9DF : index % 2 == 0 ? COLOR_ROW : COLOR_ROW_ALT);
            g.renderItem(e.stack(), l.sx0() + 2, top + 2);
            int textX = l.sx0() + 21;
            this.drawSmallText(g, this.fit(e.name().getString(), listRight - textX - 2), textX, top + 7, 0xFF202024);
        }

        g.fill(listRight, listTop, l.sx1(), l.sy1(), COLOR_SCROLL_TRACK);
        int max = this.maxScrollRow(l);
        if (max > 0) {
            int trackH = l.sy1() - listTop;
            int thumbH = this.thumbHeight(l);
            int thumbTop = listTop + (trackH - thumbH) * this.scrollRow / max;
            g.fill(listRight + 1, thumbTop, l.sx1() - 1, thumbTop + thumbH, COLOR_SCROLL_THUMB);
        }
        if (System.currentTimeMillis() < this.purchasedBannerUntil) {
            this.aboveItems(g);
            String text = ModText.translatable("gui.mineturismo.smart_dispatch.purchased").getString();
            int w = Math.round(this.font.width(text) * SMALL_TEXT_SCALE) + 10;
            int bx0 = (l.sx0() + l.sx1() - w) / 2;
            int by0 = l.sy1() - 18;
            fillPixelRounded(g, bx0, by0, bx0 + w, by0 + 12, 1, COLOR_BANNER);
            this.drawSmallText(g, text, bx0 + 5, by0 + 3, 0xFFFFFFFF);
        }
    }

    private int settingsRowCount() {
        return this.settingsPage == SETTINGS_PAGE_WALLPAPER ? SmartDispatchAppItem.WALLPAPERS.length
                : this.settingsPage == SETTINGS_PAGE_MODEL ? SmartDispatchAppItem.PHONE_MODELS.length : 3;
    }

    private record ControlRow(String labelKey, String keyText, net.minecraft.client.KeyMapping rebind) {
    }

    private static String keyName(net.minecraft.client.KeyMapping mapping) {
        return mapping.getTranslatedKeyMessage().getString();
    }

    private List<ControlRow> controlRows() {
        net.minecraft.client.Options o = this.minecraft.options;
        net.minecraft.client.KeyMapping horn = com.examplebett.client.AiObjectModClient.hornKey();
        net.minecraft.client.KeyMapping trailer = com.examplebett.client.AiObjectModClient.trailerCouplingKey();
        String p = "gui.mineturismo.smart_dispatch.controls_";
        return List.of(
                new ControlRow(p + "accelerate", keyName(o.keyUp), null),
                new ControlRow(p + "brake", keyName(o.keyDown), null),
                new ControlRow(p + "steer", keyName(o.keyLeft) + " / " + keyName(o.keyRight), null),
                new ControlRow(p + "drift", keyName(o.keyJump), null),
                new ControlRow(p + "burnout", keyName(o.keyUp) + "+" + keyName(o.keyDown), null),

                new ControlRow(p + "jturn", keyName(o.keyDown) + "+" + keyName(o.keyLeft) + "/" + keyName(o.keyRight), null),

                new ControlRow(p + "wheelie", keyName(o.keySprint), null),
                new ControlRow(p + "aircraft", keyName(o.keyJump) + " / " + keyName(o.keySprint), null),
                new ControlRow(p + "dismount", keyName(o.keyShift), null),
                new ControlRow(p + "horn", keyName(horn), horn),
                new ControlRow(p + "trailer", keyName(trailer), trailer));
    }

    private int[] controlsRowRect(Layout l, int index) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT + 16 + index * CONTROLS_ROW_HEIGHT;
        return new int[]{l.sx0() + 5, top, l.sx1() - 5, top + CONTROLS_ROW_HEIGHT};
    }

    private void drawControls(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int width = l.sx1() - l.sx0();
        this.drawSmallText(g, this.fit(ModText.translatable("gui.mineturismo.smart_dispatch.settings_controls").getString(), width - 12),
                l.sx0() + 7, top + 6, 0xFF6C6C72);
        List<ControlRow> rows = this.controlRows();
        int[] first = this.controlsRowRect(l, 0);
        int[] last = this.controlsRowRect(l, rows.size() - 1);
        fillPixelRounded(g, first[0], first[1], last[2], last[3], 1, 0xFFFFFFFF);
        for (int i = 0; i < rows.size(); i++) {
            ControlRow row = rows.get(i);
            int[] r = this.controlsRowRect(l, i);
            if (row.rebind() != null && inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
                g.fill(r[0] + 1, r[1] + (i == 0 ? 1 : 0), r[2] - 1, r[3] - (i == rows.size() - 1 ? 1 : 0), 0xFFE5E5EA);
            }
            if (i > 0) {
                g.fill(r[0] + 4, r[1], r[2], r[1] + 1, 0xFFE5E5EA);
            }

            boolean waiting = row.rebind() != null && row.rebind() == this.rebindingKey;
            String key = waiting ? ModText.translatable("gui.mineturismo.smart_dispatch.controls_press_key").getString() : row.keyText();
            key = this.fit(key, (r[2] - r[0]) / 2 - 4);
            int keyWidth = Math.round(this.font.width(key) * SMALL_TEXT_SCALE);
            int keyColor = waiting ? 0xFFFF9500 : row.rebind() != null ? 0xFF0A84FF : 0xFF6C6C72;
            this.drawSmallText(g, key, r[2] - 4 - keyWidth, r[1] + 3, keyColor);
            String label = this.fit(ModText.translatable(row.labelKey()).getString(), r[2] - r[0] - keyWidth - 12);
            this.drawSmallText(g, label, r[0] + 4, r[1] + 3, 0xFF202024);
        }

        int noteY = last[3] + 4;
        for (String noteKey : new String[]{"controls_note_rebind", "controls_note_vanilla"}) {
            String note = ModText.translatable("gui.mineturismo.smart_dispatch." + noteKey).getString();
            for (String line : this.wrapSmall(note, width - 12)) {

                if (noteY + 7 > l.sy1() - 2) {
                    break;
                }
                this.drawSmallText(g, line, l.sx0() + 7, noteY, 0xFF8E8E93);
                noteY += 7;
            }
            noteY += 2;
        }
    }

    private List<String> wrapSmall(String text, int maxWidth) {
        List<String> lines = new java.util.ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String next = line.toString() + text.charAt(i);
            if (line.length() > 0 && Math.round(this.font.width(next) * SMALL_TEXT_SCALE) > maxWidth) {
                lines.add(line.toString());
                line.setLength(0);
            }
            line.append(text.charAt(i));
        }
        if (line.length() > 0) {
            lines.add(line.toString());
        }
        return lines;
    }

    private boolean handleControlsClick(Layout l, double mouseX, double mouseY) {
        List<ControlRow> rows = this.controlRows();
        for (int i = 0; i < rows.size(); i++) {
            int[] r = this.controlsRowRect(l, i);
            if (rows.get(i).rebind() != null && inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {

                this.rebindingKey = this.rebindingKey == rows.get(i).rebind() ? null : rows.get(i).rebind();
                return true;
            }
        }
        this.rebindingKey = null;
        return false;
    }

    private boolean handleRebindKey(int keyCode, java.util.function.Supplier<com.mojang.blaze3d.platform.InputConstants.Key> key) {
        if (this.rebindingKey == null || this.openApp != App.SETTINGS || this.settingsPage != SETTINGS_PAGE_CONTROLS) {
            return false;
        }
        if (keyCode != org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            this.rebindingKey.setKey(key.get());
            net.minecraft.client.KeyMapping.resetMapping();
            this.minecraft.options.save();
        }
        this.rebindingKey = null;
        return true;
    }

    private int[] settingsRowRect(Layout l, int index) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT + 16 + index * SETTINGS_ROW_HEIGHT;
        return new int[]{l.sx0() + 5, top, l.sx1() - 5, top + SETTINGS_ROW_HEIGHT};
    }

    private void drawSettings(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFFF2F2F7);
        if (this.settingsPage == SETTINGS_PAGE_CONTROLS) {
            this.drawControls(g, l, mouseX, mouseY);
            return;
        }
        String section = this.settingsPage == SETTINGS_PAGE_WALLPAPER ? "gui.mineturismo.smart_dispatch.settings_wallpaper"
                : this.settingsPage == SETTINGS_PAGE_MODEL ? "gui.mineturismo.smart_dispatch.settings_model" : null;
        if (section != null) {
            this.drawSmallText(g, this.fit(ModText.translatable(section).getString(), l.sx1() - l.sx0() - 12), l.sx0() + 7, top + 6, 0xFF6C6C72);
        }
        int count = this.settingsRowCount();
        int[] first = this.settingsRowRect(l, 0);
        int[] last = this.settingsRowRect(l, count - 1);
        fillPixelRounded(g, first[0], first[1], last[2], last[3], 1, 0xFFFFFFFF);
        for (int i = 0; i < count; i++) {
            int[] r = this.settingsRowRect(l, i);
            if (inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
                g.fill(r[0] + 1, r[1] + (i == 0 ? 1 : 0), r[2] - 1, r[3] - (i == count - 1 ? 1 : 0), 0xFFE5E5EA);
            }
            if (i > 0) {
                g.fill(r[0] + 6, r[1], r[2], r[1] + 1, 0xFFD8D8DE);
            }
            int textX = r[0] + 6;
            String label;
            boolean selected = false;
            if (this.settingsPage == SETTINGS_PAGE_WALLPAPER) {
                String id = SmartDispatchAppItem.WALLPAPERS[i];

                g.fill(r[0] + 4, r[1] + 2, r[0] + 16, r[3] - 2, 0xFF121216);
                drawWallpaper(g, id, r[0] + 5, r[1] + 3, r[0] + 15, r[3] - 3);
                textX = r[0] + 21;
                label = ModText.translatable("gui.mineturismo.smart_dispatch.wallpaper_" + id).getString();
                selected = id.equals(this.wallpaper);
            } else if (this.settingsPage == SETTINGS_PAGE_MODEL) {
                String id = SmartDispatchAppItem.PHONE_MODELS[i];
                ItemStack icon = new ItemStack(com.examplebett.item.AiObjectModItems.SMART_DISPATCH_APP);
                SmartDispatchAppItem.applyPhoneModelIcon(icon, id);
                g.renderItem(icon, r[0] + 3, r[1] + 2);
                textX = r[0] + 21;
                label = PHONE_MODEL_NAMES[i] != null ? PHONE_MODEL_NAMES[i]
                        : ModText.translatable("gui.mineturismo.smart_dispatch.model_classic").getString();
                selected = id.equals(this.phoneModel);
            } else {
                label = ModText.translatable(i == 0 ? "gui.mineturismo.smart_dispatch.settings_wallpaper"
                        : i == 1 ? "gui.mineturismo.smart_dispatch.settings_model"
                        : "gui.mineturismo.smart_dispatch.settings_controls").getString();

                int ax = r[2] - 8;
                int ay = (r[1] + r[3]) / 2;
                for (int k = 0; k < 3; k++) {
                    g.fill(ax + k, ay - 3 + k, ax + k + 1, ay - 1 + k, 0xFFB4B4BA);
                    g.fill(ax + k, ay + 1 - k, ax + k + 1, ay + 3 - k, 0xFFB4B4BA);
                }
            }
            this.drawSmallText(g, this.fit(label, r[2] - textX - 14), textX, (r[1] + r[3]) / 2 - 3, 0xFF202024);
            if (selected) {

                int cx0 = r[2] - 11;
                int cy0 = (r[1] + r[3]) / 2;
                int[][] check = {{0, 0}, {1, 1}, {2, 2}, {3, 1}, {4, 0}, {5, -1}, {6, -2}, {7, -3}};
                for (int[] p : check) {
                    g.fill(cx0 + p[0], cy0 + p[1] - 1, cx0 + p[0] + 1, cy0 + p[1] + 1, 0xFF0A84FF);
                }
            }
        }
    }

    private boolean handleSettingsClick(Layout l, double mouseX, double mouseY) {
        if (this.settingsPage == SETTINGS_PAGE_CONTROLS) {
            return this.handleControlsClick(l, mouseX, mouseY);
        }
        for (int i = 0; i < this.settingsRowCount(); i++) {
            int[] r = this.settingsRowRect(l, i);
            if (!inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
                continue;
            }
            if (this.settingsPage == SETTINGS_PAGE_MAIN) {
                this.settingsPage = i == 0 ? SETTINGS_PAGE_WALLPAPER : i == 1 ? SETTINGS_PAGE_MODEL : SETTINGS_PAGE_CONTROLS;
                this.rebindingKey = null;
                return true;
            }
            if (this.settingsPage == SETTINGS_PAGE_WALLPAPER) {
                this.wallpaper = SmartDispatchAppItem.WALLPAPERS[i];
            } else {
                this.phoneModel = SmartDispatchAppItem.PHONE_MODELS[i];
                this.skin = PhoneSkin.byId(this.phoneModel);
            }
            com.examplebett.network.SetPhoneStylePayload payload = new com.examplebett.network.SetPhoneStylePayload(this.phoneModel, this.wallpaper);
            ModClientNetworking.sendToServer(com.examplebett.network.SetPhoneStylePayload.ID, payload, com.examplebett.network.SetPhoneStylePayload::write);
            return true;
        }
        return false;
    }

    private static final int WEATHER_SNOW = 3;
    private static final int WEATHER_BUTTON_HEIGHT = 14;
    private static final int WEATHER_BUTTON_GAP = 3;

    private static final int WEATHER_ICON_UNIT = 2;
    private static final int[] WEATHER_PICKS = {SmartDispatchAppItem.WEATHER_CLEAR, SmartDispatchAppItem.WEATHER_RAIN, SmartDispatchAppItem.WEATHER_THUNDER};
    private static final String[] WEATHER_KEYS = {"weather_clear", "weather_rain", "weather_thunder", "weather_snow"};

    private int currentWeather() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) {
            return SmartDispatchAppItem.WEATHER_CLEAR;
        }
        net.minecraft.client.multiplayer.ClientLevel level = this.minecraft.level;
        if (!level.isRaining()) {
            return SmartDispatchAppItem.WEATHER_CLEAR;
        }
        net.minecraft.core.BlockPos pos = this.minecraft.player.blockPosition();

        net.minecraft.world.level.biome.Biome.Precipitation precipitation = level.getBiome(pos).getPrecipitation();
        if (precipitation == net.minecraft.world.level.biome.Biome.Precipitation.NONE) {
            return SmartDispatchAppItem.WEATHER_CLEAR;
        }
        if (level.isThundering()) {
            return SmartDispatchAppItem.WEATHER_THUNDER;
        }
        return precipitation == net.minecraft.world.level.biome.Biome.Precipitation.SNOW ? WEATHER_SNOW : SmartDispatchAppItem.WEATHER_RAIN;
    }

    private Component currentPlaceName() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) {
            return ModText.literal("-");
        }

        java.util.Optional<net.minecraft.resources.ResourceKey<net.minecraft.world.level.biome.Biome>> biomeKey = this.minecraft.level.getBiomeName(this.minecraft.player.blockPosition());

        return biomeKey.<Component>map(key -> ModText.translatable(net.minecraft.Util.makeDescriptionId("biome", key.location())))
                .orElse(ModText.literal("-"));
    }

    private int weatherButtonTop(Layout l, int index) {

        int changeTop = l.sy1() - WEATHER_BUTTON_HEIGHT - 5;
        return index == WEATHER_PICKS.length ? changeTop
                : changeTop - (WEATHER_PICKS.length - index) * (WEATHER_BUTTON_HEIGHT + WEATHER_BUTTON_GAP);
    }

    private void drawWeatherButton(GuiGraphics g, Layout l, int top, String label, int color, int hoverColor, int textColor,
                                   int mouseX, int mouseY) {
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        boolean hover = inside(mouseX, mouseY, x0, top, x1, top + WEATHER_BUTTON_HEIGHT);
        fillPixelRounded(g, x0, top, x1, top + WEATHER_BUTTON_HEIGHT, 1, hover ? hoverColor : color);
        g.drawString(this.font, label, (x0 + x1 - this.font.width(label)) / 2, top + 3, textColor, false);
    }

    private void drawWeather(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int weather = this.currentWeather();

        int skyTop;
        int skyBottom;
        switch (weather) {
            case SmartDispatchAppItem.WEATHER_RAIN -> { skyTop = 0xFF7A8796; skyBottom = 0xFF4E5A68; }
            case SmartDispatchAppItem.WEATHER_THUNDER -> { skyTop = 0xFF4A4F5C; skyBottom = 0xFF262A33; }
            case WEATHER_SNOW -> { skyTop = 0xFFB9C9DA; skyBottom = 0xFF8EA3BA; }
            default -> { skyTop = 0xFF6FB8F5; skyBottom = 0xFF3D8BD9; }
        }
        int bands = Math.max(1, (l.sy1() - top) / (PX * 2));
        for (int i = 0; i < bands; i++) {
            int bandTop = top + i * PX * 2;
            g.fill(l.sx0(), bandTop, l.sx1(), i == bands - 1 ? l.sy1() : bandTop + PX * 2, lerpColor(skyTop, skyBottom, (float) i / bands));
        }
        int cx = (l.sx0() + l.sx1()) / 2;
        int iconSize = 16 * WEATHER_ICON_UNIT;
        int iconTop = top + 8;
        this.drawWeatherIcon(g, cx - iconSize / 2, iconTop, weather);

        int textTop = iconTop + iconSize + 6;
        String placeLabel = ModText.translatable("gui.mineturismo.smart_dispatch.weather_location").getString();
        int labelWidth = Math.round(this.font.width(placeLabel) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, placeLabel, cx - labelWidth / 2, textTop, 0xDDFFFFFF);
        String place = this.currentPlaceName().getString();
        int room = l.sx1() - l.sx0() - 8;
        if (this.font.width(place) <= room) {
            g.drawString(this.font, place, cx - this.font.width(place) / 2, textTop + 9, 0xFFFFFFFF, true);
        } else {
            String fitted = this.fit(place, room);
            this.drawSmallText(g, fitted, cx - Math.round(this.font.width(fitted) * SMALL_TEXT_SCALE) / 2, textTop + 10, 0xFFFFFFFF);
        }
        String weatherName = ModText.translatable("gui.mineturismo.smart_dispatch." + WEATHER_KEYS[weather]).getString();
        g.drawString(this.font, weatherName, cx - this.font.width(weatherName) / 2, textTop + 22, 0xFFFFFFFF, true);

        if (this.weatherPickerOpen) {
            for (int i = 0; i < WEATHER_PICKS.length; i++) {
                String label = ModText.translatable("gui.mineturismo.smart_dispatch." + WEATHER_KEYS[WEATHER_PICKS[i]]).getString();
                this.drawWeatherButton(g, l, this.weatherButtonTop(l, i), label, 0xFFFFFFFF, 0xFFFFF1B8, 0xFF202024, mouseX, mouseY);
            }
        }
        String change = ModText.translatable("gui.mineturismo.smart_dispatch.weather_change").getString();
        this.drawWeatherButton(g, l, this.weatherButtonTop(l, WEATHER_PICKS.length), change, 0xFF2A2A30, 0xFF44444E, 0xFFFFFFFF, mouseX, mouseY);
    }

    private void cell(GuiGraphics g, int ox, int oy, int ux, int uy, int w, int h, int color) {
        int u = WEATHER_ICON_UNIT;
        g.fill(ox + ux * u, oy + uy * u, ox + (ux + w) * u, oy + (uy + h) * u, color);
    }

    private void disc(GuiGraphics g, int ox, int oy, int cx, int cy, int r, int color) {
        for (int dy = -r; dy <= r; dy++) {
            int w = (int) Math.round(Math.sqrt(r * r - dy * dy));
            this.cell(g, ox, oy, cx - w, cy + dy, w * 2 + 1, 1, color);
        }
    }

    private void cloud(GuiGraphics g, int ox, int oy, int color, int shade) {
        this.disc(g, ox, oy, 5, 6, 3, color);
        this.disc(g, ox, oy, 9, 5, 4, color);
        this.cell(g, ox, oy, 2, 6, 12, 4, color);
        this.cell(g, ox, oy, 2, 9, 12, 1, shade);
    }

    private void drawWeatherIcon(GuiGraphics g, int ox, int oy, int weather) {
        switch (weather) {
            case SmartDispatchAppItem.WEATHER_CLEAR -> {
                int sun = 0xFFFFD640;
                int rim = 0xFFF5A01E;
                this.cell(g, ox, oy, 7, 0, 2, 2, sun);
                this.cell(g, ox, oy, 7, 14, 2, 2, sun);
                this.cell(g, ox, oy, 0, 7, 2, 2, sun);
                this.cell(g, ox, oy, 14, 7, 2, 2, sun);
                this.cell(g, ox, oy, 2, 2, 2, 2, sun);
                this.cell(g, ox, oy, 12, 2, 2, 2, sun);
                this.cell(g, ox, oy, 2, 12, 2, 2, sun);
                this.cell(g, ox, oy, 12, 12, 2, 2, sun);
                this.disc(g, ox, oy, 8, 8, 4, rim);
                this.disc(g, ox, oy, 8, 8, 3, sun);
                this.cell(g, ox, oy, 6, 6, 1, 1, 0xFFFFF4B0);
            }
            case SmartDispatchAppItem.WEATHER_RAIN -> {
                this.cloud(g, ox, oy, 0xFFE4E8EE, 0xFFB4BCC8);
                int drop = 0xFF5FB6FF;
                this.cell(g, ox, oy, 4, 11, 1, 2, drop);
                this.cell(g, ox, oy, 8, 12, 1, 2, drop);
                this.cell(g, ox, oy, 12, 11, 1, 2, drop);
                this.cell(g, ox, oy, 6, 14, 1, 2, drop);
                this.cell(g, ox, oy, 10, 14, 1, 2, drop);
            }
            case SmartDispatchAppItem.WEATHER_THUNDER -> {
                this.cloud(g, ox, oy, 0xFF8A909C, 0xFF5E6470);
                int bolt = 0xFFFFE14A;
                this.cell(g, ox, oy, 8, 10, 2, 1, bolt);
                this.cell(g, ox, oy, 7, 11, 2, 1, bolt);
                this.cell(g, ox, oy, 6, 12, 4, 1, bolt);
                this.cell(g, ox, oy, 8, 13, 1, 1, bolt);
                this.cell(g, ox, oy, 7, 14, 1, 1, bolt);
                this.cell(g, ox, oy, 6, 15, 1, 1, bolt);
            }
            default -> {
                this.cloud(g, ox, oy, 0xFFF4F6FA, 0xFFC4CCD8);
                int flake = 0xFFFFFFFF;
                this.cell(g, ox, oy, 4, 12, 1, 1, flake);
                this.cell(g, ox, oy, 8, 11, 1, 1, flake);
                this.cell(g, ox, oy, 12, 12, 1, 1, flake);
                this.cell(g, ox, oy, 6, 14, 1, 1, flake);
                this.cell(g, ox, oy, 10, 14, 1, 1, flake);
            }
        }
    }

    private boolean handleWeatherClick(Layout l, double mouseX, double mouseY) {
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        int changeTop = this.weatherButtonTop(l, WEATHER_PICKS.length);
        if (inside(mouseX, mouseY, x0, changeTop, x1, changeTop + WEATHER_BUTTON_HEIGHT)) {
            this.weatherPickerOpen = !this.weatherPickerOpen;
            return true;
        }
        if (this.weatherPickerOpen) {
            for (int i = 0; i < WEATHER_PICKS.length; i++) {
                int top = this.weatherButtonTop(l, i);
                if (inside(mouseX, mouseY, x0, top, x1, top + WEATHER_BUTTON_HEIGHT)) {
                    com.examplebett.network.SetAppWeatherPayload payload = new com.examplebett.network.SetAppWeatherPayload(WEATHER_PICKS[i]);
                    ModClientNetworking.sendToServer(com.examplebett.network.SetAppWeatherPayload.ID, payload, com.examplebett.network.SetAppWeatherPayload::write);
                    this.weatherPickerOpen = false;
                    return true;
                }
            }
        }
        return false;
    }

    private static final int EMERGENCY_BUTTON_HEIGHT = 30;

    private int[] emergencyButtonRect(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int y0 = (top + l.sy1()) / 2 - EMERGENCY_BUTTON_HEIGHT / 2;
        return new int[]{l.sx0() + 10, y0, l.sx1() - 10, y0 + EMERGENCY_BUTTON_HEIGHT};
    }

    private void drawEmergency(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFFF6F0F0);
        String lead = ModText.translatable("gui.mineturismo.smart_dispatch.emergency_lead").getString();
        String leadFitted = this.fit(lead, l.sx1() - l.sx0() - 8);
        this.drawSmallText(g, leadFitted, cx - Math.round(this.font.width(leadFitted) * SMALL_TEXT_SCALE) / 2, top + 10, 0xFF5A1C1C);
        int[] r = this.emergencyButtonRect(l);
        boolean hover = inside(mouseX, mouseY, r[0], r[1], r[2], r[3]);

        fillPixelRounded(g, r[0], r[1] + 2, r[2], r[3] + 2, PX, 0xFF8E1414);
        fillPixelRounded(g, r[0], r[1], r[2], r[3], PX, hover ? 0xFFEF4A3E : 0xFFD32F2F);
        String label = ModText.translatable("gui.mineturismo.smart_dispatch.emergency_call").getString();
        g.drawString(this.font, label, cx - this.font.width(label) / 2, (r[1] + r[3]) / 2 - 4, 0xFFFFFFFF, true);

        Component note = ModText.translatable("gui.mineturismo.smart_dispatch.emergency_note");
        List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(note, Math.round((l.sx1() - l.sx0() - 12) / SMALL_TEXT_SCALE));
        for (int i = 0; i < Math.min(3, lines.size()); i++) {
            int w = Math.round(this.font.width(lines.get(i)) * SMALL_TEXT_SCALE);
            this.drawSmallText(g, lines.get(i), cx - w / 2, r[3] + 10 + i * 8, 0xFF6A6A70);
        }
    }

    private boolean handleEmergencyClick(Layout l, double mouseX, double mouseY) {
        int[] r = this.emergencyButtonRect(l);
        if (!inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
            return false;
        }
        com.examplebett.network.RequestEmergencyCallPayload payload = new com.examplebett.network.RequestEmergencyCallPayload();
        ModClientNetworking.sendToServer(com.examplebett.network.RequestEmergencyCallPayload.ID, payload, com.examplebett.network.RequestEmergencyCallPayload::write);
        this.onClose();
        return true;
    }

    private static final int DETONATOR_BUTTON_SIZE = 40;

    private int[] detonatorButtonRect(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        int y0 = (top + l.sy1()) / 2 - DETONATOR_BUTTON_SIZE / 2 - 6;
        return new int[]{cx - DETONATOR_BUTTON_SIZE / 2, y0, cx + DETONATOR_BUTTON_SIZE / 2, y0 + DETONATOR_BUTTON_SIZE};
    }

    private void drawDetonator(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFF1C1D21);
        String lead = ModText.translatable("gui.mineturismo.smart_dispatch.detonator_lead").getString();
        String leadFitted = this.fit(lead, l.sx1() - l.sx0() - 8);
        this.drawSmallText(g, leadFitted, cx - Math.round(this.font.width(leadFitted) * SMALL_TEXT_SCALE) / 2, top + 10, 0xFFFFC94A);
        int[] r = this.detonatorButtonRect(l);
        boolean hover = inside(mouseX, mouseY, r[0], r[1], r[2], r[3]);

        int bcx = (r[0] + r[2]) / 2;
        int bcy = (r[1] + r[3]) / 2;
        int radius = DETONATOR_BUTTON_SIZE / 2;
        fillPixelCircle(g, bcx, bcy + 1, radius + 4, 0xFF96989F);
        fillPixelCircle(g, bcx, bcy + 3, radius, 0xFF780E12);
        fillPixelCircle(g, bcx, bcy, radius, hover ? 0xFFF04040 : 0xFFDE2424);
        String label = ModText.translatable("gui.mineturismo.smart_dispatch.detonator_button").getString();
        g.drawString(this.font, label, cx - this.font.width(label) / 2, (r[1] + r[3]) / 2 - 4, 0xFFFFFFFF, true);

        int stripeTop = r[3] + 10;
        for (int x = l.sx0(); x < l.sx1(); x += PX * 2) {
            int stripe = ((x - l.sx0()) / (PX * 2)) % 2 == 0 ? 0xFFF6C41E : 0xFF18181A;
            g.fill(x, stripeTop, Math.min(l.sx1(), x + PX * 2), stripeTop + PX * 2, stripe);
        }
        Component note = ModText.translatable("gui.mineturismo.smart_dispatch.detonator_note");
        List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(note, Math.round((l.sx1() - l.sx0() - 12) / SMALL_TEXT_SCALE));
        for (int i = 0; i < Math.min(3, lines.size()); i++) {
            int w = Math.round(this.font.width(lines.get(i)) * SMALL_TEXT_SCALE);
            this.drawSmallText(g, lines.get(i), cx - w / 2, stripeTop + PX * 2 + 6 + i * 8, 0xFFB0B0B8);
        }
    }

    private boolean handleDetonatorClick(Layout l, double mouseX, double mouseY) {
        int[] r = this.detonatorButtonRect(l);
        if (!inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
            return false;
        }
        com.examplebett.network.DetonateBombsPayload payload = new com.examplebett.network.DetonateBombsPayload();
        ModClientNetworking.sendToServer(com.examplebett.network.DetonateBombsPayload.ID, payload, com.examplebett.network.DetonateBombsPayload::write);
        this.onClose();
        return true;
    }

    private static final int MUSIC_BUTTON_HEIGHT = 16;
    private static final int MUSIC_PINK = 0xFFF5354F;
    private static final int MUSIC_PINK_HOVER = 0xFFFF5A70;

    private Component musicStatus() {
        com.examplebett.client.sound.ChorusMusicTracks.Track current = com.examplebett.client.sound.ChorusMusicPlayer.current();
        return current == null ? ModText.translatable("gui.mineturismo.smart_dispatch.music_stopped")
                : ModText.translatable("gui.mineturismo.smart_dispatch.music_now_playing", current.title());
    }

    private int[][] musicRects(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        int browseTop = top + 34;
        int volumeTop = browseTop + MUSIC_BUTTON_HEIGHT + 22;
        int rowTop = volumeTop + 18;
        int half = (x1 - x0 - 4) / 2;
        return new int[][]{
                {x0, browseTop, x1, browseTop + MUSIC_BUTTON_HEIGHT},
                {x0, volumeTop, x1, volumeTop + 8},
                {x0, rowTop, x0 + half, rowTop + MUSIC_BUTTON_HEIGHT},
                {x1 - half, rowTop, x1, rowTop + MUSIC_BUTTON_HEIGHT}
        };
    }

    private double musicVolume() {
        return this.minecraft.options.getSoundSourceVolume(net.minecraft.sounds.SoundSource.MUSIC);
    }

    private void setMusicVolume(double volume) {
        this.minecraft.options.setSoundCategoryVolume(net.minecraft.sounds.SoundSource.MUSIC, (float) volume);
    }

    private void drawMusicButton(GuiGraphics g, int[] r, String label, int color, int hoverColor, int textColor, int mouseX, int mouseY) {
        boolean hover = this.pending == null && inside(mouseX, mouseY, r[0], r[1], r[2], r[3]);
        fillPixelRounded(g, r[0], r[1], r[2], r[3], 1, hover ? hoverColor : color);
        String fitted = this.fit(label, r[2] - r[0] - 4);
        int w = Math.round(this.font.width(fitted) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, fitted, (r[0] + r[2] - w) / 2, (r[1] + r[3]) / 2 - 3, textColor);
    }

    private void drawMusicHome(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFFFFF5F7);

        String status = this.fit(this.musicStatus().getString(), l.sx1() - l.sx0() - 22);
        int statusWidth = Math.round(this.font.width(status) * SMALL_TEXT_SCALE);
        int noteX = cx - (statusWidth + 10) / 2;
        int noteY = top + 10;
        g.fill(noteX + 3, noteY, noteX + 4, noteY + 6, MUSIC_PINK);
        g.fill(noteX + 4, noteY, noteX + 6, noteY + 1, MUSIC_PINK);
        g.fill(noteX + 1, noteY + 5, noteX + 4, noteY + 7, MUSIC_PINK);
        this.drawSmallText(g, status, noteX + 10, noteY + 1, 0xFF3A2A2E);

        int[][] r = this.musicRects(l);
        this.drawMusicButton(g, r[0], ModText.translatable("gui.mineturismo.smart_dispatch.music_browse").getString(),
                MUSIC_PINK, MUSIC_PINK_HOVER, 0xFFFFFFFF, mouseX, mouseY);

        double volume = this.musicVolume();
        String volumeLabel = ModText.translatable("soundCategory.music").getString() + ": " + Math.round(volume * 100) + "%";
        this.drawSmallText(g, volumeLabel, r[1][0], r[1][1] - 9, 0xFF3A2A2E);
        int[] bar = r[1];
        int trackTop = (bar[1] + bar[3]) / 2 - 1;
        g.fill(bar[0], trackTop, bar[2], trackTop + 3, 0xFFE2C6CC);
        int knobX = bar[0] + (int) Math.round((bar[2] - bar[0] - 6) * volume);
        g.fill(bar[0], trackTop, knobX + 3, trackTop + 3, MUSIC_PINK);
        fillPixelRounded(g, knobX, bar[1], knobX + 6, bar[3], 1, this.draggingVolume ? MUSIC_PINK_HOVER : 0xFFFFFFFF);
        g.fill(knobX + 2, bar[1] + 2, knobX + 4, bar[3] - 2, MUSIC_PINK);

        Component loopState = ModText.translatable(com.examplebett.client.sound.ChorusMusicPlayer.isLoop() ? "options.on" : "options.off");
        boolean loopOn = com.examplebett.client.sound.ChorusMusicPlayer.isLoop();
        this.drawMusicButton(g, r[2], ModText.translatable("gui.mineturismo.smart_dispatch.music_loop", loopState).getString(),
                loopOn ? MUSIC_PINK : 0xFFDCC9CD, loopOn ? MUSIC_PINK_HOVER : 0xFFE8D8DB, loopOn ? 0xFFFFFFFF : 0xFF3A2A2E, mouseX, mouseY);
        this.drawMusicButton(g, r[3], ModText.translatable("gui.mineturismo.smart_dispatch.music_stop").getString(),
                0xFF3A2A2E, 0xFF54444A, 0xFFFFFFFF, mouseX, mouseY);
    }

    private void setVolumeFromMouse(Layout l, double mouseX) {
        int[] bar = this.musicRects(l)[1];
        double t = (mouseX - bar[0] - 3) / Math.max(1.0, bar[2] - bar[0] - 6);
        this.setMusicVolume(Math.max(0.0, Math.min(1.0, t)));
    }

    private boolean handleMusicHomeClick(Layout l, double mouseX, double mouseY) {
        int[][] r = this.musicRects(l);
        if (inside(mouseX, mouseY, r[0][0], r[0][1], r[0][2], r[0][3])) {
            this.musicBrowsing = true;
            this.scrollRow = 0;
            return true;
        }
        if (inside(mouseX, mouseY, r[1][0] - 2, r[1][1] - 3, r[1][2] + 2, r[1][3] + 3)) {
            this.draggingVolume = true;
            this.setVolumeFromMouse(l, mouseX);
            return true;
        }
        if (inside(mouseX, mouseY, r[2][0], r[2][1], r[2][2], r[2][3])) {
            com.examplebett.client.sound.ChorusMusicPlayer.toggleLoop();
            return true;
        }
        if (inside(mouseX, mouseY, r[3][0], r[3][1], r[3][2], r[3][3])) {
            com.examplebett.client.sound.ChorusMusicPlayer.stop();
            return true;
        }
        return false;
    }

    private static final String[] TIME_KEYS = {"time_morning", "time_noon", "time_evening", "time_night", "time_midnight"};
    private static final float CLOCK_TIME_SCALE = 3.0f;

    private int clockButtonTop(Layout l, int index) {

        int changeTop = l.sy1() - WEATHER_BUTTON_HEIGHT - 5;
        return index == TIME_KEYS.length ? changeTop
                : changeTop - (TIME_KEYS.length - index) * (WEATHER_BUTTON_HEIGHT + WEATHER_BUTTON_GAP);
    }

    private void drawClock(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        long dayTime = this.minecraft.level == null ? 0L : this.levelDayTime();
        int hours = (int) ((Math.floorMod(dayTime, 24000L) / 1000L + 6L) % 24L);

        int skyTop;
        int skyBottom;
        if (hours >= 6 && hours < 17) {
            skyTop = 0xFF6FB8F5;
            skyBottom = 0xFF3D8BD9;
        } else if (hours >= 17 && hours < 19) {
            skyTop = 0xFFF7A35C;
            skyBottom = 0xFFB4466A;
        } else {
            skyTop = 0xFF1E2A4A;
            skyBottom = 0xFF0B1022;
        }
        int bands = Math.max(1, (l.sy1() - top) / (PX * 2));
        for (int i = 0; i < bands; i++) {
            int bandTop = top + i * PX * 2;
            g.fill(l.sx0(), bandTop, l.sx1(), i == bands - 1 ? l.sy1() : bandTop + PX * 2, lerpColor(skyTop, skyBottom, (float) i / bands));
        }
        int cx = (l.sx0() + l.sx1()) / 2;

        String placeLabel = ModText.translatable("gui.mineturismo.smart_dispatch.weather_location").getString();
        int labelWidth = Math.round(this.font.width(placeLabel) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, placeLabel, cx - labelWidth / 2, top + 8, 0xDDFFFFFF);
        String place = this.currentPlaceName().getString();
        int room = l.sx1() - l.sx0() - 8;
        if (this.font.width(place) <= room) {
            g.drawString(this.font, place, cx - this.font.width(place) / 2, top + 17, 0xFFFFFFFF, true);
        } else {
            String fitted = this.fit(place, room);
            this.drawSmallText(g, fitted, cx - Math.round(this.font.width(fitted) * SMALL_TEXT_SCALE) / 2, top + 18, 0xFFFFFFFF);
        }

        String time = this.currentTimeText();
        int timeWidth = Math.round(this.font.width(time) * CLOCK_TIME_SCALE);
        int timeTop = top + 32;
        g.pose().pushPose();
        g.pose().scale(CLOCK_TIME_SCALE, CLOCK_TIME_SCALE, 1.0f);
        g.drawString(this.font, time, Math.round((cx - timeWidth / 2f) / CLOCK_TIME_SCALE), Math.round(timeTop / CLOCK_TIME_SCALE), 0xFFFFFFFF, true);
        g.pose().popPose();

        String day = ModText.translatable("gui.mineturismo.smart_dispatch.clock_day", dayTime / 24000L + 1).getString();
        int dayWidth = Math.round(this.font.width(day) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, day, cx - dayWidth / 2, timeTop + Math.round(this.font.lineHeight * CLOCK_TIME_SCALE) + 2, 0xDDFFFFFF);

        if (this.clockPickerOpen) {
            for (int i = 0; i < TIME_KEYS.length; i++) {
                String label = ModText.translatable("gui.mineturismo.smart_dispatch." + TIME_KEYS[i]).getString();
                this.drawWeatherButton(g, l, this.clockButtonTop(l, i), label, 0xFFFFFFFF, 0xFFFFF1B8, 0xFF202024, mouseX, mouseY);
            }
        }
        String change = ModText.translatable("gui.mineturismo.smart_dispatch.time_change").getString();
        this.drawWeatherButton(g, l, this.clockButtonTop(l, TIME_KEYS.length), change, 0xFF2A2A30, 0xFF44444E, 0xFFFFFFFF, mouseX, mouseY);
    }

    private boolean handleClockClick(Layout l, double mouseX, double mouseY) {
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        int changeTop = this.clockButtonTop(l, TIME_KEYS.length);
        if (inside(mouseX, mouseY, x0, changeTop, x1, changeTop + WEATHER_BUTTON_HEIGHT)) {
            this.clockPickerOpen = !this.clockPickerOpen;
            return true;
        }
        if (this.clockPickerOpen) {
            for (int i = 0; i < TIME_KEYS.length; i++) {
                int top = this.clockButtonTop(l, i);
                if (inside(mouseX, mouseY, x0, top, x1, top + WEATHER_BUTTON_HEIGHT)) {
                    com.examplebett.network.SetAppTimePayload payload = new com.examplebett.network.SetAppTimePayload(i);
                    ModClientNetworking.sendToServer(com.examplebett.network.SetAppTimePayload.ID, payload, com.examplebett.network.SetAppTimePayload::write);
                    this.clockPickerOpen = false;
                    return true;
                }
            }
        }
        return false;
    }

    private static final int MASSIVE_BUTTON_HEIGHT = 32;

    private static final int MASSIVE_BUTTON_TEXT_Y = 12;
    private static final int MASSIVE_BLUE = 0xFF1E4FA8;
    private static final int MASSIVE_BLUE_HOVER = 0xFF2D63C8;
    private static final int MASSIVE_YELLOW = 0xFFF2B705;

    private int[][] massiveAutoRects(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        int first = top + 30;
        int second = first + MASSIVE_BUTTON_HEIGHT + 10;
        return new int[][]{{x0, first, x1, first + MASSIVE_BUTTON_HEIGHT}, {x0, second, x1, second + MASSIVE_BUTTON_HEIGHT}};
    }

    private void drawMassiveAuto(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFFF4F1EA);
        g.fill(l.sx0(), top, l.sx1(), top + 2, MASSIVE_YELLOW);
        String lead = this.fit(ModText.translatable("gui.mineturismo.massive_auto.lead").getString(), l.sx1() - l.sx0() - 8);
        this.drawSmallText(g, lead, cx - Math.round(this.font.width(lead) * SMALL_TEXT_SCALE) / 2, top + 12, 0xFF3A3A40);
        int[][] r = this.massiveAutoRects(l);

        boolean buyHover = this.pending == null && inside(mouseX, mouseY, r[0][0], r[0][1], r[0][2], r[0][3]);
        fillPixelRounded(g, r[0][0], r[0][1] + 2, r[0][2], r[0][3] + 2, PX, 0xFFB08300);
        fillPixelRounded(g, r[0][0], r[0][1], r[0][2], r[0][3], PX, buyHover ? 0xFFFFC72C : MASSIVE_YELLOW);
        String buy = this.fit(ModText.translatable("gui.mineturismo.massive_auto.buy").getString(), r[0][2] - r[0][0] - 6);
        g.drawString(this.font, buy, cx - this.font.width(buy) / 2, r[0][1] + MASSIVE_BUTTON_TEXT_Y, 0xFF12306A, false);

        boolean hover = this.pending == null && inside(mouseX, mouseY, r[1][0], r[1][1], r[1][2], r[1][3]);
        fillPixelRounded(g, r[1][0], r[1][1] + 2, r[1][2], r[1][3] + 2, PX, 0xFF12306A);
        fillPixelRounded(g, r[1][0], r[1][1], r[1][2], r[1][3], PX, hover ? MASSIVE_BLUE_HOVER : MASSIVE_BLUE);
        String appraisal = this.fit(ModText.translatable("gui.mineturismo.massive_auto.appraisal").getString(), r[1][2] - r[1][0] - 6);
        g.drawString(this.font, appraisal, cx - this.font.width(appraisal) / 2, r[1][1] + MASSIVE_BUTTON_TEXT_Y, 0xFFFFFFFF, false);
    }

    private boolean handleMassiveAutoClick(Layout l, double mouseX, double mouseY) {
        int[][] r = this.massiveAutoRects(l);
        if (inside(mouseX, mouseY, r[0][0], r[0][1], r[0][2], r[0][3])) {
            this.minecraft.setScreen(new MassiveAutoUsedCarScreen());
            return true;
        }
        if (inside(mouseX, mouseY, r[1][0], r[1][1], r[1][2], r[1][3])) {

            com.examplebett.network.OpenMassiveAutoAppraisalPayload payload = new com.examplebett.network.OpenMassiveAutoAppraisalPayload();
            ModClientNetworking.sendToServer(com.examplebett.network.OpenMassiveAutoAppraisalPayload.ID, payload, com.examplebett.network.OpenMassiveAutoAppraisalPayload::write);
            return true;
        }
        return false;
    }

    private static final int SLOT_REELS = SmartDispatchAppItem.SLOT_REELS;
    private static final int SLOT_CELL_HEIGHT = 30;
    private static final int SLOT_WINDOW_HEIGHT = SLOT_CELL_HEIGHT * 2 + 6;
    private static final int SLOT_REEL_GAP = 3;
    private static final int SLOT_SPIN_BUTTON_HEIGHT = 18;
    private static final int SLOT_AUTO_BUTTON_HEIGHT = 13;

    private static final double SLOT_MAX_SPEED = 14.0;
    private static final double SLOT_ACCEL_SEC = 0.25;

    private static final double SLOT_MIN_SPIN_SEC = 0.8;
    private static final double SLOT_DECEL_SEC = 0.9;

    private static final double SLOT_STOP_INTERVAL_SEC = 0.45;

    private static final double SLOT_BLUR_SPEED = 5.0;

    private static final long SLOT_TIMEOUT_MS = 4000L;

    private static final long SLOT_AUTO_DELAY_MS = 900L;
    private static final long SLOT_AUTO_DELAY_JACKPOT_MS = 2600L;
    private static final long SLOT_TICK_MIN_INTERVAL_MS = 35L;
    private static final int SLOT_PINK = 0xFFE6197E;
    private static final int SLOT_ORANGE = 0xFFF7931E;
    private static final int SLOT_GOLD = 0xFFFFD54F;
    private static final int[] SLOT_DECOY_EMERALDS = {2, 3, 5, 8, 10, 15, 20, 30, 50};

    private enum SlotPhase { IDLE, SPINNING, STOPPING, RESULT }

    private SlotPhase slotPhase = SlotPhase.IDLE;

    private final double[] reelRest = new double[SLOT_REELS];

    private final double[] reelBase = new double[SLOT_REELS];
    private final double[] reelDecelFrom = new double[SLOT_REELS];
    private final int[] reelTarget = new int[SLOT_REELS];
    private final long[] reelDecelStartMs = new long[SLOT_REELS];
    private final boolean[] reelStopSoundPlayed = new boolean[SLOT_REELS];

    private final List<java.util.Map<Integer, SmartDispatchAppItem.SlotPrize>> reelResults = new ArrayList<>();
    private final java.util.Map<String, ItemStack> slotStackCache = new java.util.HashMap<>();
    private long slotSpinStartMs;
    private long slotResultShownMs;

    private List<SmartDispatchAppItem.SlotPrize> slotResult = null;
    private boolean slotAuto = false;
    private Component slotMessage = null;
    private long slotLastTickMs;
    private long slotLastTickCell = Long.MIN_VALUE;

    private int slotFrozenEmeralds = -1;

    private int slotEmeraldsGained = 0;
    private static final long SLOT_GAIN_POPUP_MS = 1500L;

    {
        for (int i = 0; i < SLOT_REELS; i++) {
            this.reelResults.add(new java.util.HashMap<>());
        }
    }

    public static void onSlotResult(List<SmartDispatchAppItem.SlotPrize> prizes) {
        if (net.minecraft.client.Minecraft.getInstance().screen instanceof SmartDispatchAppScreen screen) {
            screen.receiveSlotResult(prizes);
        }
    }

    private void receiveSlotResult(List<SmartDispatchAppItem.SlotPrize> prizes) {
        if (this.slotPhase != SlotPhase.SPINNING) {
            return;
        }
        long now = System.currentTimeMillis();
        long firstDecel = Math.max(now, this.slotSpinStartMs + (long) (SLOT_MIN_SPIN_SEC * 1000));
        for (int i = 0; i < SLOT_REELS; i++) {
            long decelStart = firstDecel + (long) (i * SLOT_STOP_INTERVAL_SEC * 1000);
            double from = this.spinPosition(i, decelStart);

            int target = (int) Math.ceil(from + SLOT_MAX_SPEED * SLOT_DECEL_SEC / 3.0);
            this.reelDecelStartMs[i] = decelStart;
            this.reelDecelFrom[i] = from;
            this.reelTarget[i] = target;
            if (i < prizes.size()) {
                this.reelResults.get(i).put(target, prizes.get(i));
            }
        }
        this.slotResult = prizes;
        this.slotPhase = SlotPhase.STOPPING;
    }

    private int emeraldCount() {
        return this.minecraft == null || this.minecraft.player == null ? 0
                : ModPlayer.getInventory(this.minecraft.player).countItem(Items.EMERALD);
    }

    private void playUiSound(net.minecraft.sounds.SoundEvent sound, float pitch, float volume) {
        this.minecraft.getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(sound, pitch, volume));
    }

    private void startSlotSpin() {
        if (this.slotPhase == SlotPhase.SPINNING || this.slotPhase == SlotPhase.STOPPING) {
            return;
        }
        if (!this.isFree() && this.emeraldCount() < SmartDispatchAppItem.SLOT_COST) {
            this.slotMessage = ModText.translatable("gui.mineturismo.smart_dispatch.not_enough_emeralds", SmartDispatchAppItem.SLOT_COST);
            this.slotAuto = false;
            return;
        }
        com.examplebett.network.RequestSlotSpinPayload payload = new com.examplebett.network.RequestSlotSpinPayload();
        ModClientNetworking.sendToServer(com.examplebett.network.RequestSlotSpinPayload.ID, payload, com.examplebett.network.RequestSlotSpinPayload::write);
        this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_START, 1.0f, 0.8f);
        this.slotPhase = SlotPhase.SPINNING;
        this.slotSpinStartMs = System.currentTimeMillis();
        this.slotResult = null;
        this.slotMessage = null;

        this.slotFrozenEmeralds = this.emeraldCount() - (this.isFree() ? 0 : SmartDispatchAppItem.SLOT_COST);
        for (int i = 0; i < SLOT_REELS; i++) {
            this.reelBase[i] = this.reelRest[i];
            this.reelStopSoundPlayed[i] = false;
        }
    }

    private void skipSlotSpin() {
        if (this.slotPhase != SlotPhase.STOPPING) {
            return;
        }
        long now = System.currentTimeMillis();
        for (int i = 0; i < SLOT_REELS; i++) {
            this.reelDecelStartMs[i] = Math.min(this.reelDecelStartMs[i], now - (long) (SLOT_DECEL_SEC * 1000));
        }
    }

    private double spinPosition(int reel, long time) {
        double t = Math.max(0, time - this.slotSpinStartMs) / 1000.0;
        double distance = t < SLOT_ACCEL_SEC ? SLOT_MAX_SPEED * t * t / (2 * SLOT_ACCEL_SEC) : SLOT_MAX_SPEED * (t - SLOT_ACCEL_SEC / 2);
        return this.reelBase[reel] + distance;
    }

    private double reelPosition(int reel, long time) {
        if (this.slotPhase == SlotPhase.IDLE || this.slotPhase == SlotPhase.RESULT) {
            return this.reelRest[reel];
        }
        if (this.slotPhase == SlotPhase.SPINNING || time < this.reelDecelStartMs[reel]) {
            return this.spinPosition(reel, time);
        }
        double u = (time - this.reelDecelStartMs[reel]) / (SLOT_DECEL_SEC * 1000);
        if (u >= 1) {
            return this.reelTarget[reel];
        }
        double e = 1 - u;
        return this.reelDecelFrom[reel] + (this.reelTarget[reel] - this.reelDecelFrom[reel]) * (1 - e * e * e);
    }

    private boolean reelStopped(int reel, long time) {
        return this.slotPhase == SlotPhase.STOPPING && time >= this.reelDecelStartMs[reel] + (long) (SLOT_DECEL_SEC * 1000);
    }

    private void updateSlot(long now) {
        if (this.slotPhase == SlotPhase.SPINNING && now - this.slotSpinStartMs > SLOT_TIMEOUT_MS) {
            this.receiveSlotResult(List.of());
        }
        if (this.slotPhase == SlotPhase.SPINNING || this.slotPhase == SlotPhase.STOPPING) {

            boolean stopPlayed = false;
            int tickReel = -1;
            for (int i = 0; i < SLOT_REELS; i++) {
                if (this.reelStopped(i, now)) {
                    if (!this.reelStopSoundPlayed[i]) {
                        this.reelStopSoundPlayed[i] = true;
                        if (!stopPlayed) {
                            this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_STOP, 1.0f + i * 0.08f, 0.9f);
                            stopPlayed = true;
                        }
                    }
                } else {
                    tickReel = i;
                }
            }

            if (tickReel >= 0) {
                long cell = (long) Math.floor(this.reelPosition(tickReel, now));
                if (cell != this.slotLastTickCell && now - this.slotLastTickMs >= SLOT_TICK_MIN_INTERVAL_MS) {
                    this.slotLastTickCell = cell;
                    this.slotLastTickMs = now;
                    this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_TICK, 0.95f + (float) Math.random() * 0.1f, 0.45f);
                }
            } else if (this.slotPhase == SlotPhase.STOPPING) {
                this.finishSlotSpin(now);
            }
        }
        if (this.slotPhase == SlotPhase.RESULT && this.slotAuto && this.pending == null) {
            long delay = this.slotHasJackpot() ? SLOT_AUTO_DELAY_JACKPOT_MS : SLOT_AUTO_DELAY_MS;
            if (now - this.slotResultShownMs >= delay) {
                this.startSlotSpin();
            }
        }
    }

    private void finishSlotSpin(long now) {
        for (int i = 0; i < SLOT_REELS; i++) {
            this.reelRest[i] = this.reelTarget[i];
        }
        this.slotResultShownMs = now;
        this.slotFrozenEmeralds = -1;
        this.slotEmeraldsGained = 0;
        if (this.slotResult != null) {
            for (SmartDispatchAppItem.SlotPrize prize : this.slotResult) {
                if (prize.kind() == SmartDispatchAppItem.PRIZE_EMERALD) {
                    this.slotEmeraldsGained += prize.amount();
                }
            }
        }
        if (this.slotResult == null || this.slotResult.isEmpty()) {

            this.slotPhase = SlotPhase.IDLE;
            this.slotAuto = false;
            this.slotMessage = ModText.translatable("gui.mineturismo.smart_dispatch.slot_failed");
            return;
        }
        this.slotPhase = SlotPhase.RESULT;
        if (this.slotHasJackpot()) {
            this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_JACKPOT, 1.0f, 1.0f);
        } else if (this.slotResult.stream().anyMatch(SmartDispatchAppItem.SlotPrize::isWin)) {
            this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_WIN, 1.0f, 0.9f);
        } else {
            this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_LOSE, 1.0f, 0.7f);
        }
    }

    private boolean slotHasJackpot() {
        return this.slotResult != null && this.slotResult.stream().anyMatch(SmartDispatchAppItem.SlotPrize::isJackpot);
    }

    private SmartDispatchAppItem.SlotPrize slotCell(int reel, long cell) {
        SmartDispatchAppItem.SlotPrize result = cell == (int) cell ? this.reelResults.get(reel).get((int) cell) : null;
        if (result != null) {
            return result;
        }
        long h = cell * 0x9E3779B97F4A7C15L + (reel + 1) * 0xC2B2AE3D27D4EB4FL;
        h ^= h >>> 31;
        h *= 0xBF58476D1CE4E5B9L;
        h ^= h >>> 29;
        int r = (int) Math.floorMod(h, 100L);
        int r2 = (int) Math.floorMod(h >>> 17, 1000L);

        if (r < 18) {
            return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_WARDEN_ENERGY, 1, "");
        }
        if (r < 35) {
            return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_CHOCO_BAR, 1, "");
        }
        if (r < 55) {
            return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_EMERALD, 1, "");
        }
        if (r < 85) {
            return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_EMERALD, SLOT_DECOY_EMERALDS[r2 % SLOT_DECOY_EMERALDS.length], "");
        }
        String modelId = SmartDispatchAppItem.SLOT_VEHICLES[r2 % SmartDispatchAppItem.SLOT_VEHICLES.length];
        return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_VEHICLE, 1, modelId);
    }

    private ItemStack slotStack(SmartDispatchAppItem.SlotPrize prize) {
        return this.slotStackCache.computeIfAbsent(prize.kind() + ":" + prize.amount() + ":" + prize.modelId(), key -> prize.toStack());
    }

    private static int slotPrizeColor(SmartDispatchAppItem.SlotPrize prize) {
        if (prize.kind() == SmartDispatchAppItem.PRIZE_VEHICLE) {
            return 0xFFFFA000;
        }
        if (prize.kind() == SmartDispatchAppItem.PRIZE_EMERALD) {
            return prize.amount() >= 30 ? 0xFFE53935 : prize.amount() >= 10 ? 0xFFAB47BC : prize.amount() >= 2 ? 0xFF29B6F6 : 0xFF66BB6A;
        }
        if (prize.kind() == SmartDispatchAppItem.PRIZE_CHOCO_BAR) {
            return 0xFFA1887F;
        }
        return 0xFF9CCC65;
    }

    private String slotPrizeLabel(SmartDispatchAppItem.SlotPrize prize) {
        if (prize.kind() == SmartDispatchAppItem.PRIZE_VEHICLE) {
            ItemStack stack = this.slotStack(prize);
            return stack.isEmpty() ? prize.modelId() : stack.getHoverName().getString();
        }
        if (prize.kind() == SmartDispatchAppItem.PRIZE_EMERALD) {
            return "x" + prize.amount();
        }
        if (prize.kind() == SmartDispatchAppItem.PRIZE_CHOCO_BAR) {
            return ModText.translatable("gui.mineturismo.smart_dispatch.slot_choco_short").getString();
        }
        return ModText.translatable("gui.mineturismo.smart_dispatch.slot_energy_short").getString();
    }

    private void drawFittedCentered(GuiGraphics g, String text, int cx, int y, int maxWidth, int color) {
        float scale = Math.min(SMALL_TEXT_SCALE, (float) maxWidth / Math.max(1, this.font.width(text)));
        int w = Math.round(this.font.width(text) * scale);
        this.drawScaledText(g, net.minecraft.locale.Language.getInstance().getVisualOrder(net.minecraft.network.chat.FormattedText.of(text)),
                cx - w / 2, y, color, scale);
    }

    private int[][] slotRects(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT + COST_BAR_HEIGHT + 8;
        int autoTop = l.sy1() - 5 - SLOT_AUTO_BUTTON_HEIGHT;
        int spinTop = autoTop - 4 - SLOT_SPIN_BUTTON_HEIGHT;
        return new int[][]{
                {l.sx0() + 6, top, l.sx1() - 6, top + SLOT_WINDOW_HEIGHT},
                {l.sx0() + 8, spinTop, l.sx1() - 8, spinTop + SLOT_SPIN_BUTTON_HEIGHT},
                {l.sx0() + 8, autoTop, l.sx1() - 8, autoTop + SLOT_AUTO_BUTTON_HEIGHT}
        };
    }

    private void drawSlot(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        long now = System.currentTimeMillis();
        this.updateSlot(now);
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;

        boolean flash = this.slotPhase == SlotPhase.RESULT && this.slotResult.stream().anyMatch(SmartDispatchAppItem.SlotPrize::isWin)
                && now - this.slotResultShownMs < 1500 && (now / 150) % 2 == 0;
        int bands = Math.max(1, (l.sy1() - top) / (PX * 2));
        for (int i = 0; i < bands; i++) {
            int bandTop = top + i * PX * 2;
            int color = lerpColor(SLOT_PINK, SLOT_ORANGE, (float) i / bands);
            g.fill(l.sx0(), bandTop, l.sx1(), i == bands - 1 ? l.sy1() : bandTop + PX * 2, flash ? lerpColor(color, 0xFFFFFFFF, 0.25f) : color);
        }

        g.fill(l.sx0(), top, l.sx1(), top + COST_BAR_HEIGHT, COLOR_COST_BAR);
        Component cost = this.isFree() ? ModText.translatable("gui.mineturismo.smart_dispatch.free")
                : ModText.translatable("gui.mineturismo.smart_dispatch.slot_cost", SmartDispatchAppItem.SLOT_COST);
        this.drawSmallText(g, cost.getString(), l.sx0() + 3, top + 2, 0xFFE8E8E8);
        int shownEmeralds = this.slotFrozenEmeralds >= 0 ? this.slotFrozenEmeralds : this.emeraldCount();
        String owned = ModText.translatable("gui.mineturismo.smart_dispatch.slot_owned", shownEmeralds).getString();
        int ownedX = l.sx1() - 3 - Math.round(this.font.width(owned) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, owned, ownedX, top + 2, 0xFF7CF29A);

        if (this.slotPhase == SlotPhase.RESULT && this.slotEmeraldsGained > 0 && now - this.slotResultShownMs < SLOT_GAIN_POPUP_MS) {
            String gained = "+" + this.slotEmeraldsGained;
            this.drawSmallText(g, gained, ownedX - 3 - Math.round(this.font.width(gained) * SMALL_TEXT_SCALE), top + 2, SLOT_GOLD);
        }

        int[][] r = this.slotRects(l);
        int[] window = r[0];
        fillPixelRounded(g, window[0] - 3, window[1] - 3, window[2] + 3, window[3] + 3, 1, 0xFF2A1030);
        int reelWidth = (window[2] - window[0] - SLOT_REEL_GAP * (SLOT_REELS - 1)) / SLOT_REELS;
        for (int i = 0; i < SLOT_REELS; i++) {
            int rx0 = window[0] + i * (reelWidth + SLOT_REEL_GAP);
            this.drawReel(g, i, rx0, window[1], rx0 + reelWidth, window[3], now);
        }

        int midY = (window[1] + window[3]) / 2;
        for (int i = 0; i < 3; i++) {
            g.fill(window[0] - 3 + i, midY - 3 + i, window[0] - 2 + i, midY + 4 - i, 0xFFFFFFFF);
            g.fill(window[2] + 2 - i, midY - 3 + i, window[2] + 3 - i, midY + 4 - i, 0xFFFFFFFF);
        }

        int textTop = window[3] + 8;
        int room = l.sx1() - l.sx0() - 8;
        if (this.slotPhase == SlotPhase.RESULT) {
            boolean jackpot = this.slotHasJackpot();
            boolean win = jackpot || this.slotResult.stream().anyMatch(SmartDispatchAppItem.SlotPrize::isWin);
            String headline = ModText.translatable(jackpot ?"gui.mineturismo.smart_dispatch.slot_jackpot"
                    : win ? "gui.mineturismo.smart_dispatch.slot_win" : "gui.mineturismo.smart_dispatch.slot_lose").getString();
            g.drawString(this.font, headline, cx - this.font.width(headline) / 2, textTop, win ? SLOT_GOLD : 0xFFFFFFFF, true);
            int emeralds = 0;
            for (SmartDispatchAppItem.SlotPrize prize : this.slotResult) {
                if (prize.kind() == SmartDispatchAppItem.PRIZE_EMERALD) {
                    emeralds += prize.amount();
                }
            }
            if (emeralds > 0) {
                this.drawFittedCentered(g, ModText.translatable("gui.mineturismo.smart_dispatch.slot_emeralds_won", emeralds).getString(),
                        cx, textTop + 11, room, 0xFFFFFFFF);
            }
        } else if (this.slotMessage != null && this.slotPhase == SlotPhase.IDLE) {
            List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(this.slotMessage, Math.round(room / SMALL_TEXT_SCALE));
            for (int i = 0; i < Math.min(2, lines.size()); i++) {
                int w = Math.round(this.font.width(lines.get(i)) * SMALL_TEXT_SCALE);
                this.drawSmallText(g, lines.get(i), cx - w / 2, textTop + i * 8, 0xFFFFE0E0);
            }
        } else if (this.slotPhase == SlotPhase.IDLE) {
            this.drawFittedCentered(g, ModText.translatable("gui.mineturismo.smart_dispatch.slot_hint").getString(), cx, textTop + 2, room, 0xFFFFFFFF);
        }

        int[] spin = r[1];
        boolean waiting = this.slotPhase == SlotPhase.SPINNING;
        boolean skipping = this.slotPhase == SlotPhase.STOPPING;
        boolean affordable = this.isFree() || this.emeraldCount() >= SmartDispatchAppItem.SLOT_COST;
        boolean spinEnabled = skipping || (!waiting && affordable);
        boolean spinHover = spinEnabled && inside(mouseX, mouseY, spin[0], spin[1], spin[2], spin[3]);
        fillPixelRounded(g, spin[0], spin[1] + 2, spin[2], spin[3] + 2, PX, spinEnabled ? 0xFFB07800 : 0xFF6E6E74);
        fillPixelRounded(g, spin[0], spin[1], spin[2], spin[3], PX, !spinEnabled ? 0xFF9A9AA2 : spinHover ? 0xFFFFE066 : 0xFFFFC928);
        String spinLabel = ModText.translatable(skipping ? "gui.mineturismo.smart_dispatch.slot_skip" : "gui.mineturismo.smart_dispatch.slot_spin").getString();
        g.drawString(this.font, spinLabel, cx - this.font.width(spinLabel) / 2, spin[1] + 5, spinEnabled ? 0xFF3A2000 : 0xFFDADADF, false);

        int[] auto = r[2];
        boolean autoHover = inside(mouseX, mouseY, auto[0], auto[1], auto[2], auto[3]);
        fillPixelRounded(g, auto[0], auto[1], auto[2], auto[3], 1,
                this.slotAuto ? (autoHover ? 0xFF4FC562 : COLOR_BUTTON_YES) : (autoHover ? 0xFF54444A : 0xFF3A2A2E));
        String autoLabel = ModText.translatable("gui.mineturismo.smart_dispatch.slot_auto",
                ModText.translatable(this.slotAuto ? "options.on" : "options.off")).getString();
        int autoWidth = Math.round(this.font.width(autoLabel) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, autoLabel, cx - autoWidth / 2, auto[1] + 4, 0xFFFFFFFF);
    }

    private void drawReel(GuiGraphics g, int reel, int x0, int y0, int x1, int y1, long now) {
        g.fill(x0, y0, x1, y1, 0xFF1A0A1E);
        double pos = this.reelPosition(reel, now);
        double speed = Math.abs(pos - this.reelPosition(reel, now - 16)) / 0.016;
        boolean blur = speed > SLOT_BLUR_SPEED;

        g.enableScissor(x0, y0, x1, y1);
        boolean scissor = true;
        int centerY = (y0 + y1) / 2;
        long base = (long) Math.floor(pos);
        for (long cell = base - 2; cell <= base + 2; cell++) {
            int cellTop = (int) Math.round(centerY - SLOT_CELL_HEIGHT / 2.0 + (pos - cell) * SLOT_CELL_HEIGHT);
            this.drawSlotCell(g, this.slotCell(reel, cell), x0 + 1, cellTop + 1, x1 - 1, cellTop + SLOT_CELL_HEIGHT - 1, y0, y1, blur, scissor);
        }

        if (this.slotPhase == SlotPhase.RESULT && reel < this.slotResult.size() && this.slotResult.get(reel).isWin() && (now / 250) % 2 == 0) {
            int cellTop = centerY - SLOT_CELL_HEIGHT / 2;
            int cellBottom = cellTop + SLOT_CELL_HEIGHT;
            g.fill(x0, cellTop, x1, cellTop + 2, SLOT_GOLD);
            g.fill(x0, cellBottom - 2, x1, cellBottom, SLOT_GOLD);
            g.fill(x0, cellTop, x0 + 2, cellBottom, SLOT_GOLD);
            g.fill(x1 - 2, cellTop, x1, cellBottom, SLOT_GOLD);
        }

        g.fill(x0, y0, x1, y0 + 4, 0x66000000);
        g.fill(x0, y1 - 4, x1, y1, 0x66000000);
        g.disableScissor();
    }

    private static void fillClipped(GuiGraphics g, int x0, int y0, int x1, int y1, int clipTop, int clipBottom, int color) {
        int top = Math.max(y0, clipTop);
        int bottom = Math.min(y1, clipBottom);
        if (bottom > top) {
            g.fill(x0, top, x1, bottom, color);
        }
    }

    private void drawSlotCell(GuiGraphics g, SmartDispatchAppItem.SlotPrize prize, int x0, int y0, int x1, int y1,
                              int clipTop, int clipBottom, boolean blur, boolean scissor) {
        if (y1 <= clipTop || y0 >= clipBottom) {
            return;
        }
        int color = slotPrizeColor(prize);
        if (blur) {

            fillClipped(g, x0, y0, x1, y1, clipTop, clipBottom, lerpColor(color, 0xFFFFFFFF, 0.35f));
            int mid = (x0 + x1) / 2;
            fillClipped(g, mid - 6, y0 + 4, mid + 6, y1 - 4, clipTop, clipBottom, lerpColor(color, 0xFFFFFFFF, 0.6f));
            return;
        }
        fillClipped(g, x0, y0, x1, y1, clipTop, clipBottom, color);
        int stripTop = y1 - 9;
        fillClipped(g, x0 + 1, stripTop, x1 - 1, y1 - 1, clipTop, clipBottom, 0xF2FFFFFF);

        boolean visible = scissor || (y0 >= clipTop && y1 <= clipBottom);
        if (!visible) {
            return;
        }
        int cx = (x0 + x1) / 2;
        g.renderItem(this.slotStack(prize), cx - 8, y0 + 2);
        this.drawFittedCentered(g, this.slotPrizeLabel(prize), cx, stripTop + 2, x1 - x0 - 4, 0xFF202024);
    }

    private boolean handleSlotClick(Layout l, double mouseX, double mouseY) {
        int[][] r = this.slotRects(l);
        if (inside(mouseX, mouseY, r[1][0], r[1][1], r[1][2], r[1][3])) {
            this.pressSlotSpin();
            return true;
        }
        if (inside(mouseX, mouseY, r[2][0], r[2][1], r[2][2], r[2][3])) {
            this.slotAuto = !this.slotAuto;
            if (this.slotAuto && (this.slotPhase == SlotPhase.IDLE || this.slotPhase == SlotPhase.RESULT)) {
                this.startSlotSpin();
            }
            return true;
        }

        if (inside(mouseX, mouseY, r[0][0], r[0][1], r[0][2], r[0][3])) {
            this.pressSlotSpin();
            return true;
        }
        return false;
    }

    private void pressSlotSpin() {
        if (this.slotPhase == SlotPhase.STOPPING) {
            this.skipSlotSpin();
        } else {
            this.startSlotSpin();
        }
    }

    private String fit(String text, int width) {
        int room = Math.round(width / SMALL_TEXT_SCALE);
        if (this.font.width(text) <= room) {
            return text;
        }
        return this.font.plainSubstrByWidth(text, room - this.font.width("...")) + "...";
    }

    private void drawDialog(GuiGraphics g, Layout l, App app, int mouseX, int mouseY) {
        this.aboveItems(g);
        g.fill(l.sx0(), l.sy0(), l.sx1(), l.sy1(), COLOR_DIALOG_SHADE);
        int dx0 = l.sx0() + 6;
        int dx1 = l.sx1() - 6;
        int dy0 = l.dialogTop();
        fillPixelRounded(g, dx0, dy0, dx1, dy0 + DIALOG_HEIGHT, PX, COLOR_DIALOG);

        g.renderItem(this.pending.stack(), dx0 + 6, dy0 + 6);
        this.drawSmallText(g, this.fit(this.pending.name().getString(), dx1 - dx0 - 30), dx0 + 26, dy0 + 11, 0xFF202024);

        boolean enough = this.hasEnoughEmeralds();
        Component question = enough
                ? ModText.translatable(app.vehicles ? "gui.mineturismo.smart_dispatch.confirm_ride" : "gui.mineturismo.smart_dispatch.confirm_buy",
                        costOf(app))
                : ModText.translatable("gui.mineturismo.smart_dispatch.not_enough_emeralds", costOf(app));

        List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(question, Math.round((dx1 - dx0 - 10) / SMALL_TEXT_SCALE));
        for (int i = 0; i < Math.min(2, lines.size()); i++) {
            this.drawSmallText(g, lines.get(i), dx0 + 5, dy0 + 28 + i * 8, enough ? 0xFF202024 : 0xFFC62828);
        }
        int by0 = l.buttonTop();
        int bw = l.buttonWidth();
        boolean yesHover = enough && inside(mouseX, mouseY, l.yesX0(), by0, l.yesX0() + bw, by0 + DIALOG_BUTTON_HEIGHT);
        boolean noHover = inside(mouseX, mouseY, l.noX0(), by0, l.noX0() + bw, by0 + DIALOG_BUTTON_HEIGHT);
        fillPixelRounded(g, l.yesX0(), by0, l.yesX0() + bw, by0 + DIALOG_BUTTON_HEIGHT, 1,
                !enough ? COLOR_BUTTON_NO : yesHover ? COLOR_BUTTON_YES_HOVER : COLOR_BUTTON_YES);
        fillPixelRounded(g, l.noX0(), by0, l.noX0() + bw, by0 + DIALOG_BUTTON_HEIGHT, 1, noHover ? COLOR_BUTTON_NO_HOVER : COLOR_BUTTON_NO);
        String yes = ModText.translatable("gui.yes").getString();
        String no = ModText.translatable("gui.no").getString();
        g.drawString(this.font, yes, l.yesX0() + (bw - this.font.width(yes)) / 2, by0 + 3, 0xFFFFFFFF, false);
        g.drawString(this.font, no, l.noX0() + (bw - this.font.width(no)) / 2, by0 + 3, 0xFF202024, false);
    }

    private void aboveItems(GuiGraphics g) {
    }

    private void drawSmallText(GuiGraphics g, String text, int x, int y, int color) {
        this.drawSmallText(g, net.minecraft.network.chat.FormattedText.of(text), x, y, color);
    }

    private void drawSmallText(GuiGraphics g, net.minecraft.network.chat.FormattedText text, int x, int y, int color) {
        this.drawSmallText(g, net.minecraft.locale.Language.getInstance().getVisualOrder(text), x, y, color);
    }

    private void drawSmallText(GuiGraphics g, net.minecraft.util.FormattedCharSequence text, int x, int y, int color) {
        this.drawScaledText(g, text, x, y, color, SMALL_TEXT_SCALE);
    }

    private void drawScaledText(GuiGraphics g, net.minecraft.util.FormattedCharSequence text, int x, int y, int color, float s) {
        g.pose().pushPose();
        g.pose().scale(s, s, 1.0f);
        g.drawString(this.font, text, Math.round(x / s), Math.round(y / s), color, false);
        g.pose().popPose();
    }

    private void confirm(App app, Entry entry) {
        if (app.vehicles) {
            RequestAppCarSpawnPayload payload = new RequestAppCarSpawnPayload(entry.id());
            ModClientNetworking.sendToServer(RequestAppCarSpawnPayload.ID, payload, RequestAppCarSpawnPayload::write);
            this.onClose();
        } else {
            RequestAppItemPurchasePayload payload = new RequestAppItemPurchasePayload(entry.id());
            ModClientNetworking.sendToServer(RequestAppItemPurchasePayload.ID, payload, RequestAppItemPurchasePayload::write);
            this.purchasedBannerUntil = System.currentTimeMillis() + PURCHASED_BANNER_MS;
        }
    }

    private void launchGtt() {
        com.examplebett.network.SetGttPayload payload = new com.examplebett.network.SetGttPayload(true);
        ModClientNetworking.sendToServer(com.examplebett.network.SetGttPayload.ID, payload, com.examplebett.network.SetGttPayload::write);
        com.examplebett.client.GttClient.requestStart();
        this.onClose();
    }

    private boolean handleClick(double mouseX, double mouseY, int button) {
        if (this.shutdownUntil != 0L) {
            return true;
        }
        if (button != 0) {
            return false;
        }
        Layout l = this.layout();
        if (this.pending != null) {
            int by0 = l.buttonTop();
            int bw = l.buttonWidth();
            if (inside(mouseX, mouseY, l.yesX0(), by0, l.yesX0() + bw, by0 + DIALOG_BUTTON_HEIGHT)) {
                if (this.hasEnoughEmeralds()) {
                    Entry entry = this.pending;
                    this.pending = null;
                    this.confirm(this.openApp, entry);
                }
            } else {

                this.pending = null;
            }
            return true;
        }
        if (inside(mouseX, mouseY, l.homeX0() - 2, l.homeY0() - 2, l.homeX1() + 2, l.homeY1() + 2)) {
            if (this.openApp != null) {
                this.openApp = null;
                this.musicBrowsing = false;
            } else {

                this.shutdownUntil = System.currentTimeMillis() + SHUTDOWN_MS;
            }
            return true;
        }
        if (this.openApp == null) {
            App[] apps = App.values();
            for (int i = 0; i < apps.length; i++) {
                int[] r = appIconRect(l, i);
                if (inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
                    if (apps[i] == App.GTT) {
                        this.launchGtt();
                        return true;
                    }
                    this.openApp = apps[i];
                    this.scrollRow = 0;
                    this.weatherPickerOpen = false;
                    this.clockPickerOpen = false;
                    this.musicBrowsing = false;
                    this.settingsPage = SETTINGS_PAGE_MAIN;
                    this.purchasedBannerUntil = 0L;
                    return true;
                }
            }
            return false;
        }
        int headerTop = l.sy0() + STATUS_BAR_HEIGHT;
        if (inside(mouseX, mouseY, l.sx0(), headerTop, l.sx0() + 16, headerTop + HEADER_HEIGHT)) {

            if (this.openApp == App.MUSIC && this.musicBrowsing) {
                this.musicBrowsing = false;
            } else if (this.openApp == App.SETTINGS && this.settingsPage != SETTINGS_PAGE_MAIN) {
                this.settingsPage = SETTINGS_PAGE_MAIN;
                this.rebindingKey = null;
            } else {
                this.openApp = null;
            }
            return true;
        }
        if (this.openApp == App.MUSIC && !this.musicBrowsing) {
            return this.handleMusicHomeClick(l, mouseX, mouseY);
        }
        if (this.openApp.weather) {
            return this.handleWeatherClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.EMERGENCY) {
            return this.handleEmergencyClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.DETONATOR) {
            return this.handleDetonatorClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.CLOCK) {
            return this.handleClockClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.MASSIVE_AUTO) {
            return this.handleMassiveAutoClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.SLOT) {
            return this.handleSlotClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.SETTINGS) {
            return this.handleSettingsClick(l, mouseX, mouseY);
        }
        int listTop = l.listTop();

        if (this.maxScrollRow(l) > 0 && inside(mouseX, mouseY, l.sx1() - 6, listTop, l.sx1(), l.sy1())) {
            this.draggingScrollbar = true;
            this.scrollToMouse(l, mouseY);
            return true;
        }
        if (inside(mouseX, mouseY, l.sx0(), listTop, l.sx1() - 6, listTop + l.visibleRows() * ROW_HEIGHT)) {
            int index = this.scrollRow + (int) ((mouseY - listTop) / ROW_HEIGHT);
            List<Entry> entries = this.entries();
            if (index < entries.size() && this.openApp == App.MUSIC) {

                com.examplebett.client.sound.ChorusMusicTracks.Track track = findTrack(entries.get(index).id());
                if (track != null) {
                    com.examplebett.client.sound.ChorusMusicPlayer.play(track);
                }
                return true;
            }
            if (index < entries.size()) {
                if (this.isFree()) {
                    this.confirm(this.openApp, entries.get(index));
                } else {
                    this.pending = entries.get(index);
                }
                return true;
            }
        }
        return false;
    }

    private void releaseVolume() {
        if (this.draggingVolume) {
            this.draggingVolume = false;
            this.minecraft.options.save();
        }
    }

    private int thumbHeight(Layout l) {
        return Math.max(8, (l.sy1() - l.listTop()) * l.visibleRows() / Math.max(1, this.entries().size()));
    }

    private void scrollToMouse(Layout l, double mouseY) {
        int max = this.maxScrollRow(l);
        int room = (l.sy1() - l.listTop()) - this.thumbHeight(l);
        if (max <= 0 || room <= 0) {
            return;
        }
        double t = (mouseY - l.listTop() - this.thumbHeight(l) / 2.0) / room;
        this.scrollRow = (int) Math.round(Math.max(0.0, Math.min(1.0, t)) * max);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.draggingVolume && button == 0) {
            this.setVolumeFromMouse(this.layout(), mouseX);
            return true;
        }
        if (this.draggingScrollbar && button == 0) {
            this.scrollToMouse(this.layout(), mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.draggingScrollbar = false;
            this.releaseVolume();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.handleClick(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleSlotKey(int key) {
        if (this.openApp != App.SLOT || this.pending != null
                || (key != org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE && key != org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER && key != org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER)) {
            return false;
        }
        this.pressSlotSpin();
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.handleRebindKey(keyCode, () -> com.mojang.blaze3d.platform.InputConstants.getKey(keyCode, scanCode))
                || this.handleSlotKey(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

    private boolean mineturismo$mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.openApp == null || this.pending != null || scrollY == 0) {
            return false;
        }
        Layout l = this.layout();
        this.scrollRow = Math.max(0, Math.min(this.maxScrollRow(l), this.scrollRow + (scrollY > 0 ? -1 : 1)));
        return true;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        Layout l = this.layout();
        this.drawPhone(guiGraphics, l);
        if (this.shutdownUntil != 0L) {
            this.drawShutdown(guiGraphics, l);
            return;
        }
        if (this.openApp != null) {
            this.drawApp(guiGraphics, l, this.openApp, mouseX, mouseY);
        } else {
            this.drawHome(guiGraphics, l, mouseX, mouseY);
        }
        this.drawStatusBar(guiGraphics, l);
        this.skin.drawScreenOverlay(guiGraphics, l.sx0(), l.sy0(), l.sx1());
        if (this.pending != null && this.openApp != null) {
            this.drawDialog(guiGraphics, l, this.openApp, mouseX, mouseY);
        }
    }

    private void drawShutdown(GuiGraphics g, Layout l) {
        g.fill(l.sx0(), l.sy0(), l.sx1(), l.sy1(), 0xFF000000);

        float dot = PX * 2 * 2f / 3f;
        int logoW = Math.round(CHORUS_LOGO[0].length() * dot);
        int logoH = Math.round(CHORUS_LOGO.length * dot);
        int cx = (l.sx0() + l.sx1()) / 2;
        int logoX = (cx - logoW / 2) / PX * PX;
        int logoY = ((l.sy0() + l.sy1()) / 2 - logoH - 6) / PX * PX;
        for (int row = 0; row < CHORUS_LOGO.length; row++) {
            String line = CHORUS_LOGO[row];
            int py0 = logoY + Math.round(row * dot);
            int py1 = logoY + Math.round((row + 1) * dot);
            for (int col = 0; col < line.length(); col++) {
                if (line.charAt(col) == '#') {
                    int px0 = logoX + Math.round(col * dot);
                    int px1 = logoX + Math.round((col + 1) * dot);
                    g.fill(px0, py0, px1, py1, 0xFFFFFFFF);
                }
            }
        }
        String brand = "Chorus";
        int brandY = logoY + logoH + 6;
        g.drawString(this.font, brand, cx - this.font.width(brand) / 2, brandY, 0xFFFFFFFF, false);
        String status = ModText.translatable("gui.mineturismo.smart_dispatch.shutting_down").getString();
        this.drawSmallText(g, status, cx - Math.round(this.font.width(status) * SMALL_TEXT_SCALE) / 2,
                brandY + this.font.lineHeight + 6, 0xFFB4B4B4);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.shutdownUntil != 0L && System.currentTimeMillis() >= this.shutdownUntil) {
            this.shutdownUntil = 0L;
            this.onClose();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
//? if <1.16.2 && >=1.16 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModClientNetworking;
import com.examplebett.compat.ModText;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModPlayer;
import com.examplebett.compat.ModRegistries;
import com.examplebett.item.SmartDispatchAppItem;
import com.examplebett.network.RequestAppCarSpawnPayload;
import com.examplebett.network.RequestAppItemPurchasePayload;
import com.examplebett.util.AiCarPool;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SmartDispatchAppScreen extends Screen {

    private static final int PX = 2;
    private static final int PHONE_MAX_HEIGHT = 240;
    private static final float PHONE_ASPECT = 0.56f;
    private static final int STATUS_BAR_HEIGHT = 11;
    private static final int APP_ICON_SIZE = 24;
    private static final int APP_ICON_TEXTURE_SIZE = 64;
    private static final int APP_COLUMNS = 4;
    private static final int HEADER_HEIGHT = 16;
    private static final int COST_BAR_HEIGHT = 10;
    private static final int ROW_HEIGHT = 20;
    private static final float SMALL_TEXT_SCALE = 0.75f;
    private static final int DIALOG_HEIGHT = 78;
    private static final int DIALOG_BUTTON_HEIGHT = 14;
    private static final long PURCHASED_BANNER_MS = 1500L;

    private static final long SHUTDOWN_MS = 500L;

    private static final int SETTINGS_PAGE_MAIN = 0;
    private static final int SETTINGS_PAGE_WALLPAPER = 1;
    private static final int SETTINGS_PAGE_MODEL = 2;

    private static final int SETTINGS_PAGE_CONTROLS = 3;
    private static final int CONTROLS_ROW_HEIGHT = 11;
    private static final int SETTINGS_ROW_HEIGHT = 20;

    private static final int WALLPAPER_TEXTURE_WIDTH = 64;
    private static final int WALLPAPER_TEXTURE_HEIGHT = 120;

    private static final String[] PHONE_MODEL_NAMES = {"PopPhone 3", "PopPhone 6", "PopPhone 18", null};

    private static final String[] CHORUS_LOGO = {
            "....#.......",
            "#..##.......",
            "########....",
            ".#########..",
            ".##########.",
            "..#########.",
            ".###########",
            "############",
            "############",
            ".###########",
            ".##########.",
            "..########..",
            "...######...",
    };

    private static final int COLOR_WALLPAPER_TOP = 0xFF5696E6;
    private static final int COLOR_WALLPAPER_BOTTOM = 0xFF2C5CB4;
    private static final int COLOR_STATUS_BAR = 0x66000000;
    private static final int COLOR_ROW = 0xFFFFFFFF;
    private static final int COLOR_ROW_ALT = 0xFFF1F1F4;
    private static final int COLOR_ROW_HOVER = 0xFFFFF1B8;
    private static final int COLOR_COST_BAR = 0xFF2A2A30;
    private static final int COLOR_SCROLL_TRACK = 0xFFDADAE0;
    private static final int COLOR_SCROLL_THUMB = 0xFF8C8C96;
    private static final int COLOR_DIALOG_SHADE = 0x99000000;
    private static final int COLOR_DIALOG = 0xFFFFFFFF;
    private static final int COLOR_BUTTON_YES = 0xFF3DAA4F;
    private static final int COLOR_BUTTON_YES_HOVER = 0xFF4FC562;
    private static final int COLOR_BUTTON_NO = 0xFFB4B6BE;
    private static final int COLOR_BUTTON_NO_HOVER = 0xFFC8CAD2;
    private static final int COLOR_BANNER = 0xEE202024;

    private enum App {
        MINES("Mines Car Share", "Mines\nCar Share", null, "mines", 0xFFF2C318, 0xFF000000, true, false),
        AVALON("Avalon", "Avalon", null, "avalon", 0xFF1B2838, 0xFF6FD3F3, false, false),
        WEATHER(null, null, "gui.mineturismo.smart_dispatch.weather_app", "weather", 0xFF3D8BD9, 0xFFFFFFFF, false, true),
        CLOCK(null, null, "gui.mineturismo.smart_dispatch.clock_app", "clock", 0xFF1C1C1E, 0xFFFFFFFF, false, false),

        GTT("Grand Target Track", "GTT", null, "gtt", 0xFF000000, 0xFFFFFFFF, false, false),
        EMERGENCY(null, null, "gui.mineturismo.smart_dispatch.emergency_app", "emergency", 0xFFD32F2F, 0xFFFFFFFF, false, false),

        MUSIC(null, null, "gui.mineturismo.smart_dispatch.music_app", "music", 0xFFF5354F, 0xFFFFFFFF, false, false),

        MASSIVE_AUTO("Massive Auto", "Massive\nAuto", null, "massive_auto", 0xFF1E4FA8, 0xFFFFFFFF, false, false),

        SLOT(null, null, "gui.mineturismo.smart_dispatch.slot_app", "slot", 0xFFE6197E, 0xFFFFFFFF, false, false),

        DETONATOR(null, null, "gui.mineturismo.smart_dispatch.detonator_app", "detonator", 0xFF2B2D33, 0xFFFFFFFF, false, false),

        SETTINGS(null, null, "gui.mineturismo.smart_dispatch.settings_app", "settings", 0xFFE9E9EE, 0xFF000000, false, false);

        private final String title;
        private final String shortName;
        private final String titleKey;
        final ResourceLocation icon;
        final int headerColor;
        final int titleColor;
        final boolean vehicles;
        final boolean weather;

        App(String title, String shortName, String titleKey, String iconId, int headerColor, int titleColor,
            boolean vehicles, boolean weather) {
            this.title = title;
            this.shortName = shortName;
            this.titleKey = titleKey;
            this.icon = AiObjectMod.id("textures/gui/smart_dispatch/" + iconId + ".png");
            this.headerColor = headerColor;
            this.titleColor = titleColor;
            this.vehicles = vehicles;
            this.weather = weather;
        }

        String title() {
            return this.titleKey != null ? ModText.translatable(this.titleKey).getString() : this.title;
        }

        String shortName() {
            if (this.titleKey == null) {
                return this.shortName;
            }
            String labelKey = this.titleKey + "_label";
            return net.minecraft.locale.Language.getInstance().has(labelKey) ? ModText.translatable(labelKey).getString() : this.title();
        }
    }

    private record Entry(String id, ItemStack stack, Component name) {
    }

    private record Layout(int x0, int y0, int x1, int y1, int sx0, int sy0, int sx1, int sy1,
                          int homeX0, int homeY0, int homeX1, int homeY1) {
        int listTop() {
            return sy0 + STATUS_BAR_HEIGHT + HEADER_HEIGHT + COST_BAR_HEIGHT;
        }

        int visibleRows() {
            return Math.max(1, (sy1 - listTop()) / ROW_HEIGHT);
        }

        int dialogTop() {
            return (sy0 + sy1 - DIALOG_HEIGHT) / 2;
        }

        int buttonTop() {
            return dialogTop() + DIALOG_HEIGHT - DIALOG_BUTTON_HEIGHT - 6;
        }

        int buttonWidth() {
            return (sx1 - sx0 - 12 - 6 - 6) / 2;
        }

        int yesX0() {
            return sx0 + 12;
        }

        int noX0() {
            return yesX0() + buttonWidth() + 6;
        }
    }

    private final List<Entry> vehicles = new ArrayList<>();
    private final List<Entry> shopItems = new ArrayList<>();
    private final List<Entry> musicTracks = new ArrayList<>();
    private App openApp = null;
    private int scrollRow = 0;

    private boolean draggingScrollbar = false;

    private boolean weatherPickerOpen = false;

    private boolean clockPickerOpen = false;

    private boolean musicBrowsing = false;

    private boolean draggingVolume = false;

    private Entry pending = null;
    private long purchasedBannerUntil = 0L;

    private long shutdownUntil = 0L;

    private String phoneModel = SmartDispatchAppItem.DEFAULT_PHONE_MODEL;
    private String wallpaper = SmartDispatchAppItem.WALLPAPERS[0];
    private PhoneSkin skin = PhoneSkin.POPPHONE_6;

    private int settingsPage = SETTINGS_PAGE_MAIN;

    private net.minecraft.client.KeyMapping rebindingKey;

    public SmartDispatchAppScreen() {
        super(ModText.translatable("item.mineturismo.smart_dispatch_app"));
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            ItemStack phone = SmartDispatchAppItem.heldPhone(mc.player);
            this.phoneModel = SmartDispatchAppItem.phoneModelOf(phone);
            this.wallpaper = SmartDispatchAppItem.wallpaperOf(phone);
            this.skin = PhoneSkin.byId(this.phoneModel);
        }
        for (String modelId : AiCarPool.allModelIds(id -> false)) {
            Item item = AiCarPool.spawnItemFor(modelId);
            if (item != null) {
                this.vehicles.add(new Entry(modelId, new ItemStack(item), ModText.translatable(item.getDescriptionId())));
            }
        }
        this.vehicles.sort(Comparator.comparing(e -> e.name().getString()));
        for (Item item : SmartDispatchAppItem.shopItems()) {
            this.shopItems.add(new Entry(ModRegistries.ITEM.getKey(item).toString(), new ItemStack(item),
                    ModText.translatable(item.getDescriptionId())));
        }

        ItemStack wardenEnergy = SmartDispatchAppItem.createWardenEnergy(1);
        if (!wardenEnergy.isEmpty()) {
            this.shopItems.add(new Entry(SmartDispatchAppItem.WARDEN_ENERGY_ID, wardenEnergy,
                    ModText.translatable("item.mineturismo.warden_energy").append(" x" + SmartDispatchAppItem.WARDEN_ENERGY_PACK)));
        }

        ItemStack wardenEnergySonicBoom = SmartDispatchAppItem.createWardenEnergySonicBoom(1);
        if (!wardenEnergySonicBoom.isEmpty()) {
            this.shopItems.add(new Entry(SmartDispatchAppItem.WARDEN_ENERGY_SONIC_BOOM_ID, wardenEnergySonicBoom,
                    ModText.translatable("item.mineturismo.warden_energy_sonic_boom").append(" x" + SmartDispatchAppItem.WARDEN_ENERGY_PACK)));
        }

        ItemStack chocoBar = SmartDispatchAppItem.createChocoBar(1);
        if (!chocoBar.isEmpty()) {
            this.shopItems.add(new Entry(SmartDispatchAppItem.CHOCO_BAR_ID, chocoBar,
                    ModText.translatable("item.mineturismo.choco_bar").append(" x" + SmartDispatchAppItem.CHOCO_BAR_PACK)));
        }
        this.shopItems.sort(Comparator.comparing(e -> e.name().getString()));

        java.util.Map<String, Item> discs = new java.util.HashMap<>();
        for (Item item : ModRegistries.ITEM) {
            net.minecraft.resources.ResourceLocation key = ModRegistries.ITEM.getKey(item);
            if ("minecraft".equals(key.getNamespace()) && key.getPath().startsWith("music_disc_")) {
                discs.put(key.getPath().substring("music_disc_".length()), item);
            }
        }
        for (com.examplebett.client.sound.ChorusMusicTracks.Track track : com.examplebett.client.sound.ChorusMusicTracks.TRACKS) {

            if (net.minecraft.client.Minecraft.getInstance().getSoundManager().getSoundEvent(track.soundId()) == null) {
                continue;
            }
            Item icon = switch (track.category()) {
                case RECORDS -> discs.getOrDefault(track.id(), Items.JUKEBOX);
                case CREATIVE -> Items.BRICKS;
                case MENU -> Items.PAINTING;
                case NETHER -> Items.NETHERRACK;
                case END -> Items.END_STONE;
                case UNDERWATER -> Items.PRISMARINE;
                default -> Items.GRASS_BLOCK;
            };
            this.musicTracks.add(new Entry(track.id(), new ItemStack(icon), ModText.literal(track.title())));
        }
    }

    public static SmartDispatchAppScreen massiveAuto() {
        SmartDispatchAppScreen screen = new SmartDispatchAppScreen();
        screen.openApp = App.MASSIVE_AUTO;
        return screen;
    }

    private static com.examplebett.client.sound.ChorusMusicTracks.Track findTrack(String id) {
        for (com.examplebett.client.sound.ChorusMusicTracks.Track track : com.examplebett.client.sound.ChorusMusicTracks.TRACKS) {
            if (track.id().equals(id)) {
                return track;
            }
        }
        return null;
    }

    private List<Entry> entries() {
        if (this.openApp == App.MUSIC) {
            return this.musicBrowsing ? this.musicTracks : List.of();
        }
        return this.openApp == null || this.openApp.weather || this.openApp == App.EMERGENCY || this.openApp == App.GTT
                || this.openApp == App.CLOCK || this.openApp == App.MASSIVE_AUTO || this.openApp == App.SLOT
                || this.openApp == App.SETTINGS ? List.of()
                : this.openApp.vehicles ? this.vehicles : this.shopItems;
    }

    private Layout layout() {
        int phoneH = Math.min(PHONE_MAX_HEIGHT, this.height - 16) / PX * PX;
        int phoneW = Math.round(phoneH * PHONE_ASPECT) / PX * PX;
        int x0 = (this.width - phoneW) / 2 / PX * PX;
        int y0 = (this.height - phoneH) / 2 / PX * PX;
        int x1 = x0 + phoneW;
        int y1 = y0 + phoneH;

        int[] home = this.skin.homeRect(x0, x1, y1 - this.skin.insetBottom, y1);
        return new Layout(x0, y0, x1, y1,
                x0 + this.skin.insetX, y0 + this.skin.insetTop, x1 - this.skin.insetX, y1 - this.skin.insetBottom,
                home[0], home[1], home[2], home[3]);
    }

    private int maxScrollRow(Layout l) {
        return Math.max(0, this.entries().size() - l.visibleRows());
    }

    private boolean isFree() {
        return this.minecraft != null && this.minecraft.player != null && ModPlayer.hasInstabuild(this.minecraft.player);
    }

    private static int costOf(App app) {
        return app != null && app.vehicles ? SmartDispatchAppItem.VEHICLE_CALL_COST : SmartDispatchAppItem.SHOP_COST;
    }

    private boolean hasEnoughEmeralds() {
        return this.minecraft != null && this.minecraft.player != null
                && ModPlayer.getInventory(this.minecraft.player).countItem(Items.EMERALD) >= costOf(this.openApp);
    }

    private static boolean inside(double mx, double my, int x0, int y0, int x1, int y1) {
        return mx >= x0 && mx < x1 && my >= y0 && my < y1;
    }

    static void fillPixelRounded(GuiGraphics g, int x0, int y0, int x1, int y1, int step, int color) {
        g.fill(x0 + step * 2, y0, x1 - step * 2, y0 + step, color);
        g.fill(x0 + step, y0 + step, x1 - step, y0 + step * 2, color);
        g.fill(x0, y0 + step * 2, x1, y1 - step * 2, color);
        g.fill(x0 + step, y1 - step * 2, x1 - step, y1 - step, color);
        g.fill(x0 + step * 2, y1 - step, x1 - step * 2, y1, color);
    }

    static void fillPixelCircle(GuiGraphics g, int cx, int cy, int radius, int color) {
        for (int dy = -radius; dy < radius; dy += PX) {
            double mid = dy + PX / 2.0;
            int half = (int) Math.round(Math.sqrt(Math.max(0.0, radius * (double) radius - mid * mid)) / PX) * PX;
            if (half > 0) {
                g.fill(cx - half, cy + dy, cx + half, cy + dy + PX, color);
            }
        }
    }

    private static int lerpColor(int a, int b, float t) {
        int r = Math.round(((a >> 16) & 0xFF) + (((b >> 16) & 0xFF) - ((a >> 16) & 0xFF)) * t);
        int gr = Math.round(((a >> 8) & 0xFF) + (((b >> 8) & 0xFF) - ((a >> 8) & 0xFF)) * t);
        int bl = Math.round((a & 0xFF) + ((b & 0xFF) - (a & 0xFF)) * t);
        return 0xFF000000 | (r << 16) | (gr << 8) | bl;
    }

    private void drawPhone(GuiGraphics g, Layout l) {
        this.skin.drawBody(g, l.x0(), l.y0(), l.x1(), l.y1(), l.sx0(), l.sy0(), l.sx1(), l.sy1(),
                new int[]{l.homeX0(), l.homeY0(), l.homeX1(), l.homeY1()});
    }

    private static void drawWallpaper(GuiGraphics g, String wallpaper, int x0, int y0, int x1, int y1) {
        if ("white".equals(wallpaper) || "gray".equals(wallpaper)) {
            g.fill(x0, y0, x1, y1, "white".equals(wallpaper) ? 0xFFFFFFFF : 0xFF8E8E93);
            return;
        }
        int[] fallback = switch (wallpaper) {
            case "sunset_beach" -> new int[]{0xFF8A83C6, 0xFFF4AE96};
            case "grass" -> new int[]{0xFF5FA83A, 0xFF7A4E28};
            case "flow" -> new int[]{0xFFE8336A, 0xFF4A6CD8};
            default -> null;
        };
        if (fallback != null) {
            ResourceLocation texture = AiObjectMod.id("textures/gui/smart_dispatch/wallpaper_" + wallpaper + ".png");
            int w = x1 - x0;
            int h = y1 - y0;
            int vH = WALLPAPER_TEXTURE_HEIGHT;
            int uW = Math.min(WALLPAPER_TEXTURE_WIDTH, Math.round((float) vH * w / Math.max(1, h)));
            if (uW == WALLPAPER_TEXTURE_WIDTH) {
                vH = Math.round((float) uW * h / Math.max(1, w));
            }
            float u = (WALLPAPER_TEXTURE_WIDTH - uW) / 2f;
            float v = (WALLPAPER_TEXTURE_HEIGHT - vH) / 2f;
            com.examplebett.compat.GuiTextures.blit(g, texture, x0, y0, u, v, w, h,
                    uW, vH, WALLPAPER_TEXTURE_WIDTH, WALLPAPER_TEXTURE_HEIGHT);
            return;
        }
        drawGradient(g, x0, y0, x1, y1, COLOR_WALLPAPER_TOP, COLOR_WALLPAPER_BOTTOM);
    }

    private static void drawGradient(GuiGraphics g, int x0, int y0, int x1, int y1, int top, int bottom) {
        int bands = Math.max(1, (y1 - y0) / (PX * 2));
        for (int i = 0; i < bands; i++) {
            int bandTop = y0 + i * PX * 2;
            int bandBottom = i == bands - 1 ? y1 : bandTop + PX * 2;
            g.fill(x0, bandTop, x1, bandBottom, lerpColor(top, bottom, (float) i / bands));
        }
    }

    private long levelDayTime() {

        return this.minecraft.level.getDayTime();
    }

    private String currentTimeText() {
        if (this.minecraft == null || this.minecraft.level == null) {
            return "--:--";
        }
        long dayTime = Math.floorMod(this.levelDayTime(), 24000L);
        int hours = (int) ((dayTime / 1000L + 6L) % 24L);
        int minutes = (int) (dayTime % 1000L * 60L / 1000L);
        return String.format("%d:%02d", hours, minutes);
    }

    private void drawStatusBar(GuiGraphics g, Layout l) {
        g.fill(l.sx0(), l.sy0(), l.sx1(), l.sy0() + STATUS_BAR_HEIGHT, COLOR_STATUS_BAR);
        this.drawSmallText(g, this.currentTimeText(), l.sx0() + 3, l.sy0() + 3, 0xFFFFFFFF);

        int bx1 = l.sx1() - 3;
        int by0 = l.sy0() + 3;
        g.fill(bx1 - 12, by0, bx1 - 1, by0 + 6, 0xFFFFFFFF);
        g.fill(bx1 - 1, by0 + 2, bx1, by0 + 4, 0xFFFFFFFF);
        g.fill(bx1 - 11, by0 + 1, bx1 - 2, by0 + 5, 0xFF000000);
        g.fill(bx1 - 10, by0 + 2, bx1 - 4, by0 + 4, 0xFF5BD35B);

        for (int i = 0; i < 4; i++) {
            int bx = bx1 - 26 + i * 3;
            g.fill(bx, by0 + 6 - (i + 1) * 3 / 2, bx + 2, by0 + 6, 0xFFFFFFFF);
        }
    }

    private int[] appIconRect(Layout l, int index) {
        int cellW = (l.sx1() - l.sx0()) / APP_COLUMNS;
        int x = l.sx0() + (index % APP_COLUMNS) * cellW + (cellW - APP_ICON_SIZE) / 2;
        int y = l.sy0() + STATUS_BAR_HEIGHT + 8 + (index / APP_COLUMNS) * (APP_ICON_SIZE + 20);
        return new int[]{x, y, x + APP_ICON_SIZE, y + APP_ICON_SIZE};
    }

    private void drawHome(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        drawWallpaper(g, this.wallpaper, l.sx0(), l.sy0(), l.sx1(), l.sy1());

        boolean lightWallpaper = "white".equals(this.wallpaper);
        int labelColor = lightWallpaper ? 0xFF1C1C1E : 0xFFFFFFFF;
        App[] apps = App.values();
        for (int i = 0; i < apps.length; i++) {
            App app = apps[i];
            int[] r = appIconRect(l, i);
            com.examplebett.compat.GuiTextures.blit(g, app.icon, r[0], r[1], 0f, 0f, APP_ICON_SIZE, APP_ICON_SIZE,
                    APP_ICON_TEXTURE_SIZE, APP_ICON_TEXTURE_SIZE, APP_ICON_TEXTURE_SIZE, APP_ICON_TEXTURE_SIZE);
            if (this.pending == null && inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {

                fillPixelRounded(g, r[0], r[1], r[2], r[3], 2, 0x33FFFFFF);
            }

            int cellWidth = (l.sx1() - l.sx0()) / APP_COLUMNS - 2;
            String[] labelLines = app.shortName().split("\n");
            for (int line = 0; line < labelLines.length; line++) {
                float scale = Math.min(SMALL_TEXT_SCALE, (float) cellWidth / Math.max(1, this.font.width(labelLines[line])));
                int labelWidth = Math.round(this.font.width(labelLines[line]) * scale);
                this.drawScaledText(g, (
                                net.minecraft.network.chat.FormattedText.of(labelLines[line])), (r[0] + r[2] - labelWidth) / 2,
                        r[3] + 2 + line * 7, labelColor, scale);
            }
        }

        int dotsY = l.sy1() - PX * 4;
        int cx = (l.sx0() + l.sx1()) / 2 / PX * PX;
        int dimDot = lightWallpaper ? 0x881C1C1E : 0x88FFFFFF;
        g.fill(cx - PX * 3, dotsY, cx - PX * 2, dotsY + PX, labelColor);
        g.fill(cx - PX / 2, dotsY, cx + PX / 2 + 1, dotsY + PX, dimDot);
        g.fill(cx + PX * 2, dotsY, cx + PX * 3, dotsY + PX, dimDot);
    }

    private void drawApp(GuiGraphics g, Layout l, App app, int mouseX, int mouseY) {
        int headerTop = l.sy0() + STATUS_BAR_HEIGHT;
        g.fill(l.sx0(), l.sy0(), l.sx1(), l.sy1(), COLOR_ROW);
        g.fill(l.sx0(), l.sy0(), l.sx1(), headerTop + HEADER_HEIGHT, app.headerColor);

        int ax = l.sx0() + 4;
        int ay = headerTop + HEADER_HEIGHT / 2;
        for (int i = 0; i < 4; i++) {
            g.fill(ax + i, ay - i - 1, ax + i + 1, ay - i + 1, app.titleColor);
            g.fill(ax + i, ay + i - 1, ax + i + 1, ay + i + 1, app.titleColor);
        }
        int titleWidth = this.font.width(app.title());
        int titleRoom = l.sx1() - l.sx0() - 24;
        if (titleWidth <= titleRoom) {
            g.drawString(this.font, app.title(), (l.sx0() + l.sx1() - titleWidth) / 2, headerTop + 4, app.titleColor, false);
        } else {
            int scaled = Math.round(titleWidth * SMALL_TEXT_SCALE);
            this.drawSmallText(g, app.title(), (l.sx0() + l.sx1() - scaled) / 2, headerTop + 5, app.titleColor);
        }
        if (app.weather) {
            this.drawWeather(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.EMERGENCY) {
            this.drawEmergency(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.DETONATOR) {
            this.drawDetonator(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.CLOCK) {
            this.drawClock(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.MASSIVE_AUTO) {
            this.drawMassiveAuto(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.SLOT) {
            this.drawSlot(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.SETTINGS) {
            this.drawSettings(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.MUSIC && !this.musicBrowsing) {
            this.drawMusicHome(g, l, mouseX, mouseY);
            return;
        }
        int costTop = headerTop + HEADER_HEIGHT;
        g.fill(l.sx0(), costTop, l.sx1(), costTop + COST_BAR_HEIGHT, COLOR_COST_BAR);
        Component cost = app == App.MUSIC ? this.musicStatus() : this.isFree()
                ? ModText.translatable("gui.mineturismo.smart_dispatch.free")
                : ModText.translatable(app.vehicles ? "gui.mineturismo.smart_dispatch.cost" : "gui.mineturismo.smart_dispatch.price",
                        costOf(app));
        this.drawSmallText(g, cost.getString(), l.sx0() + 3, costTop + 2, 0xFFE8E8E8);

        List<Entry> entries = this.entries();
        int listTop = l.listTop();
        int rows = l.visibleRows();
        this.scrollRow = Math.max(0, Math.min(this.maxScrollRow(l), this.scrollRow));
        int listRight = l.sx1() - 4;
        for (int i = 0; i < rows; i++) {
            int index = this.scrollRow + i;
            if (index >= entries.size()) {
                break;
            }
            Entry e = entries.get(index);
            int top = listTop + i * ROW_HEIGHT;
            boolean hover = this.pending == null && inside(mouseX, mouseY, l.sx0(), top, listRight, top + ROW_HEIGHT);
            boolean playing = app == App.MUSIC && com.examplebett.client.sound.ChorusMusicPlayer.current() != null
                    && com.examplebett.client.sound.ChorusMusicPlayer.current().id().equals(e.id());
            g.fill(l.sx0(), top, listRight, top + ROW_HEIGHT,
                    hover ? COLOR_ROW_HOVER : playing ? 0xFFFFD9DF : index % 2 == 0 ? COLOR_ROW : COLOR_ROW_ALT);
            g.renderItem(e.stack(), l.sx0() + 2, top + 2);
            int textX = l.sx0() + 21;
            this.drawSmallText(g, this.fit(e.name().getString(), listRight - textX - 2), textX, top + 7, 0xFF202024);
        }

        g.fill(listRight, listTop, l.sx1(), l.sy1(), COLOR_SCROLL_TRACK);
        int max = this.maxScrollRow(l);
        if (max > 0) {
            int trackH = l.sy1() - listTop;
            int thumbH = this.thumbHeight(l);
            int thumbTop = listTop + (trackH - thumbH) * this.scrollRow / max;
            g.fill(listRight + 1, thumbTop, l.sx1() - 1, thumbTop + thumbH, COLOR_SCROLL_THUMB);
        }
        if (System.currentTimeMillis() < this.purchasedBannerUntil) {
            this.aboveItems(g);
            String text = ModText.translatable("gui.mineturismo.smart_dispatch.purchased").getString();
            int w = Math.round(this.font.width(text) * SMALL_TEXT_SCALE) + 10;
            int bx0 = (l.sx0() + l.sx1() - w) / 2;
            int by0 = l.sy1() - 18;
            fillPixelRounded(g, bx0, by0, bx0 + w, by0 + 12, 1, COLOR_BANNER);
            this.drawSmallText(g, text, bx0 + 5, by0 + 3, 0xFFFFFFFF);
        }
    }

    private int settingsRowCount() {
        return this.settingsPage == SETTINGS_PAGE_WALLPAPER ? SmartDispatchAppItem.WALLPAPERS.length
                : this.settingsPage == SETTINGS_PAGE_MODEL ? SmartDispatchAppItem.PHONE_MODELS.length : 3;
    }

    private record ControlRow(String labelKey, String keyText, net.minecraft.client.KeyMapping rebind) {
    }

    private static String keyName(net.minecraft.client.KeyMapping mapping) {
        return mapping.getTranslatedKeyMessage().getString();
    }

    private List<ControlRow> controlRows() {
        net.minecraft.client.Options o = this.minecraft.options;
        net.minecraft.client.KeyMapping horn = com.examplebett.client.AiObjectModClient.hornKey();
        net.minecraft.client.KeyMapping trailer = com.examplebett.client.AiObjectModClient.trailerCouplingKey();
        String p = "gui.mineturismo.smart_dispatch.controls_";
        return List.of(
                new ControlRow(p + "accelerate", keyName(o.keyUp), null),
                new ControlRow(p + "brake", keyName(o.keyDown), null),
                new ControlRow(p + "steer", keyName(o.keyLeft) + " / " + keyName(o.keyRight), null),
                new ControlRow(p + "drift", keyName(o.keyJump), null),
                new ControlRow(p + "burnout", keyName(o.keyUp) + "+" + keyName(o.keyDown), null),

                new ControlRow(p + "jturn", keyName(o.keyDown) + "+" + keyName(o.keyLeft) + "/" + keyName(o.keyRight), null),

                new ControlRow(p + "wheelie", keyName(o.keySprint), null),
                new ControlRow(p + "aircraft", keyName(o.keyJump) + " / " + keyName(o.keySprint), null),
                new ControlRow(p + "dismount", keyName(o.keyShift), null),
                new ControlRow(p + "horn", keyName(horn), horn),
                new ControlRow(p + "trailer", keyName(trailer), trailer));
    }

    private int[] controlsRowRect(Layout l, int index) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT + 16 + index * CONTROLS_ROW_HEIGHT;
        return new int[]{l.sx0() + 5, top, l.sx1() - 5, top + CONTROLS_ROW_HEIGHT};
    }

    private void drawControls(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int width = l.sx1() - l.sx0();
        this.drawSmallText(g, this.fit(ModText.translatable("gui.mineturismo.smart_dispatch.settings_controls").getString(), width - 12),
                l.sx0() + 7, top + 6, 0xFF6C6C72);
        List<ControlRow> rows = this.controlRows();
        int[] first = this.controlsRowRect(l, 0);
        int[] last = this.controlsRowRect(l, rows.size() - 1);
        fillPixelRounded(g, first[0], first[1], last[2], last[3], 1, 0xFFFFFFFF);
        for (int i = 0; i < rows.size(); i++) {
            ControlRow row = rows.get(i);
            int[] r = this.controlsRowRect(l, i);
            if (row.rebind() != null && inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
                g.fill(r[0] + 1, r[1] + (i == 0 ? 1 : 0), r[2] - 1, r[3] - (i == rows.size() - 1 ? 1 : 0), 0xFFE5E5EA);
            }
            if (i > 0) {
                g.fill(r[0] + 4, r[1], r[2], r[1] + 1, 0xFFE5E5EA);
            }

            boolean waiting = row.rebind() != null && row.rebind() == this.rebindingKey;
            String key = waiting ? ModText.translatable("gui.mineturismo.smart_dispatch.controls_press_key").getString() : row.keyText();
            key = this.fit(key, (r[2] - r[0]) / 2 - 4);
            int keyWidth = Math.round(this.font.width(key) * SMALL_TEXT_SCALE);
            int keyColor = waiting ? 0xFFFF9500 : row.rebind() != null ? 0xFF0A84FF : 0xFF6C6C72;
            this.drawSmallText(g, key, r[2] - 4 - keyWidth, r[1] + 3, keyColor);
            String label = this.fit(ModText.translatable(row.labelKey()).getString(), r[2] - r[0] - keyWidth - 12);
            this.drawSmallText(g, label, r[0] + 4, r[1] + 3, 0xFF202024);
        }

        int noteY = last[3] + 4;
        for (String noteKey : new String[]{"controls_note_rebind", "controls_note_vanilla"}) {
            String note = ModText.translatable("gui.mineturismo.smart_dispatch." + noteKey).getString();
            for (String line : this.wrapSmall(note, width - 12)) {

                if (noteY + 7 > l.sy1() - 2) {
                    break;
                }
                this.drawSmallText(g, line, l.sx0() + 7, noteY, 0xFF8E8E93);
                noteY += 7;
            }
            noteY += 2;
        }
    }

    private List<String> wrapSmall(String text, int maxWidth) {
        List<String> lines = new java.util.ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String next = line.toString() + text.charAt(i);
            if (line.length() > 0 && Math.round(this.font.width(next) * SMALL_TEXT_SCALE) > maxWidth) {
                lines.add(line.toString());
                line.setLength(0);
            }
            line.append(text.charAt(i));
        }
        if (line.length() > 0) {
            lines.add(line.toString());
        }
        return lines;
    }

    private boolean handleControlsClick(Layout l, double mouseX, double mouseY) {
        List<ControlRow> rows = this.controlRows();
        for (int i = 0; i < rows.size(); i++) {
            int[] r = this.controlsRowRect(l, i);
            if (rows.get(i).rebind() != null && inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {

                this.rebindingKey = this.rebindingKey == rows.get(i).rebind() ? null : rows.get(i).rebind();
                return true;
            }
        }
        this.rebindingKey = null;
        return false;
    }

    private boolean handleRebindKey(int keyCode, java.util.function.Supplier<com.mojang.blaze3d.platform.InputConstants.Key> key) {
        if (this.rebindingKey == null || this.openApp != App.SETTINGS || this.settingsPage != SETTINGS_PAGE_CONTROLS) {
            return false;
        }
        if (keyCode != org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            this.rebindingKey.setKey(key.get());
            net.minecraft.client.KeyMapping.resetMapping();
            this.minecraft.options.save();
        }
        this.rebindingKey = null;
        return true;
    }

    private int[] settingsRowRect(Layout l, int index) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT + 16 + index * SETTINGS_ROW_HEIGHT;
        return new int[]{l.sx0() + 5, top, l.sx1() - 5, top + SETTINGS_ROW_HEIGHT};
    }

    private void drawSettings(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFFF2F2F7);
        if (this.settingsPage == SETTINGS_PAGE_CONTROLS) {
            this.drawControls(g, l, mouseX, mouseY);
            return;
        }
        String section = this.settingsPage == SETTINGS_PAGE_WALLPAPER ? "gui.mineturismo.smart_dispatch.settings_wallpaper"
                : this.settingsPage == SETTINGS_PAGE_MODEL ? "gui.mineturismo.smart_dispatch.settings_model" : null;
        if (section != null) {
            this.drawSmallText(g, this.fit(ModText.translatable(section).getString(), l.sx1() - l.sx0() - 12), l.sx0() + 7, top + 6, 0xFF6C6C72);
        }
        int count = this.settingsRowCount();
        int[] first = this.settingsRowRect(l, 0);
        int[] last = this.settingsRowRect(l, count - 1);
        fillPixelRounded(g, first[0], first[1], last[2], last[3], 1, 0xFFFFFFFF);
        for (int i = 0; i < count; i++) {
            int[] r = this.settingsRowRect(l, i);
            if (inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
                g.fill(r[0] + 1, r[1] + (i == 0 ? 1 : 0), r[2] - 1, r[3] - (i == count - 1 ? 1 : 0), 0xFFE5E5EA);
            }
            if (i > 0) {
                g.fill(r[0] + 6, r[1], r[2], r[1] + 1, 0xFFD8D8DE);
            }
            int textX = r[0] + 6;
            String label;
            boolean selected = false;
            if (this.settingsPage == SETTINGS_PAGE_WALLPAPER) {
                String id = SmartDispatchAppItem.WALLPAPERS[i];

                g.fill(r[0] + 4, r[1] + 2, r[0] + 16, r[3] - 2, 0xFF121216);
                drawWallpaper(g, id, r[0] + 5, r[1] + 3, r[0] + 15, r[3] - 3);
                textX = r[0] + 21;
                label = ModText.translatable("gui.mineturismo.smart_dispatch.wallpaper_" + id).getString();
                selected = id.equals(this.wallpaper);
            } else if (this.settingsPage == SETTINGS_PAGE_MODEL) {
                String id = SmartDispatchAppItem.PHONE_MODELS[i];
                ItemStack icon = new ItemStack(com.examplebett.item.AiObjectModItems.SMART_DISPATCH_APP);
                SmartDispatchAppItem.applyPhoneModelIcon(icon, id);
                g.renderItem(icon, r[0] + 3, r[1] + 2);
                textX = r[0] + 21;
                label = PHONE_MODEL_NAMES[i] != null ? PHONE_MODEL_NAMES[i]
                        : ModText.translatable("gui.mineturismo.smart_dispatch.model_classic").getString();
                selected = id.equals(this.phoneModel);
            } else {
                label = ModText.translatable(i == 0 ? "gui.mineturismo.smart_dispatch.settings_wallpaper"
                        : i == 1 ? "gui.mineturismo.smart_dispatch.settings_model"
                        : "gui.mineturismo.smart_dispatch.settings_controls").getString();

                int ax = r[2] - 8;
                int ay = (r[1] + r[3]) / 2;
                for (int k = 0; k < 3; k++) {
                    g.fill(ax + k, ay - 3 + k, ax + k + 1, ay - 1 + k, 0xFFB4B4BA);
                    g.fill(ax + k, ay + 1 - k, ax + k + 1, ay + 3 - k, 0xFFB4B4BA);
                }
            }
            this.drawSmallText(g, this.fit(label, r[2] - textX - 14), textX, (r[1] + r[3]) / 2 - 3, 0xFF202024);
            if (selected) {

                int cx0 = r[2] - 11;
                int cy0 = (r[1] + r[3]) / 2;
                int[][] check = {{0, 0}, {1, 1}, {2, 2}, {3, 1}, {4, 0}, {5, -1}, {6, -2}, {7, -3}};
                for (int[] p : check) {
                    g.fill(cx0 + p[0], cy0 + p[1] - 1, cx0 + p[0] + 1, cy0 + p[1] + 1, 0xFF0A84FF);
                }
            }
        }
    }

    private boolean handleSettingsClick(Layout l, double mouseX, double mouseY) {
        if (this.settingsPage == SETTINGS_PAGE_CONTROLS) {
            return this.handleControlsClick(l, mouseX, mouseY);
        }
        for (int i = 0; i < this.settingsRowCount(); i++) {
            int[] r = this.settingsRowRect(l, i);
            if (!inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
                continue;
            }
            if (this.settingsPage == SETTINGS_PAGE_MAIN) {
                this.settingsPage = i == 0 ? SETTINGS_PAGE_WALLPAPER : i == 1 ? SETTINGS_PAGE_MODEL : SETTINGS_PAGE_CONTROLS;
                this.rebindingKey = null;
                return true;
            }
            if (this.settingsPage == SETTINGS_PAGE_WALLPAPER) {
                this.wallpaper = SmartDispatchAppItem.WALLPAPERS[i];
            } else {
                this.phoneModel = SmartDispatchAppItem.PHONE_MODELS[i];
                this.skin = PhoneSkin.byId(this.phoneModel);
            }
            com.examplebett.network.SetPhoneStylePayload payload = new com.examplebett.network.SetPhoneStylePayload(this.phoneModel, this.wallpaper);
            ModClientNetworking.sendToServer(com.examplebett.network.SetPhoneStylePayload.ID, payload, com.examplebett.network.SetPhoneStylePayload::write);
            return true;
        }
        return false;
    }

    private static final int WEATHER_SNOW = 3;
    private static final int WEATHER_BUTTON_HEIGHT = 14;
    private static final int WEATHER_BUTTON_GAP = 3;

    private static final int WEATHER_ICON_UNIT = 2;
    private static final int[] WEATHER_PICKS = {SmartDispatchAppItem.WEATHER_CLEAR, SmartDispatchAppItem.WEATHER_RAIN, SmartDispatchAppItem.WEATHER_THUNDER};
    private static final String[] WEATHER_KEYS = {"weather_clear", "weather_rain", "weather_thunder", "weather_snow"};

    private int currentWeather() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) {
            return SmartDispatchAppItem.WEATHER_CLEAR;
        }
        net.minecraft.client.multiplayer.ClientLevel level = this.minecraft.level;
        if (!level.isRaining()) {
            return SmartDispatchAppItem.WEATHER_CLEAR;
        }
        net.minecraft.core.BlockPos pos = this.minecraft.player.blockPosition();

        net.minecraft.world.level.biome.Biome.Precipitation precipitation = level.getBiome(pos).getPrecipitation();
        if (precipitation == net.minecraft.world.level.biome.Biome.Precipitation.NONE) {
            return SmartDispatchAppItem.WEATHER_CLEAR;
        }
        if (level.isThundering()) {
            return SmartDispatchAppItem.WEATHER_THUNDER;
        }
        return precipitation == net.minecraft.world.level.biome.Biome.Precipitation.SNOW ? WEATHER_SNOW : SmartDispatchAppItem.WEATHER_RAIN;
    }

    private Component currentPlaceName() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) {
            return ModText.literal("-");
        }

        return this.minecraft.level.getBiome(this.minecraft.player.blockPosition()).getName();
    }

    private int weatherButtonTop(Layout l, int index) {

        int changeTop = l.sy1() - WEATHER_BUTTON_HEIGHT - 5;
        return index == WEATHER_PICKS.length ? changeTop
                : changeTop - (WEATHER_PICKS.length - index) * (WEATHER_BUTTON_HEIGHT + WEATHER_BUTTON_GAP);
    }

    private void drawWeatherButton(GuiGraphics g, Layout l, int top, String label, int color, int hoverColor, int textColor,
                                   int mouseX, int mouseY) {
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        boolean hover = inside(mouseX, mouseY, x0, top, x1, top + WEATHER_BUTTON_HEIGHT);
        fillPixelRounded(g, x0, top, x1, top + WEATHER_BUTTON_HEIGHT, 1, hover ? hoverColor : color);
        g.drawString(this.font, label, (x0 + x1 - this.font.width(label)) / 2, top + 3, textColor, false);
    }

    private void drawWeather(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int weather = this.currentWeather();

        int skyTop;
        int skyBottom;
        switch (weather) {
            case SmartDispatchAppItem.WEATHER_RAIN -> { skyTop = 0xFF7A8796; skyBottom = 0xFF4E5A68; }
            case SmartDispatchAppItem.WEATHER_THUNDER -> { skyTop = 0xFF4A4F5C; skyBottom = 0xFF262A33; }
            case WEATHER_SNOW -> { skyTop = 0xFFB9C9DA; skyBottom = 0xFF8EA3BA; }
            default -> { skyTop = 0xFF6FB8F5; skyBottom = 0xFF3D8BD9; }
        }
        int bands = Math.max(1, (l.sy1() - top) / (PX * 2));
        for (int i = 0; i < bands; i++) {
            int bandTop = top + i * PX * 2;
            g.fill(l.sx0(), bandTop, l.sx1(), i == bands - 1 ? l.sy1() : bandTop + PX * 2, lerpColor(skyTop, skyBottom, (float) i / bands));
        }
        int cx = (l.sx0() + l.sx1()) / 2;
        int iconSize = 16 * WEATHER_ICON_UNIT;
        int iconTop = top + 8;
        this.drawWeatherIcon(g, cx - iconSize / 2, iconTop, weather);

        int textTop = iconTop + iconSize + 6;
        String placeLabel = ModText.translatable("gui.mineturismo.smart_dispatch.weather_location").getString();
        int labelWidth = Math.round(this.font.width(placeLabel) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, placeLabel, cx - labelWidth / 2, textTop, 0xDDFFFFFF);
        String place = this.currentPlaceName().getString();
        int room = l.sx1() - l.sx0() - 8;
        if (this.font.width(place) <= room) {
            g.drawString(this.font, place, cx - this.font.width(place) / 2, textTop + 9, 0xFFFFFFFF, true);
        } else {
            String fitted = this.fit(place, room);
            this.drawSmallText(g, fitted, cx - Math.round(this.font.width(fitted) * SMALL_TEXT_SCALE) / 2, textTop + 10, 0xFFFFFFFF);
        }
        String weatherName = ModText.translatable("gui.mineturismo.smart_dispatch." + WEATHER_KEYS[weather]).getString();
        g.drawString(this.font, weatherName, cx - this.font.width(weatherName) / 2, textTop + 22, 0xFFFFFFFF, true);

        if (this.weatherPickerOpen) {
            for (int i = 0; i < WEATHER_PICKS.length; i++) {
                String label = ModText.translatable("gui.mineturismo.smart_dispatch." + WEATHER_KEYS[WEATHER_PICKS[i]]).getString();
                this.drawWeatherButton(g, l, this.weatherButtonTop(l, i), label, 0xFFFFFFFF, 0xFFFFF1B8, 0xFF202024, mouseX, mouseY);
            }
        }
        String change = ModText.translatable("gui.mineturismo.smart_dispatch.weather_change").getString();
        this.drawWeatherButton(g, l, this.weatherButtonTop(l, WEATHER_PICKS.length), change, 0xFF2A2A30, 0xFF44444E, 0xFFFFFFFF, mouseX, mouseY);
    }

    private void cell(GuiGraphics g, int ox, int oy, int ux, int uy, int w, int h, int color) {
        int u = WEATHER_ICON_UNIT;
        g.fill(ox + ux * u, oy + uy * u, ox + (ux + w) * u, oy + (uy + h) * u, color);
    }

    private void disc(GuiGraphics g, int ox, int oy, int cx, int cy, int r, int color) {
        for (int dy = -r; dy <= r; dy++) {
            int w = (int) Math.round(Math.sqrt(r * r - dy * dy));
            this.cell(g, ox, oy, cx - w, cy + dy, w * 2 + 1, 1, color);
        }
    }

    private void cloud(GuiGraphics g, int ox, int oy, int color, int shade) {
        this.disc(g, ox, oy, 5, 6, 3, color);
        this.disc(g, ox, oy, 9, 5, 4, color);
        this.cell(g, ox, oy, 2, 6, 12, 4, color);
        this.cell(g, ox, oy, 2, 9, 12, 1, shade);
    }

    private void drawWeatherIcon(GuiGraphics g, int ox, int oy, int weather) {
        switch (weather) {
            case SmartDispatchAppItem.WEATHER_CLEAR -> {
                int sun = 0xFFFFD640;
                int rim = 0xFFF5A01E;
                this.cell(g, ox, oy, 7, 0, 2, 2, sun);
                this.cell(g, ox, oy, 7, 14, 2, 2, sun);
                this.cell(g, ox, oy, 0, 7, 2, 2, sun);
                this.cell(g, ox, oy, 14, 7, 2, 2, sun);
                this.cell(g, ox, oy, 2, 2, 2, 2, sun);
                this.cell(g, ox, oy, 12, 2, 2, 2, sun);
                this.cell(g, ox, oy, 2, 12, 2, 2, sun);
                this.cell(g, ox, oy, 12, 12, 2, 2, sun);
                this.disc(g, ox, oy, 8, 8, 4, rim);
                this.disc(g, ox, oy, 8, 8, 3, sun);
                this.cell(g, ox, oy, 6, 6, 1, 1, 0xFFFFF4B0);
            }
            case SmartDispatchAppItem.WEATHER_RAIN -> {
                this.cloud(g, ox, oy, 0xFFE4E8EE, 0xFFB4BCC8);
                int drop = 0xFF5FB6FF;
                this.cell(g, ox, oy, 4, 11, 1, 2, drop);
                this.cell(g, ox, oy, 8, 12, 1, 2, drop);
                this.cell(g, ox, oy, 12, 11, 1, 2, drop);
                this.cell(g, ox, oy, 6, 14, 1, 2, drop);
                this.cell(g, ox, oy, 10, 14, 1, 2, drop);
            }
            case SmartDispatchAppItem.WEATHER_THUNDER -> {
                this.cloud(g, ox, oy, 0xFF8A909C, 0xFF5E6470);
                int bolt = 0xFFFFE14A;
                this.cell(g, ox, oy, 8, 10, 2, 1, bolt);
                this.cell(g, ox, oy, 7, 11, 2, 1, bolt);
                this.cell(g, ox, oy, 6, 12, 4, 1, bolt);
                this.cell(g, ox, oy, 8, 13, 1, 1, bolt);
                this.cell(g, ox, oy, 7, 14, 1, 1, bolt);
                this.cell(g, ox, oy, 6, 15, 1, 1, bolt);
            }
            default -> {
                this.cloud(g, ox, oy, 0xFFF4F6FA, 0xFFC4CCD8);
                int flake = 0xFFFFFFFF;
                this.cell(g, ox, oy, 4, 12, 1, 1, flake);
                this.cell(g, ox, oy, 8, 11, 1, 1, flake);
                this.cell(g, ox, oy, 12, 12, 1, 1, flake);
                this.cell(g, ox, oy, 6, 14, 1, 1, flake);
                this.cell(g, ox, oy, 10, 14, 1, 1, flake);
            }
        }
    }

    private boolean handleWeatherClick(Layout l, double mouseX, double mouseY) {
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        int changeTop = this.weatherButtonTop(l, WEATHER_PICKS.length);
        if (inside(mouseX, mouseY, x0, changeTop, x1, changeTop + WEATHER_BUTTON_HEIGHT)) {
            this.weatherPickerOpen = !this.weatherPickerOpen;
            return true;
        }
        if (this.weatherPickerOpen) {
            for (int i = 0; i < WEATHER_PICKS.length; i++) {
                int top = this.weatherButtonTop(l, i);
                if (inside(mouseX, mouseY, x0, top, x1, top + WEATHER_BUTTON_HEIGHT)) {
                    com.examplebett.network.SetAppWeatherPayload payload = new com.examplebett.network.SetAppWeatherPayload(WEATHER_PICKS[i]);
                    ModClientNetworking.sendToServer(com.examplebett.network.SetAppWeatherPayload.ID, payload, com.examplebett.network.SetAppWeatherPayload::write);
                    this.weatherPickerOpen = false;
                    return true;
                }
            }
        }
        return false;
    }

    private static final int EMERGENCY_BUTTON_HEIGHT = 30;

    private int[] emergencyButtonRect(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int y0 = (top + l.sy1()) / 2 - EMERGENCY_BUTTON_HEIGHT / 2;
        return new int[]{l.sx0() + 10, y0, l.sx1() - 10, y0 + EMERGENCY_BUTTON_HEIGHT};
    }

    private void drawEmergency(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFFF6F0F0);
        String lead = ModText.translatable("gui.mineturismo.smart_dispatch.emergency_lead").getString();
        String leadFitted = this.fit(lead, l.sx1() - l.sx0() - 8);
        this.drawSmallText(g, leadFitted, cx - Math.round(this.font.width(leadFitted) * SMALL_TEXT_SCALE) / 2, top + 10, 0xFF5A1C1C);
        int[] r = this.emergencyButtonRect(l);
        boolean hover = inside(mouseX, mouseY, r[0], r[1], r[2], r[3]);

        fillPixelRounded(g, r[0], r[1] + 2, r[2], r[3] + 2, PX, 0xFF8E1414);
        fillPixelRounded(g, r[0], r[1], r[2], r[3], PX, hover ? 0xFFEF4A3E : 0xFFD32F2F);
        String label = ModText.translatable("gui.mineturismo.smart_dispatch.emergency_call").getString();
        g.drawString(this.font, label, cx - this.font.width(label) / 2, (r[1] + r[3]) / 2 - 4, 0xFFFFFFFF, true);

        Component note = ModText.translatable("gui.mineturismo.smart_dispatch.emergency_note");
        List<net.minecraft.network.chat.FormattedText> lines = this.font.split(note, Math.round((l.sx1() - l.sx0() - 12) / SMALL_TEXT_SCALE));
        for (int i = 0; i < Math.min(3, lines.size()); i++) {
            int w = Math.round(this.font.width(lines.get(i)) * SMALL_TEXT_SCALE);
            this.drawSmallText(g, lines.get(i), cx - w / 2, r[3] + 10 + i * 8, 0xFF6A6A70);
        }
    }

    private boolean handleEmergencyClick(Layout l, double mouseX, double mouseY) {
        int[] r = this.emergencyButtonRect(l);
        if (!inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
            return false;
        }
        com.examplebett.network.RequestEmergencyCallPayload payload = new com.examplebett.network.RequestEmergencyCallPayload();
        ModClientNetworking.sendToServer(com.examplebett.network.RequestEmergencyCallPayload.ID, payload, com.examplebett.network.RequestEmergencyCallPayload::write);
        this.onClose();
        return true;
    }

    private static final int DETONATOR_BUTTON_SIZE = 40;

    private int[] detonatorButtonRect(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        int y0 = (top + l.sy1()) / 2 - DETONATOR_BUTTON_SIZE / 2 - 6;
        return new int[]{cx - DETONATOR_BUTTON_SIZE / 2, y0, cx + DETONATOR_BUTTON_SIZE / 2, y0 + DETONATOR_BUTTON_SIZE};
    }

    private void drawDetonator(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFF1C1D21);
        String lead = ModText.translatable("gui.mineturismo.smart_dispatch.detonator_lead").getString();
        String leadFitted = this.fit(lead, l.sx1() - l.sx0() - 8);
        this.drawSmallText(g, leadFitted, cx - Math.round(this.font.width(leadFitted) * SMALL_TEXT_SCALE) / 2, top + 10, 0xFFFFC94A);
        int[] r = this.detonatorButtonRect(l);
        boolean hover = inside(mouseX, mouseY, r[0], r[1], r[2], r[3]);

        int bcx = (r[0] + r[2]) / 2;
        int bcy = (r[1] + r[3]) / 2;
        int radius = DETONATOR_BUTTON_SIZE / 2;
        fillPixelCircle(g, bcx, bcy + 1, radius + 4, 0xFF96989F);
        fillPixelCircle(g, bcx, bcy + 3, radius, 0xFF780E12);
        fillPixelCircle(g, bcx, bcy, radius, hover ? 0xFFF04040 : 0xFFDE2424);
        String label = ModText.translatable("gui.mineturismo.smart_dispatch.detonator_button").getString();
        g.drawString(this.font, label, cx - this.font.width(label) / 2, (r[1] + r[3]) / 2 - 4, 0xFFFFFFFF, true);

        int stripeTop = r[3] + 10;
        for (int x = l.sx0(); x < l.sx1(); x += PX * 2) {
            int stripe = ((x - l.sx0()) / (PX * 2)) % 2 == 0 ? 0xFFF6C41E : 0xFF18181A;
            g.fill(x, stripeTop, Math.min(l.sx1(), x + PX * 2), stripeTop + PX * 2, stripe);
        }
        Component note = ModText.translatable("gui.mineturismo.smart_dispatch.detonator_note");
        List<net.minecraft.network.chat.FormattedText> lines = this.font.split(note, Math.round((l.sx1() - l.sx0() - 12) / SMALL_TEXT_SCALE));
        for (int i = 0; i < Math.min(3, lines.size()); i++) {
            int w = Math.round(this.font.width(lines.get(i)) * SMALL_TEXT_SCALE);
            this.drawSmallText(g, lines.get(i), cx - w / 2, stripeTop + PX * 2 + 6 + i * 8, 0xFFB0B0B8);
        }
    }

    private boolean handleDetonatorClick(Layout l, double mouseX, double mouseY) {
        int[] r = this.detonatorButtonRect(l);
        if (!inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
            return false;
        }
        com.examplebett.network.DetonateBombsPayload payload = new com.examplebett.network.DetonateBombsPayload();
        ModClientNetworking.sendToServer(com.examplebett.network.DetonateBombsPayload.ID, payload, com.examplebett.network.DetonateBombsPayload::write);
        this.onClose();
        return true;
    }

    private static final int MUSIC_BUTTON_HEIGHT = 16;
    private static final int MUSIC_PINK = 0xFFF5354F;
    private static final int MUSIC_PINK_HOVER = 0xFFFF5A70;

    private Component musicStatus() {
        com.examplebett.client.sound.ChorusMusicTracks.Track current = com.examplebett.client.sound.ChorusMusicPlayer.current();
        return current == null ? ModText.translatable("gui.mineturismo.smart_dispatch.music_stopped")
                : ModText.translatable("gui.mineturismo.smart_dispatch.music_now_playing", current.title());
    }

    private int[][] musicRects(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        int browseTop = top + 34;
        int volumeTop = browseTop + MUSIC_BUTTON_HEIGHT + 22;
        int rowTop = volumeTop + 18;
        int half = (x1 - x0 - 4) / 2;
        return new int[][]{
                {x0, browseTop, x1, browseTop + MUSIC_BUTTON_HEIGHT},
                {x0, volumeTop, x1, volumeTop + 8},
                {x0, rowTop, x0 + half, rowTop + MUSIC_BUTTON_HEIGHT},
                {x1 - half, rowTop, x1, rowTop + MUSIC_BUTTON_HEIGHT}
        };
    }

    private double musicVolume() {
        return this.minecraft.options.getSoundSourceVolume(net.minecraft.sounds.SoundSource.MUSIC);
    }

    private void setMusicVolume(double volume) {
        this.minecraft.options.setSoundCategoryVolume(net.minecraft.sounds.SoundSource.MUSIC, (float) volume);
    }

    private void drawMusicButton(GuiGraphics g, int[] r, String label, int color, int hoverColor, int textColor, int mouseX, int mouseY) {
        boolean hover = this.pending == null && inside(mouseX, mouseY, r[0], r[1], r[2], r[3]);
        fillPixelRounded(g, r[0], r[1], r[2], r[3], 1, hover ? hoverColor : color);
        String fitted = this.fit(label, r[2] - r[0] - 4);
        int w = Math.round(this.font.width(fitted) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, fitted, (r[0] + r[2] - w) / 2, (r[1] + r[3]) / 2 - 3, textColor);
    }

    private void drawMusicHome(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFFFFF5F7);

        String status = this.fit(this.musicStatus().getString(), l.sx1() - l.sx0() - 22);
        int statusWidth = Math.round(this.font.width(status) * SMALL_TEXT_SCALE);
        int noteX = cx - (statusWidth + 10) / 2;
        int noteY = top + 10;
        g.fill(noteX + 3, noteY, noteX + 4, noteY + 6, MUSIC_PINK);
        g.fill(noteX + 4, noteY, noteX + 6, noteY + 1, MUSIC_PINK);
        g.fill(noteX + 1, noteY + 5, noteX + 4, noteY + 7, MUSIC_PINK);
        this.drawSmallText(g, status, noteX + 10, noteY + 1, 0xFF3A2A2E);

        int[][] r = this.musicRects(l);
        this.drawMusicButton(g, r[0], ModText.translatable("gui.mineturismo.smart_dispatch.music_browse").getString(),
                MUSIC_PINK, MUSIC_PINK_HOVER, 0xFFFFFFFF, mouseX, mouseY);

        double volume = this.musicVolume();
        String volumeLabel = ModText.translatable("soundCategory.music").getString() + ": " + Math.round(volume * 100) + "%";
        this.drawSmallText(g, volumeLabel, r[1][0], r[1][1] - 9, 0xFF3A2A2E);
        int[] bar = r[1];
        int trackTop = (bar[1] + bar[3]) / 2 - 1;
        g.fill(bar[0], trackTop, bar[2], trackTop + 3, 0xFFE2C6CC);
        int knobX = bar[0] + (int) Math.round((bar[2] - bar[0] - 6) * volume);
        g.fill(bar[0], trackTop, knobX + 3, trackTop + 3, MUSIC_PINK);
        fillPixelRounded(g, knobX, bar[1], knobX + 6, bar[3], 1, this.draggingVolume ? MUSIC_PINK_HOVER : 0xFFFFFFFF);
        g.fill(knobX + 2, bar[1] + 2, knobX + 4, bar[3] - 2, MUSIC_PINK);

        Component loopState = ModText.translatable(com.examplebett.client.sound.ChorusMusicPlayer.isLoop() ? "options.on" : "options.off");
        boolean loopOn = com.examplebett.client.sound.ChorusMusicPlayer.isLoop();
        this.drawMusicButton(g, r[2], ModText.translatable("gui.mineturismo.smart_dispatch.music_loop", loopState).getString(),
                loopOn ? MUSIC_PINK : 0xFFDCC9CD, loopOn ? MUSIC_PINK_HOVER : 0xFFE8D8DB, loopOn ? 0xFFFFFFFF : 0xFF3A2A2E, mouseX, mouseY);
        this.drawMusicButton(g, r[3], ModText.translatable("gui.mineturismo.smart_dispatch.music_stop").getString(),
                0xFF3A2A2E, 0xFF54444A, 0xFFFFFFFF, mouseX, mouseY);
    }

    private void setVolumeFromMouse(Layout l, double mouseX) {
        int[] bar = this.musicRects(l)[1];
        double t = (mouseX - bar[0] - 3) / Math.max(1.0, bar[2] - bar[0] - 6);
        this.setMusicVolume(Math.max(0.0, Math.min(1.0, t)));
    }

    private boolean handleMusicHomeClick(Layout l, double mouseX, double mouseY) {
        int[][] r = this.musicRects(l);
        if (inside(mouseX, mouseY, r[0][0], r[0][1], r[0][2], r[0][3])) {
            this.musicBrowsing = true;
            this.scrollRow = 0;
            return true;
        }
        if (inside(mouseX, mouseY, r[1][0] - 2, r[1][1] - 3, r[1][2] + 2, r[1][3] + 3)) {
            this.draggingVolume = true;
            this.setVolumeFromMouse(l, mouseX);
            return true;
        }
        if (inside(mouseX, mouseY, r[2][0], r[2][1], r[2][2], r[2][3])) {
            com.examplebett.client.sound.ChorusMusicPlayer.toggleLoop();
            return true;
        }
        if (inside(mouseX, mouseY, r[3][0], r[3][1], r[3][2], r[3][3])) {
            com.examplebett.client.sound.ChorusMusicPlayer.stop();
            return true;
        }
        return false;
    }

    private static final String[] TIME_KEYS = {"time_morning", "time_noon", "time_evening", "time_night", "time_midnight"};
    private static final float CLOCK_TIME_SCALE = 3.0f;

    private int clockButtonTop(Layout l, int index) {

        int changeTop = l.sy1() - WEATHER_BUTTON_HEIGHT - 5;
        return index == TIME_KEYS.length ? changeTop
                : changeTop - (TIME_KEYS.length - index) * (WEATHER_BUTTON_HEIGHT + WEATHER_BUTTON_GAP);
    }

    private void drawClock(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        long dayTime = this.minecraft.level == null ? 0L : this.levelDayTime();
        int hours = (int) ((Math.floorMod(dayTime, 24000L) / 1000L + 6L) % 24L);

        int skyTop;
        int skyBottom;
        if (hours >= 6 && hours < 17) {
            skyTop = 0xFF6FB8F5;
            skyBottom = 0xFF3D8BD9;
        } else if (hours >= 17 && hours < 19) {
            skyTop = 0xFFF7A35C;
            skyBottom = 0xFFB4466A;
        } else {
            skyTop = 0xFF1E2A4A;
            skyBottom = 0xFF0B1022;
        }
        int bands = Math.max(1, (l.sy1() - top) / (PX * 2));
        for (int i = 0; i < bands; i++) {
            int bandTop = top + i * PX * 2;
            g.fill(l.sx0(), bandTop, l.sx1(), i == bands - 1 ? l.sy1() : bandTop + PX * 2, lerpColor(skyTop, skyBottom, (float) i / bands));
        }
        int cx = (l.sx0() + l.sx1()) / 2;

        String placeLabel = ModText.translatable("gui.mineturismo.smart_dispatch.weather_location").getString();
        int labelWidth = Math.round(this.font.width(placeLabel) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, placeLabel, cx - labelWidth / 2, top + 8, 0xDDFFFFFF);
        String place = this.currentPlaceName().getString();
        int room = l.sx1() - l.sx0() - 8;
        if (this.font.width(place) <= room) {
            g.drawString(this.font, place, cx - this.font.width(place) / 2, top + 17, 0xFFFFFFFF, true);
        } else {
            String fitted = this.fit(place, room);
            this.drawSmallText(g, fitted, cx - Math.round(this.font.width(fitted) * SMALL_TEXT_SCALE) / 2, top + 18, 0xFFFFFFFF);
        }

        String time = this.currentTimeText();
        int timeWidth = Math.round(this.font.width(time) * CLOCK_TIME_SCALE);
        int timeTop = top + 32;
        g.pose().pushPose();
        g.pose().scale(CLOCK_TIME_SCALE, CLOCK_TIME_SCALE, 1.0f);
        g.drawString(this.font, time, Math.round((cx - timeWidth / 2f) / CLOCK_TIME_SCALE), Math.round(timeTop / CLOCK_TIME_SCALE), 0xFFFFFFFF, true);
        g.pose().popPose();

        String day = ModText.translatable("gui.mineturismo.smart_dispatch.clock_day", dayTime / 24000L + 1).getString();
        int dayWidth = Math.round(this.font.width(day) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, day, cx - dayWidth / 2, timeTop + Math.round(this.font.lineHeight * CLOCK_TIME_SCALE) + 2, 0xDDFFFFFF);

        if (this.clockPickerOpen) {
            for (int i = 0; i < TIME_KEYS.length; i++) {
                String label = ModText.translatable("gui.mineturismo.smart_dispatch." + TIME_KEYS[i]).getString();
                this.drawWeatherButton(g, l, this.clockButtonTop(l, i), label, 0xFFFFFFFF, 0xFFFFF1B8, 0xFF202024, mouseX, mouseY);
            }
        }
        String change = ModText.translatable("gui.mineturismo.smart_dispatch.time_change").getString();
        this.drawWeatherButton(g, l, this.clockButtonTop(l, TIME_KEYS.length), change, 0xFF2A2A30, 0xFF44444E, 0xFFFFFFFF, mouseX, mouseY);
    }

    private boolean handleClockClick(Layout l, double mouseX, double mouseY) {
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        int changeTop = this.clockButtonTop(l, TIME_KEYS.length);
        if (inside(mouseX, mouseY, x0, changeTop, x1, changeTop + WEATHER_BUTTON_HEIGHT)) {
            this.clockPickerOpen = !this.clockPickerOpen;
            return true;
        }
        if (this.clockPickerOpen) {
            for (int i = 0; i < TIME_KEYS.length; i++) {
                int top = this.clockButtonTop(l, i);
                if (inside(mouseX, mouseY, x0, top, x1, top + WEATHER_BUTTON_HEIGHT)) {
                    com.examplebett.network.SetAppTimePayload payload = new com.examplebett.network.SetAppTimePayload(i);
                    ModClientNetworking.sendToServer(com.examplebett.network.SetAppTimePayload.ID, payload, com.examplebett.network.SetAppTimePayload::write);
                    this.clockPickerOpen = false;
                    return true;
                }
            }
        }
        return false;
    }

    private static final int MASSIVE_BUTTON_HEIGHT = 32;

    private static final int MASSIVE_BUTTON_TEXT_Y = 12;
    private static final int MASSIVE_BLUE = 0xFF1E4FA8;
    private static final int MASSIVE_BLUE_HOVER = 0xFF2D63C8;
    private static final int MASSIVE_YELLOW = 0xFFF2B705;

    private int[][] massiveAutoRects(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        int first = top + 30;
        int second = first + MASSIVE_BUTTON_HEIGHT + 10;
        return new int[][]{{x0, first, x1, first + MASSIVE_BUTTON_HEIGHT}, {x0, second, x1, second + MASSIVE_BUTTON_HEIGHT}};
    }

    private void drawMassiveAuto(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFFF4F1EA);
        g.fill(l.sx0(), top, l.sx1(), top + 2, MASSIVE_YELLOW);
        String lead = this.fit(ModText.translatable("gui.mineturismo.massive_auto.lead").getString(), l.sx1() - l.sx0() - 8);
        this.drawSmallText(g, lead, cx - Math.round(this.font.width(lead) * SMALL_TEXT_SCALE) / 2, top + 12, 0xFF3A3A40);
        int[][] r = this.massiveAutoRects(l);

        boolean buyHover = this.pending == null && inside(mouseX, mouseY, r[0][0], r[0][1], r[0][2], r[0][3]);
        fillPixelRounded(g, r[0][0], r[0][1] + 2, r[0][2], r[0][3] + 2, PX, 0xFFB08300);
        fillPixelRounded(g, r[0][0], r[0][1], r[0][2], r[0][3], PX, buyHover ? 0xFFFFC72C : MASSIVE_YELLOW);
        String buy = this.fit(ModText.translatable("gui.mineturismo.massive_auto.buy").getString(), r[0][2] - r[0][0] - 6);
        g.drawString(this.font, buy, cx - this.font.width(buy) / 2, r[0][1] + MASSIVE_BUTTON_TEXT_Y, 0xFF12306A, false);

        boolean hover = this.pending == null && inside(mouseX, mouseY, r[1][0], r[1][1], r[1][2], r[1][3]);
        fillPixelRounded(g, r[1][0], r[1][1] + 2, r[1][2], r[1][3] + 2, PX, 0xFF12306A);
        fillPixelRounded(g, r[1][0], r[1][1], r[1][2], r[1][3], PX, hover ? MASSIVE_BLUE_HOVER : MASSIVE_BLUE);
        String appraisal = this.fit(ModText.translatable("gui.mineturismo.massive_auto.appraisal").getString(), r[1][2] - r[1][0] - 6);
        g.drawString(this.font, appraisal, cx - this.font.width(appraisal) / 2, r[1][1] + MASSIVE_BUTTON_TEXT_Y, 0xFFFFFFFF, false);
    }

    private boolean handleMassiveAutoClick(Layout l, double mouseX, double mouseY) {
        int[][] r = this.massiveAutoRects(l);
        if (inside(mouseX, mouseY, r[0][0], r[0][1], r[0][2], r[0][3])) {
            this.minecraft.setScreen(new MassiveAutoUsedCarScreen());
            return true;
        }
        if (inside(mouseX, mouseY, r[1][0], r[1][1], r[1][2], r[1][3])) {

            com.examplebett.network.OpenMassiveAutoAppraisalPayload payload = new com.examplebett.network.OpenMassiveAutoAppraisalPayload();
            ModClientNetworking.sendToServer(com.examplebett.network.OpenMassiveAutoAppraisalPayload.ID, payload, com.examplebett.network.OpenMassiveAutoAppraisalPayload::write);
            return true;
        }
        return false;
    }

    private static final int SLOT_REELS = SmartDispatchAppItem.SLOT_REELS;
    private static final int SLOT_CELL_HEIGHT = 30;
    private static final int SLOT_WINDOW_HEIGHT = SLOT_CELL_HEIGHT * 2 + 6;
    private static final int SLOT_REEL_GAP = 3;
    private static final int SLOT_SPIN_BUTTON_HEIGHT = 18;
    private static final int SLOT_AUTO_BUTTON_HEIGHT = 13;

    private static final double SLOT_MAX_SPEED = 14.0;
    private static final double SLOT_ACCEL_SEC = 0.25;

    private static final double SLOT_MIN_SPIN_SEC = 0.8;
    private static final double SLOT_DECEL_SEC = 0.9;

    private static final double SLOT_STOP_INTERVAL_SEC = 0.45;

    private static final double SLOT_BLUR_SPEED = 5.0;

    private static final long SLOT_TIMEOUT_MS = 4000L;

    private static final long SLOT_AUTO_DELAY_MS = 900L;
    private static final long SLOT_AUTO_DELAY_JACKPOT_MS = 2600L;
    private static final long SLOT_TICK_MIN_INTERVAL_MS = 35L;
    private static final int SLOT_PINK = 0xFFE6197E;
    private static final int SLOT_ORANGE = 0xFFF7931E;
    private static final int SLOT_GOLD = 0xFFFFD54F;
    private static final int[] SLOT_DECOY_EMERALDS = {2, 3, 5, 8, 10, 15, 20, 30, 50};

    private enum SlotPhase { IDLE, SPINNING, STOPPING, RESULT }

    private SlotPhase slotPhase = SlotPhase.IDLE;

    private final double[] reelRest = new double[SLOT_REELS];

    private final double[] reelBase = new double[SLOT_REELS];
    private final double[] reelDecelFrom = new double[SLOT_REELS];
    private final int[] reelTarget = new int[SLOT_REELS];
    private final long[] reelDecelStartMs = new long[SLOT_REELS];
    private final boolean[] reelStopSoundPlayed = new boolean[SLOT_REELS];

    private final List<java.util.Map<Integer, SmartDispatchAppItem.SlotPrize>> reelResults = new ArrayList<>();
    private final java.util.Map<String, ItemStack> slotStackCache = new java.util.HashMap<>();
    private long slotSpinStartMs;
    private long slotResultShownMs;

    private List<SmartDispatchAppItem.SlotPrize> slotResult = null;
    private boolean slotAuto = false;
    private Component slotMessage = null;
    private long slotLastTickMs;
    private long slotLastTickCell = Long.MIN_VALUE;

    private int slotFrozenEmeralds = -1;

    private int slotEmeraldsGained = 0;
    private static final long SLOT_GAIN_POPUP_MS = 1500L;

    {
        for (int i = 0; i < SLOT_REELS; i++) {
            this.reelResults.add(new java.util.HashMap<>());
        }
    }

    public static void onSlotResult(List<SmartDispatchAppItem.SlotPrize> prizes) {
        if (net.minecraft.client.Minecraft.getInstance().screen instanceof SmartDispatchAppScreen screen) {
            screen.receiveSlotResult(prizes);
        }
    }

    private void receiveSlotResult(List<SmartDispatchAppItem.SlotPrize> prizes) {
        if (this.slotPhase != SlotPhase.SPINNING) {
            return;
        }
        long now = System.currentTimeMillis();
        long firstDecel = Math.max(now, this.slotSpinStartMs + (long) (SLOT_MIN_SPIN_SEC * 1000));
        for (int i = 0; i < SLOT_REELS; i++) {
            long decelStart = firstDecel + (long) (i * SLOT_STOP_INTERVAL_SEC * 1000);
            double from = this.spinPosition(i, decelStart);

            int target = (int) Math.ceil(from + SLOT_MAX_SPEED * SLOT_DECEL_SEC / 3.0);
            this.reelDecelStartMs[i] = decelStart;
            this.reelDecelFrom[i] = from;
            this.reelTarget[i] = target;
            if (i < prizes.size()) {
                this.reelResults.get(i).put(target, prizes.get(i));
            }
        }
        this.slotResult = prizes;
        this.slotPhase = SlotPhase.STOPPING;
    }

    private int emeraldCount() {
        return this.minecraft == null || this.minecraft.player == null ? 0
                : ModPlayer.getInventory(this.minecraft.player).countItem(Items.EMERALD);
    }

    private void playUiSound(net.minecraft.sounds.SoundEvent sound, float pitch, float volume) {
        this.minecraft.getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(sound, pitch, volume));
    }

    private void startSlotSpin() {
        if (this.slotPhase == SlotPhase.SPINNING || this.slotPhase == SlotPhase.STOPPING) {
            return;
        }
        if (!this.isFree() && this.emeraldCount() < SmartDispatchAppItem.SLOT_COST) {
            this.slotMessage = ModText.translatable("gui.mineturismo.smart_dispatch.not_enough_emeralds", SmartDispatchAppItem.SLOT_COST);
            this.slotAuto = false;
            return;
        }
        com.examplebett.network.RequestSlotSpinPayload payload = new com.examplebett.network.RequestSlotSpinPayload();
        ModClientNetworking.sendToServer(com.examplebett.network.RequestSlotSpinPayload.ID, payload, com.examplebett.network.RequestSlotSpinPayload::write);
        this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_START, 1.0f, 0.8f);
        this.slotPhase = SlotPhase.SPINNING;
        this.slotSpinStartMs = System.currentTimeMillis();
        this.slotResult = null;
        this.slotMessage = null;

        this.slotFrozenEmeralds = this.emeraldCount() - (this.isFree() ? 0 : SmartDispatchAppItem.SLOT_COST);
        for (int i = 0; i < SLOT_REELS; i++) {
            this.reelBase[i] = this.reelRest[i];
            this.reelStopSoundPlayed[i] = false;
        }
    }

    private void skipSlotSpin() {
        if (this.slotPhase != SlotPhase.STOPPING) {
            return;
        }
        long now = System.currentTimeMillis();
        for (int i = 0; i < SLOT_REELS; i++) {
            this.reelDecelStartMs[i] = Math.min(this.reelDecelStartMs[i], now - (long) (SLOT_DECEL_SEC * 1000));
        }
    }

    private double spinPosition(int reel, long time) {
        double t = Math.max(0, time - this.slotSpinStartMs) / 1000.0;
        double distance = t < SLOT_ACCEL_SEC ? SLOT_MAX_SPEED * t * t / (2 * SLOT_ACCEL_SEC) : SLOT_MAX_SPEED * (t - SLOT_ACCEL_SEC / 2);
        return this.reelBase[reel] + distance;
    }

    private double reelPosition(int reel, long time) {
        if (this.slotPhase == SlotPhase.IDLE || this.slotPhase == SlotPhase.RESULT) {
            return this.reelRest[reel];
        }
        if (this.slotPhase == SlotPhase.SPINNING || time < this.reelDecelStartMs[reel]) {
            return this.spinPosition(reel, time);
        }
        double u = (time - this.reelDecelStartMs[reel]) / (SLOT_DECEL_SEC * 1000);
        if (u >= 1) {
            return this.reelTarget[reel];
        }
        double e = 1 - u;
        return this.reelDecelFrom[reel] + (this.reelTarget[reel] - this.reelDecelFrom[reel]) * (1 - e * e * e);
    }

    private boolean reelStopped(int reel, long time) {
        return this.slotPhase == SlotPhase.STOPPING && time >= this.reelDecelStartMs[reel] + (long) (SLOT_DECEL_SEC * 1000);
    }

    private void updateSlot(long now) {
        if (this.slotPhase == SlotPhase.SPINNING && now - this.slotSpinStartMs > SLOT_TIMEOUT_MS) {
            this.receiveSlotResult(List.of());
        }
        if (this.slotPhase == SlotPhase.SPINNING || this.slotPhase == SlotPhase.STOPPING) {

            boolean stopPlayed = false;
            int tickReel = -1;
            for (int i = 0; i < SLOT_REELS; i++) {
                if (this.reelStopped(i, now)) {
                    if (!this.reelStopSoundPlayed[i]) {
                        this.reelStopSoundPlayed[i] = true;
                        if (!stopPlayed) {
                            this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_STOP, 1.0f + i * 0.08f, 0.9f);
                            stopPlayed = true;
                        }
                    }
                } else {
                    tickReel = i;
                }
            }

            if (tickReel >= 0) {
                long cell = (long) Math.floor(this.reelPosition(tickReel, now));
                if (cell != this.slotLastTickCell && now - this.slotLastTickMs >= SLOT_TICK_MIN_INTERVAL_MS) {
                    this.slotLastTickCell = cell;
                    this.slotLastTickMs = now;
                    this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_TICK, 0.95f + (float) Math.random() * 0.1f, 0.45f);
                }
            } else if (this.slotPhase == SlotPhase.STOPPING) {
                this.finishSlotSpin(now);
            }
        }
        if (this.slotPhase == SlotPhase.RESULT && this.slotAuto && this.pending == null) {
            long delay = this.slotHasJackpot() ? SLOT_AUTO_DELAY_JACKPOT_MS : SLOT_AUTO_DELAY_MS;
            if (now - this.slotResultShownMs >= delay) {
                this.startSlotSpin();
            }
        }
    }

    private void finishSlotSpin(long now) {
        for (int i = 0; i < SLOT_REELS; i++) {
            this.reelRest[i] = this.reelTarget[i];
        }
        this.slotResultShownMs = now;
        this.slotFrozenEmeralds = -1;
        this.slotEmeraldsGained = 0;
        if (this.slotResult != null) {
            for (SmartDispatchAppItem.SlotPrize prize : this.slotResult) {
                if (prize.kind() == SmartDispatchAppItem.PRIZE_EMERALD) {
                    this.slotEmeraldsGained += prize.amount();
                }
            }
        }
        if (this.slotResult == null || this.slotResult.isEmpty()) {

            this.slotPhase = SlotPhase.IDLE;
            this.slotAuto = false;
            this.slotMessage = ModText.translatable("gui.mineturismo.smart_dispatch.slot_failed");
            return;
        }
        this.slotPhase = SlotPhase.RESULT;
        if (this.slotHasJackpot()) {
            this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_JACKPOT, 1.0f, 1.0f);
        } else if (this.slotResult.stream().anyMatch(SmartDispatchAppItem.SlotPrize::isWin)) {
            this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_WIN, 1.0f, 0.9f);
        } else {
            this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_LOSE, 1.0f, 0.7f);
        }
    }

    private boolean slotHasJackpot() {
        return this.slotResult != null && this.slotResult.stream().anyMatch(SmartDispatchAppItem.SlotPrize::isJackpot);
    }

    private SmartDispatchAppItem.SlotPrize slotCell(int reel, long cell) {
        SmartDispatchAppItem.SlotPrize result = cell == (int) cell ? this.reelResults.get(reel).get((int) cell) : null;
        if (result != null) {
            return result;
        }
        long h = cell * 0x9E3779B97F4A7C15L + (reel + 1) * 0xC2B2AE3D27D4EB4FL;
        h ^= h >>> 31;
        h *= 0xBF58476D1CE4E5B9L;
        h ^= h >>> 29;
        int r = (int) Math.floorMod(h, 100L);
        int r2 = (int) Math.floorMod(h >>> 17, 1000L);

        if (r < 18) {
            return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_WARDEN_ENERGY, 1, "");
        }
        if (r < 35) {
            return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_CHOCO_BAR, 1, "");
        }
        if (r < 55) {
            return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_EMERALD, 1, "");
        }
        if (r < 85) {
            return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_EMERALD, SLOT_DECOY_EMERALDS[r2 % SLOT_DECOY_EMERALDS.length], "");
        }
        String modelId = SmartDispatchAppItem.SLOT_VEHICLES[r2 % SmartDispatchAppItem.SLOT_VEHICLES.length];
        return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_VEHICLE, 1, modelId);
    }

    private ItemStack slotStack(SmartDispatchAppItem.SlotPrize prize) {
        return this.slotStackCache.computeIfAbsent(prize.kind() + ":" + prize.amount() + ":" + prize.modelId(), key -> prize.toStack());
    }

    private static int slotPrizeColor(SmartDispatchAppItem.SlotPrize prize) {
        if (prize.kind() == SmartDispatchAppItem.PRIZE_VEHICLE) {
            return 0xFFFFA000;
        }
        if (prize.kind() == SmartDispatchAppItem.PRIZE_EMERALD) {
            return prize.amount() >= 30 ? 0xFFE53935 : prize.amount() >= 10 ? 0xFFAB47BC : prize.amount() >= 2 ? 0xFF29B6F6 : 0xFF66BB6A;
        }
        if (prize.kind() == SmartDispatchAppItem.PRIZE_CHOCO_BAR) {
            return 0xFFA1887F;
        }
        return 0xFF9CCC65;
    }

    private String slotPrizeLabel(SmartDispatchAppItem.SlotPrize prize) {
        if (prize.kind() == SmartDispatchAppItem.PRIZE_VEHICLE) {
            ItemStack stack = this.slotStack(prize);
            return stack.isEmpty() ? prize.modelId() : stack.getHoverName().getString();
        }
        if (prize.kind() == SmartDispatchAppItem.PRIZE_EMERALD) {
            return "x" + prize.amount();
        }
        if (prize.kind() == SmartDispatchAppItem.PRIZE_CHOCO_BAR) {
            return ModText.translatable("gui.mineturismo.smart_dispatch.slot_choco_short").getString();
        }
        return ModText.translatable("gui.mineturismo.smart_dispatch.slot_energy_short").getString();
    }

    private void drawFittedCentered(GuiGraphics g, String text, int cx, int y, int maxWidth, int color) {
        float scale = Math.min(SMALL_TEXT_SCALE, (float) maxWidth / Math.max(1, this.font.width(text)));
        int w = Math.round(this.font.width(text) * scale);
        this.drawScaledText(g, (net.minecraft.network.chat.FormattedText.of(text)),
                cx - w / 2, y, color, scale);
    }

    private int[][] slotRects(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT + COST_BAR_HEIGHT + 8;
        int autoTop = l.sy1() - 5 - SLOT_AUTO_BUTTON_HEIGHT;
        int spinTop = autoTop - 4 - SLOT_SPIN_BUTTON_HEIGHT;
        return new int[][]{
                {l.sx0() + 6, top, l.sx1() - 6, top + SLOT_WINDOW_HEIGHT},
                {l.sx0() + 8, spinTop, l.sx1() - 8, spinTop + SLOT_SPIN_BUTTON_HEIGHT},
                {l.sx0() + 8, autoTop, l.sx1() - 8, autoTop + SLOT_AUTO_BUTTON_HEIGHT}
        };
    }

    private void drawSlot(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        long now = System.currentTimeMillis();
        this.updateSlot(now);
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;

        boolean flash = this.slotPhase == SlotPhase.RESULT && this.slotResult.stream().anyMatch(SmartDispatchAppItem.SlotPrize::isWin)
                && now - this.slotResultShownMs < 1500 && (now / 150) % 2 == 0;
        int bands = Math.max(1, (l.sy1() - top) / (PX * 2));
        for (int i = 0; i < bands; i++) {
            int bandTop = top + i * PX * 2;
            int color = lerpColor(SLOT_PINK, SLOT_ORANGE, (float) i / bands);
            g.fill(l.sx0(), bandTop, l.sx1(), i == bands - 1 ? l.sy1() : bandTop + PX * 2, flash ? lerpColor(color, 0xFFFFFFFF, 0.25f) : color);
        }

        g.fill(l.sx0(), top, l.sx1(), top + COST_BAR_HEIGHT, COLOR_COST_BAR);
        Component cost = this.isFree() ? ModText.translatable("gui.mineturismo.smart_dispatch.free")
                : ModText.translatable("gui.mineturismo.smart_dispatch.slot_cost", SmartDispatchAppItem.SLOT_COST);
        this.drawSmallText(g, cost.getString(), l.sx0() + 3, top + 2, 0xFFE8E8E8);
        int shownEmeralds = this.slotFrozenEmeralds >= 0 ? this.slotFrozenEmeralds : this.emeraldCount();
        String owned = ModText.translatable("gui.mineturismo.smart_dispatch.slot_owned", shownEmeralds).getString();
        int ownedX = l.sx1() - 3 - Math.round(this.font.width(owned) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, owned, ownedX, top + 2, 0xFF7CF29A);

        if (this.slotPhase == SlotPhase.RESULT && this.slotEmeraldsGained > 0 && now - this.slotResultShownMs < SLOT_GAIN_POPUP_MS) {
            String gained = "+" + this.slotEmeraldsGained;
            this.drawSmallText(g, gained, ownedX - 3 - Math.round(this.font.width(gained) * SMALL_TEXT_SCALE), top + 2, SLOT_GOLD);
        }

        int[][] r = this.slotRects(l);
        int[] window = r[0];
        fillPixelRounded(g, window[0] - 3, window[1] - 3, window[2] + 3, window[3] + 3, 1, 0xFF2A1030);
        int reelWidth = (window[2] - window[0] - SLOT_REEL_GAP * (SLOT_REELS - 1)) / SLOT_REELS;
        for (int i = 0; i < SLOT_REELS; i++) {
            int rx0 = window[0] + i * (reelWidth + SLOT_REEL_GAP);
            this.drawReel(g, i, rx0, window[1], rx0 + reelWidth, window[3], now);
        }

        int midY = (window[1] + window[3]) / 2;
        for (int i = 0; i < 3; i++) {
            g.fill(window[0] - 3 + i, midY - 3 + i, window[0] - 2 + i, midY + 4 - i, 0xFFFFFFFF);
            g.fill(window[2] + 2 - i, midY - 3 + i, window[2] + 3 - i, midY + 4 - i, 0xFFFFFFFF);
        }

        int textTop = window[3] + 8;
        int room = l.sx1() - l.sx0() - 8;
        if (this.slotPhase == SlotPhase.RESULT) {
            boolean jackpot = this.slotHasJackpot();
            boolean win = jackpot || this.slotResult.stream().anyMatch(SmartDispatchAppItem.SlotPrize::isWin);
            String headline = ModText.translatable(jackpot ?"gui.mineturismo.smart_dispatch.slot_jackpot"
                    : win ? "gui.mineturismo.smart_dispatch.slot_win" : "gui.mineturismo.smart_dispatch.slot_lose").getString();
            g.drawString(this.font, headline, cx - this.font.width(headline) / 2, textTop, win ? SLOT_GOLD : 0xFFFFFFFF, true);
            int emeralds = 0;
            for (SmartDispatchAppItem.SlotPrize prize : this.slotResult) {
                if (prize.kind() == SmartDispatchAppItem.PRIZE_EMERALD) {
                    emeralds += prize.amount();
                }
            }
            if (emeralds > 0) {
                this.drawFittedCentered(g, ModText.translatable("gui.mineturismo.smart_dispatch.slot_emeralds_won", emeralds).getString(),
                        cx, textTop + 11, room, 0xFFFFFFFF);
            }
        } else if (this.slotMessage != null && this.slotPhase == SlotPhase.IDLE) {
            List<net.minecraft.network.chat.FormattedText> lines = this.font.split(this.slotMessage, Math.round(room / SMALL_TEXT_SCALE));
            for (int i = 0; i < Math.min(2, lines.size()); i++) {
                int w = Math.round(this.font.width(lines.get(i)) * SMALL_TEXT_SCALE);
                this.drawSmallText(g, lines.get(i), cx - w / 2, textTop + i * 8, 0xFFFFE0E0);
            }
        } else if (this.slotPhase == SlotPhase.IDLE) {
            this.drawFittedCentered(g, ModText.translatable("gui.mineturismo.smart_dispatch.slot_hint").getString(), cx, textTop + 2, room, 0xFFFFFFFF);
        }

        int[] spin = r[1];
        boolean waiting = this.slotPhase == SlotPhase.SPINNING;
        boolean skipping = this.slotPhase == SlotPhase.STOPPING;
        boolean affordable = this.isFree() || this.emeraldCount() >= SmartDispatchAppItem.SLOT_COST;
        boolean spinEnabled = skipping || (!waiting && affordable);
        boolean spinHover = spinEnabled && inside(mouseX, mouseY, spin[0], spin[1], spin[2], spin[3]);
        fillPixelRounded(g, spin[0], spin[1] + 2, spin[2], spin[3] + 2, PX, spinEnabled ? 0xFFB07800 : 0xFF6E6E74);
        fillPixelRounded(g, spin[0], spin[1], spin[2], spin[3], PX, !spinEnabled ? 0xFF9A9AA2 : spinHover ? 0xFFFFE066 : 0xFFFFC928);
        String spinLabel = ModText.translatable(skipping ? "gui.mineturismo.smart_dispatch.slot_skip" : "gui.mineturismo.smart_dispatch.slot_spin").getString();
        g.drawString(this.font, spinLabel, cx - this.font.width(spinLabel) / 2, spin[1] + 5, spinEnabled ? 0xFF3A2000 : 0xFFDADADF, false);

        int[] auto = r[2];
        boolean autoHover = inside(mouseX, mouseY, auto[0], auto[1], auto[2], auto[3]);
        fillPixelRounded(g, auto[0], auto[1], auto[2], auto[3], 1,
                this.slotAuto ? (autoHover ? 0xFF4FC562 : COLOR_BUTTON_YES) : (autoHover ? 0xFF54444A : 0xFF3A2A2E));
        String autoLabel = ModText.translatable("gui.mineturismo.smart_dispatch.slot_auto",
                ModText.translatable(this.slotAuto ? "options.on" : "options.off")).getString();
        int autoWidth = Math.round(this.font.width(autoLabel) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, autoLabel, cx - autoWidth / 2, auto[1] + 4, 0xFFFFFFFF);
    }

    private void drawReel(GuiGraphics g, int reel, int x0, int y0, int x1, int y1, long now) {
        g.fill(x0, y0, x1, y1, 0xFF1A0A1E);
        double pos = this.reelPosition(reel, now);
        double speed = Math.abs(pos - this.reelPosition(reel, now - 16)) / 0.016;
        boolean blur = speed > SLOT_BLUR_SPEED;

        g.enableScissor(x0, y0, x1, y1);
        boolean scissor = true;
        int centerY = (y0 + y1) / 2;
        long base = (long) Math.floor(pos);
        for (long cell = base - 2; cell <= base + 2; cell++) {
            int cellTop = (int) Math.round(centerY - SLOT_CELL_HEIGHT / 2.0 + (pos - cell) * SLOT_CELL_HEIGHT);
            this.drawSlotCell(g, this.slotCell(reel, cell), x0 + 1, cellTop + 1, x1 - 1, cellTop + SLOT_CELL_HEIGHT - 1, y0, y1, blur, scissor);
        }

        if (this.slotPhase == SlotPhase.RESULT && reel < this.slotResult.size() && this.slotResult.get(reel).isWin() && (now / 250) % 2 == 0) {
            int cellTop = centerY - SLOT_CELL_HEIGHT / 2;
            int cellBottom = cellTop + SLOT_CELL_HEIGHT;
            g.fill(x0, cellTop, x1, cellTop + 2, SLOT_GOLD);
            g.fill(x0, cellBottom - 2, x1, cellBottom, SLOT_GOLD);
            g.fill(x0, cellTop, x0 + 2, cellBottom, SLOT_GOLD);
            g.fill(x1 - 2, cellTop, x1, cellBottom, SLOT_GOLD);
        }

        g.fill(x0, y0, x1, y0 + 4, 0x66000000);
        g.fill(x0, y1 - 4, x1, y1, 0x66000000);
        g.disableScissor();
    }

    private static void fillClipped(GuiGraphics g, int x0, int y0, int x1, int y1, int clipTop, int clipBottom, int color) {
        int top = Math.max(y0, clipTop);
        int bottom = Math.min(y1, clipBottom);
        if (bottom > top) {
            g.fill(x0, top, x1, bottom, color);
        }
    }

    private void drawSlotCell(GuiGraphics g, SmartDispatchAppItem.SlotPrize prize, int x0, int y0, int x1, int y1,
                              int clipTop, int clipBottom, boolean blur, boolean scissor) {
        if (y1 <= clipTop || y0 >= clipBottom) {
            return;
        }
        int color = slotPrizeColor(prize);
        if (blur) {

            fillClipped(g, x0, y0, x1, y1, clipTop, clipBottom, lerpColor(color, 0xFFFFFFFF, 0.35f));
            int mid = (x0 + x1) / 2;
            fillClipped(g, mid - 6, y0 + 4, mid + 6, y1 - 4, clipTop, clipBottom, lerpColor(color, 0xFFFFFFFF, 0.6f));
            return;
        }
        fillClipped(g, x0, y0, x1, y1, clipTop, clipBottom, color);
        int stripTop = y1 - 9;
        fillClipped(g, x0 + 1, stripTop, x1 - 1, y1 - 1, clipTop, clipBottom, 0xF2FFFFFF);

        boolean visible = scissor || (y0 >= clipTop && y1 <= clipBottom);
        if (!visible) {
            return;
        }
        int cx = (x0 + x1) / 2;
        g.renderItem(this.slotStack(prize), cx - 8, y0 + 2);
        this.drawFittedCentered(g, this.slotPrizeLabel(prize), cx, stripTop + 2, x1 - x0 - 4, 0xFF202024);
    }

    private boolean handleSlotClick(Layout l, double mouseX, double mouseY) {
        int[][] r = this.slotRects(l);
        if (inside(mouseX, mouseY, r[1][0], r[1][1], r[1][2], r[1][3])) {
            this.pressSlotSpin();
            return true;
        }
        if (inside(mouseX, mouseY, r[2][0], r[2][1], r[2][2], r[2][3])) {
            this.slotAuto = !this.slotAuto;
            if (this.slotAuto && (this.slotPhase == SlotPhase.IDLE || this.slotPhase == SlotPhase.RESULT)) {
                this.startSlotSpin();
            }
            return true;
        }

        if (inside(mouseX, mouseY, r[0][0], r[0][1], r[0][2], r[0][3])) {
            this.pressSlotSpin();
            return true;
        }
        return false;
    }

    private void pressSlotSpin() {
        if (this.slotPhase == SlotPhase.STOPPING) {
            this.skipSlotSpin();
        } else {
            this.startSlotSpin();
        }
    }

    private String fit(String text, int width) {
        int room = Math.round(width / SMALL_TEXT_SCALE);
        if (this.font.width(text) <= room) {
            return text;
        }
        return this.font.plainSubstrByWidth(text, room - this.font.width("...")) + "...";
    }

    private void drawDialog(GuiGraphics g, Layout l, App app, int mouseX, int mouseY) {
        this.aboveItems(g);
        g.fill(l.sx0(), l.sy0(), l.sx1(), l.sy1(), COLOR_DIALOG_SHADE);
        int dx0 = l.sx0() + 6;
        int dx1 = l.sx1() - 6;
        int dy0 = l.dialogTop();
        fillPixelRounded(g, dx0, dy0, dx1, dy0 + DIALOG_HEIGHT, PX, COLOR_DIALOG);

        g.renderItem(this.pending.stack(), dx0 + 6, dy0 + 6);
        this.drawSmallText(g, this.fit(this.pending.name().getString(), dx1 - dx0 - 30), dx0 + 26, dy0 + 11, 0xFF202024);

        boolean enough = this.hasEnoughEmeralds();
        Component question = enough
                ? ModText.translatable(app.vehicles ? "gui.mineturismo.smart_dispatch.confirm_ride" : "gui.mineturismo.smart_dispatch.confirm_buy",
                        costOf(app))
                : ModText.translatable("gui.mineturismo.smart_dispatch.not_enough_emeralds", costOf(app));

        List<net.minecraft.network.chat.FormattedText> lines = this.font.split(question, Math.round((dx1 - dx0 - 10) / SMALL_TEXT_SCALE));
        for (int i = 0; i < Math.min(2, lines.size()); i++) {
            this.drawSmallText(g, lines.get(i), dx0 + 5, dy0 + 28 + i * 8, enough ? 0xFF202024 : 0xFFC62828);
        }
        int by0 = l.buttonTop();
        int bw = l.buttonWidth();
        boolean yesHover = enough && inside(mouseX, mouseY, l.yesX0(), by0, l.yesX0() + bw, by0 + DIALOG_BUTTON_HEIGHT);
        boolean noHover = inside(mouseX, mouseY, l.noX0(), by0, l.noX0() + bw, by0 + DIALOG_BUTTON_HEIGHT);
        fillPixelRounded(g, l.yesX0(), by0, l.yesX0() + bw, by0 + DIALOG_BUTTON_HEIGHT, 1,
                !enough ? COLOR_BUTTON_NO : yesHover ? COLOR_BUTTON_YES_HOVER : COLOR_BUTTON_YES);
        fillPixelRounded(g, l.noX0(), by0, l.noX0() + bw, by0 + DIALOG_BUTTON_HEIGHT, 1, noHover ? COLOR_BUTTON_NO_HOVER : COLOR_BUTTON_NO);
        String yes = ModText.translatable("gui.yes").getString();
        String no = ModText.translatable("gui.no").getString();
        g.drawString(this.font, yes, l.yesX0() + (bw - this.font.width(yes)) / 2, by0 + 3, 0xFFFFFFFF, false);
        g.drawString(this.font, no, l.noX0() + (bw - this.font.width(no)) / 2, by0 + 3, 0xFF202024, false);
    }

    private void aboveItems(GuiGraphics g) {
    }

    private void drawSmallText(GuiGraphics g, String text, int x, int y, int color) {
        this.drawSmallText(g, net.minecraft.network.chat.FormattedText.of(text), x, y, color);
    }

    private void drawSmallText(GuiGraphics g, net.minecraft.network.chat.FormattedText text, int x, int y, int color) {
        this.drawScaledText(g, text, x, y, color, SMALL_TEXT_SCALE);
    }

    private void drawScaledText(GuiGraphics g, net.minecraft.network.chat.FormattedText text, int x, int y, int color, float s) {
        g.pose().pushPose();
        g.pose().scale(s, s, 1.0f);
        g.drawString(this.font, text, Math.round(x / s), Math.round(y / s), color, false);
        g.pose().popPose();
    }

    private void confirm(App app, Entry entry) {
        if (app.vehicles) {
            RequestAppCarSpawnPayload payload = new RequestAppCarSpawnPayload(entry.id());
            ModClientNetworking.sendToServer(RequestAppCarSpawnPayload.ID, payload, RequestAppCarSpawnPayload::write);
            this.onClose();
        } else {
            RequestAppItemPurchasePayload payload = new RequestAppItemPurchasePayload(entry.id());
            ModClientNetworking.sendToServer(RequestAppItemPurchasePayload.ID, payload, RequestAppItemPurchasePayload::write);
            this.purchasedBannerUntil = System.currentTimeMillis() + PURCHASED_BANNER_MS;
        }
    }

    private void launchGtt() {
        com.examplebett.network.SetGttPayload payload = new com.examplebett.network.SetGttPayload(true);
        ModClientNetworking.sendToServer(com.examplebett.network.SetGttPayload.ID, payload, com.examplebett.network.SetGttPayload::write);
        com.examplebett.client.GttClient.requestStart();
        this.onClose();
    }

    private boolean handleClick(double mouseX, double mouseY, int button) {
        if (this.shutdownUntil != 0L) {
            return true;
        }
        if (button != 0) {
            return false;
        }
        Layout l = this.layout();
        if (this.pending != null) {
            int by0 = l.buttonTop();
            int bw = l.buttonWidth();
            if (inside(mouseX, mouseY, l.yesX0(), by0, l.yesX0() + bw, by0 + DIALOG_BUTTON_HEIGHT)) {
                if (this.hasEnoughEmeralds()) {
                    Entry entry = this.pending;
                    this.pending = null;
                    this.confirm(this.openApp, entry);
                }
            } else {

                this.pending = null;
            }
            return true;
        }
        if (inside(mouseX, mouseY, l.homeX0() - 2, l.homeY0() - 2, l.homeX1() + 2, l.homeY1() + 2)) {
            if (this.openApp != null) {
                this.openApp = null;
                this.musicBrowsing = false;
            } else {

                this.shutdownUntil = System.currentTimeMillis() + SHUTDOWN_MS;
            }
            return true;
        }
        if (this.openApp == null) {
            App[] apps = App.values();
            for (int i = 0; i < apps.length; i++) {
                int[] r = appIconRect(l, i);
                if (inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
                    if (apps[i] == App.GTT) {
                        this.launchGtt();
                        return true;
                    }
                    this.openApp = apps[i];
                    this.scrollRow = 0;
                    this.weatherPickerOpen = false;
                    this.clockPickerOpen = false;
                    this.musicBrowsing = false;
                    this.settingsPage = SETTINGS_PAGE_MAIN;
                    this.purchasedBannerUntil = 0L;
                    return true;
                }
            }
            return false;
        }
        int headerTop = l.sy0() + STATUS_BAR_HEIGHT;
        if (inside(mouseX, mouseY, l.sx0(), headerTop, l.sx0() + 16, headerTop + HEADER_HEIGHT)) {

            if (this.openApp == App.MUSIC && this.musicBrowsing) {
                this.musicBrowsing = false;
            } else if (this.openApp == App.SETTINGS && this.settingsPage != SETTINGS_PAGE_MAIN) {
                this.settingsPage = SETTINGS_PAGE_MAIN;
                this.rebindingKey = null;
            } else {
                this.openApp = null;
            }
            return true;
        }
        if (this.openApp == App.MUSIC && !this.musicBrowsing) {
            return this.handleMusicHomeClick(l, mouseX, mouseY);
        }
        if (this.openApp.weather) {
            return this.handleWeatherClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.EMERGENCY) {
            return this.handleEmergencyClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.DETONATOR) {
            return this.handleDetonatorClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.CLOCK) {
            return this.handleClockClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.MASSIVE_AUTO) {
            return this.handleMassiveAutoClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.SLOT) {
            return this.handleSlotClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.SETTINGS) {
            return this.handleSettingsClick(l, mouseX, mouseY);
        }
        int listTop = l.listTop();

        if (this.maxScrollRow(l) > 0 && inside(mouseX, mouseY, l.sx1() - 6, listTop, l.sx1(), l.sy1())) {
            this.draggingScrollbar = true;
            this.scrollToMouse(l, mouseY);
            return true;
        }
        if (inside(mouseX, mouseY, l.sx0(), listTop, l.sx1() - 6, listTop + l.visibleRows() * ROW_HEIGHT)) {
            int index = this.scrollRow + (int) ((mouseY - listTop) / ROW_HEIGHT);
            List<Entry> entries = this.entries();
            if (index < entries.size() && this.openApp == App.MUSIC) {

                com.examplebett.client.sound.ChorusMusicTracks.Track track = findTrack(entries.get(index).id());
                if (track != null) {
                    com.examplebett.client.sound.ChorusMusicPlayer.play(track);
                }
                return true;
            }
            if (index < entries.size()) {
                if (this.isFree()) {
                    this.confirm(this.openApp, entries.get(index));
                } else {
                    this.pending = entries.get(index);
                }
                return true;
            }
        }
        return false;
    }

    private void releaseVolume() {
        if (this.draggingVolume) {
            this.draggingVolume = false;
            this.minecraft.options.save();
        }
    }

    private int thumbHeight(Layout l) {
        return Math.max(8, (l.sy1() - l.listTop()) * l.visibleRows() / Math.max(1, this.entries().size()));
    }

    private void scrollToMouse(Layout l, double mouseY) {
        int max = this.maxScrollRow(l);
        int room = (l.sy1() - l.listTop()) - this.thumbHeight(l);
        if (max <= 0 || room <= 0) {
            return;
        }
        double t = (mouseY - l.listTop() - this.thumbHeight(l) / 2.0) / room;
        this.scrollRow = (int) Math.round(Math.max(0.0, Math.min(1.0, t)) * max);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.draggingVolume && button == 0) {
            this.setVolumeFromMouse(this.layout(), mouseX);
            return true;
        }
        if (this.draggingScrollbar && button == 0) {
            this.scrollToMouse(this.layout(), mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.draggingScrollbar = false;
            this.releaseVolume();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.handleClick(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleSlotKey(int key) {
        if (this.openApp != App.SLOT || this.pending != null
                || (key != org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE && key != org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER && key != org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER)) {
            return false;
        }
        this.pressSlotSpin();
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.handleRebindKey(keyCode, () -> com.mojang.blaze3d.platform.InputConstants.getKey(keyCode, scanCode))
                || this.handleSlotKey(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

    private boolean mineturismo$mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.openApp == null || this.pending != null || scrollY == 0) {
            return false;
        }
        Layout l = this.layout();
        this.scrollRow = Math.max(0, Math.min(this.maxScrollRow(l), this.scrollRow + (scrollY > 0 ? -1 : 1)));
        return true;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(guiGraphics.pose(), mouseX, mouseY, partialTick);
        Layout l = this.layout();
        this.drawPhone(guiGraphics, l);
        if (this.shutdownUntil != 0L) {
            this.drawShutdown(guiGraphics, l);
            return;
        }
        if (this.openApp != null) {
            this.drawApp(guiGraphics, l, this.openApp, mouseX, mouseY);
        } else {
            this.drawHome(guiGraphics, l, mouseX, mouseY);
        }
        this.drawStatusBar(guiGraphics, l);
        this.skin.drawScreenOverlay(guiGraphics, l.sx0(), l.sy0(), l.sx1());
        if (this.pending != null && this.openApp != null) {
            this.drawDialog(guiGraphics, l, this.openApp, mouseX, mouseY);
        }
    }

    private void drawShutdown(GuiGraphics g, Layout l) {
        g.fill(l.sx0(), l.sy0(), l.sx1(), l.sy1(), 0xFF000000);

        float dot = PX * 2 * 2f / 3f;
        int logoW = Math.round(CHORUS_LOGO[0].length() * dot);
        int logoH = Math.round(CHORUS_LOGO.length * dot);
        int cx = (l.sx0() + l.sx1()) / 2;
        int logoX = (cx - logoW / 2) / PX * PX;
        int logoY = ((l.sy0() + l.sy1()) / 2 - logoH - 6) / PX * PX;
        for (int row = 0; row < CHORUS_LOGO.length; row++) {
            String line = CHORUS_LOGO[row];
            int py0 = logoY + Math.round(row * dot);
            int py1 = logoY + Math.round((row + 1) * dot);
            for (int col = 0; col < line.length(); col++) {
                if (line.charAt(col) == '#') {
                    int px0 = logoX + Math.round(col * dot);
                    int px1 = logoX + Math.round((col + 1) * dot);
                    g.fill(px0, py0, px1, py1, 0xFFFFFFFF);
                }
            }
        }
        String brand = "Chorus";
        int brandY = logoY + logoH + 6;
        g.drawString(this.font, brand, cx - this.font.width(brand) / 2, brandY, 0xFFFFFFFF, false);
        String status = ModText.translatable("gui.mineturismo.smart_dispatch.shutting_down").getString();
        this.drawSmallText(g, status, cx - Math.round(this.font.width(status) * SMALL_TEXT_SCALE) / 2,
                brandY + this.font.lineHeight + 6, 0xFFB4B4B4);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.shutdownUntil != 0L && System.currentTimeMillis() >= this.shutdownUntil) {
            this.shutdownUntil = 0L;
            this.onClose();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
//? if <1.16 && >=1.15 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModClientNetworking;
import com.examplebett.compat.ModText;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModPlayer;
import com.examplebett.compat.ModRegistries;
import com.examplebett.item.SmartDispatchAppItem;
import com.examplebett.network.RequestAppCarSpawnPayload;
import com.examplebett.network.RequestAppItemPurchasePayload;
import com.examplebett.util.AiCarPool;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SmartDispatchAppScreen extends Screen {

    private static final int PX = 2;
    private static final int PHONE_MAX_HEIGHT = 240;
    private static final float PHONE_ASPECT = 0.56f;
    private static final int STATUS_BAR_HEIGHT = 11;
    private static final int APP_ICON_SIZE = 24;
    private static final int APP_ICON_TEXTURE_SIZE = 64;
    private static final int APP_COLUMNS = 4;
    private static final int HEADER_HEIGHT = 16;
    private static final int COST_BAR_HEIGHT = 10;
    private static final int ROW_HEIGHT = 20;
    private static final float SMALL_TEXT_SCALE = 0.75f;
    private static final int DIALOG_HEIGHT = 78;
    private static final int DIALOG_BUTTON_HEIGHT = 14;
    private static final long PURCHASED_BANNER_MS = 1500L;

    private static final long SHUTDOWN_MS = 500L;

    private static final int SETTINGS_PAGE_MAIN = 0;
    private static final int SETTINGS_PAGE_WALLPAPER = 1;
    private static final int SETTINGS_PAGE_MODEL = 2;

    private static final int SETTINGS_PAGE_CONTROLS = 3;
    private static final int CONTROLS_ROW_HEIGHT = 11;
    private static final int SETTINGS_ROW_HEIGHT = 20;

    private static final int WALLPAPER_TEXTURE_WIDTH = 64;
    private static final int WALLPAPER_TEXTURE_HEIGHT = 120;

    private static final String[] PHONE_MODEL_NAMES = {"PopPhone 3", "PopPhone 6", "PopPhone 18", null};

    private static final String[] CHORUS_LOGO = {
            "....#.......",
            "#..##.......",
            "########....",
            ".#########..",
            ".##########.",
            "..#########.",
            ".###########",
            "############",
            "############",
            ".###########",
            ".##########.",
            "..########..",
            "...######...",
    };

    private static final int COLOR_WALLPAPER_TOP = 0xFF5696E6;
    private static final int COLOR_WALLPAPER_BOTTOM = 0xFF2C5CB4;
    private static final int COLOR_STATUS_BAR = 0x66000000;
    private static final int COLOR_ROW = 0xFFFFFFFF;
    private static final int COLOR_ROW_ALT = 0xFFF1F1F4;
    private static final int COLOR_ROW_HOVER = 0xFFFFF1B8;
    private static final int COLOR_COST_BAR = 0xFF2A2A30;
    private static final int COLOR_SCROLL_TRACK = 0xFFDADAE0;
    private static final int COLOR_SCROLL_THUMB = 0xFF8C8C96;
    private static final int COLOR_DIALOG_SHADE = 0x99000000;
    private static final int COLOR_DIALOG = 0xFFFFFFFF;
    private static final int COLOR_BUTTON_YES = 0xFF3DAA4F;
    private static final int COLOR_BUTTON_YES_HOVER = 0xFF4FC562;
    private static final int COLOR_BUTTON_NO = 0xFFB4B6BE;
    private static final int COLOR_BUTTON_NO_HOVER = 0xFFC8CAD2;
    private static final int COLOR_BANNER = 0xEE202024;

    private enum App {
        MINES("Mines Car Share", "Mines\nCar Share", null, "mines", 0xFFF2C318, 0xFF000000, true, false),
        AVALON("Avalon", "Avalon", null, "avalon", 0xFF1B2838, 0xFF6FD3F3, false, false),
        WEATHER(null, null, "gui.mineturismo.smart_dispatch.weather_app", "weather", 0xFF3D8BD9, 0xFFFFFFFF, false, true),
        CLOCK(null, null, "gui.mineturismo.smart_dispatch.clock_app", "clock", 0xFF1C1C1E, 0xFFFFFFFF, false, false),

        GTT("Grand Target Track", "GTT", null, "gtt", 0xFF000000, 0xFFFFFFFF, false, false),
        EMERGENCY(null, null, "gui.mineturismo.smart_dispatch.emergency_app", "emergency", 0xFFD32F2F, 0xFFFFFFFF, false, false),

        MUSIC(null, null, "gui.mineturismo.smart_dispatch.music_app", "music", 0xFFF5354F, 0xFFFFFFFF, false, false),

        MASSIVE_AUTO("Massive Auto", "Massive\nAuto", null, "massive_auto", 0xFF1E4FA8, 0xFFFFFFFF, false, false),

        SLOT(null, null, "gui.mineturismo.smart_dispatch.slot_app", "slot", 0xFFE6197E, 0xFFFFFFFF, false, false),

        DETONATOR(null, null, "gui.mineturismo.smart_dispatch.detonator_app", "detonator", 0xFF2B2D33, 0xFFFFFFFF, false, false),

        SETTINGS(null, null, "gui.mineturismo.smart_dispatch.settings_app", "settings", 0xFFE9E9EE, 0xFF000000, false, false);

        private final String title;
        private final String shortName;
        private final String titleKey;
        final ResourceLocation icon;
        final int headerColor;
        final int titleColor;
        final boolean vehicles;
        final boolean weather;

        App(String title, String shortName, String titleKey, String iconId, int headerColor, int titleColor,
            boolean vehicles, boolean weather) {
            this.title = title;
            this.shortName = shortName;
            this.titleKey = titleKey;
            this.icon = AiObjectMod.id("textures/gui/smart_dispatch/" + iconId + ".png");
            this.headerColor = headerColor;
            this.titleColor = titleColor;
            this.vehicles = vehicles;
            this.weather = weather;
        }

        String title() {
            return this.titleKey != null ? ModText.translatable(this.titleKey).getString() : this.title;
        }

        String shortName() {
            if (this.titleKey == null) {
                return this.shortName;
            }
            String labelKey = this.titleKey + "_label";
            return net.minecraft.client.resources.language.I18n.exists(labelKey) ? ModText.translatable(labelKey).getString() : this.title();
        }
    }

    private record Entry(String id, ItemStack stack, Component name) {
    }

    private record Layout(int x0, int y0, int x1, int y1, int sx0, int sy0, int sx1, int sy1,
                          int homeX0, int homeY0, int homeX1, int homeY1) {
        int listTop() {
            return sy0 + STATUS_BAR_HEIGHT + HEADER_HEIGHT + COST_BAR_HEIGHT;
        }

        int visibleRows() {
            return Math.max(1, (sy1 - listTop()) / ROW_HEIGHT);
        }

        int dialogTop() {
            return (sy0 + sy1 - DIALOG_HEIGHT) / 2;
        }

        int buttonTop() {
            return dialogTop() + DIALOG_HEIGHT - DIALOG_BUTTON_HEIGHT - 6;
        }

        int buttonWidth() {
            return (sx1 - sx0 - 12 - 6 - 6) / 2;
        }

        int yesX0() {
            return sx0 + 12;
        }

        int noX0() {
            return yesX0() + buttonWidth() + 6;
        }
    }

    private final List<Entry> vehicles = new ArrayList<>();
    private final List<Entry> shopItems = new ArrayList<>();
    private final List<Entry> musicTracks = new ArrayList<>();
    private App openApp = null;
    private int scrollRow = 0;

    private boolean draggingScrollbar = false;

    private boolean weatherPickerOpen = false;

    private boolean clockPickerOpen = false;

    private boolean musicBrowsing = false;

    private boolean draggingVolume = false;

    private Entry pending = null;
    private long purchasedBannerUntil = 0L;

    private long shutdownUntil = 0L;

    private String phoneModel = SmartDispatchAppItem.DEFAULT_PHONE_MODEL;
    private String wallpaper = SmartDispatchAppItem.WALLPAPERS[0];
    private PhoneSkin skin = PhoneSkin.POPPHONE_6;

    private int settingsPage = SETTINGS_PAGE_MAIN;

    private net.minecraft.client.KeyMapping rebindingKey;

    public SmartDispatchAppScreen() {
        super(ModText.translatable("item.mineturismo.smart_dispatch_app"));
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            ItemStack phone = SmartDispatchAppItem.heldPhone(mc.player);
            this.phoneModel = SmartDispatchAppItem.phoneModelOf(phone);
            this.wallpaper = SmartDispatchAppItem.wallpaperOf(phone);
            this.skin = PhoneSkin.byId(this.phoneModel);
        }
        for (String modelId : AiCarPool.allModelIds(id -> false)) {
            Item item = AiCarPool.spawnItemFor(modelId);
            if (item != null) {
                this.vehicles.add(new Entry(modelId, new ItemStack(item), ModText.translatable(item.getDescriptionId())));
            }
        }
        this.vehicles.sort(Comparator.comparing(e -> e.name().getString()));
        for (Item item : SmartDispatchAppItem.shopItems()) {
            this.shopItems.add(new Entry(ModRegistries.ITEM.getKey(item).toString(), new ItemStack(item),
                    ModText.translatable(item.getDescriptionId())));
        }

        ItemStack wardenEnergy = SmartDispatchAppItem.createWardenEnergy(1);
        if (!wardenEnergy.isEmpty()) {
            this.shopItems.add(new Entry(SmartDispatchAppItem.WARDEN_ENERGY_ID, wardenEnergy,
                    ModText.translatable("item.mineturismo.warden_energy").append(" x" + SmartDispatchAppItem.WARDEN_ENERGY_PACK)));
        }

        ItemStack wardenEnergySonicBoom = SmartDispatchAppItem.createWardenEnergySonicBoom(1);
        if (!wardenEnergySonicBoom.isEmpty()) {
            this.shopItems.add(new Entry(SmartDispatchAppItem.WARDEN_ENERGY_SONIC_BOOM_ID, wardenEnergySonicBoom,
                    ModText.translatable("item.mineturismo.warden_energy_sonic_boom").append(" x" + SmartDispatchAppItem.WARDEN_ENERGY_PACK)));
        }

        ItemStack chocoBar = SmartDispatchAppItem.createChocoBar(1);
        if (!chocoBar.isEmpty()) {
            this.shopItems.add(new Entry(SmartDispatchAppItem.CHOCO_BAR_ID, chocoBar,
                    ModText.translatable("item.mineturismo.choco_bar").append(" x" + SmartDispatchAppItem.CHOCO_BAR_PACK)));
        }
        this.shopItems.sort(Comparator.comparing(e -> e.name().getString()));

        java.util.Map<String, Item> discs = new java.util.HashMap<>();
        for (Item item : ModRegistries.ITEM) {
            net.minecraft.resources.ResourceLocation key = ModRegistries.ITEM.getKey(item);
            if ("minecraft".equals(key.getNamespace()) && key.getPath().startsWith("music_disc_")) {
                discs.put(key.getPath().substring("music_disc_".length()), item);
            }
        }
        for (com.examplebett.client.sound.ChorusMusicTracks.Track track : com.examplebett.client.sound.ChorusMusicTracks.TRACKS) {

            if (net.minecraft.client.Minecraft.getInstance().getSoundManager().getSoundEvent(track.soundId()) == null) {
                continue;
            }
            Item icon = switch (track.category()) {
                case RECORDS -> discs.getOrDefault(track.id(), Items.JUKEBOX);
                case CREATIVE -> Items.BRICKS;
                case MENU -> Items.PAINTING;
                case NETHER -> Items.NETHERRACK;
                case END -> Items.END_STONE;
                case UNDERWATER -> Items.PRISMARINE;
                default -> Items.GRASS_BLOCK;
            };
            this.musicTracks.add(new Entry(track.id(), new ItemStack(icon), ModText.literal(track.title())));
        }
    }

    public static SmartDispatchAppScreen massiveAuto() {
        SmartDispatchAppScreen screen = new SmartDispatchAppScreen();
        screen.openApp = App.MASSIVE_AUTO;
        return screen;
    }

    private static com.examplebett.client.sound.ChorusMusicTracks.Track findTrack(String id) {
        for (com.examplebett.client.sound.ChorusMusicTracks.Track track : com.examplebett.client.sound.ChorusMusicTracks.TRACKS) {
            if (track.id().equals(id)) {
                return track;
            }
        }
        return null;
    }

    private List<Entry> entries() {
        if (this.openApp == App.MUSIC) {
            return this.musicBrowsing ? this.musicTracks : List.of();
        }
        return this.openApp == null || this.openApp.weather || this.openApp == App.EMERGENCY || this.openApp == App.GTT
                || this.openApp == App.CLOCK || this.openApp == App.MASSIVE_AUTO || this.openApp == App.SLOT
                || this.openApp == App.SETTINGS ? List.of()
                : this.openApp.vehicles ? this.vehicles : this.shopItems;
    }

    private Layout layout() {
        int phoneH = Math.min(PHONE_MAX_HEIGHT, this.height - 16) / PX * PX;
        int phoneW = Math.round(phoneH * PHONE_ASPECT) / PX * PX;
        int x0 = (this.width - phoneW) / 2 / PX * PX;
        int y0 = (this.height - phoneH) / 2 / PX * PX;
        int x1 = x0 + phoneW;
        int y1 = y0 + phoneH;

        int[] home = this.skin.homeRect(x0, x1, y1 - this.skin.insetBottom, y1);
        return new Layout(x0, y0, x1, y1,
                x0 + this.skin.insetX, y0 + this.skin.insetTop, x1 - this.skin.insetX, y1 - this.skin.insetBottom,
                home[0], home[1], home[2], home[3]);
    }

    private int maxScrollRow(Layout l) {
        return Math.max(0, this.entries().size() - l.visibleRows());
    }

    private boolean isFree() {
        return this.minecraft != null && this.minecraft.player != null && ModPlayer.hasInstabuild(this.minecraft.player);
    }

    private static int costOf(App app) {
        return app != null && app.vehicles ? SmartDispatchAppItem.VEHICLE_CALL_COST : SmartDispatchAppItem.SHOP_COST;
    }

    private boolean hasEnoughEmeralds() {
        return this.minecraft != null && this.minecraft.player != null
                && ModPlayer.getInventory(this.minecraft.player).countItem(Items.EMERALD) >= costOf(this.openApp);
    }

    private static boolean inside(double mx, double my, int x0, int y0, int x1, int y1) {
        return mx >= x0 && mx < x1 && my >= y0 && my < y1;
    }

    static void fillPixelRounded(GuiGraphics g, int x0, int y0, int x1, int y1, int step, int color) {
        g.fill(x0 + step * 2, y0, x1 - step * 2, y0 + step, color);
        g.fill(x0 + step, y0 + step, x1 - step, y0 + step * 2, color);
        g.fill(x0, y0 + step * 2, x1, y1 - step * 2, color);
        g.fill(x0 + step, y1 - step * 2, x1 - step, y1 - step, color);
        g.fill(x0 + step * 2, y1 - step, x1 - step * 2, y1, color);
    }

    static void fillPixelCircle(GuiGraphics g, int cx, int cy, int radius, int color) {
        for (int dy = -radius; dy < radius; dy += PX) {
            double mid = dy + PX / 2.0;
            int half = (int) Math.round(Math.sqrt(Math.max(0.0, radius * (double) radius - mid * mid)) / PX) * PX;
            if (half > 0) {
                g.fill(cx - half, cy + dy, cx + half, cy + dy + PX, color);
            }
        }
    }

    private static int lerpColor(int a, int b, float t) {
        int r = Math.round(((a >> 16) & 0xFF) + (((b >> 16) & 0xFF) - ((a >> 16) & 0xFF)) * t);
        int gr = Math.round(((a >> 8) & 0xFF) + (((b >> 8) & 0xFF) - ((a >> 8) & 0xFF)) * t);
        int bl = Math.round((a & 0xFF) + ((b & 0xFF) - (a & 0xFF)) * t);
        return 0xFF000000 | (r << 16) | (gr << 8) | bl;
    }

    private void drawPhone(GuiGraphics g, Layout l) {
        this.skin.drawBody(g, l.x0(), l.y0(), l.x1(), l.y1(), l.sx0(), l.sy0(), l.sx1(), l.sy1(),
                new int[]{l.homeX0(), l.homeY0(), l.homeX1(), l.homeY1()});
    }

    private static void drawWallpaper(GuiGraphics g, String wallpaper, int x0, int y0, int x1, int y1) {
        if ("white".equals(wallpaper) || "gray".equals(wallpaper)) {
            g.fill(x0, y0, x1, y1, "white".equals(wallpaper) ? 0xFFFFFFFF : 0xFF8E8E93);
            return;
        }
        int[] fallback = switch (wallpaper) {
            case "sunset_beach" -> new int[]{0xFF8A83C6, 0xFFF4AE96};
            case "grass" -> new int[]{0xFF5FA83A, 0xFF7A4E28};
            case "flow" -> new int[]{0xFFE8336A, 0xFF4A6CD8};
            default -> null;
        };
        if (fallback != null) {
            ResourceLocation texture = AiObjectMod.id("textures/gui/smart_dispatch/wallpaper_" + wallpaper + ".png");
            int w = x1 - x0;
            int h = y1 - y0;
            int vH = WALLPAPER_TEXTURE_HEIGHT;
            int uW = Math.min(WALLPAPER_TEXTURE_WIDTH, Math.round((float) vH * w / Math.max(1, h)));
            if (uW == WALLPAPER_TEXTURE_WIDTH) {
                vH = Math.round((float) uW * h / Math.max(1, w));
            }
            float u = (WALLPAPER_TEXTURE_WIDTH - uW) / 2f;
            float v = (WALLPAPER_TEXTURE_HEIGHT - vH) / 2f;
            com.examplebett.compat.GuiTextures.blit(g, texture, x0, y0, u, v, w, h,
                    uW, vH, WALLPAPER_TEXTURE_WIDTH, WALLPAPER_TEXTURE_HEIGHT);
            return;
        }
        drawGradient(g, x0, y0, x1, y1, COLOR_WALLPAPER_TOP, COLOR_WALLPAPER_BOTTOM);
    }

    private static void drawGradient(GuiGraphics g, int x0, int y0, int x1, int y1, int top, int bottom) {
        int bands = Math.max(1, (y1 - y0) / (PX * 2));
        for (int i = 0; i < bands; i++) {
            int bandTop = y0 + i * PX * 2;
            int bandBottom = i == bands - 1 ? y1 : bandTop + PX * 2;
            g.fill(x0, bandTop, x1, bandBottom, lerpColor(top, bottom, (float) i / bands));
        }
    }

    private long levelDayTime() {

        return this.minecraft.level.getDayTime();
    }

    private String currentTimeText() {
        if (this.minecraft == null || this.minecraft.level == null) {
            return "--:--";
        }
        long dayTime = Math.floorMod(this.levelDayTime(), 24000L);
        int hours = (int) ((dayTime / 1000L + 6L) % 24L);
        int minutes = (int) (dayTime % 1000L * 60L / 1000L);
        return String.format("%d:%02d", hours, minutes);
    }

    private void drawStatusBar(GuiGraphics g, Layout l) {
        g.fill(l.sx0(), l.sy0(), l.sx1(), l.sy0() + STATUS_BAR_HEIGHT, COLOR_STATUS_BAR);
        this.drawSmallText(g, this.currentTimeText(), l.sx0() + 3, l.sy0() + 3, 0xFFFFFFFF);

        int bx1 = l.sx1() - 3;
        int by0 = l.sy0() + 3;
        g.fill(bx1 - 12, by0, bx1 - 1, by0 + 6, 0xFFFFFFFF);
        g.fill(bx1 - 1, by0 + 2, bx1, by0 + 4, 0xFFFFFFFF);
        g.fill(bx1 - 11, by0 + 1, bx1 - 2, by0 + 5, 0xFF000000);
        g.fill(bx1 - 10, by0 + 2, bx1 - 4, by0 + 4, 0xFF5BD35B);

        for (int i = 0; i < 4; i++) {
            int bx = bx1 - 26 + i * 3;
            g.fill(bx, by0 + 6 - (i + 1) * 3 / 2, bx + 2, by0 + 6, 0xFFFFFFFF);
        }
    }

    private int[] appIconRect(Layout l, int index) {
        int cellW = (l.sx1() - l.sx0()) / APP_COLUMNS;
        int x = l.sx0() + (index % APP_COLUMNS) * cellW + (cellW - APP_ICON_SIZE) / 2;
        int y = l.sy0() + STATUS_BAR_HEIGHT + 8 + (index / APP_COLUMNS) * (APP_ICON_SIZE + 20);
        return new int[]{x, y, x + APP_ICON_SIZE, y + APP_ICON_SIZE};
    }

    private void drawHome(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        drawWallpaper(g, this.wallpaper, l.sx0(), l.sy0(), l.sx1(), l.sy1());

        boolean lightWallpaper = "white".equals(this.wallpaper);
        int labelColor = lightWallpaper ? 0xFF1C1C1E : 0xFFFFFFFF;
        App[] apps = App.values();
        for (int i = 0; i < apps.length; i++) {
            App app = apps[i];
            int[] r = appIconRect(l, i);
            com.examplebett.compat.GuiTextures.blit(g, app.icon, r[0], r[1], 0f, 0f, APP_ICON_SIZE, APP_ICON_SIZE,
                    APP_ICON_TEXTURE_SIZE, APP_ICON_TEXTURE_SIZE, APP_ICON_TEXTURE_SIZE, APP_ICON_TEXTURE_SIZE);
            if (this.pending == null && inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {

                fillPixelRounded(g, r[0], r[1], r[2], r[3], 2, 0x33FFFFFF);
            }

            int cellWidth = (l.sx1() - l.sx0()) / APP_COLUMNS - 2;
            String[] labelLines = app.shortName().split("\n");
            for (int line = 0; line < labelLines.length; line++) {
                float scale = Math.min(SMALL_TEXT_SCALE, (float) cellWidth / Math.max(1, com.examplebett.compat.LegacyGui.width(this.font, labelLines[line])));
                int labelWidth = Math.round(com.examplebett.compat.LegacyGui.width(this.font, labelLines[line]) * scale);
                this.drawScaledText(g, (
                                (labelLines[line])), (r[0] + r[2] - labelWidth) / 2,
                        r[3] + 2 + line * 7, labelColor, scale);
            }
        }

        int dotsY = l.sy1() - PX * 4;
        int cx = (l.sx0() + l.sx1()) / 2 / PX * PX;
        int dimDot = lightWallpaper ? 0x881C1C1E : 0x88FFFFFF;
        g.fill(cx - PX * 3, dotsY, cx - PX * 2, dotsY + PX, labelColor);
        g.fill(cx - PX / 2, dotsY, cx + PX / 2 + 1, dotsY + PX, dimDot);
        g.fill(cx + PX * 2, dotsY, cx + PX * 3, dotsY + PX, dimDot);
    }

    private void drawApp(GuiGraphics g, Layout l, App app, int mouseX, int mouseY) {
        int headerTop = l.sy0() + STATUS_BAR_HEIGHT;
        g.fill(l.sx0(), l.sy0(), l.sx1(), l.sy1(), COLOR_ROW);
        g.fill(l.sx0(), l.sy0(), l.sx1(), headerTop + HEADER_HEIGHT, app.headerColor);

        int ax = l.sx0() + 4;
        int ay = headerTop + HEADER_HEIGHT / 2;
        for (int i = 0; i < 4; i++) {
            g.fill(ax + i, ay - i - 1, ax + i + 1, ay - i + 1, app.titleColor);
            g.fill(ax + i, ay + i - 1, ax + i + 1, ay + i + 1, app.titleColor);
        }
        int titleWidth = com.examplebett.compat.LegacyGui.width(this.font, app.title());
        int titleRoom = l.sx1() - l.sx0() - 24;
        if (titleWidth <= titleRoom) {
            g.drawString(this.font, app.title(), (l.sx0() + l.sx1() - titleWidth) / 2, headerTop + 4, app.titleColor, false);
        } else {
            int scaled = Math.round(titleWidth * SMALL_TEXT_SCALE);
            this.drawSmallText(g, app.title(), (l.sx0() + l.sx1() - scaled) / 2, headerTop + 5, app.titleColor);
        }
        if (app.weather) {
            this.drawWeather(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.EMERGENCY) {
            this.drawEmergency(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.DETONATOR) {
            this.drawDetonator(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.CLOCK) {
            this.drawClock(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.MASSIVE_AUTO) {
            this.drawMassiveAuto(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.SLOT) {
            this.drawSlot(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.SETTINGS) {
            this.drawSettings(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.MUSIC && !this.musicBrowsing) {
            this.drawMusicHome(g, l, mouseX, mouseY);
            return;
        }
        int costTop = headerTop + HEADER_HEIGHT;
        g.fill(l.sx0(), costTop, l.sx1(), costTop + COST_BAR_HEIGHT, COLOR_COST_BAR);
        Component cost = app == App.MUSIC ? this.musicStatus() : this.isFree()
                ? ModText.translatable("gui.mineturismo.smart_dispatch.free")
                : ModText.translatable(app.vehicles ? "gui.mineturismo.smart_dispatch.cost" : "gui.mineturismo.smart_dispatch.price",
                        costOf(app));
        this.drawSmallText(g, cost.getString(), l.sx0() + 3, costTop + 2, 0xFFE8E8E8);

        List<Entry> entries = this.entries();
        int listTop = l.listTop();
        int rows = l.visibleRows();
        this.scrollRow = Math.max(0, Math.min(this.maxScrollRow(l), this.scrollRow));
        int listRight = l.sx1() - 4;
        for (int i = 0; i < rows; i++) {
            int index = this.scrollRow + i;
            if (index >= entries.size()) {
                break;
            }
            Entry e = entries.get(index);
            int top = listTop + i * ROW_HEIGHT;
            boolean hover = this.pending == null && inside(mouseX, mouseY, l.sx0(), top, listRight, top + ROW_HEIGHT);
            boolean playing = app == App.MUSIC && com.examplebett.client.sound.ChorusMusicPlayer.current() != null
                    && com.examplebett.client.sound.ChorusMusicPlayer.current().id().equals(e.id());
            g.fill(l.sx0(), top, listRight, top + ROW_HEIGHT,
                    hover ? COLOR_ROW_HOVER : playing ? 0xFFFFD9DF : index % 2 == 0 ? COLOR_ROW : COLOR_ROW_ALT);
            g.renderItem(e.stack(), l.sx0() + 2, top + 2);
            int textX = l.sx0() + 21;
            this.drawSmallText(g, this.fit(e.name().getString(), listRight - textX - 2), textX, top + 7, 0xFF202024);
        }

        g.fill(listRight, listTop, l.sx1(), l.sy1(), COLOR_SCROLL_TRACK);
        int max = this.maxScrollRow(l);
        if (max > 0) {
            int trackH = l.sy1() - listTop;
            int thumbH = this.thumbHeight(l);
            int thumbTop = listTop + (trackH - thumbH) * this.scrollRow / max;
            g.fill(listRight + 1, thumbTop, l.sx1() - 1, thumbTop + thumbH, COLOR_SCROLL_THUMB);
        }
        if (System.currentTimeMillis() < this.purchasedBannerUntil) {
            this.aboveItems(g);
            String text = ModText.translatable("gui.mineturismo.smart_dispatch.purchased").getString();
            int w = Math.round(com.examplebett.compat.LegacyGui.width(this.font, text) * SMALL_TEXT_SCALE) + 10;
            int bx0 = (l.sx0() + l.sx1() - w) / 2;
            int by0 = l.sy1() - 18;
            fillPixelRounded(g, bx0, by0, bx0 + w, by0 + 12, 1, COLOR_BANNER);
            this.drawSmallText(g, text, bx0 + 5, by0 + 3, 0xFFFFFFFF);
        }
    }

    private int settingsRowCount() {
        return this.settingsPage == SETTINGS_PAGE_WALLPAPER ? SmartDispatchAppItem.WALLPAPERS.length
                : this.settingsPage == SETTINGS_PAGE_MODEL ? SmartDispatchAppItem.PHONE_MODELS.length : 3;
    }

    private record ControlRow(String labelKey, String keyText, net.minecraft.client.KeyMapping rebind) {
    }

    private static String keyName(net.minecraft.client.KeyMapping mapping) {
        return mapping.getTranslatedKeyMessage();
    }

    private List<ControlRow> controlRows() {
        net.minecraft.client.Options o = this.minecraft.options;
        net.minecraft.client.KeyMapping horn = com.examplebett.client.AiObjectModClient.hornKey();
        net.minecraft.client.KeyMapping trailer = com.examplebett.client.AiObjectModClient.trailerCouplingKey();
        String p = "gui.mineturismo.smart_dispatch.controls_";
        return List.of(
                new ControlRow(p + "accelerate", keyName(o.keyUp), null),
                new ControlRow(p + "brake", keyName(o.keyDown), null),
                new ControlRow(p + "steer", keyName(o.keyLeft) + " / " + keyName(o.keyRight), null),
                new ControlRow(p + "drift", keyName(o.keyJump), null),
                new ControlRow(p + "burnout", keyName(o.keyUp) + "+" + keyName(o.keyDown), null),

                new ControlRow(p + "jturn", keyName(o.keyDown) + "+" + keyName(o.keyLeft) + "/" + keyName(o.keyRight), null),

                new ControlRow(p + "wheelie", keyName(o.keySprint), null),
                new ControlRow(p + "aircraft", keyName(o.keyJump) + " / " + keyName(o.keySprint), null),
                new ControlRow(p + "dismount", keyName(o.keyShift), null),
                new ControlRow(p + "horn", keyName(horn), horn),
                new ControlRow(p + "trailer", keyName(trailer), trailer));
    }

    private int[] controlsRowRect(Layout l, int index) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT + 16 + index * CONTROLS_ROW_HEIGHT;
        return new int[]{l.sx0() + 5, top, l.sx1() - 5, top + CONTROLS_ROW_HEIGHT};
    }

    private void drawControls(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int width = l.sx1() - l.sx0();
        this.drawSmallText(g, this.fit(ModText.translatable("gui.mineturismo.smart_dispatch.settings_controls").getString(), width - 12),
                l.sx0() + 7, top + 6, 0xFF6C6C72);
        List<ControlRow> rows = this.controlRows();
        int[] first = this.controlsRowRect(l, 0);
        int[] last = this.controlsRowRect(l, rows.size() - 1);
        fillPixelRounded(g, first[0], first[1], last[2], last[3], 1, 0xFFFFFFFF);
        for (int i = 0; i < rows.size(); i++) {
            ControlRow row = rows.get(i);
            int[] r = this.controlsRowRect(l, i);
            if (row.rebind() != null && inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
                g.fill(r[0] + 1, r[1] + (i == 0 ? 1 : 0), r[2] - 1, r[3] - (i == rows.size() - 1 ? 1 : 0), 0xFFE5E5EA);
            }
            if (i > 0) {
                g.fill(r[0] + 4, r[1], r[2], r[1] + 1, 0xFFE5E5EA);
            }

            boolean waiting = row.rebind() != null && row.rebind() == this.rebindingKey;
            String key = waiting ? ModText.translatable("gui.mineturismo.smart_dispatch.controls_press_key").getString() : row.keyText();
            key = this.fit(key, (r[2] - r[0]) / 2 - 4);
            int keyWidth = Math.round(com.examplebett.compat.LegacyGui.width(this.font, key) * SMALL_TEXT_SCALE);
            int keyColor = waiting ? 0xFFFF9500 : row.rebind() != null ? 0xFF0A84FF : 0xFF6C6C72;
            this.drawSmallText(g, key, r[2] - 4 - keyWidth, r[1] + 3, keyColor);
            String label = this.fit(ModText.translatable(row.labelKey()).getString(), r[2] - r[0] - keyWidth - 12);
            this.drawSmallText(g, label, r[0] + 4, r[1] + 3, 0xFF202024);
        }

        int noteY = last[3] + 4;
        for (String noteKey : new String[]{"controls_note_rebind", "controls_note_vanilla"}) {
            String note = ModText.translatable("gui.mineturismo.smart_dispatch." + noteKey).getString();
            for (String line : this.wrapSmall(note, width - 12)) {

                if (noteY + 7 > l.sy1() - 2) {
                    break;
                }
                this.drawSmallText(g, line, l.sx0() + 7, noteY, 0xFF8E8E93);
                noteY += 7;
            }
            noteY += 2;
        }
    }

    private List<String> wrapSmall(String text, int maxWidth) {
        List<String> lines = new java.util.ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String next = line.toString() + text.charAt(i);
            if (line.length() > 0 && Math.round(com.examplebett.compat.LegacyGui.width(this.font, next) * SMALL_TEXT_SCALE) > maxWidth) {
                lines.add(line.toString());
                line.setLength(0);
            }
            line.append(text.charAt(i));
        }
        if (line.length() > 0) {
            lines.add(line.toString());
        }
        return lines;
    }

    private boolean handleControlsClick(Layout l, double mouseX, double mouseY) {
        List<ControlRow> rows = this.controlRows();
        for (int i = 0; i < rows.size(); i++) {
            int[] r = this.controlsRowRect(l, i);
            if (rows.get(i).rebind() != null && inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {

                this.rebindingKey = this.rebindingKey == rows.get(i).rebind() ? null : rows.get(i).rebind();
                return true;
            }
        }
        this.rebindingKey = null;
        return false;
    }

    private boolean handleRebindKey(int keyCode, java.util.function.Supplier<com.mojang.blaze3d.platform.InputConstants.Key> key) {
        if (this.rebindingKey == null || this.openApp != App.SETTINGS || this.settingsPage != SETTINGS_PAGE_CONTROLS) {
            return false;
        }
        if (keyCode != org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            this.rebindingKey.setKey(key.get());
            net.minecraft.client.KeyMapping.resetMapping();
            this.minecraft.options.save();
        }
        this.rebindingKey = null;
        return true;
    }

    private int[] settingsRowRect(Layout l, int index) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT + 16 + index * SETTINGS_ROW_HEIGHT;
        return new int[]{l.sx0() + 5, top, l.sx1() - 5, top + SETTINGS_ROW_HEIGHT};
    }

    private void drawSettings(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFFF2F2F7);
        if (this.settingsPage == SETTINGS_PAGE_CONTROLS) {
            this.drawControls(g, l, mouseX, mouseY);
            return;
        }
        String section = this.settingsPage == SETTINGS_PAGE_WALLPAPER ? "gui.mineturismo.smart_dispatch.settings_wallpaper"
                : this.settingsPage == SETTINGS_PAGE_MODEL ? "gui.mineturismo.smart_dispatch.settings_model" : null;
        if (section != null) {
            this.drawSmallText(g, this.fit(ModText.translatable(section).getString(), l.sx1() - l.sx0() - 12), l.sx0() + 7, top + 6, 0xFF6C6C72);
        }
        int count = this.settingsRowCount();
        int[] first = this.settingsRowRect(l, 0);
        int[] last = this.settingsRowRect(l, count - 1);
        fillPixelRounded(g, first[0], first[1], last[2], last[3], 1, 0xFFFFFFFF);
        for (int i = 0; i < count; i++) {
            int[] r = this.settingsRowRect(l, i);
            if (inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
                g.fill(r[0] + 1, r[1] + (i == 0 ? 1 : 0), r[2] - 1, r[3] - (i == count - 1 ? 1 : 0), 0xFFE5E5EA);
            }
            if (i > 0) {
                g.fill(r[0] + 6, r[1], r[2], r[1] + 1, 0xFFD8D8DE);
            }
            int textX = r[0] + 6;
            String label;
            boolean selected = false;
            if (this.settingsPage == SETTINGS_PAGE_WALLPAPER) {
                String id = SmartDispatchAppItem.WALLPAPERS[i];

                g.fill(r[0] + 4, r[1] + 2, r[0] + 16, r[3] - 2, 0xFF121216);
                drawWallpaper(g, id, r[0] + 5, r[1] + 3, r[0] + 15, r[3] - 3);
                textX = r[0] + 21;
                label = ModText.translatable("gui.mineturismo.smart_dispatch.wallpaper_" + id).getString();
                selected = id.equals(this.wallpaper);
            } else if (this.settingsPage == SETTINGS_PAGE_MODEL) {
                String id = SmartDispatchAppItem.PHONE_MODELS[i];
                ItemStack icon = new ItemStack(com.examplebett.item.AiObjectModItems.SMART_DISPATCH_APP);
                SmartDispatchAppItem.applyPhoneModelIcon(icon, id);
                g.renderItem(icon, r[0] + 3, r[1] + 2);
                textX = r[0] + 21;
                label = PHONE_MODEL_NAMES[i] != null ? PHONE_MODEL_NAMES[i]
                        : ModText.translatable("gui.mineturismo.smart_dispatch.model_classic").getString();
                selected = id.equals(this.phoneModel);
            } else {
                label = ModText.translatable(i == 0 ? "gui.mineturismo.smart_dispatch.settings_wallpaper"
                        : i == 1 ? "gui.mineturismo.smart_dispatch.settings_model"
                        : "gui.mineturismo.smart_dispatch.settings_controls").getString();

                int ax = r[2] - 8;
                int ay = (r[1] + r[3]) / 2;
                for (int k = 0; k < 3; k++) {
                    g.fill(ax + k, ay - 3 + k, ax + k + 1, ay - 1 + k, 0xFFB4B4BA);
                    g.fill(ax + k, ay + 1 - k, ax + k + 1, ay + 3 - k, 0xFFB4B4BA);
                }
            }
            this.drawSmallText(g, this.fit(label, r[2] - textX - 14), textX, (r[1] + r[3]) / 2 - 3, 0xFF202024);
            if (selected) {

                int cx0 = r[2] - 11;
                int cy0 = (r[1] + r[3]) / 2;
                int[][] check = {{0, 0}, {1, 1}, {2, 2}, {3, 1}, {4, 0}, {5, -1}, {6, -2}, {7, -3}};
                for (int[] p : check) {
                    g.fill(cx0 + p[0], cy0 + p[1] - 1, cx0 + p[0] + 1, cy0 + p[1] + 1, 0xFF0A84FF);
                }
            }
        }
    }

    private boolean handleSettingsClick(Layout l, double mouseX, double mouseY) {
        if (this.settingsPage == SETTINGS_PAGE_CONTROLS) {
            return this.handleControlsClick(l, mouseX, mouseY);
        }
        for (int i = 0; i < this.settingsRowCount(); i++) {
            int[] r = this.settingsRowRect(l, i);
            if (!inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
                continue;
            }
            if (this.settingsPage == SETTINGS_PAGE_MAIN) {
                this.settingsPage = i == 0 ? SETTINGS_PAGE_WALLPAPER : i == 1 ? SETTINGS_PAGE_MODEL : SETTINGS_PAGE_CONTROLS;
                this.rebindingKey = null;
                return true;
            }
            if (this.settingsPage == SETTINGS_PAGE_WALLPAPER) {
                this.wallpaper = SmartDispatchAppItem.WALLPAPERS[i];
            } else {
                this.phoneModel = SmartDispatchAppItem.PHONE_MODELS[i];
                this.skin = PhoneSkin.byId(this.phoneModel);
            }
            com.examplebett.network.SetPhoneStylePayload payload = new com.examplebett.network.SetPhoneStylePayload(this.phoneModel, this.wallpaper);
            ModClientNetworking.sendToServer(com.examplebett.network.SetPhoneStylePayload.ID, payload, com.examplebett.network.SetPhoneStylePayload::write);
            return true;
        }
        return false;
    }

    private static final int WEATHER_SNOW = 3;
    private static final int WEATHER_BUTTON_HEIGHT = 14;
    private static final int WEATHER_BUTTON_GAP = 3;

    private static final int WEATHER_ICON_UNIT = 2;
    private static final int[] WEATHER_PICKS = {SmartDispatchAppItem.WEATHER_CLEAR, SmartDispatchAppItem.WEATHER_RAIN, SmartDispatchAppItem.WEATHER_THUNDER};
    private static final String[] WEATHER_KEYS = {"weather_clear", "weather_rain", "weather_thunder", "weather_snow"};

    private int currentWeather() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) {
            return SmartDispatchAppItem.WEATHER_CLEAR;
        }
        net.minecraft.client.multiplayer.ClientLevel level = this.minecraft.level;
        if (!level.isRaining()) {
            return SmartDispatchAppItem.WEATHER_CLEAR;
        }
        net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos(this.minecraft.player);

        net.minecraft.world.level.biome.Biome.Precipitation precipitation = level.getBiome(pos).getPrecipitation();
        if (precipitation == net.minecraft.world.level.biome.Biome.Precipitation.NONE) {
            return SmartDispatchAppItem.WEATHER_CLEAR;
        }
        if (level.isThundering()) {
            return SmartDispatchAppItem.WEATHER_THUNDER;
        }
        return precipitation == net.minecraft.world.level.biome.Biome.Precipitation.SNOW ? WEATHER_SNOW : SmartDispatchAppItem.WEATHER_RAIN;
    }

    private Component currentPlaceName() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) {
            return ModText.literal("-");
        }

        return this.minecraft.level.getBiome(new net.minecraft.core.BlockPos(this.minecraft.player)).getName();
    }

    private int weatherButtonTop(Layout l, int index) {

        int changeTop = l.sy1() - WEATHER_BUTTON_HEIGHT - 5;
        return index == WEATHER_PICKS.length ? changeTop
                : changeTop - (WEATHER_PICKS.length - index) * (WEATHER_BUTTON_HEIGHT + WEATHER_BUTTON_GAP);
    }

    private void drawWeatherButton(GuiGraphics g, Layout l, int top, String label, int color, int hoverColor, int textColor,
                                   int mouseX, int mouseY) {
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        boolean hover = inside(mouseX, mouseY, x0, top, x1, top + WEATHER_BUTTON_HEIGHT);
        fillPixelRounded(g, x0, top, x1, top + WEATHER_BUTTON_HEIGHT, 1, hover ? hoverColor : color);
        g.drawString(this.font, label, (x0 + x1 - com.examplebett.compat.LegacyGui.width(this.font, label)) / 2, top + 3, textColor, false);
    }

    private void drawWeather(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int weather = this.currentWeather();

        int skyTop;
        int skyBottom;
        switch (weather) {
            case SmartDispatchAppItem.WEATHER_RAIN -> { skyTop = 0xFF7A8796; skyBottom = 0xFF4E5A68; }
            case SmartDispatchAppItem.WEATHER_THUNDER -> { skyTop = 0xFF4A4F5C; skyBottom = 0xFF262A33; }
            case WEATHER_SNOW -> { skyTop = 0xFFB9C9DA; skyBottom = 0xFF8EA3BA; }
            default -> { skyTop = 0xFF6FB8F5; skyBottom = 0xFF3D8BD9; }
        }
        int bands = Math.max(1, (l.sy1() - top) / (PX * 2));
        for (int i = 0; i < bands; i++) {
            int bandTop = top + i * PX * 2;
            g.fill(l.sx0(), bandTop, l.sx1(), i == bands - 1 ? l.sy1() : bandTop + PX * 2, lerpColor(skyTop, skyBottom, (float) i / bands));
        }
        int cx = (l.sx0() + l.sx1()) / 2;
        int iconSize = 16 * WEATHER_ICON_UNIT;
        int iconTop = top + 8;
        this.drawWeatherIcon(g, cx - iconSize / 2, iconTop, weather);

        int textTop = iconTop + iconSize + 6;
        String placeLabel = ModText.translatable("gui.mineturismo.smart_dispatch.weather_location").getString();
        int labelWidth = Math.round(com.examplebett.compat.LegacyGui.width(this.font, placeLabel) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, placeLabel, cx - labelWidth / 2, textTop, 0xDDFFFFFF);
        String place = this.currentPlaceName().getString();
        int room = l.sx1() - l.sx0() - 8;
        if (com.examplebett.compat.LegacyGui.width(this.font, place) <= room) {
            g.drawString(this.font, place, cx - com.examplebett.compat.LegacyGui.width(this.font, place) / 2, textTop + 9, 0xFFFFFFFF, true);
        } else {
            String fitted = this.fit(place, room);
            this.drawSmallText(g, fitted, cx - Math.round(com.examplebett.compat.LegacyGui.width(this.font, fitted) * SMALL_TEXT_SCALE) / 2, textTop + 10, 0xFFFFFFFF);
        }
        String weatherName = ModText.translatable("gui.mineturismo.smart_dispatch." + WEATHER_KEYS[weather]).getString();
        g.drawString(this.font, weatherName, cx - com.examplebett.compat.LegacyGui.width(this.font, weatherName) / 2, textTop + 22, 0xFFFFFFFF, true);

        if (this.weatherPickerOpen) {
            for (int i = 0; i < WEATHER_PICKS.length; i++) {
                String label = ModText.translatable("gui.mineturismo.smart_dispatch." + WEATHER_KEYS[WEATHER_PICKS[i]]).getString();
                this.drawWeatherButton(g, l, this.weatherButtonTop(l, i), label, 0xFFFFFFFF, 0xFFFFF1B8, 0xFF202024, mouseX, mouseY);
            }
        }
        String change = ModText.translatable("gui.mineturismo.smart_dispatch.weather_change").getString();
        this.drawWeatherButton(g, l, this.weatherButtonTop(l, WEATHER_PICKS.length), change, 0xFF2A2A30, 0xFF44444E, 0xFFFFFFFF, mouseX, mouseY);
    }

    private void cell(GuiGraphics g, int ox, int oy, int ux, int uy, int w, int h, int color) {
        int u = WEATHER_ICON_UNIT;
        g.fill(ox + ux * u, oy + uy * u, ox + (ux + w) * u, oy + (uy + h) * u, color);
    }

    private void disc(GuiGraphics g, int ox, int oy, int cx, int cy, int r, int color) {
        for (int dy = -r; dy <= r; dy++) {
            int w = (int) Math.round(Math.sqrt(r * r - dy * dy));
            this.cell(g, ox, oy, cx - w, cy + dy, w * 2 + 1, 1, color);
        }
    }

    private void cloud(GuiGraphics g, int ox, int oy, int color, int shade) {
        this.disc(g, ox, oy, 5, 6, 3, color);
        this.disc(g, ox, oy, 9, 5, 4, color);
        this.cell(g, ox, oy, 2, 6, 12, 4, color);
        this.cell(g, ox, oy, 2, 9, 12, 1, shade);
    }

    private void drawWeatherIcon(GuiGraphics g, int ox, int oy, int weather) {
        switch (weather) {
            case SmartDispatchAppItem.WEATHER_CLEAR -> {
                int sun = 0xFFFFD640;
                int rim = 0xFFF5A01E;
                this.cell(g, ox, oy, 7, 0, 2, 2, sun);
                this.cell(g, ox, oy, 7, 14, 2, 2, sun);
                this.cell(g, ox, oy, 0, 7, 2, 2, sun);
                this.cell(g, ox, oy, 14, 7, 2, 2, sun);
                this.cell(g, ox, oy, 2, 2, 2, 2, sun);
                this.cell(g, ox, oy, 12, 2, 2, 2, sun);
                this.cell(g, ox, oy, 2, 12, 2, 2, sun);
                this.cell(g, ox, oy, 12, 12, 2, 2, sun);
                this.disc(g, ox, oy, 8, 8, 4, rim);
                this.disc(g, ox, oy, 8, 8, 3, sun);
                this.cell(g, ox, oy, 6, 6, 1, 1, 0xFFFFF4B0);
            }
            case SmartDispatchAppItem.WEATHER_RAIN -> {
                this.cloud(g, ox, oy, 0xFFE4E8EE, 0xFFB4BCC8);
                int drop = 0xFF5FB6FF;
                this.cell(g, ox, oy, 4, 11, 1, 2, drop);
                this.cell(g, ox, oy, 8, 12, 1, 2, drop);
                this.cell(g, ox, oy, 12, 11, 1, 2, drop);
                this.cell(g, ox, oy, 6, 14, 1, 2, drop);
                this.cell(g, ox, oy, 10, 14, 1, 2, drop);
            }
            case SmartDispatchAppItem.WEATHER_THUNDER -> {
                this.cloud(g, ox, oy, 0xFF8A909C, 0xFF5E6470);
                int bolt = 0xFFFFE14A;
                this.cell(g, ox, oy, 8, 10, 2, 1, bolt);
                this.cell(g, ox, oy, 7, 11, 2, 1, bolt);
                this.cell(g, ox, oy, 6, 12, 4, 1, bolt);
                this.cell(g, ox, oy, 8, 13, 1, 1, bolt);
                this.cell(g, ox, oy, 7, 14, 1, 1, bolt);
                this.cell(g, ox, oy, 6, 15, 1, 1, bolt);
            }
            default -> {
                this.cloud(g, ox, oy, 0xFFF4F6FA, 0xFFC4CCD8);
                int flake = 0xFFFFFFFF;
                this.cell(g, ox, oy, 4, 12, 1, 1, flake);
                this.cell(g, ox, oy, 8, 11, 1, 1, flake);
                this.cell(g, ox, oy, 12, 12, 1, 1, flake);
                this.cell(g, ox, oy, 6, 14, 1, 1, flake);
                this.cell(g, ox, oy, 10, 14, 1, 1, flake);
            }
        }
    }

    private boolean handleWeatherClick(Layout l, double mouseX, double mouseY) {
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        int changeTop = this.weatherButtonTop(l, WEATHER_PICKS.length);
        if (inside(mouseX, mouseY, x0, changeTop, x1, changeTop + WEATHER_BUTTON_HEIGHT)) {
            this.weatherPickerOpen = !this.weatherPickerOpen;
            return true;
        }
        if (this.weatherPickerOpen) {
            for (int i = 0; i < WEATHER_PICKS.length; i++) {
                int top = this.weatherButtonTop(l, i);
                if (inside(mouseX, mouseY, x0, top, x1, top + WEATHER_BUTTON_HEIGHT)) {
                    com.examplebett.network.SetAppWeatherPayload payload = new com.examplebett.network.SetAppWeatherPayload(WEATHER_PICKS[i]);
                    ModClientNetworking.sendToServer(com.examplebett.network.SetAppWeatherPayload.ID, payload, com.examplebett.network.SetAppWeatherPayload::write);
                    this.weatherPickerOpen = false;
                    return true;
                }
            }
        }
        return false;
    }

    private static final int EMERGENCY_BUTTON_HEIGHT = 30;

    private int[] emergencyButtonRect(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int y0 = (top + l.sy1()) / 2 - EMERGENCY_BUTTON_HEIGHT / 2;
        return new int[]{l.sx0() + 10, y0, l.sx1() - 10, y0 + EMERGENCY_BUTTON_HEIGHT};
    }

    private void drawEmergency(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFFF6F0F0);
        String lead = ModText.translatable("gui.mineturismo.smart_dispatch.emergency_lead").getString();
        String leadFitted = this.fit(lead, l.sx1() - l.sx0() - 8);
        this.drawSmallText(g, leadFitted, cx - Math.round(com.examplebett.compat.LegacyGui.width(this.font, leadFitted) * SMALL_TEXT_SCALE) / 2, top + 10, 0xFF5A1C1C);
        int[] r = this.emergencyButtonRect(l);
        boolean hover = inside(mouseX, mouseY, r[0], r[1], r[2], r[3]);

        fillPixelRounded(g, r[0], r[1] + 2, r[2], r[3] + 2, PX, 0xFF8E1414);
        fillPixelRounded(g, r[0], r[1], r[2], r[3], PX, hover ? 0xFFEF4A3E : 0xFFD32F2F);
        String label = ModText.translatable("gui.mineturismo.smart_dispatch.emergency_call").getString();
        g.drawString(this.font, label, cx - com.examplebett.compat.LegacyGui.width(this.font, label) / 2, (r[1] + r[3]) / 2 - 4, 0xFFFFFFFF, true);

        Component note = ModText.translatable("gui.mineturismo.smart_dispatch.emergency_note");
        List<String> lines = com.examplebett.compat.LegacyGui.split(this.font, note, Math.round((l.sx1() - l.sx0() - 12) / SMALL_TEXT_SCALE));
        for (int i = 0; i < Math.min(3, lines.size()); i++) {
            int w = Math.round(com.examplebett.compat.LegacyGui.width(this.font, lines.get(i)) * SMALL_TEXT_SCALE);
            this.drawSmallText(g, lines.get(i), cx - w / 2, r[3] + 10 + i * 8, 0xFF6A6A70);
        }
    }

    private boolean handleEmergencyClick(Layout l, double mouseX, double mouseY) {
        int[] r = this.emergencyButtonRect(l);
        if (!inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
            return false;
        }
        com.examplebett.network.RequestEmergencyCallPayload payload = new com.examplebett.network.RequestEmergencyCallPayload();
        ModClientNetworking.sendToServer(com.examplebett.network.RequestEmergencyCallPayload.ID, payload, com.examplebett.network.RequestEmergencyCallPayload::write);
        this.onClose();
        return true;
    }

    private static final int DETONATOR_BUTTON_SIZE = 40;

    private int[] detonatorButtonRect(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        int y0 = (top + l.sy1()) / 2 - DETONATOR_BUTTON_SIZE / 2 - 6;
        return new int[]{cx - DETONATOR_BUTTON_SIZE / 2, y0, cx + DETONATOR_BUTTON_SIZE / 2, y0 + DETONATOR_BUTTON_SIZE};
    }

    private void drawDetonator(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFF1C1D21);
        String lead = ModText.translatable("gui.mineturismo.smart_dispatch.detonator_lead").getString();
        String leadFitted = this.fit(lead, l.sx1() - l.sx0() - 8);
        this.drawSmallText(g, leadFitted, cx - Math.round(com.examplebett.compat.LegacyGui.width(this.font, leadFitted) * SMALL_TEXT_SCALE) / 2, top + 10, 0xFFFFC94A);
        int[] r = this.detonatorButtonRect(l);
        boolean hover = inside(mouseX, mouseY, r[0], r[1], r[2], r[3]);

        int bcx = (r[0] + r[2]) / 2;
        int bcy = (r[1] + r[3]) / 2;
        int radius = DETONATOR_BUTTON_SIZE / 2;
        fillPixelCircle(g, bcx, bcy + 1, radius + 4, 0xFF96989F);
        fillPixelCircle(g, bcx, bcy + 3, radius, 0xFF780E12);
        fillPixelCircle(g, bcx, bcy, radius, hover ? 0xFFF04040 : 0xFFDE2424);
        String label = ModText.translatable("gui.mineturismo.smart_dispatch.detonator_button").getString();
        g.drawString(this.font, label, cx - com.examplebett.compat.LegacyGui.width(this.font, label) / 2, (r[1] + r[3]) / 2 - 4, 0xFFFFFFFF, true);

        int stripeTop = r[3] + 10;
        for (int x = l.sx0(); x < l.sx1(); x += PX * 2) {
            int stripe = ((x - l.sx0()) / (PX * 2)) % 2 == 0 ? 0xFFF6C41E : 0xFF18181A;
            g.fill(x, stripeTop, Math.min(l.sx1(), x + PX * 2), stripeTop + PX * 2, stripe);
        }
        Component note = ModText.translatable("gui.mineturismo.smart_dispatch.detonator_note");
        List<String> lines = com.examplebett.compat.LegacyGui.split(this.font, note, Math.round((l.sx1() - l.sx0() - 12) / SMALL_TEXT_SCALE));
        for (int i = 0; i < Math.min(3, lines.size()); i++) {
            int w = Math.round(com.examplebett.compat.LegacyGui.width(this.font, lines.get(i)) * SMALL_TEXT_SCALE);
            this.drawSmallText(g, lines.get(i), cx - w / 2, stripeTop + PX * 2 + 6 + i * 8, 0xFFB0B0B8);
        }
    }

    private boolean handleDetonatorClick(Layout l, double mouseX, double mouseY) {
        int[] r = this.detonatorButtonRect(l);
        if (!inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
            return false;
        }
        com.examplebett.network.DetonateBombsPayload payload = new com.examplebett.network.DetonateBombsPayload();
        ModClientNetworking.sendToServer(com.examplebett.network.DetonateBombsPayload.ID, payload, com.examplebett.network.DetonateBombsPayload::write);
        this.onClose();
        return true;
    }

    private static final int MUSIC_BUTTON_HEIGHT = 16;
    private static final int MUSIC_PINK = 0xFFF5354F;
    private static final int MUSIC_PINK_HOVER = 0xFFFF5A70;

    private Component musicStatus() {
        com.examplebett.client.sound.ChorusMusicTracks.Track current = com.examplebett.client.sound.ChorusMusicPlayer.current();
        return current == null ? ModText.translatable("gui.mineturismo.smart_dispatch.music_stopped")
                : ModText.translatable("gui.mineturismo.smart_dispatch.music_now_playing", current.title());
    }

    private int[][] musicRects(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        int browseTop = top + 34;
        int volumeTop = browseTop + MUSIC_BUTTON_HEIGHT + 22;
        int rowTop = volumeTop + 18;
        int half = (x1 - x0 - 4) / 2;
        return new int[][]{
                {x0, browseTop, x1, browseTop + MUSIC_BUTTON_HEIGHT},
                {x0, volumeTop, x1, volumeTop + 8},
                {x0, rowTop, x0 + half, rowTop + MUSIC_BUTTON_HEIGHT},
                {x1 - half, rowTop, x1, rowTop + MUSIC_BUTTON_HEIGHT}
        };
    }

    private double musicVolume() {
        return this.minecraft.options.getSoundSourceVolume(net.minecraft.sounds.SoundSource.MUSIC);
    }

    private void setMusicVolume(double volume) {
        this.minecraft.options.setSoundCategoryVolume(net.minecraft.sounds.SoundSource.MUSIC, (float) volume);
    }

    private void drawMusicButton(GuiGraphics g, int[] r, String label, int color, int hoverColor, int textColor, int mouseX, int mouseY) {
        boolean hover = this.pending == null && inside(mouseX, mouseY, r[0], r[1], r[2], r[3]);
        fillPixelRounded(g, r[0], r[1], r[2], r[3], 1, hover ? hoverColor : color);
        String fitted = this.fit(label, r[2] - r[0] - 4);
        int w = Math.round(com.examplebett.compat.LegacyGui.width(this.font, fitted) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, fitted, (r[0] + r[2] - w) / 2, (r[1] + r[3]) / 2 - 3, textColor);
    }

    private void drawMusicHome(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFFFFF5F7);

        String status = this.fit(this.musicStatus().getString(), l.sx1() - l.sx0() - 22);
        int statusWidth = Math.round(com.examplebett.compat.LegacyGui.width(this.font, status) * SMALL_TEXT_SCALE);
        int noteX = cx - (statusWidth + 10) / 2;
        int noteY = top + 10;
        g.fill(noteX + 3, noteY, noteX + 4, noteY + 6, MUSIC_PINK);
        g.fill(noteX + 4, noteY, noteX + 6, noteY + 1, MUSIC_PINK);
        g.fill(noteX + 1, noteY + 5, noteX + 4, noteY + 7, MUSIC_PINK);
        this.drawSmallText(g, status, noteX + 10, noteY + 1, 0xFF3A2A2E);

        int[][] r = this.musicRects(l);
        this.drawMusicButton(g, r[0], ModText.translatable("gui.mineturismo.smart_dispatch.music_browse").getString(),
                MUSIC_PINK, MUSIC_PINK_HOVER, 0xFFFFFFFF, mouseX, mouseY);

        double volume = this.musicVolume();
        String volumeLabel = ModText.translatable("soundCategory.music").getString() + ": " + Math.round(volume * 100) + "%";
        this.drawSmallText(g, volumeLabel, r[1][0], r[1][1] - 9, 0xFF3A2A2E);
        int[] bar = r[1];
        int trackTop = (bar[1] + bar[3]) / 2 - 1;
        g.fill(bar[0], trackTop, bar[2], trackTop + 3, 0xFFE2C6CC);
        int knobX = bar[0] + (int) Math.round((bar[2] - bar[0] - 6) * volume);
        g.fill(bar[0], trackTop, knobX + 3, trackTop + 3, MUSIC_PINK);
        fillPixelRounded(g, knobX, bar[1], knobX + 6, bar[3], 1, this.draggingVolume ? MUSIC_PINK_HOVER : 0xFFFFFFFF);
        g.fill(knobX + 2, bar[1] + 2, knobX + 4, bar[3] - 2, MUSIC_PINK);

        Component loopState = ModText.translatable(com.examplebett.client.sound.ChorusMusicPlayer.isLoop() ? "options.on" : "options.off");
        boolean loopOn = com.examplebett.client.sound.ChorusMusicPlayer.isLoop();
        this.drawMusicButton(g, r[2], ModText.translatable("gui.mineturismo.smart_dispatch.music_loop", loopState).getString(),
                loopOn ? MUSIC_PINK : 0xFFDCC9CD, loopOn ? MUSIC_PINK_HOVER : 0xFFE8D8DB, loopOn ? 0xFFFFFFFF : 0xFF3A2A2E, mouseX, mouseY);
        this.drawMusicButton(g, r[3], ModText.translatable("gui.mineturismo.smart_dispatch.music_stop").getString(),
                0xFF3A2A2E, 0xFF54444A, 0xFFFFFFFF, mouseX, mouseY);
    }

    private void setVolumeFromMouse(Layout l, double mouseX) {
        int[] bar = this.musicRects(l)[1];
        double t = (mouseX - bar[0] - 3) / Math.max(1.0, bar[2] - bar[0] - 6);
        this.setMusicVolume(Math.max(0.0, Math.min(1.0, t)));
    }

    private boolean handleMusicHomeClick(Layout l, double mouseX, double mouseY) {
        int[][] r = this.musicRects(l);
        if (inside(mouseX, mouseY, r[0][0], r[0][1], r[0][2], r[0][3])) {
            this.musicBrowsing = true;
            this.scrollRow = 0;
            return true;
        }
        if (inside(mouseX, mouseY, r[1][0] - 2, r[1][1] - 3, r[1][2] + 2, r[1][3] + 3)) {
            this.draggingVolume = true;
            this.setVolumeFromMouse(l, mouseX);
            return true;
        }
        if (inside(mouseX, mouseY, r[2][0], r[2][1], r[2][2], r[2][3])) {
            com.examplebett.client.sound.ChorusMusicPlayer.toggleLoop();
            return true;
        }
        if (inside(mouseX, mouseY, r[3][0], r[3][1], r[3][2], r[3][3])) {
            com.examplebett.client.sound.ChorusMusicPlayer.stop();
            return true;
        }
        return false;
    }

    private static final String[] TIME_KEYS = {"time_morning", "time_noon", "time_evening", "time_night", "time_midnight"};
    private static final float CLOCK_TIME_SCALE = 3.0f;

    private int clockButtonTop(Layout l, int index) {

        int changeTop = l.sy1() - WEATHER_BUTTON_HEIGHT - 5;
        return index == TIME_KEYS.length ? changeTop
                : changeTop - (TIME_KEYS.length - index) * (WEATHER_BUTTON_HEIGHT + WEATHER_BUTTON_GAP);
    }

    private void drawClock(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        long dayTime = this.minecraft.level == null ? 0L : this.levelDayTime();
        int hours = (int) ((Math.floorMod(dayTime, 24000L) / 1000L + 6L) % 24L);

        int skyTop;
        int skyBottom;
        if (hours >= 6 && hours < 17) {
            skyTop = 0xFF6FB8F5;
            skyBottom = 0xFF3D8BD9;
        } else if (hours >= 17 && hours < 19) {
            skyTop = 0xFFF7A35C;
            skyBottom = 0xFFB4466A;
        } else {
            skyTop = 0xFF1E2A4A;
            skyBottom = 0xFF0B1022;
        }
        int bands = Math.max(1, (l.sy1() - top) / (PX * 2));
        for (int i = 0; i < bands; i++) {
            int bandTop = top + i * PX * 2;
            g.fill(l.sx0(), bandTop, l.sx1(), i == bands - 1 ? l.sy1() : bandTop + PX * 2, lerpColor(skyTop, skyBottom, (float) i / bands));
        }
        int cx = (l.sx0() + l.sx1()) / 2;

        String placeLabel = ModText.translatable("gui.mineturismo.smart_dispatch.weather_location").getString();
        int labelWidth = Math.round(com.examplebett.compat.LegacyGui.width(this.font, placeLabel) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, placeLabel, cx - labelWidth / 2, top + 8, 0xDDFFFFFF);
        String place = this.currentPlaceName().getString();
        int room = l.sx1() - l.sx0() - 8;
        if (com.examplebett.compat.LegacyGui.width(this.font, place) <= room) {
            g.drawString(this.font, place, cx - com.examplebett.compat.LegacyGui.width(this.font, place) / 2, top + 17, 0xFFFFFFFF, true);
        } else {
            String fitted = this.fit(place, room);
            this.drawSmallText(g, fitted, cx - Math.round(com.examplebett.compat.LegacyGui.width(this.font, fitted) * SMALL_TEXT_SCALE) / 2, top + 18, 0xFFFFFFFF);
        }

        String time = this.currentTimeText();
        int timeWidth = Math.round(com.examplebett.compat.LegacyGui.width(this.font, time) * CLOCK_TIME_SCALE);
        int timeTop = top + 32;
        g.pose().pushPose();
        g.pose().scale(CLOCK_TIME_SCALE, CLOCK_TIME_SCALE, 1.0f);
        g.drawString(this.font, time, Math.round((cx - timeWidth / 2f) / CLOCK_TIME_SCALE), Math.round(timeTop / CLOCK_TIME_SCALE), 0xFFFFFFFF, true);
        g.pose().popPose();

        String day = ModText.translatable("gui.mineturismo.smart_dispatch.clock_day", dayTime / 24000L + 1).getString();
        int dayWidth = Math.round(com.examplebett.compat.LegacyGui.width(this.font, day) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, day, cx - dayWidth / 2, timeTop + Math.round(this.font.lineHeight * CLOCK_TIME_SCALE) + 2, 0xDDFFFFFF);

        if (this.clockPickerOpen) {
            for (int i = 0; i < TIME_KEYS.length; i++) {
                String label = ModText.translatable("gui.mineturismo.smart_dispatch." + TIME_KEYS[i]).getString();
                this.drawWeatherButton(g, l, this.clockButtonTop(l, i), label, 0xFFFFFFFF, 0xFFFFF1B8, 0xFF202024, mouseX, mouseY);
            }
        }
        String change = ModText.translatable("gui.mineturismo.smart_dispatch.time_change").getString();
        this.drawWeatherButton(g, l, this.clockButtonTop(l, TIME_KEYS.length), change, 0xFF2A2A30, 0xFF44444E, 0xFFFFFFFF, mouseX, mouseY);
    }

    private boolean handleClockClick(Layout l, double mouseX, double mouseY) {
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        int changeTop = this.clockButtonTop(l, TIME_KEYS.length);
        if (inside(mouseX, mouseY, x0, changeTop, x1, changeTop + WEATHER_BUTTON_HEIGHT)) {
            this.clockPickerOpen = !this.clockPickerOpen;
            return true;
        }
        if (this.clockPickerOpen) {
            for (int i = 0; i < TIME_KEYS.length; i++) {
                int top = this.clockButtonTop(l, i);
                if (inside(mouseX, mouseY, x0, top, x1, top + WEATHER_BUTTON_HEIGHT)) {
                    com.examplebett.network.SetAppTimePayload payload = new com.examplebett.network.SetAppTimePayload(i);
                    ModClientNetworking.sendToServer(com.examplebett.network.SetAppTimePayload.ID, payload, com.examplebett.network.SetAppTimePayload::write);
                    this.clockPickerOpen = false;
                    return true;
                }
            }
        }
        return false;
    }

    private static final int MASSIVE_BUTTON_HEIGHT = 32;

    private static final int MASSIVE_BUTTON_TEXT_Y = 12;
    private static final int MASSIVE_BLUE = 0xFF1E4FA8;
    private static final int MASSIVE_BLUE_HOVER = 0xFF2D63C8;
    private static final int MASSIVE_YELLOW = 0xFFF2B705;

    private int[][] massiveAutoRects(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        int first = top + 30;
        int second = first + MASSIVE_BUTTON_HEIGHT + 10;
        return new int[][]{{x0, first, x1, first + MASSIVE_BUTTON_HEIGHT}, {x0, second, x1, second + MASSIVE_BUTTON_HEIGHT}};
    }

    private void drawMassiveAuto(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFFF4F1EA);
        g.fill(l.sx0(), top, l.sx1(), top + 2, MASSIVE_YELLOW);
        String lead = this.fit(ModText.translatable("gui.mineturismo.massive_auto.lead").getString(), l.sx1() - l.sx0() - 8);
        this.drawSmallText(g, lead, cx - Math.round(com.examplebett.compat.LegacyGui.width(this.font, lead) * SMALL_TEXT_SCALE) / 2, top + 12, 0xFF3A3A40);
        int[][] r = this.massiveAutoRects(l);

        boolean buyHover = this.pending == null && inside(mouseX, mouseY, r[0][0], r[0][1], r[0][2], r[0][3]);
        fillPixelRounded(g, r[0][0], r[0][1] + 2, r[0][2], r[0][3] + 2, PX, 0xFFB08300);
        fillPixelRounded(g, r[0][0], r[0][1], r[0][2], r[0][3], PX, buyHover ? 0xFFFFC72C : MASSIVE_YELLOW);
        String buy = this.fit(ModText.translatable("gui.mineturismo.massive_auto.buy").getString(), r[0][2] - r[0][0] - 6);
        g.drawString(this.font, buy, cx - com.examplebett.compat.LegacyGui.width(this.font, buy) / 2, r[0][1] + MASSIVE_BUTTON_TEXT_Y, 0xFF12306A, false);

        boolean hover = this.pending == null && inside(mouseX, mouseY, r[1][0], r[1][1], r[1][2], r[1][3]);
        fillPixelRounded(g, r[1][0], r[1][1] + 2, r[1][2], r[1][3] + 2, PX, 0xFF12306A);
        fillPixelRounded(g, r[1][0], r[1][1], r[1][2], r[1][3], PX, hover ? MASSIVE_BLUE_HOVER : MASSIVE_BLUE);
        String appraisal = this.fit(ModText.translatable("gui.mineturismo.massive_auto.appraisal").getString(), r[1][2] - r[1][0] - 6);
        g.drawString(this.font, appraisal, cx - com.examplebett.compat.LegacyGui.width(this.font, appraisal) / 2, r[1][1] + MASSIVE_BUTTON_TEXT_Y, 0xFFFFFFFF, false);
    }

    private boolean handleMassiveAutoClick(Layout l, double mouseX, double mouseY) {
        int[][] r = this.massiveAutoRects(l);
        if (inside(mouseX, mouseY, r[0][0], r[0][1], r[0][2], r[0][3])) {
            this.minecraft.setScreen(new MassiveAutoUsedCarScreen());
            return true;
        }
        if (inside(mouseX, mouseY, r[1][0], r[1][1], r[1][2], r[1][3])) {

            com.examplebett.network.OpenMassiveAutoAppraisalPayload payload = new com.examplebett.network.OpenMassiveAutoAppraisalPayload();
            ModClientNetworking.sendToServer(com.examplebett.network.OpenMassiveAutoAppraisalPayload.ID, payload, com.examplebett.network.OpenMassiveAutoAppraisalPayload::write);
            return true;
        }
        return false;
    }

    private static final int SLOT_REELS = SmartDispatchAppItem.SLOT_REELS;
    private static final int SLOT_CELL_HEIGHT = 30;
    private static final int SLOT_WINDOW_HEIGHT = SLOT_CELL_HEIGHT * 2 + 6;
    private static final int SLOT_REEL_GAP = 3;
    private static final int SLOT_SPIN_BUTTON_HEIGHT = 18;
    private static final int SLOT_AUTO_BUTTON_HEIGHT = 13;

    private static final double SLOT_MAX_SPEED = 14.0;
    private static final double SLOT_ACCEL_SEC = 0.25;

    private static final double SLOT_MIN_SPIN_SEC = 0.8;
    private static final double SLOT_DECEL_SEC = 0.9;

    private static final double SLOT_STOP_INTERVAL_SEC = 0.45;

    private static final double SLOT_BLUR_SPEED = 5.0;

    private static final long SLOT_TIMEOUT_MS = 4000L;

    private static final long SLOT_AUTO_DELAY_MS = 900L;
    private static final long SLOT_AUTO_DELAY_JACKPOT_MS = 2600L;
    private static final long SLOT_TICK_MIN_INTERVAL_MS = 35L;
    private static final int SLOT_PINK = 0xFFE6197E;
    private static final int SLOT_ORANGE = 0xFFF7931E;
    private static final int SLOT_GOLD = 0xFFFFD54F;
    private static final int[] SLOT_DECOY_EMERALDS = {2, 3, 5, 8, 10, 15, 20, 30, 50};

    private enum SlotPhase { IDLE, SPINNING, STOPPING, RESULT }

    private SlotPhase slotPhase = SlotPhase.IDLE;

    private final double[] reelRest = new double[SLOT_REELS];

    private final double[] reelBase = new double[SLOT_REELS];
    private final double[] reelDecelFrom = new double[SLOT_REELS];
    private final int[] reelTarget = new int[SLOT_REELS];
    private final long[] reelDecelStartMs = new long[SLOT_REELS];
    private final boolean[] reelStopSoundPlayed = new boolean[SLOT_REELS];

    private final List<java.util.Map<Integer, SmartDispatchAppItem.SlotPrize>> reelResults = new ArrayList<>();
    private final java.util.Map<String, ItemStack> slotStackCache = new java.util.HashMap<>();
    private long slotSpinStartMs;
    private long slotResultShownMs;

    private List<SmartDispatchAppItem.SlotPrize> slotResult = null;
    private boolean slotAuto = false;
    private Component slotMessage = null;
    private long slotLastTickMs;
    private long slotLastTickCell = Long.MIN_VALUE;

    private int slotFrozenEmeralds = -1;

    private int slotEmeraldsGained = 0;
    private static final long SLOT_GAIN_POPUP_MS = 1500L;

    {
        for (int i = 0; i < SLOT_REELS; i++) {
            this.reelResults.add(new java.util.HashMap<>());
        }
    }

    public static void onSlotResult(List<SmartDispatchAppItem.SlotPrize> prizes) {
        if (net.minecraft.client.Minecraft.getInstance().screen instanceof SmartDispatchAppScreen screen) {
            screen.receiveSlotResult(prizes);
        }
    }

    private void receiveSlotResult(List<SmartDispatchAppItem.SlotPrize> prizes) {
        if (this.slotPhase != SlotPhase.SPINNING) {
            return;
        }
        long now = System.currentTimeMillis();
        long firstDecel = Math.max(now, this.slotSpinStartMs + (long) (SLOT_MIN_SPIN_SEC * 1000));
        for (int i = 0; i < SLOT_REELS; i++) {
            long decelStart = firstDecel + (long) (i * SLOT_STOP_INTERVAL_SEC * 1000);
            double from = this.spinPosition(i, decelStart);

            int target = (int) Math.ceil(from + SLOT_MAX_SPEED * SLOT_DECEL_SEC / 3.0);
            this.reelDecelStartMs[i] = decelStart;
            this.reelDecelFrom[i] = from;
            this.reelTarget[i] = target;
            if (i < prizes.size()) {
                this.reelResults.get(i).put(target, prizes.get(i));
            }
        }
        this.slotResult = prizes;
        this.slotPhase = SlotPhase.STOPPING;
    }

    private int emeraldCount() {
        return this.minecraft == null || this.minecraft.player == null ? 0
                : ModPlayer.getInventory(this.minecraft.player).countItem(Items.EMERALD);
    }

    private void playUiSound(net.minecraft.sounds.SoundEvent sound, float pitch, float volume) {
        this.minecraft.getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(sound, pitch, volume));
    }

    private void startSlotSpin() {
        if (this.slotPhase == SlotPhase.SPINNING || this.slotPhase == SlotPhase.STOPPING) {
            return;
        }
        if (!this.isFree() && this.emeraldCount() < SmartDispatchAppItem.SLOT_COST) {
            this.slotMessage = ModText.translatable("gui.mineturismo.smart_dispatch.not_enough_emeralds", SmartDispatchAppItem.SLOT_COST);
            this.slotAuto = false;
            return;
        }
        com.examplebett.network.RequestSlotSpinPayload payload = new com.examplebett.network.RequestSlotSpinPayload();
        ModClientNetworking.sendToServer(com.examplebett.network.RequestSlotSpinPayload.ID, payload, com.examplebett.network.RequestSlotSpinPayload::write);
        this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_START, 1.0f, 0.8f);
        this.slotPhase = SlotPhase.SPINNING;
        this.slotSpinStartMs = System.currentTimeMillis();
        this.slotResult = null;
        this.slotMessage = null;

        this.slotFrozenEmeralds = this.emeraldCount() - (this.isFree() ? 0 : SmartDispatchAppItem.SLOT_COST);
        for (int i = 0; i < SLOT_REELS; i++) {
            this.reelBase[i] = this.reelRest[i];
            this.reelStopSoundPlayed[i] = false;
        }
    }

    private void skipSlotSpin() {
        if (this.slotPhase != SlotPhase.STOPPING) {
            return;
        }
        long now = System.currentTimeMillis();
        for (int i = 0; i < SLOT_REELS; i++) {
            this.reelDecelStartMs[i] = Math.min(this.reelDecelStartMs[i], now - (long) (SLOT_DECEL_SEC * 1000));
        }
    }

    private double spinPosition(int reel, long time) {
        double t = Math.max(0, time - this.slotSpinStartMs) / 1000.0;
        double distance = t < SLOT_ACCEL_SEC ? SLOT_MAX_SPEED * t * t / (2 * SLOT_ACCEL_SEC) : SLOT_MAX_SPEED * (t - SLOT_ACCEL_SEC / 2);
        return this.reelBase[reel] + distance;
    }

    private double reelPosition(int reel, long time) {
        if (this.slotPhase == SlotPhase.IDLE || this.slotPhase == SlotPhase.RESULT) {
            return this.reelRest[reel];
        }
        if (this.slotPhase == SlotPhase.SPINNING || time < this.reelDecelStartMs[reel]) {
            return this.spinPosition(reel, time);
        }
        double u = (time - this.reelDecelStartMs[reel]) / (SLOT_DECEL_SEC * 1000);
        if (u >= 1) {
            return this.reelTarget[reel];
        }
        double e = 1 - u;
        return this.reelDecelFrom[reel] + (this.reelTarget[reel] - this.reelDecelFrom[reel]) * (1 - e * e * e);
    }

    private boolean reelStopped(int reel, long time) {
        return this.slotPhase == SlotPhase.STOPPING && time >= this.reelDecelStartMs[reel] + (long) (SLOT_DECEL_SEC * 1000);
    }

    private void updateSlot(long now) {
        if (this.slotPhase == SlotPhase.SPINNING && now - this.slotSpinStartMs > SLOT_TIMEOUT_MS) {
            this.receiveSlotResult(List.of());
        }
        if (this.slotPhase == SlotPhase.SPINNING || this.slotPhase == SlotPhase.STOPPING) {

            boolean stopPlayed = false;
            int tickReel = -1;
            for (int i = 0; i < SLOT_REELS; i++) {
                if (this.reelStopped(i, now)) {
                    if (!this.reelStopSoundPlayed[i]) {
                        this.reelStopSoundPlayed[i] = true;
                        if (!stopPlayed) {
                            this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_STOP, 1.0f + i * 0.08f, 0.9f);
                            stopPlayed = true;
                        }
                    }
                } else {
                    tickReel = i;
                }
            }

            if (tickReel >= 0) {
                long cell = (long) Math.floor(this.reelPosition(tickReel, now));
                if (cell != this.slotLastTickCell && now - this.slotLastTickMs >= SLOT_TICK_MIN_INTERVAL_MS) {
                    this.slotLastTickCell = cell;
                    this.slotLastTickMs = now;
                    this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_TICK, 0.95f + (float) Math.random() * 0.1f, 0.45f);
                }
            } else if (this.slotPhase == SlotPhase.STOPPING) {
                this.finishSlotSpin(now);
            }
        }
        if (this.slotPhase == SlotPhase.RESULT && this.slotAuto && this.pending == null) {
            long delay = this.slotHasJackpot() ? SLOT_AUTO_DELAY_JACKPOT_MS : SLOT_AUTO_DELAY_MS;
            if (now - this.slotResultShownMs >= delay) {
                this.startSlotSpin();
            }
        }
    }

    private void finishSlotSpin(long now) {
        for (int i = 0; i < SLOT_REELS; i++) {
            this.reelRest[i] = this.reelTarget[i];
        }
        this.slotResultShownMs = now;
        this.slotFrozenEmeralds = -1;
        this.slotEmeraldsGained = 0;
        if (this.slotResult != null) {
            for (SmartDispatchAppItem.SlotPrize prize : this.slotResult) {
                if (prize.kind() == SmartDispatchAppItem.PRIZE_EMERALD) {
                    this.slotEmeraldsGained += prize.amount();
                }
            }
        }
        if (this.slotResult == null || this.slotResult.isEmpty()) {

            this.slotPhase = SlotPhase.IDLE;
            this.slotAuto = false;
            this.slotMessage = ModText.translatable("gui.mineturismo.smart_dispatch.slot_failed");
            return;
        }
        this.slotPhase = SlotPhase.RESULT;
        if (this.slotHasJackpot()) {
            this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_JACKPOT, 1.0f, 1.0f);
        } else if (this.slotResult.stream().anyMatch(SmartDispatchAppItem.SlotPrize::isWin)) {
            this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_WIN, 1.0f, 0.9f);
        } else {
            this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_LOSE, 1.0f, 0.7f);
        }
    }

    private boolean slotHasJackpot() {
        return this.slotResult != null && this.slotResult.stream().anyMatch(SmartDispatchAppItem.SlotPrize::isJackpot);
    }

    private SmartDispatchAppItem.SlotPrize slotCell(int reel, long cell) {
        SmartDispatchAppItem.SlotPrize result = cell == (int) cell ? this.reelResults.get(reel).get((int) cell) : null;
        if (result != null) {
            return result;
        }
        long h = cell * 0x9E3779B97F4A7C15L + (reel + 1) * 0xC2B2AE3D27D4EB4FL;
        h ^= h >>> 31;
        h *= 0xBF58476D1CE4E5B9L;
        h ^= h >>> 29;
        int r = (int) Math.floorMod(h, 100L);
        int r2 = (int) Math.floorMod(h >>> 17, 1000L);

        if (r < 18) {
            return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_WARDEN_ENERGY, 1, "");
        }
        if (r < 35) {
            return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_CHOCO_BAR, 1, "");
        }
        if (r < 55) {
            return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_EMERALD, 1, "");
        }
        if (r < 85) {
            return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_EMERALD, SLOT_DECOY_EMERALDS[r2 % SLOT_DECOY_EMERALDS.length], "");
        }
        String modelId = SmartDispatchAppItem.SLOT_VEHICLES[r2 % SmartDispatchAppItem.SLOT_VEHICLES.length];
        return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_VEHICLE, 1, modelId);
    }

    private ItemStack slotStack(SmartDispatchAppItem.SlotPrize prize) {
        return this.slotStackCache.computeIfAbsent(prize.kind() + ":" + prize.amount() + ":" + prize.modelId(), key -> prize.toStack());
    }

    private static int slotPrizeColor(SmartDispatchAppItem.SlotPrize prize) {
        if (prize.kind() == SmartDispatchAppItem.PRIZE_VEHICLE) {
            return 0xFFFFA000;
        }
        if (prize.kind() == SmartDispatchAppItem.PRIZE_EMERALD) {
            return prize.amount() >= 30 ? 0xFFE53935 : prize.amount() >= 10 ? 0xFFAB47BC : prize.amount() >= 2 ? 0xFF29B6F6 : 0xFF66BB6A;
        }
        if (prize.kind() == SmartDispatchAppItem.PRIZE_CHOCO_BAR) {
            return 0xFFA1887F;
        }
        return 0xFF9CCC65;
    }

    private String slotPrizeLabel(SmartDispatchAppItem.SlotPrize prize) {
        if (prize.kind() == SmartDispatchAppItem.PRIZE_VEHICLE) {
            ItemStack stack = this.slotStack(prize);
            return stack.isEmpty() ? prize.modelId() : stack.getHoverName().getString();
        }
        if (prize.kind() == SmartDispatchAppItem.PRIZE_EMERALD) {
            return "x" + prize.amount();
        }
        if (prize.kind() == SmartDispatchAppItem.PRIZE_CHOCO_BAR) {
            return ModText.translatable("gui.mineturismo.smart_dispatch.slot_choco_short").getString();
        }
        return ModText.translatable("gui.mineturismo.smart_dispatch.slot_energy_short").getString();
    }

    private void drawFittedCentered(GuiGraphics g, String text, int cx, int y, int maxWidth, int color) {
        float scale = Math.min(SMALL_TEXT_SCALE, (float) maxWidth / Math.max(1, com.examplebett.compat.LegacyGui.width(this.font, text)));
        int w = Math.round(com.examplebett.compat.LegacyGui.width(this.font, text) * scale);
        this.drawScaledText(g, ((text)),
                cx - w / 2, y, color, scale);
    }

    private int[][] slotRects(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT + COST_BAR_HEIGHT + 8;
        int autoTop = l.sy1() - 5 - SLOT_AUTO_BUTTON_HEIGHT;
        int spinTop = autoTop - 4 - SLOT_SPIN_BUTTON_HEIGHT;
        return new int[][]{
                {l.sx0() + 6, top, l.sx1() - 6, top + SLOT_WINDOW_HEIGHT},
                {l.sx0() + 8, spinTop, l.sx1() - 8, spinTop + SLOT_SPIN_BUTTON_HEIGHT},
                {l.sx0() + 8, autoTop, l.sx1() - 8, autoTop + SLOT_AUTO_BUTTON_HEIGHT}
        };
    }

    private void drawSlot(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        long now = System.currentTimeMillis();
        this.updateSlot(now);
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;

        boolean flash = this.slotPhase == SlotPhase.RESULT && this.slotResult.stream().anyMatch(SmartDispatchAppItem.SlotPrize::isWin)
                && now - this.slotResultShownMs < 1500 && (now / 150) % 2 == 0;
        int bands = Math.max(1, (l.sy1() - top) / (PX * 2));
        for (int i = 0; i < bands; i++) {
            int bandTop = top + i * PX * 2;
            int color = lerpColor(SLOT_PINK, SLOT_ORANGE, (float) i / bands);
            g.fill(l.sx0(), bandTop, l.sx1(), i == bands - 1 ? l.sy1() : bandTop + PX * 2, flash ? lerpColor(color, 0xFFFFFFFF, 0.25f) : color);
        }

        g.fill(l.sx0(), top, l.sx1(), top + COST_BAR_HEIGHT, COLOR_COST_BAR);
        Component cost = this.isFree() ? ModText.translatable("gui.mineturismo.smart_dispatch.free")
                : ModText.translatable("gui.mineturismo.smart_dispatch.slot_cost", SmartDispatchAppItem.SLOT_COST);
        this.drawSmallText(g, cost.getString(), l.sx0() + 3, top + 2, 0xFFE8E8E8);
        int shownEmeralds = this.slotFrozenEmeralds >= 0 ? this.slotFrozenEmeralds : this.emeraldCount();
        String owned = ModText.translatable("gui.mineturismo.smart_dispatch.slot_owned", shownEmeralds).getString();
        int ownedX = l.sx1() - 3 - Math.round(com.examplebett.compat.LegacyGui.width(this.font, owned) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, owned, ownedX, top + 2, 0xFF7CF29A);

        if (this.slotPhase == SlotPhase.RESULT && this.slotEmeraldsGained > 0 && now - this.slotResultShownMs < SLOT_GAIN_POPUP_MS) {
            String gained = "+" + this.slotEmeraldsGained;
            this.drawSmallText(g, gained, ownedX - 3 - Math.round(com.examplebett.compat.LegacyGui.width(this.font, gained) * SMALL_TEXT_SCALE), top + 2, SLOT_GOLD);
        }

        int[][] r = this.slotRects(l);
        int[] window = r[0];
        fillPixelRounded(g, window[0] - 3, window[1] - 3, window[2] + 3, window[3] + 3, 1, 0xFF2A1030);
        int reelWidth = (window[2] - window[0] - SLOT_REEL_GAP * (SLOT_REELS - 1)) / SLOT_REELS;
        for (int i = 0; i < SLOT_REELS; i++) {
            int rx0 = window[0] + i * (reelWidth + SLOT_REEL_GAP);
            this.drawReel(g, i, rx0, window[1], rx0 + reelWidth, window[3], now);
        }

        int midY = (window[1] + window[3]) / 2;
        for (int i = 0; i < 3; i++) {
            g.fill(window[0] - 3 + i, midY - 3 + i, window[0] - 2 + i, midY + 4 - i, 0xFFFFFFFF);
            g.fill(window[2] + 2 - i, midY - 3 + i, window[2] + 3 - i, midY + 4 - i, 0xFFFFFFFF);
        }

        int textTop = window[3] + 8;
        int room = l.sx1() - l.sx0() - 8;
        if (this.slotPhase == SlotPhase.RESULT) {
            boolean jackpot = this.slotHasJackpot();
            boolean win = jackpot || this.slotResult.stream().anyMatch(SmartDispatchAppItem.SlotPrize::isWin);
            String headline = ModText.translatable(jackpot ?"gui.mineturismo.smart_dispatch.slot_jackpot"
                    : win ? "gui.mineturismo.smart_dispatch.slot_win" : "gui.mineturismo.smart_dispatch.slot_lose").getString();
            g.drawString(this.font, headline, cx - com.examplebett.compat.LegacyGui.width(this.font, headline) / 2, textTop, win ? SLOT_GOLD : 0xFFFFFFFF, true);
            int emeralds = 0;
            for (SmartDispatchAppItem.SlotPrize prize : this.slotResult) {
                if (prize.kind() == SmartDispatchAppItem.PRIZE_EMERALD) {
                    emeralds += prize.amount();
                }
            }
            if (emeralds > 0) {
                this.drawFittedCentered(g, ModText.translatable("gui.mineturismo.smart_dispatch.slot_emeralds_won", emeralds).getString(),
                        cx, textTop + 11, room, 0xFFFFFFFF);
            }
        } else if (this.slotMessage != null && this.slotPhase == SlotPhase.IDLE) {
            List<String> lines = com.examplebett.compat.LegacyGui.split(this.font, this.slotMessage, Math.round(room / SMALL_TEXT_SCALE));
            for (int i = 0; i < Math.min(2, lines.size()); i++) {
                int w = Math.round(com.examplebett.compat.LegacyGui.width(this.font, lines.get(i)) * SMALL_TEXT_SCALE);
                this.drawSmallText(g, lines.get(i), cx - w / 2, textTop + i * 8, 0xFFFFE0E0);
            }
        } else if (this.slotPhase == SlotPhase.IDLE) {
            this.drawFittedCentered(g, ModText.translatable("gui.mineturismo.smart_dispatch.slot_hint").getString(), cx, textTop + 2, room, 0xFFFFFFFF);
        }

        int[] spin = r[1];
        boolean waiting = this.slotPhase == SlotPhase.SPINNING;
        boolean skipping = this.slotPhase == SlotPhase.STOPPING;
        boolean affordable = this.isFree() || this.emeraldCount() >= SmartDispatchAppItem.SLOT_COST;
        boolean spinEnabled = skipping || (!waiting && affordable);
        boolean spinHover = spinEnabled && inside(mouseX, mouseY, spin[0], spin[1], spin[2], spin[3]);
        fillPixelRounded(g, spin[0], spin[1] + 2, spin[2], spin[3] + 2, PX, spinEnabled ? 0xFFB07800 : 0xFF6E6E74);
        fillPixelRounded(g, spin[0], spin[1], spin[2], spin[3], PX, !spinEnabled ? 0xFF9A9AA2 : spinHover ? 0xFFFFE066 : 0xFFFFC928);
        String spinLabel = ModText.translatable(skipping ? "gui.mineturismo.smart_dispatch.slot_skip" : "gui.mineturismo.smart_dispatch.slot_spin").getString();
        g.drawString(this.font, spinLabel, cx - com.examplebett.compat.LegacyGui.width(this.font, spinLabel) / 2, spin[1] + 5, spinEnabled ? 0xFF3A2000 : 0xFFDADADF, false);

        int[] auto = r[2];
        boolean autoHover = inside(mouseX, mouseY, auto[0], auto[1], auto[2], auto[3]);
        fillPixelRounded(g, auto[0], auto[1], auto[2], auto[3], 1,
                this.slotAuto ? (autoHover ? 0xFF4FC562 : COLOR_BUTTON_YES) : (autoHover ? 0xFF54444A : 0xFF3A2A2E));
        String autoLabel = ModText.translatable("gui.mineturismo.smart_dispatch.slot_auto",
                ModText.translatable(this.slotAuto ? "options.on" : "options.off")).getString();
        int autoWidth = Math.round(com.examplebett.compat.LegacyGui.width(this.font, autoLabel) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, autoLabel, cx - autoWidth / 2, auto[1] + 4, 0xFFFFFFFF);
    }

    private void drawReel(GuiGraphics g, int reel, int x0, int y0, int x1, int y1, long now) {
        g.fill(x0, y0, x1, y1, 0xFF1A0A1E);
        double pos = this.reelPosition(reel, now);
        double speed = Math.abs(pos - this.reelPosition(reel, now - 16)) / 0.016;
        boolean blur = speed > SLOT_BLUR_SPEED;

        g.enableScissor(x0, y0, x1, y1);
        boolean scissor = true;
        int centerY = (y0 + y1) / 2;
        long base = (long) Math.floor(pos);
        for (long cell = base - 2; cell <= base + 2; cell++) {
            int cellTop = (int) Math.round(centerY - SLOT_CELL_HEIGHT / 2.0 + (pos - cell) * SLOT_CELL_HEIGHT);
            this.drawSlotCell(g, this.slotCell(reel, cell), x0 + 1, cellTop + 1, x1 - 1, cellTop + SLOT_CELL_HEIGHT - 1, y0, y1, blur, scissor);
        }

        if (this.slotPhase == SlotPhase.RESULT && reel < this.slotResult.size() && this.slotResult.get(reel).isWin() && (now / 250) % 2 == 0) {
            int cellTop = centerY - SLOT_CELL_HEIGHT / 2;
            int cellBottom = cellTop + SLOT_CELL_HEIGHT;
            g.fill(x0, cellTop, x1, cellTop + 2, SLOT_GOLD);
            g.fill(x0, cellBottom - 2, x1, cellBottom, SLOT_GOLD);
            g.fill(x0, cellTop, x0 + 2, cellBottom, SLOT_GOLD);
            g.fill(x1 - 2, cellTop, x1, cellBottom, SLOT_GOLD);
        }

        g.fill(x0, y0, x1, y0 + 4, 0x66000000);
        g.fill(x0, y1 - 4, x1, y1, 0x66000000);
        g.disableScissor();
    }

    private static void fillClipped(GuiGraphics g, int x0, int y0, int x1, int y1, int clipTop, int clipBottom, int color) {
        int top = Math.max(y0, clipTop);
        int bottom = Math.min(y1, clipBottom);
        if (bottom > top) {
            g.fill(x0, top, x1, bottom, color);
        }
    }

    private void drawSlotCell(GuiGraphics g, SmartDispatchAppItem.SlotPrize prize, int x0, int y0, int x1, int y1,
                              int clipTop, int clipBottom, boolean blur, boolean scissor) {
        if (y1 <= clipTop || y0 >= clipBottom) {
            return;
        }
        int color = slotPrizeColor(prize);
        if (blur) {

            fillClipped(g, x0, y0, x1, y1, clipTop, clipBottom, lerpColor(color, 0xFFFFFFFF, 0.35f));
            int mid = (x0 + x1) / 2;
            fillClipped(g, mid - 6, y0 + 4, mid + 6, y1 - 4, clipTop, clipBottom, lerpColor(color, 0xFFFFFFFF, 0.6f));
            return;
        }
        fillClipped(g, x0, y0, x1, y1, clipTop, clipBottom, color);
        int stripTop = y1 - 9;
        fillClipped(g, x0 + 1, stripTop, x1 - 1, y1 - 1, clipTop, clipBottom, 0xF2FFFFFF);

        boolean visible = scissor || (y0 >= clipTop && y1 <= clipBottom);
        if (!visible) {
            return;
        }
        int cx = (x0 + x1) / 2;
        g.renderItem(this.slotStack(prize), cx - 8, y0 + 2);
        this.drawFittedCentered(g, this.slotPrizeLabel(prize), cx, stripTop + 2, x1 - x0 - 4, 0xFF202024);
    }

    private boolean handleSlotClick(Layout l, double mouseX, double mouseY) {
        int[][] r = this.slotRects(l);
        if (inside(mouseX, mouseY, r[1][0], r[1][1], r[1][2], r[1][3])) {
            this.pressSlotSpin();
            return true;
        }
        if (inside(mouseX, mouseY, r[2][0], r[2][1], r[2][2], r[2][3])) {
            this.slotAuto = !this.slotAuto;
            if (this.slotAuto && (this.slotPhase == SlotPhase.IDLE || this.slotPhase == SlotPhase.RESULT)) {
                this.startSlotSpin();
            }
            return true;
        }

        if (inside(mouseX, mouseY, r[0][0], r[0][1], r[0][2], r[0][3])) {
            this.pressSlotSpin();
            return true;
        }
        return false;
    }

    private void pressSlotSpin() {
        if (this.slotPhase == SlotPhase.STOPPING) {
            this.skipSlotSpin();
        } else {
            this.startSlotSpin();
        }
    }

    private String fit(String text, int width) {
        int room = Math.round(width / SMALL_TEXT_SCALE);
        if (com.examplebett.compat.LegacyGui.width(this.font, text) <= room) {
            return text;
        }
        return this.font.substrByWidth(text, room - com.examplebett.compat.LegacyGui.width(this.font, "...")) + "...";
    }

    private void drawDialog(GuiGraphics g, Layout l, App app, int mouseX, int mouseY) {
        this.aboveItems(g);
        g.fill(l.sx0(), l.sy0(), l.sx1(), l.sy1(), COLOR_DIALOG_SHADE);
        int dx0 = l.sx0() + 6;
        int dx1 = l.sx1() - 6;
        int dy0 = l.dialogTop();
        fillPixelRounded(g, dx0, dy0, dx1, dy0 + DIALOG_HEIGHT, PX, COLOR_DIALOG);

        g.renderItem(this.pending.stack(), dx0 + 6, dy0 + 6);
        this.drawSmallText(g, this.fit(this.pending.name().getString(), dx1 - dx0 - 30), dx0 + 26, dy0 + 11, 0xFF202024);

        boolean enough = this.hasEnoughEmeralds();
        Component question = enough
                ? ModText.translatable(app.vehicles ? "gui.mineturismo.smart_dispatch.confirm_ride" : "gui.mineturismo.smart_dispatch.confirm_buy",
                        costOf(app))
                : ModText.translatable("gui.mineturismo.smart_dispatch.not_enough_emeralds", costOf(app));

        List<String> lines = com.examplebett.compat.LegacyGui.split(this.font, question, Math.round((dx1 - dx0 - 10) / SMALL_TEXT_SCALE));
        for (int i = 0; i < Math.min(2, lines.size()); i++) {
            this.drawSmallText(g, lines.get(i), dx0 + 5, dy0 + 28 + i * 8, enough ? 0xFF202024 : 0xFFC62828);
        }
        int by0 = l.buttonTop();
        int bw = l.buttonWidth();
        boolean yesHover = enough && inside(mouseX, mouseY, l.yesX0(), by0, l.yesX0() + bw, by0 + DIALOG_BUTTON_HEIGHT);
        boolean noHover = inside(mouseX, mouseY, l.noX0(), by0, l.noX0() + bw, by0 + DIALOG_BUTTON_HEIGHT);
        fillPixelRounded(g, l.yesX0(), by0, l.yesX0() + bw, by0 + DIALOG_BUTTON_HEIGHT, 1,
                !enough ? COLOR_BUTTON_NO : yesHover ? COLOR_BUTTON_YES_HOVER : COLOR_BUTTON_YES);
        fillPixelRounded(g, l.noX0(), by0, l.noX0() + bw, by0 + DIALOG_BUTTON_HEIGHT, 1, noHover ? COLOR_BUTTON_NO_HOVER : COLOR_BUTTON_NO);
        String yes = ModText.translatable("gui.yes").getString();
        String no = ModText.translatable("gui.no").getString();
        g.drawString(this.font, yes, l.yesX0() + (bw - com.examplebett.compat.LegacyGui.width(this.font, yes)) / 2, by0 + 3, 0xFFFFFFFF, false);
        g.drawString(this.font, no, l.noX0() + (bw - com.examplebett.compat.LegacyGui.width(this.font, no)) / 2, by0 + 3, 0xFF202024, false);
    }

    private void aboveItems(GuiGraphics g) {
    }

    private void drawSmallText(GuiGraphics g, String text, int x, int y, int color) {
        this.drawScaledText(g, text, x, y, color, SMALL_TEXT_SCALE);
    }

    private void drawScaledText(GuiGraphics g, String text, int x, int y, int color, float s) {
        g.pose().pushPose();
        g.pose().scale(s, s, 1.0f);
        g.drawString(this.font, text, Math.round(x / s), Math.round(y / s), color, false);
        g.pose().popPose();
    }

    private void confirm(App app, Entry entry) {
        if (app.vehicles) {
            RequestAppCarSpawnPayload payload = new RequestAppCarSpawnPayload(entry.id());
            ModClientNetworking.sendToServer(RequestAppCarSpawnPayload.ID, payload, RequestAppCarSpawnPayload::write);
            this.onClose();
        } else {
            RequestAppItemPurchasePayload payload = new RequestAppItemPurchasePayload(entry.id());
            ModClientNetworking.sendToServer(RequestAppItemPurchasePayload.ID, payload, RequestAppItemPurchasePayload::write);
            this.purchasedBannerUntil = System.currentTimeMillis() + PURCHASED_BANNER_MS;
        }
    }

    private void launchGtt() {
        com.examplebett.network.SetGttPayload payload = new com.examplebett.network.SetGttPayload(true);
        ModClientNetworking.sendToServer(com.examplebett.network.SetGttPayload.ID, payload, com.examplebett.network.SetGttPayload::write);
        com.examplebett.client.GttClient.requestStart();
        this.onClose();
    }

    private boolean handleClick(double mouseX, double mouseY, int button) {
        if (this.shutdownUntil != 0L) {
            return true;
        }
        if (button != 0) {
            return false;
        }
        Layout l = this.layout();
        if (this.pending != null) {
            int by0 = l.buttonTop();
            int bw = l.buttonWidth();
            if (inside(mouseX, mouseY, l.yesX0(), by0, l.yesX0() + bw, by0 + DIALOG_BUTTON_HEIGHT)) {
                if (this.hasEnoughEmeralds()) {
                    Entry entry = this.pending;
                    this.pending = null;
                    this.confirm(this.openApp, entry);
                }
            } else {

                this.pending = null;
            }
            return true;
        }
        if (inside(mouseX, mouseY, l.homeX0() - 2, l.homeY0() - 2, l.homeX1() + 2, l.homeY1() + 2)) {
            if (this.openApp != null) {
                this.openApp = null;
                this.musicBrowsing = false;
            } else {

                this.shutdownUntil = System.currentTimeMillis() + SHUTDOWN_MS;
            }
            return true;
        }
        if (this.openApp == null) {
            App[] apps = App.values();
            for (int i = 0; i < apps.length; i++) {
                int[] r = appIconRect(l, i);
                if (inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
                    if (apps[i] == App.GTT) {
                        this.launchGtt();
                        return true;
                    }
                    this.openApp = apps[i];
                    this.scrollRow = 0;
                    this.weatherPickerOpen = false;
                    this.clockPickerOpen = false;
                    this.musicBrowsing = false;
                    this.settingsPage = SETTINGS_PAGE_MAIN;
                    this.purchasedBannerUntil = 0L;
                    return true;
                }
            }
            return false;
        }
        int headerTop = l.sy0() + STATUS_BAR_HEIGHT;
        if (inside(mouseX, mouseY, l.sx0(), headerTop, l.sx0() + 16, headerTop + HEADER_HEIGHT)) {

            if (this.openApp == App.MUSIC && this.musicBrowsing) {
                this.musicBrowsing = false;
            } else if (this.openApp == App.SETTINGS && this.settingsPage != SETTINGS_PAGE_MAIN) {
                this.settingsPage = SETTINGS_PAGE_MAIN;
                this.rebindingKey = null;
            } else {
                this.openApp = null;
            }
            return true;
        }
        if (this.openApp == App.MUSIC && !this.musicBrowsing) {
            return this.handleMusicHomeClick(l, mouseX, mouseY);
        }
        if (this.openApp.weather) {
            return this.handleWeatherClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.EMERGENCY) {
            return this.handleEmergencyClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.DETONATOR) {
            return this.handleDetonatorClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.CLOCK) {
            return this.handleClockClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.MASSIVE_AUTO) {
            return this.handleMassiveAutoClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.SLOT) {
            return this.handleSlotClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.SETTINGS) {
            return this.handleSettingsClick(l, mouseX, mouseY);
        }
        int listTop = l.listTop();

        if (this.maxScrollRow(l) > 0 && inside(mouseX, mouseY, l.sx1() - 6, listTop, l.sx1(), l.sy1())) {
            this.draggingScrollbar = true;
            this.scrollToMouse(l, mouseY);
            return true;
        }
        if (inside(mouseX, mouseY, l.sx0(), listTop, l.sx1() - 6, listTop + l.visibleRows() * ROW_HEIGHT)) {
            int index = this.scrollRow + (int) ((mouseY - listTop) / ROW_HEIGHT);
            List<Entry> entries = this.entries();
            if (index < entries.size() && this.openApp == App.MUSIC) {

                com.examplebett.client.sound.ChorusMusicTracks.Track track = findTrack(entries.get(index).id());
                if (track != null) {
                    com.examplebett.client.sound.ChorusMusicPlayer.play(track);
                }
                return true;
            }
            if (index < entries.size()) {
                if (this.isFree()) {
                    this.confirm(this.openApp, entries.get(index));
                } else {
                    this.pending = entries.get(index);
                }
                return true;
            }
        }
        return false;
    }

    private void releaseVolume() {
        if (this.draggingVolume) {
            this.draggingVolume = false;
            this.minecraft.options.save();
        }
    }

    private int thumbHeight(Layout l) {
        return Math.max(8, (l.sy1() - l.listTop()) * l.visibleRows() / Math.max(1, this.entries().size()));
    }

    private void scrollToMouse(Layout l, double mouseY) {
        int max = this.maxScrollRow(l);
        int room = (l.sy1() - l.listTop()) - this.thumbHeight(l);
        if (max <= 0 || room <= 0) {
            return;
        }
        double t = (mouseY - l.listTop() - this.thumbHeight(l) / 2.0) / room;
        this.scrollRow = (int) Math.round(Math.max(0.0, Math.min(1.0, t)) * max);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.draggingVolume && button == 0) {
            this.setVolumeFromMouse(this.layout(), mouseX);
            return true;
        }
        if (this.draggingScrollbar && button == 0) {
            this.scrollToMouse(this.layout(), mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.draggingScrollbar = false;
            this.releaseVolume();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.handleClick(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleSlotKey(int key) {
        if (this.openApp != App.SLOT || this.pending != null
                || (key != org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE && key != org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER && key != org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER)) {
            return false;
        }
        this.pressSlotSpin();
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.handleRebindKey(keyCode, () -> com.mojang.blaze3d.platform.InputConstants.getKey(keyCode, scanCode))
                || this.handleSlotKey(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

    private boolean mineturismo$mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.openApp == null || this.pending != null || scrollY == 0) {
            return false;
        }
        Layout l = this.layout();
        this.scrollRow = Math.max(0, Math.min(this.maxScrollRow(l), this.scrollRow + (scrollY > 0 ? -1 : 1)));
        return true;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        Layout l = this.layout();
        this.drawPhone(guiGraphics, l);
        if (this.shutdownUntil != 0L) {
            this.drawShutdown(guiGraphics, l);
            return;
        }
        if (this.openApp != null) {
            this.drawApp(guiGraphics, l, this.openApp, mouseX, mouseY);
        } else {
            this.drawHome(guiGraphics, l, mouseX, mouseY);
        }
        this.drawStatusBar(guiGraphics, l);
        this.skin.drawScreenOverlay(guiGraphics, l.sx0(), l.sy0(), l.sx1());
        if (this.pending != null && this.openApp != null) {
            this.drawDialog(guiGraphics, l, this.openApp, mouseX, mouseY);
        }
    }

    private void drawShutdown(GuiGraphics g, Layout l) {
        g.fill(l.sx0(), l.sy0(), l.sx1(), l.sy1(), 0xFF000000);

        float dot = PX * 2 * 2f / 3f;
        int logoW = Math.round(CHORUS_LOGO[0].length() * dot);
        int logoH = Math.round(CHORUS_LOGO.length * dot);
        int cx = (l.sx0() + l.sx1()) / 2;
        int logoX = (cx - logoW / 2) / PX * PX;
        int logoY = ((l.sy0() + l.sy1()) / 2 - logoH - 6) / PX * PX;
        for (int row = 0; row < CHORUS_LOGO.length; row++) {
            String line = CHORUS_LOGO[row];
            int py0 = logoY + Math.round(row * dot);
            int py1 = logoY + Math.round((row + 1) * dot);
            for (int col = 0; col < line.length(); col++) {
                if (line.charAt(col) == '#') {
                    int px0 = logoX + Math.round(col * dot);
                    int px1 = logoX + Math.round((col + 1) * dot);
                    g.fill(px0, py0, px1, py1, 0xFFFFFFFF);
                }
            }
        }
        String brand = "Chorus";
        int brandY = logoY + logoH + 6;
        g.drawString(this.font, brand, cx - com.examplebett.compat.LegacyGui.width(this.font, brand) / 2, brandY, 0xFFFFFFFF, false);
        String status = ModText.translatable("gui.mineturismo.smart_dispatch.shutting_down").getString();
        this.drawSmallText(g, status, cx - Math.round(com.examplebett.compat.LegacyGui.width(this.font, status) * SMALL_TEXT_SCALE) / 2,
                brandY + this.font.lineHeight + 6, 0xFFB4B4B4);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.shutdownUntil != 0L && System.currentTimeMillis() >= this.shutdownUntil) {
            this.shutdownUntil = 0L;
            this.onClose();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
//? if <1.15 {
/*
package com.examplebett.client.gui;
import com.examplebett.compat.ModClientNetworking;
import com.examplebett.compat.ModText;

import com.examplebett.AiObjectMod;
import com.examplebett.compat.ModPlayer;
import com.examplebett.compat.ModRegistries;
import com.examplebett.item.SmartDispatchAppItem;
import com.examplebett.network.RequestAppCarSpawnPayload;
import com.examplebett.network.RequestAppItemPurchasePayload;
import com.examplebett.util.AiCarPool;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SmartDispatchAppScreen extends Screen {

    private static final int PX = 2;
    private static final int PHONE_MAX_HEIGHT = 240;
    private static final float PHONE_ASPECT = 0.56f;
    private static final int STATUS_BAR_HEIGHT = 11;
    private static final int APP_ICON_SIZE = 24;
    private static final int APP_ICON_TEXTURE_SIZE = 64;
    private static final int APP_COLUMNS = 4;
    private static final int HEADER_HEIGHT = 16;
    private static final int COST_BAR_HEIGHT = 10;
    private static final int ROW_HEIGHT = 20;
    private static final float SMALL_TEXT_SCALE = 0.75f;
    private static final int DIALOG_HEIGHT = 78;
    private static final int DIALOG_BUTTON_HEIGHT = 14;
    private static final long PURCHASED_BANNER_MS = 1500L;

    private static final long SHUTDOWN_MS = 500L;

    private static final int SETTINGS_PAGE_MAIN = 0;
    private static final int SETTINGS_PAGE_WALLPAPER = 1;
    private static final int SETTINGS_PAGE_MODEL = 2;

    private static final int SETTINGS_PAGE_CONTROLS = 3;
    private static final int CONTROLS_ROW_HEIGHT = 11;
    private static final int SETTINGS_ROW_HEIGHT = 20;

    private static final int WALLPAPER_TEXTURE_WIDTH = 64;
    private static final int WALLPAPER_TEXTURE_HEIGHT = 120;

    private static final String[] PHONE_MODEL_NAMES = {"PopPhone 3", "PopPhone 6", "PopPhone 18", null};

    private static final String[] CHORUS_LOGO = {
            "....#.......",
            "#..##.......",
            "########....",
            ".#########..",
            ".##########.",
            "..#########.",
            ".###########",
            "############",
            "############",
            ".###########",
            ".##########.",
            "..########..",
            "...######...",
    };

    private static final int COLOR_WALLPAPER_TOP = 0xFF5696E6;
    private static final int COLOR_WALLPAPER_BOTTOM = 0xFF2C5CB4;
    private static final int COLOR_STATUS_BAR = 0x66000000;
    private static final int COLOR_ROW = 0xFFFFFFFF;
    private static final int COLOR_ROW_ALT = 0xFFF1F1F4;
    private static final int COLOR_ROW_HOVER = 0xFFFFF1B8;
    private static final int COLOR_COST_BAR = 0xFF2A2A30;
    private static final int COLOR_SCROLL_TRACK = 0xFFDADAE0;
    private static final int COLOR_SCROLL_THUMB = 0xFF8C8C96;
    private static final int COLOR_DIALOG_SHADE = 0x99000000;
    private static final int COLOR_DIALOG = 0xFFFFFFFF;
    private static final int COLOR_BUTTON_YES = 0xFF3DAA4F;
    private static final int COLOR_BUTTON_YES_HOVER = 0xFF4FC562;
    private static final int COLOR_BUTTON_NO = 0xFFB4B6BE;
    private static final int COLOR_BUTTON_NO_HOVER = 0xFFC8CAD2;
    private static final int COLOR_BANNER = 0xEE202024;

    private enum App {
        MINES("Mines Car Share", "Mines\nCar Share", null, "mines", 0xFFF2C318, 0xFF000000, true, false),
        AVALON("Avalon", "Avalon", null, "avalon", 0xFF1B2838, 0xFF6FD3F3, false, false),
        WEATHER(null, null, "gui.mineturismo.smart_dispatch.weather_app", "weather", 0xFF3D8BD9, 0xFFFFFFFF, false, true),
        CLOCK(null, null, "gui.mineturismo.smart_dispatch.clock_app", "clock", 0xFF1C1C1E, 0xFFFFFFFF, false, false),

        GTT("Grand Target Track", "GTT", null, "gtt", 0xFF000000, 0xFFFFFFFF, false, false),
        EMERGENCY(null, null, "gui.mineturismo.smart_dispatch.emergency_app", "emergency", 0xFFD32F2F, 0xFFFFFFFF, false, false),

        MUSIC(null, null, "gui.mineturismo.smart_dispatch.music_app", "music", 0xFFF5354F, 0xFFFFFFFF, false, false),

        MASSIVE_AUTO("Massive Auto", "Massive\nAuto", null, "massive_auto", 0xFF1E4FA8, 0xFFFFFFFF, false, false),

        SLOT(null, null, "gui.mineturismo.smart_dispatch.slot_app", "slot", 0xFFE6197E, 0xFFFFFFFF, false, false),

        DETONATOR(null, null, "gui.mineturismo.smart_dispatch.detonator_app", "detonator", 0xFF2B2D33, 0xFFFFFFFF, false, false),

        SETTINGS(null, null, "gui.mineturismo.smart_dispatch.settings_app", "settings", 0xFFE9E9EE, 0xFF000000, false, false);

        private final String title;
        private final String shortName;
        private final String titleKey;
        final ResourceLocation icon;
        final int headerColor;
        final int titleColor;
        final boolean vehicles;
        final boolean weather;

        App(String title, String shortName, String titleKey, String iconId, int headerColor, int titleColor,
            boolean vehicles, boolean weather) {
            this.title = title;
            this.shortName = shortName;
            this.titleKey = titleKey;
            this.icon = AiObjectMod.id("textures/gui/smart_dispatch/" + iconId + ".png");
            this.headerColor = headerColor;
            this.titleColor = titleColor;
            this.vehicles = vehicles;
            this.weather = weather;
        }

        String title() {
            return this.titleKey != null ? ModText.translatable(this.titleKey).getString() : this.title;
        }

        String shortName() {
            if (this.titleKey == null) {
                return this.shortName;
            }
            String labelKey = this.titleKey + "_label";
            return net.minecraft.client.resources.language.I18n.exists(labelKey) ? ModText.translatable(labelKey).getString() : this.title();
        }
    }

    private record Entry(String id, ItemStack stack, Component name) {
    }

    private record Layout(int x0, int y0, int x1, int y1, int sx0, int sy0, int sx1, int sy1,
                          int homeX0, int homeY0, int homeX1, int homeY1) {
        int listTop() {
            return sy0 + STATUS_BAR_HEIGHT + HEADER_HEIGHT + COST_BAR_HEIGHT;
        }

        int visibleRows() {
            return Math.max(1, (sy1 - listTop()) / ROW_HEIGHT);
        }

        int dialogTop() {
            return (sy0 + sy1 - DIALOG_HEIGHT) / 2;
        }

        int buttonTop() {
            return dialogTop() + DIALOG_HEIGHT - DIALOG_BUTTON_HEIGHT - 6;
        }

        int buttonWidth() {
            return (sx1 - sx0 - 12 - 6 - 6) / 2;
        }

        int yesX0() {
            return sx0 + 12;
        }

        int noX0() {
            return yesX0() + buttonWidth() + 6;
        }
    }

    private final List<Entry> vehicles = new ArrayList<>();
    private final List<Entry> shopItems = new ArrayList<>();
    private final List<Entry> musicTracks = new ArrayList<>();
    private App openApp = null;
    private int scrollRow = 0;

    private boolean draggingScrollbar = false;

    private boolean weatherPickerOpen = false;

    private boolean clockPickerOpen = false;

    private boolean musicBrowsing = false;

    private boolean draggingVolume = false;

    private Entry pending = null;
    private long purchasedBannerUntil = 0L;

    private long shutdownUntil = 0L;

    private String phoneModel = SmartDispatchAppItem.DEFAULT_PHONE_MODEL;
    private String wallpaper = SmartDispatchAppItem.WALLPAPERS[0];
    private PhoneSkin skin = PhoneSkin.POPPHONE_6;

    private int settingsPage = SETTINGS_PAGE_MAIN;

    private net.minecraft.client.KeyMapping rebindingKey;

    public SmartDispatchAppScreen() {
        super(ModText.translatable("item.mineturismo.smart_dispatch_app"));
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            ItemStack phone = SmartDispatchAppItem.heldPhone(mc.player);
            this.phoneModel = SmartDispatchAppItem.phoneModelOf(phone);
            this.wallpaper = SmartDispatchAppItem.wallpaperOf(phone);
            this.skin = PhoneSkin.byId(this.phoneModel);
        }
        for (String modelId : AiCarPool.allModelIds(id -> false)) {
            Item item = AiCarPool.spawnItemFor(modelId);
            if (item != null) {
                this.vehicles.add(new Entry(modelId, new ItemStack(item), ModText.translatable(item.getDescriptionId())));
            }
        }
        this.vehicles.sort(Comparator.comparing(e -> e.name().getString()));
        for (Item item : SmartDispatchAppItem.shopItems()) {
            this.shopItems.add(new Entry(ModRegistries.ITEM.getKey(item).toString(), new ItemStack(item),
                    ModText.translatable(item.getDescriptionId())));
        }

        ItemStack wardenEnergy = SmartDispatchAppItem.createWardenEnergy(1);
        if (!wardenEnergy.isEmpty()) {
            this.shopItems.add(new Entry(SmartDispatchAppItem.WARDEN_ENERGY_ID, wardenEnergy,
                    ModText.translatable("item.mineturismo.warden_energy").append(" x" + SmartDispatchAppItem.WARDEN_ENERGY_PACK)));
        }

        ItemStack wardenEnergySonicBoom = SmartDispatchAppItem.createWardenEnergySonicBoom(1);
        if (!wardenEnergySonicBoom.isEmpty()) {
            this.shopItems.add(new Entry(SmartDispatchAppItem.WARDEN_ENERGY_SONIC_BOOM_ID, wardenEnergySonicBoom,
                    ModText.translatable("item.mineturismo.warden_energy_sonic_boom").append(" x" + SmartDispatchAppItem.WARDEN_ENERGY_PACK)));
        }

        ItemStack chocoBar = SmartDispatchAppItem.createChocoBar(1);
        if (!chocoBar.isEmpty()) {
            this.shopItems.add(new Entry(SmartDispatchAppItem.CHOCO_BAR_ID, chocoBar,
                    ModText.translatable("item.mineturismo.choco_bar").append(" x" + SmartDispatchAppItem.CHOCO_BAR_PACK)));
        }
        this.shopItems.sort(Comparator.comparing(e -> e.name().getString()));

        java.util.Map<String, Item> discs = new java.util.HashMap<>();
        for (Item item : ModRegistries.ITEM) {
            net.minecraft.resources.ResourceLocation key = ModRegistries.ITEM.getKey(item);
            if ("minecraft".equals(key.getNamespace()) && key.getPath().startsWith("music_disc_")) {
                discs.put(key.getPath().substring("music_disc_".length()), item);
            }
        }
        for (com.examplebett.client.sound.ChorusMusicTracks.Track track : com.examplebett.client.sound.ChorusMusicTracks.TRACKS) {

            if (net.minecraft.client.Minecraft.getInstance().getSoundManager().getSoundEvent(track.soundId()) == null) {
                continue;
            }
            Item icon = switch (track.category()) {
                case RECORDS -> discs.getOrDefault(track.id(), Items.JUKEBOX);
                case CREATIVE -> Items.BRICKS;
                case MENU -> Items.PAINTING;
                case NETHER -> Items.NETHERRACK;
                case END -> Items.END_STONE;
                case UNDERWATER -> Items.PRISMARINE;
                default -> Items.GRASS_BLOCK;
            };
            this.musicTracks.add(new Entry(track.id(), new ItemStack(icon), ModText.literal(track.title())));
        }
    }

    public static SmartDispatchAppScreen massiveAuto() {
        SmartDispatchAppScreen screen = new SmartDispatchAppScreen();
        screen.openApp = App.MASSIVE_AUTO;
        return screen;
    }

    private static com.examplebett.client.sound.ChorusMusicTracks.Track findTrack(String id) {
        for (com.examplebett.client.sound.ChorusMusicTracks.Track track : com.examplebett.client.sound.ChorusMusicTracks.TRACKS) {
            if (track.id().equals(id)) {
                return track;
            }
        }
        return null;
    }

    private List<Entry> entries() {
        if (this.openApp == App.MUSIC) {
            return this.musicBrowsing ? this.musicTracks : List.of();
        }
        return this.openApp == null || this.openApp.weather || this.openApp == App.EMERGENCY || this.openApp == App.GTT
                || this.openApp == App.CLOCK || this.openApp == App.MASSIVE_AUTO || this.openApp == App.SLOT
                || this.openApp == App.SETTINGS ? List.of()
                : this.openApp.vehicles ? this.vehicles : this.shopItems;
    }

    private Layout layout() {
        int phoneH = Math.min(PHONE_MAX_HEIGHT, this.height - 16) / PX * PX;
        int phoneW = Math.round(phoneH * PHONE_ASPECT) / PX * PX;
        int x0 = (this.width - phoneW) / 2 / PX * PX;
        int y0 = (this.height - phoneH) / 2 / PX * PX;
        int x1 = x0 + phoneW;
        int y1 = y0 + phoneH;

        int[] home = this.skin.homeRect(x0, x1, y1 - this.skin.insetBottom, y1);
        return new Layout(x0, y0, x1, y1,
                x0 + this.skin.insetX, y0 + this.skin.insetTop, x1 - this.skin.insetX, y1 - this.skin.insetBottom,
                home[0], home[1], home[2], home[3]);
    }

    private int maxScrollRow(Layout l) {
        return Math.max(0, this.entries().size() - l.visibleRows());
    }

    private boolean isFree() {
        return this.minecraft != null && this.minecraft.player != null && ModPlayer.hasInstabuild(this.minecraft.player);
    }

    private static int costOf(App app) {
        return app != null && app.vehicles ? SmartDispatchAppItem.VEHICLE_CALL_COST : SmartDispatchAppItem.SHOP_COST;
    }

    private boolean hasEnoughEmeralds() {
        return this.minecraft != null && this.minecraft.player != null
                && ModPlayer.getInventory(this.minecraft.player).countItem(Items.EMERALD) >= costOf(this.openApp);
    }

    private static boolean inside(double mx, double my, int x0, int y0, int x1, int y1) {
        return mx >= x0 && mx < x1 && my >= y0 && my < y1;
    }

    static void fillPixelRounded(GuiGraphics g, int x0, int y0, int x1, int y1, int step, int color) {
        g.fill(x0 + step * 2, y0, x1 - step * 2, y0 + step, color);
        g.fill(x0 + step, y0 + step, x1 - step, y0 + step * 2, color);
        g.fill(x0, y0 + step * 2, x1, y1 - step * 2, color);
        g.fill(x0 + step, y1 - step * 2, x1 - step, y1 - step, color);
        g.fill(x0 + step * 2, y1 - step, x1 - step * 2, y1, color);
    }

    static void fillPixelCircle(GuiGraphics g, int cx, int cy, int radius, int color) {
        for (int dy = -radius; dy < radius; dy += PX) {
            double mid = dy + PX / 2.0;
            int half = (int) Math.round(Math.sqrt(Math.max(0.0, radius * (double) radius - mid * mid)) / PX) * PX;
            if (half > 0) {
                g.fill(cx - half, cy + dy, cx + half, cy + dy + PX, color);
            }
        }
    }

    private static int lerpColor(int a, int b, float t) {
        int r = Math.round(((a >> 16) & 0xFF) + (((b >> 16) & 0xFF) - ((a >> 16) & 0xFF)) * t);
        int gr = Math.round(((a >> 8) & 0xFF) + (((b >> 8) & 0xFF) - ((a >> 8) & 0xFF)) * t);
        int bl = Math.round((a & 0xFF) + ((b & 0xFF) - (a & 0xFF)) * t);
        return 0xFF000000 | (r << 16) | (gr << 8) | bl;
    }

    private void drawPhone(GuiGraphics g, Layout l) {
        this.skin.drawBody(g, l.x0(), l.y0(), l.x1(), l.y1(), l.sx0(), l.sy0(), l.sx1(), l.sy1(),
                new int[]{l.homeX0(), l.homeY0(), l.homeX1(), l.homeY1()});
    }

    private static void drawWallpaper(GuiGraphics g, String wallpaper, int x0, int y0, int x1, int y1) {
        if ("white".equals(wallpaper) || "gray".equals(wallpaper)) {
            g.fill(x0, y0, x1, y1, "white".equals(wallpaper) ? 0xFFFFFFFF : 0xFF8E8E93);
            return;
        }
        int[] fallback = switch (wallpaper) {
            case "sunset_beach" -> new int[]{0xFF8A83C6, 0xFFF4AE96};
            case "grass" -> new int[]{0xFF5FA83A, 0xFF7A4E28};
            case "flow" -> new int[]{0xFFE8336A, 0xFF4A6CD8};
            default -> null;
        };
        if (fallback != null) {
            ResourceLocation texture = AiObjectMod.id("textures/gui/smart_dispatch/wallpaper_" + wallpaper + ".png");
            int w = x1 - x0;
            int h = y1 - y0;
            int vH = WALLPAPER_TEXTURE_HEIGHT;
            int uW = Math.min(WALLPAPER_TEXTURE_WIDTH, Math.round((float) vH * w / Math.max(1, h)));
            if (uW == WALLPAPER_TEXTURE_WIDTH) {
                vH = Math.round((float) uW * h / Math.max(1, w));
            }
            float u = (WALLPAPER_TEXTURE_WIDTH - uW) / 2f;
            float v = (WALLPAPER_TEXTURE_HEIGHT - vH) / 2f;
            com.examplebett.compat.GuiTextures.blit(g, texture, x0, y0, u, v, w, h,
                    uW, vH, WALLPAPER_TEXTURE_WIDTH, WALLPAPER_TEXTURE_HEIGHT);
            return;
        }
        drawGradient(g, x0, y0, x1, y1, COLOR_WALLPAPER_TOP, COLOR_WALLPAPER_BOTTOM);
    }

    private static void drawGradient(GuiGraphics g, int x0, int y0, int x1, int y1, int top, int bottom) {
        int bands = Math.max(1, (y1 - y0) / (PX * 2));
        for (int i = 0; i < bands; i++) {
            int bandTop = y0 + i * PX * 2;
            int bandBottom = i == bands - 1 ? y1 : bandTop + PX * 2;
            g.fill(x0, bandTop, x1, bandBottom, lerpColor(top, bottom, (float) i / bands));
        }
    }

    private long levelDayTime() {

        return this.minecraft.level.getDayTime();
    }

    private String currentTimeText() {
        if (this.minecraft == null || this.minecraft.level == null) {
            return "--:--";
        }
        long dayTime = Math.floorMod(this.levelDayTime(), 24000L);
        int hours = (int) ((dayTime / 1000L + 6L) % 24L);
        int minutes = (int) (dayTime % 1000L * 60L / 1000L);
        return String.format("%d:%02d", hours, minutes);
    }

    private void drawStatusBar(GuiGraphics g, Layout l) {
        g.fill(l.sx0(), l.sy0(), l.sx1(), l.sy0() + STATUS_BAR_HEIGHT, COLOR_STATUS_BAR);
        this.drawSmallText(g, this.currentTimeText(), l.sx0() + 3, l.sy0() + 3, 0xFFFFFFFF);

        int bx1 = l.sx1() - 3;
        int by0 = l.sy0() + 3;
        g.fill(bx1 - 12, by0, bx1 - 1, by0 + 6, 0xFFFFFFFF);
        g.fill(bx1 - 1, by0 + 2, bx1, by0 + 4, 0xFFFFFFFF);
        g.fill(bx1 - 11, by0 + 1, bx1 - 2, by0 + 5, 0xFF000000);
        g.fill(bx1 - 10, by0 + 2, bx1 - 4, by0 + 4, 0xFF5BD35B);

        for (int i = 0; i < 4; i++) {
            int bx = bx1 - 26 + i * 3;
            g.fill(bx, by0 + 6 - (i + 1) * 3 / 2, bx + 2, by0 + 6, 0xFFFFFFFF);
        }
    }

    private int[] appIconRect(Layout l, int index) {
        int cellW = (l.sx1() - l.sx0()) / APP_COLUMNS;
        int x = l.sx0() + (index % APP_COLUMNS) * cellW + (cellW - APP_ICON_SIZE) / 2;
        int y = l.sy0() + STATUS_BAR_HEIGHT + 8 + (index / APP_COLUMNS) * (APP_ICON_SIZE + 20);
        return new int[]{x, y, x + APP_ICON_SIZE, y + APP_ICON_SIZE};
    }

    private void drawHome(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        drawWallpaper(g, this.wallpaper, l.sx0(), l.sy0(), l.sx1(), l.sy1());

        boolean lightWallpaper = "white".equals(this.wallpaper);
        int labelColor = lightWallpaper ? 0xFF1C1C1E : 0xFFFFFFFF;
        App[] apps = App.values();
        for (int i = 0; i < apps.length; i++) {
            App app = apps[i];
            int[] r = appIconRect(l, i);
            com.examplebett.compat.GuiTextures.blit(g, app.icon, r[0], r[1], 0f, 0f, APP_ICON_SIZE, APP_ICON_SIZE,
                    APP_ICON_TEXTURE_SIZE, APP_ICON_TEXTURE_SIZE, APP_ICON_TEXTURE_SIZE, APP_ICON_TEXTURE_SIZE);
            if (this.pending == null && inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {

                fillPixelRounded(g, r[0], r[1], r[2], r[3], 2, 0x33FFFFFF);
            }

            int cellWidth = (l.sx1() - l.sx0()) / APP_COLUMNS - 2;
            String[] labelLines = app.shortName().split("\n");
            for (int line = 0; line < labelLines.length; line++) {
                float scale = Math.min(SMALL_TEXT_SCALE, (float) cellWidth / Math.max(1, com.examplebett.compat.LegacyGui.width(this.font, labelLines[line])));
                int labelWidth = Math.round(com.examplebett.compat.LegacyGui.width(this.font, labelLines[line]) * scale);
                this.drawScaledText(g, (
                                (labelLines[line])), (r[0] + r[2] - labelWidth) / 2,
                        r[3] + 2 + line * 7, labelColor, scale);
            }
        }

        int dotsY = l.sy1() - PX * 4;
        int cx = (l.sx0() + l.sx1()) / 2 / PX * PX;
        int dimDot = lightWallpaper ? 0x881C1C1E : 0x88FFFFFF;
        g.fill(cx - PX * 3, dotsY, cx - PX * 2, dotsY + PX, labelColor);
        g.fill(cx - PX / 2, dotsY, cx + PX / 2 + 1, dotsY + PX, dimDot);
        g.fill(cx + PX * 2, dotsY, cx + PX * 3, dotsY + PX, dimDot);
    }

    private void drawApp(GuiGraphics g, Layout l, App app, int mouseX, int mouseY) {
        int headerTop = l.sy0() + STATUS_BAR_HEIGHT;
        g.fill(l.sx0(), l.sy0(), l.sx1(), l.sy1(), COLOR_ROW);
        g.fill(l.sx0(), l.sy0(), l.sx1(), headerTop + HEADER_HEIGHT, app.headerColor);

        int ax = l.sx0() + 4;
        int ay = headerTop + HEADER_HEIGHT / 2;
        for (int i = 0; i < 4; i++) {
            g.fill(ax + i, ay - i - 1, ax + i + 1, ay - i + 1, app.titleColor);
            g.fill(ax + i, ay + i - 1, ax + i + 1, ay + i + 1, app.titleColor);
        }
        int titleWidth = com.examplebett.compat.LegacyGui.width(this.font, app.title());
        int titleRoom = l.sx1() - l.sx0() - 24;
        if (titleWidth <= titleRoom) {
            g.drawString(this.font, app.title(), (l.sx0() + l.sx1() - titleWidth) / 2, headerTop + 4, app.titleColor, false);
        } else {
            int scaled = Math.round(titleWidth * SMALL_TEXT_SCALE);
            this.drawSmallText(g, app.title(), (l.sx0() + l.sx1() - scaled) / 2, headerTop + 5, app.titleColor);
        }
        if (app.weather) {
            this.drawWeather(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.EMERGENCY) {
            this.drawEmergency(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.DETONATOR) {
            this.drawDetonator(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.CLOCK) {
            this.drawClock(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.MASSIVE_AUTO) {
            this.drawMassiveAuto(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.SLOT) {
            this.drawSlot(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.SETTINGS) {
            this.drawSettings(g, l, mouseX, mouseY);
            return;
        }
        if (app == App.MUSIC && !this.musicBrowsing) {
            this.drawMusicHome(g, l, mouseX, mouseY);
            return;
        }
        int costTop = headerTop + HEADER_HEIGHT;
        g.fill(l.sx0(), costTop, l.sx1(), costTop + COST_BAR_HEIGHT, COLOR_COST_BAR);
        Component cost = app == App.MUSIC ? this.musicStatus() : this.isFree()
                ? ModText.translatable("gui.mineturismo.smart_dispatch.free")
                : ModText.translatable(app.vehicles ? "gui.mineturismo.smart_dispatch.cost" : "gui.mineturismo.smart_dispatch.price",
                        costOf(app));
        this.drawSmallText(g, cost.getString(), l.sx0() + 3, costTop + 2, 0xFFE8E8E8);

        List<Entry> entries = this.entries();
        int listTop = l.listTop();
        int rows = l.visibleRows();
        this.scrollRow = Math.max(0, Math.min(this.maxScrollRow(l), this.scrollRow));
        int listRight = l.sx1() - 4;
        for (int i = 0; i < rows; i++) {
            int index = this.scrollRow + i;
            if (index >= entries.size()) {
                break;
            }
            Entry e = entries.get(index);
            int top = listTop + i * ROW_HEIGHT;
            boolean hover = this.pending == null && inside(mouseX, mouseY, l.sx0(), top, listRight, top + ROW_HEIGHT);
            boolean playing = app == App.MUSIC && com.examplebett.client.sound.ChorusMusicPlayer.current() != null
                    && com.examplebett.client.sound.ChorusMusicPlayer.current().id().equals(e.id());
            g.fill(l.sx0(), top, listRight, top + ROW_HEIGHT,
                    hover ? COLOR_ROW_HOVER : playing ? 0xFFFFD9DF : index % 2 == 0 ? COLOR_ROW : COLOR_ROW_ALT);
            g.renderItem(e.stack(), l.sx0() + 2, top + 2);
            int textX = l.sx0() + 21;
            this.drawSmallText(g, this.fit(e.name().getString(), listRight - textX - 2), textX, top + 7, 0xFF202024);
        }

        g.fill(listRight, listTop, l.sx1(), l.sy1(), COLOR_SCROLL_TRACK);
        int max = this.maxScrollRow(l);
        if (max > 0) {
            int trackH = l.sy1() - listTop;
            int thumbH = this.thumbHeight(l);
            int thumbTop = listTop + (trackH - thumbH) * this.scrollRow / max;
            g.fill(listRight + 1, thumbTop, l.sx1() - 1, thumbTop + thumbH, COLOR_SCROLL_THUMB);
        }
        if (System.currentTimeMillis() < this.purchasedBannerUntil) {
            this.aboveItems(g);
            String text = ModText.translatable("gui.mineturismo.smart_dispatch.purchased").getString();
            int w = Math.round(com.examplebett.compat.LegacyGui.width(this.font, text) * SMALL_TEXT_SCALE) + 10;
            int bx0 = (l.sx0() + l.sx1() - w) / 2;
            int by0 = l.sy1() - 18;
            fillPixelRounded(g, bx0, by0, bx0 + w, by0 + 12, 1, COLOR_BANNER);
            this.drawSmallText(g, text, bx0 + 5, by0 + 3, 0xFFFFFFFF);
        }
    }

    private int settingsRowCount() {
        return this.settingsPage == SETTINGS_PAGE_WALLPAPER ? SmartDispatchAppItem.WALLPAPERS.length
                : this.settingsPage == SETTINGS_PAGE_MODEL ? SmartDispatchAppItem.PHONE_MODELS.length : 3;
    }

    private record ControlRow(String labelKey, String keyText, net.minecraft.client.KeyMapping rebind) {
    }

    private static String keyName(net.minecraft.client.KeyMapping mapping) {
        return mapping.getTranslatedKeyMessage();
    }

    private List<ControlRow> controlRows() {
        net.minecraft.client.Options o = this.minecraft.options;
        net.minecraft.client.KeyMapping horn = com.examplebett.client.AiObjectModClient.hornKey();
        net.minecraft.client.KeyMapping trailer = com.examplebett.client.AiObjectModClient.trailerCouplingKey();
        String p = "gui.mineturismo.smart_dispatch.controls_";
        return List.of(
                new ControlRow(p + "accelerate", keyName(o.keyUp), null),
                new ControlRow(p + "brake", keyName(o.keyDown), null),
                new ControlRow(p + "steer", keyName(o.keyLeft) + " / " + keyName(o.keyRight), null),
                new ControlRow(p + "drift", keyName(o.keyJump), null),
                new ControlRow(p + "burnout", keyName(o.keyUp) + "+" + keyName(o.keyDown), null),

                new ControlRow(p + "jturn", keyName(o.keyDown) + "+" + keyName(o.keyLeft) + "/" + keyName(o.keyRight), null),

                new ControlRow(p + "wheelie", keyName(o.keySprint), null),
                new ControlRow(p + "aircraft", keyName(o.keyJump) + " / " + keyName(o.keySprint), null),
                new ControlRow(p + "dismount", keyName(o.keySneak), null),
                new ControlRow(p + "horn", keyName(horn), horn),
                new ControlRow(p + "trailer", keyName(trailer), trailer));
    }

    private int[] controlsRowRect(Layout l, int index) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT + 16 + index * CONTROLS_ROW_HEIGHT;
        return new int[]{l.sx0() + 5, top, l.sx1() - 5, top + CONTROLS_ROW_HEIGHT};
    }

    private void drawControls(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int width = l.sx1() - l.sx0();
        this.drawSmallText(g, this.fit(ModText.translatable("gui.mineturismo.smart_dispatch.settings_controls").getString(), width - 12),
                l.sx0() + 7, top + 6, 0xFF6C6C72);
        List<ControlRow> rows = this.controlRows();
        int[] first = this.controlsRowRect(l, 0);
        int[] last = this.controlsRowRect(l, rows.size() - 1);
        fillPixelRounded(g, first[0], first[1], last[2], last[3], 1, 0xFFFFFFFF);
        for (int i = 0; i < rows.size(); i++) {
            ControlRow row = rows.get(i);
            int[] r = this.controlsRowRect(l, i);
            if (row.rebind() != null && inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
                g.fill(r[0] + 1, r[1] + (i == 0 ? 1 : 0), r[2] - 1, r[3] - (i == rows.size() - 1 ? 1 : 0), 0xFFE5E5EA);
            }
            if (i > 0) {
                g.fill(r[0] + 4, r[1], r[2], r[1] + 1, 0xFFE5E5EA);
            }

            boolean waiting = row.rebind() != null && row.rebind() == this.rebindingKey;
            String key = waiting ? ModText.translatable("gui.mineturismo.smart_dispatch.controls_press_key").getString() : row.keyText();
            key = this.fit(key, (r[2] - r[0]) / 2 - 4);
            int keyWidth = Math.round(com.examplebett.compat.LegacyGui.width(this.font, key) * SMALL_TEXT_SCALE);
            int keyColor = waiting ? 0xFFFF9500 : row.rebind() != null ? 0xFF0A84FF : 0xFF6C6C72;
            this.drawSmallText(g, key, r[2] - 4 - keyWidth, r[1] + 3, keyColor);
            String label = this.fit(ModText.translatable(row.labelKey()).getString(), r[2] - r[0] - keyWidth - 12);
            this.drawSmallText(g, label, r[0] + 4, r[1] + 3, 0xFF202024);
        }

        int noteY = last[3] + 4;
        for (String noteKey : new String[]{"controls_note_rebind", "controls_note_vanilla"}) {
            String note = ModText.translatable("gui.mineturismo.smart_dispatch." + noteKey).getString();
            for (String line : this.wrapSmall(note, width - 12)) {

                if (noteY + 7 > l.sy1() - 2) {
                    break;
                }
                this.drawSmallText(g, line, l.sx0() + 7, noteY, 0xFF8E8E93);
                noteY += 7;
            }
            noteY += 2;
        }
    }

    private List<String> wrapSmall(String text, int maxWidth) {
        List<String> lines = new java.util.ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String next = line.toString() + text.charAt(i);
            if (line.length() > 0 && Math.round(com.examplebett.compat.LegacyGui.width(this.font, next) * SMALL_TEXT_SCALE) > maxWidth) {
                lines.add(line.toString());
                line.setLength(0);
            }
            line.append(text.charAt(i));
        }
        if (line.length() > 0) {
            lines.add(line.toString());
        }
        return lines;
    }

    private boolean handleControlsClick(Layout l, double mouseX, double mouseY) {
        List<ControlRow> rows = this.controlRows();
        for (int i = 0; i < rows.size(); i++) {
            int[] r = this.controlsRowRect(l, i);
            if (rows.get(i).rebind() != null && inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {

                this.rebindingKey = this.rebindingKey == rows.get(i).rebind() ? null : rows.get(i).rebind();
                return true;
            }
        }
        this.rebindingKey = null;
        return false;
    }

    private boolean handleRebindKey(int keyCode, java.util.function.Supplier<com.mojang.blaze3d.platform.InputConstants.Key> key) {
        if (this.rebindingKey == null || this.openApp != App.SETTINGS || this.settingsPage != SETTINGS_PAGE_CONTROLS) {
            return false;
        }
        if (keyCode != org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            this.rebindingKey.setKey(key.get());
            net.minecraft.client.KeyMapping.resetMapping();
            this.minecraft.options.save();
        }
        this.rebindingKey = null;
        return true;
    }

    private int[] settingsRowRect(Layout l, int index) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT + 16 + index * SETTINGS_ROW_HEIGHT;
        return new int[]{l.sx0() + 5, top, l.sx1() - 5, top + SETTINGS_ROW_HEIGHT};
    }

    private void drawSettings(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFFF2F2F7);
        if (this.settingsPage == SETTINGS_PAGE_CONTROLS) {
            this.drawControls(g, l, mouseX, mouseY);
            return;
        }
        String section = this.settingsPage == SETTINGS_PAGE_WALLPAPER ? "gui.mineturismo.smart_dispatch.settings_wallpaper"
                : this.settingsPage == SETTINGS_PAGE_MODEL ? "gui.mineturismo.smart_dispatch.settings_model" : null;
        if (section != null) {
            this.drawSmallText(g, this.fit(ModText.translatable(section).getString(), l.sx1() - l.sx0() - 12), l.sx0() + 7, top + 6, 0xFF6C6C72);
        }
        int count = this.settingsRowCount();
        int[] first = this.settingsRowRect(l, 0);
        int[] last = this.settingsRowRect(l, count - 1);
        fillPixelRounded(g, first[0], first[1], last[2], last[3], 1, 0xFFFFFFFF);
        for (int i = 0; i < count; i++) {
            int[] r = this.settingsRowRect(l, i);
            if (inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
                g.fill(r[0] + 1, r[1] + (i == 0 ? 1 : 0), r[2] - 1, r[3] - (i == count - 1 ? 1 : 0), 0xFFE5E5EA);
            }
            if (i > 0) {
                g.fill(r[0] + 6, r[1], r[2], r[1] + 1, 0xFFD8D8DE);
            }
            int textX = r[0] + 6;
            String label;
            boolean selected = false;
            if (this.settingsPage == SETTINGS_PAGE_WALLPAPER) {
                String id = SmartDispatchAppItem.WALLPAPERS[i];

                g.fill(r[0] + 4, r[1] + 2, r[0] + 16, r[3] - 2, 0xFF121216);
                drawWallpaper(g, id, r[0] + 5, r[1] + 3, r[0] + 15, r[3] - 3);
                textX = r[0] + 21;
                label = ModText.translatable("gui.mineturismo.smart_dispatch.wallpaper_" + id).getString();
                selected = id.equals(this.wallpaper);
            } else if (this.settingsPage == SETTINGS_PAGE_MODEL) {
                String id = SmartDispatchAppItem.PHONE_MODELS[i];
                ItemStack icon = new ItemStack(com.examplebett.item.AiObjectModItems.SMART_DISPATCH_APP);
                SmartDispatchAppItem.applyPhoneModelIcon(icon, id);
                g.renderItem(icon, r[0] + 3, r[1] + 2);
                textX = r[0] + 21;
                label = PHONE_MODEL_NAMES[i] != null ? PHONE_MODEL_NAMES[i]
                        : ModText.translatable("gui.mineturismo.smart_dispatch.model_classic").getString();
                selected = id.equals(this.phoneModel);
            } else {
                label = ModText.translatable(i == 0 ? "gui.mineturismo.smart_dispatch.settings_wallpaper"
                        : i == 1 ? "gui.mineturismo.smart_dispatch.settings_model"
                        : "gui.mineturismo.smart_dispatch.settings_controls").getString();

                int ax = r[2] - 8;
                int ay = (r[1] + r[3]) / 2;
                for (int k = 0; k < 3; k++) {
                    g.fill(ax + k, ay - 3 + k, ax + k + 1, ay - 1 + k, 0xFFB4B4BA);
                    g.fill(ax + k, ay + 1 - k, ax + k + 1, ay + 3 - k, 0xFFB4B4BA);
                }
            }
            this.drawSmallText(g, this.fit(label, r[2] - textX - 14), textX, (r[1] + r[3]) / 2 - 3, 0xFF202024);
            if (selected) {

                int cx0 = r[2] - 11;
                int cy0 = (r[1] + r[3]) / 2;
                int[][] check = {{0, 0}, {1, 1}, {2, 2}, {3, 1}, {4, 0}, {5, -1}, {6, -2}, {7, -3}};
                for (int[] p : check) {
                    g.fill(cx0 + p[0], cy0 + p[1] - 1, cx0 + p[0] + 1, cy0 + p[1] + 1, 0xFF0A84FF);
                }
            }
        }
    }

    private boolean handleSettingsClick(Layout l, double mouseX, double mouseY) {
        if (this.settingsPage == SETTINGS_PAGE_CONTROLS) {
            return this.handleControlsClick(l, mouseX, mouseY);
        }
        for (int i = 0; i < this.settingsRowCount(); i++) {
            int[] r = this.settingsRowRect(l, i);
            if (!inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
                continue;
            }
            if (this.settingsPage == SETTINGS_PAGE_MAIN) {
                this.settingsPage = i == 0 ? SETTINGS_PAGE_WALLPAPER : i == 1 ? SETTINGS_PAGE_MODEL : SETTINGS_PAGE_CONTROLS;
                this.rebindingKey = null;
                return true;
            }
            if (this.settingsPage == SETTINGS_PAGE_WALLPAPER) {
                this.wallpaper = SmartDispatchAppItem.WALLPAPERS[i];
            } else {
                this.phoneModel = SmartDispatchAppItem.PHONE_MODELS[i];
                this.skin = PhoneSkin.byId(this.phoneModel);
            }
            com.examplebett.network.SetPhoneStylePayload payload = new com.examplebett.network.SetPhoneStylePayload(this.phoneModel, this.wallpaper);
            ModClientNetworking.sendToServer(com.examplebett.network.SetPhoneStylePayload.ID, payload, com.examplebett.network.SetPhoneStylePayload::write);
            return true;
        }
        return false;
    }

    private static final int WEATHER_SNOW = 3;
    private static final int WEATHER_BUTTON_HEIGHT = 14;
    private static final int WEATHER_BUTTON_GAP = 3;

    private static final int WEATHER_ICON_UNIT = 2;
    private static final int[] WEATHER_PICKS = {SmartDispatchAppItem.WEATHER_CLEAR, SmartDispatchAppItem.WEATHER_RAIN, SmartDispatchAppItem.WEATHER_THUNDER};
    private static final String[] WEATHER_KEYS = {"weather_clear", "weather_rain", "weather_thunder", "weather_snow"};

    private int currentWeather() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) {
            return SmartDispatchAppItem.WEATHER_CLEAR;
        }
        net.minecraft.client.multiplayer.MultiPlayerLevel level = this.minecraft.level;
        if (!level.isRaining()) {
            return SmartDispatchAppItem.WEATHER_CLEAR;
        }
        net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos(this.minecraft.player);

        net.minecraft.world.level.biome.Biome.Precipitation precipitation = level.getBiome(pos).getPrecipitation();
        if (precipitation == net.minecraft.world.level.biome.Biome.Precipitation.NONE) {
            return SmartDispatchAppItem.WEATHER_CLEAR;
        }
        if (level.isThundering()) {
            return SmartDispatchAppItem.WEATHER_THUNDER;
        }
        return precipitation == net.minecraft.world.level.biome.Biome.Precipitation.SNOW ? WEATHER_SNOW : SmartDispatchAppItem.WEATHER_RAIN;
    }

    private Component currentPlaceName() {
        if (this.minecraft == null || this.minecraft.level == null || this.minecraft.player == null) {
            return ModText.literal("-");
        }

        return this.minecraft.level.getBiome(new net.minecraft.core.BlockPos(this.minecraft.player)).getName();
    }

    private int weatherButtonTop(Layout l, int index) {

        int changeTop = l.sy1() - WEATHER_BUTTON_HEIGHT - 5;
        return index == WEATHER_PICKS.length ? changeTop
                : changeTop - (WEATHER_PICKS.length - index) * (WEATHER_BUTTON_HEIGHT + WEATHER_BUTTON_GAP);
    }

    private void drawWeatherButton(GuiGraphics g, Layout l, int top, String label, int color, int hoverColor, int textColor,
                                   int mouseX, int mouseY) {
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        boolean hover = inside(mouseX, mouseY, x0, top, x1, top + WEATHER_BUTTON_HEIGHT);
        fillPixelRounded(g, x0, top, x1, top + WEATHER_BUTTON_HEIGHT, 1, hover ? hoverColor : color);
        g.drawString(this.font, label, (x0 + x1 - com.examplebett.compat.LegacyGui.width(this.font, label)) / 2, top + 3, textColor, false);
    }

    private void drawWeather(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int weather = this.currentWeather();

        int skyTop;
        int skyBottom;
        switch (weather) {
            case SmartDispatchAppItem.WEATHER_RAIN -> { skyTop = 0xFF7A8796; skyBottom = 0xFF4E5A68; }
            case SmartDispatchAppItem.WEATHER_THUNDER -> { skyTop = 0xFF4A4F5C; skyBottom = 0xFF262A33; }
            case WEATHER_SNOW -> { skyTop = 0xFFB9C9DA; skyBottom = 0xFF8EA3BA; }
            default -> { skyTop = 0xFF6FB8F5; skyBottom = 0xFF3D8BD9; }
        }
        int bands = Math.max(1, (l.sy1() - top) / (PX * 2));
        for (int i = 0; i < bands; i++) {
            int bandTop = top + i * PX * 2;
            g.fill(l.sx0(), bandTop, l.sx1(), i == bands - 1 ? l.sy1() : bandTop + PX * 2, lerpColor(skyTop, skyBottom, (float) i / bands));
        }
        int cx = (l.sx0() + l.sx1()) / 2;
        int iconSize = 16 * WEATHER_ICON_UNIT;
        int iconTop = top + 8;
        this.drawWeatherIcon(g, cx - iconSize / 2, iconTop, weather);

        int textTop = iconTop + iconSize + 6;
        String placeLabel = ModText.translatable("gui.mineturismo.smart_dispatch.weather_location").getString();
        int labelWidth = Math.round(com.examplebett.compat.LegacyGui.width(this.font, placeLabel) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, placeLabel, cx - labelWidth / 2, textTop, 0xDDFFFFFF);
        String place = this.currentPlaceName().getString();
        int room = l.sx1() - l.sx0() - 8;
        if (com.examplebett.compat.LegacyGui.width(this.font, place) <= room) {
            g.drawString(this.font, place, cx - com.examplebett.compat.LegacyGui.width(this.font, place) / 2, textTop + 9, 0xFFFFFFFF, true);
        } else {
            String fitted = this.fit(place, room);
            this.drawSmallText(g, fitted, cx - Math.round(com.examplebett.compat.LegacyGui.width(this.font, fitted) * SMALL_TEXT_SCALE) / 2, textTop + 10, 0xFFFFFFFF);
        }
        String weatherName = ModText.translatable("gui.mineturismo.smart_dispatch." + WEATHER_KEYS[weather]).getString();
        g.drawString(this.font, weatherName, cx - com.examplebett.compat.LegacyGui.width(this.font, weatherName) / 2, textTop + 22, 0xFFFFFFFF, true);

        if (this.weatherPickerOpen) {
            for (int i = 0; i < WEATHER_PICKS.length; i++) {
                String label = ModText.translatable("gui.mineturismo.smart_dispatch." + WEATHER_KEYS[WEATHER_PICKS[i]]).getString();
                this.drawWeatherButton(g, l, this.weatherButtonTop(l, i), label, 0xFFFFFFFF, 0xFFFFF1B8, 0xFF202024, mouseX, mouseY);
            }
        }
        String change = ModText.translatable("gui.mineturismo.smart_dispatch.weather_change").getString();
        this.drawWeatherButton(g, l, this.weatherButtonTop(l, WEATHER_PICKS.length), change, 0xFF2A2A30, 0xFF44444E, 0xFFFFFFFF, mouseX, mouseY);
    }

    private void cell(GuiGraphics g, int ox, int oy, int ux, int uy, int w, int h, int color) {
        int u = WEATHER_ICON_UNIT;
        g.fill(ox + ux * u, oy + uy * u, ox + (ux + w) * u, oy + (uy + h) * u, color);
    }

    private void disc(GuiGraphics g, int ox, int oy, int cx, int cy, int r, int color) {
        for (int dy = -r; dy <= r; dy++) {
            int w = (int) Math.round(Math.sqrt(r * r - dy * dy));
            this.cell(g, ox, oy, cx - w, cy + dy, w * 2 + 1, 1, color);
        }
    }

    private void cloud(GuiGraphics g, int ox, int oy, int color, int shade) {
        this.disc(g, ox, oy, 5, 6, 3, color);
        this.disc(g, ox, oy, 9, 5, 4, color);
        this.cell(g, ox, oy, 2, 6, 12, 4, color);
        this.cell(g, ox, oy, 2, 9, 12, 1, shade);
    }

    private void drawWeatherIcon(GuiGraphics g, int ox, int oy, int weather) {
        switch (weather) {
            case SmartDispatchAppItem.WEATHER_CLEAR -> {
                int sun = 0xFFFFD640;
                int rim = 0xFFF5A01E;
                this.cell(g, ox, oy, 7, 0, 2, 2, sun);
                this.cell(g, ox, oy, 7, 14, 2, 2, sun);
                this.cell(g, ox, oy, 0, 7, 2, 2, sun);
                this.cell(g, ox, oy, 14, 7, 2, 2, sun);
                this.cell(g, ox, oy, 2, 2, 2, 2, sun);
                this.cell(g, ox, oy, 12, 2, 2, 2, sun);
                this.cell(g, ox, oy, 2, 12, 2, 2, sun);
                this.cell(g, ox, oy, 12, 12, 2, 2, sun);
                this.disc(g, ox, oy, 8, 8, 4, rim);
                this.disc(g, ox, oy, 8, 8, 3, sun);
                this.cell(g, ox, oy, 6, 6, 1, 1, 0xFFFFF4B0);
            }
            case SmartDispatchAppItem.WEATHER_RAIN -> {
                this.cloud(g, ox, oy, 0xFFE4E8EE, 0xFFB4BCC8);
                int drop = 0xFF5FB6FF;
                this.cell(g, ox, oy, 4, 11, 1, 2, drop);
                this.cell(g, ox, oy, 8, 12, 1, 2, drop);
                this.cell(g, ox, oy, 12, 11, 1, 2, drop);
                this.cell(g, ox, oy, 6, 14, 1, 2, drop);
                this.cell(g, ox, oy, 10, 14, 1, 2, drop);
            }
            case SmartDispatchAppItem.WEATHER_THUNDER -> {
                this.cloud(g, ox, oy, 0xFF8A909C, 0xFF5E6470);
                int bolt = 0xFFFFE14A;
                this.cell(g, ox, oy, 8, 10, 2, 1, bolt);
                this.cell(g, ox, oy, 7, 11, 2, 1, bolt);
                this.cell(g, ox, oy, 6, 12, 4, 1, bolt);
                this.cell(g, ox, oy, 8, 13, 1, 1, bolt);
                this.cell(g, ox, oy, 7, 14, 1, 1, bolt);
                this.cell(g, ox, oy, 6, 15, 1, 1, bolt);
            }
            default -> {
                this.cloud(g, ox, oy, 0xFFF4F6FA, 0xFFC4CCD8);
                int flake = 0xFFFFFFFF;
                this.cell(g, ox, oy, 4, 12, 1, 1, flake);
                this.cell(g, ox, oy, 8, 11, 1, 1, flake);
                this.cell(g, ox, oy, 12, 12, 1, 1, flake);
                this.cell(g, ox, oy, 6, 14, 1, 1, flake);
                this.cell(g, ox, oy, 10, 14, 1, 1, flake);
            }
        }
    }

    private boolean handleWeatherClick(Layout l, double mouseX, double mouseY) {
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        int changeTop = this.weatherButtonTop(l, WEATHER_PICKS.length);
        if (inside(mouseX, mouseY, x0, changeTop, x1, changeTop + WEATHER_BUTTON_HEIGHT)) {
            this.weatherPickerOpen = !this.weatherPickerOpen;
            return true;
        }
        if (this.weatherPickerOpen) {
            for (int i = 0; i < WEATHER_PICKS.length; i++) {
                int top = this.weatherButtonTop(l, i);
                if (inside(mouseX, mouseY, x0, top, x1, top + WEATHER_BUTTON_HEIGHT)) {
                    com.examplebett.network.SetAppWeatherPayload payload = new com.examplebett.network.SetAppWeatherPayload(WEATHER_PICKS[i]);
                    ModClientNetworking.sendToServer(com.examplebett.network.SetAppWeatherPayload.ID, payload, com.examplebett.network.SetAppWeatherPayload::write);
                    this.weatherPickerOpen = false;
                    return true;
                }
            }
        }
        return false;
    }

    private static final int EMERGENCY_BUTTON_HEIGHT = 30;

    private int[] emergencyButtonRect(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int y0 = (top + l.sy1()) / 2 - EMERGENCY_BUTTON_HEIGHT / 2;
        return new int[]{l.sx0() + 10, y0, l.sx1() - 10, y0 + EMERGENCY_BUTTON_HEIGHT};
    }

    private void drawEmergency(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFFF6F0F0);
        String lead = ModText.translatable("gui.mineturismo.smart_dispatch.emergency_lead").getString();
        String leadFitted = this.fit(lead, l.sx1() - l.sx0() - 8);
        this.drawSmallText(g, leadFitted, cx - Math.round(com.examplebett.compat.LegacyGui.width(this.font, leadFitted) * SMALL_TEXT_SCALE) / 2, top + 10, 0xFF5A1C1C);
        int[] r = this.emergencyButtonRect(l);
        boolean hover = inside(mouseX, mouseY, r[0], r[1], r[2], r[3]);

        fillPixelRounded(g, r[0], r[1] + 2, r[2], r[3] + 2, PX, 0xFF8E1414);
        fillPixelRounded(g, r[0], r[1], r[2], r[3], PX, hover ? 0xFFEF4A3E : 0xFFD32F2F);
        String label = ModText.translatable("gui.mineturismo.smart_dispatch.emergency_call").getString();
        g.drawString(this.font, label, cx - com.examplebett.compat.LegacyGui.width(this.font, label) / 2, (r[1] + r[3]) / 2 - 4, 0xFFFFFFFF, true);

        Component note = ModText.translatable("gui.mineturismo.smart_dispatch.emergency_note");
        List<String> lines = com.examplebett.compat.LegacyGui.split(this.font, note, Math.round((l.sx1() - l.sx0() - 12) / SMALL_TEXT_SCALE));
        for (int i = 0; i < Math.min(3, lines.size()); i++) {
            int w = Math.round(com.examplebett.compat.LegacyGui.width(this.font, lines.get(i)) * SMALL_TEXT_SCALE);
            this.drawSmallText(g, lines.get(i), cx - w / 2, r[3] + 10 + i * 8, 0xFF6A6A70);
        }
    }

    private boolean handleEmergencyClick(Layout l, double mouseX, double mouseY) {
        int[] r = this.emergencyButtonRect(l);
        if (!inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
            return false;
        }
        com.examplebett.network.RequestEmergencyCallPayload payload = new com.examplebett.network.RequestEmergencyCallPayload();
        ModClientNetworking.sendToServer(com.examplebett.network.RequestEmergencyCallPayload.ID, payload, com.examplebett.network.RequestEmergencyCallPayload::write);
        this.onClose();
        return true;
    }

    private static final int DETONATOR_BUTTON_SIZE = 40;

    private int[] detonatorButtonRect(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        int y0 = (top + l.sy1()) / 2 - DETONATOR_BUTTON_SIZE / 2 - 6;
        return new int[]{cx - DETONATOR_BUTTON_SIZE / 2, y0, cx + DETONATOR_BUTTON_SIZE / 2, y0 + DETONATOR_BUTTON_SIZE};
    }

    private void drawDetonator(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFF1C1D21);
        String lead = ModText.translatable("gui.mineturismo.smart_dispatch.detonator_lead").getString();
        String leadFitted = this.fit(lead, l.sx1() - l.sx0() - 8);
        this.drawSmallText(g, leadFitted, cx - Math.round(com.examplebett.compat.LegacyGui.width(this.font, leadFitted) * SMALL_TEXT_SCALE) / 2, top + 10, 0xFFFFC94A);
        int[] r = this.detonatorButtonRect(l);
        boolean hover = inside(mouseX, mouseY, r[0], r[1], r[2], r[3]);

        int bcx = (r[0] + r[2]) / 2;
        int bcy = (r[1] + r[3]) / 2;
        int radius = DETONATOR_BUTTON_SIZE / 2;
        fillPixelCircle(g, bcx, bcy + 1, radius + 4, 0xFF96989F);
        fillPixelCircle(g, bcx, bcy + 3, radius, 0xFF780E12);
        fillPixelCircle(g, bcx, bcy, radius, hover ? 0xFFF04040 : 0xFFDE2424);
        String label = ModText.translatable("gui.mineturismo.smart_dispatch.detonator_button").getString();
        g.drawString(this.font, label, cx - com.examplebett.compat.LegacyGui.width(this.font, label) / 2, (r[1] + r[3]) / 2 - 4, 0xFFFFFFFF, true);

        int stripeTop = r[3] + 10;
        for (int x = l.sx0(); x < l.sx1(); x += PX * 2) {
            int stripe = ((x - l.sx0()) / (PX * 2)) % 2 == 0 ? 0xFFF6C41E : 0xFF18181A;
            g.fill(x, stripeTop, Math.min(l.sx1(), x + PX * 2), stripeTop + PX * 2, stripe);
        }
        Component note = ModText.translatable("gui.mineturismo.smart_dispatch.detonator_note");
        List<String> lines = com.examplebett.compat.LegacyGui.split(this.font, note, Math.round((l.sx1() - l.sx0() - 12) / SMALL_TEXT_SCALE));
        for (int i = 0; i < Math.min(3, lines.size()); i++) {
            int w = Math.round(com.examplebett.compat.LegacyGui.width(this.font, lines.get(i)) * SMALL_TEXT_SCALE);
            this.drawSmallText(g, lines.get(i), cx - w / 2, stripeTop + PX * 2 + 6 + i * 8, 0xFFB0B0B8);
        }
    }

    private boolean handleDetonatorClick(Layout l, double mouseX, double mouseY) {
        int[] r = this.detonatorButtonRect(l);
        if (!inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
            return false;
        }
        com.examplebett.network.DetonateBombsPayload payload = new com.examplebett.network.DetonateBombsPayload();
        ModClientNetworking.sendToServer(com.examplebett.network.DetonateBombsPayload.ID, payload, com.examplebett.network.DetonateBombsPayload::write);
        this.onClose();
        return true;
    }

    private static final int MUSIC_BUTTON_HEIGHT = 16;
    private static final int MUSIC_PINK = 0xFFF5354F;
    private static final int MUSIC_PINK_HOVER = 0xFFFF5A70;

    private Component musicStatus() {
        com.examplebett.client.sound.ChorusMusicTracks.Track current = com.examplebett.client.sound.ChorusMusicPlayer.current();
        return current == null ? ModText.translatable("gui.mineturismo.smart_dispatch.music_stopped")
                : ModText.translatable("gui.mineturismo.smart_dispatch.music_now_playing", current.title());
    }

    private int[][] musicRects(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        int browseTop = top + 34;
        int volumeTop = browseTop + MUSIC_BUTTON_HEIGHT + 22;
        int rowTop = volumeTop + 18;
        int half = (x1 - x0 - 4) / 2;
        return new int[][]{
                {x0, browseTop, x1, browseTop + MUSIC_BUTTON_HEIGHT},
                {x0, volumeTop, x1, volumeTop + 8},
                {x0, rowTop, x0 + half, rowTop + MUSIC_BUTTON_HEIGHT},
                {x1 - half, rowTop, x1, rowTop + MUSIC_BUTTON_HEIGHT}
        };
    }

    private double musicVolume() {
        return this.minecraft.options.getSoundSourceVolume(net.minecraft.sounds.SoundSource.MUSIC);
    }

    private void setMusicVolume(double volume) {
        this.minecraft.options.setSoundCategoryVolume(net.minecraft.sounds.SoundSource.MUSIC, (float) volume);
    }

    private void drawMusicButton(GuiGraphics g, int[] r, String label, int color, int hoverColor, int textColor, int mouseX, int mouseY) {
        boolean hover = this.pending == null && inside(mouseX, mouseY, r[0], r[1], r[2], r[3]);
        fillPixelRounded(g, r[0], r[1], r[2], r[3], 1, hover ? hoverColor : color);
        String fitted = this.fit(label, r[2] - r[0] - 4);
        int w = Math.round(com.examplebett.compat.LegacyGui.width(this.font, fitted) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, fitted, (r[0] + r[2] - w) / 2, (r[1] + r[3]) / 2 - 3, textColor);
    }

    private void drawMusicHome(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFFFFF5F7);

        String status = this.fit(this.musicStatus().getString(), l.sx1() - l.sx0() - 22);
        int statusWidth = Math.round(com.examplebett.compat.LegacyGui.width(this.font, status) * SMALL_TEXT_SCALE);
        int noteX = cx - (statusWidth + 10) / 2;
        int noteY = top + 10;
        g.fill(noteX + 3, noteY, noteX + 4, noteY + 6, MUSIC_PINK);
        g.fill(noteX + 4, noteY, noteX + 6, noteY + 1, MUSIC_PINK);
        g.fill(noteX + 1, noteY + 5, noteX + 4, noteY + 7, MUSIC_PINK);
        this.drawSmallText(g, status, noteX + 10, noteY + 1, 0xFF3A2A2E);

        int[][] r = this.musicRects(l);
        this.drawMusicButton(g, r[0], ModText.translatable("gui.mineturismo.smart_dispatch.music_browse").getString(),
                MUSIC_PINK, MUSIC_PINK_HOVER, 0xFFFFFFFF, mouseX, mouseY);

        double volume = this.musicVolume();
        String volumeLabel = ModText.translatable("soundCategory.music").getString() + ": " + Math.round(volume * 100) + "%";
        this.drawSmallText(g, volumeLabel, r[1][0], r[1][1] - 9, 0xFF3A2A2E);
        int[] bar = r[1];
        int trackTop = (bar[1] + bar[3]) / 2 - 1;
        g.fill(bar[0], trackTop, bar[2], trackTop + 3, 0xFFE2C6CC);
        int knobX = bar[0] + (int) Math.round((bar[2] - bar[0] - 6) * volume);
        g.fill(bar[0], trackTop, knobX + 3, trackTop + 3, MUSIC_PINK);
        fillPixelRounded(g, knobX, bar[1], knobX + 6, bar[3], 1, this.draggingVolume ? MUSIC_PINK_HOVER : 0xFFFFFFFF);
        g.fill(knobX + 2, bar[1] + 2, knobX + 4, bar[3] - 2, MUSIC_PINK);

        Component loopState = ModText.translatable(com.examplebett.client.sound.ChorusMusicPlayer.isLoop() ? "options.on" : "options.off");
        boolean loopOn = com.examplebett.client.sound.ChorusMusicPlayer.isLoop();
        this.drawMusicButton(g, r[2], ModText.translatable("gui.mineturismo.smart_dispatch.music_loop", loopState).getString(),
                loopOn ? MUSIC_PINK : 0xFFDCC9CD, loopOn ? MUSIC_PINK_HOVER : 0xFFE8D8DB, loopOn ? 0xFFFFFFFF : 0xFF3A2A2E, mouseX, mouseY);
        this.drawMusicButton(g, r[3], ModText.translatable("gui.mineturismo.smart_dispatch.music_stop").getString(),
                0xFF3A2A2E, 0xFF54444A, 0xFFFFFFFF, mouseX, mouseY);
    }

    private void setVolumeFromMouse(Layout l, double mouseX) {
        int[] bar = this.musicRects(l)[1];
        double t = (mouseX - bar[0] - 3) / Math.max(1.0, bar[2] - bar[0] - 6);
        this.setMusicVolume(Math.max(0.0, Math.min(1.0, t)));
    }

    private boolean handleMusicHomeClick(Layout l, double mouseX, double mouseY) {
        int[][] r = this.musicRects(l);
        if (inside(mouseX, mouseY, r[0][0], r[0][1], r[0][2], r[0][3])) {
            this.musicBrowsing = true;
            this.scrollRow = 0;
            return true;
        }
        if (inside(mouseX, mouseY, r[1][0] - 2, r[1][1] - 3, r[1][2] + 2, r[1][3] + 3)) {
            this.draggingVolume = true;
            this.setVolumeFromMouse(l, mouseX);
            return true;
        }
        if (inside(mouseX, mouseY, r[2][0], r[2][1], r[2][2], r[2][3])) {
            com.examplebett.client.sound.ChorusMusicPlayer.toggleLoop();
            return true;
        }
        if (inside(mouseX, mouseY, r[3][0], r[3][1], r[3][2], r[3][3])) {
            com.examplebett.client.sound.ChorusMusicPlayer.stop();
            return true;
        }
        return false;
    }

    private static final String[] TIME_KEYS = {"time_morning", "time_noon", "time_evening", "time_night", "time_midnight"};
    private static final float CLOCK_TIME_SCALE = 3.0f;

    private int clockButtonTop(Layout l, int index) {

        int changeTop = l.sy1() - WEATHER_BUTTON_HEIGHT - 5;
        return index == TIME_KEYS.length ? changeTop
                : changeTop - (TIME_KEYS.length - index) * (WEATHER_BUTTON_HEIGHT + WEATHER_BUTTON_GAP);
    }

    private void drawClock(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        long dayTime = this.minecraft.level == null ? 0L : this.levelDayTime();
        int hours = (int) ((Math.floorMod(dayTime, 24000L) / 1000L + 6L) % 24L);

        int skyTop;
        int skyBottom;
        if (hours >= 6 && hours < 17) {
            skyTop = 0xFF6FB8F5;
            skyBottom = 0xFF3D8BD9;
        } else if (hours >= 17 && hours < 19) {
            skyTop = 0xFFF7A35C;
            skyBottom = 0xFFB4466A;
        } else {
            skyTop = 0xFF1E2A4A;
            skyBottom = 0xFF0B1022;
        }
        int bands = Math.max(1, (l.sy1() - top) / (PX * 2));
        for (int i = 0; i < bands; i++) {
            int bandTop = top + i * PX * 2;
            g.fill(l.sx0(), bandTop, l.sx1(), i == bands - 1 ? l.sy1() : bandTop + PX * 2, lerpColor(skyTop, skyBottom, (float) i / bands));
        }
        int cx = (l.sx0() + l.sx1()) / 2;

        String placeLabel = ModText.translatable("gui.mineturismo.smart_dispatch.weather_location").getString();
        int labelWidth = Math.round(com.examplebett.compat.LegacyGui.width(this.font, placeLabel) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, placeLabel, cx - labelWidth / 2, top + 8, 0xDDFFFFFF);
        String place = this.currentPlaceName().getString();
        int room = l.sx1() - l.sx0() - 8;
        if (com.examplebett.compat.LegacyGui.width(this.font, place) <= room) {
            g.drawString(this.font, place, cx - com.examplebett.compat.LegacyGui.width(this.font, place) / 2, top + 17, 0xFFFFFFFF, true);
        } else {
            String fitted = this.fit(place, room);
            this.drawSmallText(g, fitted, cx - Math.round(com.examplebett.compat.LegacyGui.width(this.font, fitted) * SMALL_TEXT_SCALE) / 2, top + 18, 0xFFFFFFFF);
        }

        String time = this.currentTimeText();
        int timeWidth = Math.round(com.examplebett.compat.LegacyGui.width(this.font, time) * CLOCK_TIME_SCALE);
        int timeTop = top + 32;
        g.pose().pushPose();
        g.pose().scale(CLOCK_TIME_SCALE, CLOCK_TIME_SCALE, 1.0f);
        g.drawString(this.font, time, Math.round((cx - timeWidth / 2f) / CLOCK_TIME_SCALE), Math.round(timeTop / CLOCK_TIME_SCALE), 0xFFFFFFFF, true);
        g.pose().popPose();

        String day = ModText.translatable("gui.mineturismo.smart_dispatch.clock_day", dayTime / 24000L + 1).getString();
        int dayWidth = Math.round(com.examplebett.compat.LegacyGui.width(this.font, day) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, day, cx - dayWidth / 2, timeTop + Math.round(this.font.lineHeight * CLOCK_TIME_SCALE) + 2, 0xDDFFFFFF);

        if (this.clockPickerOpen) {
            for (int i = 0; i < TIME_KEYS.length; i++) {
                String label = ModText.translatable("gui.mineturismo.smart_dispatch." + TIME_KEYS[i]).getString();
                this.drawWeatherButton(g, l, this.clockButtonTop(l, i), label, 0xFFFFFFFF, 0xFFFFF1B8, 0xFF202024, mouseX, mouseY);
            }
        }
        String change = ModText.translatable("gui.mineturismo.smart_dispatch.time_change").getString();
        this.drawWeatherButton(g, l, this.clockButtonTop(l, TIME_KEYS.length), change, 0xFF2A2A30, 0xFF44444E, 0xFFFFFFFF, mouseX, mouseY);
    }

    private boolean handleClockClick(Layout l, double mouseX, double mouseY) {
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        int changeTop = this.clockButtonTop(l, TIME_KEYS.length);
        if (inside(mouseX, mouseY, x0, changeTop, x1, changeTop + WEATHER_BUTTON_HEIGHT)) {
            this.clockPickerOpen = !this.clockPickerOpen;
            return true;
        }
        if (this.clockPickerOpen) {
            for (int i = 0; i < TIME_KEYS.length; i++) {
                int top = this.clockButtonTop(l, i);
                if (inside(mouseX, mouseY, x0, top, x1, top + WEATHER_BUTTON_HEIGHT)) {
                    com.examplebett.network.SetAppTimePayload payload = new com.examplebett.network.SetAppTimePayload(i);
                    ModClientNetworking.sendToServer(com.examplebett.network.SetAppTimePayload.ID, payload, com.examplebett.network.SetAppTimePayload::write);
                    this.clockPickerOpen = false;
                    return true;
                }
            }
        }
        return false;
    }

    private static final int MASSIVE_BUTTON_HEIGHT = 32;

    private static final int MASSIVE_BUTTON_TEXT_Y = 12;
    private static final int MASSIVE_BLUE = 0xFF1E4FA8;
    private static final int MASSIVE_BLUE_HOVER = 0xFF2D63C8;
    private static final int MASSIVE_YELLOW = 0xFFF2B705;

    private int[][] massiveAutoRects(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int x0 = l.sx0() + 8;
        int x1 = l.sx1() - 8;
        int first = top + 30;
        int second = first + MASSIVE_BUTTON_HEIGHT + 10;
        return new int[][]{{x0, first, x1, first + MASSIVE_BUTTON_HEIGHT}, {x0, second, x1, second + MASSIVE_BUTTON_HEIGHT}};
    }

    private void drawMassiveAuto(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;
        g.fill(l.sx0(), top, l.sx1(), l.sy1(), 0xFFF4F1EA);
        g.fill(l.sx0(), top, l.sx1(), top + 2, MASSIVE_YELLOW);
        String lead = this.fit(ModText.translatable("gui.mineturismo.massive_auto.lead").getString(), l.sx1() - l.sx0() - 8);
        this.drawSmallText(g, lead, cx - Math.round(com.examplebett.compat.LegacyGui.width(this.font, lead) * SMALL_TEXT_SCALE) / 2, top + 12, 0xFF3A3A40);
        int[][] r = this.massiveAutoRects(l);

        boolean buyHover = this.pending == null && inside(mouseX, mouseY, r[0][0], r[0][1], r[0][2], r[0][3]);
        fillPixelRounded(g, r[0][0], r[0][1] + 2, r[0][2], r[0][3] + 2, PX, 0xFFB08300);
        fillPixelRounded(g, r[0][0], r[0][1], r[0][2], r[0][3], PX, buyHover ? 0xFFFFC72C : MASSIVE_YELLOW);
        String buy = this.fit(ModText.translatable("gui.mineturismo.massive_auto.buy").getString(), r[0][2] - r[0][0] - 6);
        g.drawString(this.font, buy, cx - com.examplebett.compat.LegacyGui.width(this.font, buy) / 2, r[0][1] + MASSIVE_BUTTON_TEXT_Y, 0xFF12306A, false);

        boolean hover = this.pending == null && inside(mouseX, mouseY, r[1][0], r[1][1], r[1][2], r[1][3]);
        fillPixelRounded(g, r[1][0], r[1][1] + 2, r[1][2], r[1][3] + 2, PX, 0xFF12306A);
        fillPixelRounded(g, r[1][0], r[1][1], r[1][2], r[1][3], PX, hover ? MASSIVE_BLUE_HOVER : MASSIVE_BLUE);
        String appraisal = this.fit(ModText.translatable("gui.mineturismo.massive_auto.appraisal").getString(), r[1][2] - r[1][0] - 6);
        g.drawString(this.font, appraisal, cx - com.examplebett.compat.LegacyGui.width(this.font, appraisal) / 2, r[1][1] + MASSIVE_BUTTON_TEXT_Y, 0xFFFFFFFF, false);
    }

    private boolean handleMassiveAutoClick(Layout l, double mouseX, double mouseY) {
        int[][] r = this.massiveAutoRects(l);
        if (inside(mouseX, mouseY, r[0][0], r[0][1], r[0][2], r[0][3])) {
            this.minecraft.setScreen(new MassiveAutoUsedCarScreen());
            return true;
        }
        if (inside(mouseX, mouseY, r[1][0], r[1][1], r[1][2], r[1][3])) {

            com.examplebett.network.OpenMassiveAutoAppraisalPayload payload = new com.examplebett.network.OpenMassiveAutoAppraisalPayload();
            ModClientNetworking.sendToServer(com.examplebett.network.OpenMassiveAutoAppraisalPayload.ID, payload, com.examplebett.network.OpenMassiveAutoAppraisalPayload::write);
            return true;
        }
        return false;
    }

    private static final int SLOT_REELS = SmartDispatchAppItem.SLOT_REELS;
    private static final int SLOT_CELL_HEIGHT = 30;
    private static final int SLOT_WINDOW_HEIGHT = SLOT_CELL_HEIGHT * 2 + 6;
    private static final int SLOT_REEL_GAP = 3;
    private static final int SLOT_SPIN_BUTTON_HEIGHT = 18;
    private static final int SLOT_AUTO_BUTTON_HEIGHT = 13;

    private static final double SLOT_MAX_SPEED = 14.0;
    private static final double SLOT_ACCEL_SEC = 0.25;

    private static final double SLOT_MIN_SPIN_SEC = 0.8;
    private static final double SLOT_DECEL_SEC = 0.9;

    private static final double SLOT_STOP_INTERVAL_SEC = 0.45;

    private static final double SLOT_BLUR_SPEED = 5.0;

    private static final long SLOT_TIMEOUT_MS = 4000L;

    private static final long SLOT_AUTO_DELAY_MS = 900L;
    private static final long SLOT_AUTO_DELAY_JACKPOT_MS = 2600L;
    private static final long SLOT_TICK_MIN_INTERVAL_MS = 35L;
    private static final int SLOT_PINK = 0xFFE6197E;
    private static final int SLOT_ORANGE = 0xFFF7931E;
    private static final int SLOT_GOLD = 0xFFFFD54F;
    private static final int[] SLOT_DECOY_EMERALDS = {2, 3, 5, 8, 10, 15, 20, 30, 50};

    private enum SlotPhase { IDLE, SPINNING, STOPPING, RESULT }

    private SlotPhase slotPhase = SlotPhase.IDLE;

    private final double[] reelRest = new double[SLOT_REELS];

    private final double[] reelBase = new double[SLOT_REELS];
    private final double[] reelDecelFrom = new double[SLOT_REELS];
    private final int[] reelTarget = new int[SLOT_REELS];
    private final long[] reelDecelStartMs = new long[SLOT_REELS];
    private final boolean[] reelStopSoundPlayed = new boolean[SLOT_REELS];

    private final List<java.util.Map<Integer, SmartDispatchAppItem.SlotPrize>> reelResults = new ArrayList<>();
    private final java.util.Map<String, ItemStack> slotStackCache = new java.util.HashMap<>();
    private long slotSpinStartMs;
    private long slotResultShownMs;

    private List<SmartDispatchAppItem.SlotPrize> slotResult = null;
    private boolean slotAuto = false;
    private Component slotMessage = null;
    private long slotLastTickMs;
    private long slotLastTickCell = Long.MIN_VALUE;

    private int slotFrozenEmeralds = -1;

    private int slotEmeraldsGained = 0;
    private static final long SLOT_GAIN_POPUP_MS = 1500L;

    {
        for (int i = 0; i < SLOT_REELS; i++) {
            this.reelResults.add(new java.util.HashMap<>());
        }
    }

    public static void onSlotResult(List<SmartDispatchAppItem.SlotPrize> prizes) {
        if (net.minecraft.client.Minecraft.getInstance().screen instanceof SmartDispatchAppScreen screen) {
            screen.receiveSlotResult(prizes);
        }
    }

    private void receiveSlotResult(List<SmartDispatchAppItem.SlotPrize> prizes) {
        if (this.slotPhase != SlotPhase.SPINNING) {
            return;
        }
        long now = System.currentTimeMillis();
        long firstDecel = Math.max(now, this.slotSpinStartMs + (long) (SLOT_MIN_SPIN_SEC * 1000));
        for (int i = 0; i < SLOT_REELS; i++) {
            long decelStart = firstDecel + (long) (i * SLOT_STOP_INTERVAL_SEC * 1000);
            double from = this.spinPosition(i, decelStart);

            int target = (int) Math.ceil(from + SLOT_MAX_SPEED * SLOT_DECEL_SEC / 3.0);
            this.reelDecelStartMs[i] = decelStart;
            this.reelDecelFrom[i] = from;
            this.reelTarget[i] = target;
            if (i < prizes.size()) {
                this.reelResults.get(i).put(target, prizes.get(i));
            }
        }
        this.slotResult = prizes;
        this.slotPhase = SlotPhase.STOPPING;
    }

    private int emeraldCount() {
        return this.minecraft == null || this.minecraft.player == null ? 0
                : ModPlayer.getInventory(this.minecraft.player).countItem(Items.EMERALD);
    }

    private void playUiSound(net.minecraft.sounds.SoundEvent sound, float pitch, float volume) {
        this.minecraft.getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(sound, pitch, volume));
    }

    private void startSlotSpin() {
        if (this.slotPhase == SlotPhase.SPINNING || this.slotPhase == SlotPhase.STOPPING) {
            return;
        }
        if (!this.isFree() && this.emeraldCount() < SmartDispatchAppItem.SLOT_COST) {
            this.slotMessage = ModText.translatable("gui.mineturismo.smart_dispatch.not_enough_emeralds", SmartDispatchAppItem.SLOT_COST);
            this.slotAuto = false;
            return;
        }
        com.examplebett.network.RequestSlotSpinPayload payload = new com.examplebett.network.RequestSlotSpinPayload();
        ModClientNetworking.sendToServer(com.examplebett.network.RequestSlotSpinPayload.ID, payload, com.examplebett.network.RequestSlotSpinPayload::write);
        this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_START, 1.0f, 0.8f);
        this.slotPhase = SlotPhase.SPINNING;
        this.slotSpinStartMs = System.currentTimeMillis();
        this.slotResult = null;
        this.slotMessage = null;

        this.slotFrozenEmeralds = this.emeraldCount() - (this.isFree() ? 0 : SmartDispatchAppItem.SLOT_COST);
        for (int i = 0; i < SLOT_REELS; i++) {
            this.reelBase[i] = this.reelRest[i];
            this.reelStopSoundPlayed[i] = false;
        }
    }

    private void skipSlotSpin() {
        if (this.slotPhase != SlotPhase.STOPPING) {
            return;
        }
        long now = System.currentTimeMillis();
        for (int i = 0; i < SLOT_REELS; i++) {
            this.reelDecelStartMs[i] = Math.min(this.reelDecelStartMs[i], now - (long) (SLOT_DECEL_SEC * 1000));
        }
    }

    private double spinPosition(int reel, long time) {
        double t = Math.max(0, time - this.slotSpinStartMs) / 1000.0;
        double distance = t < SLOT_ACCEL_SEC ? SLOT_MAX_SPEED * t * t / (2 * SLOT_ACCEL_SEC) : SLOT_MAX_SPEED * (t - SLOT_ACCEL_SEC / 2);
        return this.reelBase[reel] + distance;
    }

    private double reelPosition(int reel, long time) {
        if (this.slotPhase == SlotPhase.IDLE || this.slotPhase == SlotPhase.RESULT) {
            return this.reelRest[reel];
        }
        if (this.slotPhase == SlotPhase.SPINNING || time < this.reelDecelStartMs[reel]) {
            return this.spinPosition(reel, time);
        }
        double u = (time - this.reelDecelStartMs[reel]) / (SLOT_DECEL_SEC * 1000);
        if (u >= 1) {
            return this.reelTarget[reel];
        }
        double e = 1 - u;
        return this.reelDecelFrom[reel] + (this.reelTarget[reel] - this.reelDecelFrom[reel]) * (1 - e * e * e);
    }

    private boolean reelStopped(int reel, long time) {
        return this.slotPhase == SlotPhase.STOPPING && time >= this.reelDecelStartMs[reel] + (long) (SLOT_DECEL_SEC * 1000);
    }

    private void updateSlot(long now) {
        if (this.slotPhase == SlotPhase.SPINNING && now - this.slotSpinStartMs > SLOT_TIMEOUT_MS) {
            this.receiveSlotResult(List.of());
        }
        if (this.slotPhase == SlotPhase.SPINNING || this.slotPhase == SlotPhase.STOPPING) {

            boolean stopPlayed = false;
            int tickReel = -1;
            for (int i = 0; i < SLOT_REELS; i++) {
                if (this.reelStopped(i, now)) {
                    if (!this.reelStopSoundPlayed[i]) {
                        this.reelStopSoundPlayed[i] = true;
                        if (!stopPlayed) {
                            this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_STOP, 1.0f + i * 0.08f, 0.9f);
                            stopPlayed = true;
                        }
                    }
                } else {
                    tickReel = i;
                }
            }

            if (tickReel >= 0) {
                long cell = (long) Math.floor(this.reelPosition(tickReel, now));
                if (cell != this.slotLastTickCell && now - this.slotLastTickMs >= SLOT_TICK_MIN_INTERVAL_MS) {
                    this.slotLastTickCell = cell;
                    this.slotLastTickMs = now;
                    this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_TICK, 0.95f + (float) Math.random() * 0.1f, 0.45f);
                }
            } else if (this.slotPhase == SlotPhase.STOPPING) {
                this.finishSlotSpin(now);
            }
        }
        if (this.slotPhase == SlotPhase.RESULT && this.slotAuto && this.pending == null) {
            long delay = this.slotHasJackpot() ? SLOT_AUTO_DELAY_JACKPOT_MS : SLOT_AUTO_DELAY_MS;
            if (now - this.slotResultShownMs >= delay) {
                this.startSlotSpin();
            }
        }
    }

    private void finishSlotSpin(long now) {
        for (int i = 0; i < SLOT_REELS; i++) {
            this.reelRest[i] = this.reelTarget[i];
        }
        this.slotResultShownMs = now;
        this.slotFrozenEmeralds = -1;
        this.slotEmeraldsGained = 0;
        if (this.slotResult != null) {
            for (SmartDispatchAppItem.SlotPrize prize : this.slotResult) {
                if (prize.kind() == SmartDispatchAppItem.PRIZE_EMERALD) {
                    this.slotEmeraldsGained += prize.amount();
                }
            }
        }
        if (this.slotResult == null || this.slotResult.isEmpty()) {

            this.slotPhase = SlotPhase.IDLE;
            this.slotAuto = false;
            this.slotMessage = ModText.translatable("gui.mineturismo.smart_dispatch.slot_failed");
            return;
        }
        this.slotPhase = SlotPhase.RESULT;
        if (this.slotHasJackpot()) {
            this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_JACKPOT, 1.0f, 1.0f);
        } else if (this.slotResult.stream().anyMatch(SmartDispatchAppItem.SlotPrize::isWin)) {
            this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_WIN, 1.0f, 0.9f);
        } else {
            this.playUiSound(com.examplebett.sound.AiObjectModSounds.SLOT_LOSE, 1.0f, 0.7f);
        }
    }

    private boolean slotHasJackpot() {
        return this.slotResult != null && this.slotResult.stream().anyMatch(SmartDispatchAppItem.SlotPrize::isJackpot);
    }

    private SmartDispatchAppItem.SlotPrize slotCell(int reel, long cell) {
        SmartDispatchAppItem.SlotPrize result = cell == (int) cell ? this.reelResults.get(reel).get((int) cell) : null;
        if (result != null) {
            return result;
        }
        long h = cell * 0x9E3779B97F4A7C15L + (reel + 1) * 0xC2B2AE3D27D4EB4FL;
        h ^= h >>> 31;
        h *= 0xBF58476D1CE4E5B9L;
        h ^= h >>> 29;
        int r = (int) Math.floorMod(h, 100L);
        int r2 = (int) Math.floorMod(h >>> 17, 1000L);

        if (r < 18) {
            return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_WARDEN_ENERGY, 1, "");
        }
        if (r < 35) {
            return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_CHOCO_BAR, 1, "");
        }
        if (r < 55) {
            return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_EMERALD, 1, "");
        }
        if (r < 85) {
            return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_EMERALD, SLOT_DECOY_EMERALDS[r2 % SLOT_DECOY_EMERALDS.length], "");
        }
        String modelId = SmartDispatchAppItem.SLOT_VEHICLES[r2 % SmartDispatchAppItem.SLOT_VEHICLES.length];
        return new SmartDispatchAppItem.SlotPrize(SmartDispatchAppItem.PRIZE_VEHICLE, 1, modelId);
    }

    private ItemStack slotStack(SmartDispatchAppItem.SlotPrize prize) {
        return this.slotStackCache.computeIfAbsent(prize.kind() + ":" + prize.amount() + ":" + prize.modelId(), key -> prize.toStack());
    }

    private static int slotPrizeColor(SmartDispatchAppItem.SlotPrize prize) {
        if (prize.kind() == SmartDispatchAppItem.PRIZE_VEHICLE) {
            return 0xFFFFA000;
        }
        if (prize.kind() == SmartDispatchAppItem.PRIZE_EMERALD) {
            return prize.amount() >= 30 ? 0xFFE53935 : prize.amount() >= 10 ? 0xFFAB47BC : prize.amount() >= 2 ? 0xFF29B6F6 : 0xFF66BB6A;
        }
        if (prize.kind() == SmartDispatchAppItem.PRIZE_CHOCO_BAR) {
            return 0xFFA1887F;
        }
        return 0xFF9CCC65;
    }

    private String slotPrizeLabel(SmartDispatchAppItem.SlotPrize prize) {
        if (prize.kind() == SmartDispatchAppItem.PRIZE_VEHICLE) {
            ItemStack stack = this.slotStack(prize);
            return stack.isEmpty() ? prize.modelId() : stack.getHoverName().getString();
        }
        if (prize.kind() == SmartDispatchAppItem.PRIZE_EMERALD) {
            return "x" + prize.amount();
        }
        if (prize.kind() == SmartDispatchAppItem.PRIZE_CHOCO_BAR) {
            return ModText.translatable("gui.mineturismo.smart_dispatch.slot_choco_short").getString();
        }
        return ModText.translatable("gui.mineturismo.smart_dispatch.slot_energy_short").getString();
    }

    private void drawFittedCentered(GuiGraphics g, String text, int cx, int y, int maxWidth, int color) {
        float scale = Math.min(SMALL_TEXT_SCALE, (float) maxWidth / Math.max(1, com.examplebett.compat.LegacyGui.width(this.font, text)));
        int w = Math.round(com.examplebett.compat.LegacyGui.width(this.font, text) * scale);
        this.drawScaledText(g, ((text)),
                cx - w / 2, y, color, scale);
    }

    private int[][] slotRects(Layout l) {
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT + COST_BAR_HEIGHT + 8;
        int autoTop = l.sy1() - 5 - SLOT_AUTO_BUTTON_HEIGHT;
        int spinTop = autoTop - 4 - SLOT_SPIN_BUTTON_HEIGHT;
        return new int[][]{
                {l.sx0() + 6, top, l.sx1() - 6, top + SLOT_WINDOW_HEIGHT},
                {l.sx0() + 8, spinTop, l.sx1() - 8, spinTop + SLOT_SPIN_BUTTON_HEIGHT},
                {l.sx0() + 8, autoTop, l.sx1() - 8, autoTop + SLOT_AUTO_BUTTON_HEIGHT}
        };
    }

    private void drawSlot(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        long now = System.currentTimeMillis();
        this.updateSlot(now);
        int top = l.sy0() + STATUS_BAR_HEIGHT + HEADER_HEIGHT;
        int cx = (l.sx0() + l.sx1()) / 2;

        boolean flash = this.slotPhase == SlotPhase.RESULT && this.slotResult.stream().anyMatch(SmartDispatchAppItem.SlotPrize::isWin)
                && now - this.slotResultShownMs < 1500 && (now / 150) % 2 == 0;
        int bands = Math.max(1, (l.sy1() - top) / (PX * 2));
        for (int i = 0; i < bands; i++) {
            int bandTop = top + i * PX * 2;
            int color = lerpColor(SLOT_PINK, SLOT_ORANGE, (float) i / bands);
            g.fill(l.sx0(), bandTop, l.sx1(), i == bands - 1 ? l.sy1() : bandTop + PX * 2, flash ? lerpColor(color, 0xFFFFFFFF, 0.25f) : color);
        }

        g.fill(l.sx0(), top, l.sx1(), top + COST_BAR_HEIGHT, COLOR_COST_BAR);
        Component cost = this.isFree() ? ModText.translatable("gui.mineturismo.smart_dispatch.free")
                : ModText.translatable("gui.mineturismo.smart_dispatch.slot_cost", SmartDispatchAppItem.SLOT_COST);
        this.drawSmallText(g, cost.getString(), l.sx0() + 3, top + 2, 0xFFE8E8E8);
        int shownEmeralds = this.slotFrozenEmeralds >= 0 ? this.slotFrozenEmeralds : this.emeraldCount();
        String owned = ModText.translatable("gui.mineturismo.smart_dispatch.slot_owned", shownEmeralds).getString();
        int ownedX = l.sx1() - 3 - Math.round(com.examplebett.compat.LegacyGui.width(this.font, owned) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, owned, ownedX, top + 2, 0xFF7CF29A);

        if (this.slotPhase == SlotPhase.RESULT && this.slotEmeraldsGained > 0 && now - this.slotResultShownMs < SLOT_GAIN_POPUP_MS) {
            String gained = "+" + this.slotEmeraldsGained;
            this.drawSmallText(g, gained, ownedX - 3 - Math.round(com.examplebett.compat.LegacyGui.width(this.font, gained) * SMALL_TEXT_SCALE), top + 2, SLOT_GOLD);
        }

        int[][] r = this.slotRects(l);
        int[] window = r[0];
        fillPixelRounded(g, window[0] - 3, window[1] - 3, window[2] + 3, window[3] + 3, 1, 0xFF2A1030);
        int reelWidth = (window[2] - window[0] - SLOT_REEL_GAP * (SLOT_REELS - 1)) / SLOT_REELS;
        for (int i = 0; i < SLOT_REELS; i++) {
            int rx0 = window[0] + i * (reelWidth + SLOT_REEL_GAP);
            this.drawReel(g, i, rx0, window[1], rx0 + reelWidth, window[3], now);
        }

        int midY = (window[1] + window[3]) / 2;
        for (int i = 0; i < 3; i++) {
            g.fill(window[0] - 3 + i, midY - 3 + i, window[0] - 2 + i, midY + 4 - i, 0xFFFFFFFF);
            g.fill(window[2] + 2 - i, midY - 3 + i, window[2] + 3 - i, midY + 4 - i, 0xFFFFFFFF);
        }

        int textTop = window[3] + 8;
        int room = l.sx1() - l.sx0() - 8;
        if (this.slotPhase == SlotPhase.RESULT) {
            boolean jackpot = this.slotHasJackpot();
            boolean win = jackpot || this.slotResult.stream().anyMatch(SmartDispatchAppItem.SlotPrize::isWin);
            String headline = ModText.translatable(jackpot ?"gui.mineturismo.smart_dispatch.slot_jackpot"
                    : win ? "gui.mineturismo.smart_dispatch.slot_win" : "gui.mineturismo.smart_dispatch.slot_lose").getString();
            g.drawString(this.font, headline, cx - com.examplebett.compat.LegacyGui.width(this.font, headline) / 2, textTop, win ? SLOT_GOLD : 0xFFFFFFFF, true);
            int emeralds = 0;
            for (SmartDispatchAppItem.SlotPrize prize : this.slotResult) {
                if (prize.kind() == SmartDispatchAppItem.PRIZE_EMERALD) {
                    emeralds += prize.amount();
                }
            }
            if (emeralds > 0) {
                this.drawFittedCentered(g, ModText.translatable("gui.mineturismo.smart_dispatch.slot_emeralds_won", emeralds).getString(),
                        cx, textTop + 11, room, 0xFFFFFFFF);
            }
        } else if (this.slotMessage != null && this.slotPhase == SlotPhase.IDLE) {
            List<String> lines = com.examplebett.compat.LegacyGui.split(this.font, this.slotMessage, Math.round(room / SMALL_TEXT_SCALE));
            for (int i = 0; i < Math.min(2, lines.size()); i++) {
                int w = Math.round(com.examplebett.compat.LegacyGui.width(this.font, lines.get(i)) * SMALL_TEXT_SCALE);
                this.drawSmallText(g, lines.get(i), cx - w / 2, textTop + i * 8, 0xFFFFE0E0);
            }
        } else if (this.slotPhase == SlotPhase.IDLE) {
            this.drawFittedCentered(g, ModText.translatable("gui.mineturismo.smart_dispatch.slot_hint").getString(), cx, textTop + 2, room, 0xFFFFFFFF);
        }

        int[] spin = r[1];
        boolean waiting = this.slotPhase == SlotPhase.SPINNING;
        boolean skipping = this.slotPhase == SlotPhase.STOPPING;
        boolean affordable = this.isFree() || this.emeraldCount() >= SmartDispatchAppItem.SLOT_COST;
        boolean spinEnabled = skipping || (!waiting && affordable);
        boolean spinHover = spinEnabled && inside(mouseX, mouseY, spin[0], spin[1], spin[2], spin[3]);
        fillPixelRounded(g, spin[0], spin[1] + 2, spin[2], spin[3] + 2, PX, spinEnabled ? 0xFFB07800 : 0xFF6E6E74);
        fillPixelRounded(g, spin[0], spin[1], spin[2], spin[3], PX, !spinEnabled ? 0xFF9A9AA2 : spinHover ? 0xFFFFE066 : 0xFFFFC928);
        String spinLabel = ModText.translatable(skipping ? "gui.mineturismo.smart_dispatch.slot_skip" : "gui.mineturismo.smart_dispatch.slot_spin").getString();
        g.drawString(this.font, spinLabel, cx - com.examplebett.compat.LegacyGui.width(this.font, spinLabel) / 2, spin[1] + 5, spinEnabled ? 0xFF3A2000 : 0xFFDADADF, false);

        int[] auto = r[2];
        boolean autoHover = inside(mouseX, mouseY, auto[0], auto[1], auto[2], auto[3]);
        fillPixelRounded(g, auto[0], auto[1], auto[2], auto[3], 1,
                this.slotAuto ? (autoHover ? 0xFF4FC562 : COLOR_BUTTON_YES) : (autoHover ? 0xFF54444A : 0xFF3A2A2E));
        String autoLabel = ModText.translatable("gui.mineturismo.smart_dispatch.slot_auto",
                ModText.translatable(this.slotAuto ? "options.on" : "options.off")).getString();
        int autoWidth = Math.round(com.examplebett.compat.LegacyGui.width(this.font, autoLabel) * SMALL_TEXT_SCALE);
        this.drawSmallText(g, autoLabel, cx - autoWidth / 2, auto[1] + 4, 0xFFFFFFFF);
    }

    private void drawReel(GuiGraphics g, int reel, int x0, int y0, int x1, int y1, long now) {
        g.fill(x0, y0, x1, y1, 0xFF1A0A1E);
        double pos = this.reelPosition(reel, now);
        double speed = Math.abs(pos - this.reelPosition(reel, now - 16)) / 0.016;
        boolean blur = speed > SLOT_BLUR_SPEED;

        g.enableScissor(x0, y0, x1, y1);
        boolean scissor = true;
        int centerY = (y0 + y1) / 2;
        long base = (long) Math.floor(pos);
        for (long cell = base - 2; cell <= base + 2; cell++) {
            int cellTop = (int) Math.round(centerY - SLOT_CELL_HEIGHT / 2.0 + (pos - cell) * SLOT_CELL_HEIGHT);
            this.drawSlotCell(g, this.slotCell(reel, cell), x0 + 1, cellTop + 1, x1 - 1, cellTop + SLOT_CELL_HEIGHT - 1, y0, y1, blur, scissor);
        }

        if (this.slotPhase == SlotPhase.RESULT && reel < this.slotResult.size() && this.slotResult.get(reel).isWin() && (now / 250) % 2 == 0) {
            int cellTop = centerY - SLOT_CELL_HEIGHT / 2;
            int cellBottom = cellTop + SLOT_CELL_HEIGHT;
            g.fill(x0, cellTop, x1, cellTop + 2, SLOT_GOLD);
            g.fill(x0, cellBottom - 2, x1, cellBottom, SLOT_GOLD);
            g.fill(x0, cellTop, x0 + 2, cellBottom, SLOT_GOLD);
            g.fill(x1 - 2, cellTop, x1, cellBottom, SLOT_GOLD);
        }

        g.fill(x0, y0, x1, y0 + 4, 0x66000000);
        g.fill(x0, y1 - 4, x1, y1, 0x66000000);
        g.disableScissor();
    }

    private static void fillClipped(GuiGraphics g, int x0, int y0, int x1, int y1, int clipTop, int clipBottom, int color) {
        int top = Math.max(y0, clipTop);
        int bottom = Math.min(y1, clipBottom);
        if (bottom > top) {
            g.fill(x0, top, x1, bottom, color);
        }
    }

    private void drawSlotCell(GuiGraphics g, SmartDispatchAppItem.SlotPrize prize, int x0, int y0, int x1, int y1,
                              int clipTop, int clipBottom, boolean blur, boolean scissor) {
        if (y1 <= clipTop || y0 >= clipBottom) {
            return;
        }
        int color = slotPrizeColor(prize);
        if (blur) {

            fillClipped(g, x0, y0, x1, y1, clipTop, clipBottom, lerpColor(color, 0xFFFFFFFF, 0.35f));
            int mid = (x0 + x1) / 2;
            fillClipped(g, mid - 6, y0 + 4, mid + 6, y1 - 4, clipTop, clipBottom, lerpColor(color, 0xFFFFFFFF, 0.6f));
            return;
        }
        fillClipped(g, x0, y0, x1, y1, clipTop, clipBottom, color);
        int stripTop = y1 - 9;
        fillClipped(g, x0 + 1, stripTop, x1 - 1, y1 - 1, clipTop, clipBottom, 0xF2FFFFFF);

        boolean visible = scissor || (y0 >= clipTop && y1 <= clipBottom);
        if (!visible) {
            return;
        }
        int cx = (x0 + x1) / 2;
        g.renderItem(this.slotStack(prize), cx - 8, y0 + 2);
        this.drawFittedCentered(g, this.slotPrizeLabel(prize), cx, stripTop + 2, x1 - x0 - 4, 0xFF202024);
    }

    private boolean handleSlotClick(Layout l, double mouseX, double mouseY) {
        int[][] r = this.slotRects(l);
        if (inside(mouseX, mouseY, r[1][0], r[1][1], r[1][2], r[1][3])) {
            this.pressSlotSpin();
            return true;
        }
        if (inside(mouseX, mouseY, r[2][0], r[2][1], r[2][2], r[2][3])) {
            this.slotAuto = !this.slotAuto;
            if (this.slotAuto && (this.slotPhase == SlotPhase.IDLE || this.slotPhase == SlotPhase.RESULT)) {
                this.startSlotSpin();
            }
            return true;
        }

        if (inside(mouseX, mouseY, r[0][0], r[0][1], r[0][2], r[0][3])) {
            this.pressSlotSpin();
            return true;
        }
        return false;
    }

    private void pressSlotSpin() {
        if (this.slotPhase == SlotPhase.STOPPING) {
            this.skipSlotSpin();
        } else {
            this.startSlotSpin();
        }
    }

    private String fit(String text, int width) {
        int room = Math.round(width / SMALL_TEXT_SCALE);
        if (com.examplebett.compat.LegacyGui.width(this.font, text) <= room) {
            return text;
        }
        return this.font.substrByWidth(text, room - com.examplebett.compat.LegacyGui.width(this.font, "...")) + "...";
    }

    private void drawDialog(GuiGraphics g, Layout l, App app, int mouseX, int mouseY) {
        this.aboveItems(g);
        g.fill(l.sx0(), l.sy0(), l.sx1(), l.sy1(), COLOR_DIALOG_SHADE);
        int dx0 = l.sx0() + 6;
        int dx1 = l.sx1() - 6;
        int dy0 = l.dialogTop();
        fillPixelRounded(g, dx0, dy0, dx1, dy0 + DIALOG_HEIGHT, PX, COLOR_DIALOG);

        g.renderItem(this.pending.stack(), dx0 + 6, dy0 + 6);
        this.drawSmallText(g, this.fit(this.pending.name().getString(), dx1 - dx0 - 30), dx0 + 26, dy0 + 11, 0xFF202024);

        boolean enough = this.hasEnoughEmeralds();
        Component question = enough
                ? ModText.translatable(app.vehicles ? "gui.mineturismo.smart_dispatch.confirm_ride" : "gui.mineturismo.smart_dispatch.confirm_buy",
                        costOf(app))
                : ModText.translatable("gui.mineturismo.smart_dispatch.not_enough_emeralds", costOf(app));

        List<String> lines = com.examplebett.compat.LegacyGui.split(this.font, question, Math.round((dx1 - dx0 - 10) / SMALL_TEXT_SCALE));
        for (int i = 0; i < Math.min(2, lines.size()); i++) {
            this.drawSmallText(g, lines.get(i), dx0 + 5, dy0 + 28 + i * 8, enough ? 0xFF202024 : 0xFFC62828);
        }
        int by0 = l.buttonTop();
        int bw = l.buttonWidth();
        boolean yesHover = enough && inside(mouseX, mouseY, l.yesX0(), by0, l.yesX0() + bw, by0 + DIALOG_BUTTON_HEIGHT);
        boolean noHover = inside(mouseX, mouseY, l.noX0(), by0, l.noX0() + bw, by0 + DIALOG_BUTTON_HEIGHT);
        fillPixelRounded(g, l.yesX0(), by0, l.yesX0() + bw, by0 + DIALOG_BUTTON_HEIGHT, 1,
                !enough ? COLOR_BUTTON_NO : yesHover ? COLOR_BUTTON_YES_HOVER : COLOR_BUTTON_YES);
        fillPixelRounded(g, l.noX0(), by0, l.noX0() + bw, by0 + DIALOG_BUTTON_HEIGHT, 1, noHover ? COLOR_BUTTON_NO_HOVER : COLOR_BUTTON_NO);
        String yes = ModText.translatable("gui.yes").getString();
        String no = ModText.translatable("gui.no").getString();
        g.drawString(this.font, yes, l.yesX0() + (bw - com.examplebett.compat.LegacyGui.width(this.font, yes)) / 2, by0 + 3, 0xFFFFFFFF, false);
        g.drawString(this.font, no, l.noX0() + (bw - com.examplebett.compat.LegacyGui.width(this.font, no)) / 2, by0 + 3, 0xFF202024, false);
    }

    private void aboveItems(GuiGraphics g) {
    }

    private void drawSmallText(GuiGraphics g, String text, int x, int y, int color) {
        this.drawScaledText(g, text, x, y, color, SMALL_TEXT_SCALE);
    }

    private void drawScaledText(GuiGraphics g, String text, int x, int y, int color, float s) {
        g.pose().pushPose();
        g.pose().scale(s, s, 1.0f);
        g.drawString(this.font, text, Math.round(x / s), Math.round(y / s), color, false);
        g.pose().popPose();
    }

    private void confirm(App app, Entry entry) {
        if (app.vehicles) {
            RequestAppCarSpawnPayload payload = new RequestAppCarSpawnPayload(entry.id());
            ModClientNetworking.sendToServer(RequestAppCarSpawnPayload.ID, payload, RequestAppCarSpawnPayload::write);
            this.onClose();
        } else {
            RequestAppItemPurchasePayload payload = new RequestAppItemPurchasePayload(entry.id());
            ModClientNetworking.sendToServer(RequestAppItemPurchasePayload.ID, payload, RequestAppItemPurchasePayload::write);
            this.purchasedBannerUntil = System.currentTimeMillis() + PURCHASED_BANNER_MS;
        }
    }

    private void launchGtt() {
        com.examplebett.network.SetGttPayload payload = new com.examplebett.network.SetGttPayload(true);
        ModClientNetworking.sendToServer(com.examplebett.network.SetGttPayload.ID, payload, com.examplebett.network.SetGttPayload::write);
        com.examplebett.client.GttClient.requestStart();
        this.onClose();
    }

    private boolean handleClick(double mouseX, double mouseY, int button) {
        if (this.shutdownUntil != 0L) {
            return true;
        }
        if (button != 0) {
            return false;
        }
        Layout l = this.layout();
        if (this.pending != null) {
            int by0 = l.buttonTop();
            int bw = l.buttonWidth();
            if (inside(mouseX, mouseY, l.yesX0(), by0, l.yesX0() + bw, by0 + DIALOG_BUTTON_HEIGHT)) {
                if (this.hasEnoughEmeralds()) {
                    Entry entry = this.pending;
                    this.pending = null;
                    this.confirm(this.openApp, entry);
                }
            } else {

                this.pending = null;
            }
            return true;
        }
        if (inside(mouseX, mouseY, l.homeX0() - 2, l.homeY0() - 2, l.homeX1() + 2, l.homeY1() + 2)) {
            if (this.openApp != null) {
                this.openApp = null;
                this.musicBrowsing = false;
            } else {

                this.shutdownUntil = System.currentTimeMillis() + SHUTDOWN_MS;
            }
            return true;
        }
        if (this.openApp == null) {
            App[] apps = App.values();
            for (int i = 0; i < apps.length; i++) {
                int[] r = appIconRect(l, i);
                if (inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
                    if (apps[i] == App.GTT) {
                        this.launchGtt();
                        return true;
                    }
                    this.openApp = apps[i];
                    this.scrollRow = 0;
                    this.weatherPickerOpen = false;
                    this.clockPickerOpen = false;
                    this.musicBrowsing = false;
                    this.settingsPage = SETTINGS_PAGE_MAIN;
                    this.purchasedBannerUntil = 0L;
                    return true;
                }
            }
            return false;
        }
        int headerTop = l.sy0() + STATUS_BAR_HEIGHT;
        if (inside(mouseX, mouseY, l.sx0(), headerTop, l.sx0() + 16, headerTop + HEADER_HEIGHT)) {

            if (this.openApp == App.MUSIC && this.musicBrowsing) {
                this.musicBrowsing = false;
            } else if (this.openApp == App.SETTINGS && this.settingsPage != SETTINGS_PAGE_MAIN) {
                this.settingsPage = SETTINGS_PAGE_MAIN;
                this.rebindingKey = null;
            } else {
                this.openApp = null;
            }
            return true;
        }
        if (this.openApp == App.MUSIC && !this.musicBrowsing) {
            return this.handleMusicHomeClick(l, mouseX, mouseY);
        }
        if (this.openApp.weather) {
            return this.handleWeatherClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.EMERGENCY) {
            return this.handleEmergencyClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.DETONATOR) {
            return this.handleDetonatorClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.CLOCK) {
            return this.handleClockClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.MASSIVE_AUTO) {
            return this.handleMassiveAutoClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.SLOT) {
            return this.handleSlotClick(l, mouseX, mouseY);
        }
        if (this.openApp == App.SETTINGS) {
            return this.handleSettingsClick(l, mouseX, mouseY);
        }
        int listTop = l.listTop();

        if (this.maxScrollRow(l) > 0 && inside(mouseX, mouseY, l.sx1() - 6, listTop, l.sx1(), l.sy1())) {
            this.draggingScrollbar = true;
            this.scrollToMouse(l, mouseY);
            return true;
        }
        if (inside(mouseX, mouseY, l.sx0(), listTop, l.sx1() - 6, listTop + l.visibleRows() * ROW_HEIGHT)) {
            int index = this.scrollRow + (int) ((mouseY - listTop) / ROW_HEIGHT);
            List<Entry> entries = this.entries();
            if (index < entries.size() && this.openApp == App.MUSIC) {

                com.examplebett.client.sound.ChorusMusicTracks.Track track = findTrack(entries.get(index).id());
                if (track != null) {
                    com.examplebett.client.sound.ChorusMusicPlayer.play(track);
                }
                return true;
            }
            if (index < entries.size()) {
                if (this.isFree()) {
                    this.confirm(this.openApp, entries.get(index));
                } else {
                    this.pending = entries.get(index);
                }
                return true;
            }
        }
        return false;
    }

    private void releaseVolume() {
        if (this.draggingVolume) {
            this.draggingVolume = false;
            this.minecraft.options.save();
        }
    }

    private int thumbHeight(Layout l) {
        return Math.max(8, (l.sy1() - l.listTop()) * l.visibleRows() / Math.max(1, this.entries().size()));
    }

    private void scrollToMouse(Layout l, double mouseY) {
        int max = this.maxScrollRow(l);
        int room = (l.sy1() - l.listTop()) - this.thumbHeight(l);
        if (max <= 0 || room <= 0) {
            return;
        }
        double t = (mouseY - l.listTop() - this.thumbHeight(l) / 2.0) / room;
        this.scrollRow = (int) Math.round(Math.max(0.0, Math.min(1.0, t)) * max);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.draggingVolume && button == 0) {
            this.setVolumeFromMouse(this.layout(), mouseX);
            return true;
        }
        if (this.draggingScrollbar && button == 0) {
            this.scrollToMouse(this.layout(), mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.draggingScrollbar = false;
            this.releaseVolume();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.handleClick(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleSlotKey(int key) {
        if (this.openApp != App.SLOT || this.pending != null
                || (key != org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE && key != org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER && key != org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER)) {
            return false;
        }
        this.pressSlotSpin();
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.handleRebindKey(keyCode, () -> com.mojang.blaze3d.platform.InputConstants.getKey(keyCode, scanCode))
                || this.handleSlotKey(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(mouseX, mouseY, 0, scrollY);
    }

    private boolean mineturismo$mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.openApp == null || this.pending != null || scrollY == 0) {
            return false;
        }
        Layout l = this.layout();
        this.scrollRow = Math.max(0, Math.min(this.maxScrollRow(l), this.scrollRow + (scrollY > 0 ? -1 : 1)));
        return true;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(null), mouseX, mouseY, partialTick);
    }

    private void mineturismo$renderScreen(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        super.render(mouseX, mouseY, partialTick);
        Layout l = this.layout();
        this.drawPhone(guiGraphics, l);
        if (this.shutdownUntil != 0L) {
            this.drawShutdown(guiGraphics, l);
            return;
        }
        if (this.openApp != null) {
            this.drawApp(guiGraphics, l, this.openApp, mouseX, mouseY);
        } else {
            this.drawHome(guiGraphics, l, mouseX, mouseY);
        }
        this.drawStatusBar(guiGraphics, l);
        this.skin.drawScreenOverlay(guiGraphics, l.sx0(), l.sy0(), l.sx1());
        if (this.pending != null && this.openApp != null) {
            this.drawDialog(guiGraphics, l, this.openApp, mouseX, mouseY);
        }
    }

    private void drawShutdown(GuiGraphics g, Layout l) {
        g.fill(l.sx0(), l.sy0(), l.sx1(), l.sy1(), 0xFF000000);

        float dot = PX * 2 * 2f / 3f;
        int logoW = Math.round(CHORUS_LOGO[0].length() * dot);
        int logoH = Math.round(CHORUS_LOGO.length * dot);
        int cx = (l.sx0() + l.sx1()) / 2;
        int logoX = (cx - logoW / 2) / PX * PX;
        int logoY = ((l.sy0() + l.sy1()) / 2 - logoH - 6) / PX * PX;
        for (int row = 0; row < CHORUS_LOGO.length; row++) {
            String line = CHORUS_LOGO[row];
            int py0 = logoY + Math.round(row * dot);
            int py1 = logoY + Math.round((row + 1) * dot);
            for (int col = 0; col < line.length(); col++) {
                if (line.charAt(col) == '#') {
                    int px0 = logoX + Math.round(col * dot);
                    int px1 = logoX + Math.round((col + 1) * dot);
                    g.fill(px0, py0, px1, py1, 0xFFFFFFFF);
                }
            }
        }
        String brand = "Chorus";
        int brandY = logoY + logoH + 6;
        g.drawString(this.font, brand, cx - com.examplebett.compat.LegacyGui.width(this.font, brand) / 2, brandY, 0xFFFFFFFF, false);
        String status = ModText.translatable("gui.mineturismo.smart_dispatch.shutting_down").getString();
        this.drawSmallText(g, status, cx - Math.round(com.examplebett.compat.LegacyGui.width(this.font, status) * SMALL_TEXT_SCALE) / 2,
                brandY + this.font.lineHeight + 6, 0xFFB4B4B4);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.shutdownUntil != 0L && System.currentTimeMillis() >= this.shutdownUntil) {
            this.shutdownUntil = 0L;
            this.onClose();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
*/
//?}
