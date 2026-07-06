package com.marmitas.entregademarmitas.service;

import com.marmitas.entregademarmitas.model.ProducaoDiaria;
import com.marmitas.entregademarmitas.repository.MoradorRuaRepository;
import com.marmitas.entregademarmitas.repository.ProducaoDiariaRepository;
import com.marmitas.entregademarmitas.repository.RetiradaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class ProducaoDiariaService {

    @Autowired
    private ProducaoDiariaRepository producaoDiariaRepository;

    @Autowired
    private RetiradaRepository retiradaRepository;

    @Autowired
    private MoradorRuaRepository moradorRuaRepository;

    public ProducaoDiaria registrarProducao(LocalDate data, Integer quantidade) {
        if (quantidade == null || quantidade < 0) {
            throw new RuntimeException("Quantidade de marmitas deve ser um número igual ou maior que zero.");
        }

        long jaEntregue = totalEntregue(data);
        if (quantidade < jaEntregue) {
            throw new RuntimeException("A quantidade produzida (" + quantidade
                    + ") não pode ser menor que o total já entregue no dia (" + jaEntregue + ").");
        }

        ProducaoDiaria producao = producaoDiariaRepository.findByData(data)
                .orElseGet(() -> ProducaoDiaria.builder().data(data).build());
        producao.setQuantidade(quantidade);
        return producaoDiariaRepository.save(producao);
    }

    public Optional<ProducaoDiaria> buscarPorData(LocalDate data) {
        return producaoDiariaRepository.findByData(data);
    }

    public int quantidadeProduzida(LocalDate data) {
        return producaoDiariaRepository.findByData(data)
                .map(ProducaoDiaria::getQuantidade)
                .orElse(0);
    }

    public long totalEntregue(LocalDate data) {
        LocalDateTime inicioDoDia = LocalDateTime.of(data, LocalTime.MIN);
        LocalDateTime fimDoDia = LocalDateTime.of(data, LocalTime.MAX);
        long retiradas = retiradaRepository.countByDataRetiradaBetween(inicioDoDia, fimDoDia);
        long moradores = moradorRuaRepository.countByDataRetiradaBetween(inicioDoDia, fimDoDia);
        return retiradas + moradores;
    }

    public long saldoRestante(LocalDate data) {
        return quantidadeProduzida(data) - totalEntregue(data);
    }

    public void validarSaldoDisponivel() {
        LocalDate hoje = LocalDate.now();
        int produzida = quantidadeProduzida(hoje);
        if (produzida <= 0) {
            throw new RuntimeException("Nenhuma produção de marmitas foi cadastrada para hoje. "
                    + "Cadastre a quantidade de marmitas feitas antes de registrar as retiradas.");
        }
        if (saldoRestante(hoje) <= 0) {
            throw new RuntimeException("Não há marmitas disponíveis: todas as "
                    + produzida + " marmitas produzidas hoje já foram entregues.");
        }
    }
}
