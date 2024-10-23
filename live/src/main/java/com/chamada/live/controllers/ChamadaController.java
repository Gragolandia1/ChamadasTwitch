package com.chamada.live.controllers;

import com.chamada.live.domain.chamada.Chamada;
import com.chamada.live.domain.chamada.ChamadaRepository;
import com.chamada.live.domain.presenca.PresencaRepository;
import com.chamada.live.domain.service.ChamadaService;
import com.chamada.live.domain.chamada.dtos.ListarChamadaID;
import com.chamada.live.domain.chamada.dtos.ListarTodasAsChamadas;
import com.chamada.live.domain.usuario.dtos.UsuariosPresencaTotal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/chamadas")
public class ChamadaController {

    @Autowired
    private ChamadaService chamadaService;

    @Autowired
    ChamadaRepository chamadaRepository;

    @Autowired
    private PresencaRepository presencaRepository;

    @PostMapping
    public ResponseEntity<String> alternarEstadoDaChamada() {
        Chamada chamada = chamadaService.alternarEstadoChamada();

        if (!chamada.isEmAndamento()) {
            return ResponseEntity.ok("Chamada fechada com sucesso.");
        } else {
            return ResponseEntity.ok("Nova chamada iniciada com sucesso.");
        }
    }

    @GetMapping("/ativa")
    public ResponseEntity<Boolean> isChamadaAtiva() {
        boolean chamadaAtiva = chamadaService.isChamadaAtiva();
        return ResponseEntity.ok(chamadaAtiva);
    }

    @GetMapping("/id-ativa")
    public ResponseEntity<Long> pegarIdChamadaAtiva() {
        Long chamadaAtivaId = chamadaService.getIdChamadaAtiva();
        if (chamadaAtivaId != null) {
            return ResponseEntity.ok(chamadaAtivaId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ListarTodasAsChamadas>> pegarTodasAsChamadas() {
        var chamadas = chamadaRepository.findAll();

        var chamadasDTO = chamadas.stream()
                .map(ListarTodasAsChamadas::new)
                .toList();

        return ResponseEntity.ok(chamadasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListarChamadaID> buscarChamadaPorId(@PathVariable Long id) {
        var chamada = chamadaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chamada não encontrada"));

        return ResponseEntity.ok(new ListarChamadaID(chamada));
    }

    @GetMapping("/{chamadaId}/usuarios/{usuarioId}")
    public ResponseEntity<Void> verificarPresenca(@PathVariable Long chamadaId, @PathVariable Long usuarioId) {
        boolean usuarioPresente = presencaRepository.existsByChamadaIdAndUsuarioId(chamadaId, usuarioId);
        if (usuarioPresente) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuarios-ordenados-presencas")
    public ResponseEntity<List<UsuariosPresencaTotal>> listarUsuariosPorPresencas(@RequestParam(value = "nome", required = false) String nome) {
        List<UsuariosPresencaTotal> usuariosPorPresencas = chamadaService.listarUsuariosPorPresencas(nome);
        return ResponseEntity.ok(usuariosPorPresencas);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarChamada(@PathVariable Long id) {
        boolean deletado = chamadaService.deletarChamada(id);
        if (deletado) {
            return ResponseEntity.ok("Chamada deletada com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chamada não encontrada.");
        }
    }

}
