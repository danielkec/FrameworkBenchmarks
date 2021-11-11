FROM oraclelinux:8-slim

RUN microdnf install maven gzip tar binutils freetype fontconfig curl zip unzip -y

RUN curl -s "https://get.sdkman.io" | bash
RUN source "/root/.sdkman/bin/sdkman-init.sh" && sdk install java 18.ea.2.lm-open < /dev/null

# Pre-built warp is needed!
WORKDIR /warp
COPY target/libs libs
COPY target/benchmark-1.0.jar app.jar

EXPOSE 8080

CMD source "/root/.sdkman/bin/sdkman-init.sh" && java -classpath app.jar io.warp.benchmark.Main
