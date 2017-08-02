package com.novarto.lang.jackson.denum;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.novarto.lang.denum.DynamicEnum;

/**
 * Created by fmap on 23.06.16.
 */
public class DynamicEnumSerializers extends Serializers.Base
{
    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc)
    {
        Class<?> raw = type.getRawClass();
        if (DynamicEnum.class.isAssignableFrom(raw))
        {
            return new DynamicEnumSerializer(type);
        }

        return super.findSerializer(config, type, beanDesc);
    }
}
