package com.novarto.lang.effect;

import fj.*;
import fj.data.Validation;
import fj.function.Try0;

import static fj.P.lazy;
import static fj.data.Validation.fail;
import static fj.data.Validation.success;

/**
 *
 * LazyEffect is a pure (referentially transparent) description of a side-effecting computation which can either succeed
 * with a successful result of type A, or fail with an error of type E.
 *
 * A LazyEffect can be transformed and composed via map and bind (a.k.a. flatMap). That is, LazyEffect is a monad.
 *
 * The effect described can be evaluated via run(), which returns a Validation[E, A]
 *
 * An impure (side-effecting, non referentially transparent) computation can be lifted to a pure LazyEffect
 * via LazyEffect.effect()
 *
 * A pure value can be lifted to a LazyEffect via pure() and error()
 *
 * LazyEffect is isomorphic to a P1[Validation[E, A]], and uses P1[Validation[E, A]] as an underlying representation.
 *
 * You can obtain a lazy effect's underlying representation via {@link LazyEffect#p}.
 * You can construct a lazy effect from a P1 via {@link LazyEffect#fromP1(P1)}.
 *
 * This isomorphism allows to cache the side effect of a lazy effect, by taking a lazy effect's underlying P1,
 * calling one of its memo() methods, and constructing a new lazy effect with the memoized P1.
 *
 *
 * @param <E> the error type
 * @param <A> the success type
 */
public final class LazyEffect<E, A>
{

    public final P1<Validation<E, A>> p;

    private LazyEffect(P1<Validation<E, A>> p)
    {
        this.p = p;
    }

    /**
     * Lift a pure value to a successful lazy effect
     */
    public static <E, A> LazyEffect<E, A> pure(A a)
    {
        return new LazyEffect<>(lazy(() -> success(a)));
    }

    /**
     * Lift a lazy pure value to a successful lazy effect
     */
    public static <E, A> LazyEffect<E, A> pure(F0<A> a)
    {
        return new LazyEffect<>(lazy(a).map(Validation::success));
    }

    /**
     * Lift a pure value to a failed lazy effect
     */
    public static <E, A> LazyEffect<E, A> error(E e)
    {
        return new LazyEffect<>(lazy(() -> fail(e)));
    }

    /**
     * Lift a lazy pure value to a failed lazy effect
     */
    public static <E, A> LazyEffect<E, A> error(F0<E> e)
    {
        return new LazyEffect<>(lazy(e).map(Validation::fail));
    }

    /**
     * Lift an impure computation to a lazy effect
     */
    public static <E extends Exception, A> LazyEffect<E, A> effect(Try0<A, E> effect)
    {
        return new LazyEffect<>(Try.f(effect));
    }

    /**
     * Constructs a LazyEffect from its underlying representation, P1<Validation<E, A>>
     */
    public static <E, A> LazyEffect<E, A> fromP1(P1<Validation<E, A>> p)
    {
        return new LazyEffect<>(p);
    }


    public <B> LazyEffect<E, B> map(F<A, B> f)
    {
        return new LazyEffect<>(p.map(v -> v.map(f)));
    }

    public <B> LazyEffect<E, B> bind(F<A, LazyEffect<E, B>> f)
    {
        return new LazyEffect<>(
                this.p.map(v -> v.bind(x -> f.f(x).p.f()))
        );
    }

    /**
     * Transform this lazy effect's error side via the given function
     */
    public <E1> LazyEffect<E1, A> mapError(F<E, E1> f)
    {
        return new LazyEffect<>(this.p.map(x -> Validation.validation(x.toEither().left().map(f))));
    }

    /**
     * Evaluate this lazy effect, and return its result/
     */
    public Validation<E, A> run()
    {
        return p.f();
    }



}
