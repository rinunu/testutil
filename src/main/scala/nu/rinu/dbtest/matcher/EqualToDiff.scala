package nu.rinu.dbtest.matcher

import org.hamcrest.{Description, Matcher, Factory, TypeSafeMatcher}
import nu.rinu.dbtest._
import nu.rinu.dbtest.DataSetDiff

/**
 * Matcher for DataSetDiff
 */
class EqualToDiff(operand: DataSetDiff, options: DiffOptions) extends TypeSafeMatcher[DataSetDiff] {
  override def matchesSafely(item: DataSetDiff): Boolean = {
    filter(item) == filter(operand)
  }

  override def describeMismatchSafely(item: DataSetDiff, mismatchDescription: Description): Unit = {
    // for IDEA JUnit viewer.
    mismatchDescription.appendText("was ").appendValue(filter(item).toString)
  }

  override def describeTo(description: Description): Unit = {
    description.appendValue(filter(operand).toString)
  }

  private def filter(diff: DataSetDiff) = {
    diff.filter(options.columnPatterns)
  }
}

object EqualToDiff {
  /**
   * for java
   */
  @Factory
  def equalToDiff(operand: DataSetDiff): Matcher[DataSetDiff] = {
    new EqualToDiff(operand, DiffOptions(Filter.anything))
  }

  @Factory
  def equalToDiff(operand: DataSetDiff,
                  options: DiffOptions): Matcher[DataSetDiff] = {
    new EqualToDiff(operand, options)
  }
}

