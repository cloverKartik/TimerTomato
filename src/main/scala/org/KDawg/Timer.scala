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
  case class Stopped( remaining: Duration )
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
   def initial: Receive = { 
     case Start( time ) => 
       if( time.isDefined ) start( time.get ) else start( timerVal )
     case Abort => context.stop(self)
   }
   def receive = initial
   
   private [this] def start( time: FiniteDuration ) {
     	val canc = context.system.scheduler.schedule(0 seconds, tickRate, self, Count )
     	context.become( countingDown ( canc, sender, 0 ) )
   }
   private[this] def delta( prevTime: Long ): (Long, Long) = {
     val tmpTime = System.currentTimeMillis()
     if( prevTime != 0 )
       ( tmpTime - prevTime, tmpTime )
     else
       ( 0, tmpTime )
   }
   def countingDown( timerCancellable: Cancellable, receiver: ActorRef, prevTime: Long ): Receive = {
     case Count =>
       val ( d, tmpTime ) = delta( prevTime )
       duration += d
       if( duration >= timerVal.toMillis ) {
         timerCancellable.cancel
         receiver ! Done
         context.become(expired(receiver)) 
       }
       else{
    	 receiver ! Remaining( timerVal - Duration(duration, MILLISECONDS) )
     	 println( s"Remaining Time: ${timerVal - Duration( duration, MILLISECONDS)}")
         context.become( countingDown( timerCancellable, receiver, tmpTime) )
       }
     case Abort => 
       timerCancellable.cancel
       self ! PoisonPill
     case Stop =>
       val (d, tmpTime) = delta( prevTime )
       duration += d
       timerCancellable.cancel
       if( duration >= timerVal.toMillis ){
         receiver ! Done
         context.become(expired(receiver))
       }
       else
       {
         receiver ! Stopped( timerVal - Duration(duration, MILLISECONDS)) 
         context.become( stopped )
       }
     case Query("IsExpired") => sender ! Reply(false)
     case Query(str: String) => Reply("Failed")
   }
   
   def stopped: Receive ={
     case Start(t) => start( timerVal )
     case Abort => context.stop(self)
   }
   def expired(receiver: ActorRef): Receive =
   {
     case Query("ExpireTime") => sender ! Reply(duration)
     case Query("IsExpired") => sender ! Reply(true)
     case Abort => context.stop(self)
   }
}