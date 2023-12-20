package com.mudriyjo

import zio.*

import scala.io.StdIn

object ZIORecap extends ZIOAppDefault {

  // Data structure examples
  val miningOfLife: ZIO[Any, Nothing, Int] = ZIO.succeed(42)

  val error: ZIO[Any, String, Nothing] = ZIO.fail("Something went wrong")

  val suspend: ZIO[Any, Throwable, Int] = ZIO.suspend(miningOfLife)

  // working with ZIO data structure
  val improvedMiningOfLife: ZIO[Any, Nothing, Int] = miningOfLife.map(_ * 2)
  val otherVersion = miningOfLife.flatMap(x => ZIO.succeed(println(x)))

  // computation
  val smallProgram = for {
    _ <- Console.printLine("Hello, what is your name?")
    name <- ZIO.succeed(StdIn.readLine())
    _ <- Console.printLine(s"Hello, to ZIO $name")
  } yield ()

  // error handling
  val anAttempt: ZIO[Any, Throwable, Int] = ZIO.attempt {
    println("Hello world")
    val text: String = null
    text.length
  }

  val catchAll = anAttempt.catchAll(x => ZIO.succeed("Catch all exception!"))
  val catchSome = anAttempt.catchSome {
    case e: Throwable => ZIO.succeed(s"We ignore Throwable $e")
    case _ => ZIO.succeed("We ignore other errors")
  }

  // fibers
  val slowJob = ZIO.sleep(4.seconds) *> Random.nextIntBetween(0, 100)

  // 8 seconds for job
  val aSlowPair = for {
    a <- slowJob
    b <- slowJob
  } yield (a,b)

  // 4 seconds for job
  val aFastPair = for {
    aFork <- slowJob.fork
    bFork <- slowJob.fork
    a <- aFork.join
    b <- bFork.join
  } yield (a, b)

  // fiber interruption
  val fiberInterruption = for {
    fib <- slowJob.onInterrupt(ZIO.succeed(println("Fiber was interrupted!"))).fork
    _ <- ZIO.sleep(500.milliseconds) *> ZIO.succeed(println("canceling fiber")) *> fib.interrupt
    _ <- fib.join
  } yield ()

  val ignoreFiberInterruption = for {
    fib <- ZIO.uninterruptible(slowJob.map(println).onInterrupt(ZIO.succeed(println("Fiber was interrupted!")))).fork
    _ <- ZIO.sleep(500.milliseconds) *> ZIO.succeed(println("canceling fiber")) *> fib.interrupt
    _ <- fib.join
  } yield ()

  // ZIO API
  val pairRandom = slowJob.zipWithPar(slowJob).andThen(println)
  val randomx10 = ZIO.collectAllPar((0 to 10).map(_ => slowJob)).map(println)
  // reduceAllPar, mergeAllPar, foreachPar...

  // dependencies
  case class User(name: String, email: String)
  class SubscriptionService(emailServices: EmailServices, db: DatabaseService) {
    def subscribeUser(user: User): Task[Unit] = for {
      _ <- db.saveUser(user)
      _ <- emailServices.sendEmail(user)
      _ <- ZIO.succeed(println(s"user ${user.name} subscribed"))
    } yield ()
  }
  object SubscriptionService {
    val live: ZLayer[EmailServices with DatabaseService, Throwable, SubscriptionService] =
      ZLayer.fromFunction((emailServ, dbService) => new SubscriptionService(emailServ, dbService))
  }
  class EmailServices() {
    def sendEmail(user: User): Task[Unit] = ZIO.succeed(println(s"email sent to user: ${user.name}, mail: ${user.email}"))
  }
  object EmailServices {
    val live: ZLayer[Any, Throwable, EmailServices] = ZLayer.succeed(new EmailServices())
  }
  class DatabaseService(pool: ConnectionPool) {
    def saveUser(user: User): Task[Unit] = ZIO.succeed(println(s"user ${user.name} saved"))
  }
  object DatabaseService {
    val live: ZLayer[ConnectionPool, Throwable, DatabaseService] =
      ZLayer.fromFunction((pool) => new DatabaseService(pool))
  }
  class ConnectionPool(nConnection: Int) {
    def get: Task[Unit] = ZIO.succeed(Connection())
  }
  object ConnectionPool {
    def live(nConn: Int): ZLayer[Any, Throwable, ConnectionPool] =
      ZLayer.succeed(new ConnectionPool(nConn))
  }
  case class Connection()

  def subscribe(user: User): ZIO[SubscriptionService, Throwable, Unit] = for {
    sub <- ZIO.service[SubscriptionService]
    _ <- sub.subscribeUser(user)
  } yield ()

  val program = for {
    _ <- subscribe(User("Alex", "alex@gmail.com"))
    _ <- subscribe(User("John", "John@gmail.com"))
  } yield ()

  override def run = program.provide(
    SubscriptionService.live,
    EmailServices.live,
    DatabaseService.live,
    ConnectionPool.live(10),
  )
}
