package com.example.rfagendamentos.repository;

import com.example.rfagendamentos.model.Agendamento;
import com.example.rfagendamentos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByCliente(Usuario cliente);

    List<Agendamento> findByData(String data);
}
