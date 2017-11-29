package com.kafka.streams

import java.lang.Long
import java.util.Properties

import org.apache.kafka.common.serialization._
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.{ KStream, KStreamBuilder, KTable }

object WordCountStream extends App {

  val props = new Properties
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-wordcount")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
  props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)

  // setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded data
  // Note: To re-run the demo, you need to use the offset reset tool:
  // https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Streams+Application+Reset+Tool

  import scala.collection.JavaConverters._

  val builder = new KStreamBuilder
  val source: KStream[String, String] = builder.stream("streams-plaintext-input")
//  val counts: KTable[String, java.lang.Long] = source
//    .flatMapValues[String] { _.toLowerCase(Locale.getDefault).split(" ").toList.asJava }
//    .map[String, String] { (key, value) => new KeyValue(value, value) }
//    .groupByKey.count("Counts")
//
//  // need to override value serde to Long type
//  counts.to(Serdes.String, Serdes.Long, "streams-wordcount-output")
//
//  val streams = new KafkaStreams(builder, props)
//  streams.start()
//  // usually the stream application would be running forever,
//  // in this example we just let it run for some time and stop since the input data is finite.
//  Thread.sleep(20000L)
//  streams.close()
}
