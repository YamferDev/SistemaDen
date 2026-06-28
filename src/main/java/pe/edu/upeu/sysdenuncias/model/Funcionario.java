package pe.edu.upeu.sysdenuncias.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.sysdenuncias.enums.Cargo;


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

    @NotBlank(message = "Las credenciales (contraseña) son obligatorias")
    private String credenciales;
}