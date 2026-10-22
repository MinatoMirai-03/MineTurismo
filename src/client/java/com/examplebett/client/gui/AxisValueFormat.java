package com.examplebett.client.gui;

public final class AxisValueFormat {

    private AxisValueFormat() {
    }

    public static String format(double value) {
        String text = String.format("%.3f", value);
        return text.endsWith("0") ? text.substring(0, text.length() - 1) : text;
    }
}
