package problema;


import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.Random;

import pareto.FronteiraPareto;


import solucao.Solucao;
import solucao.SolucaoNumerica;

/**
 * Classe que representa o problema ZTD1
 * @author andre
 *
 */

public class ZDT1 extends Problema {

	/**
	 * Construtor da classe
	 * @param m Numero de objetivos do problema
	 */
		public ZDT1(){
		super(2);
		problema = "ztd1";
		n = 30;
	}
	
	/**
	 *
	 * Metodo que calcula os objetivos da solucao passada como parametro
	 * Problema ZDT1 - Codigo do jMetal"
	 */
	public double[] calcularObjetivos(Solucao sol) {
		
			SolucaoNumerica solucao = (SolucaoNumerica) sol;
			int numberOfVariables_  = solucao.n;
		    int numberOfObjectives_ =  m;
		    

		    double[] upperLimit_ = new double[numberOfVariables_];
		    double[] lowerLimit_ = new double[numberOfVariables_];

		    // Establishes upper and lower limits for the variables
		    for (int var = 0; var < numberOfVariables_; var++)
		    {
		      lowerLimit_[var] = 0.0;
		      upperLimit_[var] = 1.0;
		    } // for

		    double[] x = solucao.getVariaveis() ;
		    
		    double [] f = new double[numberOfObjectives_]  ;
		    f[0]        = x[0];
		    double g    = evalG(x, numberOfVariables_);
		    double h    = evalH(f[0],g);
		    f[1]        = h * g;
		    
		    solucao.objetivos[0] = f[0];
		    solucao.objetivos[1] = f[1];
		
		avaliacoes++;
		return solucao.objetivos;
	}
	
	
	 /**
	  * Codigo do jMetal
	   * Returns the value of the ZDT1 function G.
	   * @param decisionVariables The decision variables of the solution to 
	   * evaluate.
	   * @throws JMException 
	   */
	  private double evalG(double[] x, int numberOfVariables_) {
	    double g = 0.0;        
	    for (int i = 1; i < x.length;i++)
	      g += x[i];
	    double constante = (9.0 / (numberOfVariables_-1));
	    g = constante * g;
	    g = g + 1.0;
	    return g;
	  } // evalG
	    
	  /**
	   * Returns the value of the ZDT1 function H.
	   * @param f First argument of the function H.
	   * @param g Second argument of the function H.
	   */
	  public double evalH(double f, double g) {
	    double h = 0.0;
	    h = 1.0 - java.lang.Math.sqrt(f/g);
	    return h;        
	  } // evalH
		

	  public  ArrayList<SolucaoNumerica> obterFronteira(int n,int numSol){

		  n = 30;
		  
		  Random rand = new Random();
		  rand.setSeed(1000);

			double eps = 0;
			
			int tamanhoRepositorio = numSol;
			
			
			FronteiraPareto pareto = new FronteiraPareto(s, maxmim, r,eps, this, tamanhoRepositorio);
		
		  while(pareto.getFronteira().size()<numSol){
			  SolucaoNumerica melhor = new SolucaoNumerica(n, m);

			  for (int i = 1; i <n; i++) {
				  melhor.setVariavel(i, 0);
			  }


			  double newVal = rand.nextDouble();
			  melhor.setVariavel(0, newVal);
			  
			  calcularObjetivos(melhor);

			  if(!pareto.getFronteira().contains(melhor))
				  pareto.add(melhor, archiver);			
		  }

		  ArrayList<SolucaoNumerica> saida = new ArrayList<SolucaoNumerica>();
		  for (Iterator<Solucao> iterator = pareto.getFronteira().iterator(); iterator.hasNext();) {
			  SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
			  saida.add(solucaoNumerica);
		  }

		  return saida;
	  }
	
	  public static void main(String[] args) {

		  int numSol = 1000;
		  int m = 2;

		  System.out.println("ZTD1");

		  System.out.println(m);

		  int n = 30;


		  ZDT1 zdt1 = new ZDT1();




		  ArrayList<SolucaoNumerica> f = zdt1.obterFronteira(n, numSol);

		  try{
			  PrintStream ps = new PrintStream("pareto/ZTD1_" + m + "_pareto.txt");
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
