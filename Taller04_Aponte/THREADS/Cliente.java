// ***********************************************************************************************
//								= PONTIFICIA UNIVERSIDAD JAVERIANA =
//
//	Autor: J. Corredor Franco
//	Colaboradores: Daniel Ramirez, Ana Sofia Arboleda, Guillermo Aponte y Samuel Pico
//	Fecha: 8 mayo 2026
//	Introduccion a los Sistemas Distribuidos
//
//	Descripcion: Representa a un cliente del supermercado. Guarda su nombre y el arreglo
//	             de tiempos de su carrito de compras, donde cada valor indica cuanto tarda
//	             en procesarse un producto.
// ***********************************************************************************************

package tallerThreads;

// Clase que representa a un cliente y su carrito de compras.
public class Cliente { 
	
	// Nombre del cliente.
	private String nombre; 
	// Arreglo con los tiempos de procesamiento de cada producto.
	private int[] carroCompra; 
	
	// Constructor: guarda el nombre y el carrito del cliente.
	public Cliente(String nombre, int[] carroCompra) {
		this.nombre = nombre; 
		this.carroCompra = carroCompra; 
	}
	
	// Retorna el nombre del cliente.
	public String getNombre() { 
		return nombre;
	}
	
	// Retorna el carrito de compra del cliente.
	public int[] getCarroCompra() { 
		return carroCompra;
	}
}
