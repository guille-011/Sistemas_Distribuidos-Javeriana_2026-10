// *********************************
//								= PONTIFICIA UNIVERSIDAD JAVERIANA =
//
//	Autor: J. Corredor Franco
//	Colaboradores: Daniel Ramirez, Ana Sofia Arboleda, Guillermo Aponte y Samuel Pico
//	Fecha: 8 mayo 2026
//	Introduccion a los Sistemas Distribuidos
//
//	Descripcion: Ejecuta el cliente que utiliza el proceso de comunicación UDP.
// *********************************

import java.net.*; // DatagramSocket, InetAddress, DatagramPacket
import java.io.*;  // BufferedReader, InputStreamReader

/*
 Cliente UDP simple que envía datagramas al puerto 6000 del servidor
 especificado en argv[0].
 */
public class cliUDPsocket {
   
	 /*
	  * Lee líneas desde la entrada estándar y las envía como datagramas
	  * UDP hasta que la línea empiece con "fin".
	  */
	 public static void main(String argv[]) {
     
		// Comprueba que se haya pasado la dirección del servidor
		if (argv.length == 0) {
			System.err.println("Java socketudpcli servidor");
			System.exit(1);
		}

		// Leer líneas de la consola (entrada estándar)
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Prueba de sockets UDP (cliente)");
      
		DatagramSocket socket;       // Instancia de socket UDP
		InetAddress address;         // Dirección del servidor
		byte[] mensaje_bytes = new byte[256]; // Buffer para convertir String a bytes
		String mensaje = "";       // Línea leída del usuario
		DatagramPacket paquete;      // Paquete UDP que se enviará

		// Convierte el String vacío inicial a bytes (no estrictamente necesario aquí)
		mensaje_bytes = mensaje.getBytes();
      
		try {
		  // Crea un DatagramSocket 
		  System.out.print("Creando socket... ");
		  socket = new DatagramSocket();
		  System.out.println("ok");

		  // Resuelve la dirección del servidor
		  System.out.print("Capturando direccion de host... ");
		  address = InetAddress.getByName(argv[0]);
		  System.out.println("ok");

		  System.out.println("Introduce mensajes a enviar:");

		  // Bucle de lectura y envío de datagramas UDP
		  do {
			  // Lee una línea de la entrada estándar
			  mensaje = in.readLine();
			  // Convierte la cadena a bytes usando la codificación por defecto
			  mensaje_bytes = mensaje.getBytes();
			  // Crea un DatagramPacket con los bytes, la longitud, la dirección y el puerto destino
			  paquete = new DatagramPacket(mensaje_bytes, mensaje.length(), address, 6000);
			  // Envía el paquete por el socket UDP
			  socket.send(paquete);
		  } while (!mensaje.startsWith("fin")); 
		}
		catch (Exception e) {
		  // Manejo de errores:
		  System.err.println(e.getMessage());
		  System.exit(1);
		}
	}
}
