package parser


import fastparse.all._

import scala.collection.mutable.ArrayBuffer
import parser.modes.defaultModes._
import parser.modes.plugins._


object Config {

  //modurile dokuwiki din care va fi creat parserul
  var modeArray = ArrayBuffer(Code, Base, Header, TextStyle, List, Unformatted, Link, Quoting, Footnote, Media)

  //var modeArray = ArrayBuffer(Base, Media)

  var pluginEnable = ArrayBuffer("Color", "MathJax", "Note")

  def enablePlugins() = {

    for(plugin <- pluginEnable){
      plugin match {
        case "Color" => modeArray.append(new Color)
        case "MathJax" => modeArray.append(new MathJax)
        case "Note" => modeArray.append(new Note)
      }
    }
  }


  def getParser(): Parser[String] = {

    enablePlugins()

    modeArray = modeArray.sortWith(_.order > _.order)

    for (mode <- modeArray) {
//      println("Mode that is configured:" + mode.label)
      for (allowedMode <- modeArray) {
        if (mode.allowedGroups.contains(allowedMode.group)
              && mode.label != allowedMode.label) {
//          println("Add accepted mode to " + mode.label +": " + allowedMode.label)
          mode.addMode(allowedMode.parser)
        }
      }
    }
    Base.parser
  }

}
