package com.chamada.live.domain.chamada;

import com.chamada.live.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Chamada")
@Table(name = "Chamadas")
@Getter
@Setter
@AllArgsConstructor
public class Chamada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataFechamento;

    private boolean emAndamento;

    @ManyToMany
    @JoinTable(
            name = "presencas",
            joinColumns = @JoinColumn(name = "chamada_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> usuarios = new ArrayList<>();

    public Chamada() {
        this.dataCriacao = LocalDateTime.now();
        this.emAndamento = true;
    }

    public void adicionarUsuario(Usuario usuario) {
        if (!usuarios.contains(usuario)) {
            usuarios.add(usuario);
            usuario.getChamadas().add(this);
        }
    }

}
