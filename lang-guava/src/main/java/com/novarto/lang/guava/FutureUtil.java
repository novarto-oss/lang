package com.novarto.lang.guava;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class FutureUtil
{

    /**
     * Exact same semantics as
     *
     * @param future
     * @param onSuccess
     * @param onError
     * @param executor
     * @param <V>
     * @see com.google.common.util.concurrent.Futures#addCallback(ListenableFuture, FutureCallback, Executor)
     */
    public static <V> void addCallback(final ListenableFuture<V> future, Consumer<V> onSuccess, Consumer<Throwable> onError,
            Executor executor)
    {
        Preconditions.checkNotNull(onSuccess);
        Preconditions.checkNotNull(onError);
        future.addListener(() -> {
            try
            {
                onSuccess.accept(getUninterruptibly(future));
            }
            catch (ExecutionException e)
            {
                onError.accept(e.getCause());
                return;
            }
            catch (RuntimeException e)
            {
                onError.accept(e);
                return;
            }
            catch (Error e)
            {
                onError.accept(e);
                return;
            }

        }, executor);

    }

    public static <V> V getUninterruptibly(Future<V> future) throws ExecutionException
    {
        boolean interrupted = false;
        try
        {
            while (true)
            {
                try
                {
                    return future.get();
                }
                catch (InterruptedException e)
                {
                    interrupted = true;
                }
            }
        }
        finally
        {
            if (interrupted)
            {
                Thread.currentThread().interrupt();
            }
        }
    }
}
