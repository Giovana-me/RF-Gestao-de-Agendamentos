package com.example.rfagendamentos.service;

import com.example.rfagendamentos.dto.LoginRequest;
import com.example.rfagendamentos.dto.LoginResponse;
import com.example.rfagendamentos.exception.AuthenticationException;
import com.example.rfagendamentos.model.Usuario;
import com.example.rfagendamentos.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioService usuarioService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioService.buscarPorEmail(request.getEmail());

        if(!usuario.getAtivo()) {
            throw new AuthenticationException("Usuário desativado.");
        }

        boolean senhaValida = passwordEncoder.matches(request.getSenha(), usuario.getSenha());

        if(!senhaValida) {
            throw new AuthenticationException("E-mail ou senha inválidos.");
        }

        String token = jwtService.gerarToken(usuario.getEmail());

        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getEndereco(),
                usuario.getTipoUsuario(),
                token
        );
    }
}
