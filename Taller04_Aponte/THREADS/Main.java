// ***********************************************************************************************
//								= PONTIFICIA UNIVERSIDAD JAVERIANA =
//
//	Autor: J. Corredor Franco
//	Colaboradores: Daniel Ramirez, Ana Sofia Arboleda, Guillermo Aponte y Samuel Pico
//	Fecha: 8 mayo 2026
//	Introduccion a los Sistemas Distribuidos
//
//	Descripcion: Ejecuta la version secuencial del programa. Crea dos clientes y dos
//	             cajeras, pero procesa primero una compra completa y luego la otra,
//	             permitiendo comparar este comportamiento con las versiones con hilos.
// ***********************************************************************************************

package tallerThreads;

// Programa principal de la version secuencial.
public class Main { 
	
	public static void main(String[] args) { 
		
		// Se crean dos clientes con sus productos.
		// Cada numero representa los segundos que tarda en procesarse un producto.
		Cliente cliente1 = new Cliente("Cliente 1", new int[] { 2, 2, 1, 5, 2, 3 }); 
		Cliente cliente2 = new Cliente("Cliente 2", new int[] { 1, 3, 5, 1, 1 }); 
		
		// Se crean dos cajeras.
		Cajera cajera1 = new Cajera("Cajera 1"); 
		Cajera cajera2 = new Cajera("Cajera 2"); 
		
		// Guarda el tiempo inicial para calcular cuanto tarda el proceso.
		long initialTime = System.currentTimeMillis();
		
		// Las compras se procesan una despues de la otra.
		cajera1.procesarCompra(cliente1, initialTime);
		cajera2.procesarCompra(cliente2, initialTime);
	}
}
