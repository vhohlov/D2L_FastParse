package parser.modes.defaultModes

import fastparse.all._
import parser.Group._


/*
* Used package:
* ->\tcbuselibrary{listings}
* --->for printing code with listings inside tcolorboxes
* --->package also loads listings for global use
* ->\tcbuselibrary{breakable}
* --->allows to break tcolorboxes over multiple pages
* Settings for listing env:
* lstset{
*    breaklines=true,
*    numbers=left,
*    numberstyle=\\small
*    numbersep=8pt,
*    xleftmargin=10pt,
*    escapeinside=&&
*    }
* */
object Code extends Mode{

  var group = Protected
  var order = 200
  var label = "Code"
  var allowedGroups = Array()


  def parser = P(indent|code|file|html|HTML|php|PHP)// log("Code")

  /*code that is created by identing with space in dokuwiki*/
  def indent = P(indentElem.rep(1) ~ indentEnd).map {
    codeLines => indentLatexCode(codeLines)
  }

  //element on indented code
  def indentElem = P(("\n"|Start) ~ " ".rep(min = 2) ~ indentContent)
  //get all source chars that are found until the end of indented code element
  def indentContent = P(!"\n" ~ srcCode).rep.map{
    ctSeq=>ctSeq.mkString("")
  }
  def indentEnd = P("\n"|End)

  /*code that is created by using specific tags*/
  def code = P(openingTag("code") ~ tagContent("code") ~ closingTag("code")).map{
    case (lang, content) => tagLatexCode(lang, content)
  }

  def file = P(openingTag("file") ~ tagContent("file") ~ closingTag("file")).map{
    case (lang, content) => tagLatexCode(lang, content)
  }

  def html = P(openingTag("html") ~ tagContent("html") ~ closingTag("html")).map{
    case (lang, content) => tagLatexCode(Some("html"), content)
  }

  def HTML = P(openingTag("HTML") ~ tagContent("HTML") ~ closingTag("HTML")).map{
    case (lang, content) => tagLatexCode(Some("html"), content)
  }

  def php = P(openingTag("php") ~ tagContent("php") ~ closingTag("php")).map{
    case (lang, content) => tagLatexCode(Some("php"), content)
  }

  def PHP = P(openingTag("PHP") ~ tagContent("PHP") ~ closingTag("PHP")).map{
    case (lang, content) => tagLatexCode(Some("php"), content)
  }

  //opening tag for code section
  def openingTag(tag:String) = P("<"~tag ~ language.? ~ endTag)
  //get all source chars that are found until the closing tag
  def tagContent(tag:String) = P(!("</"~ tag ~">" ) ~ srcCode).rep.map{
    ctSeq=>ctSeq.mkString("")
  }
  //closing tag for code section
  def closingTag(tag:String) = P("</" ~ tag ~ ">")

  //ignore the file name with code to download, in case it is defined
  def endTag = P(((!">") ~ AnyChar).rep ~ ">")

  //get the language in which the code is written
  def language = P(" ".rep ~ (!(" "|">") ~ AnyChar).rep(1).!)

  /*the source code that must be shown in latex*/
  def srcCode = P(word|number|allowedChars|escapeChar|specialChar)
  //chars from source code that do not have to be escaped
  def allowedChars = P(CharIn(" ~`!@#$%^*()_-=+{}[]\\|:;'\",./<>?\t\n")).!
  //chars from source code that must be escaped with delimiter & defined in lstset
  def specialChar = P(AnyChar.!).map(char => s"&$char&")
  //escaped delimiter & when used in source code
  def escapeChar = P("&").map(_ => "&\\&&")


  //latex formatting for code defined with indentation
  def indentLatexCode(codeLines:Seq[String])={

    "\n\\begin{tcblisting}{arc=0pt, outer arc=0pt, listing only, breakable}" +
      codeLines.mkString("\n") +
      "\n\\end{tcblisting}\n"

  }

  //latex formatting for code defined with tags
  //currently language parameter is ignored
  def tagLatexCode(langOption: Option[String], content:String) = {

    var lang = langOption.getOrElse("-")
    var setLanguage = ""

    // if(lang != "-")
    //  setLanguage = s"listing options={language=$lang},"

    s"\n\\begin{tcblisting}{$setLanguage arc=0pt, outer arc=0pt, listing only, breakable}" +
      content +
      "\n\\end{tcblisting}\n"

  }
}
