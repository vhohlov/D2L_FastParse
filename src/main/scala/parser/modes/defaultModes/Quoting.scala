package parser.modes.defaultModes

import fastparse.all._
import parser.Group._


/**
  * Created by vhohlov on 6/1/18.
  */

object Quoting extends Mode {
  override var group: Group = Container
  override var order: Int = 220
  override var allowedGroups: Array[Group] = Array(Formatting, Substitution, Disabled, Protected)
  override var label: String = "Quoting"

  override def parser: Parser[String] = P(quote)//.log(label)

  //qoute has an env with typewriter font and no paragraph indentation
  def quote = P(quoteElem.rep(1) ~ quoteEnd).map { quotes =>

    "\\begin{tt}\n" +
      "\\noindent" +
      """\\""" + "\n" +
      quotes.mkString("""\\""" + "\n") +
      """\\""" + "\n" +
      "\\end{tt}"
  }

  // \verb$replyTag$ reply_content\\
  def quoteElem = P(("\n" | Start) ~ ">".rep(1).! ~ quoteContent).map {
    case (reply, content) => "\\verb$" + reply + "$" + content
  }

  //text until new line is the tag content
  def quoteContent = P(!"\n" ~ content).rep.map {
    ctSeq => ctSeq.mkString("")
  }

  def quoteEnd = P("\n" | End)

}