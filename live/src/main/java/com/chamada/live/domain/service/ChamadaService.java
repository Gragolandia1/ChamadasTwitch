package com.chamada.live.domain.service;

import com.chamada.live.domain.chamada.Chamada;
import com.chamada.live.domain.chamada.ChamadaRepository;
import com.chamada.live.domain.presenca.PresencaRepository;
import com.chamada.live.domain.usuario.Usuario;
import com.chamada.live.domain.usuario.dtos.UsuariosPresencaTotal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChamadaService {

    @Autowired
    private ChamadaRepository chamadaRepository;

    @Autowired
    private PresencaRepository presencaRepository;

    public boolean isChamadaAtiva() {
        return chamadaRepository.existsByDataFechamentoIsNull();
    }

    public Long getIdChamadaAtiva() {
        return chamadaRepository.findByDataFechamentoIsNull()
                .map(Chamada::getId)
                .orElse(null);
    }

    public Chamada alternarEstadoChamada() {
        Optional<Chamada> chamadaAtiva = chamadaRepository.findByEmAndamentoTrue();

        if (chamadaAtiva.isPresent()) {
            Chamada chamada = chamadaAtiva.get();
            chamada.setEmAndamento(false);
            chamada.setDataFechamento(LocalDateTime.now());
            chamadaRepository.save(chamada);
            return chamada;
        } else {
            Chamada novaChamada = new Chamada();
            novaChamada.setEmAndamento(true);
            novaChamada.setDataCriacao(LocalDateTime.now());
            chamadaRepository.save(novaChamada);
            return novaChamada;
        }
    }

    public Chamada buscarPorId(Long id) {
        return chamadaRepository.findById(id).orElse(null);
    }

    public void salvar(Chamada chamada) {
        chamadaRepository.save(chamada);
    }

    public boolean deletarChamada(Long id) {
        if (chamadaRepository.existsById(id)) {
            chamadaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<UsuariosPresencaTotal> listarUsuariosPorPresencas(String nome) {
        List<Object[]> resultados = presencaRepository.findUsuariosOrdenadosPorPresencas();

        List<UsuariosPresencaTotal> usuariosDTO = resultados.stream()
                .map(obj -> new UsuariosPresencaTotal((Usuario) obj[0], (Long) obj[1]))
                .collect(Collectors.toList());

        if (nome != null && !nome.isEmpty()) {
            usuariosDTO = usuariosDTO.stream()
                    .filter(usuario -> usuario.nome().toLowerCase().contains(nome.toLowerCase()))
                    .collect(Collectors.toList());
        }

        return usuariosDTO;
    }
}

