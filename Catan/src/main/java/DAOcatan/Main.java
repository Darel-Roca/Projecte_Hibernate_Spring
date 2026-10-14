package DAOcatan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import catan.Partida;

public class Main {

	public static void main(String[] args) {
		System.out.println("Iniciant l'aplicació Catan...");
		try {
			
			List<String> noms = new ArrayList<String>();
			
			noms.add("Pajuelo");
			noms.add("Ana");
			noms.add("Claudia");

			PartidaDao partidaDao = new PartidaDao();
			
			int partida_id = partidaDao.iniciarPartida(noms);

			System.out.println("Partida insertada amb id=" + partida_id);
			llistarTots();
			Partida trobada = cercar(partida_id);
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
