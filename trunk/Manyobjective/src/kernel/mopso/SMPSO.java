package kernel.mopso;






import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import archive.HyperPlaneReferenceArchiveRandom;


import pareto.FronteiraPareto;
import problema.Problema;
import solucao.Solucao;




/**
 * Classe que implementa o algoritmo SMPSO.
 * @author Andr� B. de Carvalho
 *
 */
public class SMPSO extends MOPSO{

	
	public final double INDICE_MUTACAO = 0.15;
			
	public SMPSO(int n, Problema prob, int g, int a, int t, String s, String[] maxmim, int tamRep, String tRank, double smax, String tPoda, String el, double eps, boolean eval_analysis){
		super(n,prob,g,a,t,s, maxmim, tRank, smax, el, eps, tamRep, tPoda, eval_analysis);			
	}
	
	public SMPSO(int n, Problema prob, int g, int a, int t, String s, String[] maxmim, int tamRep, String tRank, double smax, String tPoda, String el, double eps, FronteiraPareto pareto, boolean eval_analysis){
		super(n,prob,g,a,t,s, maxmim, tRank, smax, el, eps, tamRep, tPoda, pareto, eval_analysis);			
	}
		
	/**
	 * M�todo principal que executa as opera�oes do MOPSO
	 */
	public ArrayList<Solucao> executar(){
		
//		teste();
		//Apaga todas as listas antes do inicio da execucao
		reiniciarExecucao();
		
		//iniciarPopulacaoTeste();
		//rankParticula(populacao);
		
		//Inicia a populcaao
		inicializarPopulacao();
		
		//Obtem as melhores partaculas da populacao
					
		if(!rank)
			atualizarRepositorio();
		else
			iniciarRepositorioRank();;	
	
		
		calcularCrowdingDistance(pareto.getFronteira(), problema.m);
		//Obtem os melhores globais para todas as particulas da populacao
		escolherLider.escolherLideres(populacao, pareto.getFronteira());
		
		//double exploiting = geracoes -  Math.round(geracoes/10);
		
		
		escolherParticulasMutacao();
		//Inicia o laco evolutivo
		
		for(int i = 0; i<geracoes; i++){
			if(i%10 == 0)
				System.out.print(i + " ");
			if(i % 100 ==0)
				System.out.println();
			//if(i>exploiting && !escolherLider.id.equals("exploiting"))
				//escolherLider = new EscolherExploiting(problema.m);
			lacoEvolutivo();
			
			if(archiver.ID.equals("hyp_r")){
				HyperPlaneReferenceArchiveRandom h = (HyperPlaneReferenceArchiveRandom) archiver;
				h.changeReferencePoint(i);
			}
			
			/*Calculo do GD para cada iteracao
			 * ArrayList<PontoFronteira> pf_i = new ArrayList<PontoFronteira>();
			for (Iterator iterator = pareto.getFronteira().iterator(); iterator.hasNext();) {
				Solucao solucao = (Solucao) iterator
						.next();
				PontoFronteira pf = new PontoFronteira(solucao.objetivos);
				pf_i.add(pf);
			}
			
			pfs.add(pf_i);*/
		}
		
		/*
		 * Calculo do GD para cada iteracao
		 * try{
			ArrayList<PontoFronteira> pftrue= Principal.carregarFronteiraPareto(System.getProperty("user.dir"), problema.problema.toUpperCase(), problema.m);
			
			String dir = System.getProperty("user.dir") + "/medidas/";
			GD ind = new GD(problema.m, dir, "gd_" + problema.m, pftrue);
			SD ind2 = new SD(problema.m, dir, "sd_" + problema.m, pftrue);
			
			String[] maxmimObjetivos = new String[problema.m];
			for (int i = 0; i < maxmimObjetivos.length; i++) {
				maxmimObjetivos[i] = "-";
			}
			
			ind.preencherObjetivosMaxMin(maxmimObjetivos);
			ind2.preencherObjetivosMaxMin(maxmimObjetivos);

			ind.calcularIndicadorArray(pfs);
			ind2.calcularIndicadorArray(pfs);
		} catch(IOException ex){ex.printStackTrace();}
		 */
		
		//removerGranularRaio(pareto.getFronteira());
		
		if(eval_analysis)
			evaluationAnalysis(pareto.getFronteira());
		

		// Imprimir populacao e fronteira
		/*try{

			PrintStream psPopulacao = new PrintStream("populacao.txt");

			for (Iterator iterator = populacao.iterator(); iterator.hasNext();) {
				Particula particula = (Particula) iterator.next();

				for(int i = 0; i<problema.m; i++){
					psPopulacao.print(particula.solucao.objetivos[i] + "\t");
				}

				psPopulacao.println();

			}


			PrintStream psFronteira = new PrintStream("fronteira.txt");

			for (Iterator iterator = pareto.getFronteira().iterator(); iterator.hasNext();) {
				SolucaoNumerica solucao = (SolucaoNumerica) iterator.next();

				for(int i = 0; i<problema.m; i++){
					psFronteira.print(solucao.objetivos[i] + "\t");
				}

				psFronteira.println();

			}

		} catch(IOException ex){ex.printStackTrace();}*/
		
		return pareto.getFronteira();
		
	}
	
	public ArrayList<Solucao> executarAvaliacoes(){
		//Apaga todas as listas antes do inicio da execucao
		reiniciarExecucao();
		//Inicia a populcao
		inicializarPopulacao();
		//Obtem as melhores particulas da populacao
		if(!rank)
			atualizarRepositorio();
		else
			iniciarRepositorioRank();
		calcularCrowdingDistance(pareto.getFronteira(), problema.m);
		//Obtem os melhores globais para todas as particulas da populacao
		escolherLider.escolherLideres(populacao, pareto.getFronteira());
		
		//double exploiting = numeroavalicoes -  Math.round(numeroavalicoes/1.5);
		//System.out.println(exploiting);

		escolherParticulasMutacao();
		//Inicia o laco evolutivo
		while(problema.avaliacoes < numeroavalicoes){
			if(problema.avaliacoes%5000 == 0)
				System.out.print(problema.avaliacoes + " ");
			
		//	if(problema.avaliacoes>exploiting && !escolherLider.id.equals("exploiting"))
		//		escolherLider = new EscolherExploiting(problema.m);
			lacoEvolutivo();
			
		}

		//removerGranularRaio(pareto.getFronteira());
		
		if(eval_analysis)
			evaluationAnalysis(pareto.getFronteira());
	
		return pareto.getFronteira();
		
	}

	protected void lacoEvolutivo() {
		
		if(eval_analysis)
			evaluationAnalysis(pareto.getFronteira());
		//Itera sobre todas as particulas da populacao
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = (Particula) iter.next();
			//Calcula a nova velocidade
			
			//particula.calcularNovaVelocidade();
			particula.calcularNovaVelocidadeConstriction();
			//particula.calcularNovaVelocidade();
			//Calcula a nova posicao
			particula.calcularNovaPosicao();
			if(particula.mutacao){
				mutacaoPolinomial(PROB_MUT_COD,particula.posicao);
				particula.mutacao = false;
			}
			
			particula.truncar();
			//Avalia a particula
			problema.calcularObjetivos(particula.solucao);
			particula.solucao.arredondar();
			//Define o melhor local
			particula.escolherLocalBest(pareto);
		}		
		if(rank || archiver.ID.equals("par") || archiver.ID.equals("pbr"))
			rankParticula(populacao);
		//Obtem as melhores particulas da populacao
		atualizarRepositorio();
		
		//System.out.println(pareto.getFronteira().size());
		//removerGranularLimites(pareto.getFronteira());
		// System.out.println(" -  " + pareto.getFronteira().size());
		
		
		calcularCrowdingDistance(pareto.getFronteira(), problema.m);

		//filter();
		//System.out.print (pareto.getFronteira().size()  + " - ");
		
		//populacaoNoRepositorio();
		
		//System.out.println(pareto.getFronteira().size());

		//Escolhe os novos melhores globais
		escolherLider.escolherLideres(populacao, pareto.getFronteira());
		
		escolherParticulasMutacao();
		
	}
	
	/**
	 * M�todo que define quais particulas da popula��o sofrer�o mutacao
	 * AS particulas que forem domindas por mais particulas ser�o escolhidas
	 */
	public void escolherParticulasMutacao(){
		ComparetorDominacaoParticula comp = new ComparetorDominacaoParticula();
		Collections.sort(populacao, comp);
		int numMutacao = (int)(populacao.size()*INDICE_MUTACAO);
		for(int i = 1; i<=numMutacao;i++){
			Particula part = (Particula)populacao.get(populacao.size()-i);
			part.mutacao = true;
		}
	}
	

	

	public void iniciarRepositorioRank(){
		ComparetorRankParticula comp = new ComparetorRankParticula();
		Collections.sort(populacao, comp);
		for(int i = 0; i<(archiveSize); i++){
			Particula particula = populacao.get(i);
			pareto.getFronteira().add((Solucao)particula.solucao.clone());
		}
		
		
	}
	
	/*public void iniciarPopulacaoTeste(){
		int sl = 2;
		ArrayList<SolucaoNumerica> temp =  problema.obterSolucoesExtremas(n, sl);
		
		for (Iterator<SolucaoNumerica> iterator = temp.iterator(); iterator.hasNext();) {
			Particula particula = new Particula();
			SolucaoNumerica solucaoNumerica = (SolucaoNumerica) iterator.next();
			particula.iniciarParticulaAleatoriamente(problema, solucaoNumerica);
			problema.calcularObjetivos(solucaoNumerica);
			particula.localBestObjetivos = particula.solucao.objetivos;
			populacao.add(particula);
				
		}
		
		for(int i = 0; i<3; i++){
			Particula particula = new Particula();
			//Contador utilizada para a cria��o da regra n�o ficar presa no la�o
			int cont = 0;
			do{
				SolucaoNumerica s = new SolucaoNumerica(n, problema.m);
				s.iniciarSolucaoAleatoria();
				particula.iniciarParticulaAleatoriamente(problema, s);
				problema.calcularObjetivos(s);
				cont++;
			}while(populacao.contains(particula) && (cont<20));
			//Avaliando os objetivos da particula;
			particula.localBestObjetivos = particula.solucao.objetivos;
			populacao.add(particula);	
		}
		
		if(rank)
			rankParticula(populacao);
	}*/
	
	

}
