package com.novarto.lang.denum;

import org.junit.Assert;
import org.junit.Test;

import static fj.data.List.list;
import static org.junit.Assert.assertNotNull;


@SuppressWarnings({"unchecked", "rawtypes"})
public class DynamicEnumTest
{

    @Test
    public void testIt()
    {
        Assert.assertEquals(1, Color.GREEN.id);
        Assert.assertEquals("Red", Color.RED.name);
    }

    @Test(expected = IllegalStateException.class)
    public void duplicates()
    {
        new DynamicEnumFactory<>(DynamicEnum.class,
                () -> list(new DynamicEnum(0, "aa"), new DynamicEnum(1, "bb"), new DynamicEnum(1, "cc")));
    }

    @Test(expected = IllegalStateException.class)
    public void duplicateNames()
    {
        new DynamicEnumFactory<>(DynamicEnum.class,
                () -> list(new DynamicEnum(0, "aa"), new DynamicEnum(1, "bb"), new DynamicEnum(2, "aa")));
    }

    @Test(expected = IllegalStateException.class)
    public void noFactory()
    {
        DynamicEnumFactory.unsafeFindFactory(Whatever.class);
    }

    @Test
    public void notResolved() throws ClassNotFoundException
    {
        Class<DynamicEnum> type = (Class<DynamicEnum>) DynamicEnumTest.class.getClassLoader()
                .loadClass("com.novarto.lang.denum.DynamicEnumTest$Whatever2");

        DynamicEnumFactory<?> factory = DynamicEnumFactory.unsafeFindFactory(type);
        assertNotNull(factory);

        factory = DynamicEnumFactory.unsafeFindFactory(type);
        assertNotNull(factory);
    }


    public static final class Color extends DynamicEnum<Color>
    {
        private Color(int id, String name)
        {
            super(id, name);
        }

        private static final DynamicEnumFactory<Color> F = new DynamicEnumFactory<>(Color.class,
                () -> list(new Color(1, "Green"), new Color(2, "Red")));

        public static final Color GREEN = F.byId(1);
        public static final Color RED = F.byId(2);

        public static Color[] values()
        {
            return F.values();
        }

    }

    public static final class Whatever extends DynamicEnum<Whatever>
    {
        public Whatever(int id, String name)
        {
            super(id, name);
        }
    }

    public static final class Whatever2 extends DynamicEnum<Whatever2>
    {
        private static final DynamicEnumFactory<Whatever2> F = new DynamicEnumFactory<>(
                Whatever2.class, () -> list(new Whatever2(1, "ok")));


        private Whatever2(int id, String name)
        {
            super(id, name);
        }
    }
}
