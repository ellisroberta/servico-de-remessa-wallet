FROM openjdk:17-jdk-slim
VOLUME /tmp
ARG JAR_FILE=target/servico-de-remessa-wallet-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]