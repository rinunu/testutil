package nu.rinu.dbtest

import java.sql.Connection
import org.dbunit.database.DatabaseConnection
import org.dbunit.dataset.{Column, ITableMetaData}


class DiffBuilderOps() {

  private sealed trait Op {
    val value: AnyRef
  }

  private case class Insert(value: AnyRef) extends Op

  private case class Delete(value: AnyRef) extends Op

  private case class Update(value: AnyRef) extends Op

  private var ops = Seq[Op]()

  def insert(values: AnyRef*): DiffBuilderOps = {
    ops ++= values.map(Insert)
    this
  }

  def delete(values: AnyRef*): DiffBuilderOps = {
    ops ++= values.map(Delete)
    this
  }

  def update(values: AnyRef*): DiffBuilderOps = {
    ops ++= values.map(Update)
    this
  }

  /**
   * for java
   */
  def insert(values: Array[AnyRef]): DiffBuilderOps = insert(values: _*)

  /**
   * for java
   */
  def delete(values: Array[AnyRef]): DiffBuilderOps = delete(values: _*)

  /**
   * for java
   */
  def update(values: Array[AnyRef]): DiffBuilderOps = update(values: _*)

  def build()(implicit con: Connection): DataSetDiff = {
    val classToOpsMap = ops.groupBy(_.value.getClass)
    val tableDiffs = for {(dtoClass, ops) <- classToOpsMap} yield
      toDiffs(dtoClass, ops)
    DataSetDiff(tableDiffs.toSet)
  }

  private def toDiffs(dtoClass: Class[_], ops: Iterable[Op])
                     (implicit con: Connection): TableDiff = {
    val sample = ops.head.value
    val converter = Converter.createConverter(sample)
    val tableName = converter.getTableName(sample)

    val dbUtilCon = new DatabaseConnection(con)
    // TODO ホントはメタデータさえあればいいので、ちょっと無駄
    val meta = dbUtilCon.createDataSet().getTable(tableName).getTableMetaData

    TableDiff(tableName, ops.map(toRowDiff(converter, meta, _)).toSet)
  }

  private def toRowDiff(converter: Converter, meta: ITableMetaData, op: Op): RowDiff = {
    val pkColumns = meta.getPrimaryKeys
    val pkColumnSet = pkColumns.toSet
    val allColumns = meta.getColumns
    val notPkColumns = allColumns.filter(!pkColumnSet(_))
    val columnValueMap = converter.asMap(op.value).map { case (k, v) => k.toLowerCase -> v}

    def getColumnValues(columns: Iterable[Column]): Seq[(String, Any)] = {
      for {column <- columns.toSeq
           columnName = column.getColumnName
           value <- columnValueMap.get(columnName.toLowerCase) if value != null
      } yield {
        columnName -> value
      }
    }

    op match {
      case Insert(dto) => InsertRow(getColumnValues(allColumns))
      case Update(dto) => UpdateRow(getColumnValues(pkColumns), getColumnValues(notPkColumns))
      case Delete(dto) => DeleteRow(getColumnValues(pkColumns))
    }
  }
}


/**
 * Builder for DataSetDiff
 */
object DiffBuilder extends DiffBuilderOps {
}