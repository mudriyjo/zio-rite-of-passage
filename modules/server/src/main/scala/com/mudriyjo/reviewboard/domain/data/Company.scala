package com.mudriyjo.reviewboard.domain.data

import zio.json.*

final case class Company(
    id: Long,
    name: String,
    slug: String,
    url: String,
    location: Option[String] = None,
    country: Option[String] = None,
    industry: Option[String] = None,
    image: Option[String] = None,
    tags: List[String] = List()
)

object Company {
  given codec: JsonCodec[Company] = DeriveJsonCodec.gen[Company]

  def slug(name: String): String =
    name
      .replace(" +", " ")
      .split(" ")
      .map(_.toLowerCase())
      .mkString("-")
}
