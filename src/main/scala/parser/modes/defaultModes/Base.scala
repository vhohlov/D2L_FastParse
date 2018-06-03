package parser.modes.defaultModes

import fastparse.all._
import parser.Group._

/**
  * Created by vhohlov on 5/2/18.
  */
object Base extends Mode{
  var group: Group = Ungrouped
  var order: Int = 30
  var label = "Base"

  var allowedGroups = Array(BaseOnly, Formatting, Container, Substitution,
                            Protected, Disabled, Paragraph)



  def parser = P(content).rep(1).map {
    ct => ct.mkString("")
  }// log("Basic")

  //in base mode, at least two \n creates a new line in Latex
  //def whiteLine = P("\n" ~ &("\n")).rep(1).map(_ => "\\\\").log("WhiteLine")
}
