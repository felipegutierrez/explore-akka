package com.kafka.streams

import java.lang.Long
import java.util.Properties

import org.apache.kafka.common.serialization._
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.{KStream, KStreamBuilder, ValueMapper}

object WordCountStream extends App {

  val props = new Properties
  props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-wordcount")
  props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
  props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)

  val builder = new KStreamBuilder
  val source: KStream[String, String] = builder.stream("streams-plaintext-input")

  val helloStream: KStream[String, java.lang.Long] = source.mapValues(new ValueMapper[String, java.lang.Long] {
    override def apply(value: String): java.lang.Long =
      Long.valueOf(String.valueOf(value.length))
  })

  helloStream.to(Serdes.String, Serdes.Long, "streams-wordcount-output")

  val streams = new KafkaStreams(builder, props)
  streams.start()

  //  Thread.sleep(20000L)
  //  streams.close()
}
