FROM openjdk:17.0.2-slim
WORKDIR /helidon
COPY se-dbclient/target/libs libs
COPY se-dbclient/target/benchmark-se-dbclient.jar app.jar

#FROM openjdk:11.0.3-jdk-slim
#WORKDIR /helidon
#COPY --from=maven /helidon/target/libs libs
#COPY --from=maven /helidon/target/benchmark-se-dbclient.jar app.jar

EXPOSE 8080

CMD java -server \
    -XX:-UseBiasedLocking \
    -XX:+UseNUMA \
    -XX:+UseParallelGC \
    -Dio.netty.buffer.checkBounds=false \
    -Dio.netty.buffer.checkAccessible=false \
    -jar app.jar