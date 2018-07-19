package parser

import fastparse.all._

import parser.IO._



/**
  * Created by vhohlov on 5/2/18.
  */

object Parser {
  System.getProperty("file.encoding")

  var io: IO = _

  var initDoc = "\\documentclass[a4paper, 12pt]{article}\n" +
    "\\usepackage[a4paper]{geometry}\n" +
    "%type of input encoding\n" +
    "\\usepackage[utf8x]{inputenc}\n" +
    "%determines what font to use, not input encoding\n" +
    "%default OT1 supports 7-bit enconding chars\n" +
    "%T1 used for printing chars with 8-bit encodigns\n" +
    "\\usepackage[T1]{fontenc}\n" +
    "%%for printing greek letters\n" +
    "\\usepackage{textgreek}\n" +
    "%for printing greek letters\n"+
    "\\usepackage{amsmath}\n" +
    "\\usepackage{amsthm}\n" +
    "\\usepackage{amsfonts}\n" + //an extended set of fonts to be used in math
    "\\usepackage{ulem}\n" +
 //   "\\usepackage{bookmark}\n" +
    "\\usepackage[unicode]{hyperref}\n" +
    "\\usepackage[table]{xcolor}\n" +
    "\\usepackage{multirow}\n" +
    "\\usepackage{graphicx}\n" +
    "\\usepackage{tcolorbox}\n" +
//    "\\usepackage{listings}\n" +
    "%loads listings package for general use\n" +
    "%and also avaiable this way inside tcolorbox\n" +
    "\\tcbuselibrary{listings}\n" +
    "%breaks a tcolorbox over pages\n" +
    "\\tcbuselibrary{breakable}\n" +
    "%used for printing notes at the end of file-under test\n" +
     "\\usepackage{pagenote}\n" + "" +
     "\\makepagenote\n" +
     "\\let\\footnote\\pagenote\n" +
    "\\graphicspath{ {./media/} }\n" +
    "\\usepackage[export]{adjustbox}\n" + // To resize images.
    "\\usepackage{graphicx}\n" +          // To import images.
    "\\usepackage{wrapfig}\n" +
    "\\setlength{\\parindent}{0in}\n" +
    "\\newtheorem*{theorem}{Definition}\n" +
    "\\newtheorem*{corollary}{Corollary}\n" +
    "\\newtheorem*{lemma}{Preposition}\n\n" +
    "%for warning Overfull \\hbox\n" +
    "\\hfuzz=5.002pt\n" +
    "%settings for listings env\n" +
    "\\lstset{" +
    "breaklines=true,\n" +
    "numbers=left,\n" +
    "numberstyle=\\small,\n" +
    "numbersep=8pt,\n" +
    "xleftmargin=10pt,\n" +
    "escapeinside=&&\n" +
    "}\n"+
    "\\begin{document}\n"

  var endDoc = "\\printnotes\n\\end{document}"

  def main(args: Array[String]): Unit = {

    var link = "http://wiki.cs.pub.ro"
    //link = "http://wiki.cs.pub.ro/studenti/diploma/2016-2017"
    //    link = "http://ocw.cs.pub.ro/ppcarte/doku.php?id=pp:l02"
    //    link = "http://www.cfvbfbtlotto.com/test_docuwiki/dokuwiki/test"
    //    link = "http://ocw.cs.pub.ro/ppcarte/doku.php?id=aa:intro:rules"
          link = "http://www.cfvbfbtlotto.com/dokuwiki/doku.php?id=wiki:welcome"
    //    link = "http://ocw.cs.pub.ro/ppcarte/doku.php?id=lfa:nfa"
    //    link = "http://ocw.cs.pub.ro/ppcarte/doku.php?id=pp:intro"

    io = new RemoteIO


    if (args.length == 0) {
      println("Please, enter dokuwiki resourse")
    }
    else {
      link = args(0)
      println("link is:" + link)

      if (io(link) != None) {
        var input = io.getInput.get
        //println(input)
        var D2L = Config.getParser()

        D2L.parse(input) match {
          case Parsed.Success(parsed, _) => {println(initDoc + parsed + endDoc); io.writeTexSrc(initDoc + parsed + endDoc)}
          case Parsed.Failure(exp, i, extra) => println("Expected:" + exp + " at index " + i + " extra " + extra)
        }
      }

    }

    /*
        var input = ">> Well, I say we should //italic//\n> No we shouldn't\n>> Well, I say we should\n> Really?\n>> Yes!\n>>> Then lets do it!"
        println
        println("Input is :" + input)
        println
        var D2L = Config.getParser()

        D2L.parse(input) match {
          case Parsed.Success(parsed, _) => println(initDoc + parsed + endDoc);
          case Parsed.Failure(exp, i, extra) => println("Expected:" + exp + " at index " + i + " extra " + extra)
        }
    */
  }
}
