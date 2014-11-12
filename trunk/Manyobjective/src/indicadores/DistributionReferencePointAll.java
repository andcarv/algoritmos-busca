package indicadores;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;



public class DistributionReferencePointAll extends Indicador {
	
	public double[] reference; 
	
	public DistributionReferencePointAll(int m, String caminho, String idExec, double[] r, String[] maxmim){
		super(m, caminho, idExec);
		
		indicador = "dist_all";
		
		reference = r;
		
		preencherObjetivosMaxMin(maxmim);
		
	}
	
	public double calcular() {
		return 0;
	}
	
	
	
	
	
	public void calcularDistanceReferenceAll(String dirEntrada, String dirSaida, String problema, int objetivo, String[] algoritmos, int exec, String metodo[]) throws IOException{
		
		Object[] all_fronts = new Object[algoritmos.length];


		for (int j = 0; j < algoritmos.length; j++) {

			String arq = dirEntrada + "resultados/" + metodo[j] + "/" + problema + "/" + objetivo + "/" +			
			algoritmos[j] + "/" + metodo[j] + "_" + problema + "_" + objetivo + "_" + algoritmos[j] + "_fronteira.txt";	
		
			System.out.println(arq);

			ArrayList<ArrayList<PontoFronteira>> fronteiras_i = new ArrayList<ArrayList<PontoFronteira>>();
			
			carregarArrayList(arq, fronteiras_i);
			
			all_fronts[j] = fronteiras_i;
			
			
		}  
		
		int distribution[][] = new int[all_fronts.length][10];
		
		for(int j = 0; j<exec; j++){
			ArrayList<ArrayList<PontoFronteira>> fronteiras_j = new ArrayList<ArrayList<PontoFronteira>>();
			for(int i = 0; i<all_fronts.length; i++){
				@SuppressWarnings("unchecked")
				ArrayList<ArrayList<PontoFronteira>> fronteiras_alg_i = ((ArrayList<ArrayList<PontoFronteira>>) all_fronts[i]);
				fronteiras_j.add(fronteiras_alg_i.get(j));
			}
			calcularDistribution(fronteiras_j, distribution);
		}
		
		int[] numberSolutions = new int[distribution.length];
		
		for(int k = 0; k<distribution.length; k++){
			int distribuition_k[] = distribution[k];
			for(int l = 0; l<distribuition_k.length;l++){
				numberSolutions[k] += distribuition_k[l];
			}
		}
		
		
		for(int k = 0; k<distribution[0].length;k++){
			int index = k*10 + 10;
			psIndGeral.print(index + "\t");
			psIndComando.print(index + "\t");
			for(int l = 0; l<distribution.length;l++){
				double dist = distribution[l][k];
				//System.out.print(index + "\t"+ dist + "\t");
				psIndGeral.print(dist + "\t");
				psIndComando.print((dist/(double)numberSolutions[l])+ "\t");
			}
			psIndGeral.println();
			//System.out.println();
			psIndComando.println();
		}
		
		
		

	}
	
	public void calcularDistribution(ArrayList<ArrayList<PontoFronteira>> fronteiras, int distribution[][]) throws IOException{
		iniciarArquivosSaida();
		
		 
		ArrayList<double[]> distancias = new ArrayList<double[]>();
		
		//int[] numberSolutions = new int[fronteiras.size()];
		
		double distMax = 0;
		double distMin = Double.MAX_VALUE;
		
		for (Iterator<ArrayList<PontoFronteira>> iterator1 = fronteiras.iterator(); iterator1.hasNext();) {
			fronteira =  iterator1.next();
			
			double[] distancias_i = new double[fronteira.size()];

			int j = 0;
			for (Iterator<PontoFronteira> iterator = fronteira.iterator(); iterator.hasNext();) {
				
				PontoFronteira pf =  iterator.next();
				double dist = distanciaEuclidiana(pf.objetivos, reference);
				if(dist>=distMax)
					distMax = dist;
				if(dist<=distMin)
					distMin = dist;
				
				distancias_i[j++] = dist;
				//numberSolutions[i]++;
			}
			
			distancias.add(distancias_i);
			

		}
		
		double incr = (distMax-distMin)/10;
		
		

		for(int k =0; k<distancias.size();k++){
			double[] distancia_k = distancias.get(k);
			int[] distribution_k = distribution[k];
			for(int l = 0; l<distancia_k.length; l++){
				double dist = distancia_k[l];
				int index = (int) Math.floor((dist - distMin)/incr);

				if(dist == distMax)
					index--;

				distribution_k[index]++;
			}
		}
		
		

	

	}
	
	public static ArrayList<double[]> loadReferencePoints(String file){
		
		
		ArrayList<double[]> references = new ArrayList<double[]>();
		try{
			BufferedReader buff = new BufferedReader(new FileReader(file));
			while(buff.ready()){
				String line = buff.readLine();
				if(!line.isEmpty()){
					String line_split[] = line.trim().split("\t");
					double[] reference = new double[line_split.length];
					for (int i = 0; i < line_split.length; i++) {
						reference[i] = new Double(line_split[i]);
					}
					references.add(reference);
				}
			}
			buff.close();
		}catch (IOException ex){ex.printStackTrace();}
		
		
		return references;
		
	}

	
	public static void main(String[] args) {
		
		
		

		
		String dirEntrada = "/home/andrebia/gemini/doutorado/experimentos/ref/";
		String dirSaida = "/media/dados/Andre/ref/medidas/";
		
		String problema  = "DTLZ4";
		
		String id = "hyp_m_ideal_smpso";
		
		String[] algs = {"0.5_NWSum_hyp_m","0.5_NWSum_ideal","0.5_tb_crowd"};
	
		String metodo[] = {"smopso","smopso","smopso","smopso"};
	
		int objs[] = {3,5,10,15,20};
		int exec = 20;
		
		String reference_file = dirSaida + metodo[0] + "_" + problema + "_"+ algs[0] + "_reference.txt";
		
		ArrayList<double[]> references = loadReferencePoints(reference_file);
		System.out.println();

		for(int i = 0; i<objs.length; i++){
			int m = objs[i];
			
			String idExec = problema + "_" + m+ "_" + id + "_dist_all";
			
			double r[] = references.get(i);
			String maxmim[] = new String[m];
			for (int j = 0; j < maxmim.length; j++) {
				maxmim[j] = "-";
				
			}
			
			DistributionReferencePointAll dist = new DistributionReferencePointAll(m, dirSaida, idExec, r, maxmim);

			try{
				
				dist.calcularDistanceReferenceAll(dirEntrada, dirSaida, problema, m, algs, exec, metodo);

			} catch (IOException ex){ex.printStackTrace();}
		}

	}
}
