package problema;

import indicadores.PontoFronteira;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import archive.PreciseArchiver;
import archive.UnboundArchive;

import solucao.ComparetorObjetivo;
import solucao.ComparetorObjetivoPF;
import solucao.ComparetorRank;
import solucao.ComparetorRankPF;
import solucao.Solucao;
import solucao.SolucaoNumerica;

public abstract class Problema {
	
	public int m;
	public int n;
	
	
	public int avaliacoes;
	
	public String problema;
	
	public  double[] joelho = null;
	
	public  double[] lambda = null;
	
	public double inc;
	public int varVez;
	
	public double s;
	
	public String[] maxmim;
	
	public boolean r;
	
	public PreciseArchiver archiver = new UnboundArchive();
	
	public double MAX_VALUE;
	public double MIN_VALUE;
	
	public Problema(int m){
			
		this.m = m;
		avaliacoes = 0;
		
		this.s  = 0.5;
		this.maxmim = new String[m];
		for (int i = 0; i < maxmim.length; i++) {
			maxmim[i] = "-";
		}
		this.r = false;
		
		MAX_VALUE = 1;
		MIN_VALUE = 0;
		
	}
	
	public Problema(int m, double inc){
		
		this.m = m;
		avaliacoes = 0;
		
		this.inc = inc;
		
	}
	
	public abstract double[] calcularObjetivos(Solucao solucao);
	
	public abstract ArrayList<SolucaoNumerica> obterFronteira(int n, int numSol);
	
	/**
	 * Método que gera a próxima solução através do incremento passado no construtor
	 * @param solucaoBase Solução base para a geração da nova solução
	 * @param varVez2 Varia
	 * @param inicio
	 * @param fim
	 */
	public boolean getProximaSolucao(SolucaoNumerica solucaoBase, int inicio, int fim){
		
		/*int decimalPlace = 7;
		
		BigDecimal incBig = new BigDecimal(this.inc);
		BigDecimal valVarVezBig = new BigDecimal(solucaoBase.getVariavel(varVez));
		
		valVarVezBig = valVarVezBig.add(incBig);
		valVarVezBig = valVarVezBig.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);*/
		
		
		Double valVarVez = new Double(solucaoBase.getVariavel(varVez));

		valVarVez=valVarVez+inc;
		
		//Double valVarVez = new Double(valVarVezBig.toString());

		if(valVarVez >1){
			while(valVarVez>=1){
				valVarVez = 0.0;
				solucaoBase.setVariavel(varVez, valVarVez);
				varVez--;
				if(varVez < inicio)
					return false;
				valVarVez = solucaoBase.getVariavel(varVez);
			}
		}

		
		if(varVez!=fim){
			/*valVarVezBig = new BigDecimal(valVarVez);
			valVarVezBig = valVarVezBig.add(incBig);
			valVarVezBig = valVarVezBig.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
			valVarVez = new Double(valVarVezBig.toString());*/
			valVarVez=valVarVez+inc;
		}
			valVarVez = Math.min(1.0, valVarVez);
		solucaoBase.setVariavel(varVez, valVarVez);
		varVez = fim;
		

		return true;

	}
	
	/**
	 * Equacao 8 do artigo "Scalable Multi-Objective Optimization Test Problems"
	 */
	public double g1(double[] x){
		
		int k = n - m + 1;
		double g = 0.0;
		for (int i = n - k; i < n; i++){
			g += (x[i] - 0.5)*(x[i] - 0.5) - Math.cos(20.0 * Math.PI * ( x[i] - 0.5));
		}

		g = 100 * (k + g);       

		return g;
	}
	
	/*public double g12(Solucao sol){
		double g = 0;
	    for (int i = sol.n - sol.k + 1; i <= sol.n; i++)
	    {
		g += Math.pow(sol.variaveis[i-1]-0.5,2) - Math.cos(20 * Math.PI * (sol.variaveis[i-1]-0.5));
	    }
	    g = 100 * (sol.k + g);
	    
	    return g;

	}*/
	
	
	/**
	 * Equacao 9 do artigo "Scalable Multi-Objective Optimization Test Problems"
	 */
	public double g2(double[] x){
		
		int k = n - m + 1;
		double g = 0.0;
		  for (int i = n - k; i < n; i++)
		      g += (x[i] - 0.5)*(x[i] - 0.5);
		
		
		return g;
	}
	
	
	public void imprimirVetoresScilab(ArrayList<SolucaoNumerica> melhores){
		StringBuffer comandoX = new StringBuffer();
		StringBuffer comandoY = new StringBuffer();
		StringBuffer comandoZ = new StringBuffer();
		
		comandoX.append("x = [\n");
		comandoY.append("y = [\n");
		comandoZ.append("z = [\n");
		for (Iterator<SolucaoNumerica> iterator = melhores.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
			
			comandoX.append(solucao.objetivos[0]+  "\n");
			comandoY.append(solucao.objetivos[1]+  "\n");
			comandoZ.append(solucao.objetivos[2]+  "\n");
			
			
		}
		
		comandoX.append("];\n");
		comandoY.append("];\n");
		comandoZ.append("];\n");
		
		System.out.println(comandoX);
		System.out.println();
		System.out.println(comandoY);
		System.out.println();
		System.out.println(comandoZ);
		
		
	}
	
	
	public void imprimirFronteirar(int n, int m, int numSol) throws IOException{
		
		ArrayList<SolucaoNumerica> fronteira =  obterFronteira(n, numSol);
		
		String arqFronteira = problema +"_" + m +"_fronteira.txt";
		
		PrintStream psFronteira = new PrintStream(arqFronteira);
		
		for (Iterator<SolucaoNumerica> iterator = fronteira.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
			for(int i = 0; i<m; i++){
				psFronteira.print(new Double( solucao.objetivos[i]).toString().replace('.', ',')+ " ");
			}
			psFronteira.println();
		}
	}
	
	public void printFronts(int n, int m,ArrayList<SolucaoNumerica> fronteira, String id, String dir) throws IOException{

		

		String arqFronteira = dir + problema +"_" + m +"_" + id +  ".txt";

		PrintStream psFronteira = new PrintStream(arqFronteira);

		for (Iterator<SolucaoNumerica> iterator = fronteira.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();
			for(int i = 0; i<m; i++){
				psFronteira.print(new Double( solucao.objetivos[i])+ "\t");
			}
			psFronteira.println();
		}
	}
	
	public void printFrontsPF(int n, int m, ArrayList<PontoFronteira> fronteira, String id, String dir) throws IOException{
		
		String arqFronteira = dir + problema +"_" + m + "_" + id +".txt";
		
		PrintStream psFronteira = new PrintStream(arqFronteira);
		
		for (Iterator<PontoFronteira> iterator = fronteira.iterator(); iterator.hasNext();) {
			PontoFronteira solucao = (PontoFronteira) iterator.next();
			for(int i = 0; i<m; i++){
				psFronteira.print(new Double( solucao.objetivos[i])+ "\t");
			}
			psFronteira.println();
		}
	}
	
	public double distanciaEuclidiana(double[] vetor1, double[] vetor2){
		double soma = 0;
		for (int i = 0; i < vetor1.length; i++) {
			soma += Math.pow(vetor1[i]-vetor2[i],2);
		}
		return Math.sqrt(soma);
	}
	
	/**
	 * M�todo que obtem o joelho da uma fronteira de Pareto real
	 * Inicia tamb�m um array de double lambda contendo o intervalo de valores para cada objetivo das solucoes na fronteira de pareto
	 * O m�todo busca o ponto m�dio para cada dimensao do espaco de objetivos. O joelho � o ponto mais pr�ximo deste ponto m�dio. 
	 * @param n
	 * @return
	 */
	public double[] getJoelho(int n, ArrayList<PontoFronteira> fronteiraReal){
		
			joelho = new double[m];
			lambda = new double[m];
			
			if(fronteiraReal ==null){
				//N�mero de solucoes na fronteira
				int numSol = 10000;
				//Obt�m a fronteira de pareto real para o problema
				ArrayList<SolucaoNumerica> solucoes = obterFronteira(n, numSol);
				
				fronteiraReal = new ArrayList<PontoFronteira>();
				for (Iterator<SolucaoNumerica> iterator = solucoes.iterator(); iterator
						.hasNext();) {
					SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator
							.next();
					PontoFronteira pf = new PontoFronteira(solucaoNumerica.objetivos);
					fronteiraReal.add(pf);
					
				} 
			}

			double maxValorObjetivo ,minValorObjetivo; 
			
			//Ponto m�dio da fronteira real
			double[] pontoCentral = new double[m];
			
			
			//Percorre todos as dimensoes buscando o ponto m�dio
			for (int i = 0; i < m; i++) {
				ComparetorObjetivoPF comp = new ComparetorObjetivoPF(i);
				Collections.sort(fronteiraReal, comp);
				//Busca os valores m�ximo e m�nimo para o objetivo i na fronteira real
				minValorObjetivo = (fronteiraReal.get(0)).objetivos[i];
				maxValorObjetivo = (fronteiraReal.get(fronteiraReal.size()-1)).objetivos[i];
				//Calcula o intervalo para o objetivo
				lambda[i] = (maxValorObjetivo - minValorObjetivo);
				//Calcula o ponto m�dio para o objetivo
				pontoCentral[i] = ((maxValorObjetivo - minValorObjetivo)/2.0) + minValorObjetivo;
			}

			double menorDistancia = Double.MAX_VALUE;
			int indiceMenorDistancia = -1;

			int i = 0;
			//Busca o ponto da fronteira real mais pr�ximo do ponto m�dio
			for (Iterator<PontoFronteira> iterator = fronteiraReal.iterator(); iterator.hasNext();) {
				PontoFronteira pf = iterator.next();

				double dist = distanciaEuclidiana(pontoCentral, pf.objetivos);
				if(dist < menorDistancia){
					menorDistancia = dist;
					indiceMenorDistancia = i;
				}
				i++;
			}



			PontoFronteira j = (fronteiraReal.get(indiceMenorDistancia));

			joelho = new double[m];

			for (int k = 0; k < m; k++) {
				joelho[k] = j.objetivos[k];
			}


		return joelho;
		}
	
	public double[] getLambda(int n, ArrayList<PontoFronteira> fronteiraReal){
		if(lambda == null)
			getJoelho(n, fronteiraReal);
		return lambda;
	}
	
		
	public double[] obterLimites(ArrayList<SolucaoNumerica> fronteiraReal){
		double limites[] = new double[m];
		for (int i = 0; i < limites.length; i++) {
			limites[i] = Double.NEGATIVE_INFINITY;
		}
		for (Iterator<SolucaoNumerica> iterator = fronteiraReal.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
			for (int i = 0; i < m; i++) {
				if(solucaoNumerica.objetivos[i]>limites[i])
					limites[i] = solucaoNumerica.objetivos[i];
			}
		}
		
		return limites;
	}
	
	public void normalizarFronteira(double[] limites, ArrayList<SolucaoNumerica> fronteira){
		for (Iterator<SolucaoNumerica> iterator = fronteira.iterator(); iterator.hasNext();) {
			SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
			for(int i = 0; i<m; i++){
				solucaoNumerica.objetivos[i] = solucaoNumerica.objetivos[i]/limites[i];
			}
		}
		
	}
	
	/**
	 * Obtains a set of solutions near the edges of the Pareto front
	 * @param n Decision variables
	 * @param numSol Solutions on the PFTrue
	 * @param s Number of solutions for each dimension
	 * @return
	 */
	public ArrayList<SolucaoNumerica> obterSolucoesExtremas(int n, int numSol,int s) {
		ArrayList<SolucaoNumerica> retorno = new ArrayList<SolucaoNumerica>();
		

		//To Obtain PFtrue
		ArrayList<SolucaoNumerica> pftrue = obterFronteira(n, numSol);
		for(int i = 0; i<m; i++){
			ComparetorObjetivo comp = new ComparetorObjetivo(i);
			Collections.sort(pftrue, comp);
			for(int j = 1; j<=s; j++){
				retorno.add(pftrue.get(j));
			}
		}
		
	
		return retorno;
		
		
	}
	
	public ArrayList<PontoFronteira> obterSolucoesExtremas(int n, int numSol,int s, ArrayList<PontoFronteira> front) {
		ArrayList<PontoFronteira> retorno = new ArrayList<PontoFronteira>();
		

		//To Obtain PFtrue
		
		for(int i = 0; i<m; i++){
			ComparetorObjetivoPF comp = new ComparetorObjetivoPF(i);
			Collections.sort(front, comp);
			for(int j = 1; j<=s; j++){
				retorno.add(front.get(j));
			}
		}
		
	
		return retorno;
		
		
	}
	
	/**
	 * Obtains a set of solutions near the knee of the Pareto front
	 * @param n Decision variables
	 * @param numSol Solutions on the PFTrue
	 * @param s Number of solutions next the knee
	 * @return
	 */
	public ArrayList<SolucaoNumerica> obtainSolutionsKnee(int n, int numSol,int s) {
		ArrayList<SolucaoNumerica> retorno = new ArrayList<SolucaoNumerica>();
		
		//To Obtain PFtrue
				ArrayList<SolucaoNumerica> pftrue = obterFronteira(n, numSol);
		
		//Obtains the knee of the Pareto Front
		getJoelho(n, null);
		 	
		
		for(int i = 0; i<numSol; i++){
			Solucao temp = pftrue.get(i);
			double dist = distanciaEuclidiana(temp.objetivos, joelho);
			temp.rank = dist;
		}
		
		ComparetorRank comp = new ComparetorRank();
		Collections.sort(pftrue, comp);
		for(int j = 1; j<=s; j++){
			retorno.add(pftrue.get(j));
		}
		return retorno;
		
		
	}
	
	public ArrayList<PontoFronteira> obtainSolutionsKnee(int n, int numSol,int s, ArrayList<PontoFronteira> front) {
		ArrayList<PontoFronteira> retorno = new ArrayList<PontoFronteira>();
		
		
		
		//Obtains the knee of the Pareto Front
		getJoelho(n, front);
		 	
		
		for(int i = 0; i<numSol; i++){
			PontoFronteira temp = front.get(i);
			double dist = distanciaEuclidiana(temp.objetivos, joelho);
			temp.rank = dist;
		}
		
		ComparetorRankPF comp = new ComparetorRankPF();
		Collections.sort(front, comp);
		for(int j = 1; j<=s; j++){
			retorno.add(front.get(j));
		}
		return retorno;
		
		
	}
	


}
