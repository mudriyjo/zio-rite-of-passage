package com.mudriyjo.reviewboard.http.controller

import zio.*
import com.mudriyjo.reviewboard.http.endpoint.HealthEndpoint

class HealthController private extends HealthEndpoint {
  val endpoint = healthEndpoint
    .serverLogicSuccess[Task](_ => ZIO.succeed("Health check stub..."))
}

object HealthController {
  def makeZIO = ZIO.succeed(new HealthController)
}
