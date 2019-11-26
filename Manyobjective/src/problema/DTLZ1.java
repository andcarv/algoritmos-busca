package problema;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.Random;

import pareto.FronteiraPareto;


import solucao.Solucao;
import solucao.SolucaoNumerica;

/**
 * Classe que representa o problema DTLZ1
 * @author andre
 *
 */

public class DTLZ1 extends Problema {

	/**
	 * Construtor da classe
	 * @param m Numero de objetivos do problema
	 */



	public DTLZ1(int m, int k){
		super(m);
		n = m + k - 1;
		problema = "dtlz1";
	}

	/**
	 * Metodo que calcula os objetivos da solucao passada como parametro
	 * Equacao 7 do artigo "Scalable Multi-Objective Optimization Test Problems"
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
			g += (x[i] - 0.5)*(x[i] - 0.5) - Math.cos(20.0 * Math.PI * ( x[i] - 0.5));

		g = 100 * (k + g);        
		for (int i = 0; i < m; i++)
			f[i] = (1.0 + g) * 0.5;

		for (int i = 0; i < m; i++){
			for (int j = 0; j < m - (i + 1); j++)            
				f[i] *= x[j];                
			if (i != 0){
				int aux = m - (i + 1);
				f[i] *= 1 - x[aux];
			} //if
		}//for

		for (int i = 0; i < m; i++)
			solucao.objetivos[i] = f[i];


		avaliacoes++;
		return solucao.objetivos;
	}

	public double[] calcularObjetivos2(Solucao sol) {
		SolucaoNumerica solucao = (SolucaoNumerica) sol;
		if(solucao.objetivos == null)
			solucao.objetivos = new double[m];

		double g = g1(solucao.getVariaveis());
		//System.out.println(g);

		for(int i = 0; i<m; i++){
			double fxi = 0.5*(1+g);
			int j;
			for(j = 0; j<(m-1-i);j++){
				fxi*=solucao.getVariavel(j);
			}
			if(j!=m-1){
				fxi *= (1-solucao.getVariavel(j));
			}
			solucao.objetivos[i] = fxi;
		}

		avaliacoes++;
		return solucao.objetivos;
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
				melhor.setVariavel(i, 0.5);
			}

			for (int i = 0; i < m-1; i++) {
				double newVal = rand.nextDouble();
				melhor.setVariavel(i, newVal);
			}

			double somaParcial = 0;
			calcularObjetivos(melhor);

			for (int i = 0; i < melhor.m; i++) {
				somaParcial += melhor.objetivos[i];
			}
			if(somaParcial==0.5){
				if(!pareto.getFronteira().contains(melhor))
					pareto.add(melhor, archiver);	
			}

		}

		ArrayList<SolucaoNumerica> saida = new ArrayList<SolucaoNumerica>();
		for (Iterator<Solucao> iterator = pareto.getFronteira().iterator(); iterator.hasNext();) {
			SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
			saida.add(solucaoNumerica);
		}


		return saida;	
	}

	public  ArrayList<SolucaoNumerica> obterFronteiraIncremental(int n, double inc){

		ArrayList<SolucaoNumerica> melhores = new ArrayList<SolucaoNumerica>();

		Random rand = new Random();
		rand.setSeed(1000);

		//Indicies que indicam que variaves serão geradas incrementalmente para a geracao da fronteira
		//O padrao dos problemas DTLZ eh entre 0 e m-2
		int inicio = 0;
		int fim = m-2;

		SolucaoNumerica solucaoBase = new SolucaoNumerica(n, m);

		varVez = fim;

		for (int i = m-1; i <n; i++) {
			solucaoBase.setVariavel(i, 0.5);
		}

		boolean haSolucao = true;

		double eps = 0;

		int tamanhoRepositorio = Integer.MAX_VALUE;

		ArrayList<double[]> objetivos = new ArrayList<double[]>();

		while(haSolucao){

			if(melhores.size()%1000 == 0)
				System.out.println(melhores.size());

			haSolucao = getProximaSolucao(solucaoBase, inicio, fim, inc);
			SolucaoNumerica melhor = (SolucaoNumerica)solucaoBase.clone();



			double somaParcial = 0;
			calcularObjetivos(melhor);



			for (int i = 0; i < melhor.m; i++) {
				somaParcial += melhor.objetivos[i];
			}
			if(somaParcial==0.5){
				int cont = 0;
				//Só insere na fronteira pontos com vetores objetivo diferente. Se um vetor objetivo já existe no array de melhores, descarta a nova solução
				//Isso é feito para não haver redundância na fronteira
				boolean igual = true;
				for (Iterator iterator = objetivos.iterator(); iterator.hasNext();) {
					double[] ds = (double[]) iterator.next();
					igual = true;
					for (int i = 0; i < ds.length; i++) {
						if(ds[i]!=melhor.objetivos[i]) {
							igual = false;
							break;
						}	
					}
					if(igual)
						break;
				}
				
				if(!igual || objetivos.size()==0) {
					objetivos.add(melhor.objetivos);
					melhores.add(melhor);
				}
			}

		}


		/*ArrayList<SolucaoNumerica> saida = new ArrayList<SolucaoNumerica>();
		for (Iterator<Solucao> iterator = pareto.getFronteira().iterator(); iterator.hasNext();) {
			SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
			saida.add(solucaoNumerica);
		}*/

		return melhores;	
	}


	public static void main(String[] args) {
		int m = 3;
		int numSol = 200;
		int n = 12;
		int k = n - m + 1;

		DTLZ1 dtlz1 = new DTLZ1(m, k);

		//ArrayList<SolucaoNumerica> f = dtlz2.obterFronteira(n, numSol);
		ArrayList<SolucaoNumerica> f = dtlz1.obterFronteiraIncremental(n,0.125);

		try {
			PrintStream pp = new PrintStream("DTLZ1-pareto_"+ m   +".txt");
			PrintStream ps = new PrintStream("DTLZ1-solutions_"+ m  +".txt");

			for (Iterator iterator = f.iterator(); iterator.hasNext();) {
				SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
				for( double sol: solucaoNumerica.getVariaveis()) {
					ps.append(sol + "\t");
				}
				ps.append("\n");
				for( double fun: solucaoNumerica.objetivos) {
					pp.append(fun + "\t");
				}
				pp.append("\n");
			}

			pp.flush();
			pp.close();

			ps.flush();
			ps.close();


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main2(String[] args) {


		int[] ms = {4,5,6,7,8,9};
		int numSol = 100000;
		int k = 5;

		System.out.println("DTLZ1");
		for (int i = 0; i < ms.length; i++) {

			int m = ms[i];

			System.out.println(m);

			int n = m + k - 1;

			//int decimalPlace = 5;
			DTLZ1 dtlz1 = new DTLZ1(m, k);




			ArrayList<SolucaoNumerica> f = dtlz1.obterFronteira(n, numSol);

			try{
				PrintStream ps = new PrintStream("pareto/DTLZ1_" + m + "_pareto.txt");
				for (Iterator<SolucaoNumerica> iterator = f.iterator(); iterator.hasNext();) {
					SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator
							.next();
					for(int j = 0; j<m; j++){
						ps.print(solucaoNumerica.objetivos[j] + "\t");
					}
					ps.println();


				}
				ps.close();
			} catch (IOException ex){ex.printStackTrace();}

		}





		/*int m = 2;
		//int numSol = 1000;
		int k = 10;
		int n = m + k - 1;

		int decimalPlace = 5;
		DTLZ1 dtlz1 = new DTLZ1(m);

		dtlz1.inc = 0.001;

		//dtlz7.obterFronteira2(n, numSol);


		ArrayList<SolucaoNumerica> f = dtlz1.obterFronteiraIncremental(n);
		//ArrayList<SolucaoNumerica> f = dtlz1.obterFronteira(n, numSol);
		ComparetorObjetivo comp = new ComparetorObjetivo(0);
		Collections.sort(f,comp);

		try{
			PrintStream ps = new PrintStream("fronteiras/fronteira_dtlz1_inc" + m);
			PrintStream psSol = new PrintStream("fronteiras/solucoes_dtlz1_inc" + m);
			for (Iterator<SolucaoNumerica> iterator = f.iterator(); iterator.hasNext();) {
				SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator
						.next();


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
		} catch (IOException ex){ex.printStackTrace();}*/
	}


}
