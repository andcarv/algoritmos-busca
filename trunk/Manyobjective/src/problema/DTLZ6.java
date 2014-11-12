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
 * Classe que representa o problema DTLZ2
 * @author andre
 *
 */

public class DTLZ6 extends Problema {

	/**
	 * Construtor da classe
	 * @param m Numero de objetivos do problema
	 */
	public DTLZ6(int m, int k){
		super(m);
		n = m + k - 1;
		problema = "dtlz6";
	}
	
	/**
	 * Metodo que calcula os objetivos da solucao passada como parametro
	* Método do JMetal
	 */
	
	public double[] calcularObjetivos(Solucao sol) {
		SolucaoNumerica solucao = (SolucaoNumerica) sol;
		int numberOfVariables_ = solucao.n;
		int numberOfObjectives_ = m;
		
		double [] x = new double[numberOfVariables_];
		double [] f = new double[numberOfObjectives_];
		double [] theta = new double[numberOfObjectives_-1];
		int k = numberOfVariables_ - numberOfObjectives_ + 1;
		        
		    for (int i = 0; i < numberOfVariables_; i++)
		      x[i] = solucao.getVariavel(i);
		        
		    double g = 0.0;
		    for (int i = numberOfVariables_ - k; i < numberOfVariables_; i++)
		      g += java.lang.Math.pow(x[i],0.1);
		        
		    double t = java.lang.Math.PI  / (4.0 * (1.0 + g));
		    theta[0] = x[0] * java.lang.Math.PI / 2;  
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
		        } //if
		    } // for

		    for (int i = 0; i < numberOfObjectives_; i++)        
		    	solucao.objetivos[i]  =f[i];
		    
		    avaliacoes++;
		    return solucao.objetivos;
		
	}

	public  ArrayList<SolucaoNumerica> obterFronteira(int n, int numSol){
		ArrayList<SolucaoNumerica> melhores = new ArrayList<SolucaoNumerica>();
		
		Random rand = new Random();
		rand.setSeed(1000);
		
		double eps = 0;
		
		int tamanhoRepositorio = numSol;
		
		FronteiraPareto pareto = new FronteiraPareto(s, maxmim, r,eps, this, tamanhoRepositorio);
				
		while(melhores.size()<numSol){
			SolucaoNumerica melhor = new SolucaoNumerica(n, m);

			for (int i = m-1; i <n; i++) {
				melhor.setVariavel(i, 0);
			}

			for (int i = 0; i < m-1; i++) {
				double newVal = rand.nextDouble();
				melhor.setVariavel(i, newVal);
			}
			
			calcularObjetivos(melhor);
			
			if(!pareto.getFronteira().contains(melhor))
				pareto.add(melhor, archiver);
			
			melhores.add(melhor);
			
			
		}
		
		ArrayList<SolucaoNumerica> saida = new ArrayList<SolucaoNumerica>();
		for (Iterator<Solucao> iterator = pareto.getFronteira().iterator(); iterator.hasNext();) {
			SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
			saida.add(solucaoNumerica);
		}
		
			
		return saida;	
	}
	
	public static void main(String[] args) {
				
		//int[] ms = {2,3,5,10,15,20,25,30};
		int[] ms = {3};
		int numSol = 10000;
		int k = 10;
		
		
		for (int i = 0; i < ms.length; i++) {

			int m = ms[i];
			
			System.out.println(m);

			int n = m + k - 1;

			//int decimalPlace = 5;
			DTLZ6 dtlz6 = new DTLZ6(m, k);




			ArrayList<SolucaoNumerica> f = dtlz6.obterFronteira(n, numSol);

			try{
				PrintStream ps = new PrintStream("pareto5/DTLZ6_" + m + "pareto.txt");
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
		//dtlz7.obterFronteira2(n, numSol);
		
		
		
		/*ArrayList<SolucaoNumerica> f = dtlz6.obterFronteira(n, numSol);
		
		
		try{
			PrintStream ps = new PrintStream("fronteira_dtlz7" + m);
			PrintStream psSol = new PrintStream("solucoes_dtlz7" + m);
			for (Iterator<SolucaoNumerica> iterator = f.iterator(); iterator.hasNext();) {
				SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator
						.next();
				
				dtlz6.calcularObjetivos(solucaoNumerica);
							
				for(int i = 0; i<m; i++){
					BigDecimal bd = new BigDecimal(solucaoNumerica.objetivos[i]);     
					bd = bd.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
					ps.print( bd+ " ");
				}
				ps.println();
				
				for(int i = 0; i<solucaoNumerica.getVariaveis().length; i++){
					psSol.print(solucaoNumerica.getVariavel(i) + " ");
				}
				
				psSol.println();
				
			}
		} catch (IOException ex){ex.printStackTrace();}
	*/
		
		
	}

}
