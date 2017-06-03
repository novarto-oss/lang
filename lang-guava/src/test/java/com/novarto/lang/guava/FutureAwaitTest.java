package com.novarto.lang.guava;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class FutureAwaitTest
{
    public static final ListeningExecutorService SERVICE = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

    @Test
    public void testTimeout() throws Exception
    {
        long durationInNano = TimeUnit.NANOSECONDS.convert(500, TimeUnit.MILLISECONDS);

        final ListenableFuture<Integer> timeout = SERVICE.submit(() -> {
            Thread.sleep(1000);
            return 42;
        });

        final ListenableFuture<Integer> ready = FutureOpAliases.Await.ready(timeout, Duration.ofNanos(durationInNano));

        assertThat(ready.isDone(), is(true));

        addCallback(ready, new FutureCallback<Integer>()
        {
            @Override
            public void onSuccess(Integer result)
            {
                Assert.fail();
            }

            @Override
            public void onFailure(Throwable t)
            {
                assertThat(t.getClass().getCanonicalName(), is(TimeoutException.class.getCanonicalName()));
            }
        }, directExecutor());
    }

    @Test
    public void testSuccess() throws Exception
    {
        long durationInNano = TimeUnit.NANOSECONDS.convert(500, TimeUnit.MILLISECONDS);

        final ListenableFuture<Integer> timeout = SERVICE.submit(() -> 42);

        final ListenableFuture<Integer> ready = FutureOpAliases.Await.ready(timeout, Duration.ofNanos(durationInNano));

        assertThat(ready.isDone(), is(true));

        addCallback(ready, new FutureCallback<Integer>()
        {
            @Override
            public void onSuccess(Integer result)
            {
                Assert.assertThat(result, is(42));
            }

            @Override
            public void onFailure(Throwable t)
            {
                Assert.fail();
            }
        }, directExecutor());

    }

    @Test
    public void testException() throws Exception
    {
        long durationInNano = TimeUnit.NANOSECONDS.convert(500, TimeUnit.MILLISECONDS);

        final ListenableFuture<Integer> timeout = SERVICE.submit(() -> {
            throw new RuntimeException();
        });

        final ListenableFuture<Integer> ready = FutureOpAliases.Await.ready(timeout, Duration.ofNanos(durationInNano));

        assertThat(ready.isDone(), is(true));

        addCallback(ready, new FutureCallback<Integer>()
        {
            @Override
            public void onSuccess(Integer result)
            {
                Assert.fail();
            }

            @Override
            public void onFailure(Throwable t)
            {
                assertThat(t.getClass().getCanonicalName(), is(RuntimeException.class.getCanonicalName()));
            }
        }, directExecutor());
    }
}
