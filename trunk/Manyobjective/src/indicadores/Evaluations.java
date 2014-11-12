package indicadores;

import problema.Problema;

/**
 * Classe que represta o m�todo General Distance proposto por Veldhuizen
 * @author Andre
 *
 */
public class Evaluations extends Indicador {
	public Problema problem;
	
	
	/*
	 * O indicador deve receber como entrada a fronteia de pareto real, PFtrue
	 */
	public Evaluations(int m, String caminho, String idExec, Problema prob){
		super(m, caminho, idExec);
		this.problem = prob;
		indicador = "eval";
	}
	
	

	@Override
	public double calcular() {	
		return problem.avaliacoes;
	}

}
