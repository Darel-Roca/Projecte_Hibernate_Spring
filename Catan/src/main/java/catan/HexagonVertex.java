package catan;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "hexagon_vertex", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "hexagon_id", "vertex_id" })
})
public class HexagonVertex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hexagon_id", nullable = false)
    private Hexagon hexagon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vertex_id", nullable = false)
    private Vertex vertex; 

    @Column(nullable = false)
    private Integer posicio;

    public HexagonVertex() {}


    public HexagonVertex(Hexagon hexagon, Vertex vertex, Integer posicio) {
        this.hexagon = hexagon;
        this.vertex = vertex;
        this.posicio = posicio;
    }

    // Getters i Setters
    public Integer getId() {
        return id;
    }

    public Hexagon getHexagon() {
        return hexagon;
    }

    public void setHexagon(Hexagon hexagon) {
        this.hexagon = hexagon;
    }

    public Vertex getVertex() {
        return vertex;
    }

    public void setVertex(Vertex vertex) {
        this.vertex = vertex;
    }

    public Integer getPosicio() {
        return posicio;
    }

    public void setPosicio(Integer posicio) {
        this.posicio = posicio;
    }

    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HexagonVertex that = (HexagonVertex) o;
        return Objects.equals(hexagon, that.hexagon) && 
               Objects.equals(vertex, that.vertex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hexagon, vertex);
    }

    @Override
    public String toString() {
        return "HexagonVertex [id=" + id + ", posicio=" + posicio + "]";
    }
}
