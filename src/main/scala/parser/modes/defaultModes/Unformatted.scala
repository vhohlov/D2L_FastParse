package parser.modes.defaultModes

import fastparse.all._
import parser.Group._


object Unformatted extends Mode {
  override var group: Group = Disabled
  override var order: Int = 178
  override var allowedGroups: Array[Group] = Array()
  override var label: String = "Unformatted"

  override def parser = P(tag | %%).map { txt => "\\verb$" + txt + "$"}//.log(label)

  def tag = P("<nowiki>" ~ unfContent("</nowiki>") ~ "</nowiki>")

  def %% = P("%%" ~ unfContent("%%") ~ "%%")


  def unfContent(end:String) = P(!end ~ (delimiter|oneSpace|content)).rep.map{
    ctSeq=>ctSeq.mkString("")
  }
  //ignora spatiile multiple si liniile noi
  def oneSpace = P(" "| "\n" | "\t").rep(1). map (_ => " ").log("space")

  //daca textul contine delimitatorul folosit in \verb
  //se inchide \verb ul curent
  //se afiseaza delimitatorul
  //se deschide alt \verb
  def delimiter = P("$").map( _ => "$\\$\\verb$")
}

