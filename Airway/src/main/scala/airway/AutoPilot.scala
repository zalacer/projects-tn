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

class AutoPilot extends Actor {
  import AutoPilots._
  import Plane._
  import context._
  val config = system.settings.config
  var copilot: ActorRef = context.system.deadLetters
  copilot = actorOf(Props[CoPilot], config.getString("airway.flightcrew.copilotName"))
  context.watch(copilot)
  def receive = {
    case Terminated(_) =>
      context.parent ! GiveMeControl
  }
}

object AutoPilots {
  case object ReadyToGo
  case object RelinquishControl
}
