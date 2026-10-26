package com.examplebett.compat;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
//? if >=1.21.9 {
import net.minecraft.client.input.InputWithModifiers;
//?}
//? if >=1.21.2 {
import net.minecraft.util.ARGB;
//?} else
/*import net.minecraft.util.FastColor;*/

//? if >=1.20 && <26 {
import net.minecraft.client.gui.GuiGraphics;
//?}
//? if <1.20 {
/*import com.mojang.blaze3d.vertex.PoseStack;*/
//?}
//? if >=1.21.6 {
import net.minecraft.client.renderer.RenderPipelines;
//?}

public final class ModCheckbox extends AbstractButton {
	public interface OnValueChange {
		void onValueChange(ModCheckbox checkbox, boolean value);
	}

	//? if >=1.21 {
	private static final ResourceLocation SELECTED_HIGHLIGHTED_SPRITE =
			ResourceLocation.withDefaultNamespace("widget/checkbox_selected_highlighted");
	private static final ResourceLocation SELECTED_SPRITE =
			ResourceLocation.withDefaultNamespace("widget/checkbox_selected");
	private static final ResourceLocation HIGHLIGHTED_SPRITE =
			ResourceLocation.withDefaultNamespace("widget/checkbox_highlighted");
	private static final ResourceLocation SPRITE =
			ResourceLocation.withDefaultNamespace("widget/checkbox");
	//?} else {
	/*private static final ResourceLocation SELECTED_HIGHLIGHTED_SPRITE =
			new ResourceLocation("widget/checkbox_selected_highlighted");
	private static final ResourceLocation SELECTED_SPRITE =
			new ResourceLocation("widget/checkbox_selected");
	private static final ResourceLocation HIGHLIGHTED_SPRITE =
			new ResourceLocation("widget/checkbox_highlighted");
	private static final ResourceLocation SPRITE =
			new ResourceLocation("widget/checkbox");*/
	//?}

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

	//? if >=1.21.9 {
	public void onPress(InputWithModifiers inputWithModifiers) {
	//?} else
	/*public void onPress() {*/
		this.selected = !this.selected;
		this.onValueChange.onValueChange(this, this.selected);
	}

	@Override

	//? if >=26 {
	/*protected void extractContents(net.minecraft.client.gui.GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
		mineturismo$renderCheckbox(new GuiGraphics(extractor), mouseX, mouseY, partialTick);
	}*/
	//?}
	//? if >=1.21.11 && <26 {
	/*protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		mineturismo$renderCheckbox(guiGraphics, mouseX, mouseY, partialTick);
	}*/
	//?}
	//? if >=1.20 && <1.21.11 {
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		mineturismo$renderCheckbox(guiGraphics, mouseX, mouseY, partialTick);
	}
	//?}
	//? if >=1.19.4 && <1.20 {
	/*public void renderWidget(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		mineturismo$renderCheckbox(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
	}*/
	//?}
	//? if <1.19.4 {
	/*public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		mineturismo$renderCheckbox(new GuiGraphics(poseStack), mouseX, mouseY, partialTick);
	}*/
	//?}

	private int mineturismo$x() {
		//? if >=1.19.3 {
		return this.getX();
		//?} else
		/*return this.x;*/
	}

	private int mineturismo$y() {
		//? if >=1.19.3 {
		return this.getY();
		//?} else
		/*return this.y;*/
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
		//? if >=1.21.2 {
		int color = ARGB.color(alpha8, 255, 255, 255);
		//?} else
		/*int color = FastColor.ARGB32.color(alpha8, 255, 255, 255);*/

		//? if >=1.21.6 {
		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, this.mineturismo$x(), this.mineturismo$y(), boxSize, boxSize, color);
		//?}
		//? if >=1.21.2 && <1.21.6 {
		/*guiGraphics.blitSprite(net.minecraft.client.renderer.RenderType::guiTextured, sprite, this.mineturismo$x(), this.mineturismo$y(), boxSize, boxSize, color);*/
		//?}
		//? if >=1.20.2 && <1.21.2 {
		/*guiGraphics.blitSprite(sprite, this.mineturismo$x(), this.mineturismo$y(), boxSize, boxSize, color);*/
		//?}
		//? if <1.20.2 {
		/*guiGraphics.fill(this.mineturismo$x(), this.mineturismo$y(), this.mineturismo$x() + boxSize, this.mineturismo$y() + boxSize, color);
		if (this.selected) {
			int inset = Math.max(2, boxSize / 4);
			int checkColor = FastColor.ARGB32.color(alpha8, 20, 20, 20);
			guiGraphics.fill(this.mineturismo$x() + inset, this.mineturismo$y() + inset,
					this.mineturismo$x() + boxSize - inset, this.mineturismo$y() + boxSize - inset, checkColor);
		}*/
		//?}

		if (!this.getMessage().getString().isEmpty()) {
			int textX = this.mineturismo$x() + boxSize + SPACING;
			int textY = this.mineturismo$y() + boxSize / 2 - this.font.lineHeight / 2;
			guiGraphics.drawString(this.font, this.getMessage(), textX, textY, TEXT_COLOR);
		}
	}

	//? if >=1.19.3 {
	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		this.defaultButtonNarrationText(output);
	}
	//?} else {
	/*@Override
	public void updateNarration(NarrationElementOutput output) {
		this.defaultButtonNarrationText(output);
	}*/
	//?}

	private static int mineturismo$boxSize(Font font) {
		//? if >=1.20.5 {
		return Checkbox.getBoxSize(font);
		//?} else
		/*return 17;*/
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
