package nu.rinu.dbgen.util

import nu.rinu.dbgen.core.{MetaDataReader, Table}
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource
import nu.rinu.sdb.Implicits._

object SimpleRunner {
  def apply(url: String,
            user: String,
            password: String)(f: Seq[Table] => Any) {
    val dataSource = new MysqlDataSource()
    dataSource.setUrl(url)
    dataSource.setUser(user)
    dataSource.setPassword(password)

    dataSource.withConnection { con =>
      val tables = new MetaDataReader(con).read()
      f(tables)
    }

  }

}

