"""
Aplicación web Flask con HTMX para la interfaz gráfica de la Biblioteca.
Se conecta al servidor ZeroMQ para realizar operaciones.
Lee configuración web desde config.json.
"""
#Se importan las librerías necesarias para el desarrollo del taller
import json #Librerias para manejo de archivos.json
import os # Libreria para manejo de rutas en el sistema

from flask import Flask, render_template, request # Libreria para el desarrollo de la aplicación web usando Flask
from client.zmq_client import LibraryClient # Importamos el cliente ZMQ para la comunicación con el servidor

# Ruta al archivo de configuración (config.json) ubicado en el directorio raíz del proyecto
CONFIG_PATH = os.path.join(os.path.dirname(os.path.dirname(__file__)), "config.json")

# Se define una función que abre el archivo json que conteiene la configuración del servidor web
# Retorna un diccionario con la configuración específica para el servidor web contenida en el json
# o un diccionario vacío en caso de que ocurra algún error
def _load_web_config() -> dict:
    try:
        with open(CONFIG_PATH, "r", encoding="utf-8") as f:
            return json.load(f).get("web", {})
    except Exception:
        return {}

# Se crea una aplicación web usando Flask, que se encarga de manejar rutas y configurar el servidor HTTP
app = Flask(__name__)

# Se crea un cliente ZMQ global que se utilizará para comunicarse con el servidor principal usando ZeroMQ
zmq_client = LibraryClient()

# Decorador de Flask que define la ruta principal, ejecuta index() y devuelve el html donde se encuentra la página usada por el usuario
@app.route("/")
def index():
    """Página principal con las 4 operaciones."""
    return render_template("index.html")

# Se define la ruta de préstamo de libro por ISBN, que usará el método POST de HTTP para enviar datos y llama a la función loan_isbn()
@app.route("/loan-isbn", methods=["POST"])
# Función que obtiene el ISBN y el nombre del prestatario desde el formulario web llenado por el usuario, lo envía y devuelve el resultado
def loan_isbn():
    """Préstamo de libro por ISBN (HTMX partial)."""
    isbn = request.form.get("isbn", "").strip() # Se obtiene el valor del campo isbn
    borrower = request.form.get("borrower", "").strip() # Se obtiene el valor del campo borrower
    result = zmq_client.loan_by_isbn(isbn, borrower) # Se llama al método del cliente ZMQ que solicita el pŕestamo y guarda el resultado
    return render_template("partials/loan_result.html", result=result)  # Se retorna el resultado renderizado en un template HTML

# Se define la ruta de préstamo de libro por titulo, que usará el método POST de HTTP para enviar datos y llama a la función loan_title()
@app.route("/loan-title", methods=["POST"])
# Función que obtiene el Título y el nombre del prestatario desde el formulario web llenado por el usuario, lo envía y devuelve el resultado
def loan_title():
    """Préstamo de libro por título (HTMX partial)."""
    title = request.form.get("title", "").strip() # Se obtiene el valor del campo title
    borrower = request.form.get("borrower", "").strip() # Se obtiene el valor del campo borrower
    result = zmq_client.loan_by_title(title, borrower) # Se llama al método del cliente ZMQ que solicita el pŕestamo y guarda el resultado
    return render_template("partials/loan_result.html", result=result)  # Se retorna el resultado renderizado en un template HTML

# Se define la ruta de consulta de libro por titulo, que usará el método POST de HTTP para enviar datos y llama a la función query_isbn()
@app.route("/query-isbn", methods=["POST"])
# Función que obtiene el ISBN desde el formulario web llenado por el usuario, lo envía y devuelve el resultado
def query_isbn():
    """Consulta de libro por ISBN (HTMX partial)."""
    isbn = request.form.get("isbn", "").strip() # Se obtiene el valor del campo isbn
    result = zmq_client.query_by_isbn(isbn) # Se llama al método del cliente ZMQ que solicita la consulta y guarda el resultado
    return render_template("partials/query_result.html", result=result) # Se retorna el resultado renderizado en un template HTML

# Se define la ruta de devolución de libro por ISBN, que usará el método POST de HTTP para enviar datos y llama a la función return_isbn()
@app.route("/return-isbn", methods=["POST"])
# Función que obtiene el ISBN desde el formulario web llenado por el usuario, lo envía y devuelve el resultado
def return_isbn():
    """Devolución de libro por ISBN (HTMX partial)."""
    isbn = request.form.get("isbn", "").strip() # Se obtiene el valor del campo isbn
    result = zmq_client.return_by_isbn(isbn) # Se llama al método del cliente ZMQ que solicita la consulta y guarda el resultado
    return render_template("partials/return_result.html", result=result) # Se retorna el resultado renderizado en un template HTML

# Se establecer los valores predeterminados para ejecutar el script
if __name__ == "__main__":
    web_cfg = _load_web_config()
    host = web_cfg.get("host", "0.0.0.0")
    port = web_cfg.get("port", 5000)
    debug = web_cfg.get("debug", True)

    print("=" * 50)
    print(" Cliente Web de Biblioteca iniciado")
    print(f" Abrir en: http://localhost:{port}")
    print("=" * 50)
    # Se inicializa el servidor web Flask 
    app.run(debug=debug, host=host, port=port)
