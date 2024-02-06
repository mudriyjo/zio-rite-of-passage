package com.mudriyjo.reviewboard.http.api

import zio.*

import com.mudriyjo.reviewboard.http.controller.*

object HttpApi {

  def gatheringRoutes(controllers: List[BaseController]) =
    controllers.flatMap(_.routes)

  def makeControllers = {
    for {
      healthController  <- HealthController.makeZIO
      companyController <- CompanyController.makeZIO
    } yield List(healthController, companyController)
  }

  def endpointsZIO = makeControllers
}
