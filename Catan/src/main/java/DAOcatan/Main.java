
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

            // =================================================
            // NOVES FUNCIONS DEL JUGADORD AO
            // =================================================

            JugadorDao jugadorDao = new JugadorDao();

            // Agafar un jugador de la partida creada
            // Canvia l'ID si els jugadors tenen altres identificadors.
            int idJugador = 1;
            int idAresta = 1;
            int idVertex = 1;

            // 1. Mostrar els recursos del jugador
            System.out.println("\n===== RECURSOS DEL JUGADOR =====");
            List<RecursJugador> recursos =
                    jugadorDao.mostrarRecursos(idJugador);

            if (recursos != null) {
                for (RecursJugador recurs : recursos) {
                    System.out.println(recurs);
                }
            }

            // 2. Construir una carretera
            System.out.println("\n===== CONSTRUIR CARRETERA =====");
            jugadorDao.construirCarretera(idJugador, idAresta);

            // 3. Construir un poblat
            System.out.println("\n===== CONSTRUIR POBLAT =====");
            jugadorDao.construirPoblat(idJugador, idVertex);

            // 4. Millorar un poblat a ciutat
            System.out.println("\n===== MILLORAR A CIUTAT =====");
            jugadorDao.millorarACiutat(idJugador, idVertex);

            // 5. Comerciar amb la banca
            System.out.println("\n===== COMERÇ AMB LA BANCA =====");
            jugadorDao.comerciarBanca(
                    idJugador,
                    TipusRecurs.FUSTA,
                    TipusRecurs.MAO
            );

            // 6. Calcular els punts del jugador
            System.out.println("\n===== PUNTS DEL JUGADOR =====");
            int punts = jugadorDao.calcularPunts(idJugador);
            System.out.println("Punts de victòria: " + punts);

            // 7. Passar el torn
            System.out.println("\n===== PASSAR TORN =====");
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