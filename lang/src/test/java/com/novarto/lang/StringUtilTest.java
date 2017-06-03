package com.novarto.lang;

import fj.data.Stream;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringUtilTest
{
    @Test
    public void testIt()
    {
        assertThat(StringUtil.repeat('a', 0), is(""));
        assertThat(StringUtil.repeat('z', 6), is("zzzzzz"));

        assertThat(StringUtil.join(Stream.arrayStream("hey", "ho", "let's go"), "-").toString(),
                is("hey-ho-let's go"));

        String[] xs = {"a", "b", "c"};
        assertThat(StringUtil.join(xs, ",").toString(), is("a,b,c"));


    }
}
