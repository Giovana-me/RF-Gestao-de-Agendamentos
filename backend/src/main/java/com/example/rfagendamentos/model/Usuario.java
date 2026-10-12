package com.example.rfagendamentos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    private String senha;

    private String telefone;

    @Column(unique = true)
    private String cpf;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;

    private Boolean ativo = true;

    @Column(nullable = false)
    private Long versaoToken = 0L;

    public Usuario() {}

    public Usuario(Long id, String nome, String email, String senha, String telefone, String cpf, Endereco endereco, TipoUsuario tipoUsuario, Boolean ativo, Long versaoToken) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.cpf = cpf;
        this.endereco = endereco;
        this.tipoUsuario = tipoUsuario;
        this.ativo = ativo;
        this.versaoToken = versaoToken;
    }

    public Long getId() { return id; }

    public String getNome() { return nome; }

    public String getEmail() { return email; }

    public String getSenha() { return senha; }

    public String getTelefone() { return telefone; }

    public String getCpf() { return cpf; }

    public Endereco getEndereco() { return endereco; }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }

    public Boolean getAtivo() { return ativo; }

    public Long getVersaoToken() {
        return versaoToken;
    }

    public void setId(Long id) { this.id = id; }

    public void setNome(String nome) { this.nome = nome; }

    public void setEmail(String email) { this.email = email; }

    public void setSenha(String senha) { this.senha = senha; }

    public void setTelefone(String telefone) { this.telefone = telefone; }

    public void setCpf(String cpf) { this.cpf = cpf; }

    public void setEndereco(Endereco endereco) { this.endereco = endereco; }

    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public void setVersaoToken(Long versaoToken) {
        this.versaoToken = versaoToken;
    }
}
