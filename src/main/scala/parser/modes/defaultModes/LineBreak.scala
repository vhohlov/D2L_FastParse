package parser.modes.defaultModes

import fastparse.all._
import parser.Group._


/**
  * Created by vhohlov on 7/24/18.
  */
object LineBreak extends Mode{
  override var group: Group = Substitution
  override var order: Int = 140
  override var allowedGroups: Array[Group] = Array()
  override var label: String = "LineBreak"

  override def parser: Parser[String] = P("\\\\" ~ CharIn(" ", "\t", "\n")).map(_ => "\\\\")
}
