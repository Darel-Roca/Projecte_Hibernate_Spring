package catan;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("POBLAT")
public class Poblat extends Assentament {

}
