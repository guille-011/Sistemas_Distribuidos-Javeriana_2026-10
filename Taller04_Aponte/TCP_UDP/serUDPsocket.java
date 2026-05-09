// *********************************
//								= PONTIFICIA UNIVERSIDAD JAVERIANA =
//
//	Autor: J. Corredor Franco
//	Colaboradores: Daniel Ramirez, Ana Sofia Arboleda, Guillermo Aponte y Samuel Pico
//	Fecha: 8 mayo 2026
//	Introduccion a los Sistemas Distribuidos
//
//	Descripcion: Ejecuta el servidor que utiliza el proceso de comunicación UDP.
// *********************************

import java.net.*; // DatagramSocket, DatagramPacket, InetAddress


/*
Servidor UDP que escucha en el puerto 6000 y muestra los mensajes recibidos.
Recibe datagramas de hasta 256 bytes y los convierte a String para impresión.
 */
public class serUDPsocket {
   
		/*
		 Crea y enlaza un `DatagramSocket` al puerto 6000 y procesa paquetes
		 recibidos en un bucle hasta que llegue un mensaje que empiece con "fin".
		 */
		public static void main(String argv[]) {
     
			System.out.println("Prueba de sockets UDP (servidor)");
      
			DatagramSocket socket; // Instancia que representará el socket UDP
			boolean fin = false;    // Bandera para terminar el bucle cuando se reciba "fin"

			try {
				// Crea y enlaza el DatagramSocket al puerto 6000.
				System.out.print("Creando socket... ");
				socket = new DatagramSocket(6000);
				System.out.println("ok");

				System.out.println("Recibiendo mensajes... ");
        
				do {
					 // Buffer de bytes donde se almacenará el contenido del paquete entrante
					 byte[] mensaje_bytes = new byte[256];
					 // Creación de un DatagramPacket vacío para recibir datos
					 DatagramPacket paquete = new DatagramPacket(mensaje_bytes, 256);
					 //bloquea hasta que llega un paquete
					 socket.receive(paquete);
           
					 // Conversión de bytes a String.
					 String mensaje = new String(mensaje_bytes);
           
					 // Imprime el mensaje recibido por consola
					 System.out.println(mensaje);
           
					 // Si el mensaje empieza con "fin", se marca la bandera para salir
					 if (mensaje.startsWith("fin")) fin = true;
				} while (!fin); 
			}
			catch (Exception e) {
				//Manejo de errores:

				System.err.println(e.getMessage());
				System.exit(1);
			}
	 }
}
