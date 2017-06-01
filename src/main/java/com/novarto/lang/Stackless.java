package com.novarto.lang;

@SuppressWarnings("serial")
/**
 * Throw an exception without incurring the overhead of filling in the stacktrace.
 * See https://shipilev.net/blog/2014/exceptional-performance/
 *
 * This is only intended to be used in hotspots of your application.
 */
public class Stackless extends Exception
{
    public Stackless(String message)
    {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace()
    {
        return this;
    }
}
