package catan;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("CIUTAT")
public class Ciutat extends Assentament {

}
