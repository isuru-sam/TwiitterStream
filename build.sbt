name := """twitter-stream"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

libraryDependencies += "com.ning" % "async-http-client" % "1.9.29"

resolvers += "Typesafe private" at
"https://private-repo.typesafe.com/typesafe/maven-releases"

libraryDependencies +=
"com.typesafe.play.extras" %% "iteratees-extras" % "1.5.0"