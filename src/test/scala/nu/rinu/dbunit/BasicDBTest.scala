package nu.rinu.dbunit

import nu.rinu.sdb.Implicits._
import java.sql.Connection
import scala.util.Random
import org.apache.derby.jdbc.EmbeddedDataSource
import org.dbunit.database.{DatabaseConnection, IDatabaseConnection}

trait BasicDBTest {
  def withConnection[A](f: Connection => A): A = {
    val dbName = "memory:mydb" + Random.nextInt()
    val ds = new EmbeddedDataSource

    ds.setDatabaseName(dbName)
    ds.setCreateDatabase("create")
    ds.withConnection(f)
  }

  def withDBUnitConnection[A](f: IDatabaseConnection => A): A = {
    withConnection { con =>
      f(new DatabaseConnection(con))
    }
  }

  implicit def con2con(implicit c: IDatabaseConnection): Connection = {
    c.getConnection
  }
}
