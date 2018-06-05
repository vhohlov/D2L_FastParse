package parser.modes.defaultModes

import fastparse.all._
import parser.Group._
import parser.Parser

/**
  * Created by vhohlov on 5/15/18.
  */
//use  package hyperref
object Link extends Mode {
  override var group: Group = Substitution
  override var order: Int = 290
  override var allowedGroups: Array[Group] = Array()
  override var label: String = "Link"


  //The protocols supported by dokuwiki default configuration
  var schemes = P("http" | "https" | "telnet" | "gopher" | "wais" | "ftp" | "ed2k" | "irc" | "ldap")

  //Default interwiki shortcuts
  var interMap = Map(
    "wp" -> "https://en.wikipedia.org/wiki/",
    "wpfr" -> "https://fr.wikipedia.org/wiki/",
    "wpde" -> "https://de.wikipedia.org/wiki/",
    "wpes" -> "https://es.wikipedia.org/wiki/",
    "wpmeta" -> "https://meta.wikipedia.org/wiki/",
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

  def parser: Parser[String] = P(namedAddress | rawWikiAddress | webLink | email)//.log(label)

  //Parse Interwiki or currentWiki links that don't have adittional link text
  def rawWikiAddress = P(rawInterWiki | rawWiki).map {
    link => "\\url{" + link + "}"
  }

  def rawWiki = P("[[" ~ wiki("]]") ~ "]]")
  def rawInterWiki = P("[[" ~ interWiki("]]") ~ "]]")

  /*Parse links with aditional link text
  * Prints the link text and stores link in a footnote
  * to not loose the link when printing the pdf
  * Disregard	link descriptions with images -> prints the link directly
  * */
  def namedAddress = P(namedWebLink | namedInterWiki | namedWiki).map {
    case (link, description) => {

      //description is an image - is ignored and link is printed as raw
      if (description.isEmpty)
        "\\url{" + link + "}"
      //description is text
      else
        "\\texttt{" + description.get + "} \\footnote{\\url{" + link + "}}"

    }
  }

  def namedWiki = P("[[" ~ wiki("|") ~ "|" ~ description ~ "]]")

  def namedInterWiki = P("[[" ~ interWiki("|") ~ "|" ~ description ~ "]]")

  //Parse a wiki link from current doku source
  def wiki(endLink: String) = P(wikiLink(endLink)).map {
    //get the address of the current doku source from input
    link => Parser.io.getDokuAddress + link
  }

  /* Parse an interWiki link.
   * For unknown interWiki shortcut is created a google shearch url
   * with the resource specified in the unknown shortcut
  **/
  def interWiki(endLink: String) = P(wikiLink(">") ~ ">" ~ wikiLink(endLink)).map {
    case (shortcut, page) => {

      //check if shortcut is defined
      if (interMap.contains(shortcut))
        interMap(shortcut) + page
      else
      //shearch for wiki on google
        interMap("go") + page + "&btnI=lucky"
    }
  }
  //links grammar allowed between interWiki and wiki sources
  def wikiLink(s: String) = P(!(s | "]]") ~ content).rep.!

  //Link description
  //Returns None for description with image
  //Reurns Some(description) for text
  def description = P(image | text)

  //Identify image format in link description
  def image = P("{{" ~ (!("}}") ~ content).rep ~ "}}" ~ &("]]")).map(_ => None)
  //Indentify text in link description
  def text = P(!("]]") ~ content).rep.!.map(text => Some(text))

  //URL specified with description
  def namedWebLink = P("[[" ~ ((pc | www) ~ namedLink).! ~ "|" ~ description ~ "]]")

  //URL recognized automagically by dokuWiki
  def webLink = P((pc | www) ~ link).map {
    case (prefix, link) => "\\url{" + prefix + link + "}"
  }

  //link with protocol is specified -> www is optional
  def pc = P(schemes ~ "://" ~ "www.".?).!

  //www is specified -> protocol is optional
  def www = P((schemes ~ "://").? ~ "www.").!

  //link that does not have description after it
  //must contain at least two strings separated by dots
  def link = P(!linkBreak ~ content).rep(min = 2, sep = ".").!

  //link that has description after it
  //must contain at least two strings separated by dots
  def namedLink = P(!(linkBreak | "|" | "]]") ~ content).rep(min = 2, sep = ".").!

  def linkBreak = P(" " | "\n" | "\t").rep(1)


  def email = P("<" ~ (localPart ~ "@" ~domain).! ~ ">").map {
    case email => "\\href{mailto:" + email + "}{" + email + "}"
  }

  def localPart = P(word|number|CharIn("!#$%&'*+/=?^_`{|}~-")).rep(min = 1, sep = ".")

  def domain = P(((word|number|"-").rep(1) ~ ".").rep(1) ~ CharIn('a' to 'z').rep(min = 2, max = 63))

}
