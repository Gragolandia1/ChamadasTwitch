package com.chamada.live.controllers;

import com.chamada.live.domain.usuario.Usuario;
import com.chamada.live.domain.usuario.UsuarioRepository;
import com.chamada.live.domain.usuario.dtos.DadosAtualizarUsuario;
import com.chamada.live.domain.usuario.dtos.DadosCadastroUsuario;
import com.chamada.live.domain.usuario.dtos.DadosListagemUsuario;
import com.chamada.live.domain.usuario.dtos.DadosTotalUsuarios;
import com.chamada.live.domain.presenca.PresencaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;
    @Autowired
    PresencaRepository presencaRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<DadosListagemUsuario> cadastrarUsuarios(@RequestBody DadosCadastroUsuario dados, UriComponentsBuilder uriBuilder) {

        var usuario = new Usuario(dados);
        usuarioRepository.save(usuario);

        var uri = uriBuilder.path("/chamadas/{id}").buildAndExpand(usuario.getId()).toUri();

        return ResponseEntity.created(uri).body(new DadosListagemUsuario(usuario));
    }

    @GetMapping
    public ResponseEntity<DadosTotalUsuarios> listarUsuarios(@RequestParam(value = "nome", required = false) String nome) {

        List<DadosListagemUsuario> usuarios = usuarioRepository.findAll().stream().filter(usuario -> nome == null || nome.equals(usuario.getNome()))
                .map(DadosListagemUsuario::new)
                .toList();

        long totalUsuarios = usuarios.size();

        return ResponseEntity.ok(new DadosTotalUsuarios(usuarios, totalUsuarios));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Long> buscarUsuario(@RequestParam String nome) {
        Usuario usuario = usuarioRepository.findByNome(nome);
        if (usuario != null) {
            return ResponseEntity.ok(usuario.getId());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping
    @Transactional
    public ResponseEntity<DadosListagemUsuario> atualizarUsuario(@RequestBody DadosAtualizarUsuario dados) {

        var usuario = usuarioRepository.getReferenceByNome(dados.nome());
        usuario.atualizarInformaçoesUsuario(dados);

        return ResponseEntity.ok(new DadosListagemUsuario(usuario));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        var usuario = usuarioRepository.findById(id);

        if (usuario.isPresent()) {
            presencaRepository.deleteByUsuarioId(id);
            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}

