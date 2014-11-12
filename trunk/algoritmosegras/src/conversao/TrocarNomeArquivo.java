package conversao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;

public class TrocarNomeArquivo {

	public static int numFolds = 10;
	
	public void lerArquivos(String nomeBase, String caminhoBase, String nomeSaida) throws Exception{
		
		for(int i = 0 ; i<numFolds; i++){
			String nomeArquivo = caminhoBase + "/"+  
				"/regras_" + nomeBase+"_" + i + ".txt";
			System.out.println(nomeArquivo);
			Reader reader = new FileReader(nomeArquivo);
			BufferedReader buff = new BufferedReader(reader);
			String arquivoSaida = caminhoBase + "\\regras_" + nomeSaida + "_" + i + ".txt";
			PrintStream ps = new PrintStream(arquivoSaida);
			System.out.println(arquivoSaida);
			while(buff.ready()){
				ps.println(buff.readLine());
			}
			
		} 
	}
	
	public static void main(String[] args) {
		String nomeBase = "satimage1";
		String nomeSaida = "satimage43" +
				"";
		String caminhoBase = "C:\\Andre\\AlgoritmosRegras\\dados\\satimage\\" + nomeSaida;
		TrocarNomeArquivo t = new TrocarNomeArquivo();
		try{
			t.lerArquivos(nomeBase, caminhoBase,nomeSaida);
		} catch(Exception ex){ex.printStackTrace();}
	}
	
}
