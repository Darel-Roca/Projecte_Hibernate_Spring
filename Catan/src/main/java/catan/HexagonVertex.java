package catan;

import java.util.Objects;

import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "hexagon_vertex")
@DynamicInsert
public class HexagonVertex {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@NotNull(message = "La posició del vèrtex és obligatòria")
	@Min(value = 0, message = "La posició del vèrtex ha d'estar entre 0 i 5")
	@Max(value = 5, message = "La posició del vèrtex ha d'estar entre 0 i 5")
	@Column(name = "posicio", nullable = false)
	private Integer posicio;

	public Integer getPosicio() {
		return posicio;
	}

	public void setPosicio(Integer posicio) {
		this.posicio = posicio;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public String toString() {
		return "HexagonVertex [id=" + id + ", posicio=" + posicio + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, posicio);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HexagonVertex other = (HexagonVertex) obj;
		return Objects.equals(id, other.id) && Objects.equals(posicio, other.posicio);
	}

}
