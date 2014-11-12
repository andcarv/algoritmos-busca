package classificadores;

import java.io.FileReader;
import java.io.Reader;
import java.util.Random;

//import nuvemparticulas.NuvemWeka;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.Bagging;
import weka.classifiers.rules.NNge;
import weka.classifiers.trees.J48;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;

public class BaggingTest {
	
	public void executar(Instances dados) throws Exception{
		Bagging bagg = new Bagging();
		

		
	}
	
	public static void main(String[] args) {
		
		int g = 10;
		int p = 50;
		String[] objs = {"sens","spec"};
		boolean comb = true;
		String v = "ordenacao";
		
//		NuvemWeka n = new NuvemWeka(g,p,objs,comb,v);
		
		J48 j48 = new J48();
		j48.setUnpruned(true);
		j48.setDebug(true);
		
		NNge nnge = new NNge();
		
		String nomeBaseTr = "haberman_numerico_data.arff";
		String nomeBaseTe = "haberman_numerico_test.arff";
		String caminhoBase = "C:/Andre/bases/UCI/haberman_numerico/it0/";
		String arquivoARFF = caminhoBase+nomeBaseTr;
		
		BaggingTest test = new BaggingTest();
		try{	
			Reader reader = new FileReader(arquivoARFF);
			Instances dados = new Instances(reader);
			dados.setClassIndex(dados.numAttributes()-1);
			Bagging bagg = new Bagging();
		
//			bagg.setClassifier(n);
			bagg.setClassifier(j48);
			bagg.buildClassifier(dados);
			
			arquivoARFF = caminhoBase+nomeBaseTe;
			
			reader = new FileReader(arquivoARFF);
			Instances dadosTeste = new Instances(reader);
			dadosTeste.setClassIndex(dadosTeste.numAttributes()-1);
			Evaluation eval = new Evaluation(dadosTeste);
			eval.evaluateModel(bagg,dadosTeste);
			
			double auc = eval.areaUnderROC(0);
			
			System.out.println("AUC: " + auc);

		} catch (Exception ex){ex.printStackTrace();}
		
		
			
	}

}
