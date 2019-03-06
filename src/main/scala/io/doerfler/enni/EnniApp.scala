package io.doerfler.enni

import org.parboiled2._

import scala.io.Source
import scala.Console.{GREEN, RED, RESET, YELLOW_B, UNDERLINED}

object EnniApp extends App {
  for {
    s <- Source.stdin.getLines
  } {
    implicit val parser = new EnvelopeParser(s)
    val res = parser.EnvelopeInput.run().toEither.left.map {
      case e: ParseError => RED + parser.formatError(e, new ErrorFormatter(showTraces = true)) + RESET
    }
    if (res.isRight) {
      println(s)
      println()
    }
    println(res)
    println("===========================")
  }
}