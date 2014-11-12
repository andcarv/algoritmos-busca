package problema;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import solucao.Solucao;
import solucao.SolucaoNumerica;

/**
 * Classe que representa o problema DTLZ2
 * @author andre
 *
 */

public class DTLZ5 extends Problema {

	/**
	 * Construtor da classe
	 * @param m Numero de objetivos do problema
	 */
	public DTLZ5(int m, int k){
		super(m);
		n = m + k - 1;
		problema = "dtlz5";
	}
	
	
	/*public double[] calcularObjetivos(Solucao sol) {
		SolucaoNumerica solucao = (SolucaoNumerica) sol;
		if(solucao.objetivos == null)
		   solucao.objetivos = new double[m];
		
		double g = g2(solucao.xm);
		double pi_2 = Math.PI/2.0;
		
		double f0 = (1+g);
		
		for(int i = 0; i<m-1; i++){
			double fi0 = fi(solucao.getVariavel(0),g);
			f0 *= Math.cos(fi0*pi_2);
		}
	   
		solucao.objetivos[0] = f0;
		for(int i = 1; i<(m); i++){
			//System.out.print("f(" + i + "): ");
			double fxi = (1+g);
			double fiI = fi(solucao.getVariavel(i),g);
			int j = 1;
			for(j = 0; j<(m-1-i);j++){
				fxi*=(Math.cos(fiI*pi_2));
				//System.out.print("Cos " + j  + " ");
			}
			fxi *= (Math.sin(fiI*pi_2));
			//System.out.print("Sen " + j  + " ");
			//System.out.println();
			solucao.objetivos[i] = fxi;
		}
		avaliacoes++;
		return solucao.objetivos;
	}
	
	public double fi(double xi, double g){
		double temp1 = Math.PI/(4*(1+g));
		double temp2 = 1+(2*g*xi);
		return temp1*temp2;
	}*/
	
	
	/**
	 * Metodo que calcula os objetivos da solucao passada como parametro
	 * Equacao 9 do artigo "Scalable Multi-Objective Optimization Test Problems"
	 * Código do J-Metal
	 */
	
	public double[] calcularObjetivos(Solucao sol) {
		SolucaoNumerica solucao = (SolucaoNumerica) sol;
		int numberOfVariables_ = solucao.n;
		int numberOfObjectives_ = m;
		
		double[] gen  = solucao.getVariaveis();
		  
		double [] x = new double[numberOfVariables_];
	    double [] f = new double[numberOfObjectives_];
	    double [] theta = new double[numberOfObjectives_-1];
	    double g = 0.0;
	    int k = numberOfVariables_ - numberOfObjectives_ + 1;
	                              
	    for (int i = 0; i < numberOfVariables_; i++)
	      x[i] = gen[i];
	                
	    for (int i = numberOfVariables_ - k; i < numberOfVariables_; i++)
	      g += (x[i] - 0.5)*(x[i] - 0.5);        
	        
	    double t = java.lang.Math.PI  / (4.0 * (1.0 + g)); 
	    
	    theta[0] = x[0] * java.lang.Math.PI / 2.0;  
	    for (int i = 1; i < (numberOfObjectives_-1); i++) 
	      theta[i] = t * (1.0 + 2.0 * g * x[i]);			
	        
	    for (int i = 0; i < numberOfObjectives_; i++)
	      f[i] = 1.0 + g;
	        
	    for (int i = 0; i < numberOfObjectives_; i++){
	      for (int j = 0; j < numberOfObjectives_ - (i + 1); j++)            
	        f[i] *= java.lang.Math.cos(theta[j]);                
	        if (i != 0){
	          int aux = numberOfObjectives_ - (i + 1);
	          f[i] *= java.lang.Math.sin(theta[aux]);
	        } // if
	    } //for
	        
	    for (int i = 0; i < numberOfObjectives_; i++)
	      solucao.objetivos[i] = f[i];          
		avaliacoes++;
		return solucao.objetivos;
	}
	
	
	public  ArrayList<SolucaoNumerica> obterFronteira(int n, int numSol){
		ArrayList<SolucaoNumerica> melhores = new ArrayList<SolucaoNumerica>();
		
		Random rand = new Random();
		rand.setSeed(1000);
		
		while(melhores.size()<numSol){
			SolucaoNumerica melhor = new SolucaoNumerica(n, m);

			for (int i = m-1; i <n; i++) {
				melhor.setVariavel(i, 0.5);
			}

			for (int i = 0; i < m-1; i++) {
				double newVal = rand.nextDouble();
				melhor.setVariavel(i, newVal);
			}

			double somaParcial = 0;
			calcularObjetivos(melhor);

			for (int i = 0; i < melhor.m; i++) {
				somaParcial += melhor.objetivos[i]*melhor.objetivos[i];
			}
			if(somaParcial==1){
				melhores.add(melhor);
			}
		}
		
		return melhores;
	}
	

	
	public static void main(String[] args) {
		
		int[] ms = {2,3,5,10,15,20,25,30};
		int numSol = 10000;
		int k = 10;
		
		System.out.println("DTLZ5");
		for (int i = 0; i < ms.length; i++) {

			int m = ms[i];
			
			System.out.println(m);

			int n = m + k - 1;

			//int decimalPlace = 5;
			DTLZ5 dtlz5 = new DTLZ5(m,k);
			

			ArrayList<SolucaoNumerica> f = dtlz5.obterFronteira(n, numSol);

			try{
				PrintStream ps = new PrintStream("pareto/DTLZ5_" + m + "_pareto.txt");
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
		
	}

}
