package com.example.rfagendamentos.controller;

import com.example.rfagendamentos.dto.AlterarEmailDTO;
import com.example.rfagendamentos.dto.AlterarSenhaDTO;
import com.example.rfagendamentos.dto.AtualizarContaDTO;
import com.example.rfagendamentos.dto.CadastroRequest;
import com.example.rfagendamentos.dto.ClienteResponseDTO;
import com.example.rfagendamentos.model.Usuario;
import com.example.rfagendamentos.service.UsuarioService;
import jakarta.validation.Valid;
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

    @PutMapping("/{id}/email")
    public Usuario alterarEmail(
            @PathVariable Long id,
            @RequestBody AlterarEmailDTO dto) {

        return usuarioService.alterarEmail(id, dto);
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
}
