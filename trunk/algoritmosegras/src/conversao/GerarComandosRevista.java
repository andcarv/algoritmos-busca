package conversao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;

public class GerarComandosRevista {
	
	public BufferedReader bayes = null;
	public BufferedReader naive = null;
	public BufferedReader rn = null;
	public BufferedReader smo = null;
	public BufferedReader c45 = null;
	public BufferedReader c45np = null;
	public BufferedReader pso = null;
	public BufferedReader nnge = null;
	public BufferedReader ripper = null;
	//public BufferedReader simples = null;
	
	public void carregarArquivosComandos(String caminho, String base) throws Exception{
		String arquivo = caminho + "bayes_" + base + "_comandos.txt";
		bayes = new BufferedReader(new FileReader(arquivo));
		arquivo = caminho + "naive_" + base + "_comandos.txt";
		naive = new BufferedReader(new FileReader(arquivo));
		arquivo = caminho + "rn_" + base + "_comandos.txt";
		rn = new BufferedReader(new FileReader(arquivo));
		arquivo = caminho + "smo_" + base + "_comandos.txt";
		smo = new BufferedReader(new FileReader(arquivo));
		arquivo = caminho + "c45_" + base + "_comandos.txt";
		c45 = new BufferedReader(new FileReader(arquivo));
		arquivo = caminho + "c45np_" + base + "_comandos.txt";
		c45np = new BufferedReader(new FileReader(arquivo));
		arquivo = caminho + "pso_" + base + "_comandos.txt";
		pso = new BufferedReader(new FileReader(arquivo));
		arquivo = caminho + "nnge_" + base + "_comandos.txt";
		nnge = new BufferedReader(new FileReader(arquivo));
		arquivo = caminho + "ripper_" + base + "_comandos.txt";
		ripper = new BufferedReader(new FileReader(arquivo));
		//arquivo = caminho + "simples_" + base + "_comandos.txt";
		//simples = new BufferedReader(new FileReader(arquivo));
	}
	
	public void gerarScriptsR(String caminho, String base) throws Exception{
		carregarArquivosComandos(caminho,base);
		int i = 0;
		String linha = null;
		PrintStream ps = null;
		String rad = null;
		while(bayes.ready() && i<8){
			switch(i){
			case 0 :{ps = new PrintStream(caminho + "script_auc.txt"); rad = "auc_"; break;}
			case 1 :{ps = new PrintStream(caminho + "script_acc.txt"); rad = "acc_";break;}
			case 2 :{ps = new PrintStream(caminho + "script_prec.txt"); rad = "prec_";break;}
			case 3 :{ps = new PrintStream(caminho + "script_rec.txt"); rad = "rec_";break;}
			case 4 :{ps = new PrintStream(caminho + "script_fm.txt"); rad = "fm_";break;}
			case 5 :{ps = new PrintStream(caminho + "script_prec_neg.txt"); rad = "prec_neg_";break;}
			case 6 :{ps = new PrintStream(caminho + "script_rec_neg.txt"); rad = "rec_neg_";break;}
			case 7 :{ps = new PrintStream(caminho + "script_fm_neg.txt"); rad = "fm_neg_";break;}
			}
			
			linha = bayes.readLine();
			ps.println(linha);
			ps.flush();
			linha = naive.readLine();
			ps.println(linha);
			ps.flush();
			linha = rn.readLine();
			ps.println(linha);
			ps.flush();
			linha = smo.readLine();
			ps.println(linha);
			ps.flush();
			linha = c45.readLine();
			ps.println(linha);
			ps.flush();
			linha = c45np.readLine();
			ps.println(linha);
			ps.flush();
			linha = pso.readLine();
			ps.println(linha);
			ps.flush();
			linha = nnge.readLine();
			ps.println(linha);
			ps.flush();
			linha = ripper.readLine();
			ps.println(linha);
			ps.flush();
			
			//linha = simples.readLine();
			//ps.println(linha);
			//ps.flush();
			
			ps.println();
			
			
			
			//ps.println("wilcox.test("+ rad+"psokc1,"+ rad +"simpleskc1)");
			
			ps.println("wilcox.test("+ rad+"psokc1,"+ rad +"bayeskc1)");
			ps.println("wilcox.test("+ rad+"psokc1,"+ rad +"rnkc1)");
			ps.println("wilcox.test("+ rad+"psokc1,"+ rad +"naivekc1)");
			ps.println("wilcox.test("+ rad+"psokc1,"+ rad +"smokc1)");
			ps.println("wilcox.test("+ rad+"psokc1,"+ rad +"c45kc1)");
			ps.println("wilcox.test("+ rad+"psokc1,"+ rad +"c45npkc1)");
			ps.println("wilcox.test("+ rad+"psokc1,"+ rad +"ripperkc1)");
			ps.println("wilcox.test("+ rad+"psokc1,"+ rad +"nngekc1)");
			ps.flush();
			ps.println();
			
			/*ps.println("wilcox.test("+ rad+"simpleskc1,"+ rad +"bayeskc1)");
			ps.println("wilcox.test("+ rad+"simpleskc1,"+ rad +"rnkc1)");
			ps.println("wilcox.test("+ rad+"simpleskc1,"+ rad +"naivekc1)");
			ps.println("wilcox.test("+ rad+"simpleskc1,"+ rad +"smokc1)");
			ps.println("wilcox.test("+ rad+"simpleskc1,"+ rad +"c45kc1)");
			ps.println("wilcox.test("+ rad+"simpleskc1,"+ rad +"c45npkc1)");
			ps.println("wilcox.test("+ rad+"simpleskc1,"+ rad +"ripperkc1)");
			ps.println("wilcox.test("+ rad+"simpleskc1,"+ rad +"nngekc1)");
			ps.flush();
			ps.println();
			
			ps.println("boxplot("+ rad+"psokc1,"+ rad +"simpleskc1)");*/
			
			ps.println("boxplot("+ rad+"psokc1,"+ rad +"bayeskc1)");
			ps.println("boxplot("+ rad+"psokc1,"+ rad +"rnkc1)");
			ps.println("boxplot("+ rad+"psokc1,"+ rad +"naivekc1)");
			ps.println("boxplot("+ rad+"psokc1,"+ rad +"smokc1)");
			ps.println("boxplot("+ rad+"psokc1,"+ rad +"c45kc1)");
			ps.println("boxplot("+ rad+"psokc1,"+ rad +"c45npkc1)");
			ps.println("boxplot("+ rad+"psokc1,"+ rad +"ripperkc1)");
			ps.println("boxplot("+ rad+"psokc1,"+ rad +"nngekc1)");
			
			/*ps.println("boxplot("+ rad+"simpleskc1,"+ rad +"bayeskc1)");
			ps.println("boxplot("+ rad+"simpleskc1,"+ rad +"rnkc1)");
			ps.println("boxplot("+ rad+"simpleskc1,"+ rad +"naivekc1)");
			ps.println("boxplot("+ rad+"simpleskc1,"+ rad +"smokc1)");
			ps.println("boxplot("+ rad+"simpleskc1,"+ rad +"c45kc1)");
			ps.println("boxplot("+ rad+"simpleskc1,"+ rad +"c45npkc1)");
			ps.println("boxplot("+ rad+"simpleskc1,"+ rad +"ripperkc1)");
			ps.println("boxplot("+ rad+"simpleskc1,"+ rad +"nngekc1)");*/
			
			ps.flush();
			ps.close();
			
			i++;
			bayes.readLine();
			naive.readLine();
			rn.readLine();
			pso.readLine();
			c45.readLine();
			c45np.readLine();
			smo.readLine();
			nnge.readLine();
			ripper.readLine();
			//simples.readLine();
		}
		
	}
	
	public static void main(String[] args) {
		String diretorio = "C:\\Andre\\revista\\comandos\\kc1_class_defeito_numerico\\";
		String base = "kc1_class_defeito_numerico";
		try{
			GerarComandosRevista g = new GerarComandosRevista();
			g.gerarScriptsR(diretorio, base);
		}catch(Exception ex) {ex.printStackTrace();}
	}

}
