package com.marmitas.entregademarmitas.service;

import com.marmitas.entregademarmitas.exception.ClienteDuplicadoException;
import com.marmitas.entregademarmitas.model.Cliente;
import com.marmitas.entregademarmitas.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }
    
    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }
    
    public Cliente save(Cliente cliente) {
        // Se o cliente tem ID, é uma atualização, então verificar excluindo o próprio cliente
        if (cliente.getId() != null) {
            if (cliente.getRg() != null && clienteRepository.existsByRgAndIdNot(cliente.getRg(), cliente.getId())) {
                throw new ClienteDuplicadoException("Já existe um cliente com este RG: " + cliente.getRg());
            }
            
            if (cliente.getNome() != null && cliente.getEndereco() != null && 
                clienteRepository.existsByNomeAndEnderecoAndIdNot(cliente.getNome().trim(), cliente.getEndereco().trim(), cliente.getId())) {
                throw new ClienteDuplicadoException("Já existe um cliente com este nome e endereço: " + cliente.getNome() + ", " + cliente.getEndereco());
            }
        } else {
            // Se não tem ID, é um novo cliente, verificar normalmente
            if (cliente.getRg() != null && clienteRepository.existsByRg(cliente.getRg())) {
                throw new ClienteDuplicadoException("Já existe um cliente com este RG: " + cliente.getRg());
            }
            
            if (cliente.getNome() != null && cliente.getEndereco() != null && 
                clienteRepository.existsByNomeAndEndereco(cliente.getNome().trim(), cliente.getEndereco().trim())) {
                throw new ClienteDuplicadoException("Já existe um cliente com este nome e endereço: " + cliente.getNome() + ", " + cliente.getEndereco());
            }
        }
        
        return clienteRepository.save(cliente);
    }
    
    public Cliente update(Long id, Cliente clienteAtualizado) {
        return clienteRepository.findById(id)
            .map(cliente -> {
                // Verificar duplicação de RG se estiver sendo alterado
                if (clienteAtualizado.getRg() != null && 
                    (cliente.getRg() == null || !clienteAtualizado.getRg().equals(cliente.getRg()))) {
                    if (clienteRepository.existsByRg(clienteAtualizado.getRg())) {
                        throw new ClienteDuplicadoException("Já existe um cliente com este RG: " + clienteAtualizado.getRg());
                    }
                    cliente.setRg(clienteAtualizado.getRg());
                }
                
                // Verificar duplicação de nome e endereço se estiverem sendo alterados
                String novoNome = clienteAtualizado.getNome() != null ? clienteAtualizado.getNome().trim() : cliente.getNome();
                String novoEndereco = clienteAtualizado.getEndereco() != null ? clienteAtualizado.getEndereco().trim() : cliente.getEndereco();
                
                if (!novoNome.equals(cliente.getNome()) || !novoEndereco.equals(cliente.getEndereco())) {
                    // Excluir o cliente atual da verificação
                    if (clienteRepository.existsByNomeAndEnderecoAndIdNot(novoNome, novoEndereco, id)) {
                        throw new ClienteDuplicadoException("Já existe um cliente com este nome e endereço: " + novoNome + ", " + novoEndereco);
                    }
                    cliente.setNome(clienteAtualizado.getNome());
                    cliente.setEndereco(clienteAtualizado.getEndereco());
                }
                
                // Atualizar outros campos
                if (clienteAtualizado.getData() != null) {
                    cliente.setData(clienteAtualizado.getData());
                }
                if (clienteAtualizado.getRecebeu() != null) {
                    cliente.setRecebeu(clienteAtualizado.getRecebeu());
                }
                
                return clienteRepository.save(cliente);
            })
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));
    }
    
    public void deleteById(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado com id: " + id);
        }
        clienteRepository.deleteById(id);
    }
    
    public List<Cliente> findByNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    public Optional<Cliente> findByRg(String rg) {
        return clienteRepository.findByRg(rg);
    }
    
    public List<Cliente> findByRecebeu(Boolean recebeu) {
        return clienteRepository.findByRecebeu(recebeu);
    }
    
    public List<Cliente> findByDataBetween(LocalDate dataInicio, LocalDate dataFim) {
        return clienteRepository.findByDataBetween(dataInicio, dataFim);
    }
    
    public long count() {
        return clienteRepository.count();
    }
    
    public long countByRecebeu(Boolean recebeu) {
        return clienteRepository.countByRecebeu(recebeu);
    }
}
