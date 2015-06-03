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

public class SQLtoARFF {
	
	public Instances dados;
	
	
	public Instances gerarInstancias(String arquivoEntrada) throws IOException{
		 
		String dataSetName = "kc1_class_severity";
		
		Reader reader = new FileReader(arquivoEntrada);
		BufferedReader data = new BufferedReader(reader);
		
		FastVector attInfo = obterAtributos(data);
		dados = new Instances(dataSetName, attInfo,1);

		obterDados(data);
		dados.setClassIndex(dados.numAttributes()-1);
		return dados;
		
	}
	
	public void gerarArquivoARFF(String arquivoARFF) throws IOException{
		
		OutputStream os = new FileOutputStream(arquivoARFF);
		PrintStream ps = new PrintStream(os);
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
			atributos = linha.split("  ");
			int j = 0;
			 
			for (int i = 0; i < atributos.length; i++) {
				
				if(!atributos[i].equals("")){
					
					String valor = atributos[i].trim();
					if(j == dados.numAttributes()-1){
						if(valor.equals("1"))
							temp.setValue(dados.attribute(j++), "high");
						else
							temp.setValue(dados.attribute(j++), "low");
					} else{
						Double val = new Double(valor);
						temp.setValue(dados.attribute(j++), val.doubleValue());
					}
				}
			}
			dados.add(temp);

		}
	}
	
	public FastVector obterAtributos(BufferedReader data) throws IOException{
		
		String nome;
		
		Attribute att;
		FastVector attInfo = new FastVector();
		String linha = data.readLine();
		
		String nomes[] = linha.split(" ");
		
		for (int i = 0; i < nomes.length; i++) {
			nome = nomes[i];
			if(!nome.equals("")){
				if(nome.equals("SEVERITY")){
					
					String[] valores = {"high","low"}; 
					FastVector nominal_values = new FastVector(valores.length);
					for (int j = 0; j < valores.length; j++) {
						nominal_values.addElement(valores[j]);
					}
					att = new Attribute(nome,nominal_values);
				} else{
					att = new Attribute(nome);
					
				}
				attInfo.addElement(att);
			}
		}
		data.readLine();
		return attInfo;
	}
	
	public static void main(String[] args) {
		SQLtoARFF teste = new SQLtoARFF();
		try{ 
			String arquivoEntrada = "C:\\Andre\\Mestrado\\Bases\\NASA\\kc1_class_severity\\result.txt";
			teste.gerarInstancias(arquivoEntrada);
			
			System.out.println(teste.dados);
			String arquivoSaida = "C:\\Andre\\Mestrado\\Bases\\NASA\\kc1_class_severity\\kc1_class_severity_numerico.arff";
			teste.gerarArquivoARFF(arquivoSaida);
		} catch(Exception ex){ex.printStackTrace();}
	}

}
