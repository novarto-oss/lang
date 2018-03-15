package com.novarto.lang.effect;

import fj.F0;
import fj.P1;
import fj.Try;
import fj.data.Validation;
import org.junit.Test;

import static com.novarto.lang.effect.Effects.bind;
import static com.novarto.lang.effect.Effects.map;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EffectsTest
{
    @Test public void testIt()
    {

        P1<Validation<RuntimeException, Integer>> result = map(okEffect(2), x -> x * 2);
        assertThat(eval(result), is(4));


        result = bind(okEffect(2), x -> okEffect(x * 2));
        assertThat(eval(result), is(4));


        result = bind(okEffect(2), x -> failedEffect(() -> {
            System.out.println("failed I have");
            throw new RuntimeException("failed I have");
        }));

        assertThat(result.f().isFail(), is(true));



    }

    private static <A> P1<Validation<RuntimeException, A>> okEffect(A a)
    {
        return Try.f(() -> {
            System.out.println("effect -> " + a);
            return a;
        });
    }

    private static <A> P1<Validation<RuntimeException, A>> failedEffect(F0<RuntimeException> fail)
    {
        return Try.f(() -> {
            throw fail.f();
        });
    }

    private static <A> A eval(P1<Validation<RuntimeException, A>> program)
    {
        Validation<RuntimeException, A> result = program.f();
        if (result.isFail())
        {
            throw result.fail();
        }
        else
        {
            return result.success();
        }
    }
}
