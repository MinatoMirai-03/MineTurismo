package com.examplebett.client.mixin;

//? if >=1.21 && <26.2 {
import com.examplebett.client.render.SmoothShadingPipelines;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BufferBuilder.class)
public class BufferBuilderMidTexCoordMixin {
    @Inject(method = "build", at = @At("RETURN"))
    private void aiObjectMod$fixMidTexCoord(CallbackInfoReturnable<MeshData> cir) {
        if (!SmoothShadingPipelines.consumeMark((BufferBuilder) (Object) this)) {
            return;
        }
        MeshData meshData = cir.getReturnValue();
        if (meshData != null) {
            MeshData.DrawState drawState = meshData.drawState();
            SmoothShadingPipelines.fixMidTexCoord(meshData.vertexBuffer(), drawState.format(), drawState.vertexCount(),
                    drawState.mode() == VertexFormat.Mode.TRIANGLES);
        }
    }
}
//?}
//? if >=26.2 {
/*import com.examplebett.client.render.SmoothShadingPipelines;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BufferBuilder.class)
public class BufferBuilderMidTexCoordMixin {
    @Inject(method = "build", at = @At("RETURN"))
    private void aiObjectMod$fixMidTexCoord(CallbackInfoReturnable<MeshData> cir) {
        if (!SmoothShadingPipelines.consumeMark((BufferBuilder) (Object) this)) {
            return;
        }
        MeshData meshData = cir.getReturnValue();
        if (meshData != null) {
            MeshData.DrawState drawState = meshData.drawState();
            SmoothShadingPipelines.fixMidTexCoord(meshData.vertexBuffer(), drawState.format(), drawState.vertexCount(),
                    drawState.primitiveTopology() == PrimitiveTopology.TRIANGLES);
        }
    }
}*/
//?}
//? if >=1.19 && <1.21 {
/*import com.examplebett.client.render.SmoothShadingPipelines;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BufferBuilder.class)
public class BufferBuilderMidTexCoordMixin {
    @Inject(method = {"end", "endOrDiscardIfEmpty"}, at = @At("RETURN"))
    private void aiObjectMod$fixMidTexCoord(CallbackInfoReturnable<BufferBuilder.RenderedBuffer> cir) {
        if (!SmoothShadingPipelines.consumeMark((BufferBuilder) (Object) this)) {
            return;
        }
        BufferBuilder.RenderedBuffer rendered = cir.getReturnValue();
        if (rendered != null) {
            BufferBuilder.DrawState drawState = rendered.drawState();
            SmoothShadingPipelines.fixMidTexCoord(rendered.vertexBuffer(), drawState.format(), drawState.vertexCount(),
                    drawState.mode() == VertexFormat.Mode.TRIANGLES);
        }
    }
}*/
//?}
//? if >=1.18.2 && <1.19 {
/*import com.examplebett.client.render.SmoothShadingPipelines;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BufferBuilder.class)
public class BufferBuilderMidTexCoordMixin {
    @Inject(method = "popNextBuffer", at = @At("RETURN"))
    private void aiObjectMod$fixMidTexCoord(CallbackInfoReturnable<Pair<BufferBuilder.DrawState, ByteBuffer>> cir) {
        if (!SmoothShadingPipelines.consumeMark((BufferBuilder) (Object) this)) {
            return;
        }
        Pair<BufferBuilder.DrawState, ByteBuffer> popped = cir.getReturnValue();
        if (popped != null) {
            BufferBuilder.DrawState drawState = popped.getFirst();
            SmoothShadingPipelines.fixMidTexCoord(popped.getSecond(), drawState.format(), drawState.vertexCount(),
                    drawState.mode() == VertexFormat.Mode.TRIANGLES);
        }
    }
}*/
//?}
