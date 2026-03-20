/************************************************************************************************
								=	Pontificia Universidad Javeriana	=

	Author: John Corredor
	Versión 3.0 Prototipo "Inicial" MPI Distribución de Matrices en Nodos de Cómputo	
			- Ingreso OpenMP
	Descripción:
		Multiplicación de Matrices cuadradas, algoritmo Filas x Columnas
		Square Matrix Multiplication, rowsxcolumns version:
		=> MPI_Init:				- Inicializacíón del entorno MPI 
		=> MPI_Comm_size:			- Obtener cantidad de procesos o workers en ejecución
		=> MPI_Comm_rank:			- Obtener el ID del proceso o worker  
		=> MPI_Get_processor_name:	- Obtener nombre del Nodo de Cómputo 
		=> MPI_Finalize:			- Finalización del entorno MPI

	Ejecución:
		$mpirun -hostfile fileHOSTSMPI -np 3 ./mmMPIv1 8 1
************************************************************************************************/
#include <mpi.h>
#include <stdlib.h>
#include "moduloMPI.h"

#define MASTER 0
#define FROM_MASTER 1
#define FROM_WORKER 2

int main(int argc, char *argv[]) {
/****************************************************************************************************
=           0.- Revisión de argumentos de entradas 
****************************************************************************************************/
	argumentos(argc);
	int N  = (int) atoi(argv[1]);								//#--> Dimensión de Matrices (N x N) 
	int nH = (int) atoi(argv[2]);								//#--> cantidad de Hilos 
    double *matrixA = NULL, *matrixB = NULL, *matrixC = NULL;
	int cantProcesos;											//#--> Cantidad de procesos enviados: " -np "
	int idProceso;												//#--> Identidad del procesos "-np" (0: MASTER)
	int tW;														//#--> Tajada de matriz a distribuir a workers

    MPI_Init(&argc, &argv);										//#--> Inicialización de entorno MPI
    MPI_Comm_size(MPI_COMM_WORLD, &cantProcesos);				//#--> Captura de argumento cantidad de procesos
    MPI_Comm_rank(MPI_COMM_WORLD, &idProceso);					//#--> Captura de argumento identificador de procesos
	int cantidadW = cantProcesos-1; 							//#--> Cantidad de nodos workers
/****************************************************************************************************
=           1.- Proceso MASTER (idProceso=0) inicializa todo                                        =
****************************************************************************************************/
    if (idProceso == MASTER) {
		/*===> Verificar: N (dimensión de matriz NxN) es div por el Número de Workers   <===*/
		verificarDiv(cantidadW, N);
		/*===> Reserva de memoria para matrices en MASTER                               <===*/
        matrixA = (double *)calloc(N*N,sizeof(double));
        matrixB = (double *)calloc(N*N,sizeof(double));
        matrixC = (double *)calloc(N*N,sizeof(double));
		/*===> Inicializar matrixA y matrixB                                            <===*/
		iniMatrix(matrixA, matrixB, N);
		/*===> Cálculo de tajada de MatrixA para ser enviada a los Workers              <===*/
        tW = N/cantidadW;
		/*===> Sí N<13 : Impresión de Matrices, confirmación valores iniciales          <===*/
		impMatrix(matrixA,N);
		impMatrix(matrixB,N);
		mensajeVerifica(N, cantidadW);
    }
/****************************************************************************************************
=           2.- Transmisión Dimension de Matriz (N) y tajada de cómputo a todos los Workers         =
****************************************************************************************************/
    MPI_Bcast(&N,  1, MPI_INT, 0, MPI_COMM_WORLD);
    MPI_Bcast(&tW, 1, MPI_INT, 0, MPI_COMM_WORLD);
/****************************************************************************************************
=           3.- MASTER: envía las "tajadas" a los Workers: hacen el cálculo. Master imprime         =
****************************************************************************************************/
    if (idProceso == MASTER) {
		/*===> Captura de Tiempo de INICIO de cómputo                                   <===*/        
		iniTime(); 
		/*===> Se transmite la MatrixB a todos los Workers                              <===*/
        MPI_Bcast(matrixB, N*N, MPI_DOUBLE, 0, MPI_COMM_WORLD);
		/*===> Se envían las tajadas de matrixA a cada Worker                           <===*/
        for (int w = 1; w < cantProcesos; w++) {
            int iniSlice = (w-1)*tW;
            MPI_Send(matrixA+iniSlice*N, tW*N, MPI_DOUBLE, w, 0, MPI_COMM_WORLD);
        }
		/*===> Se reciben los resultados de los cálculos de cada Worker                 <===*/
        for (int w = 1; w < cantProcesos; w++) {
            int iniSlice = (w-1)*tW;
            MPI_Recv(matrixC+iniSlice*N, tW*N, MPI_DOUBLE, w, 1, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        }
		/*===> Captura de Tiempo de FINAL de cómputo                                    <===*/
		endTime(); 
		/*===> Sí N<13 : Impresión de Matrices Resultado (Verficación MxM)              <===*/ 
		impMatrix(matrixC, N);
    } 
/****************************************************************************************************
=           4.- WORKERS: recibe cada uno las "tajadas", cálculan. Finalmente envian resultado       =
****************************************************************************************************/
	if (idProceso > MASTER){
		/*===> Reserva de memoria para matrices en cada Worker                          <===*/
        double *matA = (double *)malloc(tW*N*sizeof(double));
        double *matB = (double *)malloc(N*N*sizeof(double));
        double *matC = (double *)malloc(tW*N*sizeof(double));
		/*===> Recibir matrixB desde MASTER                                             <===*/
        MPI_Bcast(matB, N*N, MPI_DOUBLE, 0, MPI_COMM_WORLD);
		/*===> Recibir tajada de matrixA desde MASTER                                   <===*/
        MPI_Recv(matA, tW*N, MPI_DOUBLE, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
/****************************************************************************************************
=           5.- WORKERS: cálculo de la multiplicación de matrices con OpenMP                        =
****************************************************************************************************/

		mxmOmpFxC(matA, matB, matC, tW, N, nH);

		/*===> Enviar resultados de cálculo de tajada al MASTER                         <===*/
        MPI_Send(matC, tW*N, MPI_DOUBLE, 0, 1, MPI_COMM_WORLD);
		/*===> Liberar la reserva de memoria en cada WORKER                             <===*/
        free(matA);
        free(matC);
        free(matB);
    }
/****************************************************************************************************
=           6.- MASTER: liberar la reserva de memoria en el MASTER                                  =
****************************************************************************************************/
    if (idProceso == MASTER) {
        free(matrixA);
        free(matrixB);
        free(matrixC);
    }
    MPI_Finalize();
    return 0;
}
