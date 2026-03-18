FROM eclipse-temurin:17-jdk-alpine
LABEL authors="ksb09"
VOLUME /tmp
ADD target/api-gateway.jar api-gateway.jar
ENTRYPOINT ["java","-jar","/api-gateway.jar"]