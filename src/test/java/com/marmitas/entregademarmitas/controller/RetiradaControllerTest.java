package com.marmitas.entregademarmitas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marmitas.entregademarmitas.model.Cliente;
import com.marmitas.entregademarmitas.model.MoradorRua;
import com.marmitas.entregademarmitas.model.Retirada;
import com.marmitas.entregademarmitas.service.ExcelExportService;
import com.marmitas.entregademarmitas.service.MoradorRuaService;
import com.marmitas.entregademarmitas.service.RetiradaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RetiradaController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {
            com.marmitas.entregademarmitas.security.JwtAuthenticationFilter.class,
            com.marmitas.entregademarmitas.security.SecurityConfig.class
        }
    ))
@AutoConfigureMockMvc(addFilters = false)
class RetiradaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RetiradaService retiradaService;

    @MockBean
    private MoradorRuaService moradorRuaService;

    @MockBean
    private ExcelExportService excelExportService;

    private Retirada retirada;
    private MoradorRua moradorRua;

    @BeforeEach
    void setUp() {
        LocalDateTime agora = LocalDateTime.now();
        
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");
        cliente.setEndereco("Rua Teste, 123");
        cliente.setRg("123456789");
        
        retirada = new Retirada();
        retirada.setId(1L);
        retirada.setCliente(cliente);
        retirada.setDataRetirada(agora);
        
        moradorRua = new MoradorRua();
        moradorRua.setId(1L);
        moradorRua.setNome("João Silva");
        moradorRua.setRg("123456789");
        moradorRua.setDescricao("Descrição teste");
        moradorRua.setDataRetirada(agora);
        moradorRua.setDataRegistro(agora);
    }

    @Test
    @DisplayName("Deve registrar retirada por código com sucesso")
    void testRegistrarRetiradaPorCodigo() throws Exception {
        when(retiradaService.registrarRetiradaPorCodigo(anyLong())).thenReturn(retirada);

        Map<String, String> request = new HashMap<>();
        request.put("codigo", "123");
        request.put("moradorRua", "false");

        mockMvc.perform(post("/api/retiradas/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Retirada registrada com sucesso"))
                .andExpect(jsonPath("$.id_retirada").value(1))
                .andExpect(jsonPath("$.data_retirada").exists());

        verify(retiradaService).registrarRetiradaPorCodigo(123L);
    }

    @Test
    @DisplayName("Deve registrar retirada por nome com sucesso")
    void testRegistrarRetiradaPorNome() throws Exception {
        when(retiradaService.registrarRetiradaPorNome(anyString())).thenReturn(retirada);

        Map<String, String> request = new HashMap<>();
        request.put("nome", "João Silva");
        request.put("moradorRua", "false");

        mockMvc.perform(post("/api/retiradas/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Retirada registrada com sucesso"))
                .andExpect(jsonPath("$.id_retirada").value(1));

        verify(retiradaService).registrarRetiradaPorNome("João Silva");
    }

    @Test
    @DisplayName("Deve registrar retirada por RG com sucesso")
    void testRegistrarRetiradaPorRg() throws Exception {
        when(retiradaService.registrarRetiradaPorRg(anyString())).thenReturn(retirada);

        Map<String, String> request = new HashMap<>();
        request.put("rg", "123456789");
        request.put("moradorRua", "false");

        mockMvc.perform(post("/api/retiradas/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Retirada registrada com sucesso"))
                .andExpect(jsonPath("$.id_retirada").value(1));

        verify(retiradaService).registrarRetiradaPorRg("123456789");
    }

    @Test
    @DisplayName("Deve registrar retirada de morador de rua com sucesso")
    void testRegistrarRetiradaMoradorRua() throws Exception {
        when(moradorRuaService.registrarRetirada(anyString(), anyString(), anyString()))
            .thenReturn(moradorRua);

        Map<String, String> request = new HashMap<>();
        request.put("nome", "João Silva");
        request.put("rg", "123456789");
        request.put("descricao", "Descrição teste");
        request.put("moradorRua", "true");

        mockMvc.perform(post("/api/retiradas/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Retirada de morador de rua registrada com sucesso"))
                .andExpect(jsonPath("$.id_morador").value(1))
                .andExpect(jsonPath("$.nome_morador").value("João Silva"));

        verify(moradorRuaService).registrarRetirada("João Silva", "123456789", "Descrição teste");
    }

    @Test
    @DisplayName("Deve retornar erro quando não fornecer identificação")
    void testRegistrarRetiradaSemIdentificacao() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("moradorRua", "false");

        mockMvc.perform(post("/api/retiradas/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("É necessário fornecer código, nome completo ou RG do cliente"));
    }

    @Test
    @DisplayName("Deve retornar erro quando código é inválido")
    void testRegistrarRetiradaCodigoInvalido() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("codigo", "abc");
        request.put("moradorRua", "false");

        mockMvc.perform(post("/api/retiradas/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Código do cliente deve ser um número válido"));
    }

    @Test
    @DisplayName("Deve retornar erro quando serviço lança RuntimeException")
    void testRegistrarRetiradaRuntimeException() throws Exception {
        when(retiradaService.registrarRetiradaPorCodigo(anyLong()))
            .thenThrow(new RuntimeException("Cliente não encontrado"));

        Map<String, String> request = new HashMap<>();
        request.put("codigo", "123");
        request.put("moradorRua", "false");

        mockMvc.perform(post("/api/retiradas/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Cliente não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar erro quando nome do morador de rua é obrigatório")
    void testRegistrarRetiradaMoradorRuaSemNome() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("rg", "123456789");
        request.put("descricao", "Descrição teste");
        request.put("moradorRua", "true");

        mockMvc.perform(post("/api/retiradas/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("Nome do morador de rua é obrigatório"));
    }

    @Test
    @DisplayName("Deve listar todas as retiradas")
    void testListarTodasRetiradas() throws Exception {
        List<Retirada> retiradas = Arrays.asList(retirada);
        when(retiradaService.findAll()).thenReturn(retiradas);

        mockMvc.perform(get("/api/retiradas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(retiradaService).findAll();
    }

    @Test
    @DisplayName("Deve listar retiradas por cliente")
    void testListarRetiradasPorCliente() throws Exception {
        List<Retirada> retiradas = Arrays.asList(retirada);
        when(retiradaService.findByClienteId(anyLong())).thenReturn(retiradas);

        mockMvc.perform(get("/api/retiradas/cliente/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(retiradaService).findByClienteId(123L);
    }

    @Test
    @DisplayName("Deve listar retiradas por período")
    void testListarRetiradasPorPeriodo() throws Exception {
        List<Retirada> retiradas = Arrays.asList(retirada);
        when(retiradaService.findByDataRetiradaBetween(any(), any())).thenReturn(retiradas);

        String dataInicio = "2023-01-01T10:00:00";
        String dataFim = "2023-01-01T18:00:00";

        mockMvc.perform(get("/api/retiradas/periodo")
                .param("dataInicio", dataInicio)
                .param("dataFim", dataFim))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(retiradaService).findByDataRetiradaBetween(any(), any());
    }

    @Test
    @DisplayName("Deve listar todas as retiradas de morador de rua")
    void testListarTodasRetiradasMoradorRua() throws Exception {
        List<MoradorRua> moradores = Arrays.asList(moradorRua);
        when(moradorRuaService.findAll()).thenReturn(moradores);

        mockMvc.perform(get("/api/retiradas/moradores-rua"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("João Silva"));

        verify(moradorRuaService).findAll();
    }

    @Test
    @DisplayName("Deve listar moradores de rua por nome")
    void testListarMoradoresRuaPorNome() throws Exception {
        List<MoradorRua> moradores = Arrays.asList(moradorRua);
        when(moradorRuaService.findByNomeContaining(anyString())).thenReturn(moradores);

        mockMvc.perform(get("/api/retiradas/moradores-rua/buscar/nome")
                .param("nome", "João"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("João Silva"));

        verify(moradorRuaService).findByNomeContaining("João");
    }

    @Test
    @DisplayName("Deve listar retiradas de morador de rua por período")
    void testListarRetiradasMoradorRuaPorPeriodo() throws Exception {
        List<MoradorRua> moradores = Arrays.asList(moradorRua);
        when(moradorRuaService.findByDataRetiradaBetween(any(), any())).thenReturn(moradores);

        String dataInicio = "2023-01-01T10:00:00";
        String dataFim = "2023-01-01T18:00:00";

        mockMvc.perform(get("/api/retiradas/moradores-rua/periodo")
                .param("dataInicio", dataInicio)
                .param("dataFim", dataFim))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(moradorRuaService).findByDataRetiradaBetween(any(), any());
    }

    @Test
    @DisplayName("Deve retornar estatísticas")
    void testEstatisticas() throws Exception {
        when(retiradaService.count()).thenReturn(10L);
        when(moradorRuaService.count()).thenReturn(5L);

        mockMvc.perform(get("/api/retiradas/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_retiradas").value(10))
                .andExpect(jsonPath("$.total_moradores_rua").value(5));

        verify(retiradaService).count();
        verify(moradorRuaService).count();
    }

    @Test
    @DisplayName("Deve exportar retiradas para Excel")
    void testExportarRetiradasExcel() throws Exception {
        List<Retirada> retiradas = Arrays.asList(retirada);
        when(retiradaService.findAll()).thenReturn(retiradas);
        when(excelExportService.exportRetiradasToExcel(any())).thenReturn(new byte[0]);

        mockMvc.perform(get("/api/retiradas/exportar/excel"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Type", "application/octet-stream"));

        verify(retiradaService).findAll();
        verify(excelExportService).exportRetiradasToExcel(retiradas);
    }

    @Test
    @DisplayName("Deve exportar retiradas para Excel por período")
    void testExportarRetiradasExcelPorPeriodo() throws Exception {
        List<Retirada> retiradas = Arrays.asList(retirada);
        when(retiradaService.findByDataRetiradaBetween(any(), any())).thenReturn(retiradas);
        when(excelExportService.exportRetiradasToExcel(any())).thenReturn(new byte[0]);

        String dataInicio = "2023-01-01T10:00:00";
        String dataFim = "2023-01-01T18:00:00";

        mockMvc.perform(get("/api/retiradas/exportar/excel")
                .param("dataInicio", dataInicio)
                .param("dataFim", dataFim))
                .andExpect(status().isOk());

        verify(retiradaService).findByDataRetiradaBetween(any(), any());
        verify(excelExportService).exportRetiradasToExcel(retiradas);
    }

    @Test
    @DisplayName("Deve retornar erro ao exportar Excel quando ocorre exceção")
    void testExportarRetiradasExcelErro() throws Exception {
        when(retiradaService.findAll()).thenReturn(Arrays.asList(retirada));
        when(excelExportService.exportRetiradasToExcel(any()))
            .thenThrow(new RuntimeException("Erro ao gerar Excel"));

        mockMvc.perform(get("/api/retiradas/exportar/excel"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Deve retornar relatório diário com data atual quando não informada")
    void testRelatorioDiarioDataAtual() throws Exception {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioDoDia = LocalDateTime.of(hoje, LocalTime.MIN);
        LocalDateTime fimDoDia = LocalDateTime.of(hoje, LocalTime.MAX);

        when(retiradaService.countByDataRetiradaBetween(inicioDoDia, fimDoDia)).thenReturn(10L);
        when(moradorRuaService.countByDataRetiradaBetween(inicioDoDia, fimDoDia)).thenReturn(5L);

        mockMvc.perform(get("/api/retiradas/relatorio-diario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.total_clientes").value(10))
                .andExpect(jsonPath("$.total_moradores_rua").value(5))
                .andExpect(jsonPath("$.total_geral").value(15));

        verify(retiradaService).countByDataRetiradaBetween(inicioDoDia, fimDoDia);
        verify(moradorRuaService).countByDataRetiradaBetween(inicioDoDia, fimDoDia);
    }

    @Test
    @DisplayName("Deve retornar relatório diário com data informada")
    void testRelatorioDiarioDataInformada() throws Exception {
        LocalDate dataInformada = LocalDate.of(2023, 6, 4);
        LocalDateTime inicioDoDia = LocalDateTime.of(dataInformada, LocalTime.MIN);
        LocalDateTime fimDoDia = LocalDateTime.of(dataInformada, LocalTime.MAX);

        when(retiradaService.countByDataRetiradaBetween(inicioDoDia, fimDoDia)).thenReturn(8L);
        when(moradorRuaService.countByDataRetiradaBetween(inicioDoDia, fimDoDia)).thenReturn(3L);

        mockMvc.perform(get("/api/retiradas/relatorio-diario")
                .param("data", "2023-06-04"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("04/06/2023"))
                .andExpect(jsonPath("$.total_clientes").value(8))
                .andExpect(jsonPath("$.total_moradores_rua").value(3))
                .andExpect(jsonPath("$.total_geral").value(11));

        verify(retiradaService).countByDataRetiradaBetween(inicioDoDia, fimDoDia);
        verify(moradorRuaService).countByDataRetiradaBetween(inicioDoDia, fimDoDia);
    }

    @Test
    @DisplayName("Deve retornar erro ao gerar relatório diário quando ocorre exceção")
    void testRelatorioDiarioErro() throws Exception {
        when(retiradaService.countByDataRetiradaBetween(any(), any()))
            .thenThrow(new RuntimeException("Erro ao contar retiradas"));

        mockMvc.perform(get("/api/retiradas/relatorio-diario"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.erro").value("Erro ao gerar relatório diário: Erro ao contar retiradas"));
    }

    @Test
    @DisplayName("Deve exportar relatório diário para Excel com data atual")
    void testExportarRelatorioDiarioExcelDataAtual() throws Exception {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioDoDia = LocalDateTime.of(hoje, LocalTime.MIN);
        LocalDateTime fimDoDia = LocalDateTime.of(hoje, LocalTime.MAX);

        List<Retirada> retiradas = Arrays.asList(retirada);
        List<MoradorRua> moradoresRua = Arrays.asList(moradorRua);

        when(retiradaService.findByDataRetiradaBetween(inicioDoDia, fimDoDia)).thenReturn(retiradas);
        when(moradorRuaService.findByDataRetiradaBetween(inicioDoDia, fimDoDia)).thenReturn(moradoresRua);
        when(excelExportService.exportRelatorioDiarioToExcel(retiradas, moradoresRua, hoje))
            .thenReturn(new byte[0]);

        mockMvc.perform(get("/api/retiradas/exportar/relatorio-diario/excel"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Type", "application/octet-stream"));

        verify(retiradaService).findByDataRetiradaBetween(inicioDoDia, fimDoDia);
        verify(moradorRuaService).findByDataRetiradaBetween(inicioDoDia, fimDoDia);
        verify(excelExportService).exportRelatorioDiarioToExcel(retiradas, moradoresRua, hoje);
    }

    @Test
    @DisplayName("Deve exportar relatório diário para Excel com data informada")
    void testExportarRelatorioDiarioExcelDataInformada() throws Exception {
        LocalDate dataInformada = LocalDate.of(2023, 6, 4);
        LocalDateTime inicioDoDia = LocalDateTime.of(dataInformada, LocalTime.MIN);
        LocalDateTime fimDoDia = LocalDateTime.of(dataInformada, LocalTime.MAX);

        List<Retirada> retiradas = Arrays.asList(retirada);
        List<MoradorRua> moradoresRua = Arrays.asList(moradorRua);

        when(retiradaService.findByDataRetiradaBetween(inicioDoDia, fimDoDia)).thenReturn(retiradas);
        when(moradorRuaService.findByDataRetiradaBetween(inicioDoDia, fimDoDia)).thenReturn(moradoresRua);
        when(excelExportService.exportRelatorioDiarioToExcel(retiradas, moradoresRua, dataInformada))
            .thenReturn(new byte[0]);

        mockMvc.perform(get("/api/retiradas/exportar/relatorio-diario/excel")
                .param("data", "2023-06-04"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"));

        verify(retiradaService).findByDataRetiradaBetween(inicioDoDia, fimDoDia);
        verify(moradorRuaService).findByDataRetiradaBetween(inicioDoDia, fimDoDia);
        verify(excelExportService).exportRelatorioDiarioToExcel(retiradas, moradoresRua, dataInformada);
    }

    @Test
    @DisplayName("Deve retornar erro ao exportar relatório diário Excel quando ocorre exceção")
    void testExportarRelatorioDiarioExcelErro() throws Exception {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioDoDia = LocalDateTime.of(hoje, LocalTime.MIN);
        LocalDateTime fimDoDia = LocalDateTime.of(hoje, LocalTime.MAX);

        when(retiradaService.findByDataRetiradaBetween(inicioDoDia, fimDoDia)).thenReturn(Arrays.asList(retirada));
        when(moradorRuaService.findByDataRetiradaBetween(inicioDoDia, fimDoDia)).thenReturn(Arrays.asList(moradorRua));
        when(excelExportService.exportRelatorioDiarioToExcel(any(), any(), any()))
            .thenThrow(new RuntimeException("Erro ao gerar Excel"));

        mockMvc.perform(get("/api/retiradas/exportar/relatorio-diario/excel"))
                .andExpect(status().isInternalServerError());
    }
}
