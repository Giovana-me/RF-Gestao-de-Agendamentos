package com.example.rfagendamentos.dto;

import com.example.rfagendamentos.model.TipoUsuario;

public class LoginResponse {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String endereco;
    private TipoUsuario tipoUsuario;
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(Long id, String nome, String email, String telefone, String endereco, TipoUsuario tipoUsuario, String token) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
        this.tipoUsuario = tipoUsuario;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public String getToken() {
        return token;
    }
}
