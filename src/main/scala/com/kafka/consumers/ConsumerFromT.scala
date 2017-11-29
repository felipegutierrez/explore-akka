package com.kafka.consumers

import java.util

import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.collection.JavaConverters._

object ConsumerFromT extends App {

  import java.util.Properties

  val TOPIC = "streams-wordcount-output"
  // val TOPIC="streams-plaintext-input"

  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")

  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id", "consumer-t")

  val consumer = new KafkaConsumer[String, String](props)

  consumer.subscribe(util.Collections.singletonList(TOPIC))

  while (true) {
    val records = consumer.poll(100)
    for (record <- records.asScala) {
      println(record)
    }
  }
}
