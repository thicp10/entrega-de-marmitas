package com.marmitas.entregademarmitas.repository;

import com.marmitas.entregademarmitas.model.MoradorRua;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MoradorRuaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MoradorRuaRepository moradorRuaRepository;

    private MoradorRua moradorRua1;
    private MoradorRua moradorRua2;
    private MoradorRua moradorRua3;

    @BeforeEach
    void setUp() {
        moradorRuaRepository.deleteAll();
        
        LocalDateTime agora = LocalDateTime.now();
        
        moradorRua1 = new MoradorRua();
        moradorRua1.setNome("João Silva");
        moradorRua1.setRg("123456789");
        moradorRua1.setDescricao("Descrição 1");
        moradorRua1.setDataRetirada(agora);
        moradorRua1.setDataRegistro(agora);
        
        moradorRua2 = new MoradorRua();
        moradorRua2.setNome("Maria Santos");
        moradorRua2.setRg("987654321");
        moradorRua2.setDescricao("Descrição 2");
        moradorRua2.setDataRetirada(agora.plusHours(1));
        moradorRua2.setDataRegistro(agora);
        
        moradorRua3 = new MoradorRua();
        moradorRua3.setNome("Pedro Costa");
        moradorRua3.setRg("555666777");
        moradorRua3.setDescricao("Descrição 3");
        moradorRua3.setDataRetirada(agora.minusDays(1));
        moradorRua3.setDataRegistro(agora.minusDays(1));
        
        entityManager.persist(moradorRua1);
        entityManager.persist(moradorRua2);
        entityManager.persist(moradorRua3);
        entityManager.flush();
    }

    @Test
    @DisplayName("Deve buscar morador por nome contendo ignorando case")
    void testFindByNomeContainingIgnoreCase() {
        List<MoradorRua> resultado = moradorRuaRepository.findByNomeContainingIgnoreCase("joão");
        
        assertEquals(1, resultado.size());
        assertEquals("João Silva", resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Deve buscar morador por nome contendo em maiúsculas")
    void testFindByNomeContainingUpperCase() {
        List<MoradorRua> resultado = moradorRuaRepository.findByNomeContainingIgnoreCase("MARIA");
        
        assertEquals(1, resultado.size());
        assertEquals("Maria Santos", resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Deve buscar morador por nome contendo parcial")
    void testFindByNomeContainingPartial() {
        List<MoradorRua> resultado = moradorRuaRepository.findByNomeContainingIgnoreCase("Silva");
        
        assertEquals(1, resultado.size());
        assertEquals("João Silva", resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nome não encontrado")
    void testFindByNomeContainingNotFound() {
        List<MoradorRua> resultado = moradorRuaRepository.findByNomeContainingIgnoreCase("Inexistente");
        
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve buscar morador por RG")
    void testFindByRg() {
        List<MoradorRua> resultado = moradorRuaRepository.findByRg("123456789");
        
        assertEquals(1, resultado.size());
        assertEquals("João Silva", resultado.get(0).getNome());
        assertEquals("123456789", resultado.get(0).getRg());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando RG não encontrado")
    void testFindByRgNotFound() {
        List<MoradorRua> resultado = moradorRuaRepository.findByRg("999999999");
        
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve buscar moradores por período de data de retirada")
    void testFindByDataRetiradaBetween() {
        LocalDateTime inicio = LocalDateTime.now().minusHours(2);
        LocalDateTime fim = LocalDateTime.now().plusHours(2);
        
        List<MoradorRua> resultado = moradorRuaRepository.findByDataRetiradaBetween(inicio, fim);
        
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando período não contém retiradas")
    void testFindByDataRetiradaBetweenEmpty() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(2);
        LocalDateTime fim = LocalDateTime.now().minusDays(1).minusHours(1);
        
        List<MoradorRua> resultado = moradorRuaRepository.findByDataRetiradaBetween(inicio, fim);
        
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve verificar se existe morador por nome e período")
    void testExistsByNomeAndDataRetiradaBetween() {
        LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fim = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        
        boolean existe = moradorRuaRepository.existsByNomeAndDataRetiradaBetween("João Silva", inicio, fim);
        
        assertTrue(existe);
    }

    @Test
    @DisplayName("Deve retornar falso quando não existe morador por nome e período")
    void testExistsByNomeAndDataRetiradaBetweenFalse() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(2);
        LocalDateTime fim = LocalDateTime.now().minusDays(1);
        
        boolean existe = moradorRuaRepository.existsByNomeAndDataRetiradaBetween("João Silva", inicio, fim);
        
        assertFalse(existe);
    }

    @Test
    @DisplayName("Deve verificar se existe morador por RG e período")
    void testExistsByRgAndDataRetiradaBetween() {
        LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fim = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        
        boolean existe = moradorRuaRepository.existsByRgAndDataRetiradaBetween("123456789", inicio, fim);
        
        assertTrue(existe);
    }

    @Test
    @DisplayName("Deve retornar falso quando não existe morador por RG e período")
    void testExistsByRgAndDataRetiradaBetweenFalse() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(2);
        LocalDateTime fim = LocalDateTime.now().minusDays(1);
        
        boolean existe = moradorRuaRepository.existsByRgAndDataRetiradaBetween("123456789", inicio, fim);
        
        assertFalse(existe);
    }

    @Test
    @DisplayName("Deve encontrar morador por ID")
    void testFindById() {
        Optional<MoradorRua> resultado = moradorRuaRepository.findById(moradorRua1.getId());
        
        assertTrue(resultado.isPresent());
        assertEquals("João Silva", resultado.get().getNome());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando ID não encontrado")
    void testFindByIdNotFound() {
        Optional<MoradorRua> resultado = moradorRuaRepository.findById(999L);
        
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve contar todos os moradores")
    void testCount() {
        long total = moradorRuaRepository.count();
        
        assertEquals(3, total);
    }

    @Test
    @DisplayName("Deve salvar novo morador")
    void testSave() {
        MoradorRua novoMorador = new MoradorRua();
        novoMorador.setNome("Novo Morador");
        novoMorador.setRg("111222333");
        novoMorador.setDescricao("Novo");
        novoMorador.setDataRetirada(LocalDateTime.now());
        novoMorador.setDataRegistro(LocalDateTime.now());
        
        MoradorRua salvo = moradorRuaRepository.save(novoMorador);
        
        assertNotNull(salvo.getId());
        assertEquals("Novo Morador", salvo.getNome());
        assertEquals("111222333", salvo.getRg());
    }

    @Test
    @DisplayName("Deve deletar morador por ID")
    void testDeleteById() {
        Long id = moradorRua1.getId();
        
        moradorRuaRepository.deleteById(id);
        
        Optional<MoradorRua> resultado = moradorRuaRepository.findById(id);
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve encontrar todos os moradores")
    void testFindAll() {
        List<MoradorRua> todos = moradorRuaRepository.findAll();
        
        assertEquals(3, todos.size());
    }

    @Test
    @DisplayName("Deve contar moradores por período de data de retirada")
    void testCountByDataRetiradaBetween() {
        LocalDateTime inicio = LocalDateTime.now().minusHours(2);
        LocalDateTime fim = LocalDateTime.now().plusHours(2);
        
        long total = moradorRuaRepository.countByDataRetiradaBetween(inicio, fim);
        
        assertEquals(2, total);
    }

    @Test
    @DisplayName("Deve contar zero quando período não contém retiradas")
    void testCountByDataRetiradaBetweenZero() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(2);
        LocalDateTime fim = LocalDateTime.now().minusDays(1).minusHours(1);
        
        long total = moradorRuaRepository.countByDataRetiradaBetween(inicio, fim);
        
        assertEquals(0, total);
    }

    @Test
    @DisplayName("Deve contar moradores no período de um dia específico")
    void testCountByDataRetiradaBetweenDiaEspecifico() {
        LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime fim = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        
        long total = moradorRuaRepository.countByDataRetiradaBetween(inicio, fim);
        
        assertEquals(2, total);
    }
}
