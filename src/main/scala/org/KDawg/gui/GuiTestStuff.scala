package org.KDawg.gui
import scala.swing._
import scala.swing.event.ButtonClicked

object GuiTestStuff extends SimpleSwingApplication
{
  def top = new MainFrame {
    title = "First Swing App"
    val startButton = new Button {	
      text = "Start"
    }
    val pauseButton = new Button {
      text = "Pause"
    }
    val stopButton = new Button {
      text = "Stop"
    }
    val label = new Label {
      text = "No button clicks registered"
      horizontalAlignment = Alignment.Center
    }
    var startCount = 0
    var stopCount = 0
    var pauseCount = 0
    
    def printCounts = label.text = s"Start: $startCount, Pause: $pauseCount, Stop: $stopCount"
    listenTo( startButton )
    listenTo( stopButton )
    listenTo( pauseButton )
    reactions += {
      case ButtonClicked(`startButton`) => startCount += 1; printCounts
      case ButtonClicked(`stopButton`) => stopCount += 1; printCounts
      case ButtonClicked(`pauseButton`) => pauseCount += 1; printCounts
    }
    contents = new BoxPanel(Orientation.Vertical) {
      
      contents += new BoxPanel( Orientation.Horizontal ) {
        contents += startButton
        contents += pauseButton
        contents += stopButton
      }
      contents += label
      //border = Swing.EmptyBorder( 30, 30, 10, 30)
    } 
  }
}
