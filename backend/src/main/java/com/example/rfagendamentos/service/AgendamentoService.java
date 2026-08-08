package com.example.rfagendamentos.service;

import com.example.rfagendamentos.dto.AgendaEventoDTO;
import com.example.rfagendamentos.model.Agendamento;
import com.example.rfagendamentos.model.Procedimento;
import com.example.rfagendamentos.model.StatusAgendamento;
import com.example.rfagendamentos.model.Usuario;
import com.example.rfagendamentos.repository.AgendamentoRepository;
import com.example.rfagendamentos.repository.ProcedimentoRepository;
import com.example.rfagendamentos.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendamentoService {
    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProcedimentoRepository procedimentoRepository;

    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            UsuarioRepository usuarioRepository,
            ProcedimentoRepository procedimentoRepository
    ) {
        this.agendamentoRepository = agendamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.procedimentoRepository = procedimentoRepository;
    }

    public Agendamento salvar(Agendamento agendamento) {
        Long clienteId = agendamento.getCliente().getId();
        Long procedimentoId = agendamento.getProcedimento().getId();

        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Procedimento procedimento = procedimentoRepository.findById(procedimentoId)
                .orElseThrow(() -> new RuntimeException("Procedimento não encontrado"));

        agendamento.setCliente(cliente);
        agendamento.setProcedimento(procedimento);
        agendamento.setStatus(StatusAgendamento.PENDENTE);

        return agendamentoRepository.save(agendamento);
    }

    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAll();
    }

    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
    }

    public List<Agendamento> listarPorCliente(Long clienteId) {
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        return agendamentoRepository.findByCliente(cliente);
    }

    public List<Agendamento> listarPorData(String data) {
        return agendamentoRepository.findByData(data);
    }

    public Agendamento atualizar(Long id, Agendamento dadosAtualizados) {
        Agendamento agendamento = buscarPorId(id);

        agendamento.setData(dadosAtualizados.getData());
        agendamento.setHora(dadosAtualizados.getHora());
        agendamento.setObservacao(dadosAtualizados.getObservacao());

        if (dadosAtualizados.getProcedimento() != null && dadosAtualizados.getProcedimento().getId() != null) {
            Procedimento procedimento = procedimentoRepository.findById(dadosAtualizados.getProcedimento().getId())
                    .orElseThrow(() -> new RuntimeException("Procedimento não encontrado"));

            agendamento.setProcedimento(procedimento);
        }

        return agendamentoRepository.save(agendamento);
    }

    public Agendamento alterarStatus(Long id, StatusAgendamento status) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.setStatus(status);
        return agendamentoRepository.save(agendamento);
    }

    public void cancelar(Long id) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        agendamentoRepository.save(agendamento);
    }

    public List<Agendamento> listarPorStatus(StatusAgendamento status) {
        return agendamentoRepository.findByStatus(status);
    }

    private AgendaEventoDTO converterParaAgenda(Agendamento agendamento) {
        return new AgendaEventoDTO(
                agendamento.getId(),
                "AGENDAMENTO",
                agendamento.getData(),
                agendamento.getHora(),
                agendamento.getObservacao(),
                agendamento.getStatus().name(),
                agendamento.getCliente().getId(),
                agendamento.getCliente().getNome(),
                agendamento.getCliente().getTelefone(),
                agendamento.getProcedimento().getId(),
                agendamento.getProcedimento().getNome()
        );
    }

    public List<AgendaEventoDTO> listarConfirmadosParaAgenda() {
        return agendamentoRepository
                .findByStatus(StatusAgendamento.CONFIRMADO)
                .stream()
                .map(this::converterParaAgenda)
                .toList();
    }
}
