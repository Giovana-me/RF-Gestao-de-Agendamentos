package com.example.rfagendamentos.repository;

import com.example.rfagendamentos.model.Solicitacao;
import com.example.rfagendamentos.model.StatusSolicitacao;
import com.example.rfagendamentos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
    List<Solicitacao> findByCliente(Usuario cliente);

    List<Solicitacao> findByStatus(StatusSolicitacao status);
}
