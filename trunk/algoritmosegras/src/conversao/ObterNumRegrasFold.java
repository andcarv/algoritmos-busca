package conversao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;

public class ObterNumRegrasFold {
	
	public static int numExec = 100;
	
	public double obterNumRegras(String nomeArquivo) throws Exception{
		Reader reader = new FileReader(nomeArquivo);
		BufferedReader buff = new BufferedReader(reader);
		
		for(int i = 0; i<14; i++)
			buff.readLine();
		String linha = buff.readLine();
		String[] dados = linha.split("\t");
		String numRegras = dados[1].replace(",", ".");
		return new Double(numRegras).doubleValue();
	}
	
	public void lerArquivos(String nomeBase, String caminhoBase) throws Exception{
		
		double somaTotal = 0;
		for(int j = 0; j<10; j++){
			double soma = 0;
			for(int i = 0 ; i<numExec; i++){
				String nomeArquivo = caminhoBase + nomeBase + "/"+ nomeBase + j + 
				"/resultado_" + nomeBase + j + ".txt";	
				double numRegras = obterNumRegras(nomeArquivo);
				soma += numRegras;
			}
			double media = soma/numExec;
			System.out.println("Regras fold " + j + ": " + media);
			somaTotal +=media;
		}	
		System.out.println("Media total: " + somaTotal/10);
	}
	
	 
	
	public static void main(String[] args) {
		String nomeBase1 = "pima_numerico";
		String nomeBase2 = "pima_discreto";
		String caminhoBase = "C:\\Andre\\Mestrado\\gecco2008\\resultados\\";
		ObterNumRegrasFold o = new ObterNumRegrasFold();
		try{
			
			o.lerArquivos(nomeBase1, caminhoBase);
			System.out.println();
			o.lerArquivos(nomeBase2, caminhoBase);

			
		} catch(Exception ex){ex.printStackTrace();}
	}
	
	@SuppressWarnings("unused")
	private class ObjetivosRegras implements Comparable<ObjetivosRegras>{
		double sens;
		double spec;
		
		public ObjetivosRegras(double se, double sp){
			sens = se;
			spec = sp;
		}
		
		public int compareTo(ObjetivosRegras or){
			if(sens < or.sens)
				return -1;
			else
				if(sens>or.sens)
					return 1;
				else
					return 0;
		}
		
		public String toString(){
			return sens + "\t" + spec;
		}
		
		
		
	}


}



