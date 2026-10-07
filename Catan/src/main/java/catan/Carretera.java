package catan;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "carretera")
public class Carretera extends Construccio {

	@NotNull
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "aresta_id", nullable = false, unique = true)
	private Aresta aresta;
	
}
