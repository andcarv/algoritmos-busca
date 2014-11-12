package conversao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class MatrizestoARFF {

	public Instances dados;
	
	public void gerarArquivoARFF(String nomeBase, String caminhoBase) throws Exception{
		String arquivoEntada = caminhoBase  + nomeBase +".txt";
		String arquivoSaida = caminhoBase  + nomeBase + ".arff";
		PrintStream ps = new PrintStream(arquivoSaida);
		Reader reader = new FileReader(arquivoEntada);
		BufferedReader buff = new BufferedReader(reader);
		
		FastVector attInfo = obterAtributos(buff);
		dados = new Instances(nomeBase, attInfo,1);
		dados.setClassIndex(dados.numAttributes()-1);
		carregarDados(buff);
		ps.println(dados);
		System.out.println(dados);
	}
	
	public FastVector obterAtributos(BufferedReader buff) throws Exception{
		
		String linha = buff.readLine();
		String[] atributos = linha.split(",");
		FastVector attInfo = new FastVector();
		for (int i = 0; i < atributos.length; i++) {
			String nome = atributos[i];
			Attribute att;
			//if(i==atributos.length-1){
				FastVector nominal_values = new FastVector(2);
				nominal_values.addElement("0");
				nominal_values.addElement("1");
				att = new Attribute(nome, nominal_values);
			/*} else
				att = new Attribute(nome);*/
			attInfo.addElement(att);
		}
		
		return attInfo;
	}
	
	public void carregarDados(BufferedReader buff) throws Exception{
		
		while(buff.ready()){
			String linha = buff.readLine();
			String atributos[] = linha.split(",");
			Instance exemplo = new Instance(dados.numAttributes());
			exemplo.setDataset(dados);
			for (int i = 0; i < atributos.length; i++) {
				String valor = atributos[i].trim();		
				//if(i==atributos.length-1){
					exemplo.setValue(i,valor);
				/*}
				else
					exemplo.setValue(i,new Double(valor));*/
				
			}
			String classV = exemplo.classAttribute().value((int)exemplo.classValue());
			dados.add(exemplo);
			if(classV.equals("1")){
				dados.add(exemplo);
				dados.add(exemplo);
			}
		}
		
	}	
	
	public static void main(String[] args) {
		try{
			MatrizestoARFF gerador = new MatrizestoARFF();
			String nomeBase = "TransImagem_MatrizMutante8";
			String caminhoBase = "C:\\Andre\\Bases\\Programas\\TransformarImagens\\";
			gerador.gerarArquivoARFF(nomeBase, caminhoBase);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
