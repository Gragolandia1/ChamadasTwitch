package com.chamada.live.domain.presenca;

import com.chamada.live.domain.chamada.Chamada;
import com.chamada.live.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "presenca")
@Table(name = "presencas")
@Getter
@Setter
public class Presenca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "chamada_id")
    private Chamada chamada;

}
