package conversao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class ObterROC {
	
	
	
	public void gerarValoresRoc(String alg, String base, String caminho, String nomeArquivo) throws IOException{
		
		FileReader reader = new FileReader(caminho + nomeArquivo);
		BufferedReader buffer = new BufferedReader(reader);
		
		PrintStream ps = new PrintStream(caminho + "roc_" + base + "_" + alg + ".txt");
		
		int i = 1;
		
		while(buffer.ready()){
			
			String linha1 = buffer.readLine();
			String linha2 = buffer.readLine();
			
			
			String[] valores1 = linha1.split("\t");
			String[] valores2 = linha2.split("\t");
			
	
			
			double tp = new Double(valores1[0].replace(',', '.'));
			double fn = new Double(valores1[1].replace(',', '.'));
			double fp = new Double(valores2[0].replace(',', '.'));
			double tn = new Double(valores2[1].replace(',', '.'));
			
			double p = tp + fn;
			double n = fp + tn;
		
			double tpr = tp/p;
			double fpr = fp/n;
			
			buffer.readLine();
			buffer.readLine();
			
			ps.println(tpr + "\t" + (fpr));
			ps.println();
			
			if(alg.equals("pso") || alg.equals("psoMedoid")){
				if(i%10==0){
				ps.println();
				ps.println();
				}
			}
			
			/*if(alg.equals("psoaco") || alg.equals("gassist")){
				for(int j=0;j<9; j++){
					ps.println(tpr + "\t" + (fpr));
					ps.println();
				}
			}*/
			
			i++;
		}
		
	}
	
	public static void main(String[] args) {
		String alg = "psoMedoid";
		String base = "pima_numerico";
		
		
		
		String caminho = "C:\\Andre\\enia2009\\resultados\\dados_confusao\\"+alg+"_confusao\\";
		String arquivo = alg + "_"+ base + "_confusao_60.txt";
		
		ObterROC o = new ObterROC();
		try{
			o.gerarValoresRoc(alg, base, caminho,arquivo);
		} catch(IOException ex){ex.printStackTrace();}
	}
	

}
