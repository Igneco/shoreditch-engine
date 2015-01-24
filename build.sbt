import scala.util.Try


name := "shoreditch-engine"

organization := "im.mange"

version := Try(sys.env("TRAVIS_BUILD_NUMBER")).map("0.0." + _).getOrElse("1.0-SNAPSHOT")

scalaVersion := "2.10.4"

crossScalaVersions := Seq("2.10.4", "2.11.0")

resolvers ++= Seq(
  "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.5",
  "net.liftweb" %% "lift-json" % "2.6",
  "com.github.nscala-time" %% "nscala-time" % "1.6.0",
  "io.shaka" %% "naive-http" % "51",
  "im.mange" %% "shoreditch-api" % "0.0.60",
  "org.scalatest" % "scalatest_2.10" % "2.1.4" % "test"
)

sonatypeSettings

publishTo <<= version { project_version ⇒
  val nexus = "https://oss.sonatype.org/"
  if (project_version.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

homepage := Some(url("https://github.com/alltonp/shoreditch-engine"))

licenses +=("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

credentials += Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", System.getenv("SONATYPE_USER"), System.getenv("SONATYPE_PASSWORD"))

pomExtra :=
    <scm>
      <url>git@github.com:alltonp/shoreditch-engine.git</url>
      <connection>scm:git:git@github.com:alltonp/shoreditch-engine.git</connection>
    </scm>
    <developers>
      <developer>
        <id>alltonp</id>
      </developer>
    </developers>
