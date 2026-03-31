FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/app.jar app.jar

ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

CMD ["java", "-Dfile.encoding=UTF-8", "-jar", "app.jar"]
