package com.examplebett.client.gui;

import com.examplebett.compat.ModText;
//? if >=26 {
/*import com.examplebett.compat.GuiGraphics;*/
//?}
//? if >=1.20 && <26 {
import net.minecraft.client.gui.GuiGraphics;
//?}
//? if <1.20 {
/*import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;*/
//?}
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
//? if >=1.21.9 {
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
//?}
import net.minecraft.client.sounds.SoundManager;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

final class ScrollbarWidget extends AbstractButton {

    private final DragScrollbar bar;
    private final IntSupplier scrollRow;

    ScrollbarWidget(int x, int y, int height, int visibleRows, int totalRows, IntSupplier scrollRow, IntConsumer onScroll) {
        super(x - 2, y, DragScrollbar.WIDTH + 4, height, ModText.literal(""));
        this.bar = new DragScrollbar(onScroll);
        this.bar.setBounds(x, y, height, visibleRows, totalRows);
        this.scrollRow = scrollRow;
        this.visible = this.bar.isActive();
        this.active = this.visible;
    }

    static int xInArrowColumn(int arrowX) {
        return arrowX + (20 - DragScrollbar.WIDTH) / 2;
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    @Override
    //? if >=1.21.9 {
    public void onPress(InputWithModifiers inputWithModifiers) {
    //?} else
    /*public void onPress() {*/
    }

    //? if >=1.21.9 {
    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        this.bar.mouseClicked(event.x(), event.y(), this.scrollRow.getAsInt());
    }

    @Override
    protected void onDrag(MouseButtonEvent event, double dragX, double dragY) {
        this.bar.mouseDragged(event.y());
    }

    @Override
    public void onRelease(MouseButtonEvent event) {
        this.bar.mouseReleased();
    }
    //?} else {
    /*@Override
    public void onClick(double mouseX, double mouseY) {
        this.bar.mouseClicked(mouseX, mouseY, this.scrollRow.getAsInt());
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.bar.mouseDragged(mouseY);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.bar.mouseReleased();
    }*/
    //?}

    //? if >=26 {
    /*protected void extractContents(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        this.bar.render(new GuiGraphics(extractor), mouseX, mouseY, this.scrollRow.getAsInt());
    }*/
    //?}
    //? if >=1.21.11 && <26 {
    /*protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.bar.render(guiGraphics, mouseX, mouseY, this.scrollRow.getAsInt());
    }*/
    //?}
    //? if >=1.20 && <1.21.11 {
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.bar.render(guiGraphics, mouseX, mouseY, this.scrollRow.getAsInt());
    }
    //?}
    //? if >=1.19.4 && <1.20 {
    /*public void renderWidget(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.bar.render(new GuiGraphics(poseStack), mouseX, mouseY, this.scrollRow.getAsInt());
    }*/
    //?}
    //? if <1.19.4 {
    /*public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.bar.render(new GuiGraphics(poseStack), mouseX, mouseY, this.scrollRow.getAsInt());
    }*/
    //?}

    //? if >=1.19.3 {
    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
    //?} else {
    /*@Override
    public void updateNarration(NarrationElementOutput output) {
    }*/
    //?}
}
