package com.example.rfagendamentos.controller;

import com.example.rfagendamentos.model.Agendamento;
import com.example.rfagendamentos.model.StatusAgendamento;
import com.example.rfagendamentos.service.AgendamentoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agendamentos")
@CrossOrigin(origins = "http://localhost:5173")
public class AgendamentoController {
    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping
    public Agendamento cadastrar(@RequestBody Agendamento agendamento) {
        return agendamentoService.salvar(agendamento);
    }

    @GetMapping
    public List<Agendamento> listarTodos() {
        return agendamentoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Agendamento buscarPorId(@PathVariable Long id) {
        return agendamentoService.buscarPorId(id);
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Agendamento> listarPorCliente(@PathVariable Long clienteId) {
        return agendamentoService.listarPorCliente(clienteId);
    }

    @GetMapping("/data/{data}")
    public List<Agendamento> listarPorData(@PathVariable String data) {
        return agendamentoService.listarPorData(data);
    }

    @PutMapping("/{id}")
    public Agendamento atualizar(@PathVariable Long id, @RequestBody Agendamento agendamento) {
        return agendamentoService.atualizar(id, agendamento);
    }

    @PatchMapping("/{id}/status")
    public Agendamento alterarStatus(@PathVariable Long id, @RequestParam StatusAgendamento status) {
        return agendamentoService.alterarStatus(id, status);
    }

    @PatchMapping("/{id}/cancelar")
    public void cancelar(@PathVariable Long id) {
        agendamentoService.cancelar(id);
    }
}
