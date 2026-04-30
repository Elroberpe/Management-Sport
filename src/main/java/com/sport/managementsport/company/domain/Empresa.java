package com.sport.managementsport.company.domain;

import com.sport.managementsport.common.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Empresa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Empresa extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "empresa_id")
    private Integer empresaId;

    @Column(name = "nombre_comercial", nullable = false, length = 100)
    private String nombreComercial;

    @Column(name = "razon_social", nullable = false, length = 100)
    private String razonSocial;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "email_contacto", length = 100)
    private String emailContacto;

    @Column(name = "telefono_principal", length = 12)
    private String telefonoPrincipal;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Sucursal> sucursales = new ArrayList<>();
}
