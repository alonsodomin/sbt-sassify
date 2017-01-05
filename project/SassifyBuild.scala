import bintray.BintrayPlugin.autoImport._
import com.typesafe.sbt.GitVersioning
import com.typesafe.sbt.SbtGit.git
import de.heikoseeberger.sbtheader.AutomateHeaderPlugin
import de.heikoseeberger.sbtheader.license.Apache2_0
import de.heikoseeberger.sbtheader.HeaderKey._
import sbt.Keys._
import sbt._
import java.time.LocalDate

object SassifyBuild extends Build {

  val compilerSettings = Seq (
    scalaVersion := "2.10.6",
    sourcesInBase := false,
    crossPaths := false,
    scalacOptions ++= Seq(
      "-unchecked",
      "-Xlint",
      "-deprecation",
      "-Xfatal-warnings",
      "-feature",
      "-encoding",
      "UTF-8"
    )
  )

  val bintraySettings = Seq(
    bintrayOrganization in bintray := None,
    bintrayPackageLabels := Seq("sbt", "sbt-plugin", "sbt-sassify"),
    bintrayRepository := "sbt-plugins",
    bintrayReleaseOnPublish in ThisBuild := false,
    publishMavenStyle := false,
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    publish <<= publish dependsOn (test in Test)
  )

  val copyrightSettings =
    headers := Map(
      "scala" -> Apache2_0(LocalDate.now().getYear.toString, "Han van Venrooij"),
      "java" -> Apache2_0(LocalDate.now().getYear.toString, "Han van Venrooij")
    )

  lazy val testScalastyle = taskKey[Unit]("testScalastyle")
  val scalaStyleSettings = Seq(
    testScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value,
    org.scalastyle.sbt.ScalastylePlugin.scalastyleFailOnError := true,
    test <<= test in Test dependsOn testScalastyle
  )

  val scriptedSettings = ScriptedPlugin.scriptedSettings ++ Seq(
    ScriptedPlugin.scriptedBufferLog := false,
    ScriptedPlugin.scriptedLaunchOpts <+= version { "-Dplugin.version=" + _ }
  )

  // File copyright headers
  lazy val sbtSassify = project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .enablePlugins(GitVersioning)
    .settings(compilerSettings)
    .settings(scriptedSettings)
    .settings(bintraySettings)
    .settings(copyrightSettings)
    .settings(scalaStyleSettings)
    .settings(git.gitUncommittedChanges := false)
}