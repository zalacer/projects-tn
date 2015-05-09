package airway


import akka.actor._ 
import akka.actor.SupervisorStrategy._
import akka.actor.{ OneForOneStrategy }
import akka.agent.Agent
import akka.pattern.ask
import akka.util.Timeout
import akka.routing.FromConfig
import akka.routing.RoundRobinRouter
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{ Success, Failure }

class Pilot(plane: ActorRef, autopilot: ActorRef, var controls: ActorRef, altimeter: ActorRef)
  extends Actor {
  import Pilots._
  import Plane._
  import context._
  var copilot: ActorRef = context.system.deadLetters
   val config = system.settings.config
  val copilotName = config.getString("airway.flightcrew.copilotName")
  def receive = {
    case ReadyToGo =>
      context.parent ! Plane.GiveMeControl
      copilot = actorOf(Props[CoPilot], config.getString("airway.flightcrew.copilotName"))
    case Controls(controlSurfaces) => controls = controlSurfaces
  }
}

object Pilots {
  case object ReadyToGo
  case object RelinquishControl
}

