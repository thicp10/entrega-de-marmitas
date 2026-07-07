package com.marmitas.entregademarmitas.controller;

import com.marmitas.entregademarmitas.model.ProducaoDiaria;
import com.marmitas.entregademarmitas.service.ProducaoDiariaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/producao")
public class ProducaoController {

    @Autowired
    private ProducaoDiariaService producaoDiariaService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarProducao(@RequestBody Map<String, Object> request) {
        try {
            LocalDate data = parseData(request.get("data"));

            Object quantidadeObj = request.get("quantidade");
            if (quantidadeObj == null) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "É necessário informar a quantidade de marmitas produzidas.");
                return ResponseEntity.badRequest().body(error);
            }
            Integer quantidade = Integer.parseInt(quantidadeObj.toString().trim());

            ProducaoDiaria producao = producaoDiariaService.registrarProducao(data, quantidade);

            return ResponseEntity.ok(montarResumo(producao.getData()));

        } catch (NumberFormatException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "A quantidade deve ser um número inteiro válido.");
            return ResponseEntity.badRequest().body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/dia")
    public ResponseEntity<?> producaoDoDia(@RequestParam(required = false) String data) {
        try {
            LocalDate dataConsulta = parseData(data);
            return ResponseEntity.ok(montarResumo(dataConsulta));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    private LocalDate parseData(Object data) {
        if (data == null || data.toString().trim().isEmpty()) {
            return LocalDate.now();
        }
        return LocalDate.parse(data.toString().trim());
    }

    private Map<String, Object> montarResumo(LocalDate data) {
        int produzida = producaoDiariaService.quantidadeProduzida(data);
        long entregue = producaoDiariaService.totalEntregue(data);
        long restante = producaoDiariaService.saldoRestante(data);

        Map<String, Object> response = new HashMap<>();
        response.put("data", data.toString());
        response.put("quantidade_produzida", produzida);
        response.put("total_entregue", entregue);
        response.put("saldo_restante", restante);
        return response;
    }
}
