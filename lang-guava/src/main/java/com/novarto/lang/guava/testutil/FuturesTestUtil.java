package com.novarto.lang.guava.testutil;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.novarto.lang.guava.FutureOpAliases;
import com.novarto.lang.guava.FutureUtil;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;


public class FuturesTestUtil
{
    public static <A> A awaitAndGet(ListenableFuture<A> fut)
    {
        try
        {
            return FutureOpAliases.Await.ready(fut, Duration.ofSeconds(10)).get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static <A> ListenableFuture<A> await(ListenableFuture<A> fut)
    {
        return FutureOpAliases.Await.ready(fut, Duration.ofSeconds(10));
    }

    public static Throwable awaitAndGetFailure(ListenableFuture<?> f)
    {

        AtomicReference<Throwable> result = new AtomicReference<>();
        await(f);
        if (!f.isDone())
        {
            throw new AssertionError();
        }
        FutureUtil.addCallback(f, ignore ->
        {
            throw new AssertionError();
        }, throwable -> result.set(throwable), MoreExecutors.directExecutor());

        if (result.get() == null)
        {
            throw new AssertionError();
        }

        return result.get();

    }

}
