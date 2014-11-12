package conversao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;

public class ObterAllAUCArquivos {
	
	public static int numExec = 10;
	public static int numFold = 10;
	
	public double[] aucs;
	int cont;
	
	public String[] obterAUC(String nomeArquivo) throws Exception{
		Reader reader = new FileReader(nomeArquivo);
		BufferedReader buff = new BufferedReader(reader);	
		buff.readLine();
		buff.readLine();
		String[] result = new String[10];
		for(int i = 0; i<10; i++){
			String linha = buff.readLine();
			String[] dados = linha.split("\t");
			String auc = dados[1];//.replace(",", ".");
			System.out.println(auc);
			aucs[cont++] = new Double(auc.replace(",", ".")).doubleValue();
			result[i] = auc;
		}
		
		return result;
	}
	
	public String criarComando(double[] dados){
		StringBuffer comando = new StringBuffer(); 
			comando.append("x <- c(");
		for (int i = 0; i < dados.length; i++) {
			String string = dados[i] + ",";
			comando.append(string);
		}
		comando.deleteCharAt(comando.length()-1);
		comando.append(")\n\n");
		comando.append("shapiro.test(x)");
		System.out.println(comando);
		return comando.toString();
	}
	
	public void lerArquivos(String nomeBase, String caminhoBase) throws Exception{
		aucs = new double[numExec*numFold];
		
		for(int i = 0 ; i<numExec; i++){
			String nomeArquivo = caminhoBase + nomeBase + "/"+ nomeBase + i + 
				"/resultado_" + nomeBase + i + ".txt";
			String[] auc = obterAUC(nomeArquivo);
			
		}
		double soma = 0;
		double media = 0;
		for(int j = 0; j<cont; j++){
			soma += aucs[j];
		}
		media = soma/cont;
		System.out.println(media*100);
		soma = 0;
		double temp = 0;
		for(int j = 0; j<cont; j++){
			temp = (aucs[j]-media)*(aucs[j]-media);
			soma +=temp;
		}
		double desv = Math.sqrt(soma/(double)cont);
		System.out.println(desv*100);
		
	}
	
	public void gravarComando(String nomeBase, String caminhoBase, String comando) throws Exception{
		String nomeArquivo = caminhoBase + nomeBase + "/comando_" + nomeBase + ".txt";
		PrintStream ps = new PrintStream(nomeArquivo);
		ps.println(comando);
	} 
	
	
	
	public static void main(String[] args) {
		String nomeBase = "kc1_class_defeito_numerico";
		String caminhoBase = "C:\\Andre\\ICTAI2008\\";
		ObterAllAUCArquivos auc = new ObterAllAUCArquivos();
		try{
			auc.lerArquivos(nomeBase, caminhoBase);
			String comando = auc.criarComando(auc.aucs);
			auc.gravarComando(nomeBase, caminhoBase, comando);
			
		} catch(Exception ex){ex.printStackTrace();}
	}

}
