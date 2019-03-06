package io.doerfler.enni

import org.joda.time.DateTime
import org.parboiled2._
import scala.util._

import org.specs2._

import org.scalacheck._
import Gen._
import Arbitrary.arbitrary

import EnvelopeStructure._

class EnniSpec extends Specification with ScalaCheck {
  def is = s2"""
# IMAP Envelope

  A basic envelope can be parsed    $e1

"""

  def e1 = Eval.envelope(""""Wed,  6 Mar 2019 01:43:22 +0000" "=?utf-8?Q?March=20of=20the=20Indies?=" (("=?utf-8?Q?Soraya=20from=20Roll20?=" NIL "soraya" "roll20.net")) (("=?utf-8?Q?Soraya=20from=20Roll20?=" NIL "soraya" "roll20.net")) (("=?utf-8?Q?Soraya=20from=20Roll20?=" NIL "soraya" "roll20.net")) (("=?utf-8?Q?Philipp?=" NIL "philipp.doerfler" "gmx.de")) NIL NIL NIL "<851adce635852cfbbd9086f3e.30e9649b6a.20190306014256.2c034044d8.870ec79d@mail104.atl281.mcsv.net>"""") must beRight
}

object Eval {
  def envelope(str: String): Either[String, Envelope] = {
    implicit val parser = new EnvelopeParser(str)
    parser.EnvelopeInput.run().toEither.left.map {
      case e: ParseError => parser.formatError(e, new ErrorFormatter(showTraces = true))
    }
  }
}