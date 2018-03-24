import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

val akkaVersion = "2.5.4"

lazy val `akka-sample-crawl-ebay-scala` = project
  .in(file("."))
  .settings(multiJvmSettings: _*)
  .settings(
    organization := "com.typesafe.akka.samples",
    scalaVersion := "2.11.8",
    scalacOptions in Compile ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlog-reflective-calls", "-Xlint"),
    javacOptions in Compile ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),
    javaOptions in run ++= Seq("-Xms500m", "-Xmx1024m", "-Djava.library.path=./target/native"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-remote" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
      "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % "10.0.11",
      "com.typesafe.akka" %% "akka-stream" % "2.5.7",
      "com.typesafe.akka" %% "akka-http-experimental" % "2.4.2",
      "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.2",
      "com.typesafe.slick" %% "slick" % "2.1.0",
      "mysql" % "mysql-connector-java" % "5.1.35",
      "org.scalatest" %% "scalatest" % "3.0.1" % Test,
      "org.jsoup" % "jsoup" % "1.8.3",
      "net.minidev" % "json-smart" % "2.2.1",
      "org.mongodb" %% "casbah" % "3.1.1",
      "io.kamon" % "sigar-loader" % "1.6.6-rev002"),
    fork in run := true,
    mainClass in (Compile, run) := Some("sample.crawl.ebay.http.AkkaHttpMicroservice"),
    // disable parallel tests
    parallelExecution in Test := false,
    licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))
  )
  .configs (MultiJvm)
