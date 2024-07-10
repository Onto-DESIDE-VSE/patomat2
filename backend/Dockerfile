FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR patomat2

COPY pom.xml pom.xml

RUN mvn -B de.qaware.maven:go-offline-maven-plugin:resolve-dependencies

COPY src src

RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine AS runtime

COPY --from=build /patomat2/target/patomat2.jar /patomat2.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/patomat2.jar"]
