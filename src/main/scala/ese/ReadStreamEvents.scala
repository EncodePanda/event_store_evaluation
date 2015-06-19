package ese

import java.io.Closeable
import java.net.InetSocketAddress
import java.util.UUID

import akka.actor.Status.Failure
import akka.actor._
import eventstore.j.{EsConnectionFactory, SettingsBuilder}
import eventstore.tcp.ConnectionActor
import eventstore._
import scala.concurrent.Future
import scala.concurrent.duration._

trait InitReadAll {
  val system = ActorSystem()

  val settings = Settings(
    address = new InetSocketAddress("172.17.0.3", 1113),
    defaultCredentials = Some(UserCredentials("admin", "changeit")))

  val stream = EventStream.Id("user_2")

  val message: ReadStreamEvents = ReadStreamEvents(stream)
}

object ReadAllStreamEventsExampleActorWay extends App with InitReadAll {

  val connection = system.actorOf(ConnectionActor.props(settings))
  implicit val readResult = system.actorOf(Props[ReadResult])

  connection ! message

  class ReadResult extends Actor with ActorLogging {
    def receive = {
      case x: ReadStreamEventsCompleted =>
        x.events.foreach { evt => log.info(s"$evt") }
        context.system.shutdown()

      case Failure(e: EsException) =>
        log.error(e.toString)
        context.system.shutdown()
    }
  }

}

object ReadAllStreamEventsExampleEsConnectionWay extends App with InitReadAll {

  import system.dispatcher

  val connection = EsConnection(system, settings)
  val log = system.log

  val readStreamEvents: Future[ReadStreamEventsCompleted] = connection.future(message)
  readStreamEvents.onSuccess {
    case x: ReadStreamEventsCompleted =>
      x.events.foreach { evt => log.info(s"$evt") }
      system.shutdown()
  }
}


