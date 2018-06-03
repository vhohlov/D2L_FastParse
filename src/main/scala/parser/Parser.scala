package parser

import fastparse.all._

import parser.IO._



/**
  * Created by vhohlov on 5/2/18.
  */

object Parser {
  var io: IO = _

  var initDoc = "\\documentclass[a4paper, 12pt]{article}\n" +
    "\\usepackage[a4paper]{geometry}\n" +
    "\\usepackage[utf8x]{inputenc}\n" +
    "\\usepackage{amsmath}\n" +
    "\\usepackage{amsthm}\n" +
    "\\usepackage{ulem}\n" +
    "\\usepackage{bookmark}\n" +
    "\\usepackage{hyperref}\n" +
    "\\usepackage[table]{xcolor}\n" +
    "\\usepackage{multirow}\n" +
    "\\usepackage{graphicx}\n" +
    "\\usepackage{tcolorbox}\n" +
    "\\usepackage{listings}\n" +
    "\\usepackage{minted}\n" +
    "\\graphicspath{ {./media/} }\n" +
    "\\usepackage[export]{adjustbox}\n" + // To resize images.
    "\\usepackage{graphicx}\n" +          // To import images.
    "\\usepackage{wrapfig}\n" +
    "\\setlength{\\parindent}{0in}\n" +
    "\\newtheorem*{theorem}{Definition}\n" +
    "\\newtheorem*{corollary}{Corollary}\n" +
    "\\newtheorem*{lemma}{Preposition}\n\n" +
    "\\begin{document}\n"

  var endDoc = "\n\\end{document}"

  def main(args: Array[String]): Unit = {

    var link = "http://wiki.cs.pub.ro"
    //link = "http://wiki.cs.pub.ro/studenti/diploma/2016-2017"
    //    link = "http://ocw.cs.pub.ro/ppcarte/doku.php?id=pp:l02"
    //    link = "http://www.cfvbfbtlotto.com/test_docuwiki/dokuwiki/test"
    //    link = "http://ocw.cs.pub.ro/ppcarte/doku.php?id=aa:intro:rules"
    //      link = "http://www.cfvbfbtlotto.com/dokuwiki/doku.php?id=wiki:welcome"
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
 //       println(input)
        var D2L = Config.getParser()

        D2L.parse(input) match {
          case Parsed.Success(parsed, _) => io.writeTexSrc(initDoc + parsed + endDoc)
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
