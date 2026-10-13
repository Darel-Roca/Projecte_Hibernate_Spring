package DAOcatan;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import catan.ColorJugador;
import catan.EstatPartida;
import catan.Jugador;
import catan.Partida;

public class Main {

	public static void main(String[] args) {
		System.out.println("Iniciant l'aplicació Catan...");
		try {
			Partida partida = new Partida();
			partida.setEstat(EstatPartida.PREPARACIO);
			partida.setDataInici(LocalDate.now());
			partida.setRonda(1);
			partida.setUltimaTirada(3);
			partida.setPuntsPerGuanyar(10);
			partida.setDausTirats(false);
			partida.setLladrePendent(false);

			Jugador jugador = new Jugador();
			jugador.setNom("Anna");
			jugador.setColor(ColorJugador.BLAU);
			jugador.setOrdreTorn(1);
			jugador.setCavallersJugats(0);
			jugador.setActiu(true);
			jugador.setGuanyador(false);
			jugador.setPartida(partida);

			partida.getJugadors().add(jugador);
			partida.setActiu(jugador);

			int id = inserir(partida);
			System.out.println("Partida insertada amb id=" + id);
			llistarTots();
			Partida trobada = cercar(id);
			if (trobada != null) {
				trobada.setRonda(2);
				actualitzar(trobada);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionManager.tancar();
		}
	}

	private static int inserir(Partida partida) {
		try (Session session = SessionManager.getSessionFactory().openSession()) {
			Transaction tx = session.beginTransaction();
			try {
				session.persist(partida);
				session.flush();
				tx.commit();
				return partida.getId();
			} catch (RuntimeException e) {
				if (tx.isActive()) tx.rollback();
				throw e;
			}
		}
	}

	private static Partida cercar(int id) {
		try (Session session = SessionManager.getSessionFactory().openSession()) {
			Transaction tx = session.beginTransaction();
			try {
				Partida p = session.find(Partida.class, id);
				tx.commit();
				System.out.println("Cerca per id: " + p);
				return p;
			} catch (RuntimeException e) {
				if (tx.isActive()) tx.rollback();
				throw e;
			}
		}
	}

	private static void llistarTots() {
		try (Session session = SessionManager.getSessionFactory().openSession()) {
			Transaction tx = session.beginTransaction();
			try {
				List<Partida> partidas = session.createQuery("FROM Partida", Partida.class).getResultList();
				System.out.println("\nLlista de partides:");
				for (Partida p : partidas) {
					System.out.println(p);
				}
				tx.commit();
			} catch (RuntimeException e) {
				if (tx.isActive()) tx.rollback();
				throw e;
			}
		}
	}

	private static void actualitzar(Partida partida) {
		try (Session session = SessionManager.getSessionFactory().openSession()) {
			Transaction tx = session.beginTransaction();
			try {
				session.merge(partida);
				tx.commit();
				System.out.println("Actualització correcta: " + partida);
			} catch (RuntimeException e) {
				if (tx.isActive()) tx.rollback();
				throw e;
			}
		}
	}
}
