package indicadores;

import java.io.IOException;

import java.util.ArrayList;

import java.util.Iterator;




/**
 * Classe que represta o m�todo General Distance proposto por Veldhuizen
 * @author Andre
 *
 */
public class DistributionReferencePoint extends Indicador {
	
	public double[] reference; 
	
	/*
	 * O indicador deve receber como entrada a fronteia de pareto real, PFtrue
	 */
	public DistributionReferencePoint(int m, String caminho, String idExec, double[] r ){
		super(m, caminho, idExec);
		indicador = "dist";
		
		reference = r;
		
		
		
	}
	
	public double calcular() {
		return 0;
	}
	

	
	public void calcularDistribution(ArrayList<ArrayList<PontoFronteira>> fronteiras) throws IOException{
		iniciarArquivosSaida();
		
		int distribution[] = new int[10];
		int numberSolutions = 0;

		for (Iterator<ArrayList<PontoFronteira>> iterator1 = fronteiras.iterator(); iterator1.hasNext();) {
			fronteira =  iterator1.next();

			double[] distancias = new double[fronteira.size()];
			double distMax = 0;
			double distMin = Double.MAX_VALUE;

			int j = 0;
			for (Iterator<PontoFronteira> iterator = fronteira.iterator(); iterator.hasNext();) {
				PontoFronteira pf =  iterator.next();
				double dist = distanciaEuclidiana(pf.objetivos, reference);
				if(dist>=distMax)
					distMax = dist;
				if(dist<=distMin)
					distMin = dist;
				distancias[j++] = dist;
			}

			double incr = (distMax-distMin)/10;

			

			for(int i = 0; i<distancias.length; i++){
				double dist = distancias[i];
				int index = (int) Math.floor((dist - distMin)/incr);

				if(dist == distMax)
					index--;

				distribution[index]++;
				numberSolutions++;
			}
		}
		
		for(int i = 0; i<distribution.length;i++){
			int index = i*10 + 10;
			psIndGeral.println(index + "\t"+ distribution[i]);
			psIndComando.println(index + "\t"+ (distribution[i]/(double)numberSolutions));
		}

	}

	public double distanciaTchebycheff(double[] z, double[] zEstrela, double[] lambda){
		double distancia = 0;
		
		for (int i = 0; i < z.length; i++) {
			distancia = Math.max(distancia, (1.0/lambda[i]) * Math.abs(zEstrela[i] - z[i]));
		}
		
		return distancia;
	}

	
}
