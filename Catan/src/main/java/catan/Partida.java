package catan;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.DynamicInsert;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;


@Entity
@Table(name = "partida")
@DynamicInsert
public class Partida {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "estat", nullable = false, length = 15)
	private EstatPartida estat;
	
	@NotNull(message = "La data de inici és obligatòria")
	@PastOrPresent(message = "La data de inici no pot ser futura")
	@Column(name = "data_inici", nullable = false)
	private LocalDate dataInici;
	
	@NotNull(message = "El num de ronda es obligatori")
	@Min(value = 1, message = "El numero de ronda ha de ser superior a 1 o igual")
	@Column(name = "ronda")
	private Integer ronda;
	
	@NotNull(message = "El valor de la ultima tirada es obligatori")
	@Min(value = 1, message = "El valor de la ultima tirada ha d'estar entre 1 i 6")
	@Max(value = 6, message = "El valor de la ultima tirada ha d'estar entre 1 i 6")
	@Column(name = "ultimaTirada")
	private Integer ultimaTirada;
	
	@NotNull(message = "Els punts per guanyar son obligatoris")
	@Min(value = 0, message = "Els punts per guanyar ha d'estar entre 0 i 10")
	@Max(value = 10, message = "Els punts per guanyar ha d'estar entre 0 i 10")
	@Column(name = "punts_per_guanyar")
	private Integer puntsPerGuanyar;
	
	@Column(name = "daus_tirats", nullable = false)
	private boolean dausTirats;
	
	@Column(name = "lladre_pendent", nullable = false)
	private boolean lladrePendent;

	@OneToMany(mappedBy = "partida", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Jugador> jugadors = new ArrayList<>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "actiu_jugador_id", nullable = true)
	private Jugador actiu;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "guanyador_jugador_id", nullable = true)
	private Jugador guanyador;
	
	@OneToMany(mappedBy = "partida", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<CartaDesenvolupament> pila = new ArrayList<>();
	
	@OneToMany(mappedBy = "partida", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Hexagon> tauler = new ArrayList<>();
	
	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "lladre", nullable = true)
	private Hexagon lladre;
	
	@OneToMany(mappedBy = "partida", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Aresta> arestas = new ArrayList<>();
	
	@OneToMany(mappedBy = "partida", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Vertex> vertexs = new ArrayList<>();
	
	public Partida() {
		super();
	}

	public List<Jugador> getJugadors() {
		return jugadors;
	}

	public void setJugadors(List<Jugador> jugadors) {
		this.jugadors = jugadors;
	}

	public Jugador getActiu() {
		return actiu;
	}

	public void setActiu(Jugador actiu) {
		this.actiu = actiu;
	}

	public Jugador getGuanyador() {
		return guanyador;
	}

	public void setGuanyador(Jugador guanyador) {
		this.guanyador = guanyador;
	}

	public List<CartaDesenvolupament> getPila() {
		return pila;
	}

	public void setPila(List<CartaDesenvolupament> pila) {
		this.pila = pila;
	}

	public EstatPartida getEstat() {
		return estat;
	}

	public void setEstat(EstatPartida estat) {
		this.estat = estat;
	}

	public LocalDate getDataInici() {
		return dataInici;
	}

	public void setDataInici(LocalDate dataInici) {
		this.dataInici = dataInici;
	}

	public Integer getRonda() {
		return ronda;
	}

	public void setRonda(Integer ronda) {
		this.ronda = ronda;
	}

	public Integer getUltimaTirada() {
		return ultimaTirada;
	}

	public void setUltimaTirada(Integer ultimaTirada) {
		this.ultimaTirada = ultimaTirada;
	}

	public Integer getPuntsPerGuanyar() {
		return puntsPerGuanyar;
	}

	public void setPuntsPerGuanyar(Integer puntsPerGuanyar) {
		this.puntsPerGuanyar = puntsPerGuanyar;
	}

	public boolean isDausTirats() {
		return dausTirats;
	}

	public void setDausTirats(boolean dausTirats) {
		this.dausTirats = dausTirats;
	}

	public boolean isLladrePendent() {
		return lladrePendent;
	}

	public void setLladrePendent(boolean lladrePendent) {
		this.lladrePendent = lladrePendent;
	}

	public Integer getId() {
		return id;
	}
	
	public List<Hexagon> getTauler() {
		return tauler;
	}

	public void setTauler(List<Hexagon> tauler) {
		this.tauler = tauler;
	}

	public Hexagon getLladre() {
		return lladre;
	}

	public void setLladre(Hexagon lladre) {
		this.lladre = lladre;
	}

	public List<Aresta> getArestas() {
		return arestas;
	}

	public void setArestas(List<Aresta> arestas) {
		this.arestas = arestas;
	}

	public List<Vertex> getVertexs() {
		return vertexs;
	}

	public void setVertexs(List<Vertex> vertexs) {
		this.vertexs = vertexs;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Partida other = (Partida) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Partida [id=" + id + ", estat=" + estat + ", dataInici=" + dataInici + ", ronda=" + ronda
				+ ", ultimaTirada=" + ultimaTirada + ", puntsPerGuanyar=" + puntsPerGuanyar + ", dausTirats="
				+ dausTirats + ", lladrePendent=" + lladrePendent + ", actiu=" + actiu + ", guanyador="
				+ guanyador + "]";
	}
	
	
	
}
