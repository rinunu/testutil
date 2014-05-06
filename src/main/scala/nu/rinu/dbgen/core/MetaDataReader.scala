package nu.rinu.dbgen.core

import java.sql.{Connection, DatabaseMetaData, ResultSet}
import nu.rinu.dbgen.core.Column


/**
 */
class MetaDataReader(connection: Connection) {

  def read(): Seq[Table] = {
    val meta = connection.getMetaData
    val result = meta.getTables(null, null, null, null)

    def loop(): List[Table] = {
      if (result.next()) {
        val table = result.getString("TABLE_NAME")
        val tableType = result.getString("TABLE_TYPE")
        val remarks = result.getString("REMARKS")
        val columns = loopColumns(meta.getColumns(null, null, table, null))
        Table(table, tableType, remarks, columns) :: loop()
      } else {
        Nil
      }
    }

    loop()
  }

  private def loopColumns(columns: ResultSet): List[Column] = {
    if (columns.next()) {
      val isNullable = columns.getString("IS_NULLABLE") match {
        case "YES" => true
        case "NO" => false
      }
      val nullable = columns.getInt("NULLABLE") match {
        case DatabaseMetaData.columnNullable => true
        case DatabaseMetaData.columnNoNulls => false
      }

      val column = Column(
        name = columns.getString("COLUMN_NAME"),
        dataType = SqlType(columns.getInt("DATA_TYPE")),
        typeName = columns.getString("TYPE_NAME"),
        columnSize = columns.getInt("COLUMN_SIZE"),
        remarks = columns.getString("REMARKS"),
        isNullable = nullable,
        isAutoIncrement = columns.getString("IS_AUTOINCREMENT") match {
          case "YES" => Some(true)
          case "NO" => Some(false)
          case _ => None
        }
      )

      column :: loopColumns(columns)
    } else {
      Nil
    }
  }
}
