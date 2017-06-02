package com.novarto.lang;

import fj.Ord;
import fj.data.Set;
import fj.data.Stream;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static fj.data.List.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

/**
 * Created by fmap on 06.07.16.
 */
public class CollectionsTest
{
    @Test @SuppressWarnings("deprecation")
    public void duplicates()
    {

        assertThat(Collections.duplicates(list()), is(Set.empty(Ord.hashEqualsOrd())));

        assertThat(Collections.duplicates(list(1, 2, 3, 7)), is(Set.empty(Ord.hashEqualsOrd())));

        assertThat(Collections.duplicates(list(1, 2, 3, 7, 7, 9, 9)), is(Set.arraySet(Ord.hashEqualsOrd(), 7, 9)));

    }

    @Test
    public void toMap()

    {
        Map<Integer, List<Bean>> map = Collections
                .toMap(list(new Bean(1, "pesho"), new Bean(2, "gosho"), new Bean(2, "ivan")), x -> x.id, x -> x);
        assertThat(map.entrySet().size(), is(2));
        assertThat(map.get(1), is(asList(new Bean(1, "pesho"))));
        assertThat(map.get(2), is(asList(new Bean(2, "gosho"), new Bean(2, "ivan"))));

    }

    @Test
    public void toMapUnique()
    {
        Map<Integer, Bean> map = Collections
                .toMapUnique(list(new Bean(1, "pesho"), new Bean(2, "gosho")), x -> x.id, x -> x);
        assertThat(map.entrySet().size(), is(2));
        assertThat(map.get(1), is(new Bean(1, "pesho")));
        assertThat(map.get(2), is(new Bean(2, "gosho")));
    }

    @Test
    public void toList()
    {
        fj.data.List<Integer> fjList = arrayList(1, 2, 3);

        assertThat(Collections.toList(fjList), is(sameInstance(fjList)));
        assertThat(Collections.toList(nil()), is(sameInstance(nil())));

        assertThat(Collections.toList(asList(1, 2, 3)), is(arrayList(1, 2, 3)));
    }

    @Test
    public void size()
    {
        assertThat(Collections.size(nil()), is(0));
        assertThat(Collections.size(emptyList()), is(0));
        assertThat(Collections.size(singletonList(5)), is(1));
        assertThat(Collections.size(arrayList(5, 6, 7)), is(3));

        assertThat(Collections.size(Stream.range(0, 5)), is(5));

    }


    private static final class Bean
    {
        public final String name;
        public final int id;

        private Bean(int id, String name)
        {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            Bean bean = (Bean) o;

            if (id != bean.id)
            {
                return false;
            }
            return name != null ? name.equals(bean.name) : bean.name == null;

        }

        @Override
        public int hashCode()
        {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + id;
            return result;
        }
    }
}
