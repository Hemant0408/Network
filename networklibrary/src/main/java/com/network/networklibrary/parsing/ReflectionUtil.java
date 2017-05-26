package com.network.networklibrary.parsing;

import java.lang.reflect.Field;

/**
 * Created by aksha_000 on 4/22/2016.
 */
public final class ReflectionUtil {

    private ReflectionUtil() {
    }

    public static Object getFieldValue(Object target, Field field) {
        try {
            boolean originalFlag = changeAccessibleFlag(field);
            Object fieldValue = field.get(target);
            restoreAccessibleFlag(field, originalFlag);
            return fieldValue;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field " + field.getDeclaringClass().getName() + "/"
                    + field.getName(), e);
        }
    }

    private static void restoreAccessibleFlag(Field field, boolean flag) {
        field.setAccessible(flag);
    }

    private static boolean changeAccessibleFlag(Field field) {
        boolean flag = field.isAccessible();
        field.setAccessible(true);
        return flag;
    }
}
