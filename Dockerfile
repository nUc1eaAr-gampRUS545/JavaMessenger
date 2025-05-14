FROM openjdk:17-jdk
COPY JavaMessenger.jar JavaMessenger.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "JavaMessenger.jar"]