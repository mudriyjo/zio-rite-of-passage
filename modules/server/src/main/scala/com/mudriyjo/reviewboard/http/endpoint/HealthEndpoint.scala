package com.mudriyjo.reviewboard.http.endpoint

import sttp.tapir.*

trait HealthEndpoint {

  val healthEndpoint = endpoint
    .tag("health")
    .name("health")
    .description("Health check endpoint")
    .in("health")
    .out(plainBody[String])
}
