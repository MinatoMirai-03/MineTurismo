//? if >=1.18 {
//?}
//? if <1.18 && >=1.17 {
/*
package com.examplebett.compat;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import com.mojang.blaze3d.vertex.PoseStack;

public final class ModCheckbox extends AbstractButton {
	public interface OnValueChange {
		void onValueChange(ModCheckbox checkbox, boolean value);
	}

	private static final ResourceLocation SELECTED_HIGHLIGHTED_SPRITE =
			new ResourceLocation("widget/checkbox_selected_highlighted");
	private static final ResourceLocation SELECTED_SPRITE =
			new ResourceLocation("widget/checkbox_selected");
	private static final ResourceLocation HIGHLIGHTED_SPRITE =
			new ResourceLocation("widget/checkbox_highlighted");
	private static final ResourceLocation SPRITE =
			new ResourceLocation("widget/checkbox");

	private static final int TEXT_COLOR = 0xFFE0E0E0;
	private static final int SPACING = 4;

	private final Font font;
	private boolean selected;
	private final OnValueChange onValueChange;

	private ModCheckbox(int x, int y, int width, int height, Font font, Component label,
	                     boolean selected, OnValueChange onValueChange) {
		super(x, y, width, height, label);
		this.font = font;
		this.selected = selected;
		this.onValueChange = onValueChange;
	}

	public boolean selected() {
		return this.selected;
	}

	@Override

	public void onPress() {
		this.selected = !this.selected;
		this.onValueChange.onValueChange(this, this.selected);
	}

	@Override

	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		mineturismo$renderCheckbox(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
	}

	private int mineturismo$x() {
		return this.x;
	}

	private int mineturismo$y() {
		return this.y;
	}

	private void mineturismo$renderCheckbox(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		int boxSize = mineturismo$boxSize(this.font);
		ResourceLocation sprite;
		if (this.selected) {
			sprite = this.isFocused() ? SELECTED_HIGHLIGHTED_SPRITE : SELECTED_SPRITE;
		} else {
			sprite = this.isFocused() ? HIGHLIGHTED_SPRITE : SPRITE;
		}
		int alpha8 = (int) (this.alpha * 255.0F);
		int color = FastColor.ARGB32.color(alpha8, 255, 255, 255);

		guiGraphics.fill(this.mineturismo$x(), this.mineturismo$y(), this.mineturismo$x() + boxSize, this.mineturismo$y() + boxSize, color);
		if (this.selected) {
			int inset = Math.max(2, boxSize / 4);
			int checkColor = FastColor.ARGB32.color(alpha8, 20, 20, 20);
			guiGraphics.fill(this.mineturismo$x() + inset, this.mineturismo$y() + inset,
					this.mineturismo$x() + boxSize - inset, this.mineturismo$y() + boxSize - inset, checkColor);
		}

		if (!this.getMessage().getString().isEmpty()) {
			int textX = this.mineturismo$x() + boxSize + SPACING;
			int textY = this.mineturismo$y() + boxSize / 2 - this.font.lineHeight / 2;
			guiGraphics.drawString(this.font, this.getMessage(), textX, textY, TEXT_COLOR);
		}
	}

	@Override
	public void updateNarration(NarrationElementOutput output) {
		this.defaultButtonNarrationText(output);
	}

	private static int mineturismo$boxSize(Font font) {
		return 17;
	}

	public static Builder builder(Component label, Font font) {
		return new Builder(label, font);
	}

	public static final class Builder {
		private final Component label;
		private final Font font;
		private int x;
		private int y;
		private boolean selected;
		private OnValueChange onValueChange = (checkbox, value) -> {
		};

		private Builder(Component label, Font font) {
			this.label = label;
			this.font = font;
		}

		public Builder pos(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public Builder selected(boolean selected) {
			this.selected = selected;
			return this;
		}

		public Builder onValueChange(OnValueChange onValueChange) {
			this.onValueChange = onValueChange;
			return this;
		}

		public ModCheckbox build() {
			int boxSize = mineturismo$boxSize(this.font);
			int width = boxSize + (this.label.getString().isEmpty() ? 0 : SPACING + this.font.width(this.label));
			return new ModCheckbox(this.x, this.y, width, boxSize, this.font, this.label, this.selected, this.onValueChange);
		}
	}
}
*/
//?}
//? if <1.17 && >=1.16 {
/*
package com.examplebett.compat;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import com.mojang.blaze3d.vertex.PoseStack;

public final class ModCheckbox extends AbstractButton {
	public interface OnValueChange {
		void onValueChange(ModCheckbox checkbox, boolean value);
	}

	private static final ResourceLocation SELECTED_HIGHLIGHTED_SPRITE =
			new ResourceLocation("widget/checkbox_selected_highlighted");
	private static final ResourceLocation SELECTED_SPRITE =
			new ResourceLocation("widget/checkbox_selected");
	private static final ResourceLocation HIGHLIGHTED_SPRITE =
			new ResourceLocation("widget/checkbox_highlighted");
	private static final ResourceLocation SPRITE =
			new ResourceLocation("widget/checkbox");

	private static final int TEXT_COLOR = 0xFFE0E0E0;
	private static final int SPACING = 4;

	private final Font font;
	private boolean selected;
	private final OnValueChange onValueChange;

	private ModCheckbox(int x, int y, int width, int height, Font font, Component label,
	                     boolean selected, OnValueChange onValueChange) {
		super(x, y, width, height, label);
		this.font = font;
		this.selected = selected;
		this.onValueChange = onValueChange;
	}

	public boolean selected() {
		return this.selected;
	}

	@Override

	public void onPress() {
		this.selected = !this.selected;
		this.onValueChange.onValueChange(this, this.selected);
	}

	@Override

	public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		mineturismo$renderCheckbox(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
	}

	private int mineturismo$x() {
		return this.x;
	}

	private int mineturismo$y() {
		return this.y;
	}

	private void mineturismo$renderCheckbox(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		int boxSize = mineturismo$boxSize(this.font);
		ResourceLocation sprite;
		if (this.selected) {
			sprite = this.isFocused() ? SELECTED_HIGHLIGHTED_SPRITE : SELECTED_SPRITE;
		} else {
			sprite = this.isFocused() ? HIGHLIGHTED_SPRITE : SPRITE;
		}
		int alpha8 = (int) (this.alpha * 255.0F);
		int color = FastColor.ARGB32.color(alpha8, 255, 255, 255);

		guiGraphics.fill(this.mineturismo$x(), this.mineturismo$y(), this.mineturismo$x() + boxSize, this.mineturismo$y() + boxSize, color);
		if (this.selected) {
			int inset = Math.max(2, boxSize / 4);
			int checkColor = FastColor.ARGB32.color(alpha8, 20, 20, 20);
			guiGraphics.fill(this.mineturismo$x() + inset, this.mineturismo$y() + inset,
					this.mineturismo$x() + boxSize - inset, this.mineturismo$y() + boxSize - inset, checkColor);
		}

		if (!this.getMessage().getString().isEmpty()) {
			int textX = this.mineturismo$x() + boxSize + SPACING;
			int textY = this.mineturismo$y() + boxSize / 2 - this.font.lineHeight / 2;
			guiGraphics.drawString(this.font, this.getMessage(), textX, textY, TEXT_COLOR);
		}
	}

	private static int mineturismo$boxSize(Font font) {
		return 17;
	}

	public static Builder builder(Component label, Font font) {
		return new Builder(label, font);
	}

	public static final class Builder {
		private final Component label;
		private final Font font;
		private int x;
		private int y;
		private boolean selected;
		private OnValueChange onValueChange = (checkbox, value) -> {
		};

		private Builder(Component label, Font font) {
			this.label = label;
			this.font = font;
		}

		public Builder pos(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public Builder selected(boolean selected) {
			this.selected = selected;
			return this;
		}

		public Builder onValueChange(OnValueChange onValueChange) {
			this.onValueChange = onValueChange;
			return this;
		}

		public ModCheckbox build() {
			int boxSize = mineturismo$boxSize(this.font);
			int width = boxSize + (this.label.getString().isEmpty() ? 0 : SPACING + this.font.width(this.label));
			return new ModCheckbox(this.x, this.y, width, boxSize, this.font, this.label, this.selected, this.onValueChange);
		}
	}
}
*/
//?}
//? if <1.16 && >=1.15 {
/*
package com.examplebett.compat;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import com.mojang.blaze3d.vertex.PoseStack;

public final class ModCheckbox extends AbstractButton {
	public interface OnValueChange {
		void onValueChange(ModCheckbox checkbox, boolean value);
	}

	private static final ResourceLocation SELECTED_HIGHLIGHTED_SPRITE =
			new ResourceLocation("widget/checkbox_selected_highlighted");
	private static final ResourceLocation SELECTED_SPRITE =
			new ResourceLocation("widget/checkbox_selected");
	private static final ResourceLocation HIGHLIGHTED_SPRITE =
			new ResourceLocation("widget/checkbox_highlighted");
	private static final ResourceLocation SPRITE =
			new ResourceLocation("widget/checkbox");

	private static final int TEXT_COLOR = 0xFFE0E0E0;
	private static final int SPACING = 4;

	private final Font font;
	private boolean selected;
	private final OnValueChange onValueChange;

	private ModCheckbox(int x, int y, int width, int height, Font font, Component label,
	                     boolean selected, OnValueChange onValueChange) {
		super(x, y, width, height, com.examplebett.compat.LegacyGui.text(label));
		this.font = font;
		this.selected = selected;
		this.onValueChange = onValueChange;
	}

	public boolean selected() {
		return this.selected;
	}

	@Override

	public void onPress() {
		this.selected = !this.selected;
		this.onValueChange.onValueChange(this, this.selected);
	}

	@Override

	public void renderButton(int mouseX, int mouseY, float partialTick) {
		mineturismo$renderCheckbox(new GuiGraphics(null), mouseX, mouseY, partialTick);
	}

	private int mineturismo$x() {
		return this.x;
	}

	private int mineturismo$y() {
		return this.y;
	}

	private void mineturismo$renderCheckbox(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		int boxSize = mineturismo$boxSize(this.font);
		ResourceLocation sprite;
		if (this.selected) {
			sprite = this.isFocused() ? SELECTED_HIGHLIGHTED_SPRITE : SELECTED_SPRITE;
		} else {
			sprite = this.isFocused() ? HIGHLIGHTED_SPRITE : SPRITE;
		}
		int alpha8 = (int) (this.alpha * 255.0F);
		int color = com.examplebett.compat.LegacyGui.argb(alpha8, 255, 255, 255);

		guiGraphics.fill(this.mineturismo$x(), this.mineturismo$y(), this.mineturismo$x() + boxSize, this.mineturismo$y() + boxSize, color);
		if (this.selected) {
			int inset = Math.max(2, boxSize / 4);
			int checkColor = com.examplebett.compat.LegacyGui.argb(alpha8, 20, 20, 20);
			guiGraphics.fill(this.mineturismo$x() + inset, this.mineturismo$y() + inset,
					this.mineturismo$x() + boxSize - inset, this.mineturismo$y() + boxSize - inset, checkColor);
		}

		if (!this.getMessage().isEmpty()) {
			int textX = this.mineturismo$x() + boxSize + SPACING;
			int textY = this.mineturismo$y() + boxSize / 2 - this.font.lineHeight / 2;
			guiGraphics.drawString(this.font, this.getMessage(), textX, textY, TEXT_COLOR);
		}
	}

	private static int mineturismo$boxSize(Font font) {
		return 17;
	}

	public static Builder builder(Component label, Font font) {
		return new Builder(label, font);
	}

	public static final class Builder {
		private final Component label;
		private final Font font;
		private int x;
		private int y;
		private boolean selected;
		private OnValueChange onValueChange = (checkbox, value) -> {
		};

		private Builder(Component label, Font font) {
			this.label = label;
			this.font = font;
		}

		public Builder pos(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public Builder selected(boolean selected) {
			this.selected = selected;
			return this;
		}

		public Builder onValueChange(OnValueChange onValueChange) {
			this.onValueChange = onValueChange;
			return this;
		}

		public ModCheckbox build() {
			int boxSize = mineturismo$boxSize(this.font);
			int width = boxSize + (this.label.getString().isEmpty() ? 0 : SPACING + com.examplebett.compat.LegacyGui.width(this.font, this.label));
			return new ModCheckbox(this.x, this.y, width, boxSize, this.font, this.label, this.selected, this.onValueChange);
		}
	}
}
*/
//?}
//? if <1.15 {
/*
package com.examplebett.compat;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import com.examplebett.compat.v114.PoseStack;

public final class ModCheckbox extends AbstractButton {
	public interface OnValueChange {
		void onValueChange(ModCheckbox checkbox, boolean value);
	}

	private static final ResourceLocation SELECTED_HIGHLIGHTED_SPRITE =
			new ResourceLocation("widget/checkbox_selected_highlighted");
	private static final ResourceLocation SELECTED_SPRITE =
			new ResourceLocation("widget/checkbox_selected");
	private static final ResourceLocation HIGHLIGHTED_SPRITE =
			new ResourceLocation("widget/checkbox_highlighted");
	private static final ResourceLocation SPRITE =
			new ResourceLocation("widget/checkbox");

	private static final int TEXT_COLOR = 0xFFE0E0E0;
	private static final int SPACING = 4;

	private final Font font;
	private boolean selected;
	private final OnValueChange onValueChange;

	private ModCheckbox(int x, int y, int width, int height, Font font, Component label,
	                     boolean selected, OnValueChange onValueChange) {
		super(x, y, width, height, com.examplebett.compat.LegacyGui.text(label));
		this.font = font;
		this.selected = selected;
		this.onValueChange = onValueChange;
	}

	public boolean selected() {
		return this.selected;
	}

	@Override

	public void onPress() {
		this.selected = !this.selected;
		this.onValueChange.onValueChange(this, this.selected);
	}

	@Override

	public void renderButton(int mouseX, int mouseY, float partialTick) {
		mineturismo$renderCheckbox(new GuiGraphics(null), mouseX, mouseY, partialTick);
	}

	private int mineturismo$x() {
		return this.x;
	}

	private int mineturismo$y() {
		return this.y;
	}

	private void mineturismo$renderCheckbox(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		int boxSize = mineturismo$boxSize(this.font);
		ResourceLocation sprite;
		if (this.selected) {
			sprite = this.isFocused() ? SELECTED_HIGHLIGHTED_SPRITE : SELECTED_SPRITE;
		} else {
			sprite = this.isFocused() ? HIGHLIGHTED_SPRITE : SPRITE;
		}
		int alpha8 = (int) (this.alpha * 255.0F);
		int color = com.examplebett.compat.LegacyGui.argb(alpha8, 255, 255, 255);

		guiGraphics.fill(this.mineturismo$x(), this.mineturismo$y(), this.mineturismo$x() + boxSize, this.mineturismo$y() + boxSize, color);
		if (this.selected) {
			int inset = Math.max(2, boxSize / 4);
			int checkColor = com.examplebett.compat.LegacyGui.argb(alpha8, 20, 20, 20);
			guiGraphics.fill(this.mineturismo$x() + inset, this.mineturismo$y() + inset,
					this.mineturismo$x() + boxSize - inset, this.mineturismo$y() + boxSize - inset, checkColor);
		}

		if (!this.getMessage().isEmpty()) {
			int textX = this.mineturismo$x() + boxSize + SPACING;
			int textY = this.mineturismo$y() + boxSize / 2 - this.font.lineHeight / 2;
			guiGraphics.drawString(this.font, this.getMessage(), textX, textY, TEXT_COLOR);
		}
	}

	private static int mineturismo$boxSize(Font font) {
		return 17;
	}

	public static Builder builder(Component label, Font font) {
		return new Builder(label, font);
	}

	public static final class Builder {
		private final Component label;
		private final Font font;
		private int x;
		private int y;
		private boolean selected;
		private OnValueChange onValueChange = (checkbox, value) -> {
		};

		private Builder(Component label, Font font) {
			this.label = label;
			this.font = font;
		}

		public Builder pos(int x, int y) {
			this.x = x;
			this.y = y;
			return this;
		}

		public Builder selected(boolean selected) {
			this.selected = selected;
			return this;
		}

		public Builder onValueChange(OnValueChange onValueChange) {
			this.onValueChange = onValueChange;
			return this;
		}

		public ModCheckbox build() {
			int boxSize = mineturismo$boxSize(this.font);
			int width = boxSize + (this.label.getString().isEmpty() ? 0 : SPACING + com.examplebett.compat.LegacyGui.width(this.font, this.label));
			return new ModCheckbox(this.x, this.y, width, boxSize, this.font, this.label, this.selected, this.onValueChange);
		}
	}
}
*/
//?}
