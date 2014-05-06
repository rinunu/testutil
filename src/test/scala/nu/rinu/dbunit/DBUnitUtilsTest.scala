package nu.rinu.dbunit

import org.junit.{Assert, Test}
import Assert._
import org.scalatest.FunSuite
import java.sql.Connection
import scala.util.Random
import org.apache.derby.jdbc.EmbeddedDataSource
import nu.rinu.sdb.Implicits._
import org.dbunit.database.IDatabaseConnection
import nu.rinu.dbunit.lombok.{Bar, Foo}
import nu.rinu.dbgen.core.SqlType

class DBUnitUtilsTest extends FunSuite with BasicDBTest {

  test("createTable") {
    withDBUnitConnection { implicit con =>
      createFooTable()

      val foo = Foo.create(1, "s1", 1)

      val table = DBUnitUtils.createTable(Array(foo))

      assert(table.getTableMetaData.getTableName === "foo")
      assert(table.getRowCount === 1)
      assert(table.getValue(0, "id") === 1)
      assert(table.getValue(0, "column_s") === "s1")
      assert(table.getValue(0, "column_n") === 1)
    }
  }

  test("createDataSet") {
    withDBUnitConnection { implicit con =>
      createFooTable()
      createBarTable()

      val foo = Foo.create(1, "s1", 1)
      val foo2 = Foo.create(2, "s2", 2)
      val bar = Bar.create(3, "s3", 3)

      val dataSet = DBUnitUtils.createDataSet(Array(foo, foo2, bar))

      val fooTable = dataSet.getTable("foo")
      assert(fooTable.getTableMetaData.getTableName === "foo")
      assert(fooTable.getRowCount === 2)
      assert(fooTable.getValue(0, "id") === 1)
      assert(fooTable.getValue(0, "column_s") === "s1")
      assert(fooTable.getValue(0, "column_n") === 1)

      assert(fooTable.getValue(1, "id") === 2)
      assert(fooTable.getValue(1, "column_s") === "s2")
      assert(fooTable.getValue(1, "column_n") === 2)

      val barTable = dataSet.getTable("bar")
      assert(barTable.getTableMetaData.getTableName === "bar")
      assert(barTable.getRowCount === 1)
      assert(barTable.getValue(0, "id") === 3)
      assert(barTable.getValue(0, "column_s") === "s3")
      assert(barTable.getValue(0, "column_n") === 3)
    }
  }

  private def createFooTable()(implicit c: Connection) {
    c.prepareStatement( """CREATE TABLE foo
          (id INT PRIMARY KEY NOT NULL,
           column_s VARCHAR(100) NOT NULL,
           column_n INT NOT NULL)""") { stmt =>
      stmt.execute()
    }
  }

  private def createBarTable()(implicit c: Connection) {
    c.prepareStatement( """CREATE TABLE bar
          (id INT PRIMARY KEY NOT NULL,
           column_s VARCHAR(100) NOT NULL,
           column_n INT NOT NULL)""") { stmt =>
      stmt.execute()
    }
  }

}

