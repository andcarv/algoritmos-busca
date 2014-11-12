package conversao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;

public class GerarComandosMedidas {
	
	public String[] auc;
	public String[] acc;
	public String[] prec;
	public String[] rec;
	public String[] fm;
	public String[] precneg;
	public String[] recneg;
	public String[] fmneg;
	
	public void gerarComandos(String caminho, String base, int numMedidas, String algoritmo, String cmd) throws Exception{
		String arquivoMedidas = caminho + base +"\\" + algoritmo + "_" + base + "_medidas.txt";
		Reader reader = new FileReader(arquivoMedidas);
		BufferedReader buff = new BufferedReader(reader);
		auc = new String[numMedidas];
		acc = new String[numMedidas];
		prec = new String[numMedidas];
		rec = new String[numMedidas];
		fm = new String[numMedidas];
		precneg = new String[numMedidas];
		recneg = new String[numMedidas];
		fmneg = new String[numMedidas];
		
		int i = 0;
		while(i<numMedidas){
			if(i==300)
				i++;
			String linha = buff.readLine();
			
			String[] medidas = linha.split("\t");
			
			auc[i] = medidas[0].replace(',','.');
			acc[i] = medidas[1].replace(',','.');
			prec[i] = medidas[3].replace(',','.');
			rec[i] = medidas[4].replace(',','.');
			fm[i] = medidas[5].replace(',','.');
			precneg[i] = medidas[7].replace(',','.');
			recneg[i] = medidas[8].replace(',','.');
			fmneg[i] = medidas[9].replace(',','.');
			i++;
		}
	
	String arquivoSaida = caminho + base + "\\" + algoritmo +"_" + base + ".txt";
	PrintStream ps = new PrintStream(arquivoSaida);
		
		
	StringBuffer comando = new StringBuffer(); 
	
	if(cmd.equals("auc")){
	
	comando.append("auc_" + algoritmo + " <- c(");
	for (int j = 0; j < auc.length; j++) {
		String string = auc[j] + ", ";
		comando.append(string);
	}
	
	comando.deleteCharAt(comando.length()-2);
	comando.append(")\n\n");
	
	ps.println(comando);
	
	}
	
	if(cmd.equals("acc")){
		comando = new StringBuffer(); 
		comando.append("acc_" + algoritmo + " <- c(");
		for (int j = 0; j < acc.length; j++) {
			String string = acc[j] + ", ";
			comando.append(string);
		}

		comando.deleteCharAt(comando.length()-2);
		comando.append(")\n\n");

		ps.println(comando);
	}
	

	if(cmd.equals("prec")){
		comando = new StringBuffer(); 
		comando.append("prec_" + algoritmo + " <- c(");

		for (int j = 0; j < prec.length; j++) {
			String string = prec[j] + ", ";
			comando.append(string);
		}

		comando.deleteCharAt(comando.length()-2);
		comando.append(")\n\n");

		ps.println(comando);
	}
	
	
	if(cmd.equals("rec")){
		comando = new StringBuffer(); 
		comando.append("rec_" + algoritmo + " <- c(");
		for (int j = 0; j < rec.length; j++) {
			String string = rec[j] + ", ";
			comando.append(string);
		}

		comando.deleteCharAt(comando.length()-2);
		comando.append(")\n\n");

		ps.println(comando);

	}
	
	if(cmd.equals("fm")){
		comando = new StringBuffer(); 
		comando.append("fm_" + algoritmo + " <- c(");
		for (int j = 0; j < fm.length; j++) {
			String string = fm[j] + ", ";
			comando.append(string);
		}

		comando.deleteCharAt(comando.length()-2);
		comando.append(")\n\n");

		ps.println(comando);

	}
	
	if(cmd.equals("prec_neg")){
		comando = new StringBuffer(); 
		comando.append("prec_neg_" + algoritmo + " <- c(");
		for (int j = 0; j < precneg.length; j++) {
			String string = precneg[j] + ", ";
			comando.append(string);
		}

		comando.deleteCharAt(comando.length()-2);
		comando.append(")\n\n");

		ps.println(comando);

	}
	
	if(cmd.equals("rec_neg")){
		comando = new StringBuffer(); 
		comando.append("rec_neg_" + algoritmo + " <- c(");
		for (int j = 0; j < recneg.length; j++) {
			String string = recneg[j] + ", ";
			comando.append(string);
		}

		comando.deleteCharAt(comando.length()-2);
		comando.append(")\n\n");

		ps.println(comando);

	}
	
	
	if(cmd.equals("fm_neg")){
		comando = new StringBuffer(); 
		comando.append("fm_neg_" + algoritmo + " <- c(");
		for (int j = 0; j < fmneg.length; j++) {
			String string = fmneg[j] + ", ";
			comando.append(string);
		}

		comando.deleteCharAt(comando.length()-2);
		comando.append(")\n\n");

		ps.println(comando);
	}

	
	}
	
	public void juntarComandosBases(String caminho, String[] bases, int numMedidas, String algoritmo, String cmd) throws Exception{
		
		StringBuffer cmdFinal = new StringBuffer();
		cmdFinal.append(cmd + "_" + algoritmo + " <- c(");
		
		for (int i = 0; i < bases.length; i++) {
			String base = bases[i];
			
			String arquivoEntrada = caminho + base + "\\" + algoritmo +"_" + base + ".txt";
			
			Reader reader = new FileReader(arquivoEntrada);
			BufferedReader buff = new BufferedReader(reader);
			
			String linha = buff.readLine();
			//System.out.println(linha);
			int comeco = linha.indexOf('(')+1;
			int fim = linha.length()-2;
			String valores = linha.substring(comeco, fim);
			//System.out.println(valores);
			cmdFinal.append(valores + ",\n");
		}
		
		cmdFinal.deleteCharAt(cmdFinal.length()-1);
		cmdFinal.deleteCharAt(cmdFinal.length()-1);
		cmdFinal.append(")");
		
		String arquivoSaida = caminho + cmd + "_" + algoritmo + ".txt";
		PrintStream saida = new PrintStream(arquivoSaida);
		saida.println(cmdFinal);
		
		
	}
	
	public static void main(String[] args) {
		GerarComandosMedidas c = new GerarComandosMedidas();
		String alg = "pso";
		String caminho = "C:\\Andre\\revista\\resultados\\"+alg + "\\laplace\\";
		String cmd = "rec_neg";
		
		String bases[] = {"cm1-cfs", "jm1-cfs", "kc1-cfs", "kc2-cfs", "pc1-cfs","kc1_class_defeito_numerico"};
		try{
			c.gerarComandos(caminho,"cm1-cfs",10,alg,cmd);
			c.gerarComandos(caminho,"jm1-cfs",10,alg,cmd);
			c.gerarComandos(caminho,"kc1-cfs",10,alg,cmd);
			c.gerarComandos(caminho,"kc2-cfs",10,alg,cmd);
			c.gerarComandos(caminho,"pc1-cfs",10,alg,cmd);
			c.gerarComandos(caminho,"kc1_class_defeito_numerico",10,alg,cmd);
			
			c.juntarComandosBases(caminho,bases,10,alg,cmd);
			
		}catch(Exception ex){ex.printStackTrace();}
		
	}

}
