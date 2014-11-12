package conversao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class UCItoARFF {

	public Instances dados;
	
	public void gerarArquivoARFF(String nomeBase, String caminhoBase) throws Exception{
		String arquivoEntada = caminhoBase  + nomeBase +".data";
		String arquivoSaida = caminhoBase  + nomeBase + ".arff";
		PrintStream ps = new PrintStream(arquivoSaida);
		Reader reader = new FileReader(arquivoEntada);
		BufferedReader buff = new BufferedReader(reader);
		
		FastVector attInfo = obterAtributos(buff);
		dados = new Instances(nomeBase, attInfo,1);
		dados.setClassIndex(1);
		System.out.println(dados);
		carregarDados(buff,",");
		ps.println(dados);
		//System.out.println(dados);
	}
	
	public FastVector obterAtributos(BufferedReader buff) throws Exception{
		
		String linha = buff.readLine();
		String[] atributos = linha.split(",");
		FastVector attInfo = new FastVector();
		for (int i = 0; i < atributos.length; i++) {
			String nome = atributos[i];
			Attribute att = null;
			boolean b = nome.endsWith("_n");
			boolean s = nome.endsWith("_s");
			if(b){
			   	String linha2 = buff.readLine();
			   	String[] valores = linha2.split(",");
			   	FastVector nominal_values = new FastVector(2);
			   	for (int j = 0; j < valores.length; j++) {
					nominal_values.addElement(valores[j].trim());
				}
			   	nome = nome.replaceAll("_n","");
			   	att = new Attribute(nome,nominal_values);
			} else{
				if(s){
					nome = nome.replaceAll("_s","");
					FastVector fv = null;
					att = new Attribute(nome,fv);
				} else
					att = new Attribute(nome);
			}
			attInfo.addElement(att);
		}
		
		return attInfo;
	}
	
	public void carregarDados(BufferedReader buff, String divisor) throws Exception{

		while(buff.ready()){
			String linha = buff.readLine();
			String atributos[] = linha.split(divisor);
			Instance exemplo = new Instance(dados.numAttributes());
			exemplo.setDataset(dados);
			for (int i = 0; i < atributos.length; i++) {
				String valor = atributos[i].trim();

				Attribute att = dados.attribute(i);
				try{
					if(att.isNumeric()){
						exemplo.setValue(i,new Double(valor));
					} 		
					else{
						if(att.isString())
							exemplo.setValue(i,valor);
						else{		
							if(valor.equals("?"))
								exemplo.setMissing(i);
							else
								exemplo.setValue(i,valor);
						}

					}
				}	 catch(IllegalArgumentException ex){
					ex.printStackTrace();
				}

			}
			dados.add(exemplo);
		}	
	}
	
	public static void main(String[] args) {
		try{
			UCItoARFF gerador = new UCItoARFF();
			String nomeBase = "hepatitis";
			String caminhoBase = "C:\\Andre\\bases\\teste\\hepatitis\\";
			gerador.gerarArquivoARFF(nomeBase, caminhoBase);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
