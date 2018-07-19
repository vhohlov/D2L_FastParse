package parser.modes.defaultModes

import fastparse.all._
import parser.Group._

/*
* Does not use any special latex env for printing raw
* Just escapes the Latex special chars
* */
object Unformatted extends Mode {

  override var group: Group = Disabled
  override var order: Int = 178
  override var allowedGroups: Array[Group] = Array()
  override var label: String = "Unformatted"

  override def parser = P(tag | %%).map(txt => txt)

  def tag = P("<nowiki>" ~ unfContent("</nowiki>") ~ "</nowiki>")
  def %% = P("%%" ~ unfContent("%%") ~ "%%")


  def unfContent(end:String) = P(!end ~ (oneSpace|content)).rep.map{
    ctSeq=>ctSeq.mkString("")
  }
  //multiple new lines and spaces are ignored
  def oneSpace = P(" "| "\n" | "\t").rep(1). map (_ => " ")//log("space")

}

