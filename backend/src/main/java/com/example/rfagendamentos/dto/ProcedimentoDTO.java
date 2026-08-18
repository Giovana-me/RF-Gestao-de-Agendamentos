package com.example.rfagendamentos.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProcedimentoDTO {
    private String nome;
    private String descricao;
    private String duracao;
    @NotNull(message = "O valor do procedimento é obrigatório.")
    @Positive(message = "O valor do procedimento deve ser maior que zero.")
    private Double valor;

    public ProcedimentoDTO() {
    }

    public ProcedimentoDTO(String nome, String descricao, String duracao, Double valor) {
        this.nome = nome;
        this.descricao = descricao;
        this.duracao = duracao;
        this.valor = valor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public @NotNull(message = "O valor do procedimento é obrigatório.") @Positive(message = "O valor do procedimento deve ser maior que zero.") Double getValor() {
        return valor;
    }

    public void setValor(@NotNull(message = "O valor do procedimento é obrigatório.") @Positive(message = "O valor do procedimento deve ser maior que zero.") Double valor) {
        this.valor = valor;
    }
}
