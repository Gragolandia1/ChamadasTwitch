package com.chamada.live.domain.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario getReferenceByNome(String nome);

    Usuario findByNome(String nome);
}
