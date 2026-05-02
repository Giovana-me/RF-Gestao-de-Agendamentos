package com.example.rfagendamentos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "procedimentos")
public class Procedimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(length = 1000)
    private String descricao;

    private String duracao;

    private Double valor;

    private Boolean ativo = true;

    public Procedimento() {
    }

    public Procedimento(Long id, String nome, String descricao, String duracao, Double valor, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.duracao = duracao;
        this.valor = valor;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDuracao() {
        return duracao;
    }

    public Double getValor() {
        return valor;
    }


    public Boolean getAtivo() {
        return ativo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }


    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
