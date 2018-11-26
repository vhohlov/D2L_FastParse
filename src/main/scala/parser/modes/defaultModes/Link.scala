package parser.modes.defaultModes

import fastparse.all._
import parser.Group._
import parser.Parser


/*Used package:
* -usepackage{hyperref}
* -usepackage{pagenote}
* --used for printing notes at the end of doc
* --continous option
* ---for not reseting counter at new sections
* --makepagenote
* ---for creating pagenote at the end
* --\let\footnote=\pagenote
* ---treat footnote as pagenote
* --\renewcommand*{\pagenotesubhead}[1]{}
* ---for not printing sections divisions in Notes
**/

object Link extends Mode {
  override var group: Group = Substitution
  override var order: Int = 290
  override var allowedGroups: Array[Group] = Array()
  override var label: String = "Link"


  //The protocols supported by dokuwiki default configuration
  var schemes = P("https" | "http" | "telnet" | "gopher" | "wais" | "ftp" | "ed2k" | "irc" | "ldap")

  //Default interWikiSrc shortcuts
  var interMap = Map(
    "wp" -> "https://en.wikipedia.org/internalWikiSrc/",
    "wpfr" -> "https://fr.wikipedia.org/internalWikiSrc/",
    "wpde" -> "https://de.wikipedia.org/internalWikiSrc/",
    "wpes" -> "https://es.wikipedia.org/internalWikiSrc/",
    "wpmeta" -> "https://meta.wikipedia.org/internalWikiSrc/",
    "doku" -> "https://www.dokuwiki.org/",
    "rfc" -> "rfc https://tools.ietf.org/html/rfc",
    "man" -> "http://man.cx/",
    "amazon" -> "https://www.amazon.com/dp/",
    "paypal" -> "https://www.paypal.com/cgi-bin/webscr?cmd=_xclick&amp;business=",
    "phpfn" -> "https://secure.php.net/",
    "skype" -> "skype:",
    "go" -> "https://www.google.com/search?q=",
    "callto" -> "callto://",
    "tel" -> "tel:"
  )

  def parser: Parser[String] = P(email| namedAddress | rawAddress | autoWebLink)

  /*Parse links with aditional link text
  * Prints the link text and stores link in a footnote
  * to not loose the link when printing the pdf
  * Disregard	link descriptions with images -> prints the link directly
  */
  def namedAddress = P(namedWebLink | namedInterWiki | namedInternalWiki).map {
    case (link, description) => {

      //description is an image - is ignored and link is printed as raw
      if (description.isEmpty)
        "\\url{" + link + "}"
      //description is text
      else
        "\\texttt{" + description.get + "} \\footnote{\\url{" + link + "}}"

    }
  }

  //Web link that has set a link text
  def namedWebLink = P("[[" ~ (" "|"\t").rep ~ namedLinkSrc ~ (" "|"\t").rep ~ "|" ~ description ~ "]]")

  //link that has description after it
  //a link is recongized if it has a specified at least the protocol or www
  def namedLinkSrc = P((pc | www) ~ (!("|") ~ (\ | content)).rep).map{
    case(linkStart, linkText) => linkStart + linkText.mkString("")
  }

  //Interwiki link that has set a link text
  def namedInterWiki = P("[[" ~ interWikiSrc("|") ~ "|" ~ description ~ "]]")

  //Internalwiki link that has set a link text
  def namedInternalWiki = P("[[" ~ internalWikiSrc("|") ~ "|" ~ description ~ "]]")



  /*Parse links that are in [[ ]] but do not have set link text*/
  def rawAddress = P(rawWebLink | rawInterWiki | rawInternalWiki).map {
    link => "\\url{" + link + "}"
  }

  //a web link also is recognized when is included in [[]]
  def rawWebLink = P("[[" ~ (" "|"\t").rep ~ rawLinkSrc ~ (" "|"\t").rep ~"]]")

  //a web link is recongized if it has a specified at least the protocol or www
  def rawLinkSrc = P((pc | www) ~ (!("]]") ~ (\ | content)).rep).map{
    case(linkStart, linkText) => linkStart + linkText.mkString("")
  }

  //Interwiki link without link text
  def rawInterWiki = P("[[" ~ interWikiSrc("]]") ~ "]]")

  //Internalwiki link without link text
  def rawInternalWiki = P("[[" ~ internalWikiSrc("]]") ~ "]]")
 


  /*Parse the text that defines a link*/
  //Parse a internalWikiSrc link from current doku source
  def internalWikiSrc(endLink: String) = P(wikiLink(endLink)).map {
    //get the address of the current doku source from input
    link => Parser.io.getDokuAddress + link
  }

  /* Parse an interWikiSrc link.
   * For unknown interWikiSrc shortcut is created a google shearch url
   * with the resource specified in the unknown shortcut
  **/
  def interWikiSrc(endLink: String) = P(wikiLink(">") ~ ">" ~ wikiLink(endLink)).map {
    case (shortcut, page) => {

      //check if shortcut is defined
      if (interMap.contains(shortcut))
        interMap(shortcut) + page
      else
      //search for internalWikiSrc on google
        interMap("go") + page + "&btnI=lucky"
    }
  }

  //grammar for interWiki and internalWiki links content
  def wikiLink(s: String) = P(!(s | "]]") ~ (\ | content)).rep.map{
    ctSeq => ctSeq.mkString("")
  }


  /*Link description
  *Returns None for description with image
  *Returns Some(description) for text
  **/
  def description = P(image | text) //.log("description")

  //Identify image format in link description
  def image = P("{{" ~ (!("}}") ~ content).rep ~ "}}" ~ &("]]")).map(_ => None)
  //Indentify text in link description
  def text = P(!("]]") ~  content).rep.map(text => Some(text.mkString("")))


  //URL recognized automagically by dokuWiki
  def autoWebLink = P((pc | www) ~ autoLinkSrc).map {
    case (prefix, link) => "\\url{" + prefix + link + "}"
  }

  //Web link that does not have description after it
  //must contain at least two strings separated by dots
  def autoLinkSrc = P(!(" " | "\n" | "\t") ~ (\ | content)).rep(min = 2, sep = ".").map{
    ctSeq => ctSeq.mkString("")}


  //link with protocol specified -> www is optional
  def pc = P(schemes ~ "://" ~ "www".?).! //.log("protocol")

  //www is specified -> protocol is optional
  def www = P((schemes ~ "://").? ~ "www").!

  //in links \ must be escaped with \\ and not \textbackslash
  def \ = P("\\").map(_ => "\\\\")


  /*Identifies an mail link*/
  def email = P(emailNote | emailDirect)

  //email that has a description text
  def emailNote = P("[[" ~ "mailto:".? ~ emailSrc ~ "|" ~ description ~ "]]").map{
    case (email, description) => {

    //description is an image - is ignored and email is printed directly
    if (description.isEmpty)
      "\\href{mailto:" + email + "}{" + email + "}"
    //description is text
    else
      "\\texttt{" + description.get + "} \\footnote{\\href{mailto:" + email + "}{" + email + "}}"
    }
  }

  //email defined between <>
  def emailDirect = P("<" ~ emailSrc ~ ">").map {
    case email => "\\href{mailto:" + email + "}{" + email + "}"
  }

  //the part of text defining the email address
  def emailSrc = P(localPart ~ "@" ~ domain).!

  def localPart = P(word|number|CharIn(".!#$%&'*+/=?^_`{|}~-")).rep(1)

  def domain = P(((word|number|"-").rep(1) ~ ".").rep(1) ~ CharIn('a' to 'z').rep(min = 2, max = 63))

}
