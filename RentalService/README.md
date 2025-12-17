# Documentation du Projet RentalService

Ce projet consiste en la mise en place d'un micro-service Java Spring Boot, son build via Gradle, et sa conteneurisation avec Docker jusqu'à la publication sur le Docker Hub.

## 🛠 1. Test du programme sans Docker

### Prérequis
* **Java JDK 21** installé sur la machine.
* Vérification de la version : `java -version`.

### Compilation (Build)
Pour compiler le projet et générer l'artefact exécutable, utilisez le wrapper Gradle :
```bash
./gradlew build
```
L'archive JAR est générée dans le dossier : `build/libs/RentalService-0.0.1-SNAPSHOT.jar`.

### Lancement local
```bash
java -jar build/libs/RentalService-0.0.1-SNAPSHOT.jar
```
Vérification : Accédez à l'adresse http://localhost:8080/bonjour dans votre navigateur.

---

## 🐳 2. Conteneurisation avec Docker

### Configuration du Dockerfile
Le fichier Dockerfile a été placé à la racine du dossier RentalService avec la configuration suivante :

```dockerfile
FROM eclipse-temurin:21-jdk
VOLUME /tmp
EXPOSE 8080
ADD ./build/libs/RentalService-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```

### Création de l'image Docker
Pour construire l'image nommée rentalservice :

```bash
docker build -t rentalservice .
```

### Test du programme avec Docker
Pour lancer le conteneur en mappant le port 8080 :

```bash
docker run -p 8080:8080 rentalservice
```
Vérification : Le service est disponible sur le port 8080 de l'hôte : http://localhost:8080/bonjour.

---

## 🚀 3. Publication sur le Docker Hub
L'image a été poussée sur le registre public pour permettre un déploiement distant.

### Étapes de publication :

**Connexion au registre :**
```bash
docker login
```

**Tag de l'image :**
```bash
docker tag rentalservice aethras/rentalservice:1.0
```

**Push de l'image :**
```bash
docker push aethras/rentalservice:1.0
```

**Récupération de l'image :**
```bash
docker pull aethras/rentalservice:1.0
```
