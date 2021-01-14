ThisBuild / scalaVersion := "2.13.3"
ThisBuild / organization := "dev.nomadblacky"
ThisBuild / organizationName := "NomadBlacky"

lazy val buildBookmarklet = taskKey[File]("Build the bookmarklet.")

lazy val root = (project in file("."))
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .settings(
    name := "scala-js-bookmarklet",
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "org.scala-js"                  %%% "scalajs-dom" % "1.1.0",
      "com.softwaremill.sttp.client3" %%% "core"        % "3.0.0",
      "org.scalatest"                 %%% "scalatest"   % "3.2.3" % Test
    )
  )
