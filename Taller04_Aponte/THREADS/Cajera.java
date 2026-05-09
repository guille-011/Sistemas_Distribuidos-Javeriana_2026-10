// ***********************************************************************************************
//								= PONTIFICIA UNIVERSIDAD JAVERIANA =
//
//	Autor: J. Corredor Franco
//	Colaboradores: Daniel Ramirez, Ana Sofia Arboleda, Guillermo Aponte y Samuel Pico
//	Fecha: 8 mayo 2026
//	Introduccion a los Sistemas Distribuidos
//
//	Descripcion: Define una cajera normal que procesa la compra de un cliente de forma
//	             secuencial. Recorre los productos del carrito, espera el tiempo indicado
//	             para cada producto y muestra el avance del proceso en consola.
// ***********************************************************************************************

package tallerThreads;

// Clase que representa a una cajera en la version secuencial.
public class Cajera { 
	
	// Nombre de la cajera que atiende al cliente.
	private String nombre;
	
	// Constructor: recibe y guarda el nombre de la cajera.
	public Cajera(String nombre) {
		this.nombre = nombre; 
	}
	
	// Procesa todos los productos del cliente uno por uno.
	public void procesarCompra(Cliente cliente, long timeStamp) { 
		// Muestra el inicio de la compra y el tiempo transcurrido.
		System.out.println("La cajera " + this.nombre + 
				" comienza a procesar la compra del cliente " + cliente.getNombre() + 
				" en el tiempo: " + (System.currentTimeMillis() - timeStamp) / 1000 + "seg"); 
		
		// Recorre el carrito del cliente. Cada numero indica los segundos que tarda un producto.
		for (int i = 0; i < cliente.getCarroCompra().length; i++) { 
			this.esperarXsegundos(cliente.getCarroCompra()[i]); 
			System.out.println("Procesado el producto " + (i + 1) + 
					" del cliente " + cliente.getNombre() + 
					" ->Tiempo: " + (System.currentTimeMillis() - timeStamp) / 1000 + "seg"); 
		}
		
		// Muestra cuando la cajera termina de procesar la compra.
		System.out.println("La cajera " + this.nombre + 
				" ha terminado de procesar " + cliente.getNombre() + 
				" en el tiempo: " + (System.currentTimeMillis() - timeStamp) / 1000 + "seg"); 
	}
	
	// Pausa la ejecucion para simular el tiempo de procesamiento de un producto.
	private void esperarXsegundos(int segundos) { 
		try {
			Thread.sleep(segundos * 1000); 
		} catch (InterruptedException ex) {
			// Si el hilo es interrumpido, se conserva el estado de interrupcion.
			Thread.currentThread().interrupt(); 
		}
	}
}
