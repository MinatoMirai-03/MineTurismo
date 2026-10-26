package com.examplebett.client.gui;

//? if >=26 {
/*import com.examplebett.compat.GuiGraphics;*/
//?}
//? if >=1.20 && <26 {
import net.minecraft.client.gui.GuiGraphics;
//?}
//? if <1.20 {
/*import com.examplebett.compat.GuiGraphics;*/
//?}

import java.util.function.IntConsumer;

final class DragScrollbar {

    static final int WIDTH = 6;
    private static final int MIN_THUMB_HEIGHT = 12;
    private static final int TRACK_COLOR = 0xFF1E1E1E;
    private static final int TRACK_BORDER_COLOR = 0xFF555555;
    private static final int THUMB_COLOR = 0xFF9A9A9A;
    private static final int THUMB_HOVER_COLOR = 0xFFD0D0D0;

    private final IntConsumer onScroll;
    private int x;
    private int y;
    private int height;
    private int visibleRows;
    private int totalRows;
    private boolean dragging;
    private double grabOffset;

    DragScrollbar(IntConsumer onScroll) {
        this.onScroll = onScroll;
    }

    void setBounds(int x, int y, int height, int visibleRows, int totalRows) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.visibleRows = visibleRows;
        this.totalRows = totalRows;
        this.dragging = false;
    }

    boolean isActive() {
        return this.totalRows > this.visibleRows && this.height > 0;
    }

    private int maxScroll() {
        return Math.max(0, this.totalRows - this.visibleRows);
    }

    private int thumbHeight() {
        return Math.max(MIN_THUMB_HEIGHT, this.height * this.visibleRows / Math.max(1, this.totalRows));
    }

    private int thumbTop(int scrollRow) {
        int travel = this.height - this.thumbHeight();
        return this.y + (this.maxScroll() == 0 ? 0 : Math.round((float) travel * scrollRow / this.maxScroll()));
    }

    void render(GuiGraphics g, int mouseX, int mouseY, int scrollRow) {
        if (!this.isActive()) {
            return;
        }
        g.fill(this.x - 1, this.y - 1, this.x + WIDTH + 1, this.y + this.height + 1, TRACK_BORDER_COLOR);
        g.fill(this.x, this.y, this.x + WIDTH, this.y + this.height, TRACK_COLOR);
        int top = this.thumbTop(scrollRow);
        boolean hover = this.dragging || (mouseX >= this.x && mouseX < this.x + WIDTH && mouseY >= top && mouseY < top + this.thumbHeight());
        g.fill(this.x, top, this.x + WIDTH, top + this.thumbHeight(), hover ? THUMB_HOVER_COLOR : THUMB_COLOR);
    }

    boolean mouseClicked(double mouseX, double mouseY, int scrollRow) {
        if (!this.isActive() || mouseX < this.x - 2 || mouseX >= this.x + WIDTH + 2
                || mouseY < this.y || mouseY >= this.y + this.height) {
            return false;
        }
        int top = this.thumbTop(scrollRow);
        if (mouseY >= top && mouseY < top + this.thumbHeight()) {
            this.grabOffset = mouseY - top;
        } else {
            this.grabOffset = this.thumbHeight() / 2.0;
            this.moveTo(mouseY);
        }
        this.dragging = true;
        return true;
    }

    boolean mouseDragged(double mouseY) {
        if (!this.dragging) {
            return false;
        }
        this.moveTo(mouseY);
        return true;
    }

    void mouseReleased() {
        this.dragging = false;
    }

    private void moveTo(double mouseY) {
        int travel = this.height - this.thumbHeight();
        double fraction = travel <= 0 ? 0 : (mouseY - this.grabOffset - this.y) / travel;
        fraction = Math.max(0, Math.min(1, fraction));
        this.onScroll.accept((int) Math.round(fraction * this.maxScroll()));
    }
}
