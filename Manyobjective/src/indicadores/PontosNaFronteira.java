package indicadores;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Classe que represta o m�todo General Distance proposto por Veldhuizen
 * @author Andre
 *
 */
public class PontosNaFronteira extends Indicador {
	
	
	
	/*
	 * O indicador deve receber como entrada a fronteia de pareto real, PFtrue
	 */
	public PontosNaFronteira(int m, String caminho, String idExec){
		super(m, caminho, idExec);
		indicador = "pnf";
	}
	
	public PontosNaFronteira(int m, String caminho, String idExec, ArrayList<PontoFronteira> pftrue){
		super(m, caminho, idExec);
		indicador = "pnf";
		PFtrue = new ArrayList<PontoFronteira>();
		PFtrue.addAll(pftrue);
	}
	

	@Override
	public double calcular() {
		int pnf = 0;
		if(objetivosMaxMin == null){
			System.err.println("Erro: N�o foi definido se cada objetivo � de maximiza��o ou minimiza��o (Executar M�todo preencherObjetivosMaxMin)");
			System.exit(0);
		}
		
		if(fronteira!=null){
			//Percorre todos os pontos do conjunto de aproximacao
			int decimalPlace = 3;
			for(int i = 0; i<fronteira.size(); i++){
 				PontoFronteira pontoAproximacao = fronteira.get(i);
 				for (Iterator<PontoFronteira> iterator = PFtrue.iterator(); iterator
						.hasNext();) {
					PontoFronteira pontoTrue = (PontoFronteira) iterator.next();
					int iguais = 0;
					for(int j = 0;  j<m; j++){
						BigDecimal bdAprox = new BigDecimal(pontoAproximacao.objetivos[j]);     
						bdAprox = bdAprox.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
						
						BigDecimal bdTrue = new BigDecimal(pontoTrue.objetivos[j]);     
						bdTrue = bdTrue.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
						
						if(bdAprox.equals(bdTrue))
							iguais++;
					}
					

					if(iguais==m){
						pnf++;
						break;
					}
					
				}
 				
 			
			}	
		} else{
			System.err.println("Erro no c�lculo do Indicador: Fronteira de Pareto n�o carregada.");
			System.exit(0);
			return 0;
		}	
		return pnf;
	}

}
