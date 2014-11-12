package indicadores;

import java.util.ArrayList;

/**
 * Classe que represta o m�todo General Distance proposto por Veldhuizen
 * @author Andre
 *
 */
public class GD extends Indicador {
	
	
	
	/*
	 * O indicador deve receber como entrada a fronteia de pareto real, PFtrue
	 */
	public GD(int m, String caminho, String idExec){
		super(m, caminho, idExec);
		indicador = "gd";
	}
	
	public GD(int m, String caminho, String idExec, ArrayList<PontoFronteira> pftrue){
		super(m, caminho, idExec);
		indicador = "gd";
		PFtrue = new ArrayList<PontoFronteira>();
		PFtrue.addAll(pftrue);
	}
	

	@Override
	public double calcular() {
		double gd = 0;
		if(objetivosMaxMin == null){
			System.err.println("Erro: N�o foi definido se cada objetivo � de maximiza��o ou minimiza��o (Executar M�todo preencherObjetivosMaxMin)");
			System.exit(0);
		}
		
		if(fronteira!=null){
			
			double soma = 0;
			//Percorre todos os pontos do conjunto de aproximacao
			for(int i = 0; i<fronteira.size(); i++){
				PontoFronteira pontoAproximacao = fronteira.get(i);
				double menor_distancia =  menorDistanciaEuclidiana(pontoAproximacao, PFtrue); 
				soma+=menor_distancia*menor_distancia;
			}
			
			gd = Math.sqrt(soma)/(double) fronteira.size();
		} else{
			System.err.println("Erro no c�lculo do Hipervolume: Fronteira de Pareto n�o carregada.");
			System.exit(0);
			return 0;
		}	
		return gd;
	}

}
