package conversao;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class ARFFtoC45 {
	
	public void gerarArquivosC45(String nomeBase, String caminhoBase) throws IOException{
		int numFolds = 1;
		for(int i = 0; i<numFolds; i++){
			String nomeArquivo = caminhoBase + nomeBase + "\\it"+i+"\\" + nomeBase;
			String arquivoTreinamento =  nomeArquivo + "_data.arff"; 
			//String arquivoTeste = nomeBase + caminhoBase + "_test.ARFF";
			Reader reader = new FileReader(arquivoTreinamento);
			Instances dadosTreinamento = new Instances(reader);
			dadosTreinamento.setClassIndex(dadosTreinamento.numAttributes()-1);
			System.out.println("Gerando arquivo .names");
			gerarArquivoNames(dadosTreinamento, nomeArquivo);
			System.out.println("Gerando arqivo .data");
			gerarArquivosDataTest(dadosTreinamento, nomeArquivo, "_C45.data");
			String arquivoTeste =  nomeArquivo + "_test.arff";
			reader = new FileReader(arquivoTeste);
			Instances dadosTeste = new Instances(reader);
			dadosTeste.setClassIndex(dadosTreinamento.numAttributes()-1);
			System.out.println("Gerando arquivo.test");
			gerarArquivosDataTest(dadosTeste, nomeArquivo, "_C45.test");
		}
	}
	
	public void gerarArquivoNames(Instances dados, String nomeArquivo) throws FileNotFoundException{
		String arquivoNames =  nomeArquivo + "_C45.names"; 
		PrintStream ps = new PrintStream(arquivoNames);
		ps.println("| Class Features");
		ps.println("class.");
		ps.println();
		ps.println("| Features");

		for(int i = 0; i<dados.numAttributes(); i++){
		   Attribute att = dados.attribute(i); 
		   ps.print(att.name() + ": ");
		   if(att.isNominal())
			   ps.print("nominal(");
		   for(int j = 0; j < att.numValues(); j++){
			   String temp = att.value(j); 
			   ps.print(temp.replace('\'','\"'));
			   if(j != att.numValues()-1 )
				   ps.print(",");
			   else
				   ps.print(")");
		   }
		   ps.println();
		}
		ps.flush();
		ps.close();
		
	}
	
	public void gerarArquivosDataTest(Instances dados, String nomeArquivo, String extensao) throws FileNotFoundException{
		String arquivoData =  nomeArquivo + extensao; 
		PrintStream ps = new PrintStream(arquivoData);
		for(int i = 0; i< dados.numInstances(); i++){
			if(i%100 == 0)
				System.out.println(i + " - " + dados.numInstances());
			Instance exemplo = dados.instance(i);
			for(int j = 0; j<dados.numAttributes(); j++){
				Attribute att = exemplo.attribute(j);
				int valor = (int)exemplo.value(j);
				String temp = att.value(valor); 
				ps.print(temp.replace('\'','\"'));
				 if(j != att.numValues()-1 )
					   ps.print(", ");
				   
			}
			ps.println();
			/*
			Attribute classAttribute = exemplo.classAttribute();
			int valorClasse = (int) exemplo.classValue();
			ps.println(classAttribute.value(valorClasse));
			*/
		}
		ps.flush();
		ps.close();
	}
	
	public void gerarArquivosFormatoCelso(String nomeBase, String caminhoBase) throws IOException{
		int numFolds = 10;
		for(int i = 0; i<numFolds; i++){
			String nomeArquivo = caminhoBase + nomeBase + "\\it"+i+"\\" + nomeBase;
			String arquivoTreinamento =  nomeArquivo + "_data.arff"; 
			//String arquivoTeste = nomeBase + caminhoBase + "_test.ARFF";
			Reader reader = new FileReader(arquivoTreinamento);
			Instances dadosTreinamento = new Instances(reader);
			dadosTreinamento.setClassIndex(dadosTreinamento.numAttributes()-1);
			String arquivoSaida =  nomeArquivo + ".learn"+i; 
			PrintStream ps = new PrintStream(arquivoSaida);
			System.out.println("Gerando arquivo de treinamento...");
			gerarArquivo(dadosTreinamento, ps);
			String arquivoTeste =  nomeArquivo + "_test.arff";
			reader = new FileReader(arquivoTeste);
			Instances dadosTeste = new Instances(reader);
			dadosTeste.setClassIndex(dadosTreinamento.numAttributes()-1);
			arquivoSaida =  nomeArquivo + ".test"+i; 
			ps = new PrintStream(arquivoSaida);
			System.out.println("Gerando arquivo de teste...");
			gerarArquivo(dadosTeste, ps);
		}
		
		
	}
	
	public void construirCabecalho(Instances dados, PrintStream ps){
		ps.print(dados.numInstances() + " ");
		//System.out.print(dados.numInstances() + " ");
		ps.print(dados.numAttributes() + " ");
		//System.out.print(dados.numAttributes() + " ");
		ps.print(dados.classAttribute().numValues() + " ");
		//System.out.print(dados.classAttribute().numValues() + " ");
		//Classe positiva true
		ps.print(1 + " ");
		//System.out.print(1 + " ");
		//Classe negativa
		ps.println(0);
		//System.out.println(0);
		ps.println(dados.numAttributes()-1);
	}
	
	public void gerarArquivo(Instances dados, PrintStream ps){
		construirCabecalho(dados, ps);
		for(int i = 0; i<dados.numInstances(); i++){
			if(i%50 == 0)
				System.out.println(i + " - " + dados.numInstances());
			Instance exemplo = dados.instance(i);
			for(int j = 0; j<exemplo.numAttributes(); j++){
				int valor = (int)exemplo.value(j);
				ps.print(valor  + "\t");
				//System.out.print(valor + "\t");
			}
			ps.println();
			//System.out.println();
		}
	}
	
	public static void main(String[] args) {
		ARFFtoC45 conversor = new ARFFtoC45();
		String nomeBase = "pc1";
		String caminhoBase  = "C:\\Andre\\Bases\\NASA\\";
		try{
			//conversor.gerarArquivosC45(nomeBase, caminhoBase);
			conversor.gerarArquivosFormatoCelso(nomeBase, caminhoBase);
		} catch(Exception ex) {ex.printStackTrace();}
	}

}
