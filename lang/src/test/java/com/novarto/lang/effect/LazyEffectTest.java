package com.novarto.lang.effect;

import fj.data.Validation;
import org.junit.Test;

import static com.novarto.lang.effect.LazyEffect.*;
import static fj.data.Validation.fail;
import static fj.data.Validation.success;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LazyEffectTest
{
    @Test public void testIt()
    {

        LazyEffect<RuntimeException, Integer> in = pure(2);

        LazyEffect<RuntimeException, Integer> result = in.map(x -> x * 2);

        assertThat(eval(result), is(4));

        result = in.bind(x -> pure(x * 2));
        assertThat(eval(result), is(4));

        result = in.bind(x -> effect(() -> {
            System.out.println("failed I have");
            throw new RuntimeException("failed I have");
        }));


        assertThat(result.run().isFail(), is(true));

        result = pure(2);
        assertThat(result.run(), is(success(2)));

        result = pure(() -> 2);
        assertThat(result.run(), is(success(2)));


        RuntimeException rte = new RuntimeException("failed I have");
        result = error(rte);
        assertThat(result.run(), is(fail(rte)));

        result = error(() -> rte);
        assertThat(result.run(), is(fail(rte)));

        LazyEffect<RuntimeException, Integer> failed = error(rte);
        LazyEffect<String, Integer> result2 = failed.mapError(Throwable::getMessage);

        assertThat(result2.run(), is(fail("failed I have")));

    }



    private static <A> A eval(LazyEffect<RuntimeException, A> program)
    {
        Validation<RuntimeException, A> result = program.run();
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
