# 🛒 API REST Java - Projet TD 2026

**API REST Spring Boot** pour gérer un système de commandes e-commerce avec validation d'adresse via API externe.

---

## 📋 Technologies

| Technologie | Version | Usage |
|------------|---------|-------|
| **Java** | 21 (LTS) | Langage principal |
| **Spring Boot** | 3.5.6 | Framework web & DI |
| **Spring Data JPA** | (Hibernate) | ORM & persistence |
| **MySQL** | 8.0 | Base de données |
| **HttpClient** | Java 11+ | Appels HTTP/2 |
| **Swagger/OpenAPI** | 3.x | Documentation API |
| **Spring Security** | - | Authentification HTTP Basic |

---

## 🗄️ Modèle de données (Relations JPA)

```
┌─────────┐      ┌─────────┐      ┌────────┐      ┌───────────────┐      ┌─────────┐
│ Address │ 1──1 │ Account │ 1──N │ Orders │ 1──N │ OrdersDetails │ N──1 │ Product │
└─────────┘      └────┬────┘      └────────┘      └───────────────┘      └────┬────┘
                      │                                                         │
                      └───────────────────── 1──N Notice N──1 ─────────────────┘
```

### Entités

- **Address** : `street`, `city`, `postalCode`, `country`
- **Account** : `username`, `password`, `email`, `firstName`, `lastName`
- **Product** : `name`, `description`, `price`, `stockQuantity`
- **Orders** : `orderDate`, `totalAmount`, `status`
- **OrdersDetails** : `quantity`, `unitPrice`, `subtotal`
- **Notice** : `rating` (1-5), `comment`, `createdAt`

---

## ✅ Validation d'adresse (TD Principal)

### Objectif

Valider automatiquement les adresses lors de la création d'un compte en utilisant une **API de géocodage externe**.

### API de géocodage

- **API du labo RIOC** : `https://api-gouv.lab.rioc.fr/search` *(utilisée par défaut)*
- **API publique** : `https://api-adresse.data.gouv.fr/search` *(alternative)*
- **Documentation** : https://adresse.data.gouv.fr/api-doc/adresse

### Implémentation technique

**Service** : `AddressValidationService`

1. **HttpClient Java natif** (HTTP/2)
   ```java
   HttpClient httpClient = HttpClient.newBuilder()
       .version(HttpClient.Version.HTTP_2)
       .build();
   ```
   ⚠️ **Note** : Utilise HttpClient au lieu de RestTemplate car certaines APIs bloquent RestTemplate

2. **Streams Java** (requis par le TD)
   ```java
   return response.getFeatures().stream()
       .findFirst()
       .map(feature -> feature.getProperties().getScore())
       .filter(score -> score != null && score > 0.5)
       .isPresent();
   ```
   - Utilise `.stream()`, `.findFirst()`, `.map()`, `.filter()`, `.isPresent()`
   - Documentation : https://www.baeldung.com/java-streams

3. **Score de confiance** : Seuil minimal de **0.5** pour valider une adresse

### Fonctionnement

```
Client → POST /api/accounts → AccountService
    ↓
AddressValidationService.validateAddress(address)
    ↓
Construit requête: "8 Boulevard du Port 80000 Amiens"
    ↓
HttpClient → GET https://api-gouv.lab.rioc.fr/search?q=...&limit=1
    ↓
Réponse JSON : {"features": [{"properties": {"score": 0.97}}]}
    ↓
Stream: .findFirst().map().filter(score > 0.5)
    ↓
✅ Adresse valide → Création compte
❌ Adresse invalide → HTTP 400 "L'adresse fournie n'est pas valide"
```

---

## 🚀 Démarrage

### Prérequis

- Java 21+
- Docker & Docker Compose
- Maven (wrapper inclus)

### 1. Démarrer MySQL

```bash
cd database
docker compose up -d
```

### 2. Lancer l'application

```bash
./mvnw spring-boot:run
# OU
./restart.sh
```

### 3. Accéder à l'application

- **API** : http://localhost:8080
- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **OpenAPI JSON** : http://localhost:8080/v3/api-docs

---

## 🔐 Authentification HTTP Basic

| Utilisateur | Mot de passe | Rôles | Accès |
|-------------|--------------|-------|-------|
| `user` | `userpassword` | USER | `/api/products`, `/api/orders`, `/api/notices` |
| `admin` | `adminpassword` | ADMIN + USER | **Tous les endpoints** (dont `/api/accounts`) |

### Dans Swagger UI

1. Cliquer sur **Authorize** 🔓 (cadenas en haut à droite)
2. Entrer : `admin` / `adminpassword`
3. Cliquer sur **Authorize** puis **Close**
4. Le cadenas devient 🔒 = authentifié

### En ligne de commande (curl)

```bash
curl -u admin:adminpassword http://localhost:8080/api/accounts
```

---

## 📡 Endpoints API

### 👤 Accounts (ADMIN uniquement)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/accounts` | Créer un compte **avec validation d'adresse** |
| `GET` | `/api/accounts` | Liste tous les comptes |
| `GET` | `/api/accounts/{id}` | Détails d'un compte |
| `PUT` | `/api/accounts/{id}` | Modifier un compte |
| `DELETE` | `/api/accounts/{id}` | Supprimer un compte |

### 📦 Products (USER ou ADMIN)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/products` | Liste des produits |
| `GET` | `/api/products/{id}` | Détails d'un produit |
| `POST` | `/api/products` | Créer un produit |
| `PUT` | `/api/products/{id}` | Modifier un produit |
| `DELETE` | `/api/products/{id}` | Supprimer un produit |

### 🛒 Orders & Notices (USER ou ADMIN)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/orders` | Liste des commandes |
| `POST` | `/api/orders` | Créer une commande |
| `GET` | `/api/notices` | Liste des avis |
| `POST` | `/api/notices` | Créer un avis |

---

## 🧪 Tests - Validation d'adresse

### ✅ Test avec adresse VALIDE

```bash
curl -X POST http://localhost:8080/api/accounts \
  -u admin:adminpassword \
  -H "Content-Type: application/json" \
  -d '{
    "username": "jean_dupont",
    "firstName": "Jean",
    "lastName": "Dupont",
    "email": "jean.dupont@example.com",
    "password": "password123",
    "address": {
      "street": "8 Boulevard du Port",
      "city": "Amiens",
      "postalCode": "80000",
      "country": "France"
    }
  }'
```

**Réponse attendue** : HTTP 201 Created
```json
{
  "accountId": 1,
  "username": "jean_dupont",
  "firstName": "Jean",
  "lastName": "Dupont",
  "email": "jean.dupont@example.com",
  "address": {
    "addressId": 1,
    "street": "8 Boulevard du Port",
    "city": "Amiens",
    "postalCode": "80000",
    "country": "France"
  }
}
```

### ❌ Test avec adresse INVALIDE

```bash
curl -X POST http://localhost:8080/api/accounts \
  -u admin:adminpassword \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_ko",
    "firstName": "Test",
    "lastName": "Invalid",
    "email": "test@invalid.com",
    "password": "pass123",
    "address": {
      "street": "999 Rue Inexistante XXXX",
      "city": "Villebidon",
      "postalCode": "00000",
      "country": "France"
    }
  }'
```

**Réponse attendue** : HTTP 400 Bad Request
```
L'adresse fournie n'est pas valide ou n'existe pas
```

### Explication

- ✅ **Score > 0.5** → Adresse validée → HTTP 201 Created
- ❌ **Score ≤ 0.5** OU **Aucun résultat** → HTTP 400 Bad Request

---

## 📁 Structure du projet

```
src/main/java/com/letocart/java_apirest_2026/
│
├── config/
│   ├── OpenApiConfig.java         # Configuration Swagger/OpenAPI
│   └── SecurityConfig.java        # HTTP Basic + rôles USER/ADMIN
│
├── controller/                    # Contrôleurs REST (@RestController)
│   ├── AccountController.java     # POST, GET, PUT, DELETE /api/accounts
│   ├── ProductController.java     # CRUD /api/products
│   ├── OrdersController.java      # Gestion /api/orders
│   └── NoticeController.java      # Gestion /api/notices
│
├── model/                         # Entités JPA (@Entity)
│   ├── Account.java               # @OneToOne Address, @OneToMany Orders/Notices
│   ├── Address.java               # street, city, postalCode, country
│   ├── Product.java               # name, price, stockQuantity
│   ├── Orders.java                # @OneToMany OrdersDetails
│   ├── OrdersDetails.java         # @ManyToOne Orders, Product
│   └── Notice.java                # rating, comment, @ManyToOne Account/Product
│
├── repository/                    # Repositories JPA (Spring Data)
│   ├── AccountRepository.java     # extends JpaRepository<Account, Long>
│   ├── ProductRepository.java
│   ├── OrdersRepository.java
│   ├── OrdersDetailsRepository.java
│   └── NoticeRepository.java
│
├── service/                       # Logique métier (@Service)
│   ├── AccountService.java        # Création compte + validation adresse
│   ├── AddressValidationService.java  # ⭐ HttpClient + Streams
│   ├── ProductService.java
│   ├── OrdersService.java
│   └── NoticeService.java
│
├── dto/
│   └── AddressValidationResponse.java  # DTO pour JSON API géocodage
│
└── JavaApirest2026Application.java     # Main Spring Boot
```

---

## ⚙️ Configuration (`application.properties`)

```properties
# Base de données MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/java_api_db
spring.datasource.username=admin
spring.datasource.password=adminpass

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Swagger
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

---

## ✅ Checklist des consignes TD

| Consigne | Statut | Détails |
|----------|--------|---------|
| Relations JPA correctes | ✅ | Address(1-1)Account(1-N)Orders(1-N)OrdersDetails(N-1)Product + Notice |
| API de géocodage intégrée | ✅ | `https://api-gouv.lab.rioc.fr/search` |
| Utilisation des **Streams** | ✅ | `.stream().findFirst().map().filter().isPresent()` |
| Validation adresse (score > 0.5) | ✅ | Implémenté dans `AddressValidationService` |
| HTTP Basic Authentication | ✅ | user/admin avec rôles |
| Documentation OpenAPI | ✅ | Swagger UI accessible |
| Code propre et lisible | ✅ | Services séparés, nommage clair |

---

## 🔧 Commandes utiles

```bash
# Démarrer MySQL
cd database && docker compose up -d

# Lancer l'application
./mvnw spring-boot:run

# Compiler sans lancer
./mvnw clean compile

# Arrêter l'application
pkill -f 'spring-boot:run'

# Voir les logs
tail -f /tmp/app.log

# Tester l'API
curl -u admin:adminpassword http://localhost:8080/api/accounts
```

---

## 📚 Points clés du TD

### Relations JPA implémentées

| Relation | Type | Détails |
|----------|------|---------|
| Account ↔ Address | `@OneToOne` | Un compte a une adresse unique |
| Account → Orders | `@OneToMany` | Un compte peut avoir plusieurs commandes |
| Orders → OrdersDetails | `@OneToMany` | Une commande contient plusieurs lignes |
| OrdersDetails → Product | `@ManyToOne` | Chaque ligne référence un produit |
| Account → Notice | `@OneToMany` | Un compte peut laisser plusieurs avis |
| Product → Notice | `@OneToMany` | Un produit peut avoir plusieurs avis |

### Streams Java utilisés

```java
// Dans AddressValidationService.validateAddress()
return response.getFeatures().stream()      // Conversion List → Stream
    .findFirst()                            // Récupère 1er élément (Optional)
    .map(f -> f.getProperties().getScore()) // Transforme Feature → Score
    .filter(score -> score > 0.5)           // Garde si score > 0.5
    .isPresent();                           // Vérifie existence
```

### Sécurité (HTTP Basic)

- **STATELESS** : Chaque requête doit s'authentifier
- **Rôles** : USER (lecture), ADMIN (lecture + écriture)
- **Endpoints protégés** : `/api/accounts` réservé aux ADMIN

---

## 🔗 Ressources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Java Streams Tutorial](https://www.baeldung.com/java-streams)
- [API Adresse Data Gouv](https://adresse.data.gouv.fr/api-doc/adresse)
- [Springdoc OpenAPI](https://springdoc.org/)

---

## 👨‍💻 Auteur

**Projet TD 2026** - API REST Java avec validation d'adresse

---
