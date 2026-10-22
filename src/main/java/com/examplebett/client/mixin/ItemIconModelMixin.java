//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.mixin;

import com.examplebett.client.render.LegacyItemIconModels;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public abstract class ItemIconModelMixin {
    @Shadow
    public abstract BakedModel getModel(ItemStack stack, Level level, LivingEntity entity, int seed);

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void mineturismo$iconModel(ItemStack stack, Level level, LivingEntity entity, int seed,
                                       CallbackInfoReturnable<BakedModel> cir) {
        ItemStack proxy = LegacyItemIconModels.proxyStack(stack);
        if (proxy != null) {
            cir.setReturnValue(this.getModel(proxy, level, entity, seed));
        }
    }
}
*/
//?}
//? if <1.17 {
/*
package com.examplebett.client.mixin;

import com.examplebett.client.render.LegacyItemIconModels;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public abstract class ItemIconModelMixin {
    @Shadow
    public abstract BakedModel getModel(ItemStack stack, Level level, LivingEntity entity);

    @Inject(method = "getModel(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/client/resources/model/BakedModel;",
            at = @At("HEAD"), cancellable = true)
    private void mineturismo$iconModel(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<BakedModel> cir) {
        ItemStack proxy = LegacyItemIconModels.proxyStack(stack);
        if (proxy != null) {
            cir.setReturnValue(this.getModel(proxy, level, entity));
        }
    }
}
*/
//?}
