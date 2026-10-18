package catan;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("CIUTAT")
public class Ciutat extends Assentament {

	public Ciutat(Jugador jugador, @NotNull Vertex vertex) {
		super(jugador, vertex);
	}

	public Ciutat() {
		super();
	}
	
}
