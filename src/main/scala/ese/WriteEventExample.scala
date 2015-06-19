package ese

import java.net.InetSocketAddress

import akka.actor.Status.Failure
import akka.actor.{ActorLogging, Actor, Props, ActorSystem}
import eventstore.EventStream.Id
import eventstore._
import eventstore.tcp.ConnectionActor

import scala.concurrent.Future

trait InitWrite {

  val system = ActorSystem()

  val settings = Settings(
    address = new InetSocketAddress("172.17.0.3", 1113),
    defaultCredentials = Some(UserCredentials("admin", "changeit")))

  val stream: Id = EventStream.Id("user_2")

  val eventId: Uuid = java.util.UUID.randomUUID
  val data: Content = Content.Json( """{"lastName" : "Doe"}""")
  val event = EventData("UserLastNameChanged", eventId = eventId, data = data)
}

object WriteEventExampleActorWay extends App with InitWrite {

  class WriteResult extends Actor with ActorLogging {
    def receive = {
      case WriteEventsCompleted(range, position) =>
        log.info("range: {}, position: {}", range, position)
        context.system.shutdown()

      case Failure(e: EsException) =>
        log.error(e.toString)
        context.system.shutdown()
    }
  }

  val connection = system.actorOf(ConnectionActor.props(settings))
  implicit val writeResult = system.actorOf(Props[WriteResult])

  connection ! WriteEvents(stream, List(event))
}


object WriteEventExampleEsConnectionWay extends App with InitWrite {

  val esConnection = EsConnection(system, settings)
  val log = system.log

  import system.dispatcher

  val writeEvents: Future[WriteEventsCompleted] = esConnection.future(WriteEvents(stream, List(event)))
  writeEvents.onSuccess {
    case x: WriteEventsCompleted =>
      log.info(x.numbersRange.toString)
      system.shutdown()
  }
}