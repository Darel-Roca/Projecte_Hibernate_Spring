package DAOcatan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import catan.Aresta;
import catan.ColorJugador;
import catan.EstatPartida;
import catan.Hexagon;
import catan.Jugador;
import catan.Partida;
import catan.Vertex;
import catan.TipusTerreny;

public class PartidaDao extends GenericDao<Partida, Integer> implements IPartidaDao {

	@Override
	public int iniciarPartida(List<String> nomsJugadors) {
	    if (nomsJugadors == null || nomsJugadors.size() < 2 || nomsJugadors.size() > 4) {
	        throw new IllegalArgumentException("La partida ha de tenir entre 2 i 4 jugadors.");
	    }

	    Session session = SessionManager.getSessionFactory().openSession();
	    Transaction tx = null;
	    Partida novaPartida = new Partida();
	    try {
	        tx = session.beginTransaction();

	        novaPartida.setEstat(EstatPartida.PREPARACIO);
	        novaPartida.setDataInici(LocalDate.now());
	        novaPartida.setRonda(1); 
	        novaPartida.setUltimaTirada(1); 
	        novaPartida.setPuntsPerGuanyar(10);   
	        novaPartida.setDausTirats(false);
	        novaPartida.setLladrePendent(false);

	        session.persist(novaPartida); 

	        
	        // CSV's
	        
	        
	        tx.commit();
	    } catch (Exception e) {
	        if (tx != null && tx.isActive()) {
	            tx.rollback();
	        }
	        e.printStackTrace();
	    } finally {
	        session.close();
	    }

	    return novaPartida.getId(); 
	}
	
	private void generarMapaAleatori(List<Hexagon> hexagons) {
		List<TipusTerreny> terrenys = new ArrayList<>(List.of(
			TipusTerreny.BOSC, TipusTerreny.TURONS, TipusTerreny.PASTURES, 
			TipusTerreny.CAMPS, TipusTerreny.MUNTANYES, TipusTerreny.DESERT
		));
		
		while (terrenys.size() < hexagons.size()) {
			terrenys.add(TipusTerreny.BOSC); 
		}

		List<Integer> numeros = new ArrayList<>(List.of(2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12));
		while (numeros.size() < hexagons.size()) {
			numeros.add(2); 
		}

		Collections.shuffle(terrenys);
		Collections.shuffle(numeros);

		int numIndex = 0;
		for (Hexagon h : hexagons) {
			TipusTerreny terreny = terrenys.remove(0);
			h.setTerreny(terreny);

			if (TipusTerreny.DESERT.equals(terreny)) {
				h.setNumero(0); 
			} else {
				h.setNumero(numeros.get(numIndex++));
			}
		}
	}

	private List<Hexagon> carregarHexagonsDesdeCSV(String ruta, Partida partida) throws Exception {
		List<Hexagon> llista = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
			String linia;
			while ((linia = br.readLine()) != null) {
				if (linia.trim().isEmpty()) continue;
				
				String[] camps = linia.split(","); 
				Hexagon h = new Hexagon();
				h.setPartida(partida); 
				h.setQ(Integer.parseInt(camps[0].trim()));
				h.setR(Integer.parseInt(camps[1].trim()));
				
				h.setTerreny(TipusTerreny.DESERT); 
				h.setNumero(0);
				
				llista.add(h);
			}
		}
		return llista;
	}

	private List<Vertex> carregarVertexsDesdeCSV(String ruta) throws Exception {
		List<Vertex> llista = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
			String linia;
			while ((linia = br.readLine()) != null) { }
		}
		return llista;
	}

	private List<Aresta> carregarArestesDesdeCSV(String ruta) throws Exception {
		List<Aresta> llista = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
			String linia;
			while ((linia = br.readLine()) != null) { }
		}
		return llista;
	}

	@Override
	public List<Hexagon> mostraTauler(int idPartida) { return null; }

	@Override
	public Partida mostraPartida(int idPartida) { return null; }

	@Override
	public void tirarDaus(int idPartida) { }

	@Override
	public void comprovarVictoria(int idPartida) { }
	
	private <E> E buscarPerNom(Session session, Class<E> classe, String nom) {
		return session.createQuery("FROM " + classe.getName() + " e WHERE e.nom = :nom", classe)
				.setParameter("nom", nom)
				.uniqueResult();
	}

	private <E> E obtenir(Map<String, E> mapa, String clau, String fitxer) {
		E e = mapa.get(clau);
		if (e == null) {
			throw new IllegalStateException(fitxer + ": no existeix '" + clau + "'");
		}
		return e;
	}
}
