package com.mudriyjo

import sttp.tapir.*
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import zio.*
import zio.http.Server
object TapirDemo extends ZIOAppDefault {

  val simpleEndpoint = endpoint
    .tag("simple")
    .name("simple")
    .description("simplest endpoint")
    .in("simple")
    .out(plainBody[String])
    .serverLogicSuccess[Task](_ => ZIO.succeed("Hello world!"))

  val program = Server.serve(
    ZioHttpInterpreter(
      ZioHttpServerOptions.default
    ).toHttp(simpleEndpoint)
  )

  override def run = program.provide(
    Server.default
  )
}
