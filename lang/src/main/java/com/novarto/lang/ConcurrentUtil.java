package com.novarto.lang;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A set of concurrency-related utility methods.
 */
public class ConcurrentUtil
{

    /**
     * Register a shutdown hook to shutdown an executor service. The hook will wait for the specified timeout for the service
     * to shutdown.
     * @param service a logical name indicating this ExecutorService
     */
    public static void registerShutdownHook(ExecutorService service, String targetServiceName, long timeout, TimeUnit timeUnit)
    {
        final Thread shutdownHook = new Thread(() -> shutdownAndAwaitTermination(service, timeout, timeUnit));

        shutdownHook.setName(targetServiceName + " Shutdown hook");

        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    private static boolean shutdownAndAwaitTermination(ExecutorService service, long timeout, TimeUnit unit)
    {
        long halfTimeoutNanos = unit.toNanos(timeout) / 2;
        // Disable new tasks from being submitted
        service.shutdown();
        try
        {
            // Wait for half the duration of the timeout for existing tasks to terminate
            if (!service.awaitTermination(halfTimeoutNanos, TimeUnit.NANOSECONDS))
            {
                // Cancel currently executing tasks
                service.shutdownNow();
                // Wait the other half of the timeout for tasks to respond to being cancelled
                service.awaitTermination(halfTimeoutNanos, TimeUnit.NANOSECONDS);
            }
        }
        catch (InterruptedException ie)
        {
            // Preserve interrupt status
            Thread.currentThread().interrupt();
            // (Re-)Cancel if current thread also interrupted
            service.shutdownNow();
        }
        return service.isTerminated();
    }

}
