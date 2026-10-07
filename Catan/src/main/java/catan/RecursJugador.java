package catan;

import java.util.Objects;

import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "recursJugador")
public class RecursJugador {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "jugador_id", nullable = false)
	private Jugador jugador;
	
	@NotNull(message = "El tipus és obligatori")
	@Enumerated(EnumType.STRING)
	@Column(name = "tipus", nullable = false, unique = true)
	private TipusRecurs tipus;
	
	@NotNull(message = "La quantitat és obligatoria")
	@Min(value = 0, message = "La quantitat no pot ser negativa")
	@Column(name = "quantitat", nullable = false)
	private Integer quantitat;

	public Jugador getJugador() {
		return jugador;
	}

	public void setJugador(Jugador jugador) {
		this.jugador = jugador;
	}

	public TipusRecurs getTipus() {
		return tipus;
	}

	public void setTipus(TipusRecurs tipus) {
		this.tipus = tipus;
	}

	public Integer getQuantitat() {
		return quantitat;
	}

	public void setQuantitat(Integer quantitat) {
		this.quantitat = quantitat;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, jugador, quantitat, tipus);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecursJugador other = (RecursJugador) obj;
		return Objects.equals(id, other.id) && Objects.equals(jugador, other.jugador)
				&& Objects.equals(quantitat, other.quantitat) && tipus == other.tipus;
	}

	@Override
	public String toString() {
		return "RecursJugador [id=" + id + ", jugador=" + jugador + ", tipus=" + tipus + ", quantitat=" + quantitat
				+ "]";
	}
	
}
