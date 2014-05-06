import java.sql.Connection
import org.apache.derby.jdbc.EmbeddedDataSource
import org.dbunit.database.{DatabaseConnection, IDatabaseConnection}
import org.dbunit.dataset.{Column, DefaultTable, DefaultDataSet}
import org.dbunit.DataSourceDatabaseTester
import org.dbunit.operation.DatabaseOperation
import org.scalatest.FunSuite
import scala.util.Random
import nu.rinu.sdb.Implicits._

class BasicSpec extends FunSuite {

  private def createFooTable()(implicit c: Connection) {
    c.prepareStatement( """CREATE TABLE foo
          (ID INT PRIMARY KEY NOT NULL,
           S VARCHAR(100) NOT NULL,
           N INT NOT NULL)""") { stmt =>
      stmt.execute()
    }
  }
  
  test("insert") {

    withConnection { implicit c =>
      createFooTable()
      val connection: IDatabaseConnection = new DatabaseConnection(c)

      val t = connection.createTable("foo")
      val table = new DefaultTable(t.getTableMetaData)
      table.addRow()
      table.setValue(0, "ID", 10)
      table.setValue(0, "S", "s0")
      table.setValue(0, "N", 1)
      table.addRow()
      table.setValue(1, "ID", 11)
      table.setValue(1, "S", "s0")
      table.setValue(1, "N", 1)
      val dataSet = new DefaultDataSet(table)
      DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet)

      val actual = connection.createDataSet()

      for {name <- actual.getTableNames} {
        val table = actual.getTable(name)
        println(name)
        for {row <- 0 until table.getRowCount} {
          val rowValues = for {col <- table.getTableMetaData.getColumns} yield {
            table.getValue(row, col.getColumnName)
          }
          println(rowValues.toList)
        }
      }
    }
  }

  private def withConnection[A](f: Connection => A): A = {
    val dbName = "memory:mydb" + Random.nextInt()
    val ds = new EmbeddedDataSource

    ds.setDatabaseName(dbName)
    ds.setCreateDatabase("create")
    ds.withConnection(f)
  }


}


