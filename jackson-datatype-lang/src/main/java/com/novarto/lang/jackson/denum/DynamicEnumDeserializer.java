package com.novarto.lang.jackson.denum;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.novarto.lang.denum.DynamicEnum;
import com.novarto.lang.denum.DynamicEnumFactory;

import java.io.IOException;

public class DynamicEnumDeserializer extends StdDeserializer<DynamicEnum<?>>
{

    private static final long serialVersionUID = 1L;


    protected DynamicEnumDeserializer(JavaType valueType)
    {
        super(valueType);

    }

    @Override
    @SuppressWarnings("unchecked")
    public DynamicEnum<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException
    {
        String name = jp.getText();
        if (name == null)
        {
            throw new JsonParseException(jp, "name text is null");
        }

        DynamicEnumFactory<?> factory = DynamicEnumFactory.unsafeFindFactory((Class<DynamicEnum>) handledType());

        return factory.byName(name);
    }
}
