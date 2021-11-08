FROM maven:3.6.1-jdk-11-slim as maven

################## TODO: Remove snapshot
WORKDIR /helidon-frm
RUN apt-get -y update
RUN apt-get -y install git
RUN git clone --single-branch --branch optimized-headers https://github.com/danielkec/helidon.git
RUN cd helidon && mvn -T2 install -DskipTests
##################################################

WORKDIR /helidon
COPY se-vertx-pg-client/src src
COPY se-vertx-pg-client/pom.xml pom.xml
RUN mvn package -q

FROM openjdk:11.0.3-jdk-slim
WORKDIR /helidon
COPY --from=maven /helidon/target/libs libs
COPY --from=maven /helidon/target/benchmark-se-vertx-pg-client.jar app.jar

EXPOSE 8080

CMD java -server \
    -XX:-UseBiasedLocking \
    -XX:+UseStringDeduplication \
    -XX:+UseNUMA \
    -XX:+AggressiveOpts \
    -XX:+UseParallelGC \
    -Dio.netty.buffer.checkBounds=false \
    -Dio.netty.buffer.checkAccessible=false \
    -jar app.jar
