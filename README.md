
[![Build Status](https://travis-ci.com/felipegutierrez/explore-akka.svg?branch=master)](https://travis-ci.com/felipegutierrez/explore-akka)
[![Coverage Status](https://coveralls.io/repos/github/felipegutierrez/explore-akka/badge.svg?branch=master)](https://coveralls.io/github/felipegutierrez/explore-akka?branch=master)
![GitHub issues](https://img.shields.io/github/issues-raw/felipegutierrez/explore-akka?style=plastic)
![GitHub closed issues](https://img.shields.io/github/issues-closed-raw/felipegutierrez/explore-akka?style=plastic)


Exploring [Akka 2.6.10](https://akka.io/) with Scala 2.12.7 and Java 8. The docker images are available at [docker hub](https://hub.docker.com/repository/docker/felipeogutierrez/explore-akka).

## In action

Compile the project and verify the image created:
```
sbt docker:stage
sbt docker:publishLocal
docker images
```
Run the docker image: `docker run -i felipeogutierrez/explore-akka:1.1` (don't forget the `-i` parameter to allow STDIN open!).

## Running in k8s

```
minikube start --cpus 4 --memory 8192
kubectl create namespace akka

// This application is an Akka-http hello world
kubectl apply -f k8s/basics/hello-pod.yaml -n akka
kubectl port-forward hello 8001
```
This application is an Akka-cluster. 1 - Deploy the seed nodes:
```
kubectl apply -f k8s/cluster/akka-cluster-seed.yaml
```
Deploy the worker nodes:
```
kubectl apply -f k8s/cluster/akka-cluster-workers.yaml
```
Scale:
```
kubectl scale statefulset akka-seed --replicas=3

```
Check the logs:
```
kubectl get pods -A
kubectl logs -f akka-seed-0 | akka-seed-1 | akka-seed-2
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
sbt protobuf:protobufGenerate
sbt docker:stage
tree target/docker/stage
sbt docker:publishLocal
docker images
docker image rm <IMAGE_ID>
```

