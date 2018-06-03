package parser.modes.defaultModes

import fastparse.all._
import parser.Group._



object TextStyle extends Mode{

  var group = Formatting
  var order = 70
  var allowedGroups = Array(Formatting, Substitution, Disabled, Protected)
  var label = "TextStyle"

  //content = falseParagraph|content


  var parser = P(bold|underline|monospaced|deleted|italic|subscript|suprascript).rep(1).map {
    ct => ct.mkString("")
  } //log("textStyle")

  def falseParagraph = P("\n").rep(1).map(_=> "\n")

  def enFormat(inTag:String, outTag:String) = P(inTag ~ &((!outTag ~ AnyChar).rep ~ inTag))

  def disFormat(outTag:String) = P(outTag|End)

  def ctFormat(outTag:String)  = P(!disFormat(outTag)~content).rep.map{
    ctSeq=>ctSeq.mkString("")
  }

  def underline = P(enFormat("__", "__") ~ ctFormat("__") ~ disFormat("__").?) map{
    ct => "\\underline{"+ ct + "}"
  }

  def bold = P(enFormat("**", "**") ~ ctFormat("**") ~ disFormat("**").?) map{
    ct => "\\textbf{"+ ct + "}"
  }

  def monospaced = P(enFormat("''", "''")~ctFormat("''") ~ disFormat("''").?) map{
    ct => "\\texttt{"+ ct + "}"
  }

  def deleted = P(enFormat("<del>", "/<del>") ~ ctFormat("<del>") ~ disFormat("</del>").?) map{
    ct => "\\sout{"+ ct + "}"
  }

  def subscript  = P(enFormat("<sub>", "/<sub>") ~ ctFormat("<sub>") ~ disFormat("</sub>").?) map{
    ct => "\\textsubscript{"+ ct + "}"
  }

  def suprascript = P(enFormat("<sup>", "/<sup>") ~ ctFormat("<sup>") ~ disFormat("</sup>").?) map{
    ct =>"\\textsuperscript{"+ ct + "}"
  }

  def italic = P("//" ~ ctFormat("//") ~ disFormat("//").?) map{
    ct => "\\textit{"+ ct + "}"
  }

}

