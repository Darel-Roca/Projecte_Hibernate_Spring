package catan;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "hexagon")
public class Hexagon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "partida_id", nullable = false)
	private Partida partida;

	@NotNull
	@Column(name = "coordenada_q", nullable = false)
	private Integer q;

	@NotNull
	@Column(name = "coordenada_r", nullable = false)
	private Integer r;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "TipusTerreny", nullable = false, length = 15)
	private TipusTerreny terreny;

	@NotNull(message = "El número és obligatori")
	@Min(value = 0, message = "El número no pot ser negatiu")
	@Column(name = "numero", nullable = false)
	private Integer numero;

	@OneToOne(mappedBy = "hexagon", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Hexagon lladre;
	
	public Integer getQ() {
		return q;
	}

	public void setQ(Integer q) {
		this.q = q;
	}

	public Integer getR() {
		return r;
	}

	public void setR(Integer r) {
		this.r = r;
	}

	public TipusTerreny getTerreny() {
		return terreny;
	}

	public void setTerreny(TipusTerreny terreny) {
		this.terreny = terreny;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, numero, partida, q, r, terreny);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Hexagon other = (Hexagon) obj;
		return Objects.equals(id, other.id) && Objects.equals(numero, other.numero)
				&& Objects.equals(partida, other.partida) && Objects.equals(q, other.q) && Objects.equals(r, other.r)
				&& terreny == other.terreny;
	}

}
