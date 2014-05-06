package nu.rinu.dbgen.core

case class Column(
                   name: String,
                   dataType: SqlType,
                   typeName: String,
                   columnSize: Int,
                   isNullable: Boolean,
                   remarks: String,
                   isAutoIncrement: Option[Boolean]
                   )
