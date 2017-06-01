package com.novarto.cletty.lang;

import fj.Ord;
import fj.data.Set;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static fj.data.List.list;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
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
