#!/usr/bin/env perl
use strict;
use warnings;

# ---------- CONFIG ----------
my $hostfile = "hostfile";      # hostfile de MPI
my $exe      = "./matmul";      # binario compilado
my @sizes    = (200, 400, 800, 1600, 3200);
my $runs     = 30;
my @nps      = (20, 4);         # primero np=20 y luego np=4

# ---------- CONFIRMACIONES PREVIAS ----------
die "ERROR: hostfile '$hostfile' not found\n" unless -e $hostfile;
die "ERROR: executable '$exe' not found\n"     unless -e $exe;
die "ERROR: executable '$exe' not executable (run: chmod +x $exe)\n" unless -x $exe;

sub run_once {
    my ($np, $N) = @_;

    # Comando: mpirun -np <np> --hostfile hostfile ./matmul N
    my $cmd = "mpirun -np $np --hostfile $hostfile $exe $N";

    # Captura stdout+stderr
    my $out = `$cmd 2>&1`;
    my $rc  = $? >> 8;

    if ($rc != 0) {
        die "ERROR: mpirun failed (np=$np, N=$N, exit=$rc)\n$out\n";
    }

    # Extraer TODOS los números del output (por si vienen warnings)
    my @nums = ($out =~ /(-?\d+(?:\.\d+)?(?:[eE]-?\d+)?)/g);

    if (!@nums) {
        die "ERROR: no numeric time found (np=$np, N=$N)\n$out\n";
    }

    # El tiempo debe ser el ÚLTIMO número impreso por el programa
    return $nums[-1] + 0.0;
}

for my $np (@nps) {
    for my $N (@sizes) {
        my $filename = "matricesde${N}_np${np}.csv";

        open(my $fh, ">", $filename)
          or die "ERROR: cannot write '$filename': $!\n";

        print $fh "run,time_seconds\n";

        for my $r (1..$runs) {
            my $t = run_once($np, $N);

            # imprime progreso en consola
            printf "N=%d np=%d run=%d time=%.6f\n", $N, $np, $r, $t;

            # guarda en archivo
            printf $fh "%d,%.6f\n", $r, $t;
        }

        close($fh);
        print "Saved: $filename\n";
    }
}

print "Done.\n";
