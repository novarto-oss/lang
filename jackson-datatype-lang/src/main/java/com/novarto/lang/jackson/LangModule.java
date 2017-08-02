package com.novarto.lang.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.novarto.lang.jackson.denum.DynamicEnumDeserializers;
import com.novarto.lang.jackson.denum.DynamicEnumSerializers;

public class LangModule extends SimpleModule
{
    private static final long serialVersionUID = 1L;

    @Override
    public void setupModule(SetupContext context)
    {
        super.setupModule(context);
        context.addDeserializers(new DynamicEnumDeserializers());
        context.addSerializers(new DynamicEnumSerializers());
    }
}
