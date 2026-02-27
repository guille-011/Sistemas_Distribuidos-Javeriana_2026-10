"""
Servicio de Biblioteca sobre ZeroMQ.
Recibe peticiones JSON por un socket REP y responde con JSON.
"""
#Se importan las librerías necesarias para el desarrollo del taller
import json # Librerias para manejo de archivos.json
import zmq # Libreria para la comunicación usando ZeroMQ

# Se importan las funciones para el manejo de libros desde el archivo server.db
from server.db import (
    get_book_by_isbn,
    loan_book,
    loan_book_by_title,
    return_book,
)


# Acciones soportadas
# Acciones que el servidor puede manejar, para evitar que el cliente envie acciones inprocesables 
ACTIONS = {
    "Prestamo por ISBN",
    "Prestamo por Titulo",
    "Consulta por ISBN",
    "Devolucion por ISBN",
}

# Router lógico interno del servidor, recibe peticiones JSON, las valida y las despacha según la acción solicitada por el cliente
def handle_request(message: dict) -> dict:
    """Despacha una petición JSON al handler correspondiente."""
    action = message.get("action")

    # Lee la petición enviada por el cliente, según la acción solicitada, se llama a la función correspondiente para procesar la petición
    # y retorna el resultado de dicha petición, si la acción no es reconocida se envía un mensaje de error
    if action not in ACTIONS:
        return {"success": False, "message": f"Acción desconocida: {action}"}

    if action == "Prestamo por ISBN":
        return _loan_by_isbn(message)
    elif action == "Prestamo por Titulo":
        return _loan_by_title(message)
    elif action == "Consulta por ISBN":
        return _query_by_isbn(message)
    elif action == "Devolucion por ISBN":
        return _return_by_isbn(message)

# Funciones para cada tipo de acción, cada función se encarga de validar los datos recibidos,
# llamar a la función de manejo en la base de datos y formatear la respuesta para enviarsela al cliente
# si los datos son incorrectos se le informa al cliente
def _loan_by_isbn(msg: dict) -> dict:
    isbn = msg.get("isbn", "").strip()
    borrower = msg.get("borrower", "").strip()

    if not isbn:
        return {"success": False, "message": "El ISBN es requerido."}
    if not borrower:
        return {"success": False, "message": "El nombre del prestatario es requerido."}

    success, message, book_data = loan_book(isbn, borrower)
    result = {"success": success, "message": message}
    if book_data:
        result["book"] = book_data
    return result


def _loan_by_title(msg: dict) -> dict:
    title = msg.get("title", "").strip()
    borrower = msg.get("borrower", "").strip()

    if not title:
        return {"success": False, "message": "El título es requerido."}
    if not borrower:
        return {"success": False, "message": "El nombre del prestatario es requerido."}

    success, message, book_data = loan_book_by_title(title, borrower)
    result = {"success": success, "message": message}
    if book_data:
        result["book"] = book_data
    return result


def _query_by_isbn(msg: dict) -> dict:
    isbn = msg.get("isbn", "").strip()

    if not isbn:
        return {"found": False, "message": "El ISBN es requerido."}

    book_data = get_book_by_isbn(isbn)
    if book_data:
        return {
            "found": True,
            "message": f"Libro encontrado: '{book_data['titulo']}'",
            "book": book_data,
        }
    else:
        return {"found": False, "message": f"No se encontró un libro con ISBN: {isbn}"}


def _return_by_isbn(msg: dict) -> dict:
    isbn = msg.get("isbn", "").strip()

    if not isbn:
        return {"success": False, "message": "El ISBN es requerido."}

    success, message = return_book(isbn)
    return {"success": success, "message": message}

# Arranque del servicio 
def run_service(bind_address: str = "tcp://*:5555"):
    """Inicia el loop del servicio ZMQ (socket REP)."""
    context = zmq.Context()  # Contexto principal del servidor, se usa para crear los sockets 
    socket = context.socket(zmq.REP) # Socket tipo REP (reply) para recibir peticiones y enviar respuestas
    socket.bind(bind_address) # Enlaza el socket a la dirección especificada para escuchar las peticiones entrantes

    print(f"  Servicio ZMQ escuchando en: {bind_address}")
    print("  Esperando peticiones...\n")

    try:
        # Crea un ciclo infinito para que el worker esté siempre escuchando peticiones, hasta que el servidor se detenga
        while True:
            # Espera a recibir peticiones, cuando llegan decodifica el mensaje, lo procesa y genera una respuesta 
            raw = socket.recv()
            try:
                # Decodifica la petición recibida de bytes a string y luego a un diccionario usando json.loads
                request = json.loads(raw.decode("utf-8"))
                print(f"  ← Petición recibida: {request.get('action', '?')}")
                response = handle_request(request) # Procesa la petición usando el router lógico y obtiene la respuesta a enviar al cliente
            except json.JSONDecodeError:
                response = {"success": False, "message": "JSON inválido."}
            except Exception as e:
                response = {"success": False, "message": f"Error interno: {str(e)}"}

            # Codifica la respueta y la envía al cliente
            socket.send(json.dumps(response, ensure_ascii=False).encode("utf-8"))
            print(f"  → Respuesta enviada: {'OK' if response.get('success') or response.get('found') else 'ERROR'}")
    except KeyboardInterrupt: # Exepción para manejar la interrupción del servidor con Ctrl+C, para detenerlo de forma segura
        print("\n  Deteniendo servicio...")
    finally:
        # Cuando termina la ejecución del servicio, se cierra el socket y el contexto ZMQ para liberar recursos
        socket.close()
        context.term()
        print("  Servicio detenido.")

