package main;

import bd.BdInterface;

public class Main {

	public static void main(String[] args) {

		BdInterface bdInterface = new BdInterface();

		Menu menu = new Menu(bdInterface.getSession());

		menu.show();
		menu.addEmpleado();

		menu.fin();
		bdInterface.close();
	}

}
