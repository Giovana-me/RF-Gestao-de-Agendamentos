package com.example.rfagendamentos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "horarios")
public class Horario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String hora;
    @Column(nullable = false)
    private Boolean ativo = true;

    public Horario() {
    }

    public Horario(Long id, String hora, Boolean ativo) {
        this.id = id;
        this.hora = hora;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
