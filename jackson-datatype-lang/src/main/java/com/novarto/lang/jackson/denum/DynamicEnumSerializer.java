package com.novarto.lang.jackson.denum;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.novarto.lang.denum.DynamicEnum;

import java.io.IOException;

public class DynamicEnumSerializer extends StdSerializer<DynamicEnum<?>>
{

    private static final long serialVersionUID = 1L;

    protected DynamicEnumSerializer(JavaType type)
    {
        super(type);
    }

    @Override
    public void serialize(DynamicEnum<?> value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException
    {

            jgen.writeString(value.name);
    }
}
