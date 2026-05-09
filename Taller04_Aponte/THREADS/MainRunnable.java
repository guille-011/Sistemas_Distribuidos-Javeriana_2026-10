// ***********************************************************************************************
//								= PONTIFICIA UNIVERSIDAD JAVERIANA =
//
//	Autor: J. Corredor Franco
//	Colaboradores: Daniel Ramirez, Ana Sofia Arboleda, Guillermo Aponte y Samuel Pico
//	Fecha: 8 mayo 2026
//	Introduccion a los Sistemas Distribuidos
//
//	Descripcion: Ejecuta la version concurrente usando la interfaz Runnable. Cada compra
//	             se convierte en una tarea que se entrega a un hilo, permitiendo que dos
//	             cajeras procesen clientes al mismo tiempo.
// ***********************************************************************************************

package tallerThreads;

// Clase que usa Runnable para procesar compras en hilos.
public class MainRunnable implements Runnable{  
	
	// Datos que necesita cada tarea para procesar una compra.
	private Cliente cliente;
	private Cajera cajera;
	private long initialTime;
	
	// Constructor: guarda el cliente, la cajera y el tiempo inicial.
	public MainRunnable (Cliente cliente, Cajera cajera, long initialTime){
		this.cajera = cajera;
		this.cliente = cliente;
		this.initialTime = initialTime;
	}

	// Metodo principal de la version con Runnable.
	public static void main(String[] args) { 
		
		// Se crean dos clientes con sus carritos de compra.
		Cliente cliente1 = new Cliente("Cliente 1", new int[] { 2, 2, 1, 5, 2, 3 }); 
		Cliente cliente2 = new Cliente("Cliente 2", new int[] { 1, 3, 5, 1, 1 }); 
		
		// Se crean las cajeras que atenderan a los clientes.
		Cajera cajera1 = new Cajera("Cajera 1"); 
		Cajera cajera2 = new Cajera("Cajera 2"); 
		
		// Tiempo inicial compartido para medir la ejecucion.
		long initialTime = System.currentTimeMillis(); 
		
		// Cada Runnable representa una compra que se puede ejecutar en paralelo.
		Runnable proceso1 = new MainRunnable(cliente1, cajera1, initialTime); 
		Runnable proceso2 = new MainRunnable(cliente2, cajera2, initialTime); 
		
		// Se crean y arrancan dos hilos usando los Runnable anteriores.
		new Thread(proceso1).start(); 
		new Thread(proceso2).start(); 
	}

	// Codigo que ejecuta cada hilo.
	@Override
	public void run() {  
		this.cajera.procesarCompra(this.cliente, this.initialTime); 
	}
}
