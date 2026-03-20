/************************************************************************************************
								=	Pontificia Universidad Javeriana	=

	Author: John Corredor
	Versión Prototipo "Inicial" MPI Distribución de Matrices en Nodos de Cómputo	

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

#ifndef __MODULOMPI_H__
#define __MODULOMPI_H__


void matrixTRP(int N, double *mB, double *mT);

void mxmOmpFxT(double *mA, double *mB, double *mC, int tw, int D, int nH);

void mxmOmpFxC(double *mA, double *mB, double *mC, int tw, int D, int nH);

void impMatrix(double *matA, int n);

void iniMatrix(double *mA, double *mB, int D);

void iniTime();

void endTime();

void argumentos(int cantidad);

void verificarDiv(int qworkers, int Dim);

void mensajeVerifica(int N, int cantidadW);

#endif
