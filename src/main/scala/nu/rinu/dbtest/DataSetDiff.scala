package nu.rinu.dbtest

case class DataSetDiff(tables: Set[TableDiff]) {
  def filter(columnPattern: Filter[TableColumn]): DataSetDiff = {
    copy(tables = tables.map(_.filter(columnPattern)))
  }

  override def toString: String = {
    tables.toSeq.sortBy(_.name).map { table =>
      table.toString
    }.mkString("\n\n")
  }
}


case class TableDiff(name: String, rows: Set[RowDiff]) {
  def filter(columnPattern: Filter[TableColumn]): TableDiff = {
    copy(rows = rows.map(_.filter(columnPattern, name)))
  }

  override def toString: String = {
    s"$name\n" +
      rows.map { row =>
        row.toString
      }.toSeq.sorted.mkString("\n")
  }
}

sealed trait RowDiff {
  def filter(columnPattern: Filter[TableColumn], table: String): RowDiff = {
    def filter(columnValues: Seq[(String, Any)]) = {
      columnValues.filter { case (column, value) =>
        columnPattern(TableColumn(table, column))
      }
    }

    this match {
      case a: InsertRow =>
        a.copy(filter(a.values))
      case a: DeleteRow =>
        a.copy(filter(a.pks))
      case a: UpdateRow =>
        a.copy(filter(a.pks), filter(a.values))
    }
  }

  override def toString: String = {
    def toString(columnValues: Seq[(String, Any)]) = {
      columnValues.map { case (column, value) => s"$column: $value"}.mkString(", ")
    }

    this match {
      case InsertRow(values) =>
        s"  [insert] " + toString(values)
      case DeleteRow(pks) =>
        s"  [delete] ${toString(pks)}"
      case UpdateRow(pks, values) =>
        s"  [update] ${toString(pks)} => ${toString(values)}"
    }
  }
}

case class DeleteRow(pks: Seq[(String, Any)]) extends RowDiff

case class InsertRow(values: Seq[(String, Any)]) extends RowDiff

case class UpdateRow(pks: Seq[(String, Any)], values: Seq[(String, Any)]) extends RowDiff
