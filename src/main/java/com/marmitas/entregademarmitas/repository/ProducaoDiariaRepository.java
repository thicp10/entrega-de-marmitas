package com.marmitas.entregademarmitas.repository;

import com.marmitas.entregademarmitas.model.ProducaoDiaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ProducaoDiariaRepository extends JpaRepository<ProducaoDiaria, Long> {

    Optional<ProducaoDiaria> findByData(LocalDate data);
}
