package catan;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "jugador")
public class Jugador {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "partida_id", nullable = false)
	private Partida partida;
	
	@NotBlank(message = "El nom és obligatori")
	@Size(max = 50, message = "El nom no pot superar els {max} caràcters")
	@Column(name = "nom", nullable = false, unique = true, length = 50)
	private String nom;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "estat", nullable = false, length = 15)
	private ColorJugador color;
	
	@NotNull(message = "El ordre de torn es obligatori")
	@Min(value = 1, message = "El ordre de torn ha d'estar entre 1 o 4")
	@Max(value = 4, message = "El ordre de torn ha d'estar entre 1 o 4")
	@Column(name = "ordre_torn")
	private Integer ordreTorn;
	
	@NotNull(message = "El ordre de torn es obligatori")
	@Min(value = 0, message = "Els cavallers jugats tenen que ser 0 o mes d'1")
	@Column(name = "cavallers_jugats")
	private Integer cavallersJugats;
	
	@OneToOne(mappedBy = "jugador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private int actiu;
	
	@OneToOne(mappedBy = "jugador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private int guanyador;
	
	@OneToMany(mappedBy = "jugador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<CartaDesenvolupament> ma = new ArrayList<>();

	@OneToMany(mappedBy = "jugador", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@Size(max=5)
	private List<Construccio> propietari = new ArrayList<>();
	
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public ColorJugador getColor() {
		return color;
	}

	public void setColor(ColorJugador color) {
		this.color = color;
	}

	public Integer getOrdreTorn() {
		return ordreTorn;
	}

	public void setOrdreTorn(Integer ordreTorn) {
		this.ordreTorn = ordreTorn;
	}

	public Integer getCavallersJugats() {
		return cavallersJugats;
	}

	public void setCavallersJugats(Integer cavallersJugats) {
		this.cavallersJugats = cavallersJugats;
	}

	public int getActiu() {
		return actiu;
	}

	public void setActiu(int actiu) {
		this.actiu = actiu;
	}

	public int getGuanyador() {
		return guanyador;
	}

	public void setGuanyador(int guanyador) {
		this.guanyador = guanyador;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cavallersJugats, color, id, nom, ordreTorn);
	}
	
	public Partida getPartida() {
		return partida;
	}

	public void setPartida(Partida partida) {
		this.partida = partida;
	}

	public List<CartaDesenvolupament> getMa() {
		return ma;
	}

	public void setMa(List<CartaDesenvolupament> ma) {
		this.ma = ma;
	}

	public List<Construccio> getPropietari() {
		return propietari;
	}

	public void setPropietari(List<Construccio> propietari) {
		this.propietari = propietari;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Jugador other = (Jugador) obj;
		return Objects.equals(cavallersJugats, other.cavallersJugats) && color == other.color
				&& Objects.equals(id, other.id) && Objects.equals(nom, other.nom)
				&& Objects.equals(ordreTorn, other.ordreTorn);
	}

	@Override
	public String toString() {
		return "Jugador [id=" + id + ", nom=" + nom + ", color=" + color + ", ordreTorn=" + ordreTorn
				+ ", cavallersJugats=" + cavallersJugats + "]";
	}
	
	
	
}
