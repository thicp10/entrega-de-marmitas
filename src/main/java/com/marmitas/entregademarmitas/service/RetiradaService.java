package com.marmitas.entregademarmitas.service;

import com.marmitas.entregademarmitas.model.Cliente;
import com.marmitas.entregademarmitas.model.Retirada;
import com.marmitas.entregademarmitas.repository.ClienteRepository;
import com.marmitas.entregademarmitas.repository.RetiradaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class RetiradaService {
    
    @Autowired
    private RetiradaRepository retiradaRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    private void validarRetiradaNoMesmoDia(Long clienteId) {
        LocalDateTime inicioDoDia = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime fimDoDia = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        if (retiradaRepository.existsByClienteIdAndDataRetiradaBetween(clienteId, inicioDoDia, fimDoDia)) {
            throw new RuntimeException("Este cliente já retirou uma marmita hoje.");
        }
    }
    
    public Retirada registrarRetiradaPorCodigo(Long clienteId) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado com código: " + clienteId);
        }
        
        validarRetiradaNoMesmoDia(clienteId);
        
        Retirada retirada = new Retirada();
        retirada.setCliente(clienteOpt.get());
        retirada.setDataRetirada(LocalDateTime.now());
        
        return retiradaRepository.save(retirada);
    }
    
    public Retirada registrarRetiradaPorNome(String nomeCompleto) {
        List<Cliente> clientes = clienteRepository.findByNomeContainingIgnoreCase(nomeCompleto);
        if (clientes.isEmpty()) {
            throw new RuntimeException("Nenhum cliente encontrado com o nome: " + nomeCompleto);
        }
        
        if (clientes.size() > 1) {
            throw new RuntimeException("Múltiplos clientes encontrados com o nome: " + nomeCompleto + ". Por favor, especifique o nome completo ou use o código do cliente.");
        }
        
        Cliente cliente = clientes.get(0);
        validarRetiradaNoMesmoDia(cliente.getId());
        
        Retirada retirada = new Retirada();
        retirada.setCliente(cliente);
        retirada.setDataRetirada(LocalDateTime.now());
        
        return retiradaRepository.save(retirada);
    }
    
    public Retirada registrarRetiradaPorRg(String rg) {
        Optional<Cliente> clienteOpt = clienteRepository.findByRg(rg);
        if (clienteOpt.isEmpty()) {
            throw new RuntimeException("Cliente não encontrado com RG: " + rg);
        }
        
        Cliente cliente = clienteOpt.get();
        validarRetiradaNoMesmoDia(cliente.getId());
        
        Retirada retirada = new Retirada();
        retirada.setCliente(cliente);
        retirada.setDataRetirada(LocalDateTime.now());
        
        return retiradaRepository.save(retirada);
    }
    
    public List<Retirada> findAll() {
        return retiradaRepository.findAll();
    }
    
    public List<Retirada> findByClienteId(Long clienteId) {
        return retiradaRepository.findByClienteId(clienteId);
    }
    
    public List<Retirada> findByDataRetiradaBetween(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return retiradaRepository.findByDataRetiradaBetween(dataInicio, dataFim);
    }
    
    public List<Retirada> findByClienteNomeContaining(String nome) {
        return retiradaRepository.findByClienteNomeContaining(nome);
    }
    
    public long count() {
        return retiradaRepository.count();
    }
    
    public long countByClienteId(Long clienteId) {
        return retiradaRepository.countByClienteId(clienteId);
    }
}