package DAOcatan;

import java.util.*;

import org.hibernate.Session;

import catan.*;

public class JugadorDao extends GenericDao<Jugador, Integer> implements IJugadorDao {
	
	public Jugador agafarJugador(Session session, int idJugador) throws RuntimeException {
		Jugador jugador = session.get(Jugador.class, idJugador);
		if (jugador == null)
			throw new RuntimeException(Missatges.JUGADOR_NO_EXISTEIX);
		return jugador;
	}
	
	public void comprovarTorn(Jugador jugador) throws RuntimeException {
		if (jugador != jugador.getPartida().getActiu())
			throw new RuntimeException(Missatges.JUGADOR_NO_TORN);
	}

	@Override
	public List<RecursJugador> mostrarRecursos(int idJugador) {
		Session session = SessionManager.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			List<RecursJugador> recursos = agafarJugador(session, idJugador).getInventari();
			session.getTransaction().commit();
			return recursos;
		} catch (RuntimeException e) {
			handleException(session, e);
			return null;
		}
	}

	@Override
	public void construirCarretera(int idJugador, int idAresta) {
		Session session = SessionManager.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			// Agafar el jugador
			Jugador jugador = agafarJugador(session, idJugador);
			// Comprovar el torn del jugador i la partida
			comprovarTorn(jugador);
			// Agafar l'aresta
			Aresta aresta = session.get(Aresta.class, idAresta);
			// Comprovar que l'aresta existeixi
			if (aresta == null)
				throw new RuntimeException(Missatges.ARESTA_NO_EXISTEIX);
			// Agafar la partida
			Partida partida = aresta.getPartida();
			// Comprovar que l'aresta pertany a la mateixa partida que el jugador
			if (!partida.equals(jugador.getPartida()))
				throw new RuntimeException(Missatges.ARESTA_NO_PARTIDA);
			// Comprovar que l'aresta no conté una carretera
			if (aresta.getCarretera() != null)
				throw new RuntimeException(Missatges.ARESTA_PLENA);
			// Comprovar que l'aresta estigui connectada
			List<Aresta> arestes;
			List<Assentament> assentaments;
			if (partida.getEstat() == EstatPartida.PREPARACIO) {
				arestes = new ArrayList<>();
				assentaments = session.createQuery("select a.* from assentament a where a.id = :id and a.tipus like \"POBLAT\" and a.vertex_id in {:a, :b}", Assentament.class)
						.setParameter("id", jugador.getPropietari().getLast().getId())
						.setParameter("a", aresta.getVertexA().getId())
						.setParameter("b", aresta.getVertexB().getId())
						.getResultList();
			} else {
				arestes = session.createQuery("SELECT a.* FROM aresta a WHERE a.vertex_a_id in {:a, :b} or a.vertex_b_id in {:c, :d}", Aresta.class)
						.setParameter("a", aresta.getVertexA().getId())
						.setParameter("b", aresta.getVertexB().getId())
						.setParameter("c", aresta.getVertexA().getId())
						.setParameter("d", aresta.getVertexB().getId())
						.getResultList();
				assentaments = session.createQuery("SELECT a.* FROM assentament a WHERE a.vertex_id in {:a, :b}", Assentament.class)
						.setParameter("a", aresta.getVertexA().getId())
						.setParameter("b", aresta.getVertexB().getId())
						.getResultList();
			}
			if (arestes.isEmpty() && assentaments.isEmpty())
				throw new RuntimeException(Missatges.ARESTA_NO_CONECTADA);
			// Comprovar que el jugador no superi el límit de carreteres
			List<Construccio> propietats = jugador.getPropietari();
			int carreteres = 0;
			for (Construccio propietat : propietats)
				if (propietat instanceof Carretera)
					carreteres++;
			if (carreteres >= 15)
				throw new RuntimeException(Missatges.JUGADOR_MAX_CARRETERA);
			if (partida.getEstat() != EstatPartida.PREPARACIO) {
				// Comprovar que el jugador tingui els recursos necessaris
				List<RecursJugador> materials = jugador.getInventari();
				RecursJugador fusta = null;
				RecursJugador mao = null;
				for (RecursJugador recurs : materials) {
					if (recurs.getTipus() == TipusRecurs.FUSTA)
						fusta = recurs;
					if (recurs.getTipus() == TipusRecurs.MAO)
						mao = recurs;
				}
				if (fusta == null || mao == null)
					throw new RuntimeException(Missatges.JUGADOR_NO_RECURS);
				if (fusta.getQuantitat() <= 0 && mao.getQuantitat() <= 0)
					throw new RuntimeException(Missatges.JUGADOR_NO_RECURS);
				// Restar els recursos utilitzats en la carretera
				fusta.setQuantitat(fusta.getQuantitat()-1);
				mao.setQuantitat(mao.getQuantitat()-1);
			}
			// Afegir la carretera
			jugador.getPropietari().add(new Carretera(jugador, aresta));
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			handleException(session, e);
		}
	}

	@Override
	public void construirPoblat(int idJugador, int idVertex) {
		Session session = SessionManager.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			// Agafar el jugador
			Jugador jugador = agafarJugador(session, idJugador);
			// Comprovar el torn
			comprovarTorn(jugador);
			// Agafar el vèrtex
			Vertex vertex = session.get(Vertex.class, idVertex);
			// Comprovar que el vèrtex existeixi
			if (vertex == null)
				throw new RuntimeException(Missatges.VERTEX_NO_EXISTEIX);
			// Comprovar que el vèrtex estigui buit
			if (vertex.getAssentament() != null)
				throw new RuntimeException(Missatges.VERTEX_PLE);
			// Comprovar que el poblat no tingui cap assentament adjacent
			List<Vertex> adjacents = session.createQuery("select a.* from aresta a join vertex va on a.vertex_a_id = va.id join vertex vb on a.vertex_b_id where (va.id = :ida or vb.id = :idb) and (va.assentament_id is not null or vb.assentament_id is not null)", Vertex.class)
					.setParameter("ida", idVertex)
					.setParameter("idb", idVertex)
					.getResultList();
			if (!adjacents.isEmpty())
				throw new RuntimeException(Missatges.VERTEX_POBLAT_ADJACENT);
			// Comprovar que el vèrtex estigui connectat
			if (jugador.getPartida().getEstat() == EstatPartida.PREPARACIO) {
				List<Vertex> carreteres = session.createQuery("select a.* from aresta a join vertex va on a.vertex_a_id = va.id join vertex vb on a.vertex_b_id where (va.id = :ida or vb.id = :idb) and a.carretera_id is not null", Vertex.class)
						.setParameter("ida", idVertex)
						.setParameter("idb", idVertex)
						.getResultList();
				if (carreteres.isEmpty())
					throw new RuntimeException(Missatges.VERTEX_NO_CONECTAT);
			}
			// Comprovar que el jugador no superi el límit de poblats
			List<Construccio> propietats = jugador.getPropietari();
			int poblats = 0;
			for (Construccio propietat : propietats)
				if (propietat instanceof Carretera)
					poblats++;
			if (poblats >= 5)
				throw new RuntimeException(Missatges.JUGADOR_MAX_POBLAT);
			if (jugador.getPartida().getEstat() == EstatPartida.PREPARACIO) {
				// Comprovar que el jugador tingui els recursos necessaris
				List<RecursJugador> materials = jugador.getInventari();
				RecursJugador fusta = null;
				RecursJugador mao = null;
				RecursJugador llana = null;
				RecursJugador blat = null;
				for (RecursJugador recurs : materials) {
					switch (recurs.getTipus()) {
					case TipusRecurs.FUSTA: fusta = recurs; break;
					case TipusRecurs.MAO: mao = recurs; break;
					case TipusRecurs.LLANA: llana = recurs; break;
					case TipusRecurs.BLAT: blat = recurs; break;
					default: break;
					}
				}
				if (fusta == null || mao == null || llana == null || blat == null)
					throw new RuntimeException(Missatges.JUGADOR_NO_RECURS);
				if (fusta.getQuantitat() <= 0 || mao.getQuantitat() <= 0 || llana.getQuantitat() <= 0 || blat.getQuantitat() <= 0)
					throw new RuntimeException(Missatges.JUGADOR_NO_RECURS);
				// Restar els recursos utilitzats en el poblat
				fusta.setQuantitat(fusta.getQuantitat()-1);
				mao.setQuantitat(mao.getQuantitat()-1);
				llana.setQuantitat(llana.getQuantitat()-1);
				blat.setQuantitat(blat.getQuantitat()-1);
			}
			// Afegir el poblat
			jugador.getPropietari().add(new Poblat(jugador, vertex));
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			handleException(session, e);
		}
	}

	@Override
	public void millorarACiutat(int idJugador, int idVertex) {
		Session session = SessionManager.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			// Agafar el jugador
			Jugador jugador = agafarJugador(session, idJugador);
			// Comprovar el torn
			comprovarTorn(jugador);
			// Agafar el vèrtex
			Vertex vertex = session.get(Vertex.class, idVertex);
			// Comprovar que el vèrtex existeixi
			if (vertex == null)
				throw new RuntimeException(Missatges.VERTEX_NO_EXISTEIX);
			// Comprovar que el vèrtex contingui un poblat
			if (!(vertex.getAssentament() instanceof Poblat))
				throw new RuntimeException(Missatges.VERTEX_NO_POBLAT);
			// Agafar el poblat
			Poblat poblat = (Poblat) vertex.getAssentament();
			// Comprovar que el poblat pertany al jugador
			if (jugador.equals(poblat.getJugador()))
				throw new RuntimeException(Missatges.POBLAT_NO_JUGADOR);
			// Comprovar que el jugador no superi el límit de ciutats
			List<Construccio> propietats = jugador.getPropietari();
			int ciutats = 0;
			for (Construccio propietat : propietats)
				if (propietat instanceof Carretera)
					ciutats++;
			if (ciutats >= 5)
				throw new RuntimeException(Missatges.JUGADOR_MAX_CIUTAT);
			// Comprovar que el jugador tingui els materials suficients
			List<RecursJugador> materials = jugador.getInventari();
			RecursJugador blat = null;
			RecursJugador mineral = null;
			for (RecursJugador recurs : materials) {
				switch (recurs.getTipus()) {
				case TipusRecurs.BLAT: blat = recurs; break;
				case TipusRecurs.MINERAL: mineral = recurs; break;
				default: break;
				}
			}
			if (blat == null || mineral == null)
				throw new RuntimeException(Missatges.JUGADOR_NO_RECURS);
			if (blat.getQuantitat() < 2 || mineral.getQuantitat() < 3)
				throw new RuntimeException(Missatges.JUGADOR_NO_RECURS);
			// Restar els materials utilitzats en la ciutat
			blat.setQuantitat(blat.getQuantitat()-2);
			mineral.setQuantitat(mineral.getQuantitat()-3);
			// Canviar el poblat per la ciutat
			jugador.getPropietari().remove(poblat);
			jugador.getPropietari().add(new Ciutat(jugador, vertex));
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			handleException(session, e);
		}
	}

	@Override
	public void comerciarBanca(int idJugador, TipusRecurs dona, TipusRecurs rep) {
		Session session = SessionManager.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			// Agafar el jugador
			Jugador jugador = agafarJugador(session, idJugador);
			// Comprovar el torn
			comprovarTorn(jugador);
			// Comprovar que no està donant i rebent el mateix
			if (dona == rep)
				throw new RuntimeException(Missatges.COMERC_MATEIX_RECURS);
			// Comprovar que el jugador tingui els materials suficients
			List<RecursJugador> materials = jugador.getInventari();
			RecursJugador rjDona = null;
			RecursJugador rjRep = null;
			for (RecursJugador recurs : materials) {
				if (recurs.getTipus() == dona)
					rjDona = recurs;
				if (recurs.getTipus() == rep)
					rjRep = recurs;
			}
			if (rjDona == null)
				throw new RuntimeException(Missatges.JUGADOR_NO_RECURS);
			if (rjDona.getQuantitat() < 4)
				throw new RuntimeException(Missatges.JUGADOR_NO_RECURS);
			// Treu 4 del recurs que dona el jugador i afegeix 1 al que rep
			rjDona.setQuantitat(rjDona.getQuantitat() - 4);
			if (rjRep == null)
				jugador.getInventari().add(new RecursJugador(jugador, rep, 1));
			else
				rjRep.setQuantitat(rjRep.getQuantitat() + 1);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			handleException(session, e);
		}
	}

	@Override
	public int calcularPunts(int idJugador) {
		// Preparar la variable del total de punts
		int total = 0;
		Session session = SessionManager.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			// Agafar el jugador
			Jugador jugador = agafarJugador(session, idJugador);
			// Compta els poblats i les ciutats del jugador
			List<Construccio> propietats = jugador.getPropietari();
			for (Construccio propietat : propietats) {
				if (propietat instanceof Poblat)
					total++;
				if (propietat instanceof Ciutat)
					total += 2;
			}
			List<CartaDesenvolupament> ma = jugador.getMa();
			for (CartaDesenvolupament carta : ma) {
				if (carta.getEstat() == TipusCarta.PUNT_VICTORIA)
					total++;
			}
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			handleException(session, e);
		}
		return total;
	}

	@Override
	public void passarTorn(int idJugador) {
		Session session = SessionManager.getSessionFactory().openSession();
		try {
			session.beginTransaction();
			// Agafar el jugador
			Jugador jugador = agafarJugador(session, idJugador);
			// Comprovar que el jugador sigui l'actiu
			if (!jugador.isActiu())
				throw new RuntimeException(Missatges.JUGADOR_NO_ACTIU);
			// Agafar la partida
			Partida partida = jugador.getPartida();
			// Comprovar que el jugador hagi tirat els daus (si la partida és EN CURS)
			if (partida.getEstat() == EstatPartida.EN_CURS)
				if (!partida.isDausTirats())
					throw new RuntimeException(Missatges.DAUS_NO_TIRATS);
			// Comprova que el jugador hagi col·locat el poble i la carretera (si la partida és PREPARACIO)
			if (partida.getEstat() == EstatPartida.PREPARACIO) {
				int carreteres = 0;
				int assentaments = 0;
				List<Construccio> propietats = jugador.getPropietari();
				for (Construccio propietat : propietats)
					if (propietat instanceof Assentament)
						assentaments++;
					else
						carreteres++;
				if (assentaments < partida.getRonda() || carreteres < partida.getRonda())
					throw new RuntimeException(Missatges.PREPARACIO_NO_ACABADA);
			}
			// Comprova que el lladre no estigui pendent
			if (partida.isLladrePendent())
				throw new RuntimeException(Missatges.LLADRE_PENDENT);
			// Canvia l'ordre si toca...
			if ((jugador.getOrdreTorn() == 1 && partida.getRonda()%2 == 1) || (jugador.getOrdreTorn() == 4 && partida.getRonda()%2 == 0))
				partida.setRonda(partida.getRonda() + 1);
			else
				// ...o passa al següent jugador
				partida.setActiu(partida.getJugadors().get(jugador.getOrdreTorn() + (partida.getRonda()%2 == 0 ? 1 : -1)));
			// Posa l'estat de la partida a el que toqui
			if (partida.getRonda() == 3 && partida.getEstat() == EstatPartida.PREPARACIO) {
				partida.setEstat(EstatPartida.EN_CURS);
				partida.setRonda(1);
			}
			// Indica que els daus s'han de tirar
			partida.setDausTirats(false);
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			handleException(session, e);
		}
	}

}
