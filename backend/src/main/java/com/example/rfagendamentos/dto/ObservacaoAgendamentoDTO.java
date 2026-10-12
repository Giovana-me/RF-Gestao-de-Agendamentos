package com.example.rfagendamentos.dto;

import java.time.LocalDateTime;

public class ObservacaoAgendamentoDTO {
    private Long id;
    private Long agendamentoId;
    private String texto;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public ObservacaoAgendamentoDTO() {}

    public ObservacaoAgendamentoDTO(Long id, Long agendamentoId, String texto,
                                    LocalDateTime dataCriacao,
                                    LocalDateTime dataAtualizacao) {
        this.id = id;
        this.agendamentoId = agendamentoId;
        this.texto = texto;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgendamentoId() {
        return agendamentoId;
    }

    public void setAgendamentoId(Long agendamentoId) {
        this.agendamentoId = agendamentoId;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
