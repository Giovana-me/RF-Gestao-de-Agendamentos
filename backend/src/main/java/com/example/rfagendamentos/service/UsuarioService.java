package com.example.rfagendamentos.service;

import com.example.rfagendamentos.model.TipoUsuario;
import com.example.rfagendamentos.model.Usuario;
import com.example.rfagendamentos.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario salvar(Usuario usuario) {
        usuario.setAtivo(true);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarAtivos() {
        return usuarioRepository.findByAtivoTrue();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    public Usuario atualizar(Long id, Usuario dadosAtualizados) {
        Usuario usuario = buscarPorId(id);

        usuario.setNome(dadosAtualizados.getNome());
        usuario.setEmail(dadosAtualizados.getEmail());
        usuario.setTelefone(dadosAtualizados.getTelefone());
        usuario.setCpf(dadosAtualizados.getCpf());
        usuario.setEndereco(dadosAtualizados.getEndereco());

        return usuarioRepository.save(usuario);
    }

    public void desativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    public List<Usuario> listarClientes() {
        return usuarioRepository.findByTipoUsuario(TipoUsuario.CLIENTE);
    }
}
