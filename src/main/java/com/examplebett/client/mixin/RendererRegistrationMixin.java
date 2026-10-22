//? if <1.15 {
/*
package com.examplebett.client.mixin;

import com.examplebett.entity.BreakableBoxEntity;
import com.examplebett.entity.BreakableConeEntity;
import com.examplebett.entity.BreakableTireStackEntity;
import com.examplebett.entity.CarEntity;
import com.examplebett.client.render.BreakableTireStackRenderer;
import com.examplebett.client.render.CarEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class RendererRegistrationMixin {

    @Shadow
    private void register(Class<? extends Entity> entityClass, EntityRenderer<?> renderer) {
        throw new AssertionError("mixin shadow not applied");
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void mineturismo$registerRenderers(TextureManager textureManager, ItemRenderer itemRenderer,
                                                ReloadableResourceManager resourceManager, CallbackInfo ci) {
        EntityRenderDispatcher self = (EntityRenderDispatcher) (Object) this;
        this.register(CarEntity.class, new CarEntityRenderer(self));
        this.register(BreakableTireStackEntity.class, new BreakableTireStackRenderer<BreakableTireStackEntity>(self));
        this.register(BreakableBoxEntity.class, new BreakableTireStackRenderer<BreakableBoxEntity>(self));
        this.register(BreakableConeEntity.class, new BreakableTireStackRenderer<BreakableConeEntity>(self));
        this.register(com.examplebett.entity.BreakableBarricadeEntity.class, new BreakableTireStackRenderer<com.examplebett.entity.BreakableBarricadeEntity>(self));
    }
}
*/
//?}
