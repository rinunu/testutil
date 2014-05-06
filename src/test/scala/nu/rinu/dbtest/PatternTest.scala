package nu.rinu.dbtest

import org.scalatest.FunSuite

class PatternTest extends FunSuite {
  test("table column regex") {
    val filter = Filter.regexTableColumnFilter("t.*", "c.*")
    assert(filter(TableColumn("t1", "c1")))
    assert(!filter(TableColumn("xx", "yy")))
  }
  
}
