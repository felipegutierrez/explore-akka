FROM openjdk:8-jre-alpine

LABEL maintainer=felipeogutierrez

ADD target/scala-2.12/explore-akka_2.12-1.1.jar /explore-akka.jar
COPY --from=builder target/scala-2.12/explore-akka_2.12-1.1.jar /explore-akka.jar
#ENTRYPOINT ["/usr/bin/java", "-jar", "/explore-akka.jar"]
