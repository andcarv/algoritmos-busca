package objetivo;

import regra.Regra;

public class Especificidade extends FuncaoObjetivo {

	public Especificidade(){
		System.out.println("Objetivo: Especificidade");
	}
	
	public double calcularHeuristica(Regra regra) {
		valorHeuristica = regra.getSpec();
		return valorHeuristica;
	}

}
