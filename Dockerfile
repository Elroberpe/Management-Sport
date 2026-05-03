# ========================================
# Etapa 1: Build (Construcción)
# ========================================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
# Copiamos primero el pom.xml para aprovechar la caché de capas de Docker
COPY pom.xml .
COPY src ./src
# Compilamos el proyecto (omitiendo tests para que sea más rápido el deploy)
RUN mvn clean package -DskipTests

# ========================================
# Etapa 2: Run (Ejecución)
# ========================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copiamos el .jar generado en la etapa anterior
COPY --from=build /app/target/Management-Sport-0.0.1-SNAPSHOT.jar app.jar
# Exponemos el puerto (Render lo detecta automáticamente de todos modos)
EXPOSE 8080
# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]