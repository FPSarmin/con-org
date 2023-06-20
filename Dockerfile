FROM maven:3 as BUILD_IMAGE
RUN rm -rf /etc/localtime
RUN ln -s /usr/share/zoneinfo/Europe/Moscow /etc/localtime
RUN echo "Europe/Moscow" > /etc/timezone
ENV APP_HOME=/root/dev/myapp/
RUN mkdir -p $APP_HOME/src/main/java
WORKDIR $APP_HOME
COPY src src
COPY pom.xml .
RUN mvn -B package -e -X --file pom.xml

FROM openjdk:17
WORKDIR /root/
COPY --from=BUILD_IMAGE /root/dev/myapp/target .
CMD java -jar conOrg.jar