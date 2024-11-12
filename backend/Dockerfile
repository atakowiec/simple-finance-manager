FROM openjdk:22-bookworm

ENV spring_profiles_active docker

COPY target/*.jar /app.jar

EXPOSE 5000

CMD ["java", "-jar", "/app.jar"]