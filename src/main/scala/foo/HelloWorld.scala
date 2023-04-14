package foo

import zio.{ ZIO, ZIOAppDefault }

object HelloWorld extends ZIOAppDefault {
  override def run = zio.Console.printLine("Hello World!")
}
