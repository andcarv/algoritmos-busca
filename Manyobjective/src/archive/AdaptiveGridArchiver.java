package archive;

import java.util.ArrayList;
import java.util.Iterator;

import pareto.AdaptiveGrid;
import problema.Problema;

import solucao.Solucao;
import solucao.SolucaoNumerica;

public class AdaptiveGridArchiver extends PreciseArchiver {

	private Problema problema;
	private int archiveSize;
	public int gridParts;
	
	public AdaptiveGridArchiver(Problema prob, int as) {
		problema = prob;
		ID = "ag";
		archiveSize = as;
		gridParts = 25;
		
	}
	
	@Override
	public void filter(ArrayList<Solucao> front, Solucao new_solution) {
		AdaptiveGrid  grid = new AdaptiveGrid(problema.m, gridParts, archiveSize);
		front.add(new_solution);
		for (Iterator<Solucao> iterator = front.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
			grid.add(solucao);
		}

		front.clear();
		front.addAll(grid.getAll());

	}

}
