package org.KDawg
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSystem
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.Cancellable
import scala.concurrent.duration._
import akka.actor.PoisonPill

object Timer
{
  case class Start( setTime: Option[FiniteDuration] = None )
  case object Stop
  case object Resume
  case object Done
  case object Count
  case object Abort
  case class Query( q: String )
  case class Reply( v: Any )
  case class Remaining( remaining: FiniteDuration )
  def props(timerVal: FiniteDuration = 1 seconds ) = Props(new Timer( timerVal ) )
}

class Timer( timerVal: FiniteDuration, tickRate: FiniteDuration = 1 second) extends Actor with ActorLogging {
  import Timer._
  import context.dispatcher

   private [this] var duration: Long = 0;
   private [this] var prevTime: Long = 0;
   def initial: Receive = { 
     case Start( time ) => 
       if( time.isDefined ) start( time.get ) else start( timerVal )
     case Abort => context.stop(self)
   }
   def receive = initial
   
   private [this] def start( time: FiniteDuration ) {
     	prevTime = System.currentTimeMillis
     	val canc = context.system.scheduler.schedule(0 seconds, tickRate, self, Count )
     	context.become( countingDown ( canc, sender ) )
   }
   def countingDown( timerCancellable: Cancellable, receiver: ActorRef ): Receive = {
     case Count =>
       val tmpTime = System.currentTimeMillis
       duration += tmpTime - prevTime
       prevTime = tmpTime
       
       if( duration >= timerVal.toMillis ) {
         timerCancellable.cancel
         receiver ! Done
         context.become(expired(receiver)) 
       }
       else{
         receiver ! Remaining( timerVal - Duration(duration, MILLISECONDS) )
       }
     case Abort => 
       timerCancellable.cancel
       self ! PoisonPill
     case Query("IsExpired") => sender ! Reply(false)
     case Query(str: String) => Reply("Failed")
   }
   
   def expired(receiver: ActorRef): Receive =
   {
     case Query("ExpireTime") => sender ! Reply(duration)
     case Query("IsExpired") => sender ! Reply(true)
     case Abort => context.stop(self)
   }
}