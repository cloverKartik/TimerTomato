package org.KDawg
import akka.testkit._
import org.scalatest._
import akka.actor._
import akka.testkit.TestKit
import scala.concurrent.duration._
import org.scalatest.time.Millisecond
import TimerController._
import org.scalatest.matchers.ShouldMatchers

class TimerControllerTestSuite( _system: ActorSystem ) extends TestKit(_system) with
	FunSuiteLike with ShouldMatchers with ImplicitSender with BeforeAndAfterAll
{
  def this() = this(ActorSystem("TimerControllerSystem"))
  
  override def afterAll = system.shutdown
  
  test("Timer Controller Should get WorkDone")
  {
    val ctrl = system.actorOf( TimerController.props( 1 second, 1 second ))
    system.eventStream.subscribe( testActor, classOf[TimeRemaining])
    system.eventStream.subscribe( testActor, WorkUnitDone.getClass )
    ctrl ! StartWork
    expectMsgClass(classOf[TimeRemaining])
    fishForMessage(2 seconds, "Looking for WorkUnitDone"){
      case WorkUnitDone => true
      case _ => false
    }
  }
  test( "Timer can start a break period" )
  {
    val ctrl = system.actorOf( TimerController.props(1 second, 1 second ))
    system.eventStream.subscribe(testActor, BreakUnitDone.getClass)
    ctrl ! StartBreak
    fishForMessage(2 seconds, "Looking for BreakUnitDone" ){
      case BreakUnitDone => true
      case _  => false
    }
  }
  
}