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

  override def parser: all.Parser[String] = P(math| mathJax) //log (label)

  def math : Parser[String] = P(_def| prop | proof | theorem | justTheorem |mathLine)
  def mathJax : Parser[String] = P(inline | newline | numberedEnv | unNumberedEnv)


  def inline = P(inline_$ | inline_p)

  def inline_$ = P("$" ~ (!"$" ~ content).rep ~ "$").!

  def inline_p = P("\\(" ~ (!"\\)" ~ content).rep ~ "\\)").!

  def newline = P(newline_$$ | newline_p)

  def newline_$$ = P("$$" ~ (!"$" ~ content).rep ~ "$$").!

  def newline_p = P("\\[" ~ (!"\\]" ~ content).rep ~ "\\]").!


  def numberedEnv = env("align") | env("alignat") | env("eqnarray") | env("equation") |
    env("flalign") | env("gather") | env("multline") | env("displaymath") | env("math")

  def unNumberedEnv = env("align*") | env("alignat*") | env("eqnarray*") | env("equation*") |
    env("flalign*") | env("gather*") | env("multline*")

  def env(envType: String) = P(s"\\begin{$envType}" ~ (!"\\end" ~ content).rep ~ s"\\end{$envType}").!


  def mathLine = P("$math[" ~ (!"]" ~ content).rep.! ~ "]").map(math => "$" + math + "$")


  def _def = P("$def[" ~ text("]") ~ "]" ~ text("$end") ~ "$end").map{
    case (name, content) => {
      "\\begin{theorem}[" + name + "]\n" + content + "\n\\end{theorem}"
    }
  }

  def prop = P("$prop[" ~ text("]") ~ "]" ~ text("$end") ~ "$end").map{
    case (name, content) => {
      "\\begin{lemma}[" + name + "]\n" + content + "\n\\end{lemma}"
    }
  }

  def proof = P("$proof" ~ text("$end") ~ "$end").map{
    case (content) => {
      "\\begin{proof}\n" + content + "\n\\end{proof}"
    }
  }

  def justTheorem = P("$justtheorem" ~ text("$end") ~ "$end").map{
    case (content) => content
  }

  def theorem = P("$theorem[" ~ text("]") ~ "]" ~ text("$end") ~ "$end").map{
    case (name, content) => {
      "\\begin{theorem}[" + name + "]\n" + content + "\n\\end{theorem}"
    }
  }

  def text(s:String) = P(!s ~ (math|AnyChar)).rep(1).map{
    ctSeq=>ctSeq.mkString("")
  }

}