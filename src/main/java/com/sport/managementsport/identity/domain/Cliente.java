package com.sport.managementsport.identity.domain;

import com.sport.managementsport.common.domain.AuditableEntity;
import com.sport.managementsport.common.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Cliente", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tip_documento", "documento"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id")
    private Integer clienteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tip_documento", nullable = false, length = 20)
    private TipoDocumento tipoDocumento;

    @Column(nullable = false, length = 30)
    private String documento;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 100)
    private String email;

    @Column(length = 12)
    private String telefono;
}
