package ese

import java.io.Closeable
import java.net.InetSocketAddress
import java.util.UUID

import akka.actor.Status.Failure
import akka.actor._
import eventstore.EventStream.Id
import eventstore.j.{EsConnectionFactory, SettingsBuilder}
import eventstore.tcp.ConnectionActor
import eventstore._
import scala.concurrent.Future
import scala.concurrent.duration._

trait InitReadAllEvents {

  val system = ActorSystem()

  val settings = Settings(
    address = new InetSocketAddress("172.17.0.3", 1113),
    defaultCredentials = Some(UserCredentials("admin", "changeit")))
}

object ReadAllEventsActorWay extends App with InitReadAllEvents {

  val connection = system.actorOf(ConnectionActor.props(settings))
  implicit val readResult = system.actorOf(Props[ReadResult])

  connection ! ReadAllEvents()

  class ReadResult extends Actor with ActorLogging {
    def receive = {
      case x: ReadAllEventsCompleted =>
        x.events.foreach(evt => log.info(s"$evt"))
        context.system.shutdown()

      case Failure(e: EsException) =>
        log.error(e.toString)
        context.system.shutdown()
    }
  }
}

object ReadAllEventsEsConnectionWay extends App with InitReadAllEvents {
  import system.dispatcher

  val connection = EsConnection(system, settings)
  val log = system.log

  val stream = EventStream.Id("users")

  val readAllEvents: Future[ReadAllEventsCompleted] = connection.future(ReadAllEvents())
  readAllEvents.onSuccess {
    case x: ReadAllEventsCompleted =>
      x.events.foreach(evt => log.info(s"$evt"))
      system.shutdown()
  }
}


