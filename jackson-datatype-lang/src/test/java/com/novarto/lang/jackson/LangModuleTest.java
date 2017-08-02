package com.novarto.lang.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novarto.lang.denum.DynamicEnum;
import com.novarto.lang.denum.DynamicEnumFactory;
import fj.Equal;
import fj.test.Arbitrary;
import fj.test.Gen;
import fj.test.Property;
import fj.test.runner.PropertyTestRunner;
import org.junit.runner.RunWith;

import static com.novarto.lang.testutil.TestUtil.tryTo;
import static fj.data.List.list;
import static fj.test.Property.prop;
import static fj.test.Property.property;

@RunWith(PropertyTestRunner.class)
public class LangModuleTest
{
    public static final ObjectMapper MAPPER = new ObjectMapper();
    static
    {
        MAPPER.registerModule(new LangModule());
    }


    public Property canDeserializeDynamicEnum()
    {
        TypeReference<Color> type = new TypeReference<Color>()
        {
        };

        return property(genDynamicEnum(Color.F), x -> serializeDeserialize(x, type, Equal.anyEqual()));
    }

    public Property canDeserializeListOfDynamicEnum()
    {
        TypeReference<java.util.List<Color>> type = new TypeReference<java.util.List<Color>>()
        {
        };

        Gen<java.util.List<Color>> enumListGen = Arbitrary.arbList(genDynamicEnum(Color.F)).map(xs -> xs.toJavaList());


        return property(enumListGen, xs -> serializeDeserialize(xs, type, Equal.anyEqual()));

    }




    private static <A> Property serializeDeserialize(A in, TypeReference<A> type, Equal<A> equal)
    {
        boolean success = tryTo(() -> {

            String json = MAPPER.writeValueAsString(in);
            A out = MAPPER.readValue(json, type);

            boolean isEqual = equal.eq(in, out);
            return isEqual;
        });

        return prop(success);
    }

    private static <A extends DynamicEnum<A>> Gen<A> genDynamicEnum(DynamicEnumFactory<A> factory)
    {
        return Gen.elements(factory.values());
    }


    public static final class Color extends DynamicEnum<Color>
    {
        private Color(int id, String name)
        {
            super(id, name);
        }

        private static final DynamicEnumFactory<Color> F = new DynamicEnumFactory<>(Color.class,
                () -> list(new Color(1, "Green"), new Color(2, "Red"), new Color(3, "Blue")));

        public static final Color GREEN = F.byId(1);
        public static final Color RED = F.byId(2);
        public static final Color BLUE = F.byId(3);


        public static Color[] values()
        {
            return F.values();
        }

    }


}
