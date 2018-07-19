package parser.modes.defaultModes

import fastparse.all._
import parser.Group._


trait Mode {

  var group: Group
  var order: Int
  var allowedGroups: Array[Group]
  var label: String;

  var content: Parser[String] = word | number | char

  def parser: Parser[String]


  def addMode(p: Parser[String]) = {
    content = p | content
  }

  def word = P(CharIn('a' to 'z', 'A' to 'Z')).rep(1).!

  def number = P(CharIn('0' to '9')).rep(1).!


  def char = P(AnyChar).!.map{
    char => {
      char match {
        case "$" => "\\$"
        case "%" => "\\%"
        case "{" => "\\{"
        case "_" => "\\_"
        case ">" => "\\textgreater "
        case "<" => "\\textless "
        case "#" => "\\#"
        case "&" => "\\&"
        case "}" => "\\}"
        case "\\" => "\\textbackslash "
        case _ => char
      }
    }
  }//.log("char")

}