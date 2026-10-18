
package DAOcatan;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import catan.Hexagon;
import catan.Partida;
import catan.RecursJugador;
import catan.TipusRecurs;

public class Main {

    public static void main(String[] args) {
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

            List<Hexagon> tauler = partidaDao.mostraTauler(partida_id);

            System.out.println("\n📊 Resum de la llista d'Hexàgons retornada:");
            for (Hexagon h : tauler) {
                System.out.println(h);
            }

            partidaDao.comprovarVictoria(partida_id);
            partidaDao.mostraPartida(partida_id);

            JugadorDao jugadorDao = new JugadorDao();
            int idJugador = 1;
            int idAresta = 1;
            int idVertex = 1;
            
            List<RecursJugador> recursos =
                    jugadorDao.mostrarRecursos(idJugador);

            if (recursos != null) {
                for (RecursJugador recurs : recursos) {
                    System.out.println(recurs);
                }
            }

            jugadorDao.construirCarretera(idJugador, idAresta);

            jugadorDao.construirPoblat(idJugador, idVertex);

            jugadorDao.millorarACiutat(idJugador, idVertex);

            jugadorDao.comerciarBanca(
                    idJugador,
                    TipusRecurs.FUSTA,
                    TipusRecurs.MAO
            );

            int punts = jugadorDao.calcularPunts(idJugador);
            System.out.println("Punts de victòria: " + punts);

            jugadorDao.passarTorn(idJugador);

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
                List<Partida> partidas =
                        session.createQuery("FROM Partida", Partida.class)
                               .getResultList();

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
                session.createMutationQuery(
                    "UPDATE Partida p SET p.ronda = :ronda, p.estat = :estat WHERE p.id = :id")
                    .setParameter("ronda", partida.getRonda())
                    .setParameter("estat", partida.getEstat())
                    .setParameter("id", partida.getId())
                    .executeUpdate();

                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }
}