package com.kafka.streams

import java.lang.Long
import java.util.Properties

import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig, Topology}

import org.apache.kafka.streams.kstream.{KStream, KTable}

import scala.collection.JavaConverters.asJavaIterableConverter

object WordCountScalaKafkaStream extends App {

  val props = new Properties
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-wordcount")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
  props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)

  val stringSerde: Serde[String] = Serdes.String()
  val longSerde: Serde[Long] = Serdes.Long()

  val builder = new StreamsBuilder()
  val textLines: KStream[String, String] = builder.stream("streams-plaintext-input")

  val topology: Topology = builder.build()

  println(topology.describe())

  // This code will work only for scalaVersion := "2.12.4"
  /**
  val wordCounts: KTable[String, Long] = textLines
    .flatMapValues { textLine =>
      println(textLine)
      println(topology.describe())
      textLine.toLowerCase.split("\\W+").toIterable.asJava
    }
    .groupBy((_, word) => word)
    // this is a stateful computation config to the topology
    .count("word-counts")

  wordCounts.to(stringSerde, longSerde, "streams-wordcount-output")
  */

  val streams = new KafkaStreams(topology, props)
  streams.start()

}
