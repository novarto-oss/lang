package com.novarto.lang.testutil;

import fj.F0;
import fj.Try;
import fj.data.Either;
import fj.function.Try0;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

/**
 * A set of utilities useful for testing.
 */
public class TestUtil
{
    /**
     * Waits for a condition to be true or throws an runtime exception otherwise. Blocks the caller thread.
     * Uses an exponential backoff
     *
     * @param pred the condition to check against
     * @param timeout
     * @param unit
     */
    public static void waitFor(F0<Boolean> pred, long timeout, TimeUnit unit)
    {
        final long start = System.currentTimeMillis();
        final long timeOutMillis = TimeUnit.MILLISECONDS.convert(timeout, unit);

        long currStepMillis = Math.max(1, timeOutMillis / 100000);

        while (!pred.f())
        {
            final long elapsed = System.currentTimeMillis() - start;
            final long remaining = timeOutMillis - elapsed;

            if (remaining <= 0)
            {
                break;
            }

            try
            {
                Thread.sleep(Math.min(remaining, currStepMillis));
            }
            catch (InterruptedException e)
            {
                //k
            }
            finally
            {
                currStepMillis = currStepMillis << 1;
            }
        }

        if (!pred.f())
        {
            throw new RuntimeException("timeout waiting for predicate to become true");
        }

    }

    /**
     * Finds a free port on which a server socket can be created.
     */
    public static int findFreePort()
    {

        try (ServerSocket socket = new ServerSocket(0))
        {
            return socket.getLocalPort();
        }
        catch (IOException e)
        {
            throw new RuntimeException("cant get free port", e);
        }
    }

    /**
     * Runs a piece of code, rethrowing a runtime exception iff the code throws
     */
    public static <A> A tryTo(Try0<A, Exception> block)
    {
        Either<Exception, A> e = Try.f(block).f().toEither();
        if (e.isLeft())
        {
            Exception cause = e.left().value();
            throw new RuntimeException(cause);
        }
        return e.right().value();
    }
}
