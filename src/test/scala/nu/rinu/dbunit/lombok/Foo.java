package nu.rinu.dbunit.lombok;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Foo {
    private Integer id;
    private String columnS;
    private Integer columnN;

    public static Foo create(Integer id, String columnS, Integer columnN) {
        return new Foo(id, columnS, columnN);
    }

    public static Foo create(Integer id, String columnS) {
        return new Foo().id(id).columnS(columnS);
    }
}
