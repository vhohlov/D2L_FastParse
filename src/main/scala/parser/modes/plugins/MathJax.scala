package parser.modes.plugins

import fastparse.all
import fastparse.all._
import parser.Group._

//use  amsmath for *env
class MathJax extends Plugin {

  override var enabled: Boolean = false
  override var group: Group = Protected
  override var order: Int = 65
  override var allowedGroups: Array[Group] = Array(Protected, Substitution, Formatting, Container)
  override var label: String = "Mathjax"

  override def parser: all.Parser[String] = P(inline | newline | numberedEnv | unNumberedEnv)//.log("Mathjax")

  def inline = P(inline_$ | inline_p)

  def inline_$ = P("$" ~ mathText("$")~ "$").!

  def inline_p = P("\\(" ~ mathText("\\)") ~ "\\)").!

  def newline = P(newline_$$ | newline_p)

  def newline_$$ = P("$$" ~ mathText("$") ~ "$$").!

  def newline_p = P("\\[" ~ mathText("\\]") ~ "\\]").!


  def numberedEnv = env("align") | env("alignat") | env("eqnarray") | env("equation") |
    env("flalign") | env("gather") | env("multline") | env("displaymath") | env("math")

  def unNumberedEnv = env("align*") | env("alignat*") | env("eqnarray*") | env("equation*") |
    env("flalign*") | env("gather*") | env("multline*")

  def env(envType: String) = P(s"\\begin{$envType}" ~ mathText("\\end") ~ s"\\end{$envType}").!

  def mathText(s:String) = P(!s ~ AnyChar).rep(1)


//  def mathText(s:String) = P(!s ~ (supraAsterix|content)).rep(1).!.map{
//    ctSeq=>ctSeq.mkString("")
//  }
//  def supraAsterix = P("^∗").map(_=>"^{∗}")
}