package com.spark.streams

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Durations, StreamingContext}

import scala.collection.mutable

object WordCountSparkStream extends App {

  val kafkaParam = new mutable.HashMap[String, String]()
  kafkaParam.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  kafkaParam.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  kafkaParam.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  kafkaParam.put(ConsumerConfig.GROUP_ID_CONFIG, "group1")
  kafkaParam.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
  kafkaParam.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")

  val conf = new SparkConf().setMaster("local[2]").setAppName("WordCountSparkStream")
  // Read messages in batch of 5 seconds
  val sparkStreamingContext = new StreamingContext(conf, Durations.seconds(5))

  //Configure Spark to listen messages in topic test
  val topicList = List("streams-plaintext-input")

  // Read value of each message from Kafka and return it
  val messageStream = KafkaUtils.createDirectStream(sparkStreamingContext,
    LocationStrategies.PreferConsistent,
    ConsumerStrategies.Subscribe[String, String](topicList, kafkaParam))
  val lines = messageStream.map(consumerRecord => consumerRecord.value().asInstanceOf[String])

  // Break every message into words and return list of words
  val words = lines.flatMap(_.split(" "))

  // Take every word and return Tuple with (word,1)
  val wordMap = words.map(word => (word, 1))

  // Count occurance of each word
  val wordCount = wordMap.reduceByKey((first, second) => first + second)

  //Print the word count
  wordCount.print()

  sparkStreamingContext.start()
  sparkStreamingContext.awaitTermination()

  // "streams-wordcount-output"
}
