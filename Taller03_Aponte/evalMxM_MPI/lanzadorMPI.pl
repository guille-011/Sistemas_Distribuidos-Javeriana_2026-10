#!/usr/bin/perl
# ==============================================================================
# lanzadorMPI.pl - Script de automatización del experimento de rendimiento.
# Ejecuta todas las combinaciones de algoritmo, tamaño de matriz, procesos MPI
# e hilos OpenMP, repitiendo cada caso 30 veces y almacenando los tiempos
# de ejecución (en microsegundos) en archivos .dat dentro de datosDAT/.
# ==============================================================================

# Obtiene el directorio de trabajo actual y elimina el salto de línea
$Path = `pwd`;
chomp($Path);

# Directorio donde se almacenarán todos los archivos de resultados .dat
$DirDatos = "$Path/datosDAT";
system("mkdir -p $DirDatos"); # Crea el directorio si no existe

# Número de repeticiones por cada configuración experimental (Ley de Grandes Números)
$Repeticiones = 30;

# Ejecutables a evaluar: algoritmo clásico (FxC) y algoritmo transpuesta (FxT)
@Programas = ("mxmOmpMPIfxc", "mxmOmpMPIfxt");

# Tamaños de matriz NxN a evaluar
@Matrices = ("400", "800", "1600", "3200");

# Hostfile para el caso 1 (distribución de procesos entre nodos, múltiples slots)
$HostProcesos = "procesosHostfile";
# Hostfile para el caso 2 (un proceso por nodo, paralelismo con hilos OpenMP)
$HostHilos = "hilosHostFile";

# Mapa de etiquetas legibles para los nombres de archivo .dat
# Asocia cada ejecutable con su nombre descriptivo
%ProgTag = ("mxmOmpMPIfxc" => "clasica", "mxmOmpMPIfxt" => "transpuesta");


# ==============================================================================
# CASO 1: Variación del número de procesos MPI entre nodos.
# Se fija nH=1 (sin paralelismo OpenMP) y se varía np.
# Usa procesosHostfile con --map-by node para distribuir procesos entre nodos.
# np=5 → 4 workers, np=17 → 16 workers, np=33 → 32 workers.
# ==============================================================================
@NPsCaso1 = ("5", "17", "33"); # Valores de np a evaluar

foreach $np (@NPsCaso1) {
	foreach $programa (@Programas) {
		foreach $size (@Matrices) {
			# Nombre del archivo .dat: codifica caso, algoritmo, tamaño y np
			# Ejemplo: Procesos-Pr-clasica-N-800-NP-17.dat
			$archivo = "$DirDatos/Procesos-Pr-".$ProgTag{$programa}."-N-".$size."-NP-".$np.".dat";

			printf("rm $archivo\n");
			system("rm -f $archivo"); # Elimina archivo previo para empezar limpio

			# Ejecuta 30 repeticiones del mismo caso experimental
			for ($i = 0; $i < $Repeticiones; $i++) {
				# nH=1: sin hilos OpenMP, solo paralelismo MPI
				$comando = "mpirun -hostfile $HostProcesos -np $np --map-by node ./$programa $size 1";
				printf("$comando\n"); # Imprime el comando en consola para seguimiento
				system("$comando >> $archivo"); # Ejecuta y redirige el tiempo al .dat
			}

			close($archivo);
		}
	}
}

# ==============================================================================
# CASO 2: Variación del número de hilos OpenMP por nodo.
# Se fija np=5 (1 proceso por nodo, 4 workers) y se varía nH.
# Usa hilosHostFile (1 slot por nodo) con --map-by node.
# nH=1 (secuencial por worker), nH=4 y nH=8 (paralelismo intra-nodo).
# ==============================================================================
@HilosCaso2 = ("1", "4", "8"); # Valores de hilos OpenMP a evaluar

foreach $programa (@Programas) {
	foreach $size (@Matrices) {
		foreach $hilos (@HilosCaso2) {
			# Nombre del archivo .dat: incluye np fijo (5) y cantidad de hilos
			# Ejemplo: Hilos-Pr-transpuesta-N-1600-NP-5-H-8.dat
			$archivo = "$DirDatos/Hilos-Pr-".$ProgTag{$programa}."-N-".$size."-NP-5-H-".$hilos.".dat";

			printf("rm $archivo\n");
			system("rm -f $archivo"); # Elimina archivo previo

			# Ejecuta 30 repeticiones del mismo caso experimental
			for ($i = 0; $i < $Repeticiones; $i++) {
				$comando = "mpirun -hostfile $HostHilos -np 5 --map-by node ./$programa $size $hilos";
				printf("$comando\n");
				system("$comando >> $archivo"); # Redirige tiempo al .dat
			}

			close($archivo);
		}
	}
}

# ==============================================================================
# CASO 3: Referencia base — ejecución concentrada en el nodo master.
# Se usa np=2 (master + 1 worker) con nH=1.
# Se necesita np=2 porque el código requiere al menos 1 worker (rango > 0);
# el proceso master (rango 0) no realiza cómputo de multiplicación.
# --host master:2 fuerza ambos procesos en el mismo nodo master.
# Este caso sirve como línea base para calcular speedup y eficiencia.
# ==============================================================================
foreach $programa (@Programas) {
	foreach $size (@Matrices) {
		# Nombre del archivo .dat: prefijo ProcesosMaster, np fijo en 2
		# Ejemplo: ProcesosMaster-Pr-clasica-N-3200-NP-2.dat
		$archivo = "$DirDatos/ProcesosMaster-Pr-".$ProgTag{$programa}."-N-".$size."-NP-2.dat";

		printf("rm $archivo\n");
		system("rm -f $archivo"); # Elimina archivo previo

		# Ejecuta 30 repeticiones del caso base
		for ($i = 0; $i < $Repeticiones; $i++) {
			# --host master:2 asigna 2 slots al nodo master, ambos procesos locales
			$comando = "mpirun --host master:2 -np 2 ./$programa $size 1";
			printf("$comando\n");
			system("$comando >> $archivo"); # Redirige tiempo al .dat
		}

		close($archivo);
	}
}
