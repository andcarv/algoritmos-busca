package objetivo;

import regra.Regra;

public class Objetivo4 extends FuncaoObjetivo {

	
	public double calcularHeuristica(Regra regra) {
		double temp = regra.matrizContigencia.getNotB();
		if(temp != 0)
			valorHeuristica = 1 - (regra.matrizContigencia.getH_NotB()/temp);
		else
			valorHeuristica = 0;
		return valorHeuristica;
	}

}
