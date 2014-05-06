package nu.rinu.dbgen.util

import nu.rinu.dbgen.core.{MetaDataReader, Table}
import nu.rinu.sdb.Implicits._
import javax.sql.DataSource

object SimpleRunner {
  def apply(dataSource: DataSource)
           (f: Seq[Table] => Any) {

    dataSource.withConnection { con =>
      val tables = new MetaDataReader(con).read()
      f(tables)
    }

  }

}

