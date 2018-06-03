package parser

/*Defines groups of modes that shares the same characteristics*/
object Group extends Enumeration {
  type Group = Value

  //Group that cannot be combined by other Modes. Used by base mode
  val Ungrouped = Value

  //Modes that can be combined just in the base mode
  val BaseOnly = Value

  //Modes used for text styling
  val Formatting = Value

  //Modes that can contain other modes
  val Container = Value

  //Modes where the text represent an token
  //that is substituted by specific value
  val Substitution = Value

  //Modes with an start and end token and where
  //no other modes are allowed inside tokens
  val Protected = Value

  //Modes where dokuWiki syntax is completely disabled
  //Used for Unformatted mode
  val Disabled = Value

  //used for paragraphs boundaries
  val Paragraph = Value
}