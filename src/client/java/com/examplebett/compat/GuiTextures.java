package com.examplebett.compat;

//? if <1.20 || >=26 {
/*import com.examplebett.compat.GuiGraphics;
*///?} else {
import net.minecraft.client.gui.GuiGraphics;
//?}
import net.minecraft.resources.ResourceLocation;

public final class GuiTextures {
    private GuiTextures() {
    }

    public static void blit(GuiGraphics g, ResourceLocation texture, int x, int y, float u, float v, int width, int height,
            int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        //? if >=26 {
        /*g.extractor().blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height,
                regionWidth, regionHeight, textureWidth, textureHeight);
        *///?} else if >=1.21.6 {
        g.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height,
                regionWidth, regionHeight, textureWidth, textureHeight);
        //?} else if >=1.21.2 {
        /*g.blit(net.minecraft.client.renderer.RenderType::guiTextured, texture, x, y, u, v, width, height,
                regionWidth, regionHeight, textureWidth, textureHeight);
        *///?} else if >=1.20 {
        /*
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        g.blit(texture, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        com.mojang.blaze3d.systems.RenderSystem.disableBlend();
        *///?} else {
        /*com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
        com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, texture);
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        net.minecraft.client.gui.GuiComponent.blit(g.pose(), x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        com.mojang.blaze3d.systems.RenderSystem.disableBlend();
        *///?}
    }
}
