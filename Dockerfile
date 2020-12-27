FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD ./target/website-1.0-SNAPSHOT.jar-1.0 app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]