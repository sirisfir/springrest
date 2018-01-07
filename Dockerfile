FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/SpringTraining-1.0.0-SNAPSHOT.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT sleep 5 && java $JAVA_OPTS -jar /app.jar