package com.marmitas.entregademarmitas.service;

import com.marmitas.entregademarmitas.model.MoradorRua;
import com.marmitas.entregademarmitas.repository.MoradorRuaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class MoradorRuaService {
    
    @Autowired
    private MoradorRuaRepository moradorRuaRepository;
    
    private void validarRetiradaNoMesmoDiaPorNome(String nome) {
        LocalDateTime inicioDoDia = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime fimDoDia = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        if (moradorRuaRepository.existsByNomeAndDataRetiradaBetween(nome, inicioDoDia, fimDoDia)) {
            throw new RuntimeException("Morador de rua com nome " + nome + " já retirou uma marmita hoje.");
        }
    }
    
    private void validarRetiradaNoMesmoDiaPorRg(String rg) {
        LocalDateTime inicioDoDia = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime fimDoDia = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        if (moradorRuaRepository.existsByRgAndDataRetiradaBetween(rg, inicioDoDia, fimDoDia)) {
            throw new RuntimeException("Morador de rua com RG " + rg + " já retirou uma marmita hoje.");
        }
    }
    
    public MoradorRua registrarRetirada(String nome, String rg, String descricao) {
        if (rg != null && !rg.trim().isEmpty()) {
            validarRetiradaNoMesmoDiaPorRg(rg.trim());
        } else {
            validarRetiradaNoMesmoDiaPorNome(nome.trim());
        }
        
        MoradorRua moradorRua = new MoradorRua();
        moradorRua.setNome(nome.trim());
        moradorRua.setRg((rg != null && !rg.trim().isEmpty()) ? rg.trim() : null);
        moradorRua.setDescricao(descricao != null ? descricao.trim() : null);
        moradorRua.setDataRetirada(LocalDateTime.now());
        
        return moradorRuaRepository.save(moradorRua);
    }
    
    public List<MoradorRua> findAll() {
        return moradorRuaRepository.findAll();
    }
    
    public List<MoradorRua> findByNomeContaining(String nome) {
        return moradorRuaRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    public List<MoradorRua> findByDataRetiradaBetween(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return moradorRuaRepository.findByDataRetiradaBetween(dataInicio, dataFim);
    }
    
    public long count() {
        return moradorRuaRepository.count();
    }
    
    public long countByDataRetiradaBetween(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return moradorRuaRepository.countByDataRetiradaBetween(dataInicio, dataFim);
    }
}