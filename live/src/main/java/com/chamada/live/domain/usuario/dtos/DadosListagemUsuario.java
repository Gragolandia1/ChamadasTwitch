package com.chamada.live.domain.usuario.dtos;

import com.chamada.live.domain.usuario.Usuario;

import java.time.LocalDateTime;

public record DadosListagemUsuario(
        Long id,
        String nome,
        LocalDateTime dataCriacao) {

    public DadosListagemUsuario(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getDataCriacao());
    }
}
