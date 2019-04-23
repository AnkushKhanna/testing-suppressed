package topology

import net.manub.embeddedkafka.schemaregistry.streams.EmbeddedKafkaStreamsAllInOne
import net.manub.embeddedkafka.schemaregistry.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.scala.{Serdes, StreamsBuilder}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}

import scala.collection.JavaConverters._

class SuppressedTopologyTest extends WordSpec with Matchers with EmbeddedKafkaStreamsAllInOne with MockitoSugar with BeforeAndAfterEach {
  private val inputTopic = "input-topic"
  private val outputTopic = "output-topic"
  implicit val config: EmbeddedKafkaConfig = EmbeddedKafkaConfig()

  "AggregatorTopology" should {
    "return aggregated value after suppressed" in {
      val streamBuilder = new StreamsBuilder()

      runStreams(
        Seq(inputTopic, outputTopic),
        SuppressedTopology.buildTopology(streamBuilder, inputTopic, outputTopic),
        Map(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG -> "30000")
      ) {

        implicit val stringSerdeS = Serdes.String.serializer()
        implicit val stringSerdeD = Serdes.String.deserializer()

        implicit val kS = Serdes.Long.serializer()
        implicit val kD = Serdes.Long.deserializer()

        publishToKafka(inputTopic, "1", "test1")
        publishToKafka(inputTopic, "1", "test2")


        withConsumer[String, String, Unit] { consumer =>
          Thread.sleep(1000)
          consumer.subscribe(List(outputTopic).asJava)

          while (true) {
            Thread.sleep(2000)
            consumerMessages(consumer)
          }
          consumer.close()
        }
      }

      def consumerMessages(consumer: KafkaConsumer[String, String]) = {
        val messages = consumer.poll(100)
        if (messages.isEmpty) println("Empty")
        messages.asScala.foreach(r => {
          println(r.key())
          println(r.value())
        })
      }
    }
  }
  sys.addShutdownHook(() => EmbeddedKafka.stop())
}
