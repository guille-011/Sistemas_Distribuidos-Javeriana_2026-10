MPICC = mpicc
FLAGS = -lm -fopenmp
MODULO = moduloMPI.c

PROGRAMAS = mxmOmpMPIfxc mxmOmpMPIfxt 
all: ${PROGRAMAS}

mxmOmpMPIfxc: 
	${MPICC} -o $@ $@.c ${MODULO} ${FLAGS}

mxmOmpMPIfxt: 
	${MPICC} -o $@ $@.c ${MODULO} ${FLAGS}

clean:
	$(RM) ${PROGRAMAS}
