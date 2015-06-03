package buscalocal;

import java.util.ArrayList;
import java.util.Iterator;

import kernel.ObterRegras;
import regra.Atributo;
import regra.AtributoNominal;
import regra.AtributoNumerico;
import regra.Regra;

public class BuscaLocal extends ObterRegras {

	public Regra regraAtual = null;
	
	public int numeroIteracoes;
	public int tamanhoVizinhanca;
	public int tamanhoTroca;
	
	
	
	public ArrayList<Regra> vizinhanca = null;
	
	public BuscaLocal(int n, int v, int t, String[] objetivos){
		super(objetivos);
		numeroIteracoes = n;
		tamanhoVizinhanca = v;
		tamanhoTroca = t;
		

	}
	
	public BuscaLocal(int n, int v, Regra regraInicial, String[] objetivos){
		super(objetivos);
		numeroIteracoes = n;
		tamanhoVizinhanca = v;
		
		regraAtual = regraInicial;
	

	}
	
	
	@SuppressWarnings("unchecked")
	public ArrayList<Regra> obterRegras(int numClasses) throws Exception {
		if(regraAtual == null)
			regraAtual = gerarRegraAleatoriaProporcional(dados.enumerateAttributes(), dados.classAttribute(), dados.numAttributes(), 0);
		preencherMatrizContigencia(regraAtual, dados);
		regraAtual.getConfidence();
		regraAtual.getValoresObjetivos();
		while(regraAtual.getConfidence()<0.1){

			regraAtual = gerarRegraAleatoriaProporcional(dados.enumerateAttributes(), dados.classAttribute(), dados.numAttributes(), 0);
			preencherMatrizContigencia(regraAtual, dados);

		}
		
		System.out.println(regraAtual);
		metodo1();
		//metodo2();
		
		regraAtual.getConfidence();
		System.out.println(regraAtual);
		
		regras = new ArrayList<Regra>();
		regras.add(regraAtual);
		return regras;
	}
	
	public void metodo1(){
		for(int i = 0; i<numeroIteracoes; i++){
			gerarVizinhanca();
			preencherMatrizContigencia(vizinhanca, dados);
			Regra melhorRegra = encontrarMelhorRegra(0);
			if(melhorRegra.getValoresObjetivos()[0]>regraAtual.getValoresObjetivos()[0])
				regraAtual = melhorRegra;	
		}
	}
	
/**
 * Método que encontra a melhor regra da vizinhança gerada pela busca local
 * @param posicaoObjetivo Indice do objetivo utilizado para a escolha do melhor
 * @return
 */
	public Regra encontrarMelhorRegra(int posicaoObjetivo){
		Regra melhorRegra = vizinhanca.get(0);
		double[] melhoresValores = melhorRegra.getValoresObjetivos();
		for (Iterator<Regra> iter = vizinhanca.iterator(); iter.hasNext();) {
			Regra temp = iter.next();
			double[] valores = temp.getValoresObjetivos();
			if(valores[posicaoObjetivo]>melhoresValores[posicaoObjetivo]){
				melhorRegra = temp;
				melhoresValores = valores;
			}
				
		}
		
		return melhorRegra;
	}
	
	/**
	 * Método que gera um conjunto de novas regras a partir da regra atual
	 *
	 */
	public void gerarVizinhanca(){	
		vizinhanca = new ArrayList<Regra>();		
		int pos = 0;
		for(int i = 0; i<tamanhoVizinhanca; i++){
			pos = tamanhoTroca*i;
			Regra novaRegra = gerarNovaRegra(regraAtual, tamanhoTroca, pos);
			while(novaRegra.isEmpty())
				novaRegra = gerarNovaRegra(regraAtual, tamanhoTroca, pos);
			vizinhanca.add(novaRegra);
		}
		
	}
	
	/**
	 * Método que constró uma nova regra a partir da atual
	 * @param regraAtual Regra atual, base para a nova regra
	 * @param vizinhanca Valor que defini quantos atributos da regra serao alterados
	 * @param pos Posicao do atributo que sera alterado inicialmente
	 * @return
	 */
	public Regra gerarNovaRegra(Regra regraAtual, int vizinhanca, int pos){
		
		 Regra novaRegra = (Regra)regraAtual.clone();
		 
		 for(int i = 0; i<vizinhanca; i++){
			 pos = pos % regraAtual.corpo.length;
			 Atributo atributo = novaRegra.corpo[pos];
			 if(atributo.isNominal()){
				 AtualizarAtributoNominalAleatorio(pos, atributo, regraAtual);
			 } else {
				 AtualizarAtributoNumericoAleatorio(pos, atributo, regraAtual);
			 }
			 
			 ++pos;

		 }
		 
		 return novaRegra;
		 
	}
	
	public void AtualizarAtributoNumericoAleatorio(int pos, Atributo a, Regra regra){
		AtributoNumerico atributo = (AtributoNumerico) a;
		//Caso o atributo seja vazio, não há limites disponiveis. Assim devem ser gerados
		//os limites aleatoriamente
		if(atributo.isVazio())
			regra.preencherAtributoNumericoAleatorio(pos, a);
		else{

			//Probabilidade de se escolher um atributo vazio
			double temp = Math.random();
			//Valor definido atraves de uma constante. Se numero aleatorio for menor
			//que a proabilidade de vazio, entao preencher o atributo com o valor vazio
			if(temp < AtributoNumerico.probabilidaVazioNumerico){
				double vazio = Double.MIN_VALUE;
				//Passa o valor mínimo para a funcao atualizar, que configura o atributo como vazio
				regra.corpo[pos].atualizar(vazio, vazio);
			} else {
				//Obtendo novo valor para o limite inferior
				//Escolher aleatoriamente se o limite vai aumentar ou diminuir.
				temp = Math.random();
				//Limite vai diminuir.
				double liminf = atributo.limiteInferior;
				if(temp<0.5){
					//Obtem o intervalo do limite inferior em relacao ao limite minimo do atributo
					double tamanhoIntervalo = atributo.limiteInferior - atributo.limiteMinimo;
					//Obtem o quanto o limite inferior vai recuar, em relação ao limite minimo.
					double delta = obterDelta(tamanhoIntervalo);
					//Diminue delta do limite inferior
					liminf-=delta;
				} else{
					//Caso o limite inferior aumente
//					Obtem o intervalo do limite inferior em relacao ao limite maximo do atributo
					double tamanhoIntervalo = atributo.limiteMaximo - atributo.limiteInferior;
					//Obtem o quanto o limite inferior vai aumentar, em relação ao limite minimo.
					double delta = obterDelta(tamanhoIntervalo);
					//Aumenta delta do limite inferior
					liminf+=delta;
				}

				//Obtendo novo valor para o limite superior
				//Escolher aleatoriamente se o limite vai aumentar ou diminuir.
				temp = Math.random();
				//Limite vai diminuir.
				double limsup = atributo.limiteSuperior;
				if(temp<0.5){
					//Obtem o intervalo do limite superior em relacao ao limite minimo do atributo
					double tamanhoIntervalo = atributo.limiteSuperior - atributo.limiteMinimo;
					//Obtem o quanto o limite superior vai recuar, em relação ao limite minimo.
					double delta = obterDelta(tamanhoIntervalo);
					//Diminue delta do limite superior
					limsup-=delta;
				} else{
					//Caso o limite superior aumente
//					Obtem o intervalo do limite superior em relacao ao limite maximo do atributo
					double tamanhoIntervalo = atributo.limiteMaximo - atributo.limiteSuperior;
					//Obtem o quanto o limite superior vai aumentar, em relação ao limite minimo.
					double delta = obterDelta(tamanhoIntervalo);
					//Aumenta delta do limite superior
					limsup+=delta;
				}

				if(liminf<limsup)
					atributo.atualizar(liminf, limsup);
				else
					atributo.atualizar(limsup, liminf);
			}
		}
	}
	
	public double obterDelta(double tamanhoIntervalo){
//		Calcula a ordem de grandeza do intervalo para o calculo dos limites
		double potencia = Math.ceil(Math.log10(tamanhoIntervalo));
		double ordemGrandeza = Math.pow(10,potencia);
		//Calcula os limites de forma aleatoria, dentro do tamanho do intevalo
		double delta = (Math.random()*ordemGrandeza)%tamanhoIntervalo;
		return delta;
	}
	
	public void AtualizarAtributoNominalAleatorio(int pos, Atributo atributo, Regra regra){
		
		//Possiveis valores para os atributos mais o valor vazio
		int numValues = atributo.att.numValues()+1;
		double porcentagens = (1.0/numValues);
		double temp = Math.random();
		int random = ((int)(temp/porcentagens)) -1;
		
		double operador = AtributoNominal.igual;
		atributo.atualizar(random, operador);
	}
	
	public static void main(String[] args) {
		BuscaLocal local = new BuscaLocal(10, 5, 2, null);
		String nomebase = "weather";
		//String caminhoBase = "/home/andre/mestrado/bases/celso/";
		String caminhoBase = "/home/andre/mestrado/weka-3-5-5/data/";
		//String arquivoARFF = caminhoBase + nomebase + "/it0/" + nomebase + "_data.arff";
		String arquivoARFF = caminhoBase + nomebase + ".arff";
		
		try{
			local.carregarInstancias(arquivoARFF,0,1);
			local.obterRegras(2);
		} catch (Exception e){e.printStackTrace();}
	}

}
