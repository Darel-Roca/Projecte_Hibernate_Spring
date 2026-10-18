package DAOcatan;

import java.util.List;

import catan.Hexagon;
import catan.Partida;

public interface IPartidaDao extends IGenericDao<Partida, Integer> {

	int iniciarPartida(List<String> nomsJugadors);
	
	List<Hexagon> mostraTauler(int idPartida);
	
	void mostraPartida(int idPartida);
	
	void tirarDaus(int idPartida);
	
	void comprovarVictoria(int idPartida);
	
}
