package indicadores;

import java.util.ArrayList;

/**
 * Classe que represta o m�todo General Distance proposto por Veldhuizen
 * @author Andre
 *
 */
public class SD extends Indicador {
	
	
	
	/*
	 * Retorna a menor distância entre um ponto do conjunto de aproximação e a fronteira
	 * O indicador deve receber como entrada a fronteia de pareto real, PFtrue
	 */
	public SD(int m, String caminho, String idExec){
		super(m, caminho, idExec);
		indicador = "sd";
	}
	
	public SD(int m, String caminho, String idExec, ArrayList<PontoFronteira> pftrue){
		super(m, caminho, idExec);
		indicador = "sd";
		PFtrue = new ArrayList<PontoFronteira>();
		PFtrue.addAll(pftrue);
	}
	

	@Override
	public double calcular() {
		double sd = Double.MAX_VALUE;
		if(objetivosMaxMin == null){
			System.err.println("Erro: N�o foi definido se cada objetivo � de maximiza��o ou minimiza��o (Executar M�todo preencherObjetivosMaxMin)");
			System.exit(0);
		}
		
		if(fronteira!=null){
			
			
			//Percorre todos os pontos do conjunto de aproximacao
			for(int i = 0; i<fronteira.size(); i++){
				PontoFronteira pontoAproximacao = fronteira.get(i);
				double menor_distancia =  menorDistanciaEuclidiana(pontoAproximacao, PFtrue); 
				if(menor_distancia<sd)
					sd = menor_distancia;
			}
			
			
		} else{
			System.err.println("Erro no c�lculo do Hipervolume: Fronteira de Pareto n�o carregada.");
			System.exit(0);
			return 0;
		}	
		return sd;
	}

}
