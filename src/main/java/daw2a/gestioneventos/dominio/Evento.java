package daw2a.gestioneventos.dominio;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false,unique = true)
    private String nombre;
    @Column(nullable = false)
    private String descripcion;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true,name = "organizador_id")
    private Organizador organizador;

    @Column(nullable = false)
    @Builder.Default //vscode marca warning sin esta línea
    @Enumerated(EnumType.STRING)
    private TipoEvento tipo=TipoEvento.CONGRESO;

    @Column(nullable = true)
    private LocalDateTime fechaInicio;

    @Column(nullable = true)
    private LocalDateTime fechaFin;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Column(nullable = true)
    @JsonManagedReference
    private List<Participante> participantes;

}
