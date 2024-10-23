package com.chamada.live.domain.usuario.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"usuariosCadastrados", "usuarios"})
public record DadosTotalUsuarios(
        List<DadosListagemUsuario> usuarios,
        long usuariosCadastrados) {
}
