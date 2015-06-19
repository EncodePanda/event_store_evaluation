name := "es-evaluation"

version := "1.0"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.geteventstore" % "eventstore-client_2.11" % "2.0.2",
  "org.scalatest" % "scalatest_2.10" % "2.2.1" % "test"
)

