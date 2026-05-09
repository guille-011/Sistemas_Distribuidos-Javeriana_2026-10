// ***********************************************************************************************
//								= PONTIFICIA UNIVERSIDAD JAVERIANA =
//
//	Autor: J. Corredor Franco
//	Colaboradores: Daniel Ramirez, Ana Sofia Arboleda, Guillermo Aponte y Samuel Pico
//	Fecha: 8 mayo 2026
//	Introduccion a los Sistemas Distribuidos
//
//	Descripcion: Ejecuta la version concurrente usando objetos CajeraThread. Crea dos
//	             cajeras que son hilos independientes y las inicia con start() para
//	             procesar las compras de manera paralela.
// ***********************************************************************************************

package tallerThreads;

// Programa principal de la version que extiende Thread.
public class MainThread { 
	
	public static void main(String[] args) { 
		
		// Se crean dos clientes con sus carritos de compra.
		Cliente cliente1 = new Cliente("Cliente 1", new int[] { 2, 2, 1, 5, 2, 3 }); 
		Cliente cliente2 = new Cliente("Cliente 2", new int[] { 1, 3, 5, 1, 1 }); 
		
		// Guarda el tiempo inicial para medir cuanto tarda cada hilo.
		long initialTime = System.currentTimeMillis();
		
		// Cada cajera es un hilo independiente.
		CajeraThread cajera1 = new CajeraThread("Cajera 1", cliente1, initialTime);
		CajeraThread cajera2 = new CajeraThread("Cajera 2", cliente2, initialTime);
		
		// start() inicia los hilos y ejecuta el metodo run() de cada cajera.
		cajera1.start();
		cajera2.start();
	}
}
