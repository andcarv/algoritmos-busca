package problema.wfg;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;


import java.util.Iterator;

import pareto.FronteiraPareto;
import solucao.Solucao;
import solucao.SolucaoNumerica;

/**
 * Classe que representa o problema WFG1
 * @author andre
 *
 */

public class WFG1 extends WFG {
	
	


	/**
	 * Construtor da classe
	 * @param m Numero de objetivos do problema
	 */
	public WFG1(Integer k, Integer l, Integer m){
		super(k,l,m);
		problema = "wfg1";
		S_ = new int[M_];
		for (int i = 0; i < M_; i++)
			S_[i] = 2 * (i+1);

		A_ = new int[M_-1];        
		for (int i = 0; i < M_-1; i++)
			A_[i] = 1;          
	}
	
	  /** 
	  * Evaluates a solution 
	  * @param z The solution to evaluate
	  * @return a double [] with the evaluation results
	  */  
	  public double [] evaluate(double [] z){                
	    double [] y;
	        
	    y = normalise(z);
	    y = t1(y,k_);
	    y = t2(y,k_);
	    try {
	      y = t3(y);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    y = t4(y,k_,M_);
	    
	    
	        
	    double [] result = new double[M_];
	    double [] x = calculate_x(y);
	    for (int m = 1; m <= M_ - 1 ; m++) {
	      result [m-1] = D_*x[M_-1] + S_[m-1] * (new Shapes()).convex(x,m);
	    }
	        
	    result[M_-1] = D_*x[M_-1] + S_[M_-1] * (new Shapes()).mixed(x,5,(double)1.0);
	        
	    return result;
	  } // evaluate
	    
	  /**
	   * WFG1 t1 transformation
	   */
	  public double [] t1(double [] z, int k){
	    double [] result = new double[z.length];

	    System.arraycopy(z, 0, result, 0, k);
	        
	    for (int i = k; i < z.length; i++) {
	      result[i] = (new Transformations()).s_linear(z[i],(double)0.35);
	    }
	        
	    return result;      
	  } // t1

	  /**
	  * WFG1 t2 transformation
	  */
	  public double [] t2(double [] z, int k){
	    double [] result = new double[z.length];

	    System.arraycopy(z, 0, result, 0, k);
	        
	    for (int i = k; i < z.length; i++) {
	      result[i] = (new Transformations()).b_flat(z[i],(double)0.8,(double)0.75,(double)0.85);
	    }
	        
	    return result;
	  } // t2
	    
	  /**
	  * WFG1 t3 transformation
	   * @throws Exception 
	  */
	  public double [] t3(double [] z) throws Exception{
	    double [] result = new double[z.length];
	        
	    for (int i = 0; i < z.length; i++) {
	      result[i] = (new Transformations()).b_poly(z[i],(double)0.02);
	    }
	        
	    return result;
	  } // t3
	    
	  /**
	  * WFG1 t4 transformation
	  */
	  public double [] t4(double [] z, int k, int M){
	    double [] result = new double[M];
	    double [] w      = new double[z.length];
	                
	    for (int i = 0; i < z.length; i++) {
	      w[i] = (double)2.0 * (i + 1);
	    }
	        
	    for (int i = 1; i <= M-1; i++){
	      int head = (i - 1)*k/(M-1) + 1;
	      int tail = i * k / (M - 1);                                   
	      double [] subZ = subVector(z,head-1,tail-1);
	      double [] subW = subVector(w,head-1,tail-1);
	            
	      result[i-1] = (new Transformations()).r_sum(subZ,subW);
	    }
	        
	    int head = k + 1 - 1;
	    int tail = z.length - 1;              
	    double [] subZ = subVector(z,head,tail);      
	    double [] subW = subVector(w,head,tail);        
	    result[M-1] = (new Transformations()).r_sum(subZ,subW);
	                
	    return result;
	  } // t4
	
	
	
	
	public double[] calcularObjetivos(Solucao sol) {
		SolucaoNumerica solucao = (SolucaoNumerica) sol;
		if(solucao.objetivos == null)
		   solucao.objetivos = new double[m];
		
		double [] variables = new double[solucao.n];
	    
	        
	    for (int i = 0; i < solucao.n; i++) {
	      variables[i] = (double) solucao.getVariavel(i);    
	    }
	        
	    double [] f = evaluate(variables);
	    
	    for (int i = 0; i < f.length; i++) 
	        solucao.objetivos[i] = f[i];
				
		return solucao.objetivos;
	}
	
	
	public ArrayList<SolucaoNumerica> obterFronteira(int n, int numSol){
		ArrayList<SolucaoNumerica> melhores = new ArrayList<SolucaoNumerica>();


		random.setSeed(1000);

		double eps = 0;

		int tamanhoRepositorio = numSol;

		FronteiraPareto pareto = new FronteiraPareto(s, maxmim, r,eps, this, tamanhoRepositorio);

		double log_intervalos = (Math.log10(numSol)/k_);
		int inter = (int) Math.ceil(Math.pow(10, log_intervalos));
		System.out.println("Intervalos = " + inter);
		int contador[] = new int[k_];
		double pedaco = 1.0/inter;
		int k = 0;
		int indice = k_-1;
		while(k < numSol){

			SolucaoNumerica melhor = new SolucaoNumerica(n, m);

			for (int i = k_+1; i <=n; i++) {
				double value = 2*i*0.35f;
				melhor.setVariavel(i-1, value);
			}

			for (int i = 1; i < k_+1; i++) {
				double newVal = contador[i-1]*pedaco*(2*i);
				melhor.setVariavel(i-1, newVal);
			}

			calcularObjetivos(melhor);

			if(!pareto.getFronteira().contains(melhor))
				pareto.add(melhor, archiver);
			melhores.add(melhor);

			while(indice!=k_-1 && contador[indice+1] < inter-1)
				indice++;

			while(indice>0 && contador[indice] == inter-1){
				contador[indice] = 0;
				indice--;
			} 
			contador[indice]++;

			k++;
		}

	ArrayList<SolucaoNumerica> saida = new ArrayList<SolucaoNumerica>();
	for (Iterator<Solucao> iterator = pareto.getFronteira().iterator(); iterator.hasNext();) {
		SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
		saida.add(solucaoNumerica);
	}

	return saida;

}
	
		
	
	
  public void teste(int numSol){
	  
	  double log_intervalos = (Math.log10(numSol)/m);
		int inter = (int) Math.ceil(Math.pow(10, log_intervalos));
		System.out.println("Intervalos = " + inter);
		int contador[] = new int[m];
		double pedaco = 1.0/inter;
		int k = 0;
		int indice = m-1;
		while(k < numSol){
			imprimir(contador, pedaco);
			while(indice!=m-1 && contador[indice+1] < inter-1)
				indice++;
			
			while(indice>0 && contador[indice] == inter-1){
				contador[indice] = 0;
				indice--;
			} 
			contador[indice]++;
			

			 
		   k++;
		}
  }

  private void imprimir(int[] contador, double pedaco) {
	  for(int l = 0; l<m; l++)
		  System.out.print(contador[l] * pedaco + "\t");
	  System.out.println();
  }
	
 
	
	public static void main(String[] args) {
		
		
		int m = 2;
		int numSol = 2000;
		int k = 4;
		int l = 20;
	
		
		WFG1 wfg1 = new WFG1(k, l, m);
				
		ArrayList<SolucaoNumerica> f = wfg1.obterFronteira(wfg1.n, numSol);

		try{
			PrintStream ps = new PrintStream("WFG1_" + m + "_pareto.txt");
			
			for (Iterator<SolucaoNumerica> iterator2 = f.iterator(); iterator2.hasNext();) {
				SolucaoNumerica solucaoNumerica2 = (SolucaoNumerica) iterator2
						.next();
				for(int j = 0; j<m; j++){
					ps.print(solucaoNumerica2.objetivos[j] + "\t");
				}
				ps.println();
			}
			ps.close();
		} catch (IOException ex){ex.printStackTrace();}

		
	}
	

}
