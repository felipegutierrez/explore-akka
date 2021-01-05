
[![Build Status](https://api.travis-ci.org/felipegutierrez/explore-akka.svg?branch=master)](https://travis-ci.org/felipegutierrez/explore-akka)

Exploring [Akka 2.6.10](https://akka.io/) with Scala 2.12.7 and Java 8. The docker images are available at [docker hub](https://hub.docker.com/repository/docker/felipeogutierrez/explore-akka).

## In action
 - Compile the project: `sbt docker:publishLocal` and verify the image created `docker images`.
 - Run the docker image: `docker run -i felipeogutierrez/explore-akka:1.1` (don't forget the `-i` parameter to allow STDIN open!).

## Running in k8s

```
minikube start --cpus 4 --memory 8192
kubectl create namespace akka
kubectl apply -f k8s/.yml -n akka
```

## Troubleshooting

 - Release notes for [scalatest 3.2.0](https://www.scalatest.org/release_notes/3.2.0)
 - useful commands:
```
sbt compile
sbt test
sbt package
sbt run
sbt clean assembly
sbt docker:stage
tree target/docker/stage
sbt docker:publishLocal
docker images
docker image rm <IMAGE_ID>
```

