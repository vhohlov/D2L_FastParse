package parser.modes.defaultModes

import fastparse.all._
import parser.Group._
import parser.Parser

/**
  * Created by vhohlov on 5/22/18.
  */
object Media extends Mode {

  override var group: Group = Substitution
  override var order: Int = 320
  override var allowedGroups: Array[Group] = Array()
  override var label: String = "Media"

  val allowedMedia: Seq[String] = Seq("gif", "jpg", "png")

  override def parser = image

  def image = P("{{" ~ s.? ~ src ~ skipParams ~ title.? ~ "}}").map {
    case (src, valid, _, title) => {
  //    println("s:!" + src +"!end")
  //    println("s:!" + valid +"!end")
      var latexMedia = ""

      if (valid == true) {
        Parser.io.writeMedia(src)

        latexMedia += "\n\\begin{figure}[!htb]\n"
        latexMedia += "\\centering\n"
        latexMedia += "\\includegraphics[max width=\\textwidth]{" + src + "}\n"

        if (!title.isEmpty && title.get != "")
          latexMedia += "\\caption{" + title.get + "}\n"

        latexMedia += "\\end{figure}\n"
      }
      else {
        var link = Parser.io.getDokuAddress() + src

        if (!title.isEmpty && title.get != "")
          latexMedia += "\\texttt{" + title.get + "} \\footnote{\\url{" + link + "}}"
        else
          latexMedia += "\\url{" + link + "}"
      }

      latexMedia
    }

  }//.log("image")

  def skipParams = P(!("|" | "}}") ~ content).rep//.log("skip")

  def params = P("?" ~ linking.? ~ "&".? ~ size.?)

  def linking = P("direct" | "nolink" | "linkOnly")

  def size = P(number.! ~ ("x" ~ number.!).?)

  def title = P("|" ~ (!"}}" ~ content).rep.!)


  def src = P((!"." ~ content).rep(1).! ~ "." ~ (!(srcEnd|s) ~ content).rep(1).!).map {
    case (name, format) => {
      if (allowedMedia.contains(format))
        (name + "." + format, true)
      else
        (name + "." + format, false)
    }
  }//.log("src")

  def srcEnd = P("}}" | "?" | "|")

  def s = P(" " | "\t").rep(1)


  //pentru fisierele care nu pot fi afisate, se creaza link unde pot fi gasite
  //se ignora plasarea imaginilor  in diferite pozitii. Doar pe centru
  //daca nu exista o dimensiune stabilita de doku, se pune dimensiunea imaginii in limita a textwidth
  //de transformat din pixel in points dimensiunea imaginii si de specificat in \includegraphiscs


}
