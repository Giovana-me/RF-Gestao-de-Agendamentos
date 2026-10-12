package com.example.rfagendamentos.controller;

import com.example.rfagendamentos.dto.*;
import com.example.rfagendamentos.model.Usuario;
import com.example.rfagendamentos.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public Usuario cadastrar(@Valid @RequestBody CadastroRequest request) {
        return usuarioService.cadastrar(request);
    }

    @GetMapping
    public List<Usuario> listarTodos() {
        return usuarioService.listarAtivos();
    }

    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Usuario atualizarConta(
            @PathVariable Long id,
            @RequestBody AtualizarContaDTO usuario) {

        return usuarioService.atualizarConta(id, usuario);
    }

    @PostMapping("/{id}/email/solicitar")
    public void solicitarAlteracaoEmail(
            @PathVariable Long id,
            @RequestBody AlterarEmailDTO dto) {

        usuarioService.solicitarAlteracaoEmail(id, dto);
    }

    @PostMapping("/{id}/email/confirmar")
    public ResponseEntity<LoginResponse> confirmarAlteracaoEmail(
            @PathVariable Long id,
            @RequestBody ConfirmarAlteracaoEmailDTO dto
    ) {
        LoginResponse response = usuarioService.confirmarAlteracaoEmail(id, dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/senha")
    public void alterarSenha(
            @PathVariable Long id,
            @RequestBody AlterarSenhaDTO dto) {

        usuarioService.alterarSenha(id, dto);
    }

    @DeleteMapping("/{id}")
    public void desativar(@PathVariable Long id) {
        usuarioService.desativar(id);
    }

    @GetMapping("/clientes")
    public List<ClienteResponseDTO> listarClientes() {
        return usuarioService.listarClientes();
    }

    @PostMapping("/{id}/senha/solicitar")
    public ResponseEntity<Void> solicitarAlteracaoSenha(@PathVariable Long id, @RequestBody AlterarSenhaDTO dto) {
        usuarioService.solicitarAlteracaoSenha(id, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/senha/confirmar")
    public ResponseEntity<Void> confirmarAlteracaoSenha(
            @PathVariable Long id, @RequestBody ConfirmarAlteracaoSenhaDTO dto) {
        usuarioService.confirmarAlteracaoSenha(id, dto);
        return ResponseEntity.ok().build();
    }
}
