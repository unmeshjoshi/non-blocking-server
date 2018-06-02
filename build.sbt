import sbt.project

name := "NonBlockingServer"

version := "0.1"

scalaVersion := "2.12.4"

val akkaVersion = "2.5.7"

lazy val `non-blocking-server` = project
  .in(file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    scalaVersion := "2.12.4",
    scalacOptions in Compile ++= Seq("-encoding", "UTF-8", "-target:jvm-1.8", "-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint"),
    javacOptions in Compile ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint:unchecked", "-Xlint:deprecation"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-remote" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
      "com.typesafe.akka" %% "akka-distributed-data" % akkaVersion,
      "org.tmatesoft.svnkit" % "svnkit" % "1.8.11",
      "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",
      "com.typesafe.akka" %% "akka-http" % "10.0.11",
      "com.typesafe.akka" %% "akka-http-testkit" % "10.0.9",
      "com.typesafe.play" % "play-json_2.12" % "2.6.5"
    )
  )

fork in run := true