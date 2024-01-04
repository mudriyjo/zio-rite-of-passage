package com.mudriyjo

import zio.json.{DeriveJsonCodec, JsonCodec}

case class JobUpdateRequest(title: String, companyName: String, url: String)
object JobUpdateRequest {
  given codec: JsonCodec[JobUpdateRequest] = DeriveJsonCodec.gen[JobUpdateRequest]
}
case class JobCreationRequest(title: String, companyName: String, url: String)
object JobCreationRequest {
  given codec: JsonCodec[JobCreationRequest] = DeriveJsonCodec.gen[JobCreationRequest]
}
case class Job(id: Long, title: String, companyName: String, url: String)
object Job {
  given codec: JsonCodec[Job] = DeriveJsonCodec.gen[Job]
}