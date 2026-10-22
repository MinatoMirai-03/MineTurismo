package com.examplebett.client.gui;
import com.examplebett.compat.ModClientNetworking;
import com.examplebett.compat.ModPlayer;
import com.examplebett.compat.ModText;

import com.examplebett.AiObjectMod;
import com.examplebett.item.UsedCarListings;
import com.examplebett.network.BuyUsedCarPayload;
import com.examplebett.util.AiCarPool;
//? if >=26 {
/*import com.examplebett.compat.GuiGraphics;*/
//?}
//? if >=1.20 && <26 {
import net.minecraft.client.gui.GuiGraphics;
//?}
//? if <1.20 {
/*import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;*/
//?}
import net.minecraft.client.gui.screens.Screen;
//? if >=1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
//?}
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MassiveAutoUsedCarScreen extends Screen {

    private static final int PX = 2;

    private static final int PHONE_MAX_HEIGHT = 240;
    private static final float PHONE_ASPECT = 0.56f;
    private static final int HEADER_HEIGHT = 18;
    private static final int GAP = 6;

    private static final int IMAGE_WIDTH = 192;
    private static final int IMAGE_HEIGHT = 108;
    private static final int SPEC_LINE = 11;
    private static final int BUY_BUTTON_HEIGHT = 22;
    private static final long BANNER_MS = 1500L;
    private static final float SMALL = 0.75f;
    private static final float NAME_SCALE = 1.25f;

    private static final int COLOR_SCREEN = 0xFFF4F1EA;
    private static final int COLOR_BLUE = 0xFF1E4FA8;
    private static final int COLOR_BLUE_HOVER = 0xFF2D63C8;
    private static final int COLOR_YELLOW = 0xFFF2B705;
    private static final int COLOR_PRICE = 0xFFD32F2F;
    private static final int COLOR_TEXT = 0xFF202024;
    private static final int COLOR_LABEL = 0xFF6E6A62;
    private static final int COLOR_LINE = 0xFFDCD7CB;
    private static final int COLOR_NO_IMAGE = 0xFFB8B8BE;
    private static final int COLOR_DISABLED = 0xFFB9B6AE;
    private static final int COLOR_BANNER = 0xEE202024;
    private static final ResourceLocation ICON = AiObjectMod.id("textures/gui/smart_dispatch/massive_auto.png");

    private record Card(String name, int price, UsedCarListings.Listing listing, int mileageKm, int repair, String noteKey) {
    }

    private static final String[] DUMMY_NOTES = {"customized", "one_owner", "non_smoker", "stock",
            "low_mileage", "dealer_serviced", "new_tires", "aftermarket_wheels", "lowered"};
    private static final int CARD_PAD = 4;
    private static final int CARD_LINE = 10;
    private static final int SMALL_LINE = 7;
    private static final int STOCK_BUTTON_HEIGHT = 14;
    private static final int COLOR_NAME = 0xFF1A56B8;
    private static final int COLOR_STOCK_BUTTON = 0xFFDE3A0E;
    private static final int COLOR_STOCK_BUTTON_HOVER = 0xFFF0532A;

    private record Layout(int x0, int y0, int x1, int y1, int sx0, int sy0, int sx1, int sy1,
                          int homeX0, int homeY0, int homeX1, int homeY1) {
        int contentTop() {
            return sy0 + HEADER_HEIGHT;
        }
    }

    private record Detail(List<FormattedCharSequence> nameLines, int nameY, int[] image, int priceY, float priceScale,
                          int specY, int[] button, int height) {
    }

    private static final int DUMMY_COUNT = 7;

    private static final int DUMMY_PRICE_MIN = 500;
    private static final int DUMMY_PRICE_MAX = 99999;

    private static final String[] DUMMY_NAMES = {
            "07_Ponta_Crossway", "21_Lancaster_Dispenser", "21_CYD_Sunfish", "99_Yatsuhashi_Prominence",
            "20_Mutsura_Resolve", "25_Kariya_BR_Abyss", "19_Mazyama_Aureola", "06_Kariya_Flos_Sedan",
            "24_Kariya_Kamui", "18_Aries_Grand_Canyon", "17_Ponta_Blaze", "12_Fermat_Pied",
            "10_Walt_Exposer", "09_Kariya_Aeris", "02_Detroit_Hunter_CV2", "14_Atlantis_Stivale_Sedan",
            "04_Nittaku_Funebre", "18_Charlevoix_Chumash", "13_Ponta_Seattle", "16_Ponta_Titanic",
            "92_Ponta_Epic_Coupe", "21_Fermat_Matinee_RS", "85_Isurugi_Castor", "22_Kariya_Flos_Sport"
    };

    private static final String DUMMY_NAMES_RESOURCE = "used_car/dummy_names.json";

    private static String dummyDisplayName(String raw) {
        String s = raw.replace('_', ' ').trim();
        return s.matches("\\d{2} .*") ? "'" + s : s;
    }

    private static List<String> dummyNames() {
        java.util.LinkedHashMap<String, String> byDisplay = new java.util.LinkedHashMap<>();
        for (String raw : DUMMY_NAMES) {
            byDisplay.put(dummyDisplayName(raw), raw);
        }
        java.util.Set<String> removed = new java.util.HashSet<>();
        try {
            for (com.examplebett.compat.ModResources.IoSupplier<java.io.InputStream> resource : com.examplebett.compat.ModResources
                    .getResourceStack(net.minecraft.client.Minecraft.getInstance().getResourceManager(), AiObjectMod.id(DUMMY_NAMES_RESOURCE))) {
                try (java.io.Reader reader = new java.io.InputStreamReader(resource.get(), java.nio.charset.StandardCharsets.UTF_8)) {
                    com.google.gson.JsonObject json = com.examplebett.compat.ModResources.parseJsonObject(reader);
                    if (json.has("add")) {
                        for (com.google.gson.JsonElement e : json.getAsJsonArray("add")) {
                            byDisplay.putIfAbsent(dummyDisplayName(e.getAsString()), e.getAsString());
                        }
                    }
                    if (json.has("remove")) {
                        for (com.google.gson.JsonElement e : json.getAsJsonArray("remove")) {
                            removed.add(dummyDisplayName(e.getAsString()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            AiObjectMod.LOGGER.warn("Failed to read {}", DUMMY_NAMES_RESOURCE, e);
        }
        byDisplay.keySet().removeAll(removed);
        return new ArrayList<>(byDisplay.keySet());
    }

    private static int randomDummyPrice(java.util.Random random) {
        double lo = Math.log(DUMMY_PRICE_MIN);
        double hi = Math.log(DUMMY_PRICE_MAX + 1);
        int price = (int) Math.exp(lo + random.nextDouble() * (hi - lo));
        return Math.max(DUMMY_PRICE_MIN, Math.min(DUMMY_PRICE_MAX, price));
    }

    private final List<Card> cards = new ArrayList<>();

    private UsedCarListings.Listing selected = null;
    private int listScroll = 0;
    private int detailScroll = 0;
    private boolean scrollbarDragging = false;
    private double scrollbarGrabOffset = 0;
    private static final int SCROLLBAR_HIT_WIDTH = 7;
    private long outOfStockUntil = 0L;
    private long purchasedUntil = 0L;

    private final PhoneSkin skin = PhoneSkin.ofHeldPhone();

    public MassiveAutoUsedCarScreen() {
        super(ModText.translatable("gui.mineturismo.massive_auto.buy"));
        for (UsedCarListings.Listing listing : UsedCarListings.LISTINGS) {
            this.cards.add(new Card(vehicleName(modelIdOf(listing)), listing.price(), listing,
                    listing.mileageKm(), listing.repair(), listing.noteKey()));
        }

        java.util.Random random = new java.util.Random();
        List<String> names = dummyNames();
        java.util.Collections.shuffle(names, random);
        for (int i = 0; i < DUMMY_COUNT && i < names.size(); i++) {
            this.cards.add(new Card(names.get(i), randomDummyPrice(random), null, (1 + random.nextInt(25)) * 10000,
                    random.nextInt(3) == 0 ? UsedCarListings.REPAIR_YES : UsedCarListings.REPAIR_NONE,
                    DUMMY_NOTES[random.nextInt(DUMMY_NOTES.length)]));
        }
    }

    private static String modelIdOf(UsedCarListings.Listing listing) {
        return listing.presetId().startsWith("used_") ? listing.presetId().substring("used_".length()) : listing.presetId();
    }

    private static String vehicleName(String modelId) {
        Item item = AiCarPool.spawnItemFor(modelId);
        if (item == null) {
            return null;
        }
        return ModText.translatable(item.getDescriptionId()).getString().replaceAll("\\s*[(（][^(（]*[)）]\\s*$", "");
    }

    private static String formatPrice(int price) {
        return ModText.translatable("gui.mineturismo.used_car.price", String.format(Locale.ROOT, "%,d", price)).getString();
    }

    private static boolean inside(double mx, double my, int x0, int y0, int x1, int y1) {
        return mx >= x0 && mx < x1 && my >= y0 && my < y1;
    }

    private boolean isFree() {
        return this.minecraft != null && this.minecraft.player != null && ModPlayer.hasInstabuild(this.minecraft.player);
    }

    private boolean canAfford(UsedCarListings.Listing listing) {
        return this.isFree() || (this.minecraft != null && this.minecraft.player != null
                && ModPlayer.getInventory(this.minecraft.player).countItem(Items.EMERALD) >= listing.price());
    }

    private Layout layout() {
        int phoneH = Math.min(PHONE_MAX_HEIGHT, this.height - 16) / PX * PX;
        int phoneW = Math.round(phoneH * PHONE_ASPECT) / PX * PX;
        int x0 = (this.width - phoneW) / 2 / PX * PX;
        int y0 = (this.height - phoneH) / 2 / PX * PX;
        int x1 = x0 + phoneW;
        int y1 = y0 + phoneH;

        int[] home = this.skin.homeRect(x0, x1, y1 - this.skin.insetBottom, y1);
        return new Layout(x0, y0, x1, y1, x0 + this.skin.insetX, y0 + this.skin.insetTop, x1 - this.skin.insetX, y1 - this.skin.insetBottom,
                home[0], home[1], home[2], home[3]);
    }

    private static int viewHeight(Layout l) {
        return l.sy1() - l.contentTop();
    }

    private int cardInnerWidth(Layout l) {
        return l.sx1() - l.sx0() - GAP * 2 - CARD_PAD * 2;
    }

    private List<FormattedCharSequence> cardNameLines(Layout l, Card card) {
        List<FormattedCharSequence> lines = this.font.split(ModText.literal(card.name()), this.cardInnerWidth(l));
        return lines.size() > 2 ? lines.subList(0, 2) : lines;
    }

    private int cardImageWidth(Layout l) {
        return this.cardInnerWidth(l) * 48 / 100;
    }

    private int cardRightWidth(Layout l) {
        return this.cardInnerWidth(l) - this.cardImageWidth(l) - 4;
    }

    private static String priceNumber(int price) {
        return String.format(Locale.ROOT, "%,d", price);
    }

    private float cardPriceScale(Layout l, Card card) {
        return Math.min(1.5f, (float) this.cardRightWidth(l) / Math.max(1, this.font.width(priceNumber(card.price()))));
    }

    private int cardMiddleHeight(Layout l, Card card) {
        int imageH = this.cardImageWidth(l) * 9 / 16;
        int rightH = Math.round(9 * this.cardPriceScale(l, card)) + 1 + SMALL_LINE + 2 + SMALL_LINE * 3;
        return Math.max(imageH, rightH);
    }

    private int cardHeight(Layout l, Card card) {
        return CARD_PAD + this.cardNameLines(l, card).size() * CARD_LINE + 2 + this.cardMiddleHeight(l, card) + 4
                + STOCK_BUTTON_HEIGHT + CARD_PAD;
    }

    private int[] cardRect(Layout l, int i) {
        int y = l.contentTop() + GAP - this.listScroll;
        for (int j = 0; j < i; j++) {
            y += this.cardHeight(l, this.cards.get(j)) + GAP;
        }
        return new int[]{l.sx0() + GAP, y, l.sx1() - GAP, y + this.cardHeight(l, this.cards.get(i))};
    }

    private int maxListScroll(Layout l) {
        int total = GAP;
        for (Card card : this.cards) {
            total += this.cardHeight(l, card) + GAP;
        }
        return Math.max(0, total - viewHeight(l));
    }

    private Detail detail(Layout l) {
        int left = l.sx0() + GAP;
        int right = l.sx1() - GAP;
        int width = right - left;
        int top = l.contentTop() + GAP - this.detailScroll;
        int y = top;
        List<FormattedCharSequence> nameLines = this.font.split(ModText.literal(vehicleName(modelIdOf(this.selected))),
                Math.round(width / NAME_SCALE));
        if (nameLines.size() > 2) {
            nameLines = nameLines.subList(0, 2);
        }
        int nameY = y;
        y += nameLines.size() * Math.round(10 * NAME_SCALE) + 3;
        int imageH = width * 9 / 16;
        int[] image = {left, y, right, y + imageH};
        y += imageH + 6;
        String price = formatPrice(this.selected.price());
        float priceScale = Math.min(2.0f, (float) width / Math.max(1, this.font.width(price)));
        int priceY = y;
        y += Math.round(9 * priceScale) + 6;
        int specY = y;
        y += SPEC_LINE * 4 + 6;
        int[] button = {left, y, right, y + BUY_BUTTON_HEIGHT};
        y += BUY_BUTTON_HEIGHT + (this.canAfford(this.selected) ? 0 : 10) + GAP;
        return new Detail(nameLines, nameY, image, priceY, priceScale, specY, button, y - top + GAP);
    }

    private int maxDetailScroll(Layout l) {
        return this.selected == null ? 0 : Math.max(0, this.detail(l).height() - viewHeight(l));
    }

    private void drawScaled(GuiGraphics g, FormattedCharSequence text, int x, int y, int color, float s) {
        //? if >=1.21.6 {
        g.pose().pushMatrix();
        g.pose().scale(s, s);
        //?} else {
        /*g.pose().pushPose();
        g.pose().scale(s, s, 1.0f);*/
        //?}
        g.drawString(this.font, text, Math.round(x / s), Math.round(y / s), color, false);
        //? if >=1.21.6 {
        g.pose().popMatrix();
        //?} else
        /*g.pose().popPose();*/
    }

    private void drawScaled(GuiGraphics g, String text, int x, int y, int color, float s) {
        this.drawScaled(g, net.minecraft.locale.Language.getInstance().getVisualOrder(net.minecraft.network.chat.FormattedText.of(text)), x, y, color, s);
    }

    private String fit(String text, int width, float s) {
        int room = Math.round(width / s);
        if (this.font.width(text) <= room) {
            return text;
        }
        return this.font.plainSubstrByWidth(text, room - this.font.width("...")) + "...";
    }

    private void drawPhone(GuiGraphics g, Layout l) {
        this.skin.drawBody(g, l.x0(), l.y0(), l.x1(), l.y1(), l.sx0(), l.sy0(), l.sx1(), l.sy1(),
                new int[]{l.homeX0(), l.homeY0(), l.homeX1(), l.homeY1()});
        g.fill(l.sx0(), l.sy0(), l.sx1(), l.sy1(), COLOR_SCREEN);
    }

    private void drawHeader(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        int top = l.sy0();
        g.fill(l.sx0(), top, l.sx1(), top + HEADER_HEIGHT, 0xFFFFFFFF);
        g.fill(l.sx0(), top + HEADER_HEIGHT - 2, l.sx1(), top + HEADER_HEIGHT, COLOR_YELLOW);

        int ax = l.sx0() + 5;
        int ay = top + (HEADER_HEIGHT - 2) / 2;
        boolean backHover = inside(mouseX, mouseY, l.sx0(), top, l.sx0() + 16, top + HEADER_HEIGHT);
        int arrow = backHover ? COLOR_BLUE_HOVER : COLOR_BLUE;
        for (int i = 0; i < 4; i++) {
            g.fill(ax + i, ay - i - 1, ax + i + 1, ay - i + 1, arrow);
            g.fill(ax + i, ay + i - 1, ax + i + 1, ay + i + 1, arrow);
        }
        String name = "Massive Auto";
        int iconSize = 12;
        int left = (l.sx0() + l.sx1() - (iconSize + 4 + this.font.width(name))) / 2;
        //? if >=1.21.6 && <26 {
        g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, ICON, left, top + 2, 0f, 0f, iconSize, iconSize, 64, 64, 64, 64);
        //?}
        g.drawString(this.font, name, left + iconSize + 4, top + 4, COLOR_BLUE, false);
    }

    private void drawImage(GuiGraphics g, UsedCarListings.Listing listing, int x0, int y0, int x1, int y1) {
        if (listing == null) {
            g.fill(x0, y0, x1, y1, COLOR_NO_IMAGE);
            String text = ModText.translatable("gui.mineturismo.used_car.no_image").getString();
            float s = this.font.width(text) + 4 <= x1 - x0 ? 1.0f : SMALL;
            text = this.fit(text, x1 - x0 - 2, s);
            int w = Math.round(this.font.width(text) * s);
            this.drawScaled(g, text, (x0 + x1 - w) / 2, (y0 + y1) / 2 - Math.round(4 * s), 0xFF6E6E76, s);
            return;
        }
        //? if >=1.21.6 && <26 {
        ResourceLocation image = AiObjectMod.id("textures/gui/used_cars/" + listing.image() + ".png");
        g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, image, x0, y0, 0f, 0f, x1 - x0, y1 - y0,
                IMAGE_WIDTH, IMAGE_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT);
        //?} else {
        /*
        g.fill(x0, y0, x1, y1, COLOR_NO_IMAGE);*/
        //?}
    }

    private void drawScrollbar(GuiGraphics g, Layout l, int scroll, int max) {
        if (max <= 0) {
            return;
        }
        int thumbH = thumbHeight(l, max);
        int thumbTop = l.contentTop() + (viewHeight(l) - thumbH) * scroll / max;
        g.fill(l.sx1() - 3, thumbTop, l.sx1() - 1, thumbTop + thumbH, this.scrollbarDragging ? 0xFF6E6E78 : 0xFF8C8C96);
    }

    private static int thumbHeight(Layout l, int max) {
        int trackH = viewHeight(l);
        return Math.max(12, trackH * trackH / (trackH + max));
    }

    private int currentScroll() {
        return this.selected != null ? this.detailScroll : this.listScroll;
    }

    private int currentMaxScroll(Layout l) {
        return this.selected != null ? this.maxDetailScroll(l) : this.maxListScroll(l);
    }

    private static boolean onScrollbar(Layout l, double mouseX, double mouseY) {
        return inside(mouseX, mouseY, l.sx1() - SCROLLBAR_HIT_WIDTH, l.contentTop(), l.sx1(), l.contentTop() + viewHeight(l));
    }

    private void dragScrollbarTo(Layout l, double mouseY) {
        int max = this.currentMaxScroll(l);
        if (max <= 0) {
            return;
        }
        int free = viewHeight(l) - thumbHeight(l, max);
        double t = free <= 0 ? 0 : (mouseY - this.scrollbarGrabOffset - l.contentTop()) / free;
        int scroll = (int) Math.round(Math.max(0, Math.min(1, t)) * max);
        if (this.selected != null) {
            this.detailScroll = scroll;
        } else {
            this.listScroll = scroll;
        }
    }

    private void drawList(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        boolean overContent = inside(mouseX, mouseY, l.sx0(), l.contentTop(), l.sx1(), l.sy1());
        for (int i = 0; i < this.cards.size(); i++) {
            Card card = this.cards.get(i);
            int[] r = this.cardRect(l, i);
            if (r[3] < l.contentTop() || r[1] > l.sy1()) {
                continue;
            }

            boolean hover = overContent && inside(mouseX, mouseY, r[0], r[1], r[2], r[3]);
            g.fill(r[0], r[1] + 1, r[2], r[3] + 1, 0x22000000);
            g.fill(r[0] - 1, r[1] - 1, r[2] + 1, r[3] + 1, hover ? COLOR_BLUE_HOVER : COLOR_LINE);
            g.fill(r[0], r[1], r[2], r[3], 0xFFFFFFFF);
            int x0 = r[0] + CARD_PAD;
            int x1 = r[2] - CARD_PAD;
            int y = r[1] + CARD_PAD;

            for (FormattedCharSequence line : this.cardNameLines(l, card)) {
                g.drawString(this.font, line, x0, y, COLOR_NAME, false);
                y += CARD_LINE;
            }
            y += 2;

            int imageW = this.cardImageWidth(l);
            int imageH = imageW * 9 / 16;
            this.drawImage(g, card.listing(), x0, y, x0 + imageW, y + imageH);

            int rx = x0 + imageW + 4;
            int rw = x1 - rx;
            float priceScale = this.cardPriceScale(l, card);
            this.drawScaled(g, priceNumber(card.price()), rx, y, COLOR_PRICE, priceScale);
            int ry = y + Math.round(9 * priceScale) + 1;
            this.drawScaled(g, this.fit(ModText.translatable("gui.mineturismo.used_car.emerald_unit").getString(), rw, SMALL), rx, ry, COLOR_PRICE, SMALL);
            ry += SMALL_LINE + 2;
            String[] specs = {
                    ModText.translatable("gui.mineturismo.used_car.mileage_value",
                            priceNumber(card.mileageKm()), card.mileageKm() / 10000).getString(),
                    ModText.translatable(card.repair() == UsedCarListings.REPAIR_YES
                            ? "gui.mineturismo.used_car.repair_short_yes" : "gui.mineturismo.used_car.repair_short_no").getString(),
                    ModText.translatable("gui.mineturismo.used_car.note." + card.noteKey()).getString()
            };
            for (String spec : specs) {
                this.drawScaled(g, this.fit(spec, rw, SMALL), rx, ry, COLOR_LABEL, SMALL);
                ry += SMALL_LINE;
            }
            y += this.cardMiddleHeight(l, card) + 4;

            SmartDispatchAppScreen.fillPixelRounded(g, x0, y, x1, y + STOCK_BUTTON_HEIGHT, 1,
                    hover ? COLOR_STOCK_BUTTON_HOVER : COLOR_STOCK_BUTTON);
            String stock = this.fit(ModText.translatable("gui.mineturismo.used_car.check_stock").getString(), x1 - x0 - 4, 1.0f);
            g.drawString(this.font, stock, (x0 + x1 - this.font.width(stock)) / 2, y + 3, 0xFFFFFFFF, false);
        }
    }

    private void drawDetail(GuiGraphics g, Layout l, int mouseX, int mouseY) {
        UsedCarListings.Listing listing = this.selected;
        Detail d = this.detail(l);
        int left = l.sx0() + GAP;
        int width = l.sx1() - GAP - left;

        for (int i = 0; i < d.nameLines().size(); i++) {
            this.drawScaled(g, d.nameLines().get(i), left, d.nameY() + i * Math.round(10 * NAME_SCALE), COLOR_TEXT, NAME_SCALE);
        }

        int[] img = d.image();
        g.fill(img[0] - 1, img[1] - 1, img[2] + 1, img[3] + 1, COLOR_LINE);
        this.drawImage(g, listing, img[0], img[1], img[2], img[3]);

        this.drawScaled(g, formatPrice(listing.price()), left, d.priceY(), COLOR_PRICE, d.priceScale());

        String[][] specs = {
                {"year", ModText.translatable("gui.mineturismo.used_car.year_value", listing.year()).getString()},
                {"mileage", ModText.translatable("gui.mineturismo.used_car.mileage_value",
                        String.format(Locale.ROOT, "%,d", listing.mileageKm()), listing.mileageKm() / 10000).getString()},
                {"repair", ModText.translatable(listing.repair() == UsedCarListings.REPAIR_YES
                        ? "gui.mineturismo.used_car.repair_yes" : "gui.mineturismo.used_car.repair_no").getString()},
                {"note", ModText.translatable("gui.mineturismo.used_car.note." + listing.noteKey()).getString()}
        };
        int specY = d.specY();
        g.fill(left, specY - 4, left + width, specY - 3, COLOR_LINE);
        for (String[] spec : specs) {
            String label = ModText.translatable("gui.mineturismo.used_car." + spec[0]).getString();
            int labelW = this.font.width(label);
            g.drawString(this.font, label, left, specY, COLOR_LABEL, false);
            g.drawString(this.font, this.fit(spec[1], width - labelW - 6, 1.0f), left + labelW + 6, specY, COLOR_TEXT, false);
            specY += SPEC_LINE;
        }

        int[] button = d.button();
        boolean afford = this.canAfford(listing);
        boolean hover = afford && inside(mouseX, mouseY, button[0], button[1], button[2], button[3])
                && inside(mouseX, mouseY, l.sx0(), l.contentTop(), l.sx1(), l.sy1());
        if (afford) {
            SmartDispatchAppScreen.fillPixelRounded(g, button[0], button[1] + 2, button[2], button[3] + 2, PX, 0xFF12306A);
        }
        SmartDispatchAppScreen.fillPixelRounded(g, button[0], button[1], button[2], button[3], PX,
                !afford ? COLOR_DISABLED : hover ? COLOR_BLUE_HOVER : COLOR_BLUE);
        String buy = ModText.translatable("gui.mineturismo.used_car.purchase").getString();
        float buyScale = 1.25f;
        int buyW = Math.round(this.font.width(buy) * buyScale);
        this.drawScaled(g, buy, (button[0] + button[2] - buyW) / 2, button[1] + 6, 0xFFFFFFFF, buyScale);
        if (!afford) {
            String note = this.fit(ModText.translatable("gui.mineturismo.smart_dispatch.not_enough_emeralds", listing.price()).getString(), width, SMALL);
            int noteW = Math.round(this.font.width(note) * SMALL);
            this.drawScaled(g, note, (button[0] + button[2] - noteW) / 2, button[3] + 3, COLOR_PRICE, SMALL);
        }
    }

    private void drawBanner(GuiGraphics g, Layout l, String text) {
        int w = this.font.width(text) + 20;
        int h = 20;
        int bx0 = (l.sx0() + l.sx1() - w) / 2;
        int by0 = (l.contentTop() + l.sy1() - h) / 2;
        SmartDispatchAppScreen.fillPixelRounded(g, bx0, by0, bx0 + w, by0 + h, PX, COLOR_BANNER);
        g.drawString(this.font, text, bx0 + 10, by0 + 6, 0xFFFFFFFF, false);
    }

    //? if >=26 {
    /*@Override
    public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(extractor), mouseX, mouseY, partialTick);
    }*/
    //?}
    //? if >=1.20 && <26 {
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(guiGraphics, mouseX, mouseY, partialTick);
    }
    //?}
    //? if <1.20 {
    /*@Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        mineturismo$renderScreen(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
    }*/
    //?}

    private void mineturismo$renderScreen(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        //? if >=26 {
        /*super.extractRenderState(g.extractor(), mouseX, mouseY, partialTick);*/
        //?}
        //? if >=1.20 && <26 {
        super.render(g, mouseX, mouseY, partialTick);
        //?}
        //? if <1.20 {
        /*super.render(g.pose(), mouseX, mouseY, partialTick);*/
        //?}
        Layout l = this.layout();
        this.drawPhone(g, l);
        this.listScroll = Math.max(0, Math.min(this.maxListScroll(l), this.listScroll));
        this.detailScroll = Math.max(0, Math.min(this.maxDetailScroll(l), this.detailScroll));
        g.enableScissor(l.sx0(), l.contentTop(), l.sx1(), l.sy1());
        if (this.selected != null) {
            this.drawDetail(g, l, mouseX, mouseY);
        } else {
            this.drawList(g, l, mouseX, mouseY);
        }
        g.disableScissor();
        if (this.selected != null) {
            this.drawScrollbar(g, l, this.detailScroll, this.maxDetailScroll(l));
        } else {
            this.drawScrollbar(g, l, this.listScroll, this.maxListScroll(l));
        }
        this.drawHeader(g, l, mouseX, mouseY);
        long now = System.currentTimeMillis();
        if (now < this.purchasedUntil) {
            this.drawBanner(g, l, ModText.translatable("gui.mineturismo.smart_dispatch.purchased").getString());
        } else if (now < this.outOfStockUntil) {
            this.drawBanner(g, l, ModText.translatable("gui.mineturismo.used_car.out_of_stock").getString());
        }
    }

    private void back() {
        if (this.selected != null) {
            this.selected = null;
        } else {
            this.minecraft.setScreen(SmartDispatchAppScreen.massiveAuto());
        }
    }

    private boolean handleClick(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        Layout l = this.layout();
        if (inside(mouseX, mouseY, l.homeX0() - 2, l.homeY0() - 2, l.homeX1() + 2, l.homeY1() + 2)) {
            this.minecraft.setScreen(new SmartDispatchAppScreen());
            return true;
        }
        if (inside(mouseX, mouseY, l.sx0(), l.sy0(), l.sx0() + 16, l.sy0() + HEADER_HEIGHT)) {
            this.back();
            return true;
        }
        if (!inside(mouseX, mouseY, l.sx0(), l.contentTop(), l.sx1(), l.sy1())) {
            return false;
        }
        int max = this.currentMaxScroll(l);
        if (max > 0 && onScrollbar(l, mouseX, mouseY)) {
            int thumbH = thumbHeight(l, max);
            int thumbTop = l.contentTop() + (viewHeight(l) - thumbH) * this.currentScroll() / max;

            this.scrollbarGrabOffset = mouseY >= thumbTop && mouseY < thumbTop + thumbH ? mouseY - thumbTop : thumbH / 2.0;
            this.scrollbarDragging = true;
            this.dragScrollbarTo(l, mouseY);
            return true;
        }
        if (this.selected != null) {
            int[] r = this.detail(l).button();
            if (inside(mouseX, mouseY, r[0], r[1], r[2], r[3]) && this.canAfford(this.selected)) {
                BuyUsedCarPayload payload = new BuyUsedCarPayload(this.selected.id());
                //? if >=1.20.5 {
                ModClientNetworking.sendToServer(payload);
                //?} else {
                /*ModClientNetworking.sendToServer(BuyUsedCarPayload.ID, payload, BuyUsedCarPayload::write);*/
                //?}
                this.purchasedUntil = System.currentTimeMillis() + BANNER_MS;
            }
            return true;
        }
        for (int i = 0; i < this.cards.size(); i++) {
            int[] r = this.cardRect(l, i);
            if (inside(mouseX, mouseY, r[0], r[1], r[2], r[3])) {
                Card card = this.cards.get(i);
                if (card.listing() == null) {
                    this.outOfStockUntil = System.currentTimeMillis() + BANNER_MS;
                } else {
                    this.selected = card.listing();
                    this.detailScroll = 0;
                    this.outOfStockUntil = 0L;
                }
                return true;
            }
        }
        return true;
    }

    @Override
    //? if >=1.21.9 {
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return this.handleClick(event.x(), event.y(), event.button()) || super.mouseClicked(event, doubleClick);
    }
    //?} else {
    /*public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.handleClick(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }*/
    //?}

    @Override
    //? if >=1.21.9 {
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (this.scrollbarDragging && event.button() == 0) {
            this.dragScrollbarTo(this.layout(), event.y());
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }
    //?} else {
    /*public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrollbarDragging && button == 0) {
            this.dragScrollbarTo(this.layout(), mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }*/
    //?}

    @Override
    //? if >=1.21.9 {
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            this.scrollbarDragging = false;
        }
        return super.mouseReleased(event);
    }
    //?} else {
    /*public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.scrollbarDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }*/
    //?}

    //? if >=1.20.2 {
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return mineturismo$mouseScrolled(scrollY);
    }
    //?} else {
    /*@Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        return mineturismo$mouseScrolled(scrollY);
    }*/
    //?}

    private boolean mineturismo$mouseScrolled(double scrollY) {
        if (scrollY == 0) {
            return false;
        }
        Layout l = this.layout();
        int step = (int) Math.round(scrollY * 24);
        if (this.selected != null) {
            this.detailScroll = Math.max(0, Math.min(this.maxDetailScroll(l), this.detailScroll - step));
        } else {
            this.listScroll = Math.max(0, Math.min(this.maxListScroll(l), this.listScroll - step));
        }
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
