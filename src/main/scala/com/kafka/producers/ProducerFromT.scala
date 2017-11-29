package com.kafka.producers

object ProducerFromT extends App {

  import java.util.Properties

  import org.apache.kafka.clients.producer._

  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")

  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  val TOPIC = "streams-plaintext-input"

  var flag = true
  println("Type something ('stop' to finish) : ")
  while (flag) {
    val input = scala.io.StdIn.readLine()
    val record = new ProducerRecord(TOPIC, "key", input)
    producer.send(record)
    if (input == "stop") flag = false
  }

  producer.close()
}
