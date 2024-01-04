package com.mudriyjo

import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import zio.*
import zio.http.Server
import zio.json.{DeriveJsonCodec, JsonCodec}

import scala.collection.mutable
object TapirDemo extends ZIOAppDefault {

  // Simple example
  val simpleEndpoint = endpoint
    .tag("simple")
    .name("simple")
    .description("simplest endpoint")
    .in("simple")
    .out(plainBody[String])
    .serverLogicSuccess[Task](_ => ZIO.succeed("Hello world!"))

  val program = Server.serve(
    ZioHttpInterpreter(
      ZioHttpServerOptions.default
    ).toHttp(simpleEndpoint)
  )

  // Endpoints for Job create, update, delete, getById, getAll
  val createJobEndpoint: ServerEndpoint[Any, Task] = endpoint
    .tag("Job")
    .name("job")
    .description("Create job")
    .post
    .in("job")
    .in(jsonBody[JobCreationRequest])
    .out(jsonBody[Job])
    .serverLogicSuccess(req => ZIO.succeed {
      val maxId = db.keys.max
      val newJob = Job(maxId + 1, title = req.title, companyName = req.companyName, url = req.url)
      db.addOne(maxId + 1, newJob)
      newJob
    })

  val updateJobEndpoint: ServerEndpoint[Any, Task] = endpoint
    .tag("Job")
    .name("job")
    .description("Update job by id")
    .put
    .in("job" / path[Long]("id"))
    .in(jsonBody[JobUpdateRequest])
    .out(jsonBody[Job])
    .serverLogicSuccess(req => ZIO.succeed {
      val updatedJob = Job(id = req._1, title = req._2.title, companyName = req._2.companyName, url = req._2.url)
      db.update(req._1, updatedJob)
      updatedJob
    })

  val deleteJobEndpoint: ServerEndpoint[Any, Task] = endpoint
    .tag("Job")
    .name("job")
    .description("Delete job by id")
    .delete
    .in("job" / path[Long]("id"))
    .out(jsonBody[Boolean])
    .serverLogicSuccess(req => ZIO.succeed {
      val deleted = db.keys.exists(key => key == req)
      db.remove(req)
      deleted
    })

  val getJobByIdEndpoint: ServerEndpoint[Any, Task] = endpoint
    .tag("Job")
    .name("job")
    .description("Get job by id")
    .get
    .in("job" / path[Long]("id"))
    .out(jsonBody[Option[Job]])
    .serverLogicSuccess(req => ZIO.succeed {
      db.get(req)
    })

  val getAllJobEndpoint: ServerEndpoint[Any, Task] = endpoint
    .tag("Job")
    .name("job")
    .description("Get all job")
    .get
    .in("job")
    .out(jsonBody[List[Job]])
    .serverLogicSuccess(req => ZIO.succeed {
      db.values.toList
    })

  val jobProgramm = Server.serve(
    ZioHttpInterpreter(
      ZioHttpServerOptions.default
    ).toHttp(List(createJobEndpoint, updateJobEndpoint, deleteJobEndpoint, getJobByIdEndpoint, getAllJobEndpoint))
  )

  val db: mutable.Map[Long, Job] =
    mutable.Map(1L -> Job(1L, "Software engineer", "LoLcompany", "lolcompany@gmail.com"))

  override def run = jobProgramm.provide(
    Server.default
  )
}
