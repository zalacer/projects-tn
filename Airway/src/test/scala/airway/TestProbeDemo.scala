package airway

import akka.actor._
import akka.testkit._
import org.scalatest._


class TestProbeDemo extends TestKit(ActorSystem("AnnoyingSpec"))
  with WordSpecLike with MustMatchers with BeforeAndAfterAll {

  // An annoying Actor that just keeps screaming at us
  class AnnoyingActor(snooper: ActorRef) extends Actor {
    override def preStart() {
      self ! 'send
    }
    def receive = {
      case 'send =>
        snooper ! "Hello!!!"
        self ! 'send
    }
  }
  // A nice Actor that just says Hi once  
  class NiceActor(snooper: ActorRef) extends Actor {
    override def preStart() {
      snooper ! "Hi"
    }
    def receive = {
      case _ =>
    }
  }

  //  "The AnnoyingActor" should {
  //    "say Hello!!!" in {
  //      val a = system.actorOf(Props(new AnnoyingActor(testActor)))
  //      expectMsg("Hello!!!")
  //      system.stop(a)
  //    }
  //  }

  "The AnnoyingActor" should {
    "say Hello!!!" in {
      val p = TestProbe() // this made it work - prevented interference with NiceActor
      val a = system.actorOf(Props(new AnnoyingActor(p.ref)))
      // We're expecting the message on the unique TestProbe,
      // not the general testActor that the TestKit provides
      p.expectMsg("Hello!!!")
      system.stop(a)
    }
  }

  //  - should say Hi *** FAILED ***
  //  java.lang.AssertionError: assertion failed: expected Hi, found Hello!!!

  "The NiceActor" should {
    "say Hi" in {
      val q = TestProbe()
      val b = system.actorOf(Props(new NiceActor(q.ref)))
      q.expectMsg("Hi")
      system.stop(b)
    }
  }

  //  "The NiceActor" should {
  //    "say Hi" in {
  //      val a = system.actorOf(Props(new NiceActor(testActor)))
  //      expectMsg("Hi")
  //      system.stop(a)
  //    }
  //  }

}
