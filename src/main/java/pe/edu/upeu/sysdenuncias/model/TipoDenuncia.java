package pe.edu.upeu.sysdenuncias.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoDenuncia {

    private Long id;

    @NotBlank(message = "El nombre del tipo de denuncia es obligatorio")
    private String nombre;
}