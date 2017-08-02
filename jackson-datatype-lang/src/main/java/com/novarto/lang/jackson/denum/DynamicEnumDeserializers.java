package com.novarto.lang.jackson.denum;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.novarto.lang.denum.DynamicEnum;

/**
 * Created by fmap on 23.06.16.
 */
public class DynamicEnumDeserializers extends Deserializers.Base
{
    @Override
    public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc)
            throws JsonMappingException
    {
        Class<?> raw = type.getRawClass();
        if (DynamicEnum.class.isAssignableFrom(raw))
        {
            return new DynamicEnumDeserializer(type);
        }

        return super.findBeanDeserializer(type, config, beanDesc);
    }
}
