package indicadores;

import java.util.ArrayList;


/**
 * Classe que represta o m�todo General Distance proposto por Veldhuizen
 * @author Andre
 *
 */
public class IGD extends Indicador {
	
	
	
	/*
	 * O indicador deve receber como entrada a fronteia de pareto real, PFtrue
	 */
	public IGD(int m, String caminho, String idExec){
		super(m, caminho, idExec);
		indicador = "igd";
	}
	
	public IGD(int m, String caminho, String idExec, ArrayList<PontoFronteira> pftrue){
		super(m, caminho, idExec);
		indicador = "igd";
		PFtrue = new ArrayList<PontoFronteira>();
		PFtrue.addAll(pftrue);
	}
	

	@Override
	public double calcular() {
		double igd = 0;
		if(objetivosMaxMin == null){
			System.err.println("Erro: N�o foi definido se cada objetivo � de maximiza��o ou minimiza��o (Executar M�todo preencherObjetivosMaxMin)");
			System.exit(0);
		}
		
		if(fronteira!=null){
			
			double soma = 0;
			//Percorre todos os pontos do conjunto de aproximacao
			for(int i = 0; i<PFtrue.size(); i++){
				PontoFronteira pontoPFTrue = PFtrue.get(i);
				double menor_distancia = menorDistanciaEuclidiana(pontoPFTrue, fronteira); 
				soma+=  menor_distancia*menor_distancia;
				
			}
			
			igd = Math.sqrt(soma)/(double) PFtrue.size();
		} else{
			System.err.println("Erro no c�lculo do IGD: Fronteira de Pareto n�o carregada.");
			System.exit(0);
			return 0;
		}	
		return igd;
	}



}
