package io.doerfler.enni

import org.parboiled2._

import scala.io.Source
import scala.Console.{GREEN, RED, RESET, YELLOW_B, UNDERLINED}
import EnvelopeStructure._

object EnniApp extends App {
  for {
    s <- Source.stdin.getLines
  } {
    implicit val parser = new EnvelopeParser(s)
    val res = parser.EnvelopeInput.run().toEither.left.map {
      case e: ParseError => parser.formatError(e, new ErrorFormatter(showTraces = true))
    }
    res match {
      case Right(e: Envelope) =>
        println(e)
        println()
      case Left(l) =>
        println(RED + l + RESET)
    }
    println("===========================")
  }
}