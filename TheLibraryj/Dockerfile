FROM docker.io/library/maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /spring_server
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM docker.io/library/openjdk:21
WORKDIR /spring_server
COPY --from=build /spring_server/target/TheLibraryj-*.jar TheLibrary.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "TheLibrary.jar"]