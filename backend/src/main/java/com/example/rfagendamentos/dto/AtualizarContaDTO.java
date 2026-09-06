package com.example.rfagendamentos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AtualizarContaDTO {
    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O telefone é obrigatório.")
    @Pattern(
            regexp = "^\\d{10,11}$",
            message = "Informe um telefone válido."
    )
    private String telefone;

    @NotBlank(message = "O CPF é obrigatório.")
    private String cpf;
    private EnderecoDTO endereco;

    public AtualizarContaDTO() {
    }

    public AtualizarContaDTO(String nome, String telefone, String cpf, EnderecoDTO endereco) {
        this.nome = nome;
        this.telefone = telefone;
        this.cpf = cpf;
        this.endereco = endereco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public EnderecoDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoDTO endereco) {
        this.endereco = endereco;
    }
}
