package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;

import archive.UnboundArchive;

import pareto.FronteiraPareto;
import problema.DTLZ2;
import problema.DTLZ4;
import problema.DTLZ6;
import problema.Problema;
import solucao.ComparetorObjetivo;
import solucao.Solucao;
import solucao.SolucaoNumerica;

public class LimparFronteirasDTLZ {
	
	
	public static void limparFronteiras(String file, int m, String prob) throws IOException{
		
		String maxmim[] = new String[m];
		for (int i = 0; i < maxmim.length; i++) {
			maxmim[i] = "-";
		}
		
		
		int k = 10;
		
		Problema problem = null;
		if(prob.toUpperCase().equals("DTLZ4"))
			problem = new DTLZ4(m, k);
		if(prob.toUpperCase().equals("DTLZ2"))
			problem = new DTLZ2(m, k);
		if(prob.toUpperCase().equals("DTLZ6"))
			problem = new DTLZ6(m, k);
		
		
		FronteiraPareto pareto = new FronteiraPareto(0.5, maxmim, false, 0.001, problem, Integer.MAX_VALUE);
		
		
		BufferedReader buff = new BufferedReader(new FileReader(file));
		int j = 0;
		while(buff.ready()){
			String line = buff.readLine();
			if(!line.isEmpty()){
				j++;
				String line_split[] = line.trim().split("\t");
				double[] point = new double[line_split.length];
				for(int i = 0; i< line_split.length; i++){
					double value = new Double(line_split[i]);
					BigDecimal b = new BigDecimal(value);		 
					double temp = (b.setScale(2, BigDecimal.ROUND_UP)).doubleValue();; 
					point[i] = temp;
				}
				
				SolucaoNumerica solution = new SolucaoNumerica(problem.n, m);
				for(int i =0; i< problem.m; i++){
					solution.objetivos[i] = point[i];
				}
				
				pareto.add(solution, new UnboundArchive());
			}
		}
		
		System.out.println(j + " - " + pareto.getFronteira().size());
		
		ComparetorObjetivo comp = new ComparetorObjetivo(0);
		
		Collections.sort(pareto.getFronteira(),comp);
		
		PrintStream psSaida = new PrintStream("saida.txt");
		
		for (Iterator<Solucao> iterator = pareto.getFronteira().iterator(); iterator.hasNext();) {
			SolucaoNumerica solution = (SolucaoNumerica) iterator.next();
			for(int i = 0; i<m; i++){
				psSaida.print(solution.objetivos[i] + "\t");
			}
			psSaida.println();
		}
		
	}
	
	public static void main(String[] args) {
		try{
			
			int m = 20;
			String prob = "dtlz4";
			String file = "D:\\Andre\\Manyobjective\\pareto2\\DTLZ4_20_pareto.txt";
			LimparFronteirasDTLZ.limparFronteiras(file, m, prob);
		}catch(IOException ex){ex.printStackTrace();}
	}

}
