package com.example.rfagendamentos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "solicitacoes")
public class Solicitacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dataDesejada;

    private String horaDesejada;

    @Column(length = 1000)
    private String observacao;

    @Enumerated(EnumType.STRING)
    private TipoSolicitacao tipoSolicitacao;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacao status = StatusSolicitacao.PENDENTE;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "procedimento_id")
    private Procedimento procedimento;

    public Solicitacao() {
    }

    public Solicitacao(Long id, String dataDesejada, String horaDesejada, String observacao,
                       TipoSolicitacao tipoSolicitacao, StatusSolicitacao status,
                       Usuario cliente, Procedimento procedimento) {
        this.id = id;
        this.dataDesejada = dataDesejada;
        this.horaDesejada = horaDesejada;
        this.observacao = observacao;
        this.tipoSolicitacao = tipoSolicitacao;
        this.status = status;
        this.cliente = cliente;
        this.procedimento = procedimento;
    }

    public Long getId() {
        return id;
    }

    public String getDataDesejada() {
        return dataDesejada;
    }

    public String getHoraDesejada() {
        return horaDesejada;
    }

    public String getObservacao() {
        return observacao;
    }

    public TipoSolicitacao getTipoSolicitacao() {
        return tipoSolicitacao;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public Procedimento getProcedimento() {
        return procedimento;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDataDesejada(String dataDesejada) {
        this.dataDesejada = dataDesejada;
    }

    public void setHoraDesejada(String horaDesejada) {
        this.horaDesejada = horaDesejada;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void setTipoSolicitacao(TipoSolicitacao tipoSolicitacao) {
        this.tipoSolicitacao = tipoSolicitacao;
    }

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public void setProcedimento(Procedimento procedimento) {
        this.procedimento = procedimento;
    }
}
