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

## 🐘 4. Deuxième Microservice (PHP)
Un service simple en PHP retournant un prénom.

* **Fichier :** `index.php`
* **Build :** `docker build -t nameservice .`
* **Run :** `docker run -p 8081:80 nameservice`
* **URL Docker Hub :** [https://hub.docker.com/r/aethras/nameservice](https://hub.docker.com/r/aethras/nameservice)

---

## 🏗 5. Orchestration avec Docker Compose

Cette étape rassemble les microservices Java et PHP dans une architecture multi-conteneur orchestrée par Docker Compose.

### Architecture

Le projet utilise une architecture micro-services avec :
- **Service Java** (RentalService) : écoute sur le port 8080
- **Service PHP** (name-service) : service interne accessible via le réseau Docker
- **Réseau bridge** : permet la communication entre les conteneurs

### Configuration de Docker Compose

Le fichier `docker-compose.yml` à la racine du projet définit 2 services et 1 réseau :

```yaml
version: '3.8'

services:
  # Le microservice Java
  java-app:
    build: ./RentalService
    ports:
      - "8080:8080"
    networks:
      - rental-network
    depends_on:
      - php-service

  # Le microservice PHP
  php-service:
    build: ./name-service
    networks:
      - rental-network

networks:
  rental-network:
    driver: bridge
```

### Modification du code Java

Pour permettre la communication inter-services, le contrôleur a été modifié pour envoyer une requête HTTP au service PHP.

#### Ajout de la méthode `getCustomer()` dans `BonjourController.java`

```java
@GetMapping("/customer/{name}")
public String getCustomer(@PathVariable String name) {
    RestTemplate restTemplate = new RestTemplate();
    
    // Le service Java appelle le service PHP via HTTP
    String responsePHP = restTemplate.getForObject(customerServiceUrl, String.class);
    
    return "Bonjour " + name + ", la réponse du service PHP est : " + responsePHP;
}
```

### Configuration du service PHP dans `application.properties`

L'adresse du service PHP a été spécifiée dans le fichier de configuration :

```properties
server.port=8080
spring.application.name=RentalService
customer.service.url=http://php-service/
```

**Note :** L'URL `http://php-service/` utilise le nom du service défini dans Docker Compose. La résolution de noms DNS fonctionne automatiquement grâce au réseau bridge.

### Lancement de Docker Compose

Pour démarrer l'infrastructure complète :

```bash
docker-compose up
```

Pour arrêter les services :

```bash
docker-compose down
```

### Test de l'application

Une fois les services lancés, testez la communication inter-services en accédant à :

```
http://localhost:8080/customer/Jean%20Dupont
```

**Réponse attendue :**
```
Bonjour Jean Dupont, la réponse du service PHP est : [réponse du service PHP]
```

Cette requête déclenche :
1. Le service Java reçoit la requête sur `http://localhost:8080/customer/Jean%20Dupont`
2. Le contrôleur `BonjourController` extrait le nom "Jean Dupont"
3. Le service Java envoie une requête HTTP au service PHP via `http://php-service/`
4. La réponse du service PHP est incluse dans la réponse finale renvoyée au client
