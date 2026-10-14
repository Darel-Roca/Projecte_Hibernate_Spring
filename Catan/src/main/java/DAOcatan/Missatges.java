package DAOcatan;

public final class Missatges {
	
	private Missatges() {
	}
	
	// Correctes
	public static final String PARTIDA_AFEGIDA = "Partida afegida";

	// Errors de dades
	public static final String JUGADOR_NO_EXISTEIX = "No existeix el jugador";
	public static final String JUGADOR_NO_TORN = "No és el torn del jugador";
	public static final String ARESTA_NO_EXISTEIX = "No existeix la aresta";
	public static final String ARESTA_NO_PERTANY = "La aresta no pertany a la partida";
	public static final String ARESTA_PLENA = "La aresta ja conté una carretera";
	public static final String ARESTA_NO_CONECTADA = "La aresta no està conectada";
	public static final String JUGADOR_NO_RECURS = "El jugador no té els recursos necessaris";
	public static final String JUGADOR_MAX_CARRETERA = "El jugador té el màxim de carreteres";

	// Error inesperat (excepció): s'ha fet rollback
	public static final String ERROR_BD = "Error de base de dades: no s'ha fet cap canvi";

}
