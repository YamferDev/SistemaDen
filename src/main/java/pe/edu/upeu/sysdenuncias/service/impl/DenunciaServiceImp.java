
package pe.edu.upeu.sysdenuncias.service.impl;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import pe.edu.upeu.sysdenuncias.config.DatabaseConnection;
import pe.edu.upeu.sysdenuncias.model.Denuncia;
import pe.edu.upeu.sysdenuncias.repository.DenunciaRepository;
import pe.edu.upeu.sysdenuncias.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysdenuncias.service.IDenunciaService;
import pe.edu.upeu.sysdenuncias.service.INotificacionService;
import pe.edu.upeu.sysdenuncias.enums.EstadoDenuncia;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class DenunciaServiceImp extends CrudGenericoServiceImp<Denuncia, Long> implements IDenunciaService {

    private final DenunciaRepository repo;
    private final INotificacionService notificacionService;

    public DenunciaServiceImp(DenunciaRepository repo, INotificacionService notificacionService) {
        super(repo);
        this.repo = repo;
        this.notificacionService = notificacionService;
    }

    @Override
    protected ICrudGenericoRepository<Denuncia, Long> getRepo() {
        return repo;
    }

    @Override
    public Denuncia update(Long id, Denuncia entity) {
        EstadoDenuncia estadoAnterior = repo.findById(id)
                .map(Denuncia::getEstado)
                .orElse(null);

        Denuncia actualizada = super.update(id, entity);

        if (estadoAnterior != null && entity.getEstado() != null
                && estadoAnterior != entity.getEstado()) {

            String funcionarioNombre = (actualizada.getFuncionario() != null)
                    ? actualizada.getFuncionario().getNombre()
                    : "No asignado";

            String mensaje = "Estimado/a " + actualizada.getCiudadano().getNombre() + ",\n" +
                    "Le informamos que el estado de su denuncia (Código: " + actualizada.getId() + ") " +
                    "ha cambiado de \"" + estadoAnterior.name() + "\" a \"" + actualizada.getEstado().name() + "\".\n" +
                    "Funcionario a cargo: " + funcionarioNombre + ".";

            notificacionService.notificarCiudadano(actualizada.getCiudadano(), mensaje);
        }

        return actualizada;
    }

    @Override
    public void generarConstanciaPdf(Long idDenuncia) {
        try {
            // Obtener datos para el nombre del archivo
            Denuncia denuncia = repo.findById(idDenuncia).orElseThrow();
            String nombreCiudadano = denuncia.getCiudadano().getNombre()
                    .replaceAll("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ ]", "")
                    .trim()
                    .replace(" ", "_");

            // Nombre único: Constancia_{id}_{NombreCiudadano}.pdf
            String nombreArchivo = "Constancia_" + idDenuncia + "_" + nombreCiudadano + ".pdf";

            // Ruta del .jrxml desde classpath (funciona en JAR y en desarrollo)
            InputStream reportStream = getClass().getResourceAsStream("/reports/constancia_denuncia.jrxml");
            if (reportStream == null) {
                throw new RuntimeException("No se encontró el archivo constancia_denuncia.jrxml en /reports/");
            }

            Connection con = DatabaseConnection.getConnection();
            Map<String, Object> params = new HashMap<>();
            params.put("ID_DENUNCIA", idDenuncia);

            JasperReport jr = JasperCompileManager.compileReport(reportStream);
            JasperPrint jp = JasperFillManager.fillReport(jr, params, con);

            // Exportar PDF con nombre único
            JasperExportManager.exportReportToPdfFile(jp, nombreArchivo);
            System.out.println("[PDF] Generado: " + nombreArchivo);

            // Abrir visor (con opción de imprimir desde el mismo visor)
            JasperViewer viewer = new JasperViewer(jp, false);
            viewer.setTitle("Constancia #" + idDenuncia + " - " + denuncia.getCiudadano().getNombre());
            viewer.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar constancia: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Integer> obtenerEstadisticasPorTipo() {
        return repo.getCountByTipo();
    }

    @Override
    public Map<String, Integer> obtenerEstadisticasPorEstado() {
        return repo.getCountByEstado();
    }
    @Override
    public void enviarConstanciaCorreo(Long idDenuncia) {

        Denuncia denuncia =
                repo.findById(idDenuncia).orElseThrow();

        File pdf =
                generarConstanciaPdfArchivo(idDenuncia);

        String mensaje =
                "Adjuntamos la constancia de la denuncia N° "
                        + denuncia.getId();

        notificacionService.enviarCorreoConAdjunto(
                denuncia.getCiudadano(),
                mensaje,
                pdf
        );
    }

    @Override
    public void enviarConstanciaWhatsapp(Long idDenuncia) {

        Denuncia denuncia = repo.findById(idDenuncia).orElseThrow();

        String mensaje =
                "Se ha generado la constancia de la denuncia N° "
                        + denuncia.getId() + ".";

        notificacionService.notificarCiudadano(
                denuncia.getCiudadano(),
                mensaje
        );
    }
    @Override
    public File generarConstanciaPdfArchivo(Long idDenuncia) {

        try {
            Denuncia denuncia = repo.findById(idDenuncia).orElseThrow();

            String nombreCiudadano = denuncia.getCiudadano().getNombre()
                    .replaceAll("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ ]", "")
                    .trim()
                    .replace(" ", "_");

            String nombreArchivo =
                    "Constancia_" + idDenuncia + "_" + nombreCiudadano + ".pdf";

            InputStream reportStream =
                    getClass().getResourceAsStream("/reports/constancia_denuncia.jrxml");

            Connection con = DatabaseConnection.getConnection();

            Map<String, Object> params = new HashMap<>();
            params.put("ID_DENUNCIA", idDenuncia);

            JasperReport jr =
                    JasperCompileManager.compileReport(reportStream);

            JasperPrint jp =
                    JasperFillManager.fillReport(jr, params, con);

            JasperExportManager.exportReportToPdfFile(jp, nombreArchivo);

            return new File(nombreArchivo);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

