package main;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Scanner;

import javax.persistence.TypedQuery;

import org.hibernate.Session;

import bd.Departamentos;
import bd.Empleados;

public class Menu {
	private Session session;
	private Scanner entrada;

	public Menu(Session session) {
		this.session = session;
		this.entrada = new Scanner(System.in);
	}

	// Funciones comunes

	public void close() {
		System.out.println("FIN");
	}

	public void show() {
		while (true) {
			System.out.println("Menu");
			System.out.println("1. Añadir un departamento");
			System.out.println("2. Añadir un empleado");
			System.out.println("3. Añadir un departamento con empleados");
			System.out.println("4. Modificar un departamento");
			System.out.println("5. Modificar un empleado");
			System.out.println("6. Eliminar un departamento");
			System.out.println("7. Eliminar un empleado");
			System.out.println("8. Mostrar todos los departamentos");
			System.out.println("9. Mostrar los empleados de un departamento");
			System.out.println("10. Mostrar todos los empleados");

			int option = askInteger("Elige opción del menu: ");
			switch (option) {
			case 1:
				addDepartamento();
				break;

			case 2:
				addEmpleado();
				break;

			case 3:
				addDepartamentoConEmpleados();
				break;

			case 4:
				updateDepartamento();
				break;

			case 5:
				updateEmpleado();
				break;

			case 6:
				deleteDepartamento();
				break;

			case 7:
				deleteEmpleado();
				break;

			case 8:
				showDepartamentos();
				break;

			case 9:
				showEmpleadosDepartamento();
				break;

			case 10:
				showEmpleados();
				break;

			default:
				close();
				return;
			}
		}

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
		System.out.print(message);
		return entrada.nextBigDecimal();
	}

	public <T> List<T> getQueryList(String queryString, Class<T> queryType) {
		TypedQuery<T> query = session.createQuery(queryString, queryType);
		return query.getResultList();
	}

	public <T, K> List<T> getQueryList(String queryString, K parameter, Class<T> queryType) {
		TypedQuery<T> query = session.createQuery(queryString + " :parameter", queryType);
		query.setParameter("parameter", parameter);
		return query.getResultList();
	}

	public <T> void showOptions(List<T> list) {
		for (int i = 0; i < list.size(); i++) {
			System.out.println("  " + i + ". " + list.get(i));
		}
	}

	public <T> T handleTransaction(T entidad, String type) {
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
		return entidad;
	}

	public <T> void save(T entidad) {
		session.save(entidad);
	}

	public <T> void delete(T entidad) {
		session.delete(entidad);
	}

	public <T> void update(T entidad) {
		session.update(entidad);
	}

	public Departamentos getDepartamentoByName(String nombre) {
		List<Departamentos> departamentos = getQueryList("FROM Departamentos WHERE nombre=", nombre,
				Departamentos.class);

		showOptions(departamentos);
		int numDepartamentoOrdinal = askInteger("Elige departamento: ");

		return departamentos.get(numDepartamentoOrdinal);
	}

	public Departamentos createDepartamento() {
		int numDepartamento = askInteger("Numero de departamento: ");
		entrada.nextLine();
		String nombre = askString("Nombre: ");
		String localidad = askString("Localidad: ");

		return new Departamentos(numDepartamento, nombre, localidad);
	}

	public void addEmpleado(Departamentos departamento, boolean multiple) {
		// Campos del empleado
		// NUM_EMPLEADO
		// NOMBRE_EMPLEADO
		// PUESTO
		// NUM_JEFE
		// FECHA_ALTA
		// SALARIO
		// COMISION
		// NUM_DEPARTAMENTO

		// Si multiple == true, pedir más de un empleado hasta que se teclee -1
		do {
			int numEmpleado = askInteger("Numero del empleado: ");
			if (numEmpleado == -1) {
				return;
			}
			entrada.nextLine();
			String nombre = askString("Nombre: ");
			String puesto = askString("Puesto: ");
			int numJefe = askInteger("Numero de jefe: ");
			Date fechaAlta = new Date(new java.util.Date().getTime());
			BigDecimal salario = askBigDecimal("Salario: ");
			BigDecimal comision = askBigDecimal("Comision: ");

			Empleados nuevoEmpleado = new Empleados(numEmpleado, departamento, nombre, puesto, numJefe, fechaAlta,
					salario, comision);

			handleTransaction(nuevoEmpleado, "s");
		} while (multiple == true);

	}

	// ***Funciones comunes***

	// 1
	public Departamentos addDepartamento() {
		return handleTransaction(createDepartamento(), "s");
	}

	// 2
	public void addEmpleado() {
		String nombreDepartamento = askString("Nombre departamento: ");

		// Segundo parametro boolean es responsable de agregar varios empleados
		addEmpleado(getDepartamentoByName(nombreDepartamento), false);
	}

	// 3
	public void addDepartamentoConEmpleados() {
		// Segundo parametro boolean es responsable de agregar varios empleados
		addEmpleado(addDepartamento(), true);
	}

	// 4
	public Departamentos updateDepartamento() {
		int numDepartamento = askInteger("Numero de departamento: ");
		entrada.nextLine();
		String nombre = askString("Nombre: ");
		String localidad = askString("Localidad: ");

		Departamentos departamento = session.get(Departamentos.class, numDepartamento);

		departamento.setNombre(nombre);
		departamento.setLocalidad(localidad);

		return handleTransaction(departamento, "u");
	}

	// 5
	public Empleados updateEmpleado() {
		int numEmpleado = askInteger("Numero del empleado: ");
		entrada.nextLine();
		String nombre = askString("Nombre: ");
		String puesto = askString("Puesto: ");
		int numJefe = askInteger("Numero de jefe: ");
		BigDecimal salario = askBigDecimal("Salario: ");
		BigDecimal comision = askBigDecimal("Comision: ");

		Empleados empleado = session.get(Empleados.class, numEmpleado);

		empleado.setNombreEmpleado(nombre);
		empleado.setPuesto(puesto);
		empleado.setNumJefe(numJefe);
		empleado.setSalario(salario);
		empleado.setComision(comision);

		return handleTransaction(empleado, "u");
	}

	// 6
	public Departamentos deleteDepartamento() {
		int numDepartamento = askInteger("Numero de departamento: ");
		entrada.nextLine();

		Departamentos departamento = session.get(Departamentos.class, numDepartamento);

		List<Empleados> empleados = getQueryList("FROM Empleados WHERE num_departamento = ", numDepartamento,
				Empleados.class);

		for (Empleados empleado : empleados) {
			handleTransaction(empleado, "d");
		}

		return handleTransaction(departamento, "d");
	}

	// 7
	public Empleados deleteEmpleado() {
		int numEmpleado = askInteger("Numero del empleado: ");
		entrada.nextLine();

		return handleTransaction(session.get(Empleados.class, numEmpleado), "d");
	}

	// 8
	public void showDepartamentos() {
		List<Departamentos> departamentos = getQueryList("FROM Departamentos", Departamentos.class);
		showOptions(departamentos);
	}

	// 9
	public void showEmpleadosDepartamento() {
		int numDepartamento = askInteger("Numero de departamento: ");
		entrada.nextLine();
		List<Empleados> empleados = getQueryList("FROM Empleados WHERE num_departamento =", numDepartamento,
				Empleados.class);
		showOptions(empleados);
	}

	// 10
	public void showEmpleados() {
		List<Empleados> empleados = getQueryList("FROM Empleados", Empleados.class);
		showOptions(empleados);
	}
}
