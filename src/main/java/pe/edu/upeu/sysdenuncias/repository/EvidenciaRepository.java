package pe.edu.upeu.sysdenuncias.repository;

import pe.edu.upeu.sysdenuncias.model.Evidencia;
import java.util.List;

public interface EvidenciaRepository extends ICrudGenericoRepository<Evidencia, Long> {
    List<Evidencia> findByDenuncia(Long denunciaId);
}
