package com.chamada.live.domain.chamada.dtos;

import com.chamada.live.domain.chamada.Chamada;

import java.time.LocalDateTime;

public record ListarTodasAsChamadas(
        Long id,
        LocalDateTime dataCriacao,
        LocalDateTime dataFechamento,
        int numeroUsuarios) {
    public ListarTodasAsChamadas(Chamada chamada) {
        this(
                chamada.getId(),
                chamada.getDataCriacao(),
                chamada.getDataFechamento(),
                chamada.getUsuarios().size());
    }
}
