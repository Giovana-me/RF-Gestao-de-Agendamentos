package com.example.rfagendamentos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "agendamentos")
public class Agendamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;

    private String hora;

    private String observacao;

    @Enumerated(EnumType.STRING)
    private StatusAgendamento status = StatusAgendamento.PENDENTE;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "procedimento_id")
    private Procedimento procedimento;

    public Agendamento() {
    }

    public Agendamento(Long id, String data, String hora, String observacao,
                       StatusAgendamento status, Usuario cliente, Procedimento procedimento) {
        this.id = id;
        this.data = data;
        this.hora = hora;
        this.observacao = observacao;
        this.status = status;
        this.cliente = cliente;
        this.procedimento = procedimento;
    }

    public Long getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public String getHora() {
        return hora;
    }

    public String getObservacao() {
        return observacao;
    }

    public StatusAgendamento getStatus() {
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

    public void setData(String data) {
        this.data = data;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public void setProcedimento(Procedimento procedimento) {
        this.procedimento = procedimento;
    }
}
