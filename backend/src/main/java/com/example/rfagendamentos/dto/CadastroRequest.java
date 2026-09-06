package com.example.rfagendamentos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CadastroRequest {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Informe um e-mail válido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    private String senha;

    @NotBlank(message = "O telefone é obrigatório.")
    @Pattern(
            regexp = "^\\d{10,11}$",
            message = "Digite apenas números no telefone. Informe 10 ou 11 dígitos."
    )
    private String telefone;

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(
            regexp = "^\\d{11}$",
            message = "Digite apenas números no CPF. Informe 11 dígitos."
    )
    private String cpf;

    public CadastroRequest() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
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
}