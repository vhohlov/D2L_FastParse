package parser.modes.defaultModes

import fastparse.all._
import parser.Group._


object Header extends Mode{

  var group = BaseOnly
  var allowedGroups = Array()
  var order = 50
  var label ="Header"
  var toc = 0


  def parser = P(startHeader ~ title ~ endHeader).map{
    case(hType, content) => genLatexHeader(hType, content)
  }//log("header")

  //Titlul header-lui nu poate avea \n
  def title = P(!("\n"|endHeader) ~ content).rep.!

  //Intoarce nr de = pentru a determina tipul header ului
  def startHeader = P("=").rep(min = 2).!.map(hType => hType.length)// log()

  def endHeader =  P("=".rep(min = 2) ~ ("\t"|" ").rep ~ &("\n"|End))// log()


  def genLatexHeader(hType: Int, content:String): String = {

    //contorizare nr de aparitii a primelor 3 headere
    if (hType > 3)
      toc += 1

    hType match {
        case 2 => "\\subparagraph{"  + content + "}\\hfill\\\\"
        case 3 => "\\paragraph{"     + content + "}\\hfill\\\\"
        case 4 => "\\subsubsection{" + content + "}"
        case 5 => "\\subsection{"  + content + "}"
        case _ => "\\section{"  + content + "}"
      }
  }
}
