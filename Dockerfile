FROM eclipse-temurin:17-jdk-alpine
LABEL authors="ksb09"
VOLUME /tmp
# target/*.jar "destination name"- according to your choice
ADD target/api-gateway.jar api-gateway.jar
# /api-gateway.jar, points to the 'destination' name
ENTRYPOINT ["java","-jar","/api-gateway.jar"]


# *NOTE: Destination name in ADD must match the filename in your ENTRYPOINT