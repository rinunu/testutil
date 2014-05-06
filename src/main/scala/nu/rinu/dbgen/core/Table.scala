package nu.rinu.dbgen.core

case class Table(
                  name: String,
                  tableType: String,
                  remarks: String,
                  columns: Seq[Column])
