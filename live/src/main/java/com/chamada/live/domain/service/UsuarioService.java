package com.chamada.live.domain.service;

import com.chamada.live.domain.usuario.Usuario;
import com.chamada.live.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario buscarPorId(Long id)
    {
        return usuarioRepository.findById(id).orElse(null);
    }
}
