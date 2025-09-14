FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY . .

RUN chmod +x ./mvnw

RUN ./mvnw clean package -DskipTests

# Change this line to use the exact JAR file name
CMD ["java", "-jar", "target/shorturl-0.0.1-SNAPSHOT.jar"]