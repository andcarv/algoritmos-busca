package conversao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Random;



import weka.core.Instances;

public class GerarFolds {
	
	public void gerarFolds(int numFold, Instances dados, String nomeBase, String caminhoBase) throws IOException{
		long seed = System.currentTimeMillis();
		Random rand = new Random(seed);
		Instances randData = new Instances(dados);
		randData.stratify(numFold);
		
		for (int n = 0; n < numFold; n++) {
			  System.out.println("Fold: " + n);
			  Instances train = randData.trainCV(numFold, n, rand);
			  Instances test = randData.testCV(numFold, n);
			  String caminhoDiretorio = caminhoBase  + "\\it" + n + "\\";
			  File dir = new File(caminhoDiretorio);   
			  dir.mkdir();
			  String arquivoTreinamento = caminhoDiretorio + nomeBase + "_data.arff";
			  String arquivoTeste = caminhoDiretorio + nomeBase + "_test.arff";
			  System.out.println("Treinamento: " + arquivoTreinamento);
			  System.out.println("Teste: " + arquivoTeste);
			  gerarArquivoARFF(arquivoTreinamento, train);
			  gerarArquivoARFF(arquivoTeste, test);
	
			 
			}
		
	}
	
	public void gerarArquivoARFF(String arquivoARFF, Instances dados) throws IOException{
		
		OutputStream os = new FileOutputStream(arquivoARFF);
		PrintStream ps = new PrintStream(os);
		ps.println(dados);
		
	}
	
	public static void main(String[] args) {
		
		GerarFolds folds = new GerarFolds();
		String nomebase = "hepatitis";
		String caminhoBase = "c:\\bases\\"+nomebase+"\\";
		//String caminhoBase = "/home/andre/mestrado/weka-3-5-5/data/";
		//String arquivoARFF = caminhoBase + nomebase + "/it0/" + nomebase + "_data.arff";
		String arquivoARFF = caminhoBase + nomebase + ".arff";
		
		try{
			Reader reader = new FileReader(arquivoARFF);
			Instances dados = new Instances(reader);
			dados.setClassIndex(dados.numAttributes()-1);
			folds.gerarFolds(10, dados, nomebase, caminhoBase);
		} catch (Exception e){e.printStackTrace();}
	}

}

