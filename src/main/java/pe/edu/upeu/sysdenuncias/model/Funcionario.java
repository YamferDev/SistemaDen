package pe.edu.upeu.sysdenuncias.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.sysdenuncias.enums.Cargo;
import pe.edu.upeu.sysdenuncias.enums.Especialidad;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario {

    private Long id;
    @NotBlank(message = "El DNI es obligatorio")
    private String dni;
    @NotBlank(message = "El nombre del funcionario es obligatorio")
    private String nombre;

    @NotBlank(message = "El cargo es obligatorio")
    private Cargo cargo;

    private Especialidad especialidad;
    private String credenciales;
}