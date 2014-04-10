package org.KDawg
import scala.slick.driver.H2Driver.simple._
import scala.slick.lifted.{ProvenShape, ForeignKeyQuery}

case class Task( id: Int, 
                 description: String, 
                 completed: Boolean, 
                 timeUnitsTaken: Int, 
                 timeUnitsEstimated: Int )

class Tasks( tag: Tag ) extends Table[Task](tag, "TASKS" ) {
  def id: Column[Int] = column[Int]("TASK_ID", O.PrimaryKey)
  def description: Column[String] = column[String]("DESC")
  def completed: Column[Boolean] = column[Boolean]("COMPLETED")
  def taken: Column[Int] = column[Int]("TAKEN")
  def estimated: Column[Int] = column[Int]("ESTIMATE")
  
  // Every table needs a * projection with the same type as the table's type parameter
  def * : ProvenShape[Task] = (id, description, completed, taken, estimated ) <> (Task.tupled, Task.unapply)
}

