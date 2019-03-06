package io.doerfler.enni

import org.parboiled2._
import scala.util._
import org.joda.time.DateTime


// https://tools.ietf.org/html/rfc3501#section-7.4.2
sealed trait EnvelopeStructure
object EnvelopeStructure {
  case class Envelope(date: DateTime, subject: String, from: From, sender: String, replyTo: String, to: String, cc: String, bcc: String, inReplyTo: String, messageId: String) extends EnvelopeStructure

  case class From(a: String, b: Option[String], c: String, d: String) extends EnvelopeStructure
}

class EnvelopeParser(val input: ParserInput) extends Parser with WhitespaceRules with StringBuilding with AutomaticWhitespaceHandling {
  import EnvelopeStructure._

  def EnvelopeInput = rule { Date ~ Subject ~ `From structure` ~> (Envelope(_,_,_, "senderu", "reply", "to", "cc", "bcc", "inReplyTo", "messageId")) }

  def Date = rule { QuotedText ~> ((s: String) => new DateTime()) }
  def Subject = rule { QuotedText }
  def `From structure` = rule { "((" ~ QuotedText ~ OptionalQuotedText ~ QuotedText ~ QuotedText ~ "))" ~> From }
  
  def OptionalQuotedText = rule { ("NIL" ~ push(None)) | (QuotedText ~> ((x: String) => Some(x))) }
  def QuotedText = rule { '"' ~ clearSB() ~ oneOrMore(noneOf("\"")) ~ '"' ~ WS ~ push(sb.toString()) }
}

trait WhitespaceRules extends Parser {
  def WSNL = rule { zeroOrMore(WhitespaceCharNewline) }
  def WhitespaceCharNewline = rule { WhitespaceChar | CharPredicate("\n\r") }
  def WS = rule(quiet(zeroOrMore(WhitespaceChar)))
  def WhitespaceChar = CharPredicate(" \t\f")
}

trait AutomaticWhitespaceHandling extends WhitespaceRules {
  implicit def wspStr(s: String): Rule0 = rule {
    str(s) ~ zeroOrMore(WhitespaceChar)
  }
}