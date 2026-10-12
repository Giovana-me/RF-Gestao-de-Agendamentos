package com.example.rfagendamentos.repository;

import com.example.rfagendamentos.model.AlteracaoEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface AlteracaoEmailRepository extends JpaRepository<AlteracaoEmail, Long> {
    Optional<AlteracaoEmail> findTopByUsuarioIdAndConfirmadoFalseOrderByIdDesc(Long usuarioId);

    List<AlteracaoEmail> findByUsuarioIdAndConfirmadoFalse(Long usuarioId);
}
