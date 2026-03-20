/************************************************************************************************
=Pontificia Universidad Javeriana=

Author: John Corredor
Módulo compartido de funciones auxiliares para la multiplicación de
matrices cuadradas en entorno MPI + OpenMP. Provee:
  - Inicialización determinista de matrices
  - Dos algoritmos de multiplicación (FxC y FxT) paralelizados con OpenMP
  - Transposición de matrices
  - Medición de tiempo con gettimeofday()
  - Validación de argumentos y divisibilidad
  - Funciones de depuración (impresión de matrices y resumen)

Ejecución (desde los programas principales):
$mpirun -hostfile fileHOSTSMPI -np 3 ./mmMPIv1 8 1
************************************************************************************************/

// === Librerías necesarias ===
#include <mpi.h>        // Funciones MPI (MPI_Abort, etc.)
#include <omp.h>        // Directivas y funciones OpenMP (omp_set_num_threads)
#include <stdio.h>      // Entrada/salida estándar (printf)
#include <omp.h>        // (inclusión duplicada heredada del prototipo original)
#include <stdlib.h>     // Gestión de memoria (malloc, calloc, free) y exit()
#include <time.h>       // Semilla para srand (time(NULL))
#include <sys/time.h>   // gettimeofday() para medición de tiempo en microsegundos
#include "moduloMPI.h"  // Prototipos de todas las funciones de este módulo

// Variables globales para captura de tiempo: inicio y fin de medición
struct timeval inicio, fin;

/*** impMatrix: imprime una matriz de tamaño n×n solo si n < 13 (depuración). ***/
// Recibe un puntero a la matriz y su dimensión n.
// Evita imprimir matrices grandes durante las pruebas reales del experimento.
void impMatrix(double *mat, int n){
if(n<13){  // Solo imprime para matrices pequeñas (verificación manual)
printf("\n====================================================================");
for (int i = 0; i < n*n; i++, mat++) {
if(i%n == 0 ) printf("\n"); // Salto de línea al inicio de cada fila
printf("%0.3f ", *mat);      // Imprime cada elemento con 3 decimales
}
printf("\n====================================================================\n");
}
}

/*** matrixTRP: transpone la matriz mB de tamaño N×N y almacena el resultado en mT. ***/
// mT[i][j] = mB[j][i], convirtiendo columnas en filas para acceso secuencial.
void matrixTRP(int N, double *mB, double *mT){
for(int i=0; i<N; i++)
for(int j=0; j<N; j++)
mT[i*N+j] = mB[j*N+i]; // Intercambia fila por columna
impMatrix(mT, N); // Imprime la transpuesta solo si N < 13
}

/*** mxmOmpFxT: multiplicación Filas × Transpuesta con OpenMP. ***/
// Parámetros:
//   mA  -> tajada local de la matriz A (tw filas × D columnas)
//   mB  -> matriz B completa (D × D)
//   mC  -> matriz resultado local (tw filas × D columnas)
//   tw  -> cantidad de filas en la tajada del worker
//   D   -> dimensión de las matrices cuadradas (N)
//   nH  -> número de hilos OpenMP a utilizar
void mxmOmpFxT(double *mA, double *mB, double *mC, int tw, int D, int nH){
double *mT  = (double *)calloc(D*D, sizeof(double)); // Reserva memoria para la transpuesta
matrixTRP(D, mB, mT);       // Transpone B → mT para acceso secuencial por filas
omp_set_num_threads(nH);     // Configura cantidad de hilos OpenMP
#pragma omp parallel
{
#pragma omp for              // Distribuye las filas de la tajada entre hilos
for(int i=0; i<tw; i++)      // Recorre cada fila de la tajada local de A
for(int j=0; j<D; j++){  // Recorre cada columna (fila de la transpuesta)
double *pA, *pB, Suma = 0.0;
pA = mA+i*D;            // Puntero al inicio de la fila i de A
pB = mT+j*D;            // Puntero al inicio de la fila j de B^T
for(int k=0; k<D; k++, pA++, pB++) // Ambos avanzan secuencialmente (+1)
Suma += *pA * *pB;   // Producto punto fila_A · fila_BT
mC[i*D+j] = Suma;       // Almacena resultado en C[i][j]
}
}
free(mT); // Libera la transpuesta temporal
}

/*** mxmOmpFxC: multiplicación Filas × Columnas (clásica) con OpenMP. ***/
// Mismos parámetros que mxmOmpFxT.
// Diferencia clave: recorre B por columnas con stride D, menos favorable para caché.
void mxmOmpFxC(double *mA, double *mB, double *mC, int tw, int D, int nH){
omp_set_num_threads(nH);     // Configura cantidad de hilos OpenMP
#pragma omp parallel
{
#pragma omp for              // Distribuye las filas de la tajada entre hilos
for(int i=0; i<tw; i++)      // Recorre cada fila de la tajada local de A
for(int j=0; j<D; j++){  // Recorre cada columna de B
double *pA, *pB, Suma = 0.0;
pA = mA+i*D;            // Puntero al inicio de la fila i de A
pB = mB+j;              // Puntero al inicio de la columna j de B
for(int k=0; k<D; k++, pA++, pB+=D) // A avanza +1, B salta +D (stride)
Suma += *pA * *pB;   // Producto punto fila_A · columna_B
mC[i*D+j] = Suma;       // Almacena resultado en C[i][j]
}
}
}

/*** mensajeVerifica: imprime un resumen de la configuración de distribución. ***/
// Solo imprime si N < 13 (modo depuración para matrices pequeñas).
// Muestra dimensión, cantidad de workers y tamaño de cada tajada.
void mensajeVerifica(int N, int cantidadW){
if(N<13){
printf("\n");
printf("********************************************************************\n");
printf("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
printf("++++++ \t\tDimensión de Matrix NxN \t  = %dx%d \t++++\n", N, N);
printf("++++++ \t\tCantidad de Workers (np - MASTER) = %d \t\t++++\n", cantidadW);
printf("++++++ \t\tTajada de matriz A para Workers   = %dx%d \t++++\n", N/cantidadW,N);
printf("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
printf("********************************************************************\n");
printf("\n");
}
}


/*** iniMatrix: inicializa las matrices A y B con valores deterministas. ***/
// Recibe punteros a ambas matrices y la dimensión D.
// Llena A con 0.08*i y B con 0.02*i, garantizando datos reproducibles.
void iniMatrix(double *mA, double *mB, int D){
srand(time(NULL));  // Semilla (presente pero no usada; se usan valores fijos)
for(int i=0; i<D*D; i++, mA++, mB++){
*mA = 0.08*i; // Valor determinista para A (no aleatorio)
*mB = 0.02*i; // Valor determinista para B (no aleatorio)
}
}

/*** iniTime: registra el instante de inicio de la medición. ***/
// Usa gettimeofday() para capturar segundos y microsegundos.
void iniTime(){
gettimeofday(&inicio, (void *)0); // Guarda timestamp de inicio
}

/*** endTime: calcula e imprime el tiempo transcurrido desde iniTime(). ***/
// Resta los campos de la estructura timeval (segundos y microsegundos),
// convierte a microsegundos totales e imprime por stdout.
// Este valor es el que lanzadorMPI.pl redirige a los archivos .dat.
void endTime(){
gettimeofday(&fin, (void *)0);    // Captura timestamp de fin
fin.tv_usec -= inicio.tv_usec;    // Resta microsegundos
fin.tv_sec  -= inicio.tv_sec;     // Resta segundos
double tiempo = (double) (fin.tv_sec*1000000 + fin.tv_usec); // Total en µs
printf("%9.0f \n", tiempo);       // Imprime tiempo en microsegundos
}

/*** argumentos: valida que el programa reciba exactamente 2 argumentos. ***/
// Si argc != 3 (ejecutable + DimMatriz + NumHilos), muestra uso y termina.
void argumentos(int cantidad){
if (cantidad != 3){
printf("Ingreso de Argumentos: \n\n");
printf("\t\t$mpirun -hostfile file -np p ./ejecutable DimMatriz NumHilos \n\n");
printf("\nfile: Archivo de Master y Workers \n");
printf("\np: procesos Master+Workers\n");
exit(0); // Termina si los argumentos son incorrectos
}
}

/*** verificarDiv: verifica que N sea divisible entre la cantidad de workers. ***/
// Si workers < 1 o N % workers != 0, aborta la ejecución MPI.
// Esta validación es crítica: la distribución por tajadas requiere división exacta.
void verificarDiv(int qworkers, int Dim){
if ((qworkers < 1) || (Dim%qworkers != 0)) {
printf("Error: NxN (%d) debe ser divisible por cantidad de workers (%d)\n", Dim, qworkers);
printf("Error: Número de procesos (%d) > 1 \n", qworkers);
MPI_Abort(MPI_COMM_WORLD, 1); // Aborta todos los procesos MPI
exit(0);
}
}
