package com.novarto.lang.effect;

import fj.F;
import fj.P1;
import fj.data.Validation;

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
}
