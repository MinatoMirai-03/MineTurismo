package com.examplebett.client.render;

import com.examplebett.render.CompiledCarModel;
//? if >=1.21.2 {
import net.minecraft.client.renderer.entity.state.EntityRenderState;
//?}

//? if >=1.21.2 {
public class BreakableTireStackRenderState extends EntityRenderState {
    public int lightCoords;
//?} else {
/*public class BreakableTireStackRenderState {
    public int lightCoords;*/
//?}
    public float yRot;

    public float xRot;

    public float pivotY;
    public CompiledCarModel compiledModel;
}
