package nu.rinu.dbunit.lombok;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
@AllArgsConstructor
public class Bar {
    private Integer id;
    private String columnS;
    private Integer columnN;

    public static Bar create(Integer id, String columnS, Integer columnN) {
        return new Bar(id, columnS, columnN);
    }
}
