package com.marmitas.entregademarmitas.repository;

import com.marmitas.entregademarmitas.model.MoradorRua;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MoradorRuaRepository extends JpaRepository<MoradorRua, Long> {
    
    List<MoradorRua> findByNomeContainingIgnoreCase(String nome);
    
    List<MoradorRua> findByRg(String rg);
    
    List<MoradorRua> findByDataRetiradaBetween(LocalDateTime dataInicio, LocalDateTime dataFim);
    
    @Query("SELECT COUNT(m) > 0 FROM MoradorRua m WHERE m.nome = :nome AND m.dataRetirada BETWEEN :dataInicio AND :dataFim")
    boolean existsByNomeAndDataRetiradaBetween(@Param("nome") String nome, @Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);
    
    @Query("SELECT COUNT(m) > 0 FROM MoradorRua m WHERE m.rg = :rg AND m.dataRetirada BETWEEN :dataInicio AND :dataFim")
    boolean existsByRgAndDataRetiradaBetween(@Param("rg") String rg, @Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);
    
    @Query("SELECT COUNT(m) FROM MoradorRua m WHERE m.dataRetirada BETWEEN :dataInicio AND :dataFim")
    long countByDataRetiradaBetween(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);
}
