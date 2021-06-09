
import java.io.File;
import java.util.Scanner;
import com.db4o.*;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.db4o.query.QueryComparator;

public class Principal {

	private static final String BD_APARCAMIENTO = "aparcamiento.oo";
	private static final int OPCION_SALIR = 5;
	private static Scanner teclado = new Scanner(System.in);

	public static void main(String[] args) {

		int opc;

		cargarDatosIniciales();

		do {
			opc = solicitarOpcion();
			tratarOpcion(opc);
		} while (opc != OPCION_SALIR);

	}

	private static void cargarDatosIniciales() {

		File f = new File(BD_APARCAMIENTO); // SI NO EXISTE EL FICHERO CON LA BD LO CREA
		if (!f.exists()) {
			ObjectContainer db = abrirBd();
			guardarDatos(db);

			cerrarBd(db);
		}

	}

	private static ObjectContainer abrirBd() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		ObjectContainer db = Db4oEmbedded.openFile(config, BD_APARCAMIENTO);
		return db;
	}

	private static ObjectContainer abrirBd(EmbeddedConfiguration configuracion) {

		ObjectContainer db = Db4oEmbedded.openFile(configuracion, BD_APARCAMIENTO);

		return db;
	}

	private static void guardarDatos(ObjectContainer db) {

		try {
			Distrito centro = new Distrito(1, "CENTRO");
			Distrito macarena = new Distrito(2, "MACARENA");
			Parking park1 = new Parking(1, centro, 100, 2.1);
			Parking park2 = new Parking(2, macarena, 150, 1.5);
			Parking park3 = new Parking(3, macarena, 120, 1.2);
			Parking park4 = new Parking(4, centro, 50, 2.3);

			park1.entrarCoche(new Coche("1122", "SEAT", new Persona("112345", "LUIS SUAREZ")));
			park1.entrarCoche(new Coche("3344", "FORD", new Persona("118888", "LAURA ALVAREZ")));
			park1.entrarCoche(new Coche("3355", "FORD", new Persona("11999", "ANDRES CAMACHO")));

			park2.entrarCoche(new Coche("6677", "SEAT", new Persona("112345", "PEPE PEREZ")));
			park2.entrarCoche(new Coche("5599", "FORD", new Persona("116666", "PEPA PEREZ")));
			park2.entrarCoche(new Coche("9900", "FORD", new Persona("226666", "ROSA PEREZ")));

			db.store(park1);
			db.store(park2);
			db.store(park3);
			db.store(park4);
		} catch (ParkingException e) {
			System.out.println(e.getMessage());
		}

	}

	private static void cerrarBd(ObjectContainer db) {
		db.close();
	}

	private static void tratarOpcion(int opc) {
		int codigoParking;
		String distrito;

		try {
			switch (opc) {

			case 0:
				consultarTodo("Parkings", new Parking());
				consultarTodo("Coches", new Coche());
				consultarTodo("Distrito", new Distrito());
				consultarTodo("Personas", new Persona());
				break;

			case 1:
				distrito = solicitarCadena("Introduce distrito:");
				consultaParkingsDeUnDistritoOrdenadosPorPorcentajeDeOcupacion(distrito);
				break;

			case 2:
				codigoParking = solicitarEntero("Introduce el código del parking");
				double nuevoPrecioPorHora = solicitarDouble("Introduce el nuevo precio:");

				modificarPrecioDeParking(codigoParking, nuevoPrecioPorHora);

				break;

			case 3:
				codigoParking = solicitarEntero("Introduce el código del parking");
				borrarParking(codigoParking);
				break;

			case 4:
				codigoParking = solicitarEntero("Introduce el código del parking");
				String matricula = solicitarCadena("Introduce matrícula del coche");

				cocheSaleDeParking(codigoParking, matricula);
				break;

			}
		} catch (ParkingException e) {
			System.out.println(e.getMessage());
		}

	}

	private static void consultarTodo(String msg, Object clase) {
		ObjectContainer db = abrirBd();

		ObjectSet<Object> resultado = db.queryByExample(clase);

		System.out.println(msg);
		for (Object objeto : resultado) {
			System.out.println(objeto);
		}

		db.close();

	}

	private static void consultaParkingsDeUnDistritoOrdenadosPorPorcentajeDeOcupacion(String distrito) {

		ObjectContainer db = abrirBd();

		// El nivel de activación debe ser dos para que se carguen los datos básicos y
		// el distrito
		db.ext().configure().activationDepth(2);

		ObjectSet<Parking> resultadoConsulta = db.query(new Predicate<Parking>() {

			@Override
			public boolean match(Parking parking) {
				return parking.getDistrito().getNombreDistrito().equals(distrito);
			}
		}, new QueryComparator<Parking>() {

			@Override
			public int compare(Parking parking1, Parking parking2) {
				// TODO Auto-generated method stub
				return Double.compare(parking1.getPorcentajeOcupacion(), parking2.getPorcentajeOcupacion());
			}
		});

		if (resultadoConsulta.isEmpty()) {
			System.out.println("No hay parking en el distrito " + distrito);
		} else {
			for (Parking parking : resultadoConsulta) {
				System.out.println(parking);
			}
		}

		db.close();

	}

	private static void cocheSaleDeParking(int codigoParking, String matricula) throws ParkingException {

		ObjectContainer db = abrirBd();

		// El nivel de activación debe ser cuatro, para que se carguen hasta los coches
		db.ext().configure().activationDepth(4);

		// Al borrar Coches se borran los propietarios
		// Al modificar datos del parking que no son de primer nivel, hay que activar
		// cascadeOnUpdate sobre Parking
		db.ext().configure().objectClass(Coche.class).cascadeOnDelete(true);
		db.ext().configure().objectClass(Parking.class).cascadeOnUpdate(true);

		Parking parking = findParkingByCodigo(db, codigoParking);
		if (parking == null) {
			System.out.println("No se encontro el parking con codigo " + codigoParking);
		} else {
			Coche coche = findCocheByMatricula(db, matricula);
			if (coche == null) {
				System.out.println("No existe ese coche");
			} else {

				if (!parking.getCoches().contains(coche)) {
					System.out.println(
							"No existe el coche con matricula " + matricula + "en el parking " + codigoParking);
				} else {

					parking.salirCoche(coche);
					db.store(parking);
					db.delete(coche);
					System.out.println("Salida correcta");
				}
			}
		}
		db.close();

	}

	private static Coche findCocheByMatricula(ObjectContainer db, String matricula) {

		ObjectSet<Coche> res = db.queryByExample(new Coche(matricula));
		Coche coche = null;

		if (res.size() > 0) {

			coche = res.next();
		}
		return coche;

	}

	private static void borrarParking(int codigoParking) {

		ObjectContainer db = abrirBd();

		// El nivel de activación debe ser cuatro, para que se carguen hasta los coches
		db.ext().configure().activationDepth(4);

		// Al borrar Coches se borran los propietarios
		// No debemos poner en cascade a true el Parking porque se borraría la categoría
		db.ext().configure().objectClass(Coche.class).cascadeOnDelete(true);

		Parking parking = findParkingByCodigo(db, codigoParking);
		if (parking == null) {
			System.out.println("No se encontro el parking con codigo " + codigoParking);
		} else {
			System.out.println("¿Esta seguro que desea borrar el parking " + codigoParking + " (S/N) ?");
			char respuesta = Character.toUpperCase(teclado.nextLine().charAt(0));

			if (respuesta == 'S') {
				// Borramos uno a uno los coches
				for (Coche coche : parking.getCoches()) {
					db.delete(coche);
				}
				// Después borramos el parking
				db.delete(parking);
				System.out.println("Se ha borrado el parking " + codigoParking);
			}
		}
		db.close();
	}

	private static Parking findParkingByCodigo(ObjectContainer db, int codigoParking) {

		ObjectSet<Parking> res = db.queryByExample(new Parking(codigoParking));
		Parking parking = null;

		if (res.size() > 0) {

			parking = res.next();
		}
		return parking;

	}

	private static void modificarPrecioDeParking(int codigoParking, double nuevoPrecioPorHora) throws ParkingException {

		ObjectContainer db = abrirBd();

		// El nivel de activación debe ser uno para que solo se carguen los datos
		// básicos
		db.ext().configure().activationDepth(1);

		Parking parking = findParkingByCodigo(db, codigoParking);
		if (parking == null) {
			System.out.println("No se encontro el parking con codigo " + codigoParking);
		} else {
			parking.setPrecioPorHora(nuevoPrecioPorHora);
			db.store(parking);
			System.out.println("Se ha modificaco correctamente el precio por hora");
		}
		db.close();

	}

	private static int solicitarOpcion() {
		int opc;

		System.out.println("0.Consultar todo");
		System.out.println("1.Consulta de  parkings de un distrito ordenadas por porcentaje de ocupación");
		System.out.println("2.Modificar el precio por hora de un parking");
		System.out.println("3.Borrar un parking");
		System.out.println("4.Coche sale de un parking");
		System.out.println("5.Salir");
		do {
			System.out.println("Introduce opcion");
			opc = Integer.parseInt(teclado.nextLine());
		} while (opc < 0 || opc > OPCION_SALIR);
		return opc;
	}

	private static double solicitarDouble(String msg) {
		double numero;

		System.out.println(msg);
		numero = Double.parseDouble(teclado.nextLine());

		return numero;
	}

	private static int solicitarEntero(String msg) {
		int numero;

		System.out.println(msg);
		numero = Integer.parseInt(teclado.nextLine());

		return numero;
	}

	private static String solicitarCadena(String msg) {
		String nombre;
		System.out.println(msg);
		nombre = teclado.nextLine();
		return nombre;
	}

}
