package com.examplebett.compat;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public final class ModWidgets {
	private ModWidgets() {
	}

	public static Button button(Component label, int x, int y, int width, int height, Button.OnPress onPress) {
		//? if >=1.19.3 {
		return Button.builder(label, onPress).bounds(x, y, width, height).build();
		//?} else
		/*return new Button(x, y, width, height, label, onPress);*/
	}

	public static void setY(net.minecraft.client.gui.components.AbstractWidget widget, int y) {
		//? if >=1.19.3 {
		widget.setY(y);
		//?} else
		/*widget.y = y;*/
	}

	public static int getX(net.minecraft.client.gui.components.AbstractWidget widget) {
		//? if >=1.19.3 {
		return widget.getX();
		//?} else
		/*return widget.x;*/
	}

	public static int getY(net.minecraft.client.gui.components.AbstractWidget widget) {
		//? if >=1.19.3 {
		return widget.getY();
		//?} else
		/*return widget.y;*/
	}

	public static void setHint(net.minecraft.client.gui.components.EditBox editBox, Component hint) {
		//? if >=1.19.3 {
		editBox.setHint(hint);
		//?} else
		/*editBox.setSuggestion(hint.getString());*/
	}
}
