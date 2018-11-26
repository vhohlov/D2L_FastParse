package parser.modes.plugins

import fastparse.all._
import parser.Group._

/**
  * Created by vhohlov on 8/1/18.
  */
/*
\newtcbtheorem{_def}{Definition}{breakable}{df}
\newtcbtheorem{prop}{Proposition}{breakable}{prp}
\newtcbtheorem{_proof}{Proof}{breakable}{prf}
\newtcbtheorem{remark}{Remark}{breakable}{rmk}
\newtcbtheorem{example}{Example}{breakable}{ex}
\newtcbtheorem{theorem}{Theorem}{breakable}{th}
\newtcbtheorem{exercise}{Exercise}{breakable}{exer}
\newtcbtheorem{solution}{Solution}{breakable}{sol}
\newtcbtheorem{algorithm}{Algorithm}{breakable}{alg}
*/
class Macros extends Plugin{

  override var enabled: Boolean = true
  override var group: Group = Substitution
  override var order: Int = 60
  override var allowedGroups: Array[Group] = Array(Formatting, Container, Substitution)
  override var label: String = "Macros"

  override def parser: Parser[String] = P(comment | mathLine| titledMath| unTitledMath|solution)

  def comment = P("{{##" ~ (!("##}}") ~ AnyChar).rep ~ "##}}").map(_ => "")

  def mathLine = P("$math[" ~ mathText("]") ~ "]").map(math => "$" + math + "$")//.log("mathematicLine")


  def mathText(s:String) = P(!s ~ (y|supraAsterix|AnyChar.!)).rep.map{
    ctSeq=>ctSeq.mkString("")
  }
  //some hacks for wrong used grammar for macro
  def supraAsterix = P("^∗").map(_=>"^{∗}")
  def y = P("\\y").map(_=> "\\textbackslash y")

  def titledMath = P("$" ~ mathStatement ~ title.? ~ body ~ "$end").map{
    case(macroType, title, body) => {

     val completeLatex = latexMathMacro(_:String, title.getOrElse(""), body)

      macroType match {
        case "def"    => completeLatex("_def")
        case "proof"  => completeLatex("_proof")
        case "proposition" => completeLatex("prop")
        case _ => completeLatex(macroType)
      }
    }
  }

  def unTitledMath = P("$just" ~ mathStatement ~ body ~ "$end").map{
    case(macroType, body) => {

      val completeLatex = latexMathMacro(_:String, "", body)

      macroType match {
        case "def"    => completeLatex("_def")
        case "proof"  => completeLatex("_proof")
        case "proposition" => completeLatex("prop")
        case _ => completeLatex(macroType)
      }
    }
  }

  def mathStatement = P(StringIn("theorem","def", "proposition", "prop", "proof","remark","example","exercise", "exercise", "algorithm").!)

  def latexMathMacro(mType:String , title:String, body:String): String = {
    s"\\begin{$mType*}{$title}\n$body\n\\end{$mType*}"
  }

  def solution = P("$sol" ~ text("$solend") ~ "$solend").map(
    text => s"\\begin{solution*}{}\n$text\n\\end{solution*}"
  )

  def title = P("[" ~ text("]") ~ "]");

  def body = P(text("$end"))

  def text(s:String) = P(!s ~ (parser|content)).rep.map{
    ctSeq=>ctSeq.mkString("")
  }


}
