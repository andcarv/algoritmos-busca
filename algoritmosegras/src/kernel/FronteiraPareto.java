package kernel;

import java.util.ArrayList;
import java.util.Iterator;

import nuvemparticulas.Particula;

import regra.Regra;

public class FronteiraPareto {
	
	private ArrayList<Regra> fronteira = null;
	
	public FronteiraPareto(){
		fronteira = new ArrayList<Regra>();
	}
	
	public void setFronteira(ArrayList<Regra> temp){
		fronteira.clear();
		for (Iterator<Regra> iter = temp.iterator(); iter.hasNext();) {
			Regra r = iter.next();
			fronteira.add(r);
			
		}
	}
	
	public void apagarFronteira(){
		fronteira.clear();
	}
	
	/**
	 * Método que adiciona um nova regra na fronteira de pareto
	 * @param regra Regra a ser adicionada
	 * @param classe Valor da classe das regras da fronteira
	 * @return Valor booleano que especifica se o elemento foi inserido ou nao na fronteira 
	 */
	@SuppressWarnings("unchecked")
	public boolean add(Regra regra, String classe){
		//Só adiciona na fronteira caso a regra seja da classe passada como parametro
		if(regra.classe.indexOfValue(classe) == regra.cabeca){
		if(fronteira.size()==0){
			fronteira.add(regra);
			return true;
		}
			
		int comp;
		
		ArrayList<Regra> cloneFronteira = (ArrayList<Regra>)fronteira.clone();
		
		for (Iterator<Regra> iter = cloneFronteira.iterator(); iter.hasNext();) {
			Regra temp = iter.next();
			comp = compararMedidas(regra.getValoresObjetivos(), temp.getValoresObjetivos());
			if(comp == -1)
				return false;
			if(comp == 1){
				fronteira.remove(temp);
			}
		}
		
			fronteira.add(regra);
		return true;
		} else return false;
	}
	
	/**
	 * Método que adiciona um nova regra na fronteira de pareto. 
	 * Controla tambem o repositorio do algoritmo da nuvem de particula
	 * @param particula Particula a ser adicionada no repositorio. A regra a ser adicionada na fronteira perntece a particula
	 * @param repositorio Repositorio com as particulas nao dominadas
	 * @return True se a particula foi adicionada no repositorio, false caso contrario
	 */
	@SuppressWarnings("unchecked")
	public boolean add_NuvemParticulas(Particula particula, ArrayList<Particula> repositorio){
		
		Regra regra = (Regra)particula.regra.clone();
		if(fronteira.size()==0){
			fronteira.add(regra);
			repositorio.add(particula);
			return true;
		}
			
		int comp;
		
		ArrayList<Regra> cloneFronteira = (ArrayList<Regra>)fronteira.clone();
		
		int pos = 0;
		for (Iterator<Regra> iter = cloneFronteira.iterator(); iter.hasNext();) {
			Regra temp = iter.next();
			comp = compararMedidas(regra.getValoresObjetivos(), temp.getValoresObjetivos());
			if(comp == -1){
				return false;
			}
			if(comp == 1){
				fronteira.remove(temp);
				repositorio.remove(pos--);
			}
			++pos;
		}
		
			fronteira.add(regra);
			repositorio.add((Particula)particula.clone());
			//repositorio.add(particula);
		return true;
	}
	
	public ArrayList<Regra> getRegras(){
		return fronteira;
	}
	
	public String toString(){
		return fronteira.toString();
	}
	/**
	 * Método que verifica se uma regra domina a outra
	 * @param regra1 Regra que será comparada com as regras pertencentes a fronteira de pareto
	 * @param regra2 Regra pertencente a fronteira de pareto
	 * @return -1 Se a regra1 for dominada, 0 se a regra1 nao domina nem eh dominada, 1 a regra1 domina a regra2 
	 */
	public static int compararMedidas(double[] regra1, double[] regra2){
		//Contador que marca quantos valores da regra 1 sao maiores que os da regra2
		//Se cont for igual ao tamanho dos elementos da regra 1 entao a regra 2 eh dominada pela regra1
		//Se cont for igual a 0 a regra2 domina a regra1
		//Se cont for maior do que 0 e menor que o tamanho ela nao domina e nem eh dominada
		int cont = 0; 
		int cont2 = regra1.length;
		for (int i = 0; i < regra1.length; i++) {
			if(regra1[i]>regra2[i]){
				++cont;
			} else {
				if(regra1[i]==regra2[i]){
					--cont2;
				}
			}
		}
		if(cont == 0){	
			if(cont2 == 0)
				return 0;
			else
				return -1;
		}
		else{
			if(cont>0 && cont<cont2)
				return 0;
			else return 1;
		}
	}

}
