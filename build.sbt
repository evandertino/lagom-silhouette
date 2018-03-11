organization in ThisBuild := "io.metabookmarks.lagom"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"

bintrayOrganization in ThisBuild := Some("metabookmarks")

bintrayPackage in ThisBuild := "laogm-silhouette"

licenses in ThisBuild += ("Apache-2.0",
  url("http://www.apache.org/licenses/LICENSE-2.0"))


//lagomCassandraEnabled in ThisBuild := false
//lagomUnmanagedServices in ThisBuild := Map("cas_native" -> "http://localhost:9042")

val monocleVersion = "1.5.0" // 1.5.0-cats-M1 based on cats 1.0.0-MF

val monocle = Seq(
  "com.github.julien-truffaut" %%  "monocle-core"  % monocleVersion,
  "com.github.julien-truffaut" %%  "monocle-macro" % monocleVersion,
  "com.github.julien-truffaut" %%  "monocle-law"   % monocleVersion % "test"
)

val playJsonDerivedCodecs = "org.julienrf" %% "play-json-derived-codecs" % "4.0.0"
val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5" % Test
val cats = Seq("org.typelevel" %% "cats-core" % "1.0.1")

lazy val `lagom-silhouette` = (project in file("."))
  .settings(publish := {}
  )
  .aggregate(security, `session-api`, `session-impl`, `user-api`, `user-impl`, `lagom-silhouette-web`)

lazy val security = (project in file("security"))
//  .settings(commonSettings: _*)
  .settings(
  bintrayRepository := "releases",
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      lagomScaladslServer % Optional,
      scalaTest
    )
  )

lazy val `session-api` = (project in file("session-api"))
  .settings(
    bintrayRepository := "releases",
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      playJsonDerivedCodecs
    )
  ).dependsOn(security)

lazy val `session-impl` = (project in file("session-impl"))
  .enablePlugins(LagomScala)
  .settings(
    bintrayRepository := "releases",
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`session-api`)

lazy val `user-api` = (project in file("user-api"))
  .settings(
    bintrayRepository := "releases",
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      playJsonDerivedCodecs
    )
  ).dependsOn(security)

lazy val `user-impl` = (project in file("user-impl"))
  .enablePlugins(LagomScala)
  .settings(
    bintrayRepository := "releases",
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`user-api`)


lazy val `bookmark-api` = (project in file("bookmark-api"))
  .settings(
    bintrayRepository := "releases",
    libraryDependencies ++= Seq(
      lagomScaladslApi,
      playJsonDerivedCodecs
    )
  ).dependsOn(security)


val silhouetteVersion = "5.0.3"

lazy val `lagom-silhouette-web` = (project in file("lagom-silhouette-web"))
  .enablePlugins(PlayScala)
  .dependsOn(security,`session-api`, `user-api`)
  .settings(
    bintrayRepository := "releases",
    resolvers += "Atlasian" at "https://maven.atlassian.com/content/repositories/atlassian-public",
    libraryDependencies ++= cats ++ Seq(
      lagomScaladslServer,
      macwire,
      scalaTest,
      "com.mohiva" %% "play-silhouette" % silhouetteVersion,
      "com.mohiva" %% "play-silhouette-password-bcrypt" % silhouetteVersion,
      "com.mohiva" %% "play-silhouette-persistence" % silhouetteVersion,
      "com.mohiva" %% "play-silhouette-crypto-jca" % silhouetteVersion,
      jdbc,
      ehcache,
      openId,
      "net.codingwell" %% "scala-guice" % "4.1.1",
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
      "com.typesafe.play" %% "play-mailer" % "6.0.1",
      "org.webjars" %% "webjars-play" % "2.6.3",
      "com.typesafe.play" %% "play-slick" % "3.0.3",
      "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B4-RC2",

      "com.iheart" %% "ficus" % "1.4.3",

      "org.webjars" % "bootstrap" % "4.0.0",
      "org.ocpsoft.prettytime" % "prettytime" % "4.0.1.Final",

      "org.webjars" % "foundation" % "6.4.3",
      "org.webjars" % "foundation-icon-fonts" % "d596a3cfb3",
      "com.vmunier" %% "scalajs-scripts" % "1.1.1",
      "org.sangria-graphql" %% "sangria" % "1.4.0",
      "org.sangria-graphql" %% "sangria-play-json" % "1.0.4"
    ),
 //   EclipseKeys.preTasks := Seq(compile in Compile),
    plantUMLSource := baseDirectory.value / "diagrams",
  ).enablePlugins(PlantUMLPlugin, SbtWeb)

/*
val bindingVersion = "11.0.1"

lazy val `web-client` = (project in file("web-client"))
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.4",
      "com.thoughtworks.binding" %%% "dom" % bindingVersion,
      "com.thoughtworks.binding" %%% "futurebinding" % bindingVersion,
      "com.thoughtworks.binding" %%% "jspromisebinding" % bindingVersion,
      "fr.hmil" %%% "roshttp" % "2.1.0"
    ),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
  )
  .enablePlugins(ScalaJSPlugin).
  dependsOn(`web-shared-js`)

lazy val `web-shared` = (crossProject.crossType(CrossType.Pure) in file("web-shared"))
  .jsSettings(name := "web-shared-js")
  .jvmSettings(name := "web-shared-jvm")
.settings(
  libraryDependencies ++= Seq(
    "com.typesafe.play" %%% "play-json" % "2.6.7",
    "org.julienrf" %%% "play-json-derived-codecs" % "4.0.0"
  )
)

lazy val `web-shared-jvm` = `web-shared`.jvm
lazy val `web-shared-js` = `web-shared`.js

 */
