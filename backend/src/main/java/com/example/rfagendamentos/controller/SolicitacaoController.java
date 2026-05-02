package com.example.rfagendamentos.controller;

import com.example.rfagendamentos.model.Solicitacao;
import com.example.rfagendamentos.service.SolicitacaoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitacoes")
@CrossOrigin(origins = "http://localhost:5173")
public class SolicitacaoController {
    private final SolicitacaoService solicitacaoService;

    public SolicitacaoController(SolicitacaoService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    @PostMapping
    public Solicitacao cadastrar(@RequestBody Solicitacao solicitacao) {
        return solicitacaoService.salvar(solicitacao);
    }

    @GetMapping
    public List<Solicitacao> listarTodas() {
        return solicitacaoService.listarTodas();
    }

    @GetMapping("/pendentes")
    public List<Solicitacao> listarPendentes() {
        return solicitacaoService.listarPendentes();
    }

    @GetMapping("/{id}")
    public Solicitacao buscarPorId(@PathVariable Long id) {
        return solicitacaoService.buscarPorId(id);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Solicitacao> listarPorCliente(@PathVariable Long clienteId) {
        return solicitacaoService.listarPorCliente(clienteId);
    }

    @PatchMapping("/{id}/aprovar")
    public Solicitacao aprovar(@PathVariable Long id) {
        return solicitacaoService.aprovar(id);
    }

    @PatchMapping("/{id}/recusar")
    public Solicitacao recusar(@PathVariable Long id) {
        return solicitacaoService.recusar(id);
    }
}
