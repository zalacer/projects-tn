package airway

import akka.actor.{ Props, Actor, ActorSystem, ActorLogging }
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Altimeter {
  case class RateChange(amount: Float)
  case class AltitudeUpdate(altitude: Double)
  def apply() = new Altimeter with ProductionEventSource
}

class Altimeter extends Actor with ActorLogging {
  this: EventSource =>

  import Altimeter._

  val ceiling = 43000
  val maxRateOfClimb = 5000
  var rateOfClimb: Float = 0
  var altitude: Double = 0
  var lastTick = System.currentTimeMillis()
  var ticker = context.system.scheduler.schedule(100.millis, 100.millis, self, Tick)

  case object Tick

  def altimeterReceive: PartialFunction[Any, Unit] = {
    case RateChange(amount) =>
      rateOfClimb = amount.min(1.0f).max(-1.0f) * maxRateOfClimb
      log.info(s"Altimeter changed rate of climb to $rateOfClimb.")
    case Tick =>
      val tick = System.currentTimeMillis
      altitude = altitude + ((tick - lastTick) / 60000.0) * rateOfClimb
      lastTick = tick
      sendEvent(AltitudeUpdate(altitude))
  }

  def receive = eventSourceReceive orElse altimeterReceive

  override def postStop(): Unit = ticker.cancel
}

trait AltimeterProvider {
  def newAltimeter: Actor = Altimeter()
}

