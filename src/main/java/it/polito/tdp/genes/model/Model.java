package it.polito.tdp.genes.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.genes.db.GenesDao;

public class Model {
	private Graph<String, DefaultWeightedEdge> grafo;
	private GenesDao dao;
	private List<String> best;
	private int pesoMax;
	
	public Model() {
		dao = new GenesDao();
	}
	
	public void creaGrafo() {
		grafo = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, dao.getVertici());
		
		for(Adiacenza adiacenza : dao.getArchi()) {
			Graphs.addEdgeWithVertices(grafo, adiacenza.getVertice1(), adiacenza.getVertice2(), adiacenza.getPeso());
		}
	}
	
	public int getNVertici() {
		return grafo.vertexSet().size();
	}
	
	public int getNArchi() {
		return grafo.edgeSet().size();
	}
	
	public Set<String> getVertici() {
		return grafo.vertexSet();
	}
	
	public String getStatistiche(String partenza) {
		String stampa = "";
		
		stampa += "Adiacenti a: " + partenza;
		for(String next : Graphs.neighborListOf(grafo, partenza)) {
			stampa += "\n" + next + "\t " + grafo.getEdgeWeight(grafo.getEdge(partenza, next));
		}
		
		return stampa;
	}
	
	public String trovaPercorso(String partenza) {
		String stampa = "";
		pesoMax = 0;
		List<String> parziale = new ArrayList<>();
		best = new ArrayList<>();
		parziale.add(partenza);
		cerca(parziale);
		
		for(String nextString : best) {
			stampa += nextString + "\n";
		}
		
		stampa += "\nPeso massimo: " + pesoMax;
		return stampa;
	}

	private void cerca(List<String> parziale) {
		if(pesoMax < calcolaPeso(parziale)) {
			best = new ArrayList<>(parziale);
			pesoMax = calcolaPeso(parziale);
		}
		
		for(String next : Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			if(!parziale.contains(next)) {
				parziale.add(next);
				cerca(parziale);
				parziale.remove(next);
			}
		}
	}

	private int calcolaPeso(List<String> parziale) {
		int peso = 0;
		for(int i = 1; i < parziale.size(); i++) {
			peso += grafo.getEdgeWeight(grafo.getEdge(parziale.get(i-1), parziale.get(i)));
		}
		return peso;
	}
}