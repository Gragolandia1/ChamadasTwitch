package com.chamada.live.domain.chamada.dtos;

import com.chamada.live.domain.chamada.Chamada;
import com.chamada.live.domain.usuario.dtos.DadosListagemUsuario;

import java.time.LocalDateTime;
import java.util.List;

public record ListarChamadaID(
        Long id,
        LocalDateTime dataCriacao,
        LocalDateTime dataFechamento,
        int numeroUsuarios,
        List<DadosListagemUsuario> usuarios) {
    public ListarChamadaID(Chamada chamada) {
        this(
                chamada.getId(),
                chamada.getDataCriacao(),
                chamada.getDataFechamento(),
                chamada.getUsuarios().size(),
                chamada.getUsuarios().stream().map(DadosListagemUsuario::new).toList());
    }
}
