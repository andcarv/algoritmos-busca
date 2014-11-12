package indicadores;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Classe que representa um indicador para mensurar a qualidade de uma fronteira de Pareto
 * Cont�m m�todo para leitura das solu��es e grava��o dos resultados
 * @author Andre
 *
 */
public abstract class Indicador {
	//N�mero de objetivos
	public int m;
	//Fronteira
	public ArrayList<PontoFronteira> fronteira = null;
	//Identiciador do indicador
	public String indicador;
	//Array que define so o objetivo � de maximiza��o ou minimiza��o
	public double[] objetivosMaxMin = null;
	
	//Arquivos de saida do indicador
	protected PrintStream psIndGeral = null;
	protected PrintStream psIndComando = null;
	protected String caminhoSaida = "";
	protected String idExecucao = "";
	protected StringBuffer comando= null;
	
	
	public ArrayList<PontoFronteira> PFtrue = null;
	
	/**
	 * Construtor da classes 
	 * @param m N�mero de objetivos
	 * @param caminho Caminho do arquivo de sa�da
	 * @param id Identificador do arquivo de sa�da
	 */
	
	public Indicador(int m, String caminho, String id){
		this.m = m;
		caminhoSaida = caminho;
		idExecucao = id;
	}
	

	/**
	 * M�todo que define para cada objetivo se ele � de maximiza��o ou minimiza��o
	 * @param maxmim
	 */
	public void preencherObjetivosMaxMin(String[] maxmim){
		objetivosMaxMin = new double[maxmim.length];
		for (int i = 0; i < maxmim.length; i++) {
			if(maxmim[i].equals("+"))
				objetivosMaxMin[i] = 1;
			else
				objetivosMaxMin[i] = -1;
		}
	}
		
	
	/**
	 * M�todo que calcula o valor do indicador para a fronteira carregada
	 * @return
	 */
	public abstract double calcular();
	
	/**
	 * M�todo que calcula o valor do indicador atrav�s dos dados da fronteira passados como parametro
	 * @param nomeArquivo Caminho do arquivo de entrada
	 * @throws IOException
	 */
	public void calcularIndicadorArquivo(String nomeArquivo) throws IOException{
		
		ArrayList<ArrayList<PontoFronteira>> fronteiras = new ArrayList<ArrayList<PontoFronteira>>();
		
		iniciarArquivosSaida();
		
		carregarArrayList(nomeArquivo, fronteiras);
		
		if(!indicador.equals("tchebycheff"))
			calcularIndicadorArray(fronteiras);
		else{
			Tchebycheff tcheb = (Tchebycheff) this;
			tcheb.calcularTchebycheff(fronteiras);
		}
		fecharArquivosSaida();
	}


	protected void carregarArrayList(String nomeArquivo,
			ArrayList<ArrayList<PontoFronteira>> fronteiras)
			throws FileNotFoundException, IOException {
		BufferedReader buff = new BufferedReader(new FileReader(nomeArquivo));
		
		ArrayList<PontoFronteira> fronteira = new ArrayList<PontoFronteira>();
		//Flag que indica se a ultima fronteira foi calculada ou nao
		boolean calcularFronteira = false;
		while(buff.ready()){
			String line = buff.readLine();
			if(line.equals("")){
				if(fronteira.size()>0){
					//double valor = calcular();
					//imprimirValor(valor);
					fronteiras.add(fronteira);
					calcularFronteira = false;
					fronteira = new ArrayList<PontoFronteira>();
				}
			} else{
				calcularFronteira = true;
				String[] valores = line.split("\t");
				if(valores.length<m){
					System.err.println("Arquivo contem fronteiras com menos objetivos (" + valores.length + ") do que passado como parametro (" + m + ")");
					System.exit(0);
				}
				
				double[] ponto = new double[m];
				
				for (int i = 0; i < m; i++) {
					if(valores[i].contains(","))
						ponto[i] = new Double(valores[i].replace(',', '.'));
					else
						ponto[i] = new Double(valores[i]);
				}
				PontoFronteira pf = new PontoFronteira(ponto);
				fronteira.add(pf);
			}
		}
		buff.close();
		if(calcularFronteira){
			//double valor = calcular();
			//imprimirValor(valor);
			fronteiras.add(fronteira);
		}
	}
	
	
	/**
	 * M�todo que calcula o indicador atrav�s de um arraylist de fronteiras
	 * @param fronteiras
	 * @throws IOException
	 */
	public void calcularIndicadorArray(ArrayList<ArrayList<PontoFronteira>> fronteiras) throws IOException{
		iniciarArquivosSaida();
//		if(indicador.equals("gd"))
//			filterSolucoesNaoDominadas(fronteiras);
		for (Iterator<ArrayList<PontoFronteira>> iterator = fronteiras.iterator(); iterator.hasNext();) {
			fronteira =  iterator.next();
			double valor = calcular();
			imprimirValor(valor);
		}
		fecharArquivosSaida();
	}
	

	
	/**
	 * M�todo que inicia os arquivos de saida
	 * @throws IOException
	 */
	public void iniciarArquivosSaida() throws IOException{	
		File dir = new File(caminhoSaida);
		dir.mkdirs();
		psIndGeral = new PrintStream(caminhoSaida + idExecucao  + "_" + indicador +".txt");
		psIndComando = new PrintStream(caminhoSaida+ idExecucao + "_" + indicador +"_comando.txt");
		comando = new StringBuffer();
		comando.append(idExecucao + "_" + indicador +"<- c(");
	}
	
	/**
	 * M�todo que imprime o valor do indicador num arquivo de sa�da e no console
	 * @param valor
	 */
	public void imprimirValor(double valor){
		if(psIndGeral == null || psIndComando == null || comando == null){
			System.err.println("Erro ao imprimir valor do indicador em arquivo. Arquivos de sa�da n�o foram iniciados");
			System.exit(0);
		}
		System.out.println(indicador + " = " + valor);
		comando.append(valor + ",");
		//psIndGeral.println(valor);
		psIndGeral.println(new Double(valor).toString().replace('.', ','));
	}
	
	/**
	 * M�todo que gera o comando do R e fecha os arquivos de sa�da
	 */
	public void fecharArquivosSaida(){
		comando.deleteCharAt(comando.length()-1);
		comando.append(")");
		psIndComando.println(comando);
		
		psIndComando.flush();
		psIndComando.close();
		
		psIndGeral.flush();
		psIndGeral.close();
	}
	
	/**
	 * M�todo que verifica se uma solu��o domina a outra
	 * @param sol1 Solu��o que ser� comparada com as regras pertencentes a fronteira de pareto
	 * @param sol2 Solu��o pertencente a fronteira de pareto
	 * @return -1 Se sol1 for dominada, 0 se a sol1 nao domina nem eh dominada, 1 sol1 domina sol2 
	 */
	public int compararMedidas(double[] sol1, double[] sol2){
		//Contador que marca quantos valores da regra 1 sao maiores que os da regra2
		//Se cont for igual ao tamanho dos elementos da regra 1 entao a regra 2 eh dominada pela regra1
		//Se cont for igual a 0 a regra2 domina a regra1
		//Se cont for maior do que 0 e menor que o tamanho ela nao domina e nem eh dominada
		int cont = 0; 
		int cont2 = sol1.length;
		for (int i = 0; i < sol1.length; i++) {
			if(sol1[i]*objetivosMaxMin[i]>sol2[i]*objetivosMaxMin[i]){
				++cont;
			} else {
				if(sol1[i]==sol2[i]){
					--cont2;
				}
			}
		}
		if(cont == 0){	
			if(cont2 == 0)
				return 0;
			else
				return -1;
		}
		else{
			if(cont>0 && cont<cont2)
				return 0;
			else return 1;
		}
	}
	
	public double distanciaEuclidiana(double[] vetor1, double[] vetor2){
		double soma = 0;
		for (int i = 0; i < vetor1.length; i++) {
			soma += Math.pow(vetor1[i]-vetor2[i],2);
		}
		return Math.sqrt(soma);
	}
	
	public void filterSolucoesNaoDominadas(ArrayList<ArrayList<PontoFronteira>> fronteiras){
		PFtrue = new ArrayList<PontoFronteira>();
		for (Iterator<ArrayList<PontoFronteira>> iterator = fronteiras.iterator(); iterator.hasNext();) {
			ArrayList<PontoFronteira> arrayList =  iterator.next();
			for (Iterator<PontoFronteira> iterator2 = arrayList.iterator(); iterator2.hasNext();) {
				PontoFronteira pontoFronteira = iterator2
						.next();
				add(pontoFronteira, PFtrue);
			}
		}		
	}
	
	@SuppressWarnings("unchecked")
	public boolean add(PontoFronteira pf, ArrayList<PontoFronteira> fronteira){

		if(fronteira.size()==0){
			fronteira.add(pf);
			return true;
		}
		
		int comp;
		
		ArrayList<PontoFronteira> cloneFronteira = (ArrayList<PontoFronteira>)fronteira.clone();
		
		for (Iterator<PontoFronteira> iter = cloneFronteira.iterator(); iter.hasNext();) {
			PontoFronteira temp =  iter.next();	
			comp = compararMedidas(pf.objetivos, temp.objetivos);
			if(comp == -1)
				return false;
			if(comp == 1)
				fronteira.remove(temp);
			
		}
		
		fronteira.add(pf);	
		
		
		return true;
		
	}
	
	protected double menorDistanciaEuclidiana(PontoFronteira ponto, ArrayList<PontoFronteira> fronteira) {
		double menor_distancia = Double.MAX_VALUE;
		//Obtem a menor dist�ncia entre um ponto do conjunto de aproxima��o e um ponto da fronteira de pareto real
		for(int j = 0; j<fronteira.size();j++){
			PontoFronteira ponto2 = fronteira.get(j);
			menor_distancia = Math.min(distanciaEuclidiana(ponto.objetivos, ponto2.objetivos), menor_distancia);
		}
		return menor_distancia;
	}
	
}


