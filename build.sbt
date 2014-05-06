scalaVersion := "2.10.2"

name := "testutil"

organization := "nu.rinu"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "com.google.guava" % "guava" % "14.0.1",
  "nu.rinu" %% "sutil" % "0.0.9",
  "nu.rinu" %% "sdb" % "0.0.5",
  "org.json4s" %% "json4s-native" % "3.1.0",
  "org.dbunit" % "dbunit" % "2.5.0",
  "junit" % "junit" % "4.11" % "test",
  "org.apache.derby" % "derby" % "10.10.1.1" % "test",
  "org.scalatest" %% "scalatest" % "2.0" % "test",
  "org.specs2" %% "specs2" % "1.12.3" % "test",
  "mysql" % "mysql-connector-java" % "5.1.20" % "test",
  "org.projectlombok" % "lombok" % "0.12.0" % "test"
)
