// ***********************************************************************************************
//								= PONTIFICIA UNIVERSIDAD JAVERIANA =
//
//	Autor: J. Corredor Franco
//	Colaboradores: Daniel Ramirez, Ana Sofia Arboleda, Guillermo Aponte y Samuel Pico
//	Fecha: 8 mayo 2026
//	Introduccion a los Sistemas Distribuidos
//
//	Descripcion: Define una cajera como un hilo independiente extendiendo la clase Thread.
//	             Permite procesar la compra de un cliente en paralelo con otras cajeras,
//	             mostrando los tiempos de inicio, avance y finalizacion.
// ***********************************************************************************************

package tallerThreads;

// Clase que representa una cajera ejecutandose como hilo.
public class CajeraThread extends Thread { 
	
	// Datos necesarios para que la cajera procese la compra.
	private String nombre;
	private Cliente cliente;
	private long initialTime;
	
	// Constructor: recibe la cajera, el cliente y el tiempo inicial.
	public CajeraThread(String nombre, Cliente cliente, long initialTime) {
		this.nombre = nombre; 
		this.cliente = cliente; 
		this.initialTime = initialTime; 
	}
	
	// Metodo que se ejecuta automaticamente al llamar start().
	@Override
	public void run() { 
		// Muestra el inicio del procesamiento de la compra.
		System.out.println("La cajera " + this.nombre + 
				" comienza a procesar la compra del cliente " + cliente.getNombre() + 
				" en el tiempo: " + (System.currentTimeMillis() - this.initialTime) / 1000 + "seg");
		
		// Procesa cada producto del carrito del cliente.
		for (int i = 0; i < cliente.getCarroCompra().length; i++) { 
			this.esperarXsegundos(cliente.getCarroCompra()[i]); 
			System.out.println("Procesado el producto " + (i + 1) + 
					" del cliente " + cliente.getNombre() + 
					" ->Tiempo: " + (System.currentTimeMillis() - this.initialTime) / 1000 + "seg");
		}
		
		// Muestra cuando la cajera termina su trabajo.
		System.out.println("La cajera " + this.nombre + 
				" ha terminado de procesar " + cliente.getNombre() + 
				" en el tiempo: " + (System.currentTimeMillis() - this.initialTime) / 1000 + "seg"); 
	}
	
	// Pausa el hilo para simular el tiempo que tarda cada producto.
	private void esperarXsegundos(int segundos) { 
		try {
			Thread.sleep(segundos * 1000); 
		} catch (InterruptedException ex) {
			// Mantiene marcada la interrupcion si ocurre un problema con el hilo.
			Thread.currentThread().interrupt(); 
		}
	}
}
