package parser.IO

import better.files.File


trait IO {

  //apply functions that prepare the class for working with it
  //return None is case of error
  def apply(s:String):Option[Any]

  //rget the input to be parsed
  def getInput():Option[String]

  //create a local repository where the parser results will be stored
  //need a folder for media and one for dokuwiki src
  def initLocalRepo(file:String): Option[File]

  //save or log a meida file specified by file string
  def writeMedia(file:String)

  //save the output of the parser to the created file structure
  def writeTexSrc(texSrc:String)

  //get the web or local address of the DocuWiki src
  def getDokuAddress(): String

}
