package com.example.rfagendamentos.dto;

public class AlterarEmailDTO {
    private String novoEmail;
    private String senhaAtual;

    public AlterarEmailDTO() {
    }

    public String getNovoEmail() {
        return novoEmail;
    }

    public void setNovoEmail(String novoEmail) {
        this.novoEmail = novoEmail;
    }

    public String getSenhaAtual() {
        return senhaAtual;
    }

    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
    }
}
