package com.mudriyjo.reviewboard.http.controller

import zio.*
import sttp.tapir.server.ServerEndpoint
import scala.collection.mutable

import com.mudriyjo.reviewboard.domain.data.*
import com.mudriyjo.reviewboard.http.request.*
import com.mudriyjo.reviewboard.http.endpoint.CompanyEndpoint

class CompanyController private extends BaseController with CompanyEndpoint {
  val db: mutable.Map[Long, Company] = mutable.Map[Long, Company]()

  val createCompanyEndpoint: ServerEndpoint[Any, Task] = createNewCompanyEndpoint
    .serverLogicSuccess { req =>
      ZIO.succeed {
        val maxId      = if db.keys.isEmpty then 0L else db.keys.max + 1L
        val newCompany = req.toCompany(maxId)
        db += (maxId -> newCompany)
        newCompany
      }
    }

  val getAllEndpoint: ServerEndpoint[Any, Task] = getCompaniesEndpoint
    .serverLogicSuccess(_ => ZIO.succeed(db.values.toList))

  val getCompanyEndpoint: ServerEndpoint[Any, Task] = getCompanyById
    .serverLogicSuccess(id => ZIO.succeed(db.get(id)))

  val routes = List(createCompanyEndpoint, getAllEndpoint, getCompanyEndpoint)
}

object CompanyController {
  def makeZIO = ZIO.succeed(new CompanyController)
}
