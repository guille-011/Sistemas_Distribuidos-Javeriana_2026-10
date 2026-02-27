"""
Módulo de acceso a datos - Lee y escribe el archivo DB.json.
Usa threading.Lock para garantizar escrituras seguras en concurrencia.
"""
#Se importan las librerías necesarias para el desarrollo del taller
import json #Librerias para manejo de archivos.json
import os #Libreria para manejo de rutas en el sistema
import threading # Libreria para manejo de concurrencia y asegurar escrituras seguras en el archivo JSON
from datetime import date, timedelta # Libreria para manejo de fechas
from typing import Optional # Libreria para anotaciones de tipos opcionales

# Ruta al archivo de base de datos JSON (en la raíz del proyecto)
DB_PATH = os.path.join(os.path.dirname(os.path.dirname(__file__)), "DB.json")

# Lock para escrituras concurrentes seguras
_lock = threading.Lock()

# Lee el json y retorna la lista de libros, cada libro es un diccionario con sus respectivos atributos
def _read_db() -> list[dict]:
    """Lee todos los libros del archivo JSON."""
    with open(DB_PATH, "r", encoding="utf-8") as f:
        data = json.load(f)
        return data["libros"]

# Reescribe el archivo cuando se realizan cambios en la base de datos
def _write_db(books: list[dict]) -> None:
    """Escribe la lista de libros al archivo JSON."""
    with open(DB_PATH, "w", encoding="utf-8") as f:
        json.dump({"libros": books}, f, ensure_ascii=False, indent=2)

# Funciones públicas que devuelven información solicitada de la base de datos
def get_all_books() -> list[dict]:
    """Retorna todos los libros."""
    with _lock:
        return _read_db()


def get_book_by_isbn(isbn: str) -> Optional[dict]:
    """Busca un libro por su ISBN. Retorna None si no se encuentra."""
    with _lock:
        books = _read_db()
        for book in books:
            if book["ISBN"] == isbn:
                return book
        return None


def get_book_by_title(title: str) -> Optional[dict]:
    """Busca un libro por su título (búsqueda case-insensitive parcial).
    Retorna el primer libro que coincida."""
    with _lock:
        books = _read_db()
        title_lower = title.lower()
        for book in books:
            if title_lower in book["titulo"].lower():
                return book
        return None

# Funciones públicas que realizan cambios en la base de datos, como préstamos y devoluciones, usando el lock para garantizar la integridad de los datos
def loan_book(isbn: str, borrower: str) -> tuple[bool, str, Optional[dict]]:
    """
    Realiza el préstamo de un libro por ISBN.
    Retorna (success, message, book_data).
    """
    with _lock:
        books = _read_db()
        for book in books:
            if book["ISBN"] == isbn:
                if book["estado"] == "prestado":
                    return (
                        False,
                        f"El libro '{book['titulo']}' no está disponible. "
                        f"Fecha de devolución: {book['fecha_devolucion']}",
                        book,
                    )
                today = date.today()
                book["estado"] = "prestado"
                book["prestatario"] = borrower
                book["fecha_prestamo"] = today.isoformat()
                book["fecha_devolucion"] = (today + timedelta(days=7)).isoformat()
                _write_db(books)
                return (
                    True,
                    f"Préstamo exitoso: '{book['titulo']}' prestado a {borrower}",
                    book,
                )
        return (False, f"No se encontró un libro con ISBN: {isbn}", None)


def loan_book_by_title(title: str, borrower: str) -> tuple[bool, str, Optional[dict]]:
    """
    Realiza el préstamo de un libro por título.
    Retorna (success, message, book_data).
    """
    with _lock:
        books = _read_db()
        title_lower = title.lower()
        for book in books:
            if title_lower in book["titulo"].lower():
                if book["estado"] == "prestado":
                    return (
                        False,
                        f"El libro '{book['titulo']}' no está disponible. "
                        f"Fecha de devolución: {book['fecha_devolucion']}",
                        book,
                    )
                today = date.today()
                book["estado"] = "prestado"
                book["prestatario"] = borrower
                book["fecha_prestamo"] = today.isoformat()
                book["fecha_devolucion"] = (today + timedelta(days=14)).isoformat()
                _write_db(books)
                return (
                    True,
                    f"Préstamo exitoso: '{book['titulo']}' prestado a {borrower}",
                    book,
                )
        return (False, f"No se encontró un libro con título: '{title}'", None)


def return_book(isbn: str) -> tuple[bool, str]:
    """
    Devuelve un libro por ISBN.
    Retorna (success, message).
    """
    with _lock:
        books = _read_db()
        for book in books:
            if book["ISBN"] == isbn:
                if book["estado"] == "no prestado":
                    return (False, f"El libro '{book['titulo']}' ya está disponible, no tiene préstamo activo.")
                book["estado"] = "no prestado"
                book["prestatario"] = None
                book["fecha_prestamo"] = None
                book["fecha_devolucion"] = None
                _write_db(books)
                return (True, f"Devolución exitosa: '{book['titulo']}' ha sido devuelto.")
        return (False, f"No se encontró un libro con ISBN: {isbn}")
