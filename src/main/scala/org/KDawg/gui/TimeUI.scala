package org.KDawg.gui

import org.KDawg._
import swing._
import swing.event._
import GridBagPanel._
import akka.actor._
import SwingExecutionContext._
import org.KDawg._
import concurrent._
import duration._

object TimeUI extends SimpleSwingApplication {
  object TimerWindowRows extends Enumeration {
    type TimerWindowRows = Value
    val TimeRow = Value("TimeRemaining")
    val ButtonRow = Value("ButtonRow")
  }
  class EventReceiver extends Actor {
    def initial: Receive = {
      case TimerController.TimeRemaining(duration) =>
        //Update the Label in Future on EDT
        val hrs = duration.toSeconds / 3600
        val min = (duration.toSeconds % 3600) / 60
        val secs = (duration.toSeconds % 3600) % 60
        future { timeLabel.text = f"$hrs%02d:$min%02d:$secs%02d" }
    }
    def receive = initial
  }
  val startWorkButton = new Button("StartWork")
  val pauseButton = new Button("Pause")
  val stopButton = new Button("Stop")
  val timeLabel = new Label {
    text = "00:00:00"
  }

  val ui = new GridBagPanel {

    val c = new Constraints
    c.fill = Fill.Horizontal
    c.weightx = 0.5
    c.gridx = 0;
    c.gridy = TimerWindowRows.ButtonRow.id;
    layout(startWorkButton) = c

    c.fill = Fill.Horizontal
    c.weightx = 0.5;
    c.gridx = 1;
    c.gridy = TimerWindowRows.ButtonRow.id;
    layout(pauseButton) = c

    c.fill = Fill.Horizontal
    c.weightx = 0.5;
    c.gridx = 2;
    c.gridy = TimerWindowRows.ButtonRow.id;
    layout(stopButton) = c

    c.fill = Fill.Horizontal
    c.weightx = 0.5
    c.gridx = 0
    c.gridy = TimerWindowRows.TimeRow.id;
    c.gridwidth = 3
    layout(timeLabel) = c
  }

  def top = new MainFrame {
    title = "GridBag Demo"
    contents = ui
    val actorSystem = ActorSystem("TimerActors")
    val timerCntrl = actorSystem.actorOf(TimerController.props(10 seconds, 2 seconds ) )
    val eventHandler = actorSystem.actorOf( Props( new EventReceiver ) )
    actorSystem.eventStream.subscribe(eventHandler, classOf[TimerController.TimeRemaining] )
    listenTo(startWorkButton) 
    reactions += {
      case ButtonClicked( `startWorkButton` ) =>
        timerCntrl ! TimerController.StartWork
    }
  }

}