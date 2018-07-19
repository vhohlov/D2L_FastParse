package parser.modes.plugins

import fastparse.all._
import parser.Group._

/**
  * Created by vhohlov on 7/8/18.
  */


class Note extends Plugin {

  override var enabled: Boolean = true
  override var group: Group = Container
  override var order: Int = 195
  override var allowedGroups: Array[Group] = Array(Container, Substitution, Protected, Disabled, Formatting, Paragraph)
  override var label: String = "Note"

  //define keywords used to identify type of note
  var noteImportant = Array("important", "importante")
  var noteWarning = Array("warning", "bloquante", "critique")
  var noteTip = Array("tip", "tuyau", "idée")
  var noteClassic = Array("", "classic", "classique")

  def parser: Parser[String] = P("<note" ~ noteType ~ ">" ~ noteContent ~ "</note>").map {
    case (noteId, content) => {
      var noteConfig = "\\begin{tcolorbox}"

      if (noteImportant.contains(noteId))
        noteConfig += "[colback=yellow!40, colframe=yellow!60, breakable]"
      else if (noteTip.contains(noteId))
        noteConfig += "[colback=cyan!5, colframe=cyan!10, breakable]"
      else if (noteWarning.contains(noteId))
        noteConfig += "[colback=red!15, colframe=red!30, breakable]"
      else
      //noteClassic is used for a wrong attemp to define a note type
        noteConfig += "[colback=blue!10, colframe=blue!20]"

      //return the according note type
      noteConfig + "\n" + content + "\n" + "\\end{tcolorbox}"
    }
  }

  //characters to be skipped arround the note's tags
  def skip = P(" " | "\t" | "\n").rep

  //a noteType identifier can contain at the start and end of tag unlimited spaces and new lines
  def noteType = P(skip ~ (!(skip ~ ">") ~ content).rep.! ~ skip).map(note => note.toLowerCase)

  //a note can contain another note inside it - (parser|content) as accepted  note content
  //spaces/newlines are skipped around tag just for formatting reasons
  def noteContent = P(skip ~ (!(skip ~ "</note>") ~ (parser | content)).rep ~ skip).map(content => content.mkString(""))

}
