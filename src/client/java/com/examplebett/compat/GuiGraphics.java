package com.examplebett.compat;

//? if <1.20 {
/*
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class GuiGraphics {
    private final PoseStack poseStack;

    public GuiGraphics(PoseStack poseStack) {
        this.poseStack = poseStack;
    }

    public PoseStack pose() {
        return this.poseStack;
    }

    public int guiWidth() {
        return net.minecraft.client.Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }

    public int guiHeight() {
        return net.minecraft.client.Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }

    public void fill(int x1, int y1, int x2, int y2, int color) {
        GuiComponent.fill(this.poseStack, x1, y1, x2, y2, color);
    }

    public void drawCenteredString(Font font, String text, int x, int y, int color) {
        GuiComponent.drawCenteredString(this.poseStack, font, text, x, y, color);
    }

    public void drawCenteredString(Font font, Component text, int x, int y, int color) {
        GuiComponent.drawCenteredString(this.poseStack, font, text, x, y, color);
    }

    public void drawCenteredString(Font font, FormattedCharSequence text, int x, int y, int color) {
        GuiComponent.drawCenteredString(this.poseStack, font, text, x, y, color);
    }

    public void drawString(Font font, String text, int x, int y, int color) {
        GuiComponent.drawString(this.poseStack, font, text, x, y, color);
    }

    public void drawString(Font font, String text, int x, int y, int color, boolean dropShadow) {
        if (dropShadow) {
            GuiComponent.drawString(this.poseStack, font, text, x, y, color);
        } else {
            font.draw(this.poseStack, text, x, y, color);
        }
    }

    public void drawString(Font font, Component text, int x, int y, int color) {
        GuiComponent.drawString(this.poseStack, font, text, x, y, color);
    }

    public void drawString(Font font, Component text, int x, int y, int color, boolean dropShadow) {
        if (dropShadow) {
            GuiComponent.drawString(this.poseStack, font, text, x, y, color);
        } else {
            font.draw(this.poseStack, text, x, y, color);
        }
    }

    public void drawString(Font font, FormattedCharSequence text, int x, int y, int color) {
        GuiComponent.drawString(this.poseStack, font, text, x, y, color);
    }

    public void drawString(Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow) {
        if (dropShadow) {
            GuiComponent.drawString(this.poseStack, font, text, x, y, color);
        } else {
            font.draw(this.poseStack, text, x, y, color);
        }
    }

    public void renderItem(net.minecraft.world.item.ItemStack stack, int x, int y) {
        LegacyItemIcons.render(this.poseStack, stack, x, y);
    }

    public void enableScissor(int x0, int y0, int x1, int y1) {
        com.mojang.blaze3d.platform.Window window = net.minecraft.client.Minecraft.getInstance().getWindow();
        double scale = window.getGuiScale();
        com.mojang.blaze3d.systems.RenderSystem.enableScissor((int) (x0 * scale), (int) (window.getHeight() - y1 * scale),
                (int) Math.max(0, (x1 - x0) * scale), (int) Math.max(0, (y1 - y0) * scale));
    }

    public void disableScissor() {
        com.mojang.blaze3d.systems.RenderSystem.disableScissor();
    }
}
*/
//?}

//? if >=26 {
/*
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class GuiGraphics {
    private final GuiGraphicsExtractor extractor;

    public GuiGraphics(GuiGraphicsExtractor extractor) {
        this.extractor = extractor;
    }

    public GuiGraphicsExtractor extractor() {
        return this.extractor;
    }

    public org.joml.Matrix3x2fStack pose() {
        return this.extractor.pose();
    }

    public int guiWidth() {
        return this.extractor.guiWidth();
    }

    public int guiHeight() {
        return this.extractor.guiHeight();
    }

    public void fill(int x1, int y1, int x2, int y2, int color) {
        this.extractor.fill(x1, y1, x2, y2, color);
    }

    public void drawCenteredString(Font font, String text, int x, int y, int color) {
        this.extractor.centeredText(font, text, x, y, color);
    }

    public void drawCenteredString(Font font, Component text, int x, int y, int color) {
        this.extractor.centeredText(font, text, x, y, color);
    }

    public void drawCenteredString(Font font, FormattedCharSequence text, int x, int y, int color) {
        this.extractor.centeredText(font, text, x, y, color);
    }

    public void drawString(Font font, String text, int x, int y, int color) {
        this.extractor.text(font, text, x, y, color);
    }

    public void drawString(Font font, String text, int x, int y, int color, boolean dropShadow) {
        this.extractor.text(font, text, x, y, color, dropShadow);
    }

    public void drawString(Font font, Component text, int x, int y, int color) {
        this.extractor.text(font, text, x, y, color);
    }

    public void drawString(Font font, Component text, int x, int y, int color, boolean dropShadow) {
        this.extractor.text(font, text, x, y, color, dropShadow);
    }

    public void drawString(Font font, FormattedCharSequence text, int x, int y, int color) {
        this.extractor.text(font, text, x, y, color);
    }

    public void drawString(Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow) {
        this.extractor.text(font, text, x, y, color, dropShadow);
    }

    public void renderItem(net.minecraft.world.item.ItemStack stack, int x, int y) {
        this.extractor.item(stack, x, y);
    }

    public void enableScissor(int x0, int y0, int x1, int y1) {
        this.extractor.enableScissor(x0, y0, x1, y1);
    }

    public void disableScissor() {
        this.extractor.disableScissor();
    }

    public void nextStratum() {
        this.extractor.nextStratum();
    }

    public void blitSprite(com.mojang.blaze3d.pipeline.RenderPipeline pipeline, net.minecraft.resources.Identifier sprite, int x, int y, int width, int height, int color) {
        this.extractor.blitSprite(pipeline, sprite, x, y, width, height, color);
    }
}
*/
//?}
