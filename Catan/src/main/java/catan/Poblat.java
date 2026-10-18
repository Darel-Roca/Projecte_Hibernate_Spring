package catan;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("POBLAT")
public class Poblat extends Assentament {

	public Poblat(Jugador jugador, @NotNull Vertex vertex) {
		super(jugador, vertex);
	}

	public Poblat() {
		super();
	}
	
}
