package nu.rinu.dbtest

import nu.rinu.dbtest.converter.FluentDTOConverter
import java.sql.Connection

/**
 * object to table data.
 *
 * one instance per one table.
 */
trait Converter {
  def getTableName(a: AnyRef): String

  def asMap(a: AnyRef): Map[String, Any]
}

object Converter {
  /**
   * TODO other types.
   */
  def createConverter(dto: AnyRef)(implicit con: Connection): Converter = {
    new FluentDTOConverter
  }
}
