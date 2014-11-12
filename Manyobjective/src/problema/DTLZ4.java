package problema;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import solucao.ComparetorObjetivo;
import solucao.Solucao;
import solucao.SolucaoNumerica;

/**
 * Classe que representa o problema DTLZ2
 * @author andre
 *
 */



public class DTLZ4 extends Problema {


	public static final double alpha = 100;
	/**
	 * Construtor da classe
	 * @param m Numero de objetivos do problema
	 */
	public DTLZ4(int m, int k){
		super(m);
		n = m + k - 1;
		problema = "dtlz4";
	}
	
	
	/*public double[] calcularObjetivos(Solucao sol) {
		SolucaoNumerica solucao = (SolucaoNumerica) sol;
		if(solucao.objetivos == null)
		   solucao.objetivos = new double[m];
		
		double g = g2(solucao.xm);
		System.out.println(g);
		double pi_2 = Math.PI/2.0;
		//System.out.print("f(0): ");
		double xiAlpha = Math.pow(solucao.getVariavel(0), alpha);
		double f0 = (1+g)*Math.cos(xiAlpha*pi_2);
		//System.out.print("Cos 0 ");
		for(int i = 1; i<m-1; i++){
			f0 *= Math.cos(solucao.getVariavel(i)*pi_2);
			//System.out.print("Cos " + i  + " ");
		}
	   solucao.objetivos[0] = f0;
		for(int i = 1; i<(m); i++){
			//System.out.print("f(" + i + "): ");
			double fxi = (1+g);
			int j = 1;
			
			for(j = 0; j<(m-1-i);j++){
				xiAlpha = Math.pow(solucao.getVariavel(j), alpha);
				fxi*=(Math.cos(xiAlpha*pi_2));
				//System.out.print("Cos " + j  + " ");
			}
			xiAlpha = Math.pow(solucao.getVariavel(j), alpha);
			fxi *= (Math.sin(xiAlpha*pi_2));
			//System.out.print("Sen " + j  + " ");
			//System.out.println();
			solucao.objetivos[i] = fxi;
		}
		avaliacoes++;
		return solucao.objetivos;
	}*/
	
	/**
	 * Metodo que calcula os objetivos da solucao passada como parametro
	 * Equacao 9 do artigo "Scalable Multi-Objective Optimization Test Problems"
	 * Metodo do J-Metal
	 */
	public double[] calcularObjetivos(Solucao sol) {
		SolucaoNumerica solucao = (SolucaoNumerica) sol;
		int numberOfVariables_ = solucao.n;
		int numberOfObjectives_ = m;
		
		double[] gen  = solucao.getVariaveis();
		  
	    double [] x = new double[numberOfVariables_];
	    double [] f = new double[numberOfObjectives_];
	    double alpha = 100.0;
	    int k = numberOfVariables_ - numberOfObjectives_ + 1;
	  
	    for (int i = 0; i < numberOfVariables_; i++)
	      x[i] = gen[i];

	    double g = 0.0;
	    for (int i = numberOfVariables_ - k; i < numberOfVariables_; i++)
	      g += (x[i] - 0.5)*(x[i] - 0.5);                
	      
	    for (int i = 0; i < numberOfObjectives_; i++)
	      f[i] = 1.0 + g;
	        
	    for (int i = 0; i < numberOfObjectives_; i++) {
	      for (int j = 0; j < numberOfObjectives_ - (i + 1); j++)            
	        f[i] *= java.lang.Math.cos(java.lang.Math.pow(x[j],alpha)*(java.lang.Math.PI/2.0));                
	        if (i != 0){
	          int aux = numberOfObjectives_ - (i + 1);
	          f[i] *= java.lang.Math.sin(java.lang.Math.pow(x[aux],alpha)*(java.lang.Math.PI/2.0));
	        } //if
	    } // for
	        
	    for (int i = 0; i < numberOfObjectives_; i++)
	      solucao.objetivos[i] = f[i];                
		avaliacoes++;
		return solucao.objetivos;
	}
	
	public  ArrayList<SolucaoNumerica> obterFronteira(int n, int numSol){
		ArrayList<SolucaoNumerica> melhores = new ArrayList<SolucaoNumerica>();
		
		Random rand = new Random();
		rand.setSeed(1000);
		
		
		int num_fim = 0;
		
		int tam_ant = 0;
		
		while(melhores.size()<numSol){
			//if(melhores.size() % 50000 == 0)
				//System.out.print(melhores.size() + " ");
			if(tam_ant != melhores.size()){
				System.out.println(melhores.size() + " ");
				tam_ant = melhores.size();
			}
				
			SolucaoNumerica melhor = new SolucaoNumerica(n, m);

			for (int i = m-1; i <n; i++) {
				melhor.setVariavel(i, 0.5);
			}

			for (int i = 0; i < m-1; i++) {
				double newVal = rand.nextDouble();
			
				melhor.setVariavel(i, newVal);
			}

			
			calcularObjetivos(melhor);
			
			for(int i = 0; i<m;i++){
				BigDecimal b = new BigDecimal(melhor.objetivos[i]);		 
				double temp = (b.setScale(3, BigDecimal.ROUND_UP)).doubleValue();
				melhor.objetivos[i] = temp;
			}
			
			
			 
			
			//if(!pareto.getFronteira().contains(melhor))
				//pareto.add(melhor, archiver);
			
			if(melhor.objetivos[0] > 0.9){
				num_fim++;
				if(num_fim < numSol*0.5)
					melhores.add(melhor);
			} else
				melhores.add(melhor);
			
		}
								
		ArrayList<SolucaoNumerica> saida = new ArrayList<SolucaoNumerica>();
		
		ComparetorObjetivo comp = new ComparetorObjetivo(0);
		Collections.sort(melhores, comp);
		
		for (Iterator<SolucaoNumerica> iterator = melhores.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
			saida.add(solucaoNumerica);
		}
					
		return saida;		
	}
	

	
	public static void main(String[] args) {
		
		int[] ms = {3};
		int numSol = 100000;
		int k = 10;
		
		System.out.println("DTLZ4");
		for (int i = 0; i < ms.length; i++) {

			int m = ms[i];
			
			System.out.println(m);

			int n = m + k - 1;

			//int decimalPlace = 5;
			DTLZ4 dtlz4 = new DTLZ4(m, k);
			

			ArrayList<SolucaoNumerica> f = dtlz4.obterFronteira(n, numSol);

			try{
				PrintStream ps = new PrintStream("pareto3/DTLZ4_" + m + "_pareto.txt");
				for (Iterator<SolucaoNumerica> iterator = f.iterator(); iterator.hasNext();) {
					SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator
					.next();
					for(int j = 0; j<m; j++){
						ps.print(solucaoNumerica.objetivos[j] + "\t");
					}
					ps.println();


				}
			} catch (IOException ex){ex.printStackTrace();}

		}
		
		/*int m = 30;
		int numSol = 10000;
		int k = 10;
		
		int n = m + k - 1;
		
		DTLZ4 dtlz4 = new DTLZ4(m);
		
		ArrayList<SolucaoNumerica> f = dtlz4.obterFronteira(n, numSol);
		
		try{
			PrintStream ps = new PrintStream("pareto/DTLZ4_" + m + "pareto.txt");
			for (Iterator<SolucaoNumerica> iterator = f.iterator(); iterator.hasNext();) {
				SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator
						.next();
				for(int i = 0; i<m; i++){
					ps.print(solucaoNumerica.objetivos[i] + "\t");
				}
				ps.println();
							

			}
		} catch (IOException ex){ex.printStackTrace();}*/
		
		
	}

}
