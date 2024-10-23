package com.chamada.live.domain.chamada;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChamadaRepository extends JpaRepository<Chamada, Long> {

    Optional<Chamada> findByEmAndamentoTrue();

    boolean existsByDataFechamentoIsNull();

    Optional<Chamada> findByDataFechamentoIsNull();
}
