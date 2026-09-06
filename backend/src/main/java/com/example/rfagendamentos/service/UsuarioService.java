package com.example.rfagendamentos.service;

import com.example.rfagendamentos.dto.AlterarEmailDTO;
import com.example.rfagendamentos.dto.AlterarSenhaDTO;
import com.example.rfagendamentos.dto.AtualizarContaDTO;
import com.example.rfagendamentos.dto.CadastroRequest;
import com.example.rfagendamentos.dto.ClienteResponseDTO;
import com.example.rfagendamentos.dto.EnderecoDTO;
import com.example.rfagendamentos.model.Endereco;
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

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario cadastrar(CadastroRequest request) {

        String email = request.getEmail().trim();
        String cpf = request.getCpf().trim();

        if (emailExiste(email)) {
            throw new RuntimeException("Este e-mail já está cadastrado.");
        }

        if (cpfExiste(cpf)) {
            throw new RuntimeException("Este CPF já está cadastrado.");
        }

        Usuario usuario = new Usuario();

        usuario.setNome(request.getNome().trim());
        usuario.setEmail(email);
        usuario.setSenha(request.getSenha());
        usuario.setTelefone(request.getTelefone());
        usuario.setCpf(cpf);

        usuario.setTipoUsuario(TipoUsuario.CLIENTE);
        usuario.setAtivo(true);

        return salvar(usuario);
    }

    public boolean emailExiste(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    public boolean cpfExiste(String cpf) {
        return usuarioRepository.findByCpf(cpf).isPresent();
    }

    public Usuario salvar(Usuario usuario) {

        usuario.setAtivo(true);
        usuario.setSenha(
                passwordEncoder.encode(usuario.getSenha())
        );

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
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado.")
                );
    }

    public Usuario buscarPorEmail(String email) {

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado.")
                );
    }

    public Usuario atualizarConta(
            Long id,
            AtualizarContaDTO dadosAtualizados) {

        Usuario usuario = buscarPorId(id);

        usuario.setNome(dadosAtualizados.getNome());
        usuario.setTelefone(dadosAtualizados.getTelefone());
        usuario.setCpf(dadosAtualizados.getCpf());

        usuario.setEndereco(
                converterEndereco(
                        dadosAtualizados.getEndereco(),
                        usuario.getEndereco()
                )
        );

        return usuarioRepository.save(usuario);
    }

    public void desativar(Long id) {

        Usuario usuario = buscarPorId(id);

        usuario.setAtivo(false);

        usuarioRepository.save(usuario);
    }

    public List<ClienteResponseDTO> listarClientes() {

        return usuarioRepository
                .findByTipoUsuarioAndAtivoTrue(TipoUsuario.CLIENTE)
                .stream()
                .map(usuario -> new ClienteResponseDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail(),
                        usuario.getTelefone(),
                        usuario.getCpf(),
                        usuario.getEndereco()
                ))
                .toList();
    }

    private Endereco converterEndereco(
            EnderecoDTO dto,
            Endereco endereco) {

        if (dto == null) {
            return endereco;
        }

        if (endereco == null) {
            endereco = new Endereco();
        }

        endereco.setCep(dto.getCep());
        endereco.setLogradouro(dto.getLogradouro());
        endereco.setNumero(dto.getNumero());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());

        return endereco;
    }

    public Usuario alterarEmail(
            Long id,
            AlterarEmailDTO dto) {

        Usuario usuario = buscarPorId(id);

        if (!passwordEncoder.matches(
                dto.getSenhaAtual(),
                usuario.getSenha())) {

            throw new RuntimeException("Senha inválida.");
        }

        Usuario existente =
                usuarioRepository
                        .findByEmail(dto.getNovoEmail())
                        .orElse(null);

        if (existente != null &&
                !existente.getId().equals(id)) {

            throw new RuntimeException(
                    "Este e-mail já está em uso."
            );
        }

        usuario.setEmail(dto.getNovoEmail());

        return usuarioRepository.save(usuario);
    }

    public void alterarSenha(
            Long id,
            AlterarSenhaDTO dto) {

        Usuario usuario = buscarPorId(id);

        boolean senhaCorreta =
                passwordEncoder.matches(
                        dto.getSenhaAtual(),
                        usuario.getSenha()
                );

        if (!senhaCorreta) {
            throw new RuntimeException(
                    "Senha atual incorreta."
            );
        }

        usuario.setSenha(
                passwordEncoder.encode(
                        dto.getNovaSenha()
                )
        );

        usuarioRepository.save(usuario);
    }
}
