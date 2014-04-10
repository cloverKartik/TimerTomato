package org.KDawg.gui

import scala.concurrent.ExecutionContext
import javax.swing.SwingUtilities
import java.util.concurrent.Executor

object SwingExecutionContext {
  implicit val swingExecutionContext: ExecutionContext = ExecutionContext.fromExecutor(new Executor {
    def execute(command: Runnable): Unit = SwingUtilities invokeLater command
  })
}

