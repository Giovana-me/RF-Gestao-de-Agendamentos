package com.example.rfagendamentos.service;

import com.example.rfagendamentos.model.*;
import com.example.rfagendamentos.repository.AgendamentoRepository;
import com.example.rfagendamentos.repository.ProcedimentoRepository;
import com.example.rfagendamentos.repository.SolicitacaoRepository;
import com.example.rfagendamentos.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitacaoService {
    private final SolicitacaoRepository solicitacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProcedimentoRepository procedimentoRepository;
    private final AgendamentoRepository agendamentoRepository;

    public SolicitacaoService(
            SolicitacaoRepository solicitacaoRepository,
            UsuarioRepository usuarioRepository,
            ProcedimentoRepository procedimentoRepository,
            AgendamentoRepository agendamentoRepository
    ) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.procedimentoRepository = procedimentoRepository;
        this.agendamentoRepository = agendamentoRepository;
    }

    public Solicitacao salvar(Solicitacao solicitacao) {
        Long clienteId = solicitacao.getCliente().getId();
        Long procedimentoId = solicitacao.getProcedimento().getId();

        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Procedimento procedimento = procedimentoRepository.findById(procedimentoId)
                .orElseThrow(() -> new RuntimeException("Procedimento não encontrado"));

        solicitacao.setCliente(cliente);
        solicitacao.setProcedimento(procedimento);
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);

        if (solicitacao.getTipoSolicitacao() == null) {
            solicitacao.setTipoSolicitacao(TipoSolicitacao.NOVO_AGENDAMENTO);
        }

        return solicitacaoRepository.save(solicitacao);
    }

    public List<Solicitacao> listarTodas() {
        return solicitacaoRepository.findAll();
    }

    public List<Solicitacao> listarPendentes() {
        return solicitacaoRepository.findByStatus(StatusSolicitacao.PENDENTE);
    }

    public List<Solicitacao> listarPorCliente(Long clienteId) {
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        return solicitacaoRepository.findByCliente(cliente);
    }

    public Solicitacao buscarPorId(Long id) {
        return solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
    }

    public Solicitacao aprovar(Long id) {
        Solicitacao solicitacao = buscarPorId(id);

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new RuntimeException("Apenas solicitações pendentes podem ser aprovadas");
        }

        solicitacao.setStatus(StatusSolicitacao.APROVADA);
        Solicitacao solicitacaoAprovada = solicitacaoRepository.save(solicitacao);

        if (solicitacao.getTipoSolicitacao() == TipoSolicitacao.NOVO_AGENDAMENTO) {
            Agendamento agendamento = new Agendamento();
            agendamento.setCliente(solicitacao.getCliente());
            agendamento.setProcedimento(solicitacao.getProcedimento());
            agendamento.setData(solicitacao.getDataDesejada());
            agendamento.setHora(solicitacao.getHoraDesejada());
            agendamento.setObservacao(solicitacao.getObservacao());
            agendamento.setStatus(StatusAgendamento.CONFIRMADO);

            agendamentoRepository.save(agendamento);
        }

        return solicitacaoAprovada;
    }

    public Solicitacao recusar(Long id) {
        Solicitacao solicitacao = buscarPorId(id);

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new RuntimeException("Apenas solicitações pendentes podem ser recusadas");
        }

        solicitacao.setStatus(StatusSolicitacao.RECUSADA);
        return solicitacaoRepository.save(solicitacao);
    }
}
