package com.example.rfagendamentos.repository;

import com.example.rfagendamentos.model.Procedimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcedimentoRepository extends JpaRepository<Procedimento, Long> {
    List<Procedimento> findByAtivoTrue();
}
