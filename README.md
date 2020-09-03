
[![Build Status](https://api.travis-ci.org/felipegutierrez/scala-akka-stream-kafka.svg?branch=master)](https://travis-ci.org/felipegutierrez/scala-akka-stream-kafka)

# scala-akka-stream-kafka

This project is a use case to work mainly with [Kafka](https://kafka.apache.org/). The idea is to have two or more producers and two or more consumer as well. Kafka is going to manage the messages. Between the consumers and producers I will process the data and extract analytics from it.



Follow the steps bellow to execute:

# 1 - Download [Kafka](https://kafka.apache.org/downloads)

Unzip the file and go to the directory. Change the number of partitions to something greater than 1 on the `config/server.properties` file:
```
num.partitions=8
```

# 2 - Start the Zookeeper server (first console):
```
$ bin/zookeeper-server-start.sh config/zookeeper.properties
```

# 3 - Start the Kafka server (second console):
```
bin/kafka-server-start.sh config/server.properties
```

# 4 - Start the Consumer (third console):
```
$ sbt
sbt:scala-akka-stream-kafka> run

Multiple main classes detected, select one to run:

 [1] Hi
 [2] com.kafka.Consumer
 [3] com.kafka.Producer

Enter number: 2
```

# 5 - Start the Producer (fourth console):
```
$ sbt
sbt:scala-akka-stream-kafka> run

Multiple main classes detected, select one to run:

 [1] Hi
 [2] com.kafka.Consumer
 [3] com.kafka.Producer

Enter number: 3
```

# 6 - Go to the third console and watch your messages.





