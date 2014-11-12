package indicadores;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;



/**
 * Classe que represta o m�todo General Distance proposto por Veldhuizen
 * @author Andre
 *
 */
public class HistogramDTLZ2 extends Indicador {
	
	public double[] joelho, lambda; 
	
	
	public HistogramDTLZ2(int m, String caminho, String idExec){
		super(m, caminho, idExec);
		indicador = "hist_dtlz2";
	}
	
	public double calcular() {
		return 0;
	}
	

	
	public void generateHistogram(ArrayList<ArrayList<PontoFronteira>> fronteiras) throws IOException{
		iniciarArquivosSaida();
		
		HashMap<Double, Integer> histograma = new HashMap<Double, Integer>();
		
		for (Iterator<ArrayList<PontoFronteira>> iterator1 = fronteiras.iterator(); iterator1.hasNext();) {
			fronteira =  iterator1.next();

			double[] distancias = new double[fronteira.size()];

			int i = 0;
			for (Iterator<PontoFronteira> iterator = fronteira.iterator(); iterator.hasNext();) {
				PontoFronteira pf =  iterator.next();
				double sum = 0;
				for(int k = 0; k<pf.objetivos.length; k++)
					sum+=(pf.objetivos[k]*pf.objetivos[k]);
				distancias[i++] = sum - 1; 
			}
			int decimalPlace = 1;

			
			for (int j = 0; j < distancias.length; j++) {				  
				BigDecimal bd = new BigDecimal(distancias[j]);     
				bd = bd.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
				Double chave = new Double(bd.doubleValue());		     
				if(histograma.containsKey(chave)){
					Integer cont = histograma.get(chave);		
					int novoValor = cont.intValue() + 1;
					histograma.put(chave, novoValor);
				} else
					histograma.put(chave, 1);
			}
		}
		
		Set<Double> keys = histograma.keySet();
		ArrayList<Double> keysOrdenados  = new ArrayList<Double>();
		keysOrdenados.addAll(keys);
		Collections.sort(keysOrdenados);
		
		double norm = 0;
		
		for (Iterator<Double> iterator = keysOrdenados.iterator(); iterator.hasNext();) {
			Double chave = iterator.next();
			norm += histograma.get(chave);
		}
		for (Iterator<Double> iterator = keysOrdenados.iterator(); iterator.hasNext();) {
			Double chave = iterator.next();
			double value_norm = histograma.get(chave)/norm;
			psIndGeral.println(chave + "\t" + value_norm);
		}



	}
	
	

	
}
