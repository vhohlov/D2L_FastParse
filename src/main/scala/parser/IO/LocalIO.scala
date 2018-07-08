package parser.IO
import better.files._

/**
  * Created by vhohlov on 6/6/18.
  */
/*
class LocalIO extends IO {

  var localRepoPath: File = _

  override def apply(s: String): Option[Any] = ???

  override def getInput(): Option[String] = ???




  override def initLocalRepo(file: String): Option[File] = {

    try {

      //main Src Directry
      localRepoPath = ("./" + file + "_LaTex").toFile.createIfNotExists(true)

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

  override def writeMedia(file: String) = Unit

  override def writeTexSrc(texSrc: String): Unit = Unit

  override def getDokuAddress(): String = ""

}
*/