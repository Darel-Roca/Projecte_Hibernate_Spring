package catan;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "assentament")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipus", discriminatorType = DiscriminatorType.STRING, length = 10)
public abstract class Assentament extends Construccio {
	
	@NotNull
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "vertex_id", nullable = false, unique = true)
	private Vertex vertex;
	
}
