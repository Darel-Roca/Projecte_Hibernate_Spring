package DAOcatan;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import catan.Aresta;
import catan.Assentament;
import catan.Carretera;
import catan.CartaDesenvolupament;
import catan.Ciutat;
import catan.Hexagon;
import catan.HexagonVertex;
import catan.Jugador;
import catan.Partida;
import catan.Poblat;
import catan.RecursJugador;
import catan.Vertex;

public class SessionManager {

	private static SessionFactory sessionFactory;

	private SessionManager() {
	}

	public static synchronized SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			try {
				Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
				configuration.addAnnotatedClass(Partida.class);
				configuration.addAnnotatedClass(Jugador.class);
				configuration.addAnnotatedClass(Hexagon.class);
				configuration.addAnnotatedClass(HexagonVertex.class);
				configuration.addAnnotatedClass(Vertex.class);
				configuration.addAnnotatedClass(Aresta.class);
				configuration.addAnnotatedClass(Carretera.class);
				configuration.addAnnotatedClass(Assentament.class);
				configuration.addAnnotatedClass(Ciutat.class);
				configuration.addAnnotatedClass(Poblat.class);
				configuration.addAnnotatedClass(CartaDesenvolupament.class);
				configuration.addAnnotatedClass(RecursJugador.class);
				sessionFactory = configuration.buildSessionFactory();
			} catch (HibernateException e) {
				System.err.println("Error al crear la SessionFactory: " + e.getMessage());
				throw new ExceptionInInitializerError(e);
			}
		}
		return sessionFactory;
	}

	public static synchronized void tancar() {
		if (sessionFactory != null) {
			sessionFactory.close();
			sessionFactory = null;
		}
	}
}
