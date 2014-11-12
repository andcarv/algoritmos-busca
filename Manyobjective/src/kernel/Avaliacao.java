package kernel;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;


import solucao.SolucaoNumerica;

public class Avaliacao {
	
	public ArrayList<SolucaoNumerica> fronteira = null;
	
	public double numDominatedSolutions;
	public double minSum;
	public double sumMin;
	public double range;
	
	public int m;
	
	public PrintStream saidaVal;
	public PrintStream saidaSol;
	
	
	public Avaliacao(ArrayList<SolucaoNumerica> f, int m){
		fronteira = f;
		numDominatedSolutions = 0;
		minSum = Double.MAX_VALUE;
		sumMin = 0;
		range = 0;
		this.m = m;
	}
	
	public Avaliacao(ArrayList<SolucaoNumerica> f, int m, String caminho, String id){
		fronteira = f;
		numDominatedSolutions = 0;
		minSum = Double.MAX_VALUE;
		sumMin = 0;
		range = 0;
		this.m = m;
		try{
			saidaVal = new PrintStream(caminho + id + "_medidas.txt");
			saidaSol = new PrintStream(caminho + id + "_solucoes.txt");
		} catch(Exception ex){ex.printStackTrace();}
	}
	
	public double[] avaliar(){

		double retorno[] = new double[4];
		retorno[0] = getnNumDominatedSolutions();
		
		retorno[1] = getMinSum();
		retorno[2] = getSumMin();
		retorno[3] = getRange();
		
		return retorno;
	
	}
	
	public double getnNumDominatedSolutions(){
		return numDominatedSolutions = fronteira.size();
	}
	
	public double getMinSum(){
		for (Iterator<SolucaoNumerica> iterator = fronteira.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao =  iterator.next();
			double soma = 0;
			for (int i = 0; i < solucao.objetivos.length; i++) {
				soma += solucao.objetivos[i];
			}
			minSum = Math.min(minSum, soma);
		}
		
		return minSum;
	}
	
	public double getSumMin(){
		for(int i=0; i<m; i++){
			double min = Double.MAX_VALUE;
			for (Iterator<SolucaoNumerica> iterator = fronteira.iterator(); iterator.hasNext();) {
				SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
				min = Math.min(min, solucao.objetivos[i]);			
			}
			sumMin += min;
		}
		return sumMin;
	}
	
	public double getRange(){
		for(int i=0; i<m; i++){
			double min = Double.MAX_VALUE;
			double max = 0;
			for (Iterator<SolucaoNumerica> iterator = fronteira.iterator(); iterator.hasNext();) {
				SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
				min = Math.min(min, solucao.objetivos[i]);
				max = Math.max(max, solucao.objetivos[i]);
			}
			range += (max - min);
		}
		return range;
	}
	
	public String toString(){
		StringBuffer buff = new StringBuffer();
		buff.append("Numero de solucoes nao dominadas: " + numDominatedSolutions + "\n");
		buff.append("MinSum = " + minSum + "\n");
		buff.append("SumMin = " + sumMin + "\n");
		buff.append("Range = " + range + "\n");
		return buff.toString();
	}

}
