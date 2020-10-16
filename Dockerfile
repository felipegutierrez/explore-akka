FROM openjdk:8-jre-alpine

LABEL maintainer=felipeogutierrez
ADD target/scala-2.12/explore-akka-assembly-1.1.jar explore-akka.jar
COPY --from=builder target/scala-2.12/explore-akka-assembly-1.1.jar explore-akka.jar
#FROM explore-akka.jar AS felipeogutierrez/explore-akka:2.6.10-scala_2.12

ENTRYPOINT ["java", "-jar", "/explore-akka.jar"]
