package com.sport.managementsport.events.domain;

import com.sport.managementsport.common.domain.AuditableEntity;
import com.sport.managementsport.common.enums.EstadoMantenimiento;
import com.sport.managementsport.common.enums.TipoMantenimiento;
import com.sport.managementsport.company.domain.Cancha;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Mantenimiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mantenimiento extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mantenimiento_id")
    private Integer mantenimientoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancha_id", nullable = false)
    private Cancha cancha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalDateTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalDateTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_mantenimiento", nullable = false, length = 20)
    private TipoMantenimiento tipoMantenimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_mantenimiento", nullable = false, length = 20)
    private EstadoMantenimiento estadoMantenimiento = EstadoMantenimiento.PROGRAMADO;

    @Column(length = 200)
    private String motivo;
}
