package com.example.rfagendamentos.dto;

public class RemarcacaoDTO {
    private String dataDesejada;
    private String horaDesejada;
    private String observacao;

    public String getDataDesejada() {
        return dataDesejada;
    }

    public void setDataDesejada(String dataDesejada) {
        this.dataDesejada = dataDesejada;
    }

    public String getHoraDesejada() {
        return horaDesejada;
    }

    public void setHoraDesejada(String horaDesejada) {
        this.horaDesejada = horaDesejada;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
