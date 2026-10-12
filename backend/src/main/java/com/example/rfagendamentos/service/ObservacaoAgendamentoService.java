package com.example.rfagendamentos.service;

import com.example.rfagendamentos.dto.ObservacaoAgendamentoDTO;
import com.example.rfagendamentos.model.Agendamento;
import com.example.rfagendamentos.model.ObservacaoAgendamento;
import com.example.rfagendamentos.model.TipoUsuario;
import com.example.rfagendamentos.model.Usuario;
import com.example.rfagendamentos.repository.ObservacaoAgendamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ObservacaoAgendamentoService {
    private final ObservacaoAgendamentoRepository observacaoRepository;
    private final AgendamentoService agendamentoService;
    private final UsuarioService usuarioService;

    public ObservacaoAgendamentoService(
            ObservacaoAgendamentoRepository observacaoRepository,
            AgendamentoService agendamentoService,
            UsuarioService usuarioService) {
        this.observacaoRepository = observacaoRepository;
        this.agendamentoService = agendamentoService;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public ObservacaoAgendamentoDTO buscarPorAgendamento(
            Long agendamentoId, String emailAutenticado) {

        Usuario proprietaria = buscarProprietaria(emailAutenticado);

        return observacaoRepository.findByAgendamento_Id(agendamentoId)
                .filter(obs -> obs.getProprietaria().getId()
                        .equals(proprietaria.getId()))
                .map(this::converterParaDTO)
                .orElse(null);
    }

    @Transactional
    public ObservacaoAgendamentoDTO salvarOuAtualizar(
            Long agendamentoId, String texto, String emailAutenticado) {

        Usuario proprietaria = buscarProprietaria(emailAutenticado);

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    "A observação não pode estar vazia.");
        }

        if (texto.length() > 2000) {
            throw new IllegalArgumentException(
                    "A observação deve ter no máximo 2000 caracteres.");
        }

        Agendamento agendamento =
                agendamentoService.buscarPorId(agendamentoId);

        ObservacaoAgendamento observacao =
                observacaoRepository.findByAgendamento_Id(agendamentoId)
                        .orElseGet(ObservacaoAgendamento::new);

        if (observacao.getId() != null
                && !observacao.getProprietaria().getId()
                .equals(proprietaria.getId())) {
            throw new IllegalStateException(
                    "Você não tem permissão para alterar esta observação.");
        }

        observacao.setAgendamento(agendamento);
        observacao.setProprietaria(proprietaria);
        observacao.setTexto(texto);

        return converterParaDTO(observacaoRepository.save(observacao));
    }

    private Usuario buscarProprietaria(String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (usuario.getTipoUsuario() != TipoUsuario.PROPRIETARIA) {
            throw new SecurityException(
                    "Somente a proprietária pode acessar estas observações.");
        }

        return usuario;
    }

    private ObservacaoAgendamentoDTO converterParaDTO(
            ObservacaoAgendamento observacao) {

        return new ObservacaoAgendamentoDTO(
                observacao.getId(),
                observacao.getAgendamento().getId(),
                observacao.getTexto(),
                observacao.getDataCriacao(),
                observacao.getDataAtualizacao()
        );
    }
}
