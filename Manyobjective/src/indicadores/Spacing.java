package indicadores;


/**
 * Classe que calcula o valor da medida Spread proposta por Veldhuizen
 * @author Andre
 *
 */
public class Spacing extends Indicador {
	
	//Array que cont�m para cada ponto da fronteira a menor dist�ncia euclidiana em rela��o � outro pronto da fronteira.
	public double[] menores_distancias = null;
	public double media;
	
	public Spacing(int m, String caminho, String idExec){
		super(m, caminho, idExec);
		indicador = "spacing";
	}
	
	@Override
	public double calcular() {
		double spread = 0;
		if(objetivosMaxMin == null){
			System.err.println("Erro: N�o foi definido se cada objetivo � de maximiza��o ou minimiza��o (Executar M�todo preencherObjetivosMaxMin)");
			System.exit(0);
		}
		
		if(fronteira!=null){
			media = 0;
			menores_distancias  = new double[fronteira.size()];
			double k = 0;
			//Cacula para cada ponto a menor distancia euclidiana em rela��o � outro ponto da fronteira
			for(int i = 0; i<fronteira.size(); i++){
				PontoFronteira ponto1 = fronteira.get(i);
				menores_distancias[i] = Double.MAX_VALUE;
				for(int j = 0; j<fronteira.size();j++){
					if(i!=j){
						PontoFronteira ponto2 = fronteira.get(j);
						double distancia = distanciaEuclidiana(ponto1.objetivos, ponto2.objetivos);
						menores_distancias[i] = Math.min(menores_distancias[i], distancia);
						if(j>i){
							k++;
							//Calcula a media das dist�ncias euclidianas
							media+= distancia;
						}
					}
				}
			}
			media = media/k;
			
			//Efetua o c�lculo do spread
			for(int i = 0; i<fronteira.size()-1; i++){
				double di = menores_distancias[i];
				spread+= Math.abs(di-media)/fronteira.size();
			}
		} else{
			System.err.println("Erro no c�lculo do Hipervolume: Fronteira de Pareto n�o carregada.");
			System.exit(0);
			return 0;
		}
		return spread;
	}
	


}
