package io.doerfler.enni

import org.parboiled2._

import scala.io.Source

object EnniApp extends App {
  for {
    s <- Source.stdin.getLines
  } {
    implicit val parser = new EnvelopeParser(s)
    val res = parser.EnvelopeInput.run().toEither.left.map {
      case e: ParseError => parser.formatError(e, new ErrorFormatter(showTraces = true))
    }
    println(res)
    println("===========================")
  }
}