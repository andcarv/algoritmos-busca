package apriori;


import java.util.ArrayList;
import java.util.Iterator;

import regra.Regra;

import kernel.FronteiraPareto;
import kernel.ObterRegras;
import weka.associations.Apriori;
import weka.associations.ItemSet;
import weka.core.FastVector;

public class ObterRegrasApriori extends ObterRegras{
	Apriori apriori = null;
	
	public boolean fronteiraApriori;
	
	public ObterRegrasApriori(String[] objs){
		super(objs);
		apriori = new Apriori();	
	}
	
	public ObterRegrasApriori(int numRegras, double confianca, double minSuporte, 
			double maxSuporte, double delta, boolean frontApri, String[] objs){
		super(objs);
		apriori = new Apriori();
		apriori.setNumRules(numRegras);
		apriori.setMinMetric(confianca);
		apriori.setLowerBoundMinSupport(minSuporte);
		apriori.setUpperBoundMinSupport(maxSuporte);
		apriori.setDelta(delta);
		
		fronteiraApriori = frontApri;
		
	}
	
	/*public ObterRegrasApriori(int numRegras){
		apriori = new Apriori();
		apriori.setNumRules(numRegras);	
		addObjetivos();
	}*/
	
	public Apriori executar() throws Exception{
			apriori.setCar(true);
			apriori.buildAssociations(dados);	
			return apriori;		
	}
	
	public ArrayList<Regra> obterRegras(int numClasses) throws Exception{
		executar();
		System.out.println("Apriori terminado!");
		FastVector v[] = apriori.getAllTheRules();
		FastVector corpos = v[0]; //v[0] --> Corpos
		FastVector cabecas = v[1]; //v[1] --> Cabecas
		FastVector confiancas = v[2];//v[2] --> Confiancas
		ItemSet corpo, cabeca;
		Double confianca;
		
		regras = new ArrayList<Regra>();
		ArrayList<Regra> regrasTemp = new ArrayList<Regra>();
		
		for (int i = 0; i < corpos.size(); i++) {
			corpo = (ItemSet)corpos.elementAt(i);
			cabeca = (ItemSet) cabecas.elementAt(i);
			confianca = (Double)confiancas.elementAt(0);	
			Regra regra = new Regra(corpo, cabeca, confianca, dados.enumerateAttributes(), dados.classAttribute());
			regra.objetivos = objetivos;
			regrasTemp.add(regra);

		}
		
		System.out.println("Numero total de regras: " + regrasTemp.size());
		preencherMatrizContigencia(regrasTemp, dados);
		
		if(fronteiraApriori){
			System.out.println("Selecao da Fronteira de Pareto");
		paretoPos = new FronteiraPareto();
		paretoNeg = new FronteiraPareto();
		int i = 0;
		for (Iterator<Regra> iter = regrasTemp.iterator(); iter.hasNext();) {
			if(verbose){
				if(i%75000 == 0)
					System.out.print(i + " - " + regrasTemp.size() + " ");
			}
			Regra regra = (Regra) iter.next();
			paretoPos.add(regra, classePositiva);
			paretoNeg.add(regra, classeNegativa);
			i++;
		}
		
		regrasTemp = null;
		
		regras.addAll(paretoPos.getRegras());
		regras.addAll(paretoNeg.getRegras());
		} else{
			System.out.println("Todas as regras");
			regras.addAll(regrasTemp);
		}		
		
		//regras = retirarRegrasRuins(regras, major[1]);


		return regras;
	}
	
	
	public Apriori executarNuvem(double suporte, double confianca) throws Exception{
		apriori.setMinMetric(confianca);
		apriori.setLowerBoundMinSupport(suporte);
		apriori.setUpperBoundMinSupport(1.0);
		apriori.setCar(true);
		apriori.buildAssociations(dados);	
		return apriori;		
}
	
	
	public ArrayList<Regra> obterRegrasNuvem(String arquivoARFF, int cPositiva, int cNegativa, double suporte, double conf) throws Exception{
		carregarInstancias(arquivoARFF, cPositiva, cNegativa);
		executarNuvem(suporte, conf);
		FastVector v[] = apriori.getAllTheRules();
		FastVector corpos = v[0]; //v[0] --> Corpos
		FastVector cabecas = v[1]; //v[1] --> Cabecas
		FastVector confiancas = v[2];//v[2] --> Confiancas
		ItemSet corpo, cabeca;
		Double confianca;
		
		regras = new ArrayList<Regra>();
		ArrayList<Regra> regrasTemp = new ArrayList<Regra>();
		
		for (int i = 0; i < corpos.size(); i++) {
			corpo = (ItemSet)corpos.elementAt(i);
			cabeca = (ItemSet) cabecas.elementAt(i);
			confianca = (Double)confiancas.elementAt(0);	
			Regra regra = new Regra(corpo, cabeca, confianca, dados.enumerateAttributes(), dados.classAttribute());
			regra.objetivos = objetivos;
			regrasTemp.add(regra);

		}
		
		preencherMatrizContigencia(regrasTemp, dados);
		
		paretoPos = new FronteiraPareto();
		paretoNeg = new FronteiraPareto();
		
		for (Iterator iter = regrasTemp.iterator(); iter.hasNext();) {
			Regra regra = (Regra) iter.next();
			paretoPos.add(regra, classePositiva);
			paretoNeg.add(regra, classeNegativa);
		}
		
		double[] major = obterClasseMajoritaria(dados);
		
		regras.addAll(paretoPos.getRegras());
		regras.addAll(paretoNeg.getRegras());
		
		regras = retirarRegrasRuins(regras, major[1]);


		return regras;
	}
			
	public static void main(String[] args) {
		
		/*String caminhoBase = null;
		String nomebase = null;
		
	
		if(args.length == 0){
			nomebase = "bupa";
			caminhoBase = "/home/andre/mestrado/bases/UCI/";
		} else {
			nomebase = args[0];
			caminhoBase = args[1];
		}
		
		ObterRegrasApriori teste = new ObterRegrasApriori(500000,0.1,0.1,0.8,0.05, true, null);
	
		try{	
			teste.executarFolds(nomebase, caminhoBase, "apriori",0,1,10,false,"confidence",false);
		
		} catch (Exception ex) {ex.printStackTrace();}
		*/
		
	}

}




