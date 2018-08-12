package parser.IO

import better.files._
import fastparse.all._
import scalaj.http._

/*Class that works with an dokuwiki src from a remote repo
 * Works will all type of url rewrite modes
 * Finds the dokuwiki root on the remote resource
 * Works if the remote DokuWiki src allows exporting the raw formatting
 * */
class RemoteIO extends IO {

  var export = "doku.php?do=export_raw&id="
  var media = "lib/exe/fetch.php?media="
  var fileName: Option[String] = None
  var serverPath: Option[String] = None
  var dokuPath: Option[String] = None
  var localRepoPath: File = _


  def apply(link: String) = {

    if (parseURL(link).isEmpty)
      None
    else
      if(initLocalRepo(fileName.get).isEmpty)
        None
      else
        Some()
  }

  /*Parseaza linkul si determina
   * serverPath - parte din url ce reprezinta radacina dokuwiki
   * dokuPath - parte din url ce reprezinta namespace-ul dokuwiki
   * fileName - numele fisierului sursa dokuwiki
   * @return: Option[Any]
   * */
  def parseURL(link: String): Option[Any] = {
    typeURL.parse(link) match {
      case Parsed.Success(parsed, _) => {
        if(!parsed.isEmpty) {
          serverPath = Some(parsed.get._1)
          dokuPath = Some(parsed.get._2)
          fileName = Some(parsed.get._3)
          Some()
        }
        else
          None
      }
      case Parsed.Failure(_, _, _) => None
    }

  }

  def typeURL = P(rawURL | rewrittenURL)

  //url rewritten by server rules
  def rewrittenURL = P(protocol.? ~ hostname ~ path ~ parsefileName.?).map {
    case (pc, host, pathSeq, fileName) =>
      proccesRewrittenUrl(pc.getOrElse("http://") + host, pathSeq, fileName)
  }

  //url that is not rewritten by server rules
  def rawURL = P(protocol.? ~ hostname ~ rawPath ~ path.! ~ parsefileName.?).map {
    case (pc, host, serverPath, dokuPath, file) =>
      proccesRawURL(pc.getOrElse("http://") + host, serverPath, dokuPath, file)
  }

  def protocol = P("http" ~ "s".? ~ "://").!

  def hostname = P((!("/" | End) ~ AnyChar).rep(1) ~ "/".?).!

  //path that does not rewrite the doku.php script
  def rawPath = P((!"doku.php" ~ AnyChar).rep(1).! ~ "doku.php" ~ ("?id=" | "/").?)

  //path that does not contain doku.php - used for rewritten url recognition
  def path = P((!pathSep ~ AnyChar).rep ~ pathSep).!.rep

  def pathSep = P("/" | ":")

  def parsefileName = P(AnyChar.rep(1)).!


  def proccesRawURL(url: String, serverPath: String, dokuPath: String, fileName: Option[String]) = {

    var contentDisp: Option[String] = None

    contentDisp = getContentHeader(url + serverPath + export + dokuPath + fileName.getOrElse(""))

    //there is no dokuwiki content found
    if (contentDisp == None) {
      println("[Error] No dokuwiki content found!");
      None
    }
    //dokuwiki content found
    else {
      var remoteFName = getFileName(contentDisp.get)

      //cannot extract the remote file name
      if (remoteFName == None)
        None
      else {
        //the default dokuwiki page was returned
        if (fileName == None) {
          Some((url + serverPath, dokuPath, remoteFName.get))
        }
        else if (fileName.get == remoteFName.get)
          Some((url + serverPath, dokuPath, fileName.get))

        //the name from url does not correspond to the name from Header
        else
          None
      }
    }
  }


  def proccesRewrittenUrl(url: String, pathSeq: Seq[String], fileName: Option[String]) = {

    var serverPath, dokuPath: String = ""
    var contentDisp: Option[String] = None
    var size = pathSeq.length

    for (i <- 0 to size if contentDisp == None) {

      serverPath = pathSeq.dropRight(i).mkString("")
      dokuPath = pathSeq.drop(size - i).mkString("")
      contentDisp = getContentHeader(url + serverPath + export + dokuPath + fileName.getOrElse(""))
    }

    //there is no dokuwiki content found
    if (contentDisp == None) {
      println("[Error] No dokuwiki content found!");
      None
    }
    //dokuwiki content found
    else {
      var remoteFName = getFileName(contentDisp.get)

      //cannot extract the remote file name
      if (remoteFName == None)
        None
      else {
        //the default dokuwiki page was returned
        if (fileName == None) {
          Some((url + serverPath, dokuPath, remoteFName.get))
        }
        else if (fileName.get == remoteFName.get)
          Some((url + serverPath, dokuPath, fileName.get))

        //the name from url does not correspond to the name from Header
        else
          None
      }
    }
  }

  //get the file name from the Content-Disposition Header
  def getFileName(header: String): Option[String] = {

    def name = P(not("filename").rep ~ "filename=" ~ not(".txt").rep(1).!)

    def not(s: String) = P(!s ~ AnyChar)

    name.parse(header) match {
      case Parsed.Success(name, _) => Some(name)
      case Parsed.Failure(_, _, _) => {
        println("[APP] No filename found for Dokuwiki page");
        None
      }
    }
  }

  //check the existence of "Content-Disposition" and return it's content
  def getContentHeader(link: String): Option[String] = {

    try {
      val response: HttpResponse[String] = Http(link).method("HEAD").asString
      val cDisp = response.headers.get("Content-Disposition").getOrElse(Vector(None))

      if (cDisp.head == None)
        None
      else
        Option(cDisp.head.toString)
    }
    catch {
      case e: Any => println(s"Dokuwiki source download exception:$e")
        None
    }
  }

  def getInput: Option[String] = {

    var dokuSrc = fileName.get

    try {
      val response: HttpResponse[String] = Http(serverPath.get + export + dokuPath.get + fileName.get).asString
      (s"$localRepoPath/DokuWiki/$dokuSrc").toFile.createIfNotExists().overwrite(response.body)
      Some(response.body)
    }
    catch {
      case e: Any => println(s"content download exception $e")
        None
    }
  }

  def initLocalRepo(file: String): Option[File] = {

    try {

      //main Src Directry
      localRepoPath = ("./tests/" + file + "_LaTex").toFile.createIfNotExists(true)

      //create DokuWiki src directory
      (s"$localRepoPath/DokuWiki").toFile.createIfNotExists(true)

      //create media directory
      (s"$localRepoPath/media").toFile.createIfNotExists(true)

      Some(localRepoPath)
    }
    catch {
      case e: Any => println(s"File creation error:$e")
        None
    }
  }

  def writeMedia(file: String) = {
    println("file to get: " + serverPath.get + media + file);
    try {
      val resp: HttpResponse[Array[Byte]] = Http(serverPath.get + media + file).asBytes
      var path: File = (s"$localRepoPath/media/$file").toFile.createIfNotExists()
      path.writeBytes(resp.body.toIterator)
    }
    catch {
      case e: Any => println(s"Media file  exception $e")
        None
    }
  }

 def writeTexSrc(texSrc:String)  = {

   var texFile = fileName.get + ".tex"

   try {
     (s"$localRepoPath/$texFile").toFile.createIfNotExists().overwrite(texSrc)
   }
   catch {
     case e: Any => println(s"Media file  exception: $e")
       None
   }
 }

  def getDokuAddress(): String = {
    serverPath.get + "doku.php?id="
  }

}
