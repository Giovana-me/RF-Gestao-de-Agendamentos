package com.example.rfagendamentos.repository;

import com.example.rfagendamentos.model.AlteracaoSenha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlteracaoSenhaRepository extends JpaRepository<AlteracaoSenha, Long> {
    List<AlteracaoSenha> findByUsuarioIdAndConfirmadoFalse(Long usuarioId);

    Optional<AlteracaoSenha> findTopByUsuarioIdAndConfirmadoFalseOrderByIdDesc(Long usuarioId);
}
