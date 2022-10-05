package main;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Scanner;

import org.hibernate.Session;
import org.hibernate.query.Query;

import bd.Departamentos;
import bd.Empleados;

public class Menu {
	private Session session;
	private Scanner entrada;

	public Menu(Session session) {
		this.session = session;
		entrada = new Scanner(System.in);
	}

	public void show() {
		System.out.println("Menu");
	}

	public int askInteger(String message) {
		System.out.print(message);
		return entrada.nextInt();
	}

	public String askString(String message) {
		System.out.print(message);
		return entrada.nextLine();
	}

	public BigDecimal askBigDecimal(String message) {
		System.out.println(message);
		return entrada.nextBigDecimal();
	}

	public Departamentos getDepartamentoByName(String nombre) {
		Query<Departamentos> query = (Query<Departamentos>) session
				.createQuery("FROM Departamentos WHERE nombre = :nombre").setParameter("nombre", nombre);
		ArrayList<Departamentos> list = (ArrayList<Departamentos>) query.list();

		for (int i = 0; i < list.size(); i++) {
			System.out.println("  " + i + ". " + list.get(i));
		}

		int numDepartamentoOrdinal = askInteger("Elige departamento: ");

		return list.get(numDepartamentoOrdinal);
	}

	public int addDepartamento() {
		int numDepartamento = askInteger("Numero de departamento: ");
		entrada.nextLine();
		String nombre = askString("Nombre: ");
		String localidad = askString("Localidad: ");

		Departamentos nuevoDepartamento = new Departamentos(numDepartamento, nombre, localidad);
		handleTransaction(nuevoDepartamento, "s");

		return numDepartamento;
	}

	public int addEmpleado() {
		// Campos del empleado
		// NUM_EMPLEADO
		// NOMBRE_EMPLEADO
		// PUESTO
		// NUM_JEFE
		// FECHA_ALTA
		// SALARIO
		// COMISION
		// NUM_DEPARTAMENTO

		String nombreDepartamento = askString("Nombre departamento: ");

		Departamentos departamento = getDepartamentoByName(nombreDepartamento);

		int numEmpleado = askInteger("Numero del empleado: ");
		entrada.nextLine();
		String nombre = askString("Nombre: ");
		String puesto = askString("Puesto: ");
		int numJefe = askInteger("Numero de jefe: ");
		Date fechaAlta = new Date(new java.util.Date().getTime());
		BigDecimal salario = askBigDecimal("Salario: ");
		BigDecimal comision = askBigDecimal("Comision: ");

		Empleados nuevoEmpleado = new Empleados(numEmpleado, departamento, nombre, puesto, numJefe, fechaAlta, salario,
				comision);

		handleTransaction(nuevoEmpleado, "s");

		return numEmpleado;
	}

	public void handleTransaction(Object entidad, String type) {
		session.beginTransaction();
		switch (type) {
		case "s":
			save(entidad);
			break;

		case "d":
			delete(entidad);
			break;

		case "u":
			update(entidad);
			break;

		default:
			break;
		}
		session.getTransaction().commit();
	}

	public void save(Object entidad) {
		session.save(entidad);
	}

	public void delete(Object entidad) {
		session.delete(entidad);
	}

	public void update(Object entidad) {
		session.update(entidad);
	}

	public void fin() {
		System.out.println("FIN");
	}
}
