package com.example.rfagendamentos.service;

import com.example.rfagendamentos.model.Procedimento;
import com.example.rfagendamentos.repository.ProcedimentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcedimentoService {
    private final ProcedimentoRepository procedimentoRepository;

    public ProcedimentoService(ProcedimentoRepository procedimentoRepository) {
        this.procedimentoRepository = procedimentoRepository;
    }

    public Procedimento salvar(Procedimento procedimento) {
        procedimento.setAtivo(true);
        return procedimentoRepository.save(procedimento);
    }

    public List<Procedimento> listarTodos() {
        return procedimentoRepository.findAll();
    }

    public List<Procedimento> listarAtivos() {
        return procedimentoRepository.findByAtivoTrue();
    }

    public Procedimento buscarPorId(Long id) {
        return procedimentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Procedimento não encontrado"));
    }

    public Procedimento atualizar(Long id, Procedimento dadosAtualizados) {
        Procedimento procedimento = buscarPorId(id);

        procedimento.setNome(dadosAtualizados.getNome());
        procedimento.setDescricao(dadosAtualizados.getDescricao());
        procedimento.setDuracao(dadosAtualizados.getDuracao());
        procedimento.setValor(dadosAtualizados.getValor());

        return procedimentoRepository.save(procedimento);
    }

    public void desativar(Long id) {
        Procedimento procedimento = buscarPorId(id);
        procedimento.setAtivo(false);
        procedimentoRepository.save(procedimento);
    }
}
