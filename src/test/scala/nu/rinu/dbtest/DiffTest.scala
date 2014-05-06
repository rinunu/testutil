package nu.rinu.dbtest

import org.scalatest.FunSuite
import nu.rinu.dbunit.{DBUnitUtils, BasicDBTest}
import java.sql.Connection
import nu.rinu.sdb.Implicits._
import nu.rinu.dbunit.lombok.Foo

class DiffTest extends FunSuite with BasicDBTest {
  test("dto to diff") {
    withDBUnitConnection { implicit con =>
      createFooTable()
      val diff = DiffBuilder
        .insert(Foo.create(1, "s1"))
        .update(Foo.create(2, "s2"))
        .delete(Foo.create(3, "s3"))
        .build()

      assert(diff === DataSetDiff(Set(
        TableDiff("foo", Set(
          InsertRow(Seq("ID" -> 1, "COLUMN_S" -> "s1")),
          UpdateRow(Seq("ID" -> 2), Seq("COLUMN_S" -> "s2")),
          DeleteRow(Seq("ID" -> 3))
        ))
      )))
    }
  }

  test("filter") {
    withDBUnitConnection { implicit con =>
      createFooTable()
      val diff = DiffBuilder
        .insert(Foo.create(1, "s1"))
        .update(Foo.create(2, "s2"))
        .delete(Foo.create(3, "s3"))
        .build()

      val diff2 = diff.filter(Filter.not(Filter.regexColumnFilter("COLUMN_S")))

      assert(diff2 === DataSetDiff(Set(
        TableDiff("foo", Set(
          InsertRow(Seq("ID" -> 1)),
          UpdateRow(Seq("ID" -> 2), Seq()),
          DeleteRow(Seq("ID" -> 3))
        ))
      )))
    }
  }

  test("diff table") {
    withDBUnitConnection { implicit con =>
      val tablePattern = Filter.regexps(".*")
      createFooTable()

      con.getConnection.prepareStatement("insert into foo values (1, 's1', 1)").executeUpdate()
      con.getConnection.prepareStatement("insert into foo values (2, 's2', 2)").executeUpdate()
      con.getConnection.prepareStatement("insert into foo values (3, 's3', 3)").executeUpdate()

      val before = DBUnitUtils.snapshot(tablePattern)

      con.getConnection.prepareStatement("delete from foo where id = 2").executeUpdate()
      con.getConnection.prepareStatement("insert into foo values (4, 's4', 4)").executeUpdate()
      con.getConnection.prepareStatement("update foo set column_s = 'ss3' where id = 3").executeUpdate()

      val after = DBUnitUtils.snapshot(tablePattern)

      val diff = DBUnitUtils.diff(before, after)

      assert(diff.tables === Set(
        TableDiff("FOO", Set(
          DeleteRow(Seq("ID" -> 2)),
          UpdateRow(Seq("ID" -> 3), Seq("COLUMN_S" -> "ss3")),
          InsertRow(Seq("ID" -> 4, "COLUMN_S" -> "s4", "COLUMN_N" -> 4))
        ))
      ))
    }
  }

  private def createFooTable()(implicit c: Connection) {
    c.prepareStatement( """CREATE TABLE foo
          (ID INT PRIMARY KEY NOT NULL,
           COLUMN_S VARCHAR(100) NOT NULL,
           COLUMN_N INT NOT NULL)""") { stmt =>
      stmt.execute()
    }
  }
}
