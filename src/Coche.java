
public class Coche {

	private String matricula;
	private String marca;
	private Persona propietario;
	public Coche(String matricula, String marca, Persona propietario) {
		super();
		this.matricula = matricula;
		this.marca = marca;
		this.propietario = propietario;
	}
	
	public Coche(String matricula) {
		this.matricula=matricula;
	}

	public Coche() {
		// TODO Auto-generated constructor stub
	}

	public String getMatricula() {
		return matricula;
	}
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	public Persona getPropietario() {
		return propietario;
	}
	public void setPropietario(Persona duenno) {
		this.propietario = duenno;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matricula == null) ? 0 : matricula.hashCode());
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
		Coche other = (Coche) obj;
		if (matricula == null) {
			if (other.matricula != null)
				return false;
		} else if (!matricula.equals(other.matricula))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Coche [matricula=" + matricula + ", marca=" + marca + ", propietario=" + propietario + "]";
	}
	
	
	
}
