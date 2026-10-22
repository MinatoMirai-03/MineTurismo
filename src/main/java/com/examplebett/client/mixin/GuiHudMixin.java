//? if <1.15 {
/*
package com.examplebett.client.mixin;

import com.examplebett.compat.v114.HudRenderCallback;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void mineturismo$renderHud(float tickDelta, CallbackInfo ci) {
        HudRenderCallback.EVENT.invoker().onHudRender(tickDelta);
    }
}
*/
//?}
