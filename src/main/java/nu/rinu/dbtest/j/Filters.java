package nu.rinu.dbtest.j;

import nu.rinu.dbtest.Filter;
import nu.rinu.dbtest.Filter$;
import nu.rinu.dbtest.TableColumn;

/**
 * for java
 */
public class Filters {
    public static Filter<TableColumn> regexTableColumnFilter(String tablePattern, String columnPattern) {
        return Filter$.MODULE$.regexTableColumnFilter(tablePattern, columnPattern);
    }

    public static Filter<TableColumn> regexColumnFilter(String columnPattern) {
        return Filter$.MODULE$.regexColumnFilter(columnPattern);
    }

    public static Filter<String> regexps(String... s) {
        return Filter$.MODULE$.regexps(s);
    }

    public static <A> Filter<A> not(Filter<A> p) {
        return Filter$.MODULE$.not(p);
    }

    public static <A> Filter<A> and(Filter<A>... p) {
        return Filter$.MODULE$.and(p);
    }
    
    public static <A> Filter<A> or(Filter<A>... p) {
        return Filter$.MODULE$.or(p);
    }
}
