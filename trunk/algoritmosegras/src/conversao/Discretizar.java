package conversao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;



public class Discretizar {

	
	public void discretizar(String nomeBase, String caminhoBase, String nomeSaida, int numFolds, int numBin) throws Exception{
		Discretize disc = new Discretize();
		disc.setBins(numBin);
		for(int i = 0; i<numFolds; i++){
			String arquivoTreinamento = caminhoBase + nomeBase + "\\it" + i + "\\" + nomeBase + "_data.arff";
			String arquivoTreinamentoSaida = caminhoBase + nomeSaida + "\\it" + i + "\\" + nomeSaida + "_data.arff";
			String diretorio = caminhoBase + nomeSaida + "\\it" + i + "\\";
			File dir = new File(diretorio);
			dir.mkdirs();
			String arquivoTeste = caminhoBase + nomeBase + "\\it" + i + "\\" + nomeBase + "_test.arff";
			String arquivoTesteSaida = caminhoBase + nomeSaida + "\\it" + i + "\\" + nomeSaida + "_test.arff";
			Reader reader = new FileReader(arquivoTreinamento);
			Instances data = new Instances(reader);
			System.out.println("Treinamento: " + arquivoTreinamentoSaida);
			System.out.println("Teste: " + arquivoTesteSaida);
			discretizarDados(arquivoTreinamentoSaida, disc, data);
			reader = new FileReader(arquivoTeste);
			data = new Instances(reader);
			discretizarDados(arquivoTesteSaida, disc, data);
		}
	}
	
	public void discretizarAuto(String nomeBase, String caminhoBase, String nomeSaida, int numFolds) throws Exception{
		weka.filters.supervised.attribute.Discretize disc = new weka.filters.supervised.attribute.Discretize();
		for(int i = 0; i<numFolds; i++){
			String arquivoTreinamento = caminhoBase + nomeBase + "\\it" + i + "\\" + nomeBase + "_data.arff";
			String arquivoTreinamentoSaida = caminhoBase + nomeSaida + "\\it" + i + "\\" + nomeSaida + "_data.arff";
			String diretorio = caminhoBase + nomeSaida + "\\it" + i + "\\";
			File dir = new File(diretorio);
			dir.mkdirs();
			String arquivoTeste = caminhoBase + nomeBase + "\\it" + i + "\\" + nomeBase + "_test.arff";
			String arquivoTesteSaida = caminhoBase + nomeSaida + "\\it" + i + "\\" + nomeSaida + "_test.arff";
			Reader reader = new FileReader(arquivoTreinamento);
			Instances data = new Instances(reader);
			System.out.println("Treinamento: " + arquivoTreinamentoSaida);
			System.out.println("Teste: " + arquivoTesteSaida);
			discretizarDados(arquivoTreinamentoSaida, disc, data);
			reader = new FileReader(arquivoTeste);
			data = new Instances(reader);
			discretizarDados(arquivoTesteSaida, disc, data);
		}
	}

	private void discretizarDados(String arquivoTreinamentoSaida, Filter filter, Instances data) throws Exception, FileNotFoundException {
		data.setClassIndex(data.numAttributes()-1);
		filter.setInputFormat(data);
		for (int j = 0; j < data.numInstances(); j++) {
			filter.input(data.instance(j));
		}
		filter.batchFinished();
		Instances newData = filter.getOutputFormat();
		Instance processed;
		while ((processed = filter.output()) != null) {
			newData.add(processed);
		}
			
		for(int i=0; i<newData.numAttributes(); i++){
			Attribute att = newData.attribute(i);
			if(att.numValues() == 1){
				newData.deleteAttributeAt(i);
				--i;
			}
		}
		
		PrintStream ps = new PrintStream(arquivoTreinamentoSaida);
		ps.println(newData);
	}
	
	public void discretizarBaseSemFolds(String nomeBase, String caminhoBase, String nomeSaida, int numBin) throws Exception{
		Discretize disc = new Discretize();
		disc.setBins(numBin);
		String arquivoTreinamento = caminhoBase  + "\\" + nomeBase + ".arff";
		String arquivoTreinamentoSaida = caminhoBase  + "\\" + nomeSaida + ".arff";
		String diretorio = caminhoBase  + "\\";
		File dir = new File(diretorio);
		dir.mkdirs();
		Reader reader = new FileReader(arquivoTreinamento);
		Instances data = new Instances(reader);
		System.out.println("Treinamento: " + arquivoTreinamentoSaida);
		discretizarDados(arquivoTreinamentoSaida, disc, data);
	}
	
	public void discretizarBaseSemFoldsAuto(String nomeBase, String caminhoBase, String nomeSaida) throws Exception{
		weka.filters.supervised.attribute.Discretize disc = new weka.filters.supervised.attribute.Discretize();
		String arquivoTreinamento = caminhoBase  + "\\" + nomeBase + ".arff";
		String arquivoTreinamentoSaida = caminhoBase  + "\\" + nomeSaida + ".arff";
		String diretorio = caminhoBase  + "\\";
		File dir = new File(diretorio);
		dir.mkdirs();
		Reader reader = new FileReader(arquivoTreinamento);
		Instances data = new Instances(reader);
		System.out.println("Treinamento: " + arquivoTreinamentoSaida);
		discretizarDados(arquivoTreinamentoSaida, disc, data);
	}
	
	public static void main(String[] args) {
		try{
			Discretizar disc = new Discretizar();
			String nome = "pima";
			String nomeBase = nome+"_numerico";
			String caminhoBase = "C:\\Andre\\Bases\\book_fronteira\\"+nome+"\\";
			String nomeSaida3 = nome+"_discreto_3";
			String nomeSaida5 = nome+"_discreto_5";
			String nomeSaida7 = nome+"_discreto_7";
			String nomeSaidaAuto = nome+"_discreto_auto";
			//disc.discretizar(nomeBase, caminhoBase, nomeSaida,10,5);
			//disc.discretizarAuto(nomeBase, caminhoBase, nomeSaida,10);
			disc.discretizarBaseSemFolds(nomeBase, caminhoBase, nomeSaida3,3);
			disc.discretizarBaseSemFolds(nomeBase, caminhoBase, nomeSaida5,5);
			disc.discretizarBaseSemFolds(nomeBase, caminhoBase, nomeSaida7,7);
			disc.discretizarBaseSemFoldsAuto(nomeBase, caminhoBase, nomeSaidaAuto);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
