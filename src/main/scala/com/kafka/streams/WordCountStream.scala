package com.kafka.streams

import java.lang.Long
import java.util.Properties

import org.apache.kafka.common.serialization._
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.{KStream, KTable}

import scala.collection.JavaConverters.asJavaIterableConverter

object WordCountStream extends App {

  val props = new Properties
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-wordcount")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
  props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)

  val stringSerde: Serde[String] = Serdes.String()
  val longSerde: Serde[Long] = Serdes.Long()

  val builder = new StreamsBuilder()
  val textLines: KStream[String, String] = builder.stream("streams-plaintext-input")

  val wordCounts: KTable[String, Long] = textLines
    .flatMapValues { textLine =>
      println(textLine)
      textLine.toLowerCase.split("\\W+").toIterable.asJava
    }
    .groupBy((_, word) => word)
    .count("word-counts")

  wordCounts.to(stringSerde, longSerde, "streams-wordcount-output")

  val streams = new KafkaStreams(builder.build(), props)
  streams.start()

}
