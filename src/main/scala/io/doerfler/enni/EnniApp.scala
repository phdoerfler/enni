package io.doerfler.enni

import scala.io.Source

class EnniApp extends App {
  val s = Source.stdin
  println(new EnvelopeParser(s).Envelope.run())
}