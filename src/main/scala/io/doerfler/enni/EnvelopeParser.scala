package io.doerfler.enni

import org.parboiled2._
import scala.util._
import org.joda.time.DateTime

import javax.mail.internet.MimeUtility


// https://tools.ietf.org/html/rfc3501#section-7.4.2
sealed trait EnvelopeStructure
object EnvelopeStructure {
  case class Envelope(date: Option[DateTime], subject: String, from: Address, sender: Address, replyTo: Address, to: Address, cc: Option[Address], bcc: Option[Address], inReplyTo: Option[Address], messageId: String) extends EnvelopeStructure

  case class Address(personalName: Option[String], sourceRoute: Option[String], mailboxName: String, hostName: String) extends EnvelopeStructure
}

class EnvelopeParser(val input: ParserInput) extends Parser with WhitespaceRules with StringBuilding with AutomaticWhitespaceHandling {
  import EnvelopeStructure._

  def EnvelopeInput = rule {
    optional(Date) ~
    Subject ~
    AddressStructure.named("From") ~
    AddressStructure.named("Sender") ~
    AddressStructure.named("Reply To") ~
    AddressStructure.named("To") ~
    OptionalAddressStructure.named("Cc") ~
    OptionalAddressStructure.named("Bcc") ~
    OptionalAddressStructure.named("In Reply To") ~
    QuotedText ~> Envelope
  }

  def Date = rule { QuotedText ~> ((s: String) => new DateTime()) }
  def Subject = rule {
    QuotedText ~> (MimeUtility.decodeText _) |
    Text ~> (MimeUtility.decodeText _)
  }

  def OptionalAddressStructure = rule { ("NIL" ~ push(None)) | (AddressStructure ~> ((x: Address) => Some(x))) }
  def AddressStructure = rule { "((" ~ OptionalQuotedText ~ OptionalQuotedText ~ QuotedText ~ QuotedText ~ "))" ~> decodeAddress _ }
  
  def OptionalQuotedText = rule { ("NIL" ~ push(None)) | (QuotedText ~> ((x: String) => Some(x))) }
  def QuotedText = rule { '"' ~ Text ~ '"' ~ WS }
  def Text = rule { capture(oneOrMore(noneOf("\""))) }

  def decodeAddress(personalName: Option[String], sourceRoute: Option[String], mailboxName: String, hostName: String): Address = {
    Address(
      personalName map MimeUtility.decodeText,
      sourceRoute map MimeUtility.decodeText,
      MimeUtility decodeText mailboxName,
      MimeUtility decodeText hostName
    )
  }
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