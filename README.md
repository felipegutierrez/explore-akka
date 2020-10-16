
[![Build Status](https://api.travis-ci.org/felipegutierrez/explore-akka.svg?branch=master)](https://travis-ci.org/felipegutierrez/explore-akka)

Exploring [Akka 2.6.10](https://akka.io/) with Scala 2.12.7 and Java 8. The docker images are available at [docker hub](https://hub.docker.com/repository/docker/felipeogutierrez/explore-akka).

## Troubleshooting

```
sbt compile
sbt package
sbt run
sbt clean assembly
sbt docker:publishLocal
docker images
docker image rm <IMAGE_ID>
```

