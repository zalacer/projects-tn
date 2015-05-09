package airway

import akka.actor.{ Actor, ActorRef, Props, ActorLogging }
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

object Plane {
  def apply() = new Plane with AltimeterProvider with PilotProvider with LeadFlightAttendantProvider
  case object GiveMeControl
  case class Controls(controls: ActorRef)
}

class Plane extends Actor with ActorLogging {
  this: AltimeterProvider with PilotProvider with LeadFlightAttendantProvider =>
  implicit val askTimeout: Timeout = 1.second 
  import Pilots._
  import Altimeter._
  import Plane._
  import IsolatedLifeCycleSupervisor._
  import context._

  val altimeter = actorOf(Props[Altimeter])
  val controls = actorOf(Props(new ControlSurfaces(altimeter)))
  val config = system.settings.config
  val pilotName = config.getString("airway.flightcrew.pilotName")
  val pilotref = actorOf(Props[Pilot], pilotName)
  val copilotName = config.getString("airway.flightcrew.copilotName")
  val copilotref = actorOf(Props[CoPilot], copilotName)
  val autopilot = actorOf(Props[AutoPilot], "AutoPilot")
  val flightAttendant = actorOf(Props(LeadFlightAttendant()),
    config.getString("airway.flightcrew.leadAttendantName"))

  def actorForControls(name: String) = actorFor("Controls/" + name)
  def actorForPilots(name: String) = actorFor("Pilots/" + name)

   def startControls() {
    val controls = actorOf(Props(new IsolatedResumeSupervisor with OneForOneStrategyFactory {
      def childStarter() {
        val alt = actorOf(Props(newAltimeter), "Altimeter")
        actorOf(Props(newAutopilot), "AutoPilot")
        actorOf(Props(new ControlSurfaces(alt)), "ControlSurfaces")
      }
    }), "Controls")
    Await.result(controls ? WaitForStart, 1.second)
  }

  def startPeople() {
    val plane = self
    val controls = actorForControls("ControlSurfaces")
    val autopilot = actorForControls("AutoPilot")
    val altimeter = actorForControls("Altimeter")
    val pilotName = config.getString("airway.flightcrew.pilotName")
    val copilotName = config.getString("airway.flightcrew.copilotName")
    val leadAttendantName = config.getString("airway.flightcrew.leadAttendantName")
    val people = actorOf(Props(new IsolatedStopSupervisor with OneForOneStrategyFactory {
      def childStarter() {
         context.actorOf(Props(newPilot(plane, autopilot, controls, altimeter)), pilotName)
        context.actorOf(Props(newCoPilot(plane, autopilot, controls, altimeter)), copilotName)
      }
    }), "Pilots")
    actorOf(Props(newFlightAttendant), leadAttendantName)
    Await.result(people ? WaitForStart, 1.second)
  }

   override def preStart() {
    startControls()
    startPeople()
    actorForControls("Altimeter") ! EventSource.RegisterListener(self)
    actorForPilots(pilotName) ! Pilots.ReadyToGo
    actorForPilots(copilotName) ! Pilots.ReadyToGo
  }

  def receive = {
    case AltitudeUpdate(altitude) => log.info(s"Altitude is now: $altitude")
    case GiveMeControl =>
      log.info("Plane giving control.")
      sender ! controls
  }
}