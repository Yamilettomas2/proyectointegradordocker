# Usa una imagen oficial de Maven con JDK 17 como imagen base
FROM maven:3.8.5-openjdk-17 AS build

# Configura el directorio de trabajo en el contenedor
WORKDIR /app

# Copia el archivo POM y el código fuente del proyecto a la imagen
COPY pom.xml .
COPY src ./src

# Compila y empaqueta el proyecto
RUN mvn clean package -DskipTests

# Usa una imagen de OpenJDK para ejecutar la aplicación
FROM openjdk:17-jdk-slim

# Configura el directorio de trabajo en el contenedor
WORKDIR /app

# Copia el archivo JAR generado desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Expone el puerto en el que la aplicación escucha (ajusta si es necesario)
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]
