#!/bin/bash

# =====================================================
# Script de Despliegue Completo
# Banking System - NTT Data
# =====================================================

set -e

# Colores para la salida
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║       🚀 DESPLIEGUE COMPLETO - SISTEMA BANCARIO NTT DATA     ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

# Función para mostrar mensaje de paso
step() {
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}▶ $1${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
}

# Función para mostrar éxito
success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Función para mostrar advertencia
warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

# Función para mostrar error
error() {
    echo -e "${RED}✗ $1${NC}"
}

# =====================================================
# PASO 1: Limpiar construcciones anteriores
# =====================================================
step "1. Limpiando construcciones anteriores..."
./gradlew clean
success "Construcciones anteriores limpiadas"
echo ""

# =====================================================
# PASO 2: Generar código desde OpenAPI
# =====================================================
step "2. Generando código desde especificaciones OpenAPI..."
./gradlew openApiGenerate
success "Código OpenAPI generado correctamente"
echo ""

# =====================================================
# PASO 3: Compilar proyectos
# =====================================================
step "3. Compilando proyectos (sin tests)..."
./gradlew build -x test
if [ $? -eq 0 ]; then
    success "Proyectos compilados exitosamente"
else
    error "Error al compilar los proyectos"
    exit 1
fi
echo ""

# =====================================================
# PASO 4: Verificar JARs generados
# =====================================================
step "4. Verificando JARs generados..."
CUSTOMER_JAR=$(find customer-service/build/libs -name "*.jar" -type f | head -1)
ACCOUNT_JAR=$(find account-service/build/libs -name "*.jar" -type f | head -1)

if [ -f "$CUSTOMER_JAR" ]; then
    success "Customer Service JAR: $CUSTOMER_JAR"
else
    error "Customer Service JAR no encontrado"
    exit 1
fi

if [ -f "$ACCOUNT_JAR" ]; then
    success "Account Service JAR: $ACCOUNT_JAR"
else
    error "Account Service JAR no encontrado"
    exit 1
fi
echo ""

# =====================================================
# PASO 5: Detener contenedores previos
# =====================================================
step "5. Deteniendo contenedores anteriores (si existen)..."
docker compose down 2>/dev/null || true
success "Contenedores anteriores detenidos"
echo ""

# =====================================================
# PASO 6: Construir imágenes Docker
# =====================================================
step "6. Construyendo imágenes Docker..."
docker compose build
if [ $? -eq 0 ]; then
    success "Imágenes Docker construidas correctamente"
else
    error "Error al construir las imágenes Docker"
    exit 1
fi
echo ""

# =====================================================
# PASO 7: Iniciar servicios
# =====================================================
step "7. Iniciando servicios con Docker Compose..."
docker compose up -d
if [ $? -eq 0 ]; then
    success "Servicios iniciados correctamente"
else
    error "Error al iniciar los servicios"
    exit 1
fi
echo ""

# =====================================================
# PASO 8: Esperar a que los servicios estén listos
# =====================================================
step "8. Esperando a que los servicios estén disponibles..."

echo -n "Esperando PostgreSQL (customer-db)..."
for i in {1..30}; do
    if docker exec customer-db pg_isready -U postgres >/dev/null 2>&1; then
        echo ""
        success "PostgreSQL Customer DB está listo"
        break
    fi
    echo -n "."
    sleep 1
done

echo -n "Esperando PostgreSQL (account-db)..."
for i in {1..30}; do
    if docker exec account-db pg_isready -U postgres >/dev/null 2>&1; then
        echo ""
        success "PostgreSQL Account DB está listo"
        break
    fi
    echo -n "."
    sleep 1
done

echo -n "Esperando Kafka..."
for i in {1..30}; do
    if docker logs kafka 2>&1 | grep -q "started (kafka.server.KafkaServer)"; then
        echo ""
        success "Kafka está listo"
        break
    fi
    echo -n "."
    sleep 1
done

echo ""
warning "Esperando a que los servicios Spring Boot inicien (15 segundos)..."
sleep 15
echo ""

# =====================================================
# PASO 9: Verificar estado de los servicios
# =====================================================
step "9. Verificando estado de los servicios..."

# Verificar Customer Service
if curl -s http://localhost:8080/api/v1/customers >/dev/null 2>&1; then
    success "Customer Service (puerto 8080) está respondiendo"
else
    warning "Customer Service no está respondiendo aún"
fi

# Verificar Account Service
if curl -s http://localhost:8081/api/v1/accounts >/dev/null 2>&1; then
    success "Account Service (puerto 8081) está respondiendo"
else
    warning "Account Service no está respondiendo aún"
fi

echo ""

# =====================================================
# PASO 10: Verificar Swagger UI
# =====================================================
step "10. Verificando Swagger UI..."

# Verificar Swagger Customer Service
if curl -s http://localhost:8080/swagger-ui/index.html >/dev/null 2>&1; then
    success "Swagger UI Customer Service disponible"
else
    warning "Swagger UI Customer Service no disponible"
fi

# Verificar Swagger Account Service
if curl -s http://localhost:8081/swagger-ui/index.html >/dev/null 2>&1; then
    success "Swagger UI Account Service disponible"
else
    warning "Swagger UI Account Service no disponible"
fi

echo ""

# =====================================================
# RESUMEN FINAL
# =====================================================
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║                  ✨ DESPLIEGUE COMPLETADO ✨                  ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""
echo -e "${GREEN}📦 Servicios Desplegados:${NC}"
echo ""
echo -e "${BLUE}🗄️  Bases de Datos:${NC}"
echo "   • Customer DB: localhost:5432"
echo "   • Account DB:  localhost:5433"
echo ""
echo -e "${BLUE}📨 Mensajería:${NC}"
echo "   • Kafka:       localhost:9092"
echo "   • Zookeeper:   localhost:2181"
echo ""
echo -e "${BLUE}🚀 Microservicios:${NC}"
echo "   • Customer Service: http://localhost:8080"
echo "   • Account Service:  http://localhost:8081"
echo ""
echo -e "${BLUE}📚 Documentación Swagger:${NC}"
echo "   • Customer Service: http://localhost:8080/swagger-ui/index.html"
echo "   • Account Service:  http://localhost:8081/swagger-ui/index.html"
echo ""
echo -e "${BLUE}📄 OpenAPI Specs:${NC}"
echo "   • Customer Service: http://localhost:8080/api-docs"
echo "   • Account Service:  http://localhost:8081/api-docs"
echo ""
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo -e "${YELLOW}💡 Comandos útiles:${NC}"
echo ""
echo "  Ver logs de todos los servicios:"
echo "    docker compose logs -f"
echo ""
echo "  Ver logs de un servicio específico:"
echo "    docker compose logs -f customer-service"
echo "    docker compose logs -f account-service"
echo ""
echo "  Reiniciar un servicio:"
echo "    docker compose restart customer-service"
echo ""
echo "  Detener todos los servicios:"
echo "    docker compose down"
echo ""
echo "  Ejecutar casos de uso:"
echo "    ./ejecutar-casos-uso.sh"
echo ""
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo -e "${GREEN}✅ ¡Sistema listo para usar!${NC}"
echo ""

# Preguntar si desea abrir Swagger UI
read -p "¿Deseas abrir Swagger UI en el navegador? (s/n): " -n 1 -r
echo ""
if [[ $REPLY =~ ^[SsYy]$ ]]; then
    echo -e "${BLUE}🌐 Abriendo Swagger UI...${NC}"
    
    # Detectar el sistema operativo y abrir el navegador
    if command -v xdg-open &> /dev/null; then
        # Linux
        xdg-open "http://localhost:8080/swagger-ui/index.html" 2>/dev/null &
        xdg-open "http://localhost:8081/swagger-ui/index.html" 2>/dev/null &
    elif command -v open &> /dev/null; then
        # macOS
        open "http://localhost:8080/swagger-ui/index.html"
        open "http://localhost:8081/swagger-ui/index.html"
    else
        warning "No se pudo detectar un navegador automáticamente"
        echo "  Por favor abre manualmente:"
        echo "    http://localhost:8080/swagger-ui/index.html"
        echo "    http://localhost:8081/swagger-ui/index.html"
    fi
fi

echo ""
echo -e "${GREEN}🎉 ¡Despliegue finalizado exitosamente!${NC}"
echo ""
