# SmartEnvironment TV

## Descripción

**SmartEnvironment TV** es una aplicación para Android TV diseñada para actuar como un centro de control inteligente para el hogar. Provee una interfaz de usuario optimizada para televisores (UI de 10 pies) que permite a la familia interactuar con dispositivos del hogar, consultar información relevante y comunicarse, todo desde la comodidad de la sala.

## Módulos y Funcionalidades

La aplicación cuenta con los siguientes módulos principales:

1.  **Dashboard Principal:**
    *   Una vista de resumen con tarjetas grandes y legibles, ideal para la visualización a distancia.
    *   Saluda a la familia y presenta un acceso rápido a todas las funcionalidades.

2.  **Módulo de Domótica (Control de Dispositivos):**
    *   Permite controlar el estado (encender/apagar) de diferentes dispositivos del hogar.
    *   Actualmente implementado con estados simulados para:
        *   Luz de la Sala
        *   Bocina Bluetooth
        *   Cafetera

3.  **Módulo de Clima:**
    *   Muestra la temperatura actual y una breve descripción del clima.
    *   Obtiene los datos de una API externa de clima, permitiendo al usuario configurar la ubicación.

4.  **Módulo de Mensajes (Pizarrón Familiar):**
    *   Muestra un mensaje destacado para toda la familia, funcionando como un pizarrón de notas digital.
    *   Actualmente muestra un mensaje local, con planes de sincronización en la nube (ej. Firebase).

## Cómo Compilar y Ejecutar

1.  **Configuración:**
    *   Clona o descarga este repositorio.
    *   Abre el proyecto con una versión reciente de Android Studio.
    *   El proyecto utiliza Gradle y las dependencias necesarias se descargarán automáticamente.

2.  **Emulador:**
    *   En el AVD Manager de Android Studio, crea un nuevo emulador de tipo **TV** (se recomienda una resolución de 1080p) con una versión de API 33 o superior.
    *   Inicia el emulador.

3.  **Ejecución:**
    *   Selecciona el emulador de TV recién creado como el dispositivo de destino.
    *   Presiona el botón "Run" (▶️) en Android Studio. La aplicación se compilará e instalará en el emulador.
