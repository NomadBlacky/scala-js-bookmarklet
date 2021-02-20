import java.awt.Desktop
import java.io.PrintWriter
import java.net.URLEncoder
import scala.io.Source
import scala.util.Using

ThisBuild / scalaVersion := "2.13.3"
ThisBuild / organization := "dev.nomadblacky"
ThisBuild / organizationName := "NomadBlacky"

lazy val buildBookmarklet = taskKey[File]("Build the bookmarklet.")
lazy val openBookmarklet  = taskKey[Unit]("Open the bookmarklet in your browser.")

def getBundleJS(files: Seq[sbt.Attributed[File]]): File =
  files
    .find(attr => attr.data.name.endsWith("-bundle.js"))
    .map(attr => attr.data)
    .getOrElse(sys.error("bundle.js file is not found."))

lazy val root = (project in file("."))
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .settings(
    name := "scala-js-bookmarklet",
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "org.scala-js"                  %%% "scalajs-dom" % "1.1.0",
      "com.softwaremill.sttp.client3" %%% "core"        % "3.0.0",
      "org.scalatest"                 %%% "scalatest"   % "3.2.5" % Test
    ),
    buildBookmarklet := {
      (Compile / fullOptJS / webpack).result.value match {
        case Inc(cause) =>
          throw cause
        case Value(value) =>
          val f        = file("bookmarklet.js")
          val bundleJS = getBundleJS(value)
          Using.resources(Source.fromFile(bundleJS), new PrintWriter(f)) { (source, pw) =>
            val script = source.mkString
            val encoded = URLEncoder
              .encode(script, "UTF-8")
              .replaceAll("\\+", "%20")
              .replaceAll("%21", "!")
              .replaceAll("%27", "'")
              .replaceAll("%28", "(")
              .replaceAll("%29", ")")
              .replaceAll("%7E", "~")
            pw.println(s"javascript:$encoded")
          }
          f
      }
    },
    openBookmarklet := {
      buildBookmarklet.result.value match {
        case Inc(cause) =>
          throw cause
        case Value(value) =>
          val f = file("bookmarklet.html")
          Using.resources(Source.fromFile(value), new PrintWriter(f)) { (source, pw) =>
            val script = source.mkString
            pw.println(
              s"""<p><a href="${script}">${name.value}</a></p><p>Click this button to test the bookmarklet. And drag and drop to the bookmark bar to save the bookmarklet.</p>"""
            )
          }
          Desktop.getDesktop.open(f)
      }
    }
  )
