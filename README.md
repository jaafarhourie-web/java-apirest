🛍️ Backend Java – API REST E‑commerce
Application Spring Boot exposant une API REST pour gérer un mini système de commande en ligne, avec vérification d’adresse via un service externe de géocodage lors de la création de compte.​​

🧰 Stack technique
Composant	Version	Rôle
Java	21 (LTS)	Langage principal de l’API
Spring Boot	3.5.6	Démarrage rapide, Web, DI
Spring Data JPA	Hibernate	Accès et mapping aux données
MySQL	8.0	Base relationnelle principale
HttpClient	Java 11+	Appels HTTP/2 vers l’API d’adresses
Swagger / OpenAPI	3.x	Documentation et test des endpoints
Spring Security	-	Authentification HTTP Basic
​​

🧱 Modèle métier (JPA)
Schéma logique des relations entre entités :​​

text
┌─────────┐      ┌─────────┐      ┌────────┐      ┌───────────────┐      ┌─────────┐
│ Address │ 1──1 │ Account │ 1──N │ Orders │ 1──N │ OrdersDetails │ N──1 │ Product │
└─────────┘      └────┬────┘      └────────┘      └───────────────┘      └────┬────┘
                      │                                                         │
                      └───────────────────── 1──N Notice N──1 ─────────────────┘
Entités principales
Address : street, city, postalCode, country​​

Account : username, password, email, firstName, lastName​​

Product : name, description, price, stockQuantity​​

Orders : orderDate, totalAmount, status​​

OrdersDetails : quantity, unitPrice, subtotal​​

Notice : rating (1–5), comment, createdAt​​

🎯 Validation d’adresse (fonctionnalité clé)
But fonctionnel
Lorsqu’un nouvel utilisateur crée un compte, son adresse est automatiquement contrôlée auprès d’une API de géocodage afin de refuser les adresses douteuses ou inexistantes.​​

Fournisseurs d’adresses
API RIOC (labo) : https://api-gouv.lab.rioc.fr/search (cible par défaut)​​

API publique : https://api-adresse.data.gouv.fr/search (fallback possible)​​

Référence : documentation officielle de l’API Adresse Data Gouv.​​

Implémentation
Service dédié : AddressValidationService.​​

Utilisation de HttpClient natif en HTTP/2 :​​

java
HttpClient httpClient = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)
    .build();
Ce choix évite certains blocages rencontrés avec RestTemplate sur des APIs externes.​​

Exploitation des Streams Java pour analyser la réponse :​​

java
return response.getFeatures().stream()
    .findFirst()
    .map(feature -> feature.getProperties().getScore())
    .filter(score -> score != null && score > 0.5)
    .isPresent();
Le flux permet de ne garder que le premier résultat suffisamment fiable selon le score fourni.​​

Seuil de confiance : une adresse est considérée comme valide si le score retourné est strictement supérieur à 0.5.​​

Vue d’ensemble du flux```
Client → POST /api/accounts → AccountService
↓
AddressValidationService.validateAddress(address)
↓
Concaténation de l’adresse ("8 Boulevard du Port 80000 Amiens")
↓
HttpClient → GET https://api-gouv.lab.rioc.fr/search?q=...&limit=1
↓
Réponse JSON : {"features": [{"properties": {"score": 0.97}}]}
↓
Stream: .findFirst().map().filter(score > 0.5)
↓
✅ Score OK → création du compte
❌ Score insuffisant → HTTP 400 avec message d’erreur

text

***

## 🚀 Mise en route du projet

### Pré-requis

- Java 21 ou plus[8][1]
- Docker + Docker Compose pour la base MySQL[9][1]
- Maven (wrapper fourni : `./mvnw`)[8][1]

### 1. Lancement de MySQL

```bash
cd database
docker compose up -d
2. Démarrer l’API
bash
./mvnw spring-boot:run
# ou
./restart.sh
3. Accéder aux interfaces
API REST : http://localhost:8080​​

Swagger UI : http://localhost:8080/swagger-ui.html​​

Spécification OpenAPI : http://localhost:8080/v3/api-docs​​

🔐 Sécurité – HTTP Basic
Utilisateur	Mot de passe	Rôles	Droits
user	userpassword	USER	lecture sur /api/products, /api/orders, /api/notices
admin	adminpassword	ADMIN + USER	accès à l’ensemble des routes, y compris /api/accounts
​​

Authentification via Swagger UI
Cliquer sur le bouton Authorize dans Swagger.​​

Saisir les identifiants (ex. admin / adminpassword).​​

Valider, fermer la fenêtre : l’icône de cadenas passe en mode authentifié.​​

Exemple avec curl
bash
curl -u admin:adminpassword http://localhost:8080/api/accounts
🌐 Endpoints REST
Comptes (ADMIN uniquement)
Méthode	URI	Description
POST	/api/accounts	Création de compte avec contrôle d’adresse
GET	/api/accounts	Liste complète des comptes
GET	/api/accounts/{id}	Détail d’un compte
PUT	/api/accounts/{id}	Mise à jour d’un compte
DELETE	/api/accounts/{id}	Suppression d’un compte
​​

Produits (USER / ADMIN)
Méthode	URI	Description
GET	/api/products	Récupère tous les produits
GET	/api/products/{id}	Détail d’un produit
POST	/api/products	Création d’un produit
PUT	/api/products/{id}	Modification d’un produit
DELETE	/api/products/{id}	Suppression d’un produit
​​

Commandes & Avis (USER / ADMIN)
Méthode	URI	Description
GET	/api/orders	Liste des commandes
POST	/api/orders	Création d’une nouvelle commande
GET	/api/notices	Récupère tous les avis
POST	/api/notices	Ajout d’un avis sur un produit
​​

🧪 Scénarios de test – Adresse
Cas d’une adresse acceptée
bash
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
Réponse attendue : HTTP 201 Created avec l’objet Account et l’Address persistés.​​

Cas d’une adresse rejetée
bash
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
Réponse attendue : HTTP 400 Bad Request avec un message indiquant que l’adresse ne peut pas être validée.​​

🗂️ Organisation du code
Arborescence simplifiée du module src/main/java :​​

text
com/letocart/java_apirest_2026/
│
├── config/
│   ├── OpenApiConfig.java           # Paramétrage Swagger/OpenAPI
│   └── SecurityConfig.java          # Définition des rôles et HTTP Basic
│
├── controller/
│   ├── AccountController.java       # /api/accounts
│   ├── ProductController.java       # /api/products
│   ├── OrdersController.java        # /api/orders
│   └── NoticeController.java        # /api/notices
│
├── model/
│   ├── Account.java
│   ├── Address.java
│   ├── Product.java
│   ├── Orders.java
│   ├── OrdersDetails.java
│   └── Notice.java
│
├── repository/
│   ├── AccountRepository.java
│   ├── ProductRepository.java
│   ├── OrdersRepository.java
│   ├── OrdersDetailsRepository.java
│   └── NoticeRepository.java
│
├── service/
│   ├── AccountService.java
│   ├── AddressValidationService.java
│   ├── ProductService.java
│   ├── OrdersService.java
│   └── NoticeService.java
│
├── dto/
│   └── AddressValidationResponse.java
│
└── JavaApirest2026Application.java  # Classe main Spring Boot
⚙️ Configuration principale
Extrait du application.properties :​​

text
# Connexion MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/java_api_db
spring.datasource.username=admin
spring.datasource.password=adminpass

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# OpenAPI / Swagger
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
✅ Rappel des exigences du TP
Élément	État	Commentaire
Mapping JPA complet	✅	Relations Account–Address–Orders–OrdersDetails–Product + Notice
Intégration d’une API de géocodage	✅	Utilisation de https://api-gouv.lab.rioc.fr/search
Usage de Streams Java	✅	Chaînage .stream().findFirst().map().filter().isPresent()
Seuil de validation par score	✅	Vérification score > 0.5 dans AddressValidationService
Authentification HTTP Basic	✅	Comptes user et admin avec rôles distincts
Documentation OpenAPI disponible	✅	Swagger UI exposé sur /swagger-ui.html
Architecture propre par couches	✅	Controllers / Services / Repositories bien isolés
​​

🔧 Commandes pratiques
bash
# Lancer MySQL via Docker
cd database && docker compose up -d

# Démarrer l’API
./mvnw spring-boot:run

# Compilation seule
./mvnw clean compile

# Arrêt (exemple simple)
pkill -f 'spring-boot:run'

# Logs applicatifs
tail -f /tmp/app.log

# Ping rapide de l’API comptes
curl -u admin:adminpassword http://localhost:8080/api/accounts
📝 Points techniques à retenir
Modèle de données JPA conçu autour des entités Account, Address, Orders, OrdersDetails, Product, Notice.​​

Intégration d’un service externe d’adresses, exploité via HttpClient et des Streams Java pour filtrer les résultats.​​

Sécurisation simple par HTTP Basic avec séparation des rôles USER / ADMIN et restrictions sur certains endpoints.​​

👤 Crédits
Projet pédagogique API REST Java – validation d’adresse pour travaux dirigés universitaires.
