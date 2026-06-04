package com.marmitas.entregademarmitas.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MoradorRuaTest {

    private MoradorRua moradorRua;

    @BeforeEach
    void setUp() {
        moradorRua = new MoradorRua();
    }

    @Test
    @DisplayName("Deve criar MoradorRua com construtor padrão")
    void testDefaultConstructor() {
        assertNotNull(moradorRua);
        assertNull(moradorRua.getId());
        assertNull(moradorRua.getNome());
        assertNull(moradorRua.getRg());
        assertNull(moradorRua.getDescricao());
        assertNull(moradorRua.getDataRetirada());
        assertNull(moradorRua.getDataRegistro());
    }

    @Test
    @DisplayName("Deve criar MoradorRua com construtor com parâmetros")
    void testParameterizedConstructor() {
        LocalDateTime beforeCreation = LocalDateTime.now();
        
        MoradorRua morador = new MoradorRua("João Silva", "123456789", "Descrição teste");
        
        assertEquals("João Silva", morador.getNome());
        assertEquals("123456789", morador.getRg());
        assertEquals("Descrição teste", morador.getDescricao());
        assertNotNull(morador.getDataRetirada());
        assertTrue(morador.getDataRetirada().isAfter(beforeCreation) || 
                  morador.getDataRetirada().isEqual(beforeCreation));
    }

    @Test
    @DisplayName("Deve criar MoradorRua com construtor all args")
    void testAllArgsConstructor() {
        LocalDateTime dataRetirada = LocalDateTime.now();
        LocalDateTime dataRegistro = LocalDateTime.now().minusDays(1);
        
        MoradorRua morador = new MoradorRua(
            1L, 
            "Maria Santos", 
            "987654321", 
            "Descrição completa", 
            dataRetirada, 
            dataRegistro
        );
        
        assertEquals(1L, morador.getId());
        assertEquals("Maria Santos", morador.getNome());
        assertEquals("987654321", morador.getRg());
        assertEquals("Descrição completa", morador.getDescricao());
        assertEquals(dataRetirada, morador.getDataRetirada());
        assertEquals(dataRegistro, morador.getDataRegistro());
    }

    @Test
    @DisplayName("Deve criar MoradorRua com builder")
    void testBuilder() {
        LocalDateTime dataRetirada = LocalDateTime.now();
        
        MoradorRua morador = MoradorRua.builder()
            .id(2L)
            .nome("Pedro Costa")
            .rg("111222333")
            .descricao("Teste builder")
            .dataRetirada(dataRetirada)
            .dataRegistro(LocalDateTime.now())
            .build();
        
        assertEquals(2L, morador.getId());
        assertEquals("Pedro Costa", morador.getNome());
        assertEquals("111222333", morador.getRg());
        assertEquals("Teste builder", morador.getDescricao());
        assertEquals(dataRetirada, morador.getDataRetirada());
    }

    @Test
    @DisplayName("Deve preencher dataRegistro automaticamente quando nulo")
    void testPrePersist() {
        moradorRua.setNome("Teste");
        moradorRua.setDataRetirada(LocalDateTime.now());
        
        moradorRua.prePersist();
        
        assertNotNull(moradorRua.getDataRegistro());
        assertTrue(moradorRua.getDataRegistro().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    @DisplayName("Não deve alterar dataRegistro quando já preenchido")
    void testPrePersistWithExistingData() {
        LocalDateTime existingData = LocalDateTime.now().minusHours(1);
        moradorRua.setDataRegistro(existingData);
        
        moradorRua.prePersist();
        
        assertEquals(existingData, moradorRua.getDataRegistro());
    }

    @Test
    @DisplayName("Deve permitir setter e getter para todos os campos")
    void testSettersAndGetters() {
        LocalDateTime dataRetirada = LocalDateTime.now();
        LocalDateTime dataRegistro = LocalDateTime.now();
        
        moradorRua.setId(10L);
        moradorRua.setNome("Ana Silva");
        moradorRua.setRg("555666777");
        moradorRua.setDescricao("Teste setters");
        moradorRua.setDataRetirada(dataRetirada);
        moradorRua.setDataRegistro(dataRegistro);
        
        assertEquals(10L, moradorRua.getId());
        assertEquals("Ana Silva", moradorRua.getNome());
        assertEquals("555666777", moradorRua.getRg());
        assertEquals("Teste setters", moradorRua.getDescricao());
        assertEquals(dataRetirada, moradorRua.getDataRetirada());
        assertEquals(dataRegistro, moradorRua.getDataRegistro());
    }

    @Test
    @DisplayName("Deve aceitar valores nulos em campos opcionais")
    void testNullableFields() {
        moradorRua.setId(1L);
        moradorRua.setNome("Nome Obrigatório");
        moradorRua.setDataRetirada(LocalDateTime.now());
        
        assertNotNull(moradorRua.getNome());
        assertNull(moradorRua.getRg());
        assertNull(moradorRua.getDescricao());
        assertNotNull(moradorRua.getDataRetirada());
    }

    @Test
    @DisplayName("Deve testar equals e hashCode")
    void testEqualsAndHashCode() {
        MoradorRua morador1 = new MoradorRua();
        morador1.setId(1L);
        
        MoradorRua morador2 = new MoradorRua();
        morador2.setId(1L);
        
        MoradorRua morador3 = new MoradorRua();
        morador3.setId(2L);
        
        assertEquals(morador1, morador2);
        assertEquals(morador1.hashCode(), morador2.hashCode());
        assertNotEquals(morador1, morador3);
        assertNotEquals(morador1.hashCode(), morador3.hashCode());
    }

    @Test
    @DisplayName("Deve testar toString")
    void testToString() {
        moradorRua.setId(1L);
        moradorRua.setNome("Teste ToString");
        
        String toString = moradorRua.toString();
        
        assertTrue(toString.contains("MoradorRua"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("nome=Teste ToString"));
    }
}
