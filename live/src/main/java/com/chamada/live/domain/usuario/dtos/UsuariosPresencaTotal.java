package com.chamada.live.domain.usuario.dtos;

import com.chamada.live.domain.usuario.Usuario;

public record UsuariosPresencaTotal(Long id, String nome, Long totalPresencas) {
    public UsuariosPresencaTotal(Usuario usuario, Long totalPresencas) {
        this(usuario.getId(), usuario.getNome(), totalPresencas);
    }
}
