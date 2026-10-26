//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.client.gui;

import com.examplebett.compat.ModText;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
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
    public void onPress() {
    }

    @Override
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
    }

    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.bar.render(new GuiGraphics(poseStack), mouseX, mouseY, this.scrollRow.getAsInt());
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
    }
}
*/
//?}
//? if <1.17 && >=1.16 {
/*
package com.examplebett.client.gui;

import com.examplebett.compat.ModText;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
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
    public void onPress() {
    }

    @Override
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
    }

    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.bar.render(new GuiGraphics(poseStack), mouseX, mouseY, this.scrollRow.getAsInt());
    }

}
*/
//?}
//? if <1.16 && >=1.15 {
/*
package com.examplebett.client.gui;

import com.examplebett.compat.ModText;

import com.examplebett.compat.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.sounds.SoundManager;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

final class ScrollbarWidget extends AbstractButton {

    private final DragScrollbar bar;
    private final IntSupplier scrollRow;

    ScrollbarWidget(int x, int y, int height, int visibleRows, int totalRows, IntSupplier scrollRow, IntConsumer onScroll) {
        super(x - 2, y, DragScrollbar.WIDTH + 4, height, "");
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
    public void onPress() {
    }

    @Override
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
    }

    public void renderButton(int mouseX, int mouseY, float partialTick) {
        this.bar.render(new GuiGraphics(null), mouseX, mouseY, this.scrollRow.getAsInt());
    }

}
*/
//?}
//? if <1.15 {
/*
package com.examplebett.client.gui;

import com.examplebett.compat.ModText;

import com.examplebett.compat.GuiGraphics;
import com.examplebett.compat.v114.PoseStack;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.sounds.SoundManager;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

final class ScrollbarWidget extends AbstractButton {

    private final DragScrollbar bar;
    private final IntSupplier scrollRow;

    ScrollbarWidget(int x, int y, int height, int visibleRows, int totalRows, IntSupplier scrollRow, IntConsumer onScroll) {
        super(x - 2, y, DragScrollbar.WIDTH + 4, height, "");
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
    public void onPress() {
    }

    @Override
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
    }

    public void renderButton(int mouseX, int mouseY, float partialTick) {
        this.bar.render(new GuiGraphics(null), mouseX, mouseY, this.scrollRow.getAsInt());
    }

}
*/
//?}
