package com.mudriyjo.reviewboard.http.endpoint

import sttp.tapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto._

import com.mudriyjo.reviewboard.domain.data.Company
import com.mudriyjo.reviewboard.http.request.*

trait CompanyEndpoint {

  // Create new company
  val createNewCompanyEndpoint = endpoint
    .tag("company")
    .name("create")
    .description("Endpoint for new company creation")
    .post
    .in("company")
    .in(jsonBody[CompanyCreationRequest])
    .out(jsonBody[Company])

  // Get all companies
  val getCompaniesEndpoint = endpoint
    .tag("company")
    .name("getAll")
    .description("Endpoint for get all companies")
    .get
    .in("company")
    .out(jsonBody[List[Company]])

  // Get company by id
  val getCompanyById = endpoint
    .tag("company")
    .name("getById")
    .description("Endpoint for get company by id")
    .get
    .in("company" / path[Long]("id"))
    .out(jsonBody[Option[Company]])
}
