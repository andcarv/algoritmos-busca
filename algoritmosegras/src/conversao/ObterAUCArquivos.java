package conversao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;

public class ObterAUCArquivos {
	
	public static int numExec = 50;
	
	public String obterAUC(String nomeArquivo) throws Exception{
		Reader reader = new FileReader(nomeArquivo);
		BufferedReader buff = new BufferedReader(reader);
		for(int i = 0; i<12; i++)
			buff.readLine();
		String linha = buff.readLine();
		String[] dados = linha.split("\t");
		String auc = dados[1].replace(",", ".");
		
		return auc;
	}
	
	public String [] lerArquivos(String nomeBase, String caminhoBase) throws Exception{
		String[] aucs = new String[numExec];
		double soma = 0;
		for(int i = 0 ; i<numExec; i++){
			String nomeArquivo = caminhoBase + nomeBase + "/"+ nomeBase + i + 
				"/resultado_" + nomeBase + i + ".txt";
			String auc = obterAUC(nomeArquivo);
			aucs[i] = auc;
			System.out.println(auc.replace('.',','));
			soma += new Double(auc);
	
		}
		
		System.out.println(soma/numExec);
		
		return aucs;
	}
	
	public String criarComando(String[] dados){
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
	
	public void gravarComando(String nomeBase, String caminhoBase, String comando) throws Exception{
		String nomeArquivo = caminhoBase + nomeBase + "/comando_" + nomeBase + ".txt";
		PrintStream ps = new PrintStream(nomeArquivo);
		ps.println(comando);
	} 
	
	public static void main(String[] args) {
		String nomeBase = "kc1_class_severity_discreto";
		String caminhoBase = "C:\\Andre\\SBIA\\resultados\\";
		ObterAUCArquivos auc = new ObterAUCArquivos();
		try{
			String[] dados = auc.lerArquivos(nomeBase, caminhoBase);
			System.out.println();
			String comando = auc.criarComando(dados);
			auc.gravarComando(nomeBase, caminhoBase, comando);
			
		} catch(Exception ex){ex.printStackTrace();}
	}

}
