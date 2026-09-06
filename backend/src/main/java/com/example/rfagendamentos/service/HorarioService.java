package com.example.rfagendamentos.service;

import com.example.rfagendamentos.model.Horario;
import com.example.rfagendamentos.repository.HorarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HorarioService {
    private final HorarioRepository horarioRepository;

    public HorarioService(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    public List<Horario> listarAtivos() {
        return horarioRepository.findByAtivoTrueOrderByHoraAsc();
    }

    public List<Horario> listarTodos() {
        return horarioRepository.findAllByOrderByHoraAsc();
    }

    public Horario salvar(Horario horario) {
        if (horario.getHora() == null || horario.getHora().trim().isEmpty()) {
            throw new IllegalArgumentException("O horário é obrigatório.");
        }
        String hora = horario.getHora().trim();
        if (!hora.matches("^([01]\\d|2[0-3]):[0-5]\\d$")) {
            throw new IllegalArgumentException(
                    "O horário deve estar no formato HH:mm."
            );
        }
        var existente = horarioRepository.findByHora(hora);
        if (existente.isPresent()) {

            Horario horarioExistente = existente.get();

            if (horarioExistente.getAtivo()) {
                throw new IllegalArgumentException(
                        "Este horário já está cadastrado."
                );
            }
            horarioExistente.setAtivo(true);
            return horarioRepository.save(horarioExistente);
        }
        horario.setHora(hora);
        horario.setAtivo(true);
        return horarioRepository.save(horario);
    }

    public Horario desativar(Long id) {
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Horário não encontrado.")
                );
        horario.setAtivo(false);
        return horarioRepository.save(horario);
    }

    public Horario ativar(Long id) {
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Horário não encontrado.")
                );
        horario.setAtivo(true);
        return horarioRepository.save(horario);
    }
}
