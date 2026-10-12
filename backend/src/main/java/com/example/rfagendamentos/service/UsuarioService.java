package com.example.rfagendamentos.service;

import com.example.rfagendamentos.dto.*;
import com.example.rfagendamentos.model.AlteracaoSenha;
import com.example.rfagendamentos.model.AlteracaoEmail;
import com.example.rfagendamentos.model.Endereco;
import com.example.rfagendamentos.model.TipoUsuario;
import com.example.rfagendamentos.model.Usuario;
import com.example.rfagendamentos.repository.AlteracaoEmailRepository;
import com.example.rfagendamentos.repository.AlteracaoSenhaRepository;
import com.example.rfagendamentos.repository.UsuarioRepository;
import com.example.rfagendamentos.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AlteracaoEmailRepository alteracaoEmailRepository;
    private final AlteracaoSenhaRepository alteracaoSenhaRepository;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final JwtService jwtService;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, AlteracaoEmailRepository alteracaoEmailRepository, AlteracaoSenhaRepository alteracaoSenhaRepository, EmailService emailService, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.alteracaoEmailRepository = alteracaoEmailRepository;
        this.alteracaoSenhaRepository = alteracaoSenhaRepository;
        this.emailService = emailService;
        this.jwtService = jwtService;
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

    private EnderecoDTO converterEnderecoDTO(Endereco endereco) {

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

    public void solicitarAlteracaoEmail(
            Long id,
            AlterarEmailDTO dto) {

        Usuario usuario = buscarPorId(id);

        if (!passwordEncoder.matches(
                dto.getSenhaAtual(),
                usuario.getSenha())) {

            throw new RuntimeException("Senha inválida.");
        }

        String novoEmail = dto.getNovoEmail().trim();

        if (novoEmail.isBlank()) {
            throw new RuntimeException(
                    "O novo e-mail é obrigatório."
            );
        }

        if (novoEmail.equalsIgnoreCase(usuario.getEmail())) {
            throw new RuntimeException(
                    "O novo e-mail deve ser diferente do atual."
            );
        }

        if (emailExiste(novoEmail)) {
            throw new RuntimeException(
                    "Este e-mail já está em uso."
            );
        }

        alteracaoEmailRepository
                .findByUsuarioIdAndConfirmadoFalse(id)
                .forEach(alteracao -> {
                    alteracao.setConfirmado(true);
                    alteracaoEmailRepository.save(alteracao);
                });

        String codigo = gerarCodigo();

        AlteracaoEmail alteracao = new AlteracaoEmail();

        alteracao.setUsuario(usuario);
        alteracao.setNovoEmail(novoEmail);
        alteracao.setCodigo(codigo);
        alteracao.setDataExpiracao(
                LocalDateTime.now().plusMinutes(15)
        );
        alteracao.setConfirmado(false);

        alteracaoEmailRepository.save(alteracao);

        emailService.enviarCodigoAlteracaoEmail(
                novoEmail,
                codigo
        );
    }

    private String gerarCodigo() {

        return String.format(
                "%06d",
                secureRandom.nextInt(1_000_000)
        );
    }

    public LoginResponse confirmarAlteracaoEmail(Long id, ConfirmarAlteracaoEmailDTO dto) {
        Usuario usuario = buscarPorId(id);

        AlteracaoEmail alteracao =
                alteracaoEmailRepository.findTopByUsuarioIdAndConfirmadoFalseOrderByIdDesc(id)
                        .orElseThrow(() -> new RuntimeException("Nenhuma alteração de e-mail pendente."));

        if (LocalDateTime.now().isAfter(alteracao.getDataExpiracao())) {
            throw new RuntimeException("O código de confirmação expirou.");
        }

        if (!alteracao.getCodigo().equals(dto.getCodigo())) {
            throw new RuntimeException("Código de confirmação inválido.");
        }

        if (emailExiste(alteracao.getNovoEmail())) {
            throw new RuntimeException("Este e-mail já está em uso.");
        }

        usuario.setEmail(alteracao.getNovoEmail());
        usuarioRepository.save(usuario);

        alteracao.setConfirmado(true);
        alteracaoEmailRepository.save(alteracao);

        String token = jwtService.gerarToken(
                usuario.getEmail(),
                usuario.getVersaoToken()
        );

        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                converterEnderecoDTO(usuario.getEndereco()),
                usuario.getTipoUsuario(),
                token
        );
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

    public void solicitarAlteracaoSenha(Long id, AlterarSenhaDTO dto) {

        Usuario usuario = buscarPorId(id);

        if (dto == null) {
            throw new RuntimeException("Dados da alteração de senha não informados.");
        }

        if (dto.getSenhaAtual() == null || dto.getSenhaAtual().isBlank()) {
            throw new RuntimeException("A senha atual é obrigatória.");
        }

        if (dto.getNovaSenha() == null || dto.getNovaSenha().isBlank()) {
            throw new RuntimeException("A nova senha é obrigatória.");
        }

        if (!passwordEncoder.matches(
                dto.getSenhaAtual(),
                usuario.getSenha()
        )) {
            throw new RuntimeException("Senha atual inválida.");
        }

        if (passwordEncoder.matches(
                dto.getNovaSenha(),
                usuario.getSenha()
        )) {
            throw new RuntimeException(
                    "A nova senha deve ser diferente da senha atual."
            );
        }

        if (usuario.getTipoUsuario() == TipoUsuario.PROPRIETARIA) {

            if (dto.getNovaSenha().length() < 8) {
                throw new RuntimeException(
                        "A nova senha da proprietária deve possuir pelo menos 8 caracteres."
                );
            }

            if (!dto.getNovaSenha().matches(".*[A-Za-z].*")) {
                throw new RuntimeException(
                        "A nova senha deve possuir pelo menos uma letra."
                );
            }

            if (!dto.getNovaSenha().matches(".*\\d.*")) {
                throw new RuntimeException(
                        "A nova senha deve possuir pelo menos um número."
                );
            }

            if (!dto.getNovaSenha().matches(".*[!@#$%^&*(),.?\":{}|<>_\\-+=\\[\\]/;'`~].*")) {
                throw new RuntimeException(
                        "A nova senha deve possuir pelo menos um caractere especial."
                );
            }

        } else {

            if (dto.getNovaSenha().length() < 6) {
                throw new RuntimeException(
                        "A nova senha deve possuir pelo menos 6 caracteres."
                );
            }

            if (!dto.getNovaSenha().matches(".*[A-Za-z].*")) {
                throw new RuntimeException(
                        "A nova senha deve possuir pelo menos uma letra."
                );
            }

            if (!dto.getNovaSenha().matches(".*[!@#$%^&*(),.?\":{}|<>_\\-+=\\[\\]/;'`~].*")) {
                throw new RuntimeException(
                        "A nova senha deve possuir pelo menos um caractere especial."
                );
            }
        }

        alteracaoSenhaRepository
                .findByUsuarioIdAndConfirmadoFalse(id)
                .forEach(alteracao -> {
                    alteracao.setConfirmado(true);
                    alteracaoSenhaRepository.save(alteracao);
                });

        String novaSenhaHash =
                passwordEncoder.encode(dto.getNovaSenha());

        String codigo = gerarCodigo();

        AlteracaoSenha alteracao = new AlteracaoSenha();

        alteracao.setUsuario(usuario);
        alteracao.setNovaSenhaHash(novaSenhaHash);

        alteracao.setCodigo(codigo);

        long minutosExpiracao =
                usuario.getTipoUsuario() == TipoUsuario.PROPRIETARIA ? 10 : 15;

        alteracao.setDataExpiracao(
                LocalDateTime.now().plusMinutes(minutosExpiracao)
        );

        alteracao.setConfirmado(false);

        alteracaoSenhaRepository.save(alteracao);

        emailService.enviarCodigoAlteracaoSenha(
                usuario.getEmail(),
                codigo
        );
    }

    public void confirmarAlteracaoSenha(
            Long id,
            ConfirmarAlteracaoSenhaDTO dto
    ) {

        Usuario usuario = buscarPorId(id);

        AlteracaoSenha alteracao =
                alteracaoSenhaRepository
                        .findTopByUsuarioIdAndConfirmadoFalseOrderByIdDesc(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Nenhuma alteração de senha pendente."
                                )
                        );

        if (LocalDateTime.now().isAfter(
                alteracao.getDataExpiracao()
        )) {
            throw new RuntimeException(
                    "O código de confirmação expirou."
            );
        }

        if (dto == null ||
                dto.getCodigo() == null ||
                dto.getCodigo().isBlank()) {

            throw new RuntimeException(
                    "O código de confirmação é obrigatório."
            );
        }

        if (!alteracao.getCodigo().equals(dto.getCodigo())) {
            throw new RuntimeException(
                    "Código de confirmação inválido."
            );
        }

        usuario.setSenha(alteracao.getNovaSenhaHash());
        usuario.setVersaoToken(usuario.getVersaoToken() + 1);
        usuarioRepository.save(usuario);

        alteracao.setConfirmado(true);

        alteracaoSenhaRepository.save(alteracao);
    }
}
