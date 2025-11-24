package daw2a.gestioneventos.dominio;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "sequence_participante", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    @Size(min=6,max=15)
    private String usuario;

    @Column(nullable = false)
    @JsonIgnore
    private String contrasenia;

    @ManyToOne
    @JoinColumn(nullable = false,name= "evento_id")
    @JsonBackReference
    private Evento evento;

}
