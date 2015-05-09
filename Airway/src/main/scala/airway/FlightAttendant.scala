package airway

import akka.actor.Actor
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

trait AttendantResponsiveness {
  val maxResponseTimeMS: Int
  def responseDuration = scala.util.Random.nextInt(maxResponseTimeMS).millis
}

object FlightAttendant {
  case class GetDrink(drinkname: String)
  case class Drink(drinkname: String)
  def apply() = new FlightAttendant with AttendantResponsiveness {
    val maxResponseTimeMS = 300000
  }
}

class FlightAttendant extends Actor { this: AttendantResponsiveness =>
  import FlightAttendant._
  def receive = {
    case GetDrink(drinkname) =>
      context.system.scheduler.scheduleOnce(responseDuration, sender,
        Drink(drinkname))
  }
}
