package com.marmitas.entregademarmitas.controller;

import com.marmitas.entregademarmitas.model.Retirada;
import com.marmitas.entregademarmitas.service.ExcelExportService;
import com.marmitas.entregademarmitas.service.RetiradaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/retiradas")
public class RetiradaController {
    
    @Autowired
    private RetiradaService retiradaService;
    
    @Autowired
    private ExcelExportService excelExportService;
    
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarRetirada(@RequestBody Map<String, String> request) {
        try {
            String codigo = request.get("codigo");
            String nome = request.get("nome");
            String rg = request.get("rg");
            
            Retirada retirada;
            
            if (codigo != null && !codigo.trim().isEmpty()) {
                Long clienteId = Long.parseLong(codigo.trim());
                retirada = retiradaService.registrarRetiradaPorCodigo(clienteId);
            } else if (nome != null && !nome.trim().isEmpty()) {
                retirada = retiradaService.registrarRetiradaPorNome(nome.trim());
            } else if (rg != null && !rg.trim().isEmpty()) {
                retirada = retiradaService.registrarRetiradaPorRg(rg.trim());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "É necessário fornecer código, nome completo ou RG do cliente");
                return ResponseEntity.badRequest().body(error);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Retirada registrada com sucesso");
            response.put("id_retirada", retirada.getId());
            response.put("nome_cliente", retirada.getCliente().getNome());
            response.put("codigo_cliente", retirada.getCliente().getId());
            response.put("data_retirada", retirada.getDataRetirada());
            
            return ResponseEntity.ok(response);
            
        } catch (NumberFormatException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Código do cliente deve ser um número válido");
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao registrar retirada: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Retirada>> listarTodasRetiradas() {
        List<Retirada> retiradas = retiradaService.findAll();
        return ResponseEntity.ok(retiradas);
    }
    
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Retirada>> listarRetiradasPorCliente(@PathVariable Long clienteId) {
        List<Retirada> retiradas = retiradaService.findByClienteId(clienteId);
        return ResponseEntity.ok(retiradas);
    }
    
    @GetMapping("/periodo")
    public ResponseEntity<List<Retirada>> listarRetiradasPorPeriodo(
            @RequestParam("dataInicio") String dataInicio,
            @RequestParam("dataFim") String dataFim) {
        
        LocalDateTime inicio = LocalDateTime.parse(dataInicio);
        LocalDateTime fim = LocalDateTime.parse(dataFim);
        
        List<Retirada> retiradas = retiradaService.findByDataRetiradaBetween(inicio, fim);
        return ResponseEntity.ok(retiradas);
    }
    
    @GetMapping("/total")
    public ResponseEntity<Map<String, Long>> estatisticas() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total_retiradas", retiradaService.count());
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarRetiradasExcel(
            @RequestParam(value = "dataInicio", required = false) String dataInicio,
            @RequestParam(value = "dataFim", required = false) String dataFim) {
        try {
            List<Retirada> retiradas;
            
            if (dataInicio != null && dataFim != null) {
                LocalDateTime inicio = LocalDateTime.parse(dataInicio);
                LocalDateTime fim = LocalDateTime.parse(dataFim);
                retiradas = retiradaService.findByDataRetiradaBetween(inicio, fim);
            } else {
                retiradas = retiradaService.findAll();
            }
            
            byte[] excelContent = excelExportService.exportRetiradasToExcel(retiradas);
            
            String filename = "retiradas_marmitas_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + 
                ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelContent.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelContent);
                    
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao gerar arquivo Excel: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
