package airway

trait PilotProvider {
  import akka.actor._
  def newPilot(plane: ActorRef, autopilot: ActorRef, controls: ActorRef, altimeter: ActorRef): Actor = new Pilot(plane, autopilot, controls, altimeter)
  def newCoPilot(plane: ActorRef, autopilot: ActorRef, controls: ActorRef, altimeter: ActorRef): Actor = new CoPilot(plane, autopilot, controls, altimeter)
  def newAutopilot: Actor = new AutoPilot()
}