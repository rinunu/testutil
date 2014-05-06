package nu.rinu.dbunit.j;


import nu.rinu.dbtest.DataSetDiff;
import nu.rinu.dbtest.Filter;
import nu.rinu.dbunit.DBUnitUtils;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;

/**
 * for java
 */
public class Tester {
    private IDatabaseConnection con;
    private Filter tablePattern;

    public Tester(IDatabaseConnection con, Filter<String> tablePattern) {
        this.tablePattern = tablePattern;
        this.con = con;
    }

    IDataSet before = null;

    public void cleanInsert(Object... dtos) {
        DBUnitUtils.cleanInsert(dtos, con);
    }

    public void insert(Object... dtos) {
        DBUnitUtils.insert(dtos, con);
    }

    public IDataSet snapshot() {
        before = DBUnitUtils.snapshot(tablePattern, con);
        return before;
    }

    public DataSetDiff diff() {
        IDataSet after = DBUnitUtils.snapshot(tablePattern, con);
        return DBUnitUtils.diff(before, after);
    }
}

