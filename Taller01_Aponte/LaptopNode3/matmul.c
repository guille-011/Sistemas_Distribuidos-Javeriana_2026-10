#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>

int main(int argc, char** argv) {
    MPI_Init(&argc, &argv);

    int rank, size;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    int N;

    if (rank == 0) {
        if (argc != 2) {
            MPI_Abort(MPI_COMM_WORLD, 1);
        }
        N = atoi(argv[1]);
        if (N <= 0) {
            MPI_Abort(MPI_COMM_WORLD, 1);
        }
    }

    MPI_Bcast(&N, 1, MPI_INT, 0, MPI_COMM_WORLD);

    if (N % size != 0) {
        MPI_Abort(MPI_COMM_WORLD, 1);
    }

    int rows = N / size;

    double *A = NULL, *B = NULL, *C = NULL;
    double *localA = malloc(rows * N * sizeof(double));
    double *localC = malloc(rows * N * sizeof(double));
    B = malloc(N * N * sizeof(double));

    if (!localA || !localC || !B) {
        MPI_Abort(MPI_COMM_WORLD, 1);
    }

    if (rank == 0) {
        A = malloc(N * N * sizeof(double));
        C = malloc(N * N * sizeof(double));
        if (!A || !C) {
            MPI_Abort(MPI_COMM_WORLD, 1);
        }

        for (int i = 0; i < N*N; i++) {
            A[i] = 1.0;
            B[i] = 1.0;
        }
    }

    MPI_Barrier(MPI_COMM_WORLD);
    double t_start = MPI_Wtime();

    MPI_Scatter(A, rows*N, MPI_DOUBLE,
                localA, rows*N, MPI_DOUBLE,
                0, MPI_COMM_WORLD);

    MPI_Bcast(B, N*N, MPI_DOUBLE, 0, MPI_COMM_WORLD);

    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < N; j++) {
            double sum = 0.0;
            for (int k = 0; k < N; k++)
                sum += localA[i*N + k] * B[k*N + j];
            localC[i*N + j] = sum;
        }
    }

    MPI_Gather(localC, rows*N, MPI_DOUBLE,
               C, rows*N, MPI_DOUBLE,
               0, MPI_COMM_WORLD);

    MPI_Barrier(MPI_COMM_WORLD);
    double t_end = MPI_Wtime();

    if (rank == 0) {
        printf("%.6f\n", t_end - t_start);
    }

    free(localA);
    free(localC);
    free(B);
    if (rank == 0) {
        free(A);
        free(C);
    }

    MPI_Finalize();
    return 0;
}
