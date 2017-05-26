package com.network.networklibrary.parsing;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AnnotatedDeserializer<T> implements JsonDeserializer<T> {

    public T deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        T target = new Gson().fromJson(je, type);
        checkRequired(target);
        return target;
    }

    private void checkRequired(Object target) {
        List<Field> invalidFields = new ArrayList<>();
        findMissingFields(target, invalidFields);

        if (!invalidFields.isEmpty()) {
            String message = new String("Missing fields: {");

            for (Field f : invalidFields)
                message = message.concat(f.getDeclaringClass().getSimpleName() + "/" + f.getName() + ", ");

            message = message.substring(0, message.length() - 2);
            message = message.concat("}");

            throw new JsonParseException(message);
        }
    }

    private List<Field> findMissingFields(Object target, List<Field> invalidFields) {
        for (Field field : target.getClass().getDeclaredFields()) {
            boolean required = field.getAnnotation(Required.class) != null;
            boolean optional = field.getAnnotation(Optional.class) != null;

            if (required || optional) {
                Object fieldValue = ReflectionUtil.getFieldValue(target, field);

                if (required && (fieldValue == null || (String.class.equals(field.getClass()) && ((String) fieldValue).isEmpty())))
                    invalidFields.add(field);

                if (fieldValue != null && fieldValue instanceof Collection)
                    for (Object item : (Collection) fieldValue)
                        findMissingFields(item, invalidFields);
                else if (fieldValue != null && !fieldValue.getClass().isPrimitive())
                    findMissingFields(fieldValue, invalidFields);
            }
        }

        return invalidFields;
    }
}
