//? if >=1.18 {
//?} else if <1.18 && >=1.16.2 {
/*
package com.examplebett.compat;

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

        font.drawShadow(this.poseStack, text, x - font.width(text) / 2.0f, y, color);
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

        font.drawShadow(this.poseStack, text, x, y, color);
    }

    public void drawString(Font font, FormattedCharSequence text, int x, int y, int color, boolean dropShadow) {
        if (dropShadow) {
            font.drawShadow(this.poseStack, text, x, y, color);
        } else {
            font.draw(this.poseStack, text, x, y, color);
        }
    }

    public void renderItem(net.minecraft.world.item.ItemStack stack, int x, int y) {
        net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
    }

    public void scaledStart(float scale) {
        this.poseStack.pushPose();
        this.poseStack.scale(scale, scale, 1.0f);
    }

    public void scaledEnd() {
        this.poseStack.popPose();
    }

    public int textWidth(Font font, Component text) {
        return font.width(text);
    }

    public void enableScissor(int x0, int y0, int x1, int y1) {
        com.mojang.blaze3d.platform.Window window = net.minecraft.client.Minecraft.getInstance().getWindow();
        double scale = window.getGuiScale();
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
        org.lwjgl.opengl.GL11.glScissor((int) (x0 * scale), (int) (window.getHeight() - y1 * scale),
                (int) Math.max(0, (x1 - x0) * scale), (int) Math.max(0, (y1 - y0) * scale));
    }

    public void disableScissor() {
        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
    }
}
*/
//?} else if <1.16.2 && >=1.16 {
/*
package com.examplebett.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;

public class GuiGraphics extends GuiComponent {
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
        this.drawCenteredString(this.poseStack, font, text, x, y, color);
    }

    public void drawCenteredString(Font font, Component text, int x, int y, int color) {
        this.drawCenteredString(this.poseStack, font, text, x, y, color);
    }

    public void drawCenteredString(Font font, net.minecraft.network.chat.FormattedText text, int x, int y, int color) {
        font.drawShadow(this.poseStack, text, x - font.width(text) / 2, y, color);
    }

    public void drawString(Font font, String text, int x, int y, int color) {
        this.drawString(this.poseStack, font, text, x, y, color);
    }

    public void drawString(Font font, String text, int x, int y, int color, boolean dropShadow) {
        if (dropShadow) {
            this.drawString(this.poseStack, font, text, x, y, color);
        } else {
            font.draw(this.poseStack, text, x, y, color);
        }
    }

    public void drawString(Font font, Component text, int x, int y, int color) {
        this.drawString(this.poseStack, font, text, x, y, color);
    }

    public void drawString(Font font, Component text, int x, int y, int color, boolean dropShadow) {
        if (dropShadow) {
            this.drawString(this.poseStack, font, text, x, y, color);
        } else {
            font.draw(this.poseStack, text, x, y, color);
        }
    }

    public void drawString(Font font, net.minecraft.network.chat.FormattedText text, int x, int y, int color, boolean dropShadow) {
        if (dropShadow) {
            font.drawShadow(this.poseStack, text, x, y, color);
        } else {
            font.draw(this.poseStack, text, x, y, color);
        }
    }

    public void renderItem(net.minecraft.world.item.ItemStack stack, int x, int y) {
        net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
    }

    public void scaledStart(float scale) {
        this.poseStack.pushPose();
        this.poseStack.scale(scale, scale, 1.0f);
    }

    public void scaledEnd() {
        this.poseStack.popPose();
    }

    public int textWidth(Font font, Component text) {
        return font.width(text);
    }

    public void enableScissor(int x0, int y0, int x1, int y1) {
        com.mojang.blaze3d.platform.Window window = net.minecraft.client.Minecraft.getInstance().getWindow();
        double scale = window.getGuiScale();
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
        org.lwjgl.opengl.GL11.glScissor((int) (x0 * scale), (int) (window.getHeight() - y1 * scale),
                (int) Math.max(0, (x1 - x0) * scale), (int) Math.max(0, (y1 - y0) * scale));
    }

    public void disableScissor() {
        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
    }
}
*/
//?} else if <1.16 && >=1.15 {
/*
package com.examplebett.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;

public class GuiGraphics extends GuiComponent {
    private final PoseStack poseStack;

    public GuiGraphics(PoseStack poseStack) {
        this.poseStack = poseStack != null ? poseStack : new GlPoseStack();
    }

    public PoseStack pose() {
        return this.poseStack;
    }

    private static final class GlPoseStack extends PoseStack {
        @Override
        public void pushPose() {
            super.pushPose();
            com.mojang.blaze3d.systems.RenderSystem.pushMatrix();
        }

        @Override
        public void popPose() {
            super.popPose();
            com.mojang.blaze3d.systems.RenderSystem.popMatrix();
        }

        @Override
        public void translate(double x, double y, double z) {
            super.translate(x, y, z);
            com.mojang.blaze3d.systems.RenderSystem.translated(x, y, z);
        }

        @Override
        public void scale(float x, float y, float z) {
            super.scale(x, y, z);
            com.mojang.blaze3d.systems.RenderSystem.scalef(x, y, z);
        }

        @Override
        public void mulPose(com.mojang.math.Quaternion rotation) {
            super.mulPose(rotation);
            com.mojang.blaze3d.systems.RenderSystem.multMatrix(new com.mojang.math.Matrix4f(rotation));
        }
    }

    public void renderItem(net.minecraft.world.item.ItemStack stack, int x, int y) {
        net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
    }

    public int guiWidth() {
        return net.minecraft.client.Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }

    public int guiHeight() {
        return net.minecraft.client.Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }

    public void drawCenteredString(Font font, Component text, int x, int y, int color) {
        this.drawCenteredString(font, text.getString(), x, y, color);
    }

    public void drawString(Font font, String text, int x, int y, int color, boolean dropShadow) {
        if (dropShadow) {
            this.drawString(font, text, x, y, color);
        } else {
            font.draw(text, x, y, color);
        }
    }

    public void drawString(Font font, Component text, int x, int y, int color) {
        this.drawString(font, text.getString(), x, y, color);
    }

    public void drawString(Font font, Component text, int x, int y, int color, boolean dropShadow) {
        if (dropShadow) {
            this.drawString(font, text.getString(), x, y, color);
        } else {
            font.draw(text.getString(), x, y, color);
        }
    }

    public void scaledStart(float scale) {
        com.mojang.blaze3d.systems.RenderSystem.pushMatrix();
        com.mojang.blaze3d.systems.RenderSystem.scalef(scale, scale, 1.0f);
    }

    public void scaledEnd() {
        com.mojang.blaze3d.systems.RenderSystem.popMatrix();
    }

    public int textWidth(Font font, Component text) {
        return font.width(text.getString());
    }

    public void enableScissor(int x0, int y0, int x1, int y1) {
        com.mojang.blaze3d.platform.Window window = net.minecraft.client.Minecraft.getInstance().getWindow();
        double scale = window.getGuiScale();
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
        org.lwjgl.opengl.GL11.glScissor((int) (x0 * scale), (int) (window.getHeight() - y1 * scale),
                (int) Math.max(0, (x1 - x0) * scale), (int) Math.max(0, (y1 - y0) * scale));
    }

    public void disableScissor() {
        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
    }
}
*/
//?} else if <1.15 {
/*
package com.examplebett.compat;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;

public class GuiGraphics extends GuiComponent {
    private final com.examplebett.compat.v114.PoseStack poseStack = com.examplebett.compat.v114.PoseStack.gl();

    public GuiGraphics(Object poseStack) {
    }

    public com.examplebett.compat.v114.PoseStack pose() {
        return this.poseStack;
    }

    public void renderItem(net.minecraft.world.item.ItemStack stack, int x, int y) {
        net.minecraft.client.Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
    }

    public void scaledStart(float scale) {
        com.mojang.blaze3d.platform.GlStateManager.pushMatrix();
        com.mojang.blaze3d.platform.GlStateManager.scalef(scale, scale, 1.0f);
    }

    public void scaledEnd() {
        com.mojang.blaze3d.platform.GlStateManager.popMatrix();
    }

    public int textWidth(Font font, Component text) {
        return font.width(text.getString());
    }

    public int guiWidth() {
        return net.minecraft.client.Minecraft.getInstance().window.getGuiScaledWidth();
    }

    public int guiHeight() {
        return net.minecraft.client.Minecraft.getInstance().window.getGuiScaledHeight();
    }

    public void drawCenteredString(Font font, Component text, int x, int y, int color) {
        this.drawCenteredString(font, text.getString(), x, y, color);
    }

    public void drawString(Font font, String text, int x, int y, int color, boolean dropShadow) {
        if (dropShadow) {
            this.drawString(font, text, x, y, color);
        } else {
            font.draw(text, x, y, color);
        }
    }

    public void drawString(Font font, Component text, int x, int y, int color) {
        this.drawString(font, text.getString(), x, y, color);
    }

    public void drawString(Font font, Component text, int x, int y, int color, boolean dropShadow) {
        if (dropShadow) {
            this.drawString(font, text.getString(), x, y, color);
        } else {
            font.draw(text.getString(), x, y, color);
        }
    }

    public void enableScissor(int x0, int y0, int x1, int y1) {
        com.mojang.blaze3d.platform.Window window = net.minecraft.client.Minecraft.getInstance().window;
        double scale = window.getGuiScale();
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
        org.lwjgl.opengl.GL11.glScissor((int) (x0 * scale), (int) (window.getHeight() - y1 * scale),
                (int) Math.max(0, (x1 - x0) * scale), (int) Math.max(0, (y1 - y0) * scale));
    }

    public void disableScissor() {
        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
    }
}
*/
//?} else {
//?}
