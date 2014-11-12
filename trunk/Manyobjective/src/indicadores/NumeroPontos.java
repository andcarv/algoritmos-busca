package indicadores;



/**
 * Classe que represta o m�todo General Distance proposto por Veldhuizen
 * @author Andre
 *
 */
public class NumeroPontos extends Indicador {
	
	
	
	/*
	 * O indicador deve receber como entrada a fronteia de pareto real, PFtrue
	 */
	public NumeroPontos(int m, String caminho, String idExec){
		super(m, caminho, idExec);
		indicador = "np";
	}
	
	
	

	@Override
	public double calcular() {
		double np = 0;
		if(objetivosMaxMin == null){
			System.err.println("Erro: N�o foi definido se cada objetivo � de maximiza��o ou minimiza��o (Executar M�todo preencherObjetivosMaxMin)");
			System.exit(0);
		}
		
		if(fronteira!=null){
			np = fronteira.size();
			
		} else{
			System.err.println("Erro no c�lculo do Hipervolume: Fronteira de Pareto n�o carregada.");
			System.exit(0);
			return 0;
		}	
		return np;
	}

}
