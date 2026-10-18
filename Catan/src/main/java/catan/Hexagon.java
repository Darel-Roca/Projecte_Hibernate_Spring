package catan;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

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

	@NotNull(message = "El codi és obligatori")
	@Column(name = "codi", nullable = false)
	private Integer codi;
	
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

	@OneToMany(mappedBy = "hexagon", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<HexagonVertex> hexagonvertices = new ArrayList<>();

	public Integer getCodi() {
		return codi;
	}

	public void setCodi(Integer codi) {
		this.codi = codi;
	}

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

	public Partida getPartida() {
		return partida;
	}

	public void setPartida(Partida partida) {
		this.partida = partida;
	}

	public List<HexagonVertex> getHexagonvertices() {
		return hexagonvertices;
	}

	public void setHexagonvertices(List<HexagonVertex> hexagonvertices) {
		this.hexagonvertices = hexagonvertices;
	}

	@Override
	public int hashCode() {
		return Objects.hash(hexagonvertices, id, numero, partida, q, r, terreny);
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
		return Objects.equals(hexagonvertices, other.hexagonvertices) && Objects.equals(id, other.id)
				&& Objects.equals(numero, other.numero) && Objects.equals(partida, other.partida)
				&& Objects.equals(q, other.q) && Objects.equals(r, other.r) && terreny == other.terreny;
	}

	@Override
	public String toString() {
	    return "Hexagon [id=" + id + ", codi=" + codi + ", q=" + q + ", r=" + r + 
	           ", terreny=" + terreny + ", numero=" + numero + "]";
	}
	
	

}
