package com.marmitas.entregademarmitas.controller;

import com.marmitas.entregademarmitas.model.Retirada;
import com.marmitas.entregademarmitas.model.MoradorRua;
import com.marmitas.entregademarmitas.service.ExcelExportService;
import com.marmitas.entregademarmitas.service.ProducaoDiariaService;
import com.marmitas.entregademarmitas.service.RetiradaService;
import com.marmitas.entregademarmitas.service.MoradorRuaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private MoradorRuaService moradorRuaService;
    
    @Autowired
    private ExcelExportService excelExportService;
    
    @Autowired
    private ProducaoDiariaService producaoDiariaService;
    
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarRetirada(@RequestBody Map<String, String> request) {
        try {
            producaoDiariaService.validarSaldoDisponivel();
            
            String codigo = request.get("codigo");
            String nome = request.get("nome");
            String rg = request.get("rg");
            String moradorRua = request.get("moradorRua");
            
            // Verificar se é retirada de morador de rua
            if ("true".equalsIgnoreCase(moradorRua)) {
                return registrarRetiradaMoradorRua(request);
            }
            
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
    
    private ResponseEntity<?> registrarRetiradaMoradorRua(Map<String, String> request) {
        try {
            String nome = request.get("nome");
            String rg = request.get("rg");
            String descricao = request.get("descricao");
            
            if (nome == null || nome.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "Nome do morador de rua é obrigatório");
                return ResponseEntity.badRequest().body(error);
            }
            
            MoradorRua moradorRua = moradorRuaService.registrarRetirada(nome.trim(), rg, descricao);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Retirada de morador de rua registrada com sucesso");
            response.put("id_morador", moradorRua.getId());
            response.put("nome_morador", moradorRua.getNome());
            response.put("data_retirada", moradorRua.getDataRetirada());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao registrar retirada de morador de rua: " + e.getMessage());
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
    
    @GetMapping("/moradores-rua")
    public ResponseEntity<List<MoradorRua>> listarTodasRetiradasMoradorRua() {
        List<MoradorRua> moradores = moradorRuaService.findAll();
        return ResponseEntity.ok(moradores);
    }
    
    @GetMapping("/moradores-rua/buscar/nome")
    public ResponseEntity<List<MoradorRua>> listarMoradoresRuaPorNome(@RequestParam String nome) {
        List<MoradorRua> moradores = moradorRuaService.findByNomeContaining(nome);
        return ResponseEntity.ok(moradores);
    }
    
    @GetMapping("/moradores-rua/periodo")
    public ResponseEntity<List<MoradorRua>> listarRetiradasMoradorRuaPorPeriodo(
            @RequestParam("dataInicio") String dataInicio,
            @RequestParam("dataFim") String dataFim) {
        
        LocalDateTime inicio = LocalDateTime.parse(dataInicio);
        LocalDateTime fim = LocalDateTime.parse(dataFim);
        
        List<MoradorRua> moradores = moradorRuaService.findByDataRetiradaBetween(inicio, fim);
        return ResponseEntity.ok(moradores);
    }
    
    @GetMapping("/total")
    public ResponseEntity<Map<String, Long>> estatisticas() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total_retiradas", retiradaService.count());
        stats.put("total_moradores_rua", moradorRuaService.count());
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
    
    @GetMapping("/relatorio-diario")
    public ResponseEntity<Map<String, Object>> relatorioDiario(@RequestParam(required = false) String data) {
        try {
            LocalDate dataRelatorio;
            if (data != null && !data.trim().isEmpty()) {
                dataRelatorio = LocalDate.parse(data);
            } else {
                dataRelatorio = LocalDate.now();
            }
            
            LocalDateTime inicioDoDia = LocalDateTime.of(dataRelatorio, LocalTime.MIN);
            LocalDateTime fimDoDia = LocalDateTime.of(dataRelatorio, LocalTime.MAX);
            
            long totalClientes = retiradaService.countByDataRetiradaBetween(inicioDoDia, fimDoDia);
            long totalMoradoresRua = moradorRuaService.countByDataRetiradaBetween(inicioDoDia, fimDoDia);
            long totalGeral = totalClientes + totalMoradoresRua;
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", dataRelatorio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            response.put("total_clientes", totalClientes);
            response.put("total_moradores_rua", totalMoradoresRua);
            response.put("total_geral", totalGeral);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("erro", "Erro ao gerar relatório diário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping("/exportar/relatorio-diario/excel")
    public ResponseEntity<byte[]> exportarRelatorioDiarioExcel(@RequestParam(required = false) String data) {
        try {
            LocalDate dataRelatorio;
            if (data != null && !data.trim().isEmpty()) {
                dataRelatorio = LocalDate.parse(data);
            } else {
                dataRelatorio = LocalDate.now();
            }
            
            LocalDateTime inicioDoDia = LocalDateTime.of(dataRelatorio, LocalTime.MIN);
            LocalDateTime fimDoDia = LocalDateTime.of(dataRelatorio, LocalTime.MAX);
            
            List<Retirada> retiradas = retiradaService.findByDataRetiradaBetween(inicioDoDia, fimDoDia);
            List<MoradorRua> moradoresRua = moradorRuaService.findByDataRetiradaBetween(inicioDoDia, fimDoDia);
            
            byte[] excelContent = excelExportService.exportRelatorioDiarioToExcel(retiradas, moradoresRua, dataRelatorio);
            
            String filename = "relatorio_diario_marmitas_" + 
                dataRelatorio.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
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
            error.put("erro", "Erro ao gerar arquivo Excel do relatório diário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
