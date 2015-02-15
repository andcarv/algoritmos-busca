package regra;

import java.util.Comparator;

public class ComparatorSensitividade implements Comparator<Regra>{

	/**
	 * Método comparação por sensitividade 
	 * * @param Regras
	 *            a serem comparadas
	 * @return -1 menor, 0 igual, 1 maior
	 */
		@Override
	public int compare(Regra r1, Regra r2) {
			if(r1.getSens()< r2.getSens())
				return -1;
			else if(r1.getSens()> r2.getSens())
				return 1;
		return 0;
	}
}
