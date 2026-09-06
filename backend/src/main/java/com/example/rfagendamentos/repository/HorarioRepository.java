package com.example.rfagendamentos.repository;

import com.example.rfagendamentos.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
    Optional<Horario> findByHora(String hora);
    Optional<Horario> findByHoraAndAtivoTrue(String hora);

    List<Horario> findAllByOrderByHoraAsc();

    List<Horario> findByAtivoTrueOrderByHoraAsc();
}
