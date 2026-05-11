package com.marmitas.entregademarmitas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "retiradas")
public class Retirada {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Cliente é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @NotNull(message = "Data de retirada é obrigatória")
    @Column(nullable = false)
    private LocalDateTime dataRetirada;
    
    public Retirada() {}
    
    public Retirada(Cliente cliente, LocalDateTime dataRetirada) {
        this.cliente = cliente;
        this.dataRetirada = dataRetirada;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public LocalDateTime getDataRetirada() {
        return dataRetirada;
    }
    
    public void setDataRetirada(LocalDateTime dataRetirada) {
        this.dataRetirada = dataRetirada;
    }
}
