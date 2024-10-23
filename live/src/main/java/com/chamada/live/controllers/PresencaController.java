package com.chamada.live.controllers;

import com.chamada.live.domain.chamada.Chamada;
import com.chamada.live.domain.service.ChamadaService;
import com.chamada.live.domain.usuario.Usuario;
import com.chamada.live.domain.service.UsuarioService;
import com.chamada.live.domain.presenca.dtos.RegistroPresenca;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/presencas")
public class PresencaController {

    @Autowired
    private ChamadaService chamadaService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<?> registrarPresenca(@RequestBody RegistroPresenca registroPresenca) {
        try {
            Chamada chamada = chamadaService.buscarPorId(registroPresenca.chamadaId());
            Usuario usuario = usuarioService.buscarPorId(registroPresenca.usuarioId());

            if (chamada != null && chamada.isEmAndamento()) {
                chamada.adicionarUsuario(usuario);
                chamadaService.salvar(chamada);
                return ResponseEntity.ok("Presença registrada com sucesso.");
            } else {
                return ResponseEntity.badRequest().body("Chamada não encontrada ou já encerrada.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao registrar presença.");
        }
    }
}
