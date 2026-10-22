package com.examplebett.render;

import java.util.List;

public record LiveryOptions(List<String> ids, List<String> labels, List<String> decalIds) {

    public static LiveryOptions of(List<String> ids, List<String> labels, List<String> decalIds) {
        return new LiveryOptions(ids, (labels != null && labels.size() == ids.size()) ? labels : ids,
                decalIds != null ? decalIds : List.of());
    }

    public boolean isDecal(String liveryId) {
        return decalIds.contains(liveryId);
    }
}
