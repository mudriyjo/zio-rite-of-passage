package com.mudriyjo

import io.getquill._
import io.getquill.jdbczio.Quill
import zio.*

object QuillDemo extends ZIOAppDefault {
  val program = for {
    repo <- ZIO.service[JobRepository]
    _ <- repo.create(Job(-1, "Software engineer", "noname", "noname.com"))
    _ <- repo.create(Job(-1, "Data engineer", "noname", "noname.com"))
    _ <- repo.create(Job(-1, "Trainer", "X", "x.com"))
  } yield()
  override def run = program.provide(
    PostgresJobRepository.live,
    Quill.Postgres.fromNamingStrategy(SnakeCase),
    Quill.DataSource.fromPrefix("db")
  )
}

trait JobRepository {
  def create(job: Job): Task[Job]
  def delete(id: Long): Task[Job]
  def update(id: Long, f: Job => Job): Task[Job]
  def getAll: Task[List[Job]]
  def get(id: Long): Task[Option[Job]]
}

class PostgresJobRepository(quill: Quill.Postgres[SnakeCase]) extends JobRepository {
  import quill._

  inline given schema: SchemaMeta[Job] = schemaMeta("job")
  inline given insSchema: InsertMeta[Job] = insertMeta(_.id)
  inline given updSchema: UpdateMeta[Job] = updateMeta(_.id)
  override def create(job: Job): Task[Job] =
    run {
      query[Job]
        .insertValue(lift(job))
        .returning(j => j)
    }

  override def delete(id: Long): Task[Job] =
    run {
      query[Job]
        .filter(j => j.id == lift(id))
        .delete
        .returning(j => j)
    }

  override def update(id: Long, f: Job => Job): Task[Job] =
    for {
      job <- get(id).someOrFail(new RuntimeException(s"can't find key $id"))
      res <- run {
        query[Job]
          .filter(j => j.id == lift(id))
          .updateValue(lift(f(job)))
          .returning(j => j)
      }
    } yield res

  override def getAll: Task[List[Job]] =
    run {query[Job]}

  override def get(id: Long): Task[Option[Job]] =
    run {
      query[Job]
        .filter(j => j.id == lift(id))
    }.map(_.headOption)
}

object PostgresJobRepository {
  val live: ZLayer[Quill.Postgres[SnakeCase], Nothing, PostgresJobRepository] = ZLayer {
    ZIO.service[Quill.Postgres[SnakeCase]].map(q => new PostgresJobRepository(q))
  }
}