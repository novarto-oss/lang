package com.novarto.lang.effect;

import fj.F;
import fj.F0;
import fj.P1;
import fj.data.Validation;

import static fj.P.lazy;
import static fj.data.Validation.fail;
import static fj.data.Validation.success;

public class Effects
{
    public static <A, B, E> P1<Validation<E, B>> map(P1<Validation<E, A>> program, F<A, B> f)
    {
        return program.map(v -> v.map(f));
    }

    public static <A, B, E> P1<Validation<E, B>> bind(P1<Validation<E, A>> program, F<A, P1<Validation<E, B>>> f)
    {
        return program.map(v -> v.bind(x -> f.f(x).f()));
    }

    public static <A, E> P1<Validation<E, A>> pure(A a)
    {
        return lazy(() -> success(a));
    }

    public static <A, E> P1<Validation<E, A>> pure(F0<A> a)
    {
        return lazy(a).map(Validation::success);
    }

    public static <A, E> P1<Validation<E, A>> error(E e)
    {
        return lazy(() -> fail(e));
    }

    public static <A, E> P1<Validation<E, A>> error(F0<E> e)
    {
        return lazy(e).map(Validation::fail);
    }
}
