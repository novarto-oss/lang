package com.novarto.lang.denum;

/**
 * An enumeration-like type whose values are not known on compile-time; for example they are specified in a
 * configuration file. Together with DynamicEnumFactory it provides an interface equivalent to java
 * enumerations.
 */
public class DynamicEnum<A extends DynamicEnum<A>> implements Comparable<A>
{
    public final int id;
    public final String name;

    protected DynamicEnum(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    @Override
    public final boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        DynamicEnum<?> that = (DynamicEnum<?>) o;

        return id == that.id;

    }

    @Override
    public final int hashCode()
    {
        return id;
    }

    @Override
    public final String toString()
    {
        //compatibility with Java enum
        return name;
    }

    @Override
    public final int compareTo(A o)
    {
        return this.id - o.id;
    }

}
