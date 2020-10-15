FROM openjdk:8-jre-alpine

ADD target/scala-2.12/explore-akka-assembly-1.1.jar explore-akka.jar

ENTRYPOINT ["java", "-jar", "/explore-akka.jar"]
