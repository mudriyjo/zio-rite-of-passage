package com.mudriyjo

import sttp.tapir.*
import sttp.tapir.server.ziohttp.*
import zio.*
import zio.http.Server
import com.mudriyjo.reviewboard.http.controller.*

object Application extends ZIOAppDefault {

  val server = for {
    healthController <- HealthController.makeZIO
    _ <- Server.serve(
      ZioHttpInterpreter(
        ZioHttpServerOptions.default
      ).toHttp(healthController.endpoint)
    )
  } yield ()

  override def run = server.provide(
    Server.default
  )
}
