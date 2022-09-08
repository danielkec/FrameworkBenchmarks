#FROM openjdk:17.0.2-slim
FROM openjdk:17.0.2
WORKDIR /helidon
COPY se-jdbc/target/libs libs
COPY se-jdbc/target/benchmark-se.jar app.jar


#ENV PROXY_HOST=www-proxy-ams.nl.oracle.com
#ENV PROXY_PORT=80
#ENV ORACLE_PROXY=http://www-proxy-ams.nl.oracle.com:80
#ENV http_proxy=http://www-proxy-ams.nl.oracle.com:80
#ENV https_proxy=http://www-proxy-ams.nl.oracle.com:80
#ENV HTTP_PROXY=http://www-proxy-ams.nl.oracle.com:80
#ENV HTTPS_PROXY=http://www-proxy-ams.nl.oracle.com:80
#ENV no_proxy=localhost

RUN echo "proxy >>> ${http_proxy}"
#RUN microdnf install curl

EXPOSE 8080

CMD java -server \
    -XX:-UseBiasedLocking \
    -XX:+UseNUMA \
    -XX:+UseParallelGC \
    -Dio.netty.buffer.checkBounds=false \
    -Dio.netty.buffer.checkAccessible=false \
    -jar app.jar