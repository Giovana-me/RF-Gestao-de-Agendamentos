package com.example.rfagendamentos.service;

import com.example.rfagendamentos.model.*;
import com.example.rfagendamentos.repository.AgendamentoRepository;
import com.example.rfagendamentos.repository.ProcedimentoRepository;
import com.example.rfagendamentos.repository.SolicitacaoRepository;
import com.example.rfagendamentos.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import com.example.rfagendamentos.dto.AgendaEventoDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

        return solicitacaoRepository.findByClienteAndAtivoTrue(cliente);
    }

    public Solicitacao buscarPorId(Long id) {
        return solicitacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitação não encontrada"));
    }

    public Solicitacao remarcar(
            Long id,
            String dataDesejada,
            String horaDesejada,
            String observacao
    ) {

        Solicitacao solicitacao = buscarPorId(id);

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new RuntimeException(
                    "Somente solicitações pendentes podem ser remarcadas"
            );
        }

        solicitacao.setDataDesejada(dataDesejada);
        solicitacao.setHoraDesejada(horaDesejada);
        solicitacao.setObservacao(observacao);

        return solicitacaoRepository.save(solicitacao);
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

    public Solicitacao cancelar(Long id) {
        Solicitacao solicitacao = buscarPorId(id);

        if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
            throw new RuntimeException("Apenas solicitações pendentes podem ser canceladas");
        }

        solicitacao.setStatus(StatusSolicitacao.CANCELADA);
        return solicitacaoRepository.save(solicitacao);
    }

    private AgendaEventoDTO converterParaAgenda(Solicitacao solicitacao) {
        return new AgendaEventoDTO(
                solicitacao.getId(),
                "SOLICITACAO",
                solicitacao.getDataDesejada(),
                solicitacao.getHoraDesejada(),
                solicitacao.getObservacao(),
                solicitacao.getStatus().name(),
                solicitacao.getCliente().getId(),
                solicitacao.getCliente().getNome(),
                solicitacao.getCliente().getTelefone(),
                solicitacao.getProcedimento().getId(),
                solicitacao.getProcedimento().getNome()
        );
    }

    public List<AgendaEventoDTO> listarPendentesParaAgenda() {
        return solicitacaoRepository
                .findByStatus(StatusSolicitacao.PENDENTE)
                .stream()
                .map(this::converterParaAgenda)
                .toList();
    }

    public void inativar(Long id) {
        Solicitacao solicitacao = buscarPorId(id);
        if (solicitacao.getStatus() != StatusSolicitacao.RECUSADA &&
                solicitacao.getStatus() != StatusSolicitacao.CANCELADA) {
            throw new RuntimeException(
                    "Somente solicitações recusadas ou canceladas podem ser excluídas."
            );
        }
        solicitacao.setAtivo(false);
        solicitacaoRepository.save(solicitacao);
    }

    public List<Solicitacao> listarRecentesPorCliente(Long clienteId) {
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        LocalDate limite = LocalDate.now().minusMonths(1);

        return solicitacaoRepository.findByClienteAndAtivoTrue(cliente)
                .stream()
                .filter(s -> {
                    if (s.getStatus() == StatusSolicitacao.PENDENTE) {
                        return true;
                    }

                    if (s.getDataDesejada() == null) {
                        return true;
                    }

                    LocalDate data = LocalDate.parse(s.getDataDesejada());
                    return !data.isBefore(limite);
                })
                .collect(Collectors.toList());
    }

    public List<Solicitacao> listarHistoricoPorCliente(Long clienteId) {
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        LocalDate limite = LocalDate.now().minusMonths(1);

        return solicitacaoRepository.findByClienteAndAtivoTrue(cliente)
                .stream()
                .filter(s -> {
                    if (s.getStatus() == StatusSolicitacao.PENDENTE) {
                        return false;
                    }

                    if (s.getDataDesejada() == null) {
                        return false;
                    }
                    LocalDate data = LocalDate.parse(s.getDataDesejada());
                    return data.isBefore(limite);
                })
                .collect(Collectors.toList());
    }
}
