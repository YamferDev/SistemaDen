package pe.edu.upeu.sysdenuncias.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.sysdenuncias.enums.EstadoDenuncia;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.Collection;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Denuncia {


    private Long id;

    @NotBlank(message = "La descripción de la denuncia es obligatoria")
    private String descripcion;

    private java.time.LocalDateTime fecha;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @NotNull(message = "El estado de la denuncia es obligatorio")
    private EstadoDenuncia estado;

    
    @NotNull(message = "El ciudadano es obligatorio")
    private Ciudadano ciudadano;

    @NotNull(message = "El tipo de denuncia es obligatorio")
    private TipoDenuncia tipoDenuncia;

    
    private Funcionario funcionario;

    private String observacion;

    @Builder.Default
    private Collection<Evidencia> evidencias = new java.util.ArrayList<>();
}