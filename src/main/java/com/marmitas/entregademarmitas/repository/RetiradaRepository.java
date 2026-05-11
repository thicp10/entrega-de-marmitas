package com.marmitas.entregademarmitas.repository;

import com.marmitas.entregademarmitas.model.Retirada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RetiradaRepository extends JpaRepository<Retirada, Long> {
    
    List<Retirada> findByClienteId(Long clienteId);
    
    List<Retirada> findByDataRetiradaBetween(LocalDateTime dataInicio, LocalDateTime dataFim);
    
    @Query("SELECT r FROM Retirada r WHERE r.cliente.nome LIKE %:nome%")
    List<Retirada> findByClienteNomeContaining(@Param("nome") String nome);
    
    @Query("SELECT r FROM Retirada r WHERE r.cliente.rg = :rg")
    List<Retirada> findByClienteRg(@Param("rg") String rg);
    
    @Query("SELECT COUNT(r) FROM Retirada r WHERE r.cliente.id = :clienteId")
    long countByClienteId(@Param("clienteId") Long clienteId);
    
    boolean existsByClienteIdAndDataRetiradaBetween(Long clienteId, LocalDateTime dataInicio, LocalDateTime dataFim);
}