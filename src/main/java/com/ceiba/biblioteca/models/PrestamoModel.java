package com.ceiba.biblioteca.models;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.persistence.*;
import javax.validation.constraints.Size;

@Data
@Entity
@Table(name = "prestamo")
public class PrestamoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 10)
    private String isbn;

    @NotBlank
    @Size(max = 10)
    private String identificacionUsuario;

    private Byte tipoUsuario;

    private String fechaMaximaDevolucion;
}
