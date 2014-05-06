package nu.rinu.dbtest.j;

import java.sql.Connection;

import nu.rinu.dbtest.DataSetDiff;
import nu.rinu.dbtest.DiffBuilderOps;

public class DiffBuilder {
    private DiffBuilderOps impl;

    public DiffBuilder() {
        this.impl = new DiffBuilderOps();
    }

    public DiffBuilder insert(Object... values) {
        impl.insert(values);
        return this;
    }

    public DiffBuilder delete(Object... values) {
        impl.delete(values);
        return this;
    }

    public DiffBuilder update(Object... values) {
        impl.update(values);
        return this;
    }

    public DataSetDiff build(Connection connection) {
        return impl.build(connection);
    }
}
