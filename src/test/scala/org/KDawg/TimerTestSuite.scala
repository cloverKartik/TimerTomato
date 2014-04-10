package org.KDawg

import akka.testkit._
import org.scalatest._
import akka.actor._
import akka.testkit.TestKit
import scala.concurrent.duration._
import org.scalatest.time.Millisecond

class TimerTestSuite(_system: ActorSystem) extends TestKit(_system) 
	with FunSuiteLike with Matchers with BeforeAndAfterAll with ImplicitSender  
{	
	def this() = this(ActorSystem("TimerTest"))
	
	override def afterAll{ 
	  TestKit.shutdownActorSystem(system)
	}
	
	test( "A Timer Should be created but not started" )
	{
		val timer = system.actorOf(Timer.props())
		expectNoMsg(2 seconds)
		timer ! Timer.Start()
		expectMsg( 2 seconds, Timer.Done )
	}	
	test( "Started Timer should expire with notification" )
	{
	  val timer = system.actorOf(Timer.props())
	  timer ! Timer.Start()
	  expectMsg( 2 seconds, Timer.Done)
	}
	test( "Make Sure Timer doesn't expire early")
	{
	  val timer = system.actorOf( Timer.props(2 seconds)) 
	  timer ! Timer.Start()
	  expectMsg( Timer.Done )
	  timer ! Timer.Query("ExpireTime")
	  expectMsgPF(){
	    case Timer.Reply(v:Long) => ( ( v  > (2 seconds).toMillis) && ( v < (2.5 seconds).toMillis ) )
	    case _ => false
	  }
	}
	test( "Query Expired Timer" ) 
	{
	  val timer = system.actorOf( Timer.props() )
	  timer ! Timer.Start()
	  expectMsg( Timer.Done )
	  timer ! Timer.Query( "IsExpired" )
	  expectMsg( Timer.Reply(true))
	}
	test( "Timer Should aaccept target time specified by start" )
	{
	  val timer = system.actorOf(Timer.props())
	  timer ! Timer.Start( Some(2 seconds) )
	  expectMsg( Timer.Done )
	  timer ! Timer.Query( "ExpireTime" )
	  expectMsgPF() {
	    case Timer.Reply(v:Long) => 
	      println( "Time: " + Duration(v, MILLISECONDS))
	      ( ( v  > (2 seconds).toMillis) && ( v < (2.5 seconds).toMillis ) )
	    case _ => false
	  }
	}
}