package com.mudriyjo.reviewboard.http.request

import zio.json.*

import com.mudriyjo.reviewboard.domain.data.Company

final case class CompanyCreationRequest(
    name: String,
    url: String,
    location: Option[String] = None,
    country: Option[String] = None,
    industry: Option[String] = None,
    image: Option[String] = None,
    tags: Option[List[String]] = None
) {
  def toCompany(id: Long): Company =
    Company(
      id,
      name,
      Company.slug(name),
      url,
      location,
      country,
      industry,
      image,
      tags.getOrElse(List())
    )
}

object CompanyCreationRequest {
  given codec: JsonCodec[CompanyCreationRequest] = DeriveJsonCodec.gen[CompanyCreationRequest]
}
