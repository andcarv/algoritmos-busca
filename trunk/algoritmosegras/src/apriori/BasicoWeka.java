package apriori;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class BasicoWeka {
	
	public Instances dados;
	
	public BasicoWeka(){
		
	}
	
	/**
	 * Método que carrega um arquivo texto no formato dos dados do Weka
	 * @param arquivoArff Arquivo texto no formato ARFF, padrão do Weka.
	 * @throws IOException Exceção lançada pela leitura do arquivo.
	 */
	public void carregarDados(String arquivoArff) throws IOException{
		/*Estrutura do Java que carrega um arquivo texto em memoria
		No Java existem muitas formas para se carregar um arquivo, 
		cada classe possibilita fazer alguma coisa diferente com ele.
		Como a gente nao precisa fazer nada, so repassar para classe do Weka
		eu utilizei esta estrutura. Este metodo lança uma exceção IOException,
		 casa haja algum problema com o arquivo que eu passei (ex. ele não existe...).
		Eu estou usando uma condição que repassa essa exceção para o método 
		que vai chamar este método carregarDados.
		*/
		Reader reader = new FileReader(arquivoArff);
		//Carregando o arquivo texto na estrutuda do Weka
		dados = new Instances(reader);
		//Aponta qual é o atributo classe da base, nas bases que nós estamos
		//trabalhando sempre é o último
		dados.setClassIndex(dados.numAttributes()-1);
	}
	
	/**
	 * Métod que percorre todos os dados pertencentes à Instances dados. 
	 * Imprimindo as informações da base.
	 *
	 */
	public void percorrerDados(){
	
		if(dados!=null){
		/*Cada exemplo contido nos dados é identificado no Weka através da 
		 * classe Instance. Assim, o objeto dados, do tipo Instances, é uma coleçao de 
		 * Instance. Voce vai ter metodos que possibilitam acessar todos os exemplos
		 * presentes na base.
		 * */
			//Percorre todos os exemples presentes na base
			for(int i=0; i<dados.numInstances(); i++){
				//Método para obter a instance de número 1.
				//Voce pode pegar a primeira e a ultima instance tb.
				//Além de poder deletar entre outras coisas.
				Instance exemplo = dados.instance(i);
				
				/*Uma Intance é formada por vários atributos, que são os atributos
				 * da base. Voce pode percorrer todos os atributos Instace, ou pode
				 * "setar" (set) ou pegar (get) um atributo especifico.
				 * */
				
				//É possível transforma todos os atributos em um array de double
				
				double[] arrayAtributos = exemplo.toDoubleArray();
				
				System.out.println("Valores para o exemplo " + i);
				System.out.print("Array de atributos: ");
				for (int j = 0; j < arrayAtributos.length; j++) {
					System.out.print(arrayAtributos[j] + " ");
				}
				
				System.out.println();
				//Percorrendo todos os atributos para se obter informacoes sobre eles
				for(int j = 0; j<exemplo.numAttributes(); j++){
					Attribute att = exemplo.attribute(j);
					double valor = exemplo.value(att);
					System.out.println("Valor do atributo " + att.name() + ":"
							+ valor + " - " + att.value((int) valor ));
					
				}
				
				System.out.println();
				//Mudando o valor do atributo 0, para um valor possível do atributos
				//Obtendo as informacoes do atributo 0;
				Attribute att = exemplo.attribute(0);
				//Obtendo o valor do atributo 0.
				double valorDoAtributo0 = exemplo.value(att);
			
				System.out.println("Valor antigo, em double: " + valorDoAtributo0);
				System.out.println("Valor antigo, em nome: " + att.value((int)valorDoAtributo0));
				
				int novoValor = 1;
				exemplo.setValue(att, novoValor);
				
				valorDoAtributo0 = exemplo.value(att);
				
				System.out.println("Valor novo, em nome: " + att.value((int)valorDoAtributo0));

				System.out.println();
				System.out.println();
				
			}
		}
		
	}

	public static void main(String[] args) {
		String nomebase = "hepatitis.arff";
		String caminhoBase = "C:\\bases\\hepatitis\\";
		String arquivoARFF = caminhoBase + nomebase;
		BasicoWeka basico = new BasicoWeka();
		//Aquela exceção que eu repassei, agora eu tenho que tratar ela.
		//Caso haja um problema, eu mando imprimir a StackTrace, que vai me mostrar
		//aonde ocorreu o problema.
		try{
			basico.carregarDados(arquivoARFF); 
			basico.percorrerDados();
		} catch(IOException ex) {ex.printStackTrace();}
		
	}

}
