package DAOcatan;

public final class Missatges {
	
	private Missatges() {
	}
	
	// Correctes
	public static final String PARTIDA_AFEGIDA = "Partida afegida";
	
	// Objectes no existents
	public static final String JUGADOR_NO_EXISTEIX = "No existeix el jugador";
	public static final String ARESTA_NO_EXISTEIX = "No existeix la aresta";
	public static final String VERTEX_NO_EXISTEIX = "No existeix el vèrtex";
	
	// Objectes ocupats
	public static final String ARESTA_PLENA = "La aresta ja conté una carretera";
	public static final String VERTEX_PLE = "El vèrtex ja conté un assentament";
	
	// Objectes inconexos
	public static final String ARESTA_NO_CONECTADA = "La aresta no està conectada";
	public static final String VERTEX_NO_CONECTAT = "El vèrtex no està conectat";
	
	// Límit superat
	public static final String JUGADOR_MAX_CARRETERA = "El jugador té el màxim de carreteres";
	public static final String JUGADOR_MAX_POBLAT = "El jugador té el màxim de poblats";
	public static final String JUGADOR_MAX_CIUTAT = "El jugador té el màxim de ciutats";
	
	// Errors específics al passar torn
	public static final String DAUS_NO_TIRATS = "El jugador encara no ha tirat els daus";
	public static final String PREPARACIO_NO_ACABADA = "El jugador encara no ha col·locat el poblat i la carretera";
	public static final String LLADRE_PENDENT = "El lladre encara està pendent";

	// Errors de dades
	public static final String JUGADOR_NO_TORN = "No és el torn del jugador";
	public static final String ARESTA_NO_PARTIDA = "La aresta no pertany a la partida";
	public static final String JUGADOR_NO_RECURS = "El jugador no té els recursos necessaris";
	public static final String VERTEX_POBLAT_ADJACENT = "El vèrtex és adjacent a un poblat";
	public static final String VERTEX_NO_POBLAT = "El vèrtex no conté un poblat";
	public static final String POBLAT_NO_JUGADOR = "El poblat no pertany al jugador";
	public static final String COMERC_MATEIX_RECURS = "No es pot donar i rebre el mateix recurs";
	public static final String JUGADOR_NO_ACTIU = "El jugador no està actiu";

	// Error inesperat (excepció): s'ha fet rollback
	public static final String ERROR_BD = "Error de base de dades: no s'ha fet cap canvi";

}
