# Simulador de Cajero Automático Concurrente · Exclusión Mutua

## Descripción
El objetivo principal es demostrar el uso de mecanismos de sincronización para proteger una sección crítica, evitando condiciones de carrera y garantizando que el acceso y modificación del saldo sea seguro.

El sistema está compuesto por un backend desarrollado en Spring Boot (Java) que gestiona la lógica multihilo y la exclusión mutua y un frontend en React (Vite) conectado mediante una API REST para visualizar las operaciones y la bitácora en tiempo real.

---

## Arquitectura del Sistema
El proyecto sigue una arquitectura Cliente-Servidor desacoplada:

### 1. Backend (Spring Boot)
*Ubicación:* `backend/src/main/java/com/unl/sistemas/distribuidos/`
Diseñado bajo el patrón de arquitectura en capas:
- **`controller/`**: Contiene `BankController.java`. Expone los endpoints REST (`/api/cajero/saldo`, `/api/cajero/operar`, `/api/cajero/simular-practica`, `/api/cajero/reiniciar`), procesa las peticiones del cliente y gestiona las respuestas HTTP con códigos de estado adecuados.
- **`service/`**: Contiene `BankService.java`. Encapsula la lógica de negocio, gestionando el recurso compartido (`saldo` inicializado en **$1000.00** con precisión monetaria de `BigDecimal`) protegido por un `ReentrantLock` (Mutex) para asegurar la exclusión mutua estricta.

### 2. Frontend (React / Vite)
*Ubicación:* `frontend/src/`
- **`index.html`**: Estructura base de la interfaz y metadatos con tipografía editorial.
- **`App.css`**: Estilos basados en un diseño "Editorial de Alta Gama" en modo oscuro.
- **`App.jsx`**: Lógica de la interfaz de usuario. Permite disparar los procesos individuales de la práctica o lanzar la simulación concurrente oficial, actualizando dinámicamente las tarjetas de métricas y la bitácora de eventos.

---

##  ¿Cómo funciona la simulación?
1. La cuenta bancaria se inicializa con un saldo base de **$1000.00**.
2. El sistema gestiona los procesos concurrentes definidos en la práctica:
   - **Proceso 1:** Intenta realizar un retiro de **$300.00**.
   - **Proceso 2:** Intenta realizar un retiro de **$250.00**.
   - **Proceso 3:** Realiza un depósito de **$200.00**.
3. Cuando se ejecutan de manera concurrente (simultánea):
   - Cada hilo invoca `mutexSaldo.lock()` antes de acceder a la sección crítica.
   - Se evalúan las reglas de negocio (validando montos positivos y existencia de fondos suficientes para retiros).
   - Se actualiza el recurso compartido de forma atómica y se libera el cerrojo mediante `mutexSaldo.unlock()` en un bloque `finally`.
4. El frontend interactúa mediante solicitudes HTTP asíncronas para refrescar el saldo actual y registrar cada evento en la bitácora.

---

## ¿Se logró lo solicitado?

### Objetivo General
Implementar mecanismos de exclusión mutua y sincronización para proteger secciones críticas, garantizando que no se produzcan condiciones de carrera al acceder a recursos compartidos.

**Sí se logró.** El uso del `ReentrantLock` garantizó que las operaciones concurrentes sobre la cuenta bancaria no se superpusieran. Al ejecutar la simulación oficial de los tres procesos, el sistema siempre converge de forma determinista al saldo final correcto de **$650.00**.

### Criterios de la Rúbrica
- **Resultado Esperado:** Modificación segura del saldo bajo concurrencia y prevención de sobregiros.
  - *Verificación:* Se ejecutó la simulación concurrente y los intentos inválidos fueron rechazados de manera controlada mediante códigos HTTP estándar (`422 Unprocessable Entity`), evitando estados inconsistentes de la cuenta.
- **Resultado Esperado:** Bitácora clara de eventos que refleje la ejecución de los procesos.
  - *Verificación:* La interfaz web y la consola muestran de forma ordenada el flujo de intentos, aprobaciones y rechazos de los procesos.

---

##  Cómo ejecutar el proyecto

### Requisitos Previos
- **Java 17** o superior.
- **Node.js** y **npm** (para el frontend).
- **Maven** (gestor de dependencias del backend).

### 1. Ejecutar el Backend (Spring Boot)
Abre una terminal y navega hasta la carpeta del backend:
cd backend/
mvn spring-boot:run
cd frontend/
npm install
npm run dev
