package kernel;

import java.util.Comparator;

/**
 * Classe utilizada para armazenar todas as classes dos exemplos e ter 
 * uma relação entre o nome e as informaçoes de cada classe
 * @author Matheus Rosendo
 *	
 */
public class Classe implements Comparator<Classe> {

	private int pos;
	private int exemplosCobertos;
	private double porcentagem;
	private String nome;
	
	
	public int getExemplosCobertos() {
		return exemplosCobertos;
	}
	public void setExemplosCobertos(int exemplosCobertos) {
		this.exemplosCobertos = exemplosCobertos;
	}
	public void addExemplosCobertos(int add) {
		this.exemplosCobertos += add;
	}
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	public double getPorcentagem() {
		return porcentagem;
	}
	public void setPorcentagem(double porcentagem) {
		this.porcentagem = porcentagem;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public int compare(Classe c1, Classe c2) {
		return c1.exemplosCobertos > c2.exemplosCobertos ? c1.exemplosCobertos : c2.exemplosCobertos; 
	}
	
}
