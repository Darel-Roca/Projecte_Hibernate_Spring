package catan;

import java.util.Objects;

import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "vertex")
@DynamicInsert
public class Vertex {
	
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
	
	@OneToOne(mappedBy = "vertex", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Assentament assentament;
	
	@OneToOne(mappedBy = "vertexA", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Aresta arestaA;
	
	@OneToOne(mappedBy = "vertexB", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Aresta arestaB;

	public Integer getCodi() {
		return codi;
	}

	public void setCodi(Integer codi) {
		this.codi = codi;
	}

	public Assentament getAssentament() {
		return assentament;
	}

	public void setAssentament(Assentament assentament) {
		this.assentament = assentament;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(codi, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		return Objects.equals(codi, other.codi) && Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Vertex [id=" + id + ", codi=" + codi + "]";
	}

	public Vertex() {
		super();
	}
	
	

}
