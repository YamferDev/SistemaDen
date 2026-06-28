package pe.edu.upeu.sysdenuncias.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.sysdenuncias.enums.Genero;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ciudadano {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 8, max = 8, message = "El DNI debe tener exactamente 8 dígitos")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe contener solo dígitos")
    private String dni;


    @Size(min = 9, max = 9, message = "El número debe tener exactamente 9 dígitos")
    @Pattern(regexp = "\\d{9}", message = "El teléfono debe contener solo dígitos") // <--- Cambia a 9 aquí
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    private Genero genero;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingrese un correo electrónico válido (ej: usuario@dominio.com)")
    private String correo;
}