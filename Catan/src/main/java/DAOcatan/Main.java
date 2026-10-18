package DAOcatan;

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
				System.out.println("Partida actualitzada correctament a la ronda 2.");
			}
		} catch (Exception e) {
			System.err.println("S'ha produït un error en l'execució del Main:");
			e.printStackTrace();
		} finally {
			SessionManager.tancar();
		}
	}
	public static Partida cercar(int id) {
		try (Session session = SessionManager.getSessionFactory().openSession()) {
			Transaction tx = session.beginTransaction();
			try {
				Partida p = session.find(Partida.class, id);
				tx.commit();
				System.out.println("Cerca per id (" + id + "): " + p);
				return p;
			} catch (RuntimeException e) {
				if (tx.isActive()) tx.rollback();
				throw e;
			}
		}
	}

	public static void llistarTots() {
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

	public static void actualitzar(Partida partida) {
		try (Session session = SessionManager.getSessionFactory().openSession()) {
			Transaction tx = session.beginTransaction();
			try {
				session.merge(partida);
				tx.commit();
			} catch (RuntimeException e) {
				if (tx.isActive()) tx.rollback();
				throw e;
			}
		}
	}
}
