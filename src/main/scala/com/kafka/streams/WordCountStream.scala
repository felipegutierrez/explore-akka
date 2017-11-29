package com.kafka.streams

import java.lang.Long
import java.util.Properties

import org.apache.kafka.common.serialization._
import org.apache.kafka.streams._
import org.apache.kafka.streams.kstream.{KStream, KStreamBuilder, KTable}

object WordCountStream extends App {

  val streamsConfiguration: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-stream-scala")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    p.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181")
    p.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    p.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    p
  }
  val stringSerde: Serde[String] = Serdes.String()
  val longSerde: Serde[Long] = Serdes.Long()

  val builder: KStreamBuilder = new KStreamBuilder()
  val textLines: KStream[String, String] = builder.stream("streams-plaintext-input")

  // Scala-Java interoperability: to convert `scala.collection.Iterable` to  `java.util.Iterable`
  // in `flatMapValues()` below.
  import scala.collection.JavaConverters._

  //  val wordCounts: KTable[String, Long] = textLines
  //    .flatMapValues(textLine => textLine.toLowerCase.split("\\W+").toIterable.asJava)
  //    .groupBy((_, word) => word)
  //    .count("streams-wordcount-output")
  //  val wordCounts: KTable[String, Long] = textLines.
  //    flatMapValues[String] { _.toLowerCase(Locale.getDefault).split(" ").toList.asJava }.
  //    map[String, String] { (key, value) => new KeyValue(value, value) }.
  //    groupByKey.count("Counts")

  //  val wordCounts: KStream[String, Long] = textLines
  //    //   .flatMapValues(value => value.toLowerCase.split("\\W+").toIterable.asJava)
  //    .map((key, word) => new KeyValue(word, word))
  //    .groupBy((_, word) => word)
  //    .count("Counts")
  //    .toStream()

  //  wordCounts.to(stringSerde, longSerde, "streams-wordcount-output")

  //  val uppercasedWithMapValues: KStream[String, String] = textLines.mapValues(_.toUpperCase())
  //  uppercasedWithMapValues.to("streams-wordcount-output")

  //  val streams: KafkaStreams = new KafkaStreams(builder, streamsConfiguration)
  //  streams.start()
}
