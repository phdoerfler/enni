package io.doerfler.enni

import org.parboiled2._
import scala.util._
import org.joda.time.DateTime


// https://tools.ietf.org/html/rfc3501#section-7.4.2
case class Envelope(date: DateTime, subject: String, from: String, sender: String, replyTo: String, to: String, cc: String, bcc: String, inReplyTo: String, messageId: String)

class EnvelopeParser(val input: ParserInput) extends Parser with Whitespace with AutomaticWhitespaceHandling {
  def Envelope: Rule1[Envelope] = rule { Envelope ~ EOI }

  def BashFilter: Rule1[Envelope] = rule {
     ~> Envelope
  }

  def Shebang = rule { runSubParser(new ShebangParser(_).InputLine) }
  def Content = rule { runSubParser(new ContentParser(_).InputLine) }
}

trait Whitespace extends Parser {
  def WhitespaceMultiline = rule { zeroOrMore(WhitespaceCharMultiline) }
  def WhitespaceCharMultiline = rule { WhitespaceChar | CharPredicate("\n\r") }
  def Whitespace = rule(zeroOrMore(WhitespaceChar))
  def WhitespaceChar = CharPredicate(" \t\f")
}

trait AutomaticWhitespaceHandling extends ParserExtras {
  implicit def wspStr(s: String): Rule0 = rule {
    str(s) ~ zeroOrMore(WhitespaceChar)
  }
}