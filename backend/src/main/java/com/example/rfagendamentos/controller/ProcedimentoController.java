package com.example.rfagendamentos.controller;

import com.example.rfagendamentos.model.Procedimento;
import com.example.rfagendamentos.service.ProcedimentoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/procedimentos")
@CrossOrigin(origins = "http://localhost:5173")
public class ProcedimentoController {
    private final ProcedimentoService procedimentoService;

    public ProcedimentoController(ProcedimentoService procedimentoService) {
        this.procedimentoService = procedimentoService;
    }

    @PostMapping
    public Procedimento cadastrar(@RequestBody Procedimento procedimento) {
        return procedimentoService.salvar(procedimento);
    }

    @GetMapping
    public List<Procedimento> listarAtivos() {
        return procedimentoService.listarAtivos();
    }

    @GetMapping("/todos")
    public List<Procedimento> listarTodos() {
        return procedimentoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Procedimento buscarPorId(@PathVariable Long id) {
        return procedimentoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Procedimento atualizar(@PathVariable Long id, @RequestBody Procedimento procedimento) {
        return procedimentoService.atualizar(id, procedimento);
    }

    @DeleteMapping("/{id}")
    public void desativar(@PathVariable Long id) {
        procedimentoService.desativar(id);
    }
}
