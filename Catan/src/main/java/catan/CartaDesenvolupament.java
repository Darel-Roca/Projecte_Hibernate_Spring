package catan;

import java.util.Objects;

import org.hibernate.annotations.DynamicInsert;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "carta_desenvolupament")
@DynamicInsert
public class CartaDesenvolupament {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "partida_id", nullable = false)
	private Partida partida;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "estat", nullable = false, length = 15)
	private TipusCarta estat;
	
	@Column(name = "usada", nullable = false)
	private boolean usada;
	
	@NotNull(message = "El num de ronda es obligatori")
	@Min(value = 1, message = "El numero de ronda ha de ser superior a 1 o igual")
	@Column(name = "ronda_compra")
	private Integer rondaCompra;
	
	public TipusCarta getEstat() {
		return estat;
	}

	public void setEstat(TipusCarta estat) {
		this.estat = estat;
	}

	public boolean isUsada() {
		return usada;
	}

	public void setUsada(boolean usada) {
		this.usada = usada;
	}

	public Integer getRondaCompra() {
		return rondaCompra;
	}

	public void setRondaCompra(Integer rondaCompra) {
		this.rondaCompra = rondaCompra;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(estat, id, rondaCompra, Boolean.valueOf(usada));
	}
	
	public Partida getPartida() {
		return partida;
	}

	public void setPartida(Partida partida) {
		this.partida = partida;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CartaDesenvolupament other = (CartaDesenvolupament) obj;
		return estat == other.estat && Objects.equals(id, other.id) && Objects.equals(rondaCompra, other.rondaCompra)
				&& usada == other.usada;
	}

	@Override
	public String toString() {
		return "CartaDesenvolupament [id=" + id + ", estat=" + estat + ", usada=" + usada + ", rondaCompra="
				+ rondaCompra + "]";
	}
	
	
}

