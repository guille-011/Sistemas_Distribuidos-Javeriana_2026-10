# Sistemas_Distribuidos-Javeriana_2026-10  

Bienvenido a mi repositorio.

En este repositorio se recogen los **códigos correspondiente a talleres, laboratorios y ejercicios de la materia de Indroducción a los Sistemas Distribuidos** para la Pontificia Universidad Javeriana, periodo 2026-10, creado por mi persona, Guillermo Aponte. Espero le sirva como un apoyo para su aprendizaje en caso de que su intención sea esta, o le permita echar un vistazo al proceso de mi formación profesional.

---

## Descripción

Este repositorio guarda todos programas en lenguaje C,realizados como ejercicios para reforzar el aprendizaje corespondiente a los temas vistos durante las clases magistrales de la materia de Instroducción a los Sistemas Distribuidos. Sirve como portafolio de laboratorio y código fuente entregable para la asignatura, además de un medio recopilatorio de los procesos de aprendizajes realizados por mi persona.

---


## Requisitos

Para compilar y ejecutar los códigos expuestos en este respositorio necesita:  

- Un sistema Unix / Linux / macOS (o WSL si estás en Windows)  
- GCC (compilador de C, por ejemplo `gcc`)  
- Make (para usar el `Makefile`)  
- MPI (para la creación de clusters)

## Resumen de contenidos

| Nombre del fichero                          | Tema trabajado                                       |
|---------------------------------------------|------------------------------------------------------|
| Taller01_Aponte                             | Creación de Cluster y pruebas de rendimiento         |
| Taller02_Aponte                             | Creación de modelo cliente-servidor usando ZMQ       |
| Taller03_Aponte                             | Implementación de un sistema de archivos distribuido |
---

## Desarrollos de contenido

### Taller01_Aponte

Taller en grupo referente a todo el proceso de craeación de un cluster usando varios equipos de la universidad y sus respectivas pruebas de rendimiento para entender como influye la conexión de los equipos en su eficiencia al resolver un algoritmo de multiplicación de matrices en C.

#### Ficheros en el directorio

- `LaptopNode0`: Ficheros usados en la laptop representada como nodo 0, y por consecuencia el organizador de los demás. Contiene un directorio con todos los resultados de la multiplicación de matrices en .csv, un fichero benchmark_mpi.pl, archivo en perl que facilita la automatización en la ejecución de todas las pruebas, usando diferentes tamaños de matrices. Un fichero hostfile que contiene los nombres de los nodos usados y la cantidad de slots que tienen. Un fichero matmul.c, que corresponde al algoritmo de multiplicación de matrices en C. Un fichero results.csv que contiene todos los resultados ordenados.
- `LaptopNode1-4`: Ficheros usados en las demás laptops, todos contienen los archivos hostfile y matmul.c.
- `analisis_resultados.ipynb`: Cuaderno en python que contiene todas las tablas generadas con los resultados para comprender los mismos.
- `Taller-1_SD_Ramirez-Aponte-Pico-Arboleda-Losada-Santos-Rendon.pdf`: Documento pdf que contiene todo el proceso de análisis realizado a partir del taller.

### Taller02_Aponte


Trabajo en grupo: Este repositorio contiene el trabajo en grupo para el Taller 02 del curso Sistemas Distribuidos. Está diseñado como un ejercicio práctico para implementar y probar un servicio de biblioteca distribuido que expone operaciones vía un servicio (server) y un cliente que consume el servicio usando ZMQ.

Propósito: Proveer una implementación educativa de una pequeña arquitectura cliente-servidor con almacenamiento local (simulado con DB.json), configuración externa y comunicación por mensajería (ZMQ). Sirve para experimentar con RPC/IPC, serialización de datos y manejo simple de base de datos en archivos JSON.

#### Ficheros en el directorio

- `client/static-templates`: Implementación del frontent para el programa.
- `client/app.py`: Aplicación cliente de alto nivel. Contiene la interfaz o flujo principal del cliente para invocar operaciones del servicio de biblioteca (por ejemplo: listar libros, buscar, prestar, devolver).
- `client/zmq_client.py`: Implementación del cliente ZMQ que se encarga de la comunicación con el servidor (envío/recepción de mensajes, serialización). Contiene la lógica de socket, envío de requests y manejo de respuestas.
- `server/db.py`: Módulo responsable del acceso y manipulación de la "base de datos" local (DB.json). Lee y escribe datos, y ofrece funciones auxiliares para operaciones CRUD sobre los registros de libros.
- `server/library_service.py`: Implementa la lógica del servicio de biblioteca: recibe peticiones, aplica la lógica de negocio (consulta, préstamo, devolución) y delega persistencia a server/db.py.
- `server/main.py`: Punto de entrada del servidor. Inicializa la configuración, arranca el servicio ZMQ/listener y registra los handlers necesarios para procesar peticiones entrantes.
- `config.json`: Archivo de configuración del proyecto (puerto, host, rutas de archivos, parámetros de ZMQ, etc.). Permite cambiar parámetros sin modificar el código.
- `DB.json`: Archivo JSON que actúa como base de datos persistente sencilla para almacenar la información de libros/usuarios según el diseño del taller.
- `requirements.txt`: Dependencias de Python necesarias para ejecutar cliente y servidor (por ejemplo: pyzmq, etc.). Usar `pip install -r requirements.txt` para instalarlas.

### Taller03_Aponte:

Trabajo en grupo orientado al análisis de rendimiento de multiplicación de matrices en un entorno híbrido que combina **MPI** y **OpenMP**. El taller se centra en estudiar cómo influyen el número de procesos distribuidos, el número de hilos por proceso y la distribución de carga entre hosts en el tiempo de ejecución de un algoritmo $O(N^3)$ de multiplicación de matrices cuadradas. El objetivo principal es comparar configuraciones de ejecución, automatizar experimentos y recopilar mediciones de rendimiento para documentar conclusiones sobre escalabilidad y eficiencia en un sistema distribuido paralelo.

#### Ficheros en el directorio:

Todos los archivos se encuentran dentro del directorio `evalMxM_MPI`:

- `resultadosDAT`: Directorio donde se almacenan los ficheros de resultados de las ejecuciones en formato `.dat`. Cada archivo contiene las mediciones de tiempo obtenidas para distintas combinaciones de tamaño de matriz, número de procesos y número de hilos, lo que sirve como base para el análisis posterior de rendimiento.

- `hilosHostFile`: Fichero de configuración que define la distribución de **hilos OpenMP** por host y/o por proceso MPI. Se utiliza para indicar cuántos hilos debe usar cada proceso en cada nodo, permitiendo experimentar con diferentes niveles de paralelismo a nivel de hilos.

- `lanzadorMPI.pl`: Script en **Perl** que automatiza la ejecución del experimento de multiplicación de matrices usando `mpirun`. Lee las configuraciones (tamaños de matrices, número de procesos, número de hilos, hostfiles) y lanza múltiples corridas del ejecutable, captura los tiempos impresos por el programa y los guarda en los archivos del directorio `resultadosDAT`, facilitando la ejecución masiva y repetible de pruebas.

- `Makefile`: Archivo de construcción que define las reglas para compilar los distintos módulos y ejecutables del taller. Incluye objetivos para generar los binarios principales que usan **MPI** y **OpenMP**, así como las dependencias entre `moduloMPI.c`, `moduloMPI.h` y los programas de prueba de rendimiento, asegurando una compilación consistente con las banderas de paralelismo adecuadas.

- `moduloMPI.c`: Implementación en C del módulo que encapsula la lógica de comunicación y coordinación usando **MPI** para la multiplicación de matrices. Contiene funciones reutilizables para inicializar el entorno MPI, distribuir submatrices entre procesos, recolectar resultados y medir tiempos, de forma que el código de los programas principales se mantenga más limpio y modular.

- `moduloMPI.h`: Cabecera en C asociada a `moduloMPI.c`. Declara las funciones y estructuras expuestas por el módulo MPI (por ejemplo, inicialización, distribución de bloques, utilidades de sincronización y medición), permitiendo que los distintos programas de prueba (como las versiones híbridas MPI+OpenMP) puedan reutilizar la misma interfaz de comunicación distribuida.

- `mxmOmpMPIfxc.c`: Programa principal en C que implementa la **multiplicación de matrices** usando un enfoque **híbrido MPI + OpenMP** con una estrategia de cálculo fija (fxc). Usa el módulo definido en `moduloMPI.h` para la distribución entre procesos y emplea directivas OpenMP para paralelizar los bucles internos de multiplicación, registrando tiempos de ejecución para cada configuración probada.

- `mxmOmpMPIfxt.c`: Variante del programa principal de multiplicación de matrices híbrida **MPI + OpenMP** con otra estrategia de distribución / configuración (fxt). Permite comparar cómo diferentes políticas (por ejemplo, variación en el reparto de filas/columnas o en el uso de hilos) afectan el rendimiento. También integra el módulo MPI común y genera resultados compatibles con el flujo de análisis del taller.

- `procesosHostfile`: Fichero de configuración que define la distribución de **procesos MPI** por host. Es utilizado por `mpirun` (a través del script `lanzadorMPI.pl`) para indicar cuántos procesos ejecutar en cada nodo del cluster o conjunto de máquinas, permitiendo explorar escenarios de balanceo de carga y afinidad entre procesos y recursos físicos.

- `informeDeRendimiento.pdf`: Documento generado a partir de los resultados obtenidos en `resultadosDAT`. Contiene el análisis de rendimiento del taller: tablas, gráficas y discusión sobre cómo escalan los tiempos de ejecución con el tamaño de la matriz, el número de procesos MPI, el número de hilos OpenMP y las diferentes configuraciones de hostfiles, así como las conclusiones finales del experimento.
