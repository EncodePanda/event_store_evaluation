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

trait InitReadParticular {

  val system = ActorSystem()

  val settings = Settings(
    address = new InetSocketAddress("172.17.0.3", 1113),
    defaultCredentials = Some(UserCredentials("admin", "changeit")))

  val stream: Id = EventStream.Id("user_2")

  val messages =  List(
    ReadEvent(stream, EventNumber.First),
    ReadEvent(stream, EventNumber.Last),
    ReadEvent(stream, EventNumber.Exact(3))
  )
}

object ReadParticularWithActors extends App with InitReadParticular {
  val connection = system.actorOf(ConnectionActor.props(settings))
  implicit val readResult = system.actorOf(Props[ReadResult])

  messages.foreach{message =>
    connection ! message
  }

  class ReadResult extends Actor with ActorLogging {
    def receive = {
      case ReadEventCompleted(event) =>
        log.info("event: {}", event)

      case Failure(e: EsException) =>
        log.error(e.toString)
        context.system.shutdown()
    }
  }
}


object ReadParticularWithEsConnection extends App with InitReadParticular {

  import system.dispatcher

  val connection = EsConnection(system, settings)
  val log = system.log

  messages.map(connection.future(_)).foreach{ readEvent =>
    readEvent.onSuccess {
      case ReadEventCompleted(event) =>
        log.info(event.toString)
    }
  }
}


