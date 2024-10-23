package com.chamada.live.domain.usuario;


import com.chamada.live.domain.chamada.Chamada;
import com.chamada.live.domain.usuario.dtos.DadosAtualizarUsuario;
import com.chamada.live.domain.usuario.dtos.DadosCadastroUsuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Usuario")
@Table(name = "Usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private LocalDateTime dataCriacao;

    @ManyToMany(mappedBy = "usuarios")
    private List<Chamada> chamadas = new ArrayList<>();

    public Usuario(DadosCadastroUsuario dados) {
        this.nome = dados.nome();
    }

    @PrePersist
    protected void aoCriar() {
        dataCriacao = LocalDateTime.now();
    }

    public void atualizarInformaçoesUsuario(DadosAtualizarUsuario dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
    }
}
