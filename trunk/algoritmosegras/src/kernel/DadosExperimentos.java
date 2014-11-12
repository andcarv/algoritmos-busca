package kernel;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Classe que guarda informações de todo o conjunto de execuções. Coleção de DadosFold
 * @author andre
 *
 */
public class DadosExperimentos {
	
	public String nomeBase = null;
	public String metodo = null;
	public ArrayList<DadosExecucao> dadosFolds = null;
	public double mediaAreas;
	public double mediaPrecisao;
	public double desvioPadrao;
	public double mediaNumRegras;
	
	public String ID;
	
	
	public DadosExperimentos(String n, String m, String id){
		nomeBase = n;
		metodo = m;
		dadosFolds = new ArrayList<DadosExecucao>();
		mediaAreas = 0;
		mediaPrecisao = 0;
		mediaNumRegras = 0;
		
		this.ID=id;
		
	}
	
	public DadosExperimentos(){
		dadosFolds = new ArrayList<DadosExecucao>();
		mediaAreas = 0;
		mediaPrecisao = 0;
		mediaNumRegras = 0;
		ID = "";
	}
	
	public void addFold(DadosExecucao d){
		dadosFolds.add(d);
	}
	
	public void calcularMediaAreasPrecisaoNumRegras(){
	
		double somaAUC = 0;
		double somaRegra = 0;
		double somaPrecisao = 0;
		for (Iterator<DadosExecucao> iter = dadosFolds.iterator(); iter.hasNext();) {
			DadosExecucao dados = iter.next();
			somaAUC+= dados.auc;
			somaRegra+= dados.numRegrasTotal;
			somaPrecisao+= dados.accuracy;
		}
		
		if(dadosFolds.size()>0){
		   mediaAreas = somaAUC/dadosFolds.size();
		   mediaNumRegras = somaRegra/dadosFolds.size();
		   mediaPrecisao = somaPrecisao/dadosFolds.size();
		}
	}
	
	public double calcularDesvioPadrao(){
		if(mediaAreas == 0)
			calcularMediaAreasPrecisaoNumRegras();
		double soma = 0;
		for (Iterator<DadosExecucao> iter = dadosFolds.iterator(); iter.hasNext();) {
			DadosExecucao fold = (DadosExecucao) iter.next();
			double diferenca = fold.auc - mediaAreas; 
			double sqrtDif = diferenca*diferenca;
			soma+= sqrtDif;
		}
		
		if(dadosFolds.size()>0)
		   desvioPadrao = Math.sqrt(soma/dadosFolds.size());
		return desvioPadrao;
	}
	
	/**
	 * Método que gera dois arquivos um contendo as medidas (AUC, Accuracy, Precison, Recall, F-Measure) e
	 * outro contendo os comandos para carregar vetores na ferramenta R
	 * @param caminhoSaida Caminho da saida dos arquivos
	 * @param arquivoMedidas Nome do arquivo com as medidas
	 * @param arquivoComandos Nome do arquivo com os comandos
	 * @param ID String utilizada para diferenciar vários dados de uma mesma execucao
	 * @throws Exception
	 */
	public void gerarArquivosMedidas(String caminhoSaida, String arquivoMedidas, String arquivoComandos, String arquivoConfusao) throws Exception{
		PrintStream med = new PrintStream(caminhoSaida + arquivoMedidas + ID + ".txt");
		PrintStream com = new PrintStream(caminhoSaida + arquivoComandos + ID + ".txt");
		PrintStream conf = new PrintStream(caminhoSaida + arquivoConfusao + ID + ".txt");
		
		//Criacao das strings com os comandos
		
		StringBuffer comandoAuc = new StringBuffer();
		comandoAuc.append("auc_" + metodo);
		comandoAuc.append(nomeBase.substring(0,3) + "<-c(");
		
		StringBuffer comandoFm = new StringBuffer();
		comandoFm.append("fm_"+metodo);
		comandoFm.append(nomeBase.substring(0,3) + "<-c(");
		
		StringBuffer comandoRec = new StringBuffer();
		comandoRec.append("rec_" + metodo);
		comandoRec.append(nomeBase.substring(0,3) + "<-c(");
		
		StringBuffer comandoPrec = new StringBuffer();
		comandoPrec.append("prec_" + metodo);
		comandoPrec.append(nomeBase.substring(0,3) + "<-c(");
		
		StringBuffer comandoFmNeg = new StringBuffer();
		comandoFmNeg.append("fm_neg_"+metodo);
		comandoFmNeg.append(nomeBase.substring(0,3) + "<-c(");
		
		StringBuffer comandoRecNeg = new StringBuffer();
		comandoRecNeg.append("rec_neg_" + metodo);
		comandoRecNeg.append(nomeBase.substring(0,3) + "<-c(");
		
		StringBuffer comandoPrecNeg = new StringBuffer();
		comandoPrecNeg.append("prec_neg_" + metodo);
		comandoPrecNeg.append(nomeBase.substring(0,3) + "<-c(");
		
		StringBuffer comandoAcc = new StringBuffer();
		comandoAcc.append("acc_" + metodo);
		comandoAcc.append(nomeBase.substring(0,3) + "<-c(");
		
		//Precorre os dados de todas as execuções do programa
		for (Iterator<DadosExecucao> iter = dadosFolds.iterator(); iter.hasNext();) {
			DadosExecucao fold = (DadosExecucao) iter.next();
			//Impressao das medidas no arquivo
			med.println(fold);
			conf.println(fold.confusao);
			conf.println();
			
			comandoAuc.append(fold.auc + ",");
			comandoAcc.append(fold.accuracy + ",");
			
			comandoPrec.append(fold.precisionPos + ",");
			comandoRec.append(fold.recallPos + ",");
			comandoFm.append(fold.fmeasurePos + ",");
			
			
			comandoPrecNeg.append(fold.precisionNeg + ",");
			comandoRecNeg.append(fold.recallNeg + ",");
			comandoFmNeg.append(fold.fmeasureNeg + ",");
			
		}
		
		
		comandoAuc.deleteCharAt(comandoAuc.length()-1);
		comandoAuc.append(")");
		
		comandoAcc.deleteCharAt(comandoAcc.length()-1);
		comandoAcc.append(")");

		
		comandoPrec.deleteCharAt(comandoPrec.length()-1);
		comandoPrec.append(")");
		
		comandoRec.deleteCharAt(comandoRec.length()-1);
		comandoRec.append(")");
		
		comandoFm.deleteCharAt(comandoFm.length()-1);
		comandoFm.append(")");
		
		comandoRecNeg.deleteCharAt(comandoRecNeg.length()-1);
		comandoRecNeg.append(")");
		
		comandoPrecNeg.deleteCharAt(comandoPrecNeg.length()-1);
		comandoPrecNeg.append(")");
		
		comandoFmNeg.deleteCharAt(comandoFmNeg.length()-1);
		comandoFmNeg.append(")");
		
				
		
		com.println(comandoAuc);
		com.println();
		com.println(comandoAcc);
		com.println();
		com.println(comandoPrec);
		com.println();
		com.println(comandoRec);
		com.println();
		com.println(comandoFm);
		com.println();
		com.println(comandoPrecNeg);
		com.println();
		com.println(comandoRecNeg);
		com.println();
		com.println(comandoFmNeg);
		com.println();
		
	}
	
	
	public String toString(){
		StringBuffer str = new StringBuffer();
		str.append("Base:\t"+ nomeBase + "\n");
		str.append("\tAUC\t#Accuracy\t\t#PrecisionPos\t#RecallPos\t#F-MeasurePos\t\t#PrecisionNeg\t#RecallNeg\t#F-MeasureNeg\t\t#Regras Total\n");
		for (Iterator<DadosExecucao> iter = dadosFolds.iterator(); iter.hasNext();) {
			DadosExecucao fold = (DadosExecucao) iter.next();
			str.append("Fold: " + fold.numExec+"-"+ fold.numFold + "\t" + fold.toString() + "\n");
		}
		Double media = new Double(mediaAreas);
		Double precisao = new Double(mediaPrecisao);
		Double desvio = new Double(desvioPadrao);
		Double numRegras = new Double(mediaNumRegras);
		str.append("Media das Areas:\t" + media.toString().replace('.', ',') + "\n");
		str.append("Desvio Padrao:\t" + desvio.toString().replace('.', ',') + "\n");
		str.append("Número médio de regras:\t" + numRegras.toString().replace('.', ',') + "\n");
		str.append("Media das Precisoes:\t" + precisao.toString().replace('.', ',') + "\n\n");
		/*for (Iterator iter = dadosFolds.iterator(); iter.hasNext();) {
			DadosFold fold = (DadosFold) iter.next();
			str.append("Fold: " + fold.numFold + "\n");
			str.append(fold.confusao + "\n\n");
		}*/
		
		
		return str.toString();
	}

}
