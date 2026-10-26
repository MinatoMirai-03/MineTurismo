package com.examplebett.item;

import com.examplebett.compat.ModText;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public final class LegacyAvalonItems {
    private static final int CHOCO_BAR_NUTRITION = 1;
    private static final float CHOCO_BAR_SATURATION = 0.1f;

    private LegacyAvalonItems() {
    }

    public static List<MobEffectInstance> wardenEffects(boolean sonicBoom) {
        List<MobEffectInstance> effects = new ArrayList<>();
        effects.add(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
        //? if >=1.21.5 {
        effects.add(new MobEffectInstance(MobEffects.STRENGTH, 1200, 0));
        effects.add(new MobEffectInstance(MobEffects.SPEED, 1200, 0));
        //?} else {
        /*effects.add(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 0));
        effects.add(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 0));*/
        //?}
        //? if >=1.19 {
        effects.add(new MobEffectInstance(MobEffects.DARKNESS, 120, 0));
        //?} else {
        /*effects.add(new MobEffectInstance(MobEffects.BLINDNESS, 120, 0));*/
        //?}
        if (sonicBoom && com.examplebett.effect.SonicBoomEnergy.EFFECT != null) {
            effects.add(new MobEffectInstance(com.examplebett.effect.SonicBoomEnergy.EFFECT, 1200, 0));
        }
        return effects;
    }

    public static void appendEffectLines(List<MobEffectInstance> effects, List<Component> lines) {
        for (MobEffectInstance effect : effects) {
            Component line = ModText.translatable(effect.getDescriptionId());
            if (effect.getAmplifier() > 0) {
                line = ModText.translatable("potion.withAmplifier", line, ModText.translatable("potion.potency." + effect.getAmplifier()));
            }
            int seconds = effect.getDuration() / 20;
            line = ModText.translatable("potion.withDuration", line, String.format("%d:%02d", seconds / 60, seconds % 60));
            lines.add(line.copy().withStyle(isHarmful(effect) ? ChatFormatting.RED : ChatFormatting.BLUE));
        }
    }

    private static boolean isHarmful(MobEffectInstance effect) {
        //? if >=1.20.5 {
        return effect.getEffect().value().getCategory() == net.minecraft.world.effect.MobEffectCategory.HARMFUL;
        //?} else {
        /*return effect.getEffect().getCategory() == net.minecraft.world.effect.MobEffectCategory.HARMFUL;*/
        //?}
    }

    public static Item.Properties drinkProperties(Item.Properties properties) {
        //? if >=1.21.2 && <1.21.4 {
        /*return properties.food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build(),
                net.minecraft.world.item.component.Consumable.builder()
                        .consumeSeconds(1.2f)
                        .animation(net.minecraft.world.item.ItemUseAnimation.DRINK)
                        .sound(net.minecraft.sounds.SoundEvents.GENERIC_DRINK)
                        .hasConsumeParticles(false)
                        .build());*/
        //?}
        //? if >=1.20.5 && <1.21.2 {
        /*return properties.food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build());*/
        //?}
        //? if <1.20.5 {
        /*return properties.food(new FoodProperties.Builder().nutrition(0).saturationMod(0f).alwaysEat().build());*/
        //?}
        //? if >=1.21.4 {
        return properties;
        //?}
    }

    public static Item.Properties chocoBarProperties(Item.Properties properties) {
        //? if >=1.20.5 {
        return properties.food(new FoodProperties.Builder().nutrition(CHOCO_BAR_NUTRITION).saturationModifier(CHOCO_BAR_SATURATION).build());
        //?} else {
        /*return properties.food(new FoodProperties.Builder().nutrition(CHOCO_BAR_NUTRITION).saturationMod(CHOCO_BAR_SATURATION).build());*/
        //?}
    }

    public static class Drink extends Item {
        private final boolean sonicBoom;

        public Drink(Item.Properties properties, boolean sonicBoom) {
            super(drinkProperties(properties));
            this.sonicBoom = sonicBoom;
        }

        @Override
        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
            ItemStack result = super.finishUsingItem(stack, level, entity);
            if (!level.isClientSide()) {
                for (MobEffectInstance effect : wardenEffects(this.sonicBoom)) {
                    entity.addEffect(effect);
                }
            }
            return result;
        }

        //? if <1.21.2 {
        /*@Override
        public net.minecraft.world.item.UseAnim getUseAnimation(ItemStack stack) {
            return net.minecraft.world.item.UseAnim.DRINK;
        }*/
        //?}

        //? if >=1.20.5 && <1.21.4 {
        /*@Override
        public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip,
                                    net.minecraft.world.item.TooltipFlag flag) {
            appendEffectLines(wardenEffects(this.sonicBoom), tooltip);
        }*/
        //?}
        //? if <1.20.5 {
        /*@Override
        public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
            appendEffectLines(wardenEffects(this.sonicBoom), tooltip);
        }*/
        //?}
    }
}
