package conversao;

import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;



import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class Reclassificar {
	
	public Instances reclassificar(Instances dadosEntrada, String classe1, String classe2, String classePositiva){
		
		Instances dadosSaida = new Instances(dadosEntrada,1);
		dadosSaida.deleteAttributeAt(dadosSaida.numAttributes()-1);
		FastVector nominal_values = new FastVector(2);
		nominal_values.addElement(classe1);
		nominal_values.addElement(classe2);
		Attribute sev = new Attribute("class", nominal_values);
		dadosSaida.insertAttributeAt(sev,dadosEntrada.numAttributes()-1);
		dadosSaida.setClassIndex(dadosSaida.numAttributes()-1);
		for (int i = 0; i < dadosEntrada.numInstances(); i++) {
			Instance exemploEntrada = dadosEntrada.instance(i);
			Instance exemploSaida = new Instance(exemploEntrada.numAttributes());
			exemploSaida.setDataset(dadosSaida);
			for (int j = 0; j < exemploEntrada.numAttributes(); j++) {
				if(j == exemploEntrada.numAttributes()-1){
					String valor = exemploEntrada.stringValue(j);
					if(valor.equals(classePositiva))
						exemploSaida.setValue(dadosSaida.attribute(j), classe1);
					else
						exemploSaida.setValue(dadosSaida.attribute(j), classe2);
				}
				else{
					if(dadosSaida.attribute(j).isNominal() || dadosSaida.attribute(j).isString() || dadosSaida.attribute(j).isDate())
						exemploSaida.setValue(dadosSaida.attribute(j), exemploEntrada.stringValue(j));
					else
						exemploSaida.setValue(dadosSaida.attribute(j), exemploEntrada.value(j));
				}
			}
			dadosSaida.add(exemploSaida);
		}
		
		return dadosSaida;
	}
	
	public static void main(String[] args) {
		Reclassificar rec = new Reclassificar();
		try{
			String nomebase = "hepatitis";
			String caminhoBase = "C:\\bases\\hepatitis\\";
			//String caminhoBase = "/home/andre/mestrado/weka-3-5-5/data/";
			//String arquivoARFF = caminhoBase + nomebase + "/it0/" + nomebase + "_data.arff";
			String arquivoARFF = caminhoBase + nomebase + ".arff";
			Reader reader = new FileReader(arquivoARFF);
			Instances dados = new Instances(reader);
			Instances saida = rec.reclassificar(dados, "positive", "negative", "cp");
			String arquivoSaida = caminhoBase + nomebase + "_novo.arff";
			PrintStream ps = new PrintStream(arquivoSaida);
			System.out.println(saida);
			ps.println(saida);
			
		} catch(Exception ex){ex.printStackTrace();}
	}

}
