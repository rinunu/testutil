package nu.rinu.dbgen.core

import java.sql.Types

sealed trait SqlType

object SqlType {
  def apply(raw: Int): SqlType = {
    raw match {
      case Types.INTEGER => SqlType.Integer

      case Types.DOUBLE => SqlType.Double // 8

      case Types.DATE => SqlType.Date // 91
        
      case Types.TINYINT => SqlType.TinyInt
        
      case Types.VARCHAR => SqlType.Varchar
        
      case Types.BIGINT => SqlType.BigInt
      case Types.SMALLINT => SqlType.SmallInt
        
      case Types.REAL => SqlType.Real
      case Types.BIT => SqlType.Bit
      case Types.LONGVARCHAR => SqlType.LongVarchar
      case Types.VARBINARY => SqlType.VarBinary
      case Types.CHAR => SqlType.Char
      case Types.TIMESTAMP => SqlType.Timestamp
        
      case _ =>
        // TODO
        sys.error("unknown data type: " + raw)
    }
  }
  
  // TODO 網羅
  
  // number
  case object BigInt extends SqlType
  case object Bit extends SqlType
  case object SmallInt extends SqlType
  case object Integer extends SqlType
  case object TinyInt extends SqlType
  
  // string
  case object Varchar extends SqlType
  case object Char extends SqlType
  case object LongVarchar extends SqlType

  // date
  case object Timestamp extends SqlType
  case object Date extends SqlType

  // float
  case object Double extends SqlType
  case object Real extends SqlType

  case object VarBinary extends SqlType
}

/**
 * for java
 */
object SqlTypes {
  val BigInt = SqlType.BigInt
}
