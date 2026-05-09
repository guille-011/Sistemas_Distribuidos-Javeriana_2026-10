// *********************************
//								= PONTIFICIA UNIVERSIDAD JAVERIANA =
//
//	Autor: J. Corredor Franco
//	Colaboradores: Daniel Ramirez, Ana Sofia Arboleda, Guillermo Aponte y Samuel Pico
//	Fecha: 8 mayo 2026
//	Introduccion a los Sistemas Distribuidos
//
//	Descripcion: Ejecuta el servidor que utiliza el proceso de comunicación TCP.
// *********************************
import java.net.*; // Importa ServerSocket, Socket, InetAddress si hace falta
import java.io.*;  // Importa DataInputStream

/*
Servidor TCP simple que escucha en el puerto 6001.
Acepta una única conexión y lee mensajes enviados por el cliente
 */
public class serTCPsocket {
	 //Inicia el `ServerSocket`, acepta la conexión entrante y procesa mensajes.
	  
	 public static void main(String argv[]) {
      
		// Mensaje informativo por consola
		System.out.println("\n\n\t=**SOCKETS TCP <<SERVIDOR>>");
      
		ServerSocket socket; // Declaración de la instancia
		boolean fin = false;  // Variable que podría usarse para terminar el bucle

		try {
			// Crea y enlaza un ServerSocket al puerto 6001.
			socket = new ServerSocket(6001);
        
			// Espera y acepta la primera conexión entrante 
			Socket socket_cli = socket.accept();
        
			// Crea un DataInputStream para leer datos del cliente.
			DataInputStream in = new DataInputStream(socket_cli.getInputStream());
        
			// Bucle de lectura continuo:
			// - readUTF() espera el formato escrito por writeUTF() en el cliente.
			do {
				String mensaje = ""; // Variable local para almacenar cada mensaje recibido
				// Método: readUTF() lee una cadena precedida por su longitud en UTF-8.
				mensaje = in.readUTF();
				// Imprime el mensaje recibido por consola.
				System.out.println(mensaje);
			} while (1>0);
		}
		catch (Exception e) {
			// Manejo de errores
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}
}
