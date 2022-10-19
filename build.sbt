val scala3Version = "3.2.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "typeclass3",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
  )

lazy val macroIntro = project
  .in(file("macroIntro"))
  .settings(
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % "0.14.2",
      "org.scalameta" %% "munit" % "0.7.29" % Test
    )
  )