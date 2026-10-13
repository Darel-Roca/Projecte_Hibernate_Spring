package DAOcatan;

import java.util.List;

import catan.*;

public interface IJugadorDao extends IGenericDao<Jugador, Integer> {
	
	List<RecursJugador> mostrarRecursos(int idJugador);
	
	void construirCarretera(int idJugador, int idAresta);
	
	void construirPoblat(int idJugador, int idVertex);
	
	void millorarACiutat(int idJugador, int idVertex);
	
	void comerciarBanca(int idJugador, TipusRecurs dona, TipusRecurs rep);
	
	int calcularPunts(int idJugador);
	
	void passarTorn(int idJugador);
	
}
