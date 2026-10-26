//? if <1.15 {
/*
package com.examplebett.client.mixin;

import com.examplebett.block.AiObjectModBlocks;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockRenderLayerMixin {

    @Inject(method = "getRenderLayer", at = @At("HEAD"), cancellable = true)
    private void mineturismo$renderLayer(CallbackInfoReturnable<BlockLayer> cir) {
        Object self = this;
        if (self == AiObjectModBlocks.CAR_BREAKABLE_GLASS) {
            cir.setReturnValue(BlockLayer.CUTOUT);
        } else if (self == AiObjectModBlocks.WATER_PUDDLE) {
            cir.setReturnValue(BlockLayer.TRANSLUCENT);
        }
    }
}
*/
//?}
