package com.marmitas.entregademarmitas.controller;

import com.marmitas.entregademarmitas.exception.ClienteDuplicadoException;
import com.marmitas.entregademarmitas.model.Cliente;
import com.marmitas.entregademarmitas.service.ClienteService;
import com.marmitas.entregademarmitas.service.ExcelExportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private ExcelExportService excelExportService;
    
    @GetMapping
    public ResponseEntity<List<Cliente>> findAll() {
        List<Cliente> clientes = clienteService.findAll();
        return ResponseEntity.ok(clientes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> findById(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.findById(id);
        return cliente.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente) {
        try {
            Cliente novoCliente = clienteService.save(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoCliente);
        } catch (ClienteDuplicadoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        try {
            Cliente clienteAtualizado = clienteService.update(id, cliente);
            return ResponseEntity.ok(clienteAtualizado);
        } catch (ClienteDuplicadoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            clienteService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/buscar/nome")
    public ResponseEntity<List<Cliente>> findByNome(@RequestParam String nome) {
        List<Cliente> clientes = clienteService.findByNome(nome);
        return ResponseEntity.ok(clientes);
    }
    
    @GetMapping("/buscar/rg")
    public ResponseEntity<Cliente> findByRg(@RequestParam String rg) {
        Optional<Cliente> cliente = clienteService.findByRg(rg);
        return cliente.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/buscar/recebeu")
    public ResponseEntity<List<Cliente>> findByRecebeu(@RequestParam Boolean recebeu) {
        List<Cliente> clientes = clienteService.findByRecebeu(recebeu);
        return ResponseEntity.ok(clientes);
    }
    
    @GetMapping("/buscar/data")
    public ResponseEntity<List<Cliente>> findByDataBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<Cliente> clientes = clienteService.findByDataBetween(dataInicio, dataFim);
        return ResponseEntity.ok(clientes);
    }
    
    @GetMapping("/estatisticas/total")
    public ResponseEntity<Long> count() {
        long total = clienteService.count();
        return ResponseEntity.ok(total);
    }
    
    @GetMapping("/estatisticas/receberam")
    public ResponseEntity<Long> countByReceberam() {
        long total = clienteService.countByRecebeu(true);
        return ResponseEntity.ok(total);
    }
    
    @GetMapping("/estatisticas/nao-receberam")
    public ResponseEntity<Long> countByNaoReceberam() {
        long total = clienteService.countByRecebeu(false);
        return ResponseEntity.ok(total);
    }
    
    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportToExcel() {
        try {
            List<Cliente> clientes = clienteService.findAll();
            byte[] excelContent = excelExportService.exportClientesToExcel(clientes);
            
            String filename = "clientes_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelContent.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelContent);
                    
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
