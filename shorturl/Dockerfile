FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY . .

# Add this line to make the Maven Wrapper executable
RUN chmod +x ./mvnw

RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "target/*.jar"]