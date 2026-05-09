// *********************************
//								= PONTIFICIA UNIVERSIDAD JAVERIANA =
//
//	Autor: J. Corredor Franco
//	Colaboradores: Daniel Ramirez, Ana Sofia Arboleda, Guillermo Aponte y Samuel Pico
//	Fecha: 8 mayo 2026
//	Introduccion a los Sistemas Distribuidos
//
//	Descripcion: Ejecuta el cliente que utiliza el proceso de comunicación TCP.
// *********************************
import java.net.*; // Importa Socket, InetAddress
import java.io.*;  // Importa BufferedReader, InputStreamReader, DataOutputStream

/*
Clase pública que contiene el método main para ejecutar el cliente TCP. Conectar con un servidor TCP en 
el puerto 6001 y envia mensajes leídos desde la entrada estándar hasta que el texto empiece con "fin".
 */
public class cliTCPsocket {
   /*
   Punto de entrada del programa Java. Recibe como parámetro
   espera la dirección (host o IP) del servidor.
    */
   public static void main(String argv[]) {

      // Verificación de argumentos: si no se pasa la dirección, se termina.
      if (argv.length == 0) {
         // mensajes de error.
         System.err.println("JAVA cliTCPsocket <<SERVIDOR>>");
         //  termina el programa con estado de error (1).
         System.exit(1);
      }

      // BufferedReader envolviendo permite leer líneas de texto desde la consola (entrada estándar).
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      System.out.println("Prueba de sockets TCP (CLIENTE)");

      // Declaraciones de variables
      Socket socket;               // instancia de java.net.Socket 
      InetAddress address;         // instancia de `java.net.InetAddress
      byte[] mensaje_bytes = new byte[256]; // buffer de bytes 
      String mensaje = "";       // variable para almacenar la línea leída del usuario

      try {
         // Resolución de nombre/host a una dirección IP.
         System.out.print("Capturando direccion de host... ");
         // InetAddress.getByName(String) devuelve un objeto InetAddress y lo guarda en addres.
         address = InetAddress.getByName(argv[0]);
         System.out.println("ok");

         System.out.print("Creando socket... ");
         // new Socket(address, 6001) inicia la conexión hacia el puerto 6001 del host especificado.
         socket = new Socket(address, 6001);
         System.out.println("ok");

         // Creación de flujo de salida para enviar datos al servidor.
         DataOutputStream out = new DataOutputStream(socket.getOutputStream());

         System.out.println("Introduce mensajes a enviar:");

         // Bucle que lee una línea desde la consola, luego envía la línea usando writeUTF, que escribe 
         // el tamaño y los bytes. Termina cuando el mensaje empieza con "fin" (condición de protocolo).
         do {
            // Devuelve la línea escrita por el usuario.
            mensaje = in.readLine();
            // Escribe la cadena en formato UTF.
            out.writeUTF(mensaje);
         } while (!mensaje.startsWith("fin"));
      }
      catch (Exception e) {
         // Manejo de errores genérico:
         System.err.println(e.getMessage());
         System.exit(1);
      }
   }
}
