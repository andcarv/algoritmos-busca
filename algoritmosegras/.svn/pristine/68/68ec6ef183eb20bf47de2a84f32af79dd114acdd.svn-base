package regra;

import java.util.Comparator;

public class ComparatorRegraSupportConfidence implements Comparator<Regra> {

	/**
	 * Método que compara duas regras inicialmente através do valor de Confiança
	 * Em caso de empate é comparado o valor de suporte
	 * 
	 * @param 
	 *      Regras a serem comparadas     
	 * @return -1 menor, 0 igual, 1 maior
	 */

	public int compare(Regra r1, Regra r2) {
		if (r1.getConfidence() < r2.getConfidence()) {
			return -1;
		} else if (r1.getConfidence() > r2.getConfidence()) {
			return 1;
		} else if (r1.getSup() < r2.getSup()) {
			return -1;
		}else if(r1.corpo.length < r2.corpo.length){
			return -1;
		}else if(r1.corpo.length > r2.corpo.length){
			return 1;
		}
		return 0;
	}
}
