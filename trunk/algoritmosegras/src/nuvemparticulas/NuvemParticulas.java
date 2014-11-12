package nuvemparticulas;

import java.util.ArrayList;
import java.util.Iterator;

import apriori.ObterRegrasApriori;

import regra.Regra;
import kernel.DadosExperimentos;
import kernel.FronteiraPareto;
import kernel.ObterRegras;

/**
 * Classe que implementa o algoritmo de aprendizado de regras baseado na meta-heuristica
 * da Otimiza��o por nuvem de part�culas multi-objetivo que trabalha com atributos n�mericos e
 * discretos, MOPSO-N.
 * @author Andr� B. de Carvalho
 *
 */
public class NuvemParticulas extends ObterRegras{

	//Arraylist que cont�m as particulas da execu��o do algoritmo
	public ArrayList<Particula> populacao = null;
	//Arraylist que representa o reposit�rio com as solu��es n�o dominadas
	public ArrayList<Particula> repositorio = null;
	
	//Arrays utilizados na hibridizado do apriorio com o MOPSO-N
	public ArrayList<Regra>[] repositorios = null;
	public ArrayList<Regra> repositorioInicial = null;
	
	//N�mero de execu��es do mopso-n
	public int geracoes;
	//Tamanho inicial da popula��o
	public int tamanhoPopulacao;
	//Flag q indica se os atributos nominais vao ser combinados ou nao
	public boolean combinacao = false;
	
	
	public NuvemParticulas(int g, int t, String[] objs, boolean comb){
		super(objs);
		geracoes = g;
		tamanhoPopulacao = t;
		populacao = new ArrayList<Particula>();
		repositorio = new ArrayList<Particula>();
		paretoPos = new FronteiraPareto();
		dadosExperimento = new DadosExperimentos();
		combinacao = comb;
	}
	
	/**
	 * M�todo que limpa as listas do algoritmo e o prepara para uma nova execu��o.
	 *
	 */
	public void reiniciarExecucao(){
		populacao = new ArrayList<Particula>();
		repositorio = new ArrayList<Particula>();
		paretoPos = new FronteiraPareto();
	}
	
	/**
	 * M�todo principal que executa as opera�oes do MOPSO-N
	 * @param classe Classe a qual ser�o obtidas as regras
	 * @return Regras obtidas para a classe passado como par�metro a partir de um conjunto de dados
	 * previamente carregado
	 * @throws Exception Lan�ada caso haja algum erro no arquivo de dados.
	 */
	public ArrayList<Regra> executar(int classe) throws Exception{
		//Apaga todas as listas antes do inicio da execu��o
		reiniciarExecucao();
		//Inicia a popul�ao
		inicializarPopulacao(classe);
		/*if(repositorioInicial == null)
			atualizarRepositorio();
		else
			preencherRepositorioRegras(classe);
		*/
		//Obt�m as melhores part�culas da popula��o
		atualizarRepositorio();		
		//Obt�m os melhores globais para todas as part�culas da popula��o
		dividirEspacoBusca();
		//In�cia o la�o evolutivo
		for(int i = 0; i<geracoes; i++){
			if(verbose){
				if(i%10 == 0)
					System.out.print(i + " - " + geracoes + " ");
			}
			//Itera sobre todas as part�culas da popula��o
 			for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
				Particula particula = iter.next();
				//Calcula a nova velocidade
				particula.calcularNovaVelocidade(i);
				//Calcula a nova posi��o
				particula.calcularNovaPosicao();
				//Avalia a part�cula
				preencherMatrizContigencia(particula.regra,dados);
				//Define o melhor local
				particula.escolherLocalBest();
			}
 			//Obt�m as melhores particulas da popula��o
			atualizarRepositorio();
			//Retira redund�cias e part�culas vazias do reposit�rio
			limparRepositorio();
			//Escolhe os novos melhores globais
			dividirEspacoBusca();
		}
		limparRepositorio();
		//Obt�m as regras a partir das part�culas do reposit�rio
		ArrayList<Regra> regrasRetorno = new ArrayList<Regra>();
		for (Iterator<Particula> iter = repositorio.iterator(); iter.hasNext();) {
			Particula particula = iter.next();
			regrasRetorno.add(particula.regra);
		}
		return regrasRetorno;
	}
	
	/**
	 * M�todo que executa o aprendizado de regras do m�todo executar para as duas classes.
	 */
	public ArrayList<Regra> obterRegras(int numClasses) throws Exception {
		
		//Caso haja uma hibridiza��o com o apriori
		if(repositorios != null)
			repositorioInicial = repositorios[numFold];
		
		ArrayList<Regra> temp = null;
		regras = new ArrayList<Regra>();
		
		for(int i=0; i<numClasses; i++){
			temp = executar(i);
			regras.addAll(temp);
			temp.clear();
		}
		return regras;
	}
	

	/*public void iniciarRepositorios(String nomeBase, String caminhoBase, int numFolds, double suporte, double confianca, int numRegras) throws Exception{
		System.out.println("Executando Apriori");
		repositorios = new ArrayList[numFolds];
		for(int i = 0; i<numFolds; i++){
			System.out.println("Fold: "+ i);	
			String arquivoTreinamento = caminhoBase + nomeBase + "/it"+i+"/" + nomeBase + "_data.arff";
			repositorios[i] = executarApriori(arquivoTreinamento, 0,1, suporte,confianca, numRegras);
		}
	}*/
	
	/**
	 * M�todos que executam o apriori quando h� a hibridiza��o com o MOPSO-N
	 */
	public ArrayList<Regra> executarApriori(String arquivoARFF,int cPositiva, int cNegativa, double suporte, double confianca, int numRegras, String[] objs) throws Exception{
		ObterRegrasApriori apri = new ObterRegrasApriori(numRegras,0.1,0.1,1.0,0.05, true, objs);
		apri.objetivos = objetivos;
		apri.carregarInstancias(arquivoARFF, cPositiva, cNegativa);
		return  apri.obterRegras(2);
	}
	
	public void preencherRepositorioRegras(int classe){
		for (Iterator<Regra> iter = repositorioInicial.iterator(); iter.hasNext();) {
			Regra regra = iter.next();
			
			if(regra.cabeca == classe){
				Particula particula = new Particula();
				Regra regraParticula = (Regra)regra.clone();
				particula.iniciarParticulaAleatoriamente(objetivos, regraParticula);
				preencherMatrizContigencia(particula.regra, dados);
				
				particula.localBestObjetivos = particula.regra.getValoresObjetivos();
				populacao.add(particula);
				
				paretoPos.add_NuvemParticulas(particula, repositorio);
			}
		}
		if(repositorio.size() == 0){
			atualizarRepositorio();
		}
	}

	
	/***
	 * M�todo que retira as regras repetidas do repositorio
	 *
	 */
	public void limparRepositorio(){
		ArrayList<Particula> temp = new ArrayList<Particula>();
		ArrayList<Regra> tempRegras = new ArrayList<Regra>();
		for (Iterator<Particula> iter = repositorio.iterator(); iter.hasNext();) {
			Particula part = iter.next();
			if(!temp.contains(part) && !part.regra.isEmpty()){
				temp.add(part);
				tempRegras.add(part.regra);
			}

		}
		if(temp.size()!=repositorio.size()){
			repositorio.clear();
			for (Iterator<Particula> iter = temp.iterator(); iter.hasNext();) {
				Particula part = iter.next();
				repositorio.add(part);
			}
			paretoPos.setFronteira(tempRegras);
		}
	}
	
	/**
	 * M�todo que inicia a popula��o de part�culas. 
	 * Os dados j� devem estar carregados.
	 */
	@SuppressWarnings({ "unchecked" })
	public void inicializarPopulacao(int classe){
		
		for(int i = 0; i<tamanhoPopulacao; i++){
			Particula particula = new Particula();
			//Contador utilizada para a cria��o da regra n�o ficar presa no la�o
			int cont = 0;
			do{
				Regra r = null;
				//Caso seja selecionado a opcao por combinacao de atributos nominais e executado o metodo de
				//geracao de regras combinadas, caso nao e executado o metodo que utiliza a proporcao de 
				//cada valor para preencher as regras iniciais
				if(!combinacao)
					 r = gerarRegraAleatoriaProporcional(dados.enumerateAttributes(), dados.classAttribute(), dados.numAttributes(), classe);
				else
					 r = gerarRegraAleatoriaCombinacao(dados.enumerateAttributes(), dados.classAttribute(), dados.numAttributes(), classe);
				
				particula.iniciarParticulaAleatoriamente(objetivos, r);
				preencherMatrizContigencia(particula.regra, dados);
				cont++;
			}while(populacao.contains(particula) && (cont<20));
			//Avaliando os objetivos da particula;
			particula.localBestObjetivos = particula.regra.getValoresObjetivos();
			populacao.add(particula);	
		}
	}
	
	/**
	 * M�todo que preenche o respositorio com as solu��o n�o dominadas
	 *
	 */
	public void atualizarRepositorio(){
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = iter.next();
			if(!repositorio.contains(particula) && !particula.regra.isEmpty())
				paretoPos.add_NuvemParticulas(particula, repositorio);
		}
	}
	
	/**
	 * M�todo que escolhe para cada particula da populacao uma particula presente no repositorio
	 *
	 */
	public void dividirEspacoBusca(){
		for (Iterator<Particula> iter = repositorio.iterator(); iter.hasNext();) {
			Particula partRepositorio = iter.next();
			partRepositorio.calcularSigmaVector();
		}
		
		for (Iterator<Particula> iter = populacao.iterator(); iter.hasNext();) {
			Particula particula = iter.next();
			particula.calcularSigmaVector();
			particula.escolherGlobalBest(repositorio);
		}
	}
	
	public static void main(String[] args) {
		
		
		/*String nomebase = args[0];
		String caminhoBase = args[1];
		
		NuvemParticulas nuvemParticulas = new NuvemParticulas(50,500, null);
		try{
			
			nuvemParticulas.executarFolds(nomebase, caminhoBase, "pso", 0, 1,10,false,"confidence", false);
			
		} catch (Exception e){e.printStackTrace();}
		*/
	}
	

}
