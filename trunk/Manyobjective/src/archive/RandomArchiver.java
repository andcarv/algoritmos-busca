package archive;

import java.util.ArrayList;

import kernel.AlgoritmoAprendizado;

import solucao.Solucao;

public class RandomArchiver extends PreciseArchiver {
	
	public RandomArchiver() {
		ID = "rand";
	}

	@Override
	public void filter(ArrayList<Solucao> front, Solucao new_solution) {
		front.add(new_solution);
		double num = AlgoritmoAprendizado.random.nextDouble();
		int indice = (int) (Math.round(num*front.size())) % front.size();
		
		front.remove(indice);

	}

}
