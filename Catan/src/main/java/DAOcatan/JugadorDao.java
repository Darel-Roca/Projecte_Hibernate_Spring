package DAOcatan;

import java.util.*;

import org.hibernate.Session;

import catan.*;

public class JugadorDao extends GenericDao<Jugador, Integer> implements IJugadorDao {

	@Override
	public List<RecursJugador> mostrarRecursos(int idJugador) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			List<RecursJugador> recursos = session.createQuery("SELECT r.* FROM recursJugador r WHERE r.id = :id", RecursJugador.class)
					.setParameter("id", idJugador)
					.getResultList();
			session.getTransaction().commit();
			return recursos;
		} catch (RuntimeException e) {
			handleException(session, e);
			return null;
		}
	}

	@Override
	public void construirCarretera(int idJugador, int idAresta) {
		Session session = sessionFactory.getCurrentSession();
		try {
			session.beginTransaction();
			Jugador jugador = session.createQuery("SELECT j.* FROM jugador j WHERE j.id = :id", Jugador.class)
					.setParameter("id", idJugador)
					.uniqueResult();
			if (jugador == jugador.getPartida().getActiu()) {
				Aresta aresta = session.createQuery("SELECT a.* FROM aresta a WHERE a.id = :id", Aresta.class)
						.setParameter("id", idAresta)
						.uniqueResult();
				if (aresta != null) {
					Partida partida = aresta.getPartida();
					if (partida.equals(jugador.getPartida())) {
						if (aresta.getCarretera() == null) {
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
							if (!arestes.isEmpty() || !assentaments.isEmpty()) {
								if (partida.getEstat() != EstatPartida.PREPARACIO) {
									List<RecursJugador> materials = jugador.getInventari();
									RecursJugador fusta = null;
									RecursJugador mao = null;
									for (RecursJugador recurs : materials) {
										if (recurs.getTipus() == TipusRecurs.FUSTA)
											fusta = recurs;
										if (recurs.getTipus() == TipusRecurs.MAO)
											mao = recurs;
									}
									if (fusta != null && mao != null) {
										if (fusta.getQuantitat() > 0 && mao.getQuantitat() > 0) {
											List<Construccio> propietats = jugador.getPropietari();
											int carreteres = 0;
											for (Construccio propietat : propietats) {
												if (propietat instanceof Carretera)
													carreteres++;
											}
											if (carreteres < 15) {
												fusta.setQuantitat(fusta.getQuantitat()-1);
												mao.setQuantitat(mao.getQuantitat()-1);
											} else {
												throw new RuntimeException(Missatges.JUGADOR_MAX_CARRETERA);
											}
										} else {
											throw new RuntimeException(Missatges.JUGADOR_NO_RECURS);
										}
									} else {
										throw new RuntimeException(Missatges.JUGADOR_NO_RECURS);
									}
								}
								jugador.getPropietari().add(new Carretera(jugador, aresta));
							} else {
								throw new RuntimeException(Missatges.ARESTA_NO_CONECTADA);
							}
						} else {
							throw new RuntimeException(Missatges.ARESTA_PLENA);
						}
					} else {
						throw new RuntimeException(Missatges.ARESTA_NO_PERTANY);
					}
				} else {
					throw new RuntimeException(Missatges.ARESTA_NO_EXISTEIX);
				}
			} else {
				throw new RuntimeException(Missatges.JUGADOR_NO_TORN);
			}
			session.getTransaction().commit();
		} catch (RuntimeException e) {
			handleException(session, e);
		}
	}

	@Override
	public void construirPoblat(int idJugador, int idVertex) {
		
	}

	@Override
	public void millorarACiutat(int idJugador, int idVertex) {
		
	}

	@Override
	public void comerciarBanca(int idJugador, TipusRecurs dona, TipusRecurs rep) {
		
	}

	@Override
	public int calcularPunts(int idJugador) {
		return 0;
	}

	@Override
	public void passarTorn(int idJugador) {
		
	}

}
