# Utiliser une image de base Java avec Temurin JDK 21
FROM eclipse-temurin:21-jre as base

# Définir le répertoire de travail dans le conteneur
WORKDIR /opt/app

# Copier le fichier JAR de l'application dans le conteneur
COPY pokemon-api.jar .

# Définir la commande de démarrage
CMD ["java", "-jar", "pokemon-api.jar", "-p", "7000"]
