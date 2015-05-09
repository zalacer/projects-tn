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

class CoPilot(plane: ActorRef, autopilot: ActorRef, var controls: ActorRef, altimeter: ActorRef) extends Actor {
  import CoPilots._
  import Plane._
  var pilot: ActorRef = context.system.deadLetters
  import context._
  val config = system.settings.config
  pilot = actorOf(Props[Pilot], config.getString("airway.flightcrew.pilotName"))
  context.watch(pilot) 
  def receive = {
    case ReadyToGo =>
    case Terminated(_) => context.parent ! GiveMeControl
  }
}

object CoPilots {
  case object ReadyToGo
  case object RelinquishControl
}
