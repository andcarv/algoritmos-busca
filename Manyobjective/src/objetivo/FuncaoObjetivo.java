package objetivo;

import solucao.Solucao;

public abstract class FuncaoObjetivo {
	
	public double valorObjetivo;
	
	public abstract double calcularObjetivo(Solucao solucao);
	
	public String toString(){
		return valorObjetivo+"";
	}
	
}
