name := """TimerTomato"""

version := "1.0"

scalaVersion := "2.10.2"

mainClass in Compile := Some("HelloSlick")

libraryDependencies ++= List(
  "com.typesafe.akka" %% "akka-actor" % "2.2.1",
  "com.typesafe.akka" %% "akka-testkit" % "2.2.1",
  "com.typesafe.slick" %% "slick" % "2.0.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.h2database" % "h2" % "1.3.170",
  "org.scalatest" %% "scalatest" % "2.0"
)
