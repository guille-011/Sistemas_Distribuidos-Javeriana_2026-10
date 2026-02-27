"""
Cliente ZeroMQ para el servicio de Biblioteca.
Encapsula la comunicación con el servidor ZMQ usando patrón REQ-REP.
"""
#Se importan las librerías necesarias para el desarrollo del taller
import json #Librerias para manejo de archivos.json
import os #Libreria para manejo de rutas en el sistema
import zmq # Libreria para la comunicación usando ZeroMQ


# Ruta al archivo de configuración (config.json) ubicado en el directorio raíz del proyecto
CONFIG_PATH = os.path.join(os.path.dirname(os.path.dirname(__file__)), "config.json")

# Función que establece a que dirección del servidor ZMQ se debe contectar el cliente,
# leyendo la configuración desde el archivo config.json, si ocurre un error, retorna una dirección por defecto
def _default_server_address() -> str:
    """Lee la dirección del servidor desde config.json."""
    try:
        with open(CONFIG_PATH, "r", encoding="utf-8") as f:
            cfg = json.load(f)
        host = cfg["client"]["server_host"]
        port = cfg["client"]["server_port"]
        return f"tcp://{host}:{port}"
    except Exception:
        return "tcp://localhost:5555"


# Clase que encapsula toda la lógica de comunicación 
class LibraryClient:
    """Cliente que se conecta al servidor ZMQ de la biblioteca."""

    # Se crea el método constructor, que establece la conexión con el servidor ZMQ,
    # el ZeroMQ context, es decir, el contenedor principal de los sockets
    # el socket tipo REQ, es decir, petición y se conecta al servidor indicándole su dirección IP y su puerto
    # y los tiempos de espera para evitar bloqueos indefinidos si el servidor no responde a tiempo
    def __init__(self, server_address: str | None = None):
        if server_address is None:
            server_address = _default_server_address()
        self.server_address = server_address
        self.context = zmq.Context()
        self.socket = self.context.socket(zmq.REQ)
        self.socket.connect(server_address)
        # Timeout de 10 segundos para evitar bloqueos indefinidos
        self.socket.setsockopt(zmq.RCVTIMEO, 10000)
        self.socket.setsockopt(zmq.SNDTIMEO, 10000)
    
    # Método que cierra el socket y el contexto ZMQ para liberar recursos al terminar
    def close(self):
        """Cierra el socket y el contexto ZMQ."""
        self.socket.close()
        self.context.term()

    # Método que se encarga de enviar las peticiones al servidor ZMQ, recibe un diccionario con la petición, lo convierto a JSON
    # lo envía al servidor, espera la respuesta, la decodifica y la convierte a un nuevo diccionario para mostrarla 
    def _send_request(self, request: dict) -> dict:
        """Envía una petición JSON y espera la respuesta."""
        try:
            # Envía al socket:
            # - json.dumps, convierte el diccionario de la petición a una cadena json
            # - ensure_ascii=False, para permitir caracteres no ASCII en la salida JSON
            # - encode("utf-8"), para convertir la cadena JSON a bytes antes de enviarla
            self.socket.send(json.dumps(request, ensure_ascii=False).encode("utf-8"))
            # Espera la respuesta del servidor.
            raw = self.socket.recv()
            # Decodifica la respuesta de bytes y la convierte a un diccionario usando json.loads
            return json.loads(raw.decode("utf-8"))
        # Manejo de errores
        except zmq.Again:  # Caso de timeout
            return {"success": False, "found": False, "message": "Timeout: el servidor no respondió a tiempo."}
        except zmq.ZMQError as e: # Errores de conexión
            return {"success": False, "found": False, "message": f"Error de conexión ZMQ: {str(e)}"}
        except Exception as e: # Cualquier otro error 
            return {"success": False, "found": False, "message": f"Error inesperado: {str(e)}"}

    # Métodos públicos en envía las peticiones al servidor
    # -loan_by_isbn
    # -loan_by_title
    # -query_by_isbn
    # -return_by_isbn
    def loan_by_isbn(self, isbn: str, borrower: str) -> dict:
        """Solicita préstamo de un libro por ISBN."""
        return self._send_request({
            "action": "Prestamo por ISBN",
            "isbn": isbn,
            "borrower": borrower,
        })

    def loan_by_title(self, title: str, borrower: str) -> dict:
        """Solicita préstamo de un libro por título."""
        return self._send_request({
            "action": "Prestamo por Titulo",
            "title": title,
            "borrower": borrower,
        })

    def query_by_isbn(self, isbn: str) -> dict:
        """Consulta un libro por ISBN."""
        return self._send_request({
            "action": "Consulta por ISBN",
            "isbn": isbn,
        })

    def return_by_isbn(self, isbn: str) -> dict:
        """Devuelve un libro por ISBN."""
        return self._send_request({
            "action": "Devolucion por ISBN",
            "isbn": isbn,
        })

