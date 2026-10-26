package com.examplebett.client.gui;
import com.examplebett.compat.ModText;

import com.examplebett.AiObjectMod;
import com.examplebett.inventory.MassiveAutoAppraisalMenu;
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
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//? if >=1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class MassiveAutoAppraisalScreen extends AbstractContainerScreen<MassiveAutoAppraisalMenu> {

    private static final int PX = 2;
    private static final int SCREEN_X0 = 8;
    private static final int SCREEN_X1 = 184;
    private static final int SCREEN_Y0 = 14;
    private static final int SCREEN_Y1 = 202;
    private static final int HEADER_HEIGHT = 18;
    private static final int SELL_BUTTON_Y = 82;
    private static final int SELL_BUTTON_HEIGHT = 16;
    private static final int SELL_BUTTON_X0 = 46;
    private static final int SELL_BUTTON_X1 = 146;
    private static final int DIALOG_Y0 = 58;
    private static final int DIALOG_Y1 = 138;
    private static final int DIALOG_BUTTON_Y = 116;
    private static final float SMALL = 0.75f;

    private static final int COLOR_SCREEN = 0xFFF4F1EA;
    private static final int COLOR_BLUE = 0xFF1E4FA8;
    private static final int COLOR_BLUE_HOVER = 0xFF2D63C8;
    private static final int COLOR_YELLOW = 0xFFF2B705;
    private static final int COLOR_DISABLED = 0xFFB9B6AE;
    private static final int COLOR_SLOT = 0xFF8B8B8B;
    private static final int COLOR_SLOT_DARK = 0xFF373737;
    private static final int COLOR_SLOT_LIGHT = 0xFFFFFFFF;
    private static final ResourceLocation ICON = AiObjectMod.id("textures/gui/smart_dispatch/massive_auto.png");

    private boolean confirming = false;

    private final PhoneSkin skin = PhoneSkin.ofHeldPhone();

    private int[] homeRect() {
        return this.skin.homeRect(this.leftPos, this.leftPos + this.imageWidth, this.topPos + SCREEN_Y1, this.topPos + this.imageHeight);
    }

    public MassiveAutoAppraisalScreen(MassiveAutoAppraisalMenu menu, Inventory playerInventory, Component title) {

        //? if >=26.1 {
        /*super(menu, playerInventory, title, 192, 226);*/
        //?} else {
        super(menu, playerInventory, title);
        this.imageWidth = 192;
        this.imageHeight = 226;
        //?}
    }

    private static boolean inside(double mx, double my, int x0, int y0, int x1, int y1) {
        return mx >= x0 && mx < x1 && my >= y0 && my < y1;
    }

    private void drawSmall(GuiGraphics g, Component text, int centerX, int y, int color) {
        int w = Math.round(this.font.width(text) * SMALL);
        //? if >=1.21.6 {
        g.pose().pushMatrix();
        g.pose().scale(SMALL, SMALL);
        //?} else {
        /*g.pose().pushPose();
        g.pose().scale(SMALL, SMALL, 1.0f);*/
        //?}
        g.drawString(this.font, text, Math.round((centerX - w / 2f) / SMALL), Math.round(y / SMALL), color, false);
        //? if >=1.21.6 {
        g.pose().popMatrix();
        //?} else
        /*g.pose().popPose();*/
    }

    private static void drawSlotFrame(GuiGraphics g, int x, int y) {
        g.fill(x - 1, y - 1, x + 17, y + 17, COLOR_SLOT_DARK);
        g.fill(x, y, x + 18, y + 18, COLOR_SLOT_LIGHT);
        g.fill(x, y, x + 17, y + 17, COLOR_SLOT);
    }

    private void drawPhone(GuiGraphics g, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        this.skin.drawBody(g, x, y, x + this.imageWidth, y + this.imageHeight,
                x + SCREEN_X0, y + SCREEN_Y0, x + SCREEN_X1, y + SCREEN_Y1, this.homeRect());
        g.fill(x + SCREEN_X0, y + SCREEN_Y0, x + SCREEN_X1, y + SCREEN_Y1, COLOR_SCREEN);
        int cx = x + this.imageWidth / 2;

        g.fill(x + SCREEN_X0, y + SCREEN_Y0, x + SCREEN_X1, y + SCREEN_Y0 + HEADER_HEIGHT, 0xFFFFFFFF);
        g.fill(x + SCREEN_X0, y + SCREEN_Y0 + HEADER_HEIGHT - 2, x + SCREEN_X1, y + SCREEN_Y0 + HEADER_HEIGHT, COLOR_YELLOW);
        String name = "Massive Auto";
        int nameWidth = this.font.width(name);
        int iconSize = 12;
        int headerLeft = cx - (iconSize + 4 + nameWidth) / 2;
        com.examplebett.compat.GuiTextures.blit(g, ICON, headerLeft, y + SCREEN_Y0 + 2, 0f, 0f, iconSize, iconSize, 64, 64, 64, 64);
        g.drawString(this.font, name, headerLeft + iconSize + 4, y + SCREEN_Y0 + 4, COLOR_BLUE, false);

        this.drawSmall(g, ModText.translatable("gui.mineturismo.massive_auto.select_car"), cx, y + 38, 0xFF3A3A40);
        int slotX = x + MassiveAutoAppraisalMenu.SELL_SLOT_X - 1;
        int slotY = y + MassiveAutoAppraisalMenu.SELL_SLOT_Y - 1;

        g.fill(slotX - 3, slotY - 3, slotX + 21, slotY + 21, COLOR_YELLOW);
        drawSlotFrame(g, slotX, slotY);

        boolean canSell = this.menu.hasCarToSell();
        boolean hover = !this.confirming && canSell
                && inside(mouseX, mouseY, x + SELL_BUTTON_X0, y + SELL_BUTTON_Y, x + SELL_BUTTON_X1, y + SELL_BUTTON_Y + SELL_BUTTON_HEIGHT);
        SmartDispatchAppScreen.fillPixelRounded(g, x + SELL_BUTTON_X0, y + SELL_BUTTON_Y, x + SELL_BUTTON_X1, y + SELL_BUTTON_Y + SELL_BUTTON_HEIGHT, 1,
                !canSell ? COLOR_DISABLED : hover ? COLOR_BLUE_HOVER : COLOR_BLUE);
        Component sell = ModText.translatable("gui.mineturismo.massive_auto.sell");
        g.drawString(this.font, sell, cx - this.font.width(sell) / 2, y + SELL_BUTTON_Y + 4, 0xFFFFFFFF, false);

        g.fill(x + SCREEN_X0 + 6, y + 106, x + SCREEN_X1 - 6, y + 107, 0xFFD8D3C6);
        for (int i = 1; i < this.menu.slots.size(); i++) {
            Slot slot = this.menu.slots.get(i);
            drawSlotFrame(g, x + slot.x - 1, y + slot.y - 1);
        }
    }

    private void drawConfirm(GuiGraphics g, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        int cx = x + this.imageWidth / 2;
        //? if >=1.21.6 {

        g.nextStratum();
        //?} else {
        /*

        g.pose().pushPose();
        g.pose().translate(0.0, 0.0, 400.0);*/
        //?}
        g.fill(x + SCREEN_X0, y + SCREEN_Y0, x + SCREEN_X1, y + SCREEN_Y1, 0x99000000);
        int dx0 = x + SCREEN_X0 + 10;
        int dx1 = x + SCREEN_X1 - 10;
        SmartDispatchAppScreen.fillPixelRounded(g, dx0, y + DIALOG_Y0, dx1, y + DIALOG_Y1, PX, 0xFFFFFFFF);
        ItemStack car = this.menu.slots.get(0).getItem();
        g.renderItem(car, cx - 8, y + DIALOG_Y0 + 6);
        this.drawSmall(g, car.getHoverName(), cx, y + DIALOG_Y0 + 26, 0xFF202024);
        this.drawSmall(g, ModText.translatable("gui.mineturismo.massive_auto.confirm"), cx, y + DIALOG_Y0 + 38, 0xFF202024);
        List<net.minecraft.util.FormattedCharSequence> warn = this.font.split(
                ModText.translatable("gui.mineturismo.massive_auto.irreversible"), Math.round((dx1 - dx0 - 8) / SMALL));
        for (int i = 0; i < Math.min(2, warn.size()); i++) {
            int w = Math.round(this.font.width(warn.get(i)) * SMALL);
            //? if >=1.21.6 {
            g.pose().pushMatrix();
            g.pose().scale(SMALL, SMALL);
            //?} else {
            /*g.pose().pushPose();
            g.pose().scale(SMALL, SMALL, 1.0f);*/
            //?}
            g.drawString(this.font, warn.get(i), Math.round((cx - w / 2f) / SMALL), Math.round((y + DIALOG_Y0 + 47 + i * 8) / SMALL), 0xFFC62828, false);
            //? if >=1.21.6 {
            g.pose().popMatrix();
            //?} else
            /*g.pose().popPose();*/
        }
        int bw = (dx1 - dx0 - 18) / 2;
        int yesX = dx0 + 6;
        int noX = yesX + bw + 6;
        int by = y + DIALOG_BUTTON_Y;
        boolean yesHover = inside(mouseX, mouseY, yesX, by, yesX + bw, by + 14);
        boolean noHover = inside(mouseX, mouseY, noX, by, noX + bw, by + 14);
        SmartDispatchAppScreen.fillPixelRounded(g, yesX, by, yesX + bw, by + 14, 1, yesHover ? COLOR_BLUE_HOVER : COLOR_BLUE);
        SmartDispatchAppScreen.fillPixelRounded(g, noX, by, noX + bw, by + 14, 1, noHover ? 0xFFC8CAD2 : 0xFFB4B6BE);
        String yes = ModText.translatable("gui.yes").getString();
        String no = ModText.translatable("gui.no").getString();
        g.drawString(this.font, yes, yesX + (bw - this.font.width(yes)) / 2, by + 3, 0xFFFFFFFF, false);
        g.drawString(this.font, no, noX + (bw - this.font.width(no)) / 2, by + 3, 0xFF202024, false);
        //? if <1.21.6 {
        /*g.pose().popPose();*/
        //?}
    }

    //? if >=1.20 && <26 {
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        this.drawPhone(guiGraphics, mouseX, mouseY);
    }
    //?}
    //? if <1.20 {
    /*@Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        this.drawPhone(new GuiGraphics(poseStack), mouseX, mouseY);
    }*/
    //?}

    //? if >=26 {
    /*@Override
    public void extractBackground(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(extractor, mouseX, mouseY, partialTick);
        this.drawPhone(new GuiGraphics(extractor), mouseX, mouseY);
    }

    @Override
    protected void extractLabels(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
    }

    @Override
    protected void extractTooltip(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        if (!this.confirming) {
            super.extractTooltip(extractor, mouseX, mouseY);
        }
    }

    @Override
    public void extractRenderState(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(extractor, mouseX, mouseY, partialTick);
        if (this.confirming) {
            this.drawConfirm(new GuiGraphics(extractor), mouseX, mouseY);
        }
    }*/
    //?}

    //? if >=1.20 && <26 {
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }
    //?}

    //? if >=1.20 && <26 {
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (this.confirming) {
            this.drawConfirm(guiGraphics, mouseX, mouseY);
        } else {
            this.renderTooltip(guiGraphics, mouseX, mouseY);
        }
    }
    //?}

    //? if <1.20 {
    /*@Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
        if (this.confirming) {
            this.drawConfirm(new GuiGraphics(poseStack), mouseX, mouseY);
        } else {
            this.renderTooltip(poseStack, mouseX, mouseY);
        }
    }*/
    //?}

    private boolean handleClick(double mouseX, double mouseY, int button) {
        int x = this.leftPos;
        int y = this.topPos;
        if (this.confirming) {
            int dx0 = x + SCREEN_X0 + 10;
            int dx1 = x + SCREEN_X1 - 10;
            int bw = (dx1 - dx0 - 18) / 2;
            int yesX = dx0 + 6;
            int by = y + DIALOG_BUTTON_Y;
            if (button == 0 && inside(mouseX, mouseY, yesX, by, yesX + bw, by + 14) && this.menu.hasCarToSell()) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, MassiveAutoAppraisalMenu.SELL_BUTTON_ID);
            }

            this.confirming = false;
            return true;
        }
        if (button != 0) {
            return false;
        }
        if (inside(mouseX, mouseY, x + SELL_BUTTON_X0, y + SELL_BUTTON_Y, x + SELL_BUTTON_X1, y + SELL_BUTTON_Y + SELL_BUTTON_HEIGHT)) {
            if (this.menu.hasCarToSell()) {
                this.confirming = true;
            }
            return true;
        }
        int[] home = this.homeRect();
        if (inside(mouseX, mouseY, home[0] - 2, home[1] - 2, home[2] + 2, home[3] + 2)) {
            this.onClose();
            this.minecraft.setScreen(new SmartDispatchAppScreen());
            return true;
        }
        return false;
    }

    @Override
    //? if >=1.21.9 {
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return this.handleClick(event.x(), event.y(), event.button()) || super.mouseClicked(event, doubleClick);
    }
    //?} else {
    /*public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.handleClick(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }*/
    //?}

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
