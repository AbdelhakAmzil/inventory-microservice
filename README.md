# 🏗️ Inventory Microservices — Spring Boot 3.4.5

Architecture microservices complète avec Spring Boot, Spring Cloud, Eureka, API Gateway (Netty), Circuit Breaker (Resilience4j) et OpenFeign.

---

## 📋 Table des matières

- [Architecture](#architecture)
- [Technologies](#technologies)
- [Services](#services)
- [Prérequis](#prérequis)
- [Installation & Démarrage](#installation--démarrage)
- [URLs de test](#urls-de-test)
- [Circuit Breaker](#circuit-breaker)
- [APIs externes](#apis-externes)
- [Structure du projet](#structure-du-projet)

---

## 🏛️ Architecture

```
                        ┌─────────────────────────────────────────┐
                        │           PRIVATE NETWORK               │
                        │                                         │
Client                  │  ┌──────────────┐   ┌───────────────┐  │
  │                     │  │   Customer   │   │   Inventory   │  │
  │  HTTP               │  │   Service    │   │   Service     │  │
  ▼                     │  │   :8081      │   │   :8082       │  │
┌─────────────┐         │  └──────┬───────┘   └───────┬───────┘  │
│   Gateway   │◄────────►         │                   │          │
│   :8888     │         │         └─────────┬─────────┘          │
│  (Netty)    │         │                   │ OpenFeign           │
└─────────────┘         │          ┌────────▼────────┐           │
                        │          │  Billing Service │           │
                        │          │     :8083        │           │
                        │          └─────────────────┘           │
                        │                                         │
                        │  ┌──────────────────────────────────┐  │
                        │  │    Discovery Service (Eureka)    │  │
                        │  │              :8761               │  │
                        │  └──────────────────────────────────┘  │
                        └─────────────────────────────────────────┘
```

---

## 🛠️ Technologies

| Technologie | Version | Rôle |
|---|---|---|
| Java | 17 (Corretto) | Langage principal |
| Spring Boot | 3.4.5 | Framework principal |
| Spring Cloud | 2024.0.1 | Outils microservices |
| Eureka | — | Service discovery |
| Spring Cloud Gateway | 4.2.1 | API Gateway (Netty/WebFlux) |
| Resilience4j | 2.2.0 | Circuit Breaker |
| OpenFeign | 4.2.1 | Client REST déclaratif |
| Spring Data REST | 4.4.5 | API REST automatique |
| H2 Database | 2.3.x | Base in-memory (dev) |
| Lombok | 1.18.x | Réduction boilerplate |
| Hibernate | 6.6.x | ORM |

> ⚠️ **Important** : Spring Boot 4.x est incompatible avec `spring-cloud-starter-gateway` (WebFlux/Netty). Utiliser Spring Boot **3.4.5** obligatoirement.

---

## 🔧 Services

### 1. Discovery Service `:8761`
Serveur Eureka — registre central de tous les microservices.

### 2. Customer Service `:8081`
Gestion des clients. Expose une API REST via Spring Data REST.
- `GET /customers` — liste des clients
- `GET /customers/{id}` — client par id

### 3. Inventory Service `:8082`
Gestion des produits. Expose une API REST via Spring Data REST.
- `GET /products` — liste des produits
- `GET /products/{id}` — produit par id

### 4. Gateway Service `:8888`
Point d'entrée unique. Routes vers tous les services + APIs externes + Circuit Breaker.

### 5. Billing Service `:8083`
Facturation. Agrège les données de customer-service et inventory-service via OpenFeign.
- `GET /bills` — liste des factures
- `GET /bills/full/{id}` — facture enrichie (client + produits)

---

## ✅ Prérequis

- Java 17+
- Maven 3.8+
- Git

---

## 🚀 Installation & Démarrage

### 1. Cloner le projet

```bash
git clone https://github.com/AbdelhakAmzil/inventory-microservice.git
cd inventory-microservice
```

### 2. Démarrer les services dans l'ordre

> ⚠️ **L'ordre de démarrage est obligatoire** — Eureka doit être UP avant les autres.

```bash
# Terminal 1 — Discovery Service (en premier)
cd discovery-service
mvn spring-boot:run

# Attendre que Eureka soit UP sur http://localhost:8761

# Terminal 2
cd customer-service
mvn spring-boot:run

# Terminal 3
cd inventory-service
mvn spring-boot:run

# Terminal 4
cd gateway-service
mvn spring-boot:run

# Terminal 5 — Billing Service (en dernier)
cd billing-service
mvn spring-boot:run
```

### 3. Vérifier que tous les services sont UP

Ouvrir : [http://localhost:8761](http://localhost:8761)

```
BILLING-SERVICE   UP (1) - 192.168.x.x:billing-service:8083
CUSTOMER-SERVICE  UP (1) - 192.168.x.x:customer-service:8081
GATEWAY-SERVICE   UP (1) - 192.168.x.x:gateway-service:8888
INVENTORY-SERVICE UP (1) - 192.168.x.x:inventory-service:8082
```

---

## 🌐 URLs de test

### Accès direct

```
http://localhost:8081/customers          # Liste clients
http://localhost:8081/customers/1        # Client par id
http://localhost:8082/products           # Liste produits
http://localhost:8082/products/1         # Produit par id
http://localhost:8083/bills              # Liste factures
http://localhost:8083/bills/full/1       # Facture enrichie
```

### Via Gateway — Routes statiques

```
http://localhost:8888/customers          # → customer-service
http://localhost:8888/products           # → inventory-service
http://localhost:8888/bills/full/1       # → billing-service
```

### Via Gateway — Routes dynamiques (Eureka)

```
http://localhost:8888/customer-service/customers
http://localhost:8888/inventory-service/products
http://localhost:8888/billing-service/bills
```

### APIs externes

```
# REST Countries (gratuit, sans clé)
http://localhost:8888/restcountries/name/morocco
http://localhost:8888/restcountries/all?fields=name,capital,flags
http://localhost:8888/restcountries/alpha/ma

# Aladhan — Horaires de prière (gratuit, sans clé)
http://localhost:8888/muslimsalat/timingsByCity?city=Cairo&country=Egypt&method=5
http://localhost:8888/muslimsalat/timingsByCity?city=Casablanca&country=Morocco&method=21
```

### Monitoring

```
http://localhost:8761                         # Eureka Dashboard
http://localhost:8888/actuator/health         # Santé + Circuit Breakers
http://localhost:8888/actuator/circuitbreakers # État des CB
http://localhost:8888/actuator/metrics        # Métriques
```

---

## ⚡ Circuit Breaker

Le Gateway utilise **Resilience4j** (remplaçant officiel de Hystrix) pour protéger les routes.

### Configuration

```yaml
resilience4j:
  circuitbreaker:
    instances:
      customerCB:
        sliding-window-size: 5
        failure-rate-threshold: 50      # Circuit ouvert si 50% d'échecs
        wait-duration-in-open-state: 10s
  timelimiter:
    instances:
      countriesCB:
        timeout-duration: 5s
```

### États du Circuit Breaker

```
CLOSED  →  (trop d'erreurs)  →  OPEN  →  (après 10s)  →  HALF_OPEN
  ▲                                                           │
  └──────────────── (succès) ─────────────────────────────────┘
```

### Fallbacks configurés

| Route | Fallback |
|---|---|
| `/customers/**` | "Customer service is unavailable." |
| `/products/**` | "Inventory service is unavailable." |
| `/bills/**` | "Billing service is unavailable." |
| `/restcountries/**` | "Countries service is unavailable." |
| `/muslimsalat/**` | "Muslim Salat service is unavailable." |

---

## 🌍 APIs externes

### REST Countries
- **URL** : `https://restcountries.com/v3.1/`
- **Clé** : Aucune
- **Note** : Le endpoint `/all` nécessite le paramètre `?fields=name,capital,...`

### Aladhan (Horaires de prière)
- **URL** : `https://api.aladhan.com/v1/`
- **Clé** : Aucune
- **Methods** : 5 = Égypte, 21 = Maroc

---

## 📁 Structure du projet

```
inventory-microservice/
├── discovery-service/
│   ├── src/main/java/com/abdel/discoveryservice/
│   │   └── DiscoveryServiceApplication.java
│   └── src/main/resources/application.yml
│
├── customer-service/
│   ├── src/main/java/com/abdel/customerservice/
│   │   ├── CustomerServiceApplication.java
│   │   ├── entities/Customer.java
│   │   ├── repositories/CustomerRepository.java
│   │   └── config/RestConfig.java
│   └── src/main/resources/application.yml
│
├── inventory-service/
│   ├── src/main/java/com/abdel/inventoryservice/
│   │   ├── InventoryServiceApplication.java
│   │   ├── entities/Product.java
│   │   ├── repositories/ProductRepository.java
│   │   └── config/RestConfig.java
│   └── src/main/resources/application.yml
│
├── gateway-service/
│   ├── src/main/java/com/abdel/gatewayservice/
│   │   ├── GatewayServiceApplication.java
│   │   ├── config/GatewayConfig.java
│   │   └── controller/FallbackController.java
│   └── src/main/resources/application.yml
│
├── billing-service/
│   ├── src/main/java/com/abdel/billingservice/
│   │   ├── BillingServiceApplication.java
│   │   ├── entities/
│   │   │   ├── Bill.java
│   │   │   └── ProductItem.java
│   │   ├── models/
│   │   │   ├── Customer.java
│   │   │   └── Product.java
│   │   ├── repositories/
│   │   │   ├── BillRepository.java
│   │   │   └── ProductItemRepository.java
│   │   ├── feign/
│   │   │   ├── CustomerServiceClient.java
│   │   │   └── InventoryServiceClient.java
│   │   └── controllers/
│   │       └── BillRestController.java
│   └── src/main/resources/application.yml
│
└── README.md
```

---

## 👤 Auteur

**Abdelhak Amzil**
- GitHub: [@AbdelhakAmzil](https://github.com/AbdelhakAmzil)
- LinkedIn: [Abdelhak Amzil](https://linkedin.com/in/abdelhak-amzil)
- Casablanca, Maroc

---

## 📄 Licence

Ce projet est open source — libre d'utilisation à des fins éducatives.
