package conversao;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class C45toARFF {
	
	public Instances dados;
	
	
	public Instances gerarInstancias(String arquivoC45_names, String arquivoC45_data_test ) throws IOException{
		 
		String dataSetNameTemp = arquivoC45_names.substring(arquivoC45_names.lastIndexOf("/")+1);
		String dataSetName = dataSetNameTemp.substring(0, dataSetNameTemp.indexOf("_"));
		
		Reader reader = new FileReader(arquivoC45_names);
		BufferedReader names = new BufferedReader(reader);
		
		FastVector attInfo = obterAtributos(names);
		dados = new Instances(dataSetName, attInfo,1);
		
		reader = new FileReader(arquivoC45_data_test);
		BufferedReader data = new BufferedReader(reader);
		obterDados(data);
		return dados;
		
	}
	
	public void gerarArquivoARFF(String arquivoARFF) throws IOException{
		
		OutputStream os = new FileOutputStream(arquivoARFF);
		PrintStream ps = new PrintStream(os);
		System.out.println(dados);
		ps.println(dados);
		
	}
	
	public void obterDados(BufferedReader data) throws IOException{
		String linha;
		String atributos[];
		Instance temp;
		while(data.ready()){
			temp = new Instance(dados.numAttributes());
			temp.setDataset(dados);
			linha = data.readLine();
			atributos = linha.split(",");
			for (int i = 0; i < atributos.length; i++) {
				temp.setValue(dados.attribute(i), atributos[i].trim());
			}
			dados.add(temp);

		}
	}
	
	public FastVector obterAtributos(BufferedReader names) throws IOException{
		
		String temp;
		boolean lerAtributos = false;
		//Ler o arquivo ate achar os atributos
		while(names.ready() && !lerAtributos){
			temp = names.readLine();
			if(temp.equals("| Features"))
				lerAtributos = true;
		}
		
		int indice_nome;
		int indice_tipo;
		int indice_valores;
		String nome;
		String tipo;
		String valoresString;
		String[] valores;
		Attribute att;
		FastVector nominal_values;
		FastVector attInfo = new FastVector();
		//Verifica se os atributos foram encontrados no arquivo. Caso nao e retornado vazio.
		if(lerAtributos){
			while(names.ready()){
				temp = names.readLine();
				indice_nome = temp.indexOf(":");
				nome = temp.substring(0,indice_nome);
				indice_tipo = temp.indexOf("(");
				tipo = (temp.substring(indice_nome+1,indice_tipo)).trim();
				indice_valores = temp.indexOf(")");
				valoresString = temp.substring(indice_tipo+1,indice_valores);
				valores = valoresString.split(",");
				if(tipo.equals("nominal") || tipo.equals("enumerated")){
					nominal_values = new FastVector(valores.length);
					for (int i = 0; i < valores.length; i++) {
						nominal_values.addElement(valores[i]);
					}
					
					att = new Attribute(nome,nominal_values);
				} else {
					att = new Attribute(nome);
				}
				attInfo.addElement(att);
				
			}
			return attInfo;
		} else return null; 
		
	}
	
	public static void main(String[] args) {
		C45toARFF teste = new C45toARFF();
		try{
			String nomebase = "bupa"; 
			for(int i = 0; i<10; i++){
				teste.gerarInstancias("C://Andre//Bases//celso//celso//"+ nomebase + "/it"+i+"/" + nomebase + "_C45.names", 
						"C://Andre//Bases//celso//celso//"+ nomebase + "/it"+i+"/" + nomebase + "_C45.test");
				teste.gerarArquivoARFF("C://Andre//Bases//celso//celso//"+ nomebase + "/it"+i+"/" + nomebase + "_test.arff");
				
				teste.gerarInstancias("C://Andre//Bases//celso//celso//"+ nomebase + "/it"+i+"/" + nomebase + "_C45.names", 
						"C://Andre//Bases//celso//celso//"+ nomebase + "/it"+i+"/" + nomebase + "_C45.data");
				teste.gerarArquivoARFF("C://Andre//Bases//celso//celso//"+ nomebase + "/it"+i+"/" + nomebase + "_data.arff");

			}
		} catch(Exception ex){ex.printStackTrace();}
	}

}
