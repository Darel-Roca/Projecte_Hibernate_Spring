package DAOcatan;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.hibernate.Session;
import org.hibernate.Transaction;
import catan.*;

/** Proves d'integracio: creen dades reals i verifiquen la persistencia. */
public class Proves {
    private static int correctes = 0;
    private static int fallides = 0;

    private static List<String> nomsProva(String... noms) {
        String sufix = "_" + UUID.randomUUID().toString().replace("-", "");
        return Arrays.stream(noms).map(nom -> nom + sufix).toList();
    }

    private static void comprovar(boolean condicio, String missatge) {
        if (!condicio) throw new AssertionError(missatge);
    }

    private static void executar(String nom, Runnable prova) {
        try {
            prova.run();
            correctes++;
            System.out.println("[OK] " + nom);
        } catch (Throwable e) {
            fallides++;
            System.err.println("[ERROR] " + nom + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void provarCreacioPartida() {
        int id = new PartidaDao().iniciarPartida(nomsProva("Prova_A", "Prova_B", "Prova_C"));
        try (Session s = SessionManager.getSessionFactory().openSession()) {
            Partida p = s.get(Partida.class, id);
            comprovar(p != null, "La partida no existeix a la BD");
            comprovar(p.getRonda() == 1, "La ronda inicial no es 1");
            comprovar(p.getEstat() == EstatPartida.PREPARACIO, "Estat inicial incorrecte");
            comprovar(p.getPuntsPerGuanyar() == 10, "Punts per guanyar incorrectes");
        }
    }

    public static void provarJugadorsIRecursos() {
        int id = new PartidaDao().iniciarPartida(nomsProva("Test_1", "Test_2", "Test_3"));
        try (Session s = SessionManager.getSessionFactory().openSession()) {
            List<Jugador> jugadors = s.createQuery(
                "from Jugador j where j.partida.id = :id", Jugador.class)
                .setParameter("id", id).getResultList();
            comprovar(jugadors.size() == 3, "No s'han desat els 3 jugadors");
            for (Jugador j : jugadors) {
                Long n = s.createQuery("select count(r) from RecursJugador r where r.jugador.id = :id", Long.class)
                    .setParameter("id", j.getId()).getSingleResult();
                comprovar(n == TipusRecurs.values().length, "Inventari incomplet: " + j.getNom());
            }
        }
    }

    public static void provarActualitzacioRonda() {
        int id = new PartidaDao().iniciarPartida(nomsProva("Ronda_A", "Ronda_B", "Ronda_C"));
        try (Session s = SessionManager.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            try {
                Partida p = s.get(Partida.class, id);
                p.setRonda(4);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
        try (Session s = SessionManager.getSessionFactory().openSession()) {
            comprovar(s.get(Partida.class, id).getRonda() == 4,
                "La ronda actualitzada no s'ha persistit");
        }
    }

    public static void provarTauler() {
        // La segona partida ha de funcionar encara que els IDs ja no coincideixin amb els codis CSV.
        new PartidaDao().iniciarPartida(nomsProva("Mapa_Previ_A", "Mapa_Previ_B"));
        int id = new PartidaDao().iniciarPartida(nomsProva("Mapa_A", "Mapa_B", "Mapa_C"));
        try (Session s = SessionManager.getSessionFactory().openSession()) {
            Long hexagons = s.createQuery("select count(h) from Hexagon h where h.partida.id = :id", Long.class)
                .setParameter("id", id).getSingleResult();
            comprovar(hexagons > 0, "No s'han inserit hexagons");
            comprovar(s.get(Partida.class, id).getLladre() != null, "No hi ha lladre inicial");
            Long enllacos = s.createQuery(
                "select count(hv) from HexagonVertex hv where hv.hexagon.partida.id = :id", Long.class)
                .setParameter("id", id).getSingleResult();
            comprovar(enllacos == hexagons * 6, "Cada hexagon ha de tenir sis vertexs");
            Long vertexsAliens = s.createQuery(
                "select count(hv) from HexagonVertex hv where hv.hexagon.partida.id = :id " +
                "and hv.vertex.partida.id <> :id", Long.class)
                .setParameter("id", id).getSingleResult();
            comprovar(vertexsAliens == 0, "Hi ha enllacos amb vertexs d'una altra partida");
            Long arestes = s.createQuery(
                "select count(a) from Aresta a where a.partida.id = :id", Long.class)
                .setParameter("id", id).getSingleResult();
            comprovar(arestes > 0, "No s'han inserit arestes");
            Long arestesAlienes = s.createQuery(
                "select count(a) from Aresta a where a.partida.id = :id " +
                "and (a.vertexA.partida.id <> :id or a.vertexB.partida.id <> :id)", Long.class)
                .setParameter("id", id).getSingleResult();
            comprovar(arestesAlienes == 0, "Hi ha arestes amb vertexs d'una altra partida");
        }
    }

    /** Retorna el primer jugador de la partida, sense dependre de l'ordre de la consulta. */
    private static int primerJugador(int idPartida) {
        try (Session s = SessionManager.getSessionFactory().openSession()) {
            return s.createQuery("select j.id from Jugador j where j.partida.id = :id order by j.id", Integer.class)
                .setParameter("id", idPartida).setMaxResults(1).getSingleResult();
        }
    }

    private static int quantitat(int idJugador, TipusRecurs tipus) {
        try (Session s = SessionManager.getSessionFactory().openSession()) {
            return s.createQuery("select r.quantitat from RecursJugador r where r.jugador.id = :id and r.tipus = :tipus", Integer.class)
                .setParameter("id", idJugador).setParameter("tipus", tipus).getSingleResult();
        }
    }

    private static void prepararJugadorActiu(int idPartida, int idJugador) {
        try (Session s = SessionManager.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            try {
                Partida p = s.get(Partida.class, idPartida);
                Jugador j = s.get(Jugador.class, idJugador);
                p.setActiu(j);
                j.setActiu(true);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    /** Comprova que el DAO retorna els recursos persistits. */
    public static void provarConsultaRecursos() {
        int partida = new PartidaDao().iniciarPartida(nomsProva("Consulta_A", "Consulta_B", "Consulta_C"));
        int jugador = primerJugador(partida);
        List<RecursJugador> recursos = new JugadorDao().mostrarRecursos(jugador);
        comprovar(recursos != null, "La consulta de recursos ha retornat null");
        comprovar(recursos.size() == TipusRecurs.values().length, "Nombre de recursos incorrecte");
        for (RecursJugador r : recursos) {
            comprovar(r.getQuantitat() >= 0, "Quantitat negativa de " + r.getTipus());
        }
    }

    /** Insereix recursos, comercia amb la banca i comprova el canvi a la BD. */
    public static void provarComercBanca() {
        int partida = new PartidaDao().iniciarPartida(nomsProva("Banca_A", "Banca_B", "Banca_C"));
        int jugador = primerJugador(partida);
        prepararJugadorActiu(partida, jugador);
        try (Session s = SessionManager.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            try {
                Jugador j = s.get(Jugador.class, jugador);
                for (RecursJugador r : j.getInventari()) {
                    if (r.getTipus() == TipusRecurs.FUSTA) r.setQuantitat(8);
                    if (r.getTipus() == TipusRecurs.BLAT) r.setQuantitat(2);
                }
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
        new JugadorDao().comerciarBanca(jugador, TipusRecurs.FUSTA, TipusRecurs.BLAT);
        comprovar(quantitat(jugador, TipusRecurs.FUSTA) == 4, "No s'han descomptat les 4 fustes");
        comprovar(quantitat(jugador, TipusRecurs.BLAT) == 3, "No s'ha afegit 1 blat");
    }

    /** Un intercanvi del mateix recurs no ha de modificar l'inventari. */
    public static void provarComercInvalid() {
        int partida = new PartidaDao().iniciarPartida(nomsProva("Invalid_A", "Invalid_B", "Invalid_C"));
        int jugador = primerJugador(partida);
        prepararJugadorActiu(partida, jugador);
        int abans = quantitat(jugador, TipusRecurs.FUSTA);
        new JugadorDao().comerciarBanca(jugador, TipusRecurs.FUSTA, TipusRecurs.FUSTA);
        comprovar(quantitat(jugador, TipusRecurs.FUSTA) == abans,
            "El comerc invalid ha modificat els recursos");
    }

    /** Sense construccions ni cartes de victoria, la puntuacio ha de ser zero. */
    public static void provarPuntsInicials() {
        int partida = new PartidaDao().iniciarPartida(nomsProva("Punts_A", "Punts_B", "Punts_C"));
        int jugador = primerJugador(partida);
        comprovar(new JugadorDao().calcularPunts(jugador) == 0,
            "Un jugador nou no hauria de tenir punts");
    }

    /** En preparacio no es pot passar torn sense construir. */
    public static void provarTornBloquejat() {
        int partida = new PartidaDao().iniciarPartida(nomsProva("Torn_A", "Torn_B", "Torn_C"));
        int jugador = primerJugador(partida);
        prepararJugadorActiu(partida, jugador);
        new JugadorDao().passarTorn(jugador);
        try (Session s = SessionManager.getSessionFactory().openSession()) {
            Partida p = s.get(Partida.class, partida);
            comprovar(p.getRonda() == 1, "Ha avancat la ronda sense construccions");
            comprovar(p.getActiu().getId() == jugador, "Ha canviat el jugador actiu sense construccions");
        }
    }

    /** Una aresta inexistent no ha de generar una carretera. */
    public static void provarCarreteraInexistent() {
        int partida = new PartidaDao().iniciarPartida(nomsProva("Via_A", "Via_B", "Via_C"));
        int jugador = primerJugador(partida);
        prepararJugadorActiu(partida, jugador);
        new JugadorDao().construirCarretera(jugador, -1);
        try (Session s = SessionManager.getSessionFactory().openSession()) {
            Long n = s.createQuery("select count(c) from Carretera c where c.jugador.id = :id", Long.class)
                .setParameter("id", jugador).getSingleResult();
            comprovar(n == 0, "S'ha creat una carretera amb aresta inexistent");
        }
    }

    /** Un vertex inexistent no ha de generar un poblat. */
    public static void provarPoblatInexistent() {
        int partida = new PartidaDao().iniciarPartida(nomsProva("Poblat_A", "Poblat_B", "Poblat_C"));
        int jugador = primerJugador(partida);
        prepararJugadorActiu(partida, jugador);
        new JugadorDao().construirPoblat(jugador, -1);
        try (Session s = SessionManager.getSessionFactory().openSession()) {
            Long n = s.createQuery("select count(p) from Poblat p where p.jugador.id = :id", Long.class)
                .setParameter("id", jugador).getSingleResult();
            comprovar(n == 0, "S'ha creat un poblat amb vertex inexistent");
        }
    }

    public static void executarTotes() {
        correctes = 0;
        fallides = 0;
        executar("Creacio i persistencia de partida", Proves::provarCreacioPartida);
        executar("Jugadors i inventaris", Proves::provarJugadorsIRecursos);
        executar("Actualitzacio de ronda", Proves::provarActualitzacioRonda);
        executar("Generacio i persistencia del tauler", Proves::provarTauler);
        executar("Consulta de recursos", Proves::provarConsultaRecursos);
        executar("Comerc valid amb la banca", Proves::provarComercBanca);
        executar("Comerc invalid sense canvis", Proves::provarComercInvalid);
        executar("Puntuacio inicial", Proves::provarPuntsInicials);
        executar("Torn bloquejat en preparacio", Proves::provarTornBloquejat);
        executar("Carretera amb aresta inexistent", Proves::provarCarreteraInexistent);
        executar("Poblat amb vertex inexistent", Proves::provarPoblatInexistent);
        System.out.println("RESULTAT: " + correctes + " correctes, " + fallides + " fallides");
        if (fallides > 0) throw new AssertionError("Hi ha proves fallides");
    }
}
