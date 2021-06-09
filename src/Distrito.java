
public class Distrito {
	
	private int codigo;
	private String nombreDistrito;
	public Distrito(int codigo, String nombreDistrito) {
		super();
		this.codigo = codigo;
		this.nombreDistrito = nombreDistrito;
	}
	public Distrito() {
		// TODO Auto-generated constructor stub
	}
	public int getCodigo() {
		return codigo;
	}
	public String getNombreDistrito() {
		return nombreDistrito;
	}
	
	@Override
	public String toString() {
		return "Distrito [codigo=" + codigo + ", nombreDistrito=" + nombreDistrito + "]";
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
		Distrito other = (Distrito) obj;
		if (codigo != other.codigo)
			return false;
		return true;
	}
	
	
	
	
}
