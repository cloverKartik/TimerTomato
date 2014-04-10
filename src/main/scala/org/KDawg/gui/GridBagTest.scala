package org.KDawg.gui

import swing._
import swing.event._
import GridBagPanel._

object GridBagDemo extends SimpleSwingApplication {
  object TimerWindowRows extends Enumeration {
    type TimerWindowRows = Value
    val TimeRow = Value("TimeRemaining")
    val ButtonRow = Value("ButtonRow")
  }
  lazy val ui = new GridBagPanel {
    val c = new Constraints
    val shouldFill = true
    if (shouldFill) {
      c.fill = Fill.Horizontal
    }

    val startWorkButton = new Button("StartWork") 
    
    c.weightx = 0.5

    c.fill = Fill.Horizontal
    c.gridx = 0;
    c.gridy = TimerWindowRows.ButtonRow.id;
    layout(startWorkButton) = c
    
    val pauseButton = new Button("Pause")
    c.fill = Fill.Horizontal
    c.weightx = 0.5;
    c.gridx = 1;
    c.gridy = TimerWindowRows.ButtonRow.id;
    layout(pauseButton) = c

    val stopButton = new Button("Stop")
    c.fill = Fill.Horizontal
    c.weightx = 0.5;
    c.gridx = 2;
    c.gridy = TimerWindowRows.ButtonRow.id;
    layout(stopButton) = c
    
    val timeLabel = new Label {
     	text = "00:00:00"
    }
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
  }
}
