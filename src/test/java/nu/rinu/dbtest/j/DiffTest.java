package nu.rinu.dbtest.j;

import nu.rinu.dbunit.lombok.Foo;
import org.junit.Test;

public class DiffTest {
    @Test
    public void dto_to_diff() {
        new DiffBuilder()
            .delete(new Foo().id(1))
            .update(new Foo().id(2))
            .insert(new Foo().id(3));
    }
}
