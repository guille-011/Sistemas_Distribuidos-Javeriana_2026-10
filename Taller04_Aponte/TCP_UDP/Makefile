#!/usr/bin/make -f
# ***********************************************************************************************
#			= PONTIFICIA UNIVERSIDAD JAVERIANA =
#
#	Autor: J. Corredor Franco
#	Colaboradores: Daniel Ramirez, Ana Sofia Arboleda, Guillermo Aponte y Samuel Pico
#	Fecha: 8 mayo 2026
#	Introduccion a los Sistemas Distribuidos
#
#	Descripcion: Automatización
# ***********************************************************************************************

JAVAC = javac
JAVA = java
BIN = bin
SOURCES = *.java


# Objetivo por defecto: compila todos los .java en $(BIN)
All:
	@mkdir -p $(BIN)
	$(JAVAC) -d $(BIN) $(SOURCES)

# Ejecutar servidor TCP (usa clases en $(BIN))
run-tcp-server:
	$(JAVA) -cp $(BIN) serTCPsocket

# Ejecutar cliente TCP: `make run-tcp-client ARGS=localhost`
run-tcp-client:
	$(JAVA) -cp $(BIN) cliTCPsocket $(ARGS)

# Ejecutar servidor UDP
run-udp-server:
	$(JAVA) -cp $(BIN) serUDPsocket

# Ejecutar cliente UDP: `make run-udp-client ARGS=localhost`
run-udp-client:
	$(JAVA) -cp $(BIN) cliUDPsocket $(ARGS)

clean:
	$(RM) -r $(BIN)
