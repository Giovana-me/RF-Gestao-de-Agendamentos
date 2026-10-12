package com.example.rfagendamentos.repository;

import com.example.rfagendamentos.model.ObservacaoAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ObservacaoAgendamentoRepository
        extends JpaRepository<ObservacaoAgendamento, Long> {

    Optional<ObservacaoAgendamento> findByAgendamento_Id(Long agendamentoId);

    Optional<ObservacaoAgendamento> findByAgendamento_IdAndProprietaria_Id(
            Long agendamentoId,
            Long proprietariaId
    );
}

