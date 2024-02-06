package com.mudriyjo

import sttp.tapir.*
import sttp.tapir.server.ziohttp.*
import zio.*
import zio.http.Server
import reviewboard.http.controller.HealthController
import com.mudriyjo.reviewboard.http.api.HttpApi

object Application extends ZIOAppDefault {

  val server = for {
    api <- HttpApi.endpointsZIO
    _ <- Server.serve(
      ZioHttpInterpreter(
        ZioHttpServerOptions.default
      ).toHttp(HttpApi.gatheringRoutes(api))
    )
  } yield ()

  override def run = server.provide(
    Server.default
  )
}
