package com.example.rfagendamentos.dto;

import com.example.rfagendamentos.model.TipoUsuario;

public class LoginResponse {
    private Long id;
    private String nome;
    private String email;
    private TipoUsuario tipoUsuario;
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(Long id, String nome, String email, TipoUsuario tipoUsuario, String token) {
        this.id = id;
        this.nome = nome;
        this.email = email;
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

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public String getToken() {
        return token;
    }
}
