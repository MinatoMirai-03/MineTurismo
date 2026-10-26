package com.examplebett.client.mixin;

import com.examplebett.client.gui.CarCustomizationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    //? if >=26.3 {
    /*@Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void aiObjectMod$hidePlayerDuringCarCustomization(
            Entity entity, Frustum frustum, double camX, double camY, double camZ, float partialTick, CallbackInfoReturnable<Boolean> cir) {
        this.aiObjectMod$hidePlayer(entity, cir);
    }*/
    //?} else {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void aiObjectMod$hidePlayerDuringCarCustomization(
            Entity entity, Frustum frustum, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir) {
        this.aiObjectMod$hidePlayer(entity, cir);
    }
    //?}

    private void aiObjectMod$hidePlayer(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        Minecraft minecraft = Minecraft.getInstance();
        if (entity == minecraft.player && minecraft.screen instanceof CarCustomizationScreen) {
            cir.setReturnValue(false);
        }
    }
}
