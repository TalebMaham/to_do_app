# Étape 1 : Utiliser une image JDK pour la compilation
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copier les fichiers et compiler le projet
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Utiliser une image JRE pour exécuter l'application
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copier le fichier JAR généré
COPY --from=build /app/target/*.jar app.jar

# Exposer le port de l'application
EXPOSE 8080

# Démarrer l'application
CMD ["java", "-jar", "app.jar"]
