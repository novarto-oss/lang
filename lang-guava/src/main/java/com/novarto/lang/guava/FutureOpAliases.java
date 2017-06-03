package com.novarto.lang.guava;

import com.google.common.base.Function;
import com.google.common.util.concurrent.*;
import com.novarto.lang.Stackless;
import fj.function.Effect0;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.LockSupport;

import static com.google.common.util.concurrent.Futures.*;

/**
 * <p>
 * This class gives aliases to the monadic operations over ListenableFuture so that they have the standard names:
 * map, bind, unit, join.
 * These are the standard names which you will find in most books and articles on functional programming.
 *
 *
 * In addition it provides a couple of utility functions.
 *
 * <p>The monadic operations bind and unit obey the monadic laws. These are left identity, right identity and associativity.
 * For more details on these, see the test case FutureOpAliasesTest.
 */
public class FutureOpAliases
{

    /**
     * The map function takes a Future[A] and a  transformation A->B, and yields a Future[B].
     */
    public static <A, B> ListenableFuture<B> map(ListenableFuture<A> future, Function<A, B> f)
    {
        return transform(future, f, MoreExecutors.directExecutor());
    }

    /**
     * The map function takes a Future[A] and a  transformation A->B, and yields a Future[B].
     */
    public static <A, B> ListenableFuture<B> map(ListenableFuture<A> future, Function<A, B> f, Executor exec)
    {
        return transform(future, f, exec);
    }

    /**
     * The bind function takes a Future[A] and a transformation A->Future[B], and returns a Future[B]
     */
    public static <A, B> ListenableFuture<B> bind(ListenableFuture<A> future, AsyncFunction<A, B> f)
    {
        return transformAsync(future, f, MoreExecutors.directExecutor());
    }

    /**
     * The bind function takes a Future[A] and a transformation A->Future[B], and returns a Future[B]
     */
    public static <A, B> ListenableFuture<B> bind(ListenableFuture<A> future, AsyncFunction<A, B> f, Executor e)
    {
        return transformAsync(future, f, e);
    }

    /**
     * The unit operation takes a regular value and wraps it in a monadic context.
     * I.e. it takes an A and returns a Future[A].
     */
    public static <A> ListenableFuture<A> unit(A a)
    {
        return immediateFuture(a);
    }



    public static <A> ListenableFuture<A> fail(String msg)
    {
        return immediateFailedFuture(new Stackless(msg));
    }

    public static <A> ListenableFuture<A> join(ListenableFuture<ListenableFuture<A>> ff)
    {
        return bind(ff, x -> x);
    }

    /**
     * collect takes a set of Futures of the same type, and yields a Future of a list of values of that type.
     * This future is complete when all of the underlying futures have completed or when any of them have failed.
     * The returned sequence’s order corresponds to the order of the passed-in sequence.
     *
     * @param futures
     * @param <A>
     * @return
     */
    @SafeVarargs @SuppressWarnings("varargs")
    public static <A> ListenableFuture<java.util.List<A>> collect(ListenableFuture<A>... futures)
    {

        return allAsList(futures);

    }

    /**
     * collect takes a set of Futures of the same type, and yields a Future of a list of values of that type.
     * This future is complete when all of the underlying futures have completed or when any of them have failed.
     * The returned sequence’s order corresponds to the order of the passed-in sequence.
     *
     * @param futures
     * @param <A>
     * @return
     */
    public static <A> ListenableFuture<java.util.List<A>> collect(Iterable<ListenableFuture<A>> futures)
    {

        return allAsList(futures);

    }

    public static class Await
    {
        /**
         * Awaits the completion of future with timeout duration.
         * Method will always return Completed future thus {@link ListenableFuture#isDone()} of the
         * returned future will be true.
         * If computation of the passed future is not finished within specified timeout, a completed failed
         * future with {@link TimeoutException} will be returned.
         *
         * @param future
         * @param duration
         * @return Completed future.
         */
        public static <A> ListenableFuture<A> ready(final ListenableFuture<A> future, final Duration duration)
        {
            final SettableFuture<A> computed = SettableFuture.create();

            addCallback(future, new FutureCallback<A>()
            {
                @Override
                public void onSuccess(A result)
                {
                    computed.set(result);
                }

                @Override
                public void onFailure(Throwable t)
                {
                    computed.setException(t);
                }
            }, MoreExecutors.directExecutor());

            final long start = System.nanoTime();

            long currStepNanos = Math.max(1000000, duration.toNanos() / 100000000000L);

            while (!computed.isDone())
            {
                final long elapsed = System.nanoTime() - start;
                final long remaining = duration.toNanos() - elapsed;

                if (remaining <= 0)
                {
                    computed.setException(new TimeoutException("Failed to complete for " + duration.toNanos()));
                    break;
                }

                LockSupport.parkNanos(Math.min(remaining, currStepNanos));

                currStepNanos = currStepNanos << 1;

            }

            return computed;

        }

    }

    /**
     * Executes an effect when the passed future completes successfully, or fails.
     * Make sure the effect is lightweight and non-blocking, since it will execute in the direct executor.
     * If the future passed never completes or fails, the effect will never execute.
     *
     * @param future
     * @param eff
     */
    public static void finallyDo(ListenableFuture<?> future, Effect0 eff)
    {
        future.addListener(() -> eff.f(), MoreExecutors.directExecutor());
    }

}
