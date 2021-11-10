FROM openjdk:18-ea-jdk-oraclelinux8 as maven

RUN microdnf install maven git curl zip unzip -y

RUN echo ss && curl -s "https://get.sdkman.io" | bash
RUN source "/root/.sdkman/bin/sdkman-init.sh" && sdk install java 18.ea.2.lm-open < /dev/null

#WORKDIR /helidon-frm
#RUN git clone --single-branch --branch master https://github.com/danielkec/helidon.git
#RUN cd helidon && mvn -pl :helidon-common,:helidon-common-http,:helidon-webclient -am -T2 install -DskipTests

################## Build warp
WORKDIR /warp-frm
RUN echo 1 && git clone --single-branch --branch main https://<USER>:<TOKEN>@gitlab-odx.oracledx.com/j4c/warp.git
RUN source "/root/.sdkman/bin/sdkman-init.sh" && cd warp && mvn install -DskipTests

WORKDIR /warp
COPY src src
COPY pom.xml pom.xml
RUN mvn package -q
RUN ls -la ./target


FROM openjdk:18-jdk

RUN microdnf install maven git curl zip unzip -y

RUN curl -s "https://get.sdkman.io" | bash
RUN source "/root/.sdkman/bin/sdkman-init.sh" && sdk install java 18.ea.2.lm-open < /dev/null

WORKDIR /warp
COPY --from=maven /warp/target/libs libs
COPY --from=maven /warp/target/benchmark-1.0.jar app.jar

EXPOSE 8080

CMD source "/root/.sdkman/bin/sdkman-init.sh" && java -classpath app.jar io.warp.benchmark.Main
