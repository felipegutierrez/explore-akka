package com.spark.streams

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.{SparkConf, TaskContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Durations, StreamingContext}
import org.apache.kafka.common.serialization.StringDeserializer

import scala.collection.mutable

object WordCountScalaSparkStream extends App {

//  val spark = SparkSession
//    .builder
//    .appName("Spark-Kafka-Integration")
//    .master("local")
//    .getOrCreate()
//
//  import spark.implicits._
//
//  val df = spark
//    .readStream
//    .format("kafka")
//    .option("kafka.bootstrap.servers", "localhost:9092")
//    .option("subscribe", "streams-plaintext-input")
//    .load()
//  df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
//    .as[(String, String)]
//
//  df.writeStream
//    .format("console")
//    .option("truncate","false")
//    .start()
//    .awaitTermination()



  val kafkaParam = Map[String, Object](
    "bootstrap.servers" -> "localhost:9092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "use_a_separate_group_id_for_each_stream",
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )

  val conf = new SparkConf().setMaster("local[2]").setAppName("WordCountSparkStream")
  // Read messages in batch of 5 seconds
  val sparkStreamingContext = new StreamingContext(conf, Durations.seconds(5))

  //Configure Spark to listen messages in topic test
  val topicList = List("streams-plaintext-input")

  // Read value of each message from Kafka and return it
  val messageStream = KafkaUtils.createDirectStream(sparkStreamingContext,
    LocationStrategies.PreferConsistent,
    ConsumerStrategies.Subscribe[String, String](topicList, kafkaParam))

  messageStream.map(record => (record.key, record.value))

  messageStream.foreachRDD { rdd =>
    val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
    rdd.foreachPartition { iter =>
      val o: OffsetRange = offsetRanges(TaskContext.get.partitionId)
      println(s"${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
    }
  }

  // Get the lines, split them into words, count the words and print
  val lines = messageStream.map(_.value)
  val words = lines.flatMap(_.split(" "))
  val wordCounts = words.map(x => (x, 1L)).reduceByKey(_ + _)
  wordCounts.print()

  sparkStreamingContext.start()
  sparkStreamingContext.awaitTermination()

  // "streams-wordcount-output"
}
