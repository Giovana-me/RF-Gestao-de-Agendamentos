package com.example.rfagendamentos.service;

import com.example.rfagendamentos.dto.CadastroRequest;
import com.example.rfagendamentos.dto.LoginRequest;
import com.example.rfagendamentos.dto.LoginResponse;
import com.example.rfagendamentos.dto.EnderecoDTO;
import com.example.rfagendamentos.exception.AuthenticationException;
import com.example.rfagendamentos.model.Endereco;
import com.example.rfagendamentos.model.TipoUsuario;
import com.example.rfagendamentos.model.Usuario;
import com.example.rfagendamentos.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioService usuarioService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public void cadastrar(CadastroRequest request) {

        if (usuarioService.emailExiste(request.getEmail())) {
            throw new RuntimeException("Este e-mail já está cadastrado.");
        }

        if (usuarioService.cpfExiste(request.getCpf())) {
            throw new RuntimeException("Este CPF já está cadastrado.");
        }

        Usuario usuario = new Usuario();

        usuario.setNome(request.getNome().trim());
        usuario.setEmail(request.getEmail().trim());
        usuario.setSenha(request.getSenha());
        usuario.setTelefone(request.getTelefone());
        usuario.setCpf(request.getCpf());

        usuario.setTipoUsuario(TipoUsuario.CLIENTE);
        usuario.setAtivo(true);

        usuarioService.salvar(usuario);
    }

    public LoginResponse login(LoginRequest request) {

        String email = request.getEmail().trim();

        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (!usuario.getAtivo()) {
            throw new AuthenticationException("Usuário desativado.");
        }

        boolean senhaValida =
                passwordEncoder.matches(
                        request.getSenha(),
                        usuario.getSenha()
                );

        if (!senhaValida) {
            throw new AuthenticationException(
                    "E-mail ou senha inválidos."
            );
        }

        String token = jwtService.gerarToken(
                usuario.getEmail(),
                usuario.getVersaoToken()
        );

        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                converterEndereco(usuario.getEndereco()),
                usuario.getTipoUsuario(),
                token
        );
    }

    private EnderecoDTO converterEndereco(Endereco endereco) {

        if (endereco == null) {
            return null;
        }

        EnderecoDTO dto = new EnderecoDTO();

        dto.setCep(endereco.getCep());
        dto.setLogradouro(endereco.getLogradouro());
        dto.setNumero(endereco.getNumero());
        dto.setBairro(endereco.getBairro());
        dto.setCidade(endereco.getCidade());
        dto.setEstado(endereco.getEstado());

        return dto;
    }
}
