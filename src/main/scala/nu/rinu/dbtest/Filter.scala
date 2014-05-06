package nu.rinu.dbtest

trait Filter[A] extends (A => Boolean) {
  def apply(s: A): Boolean
}

case class TableColumn(table: String, column: String)

/**
  */
object Filter {

  /**
   * always matches
   */
  def anything[A]: Filter[A] = {
    new Filter[A] {
      override def apply(s: A): Boolean = {
        true
      }
    }
  }

  def regexTableColumnFilter(tablePattern: String, columnPattern: String): Filter[TableColumn] = {
    new Filter[TableColumn] {
      val TableRegex = tablePattern.r
      val ColumnRegex = columnPattern.r

      override def apply(s: TableColumn): Boolean = {
        (s.table, s.column) match {
          case (TableRegex(), ColumnRegex()) => true
          case _ => false
        }
      }
    }
  }

  def regexColumnFilter(columnPattern: String): Filter[TableColumn] = {
    new Filter[TableColumn] {
      val ColumnRegex = columnPattern.r

      override def apply(s: TableColumn): Boolean = {
        s.column match {
          case ColumnRegex() => true
          case _ => false
        }
      }
    }
  }


  def regexps(patterns: String*): Filter[String] = {
    regexps(patterns.toIterable)
  }

  def regexps(patterns: Iterable[String]): Filter[String] = {
    new Filter[String] {
      val regexps = patterns.map(_.r)

      override def apply(s: String): Boolean = {
        regexps.exists { R => R.unapplySeq(s).isDefined}
      }
    }
  }

  def not[A](pattern: Filter[A]): Filter[A] = {
    new Filter[A] {
      override def apply(s: A): Boolean = {
        !pattern(s)
      }
    }
  }

  def and[A](patterns: Filter[A]*): Filter[A] = {
    new Filter[A] {
      override def apply(s: A): Boolean = {
        patterns.forall(_(s))
      }
    }
  }

  def or[A](patterns: Filter[A]*): Filter[A] = {
    new Filter[A] {
      override def apply(s: A): Boolean = {
        patterns.exists(_(s))
      }
    }
  }


  /**
   * for java
   */
  def regexps(patterns: Array[String]): Filter[String] = {
    regexps(patterns.toIterable)
  }

  /**
   * for java
   */
  def and[A](patterns: Array[Filter[A]]): Filter[A] = {
    and(patterns: _*)
  }

  /**
   * for java
   */
  def or[A](patterns: Array[Filter[A]]): Filter[A] = {
    or(patterns: _*)
  }
}