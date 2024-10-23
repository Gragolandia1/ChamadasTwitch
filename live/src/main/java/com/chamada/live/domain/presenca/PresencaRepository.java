package com.chamada.live.domain.presenca;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PresencaRepository extends JpaRepository<Presenca, Long> {
    void deleteByUsuarioId(Long usuarioId);

    boolean existsByChamadaIdAndUsuarioId(Long chamadaId, Long usuarioId);

    @Query("SELECT p.usuario, COUNT(p) as total FROM presenca p GROUP BY p.usuario ORDER BY total DESC")
    List<Object[]> findUsuariosOrdenadosPorPresencas();
}
