package catan;

import java.util.Objects;

import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "aresta")
@DynamicInsert
public class Aresta {
	
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

	@NotNull(message = "Els vertexs son obligatoris")
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "vertexA", nullable = false)
	private Integer vertexA;

	@NotNull(message = "Els vertexs son obligatoris")
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "vertexB", nullable = false)
	private Integer vertexB;
	
	@OneToOne(mappedBy = "aresta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Carretera carretera;

	public Integer getCodi() {
		return codi;
	}

	public void setCodi(Integer codi) {
		this.codi = codi;
	}

	public Partida getPartida() {
		return partida;
	}

	public void setPartida(Partida partida) {
		this.partida = partida;
	}
	
	public Integer getVertexA() {
		return vertexA;
	}

	public void setVertexA(Integer vertexA) {
		this.vertexA = vertexA;
	}

	public Integer getVertexB() {
		return vertexB;
	}

	public void setVertexB(Integer vertexB) {
		this.vertexB = vertexB;
	}

	public Integer getId() {
		return id;
	}

	public Carretera getCarretera() {
		return carretera;
	}

	public void setCarretera(Carretera carretera) {
		this.carretera = carretera;
	}

	@Override
	public int hashCode() {
		return Objects.hash(carretera, codi, id, partida, vertexA, vertexB);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Aresta other = (Aresta) obj;
		return Objects.equals(carretera, other.carretera) && Objects.equals(codi, other.codi)
				&& Objects.equals(id, other.id) && Objects.equals(partida, other.partida)
				&& Objects.equals(vertexA, other.vertexA) && Objects.equals(vertexB, other.vertexB);
	}

	@Override
	public String toString() {
		return "Aresta [id=" + id + ", partida=" + partida + ", codi=" + codi + ", vertexA=" + vertexA + ", vertexB="
				+ vertexB + ", carretera=" + carretera + "]";
	}

	

}
