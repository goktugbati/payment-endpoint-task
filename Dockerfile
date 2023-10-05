FROM adoptopenjdk/openjdk8
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ADD . ./src/main/resources/static
ENTRYPOINT ["java","-jar","/app.jar"]