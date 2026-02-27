"""
Punto de entrada del servidor ZeroMQ de la Biblioteca.
Lee host/puerto desde config.json.
"""
#Se importan las librerías necesarias para el desarrollo del taller
import json #Librerias para manejo de archivos.json
import os #Libreria para manejo de rutas en el sistema

from server.library_service import run_service # Importamos la función que ejecuta el servidor de la biblioteca usando ZeroMQ

# Ruta al archivo de configuración (config.json) ubicado en el directorio raíz del proyecto
CONFIG_PATH = os.path.join(os.path.dirname(os.path.dirname(__file__)), "config.json")

# Abre toda la configuración del servidor desde el arhivo json y la devuelve como un diccionario 
def load_config():
    with open(CONFIG_PATH, "r", encoding="utf-8") as f:
        return json.load(f)

# Función principal que se encarga de la configuración del servidor
def main():
    cfg = load_config() # Lee la configuración del servidor
    host = cfg["server"]["host"] # Lee el host del servidor desde la configuración
    port = cfg["server"]["port"] # Lee el puerto del servidor desde la configuración
    bind_address = f"tcp://{host}:{port}" # Construye la dirección de enlace para el servidor ZMQ

    # Imprime la información de arranque del servidor
    print("=" * 50)
    print("  Servidor ZeroMQ de Biblioteca")
    print(f"  Dirección: {bind_address}")
    print("=" * 50)
    print()

     # Arranca el servidor ZMQ con las direciones correspondientes y el número de trabajadores indicados
    run_service(bind_address)


# Solo se ejecuta el main si se corre el archivo directamente  
if __name__ == "__main__":
    main()

