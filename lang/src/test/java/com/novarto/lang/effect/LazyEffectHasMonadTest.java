package com.novarto.lang.effect;

import fj.Equal;
import fj.F;
import fj.P1;
import fj.data.Validation;
import fj.test.Arbitrary;
import fj.test.Cogen;
import fj.test.Gen;
import fj.test.Property;
import fj.test.runner.PropertyTestRunner;
import org.junit.runner.RunWith;

import static com.novarto.lang.effect.LazyEffect.error;
import static com.novarto.lang.effect.LazyEffect.pure;
import static fj.Equal.stringEqual;
import static fj.test.Arbitrary.*;
import static fj.test.Cogen.cogenInteger;
import static fj.test.Property.prop;
import static fj.test.Property.property;

@RunWith(PropertyTestRunner.class)
public class LazyEffectHasMonadTest
{

    static final Equal<LazyEffect<String, Integer>> EQ = leEqual(Equal.intEqual, stringEqual);

    public Property leftIdentity()
    {


        // unit(a).bind(f) = f(a)
        return property(arbInteger, arbFun(cogenInteger, arbInteger, arbString),
                (Integer x, F<Integer, LazyEffect<String, Integer>> f) -> prop(EQ.eq(
                        LazyEffect.<String, Integer>pure(x).bind(f),
                        f.f(x)
                )));
    }

    public Property rightIdentity()
    {

        // m.bind(x -> unit(x)) = m
        return property(arbLazyEffect(arbInteger, arbString), m -> prop(EQ.eq(
                m.bind(x -> pure(x)),
                m
        )));
    }

    public Property associativity()
    {
        return property(
                arbFun(cogenInteger, arbInteger, arbString), arbFun(cogenInteger, arbInteger, arbString),
                arbLazyEffect(arbInteger, arbString),


                (f, g, m) -> prop(EQ.eq(
                        m.bind(f).bind(g),
                        m.bind(x -> f.f(x).bind(g))
                ))
        );
    }



    private static <E, A, B> Gen<F<A, LazyEffect<E, B>>> arbFun(Cogen<A> aCogen, Gen<B> bGen, Gen<E> eGen)
    {

        return Arbitrary.arbF(aCogen, arbLazyEffect(bGen, eGen));
    }


    private static <E, A> Gen<LazyEffect<E, A>> arbLazyEffect(Gen<A> aGen, Gen<E> eGen)
    {
        return arbBoolean.bind(ok -> ok ? arbOkEffect(aGen) : arbFailedEffect(eGen));
    }

    private static <E, A> Gen<LazyEffect<E, A>> arbOkEffect(Gen<A> gen)
    {
        return gen.map(x -> pure(x));
    }

    private static <E, A> Gen<LazyEffect<E, A>> arbFailedEffect(Gen<E> gen)
    {
        return gen.map(x -> error(x));
    }

    private static <E, A> Equal<LazyEffect<E, A>> leEqual(Equal<A> aEq, Equal<E> eEq)
    {
        Equal<P1<Validation<E, A>>> pEq = Equal.p1Equal(Equal.validationEqual(eEq, aEq));
        return pEq.contramap(x -> x.p);
    }


}
