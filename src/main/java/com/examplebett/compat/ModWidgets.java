//? if >=1.18 {
//?}
//? if <1.18 && >=1.16 {
/*
package com.examplebett.compat;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public final class ModWidgets {
	private ModWidgets() {
	}

	public static Button button(Component label, int x, int y, int width, int height, Button.OnPress onPress) {
		return new Button(x, y, width, height, label, onPress);
	}

	public static void setY(net.minecraft.client.gui.components.AbstractWidget widget, int y) {
		widget.y = y;
	}

	public static int getX(net.minecraft.client.gui.components.AbstractWidget widget) {
		return widget.x;
	}

	public static int getY(net.minecraft.client.gui.components.AbstractWidget widget) {
		return widget.y;
	}

	public static void setHint(net.minecraft.client.gui.components.EditBox editBox, Component hint) {
		editBox.setSuggestion(hint.getString());
	}
}
*/
//?}
//? if <1.16 && >=1.15 {
/*
package com.examplebett.compat;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public final class ModWidgets {
    private ModWidgets() {
    }

    public static Button button(Component label, int x, int y, int width, int height, Button.OnPress onPress) {
        return new Button(x, y, width, height, label.getString(), onPress);
    }

    public static void setY(net.minecraft.client.gui.components.AbstractWidget widget, int y) {
        widget.y = y;
    }

    public static int getX(net.minecraft.client.gui.components.AbstractWidget widget) {
        return widget.x;
    }

    public static int getY(net.minecraft.client.gui.components.AbstractWidget widget) {
        return widget.y;
    }

    public static void setHint(net.minecraft.client.gui.components.EditBox editBox, Component hint) {
        editBox.setSuggestion(hint.getString());
    }
}
*/
//?}
//? if <1.15 {
/*
package com.examplebett.compat;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public final class ModWidgets {
    private ModWidgets() {
    }

    public static Button button(Component label, int x, int y, int width, int height, Button.OnPress onPress) {
        return new Button(x, y, width, height, label.getString(), onPress);
    }

    public static void setY(net.minecraft.client.gui.components.AbstractWidget widget, int y) {
        widget.y = y;
    }

    public static int getX(net.minecraft.client.gui.components.AbstractWidget widget) {
        return widget.x;
    }

    public static int getY(net.minecraft.client.gui.components.AbstractWidget widget) {
        return widget.y;
    }

    public static void setHint(net.minecraft.client.gui.components.EditBox editBox, Component hint) {
        editBox.setSuggestion(hint.getString());
    }
}
*/
//?}
