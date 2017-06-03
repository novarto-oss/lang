package com.novarto.lang.guava;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;
import fj.F;
import fj.test.Arbitrary;
import fj.test.Gen;
import fj.test.Property;
import fj.test.runner.PropertyTestRunner;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

import static fj.test.Cogen.cogenInteger;

@RunWith(PropertyTestRunner.class)
public class FutureOpAliasesTest
{

    /**
     * A generator of arbitrary functions Int->Future[Int]
     */
    public final Gen<F<Integer, ListenableFuture<Integer>>> arbFunction = Arbitrary
            .arbF(cogenInteger, genFuture(Arbitrary.arbInteger));
    /**
     * A generator of arbitrary Future[Integer]'s
     */
    public final Gen<ListenableFuture<Integer>> arbFuture = genFuture(Arbitrary.arbInteger);
    /**
     * A generator of arbitrary functions Int->Int
     */
    public final Gen<F<Integer, Integer>> arbFlatFunction = Arbitrary.arbF(cogenInteger, Arbitrary.arbInteger);

    /**
     * The left identity law (in the case of futures) says that if you wrap a value using unit and then bind it with a function f,
     * this will always equal the result of calling f. More formally,
     * forall x, f => bind(unit(x),f) === f(x)
     */
    public Property leftIdentity()
    {

        return Property.property(Arbitrary.arbInteger, arbFunction, (x, f) -> {
            ListenableFuture<Integer> bindUnitValueWithF = FutureOpAliases.bind(FutureOpAliases.unit(x), i -> f.f(i));
            ListenableFuture<Integer> applyF = f.f(x);
            return Property.prop(futuresEqual(bindUnitValueWithF, applyF));
        });
    }

    /**
     * The right identity law (in the case of futures) says that if you bind a future with the unit function, the result will
     * equal the original future. More formally,
     * forall future => bind(future, fut->unit(f)) === future
     *
     * @return
     */
    public Property rightIdentity()
    {
        return Property.property(arbFuture, future -> {
            ListenableFuture<Integer> bindWithUnitFunction = FutureOpAliases.bind(future, i -> FutureOpAliases.unit(i));
            return Property.prop(futuresEqual(future, bindWithUnitFunction));
        });
    }

    /**
     * The associativity law (in the case of futures) says that if you have a chain of 'bind' calls on a future, it doesn't
     * matter if you compute them left to right or right to left. More formally,
     * forall future, f, g => bind( bind(future,f) , g) == bind( future, val->bind(f(val),g) )
     *
     * @return
     */
    public Property associativity()
    {

        return Property.property(arbFunction, arbFunction, arbFuture, (f, g, future) -> {

            ListenableFuture<Integer> leftToRight = FutureOpAliases.bind(FutureOpAliases.bind(future, i -> f.f(i)), j -> g.f(j));
            AsyncFunction<Integer, Integer> fBindG = i -> FutureOpAliases.bind(f.f(i), j -> g.f(j));
            ListenableFuture<Integer> rightToLeft = FutureOpAliases.bind(future, fBindG);

            return Property.prop(futuresEqual(leftToRight, rightToLeft));
        });
    }

    /**
     * We have not mentined 'map' in the above tests. The reason for this is that map can be expressed in terms
     * of bind an unit. This test tests that mapping a future with a function of type a->b is the same as
     * binding a future with a function that given an 'x' of type 'a', returns unit(f(x))
     * forall future, f: a->b => map(future, f) === bind( future, x->unit(f(x)) )
     *
     * @return
     */
    public Property mapIsCorrect()
    {
        //since above we only tested bind and unit, we will now test that map behaves as if it was implemented
        //in terms of bind and unit, thereby proving its correctness indirectly
        return Property.property(arbFuture, arbFlatFunction, (future, f) -> {

            ListenableFuture<Integer> mapped = FutureOpAliases.map(future, i -> f.f(i));
            ListenableFuture<Integer> bound = FutureOpAliases.bind(future, i -> FutureOpAliases.unit(f.f(i)));

            return Property.prop(futuresEqual(mapped, bound));
        });
    }

    private static <A> Gen<ListenableFuture<A>> genFuture(Gen<A> gen)
    {
        return gen.map(x -> FutureOpAliases.unit(x));
    }

    private static <A> boolean futuresEqual(ListenableFuture<A> fut1, ListenableFuture<A> fut2)
    {
        ListenableFuture<Boolean> areEqual = FutureOpAliases.bind(fut1, x -> FutureOpAliases.map(fut2, y -> x.equals(y)));
        try
        {
            return areEqual.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }

}