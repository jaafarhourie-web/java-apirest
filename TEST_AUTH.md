# 🔐 Guide de test de l'authentification

## Tests avec CURL (ligne de commande)

### ✅ Test 1 : Sans authentification (doit échouer)
```bash
curl -v http://localhost:8080/api/products
# Résultat attendu : 401 Unauthorized
```

### ✅ Test 2 : Avec USER (doit réussir pour products)
```bash
curl -u user:userpassword http://localhost:8080/api/products
# Résultat attendu : 200 OK + liste des produits
```

### ❌ Test 3 : Avec USER sur /accounts (doit échouer)
```bash
curl -u user:userpassword http://localhost:8080/api/accounts
# Résultat attendu : 403 Forbidden
```

### ✅ Test 4 : Avec ADMIN sur /accounts (doit réussir)
```bash
curl -u admin:adminpassword http://localhost:8080/api/accounts
# Résultat attendu : 200 OK + liste des comptes
```

## Tests avec Swagger UI

### 📌 Procédure complète

1. **Ouvrir Swagger dans une fenêtre de navigation privée** pour éviter le cache
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

2. **TEST 1 : Sans authentification (cadenas déverrouillé 🔓)**
   - Tester `GET /api/products` directement
   - Résultat attendu : **401 Unauthorized** ✅
   - Cela prouve que la sécurité fonctionne !

3. **Cliquer sur le bouton "Authorize" (cadenas) en haut à droite**

4. **TEST 2 : Avec USER (cadenas verrouillé 🔒)**
   - **Username:** `user`
   - **Password:** `userpassword`
   - Cliquer sur **"Authorize"** puis **"Close"**
   - Le cadenas devient **verrouillé** (normal, Swagger a stocké les credentials)
   - Tester `GET /api/products` → Résultat attendu : **200 OK** ✅
   - Tester `GET /api/accounts` → Résultat attendu : **403 Forbidden** ✅

5. **TEST 3 : Avec ADMIN (cadenas verrouillé 🔒)**
   - Cliquer à nouveau sur "Authorize"
   - Cliquer sur **"Logout"** (important pour effacer les anciens credentials !)
   - **Username:** `admin`
   - **Password:** `adminpassword`
   - Cliquer sur **"Authorize"** puis **"Close"**
   - Le cadenas reste **verrouillé** (normal, Swagger a les nouveaux credentials)
   - Tester `GET /api/accounts` → Résultat attendu : **200 OK** ✅

### ⚠️ Points importants - COMPRENDRE LE CADENAS

| État Swagger UI | Signification | Comportement |
|----------------|---------------|--------------|
| 🔓 **Cadenas déverrouillé** | Aucun credential stocké | Requêtes envoyées **SANS** header `Authorization` → **401** |
| 🔒 **Cadenas verrouillé** | Credentials stockés (user ou admin) | Requêtes envoyées **AVEC** header `Authorization: Basic ...` → **200** ou **403** |

**Le cadenas verrouillé est NORMAL et SOUHAITABLE quand vous êtes authentifié !**

- ✅ **Comportement correct** : Cadenas déverrouillé → 401, Cadenas verrouillé → 200/403 selon les droits
- ❌ **Comportement anormal** : Cadenas verrouillé → 401 (ça voudrait dire que l'auth ne passe pas)
- **En mode STATELESS**, chaque requête doit envoyer le header `Authorization: Basic ...`
- **Le vrai test** est le code HTTP retourné (200/401/403), pas l'état du cadenas !
- **Pour changer d'utilisateur** : Toujours cliquer sur "Logout" avant d'entrer de nouveaux credentials

## Vérification des permissions

| Route | Anonyme | USER | ADMIN |
|-------|---------|------|-------|
| `/swagger-ui/**` | ✅ | ✅ | ✅ |
| `/api/products/**` | ❌ 401 | ✅ 200 | ✅ 200 |
| `/api/orders/**` | ❌ 401 | ✅ 200 | ✅ 200 |
| `/api/notices/**` | ❌ 401 | ✅ 200 | ✅ 200 |
| `/api/accounts/**` | ❌ 401 | ❌ 403 | ✅ 200 |

## Utilisateurs configurés

```java
// Utilisateur standard
user / userpassword (rôle: USER)

// Administrateur
admin / adminpassword (rôles: ADMIN, USER)
```
