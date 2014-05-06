package nu.rinu.dbunit

import org.dbunit.dataset._
import org.dbunit.database.IDatabaseConnection
import nu.rinu.dbtest._
import org.dbunit.operation.DatabaseOperation
import nu.rinu.dbtest.TableDiff
import scala.Some
import nu.rinu.dbtest.DataSetDiff
import org.dbunit.dataset.Column
import java.sql.Timestamp
import java.util.Date

/**
 * DBUnit Utils
 */
object DBUnitUtils {
  /**
   * Various DTOs to IDataSet.
   */
  def createDataSet(dtos: Array[AnyRef])(implicit connection: IDatabaseConnection): IDataSet = {
    val classDtoMap = dtos.groupBy(_.getClass)
    val tables = for {(dtoClass, values) <- classDtoMap} yield {
      createTable(values)
    }
    val dataSet = new DefaultDataSet()
    for {table <- tables} {
      dataSet.addTable(table)
    }
    dataSet
  }

  /**
   * Various DTOs to ITable.
   */
  def createTable(dtos: Array[AnyRef])(implicit connection: IDatabaseConnection): ITable = {
    implicit val c = connection.getConnection

    if (dtos.isEmpty) {
      sys.error("dtos is empty.")
    }

    val converter = Converter.createConverter(dtos(0))

    val meta = connection.createTable(converter.getTableName(dtos(0))).getTableMetaData
    val table = new DefaultTable(meta)
    for {(dto, row) <- dtos.zipWithIndex} {
      table.addRow()
      val map = converter.asMap(dto)
      for {(column, value) <- map} {
        table.setValue(row, column, value)
      }
    }
    table
  }

  def cleanInsert(dtos: AnyRef*)(implicit con: IDatabaseConnection) {
    val dataSet = DBUnitUtils.createDataSet(dtos.toArray)
    DatabaseOperation.CLEAN_INSERT.execute(con, dataSet)
  }

  def cleanInsert(dtos: Array[AnyRef])(implicit con: IDatabaseConnection) {
    cleanInsert(dtos: _*)
  }

  def insert(dtos: AnyRef*)(implicit con: IDatabaseConnection) {
    val dataSet = DBUnitUtils.createDataSet(dtos.toArray)
    DatabaseOperation.INSERT.execute(con, dataSet)
  }

  def insert(dtos: Array[AnyRef])(implicit con: IDatabaseConnection) {
    insert(dtos: _*)
  }

  def snapshot(tablePattern: Filter[String])(implicit con: IDatabaseConnection): IDataSet = {
    val dbDataSet = con.createDataSet()

    val filteredDataSet = new FilteredDataSet(
      dbDataSet.getTableNames.filter(tablePattern),
      dbDataSet)

    val tables = for {name <- filteredDataSet.getTableNames} yield
      filteredDataSet.getTable(name)

    new DefaultDataSet(tables)
  }

  /**
   * diff two IDataSets.
   */
  def diff(a: IDataSet, b: IDataSet): DataSetDiff = {
    val tableNames = a.getTableNames.toSet
    val bTableNames = b.getTableNames.toSet
    if (tableNames != bTableNames) sys.error("a schema != b schema")

    val tables = for {tableName <- tableNames} yield {
      println("diff", tableName)
      diffTable(a.getTable(tableName), b.getTable(tableName))
    }

    DataSetDiff(tables.flatten.toSet)
  }

  private type ColumnNameValue = (String, Any)
  private type ColumnValue = (Column, Any)
  private type PKs = Seq[ColumnNameValue]

  /**
   *
   */
  private def diffTable(a: ITable, b: ITable): Option[TableDiff] = {
    val meta = a.getTableMetaData
    val pkColumns = meta.getPrimaryKeys
    val columns = meta.getColumns

    def toJavaValue(value: AnyRef) = {
      value match {
        case a: Timestamp => new Date(a.getTime)
        case a: java.math.BigInteger => a.longValue()
        case _ => value
      }
    }

    def getColumnValues(table: ITable, row: Int, columns: Iterable[Column]): Seq[ColumnNameValue] = {
      columns.map { column =>
        column.getColumnName -> toJavaValue(table.getValue(row, column.getColumnName))
      }.toSeq
    }

    def toMap(table: ITable): Map[PKs, Seq[ColumnNameValue]] = {
      (for {row <- 0 until table.getRowCount} yield {
        val pks = getColumnValues(table, row, pkColumns)
        pks -> getColumnValues(table, row, columns)
      }).toMap
    }

    val aMap = toMap(a)
    val bMap = toMap(b)
    val deletes = for {(pks, aValues) <- aMap
                       bValuesOpt = bMap.get(pks) if bValuesOpt.isEmpty} yield {
      DeleteRow(pks)
    }

    val updates = for {(pks, aValues) <- aMap
                       bValues <- bMap.get(pks)
                       diff = diffRow(aValues, bValues) if !diff.isEmpty
    } yield {
      UpdateRow(pks, diff)
    }

    val inserts = for {(pks, bValues) <- bMap
                       aValuesOpt = aMap.get(pks) if aValuesOpt.isEmpty} yield {
      InsertRow(bValues)
    }

    val rows = updates ++ deletes ++ inserts
    if (rows.isEmpty)
      None
    else
      Some(TableDiff(meta.getTableName, rows.toSet))
  }

  private def diffRow(a: Seq[ColumnNameValue], b: Seq[ColumnNameValue]): Seq[ColumnNameValue] = {
    val aMap = a.toMap

    b.filter { case (column, bValue) => aMap(column) != bValue}
  }
}
