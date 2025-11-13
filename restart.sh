#!/bin/bash

echo "🛑 Arrêt de toutes les instances..."
# Tuer tous les processus Spring Boot
pkill -9 -f "spring-boot:run" 2>/dev/null
pkill -9 -f "JavaApirest2026Application" 2>/dev/null

# Libérer le port 8080
lsof -ti:8080 2>/dev/null | xargs kill -9 2>/dev/null

# Attendre que tout se termine
sleep 2

echo ""
echo "🧹 Nettoyage et compilation..."
cd /home/thomas/Documents/Projects/java-apirest
./mvnw clean compile

echo ""
echo "🚀 Démarrage de l'application en arrière-plan..."
nohup ./mvnw spring-boot:run > app.log 2>&1 &

echo ""
echo "⏳ Attente du démarrage (15 secondes)..."
sleep 15

echo ""
echo "📊 Vérification de l'état:"
echo ""

# Vérifier Swagger UI
SWAGGER_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/swagger-ui/index.html)
echo "✓ Swagger UI: http://localhost:8080/swagger-ui/index.html → Status $SWAGGER_STATUS"

# Tests d'authentification
echo ""
echo "🔐 Tests d'authentification:"
TEST1=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/products)
echo "  1. Sans auth → $TEST1 (attendu: 401)"

TEST2=$(curl -s -o /dev/null -w "%{http_code}" -u user:userpassword http://localhost:8080/api/products)
echo "  2. USER sur /api/products → $TEST2 (attendu: 200)"

TEST3=$(curl -s -o /dev/null -w "%{http_code}" -u user:userpassword http://localhost:8080/api/accounts)
echo "  3. USER sur /api/accounts → $TEST3 (attendu: 403)"

TEST4=$(curl -s -o /dev/null -w "%{http_code}" -u admin:adminpassword http://localhost:8080/api/accounts)
echo "  4. ADMIN sur /api/accounts → $TEST4 (attendu: 200)"

echo ""
echo "✅ Application redémarrée avec succès!"
echo ""
echo "📝 Logs en temps réel: tail -f app.log"
echo "🛑 Arrêter l'app: pkill -f 'spring-boot:run'"
