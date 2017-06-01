package com.novarto.cletty.lang;

public final class SneakyThrow
{
    private SneakyThrow()
    {
    }

    /**
     * Rethrow an {@link java.lang.Exception} preserving the stack trace but making it unchecked.
     * This method is not intended to be used from application code.
     *
     * @param ex to be rethrown and unchecked.
     */
    public static void sneakyThrow(final Exception ex)
    {
        SneakyThrow.<RuntimeException>rethrow(ex);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Exception> void rethrow(final Exception ex) throws T
    {
        throw (T) ex;
    }
}