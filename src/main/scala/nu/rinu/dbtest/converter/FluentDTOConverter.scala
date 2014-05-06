package nu.rinu.dbtest.converter

import com.google.common.base.CaseFormat._
import java.lang
import java.lang.reflect.Method
import nu.rinu.dbtest.Converter

/**
 * like lombok @Accessors(fluent = true)
 */
class FluentDTOConverter extends Converter {
  def getTableName(a: AnyRef): String = {
    val className = a.getClass.getSimpleName
    UPPER_CAMEL.to(LOWER_UNDERSCORE, className)
  }

  def asMap(a: AnyRef): Map[String, Any] = {
    val dtoClass = a.getClass
    val getters = getGetters(dtoClass)
    val map = getters.map { getter =>
      propertyToColumnName(getter.getName) -> getter.invoke(a)
    }.toMap
    map
  }

  private def propertyToColumnName(s: String): String = {
    LOWER_CAMEL.to(LOWER_UNDERSCORE, s)
  }

  private def getGetters[A <: AnyRef](dtoClass: Class[A]): Seq[Method] = {
    val setters = getSetters(dtoClass).map(_.getName).toSet
    val methods = dtoClass.getMethods
    for {method <- methods
         if method.getReturnType != lang.Void.TYPE
         if method.getParameterTypes.isEmpty
         if setters(method.getName)
    } yield method
  }

  private def getSetters[A <: AnyRef](dtoClass: Class[A]): Seq[Method] = {
    val methods = dtoClass.getMethods
    for {method <- methods
         if method.getReturnType == dtoClass
         if method.getParameterTypes.length == 1
    } yield method
  }
}
