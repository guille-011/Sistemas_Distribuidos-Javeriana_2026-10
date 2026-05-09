# ***********************************************************************************************
#								= PONTIFICIA UNIVERSIDAD JAVERIANA =
#
#	Autor: J. Corredor Franco
#	Colaboradores: Daniel Ramirez, Ana Sofia Arboleda, Guillermo Aponte y Samuel Pico
#	Fecha: 8 mayo 2026
#	Introducción a los Sistemas Distribuidos
#
#	Descripción: Define las instrucciones para compilar los archivos Java del taller,
#	             guardar los archivos generados en la carpeta bin y ejecutar las versiones
#	             secuencial y paralelas del programa de cajeras y clientes.
# ***********************************************************************************************

JAVAC = javac
JAVA = java
PKG = tallerThreads
BIN = bin
SOURCES = *.java

# Compila todos los archivos Java y guarda los .class en la carpeta bin.
All:
	@mkdir -p $(BIN)
	$(JAVAC) -d $(BIN) $(SOURCES)

# Ejecuta la version secuencial.
# En esta version una cajera termina toda su compra antes de que empiece la siguiente.
secuencial:
	@echo "\n >>> EJECUCIÓN versión Secuencial: <<< \n"
	$(JAVA) -cp $(BIN) $(PKG).Main
	@echo ""

# Ejecuta la version paralela usando Thread.
# En esta version cada cajera es un hilo porque CajeraThread extiende de Thread.
thread:
	@echo "\n >>> EJECUCIÓN versión Paralela con Thread: <<< \n"
	$(JAVA) -cp $(BIN) $(PKG).MainThread
	@echo ""

# Ejecuta la version paralela usando Runnable.
# En esta version cada compra es una tarea Runnable que se entrega a un hilo.
runnable:
	@echo "\n >>> EJECUCIÓN versión Paralela con Runnable: <<< \n"
	$(JAVA) -cp $(BIN) $(PKG).MainRunnable
	@echo ""

# Elimina la carpeta bin con los archivos compilados.
clean:
	$(RM) -r $(BIN)
