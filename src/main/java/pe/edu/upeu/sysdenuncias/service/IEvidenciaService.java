package pe.edu.upeu.sysdenuncias.service;

import pe.edu.upeu.sysdenuncias.model.Evidencia;
import java.io.File;

public interface IEvidenciaService extends ICrudGenericoService<Evidencia, Long> {
    Evidencia guardarConArchivo(Evidencia evidencia, File archivoSeleccionado);
}