package objetivo;

import regra.Regra;

public class Laplace extends FuncaoObjetivo {

	public Laplace(){
		System.out.println("Objetivo: Laplace");
	}
	
	public double calcularHeuristica(Regra regra) {
		valorHeuristica = regra.getLaplace();
		return valorHeuristica;
	}

}
