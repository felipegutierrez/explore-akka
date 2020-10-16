FROM openjdk:8-jre-alpine

LABEL maintainer=felipeogutierrez
ADD target/scala-2.12/explore-akka-assembly-1.1.jar explore-akka.jar
COPY --from=builder target/scala-2.12/explore-akka-assembly-1.1.jar explore-akka.jar

ENTRYPOINT ["java", "-jar", "/explore-akka.jar"]
