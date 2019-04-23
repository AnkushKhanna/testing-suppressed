package topology

import java.time.Duration

import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.kstream.Suppressed.BufferConfig
import org.apache.kafka.streams.kstream.{Suppressed, TimeWindows}
import org.apache.kafka.streams.scala.kstream.{Consumed, Grouped, Materialized, Produced}
import org.apache.kafka.streams.scala.{ByteArrayWindowStore, Serdes, StreamsBuilder}
import org.apache.kafka.streams.state.Stores

trait SuppressedTopology {

  def buildTopology(streamBuilder: StreamsBuilder, inputTopic: String, outputTopic: String): Topology = {

    implicit val consumed: Consumed[String, String] = Consumed.`with`(Serdes.String, Serdes.String)
    implicit val grouped: Grouped[String, String] = Grouped.`with`(Serdes.String, Serdes.String)
    implicit val materialized: Materialized[String, String, ByteArrayWindowStore] = Materialized.as(
      Stores.persistentWindowStore(
        "suppressed_testing3",
        Duration.ofSeconds(60),
        Duration.ofSeconds(40),
        false))(Serdes.String, Serdes.String)

    implicit val p = Produced.`with`(Serdes.String, Serdes.String)

    import org.apache.kafka.streams.scala.kstream.KTable
    val kt =
      streamBuilder.stream(inputTopic)
        .groupByKey
        .windowedBy(TimeWindows.of(Duration.ofSeconds(40)).grace(Duration.ofSeconds(20)))
        .aggregate("")((k, v, agg) => v+agg+" ")
        .inner
        .suppress(Suppressed.untilWindowCloses(BufferConfig.unbounded()gst))

    new KTable(kt)
      .toStream[String]((k, v) => k.key())
      .to(outputTopic)

    streamBuilder.stream(outputTopic)(Consumed.`with`(Serdes.String, Serdes.String))
      .foreach((k, v) => println(v))

    streamBuilder.build()
  }

}

object SuppressedTopology extends SuppressedTopology