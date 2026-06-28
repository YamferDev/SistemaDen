package pe.edu.upeu.sysdenuncias.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evidencia {

    private Long id;

    private Denuncia denuncia;

    private String nombreArchivo;

    private String rutaArchivo;

    private String tipoArchivo;

    private LocalDateTime fechaSubida;

}
