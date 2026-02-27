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

| Nombre del fichero                          | Tema trabajado                                 |
|---------------------------------------------|------------------------------------------------|
| Taller01_Aponte                             | Creación de Cluster y pruebas de rendimiento   |
| Taller02_Aponte                             | Creación de modelo cliente-servidor usando ZMQ |
---

## Desarrollos de contenido

### Taller01_Aponte

Taller en grupo referente a todo el proceso de craeación de un cluster usando varios equipos de la universidad y sus respectivas pruebas de rendimiento para entender como influye la conexión de los equipos en su eficiencia al resolver un algoritmo de multiplicación de matrices en C.

#### Ficheros en el directorio

- LaptopNode0: Ficheros usados en la laptop representada como nodo 0, y por consecuencia el organizador de los demás. Contiene un directorio con todos los resultados de la multiplicación de matrices en .csv, un fichero benchmark_mpi.pl, archivo en perl que facilita la automatización en la ejecución de todas las pruebas, usando diferentes tamaños de matrices. Un fichero hostfile que contiene los nombres de los nodos usados y la cantidad de slots que tienen. Un fichero matmul.c, que corresponde al algoritmo de multiplicación de matrices en C. Un fichero results.csv que contiene todos los resultados ordenados.
- LabtopNode1-4: Ficheros usados en las demás laptops, todos contienen los archivos hostfile y matmul.c.
- analisis_resultados.ipynb: Cuaderno en python que contiene todas las tablas generadas con los resultados para comprender los mismos.
- Taller-1_SD_Ramirez-Aponte-Pico-Arboleda-Losada-Santos-Rendon.pdf: Documento pdf que contiene todo el proceso de análisis realizado a partir del taller.

### Taller02_Aponte

Taller