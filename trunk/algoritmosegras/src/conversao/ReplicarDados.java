package conversao;


import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;


import weka.core.Instance;
import weka.core.Instances;

public class ReplicarDados {
	
	public void replicarDados(String caminhoFold, String nomeBase, String classeReplicada, int vezesReplicar) throws IOException{
		System.out.println("Carregando dados");
		String arquivo = caminhoFold + nomeBase+ "_data.arff";
		Reader reader = new FileReader(arquivo);
		Instances dados = new Instances(reader);
		dados.setClassIndex(dados.numAttributes()-1);
		Instances dadosSaida = new Instances(dados,0);
		dadosSaida.setClassIndex(dados.numAttributes()-1);
		System.out.println("Replicando os dados");
		for(int i = 0; i<dados.numInstances(); i++){
			Instance exemplo = dados.instance(i);
			dadosSaida.add(exemplo);
			int classe = (int) exemplo.classValue();
			if((exemplo.classAttribute().value(classe)).equals(classeReplicada)){
				for(int j = 0; j<vezesReplicar-1; j++)
					dadosSaida.add(exemplo);
			}
		}
		
		String arquivoSaida = caminhoFold +  "\\" + nomeBase+ "_data.arff";
		System.out.println("Gerando arquivo de saida: " + arquivoSaida);
		PrintStream ps = new PrintStream(arquivoSaida);
		ps.println(dadosSaida);
		
	}
	
	
	

	
	public static void main(String[] args) {
		ReplicarDados replicador = new ReplicarDados();
		String nomeBase = "kc1_class_severity_discreto";
		String caminhoBase  = "C:\\Andre\\Bases\\SBIA\\";
		String classeReplicada = "high";
		int vezes = 3;
		int numFolds = 10;
		try{
			for(int i = 0; i< numFolds; i++){
				String caminhoFold = caminhoBase + nomeBase + "\\it" + i + "\\";
				replicador.replicarDados(caminhoFold, nomeBase, classeReplicada, vezes);
			}
		} catch(Exception ex) {ex.printStackTrace();}
	}

}
