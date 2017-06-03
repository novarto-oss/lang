package com.novarto.lang;

import fj.*;
import fj.data.*;
import fj.function.Effect2;

import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static fj.Function.identity;
import static java.util.Arrays.asList;

/**
 * A set of collection-related utilities.
 */
public class Collections
{

    /**
     * Convert an iterable to a Map, accumulating multiple elements with the same key in a single value.
     * This is the most parametric version of this function, therefore the long signature. You may want to
     * use one of the convenience versions of this function, available below.
     *
     * @param xs the iterable to convert
     * @param getKey function to calculate the key for a given element in the iterable
     * @param getSingleVal the function to calculate a single value from a given element in the iterable
     * @param createMap The function to create an empty map
     * @param createVal The function to create an accumulator value
     * @param merge The function to merge the accumulated value so far with a single element from the iterable into
     *              a new accumulated value
     * @param <A> The type of elements in the iterable
     * @param <B> The type of the map keys
     * @param <C> The type of a single value to which the iterable element should be converted
     * @param <D> The type of a value in the map, which accumulates multiple iterable elements
     *
     * @param <E> The type of the result, for example HashMap<Foo,List<Bar>>
     * @return the map, with the values grouped by key and accumulated
     */
    public static <A, B, C, D, E extends Map<B, D>> E toMap(Iterable<A> xs, F<A, B> getKey, F<A, C> getSingleVal, F0<E> createMap,
            F<C, D> createVal, F2<D, C, D> merge)
    {
        E res = createMap.f();

        for (A x : xs)
        {
            B key = getKey.f(x);
            C val = getSingleVal.f(x);

            D coll = res.get(key);
            if (coll == null)
            {
                coll = createVal.f(val);
            }
            else
            {
                coll = merge.f(coll, val);
            }

            res.put(key, coll);
        }

        return res;
    }

    /**
     * Convert an iterable to a map, calculating keys using getKey, calculating single values using getSingleVal,
     * and accumulating values with the same key by adding them to the same List
     */
    public static <A, B, C> Map<B, List<C>> toMap(Iterable<A> xs, F<A, B> getKey, F<A, C> getSingleVal)
    {
        F2<List<C>, C, List<C>> merge = (ys, x) ->
        {
            ys.add(x);
            return ys;
        };

        return toMap(xs, getKey, getSingleVal, HashMap::new, val ->
        {
            List<C> result = new ArrayList<>();
            result.add(val);
            return result;
        }, merge);
    }

    /**
     * Given an iterable, group it by getKey
     */
    public static <A, B> Map<B, List<A>> groupBy(Iterable<A> xs, F<A, B> getKey)
    {
        return toMap(xs, getKey, identity());
    }

    /**
     * Given an iterable, group it by its unique keys, first transforming values using getVal,
     * and throw on duplicate keys
     */
    public static <A, B, C> Map<A, B> toMapUnique(fj.data.List<C> xs, F<C, A> getKey, F<C, B> getVal)
    {
        return toMap(xs, getKey, getVal, HashMap::new, x -> x, (x, y) ->
        {
            throw new IllegalStateException("duplicate elements");
        });
    }

    public static <K, V, C extends Iterable<V>> void putInMap(Map<K, C> map, K key, V value, F0<C> valueCollectionCreator,
            Effect2<C, V> add)
    {
        C entry = map.get(key);
        if (entry == null)
        {
            entry = valueCollectionCreator.f();
            map.put(key, entry);
        }

        add.f(entry, value);
    }

    public static <K, V, C extends Iterable<V>> void putInMap(fj.data.HashMap<K, C> map, K key, V value,
            F0<C> valueCollectionCreator, Effect2<C, V> add)
    {
        Option<C> entry = map.get(key);

        if (entry.isNone())
        {

            C xs = valueCollectionCreator.f();
            map.set(key, xs);
        }

        add.f(map.get(key).some(), value);

    }

    public static <K, V> void putInMap(Map<K, List<V>> map, K key, V value)
    {
        putInMap(map, key, value, ArrayList::new, (xs, x) -> xs.add(x));
    }

    /**
     * Generic function to return the set of duplicates in a collection, according to object equality
     */
    public static <A, C extends Iterable<A>, C1 extends Iterable<P2<A, Integer>>> fj.data.Set<A> duplicates(C xs,
            F<C, C1> zipIndex)
    {

        fj.data.HashSet<A> result = fj.data.HashSet.empty();
        C1 withIndex = zipIndex.f(xs);
        withIndex.forEach(x -> withIndex.forEach(y ->
        {
            if (x._2() != y._2() && x._1().equals(y._1()))
            {
                result.set(x._1());
            }
        }));

        @SuppressWarnings("deprecation") Ord<A> ord = Ord.hashEqualsOrd();

        return fj.data.Set.iterableSet(ord, result);
    }

    /**
     * Function to calculate duplicates according to object equality, specialized for fj List
     */
    public static <A> fj.data.Set<A> duplicates(fj.data.List<A> xs)
    {
        return duplicates(xs, ys -> ys.zipIndex());
    }

    /**
     * Function to calculate duplicates according to object equality, specialized for fj Stream
     */
    public static <A> fj.data.Set<A> duplicates(fj.data.Stream<A> xs)
    {
        return duplicates(xs, ys -> ys.zipIndex());
    }

    @SafeVarargs @SuppressWarnings("varargs")
    /**
     * Convenience function to create a j.u.Set from a variable number of items
     */
    public static <A> Set<A> javaSet(A... as)
    {
        return javaSet(asList(as));
    }

    /**
     * Convenience function to create a j.u.Set from an arbitrary iterable
     */
    public static <A> Set<A> javaSet(Iterable<A> as)
    {
        java.util.HashSet<A> result = new java.util.HashSet<>();
        for (A a : as)
        {
            result.add(a);
        }
        return result;
    }

    /**
     * Convenience function to create a j.u.Set from a j.u.Collection
     */
    public static <A> Set<A> javaSet(Collection<A> as)
    {
        java.util.HashSet<A> result = new java.util.HashSet<>();
        result.addAll(as);
        return result;
    }

    /**
     * Convert this iterable to a fj List. If the iterable already is a fj List, the same instance is returned.
     */
    public static <A> fj.data.List<A> toList(Iterable<A> xs)
    {
        if (xs instanceof fj.data.List)
        {
            return (fj.data.List<A>) xs;
        }
        else
        {
            return fj.data.List.iterableList(xs);
        }
    }

    /**
     * Returns the size of an arbitrary iterable. Tries to be efficient, if possible.
     */
    public static int size(Iterable<?> xs)
    {
        if (xs instanceof Collection)
        {
            return ((Collection) xs).size();
        }
        else if (xs instanceof fj.data.List)
        {
            return ((fj.data.List) xs).length();
        }
        else
        {
            int result = 0;
            for (@SuppressWarnings("unused") Object a : xs)
            {
                result++;
            }
            return result;
        }
    }

    /**
     * Determines whether an arbitrary iterable is empty. Tries to be efficient, if possible.
     */
    public static boolean isEmpty(Iterable<?> xs)
    {
        if (xs instanceof Collection)
        {
            return ((Collection) xs).isEmpty();
        }
        else if (xs instanceof fj.data.List)
        {
            return ((fj.data.List) xs).isEmpty();
        }
        else
        {
            return !xs.iterator().hasNext();
        }
    }

}