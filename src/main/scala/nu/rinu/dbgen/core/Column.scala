package nu.rinu.dbgen.core

import nu.rinu.dbgen.core.SqlType

case class Column(
                   name: String,
                   dataType: SqlType,
                   typeName: String,
                   columnSize: Int,
                   isNullable: Boolean,
                   remarks: String,
                   isAutoIncrement: Option[Boolean]
                   )
