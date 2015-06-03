package kernel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeSet;

import objetivo.Especificidade;
import objetivo.FuncaoObjetivo;
import objetivo.Laplace;
import objetivo.Novidade;
import objetivo.Sensitividade;
import regra.Atributo;
import regra.AtributoCombinado;
import regra.AtributoNominal;
import regra.AtributoNumerico;
import regra.Regra;
import votacao.Votacao;
import votacao.VotacaoConfidence;
import votacao.VotacaoConfidenceLaplace;
import votacao.VotacaoConfidenceLaplaceOrdenacao;
import votacao.VotacaoEspecificidadeOrdenacao;
import votacao.VotacaoGeral;
import votacao.VotacaoSensitividadeOrdenacao;
import votacao.VotacaoSimples;
import votacao.VotacaoSupportConfidenceOrdenacao;
import weka.classifiers.meta.Bagging;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Classe abstrata que representa um algoritmo de obtenï¿½ï¿½o de regras. Classe
 * principal que contï¿½m os mï¿½todos e atributos necessï¿½rios para
 * obtenï¿½ï¿½o das regras.
 * 
 * @author Andrï¿½ B. de Carvalho
 * 
 */
public abstract class ObterRegras {
	private int medida = -1;
	private int ordenacao = -1;
	private int votacao = -1;
	/*
	 * CSA  -> support confidence 
	 * CONF -> Confidence 
	 * LP   -> La Place 
	 * Sens -> Sensitividade 
	 * Spec -> Especificidade 
	 * Knee -> Joelho 
	 * Extr -> Extremos ORD
	 * ORD  -> ORDENACAO
	 */
	public static final int CSA = 0;
	public static final int CONF = 1;
	public static final int LP = 2;
	public static final int SENS = 3;
	public static final int SPEC = 4;
	public static final int KNEE = 5;
	public static final int EXTR = 6;
	public static final int ORD = 7;

	// SortedSet<Double> limiares; // limiares utilizados na construï¿½ï¿½o do
	// grï¿½fico ROC

	// Arraylist que contï¿½m as regras encontradas pelos algoritmos de
	// obtenï¿½ï¿½o
	// de regras.
	public ArrayList<Regra> regras = null;
	// Obejto do framework weka que contï¿½m os dados de treinamento
	public Instances dados = null;

	// Objetos que contï¿½m as fronteiras de pareto para as classes positivas ou
	// negativas
	public FronteiraPareto paretoPos = null;
	public FronteiraPareto paretoNeg = null;

	// Matriz de confusï¿½o que ï¿½ calculada para cada execuï¿½ï¿½o
	public MatrizConfusao confusao;
	public MatrizConfusaoMultiClasse confusaoMultiClasse;

	// Objeto que cotem todas as informacoes os dados do experimento
	public DadosExperimentos dadosExperimento = null;

	// Objeto que ira conter o metodod e votacao passado como parametro da
	// execucao (Padrï¿½o Statement )
	public Votacao metodoVotacao = null;

	// String que contï¿½m o nome da classe positiva da execuï¿½ï¿½o (Ex:
	// positive ou
	// true )
	public String classePositiva;
	// String que contï¿½m o nome da classe negativa da execuï¿½ï¿½o (Ex:
	// negative ou
	// false )
	public String classeNegativa;

	// ArrayList que contï¿½m todas as classes da base
	public ArrayList<Classe> classes;

	// Contï¿½ma informaï¿½ï¿½o do fold corrente
	public int numFold;

	// Variï¿½vel que indica se vai haver ou nï¿½o impressï¿½o na tela e do log
	// do
	// andamento da execuï¿½ï¿½o
	public boolean verbose;

	// Matriz que contï¿½m o cï¿½lculo do coeficiente de Jaccard de cada regra
	// encontrada
	// Utilizado no mï¿½todo para a reduï¿½ï¿½o do nï¿½mero de regras. Nï¿½o
	// implementado
	public double[][] coeficienteJaccard = null;

	// Array que contï¿½m informaï¿½ï¿½es necessï¿½rias para o cï¿½lculo do
	// coeficiente de
	// Jaccard
	public int[][] distribuicaoAtributos = null;

	// Arraylist que contï¿½m todos os objetivos da busca
	public ArrayList<FuncaoObjetivo> objetivos = null;

	/**
	 * Mï¿½todo abstrato que deve ser implementado por todo algoritmo de
	 * obtenï¿½ï¿½o de regras a partir de uma base de dados previamente
	 * carregada.
	 * 
	 * @param cPositiva
	 *            Indice da classe positiva
	 * @param cNegativa
	 *            Indice da classe negativa
	 * @return As regras encontradas
	 * @throws Exception
	 */
	public abstract ArrayList<Regra> obterRegras(int numClasses)
			throws Exception;

	public ObterRegras(String[] objs) {
		addObjetivos(objs);
		classes = new ArrayList<Classe>();
	}

	/**
	 * Mï¿½todo que carrega o arquivo do tipo ARFF num objeto do tipo Instances
	 * 
	 * @param arquivoARFF
	 *            Caminho do arquivo ARFF
	 * @param indicePositiva
	 *            Indice que indica qual eh a classe positiva
	 * @param indiceNegativa
	 *            Indice que indica qual eh a classe negativa
	 * @throws Exception
	 *             Execï¿½ï¿½o lanï¿½ada se hï¿½ erro no acesso ao arquivo
	 */
	public void carregarInstancias(String arquivoARFF, int indicePositiva,
			int indiceNegativa) throws Exception {
		Reader reader = new FileReader(arquivoARFF);
		dados = new Instances(reader);
		dados.setClassIndex(dados.numAttributes() - 1);
		Attribute classe = dados.classAttribute();
		classePositiva = classe.value(indicePositiva);
		classeNegativa = classe.value(indiceNegativa);
		// preenche o arrayList classes com os nomes
		for (int i = 0; i < dados.numClasses(); i++) {
			Classe novaClasse = new Classe();
			novaClasse.setNome(classe.value(i));
			classes.add(novaClasse);
		}
	}

	/**
	 * Mï¿½todo que preencher a matriz de contingencia para todas as regras
	 * passadas como parametro atraves das instancias passadas como parametros
	 * 
	 * @param regras
	 *            Regras a serem preenchidas as matrizes de contigencias
	 * @param dados
	 *            Instancias utilizadas no prenchimendo da matriz
	 */
	public void preencherMatrizContigencia(ArrayList<Regra> regras,
			Instances dados) {

		for (Iterator<Regra> iter = regras.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			regra.matrizContigencia.limparMatrizContingencia();
			// Mï¿½todo que limpa a matriz de contingencia antes de preenche-la
			// regra.matrizContigencia.limparMatrizContingencia();
			for (int i = 0; i < dados.numInstances(); i++) {
				Instance instancia = dados.instance(i);
				regra.compararRegraContigencia(instancia);
			}
			regra.calcularValoresObjetivos();
		}
	}

	/**
	 * Mï¿½todo que preenche a matriz de continegï¿½ncia da regra passada como
	 * parametro
	 * 
	 * @param regra
	 *            Regra a ser preenchida a matriz de contigencia
	 * @param dados
	 *            Instancias utilizadas no prenchimendo da matriz
	 */
	public void preencherMatrizContigencia(Regra regra, Instances dados) {
		regra.matrizContigencia.limparMatrizContingencia();
		for (int i = 0; i < dados.numInstances(); i++) {
			Instance instancia = dados.instance(i);
			regra.compararRegraContigencia(instancia);
		}
		regra.calcularValoresObjetivos();

	}

	/**
	 * Mï¿½todo que percorre os dados da base e retorna o valor maximo e o valor
	 * minimo presentes na base para o atributo passabdo como parametro.
	 * 
	 * @param att
	 *            Tipo do atributo a ser calculado os valores maximo e minimo
	 * @return Array contendo os valores maximo e minimo
	 */
	public double[] obterMaximoMinimoAtributo(Attribute att) {
		Instance exemplo1 = dados.firstInstance();
		double max = exemplo1.value(att);
		double min = exemplo1.value(att);
		for (int i = 1; i < dados.numInstances(); i++) {
			Instance exemplo = dados.instance(i);
			double temp = exemplo.value(att);
			if (temp < min)
				min = temp;
			if (temp > max)
				max = temp;
		}
		double[] retorno = new double[2];
		retorno[0] = min;
		retorno[1] = max;
		return retorno;

	}

	/**
	 * Calcula a porcentagem da classe majoritï¿½rio dos dados passados como
	 * parï¿½metro
	 * 
	 * @param dados
	 *            Dados para o cï¿½lcula da classe majoritï¿½ria
	 * @return Vetor contendo o ï¿½ndice e a porcentagem da classe majoritï¿½ria
	 */
	public double[] obterClasseMajoritaria(Instances dados) {
		int positivo = 0;
		int negativo = 0;
		double classeMajoritaria = 0;
		double porcentagemClasseMajoritaria = 0;
		for (int i = 0; i < dados.numInstances(); i++) {
			Instance temp = dados.instance(i);
			if (temp.classValue() == temp.classAttribute().indexOfValue(
					classePositiva)) {
				positivo++;
			} else {
				negativo++;
			}
		}

		if (positivo > negativo) {
			classeMajoritaria = dados.classAttribute().indexOfValue(
					classePositiva);
			porcentagemClasseMajoritaria = (double) positivo
					/ (double) (positivo + negativo);
		} else {
			classeMajoritaria = dados.classAttribute().indexOfValue(
					classeNegativa);
			porcentagemClasseMajoritaria = (double) negativo
					/ (double) (positivo + negativo);

		}

		double[] retorno = new double[2];
		retorno[0] = classeMajoritaria;
		retorno[1] = porcentagemClasseMajoritaria;
		return retorno;

	}

	/**
	 * @author Matheus Rosendo Preenche o arrayList com os exemplos cobertos das
	 *         classes
	 * @param dados
	 *            Dados para o cï¿½lcula da classe majoritï¿½ria
	 * @param classes
	 *            array list de classes
	 */
	public void preencherArrayClasses(ArrayList<Classe> classes, Instances dados) {
		for (int i = 0; i < dados.numInstances(); i++) {
			Instance temp = dados.instance(i);
			for (Classe classe : classes) {
				if (temp.classValue() == temp.classAttribute().indexOfValue(
						classe.getNome())) {
					classe.addExemplosCobertos(1);
					break;
				}
			}
		}
	}

	/**
	 * @author Matheus Rosendo Retorna a classe de maior cobertura entre as
	 *         passadas como parametro
	 * @param classes
	 *            array list de classes
	 * @return Classe
	 */
	public Classe getClasseMaiorCobertura(ArrayList<Classe> classes) {
		int exemplos = 0;
		int indice = 0;
		for (Classe classe : classes) {
			if (classe.getExemplosCobertos() > exemplos) {
				exemplos = classe.getExemplosCobertos();
				indice = classes.indexOf(classe);
			}
		}
		return classes.get(indice);
	}

	/**
	 * Calcula a AUC a partir de um conjunto de dados passado como parï¿½metro
	 * 
	 * @param dadosTeste
	 *            Dados de teste para o cï¿½lculo da AUC
	 * @return O valor da AUC das regras para os dados passados como parï¿½metro
	 * @throws IOException
	 */
	public double obterAUC(Instances dadosTeste, ArrayList<Regra> regrasTeste)
			throws IOException {

		ArrayList<InstanceVotacao> votos = new ArrayList<InstanceVotacao>();
		TreeSet<Double> limiares = new TreeSet<Double>();
		obterRankingsVotacao(dadosTeste, regrasTeste, votos, limiares);
		double[][] d = construirROC(dadosTeste, votos, limiares);
		// System.out.println("limiares.size = " + limiares.size());

		PrintStream psROC = new PrintStream("roc_mopso");

		for (int i = 0; i < d[0].length; i++) {
			psROC.println(new Double(d[1][i]).toString().replace('.', ',')
					+ "\t" + new Double(d[0][i]).toString().replace('.', ','));
		}

		/*
		 * CurvaROC positive = new CurvaROC(d[0], d[1], "Curva ROC - Positive",
		 * limiares); positive.setVisible(true); positive.pack();
		 */

		// Calcular Area da classe positiva
		double area = calcularArea(d[1], d[0]);

		return area;

	}

	/**
	 * Mï¿½todo que cï¿½lcula a ï¿½rea da curva ROC pelo mï¿½todo os trapï¿½zios
	 * 
	 * @param vetorX
	 *            Vetor que contï¿½m as coordenadas dos pontos no eixo X
	 * @param vetorY
	 *            Vetor que contï¿½m as coordenadas dos pontos no eixo Y
	 * @return
	 */
	public double calcularArea(double[] vetorX, double[] vetorY) {

		double h;
		double area = 0;
		for (int i = 0; i < vetorX.length - 1; i++) {

			h = Math.abs(vetorX[i] - vetorX[i + 1]);

			if (h > 0) {
				double temp = ((vetorY[i + 1] + vetorY[i]) / 2.0) * h;
				area += temp;
			}

		}

		return area;
	}

	public void obterRankingsVotacao(Instances dadosTeste,
			ArrayList<Regra> regras, ArrayList<InstanceVotacao> votos,
			TreeSet<Double> limiares) {

		for (int i = 0; i < dadosTeste.numInstances(); i++) {
			Instance exemplo = dadosTeste.instance(i);
			double voto = metodoVotacao
					.votacao(regras, exemplo, classePositiva);
			InstanceVotacao temp = new InstanceVotacao(exemplo, voto);
			votos.add(temp);
			limiares.add(new Double(voto));
		}

	}

	/**
	 * Metodo que construi a curva ROC atraves da instancia de teste e das
	 * regras passadas como parametro. Mï¿½todo implementado a partir do artigo:
	 * ROC Graphs: Notes and Practical Considerations for Researchers
	 * 
	 * @param dadosTeste
	 *            Instancias de teste
	 * @param regras
	 *            Regras
	 * @return Array de double contendo os valores dos pontos da curva ROC para
	 *         a classe positiva e negativa. Indice 0 valores True Postive da
	 *         classe positiva Indice 1 valores False Postive da classe positiva
	 *         Indice 2 valores True Negative da classe negativa Indice 3
	 *         valores False Negative da classe negativa
	 */
	@SuppressWarnings("unchecked")
	public double[][] construirROC(Instances dadosTeste,
			ArrayList<InstanceVotacao> votos, TreeSet<Double> limiares) {

		int i = 0;
		for (Iterator iter = votos.iterator(); iter.hasNext(); i++) {
			InstanceVotacao element = (InstanceVotacao) iter.next();
			element.codigo = i;

		}
		Collections.sort(votos);
		// System.out.println("::::: construirROC() - limiares.size() = " +
		// limiares.size());

		double[][] classesSugeridasPositivos = new double[limiares.size()][dadosTeste
				.numInstances()];

		i = 0;
		for (Iterator iter = limiares.iterator(); iter.hasNext(); i++) {
			Double limiar = (Double) iter.next();
			int j = 0;
			for (Iterator iterator = votos.iterator(); iterator.hasNext(); j++) {
				InstanceVotacao temp = (InstanceVotacao) iterator.next();
				if (temp.votacao < limiar.doubleValue()) {
					classesSugeridasPositivos[i][j] = temp.exemplo
							.classAttribute().indexOfValue(classeNegativa);

				} else {
					classesSugeridasPositivos[i][j] = temp.exemplo
							.classAttribute().indexOfValue(classePositiva);

				}
			}
		}

		double[] tpr = new double[classesSugeridasPositivos.length];
		double[] fpr = new double[classesSugeridasPositivos.length];

		for (int k = 0; k < classesSugeridasPositivos.length; k++) {
			int j = 0;
			int tp = 0;
			int fp = 0;

			int p = 0;
			int n = 0;

			for (Iterator iter = votos.iterator(); iter.hasNext(); j++) {
				InstanceVotacao element = (InstanceVotacao) iter.next();
				double classeReal = element.exemplo.classValue();

				double classeSugeridaPositiva = classesSugeridasPositivos[k][j];
				if (classeSugeridaPositiva == element.exemplo.classAttribute()
						.indexOfValue(classePositiva)) {

					if (classeSugeridaPositiva == classeReal) {
						tp++;
					} else {
						fp++;
					}
				}

				if (classeReal == element.exemplo.classAttribute()
						.indexOfValue(classePositiva))
					p++;
				else
					n++;
			}

			if (p != 0) {
				tpr[k] = (double) tp / (double) p;

			} else {
				tpr[k] = 0;
			}
			if (n != 0) {
				fpr[k] = (double) fp / (double) n;
			} else {
				fpr[k] = 0;
			}

		}

		double[][] result = new double[2][tpr.length];
		result[0] = tpr;
		result[1] = fpr;
		return result;

	}

	/**
	 * Mï¿½todo que preenche a matriz de confusï¿½o para a classe passada em
	 * parï¿½metro
	 * 
	 * @param confusao
	 *            Matriz de confusao a ser preenchida
	 * @param dadosTeste
	 *            Dados de testes
	 * @param regras
	 *            Regras do modelo
	 */
	public void preencherMatrizConfusao(MatrizConfusao confusao,
			Instances dadosTeste, ArrayList<Regra> regras) {
		double votacao = 0;
		Instance temp = null;
		for (int i = 0; i < dadosTeste.numInstances(); i++) {
			temp = dadosTeste.instance(i);
			votacao = metodoVotacao.votacao(regras, temp, classePositiva);
			// Se a votacao for 0 o elemento ï¿½ assinalado como pertencente da
			// classe majoritaria
			if (votacao == 0) {
				double[] classeMajor = obterClasseMajoritaria(dadosTeste);
				if (classeMajor[0] == dadosTeste.classAttribute().indexOfValue(
						classePositiva))
					votacao = 1;
				else
					votacao = -1;
			}
			// Exemplo votado como positivo
			if (votacao > 0) {
				// Se o valor real do exemplo for positivo
				if (temp.classValue() == dadosTeste.classAttribute()
						.indexOfValue(classePositiva)) {
					confusao.tp++;
				} else
					confusao.fp++;
			} else {
				if (votacao < 0) {
					// Se o valor real do exemplo for negativo
					if (temp.classValue() == dadosTeste.classAttribute()
							.indexOfValue(classeNegativa)) {
						confusao.tn++;
					} else {
						confusao.fn++;
					}
				}
			}
		}
	}

	/**
	 * Mï¿½todo que preenche a matriz de confusï¿½o multi classe de acordo com
	 * as instancias e regras passadas por parametro
	 * 
	 * @author matheus
	 * @param confusao
	 *            Matriz de confusao a ser preenchida
	 * @param dadosTeste
	 *            Dados de testes
	 * @param regras
	 *            Regras do modelo
	 */
	public void preencherMatrizConfusaoMultiClasse(
			MatrizConfusaoMultiClasse confusao, Instances dadosTeste,
			ArrayList<Regra> regras) {
		ArrayList<Integer> classesMaisVotadas;
		int classeReal;
		Instance temp = null;
		for (int i = 0; i < dadosTeste.numInstances(); i++) {
			temp = dadosTeste.instance(i);
			classesMaisVotadas = metodoVotacao.votacaoMultiClasse(regras, temp,
					this.classes);
			classeReal = (int) temp.classValue();
			// em caso de nï¿½o haver empate pega-se o primeiro e ï¿½nico
			// elemento
			// do arraylist que ï¿½ a classe eleita
			if (classesMaisVotadas.size() == 1) {
				confusao.matriz[classeReal][classesMaisVotadas.get(0)]++;
			} else {
				ArrayList<Classe> arrayClassesMaisVotadas = new ArrayList<Classe>();
				for (Integer indiceClasse : classesMaisVotadas) {
					arrayClassesMaisVotadas.add(this.classes.get(indiceClasse));
				}
				int indiceClasseMaiorCobertura = classes
						.indexOf(getClasseMaiorCobertura(arrayClassesMaisVotadas));
				confusao.matriz[classeReal][indiceClasseMaiorCobertura]++;
			}
		}
	}

	/**
	 * Mï¿½todo que recebe como parï¿½metro um conjunto de regras e retira deste
	 * conjunto as regras cujo o erro e maior que a porcentagem da class
	 * majortaria
	 * 
	 * @param regras
	 *            Regras de entrada
	 * @param porcentageClasseMajoritaria
	 *            Procentagem da classe majoritï¿½ria
	 * @return Regras filtradas
	 */
	public ArrayList<Regra> retirarRegrasRuins(ArrayList<Regra> regras,
			double porcentageClasseMajoritaria) {
		ArrayList<Regra> novasRegras = new ArrayList<Regra>();
		for (Iterator<Regra> iter = regras.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			double erro = regra.getErro();
			if (erro < porcentageClasseMajoritaria)
				novasRegras.add(regra);
		}
		return novasRegras;
	}

	/**
	 * Mï¿½todo que percorre a base e verifica quantos exemplos nï¿½o foram
	 * cobertos pelas regras geradas.
	 * 
	 * @param dadosTeste
	 */
	public void contarExemplosNaoCobertos(Instances dados) {
		int cobertos = 0;
		for (int i = 0; i < dados.numInstances(); i++) {
			Instance exemplo = dados.instance(i);
			int x = 0;
			for (Iterator<Regra> iter = regras.iterator(); iter.hasNext();) {
				Regra r = (Regra) iter.next();
				boolean b = r.compararCorpo(exemplo.toDoubleArray());
				if (b) {
					cobertos++;
					x = 1;
					break;
				}
			}
			if (x == 0)
				System.out.println("Aquii");
		}

		System.out.println("Cobertos: " + cobertos);
		System.out.println("Nï¿½o cobertos: "
				+ (dados.numInstances() - cobertos));

	}

	/**
	 * Mï¿½todo que apaga todas as listas e prepara o algoritmo para prï¿½xima
	 * execuï¿½ï¿½o
	 * 
	 */
	public void apagarListas() {
		regras.clear();
		if (paretoPos != null)
			paretoPos.getRegras().clear();
		if (paretoNeg != null)
			paretoNeg.getRegras().clear();
		dados = null;
	}

	/**
	 * Mï¿½todo que calcula a mï¿½dia dos valores do objetivos para todas as
	 * regras da fronteira
	 * 
	 * @return
	 */
	public double[] obterMediaValoresObjetivos() {
		double[] somaValores = new double[objetivos.size()];
		for (Iterator<Regra> iter = regras.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			double valores[] = regra.getValoresObjetivos();
			for (int i = 0; i < valores.length; i++) {
				somaValores[i] += valores[i];
			}
		}

		for (int i = 0; i < somaValores.length; i++) {
			somaValores[i] = somaValores[i] / regras.size();
		}

		return somaValores;
	}

	/**
	 * Mï¿½todo que recebe como paramentros as informacoes dos atributos e gera
	 * uma regra aleatoria
	 * 
	 * @deprecated Please now use gerarRegraAleatoriaProporcional()
	 * @see gerarRegraAleatoriaProporcional()
	 * @param atributos
	 *            Enumeration contendo os atributos
	 * @param classAttribute
	 *            Objeto do tipo Attribute referente a classe
	 * @param numAtributos
	 *            Nï¿½mero total de atributos
	 * @param classe
	 *            String contendo o nome da classe da regra que sera gerada
	 * @return Nova regra gerada
	 */
	public Regra gerarRegraAleatoria(Enumeration<Attribute> atributos,
			Attribute classAttribute, int numAtributos, int classe) {
		Regra regra = new Regra(objetivos);
		regra.corpo = new Atributo[numAtributos - 1];

		int i = 0;
		while (atributos.hasMoreElements()) {
			Attribute att = (Attribute) atributos.nextElement();
			if (att.isNominal())
				regra.corpo[i] = new AtributoNominal(true, att, i);
			else {
				double[] limites = obterMaximoMinimoAtributo(att);
				regra.corpo[i] = new AtributoNumerico(true, att, i, limites[0],
						limites[1]);
			}
			++i;
		}

		double temp = Math.random();
		int n = (int) ((temp * 100) % regra.corpo.length);
		if (n == 0)
			n = 1;

		preencherRegraAleatoria(n, regra);

		while (regra.isEmpty()) {
			preencherRegraAleatoria(n, regra);
		}

		if (classe == 0)
			regra.cabeca = classAttribute.indexOfValue(classePositiva);
		else
			regra.cabeca = classAttribute.indexOfValue(classeNegativa);
		regra.classe = classAttribute;
		regra.getNumAtributosNaoVazios();
		return regra;
	}

	/**
	 * Mï¿½todo que gera regras iniciais aleatï¿½rias de acordo com a
	 * distribuiï¿½ï¿½o dos valores na base
	 * 
	 * @param atributos
	 *            Informaï¿½ï¿½es sobre os atributos da base
	 * @param classAttribute
	 *            Atributo classe
	 * @param numAtributos
	 *            Nï¿½mero de atributos
	 * @param classe
	 *            Classe da regra gerada
	 * @return A regra com seus valores iniciais gerados
	 */
	public Regra gerarRegraAleatoriaProporcional(
			Enumeration<Attribute> atributos, Attribute classAttribute,
			int numAtributos, int classe) {
		Regra regra = new Regra(objetivos);
		regra.corpo = new Atributo[numAtributos - 1];

		preencherDistribuicaoValores();

		int i = 0;
		while (atributos.hasMoreElements()) {
			Attribute att = (Attribute) atributos.nextElement();
			if (att.isNominal())
				regra.corpo[i] = new AtributoNominal(true, att, i);
			else {
				double[] limites = obterMaximoMinimoAtributo(att);
				regra.corpo[i] = new AtributoNumerico(true, att, i, limites[0],
						limites[1]);
			}
			++i;
		}

		preencherRegraAleatoriaProporcional(regra);

		while (regra.isEmpty()) {
			preencherRegraAleatoriaProporcional(regra);
		}

		if (classe == 0)
			regra.cabeca = classAttribute.indexOfValue(classePositiva);
		else
			regra.cabeca = classAttribute.indexOfValue(classeNegativa);
		regra.classe = classAttribute;
		regra.getNumAtributosNaoVazios();
		return regra;
	}

	/**
	 * Mï¿½todo que gera regras iniciais aleatï¿½rias de acordo com a
	 * distribuiï¿½ï¿½o dos valores na base Utiliza atributos nominais
	 * combinados
	 * 
	 * @param atributos
	 *            Informaï¿½ï¿½es sobre os atributos da base
	 * @param classAttribute
	 *            Atributo classe
	 * @param numAtributos
	 *            Nï¿½mero de atributos
	 * @param classe
	 *            Classe da regra gerada
	 * @return A regra com seus valores iniciais gerados
	 */
	public Regra gerarRegraAleatoriaCombinacao(
			Enumeration<Attribute> atributos, Attribute classAttribute,
			int numAtributos, int classe) {
		Regra regra = new Regra(objetivos);
		regra.corpo = new Atributo[numAtributos - 1];
		int i = 0;
		while (atributos.hasMoreElements()) {
			Attribute att = (Attribute) atributos.nextElement();
			if (att.isNominal())
				regra.corpo[i] = new AtributoCombinado(true, att, i);
			else {
				double[] limites = obterMaximoMinimoAtributo(att);
				regra.corpo[i] = new AtributoNumerico(true, att, i, limites[0],
						limites[1]);
			}
			++i;
		}

		double temp = Math.random();
		int n = (int) ((temp * 100) % regra.corpo.length);
		if (n == 0)
			n = 1;

		preencherRegraAleatoria(n, regra);

		while (regra.isEmpty()) {
			preencherRegraAleatoria(n, regra);
		}
		if (classe == 0)
			regra.cabeca = classAttribute.indexOfValue(classePositiva);
		else
			regra.cabeca = classAttribute.indexOfValue(classeNegativa);
		regra.classe = classAttribute;
		regra.getNumAtributosNaoVazios();
		return regra;
	}

	/**
	 * Preenche n posicoes da regra aleatoriamente
	 * 
	 * @param n
	 *            Numero de posicoes a serem preenchidas
	 * @param regra
	 *            Regra a ser preenchida
	 */
	public void preencherRegraAleatoria(int n, Regra regra) {
		for (int i = 0; i < n; i++) {
			double temp = Math.random();
			int pos = (int) ((temp * 100) % regra.corpo.length);
			Atributo atributo = regra.corpo[pos];
			if (atributo.isNominal())
				regra.preencherAtributoNominalAleatorio(pos, atributo);
			else {
				regra.preencherAtributoNumericoAleatorio(pos, atributo);
			}
		}
	}

	/**
	 * Mï¿½todo que preenche todos os atributos da regra aleatoriamente,
	 * proporcional ï¿½ distribuiï¿½ï¿½o dos valores na base.
	 * 
	 * @param regra
	 *            Regra a ser preenchida
	 */
	public void preencherRegraAleatoriaProporcional(Regra regra) {
		int num = 0;
		for (int i = 0; i < regra.corpo.length; i++) {
			Atributo atributo = regra.corpo[i];
			if (atributo.isNominal())
				regra.preencherAtributoNominalAleatorioProporcional(i,
						atributo, distribuicaoAtributos[num++]);
			else {
				regra.preencherAtributoNumericoAleatorio(i, atributo);
			}
		}
	}

	/**
	 * Mï¿½todo principal que executa o algoritmo passado como parametro
	 * 
	 * @param nomeBase
	 *            Base de dados da execuï¿½ï¿½o
	 * @param caminhoBase
	 *            Caminho da base de dados
	 * @param nomeMetodo
	 *            Nome mï¿½todo que serï¿½ executado
	 * @param cPositiva
	 *            ï¿½ndice da classe postiva da base
	 * @param cNegativa
	 *            ï¿½ndice da classe postiva da base
	 * @param numFolds
	 *            Nï¿½mero de folds da base
	 * @param numExec
	 *            Nï¿½mero de execuï¿½ï¿½es
	 * @param dirResultado
	 *            Nome do diretï¿½rio onde o resultado serï¿½ guardado
	 * @param AUC
	 *            Boolean que indica se sï¿½ irï¿½ ocorrer a etapa de
	 *            avaliaï¿½ï¿½o das regras geradas
	 * @param verbose
	 *            Indica se haverï¿½ a impressï¿½o do andamento da execuï¿½ï¿½o
	 *            ou nï¿½o
	 * @param votacao
	 *            Contï¿½m o nome do mï¿½todod e votaï¿½aï¿½ utilizado (simples,
	 *            confidence, laplace, ordenacao)
	 * @param selecao
	 *            Indica se as regras finais vï¿½o ser somente as regras que
	 *            votaram na etapa de teste
	 * @throws Exception
	 *             Lanï¿½a execuï¿½ï¿½oc caso haja algum erro nos arquivos de
	 *             entrada ou saï¿½da.
	 */
	public void executarFolds(String nomeBase, String caminhoBase,
			String nomeMetodo, int numClasses, int cPositiva, int cNegativa,
			int numFolds, int numExec, String dirResultado, boolean AUC,
			boolean verbose, String votacao, boolean selecao/*
															 * , String[]
															 * objetivos
															 */)
			throws Exception {

		dadosExperimento = new DadosExperimentos();
		dadosExperimento.nomeBase = nomeBase;
		dadosExperimento.metodo = nomeMetodo;
		String caminhoDir = System.getProperty("user.dir");

		// Arquivo de redundï¿½ncia que salva as informaï¿½ï¿½es das
		// execuï¿½ï¿½es durante
		// o processo.
		// Evita perder as informaï¿½ï¿½es se houver um problema na
		// execuï¿½ï¿½o.
		String caminhoTemp = caminhoDir + "/resultados/" + nomeMetodo + "/"
				+ dirResultado + "/";
		File dir = new File(caminhoTemp);
		dir.mkdirs();
		String arquivoTemp = caminhoTemp + "temp_" + nomeBase + ".txt";
		PrintStream psTemp = new PrintStream(arquivoTemp);

		// Arquivos que salvam os tempos de cada execucao
		PrintStream psExec = null;
		PrintStream psFold = null;

		String arquivoExec = caminhoTemp + "texec_" + nomeBase + ".txt";
		psExec = new PrintStream(arquivoExec);

		String arquivoFold = caminhoTemp + "tfold_" + nomeBase + ".txt";
		psFold = new PrintStream(arquivoFold);

		for (int j = 0; j < numExec; j++) {

			String diretorio = caminhoDir + "/resultados/" + nomeMetodo + "/"
					+ dirResultado + "/" + nomeBase + "" + j + "/";
			dir = new File(diretorio);
			dir.mkdirs();

			System.out.println("Inicio Execucao: "
					+ Calendar.getInstance().getTime());
			psExec.print(j + ":\t" + Calendar.getInstance().getTimeInMillis()
					+ "\t");
			System.out.println("Base: " + nomeBase);
			System.out.println("Caminho da Base: " + caminhoBase);
			System.out.println("Execucao: " + j);

			this.verbose = verbose;

			setVotacao(votacao);

			ArrayList<Regra> regrasFinais = new ArrayList<Regra>();
			for (int i = 0; i < numFolds; i++) {

				System.out.println("Inicio Fold: "
						+ Calendar.getInstance().getTime());
				psFold.print(j + "-" + i + ":\t"
						+ Calendar.getInstance().getTimeInMillis() + "\t");
				System.out.println("Fold: " + j + "-" + i);
				numFold = i;

				executarTreinamento(nomeBase, caminhoBase, cPositiva,
						cNegativa, i, numClasses);

				if (regras.isEmpty()) {
					System.out.println("Nenhuma regra encontrada");
				} else {

					String arquivoRegras = "resultados/" + nomeMetodo + "/"
							+ dirResultado + "/" + nomeBase + "" + j
							+ "/regras_" + nomeBase + "" + j + "_" + i + ".txt";
					PrintStream psRegras = new PrintStream(arquivoRegras);

					if (AUC) {
						String arquivoTeste = caminhoBase + nomeBase + "/it"
								+ i + "/" + nomeBase + "_test.arff";
						executarTeste(regras, nomeBase, numClasses, psTemp, j,
								i, arquivoTeste, dadosExperimento);
						gravarRegrasArquivo(selecao, regrasFinais, psRegras,
								regras);
					} else {
						// Como nï¿½o hï¿½ cï¿½lculo das medidas, nï¿½o hï¿½ o
						// processod e
						// votaï¿½ï¿½o.
						// Assim a opï¿½ï¿½o seleï¿½ï¿½o na gravaï¿½ï¿½o de
						// regras deve ser
						// sempre falsa
						gravarRegrasArquivo(false, regrasFinais, psRegras,
								regras);
					}

				}
				System.out.println();
				apagarListas();

				System.out.println("Fim Fold: "
						+ Calendar.getInstance().getTime());
				psFold.print(Calendar.getInstance().getTimeInMillis() + "\n");
			}

			// Grava as regras finais da execuï¿½ï¿½o
			String arquivoRegrasFinais = "resultados/" + nomeMetodo + "/"
					+ dirResultado + "/" + nomeBase + "" + j + "/regras_"
					+ nomeBase + "" + j + ".txt";
			PrintStream psRegras = new PrintStream(arquivoRegrasFinais);
			for (Iterator<Regra> iter = regrasFinais.iterator(); iter.hasNext();) {
				Regra regra = (Regra) iter.next();
				psRegras.println(regra);
			}

			System.out.println("Fim Execucao: "
					+ Calendar.getInstance().getTime());
			psExec.print(Calendar.getInstance().getTimeInMillis() + "\n");
		}

		if (AUC) {
			gravarMedidasFinaisArquivo(dadosExperimento, nomeBase, nomeMetodo,
					caminhoDir);
		}
	}

	/**
	 * Mï¿½todo que divide a execuï¿½ï¿½o dos em folds em diversas
	 * partiï¿½ï¿½es, junta todas as regras geradas e testa no arquivo de test.
	 * Jï¿½ esta divindo o base de treinamento em partes iguais, executando o
	 * algoritmo para todas as partiï¿½ï¿½es, juntando as regras deixando apenas
	 * as regras nï¿½o dominadas. Falta a parte do cï¿½lculo da AUC no arquivo
	 * de teste.
	 * 
	 * @param nomeBase
	 * @param caminhoBase
	 * @param nomeMetodo
	 * @param cPositiva
	 * @param cNegativa
	 * @param numFolds
	 * @param indice
	 * @param dirResultado
	 * @param AUC
	 * @param numParticoes
	 * @throws Exception
	 */

	public void executarParalelo(String nomeBase, String caminhoBase,
			String nomeMetodo, int numClasses, int cPositiva, int cNegativa,
			int numFolds, int numExec, String dirResultado, boolean AUC,
			boolean verbose, String votacao, boolean selecao, int numParticoes)
			throws Exception {

		dadosExperimento = new DadosExperimentos();
		dadosExperimento.nomeBase = nomeBase;
		dadosExperimento.metodo = nomeMetodo;
		String caminhoDir = System.getProperty("user.dir");

		// Arquivo de redundï¿½ncia que salva as informaï¿½ï¿½es das
		// execuï¿½ï¿½es durante
		// o processo.
		// Evita perder as informaï¿½ï¿½es se houver um problema na
		// execuï¿½ï¿½o.
		String caminhoTemp = caminhoDir + "/resultados/" + nomeMetodo + "/"
				+ dirResultado + "/";
		File dir = new File(caminhoTemp);
		dir.mkdirs();

		String arquivoTemp = caminhoTemp + "temp_" + nomeBase + ".txt";
		PrintStream psTemp = new PrintStream(arquivoTemp);

		// Arquivos que salvam os tempos de cada execucao
		PrintStream psExec = null;
		PrintStream psFold = null;
		PrintStream psPart = null;

		String arquivoExec = caminhoTemp + "texec_" + nomeBase + ".txt";
		psExec = new PrintStream(arquivoExec);

		String arquivoFold = caminhoTemp + "tfold_" + nomeBase + ".txt";
		psFold = new PrintStream(arquivoFold);

		String arquivoPart = caminhoTemp + "tpart_" + nomeBase + ".txt";
		psPart = new PrintStream(arquivoPart);

		for (int j = 0; j < numExec; j++) {

			String diretorio = caminhoDir + "/resultados/" + nomeMetodo + "/"
					+ dirResultado + "/" + nomeBase + "" + j + "/";
			dir = new File(diretorio);
			dir.mkdirs();

			System.out.println("Inicio Execucao: "
					+ Calendar.getInstance().getTime());
			psExec.print(j + ":\t" + Calendar.getInstance().getTimeInMillis()
					+ "\t");
			System.out.println("Base: " + nomeBase);
			System.out.println("Caminho da Base: " + caminhoBase);
			System.out.println("Execucao: " + j);

			this.verbose = verbose;

			setVotacao(votacao);

			ArrayList<Regra> regrasFinais = new ArrayList<Regra>();

			ArrayList<Regra> regrasParticoes = new ArrayList<Regra>();

			for (int i = 0; i < numFolds; i++) {
				System.out.println("Inicio Fold: "
						+ Calendar.getInstance().getTime());
				psFold.print(j + "-" + i + ":\t"
						+ Calendar.getInstance().getTimeInMillis() + "\t");
				System.out.println("Fold: " + j + "-" + i);
				numFold = i;

				String arquivoTreinamento = caminhoBase + nomeBase + "/it" + i
						+ "/" + nomeBase + "_data.arff";
				System.out
						.println("Base de Treinamento: " + arquivoTreinamento);
				carregarInstancias(arquivoTreinamento, cPositiva, cNegativa);

				int tamParticao = dados.numInstances() / numParticoes;
				Instances dadosTreinamentoTotal = new Instances(dados);
				for (int n = 0; n < numParticoes; n++) {
					System.out.println("Inicio Particao: "
							+ Calendar.getInstance().getTime());
					psPart.print(j + "-" + i + "-" + n + ":\t"
							+ Calendar.getInstance().getTimeInMillis() + "\t");
					System.out.println("Partiï¿½ï¿½o: " + n);
					int inicio = n * tamParticao;
					int fim = 0;
					if (n + 1 < numParticoes)
						fim = (n + 1) * tamParticao;
					else
						fim = dadosTreinamentoTotal.numInstances();

					dados = new Instances(dadosTreinamentoTotal, 0);
					for (int z = inicio; z < fim; z++) {
						dados.add(dadosTreinamentoTotal.instance(z));
					}

					obterRegras(numClasses);
					System.out.println("Numero de Instancias: "
							+ dados.numInstances());
					System.out.println("Numero de Regras Partiï¿½ï¿½o: "
							+ regras.size());
					regrasParticoes.addAll(regras);
					apagarListas();
					System.out.println("Fim Particao: "
							+ Calendar.getInstance().getTime());
					psPart.print(Calendar.getInstance().getTimeInMillis()
							+ "\n");
				}

				// Recalcular os objetivos para a base de dados original
				// preencherMatrizContigencia(regrasParticoes,
				// dadosTreinamentoTotal);

				// eliminarRegrasDominadas(regrasParticoes);

				regras.addAll(regrasParticoes);
				regrasParticoes.clear();

				System.out
						.println("\nNumero de Regras Total: " + regras.size());

				if (regras.isEmpty()) {
					System.out.println("Nenhuma regra encontrada");
				} else {

					String arquivoRegras = "resultados/" + nomeMetodo + "/"
							+ dirResultado + "/" + nomeBase + "" + j
							+ "/regras_" + nomeBase + "" + j + "_" + i + ".txt";
					PrintStream psRegras = new PrintStream(arquivoRegras);

					if (AUC) {

						String arquivoTeste = caminhoBase + nomeBase + "/it"
								+ i + "/" + nomeBase + "_test.arff";
						executarTeste(regras, nomeBase, numClasses, psTemp, j,
								i, arquivoTeste, dadosExperimento);

						gravarRegrasArquivo(selecao, regrasFinais, psRegras,
								regras);
					} else {
						// Como nï¿½o hï¿½ cï¿½lculo das medidas, nï¿½o hï¿½ o
						// processod e
						// votaï¿½ï¿½o.
						// Assim a opï¿½ï¿½o seleï¿½ï¿½o na gravaï¿½ï¿½o de
						// regras deve ser
						// sempre falsa
						gravarRegrasArquivo(false, regrasFinais, psRegras,
								regras);
					}

				}
				System.out.println();
				apagarListas();
				System.out.println("Fim Fold: "
						+ Calendar.getInstance().getTime());
				psFold.print(Calendar.getInstance().getTimeInMillis() + "\n");
			}

			// Grava as regras finais da execuï¿½ï¿½o
			String arquivoRegrasFinais = "resultados/" + nomeMetodo + "/"
					+ dirResultado + "/" + nomeBase + "" + j + "/regras_"
					+ nomeBase + "" + j + ".txt";
			PrintStream psRegras = new PrintStream(arquivoRegrasFinais);
			for (Iterator<Regra> iter = regrasFinais.iterator(); iter.hasNext();) {
				Regra regra = (Regra) iter.next();
				psRegras.println(regra);
			}

			System.out.println("Fim: " + Calendar.getInstance().getTime());
			psExec.print(Calendar.getInstance().getTimeInMillis() + "\n");
		}

		if (AUC) {
			gravarMedidasFinaisArquivo(dadosExperimento, nomeBase, nomeMetodo,
					caminhoDir);
		}
	}

	/**
	 * 
	 * Mï¿½todo que divide a execuï¿½ï¿½o dos em folds em diversas
	 * partiï¿½ï¿½es, junta todas as regras geradas e testa no arquivo de test.
	 * Jï¿½ esta divindo o base de treinamento em partes iguais, executando o
	 * algoritmo para todas as partiï¿½ï¿½es, juntando as regras deixando apenas
	 * as regras nï¿½o dominadas. Falta a parte do cï¿½lculo da AUC no arquivo
	 * de teste.
	 * 
	 * @deprecated Metodo com problema. A versÃ£o nova do Weka nÃ£o possui o
	 *             mÃ©todo resampleWithWeights(dadosTreinamentoTotal, new
	 *             Random(), sampled);
	 * @param nomeBase
	 * @param caminhoBase
	 * @param nomeMetodo
	 * @param cPositiva
	 * @param cNegativa
	 * @param numFolds
	 * @param indice
	 * @param dirResultado
	 * @param AUC
	 * @param numParticoes
	 * @throws Exception
	 */

	public void executarParaleloBagging(String nomeBase, String caminhoBase,
			String nomeMetodo, int numClasses, int cPositiva, int cNegativa,
			int numFolds, int numExec, String dirResultado, boolean AUC,
			boolean verbose, String votacao, boolean selecao, int numParticoes)
			throws Exception {

		dadosExperimento = new DadosExperimentos();
		dadosExperimento.nomeBase = nomeBase;
		dadosExperimento.metodo = nomeMetodo;
		String caminhoDir = System.getProperty("user.dir");

		// Arquivo de redundï¿½ncia que salva as informaï¿½ï¿½es das
		// execuï¿½ï¿½es durante
		// o processo.
		// Evita perder as informaï¿½ï¿½es se houver um problema na
		// execuï¿½ï¿½o.
		String caminhoTemp = caminhoDir + "/resultados/" + nomeMetodo + "/"
				+ dirResultado + "/";
		File dir = new File(caminhoTemp);
		dir.mkdirs();

		String arquivoTemp = caminhoTemp + "temp_" + nomeBase + ".txt";
		PrintStream psTemp = new PrintStream(arquivoTemp);

		// Arquivos que salvam os tempos de cada execucao
		PrintStream psExec = null;
		PrintStream psFold = null;
		PrintStream psPart = null;

		String arquivoExec = caminhoTemp + "texec_" + nomeBase + ".txt";
		psExec = new PrintStream(arquivoExec);

		String arquivoFold = caminhoTemp + "tfold_" + nomeBase + ".txt";
		psFold = new PrintStream(arquivoFold);

		String arquivoPart = caminhoTemp + "tpart_" + nomeBase + ".txt";
		psPart = new PrintStream(arquivoPart);

		setVotacao(votacao);

		for (int j = 0; j < numExec; j++) {

			String diretorio = caminhoDir + "/resultados/" + nomeMetodo + "/"
					+ dirResultado + "/" + nomeBase + "" + j + "/";
			dir = new File(diretorio);
			dir.mkdirs();

			System.out.println("Inicio Execucao: "
					+ Calendar.getInstance().getTime());
			psExec.print(j + ":\t" + Calendar.getInstance().getTimeInMillis()
					+ "\t");
			System.out.println("Base: " + nomeBase);
			System.out.println("Caminho da Base: " + caminhoBase);
			System.out.println("Execucao: " + j);

			this.verbose = verbose;

			ArrayList<Regra> regrasFinais = new ArrayList<Regra>();

			ArrayList<Regra> regrasParticoes = new ArrayList<Regra>();

			for (int i = 0; i < numFolds; i++) {
				System.out.println("Inicio Fold: "
						+ Calendar.getInstance().getTime());
				psFold.print(j + "-" + i + ":\t"
						+ Calendar.getInstance().getTimeInMillis() + "\t");
				System.out.println("Fold: " + j + "-" + i);
				numFold = i;

				String arquivoTreinamento = caminhoBase + nomeBase + "/it" + i
						+ "/" + nomeBase + "_data.arff";
				System.out
						.println("Base de Treinamento: " + arquivoTreinamento);
				carregarInstancias(arquivoTreinamento, cPositiva, cNegativa);

				Instances dadosTreinamentoTotal = new Instances(dados);

				for (int n = 0; n < numParticoes; n++) {
					System.out.println("Inicio Particao: "
							+ Calendar.getInstance().getTime());
					psPart.print(j + "-" + i + "-" + n + ":\t"
							+ Calendar.getInstance().getTimeInMillis() + "\t");
					System.out.println("Partiï¿½ï¿½o: " + n);

					Bagging bagg = new Bagging();

					boolean[] sampled = new boolean[dadosTreinamentoTotal
							.numInstances()];

					// dados = bagg.resampleWithWeights(dadosTreinamentoTotal,
					// new Random(), sampled);

					obterRegras(numClasses);
					System.out.println("Numero de Instancias: "
							+ dados.numInstances());
					System.out.println("Numero de Regras Partiï¿½ï¿½o: "
							+ regras.size());
					regrasParticoes.addAll(regras);
					apagarListas();
					System.out.println("Fim Particao: "
							+ Calendar.getInstance().getTime());
					psPart.print(Calendar.getInstance().getTimeInMillis()
							+ "\n");
				}

				// Recalcular os objetivos para a base de dados original
				// preencherMatrizContigencia(regrasParticoes,
				// dadosTreinamentoTotal);

				// eliminarRegrasDominadas(regrasParticoes);

				regras.addAll(regrasParticoes);
				regrasParticoes.clear();

				System.out
						.println("\nNumero de Regras Total: " + regras.size());

				if (regras.isEmpty()) {
					System.out.println("Nenhuma regra encontrada");
				} else {

					String arquivoRegras = "resultados/" + nomeMetodo + "/"
							+ dirResultado + "/" + nomeBase + "" + j
							+ "/regras_" + nomeBase + "" + j + "_" + i + ".txt";
					PrintStream psRegras = new PrintStream(arquivoRegras);

					if (AUC) {

						String arquivoTeste = caminhoBase + nomeBase + "/it"
								+ i + "/" + nomeBase + "_test.arff";
						executarTeste(regras, nomeBase, numClasses, psTemp, j,
								i, arquivoTeste, dadosExperimento);

						gravarRegrasArquivo(selecao, regrasFinais, psRegras,
								regras);
					} else {
						// Como nï¿½o hï¿½ cï¿½lculo das medidas, nï¿½o hï¿½ o
						// processod e
						// votaï¿½ï¿½o.
						// Assim a opï¿½ï¿½o seleï¿½ï¿½o na gravaï¿½ï¿½o de
						// regras deve ser
						// sempre falsa
						gravarRegrasArquivo(false, regrasFinais, psRegras,
								regras);
					}

				}
				System.out.println();
				apagarListas();
				System.out.println("Fim Fold: "
						+ Calendar.getInstance().getTime());
				psFold.print(Calendar.getInstance().getTimeInMillis() + "\n");
			}

			// Grava as regras finais da execuï¿½ï¿½o
			String arquivoRegrasFinais = "resultados/" + nomeMetodo + "/"
					+ dirResultado + "/" + nomeBase + "" + j + "/regras_"
					+ nomeBase + "" + j + ".txt";
			PrintStream psRegras = new PrintStream(arquivoRegrasFinais);
			for (Iterator<Regra> iter = regrasFinais.iterator(); iter.hasNext();) {
				Regra regra = (Regra) iter.next();
				psRegras.println(regra);
			}

			System.out.println("Fim: " + Calendar.getInstance().getTime());
			psExec.print(Calendar.getInstance().getTimeInMillis() + "\n");
		}

		if (AUC) {
			gravarMedidasFinaisArquivo(dadosExperimento, nomeBase, nomeMetodo,
					caminhoDir);
		}
	}

	/**
	 * Mï¿½todo que divide a execuï¿½ï¿½o dos em folds em diversas
	 * partiï¿½ï¿½es, junta todas as regras geradas e testa no arquivo de test.
	 * Jï¿½ esta divindo o base de treinamento em partes iguais, executando o
	 * algoritmo para todas as partiï¿½ï¿½es, juntando as regras deixando apenas
	 * as regras nï¿½o dominadas. Falta a parte do cï¿½lculo da AUC no arquivo
	 * de teste.
	 * 
	 * @param nomeBase
	 * @param caminhoBase
	 * @param nomeMetodo
	 * @param cPositiva
	 * @param cNegativa
	 * @param numFolds
	 * @param indice
	 * @param dirResultado
	 * @param AUC
	 * @param numParticoes
	 * @throws Exception
	 */

	public void executarMedoid(String nomeBase, String caminhoBase,
			String nomeMetodo, int numClasses, int cPositiva, int cNegativa,
			int numFolds, int numExec, String dirResultado, boolean AUC,
			boolean verbose, String votacao, boolean selecao) throws Exception {

		dadosExperimento = new DadosExperimentos();
		dadosExperimento.nomeBase = nomeBase;
		dadosExperimento.metodo = nomeMetodo;

		DadosExperimentos dadosExperimento_4 = new DadosExperimentos();
		dadosExperimento_4.nomeBase = nomeBase;
		dadosExperimento_4.metodo = nomeMetodo;
		dadosExperimento_4.ID = "_4";

		DadosExperimentos dadosExperimento_8 = new DadosExperimentos();
		dadosExperimento_8.nomeBase = nomeBase;
		dadosExperimento_8.metodo = nomeMetodo;
		dadosExperimento_8.ID = "_8";

		DadosExperimentos dadosExperimento_12 = new DadosExperimentos();
		dadosExperimento_12.nomeBase = nomeBase;
		dadosExperimento_12.metodo = nomeMetodo;
		dadosExperimento_12.ID = "_12";

		DadosExperimentos dadosExperimento_16 = new DadosExperimentos();
		dadosExperimento_16.nomeBase = nomeBase;
		dadosExperimento_16.metodo = nomeMetodo;
		dadosExperimento_16.ID = "_16";

		String caminhoDir = System.getProperty("user.dir");

		// Arquivo de redundï¿½ncia que salva as informaï¿½ï¿½es das
		// execuï¿½ï¿½es durante
		// o processo.
		// Evita perder as informaï¿½ï¿½es se houver um problema na
		// execuï¿½ï¿½o.
		String caminhoTemp = caminhoDir + "/resultados/" + nomeMetodo + "/"
				+ dirResultado + "/";
		File dir = new File(caminhoTemp);
		dir.mkdirs();
		String arquivoTemp = caminhoTemp + "temp_" + nomeBase + ".txt";
		PrintStream psTemp = new PrintStream(arquivoTemp);

		for (int j = 0; j < numExec; j++) {

			String diretorio = caminhoDir + "/resultados/" + nomeMetodo + "/"
					+ dirResultado + "/" + nomeBase + "" + j + "/";
			dir = new File(diretorio);
			dir.mkdirs();

			System.out.println("Inicio: " + Calendar.getInstance().getTime());
			System.out.println("Base: " + nomeBase);
			System.out.println("Caminho da Base: " + caminhoBase);
			System.out.println("Execucao: " + j);

			this.verbose = verbose;

			setVotacao(votacao);

			ArrayList<Regra> regrasFinais = new ArrayList<Regra>();
			for (int i = 0; i < numFolds; i++) {
				System.out.println("Fold: " + j + "-" + i);
				numFold = i;

				executarTreinamento(nomeBase, caminhoBase, cPositiva,
						cNegativa, i, numClasses);

				// ArrayList regras4 = medoid(... ,... ,..., 4);
				// ArrayList regras8 = medoid(... ,... ,..., 8);

				if (regras.isEmpty()) {
					System.out.println("Nenhuma regra encontrada");
				} else {

					String arquivoRegras = "resultados/" + nomeMetodo + "/"
							+ dirResultado + "/" + nomeBase + "" + j
							+ "/regras_" + nomeBase + "" + j + "_" + i + ".txt";
					PrintStream psRegras = new PrintStream(arquivoRegras);

					// Arquivo com as regras do medoid com 4 grupos
					// String arquivoRegras4 = "resultados/" + nomeMetodo + "/"
					// + dirResultado + "/" + nomeBase+ "" + j +"/regras_" +
					// nomeBase + "" + j + "_" + i + "_4.txt";
					// PrintStream psRegras4 = new PrintStream(arquivoRegras);

					if (AUC) {
						String arquivoTeste = caminhoBase + nomeBase + "/it"
								+ i + "/" + nomeBase + "_test.arff";

						// Teste com as regras originais
						executarTeste(regras, nomeBase, numClasses, psTemp, j,
								i, arquivoTeste, dadosExperimento);

						// Teste com as regras apos aplicacao do medoid com 4
						// cluster
						// executarTeste(regras4, nomeBase, psTemp, j, i,
						// arquivoTeste, dadosExperimento_4);

						// Grava as regras originais no arquivo psRegras
						gravarRegrasArquivo(selecao, regrasFinais, psRegras,
								regras);

						// Grava as regras originais do medoid 4 arquivo
						// psRegras4
						// Adiciona todas (mistura as regras de todos os
						// medoids) as regras nesse objeto regrasFinais.]
						// Se quiser as regras separadas crie uma lista de
						// regrasFinais para cada medoid.
						// gravarRegrasArquivo(selecao, regrasFinais, psRegras,
						// regras);

					}

				}
				System.out.println();
				apagarListas();
			}

			// Grava as regras finais da execuï¿½ï¿½o
			String arquivoRegrasFinais = "resultados/" + nomeMetodo + "/"
					+ dirResultado + "/" + nomeBase + "" + j + "/regras_"
					+ nomeBase + "" + j + ".txt";
			PrintStream psRegras = new PrintStream(arquivoRegrasFinais);
			for (Iterator<Regra> iter = regrasFinais.iterator(); iter.hasNext();) {
				Regra regra = (Regra) iter.next();
				psRegras.println(regra);
			}
			System.out.println("Fim: " + Calendar.getInstance().getTime());
		}

		if (AUC) {
			// Grava as medidas finais das regras originais em um arquivo texto
			gravarMedidasFinaisArquivo(dadosExperimento, nomeBase, nomeMetodo,
					caminhoDir);
			// Grava as medidas finais das regras do medoid4 em um arquivo texto
			// gravarMedidasFinaisArquivo(dadosExperimento_4,nomeBase,
			// nomeMetodo, caminhoDir);
		}
	}

	/**
	 * Mï¿½todo que grava as regras da execucao em arquivo texto e adiciona as
	 * regras numa lista que contem todas as regras de todas as execucoes
	 * 
	 * @param selecao
	 *            Booleano que define se serao gravadas somente as regras que
	 *            votaram
	 * @param regrasFinais
	 *            ArrayList que contem todas as regras da execucao
	 * @param psRegras
	 *            Caminho do arquivo o qual serao gravadas as regras
	 */
	private void gravarRegrasArquivo(boolean selecao,
			ArrayList<Regra> regrasFinais, PrintStream psRegras,
			ArrayList<Regra> regras) {
		// Grava as regras num arquivo texto
		for (Iterator<Regra> iter = regras.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			if (!selecao) {
				psRegras.println(regra);
				if (regrasFinais != null) {
					if (!regrasFinais.contains(regra)) {
						regrasFinais.add(regra);
					}
				}
			} else {
				if (regra.votou) {
					psRegras.println(regra);
					if (regrasFinais != null) {
						if (!regrasFinais.contains(regra))
							regrasFinais.add(regra);
					}
				}
			}
		}
	}

	/**
	 * Mï¿½todo que executa o teste das regras passadas como parametro
	 * 
	 * @param regrasTeste
	 *            Regras que irao executar o teste
	 * @param nomeBase
	 *            Nome da base de dados da execucao
	 * @param psTemp
	 *            Arquivo temporario que ira salvar os valores das medidas do
	 *            fold
	 * @param j
	 *            Numero da execucao
	 * @param i
	 *            Numero do fold
	 * @param arquivoTeste
	 *            Caminho do arquivo de teste
	 * @param dadosExperimentosTeste
	 *            Classe que guarda as informacoes sobre a execucao
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void executarTeste(ArrayList<Regra> regrasTeste, String nomeBase,
			int numClasses, PrintStream psTemp, int j, int i,
			String arquivoTeste, DadosExperimentos dadosExperimentosTeste)
			throws FileNotFoundException, IOException {

		Reader reader = new FileReader(arquivoTeste);
		Instances dadosTeste = new Instances(reader);
		dadosTeste.setClassIndex(dadosTeste.numAttributes() - 1);
		System.out.println("Numero de Instancias de Teste: "
				+ dadosTeste.numInstances());

		preencherArrayClasses(this.classes, dadosTeste);

		confusao = new MatrizConfusao();
		preencherMatrizConfusao(confusao, dadosTeste, regrasTeste);

		/*
		 * confusaoMultiClasse = new MatrizConfusaoMultiClasse(numClasses);
		 * preencherMatrizConfusaoMultiClasse(confusaoMultiClasse, dadosTeste,
		 * regrasTeste);
		 * 
		 * double getFmeasure0, getFmeasure1, getAccuracy,
		 * getRecall0,getRecall1, mgetFmeasure0, mgetFmeasure1, mgetAccuracy,
		 * mgetRecall0, mgetRecall1;
		 * 
		 * getFmeasure0 = confusao.getFmeasure(0); getFmeasure1 =
		 * confusao.getFmeasure(1); getAccuracy = confusao.getAccuracy();
		 * getRecall0 = confusao.getRecall(0); getRecall1 =
		 * confusao.getRecall(1);
		 * 
		 * mgetFmeasure0 = confusaoMultiClasse.getFmeasure(0); mgetFmeasure1 =
		 * confusaoMultiClasse.getFmeasure(1); mgetAccuracy =
		 * confusaoMultiClasse.getAccuracy(); mgetRecall0 =
		 * confusaoMultiClasse.getRecall(0); mgetRecall1 =
		 * confusaoMultiClasse.getRecall(1);
		 */

		double a = obterAUC(dadosTeste, regrasTeste);

		System.out.println("AUC: " + a);

		DadosExecucao fold = new DadosExecucao(nomeBase, i, j, a,
				regras.size(), confusao);

		dadosExperimentosTeste.addFold(fold);
		psTemp.println(fold);
	}

	/**
	 * Mï¿½todo que executa o treinamento da algoritmo passado como parametro no
	 * arquivo principal
	 * 
	 * @param nomeBase
	 *            Nome da base de dados de treinamento
	 * @param caminhoBase
	 *            Caminho da base de dados de treinamento
	 * @param cPositiva
	 *            Indice da classe positiva
	 * @param cNegativa
	 *            Indice da classe negativa
	 * @param i
	 *            Numero do fold
	 * @throws Exception
	 */
	public void executarTreinamento(String nomeBase, String caminhoBase,
			int cPositiva, int cNegativa, int i, int numClasses)
			throws Exception {

		String arquivoTreinamento = caminhoBase + nomeBase + "/it" + i + "/"
				+ nomeBase + "_data.arff";
		System.out.println("Base de Treinamento: " + arquivoTreinamento);
		carregarInstancias(arquivoTreinamento, cPositiva, cNegativa);
		obterRegras(numClasses);

		System.out.println();
		System.out.println("Numero de Instancias: " + dados.numInstances());
		System.out.println("Numero de Regras: " + regras.size());
	}

	/**
	 * Mï¿½todo que executa o treinamento da algoritmo passado como parametro no
	 * arquivo principal
	 * 
	 * @param nomeBase
	 *            Nome da base de dados de treinamento
	 * @param caminhoBase
	 *            Caminho da base de dados de treinamento
	 * @param cPositiva
	 *            Indice da classe positiva
	 * @param cNegativa
	 *            Indice da classe negativa
	 * @param i
	 *            Numero do fold
	 * @throws Exception
	 */
	public void executarTreinamento(Instances d, int numClasses)
			throws Exception {

		dados = d;
		obterRegras(numClasses);
		System.out.println();
		System.out.println("Numero de Regras: " + regras.size());
	}

	/**
	 * Mï¿½todo que grava as informacoes dos dados da execucao em arquivos
	 * 
	 * @param dadosExperimento
	 * @param nomeBase
	 * @param nomeMetodo
	 * @param caminhoDir
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	private void gravarMedidasFinaisArquivo(DadosExperimentos dadosExperimento,
			String nomeBase, String nomeMetodo, String caminhoDir)
			throws FileNotFoundException, Exception {

		String diretorio = caminhoDir + "/resultados/" + nomeMetodo + "/"
				+ nomeBase + "/";
		String arquivoResult = diretorio + "/resultado_" + nomeBase + ".txt";
		System.out.println("Resultado gerado em: " + arquivoResult);

		PrintStream ps = new PrintStream(arquivoResult);
		dadosExperimento.calcularMediaAreasPrecisaoNumRegras();
		dadosExperimento.calcularDesvioPadrao();

		System.out.println(dadosExperimento);
		ps.println(dadosExperimento);

		dadosExperimento.gerarArquivosMedidas(diretorio, nomeMetodo + "_"
				+ nomeBase + "_medidas", nomeMetodo + "_" + nomeBase
				+ "_comandos", nomeMetodo + "_" + nomeBase + "_confusao");
	}
        /*
			ORD  == ORDENACAO
			CONF == CONFIDENCE
			CSA  == Support Confidence Algorithm
			SENS == Sensitividade
			SPEC == Especificidade
			LP   == Laplace
			K    == Joelho
			E    == Extremo
			Exemplo de escrita no arquivo principal.txt:
			-->Votacao com Joelho ou Extremo parametros:
				Ordenacao       : K ou E (Joelho ou Extremo)
				Medida Ordenacao: CSA ou SENS ou SPEC 
				Votacao         : LP ou SENS ou SPEC
				Exemplo de uso  : k CSA LP
			-->Votacao com Outros métodos de Ordenacao:
				Ordenacao       : CSA ou SENS ou SPEC
				Votacao         : LP ou SENS ou SPEC 
				Exemplo de uso  : CSA SENS
			-->Votacao sem Ordenacao:
				método          : ORD ou CONF
				Exemplo de uso  : CONF
			OBS : Deve existir um espaço separando cada parametro de entrada;
			OBS2: Escrita maiuscula ou minuscula
	    */
	public void setVotacao(String votacao) {		
		String[] votacaoArray = votacao.split(" ");
		switch (votacaoArray.length) {
		case 1:
			if (votacaoArray[0].equalsIgnoreCase("ORD")) {
				this.metodoVotacao = new VotacaoConfidence();
			} else if (votacaoArray[0].equalsIgnoreCase("CONF")) {
				this.metodoVotacao = new VotacaoConfidenceLaplaceOrdenacao();
			}
			break;

		case 2:
			// Ordenacao
			if (votacaoArray[0].equalsIgnoreCase("CSA"))
				this.ordenacao = CSA;
			else if (votacaoArray[0].equalsIgnoreCase("SENS"))
				this.ordenacao = SENS;
			else if (votacaoArray[0].equalsIgnoreCase("SPEC"))
				this.ordenacao = SPEC;
			// Votacao
			if (votacaoArray[1].equalsIgnoreCase("LP"))
				this.votacao = LP;
			else if (votacaoArray[1].equalsIgnoreCase("SENS"))
				this.votacao = SENS;
			if (votacaoArray[1].equalsIgnoreCase("SPEC"))
				this.votacao = SPEC;
			this.metodoVotacao = new VotacaoGeral(this.ordenacao, this.votacao);
			break;

		case 3:
			if (votacaoArray[0].equalsIgnoreCase("K"))
				this.ordenacao = KNEE;
			else if (votacaoArray[0].equalsIgnoreCase("E"))
				this.ordenacao = EXTR;
			// Ordenacao
			if (votacaoArray[1].equalsIgnoreCase("CSA"))
				this.medida = CSA;
			else if (votacaoArray[1].equalsIgnoreCase("SENS"))
				this.medida = SENS;
			else if (votacaoArray[1].equalsIgnoreCase("SPEC"))
				this.medida = SPEC;
			// Votacao
			if (votacaoArray[2].equalsIgnoreCase("LP"))
				this.votacao = LP;
			else if (votacaoArray[2].equalsIgnoreCase("SENS"))
				this.votacao = SENS;
			if (votacaoArray[2].equalsIgnoreCase("SPEC"))
				this.votacao = SPEC;
			this.metodoVotacao = new VotacaoGeral(ordenacao, medida,
					this.votacao);
			break;
		}
	}

	/**
	 * Uma sï¿½ execuï¿½ï¿½o
	 * 
	 * @deprecated Please now use executarFolds(String, String, String, int, int
	 *             ,int, String, String, boolean, boolean, String, boolean,
	 *             String[])
	 * @see executarFolds(String, String, String, int, int ,int, String, String,
	 *      boolean, boolean, String, boolean, String[])
	 * @param nomeBase
	 * @param caminhoBase
	 * @param nomeMetodo
	 * @param cPositiva
	 * @param cNegativa
	 * @param numFolds
	 * @param verbose
	 * @param votacao
	 * @throws Exception
	 */
	public void executarFolds(String nomeBase, String caminhoBase,
			String nomeMetodo, int cPositiva, int cNegativa, int numFolds,
			boolean verbose, String votacao, boolean selecao) throws Exception {
		String caminhoDir = System.getProperty("user.dir");
		String diretorio = caminhoDir + "/resultados/" + nomeMetodo + "/"
				+ nomeBase + "/";
		File dir = new File(diretorio);
		dir.mkdirs();
		System.out.println("Inicio: " + Calendar.getInstance().getTime());
		System.out.println("Base: " + nomeBase);
		System.out.println("Caminho da Base: " + caminhoBase);
		System.out.println("Inicio da execucao");
		dadosExperimento = new DadosExperimentos();
		dadosExperimento.nomeBase = nomeBase;

		this.verbose = verbose;

		if (votacao.equals("confidence"))
			this.metodoVotacao = new VotacaoConfidence();
		else {
			if (votacao.equals("ordenacao"))
				this.metodoVotacao = new VotacaoConfidenceLaplaceOrdenacao();
			else {
				if (votacao.equals("laplace"))
					this.metodoVotacao = new VotacaoConfidenceLaplace();
				else {
					this.metodoVotacao = new VotacaoSimples();
				}
			}

		}

		ArrayList<Regra> regrasFinais = new ArrayList<Regra>();
		for (int i = 0; i < numFolds; i++) {
			System.out.println("Fold: " + i);
			numFold = i;
			String arquivoTreinamento = caminhoBase + nomeBase + "/it" + i
					+ "/" + nomeBase + "_data.arff";
			System.out.println("Base de Treinamento: " + arquivoTreinamento);
			carregarInstancias(arquivoTreinamento, cPositiva, cNegativa);
			obterRegras(2);
			String arquivoTeste = caminhoBase + nomeBase + "/it" + i + "/"
					+ nomeBase + "_test.arff";

			if (regras.isEmpty()) {
				System.out.println("Nenhuma regra encontrada");
			} else {

				Reader reader = new FileReader(arquivoTeste);
				Instances dadosTeste = new Instances(reader);
				dadosTeste.setClassIndex(dadosTeste.numAttributes() - 1);
				System.out.println("Numero de Instancias de Teste: "
						+ dadosTeste.numInstances());

				double a = obterAUC(dadosTeste, regras);

				System.out.println("AUC: " + a);

				DadosExecucao fold = new DadosExecucao(nomeBase, i, 0, a,
						regras.size(), confusao);

				dadosExperimento.addFold(fold);

				String arquivoRegras = "resultados/" + nomeMetodo + "/"
						+ nomeBase + "/regras_" + nomeBase + "_" + i + ".txt";
				PrintStream psRegras = new PrintStream(arquivoRegras);

				gravarRegrasArquivo(selecao, regrasFinais, psRegras, regras);

			}
			System.out.println();
			apagarListas();
		}

		String arquivoResult = "resultados/" + nomeMetodo + "/" + nomeBase
				+ "/resultado_" + nomeBase + ".txt";
		System.out.println("Resultado gerado em: " + arquivoResult);

		PrintStream ps = new PrintStream(arquivoResult);
		dadosExperimento.calcularMediaAreasPrecisaoNumRegras();
		dadosExperimento.calcularDesvioPadrao();

		System.out.println(dadosExperimento);
		ps.println(dadosExperimento);

		String arquivoRegrasFinais = "resultados/" + nomeMetodo + "/"
				+ nomeBase + "/regras_" + nomeBase + ".txt";
		PrintStream psRegras = new PrintStream(arquivoRegrasFinais);
		for (Iterator<Regra> iter = regrasFinais.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			psRegras.println(regra);
		}
		System.out.println("Fim: " + Calendar.getInstance().getTime());
	}

	/**
	 * Mï¿½todo que recebe um conjunto de regras como parï¿½metro, escolhe as
	 * regras nï¿½o dominadas e preenche o atributo regras, que sï¿½o as regras
	 * finais geradas pelo algoritmo.
	 * 
	 * @param regrasTemp
	 *            Regras ï¿½ serem refinadas
	 */
	public void eliminarRegrasDominadas(ArrayList<Regra> regrasTemp) {

		paretoPos = new FronteiraPareto();
		paretoNeg = new FronteiraPareto();

		for (Iterator<Regra> iter = regrasTemp.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			paretoPos.add(regra, classePositiva);
			paretoNeg.add(regra, classeNegativa);
		}

		regras.addAll(paretoPos.getRegras());
		regras.addAll(paretoNeg.getRegras());
	}

	/**
	 * * @deprecated Mï¿½todo ainda nï¿½o terminado. Mï¿½todo que preenche uma
	 * matriz com o coeficient de Jaccard de todas as regras Sï¿½ deve ser
	 * executado quando o array de regras estiver preenchido
	 * 
	 */
	public void calcularCoeficienteJaccard() {
		if (regras.size() > 0) {
			coeficienteJaccard = new double[regras.size()][regras.size()];
			int i = 0;
			int j = 0;
			for (Iterator<Regra> iter = regras.iterator(); iter.hasNext();) {
				Regra r1 = (Regra) iter.next();
				for (Iterator<Regra> iterator = regras.iterator(); iterator
						.hasNext();) {
					Regra r2 = (Regra) iterator.next();
					double[] similaridade = medidadeSimilaridade(r1, r2);
					// Calcula do coeficiente de Jaccard
					coeficienteJaccard[i][j] = similaridade[0]
							/ similaridade[1];
				}
				i++;
			}

		}
	}

	/**
	 * * @deprecated Mï¿½todo ainda nï¿½o terminado Medida de similiradade entre
	 * duas regras utilizad no cï¿½lculo do coeficiente de Jaccard
	 * 
	 * @param r1
	 *            Regra 1
	 * @param r2
	 *            Regra 2
	 * @return Valor da medida de similaridade e o total de exemplos cobertos
	 *         pelas duas regras
	 */
	public double[] medidadeSimilaridade(Regra r1, Regra r2) {
		int uniao = 0;
		int interseccao = 0;
		int r1menosr2 = 0;
		int r2menosr1 = 0;

		for (int i = 0; i < dados.numInstances(); i++) {
			Instance exemplo = dados.instance(i);
			if (r1.cobreCorretamente(exemplo)) {
				if (r2.cobreCorretamente(exemplo))
					interseccao++;
				else
					r1menosr2++;
			} else {
				if (r2.cobreCorretamente(exemplo))
					r2menosr1++;
			}
		}

		uniao = interseccao + r1menosr2 + r2menosr1;

		double similaridade = r1menosr2 + r2menosr1;

		double retorno[] = new double[2];
		retorno[0] = similaridade;
		retorno[1] = uniao;
		return retorno;
	}

	/**
	 * Mï¿½todo que recebe como parï¿½metros os objetivos da busca e os
	 * instï¿½ncia
	 * 
	 * @param objetivos
	 *            Objetivos da busca
	 */
	public void addObjetivos(String[] objs) {
		objetivos = new ArrayList<FuncaoObjetivo>();
		if (objs != null) {
			for (int i = 0; i < objs.length; i++) {
				String objetivo = objs[i].toLowerCase();
				if (objetivo.equals("sens")) {
					Sensitividade sens = new Sensitividade();
					objetivos.add(sens);
				} else {
					if (objetivo.equals("spec")) {
						Especificidade spec = new Especificidade();
						objetivos.add(spec);
					} else {
						if (objetivo.equals("nov")) {
							Novidade nov = new Novidade();
							objetivos.add(nov);
						} else {
							if (objetivo.equals("lap")) {
								Laplace lap = new Laplace();
								objetivos.add(lap);
							}
						}
					}
				}
			}
		}
		// Caso os objetivos passados como parï¿½metro nï¿½o existam ï¿½
		// definido como
		// default Sens e Spec
		if (objetivos.size() == 0) {
			Sensitividade sens = new Sensitividade();
			objetivos.add(sens);
			Especificidade spec = new Especificidade();
			objetivos.add(spec);
		}
	}

	/**
	 * Mï¿½todo que percorre a base de dados e para cada atributo nominal
	 * preenche a distribuiï¿½ï¿½o dos valores
	 */
	@SuppressWarnings("unchecked")
	public void preencherDistribuicaoValores() {
		// Maior nï¿½mero de valores para um atributo na base
		int maxValues = 0;
		int num = 0;
		Enumeration<Attribute> atributos = dados.enumerateAttributes();
		while (atributos.hasMoreElements()) {
			Attribute att = (Attribute) atributos.nextElement();
			if (att.isNominal()) {
				num++;
				if (att.numValues() > maxValues)
					maxValues = att.numValues();
			}

		}

		distribuicaoAtributos = new int[num][maxValues];

		for (int i = 0; i < dados.numInstances(); i++) {
			Instance exemplo = dados.instance(i);
			int indice = 0;
			for (int j = 0; j < exemplo.numAttributes(); j++) {
				Attribute att = exemplo.attribute(j);
				if (att.isNominal() && att != dados.classAttribute()) {
					double temp = exemplo.value(j);
					if (temp != Double.NaN) {
						distribuicaoAtributos[indice++][(int) temp]++;
					}
				}
			}
		}

	}

	/**
	 * * @deprecated Resultado ruim!. Mï¿½todo que percorre todas as regras e
	 * verifica quais regras sï¿½o iguais nos atributos numï¿½ricos e possuem
	 * atributos numï¿½ricos que se cruzam.
	 */
	public void mergeRegrasCruzam(ArrayList<Regra> regrasCruzam) {

		for (int i = 0; i < regrasCruzam.size(); i++) {
			Regra r1 = regrasCruzam.get(i);
			for (int j = 0; j < regrasCruzam.size(); j++) {
				Regra r2 = regrasCruzam.get(j);
				boolean m = r1.merge(r2);
				if (m)
					regrasCruzam.remove(r2);
			}
		}
	}

	/**
	 * Mï¿½todo que preenche o conjunto de regras a partir de um arquivo com as
	 * regras com todos os valores dos atributos separados por virgula
	 * 
	 * @param arquivoRegras
	 *            Arquivo com as regras com todos os atributos separados por
	 *            virgula
	 * @param dadosOriginais
	 *            Objeto com as informacoes sobre os atributos da base de dados.
	 */
	/*
	 * public void preencherRegrasArquivo(String arquivoRegras, Instances
	 * dadosOriginais){
	 * 
	 * Enumeration<Attribute> atributos = dadosOriginais.enumerateAttributes();
	 * 
	 * int i = 0; while(atributos.hasMoreElements()){ Attribute att =
	 * (Attribute)atributos.nextElement(); if(att.isNominal()) regra.corpo[i] =
	 * new AtributoNominal(true, att, i); else{ double[] limites =
	 * obterMaximoMinimoAtributo(att); regra.corpo[i] = new
	 * AtributoNumerico(true, att, i, limites[0], limites[1]); } ++i; }
	 * 
	 * }
	 */
}
