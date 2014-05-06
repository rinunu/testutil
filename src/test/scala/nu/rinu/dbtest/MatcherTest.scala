package nu.rinu.dbtest

import org.scalatest.FunSuite
import org.junit.Assert._
import nu.rinu.dbtest.matcher.EqualToDiff._
import org.hamcrest.CoreMatchers._
import org.junit.Test

/**
  */
class MatcherTest {
  @Test
  def equalTo_diff() {
    val a = DataSetDiff(Set(TableDiff("FOO", Set(
      DeleteRow(Seq("ID" -> 2)),
      UpdateRow(Seq("ID" -> 3), Seq("COLUMN_S" -> "ss3")),
      InsertRow(Seq("ID" -> 4, "COLUMN_S" -> "s4", "COLUMN_N" -> 4))
    ))))

    val b = DataSetDiff(Set(TableDiff("FOO", Set(
      DeleteRow(Seq("ID" -> 2)),
      UpdateRow(Seq("ID" -> 3), Seq("COLUMN_S" -> "ss3")),
      InsertRow(Seq("ID" -> 4, "COLUMN_S" -> "s5", "COLUMN_N" -> 4))
    ))))

    assertThat(a, equalToDiff(b))
  }
}
