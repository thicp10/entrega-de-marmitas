package com.marmitas.entregademarmitas.service;

import com.marmitas.entregademarmitas.model.MoradorRua;
import com.marmitas.entregademarmitas.repository.MoradorRuaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoradorRuaServiceTest {

    @Mock
    private MoradorRuaRepository moradorRuaRepository;

    @InjectMocks
    private MoradorRuaService moradorRuaService;

    private MoradorRua moradorRua;

    @BeforeEach
    void setUp() {
        moradorRua = new MoradorRua();
        moradorRua.setId(1L);
        moradorRua.setNome("João Silva");
        moradorRua.setRg("123456789");
        moradorRua.setDescricao("Descrição teste");
        moradorRua.setDataRetirada(LocalDateTime.now());
        moradorRua.setDataRegistro(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve registrar retirada com sucesso usando RG")
    void testRegistrarRetiradaComRg() {
        when(moradorRuaRepository.existsByRgAndDataRetiradaBetween(anyString(), any(), any()))
            .thenReturn(false);
        when(moradorRuaRepository.save(any(MoradorRua.class))).thenReturn(moradorRua);

        MoradorRua resultado = moradorRuaService.registrarRetirada(
            "João Silva", "123456789", "Descrição teste");

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertEquals("123456789", resultado.getRg());
        assertEquals("Descrição teste", resultado.getDescricao());
        verify(moradorRuaRepository).existsByRgAndDataRetiradaBetween(eq("123456789"), any(), any());
        verify(moradorRuaRepository).save(any(MoradorRua.class));
    }

    @Test
    @DisplayName("Deve registrar retirada com sucesso usando nome quando RG é nulo")
    void testRegistrarRetiradaSemRg() {
        when(moradorRuaRepository.existsByNomeAndDataRetiradaBetween(anyString(), any(), any()))
            .thenReturn(false);
        when(moradorRuaRepository.save(any(MoradorRua.class))).thenReturn(moradorRua);

        MoradorRua resultado = moradorRuaService.registrarRetirada(
            "João Silva", null, "Descrição teste");

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertNull(resultado.getRg());
        assertEquals("Descrição teste", resultado.getDescricao());
        verify(moradorRuaRepository).existsByNomeAndDataRetiradaBetween(eq("João Silva"), any(), any());
        verify(moradorRuaRepository).save(any(MoradorRua.class));
    }

    @Test
    @DisplayName("Deve registrar retirada com sucesso usando nome quando RG é vazio")
    void testRegistrarRetiradaComRgVazio() {
        when(moradorRuaRepository.existsByNomeAndDataRetiradaBetween(anyString(), any(), any()))
            .thenReturn(false);
        when(moradorRuaRepository.save(any(MoradorRua.class))).thenReturn(moradorRua);

        MoradorRua resultado = moradorRuaService.registrarRetirada(
            "João Silva", "", "Descrição teste");

        assertNotNull(resultado);
        assertEquals("João Silva", resultado.getNome());
        assertNull(resultado.getRg());
        assertEquals("Descrição teste", resultado.getDescricao());
        verify(moradorRuaRepository).existsByNomeAndDataRetiradaBetween(eq("João Silva"), any(), any());
        verify(moradorRuaRepository).save(any(MoradorRua.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando morador já retirou com RG hoje")
    void testRegistrarRetiradaRgJaExisteHoje() {
        when(moradorRuaRepository.existsByRgAndDataRetiradaBetween(anyString(), any(), any()))
            .thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            moradorRuaService.registrarRetirada("João Silva", "123456789", "Descrição teste");
        });

        assertEquals("Morador de rua com RG 123456789 já retirou uma marmita hoje.", exception.getMessage());
        verify(moradorRuaRepository).existsByRgAndDataRetiradaBetween(eq("123456789"), any(), any());
        verify(moradorRuaRepository, never()).save(any(MoradorRua.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando morador já retirou com nome hoje")
    void testRegistrarRetiradaNomeJaExisteHoje() {
        when(moradorRuaRepository.existsByNomeAndDataRetiradaBetween(anyString(), any(), any()))
            .thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            moradorRuaService.registrarRetirada("João Silva", null, "Descrição teste");
        });

        assertEquals("Morador de rua com nome João Silva já retirou uma marmita hoje.", exception.getMessage());
        verify(moradorRuaRepository).existsByNomeAndDataRetiradaBetween(eq("João Silva"), any(), any());
        verify(moradorRuaRepository, never()).save(any(MoradorRua.class));
    }

    @Test
    @DisplayName("Deve trim nos campos ao registrar retirada")
    void testRegistrarRetiradaComTrim() {
        MoradorRua moradorSalvo = new MoradorRua();
        moradorSalvo.setId(1L);
        moradorSalvo.setNome("João Silva");
        moradorSalvo.setRg("123456789");
        moradorSalvo.setDescricao("Descrição teste");

        when(moradorRuaRepository.existsByRgAndDataRetiradaBetween(anyString(), any(), any()))
            .thenReturn(false);
        when(moradorRuaRepository.save(any(MoradorRua.class))).thenReturn(moradorSalvo);

        MoradorRua resultado = moradorRuaService.registrarRetirada(
            "  João Silva  ", "  123456789  ", "  Descrição teste  ");

        verify(moradorRuaRepository).save(argThat(morador -> 
            morador.getNome().equals("João Silva") &&
            morador.getRg().equals("123456789") &&
            morador.getDescricao().equals("Descrição teste")
        ));
    }

    @Test
    @DisplayName("Deve trim no nome quando RG é nulo")
    void testRegistrarRetiradaTrimNomeSemRg() {
        MoradorRua moradorSalvo = new MoradorRua();
        moradorSalvo.setId(1L);
        moradorSalvo.setNome("João Silva");

        when(moradorRuaRepository.existsByNomeAndDataRetiradaBetween(anyString(), any(), any()))
            .thenReturn(false);
        when(moradorRuaRepository.save(any(MoradorRua.class))).thenReturn(moradorSalvo);

        moradorRuaService.registrarRetirada("  João Silva  ", null, null);

        verify(moradorRuaRepository).save(argThat(morador -> 
            morador.getNome().equals("João Silva")
        ));
    }

    @Test
    @DisplayName("Deve retornar todos os moradores")
    void testFindAll() {
        List<MoradorRua> moradores = Arrays.asList(moradorRua);
        when(moradorRuaRepository.findAll()).thenReturn(moradores);

        List<MoradorRua> resultado = moradorRuaService.findAll();

        assertEquals(1, resultado.size());
        assertEquals(moradorRua, resultado.get(0));
        verify(moradorRuaRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar moradores por nome contendo")
    void testFindByNomeContaining() {
        List<MoradorRua> moradores = Arrays.asList(moradorRua);
        when(moradorRuaRepository.findByNomeContainingIgnoreCase("João")).thenReturn(moradores);

        List<MoradorRua> resultado = moradorRuaService.findByNomeContaining("João");

        assertEquals(1, resultado.size());
        assertEquals(moradorRua, resultado.get(0));
        verify(moradorRuaRepository).findByNomeContainingIgnoreCase("João");
    }

    @Test
    @DisplayName("Deve buscar moradores por período de data")
    void testFindByDataRetiradaBetween() {
        LocalDateTime dataInicio = LocalDateTime.now().minusDays(1);
        LocalDateTime dataFim = LocalDateTime.now().plusDays(1);
        List<MoradorRua> moradores = Arrays.asList(moradorRua);
        
        when(moradorRuaRepository.findByDataRetiradaBetween(dataInicio, dataFim))
            .thenReturn(moradores);

        List<MoradorRua> resultado = moradorRuaService.findByDataRetiradaBetween(dataInicio, dataFim);

        assertEquals(1, resultado.size());
        assertEquals(moradorRua, resultado.get(0));
        verify(moradorRuaRepository).findByDataRetiradaBetween(dataInicio, dataFim);
    }

    @Test
    @DisplayName("Deve contar total de moradores")
    void testCount() {
        when(moradorRuaRepository.count()).thenReturn(5L);

        long resultado = moradorRuaService.count();

        assertEquals(5L, resultado);
        verify(moradorRuaRepository).count();
    }

    @Test
    @DisplayName("Deve tratar descrição nula corretamente")
    void testRegistrarRetiradaDescricaoNula() {
        MoradorRua moradorSalvo = new MoradorRua();
        moradorSalvo.setId(1L);
        moradorSalvo.setNome("João Silva");
        moradorSalvo.setRg("123456789");
        moradorSalvo.setDescricao(null);

        when(moradorRuaRepository.existsByRgAndDataRetiradaBetween(anyString(), any(), any()))
            .thenReturn(false);
        when(moradorRuaRepository.save(any(MoradorRua.class))).thenReturn(moradorSalvo);

        MoradorRua resultado = moradorRuaService.registrarRetirada("João Silva", "123456789", null);

        verify(moradorRuaRepository).save(argThat(morador -> 
            morador.getDescricao() == null
        ));
    }

    @Test
    @DisplayName("Deve tratar descrição vazia corretamente")
    void testRegistrarRetiradaDescricaoVazia() {
        MoradorRua moradorSalvo = new MoradorRua();
        moradorSalvo.setId(1L);
        moradorSalvo.setNome("João Silva");
        moradorSalvo.setRg("123456789");
        moradorSalvo.setDescricao("");

        when(moradorRuaRepository.existsByRgAndDataRetiradaBetween(anyString(), any(), any()))
            .thenReturn(false);
        when(moradorRuaRepository.save(any(MoradorRua.class))).thenReturn(moradorSalvo);

        MoradorRua resultado = moradorRuaService.registrarRetirada("João Silva", "123456789", "");

        verify(moradorRuaRepository).save(argThat(morador -> 
            morador.getDescricao().equals("")
        ));
    }
}
