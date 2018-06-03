package parser.modes.defaultModes

import fastparse.all._
import parser.Group._


/**
  * Created by vhohlov on 5/13/18.
  */
object Code extends Mode{
  //usepackage{listings} - to be added to latex doc
  var group = Protected
  var order = 200
  var label = "Code"
  var allowedGroups = Array()


  def parser = P(indent|code|file|html|HTML|php|PHP)// log("Code")


  def indent = P(indentElem.rep(1) ~ indentEnd).map {
    codeLines => indentLatexCode(codeLines)
  }

  def indentElem = P(("\n"|Start) ~ " ".rep(min = 2) ~ indentContent)
  def indentContent = P(!"\n" ~ content).rep.!
  def indentEnd = P("\n"|End)


  def code = P(openingTag("code") ~ tagContent("code") ~ closingTag("code")).map{
    case (lang, content) => tagLatexCodeMinted(lang, content)
  }

  def file = P(openingTag("file") ~ tagContent("file") ~ closingTag("file")).map{
    case (lang, content) => tagLatexCodeMinted(lang, content)
  }

  def html = P(openingTag("html") ~ tagContent("html") ~ closingTag("html")).map{
    case (lang, content) => tagLatexCodeMinted(Some("html"), content)
  }

  def HTML = P(openingTag("HTML") ~ tagContent("HTML") ~ closingTag("HTML")).map{
    case (lang, content) => tagLatexCodeMinted(Some("html"), content)
  }

  def php = P(openingTag("php") ~ tagContent("php") ~ closingTag("php")).map{
    case (lang, content) => tagLatexCodeMinted(Some("php"), content)
  }

  def PHP = P(openingTag("PHP") ~ tagContent("PHP") ~ closingTag("PHP")).map{
    case (lang, content) => tagLatexCodeMinted(Some("php"), content)
  }


  def openingTag(tag:String) = P("<"~tag ~ language.? ~ endTag)
  def tagContent(tag:String) = P(!("</"~ tag ~">" ) ~ AnyChar).rep.!
  def closingTag(tag:String) = P("</" ~ tag ~ ">")

  //se ignora numele fisierului, in cazul in care exista
  def endTag = P(((!">") ~ AnyChar).rep ~ ">")
  def language = P(" ".rep ~ (!(" "|">") ~ AnyChar).rep(1).!)


  def indentLatexCode(codeLines:Seq[String])={

    "\n\\begin{lstlisting}\n" +
      codeLines.mkString("\n") +
      "\n\\end{lstlisting}\n"

  }

  //sudo apt-get install texlive-latex-extra - pentru minted package
  //sudo apt-get install python-pygments - minted necesita instalat pigments
  //pdflatex -shell-escape myfile.tex - trebuie adaugat -shell-escape la Tex run
  def tagLatexCodeMinted(langOption:Option[String], content:String) = {

    var lang = langOption.getOrElse("-")
    var setLanguage = "{text}"

    if(lang != "-")
      setLanguage = "{" + lang + "}"

    "\n\\begin{minted}"+ setLanguage + "\n" +
      content +
      "\n\\end{minted}\n"

  }


  def tagLatexCode(langOption: Option[String], content:String) = {

    var lang = langOption.getOrElse("-")
    var setLanguage = ""

    if(lang != "-")
      setLanguage = "[language=" + lang + "]"

    "\n\\begin{lstlisting}"+ setLanguage + "\n" +
      content +
      "\n\\end{lstlisting}\n"

  }
}
