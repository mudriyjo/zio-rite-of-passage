package com.mudriyjo.reviewboard.http.controller

import zio.*
import sttp.tapir.server.ServerEndpoint

import com.mudriyjo.reviewboard.http.endpoint.HealthEndpoint

class HealthController private extends BaseController with HealthEndpoint {
  val endpoint: ServerEndpoint[Any, Task] = healthEndpoint
    .serverLogicSuccess[Task](_ => ZIO.succeed("Health check stub..."))

  val routes = List(endpoint)
}

object HealthController {
  def makeZIO = ZIO.succeed(new HealthController)
}
