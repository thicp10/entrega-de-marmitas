package com.marmitas.entregademarmitas.repository;

import com.marmitas.entregademarmitas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByRg(String rg);
    
    List<Cliente> findByNomeContainingIgnoreCase(String nome);
    
    List<Cliente> findByRecebeu(Boolean recebeu);
    
    @Query("SELECT c FROM Cliente c WHERE c.data BETWEEN :dataInicio AND :dataFim")
    List<Cliente> findByDataBetween(@Param("dataInicio") java.time.LocalDate dataInicio, 
                                   @Param("dataFim") java.time.LocalDate dataFim);
    
    boolean existsByRg(String rg);
    
    boolean existsByRgAndIdNot(String rg, Long id);
    
    boolean existsByNomeAndEndereco(String nome, String endereco);
    
    boolean existsByNomeAndEnderecoAndIdNot(String nome, String endereco, Long id);
    
    long countByRecebeu(Boolean recebeu);
}
