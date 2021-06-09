import java.util.HashSet;
import java.util.LinkedList;


public class Parking {

	private int codigo;
	private Distrito distrito;
	private int plazas;
	private double precioPorHora;
	private LinkedList<Coche> coches;
	
	public Parking() {
		
	}
	
	public Parking(int codigo, Distrito distrito, int plazas, double precio) throws ParkingException {
		super();
		if (plazas <=0)
			throw new ParkingException("Número de plazas incorrecto " + plazas);
		this.plazas=plazas;
		this.codigo = codigo;
		this.distrito = distrito;

		setPrecioPorHora(precio);
		coches =new LinkedList<Coche>();
	}
	
	
	// Constructor para patrón
	public Parking(int codigoParking) {
		this.codigo=codigoParking;
	}



	public int getCodigo() {
		return codigo;
	}



	public Distrito getDistrito() {
		return distrito;
	}



	public void setDistrito(Distrito distrito) {
		this.distrito = distrito;
	}



	public int getPlazas() {
		return plazas;
	}





	public double getPrecioPorHora() {
		return precioPorHora;
	}



	public void setPrecioPorHora(double precio) throws ParkingException {
		if ( precio <= 0) {
			throw new ParkingException("Precio incorrecto " + precio);
		}
		this.precioPorHora = precio;
	}



	public void entrarCoche( Coche coche) throws ParkingException {
		if ( coches.size() == plazas) {
			throw new ParkingException("El coche " + coche.getMatricula() + " no puede entrar, parking completo");
		}
		coches.add(coche);
	}
	
	
	
	@Override
	public String toString() {
		return "Parking " + codigo + ", distrito=" + distrito + ", plazas=" + plazas + ", precio=" + precioPorHora
				+ "]" + " Porcentaje de ocupación " + coches.size()*100.0/ plazas + "%";
	}



	/**
	 * Devuelve true si se puede borrar el coche y false en caso contrario
	 * @param coche
	 * @return
	 * @throws ParkingException 
	 */
	public boolean salirCoche(Coche coche) throws ParkingException {
		
		
		return coches.remove(coche);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + codigo;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parking other = (Parking) obj;
		if (codigo != other.codigo)
			return false;
		return true;
	}



	public LinkedList<Coche> getCoches() {
		return coches;
	}


	


	public double getPorcentajeOcupacion() {
		
		return coches.size()*100.0/ plazas ;
	}
	
	
	
}
