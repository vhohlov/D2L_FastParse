package parser.modes.defaultModes

import fastparse.all._
import parser.Group._

import scala.collection.mutable


object List extends Mode {

  override var group = Container
  override var order = 10
  override var allowedGroups = Array(Formatting, Protected, Substitution, Disabled)
  override var label: String = "List"

  //stiva ce contine proprietatile de identare si
  //tip a listelor deschise - (Indentation, ListType)
  var listStack = mutable.Stack[(Int, String)]()



  def parser = P(startElement ~ element.rep ~ endList).map {
    case (s, c, e) => s + c.mkString("") + e
  } //log ("Lista")


  def startElement = P(("\n" | Start) ~ indent ~ tag ~ listContent).map {
    case (indent, tag, content) => _startElement(indent, tag, content)
  }

  def element = P("\n" ~ indent ~ tag ~ listContent).map {
    case (indent, tag, content) => _element(indent, tag, content)
  }

  //\n semnalizeaza sfarsitul continutului unui element
  def listContent = P(!"\n" ~ content).rep.map {
    ctSeq => ctSeq.mkString("")
  }

  def indent = P(spaceIndent | tabIndent)

  //un element de lista e indentat cu minim 2 spatii
  def spaceIndent = P(" ").rep(min = 2).!.map(indent => indent.length - 2)

  //Un tab e considerat echivalent cu 2 spatii
  def tabIndent = P("\t").rep(min = 1).!.map(indent => indent.length * 2 - 2)

  //tipul listei
  def tag = P("*" | "-").!

  //identifica sfaristul intregului modul de liste
  def endList = P("\n" | End).map(_ => _endList)


  def _startElement(indent: Int, tag: String, content: String) = {

    listStack.clear()
    listStack.push((indent, tag))

    openList(indent, tag) + item(indent, content)
  }

  //adauga un nou element in modulul de liste
  def _element(indent: Int, tag: String, content: String) = {

    var lastIndent = listStack.top._1
    var lastTag = listStack.top._2

    //acelasi nivel de indentare cu elementul superior
    if (lastIndent == indent || lastIndent + 1 == indent) {

      //acelasi tip de lista ca si elementul superior
      if (lastTag == tag)
        item(lastIndent, content)

      //element de lista diferita
      else {
        listStack.pop
        // se deschide o lista noua cu indetarea elementului superior,
        // pentru a normaliza identarea in cazul in care nu se respecta regula de 2 spatii
        // la crearea unui element nou
        listStack.push((lastIndent, tag))

        closeList(lastIndent, lastTag) + openList(lastIndent, tag) + item(lastIndent, content)
      }
    }
    //se deschide o noua lista in cadrul listei curente
    else if (indent > lastIndent) {
      listStack.push((indent, tag))
      openList(indent, tag) + item(indent, content)
    }
    //lista curenta se inchide. Se adauga un element in lista precedenta
    else {
      var listComponents = ""

      while (lastIndent > indent && listStack.length > 1) {
        listComponents += closeList(lastIndent, lastTag)
        listStack.pop
        lastIndent = listStack.top._1
        lastTag = listStack.top._2
      }

      if (tag == lastTag)
        listComponents += item(lastIndent, content)
      else {
        listStack.pop
        listStack.push((lastIndent, tag))

        listComponents += closeList(lastIndent, lastTag)
        listComponents += openList(lastIndent, tag)
        listComponents += item(lastIndent, content)
      }

      listComponents
    }
  }

  //inchide toate listele ramase deschise
  def _endList() = {
    var closeLists = ""

    while (listStack.isEmpty == false) {
      //closeList(indent, tag)
      closeLists += closeList(listStack.top._1, listStack.top._2)
      listStack.pop
    }
    closeLists
  }


  def openList(indent: Int, tag: String) = {

    //lista neordonata
    if (tag == "*")
      "\n" + " " * indent + "\\begin{itemize}" + "\n"
    //lista ordonata
    else
      "\n" + " " * indent + "\\begin{enumerate}" + "\n"

  }

  def closeList(indent: Int, tag: String) = {

    //lista neordonata
    if (tag == "*")
      " " * indent + "\\end{itemize}" + "\n"
    //lista ordonata
    else
      " " * indent + "\\end{enumerate}" + "\n"
  }


  def item(indent: Int, content: String): String = {
    " " * indent  + "\t\\item" + " " + content + "\n"
  }

}
