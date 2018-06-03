package parser.modes.defaultModes

import fastparse.all._
import parser.Group._

/**
  * Created by vhohlov on 5/25/18.
  */
object Footnote extends Mode {
  override var group: Group = Formatting
  override var order: Int = 150
  override var allowedGroups: Array[Group] = Array(Container, Formatting, Substitution, Protected, Disabled)
  override var label: String = "Footnote"

  override def parser: Parser[String] = P("((" ~ footnoteContent ~ "))").map(
    note => s"\\footnote{$note}"
  )

  def footnoteContent  = P(!"))"~content).rep.map{
    ctSeq=>ctSeq.mkString("")
  }

}
