package com.novarto.lang;

import java.util.ArrayList;
import java.util.List;

/**
 *  A utility interface to abstract the process of building collections element by element,
 *  optionally supporting an intermediate (mutable) buffer type for performance.
 *  The concept is borrowed from the Scala standard collections library.
 *
 * @param <A> The type of the elements in the collection
 * @param <C1> The type of the intermediate collection. If your implementation does not use an intermediate buffer type,
 *            then let C1=C2
 * @param <C2> The final result type
 */
public interface CanBuildFrom<A, C1 extends Iterable<A>, C2 extends Iterable<A>>
{

    /**
       Create an empty intermediate buffer
     */
    C1 createBuffer();

    /**
     * Add a single element to the buffer
     */
    C1 add(A a, C1 buf);

    /**
     * Build the final result, given a buffer with all the necessary elements present.
     */
    C2 build(C1 buf);

    /**
     * A CanBuildFrom instance for java lists, using an array list. Since ArrayList is itself mutable, C1=C2=List
     */
    static <A> CanBuildFrom<A, List<A>, List<A>> listCanBuildFrom()
    {
        return new CanBuildFrom<A, List<A>, List<A>>()
        {
            @Override
            public List<A> createBuffer()
            {
                return new ArrayList<>();
            }

            @Override
            public List<A> add(A a, List<A> buf)
            {
                buf.add(a);
                return buf;
            }

            @Override
            public List<A> build(List<A> buf)
            {
                return buf;
            }
        };
    }

    /**
     * A CanBuildFrom instance for fj.data.List, using fj.data.List.Buffer as an intermediate type.
     * It returns an immutable view of the buffer. Safe and performant.
     */
    static <A> CanBuildFrom<A, fj.data.List.Buffer<A>, fj.data.List<A>> fjListCanBuildFrom()
    {
        return new CanBuildFrom<A, fj.data.List.Buffer<A>, fj.data.List<A>>()
        {
            @Override
            public fj.data.List.Buffer<A> createBuffer()
            {
                return fj.data.List.Buffer.empty();
            }

            @Override
            public fj.data.List.Buffer<A> add(A a, fj.data.List.Buffer<A> buf)
            {
                return buf.snoc(a);
            }

            @Override
            public fj.data.List<A> build(fj.data.List.Buffer<A> buf)
            {
                return buf.toList();
            }
        };
    }

}
