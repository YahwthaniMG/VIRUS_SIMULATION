# VIRUS (Virtual Infection Research Using Simulation)

VIRUS es un sistema de simulación multi-agente que modela la propagación de una enfermedad en un entorno visual interactivo basado en un mapamundi. El sistema demuestra cómo una enfermedad puede propagarse a través de una población móvil, considerando diferentes estados de salud, interacciones entre individuos y la intervención de centros médicos.

## 🦠 Características Principales

- Simulación visual en tiempo real sobre un mapamundi
- Sistema multi-agente con comportamientos autónomos
- Diferentes estados de agentes (Sano, Infectado, Mutado, etc.)
- Sistema de hospitales para curación de agentes
- Registro de muertes en servidor TCP dockerizado
- Implementación de patrones de concurrencia

## 🔧 Tecnologías Utilizadas

- Java 17
- Swing para la interfaz gráfica
- Threads y concurrencia
- Sockets TCP para comunicación cliente-servidor
- Docker para el servidor de registro
- Patrones de diseño (Singleton, Observer, State)

## 💻 Requisitos del Sistema

- Java Development Kit (JDK) 17 o superior
- Docker
- Sistema operativo compatible con Java y Visual Studio Code (Windows, macOS)

## 🚀 Instalación y Ejecución

### Configuración del Servidor

1. Navegar al directorio del servidor:
```bash
cd src/main/docker
```

2. Construir la imagen Docker:
```bash
docker build -t virus-server .
```

3. Ejecutar el contenedor:
```bash
docker run -p 7777:7777 virus-server
```

### Ejecución del Simulador

1. Compilar el proyecto:
   - Para Visual Studio Code, abra la carpeta del proyecto raiz (VIRUS), busque el codigo Main.java y ejecute (Es necesario tener extensiones de Java para el editor ).
   - Extensiones necesarias:
        - Extension Pack for Java
        - Java
        - Java Language Support
        - Java Run

## 🎮 Uso del Simulador

1. Al iniciar, ingresar el número total de agentes (recomendado: entre 3 y 100)
2. Configurar la distribución de agentes y parámetros de simulación (editas variables del simulador)
3. La simulación comenzará mostrando:
   - Agentes sanos (verde)
   - Agentes infectados (púrpura)
   - Agentes mutados (rojo)
   - Hospitales (azul)
   - Agentes en curación (amarillo)
   - Agentes muertos (gris)
4. La opción Thread Status (muestra los agentes en el estado del Thread)   

## 🏗 Arquitectura del Sistema

### Agentes y Estados
- **Healthy**: Agentes sanos susceptibles a infección
- **Infected**: Agentes que pueden infectar a otros
- **Mutated**: Versión más peligrosa con mayor rango de infección
- **Dead**: Estado final tras mutación
- **Healing**: Estado transitorio durante curación
- **Hospital**: Centros de curación estáticos

### Sistema de Interacción
- Detección de colisiones
- Radio de infección
- Sistema de inmunidad temporal
- Gestión de curaciones

### Comunicación Cliente-Servidor
- Registro de muertes de agentes
- Servidor TCP multi-threaded
- Sistema de reportes en tiempo real

## 👥 Desarrolladores

- Brandon Mitzrael Magaña Avalos
- Eduardo Ulises Martínez Vaca
- Yahwthani Morales Gómez

## 📚 Curso
Fundamentos de Programación en Paralelo
Dr. Juan Carlos López Pimentel
ISGC - Universidad Panamericana Campus Guadalajara

## 📝 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.
