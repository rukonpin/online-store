FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml ./pom.xml
COPY store-app/pom.xml ./store-app/pom.xml
COPY payment-service/pom.xml ./payment-service/pom.xml
COPY contracts ./contracts
COPY store-app/src ./store-app/src
COPY payment-service/src ./payment-service/src
RUN apk add --no-cache maven && \
    mvn -f pom.xml clean package -pl payment-service -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=builder /app/payment-service/target/*.jar app.jar
RUN chown spring:spring app.jar
USER spring
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]