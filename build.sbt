name := "lagom-with-kafka"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.6"

organization in ThisBuild := "com.knoldus"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4" % Test
val logger = "ch.qos.logback" % "logback-classic" % "1.2.3"
val scalaLogger = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"

lazy val `user` = (project in file("."))
  .aggregate(`user-api`, `user-impl`)

lazy val `user-api` = (project in file("user-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `user-impl` = (project in file("user-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      logger,
      scalaLogger,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`user-api`)

lagomUnmanagedServices in ThisBuild := Map("external-service" -> "https://gist.githubusercontent.com")
import scala.concurrent.duration._
lagomCassandraMaxBootWaitingTime in ThisBuild := 0.seconds
