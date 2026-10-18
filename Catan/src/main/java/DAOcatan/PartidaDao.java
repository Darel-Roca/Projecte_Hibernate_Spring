package DAOcatan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import catan.Aresta;
import catan.ColorJugador;
import catan.EstatPartida;
import catan.Hexagon;
import catan.HexagonVertex;
import catan.Jugador;
import catan.Partida;
import catan.RecursJugador;
import catan.TipusRecurs;
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

	        List<String> nomsBarrejats = new ArrayList<>(nomsJugadors);
	        Collections.shuffle(nomsBarrejats);

	        ColorJugador[] colors = ColorJugador.values();
	        List<Jugador> jugadorsPartida = new ArrayList<>();

	        for (int i = 0; i < nomsBarrejats.size(); i++) {
	            Jugador j = new Jugador();
	            j.setNom(nomsBarrejats.get(i));
	            j.setPartida(novaPartida);
	            j.setColor(colors[i]); 
	            j.setOrdreTorn(i + 1); 
	            j.setCavallersJugats(0); 

	            session.persist(j);
	            jugadorsPartida.add(j);

	            for (TipusRecurs tipus : TipusRecurs.values()) {
	                RecursJugador rj = new RecursJugador();
	                rj.setJugador(j);
	                rj.setTipus(tipus);
	                rj.setQuantitat(0);
	                session.persist(rj);
	            }
	        }
	        novaPartida.setJugadors(jugadorsPartida);

	        String rutaHexagons = "src/main/resources/dades/hexagons.csv";
	        String rutaVertexs = "src/main/resources/dades/vertexs.csv";
	        String rutaArestes = "src/main/resources/dades/arestes.csv";
	        String rutaHexagonVertex = "src/main/resources/dades/hexagon_vertex.csv";

	        List<Hexagon> hexagons = carregarHexagonsDesdeCSV(rutaHexagons, novaPartida);
	        List<Vertex> vertexs = carregarVertexsDesdeCSV(rutaVertexs); 
	        
	        generarMapaAleatori(hexagons);

	        Hexagon hexagonDesert = null;
	        for (Hexagon h : hexagons) {
	            session.persist(h);
	            if (TipusTerreny.DESERT.equals(h.getTerreny())) {
	                hexagonDesert = h;
	            }
	        }
	        novaPartida.setTauler(hexagons);

	        if (hexagonDesert != null) {
	            novaPartida.setLladre(hexagonDesert); 
	        }

	        for (Vertex v : vertexs) {
	            v.setPartida(novaPartida);
	            session.persist(v);
	        }

	        session.flush();

	        List<Aresta> arestes = carregarArestesDesdeCSV(rutaArestes, session);
	        for (Aresta a : arestes) {
	            a.setPartida(novaPartida);
	            session.persist(a);
	        }

	        carregarHexagonVertexDesdeCSV(rutaHexagonVertex, session);

	        session.merge(novaPartida);

	        tx.commit();
	    } catch (Exception e) {
	        if (tx != null && tx.isActive()) {
	            tx.rollback();
	        }
	        e.printStackTrace();
	        throw new RuntimeException("Error en iniciar la partida al DAO: " + e.getMessage(), e);
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

	private List<String[]> llegirCSV(String ruta) throws Exception {
		List<String[]> linies = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
			String l; boolean primera = true;
			while ((l = br.readLine()) != null) {
				if (l.trim().isEmpty()) continue;
				if (primera) { primera = false; continue; }
				linies.add(l.split(","));
			}
		}
		return linies;
	}

	private List<Hexagon> carregarHexagonsDesdeCSV(String ruta, Partida p) throws Exception {
		List<Hexagon> llista = new ArrayList<>();
		for (String[] c : llegirCSV(ruta)) {
			Hexagon h = new Hexagon();
			h.setPartida(p);
			h.setCodi(Integer.parseInt(c[0].trim()));
			h.setQ(Integer.parseInt(c[1].trim()));
			h.setR(Integer.parseInt(c[2].trim()));
			h.setTerreny(TipusTerreny.DESERT);
			h.setNumero(0);
			llista.add(h);
		}
		return llista;
	}

	private List<Vertex> carregarVertexsDesdeCSV(String ruta) throws Exception {
		List<Vertex> llista = new ArrayList<>();
		for (String[] c : llegirCSV(ruta)) {
			Vertex v = new Vertex();
			v.setCodi(Integer.parseInt(c[0].trim()));
			llista.add(v);
		}
		return llista;
	}

	private List<Aresta> carregarArestesDesdeCSV(String ruta, Session session) throws Exception {
		List<Aresta> llista = new ArrayList<>();
		for (String[] c : llegirCSV(ruta)) {
			Aresta a = new Aresta();
			a.setCodi(Integer.parseInt(c[0].trim()));
			a.setVertexA(session.getReference(Vertex.class, Integer.parseInt(c[1].trim())));
			a.setVertexB(session.getReference(Vertex.class, Integer.parseInt(c[2].trim())));
			llista.add(a);
		}
		return llista;
	}

	private void carregarHexagonVertexDesdeCSV(String r, Session session) throws Exception {
		for (String[] c : llegirCSV(r)) {
			HexagonVertex hv = new HexagonVertex();
			hv.setHexagon(session.getReference(Hexagon.class, Integer.parseInt(c[0].trim())));
			hv.setVertex(session.getReference(Vertex.class, Integer.parseInt(c[1].trim())));
			hv.setPosicio(Integer.parseInt(c[2].trim()));
			session.persist(hv);
		}
	}

	@Override
	public List<Hexagon> mostraTauler(int idPartida) { return null; }

	@Override
	public Partida mostraPartida(int idPartida) { return null; }

	@Override
	public void tirarDaus(int idPartida) { }

	@Override
	public void comprovarVictoria(int idPartida) { }
	
}
