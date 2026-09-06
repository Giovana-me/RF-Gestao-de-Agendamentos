package com.example.rfagendamentos.controller;

import com.example.rfagendamentos.model.Horario;
import com.example.rfagendamentos.service.HorarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/horarios")
@CrossOrigin(origins = "http://localhost:5173")
public class HorarioController {
    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @GetMapping
    public ResponseEntity<List<Horario>> listarAtivos() {
        return ResponseEntity.ok(horarioService.listarAtivos());
    }
    @GetMapping("/todos")
    public ResponseEntity<List<Horario>> listarTodos() {
        return ResponseEntity.ok(horarioService.listarTodos());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Horario horario) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(horarioService.salvar(horario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    horarioService.desativar(id)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    horarioService.ativar(id)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
