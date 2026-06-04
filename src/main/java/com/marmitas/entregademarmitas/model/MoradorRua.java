package com.marmitas.entregademarmitas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Entity
@Table(name = "moradores_rua")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoradorRua {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;
    
    @Column
    private String rg;
    
    @Column
    private String descricao;
    
    @NotNull(message = "Data de retirada é obrigatória")
    @Column(nullable = false)
    private LocalDateTime dataRetirada;
    
    @Column(nullable = false)
    private LocalDateTime dataRegistro;
    
    @PrePersist
    public void prePersist() {
        if (dataRegistro == null) {
            dataRegistro = LocalDateTime.now();
        }
    }
    
    public MoradorRua(String nome, String rg, String descricao) {
        this.nome = nome;
        this.rg = rg;
        this.descricao = descricao;
        this.dataRetirada = LocalDateTime.now();
    }
}
