package com.example.rfagendamentos.controller;

import com.example.rfagendamentos.dto.ObservacaoAgendamentoDTO;
import com.example.rfagendamentos.dto.SalvarObservacaoAgendamentoDTO;
import com.example.rfagendamentos.service.ObservacaoAgendamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/observacoes-agendamento")
@CrossOrigin(origins = "http://localhost:5173")
public class ObservacaoAgendamentoController {
    private final ObservacaoAgendamentoService observacaoService;

    public ObservacaoAgendamentoController(
            ObservacaoAgendamentoService observacaoService) {
        this.observacaoService = observacaoService;
    }

    @GetMapping("/agendamento/{agendamentoId}")
    public ResponseEntity<ObservacaoAgendamentoDTO> buscar(
            @PathVariable Long agendamentoId,
            Authentication authentication) {

        ObservacaoAgendamentoDTO observacao =
                observacaoService.buscarPorAgendamento(
                        agendamentoId,
                        authentication.getName()
                );

        if (observacao == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(observacao);
    }

    @PutMapping("/agendamento/{agendamentoId}")
    public ResponseEntity<ObservacaoAgendamentoDTO> salvarOuAtualizar(
            @PathVariable Long agendamentoId,
            @RequestBody SalvarObservacaoAgendamentoDTO requisicao,
            Authentication authentication) {

        ObservacaoAgendamentoDTO observacao =
                observacaoService.salvarOuAtualizar(
                        agendamentoId,
                        requisicao.getTexto(),
                        authentication.getName()
                );

        return ResponseEntity.ok(observacao);
    }
}
