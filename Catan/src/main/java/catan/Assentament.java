package catan;

import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "assentament")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipus", discriminatorType = DiscriminatorType.STRING, length = 10)
public abstract class Assentament extends Construccio {

	@NotNull
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "vertex_id", nullable = false, unique = true)
	private Vertex vertex;

	public Vertex getVertex() {
		return vertex;
	}

	public void setVertex(Vertex vertex) {
		this.vertex = vertex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(vertex);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Assentament other = (Assentament) obj;
		return Objects.equals(vertex, other.vertex);
	}

	@Override
	public String toString() {
		return "Assentament [vertex=" + vertex + "]";
	}

	public Assentament(Jugador jugador, @NotNull Vertex vertex) {
		super(jugador);
		this.vertex = vertex;
	}

	public Assentament() {
		super();
	}
	
	
	
}
