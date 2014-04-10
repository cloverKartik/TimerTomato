package org.KDawg

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.Cancellable
import scala.concurrent.duration._
import Timer._


object TimerController
{
  case object StartWork
  case object PauseWork
  case object StopWork
  case object StartBreak
  case object PauseBreak
  case object StopBreak
  case object WorkUnitDone
  case object BreakUnitDone
  case class TimeRemaining( duration: FiniteDuration )
  
  def props( workUnit: FiniteDuration = 25 minutes, breakUnit: FiniteDuration = 5 minutes ) = 
    Props( new TimerController( workUnit, breakUnit ) ) 
}
class TimerController( work: FiniteDuration, brkTime: FiniteDuration ) extends Actor with ActorLogging{
  import TimerController._
  var workUnit = work
  var breakUnit = brkTime
  
  private[this] def startTimer( timeUnit: FiniteDuration ) =
  {
    val timer = context.actorOf( Timer.props(timeUnit), "timer" )
    timer ! Start()
    timer
  }
  
  def receive = initial
  
  def initial: Receive = {
    case StartWork => context.become( working( startTimer( workUnit ) ) )
    case StartBreak => context.become( break( startTimer( breakUnit ) ) ) 
  }
  
  def break( timer: ActorRef):Receive = {
    case Done =>
      context.system.eventStream.publish(BreakUnitDone)
      timer ! Abort
      context.become( initial )
    case Remaining( remaining: FiniteDuration ) =>
      context.system.eventStream.publish( TimeRemaining( remaining ) )
    
  }
  def working( timer: ActorRef): Receive = {
    case Done => 
      context.system.eventStream.publish(WorkUnitDone)
      timer !  Abort
      context.become(initial)
    case Remaining( remaining: FiniteDuration ) =>
      context.system.eventStream.publish( TimeRemaining( remaining ) )
      
  }
  /*
  def working( timer: ActorRef, current: FiniteDuration): Receive = {
    case Done => 
      context.system.eventStream.publish(WorkUnitDone)
      timer !  Abort
      context.become(initial)
    case Remaining( remaining: FiniteDuration ) =>
      println( s"Remaining Time: $remaining" )
      if( (current - remaining) >= (1 second) ){
    	  context.system.eventStream.publish( TimeRemaining( remaining ) )
    	  context.become(working( timer, remaining ))
      }
  }*/
}