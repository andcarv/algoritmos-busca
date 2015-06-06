package ordenacao;

import java.util.ArrayList;
import java.util.Collections;

import regra.ComparatorEspecificidade;
import regra.ComparatorRegraSupportConfidence;
import regra.ComparatorSensitividade;
import regra.Regra;

public class Extremos {

	private static final int CSA = 0;
	private static final int SENSITIVIDADE = 1;
	private static final int ESPECIFICIDADE = 2;

	private static ArrayList<Regra> retorno = new ArrayList<Regra>();

	public static ArrayList<Regra> obterExtremo(ArrayList<Regra> regras,
			int medida, int k) {
		int acima = 0;
		int abaixo = 0;
		switch (medida) {
		case SENSITIVIDADE:
			Collections.sort(regras, new ComparatorSensitividade());
			break;
		case ESPECIFICIDADE:
			Collections.sort(regras, new ComparatorEspecificidade());
			break;
		case CSA:
			Collections.sort(regras, new ComparatorRegraSupportConfidence());
			break;
		}
		if (regras.size() >= 4) {
			if (k % 2 == 0) {
				acima = k / 2;
				abaixo = k / 2;
			} else {
				if(Math.random()<=0.5){
					acima = ((k - 1) / 2) + 1;
					abaixo = ((k - 1) / 2);
				}else{
					acima = ((k - 1) / 2);
					abaixo = ((k - 1) / 2)+1;
				}
			}

			for (int i = 0; i < acima; i++) {
				retorno.add(regras.get(i));
			}
			for (int j = 0; j < abaixo; j++) {
				retorno.add(regras.get(regras.size()-1 - j));
			}
			return retorno;
		} else {
			return regras;
		}
	}
}