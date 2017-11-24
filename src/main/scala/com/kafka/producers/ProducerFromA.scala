package com.kafka.producers

object ProducerFromA extends App {
 import java.util.Properties

 import org.apache.kafka.clients.producer._

 val  props = new Properties()
 props.put("bootstrap.servers", "localhost:9092")
  
 props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
 props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

 val producer = new KafkaProducer[String, String](props)
   
 val TOPIC="test"
 
 for(i<- 1 to 50){
  val record = new ProducerRecord(TOPIC, "key-a", s"hello A - $i")
  println(s"ProducerRecord($TOPIC, 'key-a', hello A - $i)")
  Thread.sleep(1000)
  producer.send(record)
 }
    
 val record = new ProducerRecord(TOPIC, "key-a", "the end of A - "+new java.util.Date)
 producer.send(record)

 producer.close()
}
