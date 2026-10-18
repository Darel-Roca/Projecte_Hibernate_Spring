package catan;

import java.util.Objects;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class Construccio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "jugador_id", nullable = false)
	private Jugador jugador;

	public Integer getId() {
		return id;
	}

	public Jugador getJugador() {
		return jugador;
	}

	public void setJugador(Jugador jugador) {
		this.jugador = jugador;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, jugador);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Construccio other = (Construccio) obj;
		return Objects.equals(id, other.id) && Objects.equals(jugador, other.jugador);
	}

	@Override
	public String toString() {
		return "Construccio [id=" + id + ", jugador=" + jugador + "]";
	}

	public Construccio(Jugador jugador) {
		super();
		this.jugador = jugador;
	}

	public Construccio() {
		super();
	}
	
	
}
