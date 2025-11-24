#!/bin/bash

# =====================================================
# Script de Prueba - Casos de Uso
# Banking System - NTT Data Technical Test
# =====================================================

set -e

BASE_URL_CUSTOMER="http://localhost:8080"
BASE_URL_ACCOUNT="http://localhost:8081"

echo "=================================================="
echo "   CASOS DE USO - SISTEMA BANCARIO NTT DATA"
echo "=================================================="
echo ""

# =====================================================
# CASO 1: Creación de Usuarios
# =====================================================
echo "CASO 1: Creación de Usuarios"
echo "=============================="

# 1.1 Jose Lema
echo "→ Creando: Jose Lema..."
JOSE_RESPONSE=$(curl -s -X POST $BASE_URL_CUSTOMER/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jose Lema",
    "gender": "MALE",
    "identification": "1234567890",
    "address": "Otavalo sn y principal",
    "phone": "098254785",
    "password": "1234",
    "status": true
  }')
JOSE_ID=$(echo $JOSE_RESPONSE | jq -r '.customerId')
echo "  ✓ Jose Lema creado: $JOSE_ID"

# 1.2 Marianela Montalvo
echo "→ Creando: Marianela Montalvo..."
MARIANELA_RESPONSE=$(curl -s -X POST $BASE_URL_CUSTOMER/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Marianela Montalvo",
    "gender": "FEMALE",
    "identification": "0987654321",
    "address": "Amazonas y NNUU",
    "phone": "097548965",
    "password": "5678",
    "status": true
  }')
MARIANELA_ID=$(echo $MARIANELA_RESPONSE | jq -r '.customerId')
echo "  ✓ Marianela Montalvo creada: $MARIANELA_ID"

# 1.3 Juan Osorio
echo "→ Creando: Juan Osorio..."
JUAN_RESPONSE=$(curl -s -X POST $BASE_URL_CUSTOMER/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Osorio",
    "gender": "MALE",
    "identification": "1122334455",
    "address": "13 junio y Equinoccial",
    "phone": "098874587",
    "password": "1245",
    "status": true
  }')
JUAN_ID=$(echo $JUAN_RESPONSE | jq -r '.customerId')
echo "  ✓ Juan Osorio creado: $JUAN_ID"

echo ""
echo "Esperando sincronización Kafka (5 segundos)..."
sleep 5

# =====================================================
# CASO 2: Creación de Cuentas de Usuario
# =====================================================
echo ""
echo "CASO 2: Creación de Cuentas de Usuario"
echo "========================================"

# 2.1 Cuenta Ahorro Jose - 478758
echo "→ Creando cuenta AHORRO 478758 para Jose Lema (saldo inicial: 2000)..."
CUENTA1=$(curl -s -X POST $BASE_URL_ACCOUNT/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d "{
    \"accountNumber\": \"478758\",
    \"accountType\": \"SAVING\",
    \"initialBalance\": 2000.00,
    \"status\": true,
    \"customerId\": \"$JOSE_ID\"
  }")
CUENTA1_ID=$(echo $CUENTA1 | jq -r '.accountId')
echo "  ✓ Cuenta 478758 creada: $CUENTA1_ID"

# 2.2 Cuenta Corriente Marianela - 225487
echo "→ Creando cuenta CORRIENTE 225487 para Marianela Montalvo (saldo inicial: 100)..."
CUENTA2=$(curl -s -X POST $BASE_URL_ACCOUNT/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d "{
    \"accountNumber\": \"225487\",
    \"accountType\": \"CHECKING\",
    \"initialBalance\": 100.00,
    \"status\": true,
    \"customerId\": \"$MARIANELA_ID\"
  }")
CUENTA2_ID=$(echo $CUENTA2 | jq -r '.accountId')
echo "  ✓ Cuenta 225487 creada: $CUENTA2_ID"

# 2.3 Cuenta Ahorro Juan - 495878
echo "→ Creando cuenta AHORRO 495878 para Juan Osorio (saldo inicial: 0)..."
CUENTA3=$(curl -s -X POST $BASE_URL_ACCOUNT/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d "{
    \"accountNumber\": \"495878\",
    \"accountType\": \"SAVING\",
    \"initialBalance\": 0.00,
    \"status\": true,
    \"customerId\": \"$JUAN_ID\"
  }")
CUENTA3_ID=$(echo $CUENTA3 | jq -r '.accountId')
echo "  ✓ Cuenta 495878 creada: $CUENTA3_ID"

# 2.4 Cuenta Ahorro Marianela - 496825
echo "→ Creando cuenta AHORRO 496825 para Marianela Montalvo (saldo inicial: 540)..."
CUENTA4=$(curl -s -X POST $BASE_URL_ACCOUNT/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d "{
    \"accountNumber\": \"496825\",
    \"accountType\": \"SAVING\",
    \"initialBalance\": 540.00,
    \"status\": true,
    \"customerId\": \"$MARIANELA_ID\"
  }")
CUENTA4_ID=$(echo $CUENTA4 | jq -r '.accountId')
echo "  ✓ Cuenta 496825 creada: $CUENTA4_ID"

# =====================================================
# CASO 3: Crear Cuenta Corriente para José Lema
# =====================================================
echo ""
echo "CASO 3: Crear Cuenta Corriente para José Lema"
echo "=============================================="
echo "→ Creando cuenta CORRIENTE 585545 para Jose Lema (saldo inicial: 1000)..."
CUENTA5=$(curl -s -X POST $BASE_URL_ACCOUNT/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d "{
    \"accountNumber\": \"585545\",
    \"accountType\": \"CHECKING\",
    \"initialBalance\": 1000.00,
    \"status\": true,
    \"customerId\": \"$JOSE_ID\"
  }")
CUENTA5_ID=$(echo $CUENTA5 | jq -r '.accountId')
echo "  ✓ Cuenta 585545 creada: $CUENTA5_ID"

# =====================================================
# CASO 4: Realizar Movimientos
# =====================================================
echo ""
echo "CASO 4: Realizar Movimientos"
echo "============================="

# 4.1 Retiro de 575 en cuenta 478758 (Jose Lema - Ahorro)
echo "→ Retiro de 575 en cuenta 478758 (Jose Lema)..."
MOV1=$(curl -s -X POST $BASE_URL_ACCOUNT/api/v1/movements \
  -H "Content-Type: application/json" \
  -d "{
    \"accountId\": \"$CUENTA1_ID\",
    \"movementType\": \"DEBIT\",
    \"value\": 575.00
  }")
echo "  ✓ Retiro realizado. Saldo: $(echo $MOV1 | jq -r '.balance')"

# 4.2 Depósito de 600 en cuenta 225487 (Marianela - Corriente)
echo "→ Depósito de 600 en cuenta 225487 (Marianela Montalvo)..."
MOV2=$(curl -s -X POST $BASE_URL_ACCOUNT/api/v1/movements \
  -H "Content-Type: application/json" \
  -d "{
    \"accountId\": \"$CUENTA2_ID\",
    \"movementType\": \"CREDIT\",
    \"value\": 600.00
  }")
echo "  ✓ Depósito realizado. Saldo: $(echo $MOV2 | jq -r '.balance')"

# 4.3 Depósito de 150 en cuenta 495878 (Juan - Ahorro)
echo "→ Depósito de 150 en cuenta 495878 (Juan Osorio)..."
MOV3=$(curl -s -X POST $BASE_URL_ACCOUNT/api/v1/movements \
  -H "Content-Type: application/json" \
  -d "{
    \"accountId\": \"$CUENTA3_ID\",
    \"movementType\": \"CREDIT\",
    \"value\": 150.00
  }")
echo "  ✓ Depósito realizado. Saldo: $(echo $MOV3 | jq -r '.balance')"

# 4.4 Retiro de 540 en cuenta 496825 (Marianela - Ahorro)
echo "→ Retiro de 540 en cuenta 496825 (Marianela Montalvo)..."
MOV4=$(curl -s -X POST $BASE_URL_ACCOUNT/api/v1/movements \
  -H "Content-Type: application/json" \
  -d "{
    \"accountId\": \"$CUENTA4_ID\",
    \"movementType\": \"DEBIT\",
    \"value\": 540.00
  }")
echo "  ✓ Retiro realizado. Saldo: $(echo $MOV4 | jq -r '.balance')"

# =====================================================
# CASO 5: Listado de Movimientos por Usuario
# =====================================================
echo ""
echo "CASO 5: Listado de Movimientos por Usuario"
echo "==========================================="

# Obtener fecha actual y fecha de hace 7 días
END_DATE=$(date +%Y-%m-%d)
START_DATE=$(date -d "7 days ago" +%Y-%m-%d 2>/dev/null || date -v-7d +%Y-%m-%d 2>/dev/null || echo "2025-11-17")

# 5.1 Reporte de Marianela Montalvo
echo "→ Reporte de Marianela Montalvo (del $START_DATE al $END_DATE)..."
curl -s "$BASE_URL_ACCOUNT/api/v1/reports/$MARIANELA_ID?startDate=$START_DATE&endDate=$END_DATE" | jq '.'

echo ""
echo "→ Reporte de Jose Lema..."
curl -s "$BASE_URL_ACCOUNT/api/v1/reports/$JOSE_ID?startDate=$START_DATE&endDate=$END_DATE" | jq '.'

echo ""
echo "→ Reporte de Juan Osorio..."
curl -s "$BASE_URL_ACCOUNT/api/v1/reports/$JUAN_ID?startDate=$START_DATE&endDate=$END_DATE" | jq '.'

echo ""
echo "=================================================="
echo "   ✓ TODOS LOS CASOS DE USO EJECUTADOS"
echo "=================================================="
echo ""
echo "Resumen de IDs creados:"
echo "  Jose Lema: $JOSE_ID"
echo "  Marianela Montalvo: $MARIANELA_ID"
echo "  Juan Osorio: $JUAN_ID"
echo ""
echo "  Cuenta 478758 (Jose - Ahorro): $CUENTA1_ID"
echo "  Cuenta 225487 (Marianela - Corriente): $CUENTA2_ID"
echo "  Cuenta 495878 (Juan - Ahorro): $CUENTA3_ID"
echo "  Cuenta 496825 (Marianela - Ahorro): $CUENTA4_ID"
echo "  Cuenta 585545 (Jose - Corriente): $CUENTA5_ID"
echo ""
