package com.example.rfagendamentos.dto;

public class AgendaEventoDTO {
    private Long id;
    private String tipo;
    private String data;
    private String hora;
    private String observacao;
    private String status;
    private Long clienteId;
    private String clienteNome;
    private String clienteTelefone;
    private Long procedimentoId;
    private String procedimentoNome;

    public AgendaEventoDTO() {
    }

    public AgendaEventoDTO(Long id, String tipo, String data, String hora, String observacao, String status, Long clienteId, String clienteNome, String clienteTelefone, Long procedimentoId, String procedimentoNome) {
        this.id = id;
        this.tipo = tipo;
        this.data = data;
        this.hora = hora;
        this.observacao = observacao;
        this.status = status;
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
        this.clienteTelefone = clienteTelefone;
        this.procedimentoId = procedimentoId;
        this.procedimentoNome = procedimentoNome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public String getClienteTelefone() {
        return clienteTelefone;
    }

    public void setClienteTelefone(String clienteTelefone) {
        this.clienteTelefone = clienteTelefone;
    }

    public Long getProcedimentoId() {
        return procedimentoId;
    }

    public void setProcedimentoId(Long procedimentoId) {
        this.procedimentoId = procedimentoId;
    }

    public String getProcedimentoNome() {
        return procedimentoNome;
    }

    public void setProcedimentoNome(String procedimentoNome) {
        this.procedimentoNome = procedimentoNome;
    }
}
