# Concepts Spring Boot Avancés Utilisés

## 1. Spring Cloud Eureka - Service Discovery

### Description
Eureka est utilisé pour la découverte et l'enregistrement de services dans une architecture de microservices.

### Implémentation
- **Eureka Server** : Serveur central qui maintient le registre des services disponibles
- **Eureka Client** : Chaque microservice s'enregistre auprès d'Eureka et découvre les autres services

### Code
```java
@EnableEurekaServer  // Sur le serveur Eureka
@EnableEurekaClient  // Sur chaque microservice
```

### Avantages
- Découverte dynamique des services
- Load balancing automatique
- Health checks intégrés
- Pas besoin de configuration statique des URLs

## 2. WebClient - Communication Réactive

### Description
WebClient est utilisé pour la communication HTTP non-bloquante et réactive entre microservices.

### Implémentation
```java
@Bean
public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
}

// Utilisation dans RemoteActorRef
webClient.post()
    .uri("http://" + serviceName + "/api/actors/message")
    .bodyValue(message)
    .retrieve()
    .bodyToMono(Object.class)
    .timeout(Duration.ofMillis(timeoutMillis))
    .block();
```

### Avantages
- Non-bloquant (meilleure performance)
- Support des timeouts
- Gestion des erreurs avec Mono/Flux
- Compatible avec Spring WebFlux

## 3. Spring Boot Actuator - Monitoring

### Description
Actuator fournit des endpoints pour le monitoring et la gestion de l'application.

### Endpoints Utilisés
- `/actuator/health` : Vérification de l'état de santé
- Utilisé par RemoteActorRef pour vérifier la disponibilité des services

### Configuration
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info
```

## 4. Configuration Externalisée avec YAML

### Description
Utilisation de fichiers `application.yml` pour la configuration externalisée.

### Exemple
```yaml
server:
  port: 8081

spring:
  application:
    name: nba-player-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### Avantages
- Configuration par environnement
- Pas de hardcoding
- Facilite le déploiement

## 5. Spring Boot Auto-Configuration

### Description
Spring Boot configure automatiquement les beans nécessaires basés sur les dépendances présentes.

### Exemples
- `@SpringBootApplication` : Active l'auto-configuration
- Détection automatique des contrôleurs REST
- Configuration automatique de Jackson pour la sérialisation JSON

## 6. Dependency Injection avec @RequiredArgsConstructor (Lombok)

### Description
Utilisation de Lombok pour simplifier l'injection de dépendances.

### Code
```java
@RestController
@RequiredArgsConstructor
public class PlayerController {
    private final ActorSystem actorSystem;
    private final ActorRegistry actorRegistry;
}
```

### Avantages
- Code plus concis
- Moins de boilerplate
- Injection par constructeur (recommandé)

## 7. Spring WebFlux - Programmation Réactive

### Description
Utilisation de WebFlux pour la programmation réactive côté serveur.

### Composants
- `WebClient` : Client HTTP réactif
- `Mono` : Flux réactif pour un seul élément
- `Flux` : Flux réactif pour plusieurs éléments

## 8. Bean Configuration avec @Configuration

### Description
Configuration explicite des beans du framework.

### Code
```java
@Configuration
public class ActorFrameworkConfig {
    @Bean
    public ActorLogger actorLogger() {
        return new ActorLogger();
    }
    
    @Bean
    public ActorSystem actorSystem(ActorLogger actorLogger) {
        return new ActorSystem(actorLogger);
    }
}
```

### Avantages
- Contrôle total sur la création des beans
- Injection de dépendances explicite
- Facilite les tests

## 9. REST Controllers avec @RestController

### Description
Création d'APIs REST pour exposer les fonctionnalités des acteurs.

### Patterns Utilisés
- `@RequestMapping` : Mapping des URLs
- `@PathVariable` : Paramètres d'URL
- `@RequestBody` : Corps de requête JSON
- `ResponseEntity` : Contrôle de la réponse HTTP

## 10. Logging Structuré avec SLF4J et Logback

### Description
Système de logging avancé avec fichiers dédiés par acteur.

### Implémentation
- SLF4J pour l'interface de logging
- Logback pour l'implémentation
- Fichiers de logs séparés par acteur dans `logs/actors/`

## 11. Maven Multi-Module Project

### Description
Structure Maven avec modules séparés pour une meilleure organisation.

### Structure
```
actor-framework-project (parent)
├── actor-framework-core
├── eureka-server
├── nba-player-service
├── nba-team-service
└── nba-game-service
```

### Avantages
- Séparation des responsabilités
- Réutilisation du framework
- Compilation indépendante
- Gestion centralisée des dépendances

## 12. Profiles Spring Boot

### Description
Possibilité de définir des configurations par profil (dev, prod, test).

### Utilisation Potentielle
```yaml
spring:
  profiles:
    active: dev
```

## Résumé

Ces concepts avancés permettent de créer une architecture de microservices robuste, scalable et maintenable, avec :
- Découverte de services automatique
- Communication réactive non-bloquante
- Monitoring et observabilité
- Configuration externalisée
- Architecture modulaire

