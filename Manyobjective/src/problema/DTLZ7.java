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

public class DTLZ7 extends Problema {
	
	
	/**
	 * Construtor da classe
	 * @param m Numero de objetivos do problema
	 */
	public DTLZ7(int m, int k){
		super(m);
		
		this.s  = 0.5;
		this.maxmim = new String[m];
		n = m + k - 1;
		for (int j = 0; j < maxmim.length; j++) {
			maxmim[j] = "-";
		}
		this.r = false;
		
		problema = "dtlz7";
		
		
	}
	
	/**
	 * Metodo que calcula os objetivos da solucao passada como parametro
	 * Equacao 9 do artigo "Scalable Multi-Objective Optimization Test Problems"
	 * Código do J-Metal
	 */

	
	public double[] calcularObjetivos(Solucao sol) {
		SolucaoNumerica solucao = (SolucaoNumerica) sol;
		
		for(int i = 0; i<m; i++)
			solucao.objetivos[i] = 0;
		
		
		int numberOfVariables_ = solucao.getVariaveis().length;
		int numberOfObjectives_ = m;

	    double [] x = new double[numberOfVariables_];
	    double [] f = new double[numberOfObjectives_];
	    int k = numberOfVariables_ - numberOfObjectives_ + 1;
	            
	    for (int i = 0; i < numberOfVariables_; i++)
	      x[i] = solucao.getVariavel(i);
	        
	    //Calculate g
	    double g = 0.0;
	    for (int i = numberOfVariables_ - k; i < numberOfVariables_; i++)
	      g += x[i] ;
	        
	    g = 1 + (9.0 * g) / k;
	    //<-
	                
	    //Calculate the value of f1,f2,f3,...,fM-1 (take acount of vectors start at 0)
	    for (int i = 0; i < numberOfObjectives_-1; i++)
	      f[i] = x[i];
	    //<-
	        
	    //->Calculate fM
	    double h = 0.0;
	    for (int i = 0; i < numberOfObjectives_ -1; i++)
	      h += (f[i]/(1.0 + g))*(1 + Math.sin(3.0 * Math.PI * f[i]));
	       
	    h = numberOfObjectives_ - h;
	        
	    f[numberOfObjectives_-1] = (1 + g) * h;
	    //<-
	        
	    //-> Setting up the value of the objetives
	    for (int i = 0; i < numberOfObjectives_; i++)
	      solucao.objetivos[i] = f[i];
	    
	    avaliacoes++;
	    
	    return solucao.objetivos;
		
	}
	
	public double h(double[] objetivos, double g){
		double soma = 0;
		for(int i = 0; i<m-1; i++){
			double fi = objetivos[i];
			double sen = Math.sin(3*Math.PI*fi);
			double parte1 = fi/(1+g);
			double parte2 = 1+sen;
			soma+=parte1*parte2;
		}
		
		return m - soma;
	}
	
	public  ArrayList<SolucaoNumerica> obterFronteira(int n, int numSol){
		
		Random rand = new Random();
		rand.setSeed(1000);
		
		double eps = 0;
		
		int tamanhoRepositorio = numSol;
		
		
		FronteiraPareto pareto = new FronteiraPareto(s, maxmim, r,eps, this, tamanhoRepositorio);
		                         
				
		while(pareto.getFronteira().size()<numSol){
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
			
			
			
		}
		
		ArrayList<SolucaoNumerica> saida = new ArrayList<SolucaoNumerica>();
		for (Iterator<Solucao> iterator = pareto.getFronteira().iterator(); iterator.hasNext();) {
			SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
			saida.add(solucaoNumerica);
		}
		
		/*ComparetorObjetivo comp = new ComparetorObjetivo(m-1);
		
		Collections.sort(melhores, comp);*/
		
		
		
		
		
		return saida;	
	}
	
	/*public void temp(int n){
		
		SolucaoNumerica solucaoBase = new SolucaoNumerica(n, m);
		
		for (int i = 0; i < solucaoBase.getVariaveis().length; i++) {
			solucaoBase.setVariavel(i, 0);
		}
		int varVez = solucaoBase.getVariaveis().length-1;
		while(varVez!=-1){

			double valVarVez = solucaoBase.getVariavel(varVez);

			valVarVez+=inc;

			if(valVarVez >1){
				valVarVez = 0;
				solucaoBase.setVariavel(varVez, valVarVez);
				valVarVez = solucaoBase.getVariavel(--varVez);
				while(valVarVez==1){
					--varVez;
					if(varVez == -1)
						break;
					valVarVez = solucaoBase.getVariavel(varVez);
				}
			}

			if(varVez!=-1){
				if(varVez!=solucaoBase.getVariaveis().length-1)
					valVarVez = Math.min(1.0, valVarVez+inc);
				solucaoBase.setVariavel(varVez, valVarVez);
				varVez = solucaoBase.getVariaveis().length-1;
			}
			
			for(int j = 0; j<solucaoBase.getVariaveis().length; j++){
				System.out.print(solucaoBase.getVariavel(j) + " ");
			}
			System.out.println();

		}
	}*/
	
	
	public static void main(String[] args) {
		
		int[] ms = {2,3,5,10,15,20,25,30};
		int numSol = 10000;
		int k = 10;
		
		
		for (int i = 0; i < ms.length; i++) {

			int m = ms[i];
			
			System.out.println(m);

			int n = m + k - 1;

			//int decimalPlace = 5;
			DTLZ7 dtlz7 = new DTLZ7(m, k);




			ArrayList<SolucaoNumerica> f = dtlz7.obterFronteira(n, numSol);

			try{
				PrintStream ps = new PrintStream("pareto/DTLZ7_" + m + "_pareto.txt");
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
		/*dtlz7.inc = 0.01;
		
		//dtlz7.obterFronteira2(n, numSol);
		System.out.println(Calendar.getInstance().getTime());
		
		ArrayList<SolucaoNumerica> f = dtlz7.obterFronteira(n, numSol);
		ComparetorObjetivo comp = new ComparetorObjetivo(0);
		Collections.sort(f,comp);
		
		try{
			PrintStream ps = new PrintStream("fronteiras/fronteira_dtlz7" + m + ".txt");
			PrintStream psSol = new PrintStream("fronteiras/solucoes_dtlz7" + m + ".txt");
			for (Iterator iterator = f.iterator(); iterator.hasNext();) {
				SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator
						.next();
			
				
				for(int i = 0; i<m; i++){
					BigDecimal bd = new BigDecimal(solucaoNumerica.objetivos[i]);     
					bd = bd.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
					ps.print( bd+ "\t");
				}
				ps.println();
				
				for(int i = 0; i<solucaoNumerica.getVariaveis().length; i++){
					psSol.print(solucaoNumerica.getVariavel(i) + "\t");
				}
				
				psSol.println();
				
			}
		} catch (IOException ex){ex.printStackTrace();}
		
	*/	
	}

}
