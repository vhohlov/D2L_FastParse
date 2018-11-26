package parser

import fastparse.all._
import parser.IO._
import better.files._



/**
  * Created by vhohlov on 5/2/18.
  */

object Parser {
  var io: IO = _

  var initDoc = "\\documentclass[a4paper, 12pt]{report}\n" +
    "\\usepackage[margin=2.54cm]{geometry}\n" +
    "%type of input encoding\n" +
    "\\usepackage[utf8x]{inputenc}\n" +
    "%determines what font to use, not input encoding\n" +
    "%default OT1 supports 7-bit enconding chars\n" +
    "%T1 used for printing chars with 8-bit encodigns\n" +
    "\\usepackage[T1]{fontenc}\n" +
    "%%for printing greek letters\n" +
    "\\usepackage{textgreek}\n" +
    "\\usepackage{titlesec}\n" +
    "\\usepackage[titletoc, title]{appendix}\n" +
    "\\titleformat{\\chapter}\n{\\normalfont\\LARGE\\bfseries}{\\thechapter}{1em}{}\n" +
    "\\titleformat*{\\section}{\\LARGE\\bfseries}\n" +
    "\\titleformat*{\\subsection}{\\Large\\bfseries}\n" +
    "\\titleformat*{\\subsubsection}{\\large\\bfseries}\n" +
    "%for printing greek letters\n"+
    "\\usepackage{amsmath}\n" +
    "\\usepackage{amsthm}\n" +
    "\\usepackage{amsfonts}\n" +
    "\\usepackage{ulem}\n" +
    "\\usepackage[unicode]{hyperref}\n" +
    "\\usepackage[table]{xcolor}\n" +
    "\\usepackage{multirow}\n" +
    "\\usepackage{graphicx}\n" +
    "\\usepackage{tcolorbox}\n" +
    "%loads listings package for general use\n" +
    "%and also avaiable this way inside tcolorbox\n" +
    "\\tcbuselibrary{listings}\n" +
    "%breaks a tcolorbox over pages\n" +
    "\\tcbuselibrary{breakable}\n" +
    "%for printing theorems\n" +
    "\\tcbuselibrary{theorems}" +
    "%used for printing notes at the end of file\n" +
    "%continuous used for one numbering in the whole doc\n" +
    "\\usepackage[continuous]{pagenote}\n" +
    "\\makepagenote\n" +
    "\\let\\footnote=\\pagenote\n" +
    "%for not counting Notes as a new section\n" +
    "%\\renewcommand*{\\notedivision}{\\section*{\\notesname}}\n" +
    "%for not printing sections divisions in Notes\n" +
    "\\renewcommand*{\\pagenotesubhead}[1]{}" +
    "\\graphicspath{ {./media/} }\n" +
    "\\usepackage[export]{adjustbox}\n" + // To resize images.
    "\\usepackage{graphicx}\n" +          // To import images.
    "\\usepackage{wrapfig}\n" +
    "\\setlength{\\parindent}{0in}\n" +
    "%for warning Overfull \\hbox\n" +
    "\\hfuzz=50.002pt\n" +
    "%settings for listings env\n" +
    "\\lstset{" +
    "breaklines=true,\n" +
    "numbers=left,\n" +
    "numberstyle=\\small,\n" +
    "numbersep=8pt,\n" +
    "xleftmargin=10pt,\n" +
    "escapeinside=&&\n" +
    "}\n"+
    "\\newtcbtheorem{_def}{Definition}{breakable}{df}\n"+
    "\\newtcbtheorem{prop}{Proposition}{breakable}{prp}\n" +
    "\\newtcbtheorem{_proof}{Proof}{breakable}{prf}\n"+
    "\\newtcbtheorem{remark}{Remark}{breakable}{rmk}\n" +
    "\\newtcbtheorem{example}{Example}{breakable}{exp}\n" +
    "\\newtcbtheorem{theorem}{Theorem}{breakable}{th}\n" +
    "\\newtcbtheorem{exercise}{Exercise}{breakable}{exer}\n" +
    "\\newtcbtheorem{solution}{Solution}{breakable}{sol}\n" +
    "\\newtcbtheorem{algorithm}{Algorithm}{breakable}{alg}\n"

  var endDoc = "\\newpage\n\\printnotes\n\\end{document}"



  def main(args: Array[String]): Unit = {

    //build the parser based on the configurations
    var D2L = Config.getParser()
    var sidebarContent:String = ""
    var configData : String = ""

    //Options that can be set by user
    var dokuSrc = None:Option[String]
    var name    = None:Option[String]
    var author  = None:Option[String]
    var date    = None:Option[String]
    io = new RemoteIO

    //test if config file is given
    if (args.length == 0) {
      println("[Error] Please, enter the config file")
    }

    //read configuration file
    try {
      configData = args(0).toFile.contentAsString
    }
    catch {
      case e: Any => println(s"Config file exception:$e")
    }


    /*Parser for configuration parameters detection*/
    def param  = P(CharIn('a' to 'z', 'A' to 'Z', "_").rep(1)).!
    def sep    = P(" ".rep ~ ":" ~ " ".rep)
    def value  = P((!("\n") ~ AnyChar).rep(1).! ~ "\n")

    def parseConfig = P(param ~ sep ~ value).map{
      case(param, value) => {
        param.toLowerCase match {
          case "url_dokuwiki"     => dokuSrc = Some(value)
          case "denumire_lucrare" => name = Some(value)
          case "autor"  => author = Some(value)
          case "data"   => date = Some(value)
        }
      }
    }
    //parse and detect the config data
    parseConfig.rep.parse(configData)

    //check for url parameter that is necessary
    if(dokuSrc.isEmpty) {
      println("[Error] No DokuWiki source identified in config!")
      return
    }

    //show detected parameters to user
    println("Defined parameters are: ")
    println("DokuWiki Source: " + dokuSrc.getOrElse("Not defined"))
    println("Document Name: " + name.getOrElse("Not defined"))
    println("Author Name: " + author.getOrElse("Not defined"))
    println("Date: " + date.getOrElse("Not defined"))
    println()

    //create a main.tex file in which document settigs and other files are included
    //tests the DokuWiki repo link and gets the content
    if (io(dokuSrc.get) != None) {
      sidebarContent = io.getInput.get
    }

    //insert Latex configuration in preamble
    io.writeMain(initDoc)

    //set the option specified by user
    io.writeMain(s"\\title{" + name.getOrElse("") + "}\n")
    io.writeMain(s"\\author{" + author.getOrElse("") + "}\n")
    io.writeMain(s"\\date{" + date.getOrElse("") + "}\n")

    //begin the document
    io.writeMain("\n\\begin{document}\n\\maketitle\n\\tableofcontents\n")


    /*create a parser that scans the sidebar content*/
    var startIndent = 0;

    //scaneaza lista de linkuri si porneste parserul pentru fiecare
    def element = P("\n" ~ " ".rep(1).! ~ "*" ~ " ".rep).map(spaces=> spaces.length)//.log("element")

    def treatElement(indent:Int, title:String): Unit = {

      if (startIndent == 0)
        startIndent = indent

      var level = (indent - startIndent) / 2

      level match {
        case 0 => io.writeMain("\\chapter{")
        case 1 => io.writeMain("\\section{")
        case 2 => io.writeMain("\\subsection{")
        case 3 => io.writeMain("\\subsubsection{")
        case 4 => io.writeMain("\\paragraph{")
        case _ => io.writeMain("\\subparagraph")
      }

      io.writeMain(title + "}\n")
    }
    //recognize url
    def link = P("[[" ~ (!"|" ~ AnyChar).rep(1).! ~ "|" ~ (!"]]" ~ AnyChar).rep(1).! ~ "]]")//.log("link")
    //recongnize title
    def title = P((!("\n") ~ AnyChar).rep(1).!)
    //skip content till gets the first element of list
    def skip = P((!element ~ AnyChar).rep(1).!)

    //a section that contains other DokuWiki src to parse
    def textSection = P(element ~ link).map{
      case (indent, (link, title)) =>{

       var clean_link = link.replaceAll("\\s", "")
        treatElement(indent, title)

        var content = io.getLinkContent(link)
        //translate DokuWiki content to Latex format
        D2L.parse(content.get) match {
          case Parsed.Success(parsed, _) => {
            io.writeMain(s"\\input{$clean_link}\n")
            io.writeTexSrc(parsed, clean_link)
          }
          case Parsed.Failure(exp, i, extra) => println("Expected:" + exp + " at index " + i)
        }
      }
    }

    //parser that recongnize a text section without DokuWiki src
    def dumpSection = P(element ~ title).map{
      case (indent, title) =>{
        treatElement(indent, title)
      }
    }

    //parse sidebar and all other links inside sidebar
    def sidebarParse = P(skip ~ (textSection | dumpSection).rep)
    sidebarParse.parse(sidebarContent) match {
      case Parsed.Success(parsed, _) => {}
      case Parsed.Failure(exp, i, extra) => println("Expected:" + exp + " at index " + i)
    }

    //end Latex document
    io.writeMain(endDoc)

  }
}
