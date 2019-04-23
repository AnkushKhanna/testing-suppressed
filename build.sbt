
name := "testSuppressed"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++=Seq(
  "org.apache.kafka" %% "kafka-streams-scala" % "2.2.0",

  "com.github.pureconfig" %% "pureconfig" % "0.10.2",
  "com.typesafe.akka" %% "akka-actor" % "2.5.22",

  "org.apache.avro" % "avro" % "1.8.2",
  "io.confluent" % "kafka-connect-avro-converter" % "5.2.0",
  "io.confluent" % "kafka-streams-avro-serde" % "5.2.0",

  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "io.scalaland" %% "chimney" % "0.3.1",

  "io.circe" %% "circe-core" % "0.10.0",
  "io.circe" %% "circe-generic" % "0.10.0",
  "io.circe" %% "circe-parser" % "0.10.0",

  "com.madewithtea" %% "mockedstreams" % "3.3.0" % Test,
  "org.mockito" % "mockito-all" % "1.8.4" % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,

  "io.github.embeddedkafka" %% "embedded-kafka" % "2.2.0" % Test,
  "io.github.embeddedkafka" %% "embedded-kafka-streams" % "2.2.0" % Test,
  "io.github.embeddedkafka" %% "embedded-kafka-schema-registry" % "5.2.1" % Test,
)

resolvers += "confluent" at "https://packages.confluent.io/maven/"

