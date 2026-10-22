package com.examplebett.compat;

import java.lang.reflect.Field;

public final class ModReflection {

    public static Field findField(Class<?> owner, String devName, String intermediaryName)
            throws NoSuchFieldException {
        Field field;
        try {
            field = owner.getDeclaredField(devName);
        } catch (NoSuchFieldException devMissing) {
            field = owner.getDeclaredField(intermediaryName);
        }
        field.setAccessible(true);
        return field;
    }

    private ModReflection() {
    }
}
