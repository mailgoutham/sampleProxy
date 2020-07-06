FROM openjdk:8

COPY target/sample-proxy-0.0.1-SNAPSHOT.jar /usr/app

WORKDIR /usr/local/bin

CMD ["java", "-jar", "sample-proxy-0.0.1-SNAPSHOT.jar"]

EXPOSE 8081
