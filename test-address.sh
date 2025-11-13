#!/bin/bash

echo "🔄 Recompilation..."
./mvnw compile -q

echo "🛑 Arrêt des processus existants..."
pkill -9 java 2>/dev/null
sleep 2

echo "🚀 Démarrage de l'application..."
nohup ./mvnw spring-boot:run > /tmp/app.log 2>&1 &
APP_PID=$!
echo "PID: $APP_PID"

echo "⏳ Attente du démarrage (15 secondes)..."
sleep 15

echo ""
echo "🧪 Test de création de compte avec adresse valide:"
echo ""

curl -s -X POST http://localhost:8080/api/accounts \
  -u admin:adminpassword \
  -H "Content-Type: application/json" \
  -d '{
    "firstName":"Sophie",
    "lastName":"Bernard",
    "email":"sophie.test@example.com",
    "password":"pass123",
    "address":{
      "street":"8 Boulevard du Port",
      "city":"Amiens",
      "postalCode":"80000",
      "country":"France"
    }
  }'

echo ""
echo ""
echo "📋 Logs de debug:"
tail -n 30 /tmp/app.log | grep -E "DEBUG|Erreur"
