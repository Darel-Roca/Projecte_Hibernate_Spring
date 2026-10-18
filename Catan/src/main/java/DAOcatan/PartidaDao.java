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

	        if (hexagonDesert != null) {
	            session.createMutationQuery(
	                "UPDATE Partida p SET p.lladre = :lladre WHERE p.id = :partidaId")
	                .setParameter("lladre", hexagonDesert)
	                .setParameter("partidaId", novaPartida.getId())
	                .executeUpdate();
	        }

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
			TipusTerreny.DESERT,
			TipusTerreny.BOSC, TipusTerreny.BOSC, TipusTerreny.BOSC, TipusTerreny.BOSC,
			TipusTerreny.PASTURES, TipusTerreny.PASTURES, TipusTerreny.PASTURES, TipusTerreny.PASTURES,
			TipusTerreny.CAMPS, TipusTerreny.CAMPS, TipusTerreny.CAMPS, TipusTerreny.CAMPS,
			TipusTerreny.TURONS, TipusTerreny.TURONS, TipusTerreny.TURONS,
			TipusTerreny.MUNTANYES, TipusTerreny.MUNTANYES, TipusTerreny.MUNTANYES
		));
		
		while (terrenys.size() < hexagons.size()) {
			terrenys.add(TipusTerreny.BOSC); 
		}

		List<Integer> numeros = new ArrayList<>(List.of(
			2, 12, 
			3, 3, 4, 4, 5, 5, 6, 6, 
			8, 8, 9, 9, 10, 10, 11, 11
		));
		
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
	public List<Hexagon> mostraTauler(int idPartida) {
		Session session = SessionManager.getSessionFactory().openSession();
		List<Hexagon> hexagons = null;
		try {
			hexagons = session.createQuery(
				"FROM Hexagon h WHERE h.partida.id = :idPartida", Hexagon.class)
				.setParameter("idPartida", idPartida)
				.getResultList();

			if (hexagons.isEmpty()) {
				System.out.println("No s'ha trobat cap tauler per a la partida amb ID: " + idPartida);
				return hexagons;
			}

			Partida partida = session.find(Partida.class, idPartida);
			Hexagon hexagonLladre = (partida != null) ? partida.getLladre() : null;

			System.out.println("\nTAULER DE LA PARTIDA (ID: " + idPartida + ")");
			
			for (Hexagon h : hexagons) {
				boolean teLladre = (hexagonLladre != null && hexagonLladre.getId() == h.getId());
				
				System.out.printf("Hexàgon [%d] -> Terreny: %s | Número: %d %s\n", 
						h.getId(), h.getTerreny(), h.getNumero(), teLladre ? "[🦹 LLADRE SITUAT AQUÍ]" : "");

				List<Object[]> dadesVertexs = session.createQuery(
					"SELECT v.codi, hv.posicio, a.id, j.nom " +
					"FROM HexagonVertex hv " +
					"JOIN hv.vertex v " +
					"LEFT JOIN v.assentament a " +
					"LEFT JOIN a.jugador j " +
					"WHERE hv.hexagon.id = :hexId " +
					"ORDER BY hv.posicio", Object[].class)
					.setParameter("hexId", h.getId())
					.getResultList();

				for (Object[] fila : dadesVertexs) {
					Integer vtxCodi = (Integer) fila[0];
					Integer posicio = (Integer) fila[1];
					Integer assentamentId = (Integer) fila[2];
					String nomPropietari = (String) fila[3];

					String infoConstruccio = "Buit";
					if (assentamentId != null) {
						String tipus = "Assentament"; 
						Object assentamentReal = session.find(catan.Assentament.class, assentamentId);
						if (assentamentReal != null) {
							tipus = assentamentReal.getClass().getSimpleName();
						}
						infoConstruccio = String.format("%s (Propietari: %s)", tipus, nomPropietari != null ? nomPropietari : "Desconegut");
					}

					System.out.printf("   └─ Vèrtex Codi %d (Posició: %d) -> %s\n", 
							vtxCodi, posicio, infoConstruccio);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		return hexagons;
	}



	@Override
	public void mostraPartida(int idPartida) {
		Session session = SessionManager.getSessionFactory().openSession();
		try {
			Partida partida = session.find(Partida.class, idPartida);
			if (partida == null) {
				System.out.println("No s'ha trobat cap partida amb ID: " + idPartida);
				return; 
			}

			String nomJugadorActiu = "Cap (Fase de Preparació)";
			if (partida.getActiu() != null) {
				nomJugadorActiu = partida.getActiu().getNom();
			}

			System.out.println("\n=======================================================");
			System.out.println("📊 ESTAT DE LA PARTIDA (ID: " + idPartida + ")");
			System.out.println("=======================================================");
			System.out.println("-> Estat actual:    " + partida.getEstat());
			System.out.println("-> Ronda:           " + partida.getRonda());
			System.out.println("-> Jugador Actiu:   " + nomJugadorActiu);
			System.out.println("-> Última Tirada:   " + partida.getUltimaTirada());
			System.out.println("-------------------------------------------------------");

			for (Jugador j : partida.getJugadors()) {
				System.out.println("👤 JUGADOR: " + j.getNom() + " (Color: " + j.getColor() + ")");

				Long numPoblats = session.createQuery(
					"SELECT count(p) FROM Poblat p WHERE p.jugador.id = :jId", Long.class)
					.setParameter("jId", j.getId())
					.getSingleResult();

				Long numCiutats = session.createQuery(
					"SELECT count(c) FROM Ciutat c WHERE c.jugador.id = :jId", Long.class)
					.setParameter("jId", j.getId())
					.getSingleResult();

				Long numCarreteres = session.createQuery(
					"SELECT count(ca) FROM Carretera ca WHERE ca.jugador.id = :jId", Long.class)
					.setParameter("jId", j.getId())
					.getSingleResult();

				Long numCartesPunts = session.createQuery(
					"SELECT count(cd) FROM CartaDesenvolupament cd WHERE cd.jugador.id = :jId AND cd.estat = 'PUNT_VICTORIA'", Long.class)
					.setParameter("jId", j.getId())
					.getSingleResult();

				int puntsTotals = (numPoblats.intValue() * 1) + (numCiutats.intValue() * 2) + numCartesPunts.intValue();

				Long totalRecursos = session.createQuery(
					"SELECT sum(rj.quantitat) FROM RecursJugador rj WHERE rj.jugador.id = :jId", Long.class)
					.setParameter("jId", j.getId())
					.getSingleResult();
				
				long recursosSegurs = (totalRecursos != null) ? totalRecursos : 0;

				System.out.printf("   ├─ Punts de Victòria:  %d\n", puntsTotals);
				System.out.printf("   ├─ Nombre de Poblats:  %d\n", numPoblats);
				System.out.printf("   ├─ Nombre de Ciutats:  %d\n", numCiutats);
				System.out.printf("   ├─ Nombre de Carreteres: %d\n", numCarreteres);
				System.out.printf("   └─ Total de Recursos:   %d (Detall ocult)\n", recursosSegurs);
				System.out.println("-------------------------------------------------------");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	@Override
	public void tirarDaus(int idPartida) {
		Session session = SessionManager.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Partida partida = session.find(Partida.class, idPartida);
			if (partida == null) {
				System.out.println("No s'ha trobat la partida amb ID: " + idPartida);
				session.close();
				return;
			}
			if (partida.isDausTirats()) {
				throw new IllegalStateException("El jugador actiu ja ha tirat els daus en aquest torn.");
			}
			int dau1 = (int) (Math.random() * 6) + 1;
			int dau2 = (int) (Math.random() * 6) + 1;
			int resultatTirada = dau1 + dau2;

			partida.setUltimaTirada(resultatTirada);
			partida.setDausTirats(true);
			
			System.out.println("\n ELS DAUS HAN RATLLAT UN: " + resultatTirada + " (" + dau1 + " + " + dau2 + ")");
			System.out.println("=========================================");

			if (resultatTirada == 7) {
				System.out.println("🦹 ¡Ha sortit un 7! S'activa el lladre. No hi ha producció de recursos.");
				partida.setLladrePendent(true);
			} 
			else {
				int lladreHexId = (partida.getLladre() != null) ? partida.getLladre().getId() : -1;

				List<Hexagon> hexagonsProductius = session.createQuery(
					"FROM Hexagon h WHERE h.partida.id = :pId AND h.numero = :num AND h.id != :lladreId", Hexagon.class)
					.setParameter("pId", idPartida)
					.setParameter("num", resultatTirada)
					.setParameter("lladreId", lladreHexId)
					.getResultList();

				if (hexagonsProductius.isEmpty()) {
					System.out.println("ℹ️ Cap hexàgon lliure conté el número " + resultatTirada + ". Ningú rep recursos.");
				}

				for (Hexagon h : hexagonsProductius) {
					TipusRecurs recursGenerat = obtenirTipusRecursPerTerreny(h.getTerreny());
					if (recursGenerat == null) continue;

					List<Object[]> assentamentsAdjacents = session.createQuery(
						"SELECT a.id, j.id, j.nom " +
						"FROM HexagonVertex hv " +
						"JOIN hv.vertex v " +
						"JOIN v.assentament a " +
						"JOIN a.jugador j " +
						"WHERE hv.hexagon.id = :hexId", Object[].class)
						.setParameter("hexId", h.getId())
						.getResultList();

					for (Object[] fila : assentamentsAdjacents) {
						Integer assentamentId = (Integer) fila[0];
						Integer jugadorId = (Integer) fila[1];
						String nomJugador = (String) fila[2];

						Object assentamentReal = session.find(catan.Assentament.class, assentamentId);
						int quantitatAFegir = 0;

						if (assentamentReal instanceof catan.Poblat) {
							quantitatAFegir = 1;
						} else if (assentamentReal instanceof catan.Ciutat) {
							quantitatAFegir = 2;
						}

						if (quantitatAFegir > 0) {
							session.createMutationQuery(
								"UPDATE RecursJugador rj SET rj.quantitat = rj.quantitat + :quant " +
								"WHERE rj.jugador.id = :jId AND rj.tipus = :tipus")
								.setParameter("quant", quantitatAFegir)
								.setParameter("jId", jugadorId)
								.setParameter("tipus", recursGenerat)
								.executeUpdate();

							System.out.printf(" Jugador %s rep %d de %s (Hexàgon %d - %s)\n", 
									nomJugador, quantitatAFegir, recursGenerat, h.getId(), h.getTerreny());
						}
					}
				}
			}

			session.merge(partida);
			tx.commit();
		} catch (Exception e) {
			if (tx != null && tx.isActive()) tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	private TipusRecurs obtenirTipusRecursPerTerreny(TipusTerreny terreny) {
		switch (terreny) {
			case BOSC:      return TipusRecurs.FUSTA;
			case TURONS:    return TipusRecurs.MAO;
			case PASTURES:  return TipusRecurs.LLANA;
			case CAMPS:     return TipusRecurs.BLAT;
			case MUNTANYES: return TipusRecurs.MINERAL;
			default:        return null;
		}
	}

	@Override
	public void comprovarVictoria(int idPartida) {
		Session session = SessionManager.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			Partida partida = session.find(Partida.class, idPartida);
			if (partida == null || partida.getEstat() == EstatPartida.FINALITZADA) {
				session.close();
				return;
			}
			
			int puntsNecessaris = partida.getPuntsPerGuanyar();
			Jugador guanyador = null;

			for (Jugador j : partida.getJugadors()) {
				int puntsTotals = 0;

				Long numPoblats = session.createQuery(
					"SELECT count(p) FROM Poblat p WHERE p.jugador.id = :jId", Long.class)
					.setParameter("jId", j.getId())
					.getSingleResult();

				Long numCiutats = session.createQuery(
					"SELECT count(c) FROM Ciutat c WHERE c.jugador.id = :jId", Long.class)
					.setParameter("jId", j.getId())
					.getSingleResult();

				puntsTotals += (numPoblats.intValue() * 1) + (numCiutats.intValue() * 2);

				System.out.println("-> Jugador " + j.getNom() + " té actualment: " + puntsTotals + " punts.");

				if (puntsTotals >= puntsNecessaris) {
					guanyador = j;
					break;
				}
			}

			if (guanyador != null) {
				System.out.println("\n🎉🏆 ¡EL JUGADOR " + guanyador.getNom().toUpperCase() + " HA GUANYAT LA PARTIDA! 🏆🎉");
				
				partida.setEstat(EstatPartida.FINALITZADA);
				partida.setGuanyador(guanyador); 
				
				session.merge(partida);
			}

			tx.commit();
		} catch (Exception e) {
			if (tx != null && tx.isActive()) tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	
}
