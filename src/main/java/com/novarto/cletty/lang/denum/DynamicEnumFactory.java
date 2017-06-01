package com.novarto.cletty.lang.denum;

import com.novarto.cletty.lang.Collections;
import fj.F0;
import fj.data.List;
import fj.data.Set;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.novarto.cletty.lang.Collections.duplicates;
import static java.text.MessageFormat.format;

/**
 * A module which, given a concrete DynamicEnum class, and a function which loads the list of all its values,
 * provides access to the individual instances by id and by name, and access to all instances as an array;
 * much like a java enum.
 *
 * The caller must take care that for a given DynamicEnum class, only one DynamicEnumFactory is instantiated JVM-wide.
 * The caller must take care that the instances for a given DynamicEnum have distinct names and id's.
 * If any of these conditions is not met, the instantiation of a DynamicEnumFactory will fail with an illegal state.
 *
 */
@SuppressWarnings("unchecked")
public class DynamicEnumFactory<A extends DynamicEnum<A>>
{
    private final Map<Integer, A> byId;
    private final Map<String, A> byName;
    private final A[] values;

    private static final ConcurrentHashMap<String, DynamicEnumFactory<?>> ALL_FACTORIES = new ConcurrentHashMap<>();
    private final String typeName;

    @SuppressWarnings("unchecked")
    /**
     * Construct a new DynamicEnumFactory, given the class of the dynamic enum, and a function to load all the enum values.
     * Only one instance of a DynamicEnumFactory must be instantiated JVM-wide for a given DynamicEnum class declaration.
     */
    public DynamicEnumFactory(Class<A> type, F0<List<A>> loadF)
    {

        if (ALL_FACTORIES.get(type.getCanonicalName()) != null)
        {
            throw new IllegalStateException(
                    "you must instantiate only one factory per dynamic enum type." + "Duplicate factory instantiated for " +
                            type);
        }
        List<A> allAsList = loadF.f();

        List<String> namesAsList = allAsList.map(x -> x.name);

        Set<A> duplicates = duplicates(allAsList);

        if (!duplicates.isEmpty())
        {
            throw new IllegalStateException(format("Duplicate enums of type {0} : {1}", type, duplicates));
        }

        Set<String> duplicateNames = duplicates(namesAsList);

        if (!duplicateNames.isEmpty())
        {
            throw new IllegalStateException(format("Duplicate enum names of type {0} : {1}", type, duplicates));
        }


        this.byId = Collections.toMapUnique(loadF.f(), x -> x.id, x -> x);
        this.byName = Collections.toMapUnique(loadF.f(), x -> x.name, x -> x);

        this.values = byId.values().toArray((A[]) Array.newInstance(type, byId.values().size()));

        ALL_FACTORIES.put(type.getName(), this);

        typeName = type.getName();
    }

    /**
     * Get the enum instance, given its int id
     */
    public A byId(int id)
    {
        A result = byId.get(id);
        checkExists(id, result);
        return byId.get(id);
    }

    /**
     * Get the enum instance, given its name
     * @param name
     */
    public A byName(String name)
    {
        A result = byName.get(name);
        checkExists(name, result);
        return result;
    }

    /**
     * Get all the enum instances for this enum class
     * @return
     */
    public A[] values()
    {
        return values;
    }


    @SuppressWarnings("unchecked")
    /**
     * Get the factory for the passed enum class. This method is unsafe and is not intended to be used by
     * application code. The method is useful for e.g. automatic json serializer / deserializer libraries.
     */
    public static <B extends DynamicEnum<B>> DynamicEnumFactory<B> unsafeFindFactory(Class<B> enumType)
    {

        DynamicEnumFactory<B> f = (DynamicEnumFactory<B>) ALL_FACTORIES.get(enumType.getName());
        if (f == null)
        {
            try
            {
                Class.forName(enumType.getName());
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            }
        }

        f = (DynamicEnumFactory<B>) ALL_FACTORIES.get(enumType.getName());

        if (f == null)
        {
            throw new IllegalStateException("no factory registered for " + enumType.getClass());

        }

        return f;

    }

    private void checkExists(String name, A result)
    {
        if (result == null)
        {
            throw new IllegalArgumentException(format("No value {0} for enum class {1}", name, typeName));
        }
    }

    private void checkExists(int id, A result)
    {
        if (result == null)
        {
            throw new IllegalArgumentException(format("No value with id {0} for enum class {1}", id, typeName));
        }
    }

}
