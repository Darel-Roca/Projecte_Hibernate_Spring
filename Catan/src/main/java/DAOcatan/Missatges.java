package DAOcatan;

public final class Missatges {
	
	private Missatges() {
	}
	
	// Correctes
	public static final String PARTIDA_AFEGIDA = "Partida afegida";

	// Errors de dades
	public static final String JUGADOR_NO_EXISTEIX = "No existeix el jugador";

	// Error inesperat (excepció): s'ha fet rollback
	public static final String ERROR_BD = "Error de base de dades: no s'ha fet cap canvi";

}
