package com.examplebett.render;

import java.util.List;

public record WheelColorOptions(List<String> ids, List<String> labels) {

    public static WheelColorOptions of(List<String> ids, List<String> labels) {
        return new WheelColorOptions(ids, (labels != null && labels.size() == ids.size()) ? labels : ids);
    }
}
