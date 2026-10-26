package com.examplebett.client.render;
import com.examplebett.compat.ModText;

import com.examplebett.AiObjectMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class PlateTextures {

    public record Design(String id, ResourceLocation texture, Component displayName, boolean european, float aspect) {
        private static final float DEFAULT_ASPECT = 2f;

        Design(String id, ResourceLocation texture, Component displayName, boolean european) {
            this(id, texture, displayName, european, DEFAULT_ASPECT);
        }
    }

    public static final List<Design> DESIGNS;

    private static final List<Design> CORE_DESIGNS = List.of(
            new Design("mineturismo_white", AiObjectMod.id("textures/entity/plates/plate_mineturismo_white.png"), ModText.translatable("plate.mineturismo.mineturismo_white"), false),
            new Design("mineturismo_black", AiObjectMod.id("textures/entity/plates/plate_mineturismo_black.png"), ModText.translatable("plate.mineturismo.mineturismo_black"), false),
            new Design("yokohama", AiObjectMod.id("textures/entity/plates/plate_yokohama.png"), ModText.translatable("plate.mineturismo.yokohama"), false),
            new Design("newyork", AiObjectMod.id("textures/entity/plates/plate_newyork.png"), ModText.translatable("plate.mineturismo.newyork"), false),
            new Design("massiveauto", AiObjectMod.id("textures/entity/plates/plate_massiveauto.png"), ModText.translatable("plate.mineturismo.massiveauto"), false),
            new Design("southkorea", AiObjectMod.id("textures/entity/plates/plate_southkorea.png"), ModText.translatable("plate.mineturismo.southkorea"), true, 256f / 62f),
            new Design("germany", AiObjectMod.id("textures/entity/plates/plate_germany.png"), ModText.translatable("plate.mineturismo.germany"), true, 256f / 56f),
            new Design("robinson", AiObjectMod.id("textures/entity/plates/plate_robinson.png"), ModText.translatable("plate.mineturismo.robinson"), false),
            new Design("owarikomaki", AiObjectMod.id("textures/entity/plates/plate_owarikomaki.png"), ModText.translatable("plate.mineturismo.owarikomaki"), false),
            new Design("toyota", AiObjectMod.id("textures/entity/plates/plate_toyota.png"), ModText.translatable("plate.mineturismo.toyota"), false),
            new Design("naniwa", AiObjectMod.id("textures/entity/plates/plate_naniwa.png"), ModText.translatable("plate.mineturismo.naniwa"), false),
            new Design("suginami", AiObjectMod.id("textures/entity/plates/plate_suginami.png"), ModText.translatable("plate.mineturismo.suginami"), false),
            new Design("france", AiObjectMod.id("textures/entity/plates/plate_france.png"), ModText.translatable("plate.mineturismo.france"), true, 256f / 58f),
            new Design("italy", AiObjectMod.id("textures/entity/plates/plate_italy.png"), ModText.translatable("plate.mineturismo.italy"), true, 256f / 54f),
            new Design("italy_glossy", AiObjectMod.id("textures/entity/plates/plate_italy_glossy.png"), ModText.translatable("plate.mineturismo.italy_glossy"), true, 256f / 74f),
            new Design("beijing_green", AiObjectMod.id("textures/entity/plates/plate_beijing_green.png"), ModText.translatable("plate.mineturismo.beijing_green"), true, 256f / 86f),
            new Design("beijing_blue", AiObjectMod.id("textures/entity/plates/plate_beijing_blue.png"), ModText.translatable("plate.mineturismo.beijing_blue"), true, 256f / 90f),
            new Design("texas", AiObjectMod.id("textures/entity/plates/plate_texas.png"), ModText.translatable("plate.mineturismo.texas"), false),
            new Design("unitedkingdom", AiObjectMod.id("textures/entity/plates/plate_unitedkingdom.png"), ModText.translatable("plate.mineturismo.unitedkingdom"), true, 256f / 56f),
            new Design("unitedkingdom_front", AiObjectMod.id("textures/entity/plates/plate_unitedkingdom_front.png"), ModText.translatable("plate.mineturismo.unitedkingdom_front"), true, 256f / 56f),
            new Design("russia", AiObjectMod.id("textures/entity/plates/plate_russia.png"), ModText.translatable("plate.mineturismo.russia"), true, 256f / 58f),
            new Design("adachi_green", AiObjectMod.id("textures/entity/plates/plate_adachi_green.png"), ModText.translatable("plate.mineturismo.adachi_green"), false),
            new Design("tsuchiura", AiObjectMod.id("textures/entity/plates/plate_tsuchiura.png"), ModText.translatable("plate.mineturismo.tsuchiura"), false),
            new Design("sapporo", AiObjectMod.id("textures/entity/plates/plate_sapporo.png"), ModText.translatable("plate.mineturismo.sapporo"), false),
            new Design("chikuho", AiObjectMod.id("textures/entity/plates/plate_chikuho.png"), ModText.translatable("plate.mineturismo.chikuho"), false),
            new Design("hiroshima", AiObjectMod.id("textures/entity/plates/plate_hiroshima.png"), ModText.translatable("plate.mineturismo.hiroshima"), false),
            new Design("kobe", AiObjectMod.id("textures/entity/plates/plate_kobe.png"), ModText.translatable("plate.mineturismo.kobe"), false),
            new Design("himeji", AiObjectMod.id("textures/entity/plates/plate_himeji.png"), ModText.translatable("plate.mineturismo.himeji"), false),
            new Design("shinagawa", AiObjectMod.id("textures/entity/plates/plate_shinagawa.png"), ModText.translatable("plate.mineturismo.shinagawa"), false),
            new Design("maikura", AiObjectMod.id("textures/entity/plates/plate_maikura.png"), ModText.translatable("plate.mineturismo.maikura"), false, 256f / 137f),
            new Design("sayama", AiObjectMod.id("textures/entity/plates/plate_sayama.png"), ModText.translatable("plate.mineturismo.sayama"), false, 256f / 136f),
            new Design("koto_green", AiObjectMod.id("textures/entity/plates/plate_koto_green.png"), ModText.translatable("plate.mineturismo.koto_green"), false, 256f / 131f),
            new Design("taiwan_black", AiObjectMod.id("textures/entity/plates/plate_taiwan_black.png"), ModText.translatable("plate.mineturismo.taiwan_black"), false, 256f / 108f),
            new Design("taiwan_red", AiObjectMod.id("textures/entity/plates/plate_taiwan_red.png"), ModText.translatable("plate.mineturismo.taiwan_red"), false, 256f / 108f),
            new Design("southkorea_yellow", AiObjectMod.id("textures/entity/plates/plate_southkorea_yellow.png"), ModText.translatable("plate.mineturismo.southkorea_yellow"), true, 256f / 59f),
            new Design("gaikoku_blue", AiObjectMod.id("textures/entity/plates/plate_gaikoku_blue.png"), ModText.translatable("plate.mineturismo.gaikoku_blue"), false, 256f / 108f),

            new Design("greenfield", AiObjectMod.id("textures/entity/plates/plate_greenfield.png"), ModText.translatable("plate.mineturismo.greenfield"), false),
            new Design("greenfield_blue", AiObjectMod.id("textures/entity/plates/plate_greenfield_blue.png"), ModText.translatable("plate.mineturismo.greenfield_blue"), false),
            new Design("leonida", AiObjectMod.id("textures/entity/plates/plate_leonida.png"), ModText.translatable("plate.mineturismo.leonida"), false),

            new Design("thailand", AiObjectMod.id("textures/entity/plates/plate_thailand.png"), ModText.translatable("plate.mineturismo.thailand"), false, 256f / 113f),
            new Design("philippines", AiObjectMod.id("textures/entity/plates/plate_philippines.png"), ModText.translatable("plate.mineturismo.philippines"), false, 256f / 92f),
            new Design("singapore", AiObjectMod.id("textures/entity/plates/plate_singapore.png"), ModText.translatable("plate.mineturismo.singapore"), true, 256f / 55f),
            new Design("malaysia", AiObjectMod.id("textures/entity/plates/plate_malaysia.png"), ModText.translatable("plate.mineturismo.malaysia"), true, 256f / 55f),
            new Design("hongkong", AiObjectMod.id("textures/entity/plates/plate_hongkong.png"), ModText.translatable("plate.mineturismo.hongkong"), true, 256f / 55f),
            new Design("australia_nsw", AiObjectMod.id("textures/entity/plates/plate_australia_nsw.png"), ModText.translatable("plate.mineturismo.australia_nsw"), false, 256f / 92f),
            new Design("sweden", AiObjectMod.id("textures/entity/plates/plate_sweden.png"), ModText.translatable("plate.mineturismo.sweden"), true, 256f / 55f),
            new Design("newmexico", AiObjectMod.id("textures/entity/plates/plate_newmexico.png"), ModText.translatable("plate.mineturismo.newmexico"), false),
            new Design("newmexico_turquoise", AiObjectMod.id("textures/entity/plates/plate_newmexico_turquoise.png"), ModText.translatable("plate.mineturismo.newmexico_turquoise"), false),
            new Design("hawaii", AiObjectMod.id("textures/entity/plates/plate_hawaii.png"), ModText.translatable("plate.mineturismo.hawaii"), false),
            new Design("colorado", AiObjectMod.id("textures/entity/plates/plate_colorado.png"), ModText.translatable("plate.mineturismo.colorado"), false),

            new Design("india", AiObjectMod.id("textures/entity/plates/plate_india.png"), ModText.translatable("plate.mineturismo.india"), true, 256f / 61f),
            new Design("dubai", AiObjectMod.id("textures/entity/plates/plate_dubai.png"), ModText.translatable("plate.mineturismo.dubai"), true, 256f / 54f),

            new Design("blank_japan", AiObjectMod.id("textures/entity/plates/plate_blank_japan.png"), ModText.translatable("plate.mineturismo.blank_japan"), false),
            new Design("blank_europe", AiObjectMod.id("textures/entity/plates/plate_blank_europe.png"), ModText.translatable("plate.mineturismo.blank_europe"), true, 256f / 56f),

            new Design("blank_europe_yellow", AiObjectMod.id("textures/entity/plates/plate_blank_europe_yellow.png"), ModText.translatable("plate.mineturismo.blank_europe_yellow"), true, 256f / 56f),
            new Design("blank_newyork", AiObjectMod.id("textures/entity/plates/plate_blank_newyork.png"), ModText.translatable("plate.mineturismo.blank_newyork"), false),
            new Design("blank_california", AiObjectMod.id("textures/entity/plates/plate_blank_california.png"), ModText.translatable("plate.mineturismo.blank_california"), false),

            new Design("blank_green", AiObjectMod.id("textures/entity/plates/plate_blank_green.png"), ModText.translatable("plate.mineturismo.blank_green"), false),
            new Design("blank_yellow", AiObjectMod.id("textures/entity/plates/plate_blank_yellow.png"), ModText.translatable("plate.mineturismo.blank_yellow"), false),
            new Design("blank_black", AiObjectMod.id("textures/entity/plates/plate_blank_black.png"), ModText.translatable("plate.mineturismo.blank_black"), false),
            new Design("blank_brazil", AiObjectMod.id("textures/entity/plates/plate_blank_brazil.png"), ModText.translatable("plate.mineturismo.blank_brazil"), true, 256f / 82f),
            new Design("blank_ontario", AiObjectMod.id("textures/entity/plates/plate_blank_ontario.png"), ModText.translatable("plate.mineturismo.blank_ontario"), false),
            new Design("blank_italy", AiObjectMod.id("textures/entity/plates/plate_blank_italy.png"), ModText.translatable("plate.mineturismo.blank_italy"), true, 256f / 53f),

            new Design("blank_singapore", AiObjectMod.id("textures/entity/plates/plate_blank_singapore.png"), ModText.translatable("plate.mineturismo.blank_singapore"), true, 256f / 55f),
            new Design("blank_malaysia", AiObjectMod.id("textures/entity/plates/plate_blank_malaysia.png"), ModText.translatable("plate.mineturismo.blank_malaysia"), true, 256f / 55f),
            new Design("blank_hongkong", AiObjectMod.id("textures/entity/plates/plate_blank_hongkong.png"), ModText.translatable("plate.mineturismo.blank_hongkong"), true, 256f / 55f),
            new Design("blank_texas", AiObjectMod.id("textures/entity/plates/plate_blank_texas.png"), ModText.translatable("plate.mineturismo.blank_texas"), false),
            new Design("blank_hawaii", AiObjectMod.id("textures/entity/plates/plate_blank_hawaii.png"), ModText.translatable("plate.mineturismo.blank_hawaii"), false),
            new Design("blank_colorado", AiObjectMod.id("textures/entity/plates/plate_blank_colorado.png"), ModText.translatable("plate.mineturismo.blank_colorado"), false),
            new Design("blank_china", AiObjectMod.id("textures/entity/plates/plate_blank_china.png"), ModText.translatable("plate.mineturismo.blank_china"), true, 256f / 90f),
            new Design("blank_taiwan", AiObjectMod.id("textures/entity/plates/plate_blank_taiwan.png"), ModText.translatable("plate.mineturismo.blank_taiwan"), false, 256f / 108f)
    );

    static {
        List<Design> all = new java.util.ArrayList<>(CORE_DESIGNS);
        for (com.examplebett.render.AddonPlateDesigns.Entry entry : com.examplebett.render.AddonPlateDesigns.entries()) {
            if (all.stream().anyMatch(d -> d.id().equals(entry.id()))) {
                continue;
            }
            all.add(new Design(entry.id(), AiObjectMod.id("textures/entity/plates/plate_" + entry.id() + ".png"),
                    ModText.translatable("plate.mineturismo." + entry.id()), entry.european(), entry.aspect()));
        }
        DESIGNS = java.util.Collections.unmodifiableList(all);
    }

    public static ResourceLocation texture(String id) {
        for (Design design : DESIGNS) {
            if (design.id().equals(id)) {
                return design.texture();
            }
        }
        return null;
    }

    public static boolean isEuropean(String id) {
        for (Design design : DESIGNS) {
            if (design.id().equals(id)) {
                return design.european();
            }
        }
        return false;
    }

    public static float aspect(String id) {
        for (Design design : DESIGNS) {
            if (design.id().equals(id)) {
                return design.aspect();
            }
        }
        return Design.DEFAULT_ASPECT;
    }

    private PlateTextures() {
    }
}
