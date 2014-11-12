package problema;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import solucao.Solucao;
import solucao.SolucaoNumerica;

/**
 * Classe que representa o problema DTLZ3
 * @author andre
 *
 */

public class DTLZ3 extends Problema {

	/**
	 * Construtor da classe
	 * @param m Numero de objetivos do problema
	 */
	public DTLZ3(int m, int k){
		super(m);
		n = m + k - 1;
		problema = "dtlz3";
	}
	
	/**
	 * Metodo que calcula os objetivos da solucao passada como parametro
	 * Equacao 9 do artigo "Scalable Multi-Objective Optimization Test Problems" utilizado a equacao 8 como g
	 */
	public double[] calcularObjetivos(Solucao sol) {
		SolucaoNumerica solucao = (SolucaoNumerica) sol;
		if(solucao.objetivos == null)
		   solucao.objetivos = new double[m];
		double [] x = new double[n];
	    double [] f = new double[m];
	    int k = n - m + 1;
	        
	    for (int i = 0; i < n; i++)
	      x[i] = solucao.getVariavel(i);
	        
	    double g = 0.0;
	    for (int i = n - k; i < n; i++)
	      g += (x[i] - 0.5)*(x[i] - 0.5) - Math.cos(20.0 * Math.PI * (x[i] - 0.5));
	        
	    g = 100.0 * (k + g);
	    for (int i = 0; i < m; i++)
	      f[i] = 1.0 + g;
	        
	    for (int i = 0; i < m; i++){
	      for (int j = 0; j < m - (i + 1); j++)            
	        f[i] *= java.lang.Math.cos(x[j]*0.5*java.lang.Math.PI);                
	        if (i != 0){
	          int aux = m - (i + 1);
	          f[i] *= java.lang.Math.sin(x[aux]*0.5*java.lang.Math.PI);
	        } // if
	    } //for
	        
	    for (int i = 0; i < m; i++)
	      solucao.objetivos[i] =f[i];                
		avaliacoes++;
		return solucao.objetivos;
	}
	
	public double[] calcularObjetivos2(Solucao sol) {
		SolucaoNumerica solucao = (SolucaoNumerica) sol;
		if(solucao.objetivos == null)
		   solucao.objetivos = new double[m];
		
		double g = g1(solucao.getVariaveis());
		double pi_2 = Math.PI/2.0;
		//System.out.print("f(0): ");
		double f0 = (1+g)*Math.cos(solucao.getVariavel(0)*pi_2);
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
				fxi*=(Math.cos(solucao.getVariavel(j)*pi_2));
				//System.out.print("Cos " + j  + " ");
			}
			fxi *= (Math.sin(solucao.getVariavel(j)*pi_2));
			//System.out.print("Sen " + j  + " ");
			//System.out.println();
			solucao.objetivos[i] = fxi;
		}
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

			//double somaParcial = 0;
			calcularObjetivos(melhor);

			/*for (int i = 0; i < melhor.m; i++) {
				somaParcial += melhor.objetivos[i]*melhor.objetivos[i];
			}
			if(somaParcial==1){
				melhores.add(melhor);
			}*/
			
			if(g1(melhor.getVariaveis())==0)
				melhores.add(melhor);
		}
		
		return melhores;
	}
	

	
	public static void main(String[] args) {
		
		int[] ms = {2,3,5,10,15,20,25,30};
		int numSol = 10000;
		int k = 10;
		
		System.out.println("DTLZ3");
		for (int i = 0; i < ms.length; i++) {

			int m = ms[i];
			
			System.out.println(m);

			int n = m + k - 1;

			//int decimalPlace = 5;
			DTLZ3 dtlz3 = new DTLZ3(m, k);

			


			ArrayList<SolucaoNumerica> f = dtlz3.obterFronteira(n, numSol);

			try{
				PrintStream ps = new PrintStream("pareto/DTLZ3_" + m + "_pareto.txt");
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
		
		
		
		/*int m = 3;

		DTLZ3 dtlz3 = new DTLZ3(m);
		ArrayList<SolucaoNumerica> f =  dtlz3.obterFronteira(12, 100);
		
		StringBuffer x = new StringBuffer();
		StringBuffer y = new StringBuffer();
		StringBuffer z = new StringBuffer();
		
		x.append("x<-c(");
		y.append("y<-c(");
		z.append("z<-c(");
		
		
		try{
			PrintStream ps = new PrintStream("fronteira_dtlz3_" + m);
			for (Iterator<SolucaoNumerica> iterator = f.iterator(); iterator.hasNext();) {
				SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator
						.next();
				
				dtlz3.calcularObjetivos(solucaoNumerica);
				
				dtlz3.calcularObjetivosJmetal(solucaoNumerica);
				
				
				for(int i = 0; i<m; i++){
					ps.print(solucaoNumerica.objetivos[i] + "\t");
					if(i==0)
						x.append(solucaoNumerica.objetivos[i] + ",");
					if(i==1)
						y.append(solucaoNumerica.objetivos[i] + ",");
					if(i==2)
						z.append(solucaoNumerica.objetivos[i] + ",");
				}
				ps.println();
				
			}
			
			x.deleteCharAt(x.length()-1);
			x.append(")");
			
			y.deleteCharAt(y.length()-1);
			y.append(")");
			
			z.deleteCharAt(z.length()-1);
			z.append(")");
		} catch (IOException ex){ex.printStackTrace();}
		//dtlz3.imprimirVetoresScilab(melhores);
		
		System.out.println(x);
		System.out.println();
		
		System.out.println(y);
		System.out.println();
		System.out.println(z);
		System.out.println();
		
		
		*/
	}

}
