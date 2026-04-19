package com.sport.managementsport.company.domain;

import com.sport.managementsport.common.domain.AuditableEntity;
import com.sport.managementsport.common.enums.EstadoCancha;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "Cancha")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cancha extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cancha_id")
    private Integer canchaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id", nullable = false)
    private Sucursal sucursal;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cancha", nullable = false, length = 20)
    private EstadoCancha estadoCancha = EstadoCancha.DISPONIBLE;

    @Column(name = "precio_hora", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioHora;
}
