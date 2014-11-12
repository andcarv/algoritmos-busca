package objetivo;

import regra.Regra;

public class Novidade extends FuncaoObjetivo {

	public Novidade(){
		System.out.println("Objetivo: Novidade");
	}
	
	public double calcularHeuristica(Regra regra) {
		valorHeuristica = regra.getNovelty();
		return valorHeuristica;
	}

}
