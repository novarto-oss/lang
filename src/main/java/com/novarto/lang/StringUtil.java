package com.novarto.lang;

import java.util.Arrays;

/**
 * A collection of string-related utulities
 */
public class StringUtil
{
    /**
     * Repeat the given character a number of times, and return a String
     */
    public static String repeat(char c, int times)
    {
        char[] arr = new char[times];
        Arrays.fill(arr, c);
        return new String(arr);
    }

    /**
     * Join the iterable in a stringbuilder, using the given separator
     */
    public static StringBuilder join(Iterable<String> xs, String separator)
    {
        StringBuilder b = new StringBuilder();
        for (String x : xs)
        {
            b.append(x).append(separator);
        }
        return b.delete(b.length() - separator.length(), b.length());
    }

    /**
     * Specialized version of join for arrays
     */
    public static StringBuilder join(String[] xs, String separator)
    {
        StringBuilder b = new StringBuilder();
        for (String x : xs)
        {
            b.append(x).append(separator);
        }
        return b.delete(b.length() - separator.length(), b.length());
    }

}
