package pe.edu.upeu.sysdenuncias.service;

import pe.edu.upeu.sysdenuncias.model.Denuncia;

import java.io.File;
import java.util.Map;

public interface IDenunciaService extends ICrudGenericoService<Denuncia, Long> {

    void generarConstanciaPdf(Long idDenuncia);

    File generarConstanciaPdfArchivo(Long idDenuncia);

    void enviarConstanciaCorreo(Long idDenuncia);

    void enviarConstanciaWhatsapp(Long idDenuncia);

    Map<String, Integer> obtenerEstadisticasPorTipo();

    Map<String, Integer> obtenerEstadisticasPorEstado();
}