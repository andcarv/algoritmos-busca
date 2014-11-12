package indicadores;

import java.util.ArrayList;


/**
 * Classe que represta o m�todo General Distance proposto por Veldhuizen
 * @author Andre
 *
 */
public class Convergence extends Indicador {
	
	
	
	/*
	 * O indicador deve receber como entrada a fronteia de pareto real, PFtrue
	 */
	public Convergence(int m, String caminho, String idExec){
		super(m, caminho, idExec);
		indicador = "con";
	}
	
	public Convergence(int m, String caminho, String idExec, ArrayList<PontoFronteira> pftrue){
		super(m, caminho, idExec);
		indicador = "con";
		PFtrue = new ArrayList<PontoFronteira>();
		PFtrue.addAll(pftrue);
	}
	

	@Override
	public double calcular() {
		double convergence = Double.MAX_VALUE;
		if(objetivosMaxMin == null){
			System.err.println("Erro: N�o foi definido se cada objetivo � de maximiza��o ou minimiza��o (Executar M�todo preencherObjetivosMaxMin)");
			System.exit(0);
		}
		
		if(fronteira!=null){
			

			//Percorre todos os pontos do conjunto de aproximacao
			for(int i = 0; i<PFtrue.size(); i++){
				PontoFronteira pontoPFTrue = PFtrue.get(i);
				double menor_distancia = menorDistanciaEuclidiana(pontoPFTrue, fronteira); 
				if(menor_distancia<convergence)
					convergence = menor_distancia;
				
			}
		} else{
			System.err.println("Erro no c�lculo do IGD: Fronteira de Pareto n�o carregada.");
			System.exit(0);
			return 0;
		}	
		return convergence;
	}



}
