FROM openjdk:22-slim-bullseye
WORKDIR /app
COPY /target/EinfachML-0.0.1.jar /app/einfachml.jar
CMD ["java", "-jar", "einfachml.jar"]

#CMD ["java", "-jar", "/app.jar"]

#FROM openjdk:21-jdk-slim
#WORKDIR ../../
#COPY --from=build /target/target/your-app.jar ./your-app.jar
#ENTRYPOINT ["java", "-jar", "your-app.jar"]