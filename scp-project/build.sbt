ThisBuild / scalaVersion     := "2.12.15"
ThisBuild / version          := "0.1"
val sparkVersion = "3.1.3"

lazy val root = (project in file("."))
  .settings(
    name := "scp-project",
    mainClass := Some("Main"),
    libraryDependencies ++= Seq(    // Spark dependencies
      "org.apache.spark" %% "spark-core" % sparkVersion,
      "org.apache.spark" %% "spark-sql" % sparkVersion,
      "org.apache.hadoop" % "hadoop-hdfs" % "2.5.2"
    )
  )