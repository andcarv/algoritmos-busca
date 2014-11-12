package objetivo;

import regra.Regra;

public class ConfiancaNegativa extends FuncaoObjetivo {

	@Override
	public double calcularHeuristica(Regra regra) {
		return regra.getConfidence();
	}

}
